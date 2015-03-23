package com.lza.pad.app2.ui.base;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.lza.pad.app2.ui.device.DeviceAuthorityActivity;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.helper.RequestHelper;
import com.lza.pad.helper.SimpleRequestListener;
import com.lza.pad.helper.UrlHelper;
import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.utils.Consts;
import com.lza.pad.support.utils.UniversalUtility;

import java.io.File;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/5/15.
 */
public abstract class BaseActivity extends SherlockFragmentActivity implements Consts {

    protected Context mCtx;

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

    protected void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    protected void updateProgressDialog(String msg) {
        if (mProgressDialog == null || !mProgressDialog.isShowing()) return;
        mProgressDialog.setMessage(msg);
    }

    protected void showProgressDialog(String msg) {
        _showProgressDialog(msg, true);
    }

    protected void showProgressDialog(int resId) {
        _showProgressDialog(getResources().getString(resId), true);
    }

    protected void showProgressDialog(String msg, boolean cancelable) {
        _showProgressDialog(msg, cancelable);
    }

    protected void showProgressDialog(int resId, boolean cancelable) {
        _showProgressDialog(getResources().getString(resId), cancelable);
    }

    protected void updateProgressDialog(int resId) {
        updateProgressDialog(getResources().getString(resId));
    }

    protected boolean isProgressDialogShowing() {
        return mProgressDialog != null && mProgressDialog.isShowing();
    }

    private Handler mHandler = new Handler();

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

    protected <T> boolean isEmpty(List<T> data) {
        return data == null || data.size() <= 0;
    }

    protected <T> T pickFirst(List<T> data) {
        if (isEmpty(data)) return null;
        return data.get(0);
    }

    protected  <T> void clear(List<T> data) {
        if (isEmpty(data)) return;
        data.clear();
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

    protected String buildCodePath(String activityPath) {
        String packageName = getPackageName();
        StringBuffer buffer = new StringBuffer();
        buffer.append(packageName).append(".").append(activityPath);
        return buffer.toString();
    }

    protected void backToDeviceAuthorityActivity() {
        startActivity(new Intent(mCtx, DeviceAuthorityActivity.class));
    }

    protected String getTopActivityName() {
        ActivityManager manager = (ActivityManager) mCtx.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = manager.getRunningTasks(1);
        if (runningTasks == null || runningTasks.size() == 0) return null;
        String className = runningTasks.get(0).topActivity.getShortClassName();
        if (TextUtils.isEmpty(className) || !className.contains(".")) return null;
        int index = className.lastIndexOf(".");
        return className.substring(index + 1, className.length());
    }

    protected int getRunningActivities() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = manager.getRunningTasks(1);
        if (isEmpty(runningTasks)) return 0;
        int numActivities = runningTasks.get(0).numActivities;
        log("numActivities:" + numActivities);
        return numActivities;
    }

    protected void launchFragment(Fragment fragment, int id) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(id, fragment);
        ft.commit();
    }

}
