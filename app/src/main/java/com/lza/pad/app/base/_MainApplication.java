package com.lza.pad.app.base;

import android.app.Application;

import com.lza.pad.app.socket.admin.file.MinaFileServerAdmin;
import com.lza.pad.app.socket.admin.server.MinaServerHelper;
import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.utils.Consts;
import com.lza.pad.support.utils.RuntimeUtility;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/2/4.
 */
@Deprecated
public class _MainApplication extends Application implements Consts {

    private static _MainApplication mCtx;

    public static _MainApplication getInstance() {
        return mCtx;
    }

    public static String DEFAULT_URL = "http://pad2.smartlib.cn/interface.cx?";
    //public static String DEFAULT_URL = "http://114.212.7.87/book_center/interface.cx?";

    private MinaServerHelper mMinaServerHelper;
    private MinaFileServerAdmin mMinaFileServerAdmin;

    public String getUrl() {
        return RuntimeUtility.getFromSP(this, KEY_URL, DEFAULT_URL);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mCtx = this;
        startMinaServer();
        mMinaFileServerAdmin = MinaFileServerAdmin.getInstance();
        mMinaFileServerAdmin.start();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        stopMinaServer();
        mMinaFileServerAdmin.stop();
    }

    private void startMinaServer() {
        AppLogger.e("[" + Thread.currentThread().getName() + "]:开始启动Mina服务");

        mMinaServerHelper = MinaServerHelper.instance();
        boolean ret = mMinaServerHelper.startMinaServer();
        if (ret) {
            AppLogger.e("Mina服务启动成功！");
        } else {
            AppLogger.e("Mina服务启动失败！");
        }
    }

    private boolean stopMinaServer() {
        AppLogger.e("[" + Thread.currentThread().getName() + "]:开始关闭Mina服务");
        return mMinaServerHelper.stopMinaServer();
    }
}
