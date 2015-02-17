package com.lza.pad.app.base;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.lza.pad.app.socket.admin.server.OnServerIoAdapter;
import com.lza.pad.app.socket.admin.server.ServerMessageHandler;
import com.lza.pad.app.socket.model.MinaClient;
import com.lza.pad.app.socket.service.MinaServiceHelper;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.helper.CommonRequestListener;
import com.lza.pad.helper.GsonHelper;
import com.lza.pad.helper.RequestHelper;
import com.lza.pad.helper.UrlHelper;
import com.lza.pad.service.UpdateDeviceService;
import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.utils.Consts;

import org.apache.mina.core.session.IoSession;

import de.greenrobot.event.EventBus;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/5/15.
 */
public class BaseActivity extends Activity implements Consts {

    public static final String JSON_LAYOUT_MODULE = "json_layout_module";
    public static final String JSON_MODULE_CONTROL = "json_module_control";

    /**
     * 重要的状态，Activity的更新状态，关系到界面的实时更新，只有首页才能进行实时更新
     */

    /**
     * 未更新界面，默认状态
     */
    public static final int ACTIVITY_STATE_NOT_UPDATE = 0x01;

    /**
     * 正在获取数据
     */
    public static final int ACTIVITY_STATE_GETTING_DATA = 0x02;

    /**
     * 获取数据成功
     */
    public static final int ACTIVITY_STATE_GET_DATA_SUCCESS = 0x03;

    /**
     * 获取数据失败
     */
    public static final int ACTIVITY_STATE_GET_DATA_FAILED = 0x04;

    /**
     * 界面需要更新
     */
    public static final int ACTIVITY_STATE_NEED_UPDATE = 0x05;

    /**
     * 界面已经更新
     */
    public static final int ACTIVITY_STATE_HAVE_UPDATED = 0x06;

    /**
     * 正在更新
     */
    public static final int ACTIVITY_STATE_UPDATING = 0x07;

    /**
     * 请求的延迟
     */
    protected static final int DEFAULT_REQUEST_DELAY = 0;

    public static final int DEFAULT_SIZE = 4;

    protected Context mCtx;

    protected MinaServiceHelper mMinaServiceHelper;

    protected int mActivityUpdateState = ACTIVITY_STATE_NOT_UPDATE;

    /**
     * 是否为首页
     */
    protected boolean mIsHome = false;

    /**
     * 用于接收更新界面服务提交的请求
     */
    private UpdateReceiver mUpdateReceiver;

    private Intent mIntentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCtx = this;

        //全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //启动异常监控
        //CrashHelper.getInstance(this).init();

        mMinaServiceHelper = MinaServiceHelper.instance();
        if (mMinaServiceHelper.isStarted()) {
            mMinaServiceHelper.setOnServerIoAdapter(mMinaServerListener);
        }

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsHome) {
            //启动界面自动更新服务
            mIntentService = new Intent(ACTION_UPDATE_DEVICE_SERVICE);
            startService(mIntentService);
            if (mUpdateReceiver == null) {
                //如果是首页，则开始监听更新的状况
                log("注册UpdateReceiver");
                mUpdateReceiver = new UpdateReceiver();
                IntentFilter filter = new IntentFilter();
                filter.addAction(ACTION_UPDATE_DEVICE_RECEIVER);
                registerReceiver(mUpdateReceiver, filter);
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mIsHome) {
            //回传给更新界面服务，使之停止运行
            UpdateDeviceService.UpdateCallback callback = new UpdateDeviceService.UpdateCallback();
            callback.isRunning = false;
            EventBus.getDefault().post(callback);
            //停止监听
            log("注销UpdateReceiver");
            unregisterReceiver(mUpdateReceiver);
        }
    }

    protected void launchFragment(Fragment fragment, int id) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(id, fragment);
        ft.commit();
    }

    protected void launchFragment(Fragment fragment, int id, int w, int h) {
        launchFragment(fragment, id, w, h, true);
    }

    protected void launchFragment(Fragment fragment, int id, int w, int h, boolean ifHome) {
        Bundle arg = new Bundle();
        arg.putInt(KEY_FRAGMENT_WIDTH, w);
        arg.putInt(KEY_FRAGMENT_HEIGHT, h);
        arg.putBoolean(KEY_IF_HOME, ifHome);
        fragment.setArguments(arg);
        launchFragment(fragment, id);
    }

    private ServerMessageHandler.OnServerIoListener mMinaServerListener = new OnServerIoAdapter() {
        @Override
        public void onMessageReceived(IoSession session, Object message) {
            Gson gson = GsonHelper.instance();
            try {
                MinaClient client = gson.fromJson(message.toString(), MinaClient.class);
                client.setSession(session);
                mMinaServiceHelper.addClient(client);
                if (client.getAction().equals(MinaClient.ACTION_CONNECT)) {
                    //onMinaClientConnect(client);
                    EventBus.getDefault().post(client);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    protected boolean checkMinaClient(MinaClient client) {
        if (client != null && client.getSession() != null) return true;
        return false;
    }

    /**
     * 处理客户端连接请求
     *
     * @param client
     */
    public void onEvent(MinaClient client) {

    }

    /**
     * 在主线程处理客户端连接请求
     *
     * @param client
     */
    public void onEventMainThread(MinaClient client) {

    }

    protected ProgressDialog mProgressDialog = null;

    protected void showProgressDialog(final String msg, boolean ifDismiss) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
        }
        if (ifDismiss && mProgressDialog.isShowing()) {
            dismissProgressDialog();
        }
        mProgressDialog.setMessage(msg);
        mProgressDialog.show();
    }

    protected void showProgressDialog(final String msg) {
        showProgressDialog(msg, true);
    }

    protected void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    protected void updateProgressDialog(final String msg) {
        if (mProgressDialog == null || !mProgressDialog.isShowing()) return;
        mProgressDialog.setMessage(msg);
    }

    protected void log(String msg) {
        AppLogger.e("---------------- " + msg + " ----------------");
    }

    public void logState() {
        int state = mActivityUpdateState;
        if (state == ACTIVITY_STATE_NOT_UPDATE) {
            log("状态：暂未更新");
        } else if (state == ACTIVITY_STATE_GETTING_DATA) {
            log("状态：正在获取数据...");
        } else if (state == ACTIVITY_STATE_GET_DATA_SUCCESS) {
            log("状态：获取数据成功");
        } else if (state == ACTIVITY_STATE_GET_DATA_FAILED) {
            log("状态：获取数据失败");
        } else if (state == ACTIVITY_STATE_NEED_UPDATE) {
            log("状态：需要更新界面");
        } else if (state == ACTIVITY_STATE_HAVE_UPDATED) {
            log("状态：界面已经更新");
        } else if (state == ACTIVITY_STATE_UPDATING) {
            log("状态：正在更新界面");
        } else {
            log("状态：未知...请核实");
        }
    }

    public int getUpdateState() {
        return mActivityUpdateState;
    }

    public boolean isHome() {
        return mIsHome;
    }

    /**
     * 接收更新设备信息的服务，如果设备信息有更新，需要启动该Receiver去接收
     */
    private class UpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            onUpdateReceive(context, intent);
        }
    }

    public void onUpdateReceive(Context context, Intent intent) {}

    /**
     * 发送请求给服务器，更新设备信息
     *
     * @param deviceInfo
     */
    protected void requestUpdateDeviceInfo(final PadDeviceInfo deviceInfo, String key, String value) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                updateProgressDialog("正在更新设备状态");
            }
        });

        String url = UrlHelper.updateDeviceInfoUrl(deviceInfo, key, value);
        RequestHelper.getInstance(mCtx, url, new CommonRequestListener() {
            @Override
            public void handleResponseSuccess() {
                log("设备状态更新成功");
                onDeviceUpdateSuccess(deviceInfo);
                //终止进程
                System.exit(0);
            }

            @Override
            public void onResponseStateError(ResponseData response) {
                log("设备状态更新失败");
                onDeviceUpdateFailed(deviceInfo);
                //终止进程
                System.exit(0);
            }
        }).send();
    }

    protected void onDeviceUpdateSuccess(PadDeviceInfo deviceInfo) {};
    protected void onDeviceUpdateFailed(PadDeviceInfo deviceInfo) {};
}

/**
 * 客户端发起连接请求时
 *
 * @param client
 */
    /*protected void onMinaClientConnect(MinaClient client) {
        if (client == null || client.getSession() == null) return;
        AppLogger.e("接收到客户端连接请求[" + client.getSession().getRemoteAddress() + "]");
    }*/

/*private int mScreenType = ACTIVITY_ALWAYS_SCREEN;
    private static final int ACTIVITY_NO_SCREEN = 0;
    private static final int ACTIVITY_ALWAYS_SCREEN = 1;

    protected void setScreenType(int type) {
        mScreenType = type;
    }

    protected void setNoScreen() {
        setScreenType(ACTIVITY_NO_SCREEN);
    }

    protected void setAlwaysScreen() {
        setScreenType(ACTIVITY_ALWAYS_SCREEN);
    }

    protected void sendScreen() {
        AppLogger.e("Screen Type:" + mScreenType);
        if (mScreenType == ACTIVITY_ALWAYS_SCREEN) {
            //mApp.sendScreen();
        }
    }

    private byte[] saveScreenView() {
        View decoreView = getWindow().getDecorView();
        decoreView.setDrawingCacheEnabled(true);
        decoreView.buildDrawingCache();
        Bitmap bmp = decoreView.getDrawingCache();

        if (bmp != null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bmp.compressQuality(Bitmap.CompressFormat.JPEG, 10, bos);
            return bos.toByteArray();
        } else {
            return null;
        }
    }*/
