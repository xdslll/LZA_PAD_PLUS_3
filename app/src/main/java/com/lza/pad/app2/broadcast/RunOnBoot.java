package com.lza.pad.app2.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lza.pad.app2.ui.device.UniversalVerifyActivity;
import com.lza.pad.support.debug.AppLogger;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 14-9-25.
 */
public class RunOnBoot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        AppLogger.e("开机启动(action:" + intent.getAction() + ")");
        Intent startIntent = new Intent(context, UniversalVerifyActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startIntent);
    }
}
