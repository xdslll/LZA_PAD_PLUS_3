package com.lza.pad.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.lza.pad.db.model.DownloadFile;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.db.model.pad.PadVersionInfo;
import com.lza.pad.helper._DownloadHelper;
import com.lza.pad.helper.JsonParseHelper;
import com.lza.pad.helper.RequestHelper;
import com.lza.pad.helper.SimpleRequestListener;
import com.lza.pad.helper.UrlHelper;
import com.lza.pad.helper.event.model.ResponseEventInfo;
import com.lza.pad.helper.event.state.ResponseEventTag;
import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.file.FileTools;
import com.lza.pad.support.utils.Consts;
import com.lza.pad.support.utils.Utility;

import java.io.File;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/2/10.
 */
public class UpdateDeviceService extends IntentService implements Consts, RequestHelper.OnRequestListener {

    private PadDeviceInfo mDeviceInfo;
    private Context mContext;
    private String mRequestUrl;
    private String mUpdateTag;

    private boolean mIsUpdating = false;
    private boolean mIsRunning = true;

    private final Object mLock = new Object();

    /**
     * 每隔5秒更新一次
     */
    private static int mUpdateTime = 5 * 1000;

    public UpdateDeviceService() {
        super("UpdateDeviceService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getBaseContext();
        String macAddress = Utility.getMacAddress(this);
        mRequestUrl = UrlHelper.getDeviceUrl(macAddress);
        if (!EventBus.getDefault().isRegistered(UpdateDeviceService.this)) {
            EventBus.getDefault().register(UpdateDeviceService.this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        log("正在启动服务");
        mHandler.sendEmptyMessageDelayed(REQUEST_UPDATE_STATE, mUpdateTime);
    }

    public static final int REQUEST_UPDATE_STATE = 0x01;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mIsRunning) {
                log("正在停止服务");
                if (EventBus.getDefault().isRegistered(UpdateDeviceService.this)) {
                    EventBus.getDefault().unregister(UpdateDeviceService.this);
                }
                return;
            }
            synchronized (mLock) {
                if (msg.what == REQUEST_UPDATE_STATE) {
                    //如果正在更新，直接跳过，下一次再试
                    if (mIsUpdating) {
                        log("正在更新");
                        mHandler.sendEmptyMessageDelayed(REQUEST_UPDATE_STATE, mUpdateTime);
                    } else {
                        //如果没有生成请求，则先生成请求
                        if (TextUtils.isEmpty(mRequestUrl)) mRequestUrl = createUrl();
                        //发送请求
                        RequestHelper.getInstance(mContext, mRequestUrl, UpdateDeviceService.this).send();
                    }
                }
            }
        }
    };

    private String createUrl() {
        String macAddress = Utility.getMacAddress(this);
        return UrlHelper.getDeviceUrl(macAddress);
    }

    @Override
    public void onResponse(ResponseEventInfo response) {
        if (response == null) {
            mHandler.sendEmptyMessageDelayed(REQUEST_UPDATE_STATE, mUpdateTime);
            return;
        }
        if (response.getTag() == ResponseEventTag.ON_RESONSE) {
            String json = response.getResponseData();
            final ResponseData<PadDeviceInfo> data = JsonParseHelper.parseDeviceInfoResponse(json);
            if (data == null) {
                mHandler.sendEmptyMessageDelayed(REQUEST_UPDATE_STATE, mUpdateTime);
                return;
            }
            String state = data.getState();
            if (state == null) {
                mHandler.sendEmptyMessageDelayed(REQUEST_UPDATE_STATE, mUpdateTime);
                return;
            }
            if (state.equals(ResponseData.RESPONSE_STATE_OK)) {
                if (data.getContent() == null || data.getContent().size() <= 0) return;
                mDeviceInfo = data.getContent().get(0);
                updateDevice();
                if (checkVersion(mDeviceInfo)) {
                    updateVersion(mDeviceInfo);
                } else {
                    updateUI();
                }
            }
            mHandler.sendEmptyMessageDelayed(REQUEST_UPDATE_STATE, mUpdateTime);
        } else {
            log(response.getErrorMessage());
            mHandler.sendEmptyMessageDelayed(REQUEST_UPDATE_STATE, mUpdateTime);
        }
    }

    /**
     * 检查版本
     */
    private boolean checkVersion(PadDeviceInfo deviceInfo) {
        //读取设备的版本号，比对现有的版本号
        int currentVersionCode = Utility.getVersionCode(getBaseContext());
        String newVersionCode = deviceInfo.getVersion();
        log("当前版本号：" + currentVersionCode + ",新版本号：" + newVersionCode);
        if (TextUtils.isEmpty(newVersionCode)) {
            return false;
        } else {
            int newVersion = Utility.safeIntParse(newVersionCode, 0);
            if (currentVersionCode < newVersion) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 更新版本
     */
    private void updateVersion(PadDeviceInfo deviceInfo) {
        //查询该版本是否存在
        String requestUrl = UrlHelper.getVersionUrl(deviceInfo);
        //如果存在则下载该版本
        send(requestUrl, new UpdateNewVersionListener(deviceInfo));
        mIsUpdating = true;
    }

    /**
     * 立即更新当前界面
     */
    private void updateUI() {
        mUpdateTag = mDeviceInfo.getUpdate_tag();
        //获取更新时间
        mUpdateTime = Integer.parseInt(mDeviceInfo.getUpdate_time()) * 1000;
        if (mUpdateTag.equals(PadDeviceInfo.TAG_NEED_UDPATE)) {
            log("需要更新");
            //检查自动更新标识
            String autoUpdateTag = mDeviceInfo.getAuto_update();
            if (autoUpdateTag.equals(PadDeviceInfo.TAG_AUTO_UPDATE)) {
                //允许自动更新
                Intent intentReceiver = new Intent(ACTION_UPDATE_DEVICE_RECEIVER);
                intentReceiver.putExtra(KEY_PAD_DEVICE_INFO, mDeviceInfo);
                sendBroadcast(intentReceiver);
                mIsUpdating = true;
            } else {
                //不允许自动更新
                log("不允许自动更新，界面将不会更新");
            }
        } else if (mUpdateTag.equals(PadDeviceInfo.TAG_HAVE_UDPATE)) {
            log("已经更新");
        } else {
            log("未知状态");
        }
    }

    private void updateDevice() {
        log("开始更新设备状态");
        mDeviceInfo.setLast_connect_time(String.valueOf(System.currentTimeMillis()));
        mDeviceInfo.setState(PadDeviceInfo.TAG_STATE_ON);
        int currentVersion = Utility.getVersionCode(mContext);
        mDeviceInfo.setVersion(String.valueOf(currentVersion));
        String updateDeviceUrl = UrlHelper.updateDeviceInfoUrl(mDeviceInfo);
        send(updateDeviceUrl, new SimpleRequestListener() {
            @Override
            public boolean handleResponseStatusOK(String json) {
                log("更新设备状态成功！");
                return super.handleResponseStatusOK(json);
            }

            @Override
            public void handleRespone(Throwable error) {
                log("更新设备状态失败！");
            }
        });
    }

    protected void send(String url, RequestHelper.OnRequestListener listener) {
        RequestHelper.getInstance(getBaseContext(), url, listener).send();
    }

    private void log(String msg) {
        AppLogger.e("service : " + msg);
    }

    public static class UpdateCallback {
        public boolean isUpdating = false;
        public boolean isRunning = true;
    }

    public void onEvent(UpdateCallback callback) {
        mIsRunning = callback.isRunning;
        mIsUpdating = callback.isUpdating;
        if (mDeviceInfo != null) {
            mDeviceInfo.setUpdate_tag(PadDeviceInfo.TAG_HAVE_UDPATE);
            updateDevice();
        }
    }

    private class UpdateNewVersionListener extends SimpleRequestListener<PadVersionInfo> {

        PadDeviceInfo deviceInfo;

        private UpdateNewVersionListener(PadDeviceInfo deviceInfo) {
            this.deviceInfo = deviceInfo;
        }

        @Override
        public ResponseData<PadVersionInfo> parseJson(String json) {
            return JsonParseHelper.pareseVersionInfo(json);
        }

        @Override
        public void handleRespone(List<PadVersionInfo> content) {
            log("开始更新版本...");
            PadVersionInfo version = content.get(0);
            String downloadUrl = version.getUrl();
            if (TextUtils.isEmpty(downloadUrl)) {
                log("下载链接为空！");
            } else {
                String fileName = deviceInfo.getVersion() + ".apk";
                File filePath = FileTools.getCacheFile(Consts.CACHE_PATH + "/version/" + fileName);
                try {
                    if (filePath.exists()) {
                        filePath.delete();
                    }
                } catch (Exception ex) {

                }
                _DownloadHelper.InternelDownloadFile downloadFile = new _DownloadHelper.InternelDownloadFile();
                downloadFile.setFileName(fileName);
                downloadFile.setFilePath(filePath.getAbsolutePath());
                downloadFile.setFileType(PadVersionInfo.FILE_TYPE);
                _DownloadHelper helper = new _DownloadHelper(getBaseContext(), downloadUrl, downloadFile);
                helper.download();
            }
        }

        @Override
        public void handleRespone(Throwable error) {
            log("下载失败");
            mIsUpdating = false;
        }
    }

    public void onEventAsync(DownloadFile downloadFile) {
        if (downloadFile == null) return;
        String filePath = downloadFile.getFilePath();
        if (TextUtils.isEmpty(filePath)) {
            log("更新文件下载失败！");
            mIsUpdating = false;
        } else {
            File file = new File(filePath);
            log("文件是否下载成功：" + file.exists());
            if (file.exists()) {
                Uri uri = Uri.fromFile(new File(filePath));
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
                mIsUpdating = true;
            } else {
                mIsUpdating = false;
            }

        }
    }
}
