package com.lza.pad.app;

import android.app.Application;

import com.lza.pad.helper.RequestHelper;
import com.lza.pad.support.utils.Consts;
import com.lza.pad.support.utils.RuntimeUtility;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/4/15.
 */
public class MainApplication extends Application implements Consts {

    private static MainApplication mCtx;

    public static MainApplication getInstance() {
        return mCtx;
    }

    public static String DEFAULT_URL = "http://114.212.7.87/book_center/interface.cx?";

    public String getUrl() {
        return RuntimeUtility.getFromSP(this, KEY_URL, DEFAULT_URL);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mCtx = this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        RequestHelper.releaseService();
    }
}
