package com.lza.pad.app;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.lza.pad.R;
import com.lza.pad.app.base.BaseActivity;
import com.lza.pad.fragment.EbookListFragment;
import com.lza.pad.fragment.TestFragment;
import com.lza.pad.fragment.TitleFragment;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/5/15.
 */
public class HomeActivity extends BaseActivity {

    private LinearLayout mMainContainer;

    private FragmentManager mFm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home);
        mMainContainer = (LinearLayout) findViewById(R.id.home);

        mFm = getFragmentManager();

        //获取屏幕尺寸
        int w, h, size = 4;
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        w = metrics.widthPixels;
        h = metrics.heightPixels;

        int[] colors = new int[] {
                R.color.white, R.color.white, R.color.white, R.color.white
        };
        for (int i = 0; i < size; i++) {
            int id = (i + 1) << (i + 1);
            FrameLayout subContainer = new FrameLayout(this);
            subContainer.setLayoutParams(new ViewGroup.LayoutParams(w, h / size));
            subContainer.setId(id);
            mMainContainer.addView(subContainer);

            if (i == 0) {
                TitleFragment fragment = new TitleFragment();
                Bundle arg = new Bundle();
                arg.putInt(KEY_FRAGMENT_WIDTH, w);
                arg.putInt(KEY_FRAGMENT_HEIGHT, h / size);
                fragment.setArguments(arg);
                FragmentTransaction ft = mFm.beginTransaction();
                ft.replace(id, fragment);
                ft.commit();
            } else if (i == 1) {
                EbookListFragment fragment = new EbookListFragment();
                FragmentTransaction ft = mFm.beginTransaction();
                ft.replace(id, fragment);
                ft.commit();
            } else {
                TestFragment fragment = new TestFragment();
                Bundle arg = new Bundle();
                arg.putString("data", "Hello " + i);
                arg.putInt("color", colors[i - 1]);
                fragment.setArguments(arg);
                FragmentTransaction ft = mFm.beginTransaction();
                ft.replace(id, fragment);
                ft.commit();
            }
        }
    }

}

/*if (i == 1) {
                VideoFragment fragment = new VideoFragment();
                FragmentTransaction ft = mFm.beginTransaction();
                ft.replace(id, fragment);
                ft.commit();
            } else if(i == 0) {
                EbookListFragment fragment = new EbookListFragment();
                FragmentTransaction ft = mFm.beginTransaction();
                ft.replace(id, fragment);
                ft.commit();
            } else {
                TestFragment fragment = new TestFragment();
                Bundle arg = new Bundle();
                arg.putString("data", "Hello " + i);
                arg.putInt("color", colors[i]);
                fragment.setArguments(arg);
                FragmentTransaction ft = mFm.beginTransaction();
                ft.replace(id, fragment);
                ft.commit();
            }*/