package com.lza.pad.app.base;

import android.app.Application;
import android.os.Handler;

import com.lza.pad.app.socket.service.MinaServiceHelper;
import com.lza.pad.helper.RequestHelper;
import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.utils.Consts;
import com.lza.pad.support.utils.RuntimeUtility;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/2/4.
 */
public class MainApplication extends Application implements Consts {

    private static MainApplication mCtx;

    public static MainApplication getInstance() {
        return mCtx;
    }

    public static String DEFAULT_URL = "http://pad2.smartlib.cn/interface.cx?";
    //public static String DEFAULT_URL = "http://114.212.7.87/book_center/interface.cx?";

    public String getUrl() {
        return RuntimeUtility.getFromSP(this, KEY_URL, DEFAULT_URL);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mCtx = this;

        startMinaServer();

        RequestHelper.getInstance(mCtx);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        RequestHelper.releaseService();

        stopMinaServer();

        RequestHelper.releaseService();
    }

    private MinaServiceHelper mMinaServiceHelper;
    private Handler mHandler = new Handler();

    private void startMinaServer() {
        AppLogger.e("[" + Thread.currentThread().getName() + "]:开始启动Mina服务");

        mMinaServiceHelper = MinaServiceHelper.instance();
        boolean ret = mMinaServiceHelper.startMinaServer();
        //启动重试机制
        /*if (ret) {
            AppLogger.e("Mina服务启动成功！");
        } else {
            AppLogger.e("Mina服务启动失败！10秒后重试...");
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startMinaServer();
                }
            }, 10 * 1000);
        }*/
    }

    private boolean stopMinaServer() {
        AppLogger.e("[" + Thread.currentThread().getName() + "]:开始关闭Mina服务");

        return mMinaServiceHelper.stopMinaServer();
        //startMinaService(ACTION_STOP_SERVER);
    }

    /*private void startMinaService(String action) {
        Intent service = new Intent(ACTION_MINA_SERVICE);
        service.putExtra(KEY_MINA_SERVER_ACTION, action);
        mCtx.startService(service);
    }*/

}
