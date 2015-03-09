package com.lza.pad.app2.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.helper.RequestHelper;
import com.lza.pad.helper.SimpleRequestListener;
import com.lza.pad.helper.UrlHelper;
import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.utils.Consts;
import com.lza.pad.support.utils.UniversalUtility;

import java.io.File;

import de.greenrobot.event.EventBus;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/5/15.
 */
public abstract class BaseActivity extends SherlockFragmentActivity implements Consts {

    protected Context mCtx;

    private int mDefaultDelay = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCtx = this;

        //全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //不显示ActionBar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    private ProgressDialog mProgressDialog = null;

    private void _showProgressDialog(String msg, boolean cancelable) {
        mProgressDialog = new ProgressDialog(mCtx);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(cancelable);

        mProgressDialog.setMessage(msg);
        mProgressDialog.show();
    }

    private void _showProgressDialog(String msg) {
        _showProgressDialog(msg, true);
    }

    private void _dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private void _updateProgressDialog(String msg) {
        if (mProgressDialog == null || !mProgressDialog.isShowing()) return;
        mProgressDialog.setMessage(msg);
    }

    protected void showProgressDialog(String msg, boolean cancelable) {
        Message message = Message.obtain(mHandler, REQUEST_SHOW_DIALOG, msg);
        Bundle data = new Bundle();
        data.putBoolean(KEY_CANCELABLE, cancelable);
        message.setData(data);
        mHandler.sendMessageDelayed(message, mDefaultDelay);
    }

    protected void showProgressDialog(String msg) {
        showProgressDialog(msg, true);
    }

    protected void showProgressDialog(int resId) {
        showProgressDialog(getResources().getString(resId), true);
    }

    protected void showProgressDialog(int resId, boolean cancelable) {
        showProgressDialog(getResources().getString(resId), cancelable);
    }

    protected void updateProgressDialog(String msg) {
        Message message = Message.obtain(mHandler, REQUEST_UPDATE_DIALOG, msg);
        mHandler.sendMessageDelayed(message, mDefaultDelay);
    }

    protected void updateProgressDialog(int resId) {
        updateProgressDialog(getResources().getString(resId));
    }

    protected void dismissProgressDialog() {
        Message message = Message.obtain(mHandler, REQUEST_DISMISS_DIALOG);
        mHandler.sendMessageDelayed(message, mDefaultDelay);
    }

    public static final int REQUEST_SHOW_DIALOG = 0x01;
    public static final int REQUEST_UPDATE_DIALOG = 0x02;
    public static final int REQUEST_DISMISS_DIALOG = 0x03;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == REQUEST_SHOW_DIALOG) {
                boolean cancelable = msg.getData().getBoolean(KEY_CANCELABLE);
                _showProgressDialog(msg.obj.toString(), cancelable);
            } else if (msg.what == REQUEST_UPDATE_DIALOG) {
                _updateProgressDialog(msg.obj.toString());
            } else if (msg.what == REQUEST_DISMISS_DIALOG) {
                _dismissProgressDialog();
            }
        }
    };

    protected void setDefaultDelay(int defaultDelay) {
        this.mDefaultDelay = defaultDelay;
    }

    protected void send(String url, RequestHelper.OnRequestListener listener) {
        RequestHelper.getInstance(mCtx, url, listener).send();
    }

    protected String wrap(String value, String defaultValue) {
        return TextUtils.isEmpty(value) ? defaultValue : value;
    }

    protected int parseInt(String value) {
        return UniversalUtility.safeIntParse(value, 0);
    }

    protected boolean isEmpty(String str) {
        return TextUtils.isEmpty(str);
    }

    protected void log(String msg) {
        AppLogger.e("---------------- " + msg + " ----------------");
    }

    protected void registerEventBus() {
        EventBus.getDefault().register(this);
    }

    protected void unregisterEventBus() {
        EventBus.getDefault().unregister(this);
    }

    protected void installApk(File file) {
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(intent);
    }

    protected void requestUpdateDeviceInfo(PadDeviceInfo deviceInfo) {
        String url = UrlHelper.updateDeviceInfoUrl(deviceInfo);
        send(url, new UpdateDeviceInfoListener(deviceInfo));
    }

    private class UpdateDeviceInfoListener extends SimpleRequestListener {

        PadDeviceInfo deviceInfo;

        private UpdateDeviceInfoListener(PadDeviceInfo deviceInfo) {
            this.deviceInfo = deviceInfo;
        }

        @Override
        public boolean handleResponseStatusOK(String json) {
            log("设备状态更新成功");
            onDeviceUpdateSuccess(deviceInfo);
            return true;
        }

        @Override
        public void handleResponseFailed() {
            log("设备状态更新失败");
            onDeviceUpdateFailed(deviceInfo);
        }
    }

    protected void onDeviceUpdateSuccess(PadDeviceInfo deviceInfo) {};
    protected void onDeviceUpdateFailed(PadDeviceInfo deviceInfo) {};

}
