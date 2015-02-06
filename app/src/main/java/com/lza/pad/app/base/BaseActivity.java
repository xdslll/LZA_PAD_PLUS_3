package com.lza.pad.app.base;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.lza.pad.app.socket.admin.server.OnServerIoAdapter;
import com.lza.pad.app.socket.admin.server.ServerMessageHandler;
import com.lza.pad.app.socket.model.MinaClient;
import com.lza.pad.app.socket.service.MinaServiceHelper;
import com.lza.pad.event.model.ResponseEventInfo;
import com.lza.pad.helper.CrashHelper;
import com.lza.pad.helper.GsonHelper;
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

    public static final int DEFAULT_SIZE = 4;

    protected Context mCtx;

    protected MinaServiceHelper mMinaServiceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCtx = this;

        //全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //启动异常监控
        CrashHelper.getInstance(this).init();

        mMinaServiceHelper = MinaServiceHelper.instance();
        if (mMinaServiceHelper.isStarted()) {
            mMinaServiceHelper.setOnServerIoAdapter(mMinaServerListener);
        }

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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

    /**
     * 处理网络请求
     *
     * @param response
     */
    public void onEvent(ResponseEventInfo response) {

    }

    /**
     * 在主线程处理网络请求
     *
     * @param response
     */
    public void onEventMainThread(ResponseEventInfo response) {

    }

    protected ProgressDialog mProgressDialog = null;

    protected void showProgressDialog(String msg, boolean ifDismiss) {
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

    protected void showProgressDialog(String msg) {
        showProgressDialog(msg, true);
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
            bmp.compress(Bitmap.CompressFormat.JPEG, 10, bos);
            return bos.toByteArray();
        } else {
            return null;
        }
    }*/
