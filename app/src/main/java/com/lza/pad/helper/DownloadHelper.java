package com.lza.pad.helper;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;

import com.lza.pad.support.debug.AppLogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 5/26/15.
 */
public class DownloadHelper {

    private static DownloadHelper sInstance;
    DownloadManager mDownloadManager;
    Context mCtx;

    DownloadReceiver mDownloadReceiver = new DownloadReceiver();
    DownloadChangeObserver mDownloadObserver;

    public static final Uri CONTENT_URI = Uri.parse("content://downloads/my_downloads");

    public static final String DEFAULT_FILE_NAME = "file";

    List<Long> mDownloadList = new ArrayList<Long>();

    private DownloadHelper(Context c) {
        mCtx = c;
        mDownloadManager = (DownloadManager) mCtx.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public static DownloadHelper getInstance(Context c) {
        if (sInstance == null) {
            sInstance = new DownloadHelper(c);
        }
        return sInstance;
    }

    private boolean addToDownloadList(long reference) {
        if (!mDownloadList.contains(reference)) {
            return mDownloadList.add(reference);
        }
        return false;
    }

    private boolean removetFromDownloadList(long reference) {
        if (mDownloadList.contains(reference)) {
            return mDownloadList.remove(reference);
        }
        return false;
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        mCtx.registerReceiver(mDownloadReceiver, filter);
    }

    private void registerContentObserver(long reference) {
        mDownloadObserver = new DownloadChangeObserver(null, reference);
        mCtx.getContentResolver().registerContentObserver(
                CONTENT_URI, true, mDownloadObserver);
    }

    private void unregisterReceiver() {
        mCtx.unregisterReceiver(mDownloadReceiver);
    }

    private void unregisterContentObserver() {
        mCtx.getContentResolver().unregisterContentObserver(mDownloadObserver);
    }

    /**
     * 执行下载过程
     *
     * @param info  下载参数
     * @return
     */
    public long download(DownloadInfo info) {
        //解析下载地址
        Uri uri = Uri.parse(info.getUrl());
        DownloadManager.Request request = new DownloadManager.Request(uri);
        //设置Notification是否可见
        request.setNotificationVisibility(info.getNotificationVisibility());
        //设置系统的下载UI中是否显示该下载
        request.setVisibleInDownloadsUi(info.isVisibleInDownloadsUri());
        //设置MimeType
        if (!TextUtils.isEmpty(info.getMimeType())) {
            request.setMimeType(info.getMimeType());
        }
        //设置Notification的标题和描述
        if (!TextUtils.isEmpty(info.getNotificationTitle())) {
            request.setTitle(info.getNotificationTitle());
            request.setDescription(info.getNotificationDescription());
        }
        //设置允许下载的网络类型
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        if (info.isAllowDownloadInMobile()) {
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
        }
        //设置目标文件的存放路径
        if (info.getFilePath() != null) {
            File destinationFile = new File(info.getFilePath());
            request.setDestinationUri(Uri.fromFile(destinationFile));
        } else {
            //如果用户不设置下载路径，则会将其保存到Downloads文件夹中
            request.setDestinationInExternalFilesDir(mCtx, Environment.DIRECTORY_DOWNLOADS, DEFAULT_FILE_NAME);
        }
        //是否允许文件作为媒体文件被扫描
        if (info.isAllowScanningByMediaScanner()) {
            request.allowScanningByMediaScanner();
        }
        Map<String, String> headers = info.getHeaders();
        if (headers != null && !headers.isEmpty()) {
            Set<Map.Entry<String, String>> keySet = headers.entrySet();
            for (Map.Entry<String, String> entry : keySet) {
                request.addRequestHeader(entry.getKey(), entry.getValue());
            }
        }
        //设置下载过程中的接听器
        mDownloadListener = info.getListener();
        long reference = mDownloadManager.enqueue(request);
        registerReceiver();
        registerContentObserver(reference);
        addToDownloadList(reference);

        return reference;
    }

    /**
     * 执行下载过程
     *
     * @param url                   下载地址
     * @param destinationFilePath   下载文件存放的路径，注意：需要指定文件名，文件名可以重复，
     *                              通过{@link #getUriForDownloadedFile(long)}获取文件路径，
     *                              通过{@link #getMimeTypeForDownloadedFile(long)}获取文件类型
     * @param downloadListener      下载过程中的回调函数
     * @return                      返回下载ID，该值唯一
     */
    public long download(String url, String destinationFilePath, OnDownloadListener downloadListener) {
        return download(new DownloadInfo(url, destinationFilePath, downloadListener));
    }

    public void cancelDownload(long reference) {
        mDownloadManager.remove(reference);
        removetFromDownloadList(reference);
    }

    /**
     * 实时获取下载过程中的文件大小、进度、状态等
     */
    private class DownloadChangeObserver extends ContentObserver {

        long mReference;
        public DownloadChangeObserver(Handler handler, long reference) {
            super(handler);
            mReference = reference;
        }

        @Override
        public void onChange(boolean selfChange) {
            queryDownloadStatus(mReference);
        }
    }

    private void queryDownloadStatus(long reference) {
        List<DownloadQuery> downloadQueryList = getDownloadQueryList(reference);
        if (downloadQueryList != null && !downloadQueryList.isEmpty()) {
            DownloadQuery downloadQuery = downloadQueryList.get(0);
            if (downloadQuery.status == DownloadManager.STATUS_PAUSED ||
                    downloadQuery.status == DownloadManager.STATUS_FAILED) {
                unregisterReceiver();
                unregisterContentObserver();
                removetFromDownloadList(reference);
            }
            if (mDownloadListener != null) {
                mDownloadListener.onDownloadProgress(downloadQuery);
            }
        }
    }

    public List<DownloadQuery> getDownloadQueryList(long... references) {
        List<DownloadQuery> downloadQueryList = new ArrayList<DownloadQuery>();
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(references);
        Cursor c = mDownloadManager.query(query);
        if (c != null && c.moveToFirst()) {
            int i = 0;
            do {
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                int fileSizeIndex = c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                int bytesSizeIndex = c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                int fileNameIndex = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
                int fileUriIndex = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);

                int fileSize = c.getInt(fileSizeIndex);
                int bytesSize = c.getInt(bytesSizeIndex);
                int percent = (int) ((float) bytesSize / fileSize * 100);
                String fileName = c.getString(fileNameIndex);
                String fileUri = c.getString(fileUriIndex);

                AppLogger.e("[DownloadManager] reference=" + references[i] +
                        ",status=" + parseStatus(status) + ",fileSize=" + fileSize +
                        ",bytesSize=" + bytesSize + ",percent=" + percent + "%" +
                        ",fileName=" + fileName + ",fileUri=" + fileUri);

                DownloadQuery downloadQuery = new DownloadQuery();
                downloadQuery.fileSize = fileSize;
                downloadQuery.bytes = bytesSize;
                downloadQuery.percent = percent;
                downloadQuery.status = status;
                downloadQuery.fileName = fileName;
                downloadQuery.fileUri = fileUri;
                downloadQuery.reference = references[i];

                downloadQueryList.add(downloadQuery);

                i++;
            } while (c.moveToNext());
        }
        return downloadQueryList;
    }

    /**
     * 用于描述下载相关的状态
     */
    public class DownloadQuery {
        /**
         * 下载ID
         */
        long reference;

        /**
         * 文件名称
         */
        String fileName;

        /**
         * 文件路径Uri
         */
        String fileUri;

        /**
         * 文件的整体容量
         */
        int fileSize;

        /**
         * 已经下载了多少字节
         */
        int bytes;

        /**
         * 已经下载的百分比
         */
        int percent;

        /**
         * 当前的下载状态
         */
        int status;

        public long getReference() {
            return reference;
        }

        public void setReference(long reference) {
            this.reference = reference;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileUri() {
            return fileUri;
        }

        public void setFileUri(String fileUri) {
            this.fileUri = fileUri;
        }

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

    public static String parseStatus(int downloadStatus) {
        if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL) {
            return "STATUS_SUCCESSFUL";
        } else if (downloadStatus == DownloadManager.STATUS_FAILED) {
            return "STATUS_FAILED";
        } else if (downloadStatus == DownloadManager.STATUS_PAUSED) {
            return "STATUS_PAUSED";
        } else if (downloadStatus == DownloadManager.STATUS_PENDING) {
            return "STATUS_PENDING";
        } else if (downloadStatus == DownloadManager.STATUS_RUNNING) {
            return "STATUS_RUNNING";
        }
        return "";
    }

    /**
     * 用于接收和DownloadManager相关的广播
     */
    private class DownloadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (mDownloadListener != null) {
                    mDownloadListener.onDownloadComplete(reference);
                }
                unregisterReceiver();
                unregisterContentObserver();
                removetFromDownloadList(reference);
            }
        }
    }

    /**
     * 获取资源类型
     *
     * @param reference
     * @return
     */
    public String getMimeTypeForDownloadedFile(long reference) {
        return mDownloadManager.getMimeTypeForDownloadedFile(reference);
    }

    /**
     * 获取文件Uri
     *
     * @param reference
     * @return
     */
    public Uri getUriForDownloadedFile(long reference) {
        return mDownloadManager.getUriForDownloadedFile(reference);
    }

    public ParcelFileDescriptor openDownloadedFile(long reference) {
        try {
            return mDownloadManager.openDownloadedFile(reference);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 下载过程的监听器
     */
    public interface OnDownloadListener {
        /**
         * 下载完成时调用
         *
         * @param reference 下载id号
         */
        void onDownloadComplete(long reference);

        /**
         * 下载过程中调用
         *
         * @param query 下载进度相关参数
         */
        void onDownloadProgress(DownloadQuery query);
    }

    /**
     * OnDownloadListener对象，下载时由用户传入
     */
    private OnDownloadListener mDownloadListener;

    /**
     * 描述下载过程中所涉及的参数
     */
    public static class DownloadInfo {

        public DownloadInfo(String url, String filePath, OnDownloadListener listener) {
            this.url = url;
            this.filePath = filePath;
            this.listener = listener;

            notificationTitle = "";
            notificationDescription = "";
            notificationVisibility = DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED;
            allowScanningByMediaScanner = false;
            visibleInDownloadsUri = true;
            allowDownloadInMobile = false;
            mimeType = "";
            headers = null;
        }

        String url;
        String filePath;
        OnDownloadListener listener;

        String notificationTitle;
        String notificationDescription;
        int notificationVisibility;
        boolean allowScanningByMediaScanner;
        boolean visibleInDownloadsUri;
        boolean allowDownloadInMobile;
        String mimeType;
        Map<String, String> headers;

        public String getUrl() {
            return url;
        }

        public String getFilePath() {
            return filePath;
        }

        public OnDownloadListener getListener() {
            return listener;
        }

        public String getNotificationTitle() {
            return notificationTitle;
        }

        public String getNotificationDescription() {
            return notificationDescription;
        }

        public int getNotificationVisibility() {
            return notificationVisibility;
        }

        public boolean isAllowScanningByMediaScanner() {
            return allowScanningByMediaScanner;
        }

        public boolean isVisibleInDownloadsUri() {
            return visibleInDownloadsUri;
        }

        public boolean isAllowDownloadInMobile() {
            return allowDownloadInMobile;
        }

        public String getMimeType() {
            return mimeType;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }
    }

    public static class DownloadInfoBuilder {
        DownloadInfo info;

        public DownloadInfoBuilder(String url, String filePath, OnDownloadListener listener) {
            this.info = new DownloadInfo(url, filePath, listener);
        }

        public DownloadInfo build() {
            return info;
        }

        public DownloadInfoBuilder setUrl(String url) {
            info.url = url;
            return this;
        }

        public DownloadInfoBuilder setFilePath(String filePath) {
            info.filePath = filePath;
            return this;
        }

        public DownloadInfoBuilder setListener(OnDownloadListener listener) {
            info.listener = listener;
            return this;
        }

        public DownloadInfoBuilder setNotificationTitle(String notificationTitle) {
            info.notificationTitle = notificationTitle;
            return this;
        }

        public DownloadInfoBuilder setNotificationDescription(String notificationDescription) {
            info.notificationDescription = notificationDescription;
            return this;
        }

        public DownloadInfoBuilder setNotificationVisibility(int notificationVisibility) {
            info.notificationVisibility = notificationVisibility;
            return this;
        }

        public DownloadInfoBuilder setAllowScanningByMediaScanner(boolean allowScanningByMediaScanner) {
            info.allowScanningByMediaScanner = allowScanningByMediaScanner;
            return this;
        }

        public DownloadInfoBuilder setVisibleInDownloadsUri(boolean visibleInDownloadsUri) {
            info.visibleInDownloadsUri = visibleInDownloadsUri;
            return this;
        }

        public DownloadInfoBuilder setAllowDownloadInMobile(boolean allowDownloadInMobile) {
            info.allowDownloadInMobile = allowDownloadInMobile;
            return this;
        }

        public DownloadInfoBuilder setMimeType(String mimeType) {
            info.mimeType = mimeType;
            return this;
        }

        public DownloadInfoBuilder setHeaders(Map<String, String> headers) {
            info.headers = headers;
            return this;
        }
    }
}
