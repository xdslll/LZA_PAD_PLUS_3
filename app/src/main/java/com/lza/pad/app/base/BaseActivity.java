package com.lza.pad.app.base;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.lza.pad.app.MainApplication;
import com.lza.pad.helper.CrashHelper;
import com.lza.pad.support.utils.Consts;

import java.io.ByteArrayOutputStream;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/5/15.
 */
public class BaseActivity extends Activity implements Consts {

    public static final int DEFAULT_SIZE = 4;

    MainApplication mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //启动异常监控
        CrashHelper.getInstance(this).init();

        mApp = (MainApplication) getApplication();
        mApp.setOnClientListener(new MainApplication.OnClientListener() {
            @Override
            public byte[] captureScreen() {
                return saveScreenView();
            }
        });
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

    private static final int REQUEST_SAVE_SCREEN = 0x01;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == REQUEST_SAVE_SCREEN) {
                saveScreenView();
            }
        }
    };

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
    }

}
