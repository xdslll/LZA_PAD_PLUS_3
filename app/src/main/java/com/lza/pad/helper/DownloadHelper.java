package com.lza.pad.helper;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import com.lza.pad.db.facade.DownloadFileFacade;
import com.lza.pad.db.model.DownloadFile;
import com.lza.pad.db.model.pad.PadVersionInfo;
import com.lza.pad.support.debug.AppLogger;

import java.io.File;

import de.greenrobot.event.EventBus;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 11/6/14.
 */
public class DownloadHelper {

    public static final Uri CONTENT_URI = Uri.parse("content://downloads/my_downloads");
    private DownloadChangeObserver mDownloadChangeObserver;

    private long mDownloadReference;
    private File mDownloadFile;
    private String mDownloadUrl = "";
    private Context mCtx;
    private InternelDownloadFile mInnerFile;

    private DownloadManager mDownloadManager;
    private DownloadFile mDBDownloadFile;

    /**
     * 更新版本专用的DownloadHelper构造函数
     *
     * @param ctx
     * @param version
     * @param fileName
     * @param file
     */
    public DownloadHelper(Context ctx, PadVersionInfo version, String fileName, File file) {
        this(ctx, version.getUrl(), fileName, file.getAbsolutePath(), PadVersionInfo.FILE_TYPE);
    }

    public DownloadHelper(Context ctx, String url, String fileName, String filePath, int fileType) {
        mInnerFile = new InternelDownloadFile();
        mInnerFile.setUrl(url);
        mInnerFile.setFileName(fileName);
        mInnerFile.setFilePath(filePath);
        mInnerFile.setFileType(fileType);
        mDownloadUrl = url;
        mCtx = ctx;
        mDownloadFile = new File(filePath);
        mDownloadManager = (DownloadManager) mCtx.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public DownloadHelper(Context ctx, InternelDownloadFile downloadFile) {
        this(ctx, downloadFile.getUrl(), downloadFile);
    }

    public DownloadHelper(Context ctx, String url, InternelDownloadFile downloadFile) {
        mDownloadUrl = url;
        mCtx = ctx;
        mDownloadFile = new File(downloadFile.filePath);
        mInnerFile = downloadFile;
        mDownloadManager = (DownloadManager) mCtx.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public void download() {
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        mCtx.registerReceiver(mDownloadCompleteReceiver, filter);
        //创建下载请求
        Uri uri = Uri.parse(mDownloadUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationUri(Uri.fromFile(mDownloadFile));
        //request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        //request.setTitle("正在下载");
        //request.setDescription("正在下载：" + mDownloadFile);
        mDownloadReference = mDownloadManager.enqueue(request);

        mDownloadChangeObserver = new DownloadChangeObserver(null);
        mCtx.getContentResolver().registerContentObserver(
                CONTENT_URI, true, mDownloadChangeObserver);
    }

    /**
     * 实时获取下载过程中的文件大小、进度、状态等
     */
    private class DownloadChangeObserver extends ContentObserver {

        public DownloadChangeObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            queryDownloadStatus();
        }
    }

    private void queryDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mDownloadReference);
        Cursor c = mDownloadManager.query(query);
        if (c != null && c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            int fileSizeIndex = c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
            int bytesSizeIndex = c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
            int fileSize = c.getInt(fileSizeIndex);
            int bytesSize = c.getInt(bytesSizeIndex);
            int percent = (int) ((float) bytesSize / fileSize * 100);
            AppLogger.e("[DownloadManager] status=" + parseStatus(status)
                    + ",fileSize=" + fileSize + ",bytesSize=" + bytesSize
                    + ",percent=" + percent + "%");
            DownloadQuery downloadQuery = new DownloadQuery();
            downloadQuery.fileSize = fileSize;
            downloadQuery.bytes = bytesSize;
            downloadQuery.percent = percent;
            downloadQuery.status = status;
            EventBus.getDefault().post(downloadQuery);
        }
    }

    public static class DownloadQuery {
        int fileSize;
        int bytes;
        int percent;
        int status;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getFileSize() {
            return fileSize;
        }

        public void setFileSize(int fileSize) {
            this.fileSize = fileSize;
        }

        public int getBytes() {
            return bytes;
        }

        public void setBytes(int bytes) {
            this.bytes = bytes;
        }

        public int getPercent() {
            return percent;
        }

        public void setPercent(int percent) {
            this.percent = percent;
        }
    }

    public static String parseStatus(int status) {
        if (status == DownloadManager.STATUS_SUCCESSFUL) {
            return "STATUS_SUCCESSFUL";
        } else if (status == DownloadManager.STATUS_FAILED) {
            return "STATUS_FAILED";
        } else if (status == DownloadManager.STATUS_PAUSED) {
            return "STATUS_PAUSED";
        } else if (status == DownloadManager.STATUS_PENDING) {
            return "STATUS_PENDING";
        } else if (status == DownloadManager.STATUS_RUNNING) {
            return "STATUS_RUNNING";
        }
        return "";
    }

    public void release() {
        if (mDownloadCompleteReceiver != null) {
            try {
                mCtx.unregisterReceiver(mDownloadCompleteReceiver);
            } catch (Exception ex) {

            }
        }
        if (mDownloadChangeObserver != null) {
            try {
                mCtx.getContentResolver().unregisterContentObserver(mDownloadChangeObserver);
            } catch (Exception ex) {

            }
        }
    }

    private BroadcastReceiver mDownloadCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (mDownloadReference == reference) {
                    if (mDownloadFile != null && mDownloadFile.exists()) {
                        AppLogger.e("下载文件[" + mInnerFile.fileName + "]成功");
                        mDBDownloadFile = new DownloadFile();
                        mDBDownloadFile.setFileName(mInnerFile.fileName);
                        mDBDownloadFile.setFilePath(mInnerFile.filePath);
                        mDBDownloadFile.setType(mInnerFile.fileType);
                        mDBDownloadFile.setArtId(mInnerFile.fileArtId);
                        mDBDownloadFile.setArtTitle(mInnerFile.fileArtTitle);
                        mDBDownloadFile.setArtDescription(mInnerFile.fileArtDescription);
                        mDBDownloadFile.setImgPath(mInnerFile.imgPath);
                        DownloadFileFacade.create(mCtx, mDBDownloadFile);
                        //发送广播
                        EventBus.getDefault().post(mDBDownloadFile);
                    } else {
                        AppLogger.e("下载文件[" + mInnerFile.fileName + "]失败");
                        mDBDownloadFile = new DownloadFile();
                        mDBDownloadFile.setFilePath(null);
                        EventBus.getDefault().post(mDBDownloadFile);
                    }
                    release();
                } else {

                }
            }
        }
    };

    public static class InternelDownloadFile {
        String url;
        String filePath;
        String fileName;
        int fileType;
        String fileArtId;
        String fileArtTitle;
        String fileArtDescription;
        String imgPath;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public int getFileType() {
            return fileType;
        }

        public void setFileType(int fileType) {
            this.fileType = fileType;
        }

        public String getFileArtId() {
            return fileArtId;
        }

        public void setFileArtId(String fileArtId) {
            this.fileArtId = fileArtId;
        }

        public String getFileArtTitle() {
            return fileArtTitle;
        }

        public void setFileArtTitle(String fileArtTitle) {
            this.fileArtTitle = fileArtTitle;
        }

        public String getFileArtDescription() {
            return fileArtDescription;
        }

        public void setFileArtDescription(String fileArtDescription) {
            this.fileArtDescription = fileArtDescription;
        }

        public String getImgPath() {
            return imgPath;
        }

        public void setImgPath(String imgPath) {
            this.imgPath = imgPath;
        }
    }
}
