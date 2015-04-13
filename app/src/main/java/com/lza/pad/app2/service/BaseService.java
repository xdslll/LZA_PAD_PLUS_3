package com.lza.pad.app2.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.lza.pad.helper.RequestHelper;
import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.utils.Consts;
import com.lza.pad.support.utils.Utility;

import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/13/15.
 */
public abstract class BaseService extends Service implements Consts {

    protected Context mCtx;

    @Override
    public void onCreate() {
        super.onCreate();
        mCtx = getBaseContext();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected void send(String url, RequestHelper.OnRequestListener listener) {
        //RequestHelper.getInstance(getBaseContext(), url, listener).sendByVolley();
        RequestHelper.getInstance(getBaseContext(), url, listener).send();
    }

    protected String wrap(String value, String defaultValue) {
        return TextUtils.isEmpty(value) ? defaultValue : value;
    }

    protected int parseInt(String value) {
        return Utility.safeIntParse(value, 0);
    }

    protected boolean isEmpty(String str) {
        return TextUtils.isEmpty(str);
    }

    protected <T> boolean isEmpty(List<T> data) {
        return data == null || data.size() <= 0;
    }

    protected void log(String msg) {
        AppLogger.e("---------------- " + msg + " ----------------");
    }

    protected <T> T pickFirst(List<T> data) {
        if (isEmpty(data)) return null;
        return data.get(0);
    }

    protected  <T> void clear(List<T> data) {
        if (isEmpty(data)) return;
        data.clear();
    }

    protected String buildCodePath(String activityPath) {
        String packageName = getPackageName();
        StringBuffer buffer = new StringBuffer();
        buffer.append(packageName).append(".").append(activityPath);
        return buffer.toString();
    }

}
