package com.lza.pad.app2.ui.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.lza.pad.db.model.pad._PadDeviceInfo;
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

    protected void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private void _updateProgressDialog(String msg) {
        if (mProgressDialog == null || !mProgressDialog.isShowing()) return;
        mProgressDialog.setMessage(msg);
    }

    protected void showProgressDialog(String msg) {
        _showProgressDialog(msg);
    }

    protected void showProgressDialog(int resId) {
        _showProgressDialog(getResources().getString(resId), true);
    }

    protected void showProgressDialog(int resId, boolean cancelable) {
        _showProgressDialog(getResources().getString(resId), cancelable);
    }

    protected void updateProgressDialog(String msg) {
        _updateProgressDialog(msg);
    }

    protected void updateProgressDialog(int resId) {
        updateProgressDialog(getResources().getString(resId));
    }

    protected boolean isProgressDialogShowing() {
        return mProgressDialog != null && mProgressDialog.isShowing();
    }

    private Handler mHandler = new Handler();

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

    protected Handler getMainHandler() {
        return mHandler;
    }

    protected void installApk(File file) {
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(intent);
    }

    protected void requestUpdateDeviceInfo(_PadDeviceInfo deviceInfo) {
        String url = UrlHelper.updateDeviceInfoUrl(deviceInfo);
        send(url, new UpdateDeviceInfoListener(deviceInfo));
    }

    private class UpdateDeviceInfoListener extends SimpleRequestListener {

        _PadDeviceInfo deviceInfo;

        private UpdateDeviceInfoListener(_PadDeviceInfo deviceInfo) {
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

    protected void onDeviceUpdateSuccess(_PadDeviceInfo deviceInfo) {};
    protected void onDeviceUpdateFailed(_PadDeviceInfo deviceInfo) {};

}
