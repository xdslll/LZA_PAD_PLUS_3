package com.lza.pad.helper;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;

import com.lza.pad.db.facade.DownloadFileFacade;
import com.lza.pad.db.model.DownloadFile;

import java.io.File;

import de.greenrobot.event.EventBus;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 11/6/14.
 */
public class DownloadHelper {

    public DownloadHelper(Activity activity, String url, InternelDownloadFile downloadFile) {
        mDownloadUrl = url;
        mActivity = activity;
        mDownloadFile = new File(downloadFile.filePath);
        mInnerFile = downloadFile;

        mDownloadManager = (DownloadManager) mActivity.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public void download() {
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        mActivity.registerReceiver(mDownloadCompleteReceiver, filter);
        //创建下载请求
        Uri uri = Uri.parse(mDownloadUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationUri(Uri.fromFile(mDownloadFile));
        //request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        //request.setTitle("正在下载");
        //request.setDescription("正在下载：" + mDownloadFile);
        //开始下载
        mDownloadReference = mDownloadManager.enqueue(request);

        ifDownload = true;
    }

    public void release() {
        if (mDownloadCompleteReceiver != null) {
            try {
                mActivity.unregisterReceiver(mDownloadCompleteReceiver);
            } catch (Exception ex) {

            }
        }
    }

    private boolean ifDownload = false;
    private long mDownloadReference;
    private File mDownloadFile;
    private String mDownloadUrl = "";
    private Activity mActivity;
    private InternelDownloadFile mInnerFile;

    private DownloadManager mDownloadManager;
    private DownloadFile mDBDownloadFile;

    private BroadcastReceiver mDownloadCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (mDownloadReference == reference) {
                if (mDownloadFile != null && mDownloadFile.exists()) {
                    //Toast.makeText(context, "下载完成", Toast.LENGTH_SHORT).show();
                    mDBDownloadFile = new DownloadFile();
                    mDBDownloadFile.setFileName(mInnerFile.fileName);
                    mDBDownloadFile.setFilePath(mInnerFile.filePath);
                    mDBDownloadFile.setType(mInnerFile.fileType);
                    mDBDownloadFile.setArtId(mInnerFile.fileArtId);
                    mDBDownloadFile.setArtTitle(mInnerFile.fileArtTitle);
                    mDBDownloadFile.setArtDescription(mInnerFile.fileArtDescription);
                    mDBDownloadFile.setImgPath(mInnerFile.imgPath);
                    DownloadFileFacade.create(mActivity, mDBDownloadFile);
                    release();
                    //发送广播
                    EventBus.getDefault().post(mDBDownloadFile);
                } else {
                    //Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    public static class InternelDownloadFile {
        String filePath;
        String fileName;
        int fileType;
        String fileArtId;
        String fileArtTitle;
        String fileArtDescription;
        String imgPath;

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
