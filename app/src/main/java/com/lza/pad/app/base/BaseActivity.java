package com.lza.pad.app.base;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.lza.pad.helper.CrashHelper;
import com.lza.pad.support.utils.Consts;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/5/15.
 */
public class BaseActivity extends Activity implements Consts {

    public static final int DEFAULT_SIZE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //启动异常监控
        CrashHelper.getInstance(this).init();
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
}
