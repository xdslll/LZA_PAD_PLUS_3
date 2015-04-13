package com.lza.pad.app2.ui.base;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.lza.pad.R;
import com.lza.pad.app.socket.model.MinaClient;
import com.lza.pad.app2.ui.device.DeviceAuthorityActivity;
import com.lza.pad.db.model.DownloadFile;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.helper.RequestHelper;
import com.lza.pad.helper.SimpleRequestListener;
import com.lza.pad.helper.UrlHelper;
import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.utils.Consts;
import com.lza.pad.support.utils.Utility;
import com.umeng.analytics.MobclickAgent;

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
    protected final static int RETRY_TIMEOUT = 10 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCtx = this;

        //全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //不显示ActionBar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //CrashHelper.getInstance(mCtx).init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
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
        return Utility.safeIntParse(value, 0);
    }

    protected int parseInt(String value, int defaultValue) {
        return Utility.safeIntParse(value, defaultValue);
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

    protected boolean isRegisterEventBus() {
        return EventBus.getDefault().isRegistered(this);
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

    protected void requestUpdateDeviceInfo(PadDeviceInfo deviceInfo, SimpleRequestListener listener) {
        String url = UrlHelper.updateDeviceInfoUrl(deviceInfo);
        send(url, listener);
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

    protected boolean isTopActivity() {
        String currentActivityName = getClass().getSimpleName();
        String topActivityName = getTopActivityName();
        boolean isTopActivity = currentActivityName.equals(topActivityName);
        log("是否为Top Activity：" + isTopActivity);
        return isTopActivity;
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

    /**
     * 处理失败流程，10秒后会自动关闭对话框并执行重试流程
     *
     * @param title         标题文本
     * @param message       内容文本
     * @param runnable      处理事件
     */
    protected void handleErrorProcess(String title, String message, final Runnable runnable) {
        log("处理失败信息");
        try {
            dismissProgressDialog();
            final AlertDialog alertDialog = new AlertDialog.Builder(mCtx)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(R.string.dialog_button_retry, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (runnable != null) {
                                runnable.run();
                            }
                        }
                    })
                    .create();
            alertDialog.show();
            getMainHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    alertDialog.dismiss();
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            }, RETRY_TIMEOUT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 处理客户端连接请求
     *
     * @param client
     */
    public void onEvent(MinaClient client) {}

    /**
     * 在主线程处理客户端连接请求
     *
     * @param client
     */
    public void onEventMainThread(MinaClient client) {}

    /**
     * 处理下载事件
     *
     * @param downloadFile
     */
    public void onEventAsync(DownloadFile downloadFile) {}

}
