package com.lza.pad.helper;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.lza.pad.event.model.ResponseEventInfo;
import com.lza.pad.event.state.ResponseEventTag;
import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.utils.Consts;
import com.lza.pad.support.utils.ToastUtils;

import de.greenrobot.event.EventBus;

/**
 * 通过IPC发送请求，并接受响应结果
 *
 * @author xiads
 * @Date 11/4/14.
 */
public class RequestHelper implements Consts {

    private String mOriginalResponseData;

    private String mRequestUrl;

    private Activity mActivity;

    private IRequest mResponseSevice = null;

    private ServiceConnection mServiceConnection = null;

    private ResponseReceiver mResponseReceiver = null;

    private RequestHelper(Activity activity, String requestUrl) {
        this.mActivity = activity;
        this.mRequestUrl = requestUrl;

        mServiceConnection = new RequestServiceConnection();
        mResponseReceiver = new ResponseReceiver();
        mActivity.registerReceiver(mResponseReceiver, new IntentFilter(INTENT_ACTION_RESPONSE_RECEIVER));
        mActivity.bindService(new Intent(INTENT_ACTION_NEW_API_SERVICE), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private static RequestHelper sInstance = null;

    public static RequestHelper getInstance(Activity activity, String requestUrl) {
        if (sInstance == null) {
            sInstance = new RequestHelper(activity, requestUrl);
        }
        return sInstance;
    }

    public static void sendRequest(Activity activity, String requestUrl) {
        if (sInstance == null) {
            getInstance(activity, requestUrl);
        } else {
            sInstance.mRequestUrl = requestUrl;
            sInstance.send();
        }
    }

    public static void releaseService() {
        if (sInstance != null) {
            sInstance.release();
        }
    }

    private void send() {
        if (mResponseSevice != null && !TextUtils.isEmpty(mRequestUrl)) {
            try {
                mResponseSevice.doRequest(mRequestUrl);
                AppLogger.e("RequestHelper --> " + mRequestUrl);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void release() {
        try {
            if (mServiceConnection != null) {
                mActivity.unbindService(mServiceConnection);
            }
            if (mResponseReceiver != null) {
                mActivity.unregisterReceiver(mResponseReceiver);
            }
            sInstance = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private class RequestServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mResponseSevice = IRequest.Stub.asInterface(service);
            send();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mResponseSevice = null;
            mServiceConnection = null;
            ToastUtils.showShort(mActivity, "服务连接中断！");
        }
    }

    private class ResponseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ResponseEventInfo responseInfo = new ResponseEventInfo();
            responseInfo.setUrl(mRequestUrl);
            if (intent != null) {
                String responseCode = intent.getStringExtra(KEY_RESPONSE_CODE);
                if (!TextUtils.isEmpty(responseCode)) {
                    mOriginalResponseData = intent.getStringExtra(KEY_COMMON_RESPONSE);
                    AppLogger.e("RequestHelper --> " + mOriginalResponseData);
                    if (responseCode.equals(INTENT_ACTION_RESPONSE_OK)) {
                        responseInfo.setTag(ResponseEventTag.ON_RESONSE);
                        responseInfo.setResponseData(mOriginalResponseData);
                    } else {
                        responseInfo.setTag(ResponseEventTag.ON_ERROR);
                        responseInfo.setErrorMessage(mOriginalResponseData);
                    }
                } else {
                    responseInfo.setTag(ResponseEventTag.NO_RESPONSE);
                }
            } else {
                responseInfo.setTag(ResponseEventTag.NO_RESPONSE);
            }
            EventBus.getDefault().post(responseInfo);
        }
    }
}
