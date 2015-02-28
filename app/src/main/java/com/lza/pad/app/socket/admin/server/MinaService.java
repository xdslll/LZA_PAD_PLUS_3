package com.lza.pad.app.socket.admin.server;

import android.app.IntentService;
import android.content.Intent;

import com.lza.pad.app.socket.admin.server.MinaServerHelper;
import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.utils.Consts;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/2/5.
 */
@Deprecated
public class MinaService extends IntentService implements Consts {

    public MinaService() {
        super("MinaService");
    }

    MinaServerHelper mMinaServerHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        AppLogger.e("[" + Thread.currentThread().getName() + "]:MinaService onCreate");
        mMinaServerHelper = MinaServerHelper.instance();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppLogger.e("[" + Thread.currentThread().getName() + "]:MinaService onHandleIntent");

        String action = intent.getStringExtra(KEY_MINA_SERVER_ACTION);
        if (action.equals(ACTION_START_SERVER)) {
            mMinaServerHelper.startMinaServer();
            if (mMinaServerHelper.isStarted())
                AppLogger.e("[" + Thread.currentThread().getName() + "]Mina服务启动成功！");
        } else if (action.equals(ACTION_STOP_SERVER)){
            if (mMinaServerHelper.isStarted())
                mMinaServerHelper.stopMinaServer();
        }
    }
}
