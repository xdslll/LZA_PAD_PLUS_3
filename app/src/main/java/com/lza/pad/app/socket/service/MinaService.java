package com.lza.pad.app.socket.service;

import android.app.IntentService;
import android.content.Intent;

import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.utils.Consts;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/2/5.
 */
public class MinaService extends IntentService implements Consts {

    public MinaService() {
        super("MinaService");
    }

    MinaServiceHelper mMinaServiceHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        AppLogger.e("[" + Thread.currentThread().getName() + "]:MinaService onCreate");
        mMinaServiceHelper = MinaServiceHelper.instance();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppLogger.e("[" + Thread.currentThread().getName() + "]:MinaService onHandleIntent");

        String action = intent.getStringExtra(KEY_MINA_SERVER_ACTION);
        if (action.equals(ACTION_START_SERVER)) {
            mMinaServiceHelper.startMinaServer();
            if (mMinaServiceHelper.isStarted())
                AppLogger.e("[" + Thread.currentThread().getName() + "]Mina服务启动成功！");
        } else if (action.equals(ACTION_STOP_SERVER)){
            if (mMinaServiceHelper.isStarted())
                mMinaServiceHelper.stopMinaServer();
        }
    }
}
