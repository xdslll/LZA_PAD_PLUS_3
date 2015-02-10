package com.lza.pad.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.event.model.ResponseEventInfo;
import com.lza.pad.event.state.ResponseEventTag;
import com.lza.pad.helper.JsonParseHelper;
import com.lza.pad.helper.RequestHelper;
import com.lza.pad.helper.UrlHelper;
import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.utils.Consts;
import com.lza.pad.support.utils.UniversalUtility;

import de.greenrobot.event.EventBus;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/2/10.
 */
public class UpdateService extends IntentService implements Consts, RequestHelper.OnRequestListener {

    private PadDeviceInfo mDeviceInfo;
    private Context mContext;
    private String mRequestUrl;
    private String mUpdateTag;

    private boolean mIsUpdating = false;
    private boolean mIsRunning = true;

    private Object mLock = new Object();

    /**
     * 每隔5秒更新一次
     */
    private static int mUpdateTime = 5 * 1000;

    public UpdateService() {
        super("UpdateService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getBaseContext();
        String macAddress = UniversalUtility.getMacAddress(this);
        mRequestUrl = UrlHelper.getDeviceUrl(macAddress);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        log("正在启动服务");
        mHandler.sendEmptyMessageDelayed(REQUEST_UPDATE_STATE, mUpdateTime);
        EventBus.getDefault().register(UpdateService.this);
    }

    public static final int REQUEST_UPDATE_STATE = 0x01;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!mIsRunning) {
                log("正在停止服务");
                EventBus.getDefault().unregister(UpdateService.this);
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
                        RequestHelper.getInstance(mContext, UpdateService.this, mRequestUrl).send();
                    }
                }
            }
        }
    };

    private String createUrl() {
        String macAddress = UniversalUtility.getMacAddress(this);
        return UrlHelper.getDeviceUrl(macAddress);
    }

    @Override
    public void onResponse(ResponseEventInfo response) {
        if (response == null) return;
        if (response.getTag() == ResponseEventTag.ON_RESONSE) {
            String json = response.getResponseData();
            final ResponseData<PadDeviceInfo> data = JsonParseHelper.parseDeviceInfoResponse(json);
            String state = data.getState();
            if (state.equals(ResponseData.RESPONSE_STATE_OK)) {
                if (data.getContent() == null || data.getContent().size() <= 0) return;
                mDeviceInfo = data.getContent().get(0);
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
                    }
                } else if (mUpdateTag.equals(PadDeviceInfo.TAG_HAVE_UDPATE)) {
                    log("已经更新");
                } else {
                    log("未知状态");
                }
            }
            mHandler.sendEmptyMessageDelayed(REQUEST_UPDATE_STATE, mUpdateTime);
        } else {
            log(response.getErrorMessage());
            mHandler.sendEmptyMessageDelayed(REQUEST_UPDATE_STATE, mUpdateTime);
        }
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
    }
}
