package com.lza.pad.app2.application;

import android.app.Application;

import com.lza.pad.app.socket.admin.file.MinaFileServerAdmin;
import com.lza.pad.app.socket.admin.server.BaseServerIoAdapter;
import com.lza.pad.app.socket.admin.server.MinaServerHelper;
import com.lza.pad.support.debug.AppLogger;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/6/15.
 */
public class MainApplication extends Application {

    MinaServerHelper mMinaServer = MinaServerHelper.instance();
    MinaFileServerAdmin mMinaFileServerAdmin;

    @Override
    public void onCreate() {
        super.onCreate();
        mMinaFileServerAdmin = MinaFileServerAdmin.getInstance();

        startMinaServer();
        startMinaFileServer();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        stopMinaServer();
        stopMinaFileServer();
    }

    private void startMinaFileServer() {
        try {
            mMinaFileServerAdmin.start();
            if (mMinaFileServerAdmin.isActive()) {
                AppLogger.e("MinaFileServer服务已启动");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void stopMinaFileServer() {
        try {
            mMinaFileServerAdmin.stop();
            if (mMinaFileServerAdmin.isDisposed()) {
                AppLogger.e("MinaFileServer服务已关闭");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void startMinaServer() {
        try {
            mMinaServer.startMinaServer();
            mMinaServer.setOnServerIoAdapter(new BaseServerIoAdapter());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void stopMinaServer() {
        try {
            mMinaServer.stopMinaServer();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
