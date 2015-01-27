package com.lza.pad.app;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.lza.pad.R;
import com.lza.pad.app.base.BaseActivity;
import com.lza.pad.fragment.EbookNormalListFragment;
import com.lza.pad.fragment.IrregularNewsFragment;
import com.lza.pad.fragment.TitleFragment;
import com.lza.pad.fragment._EbookListFragment;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/5/15.
 */
public class HomeActivity extends BaseActivity {

    private LinearLayout mMainContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.common_main_container);
        mMainContainer = (LinearLayout) findViewById(R.id.home);

        //获取屏幕尺寸
        int w, h, size = 4;
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        w = metrics.widthPixels;
        h = metrics.heightPixels;

        for (int i = 0; i < size; i++) {
            if (i == 0) {
                int id = (i + 1) << (i + 1);
                FrameLayout subContainer = new FrameLayout(this);
                subContainer.setLayoutParams(new ViewGroup.LayoutParams(w, h / size));
                subContainer.setId(id);
                mMainContainer.addView(subContainer);
                TitleFragment fragment = new TitleFragment();
                launchFragment(fragment, id);
            } else if (i == 1) {
                int id = (i + 1) << (i + 1);
                FrameLayout subContainer = new FrameLayout(this);
                subContainer.setLayoutParams(new ViewGroup.LayoutParams(w, h / size));
                subContainer.setId(id);
                mMainContainer.addView(subContainer);
                _EbookListFragment fragment = new _EbookListFragment();
                launchFragment(fragment, id, w, h / size);
            } else if (i == 2) {
                int id = (i + 1) << (i + 1);
                FrameLayout subContainer = new FrameLayout(this);
                subContainer.setLayoutParams(new ViewGroup.LayoutParams(w, h / size));
                subContainer.setId(id);
                mMainContainer.addView(subContainer);
                EbookNormalListFragment fragment = new EbookNormalListFragment();
                launchFragment(fragment, id, w, h / size);
            } else {
                int id = (i + 1) << (i + 1);
                FrameLayout subContainer = new FrameLayout(this);
                subContainer.setLayoutParams(new ViewGroup.LayoutParams(w, h / size));
                subContainer.setId(id);
                mMainContainer.addView(subContainer);
                IrregularNewsFragment fragment = new IrregularNewsFragment();
                launchFragment(fragment, id);
            }
            /*else if (i == 2) {
                int id = (i + 1) << (i + 1);
                FrameLayout subContainer = new FrameLayout(this);
                subContainer.setLayoutParams(new ViewGroup.LayoutParams(w, h / 2));
                subContainer.setId(id);
                mMainContainer.addView(subContainer);
                LibraryMapFragment fragment = new LibraryMapFragment();
                launchFragment(fragment, id, w, h / 2);
            } else {

            }*/

        }
    }

}