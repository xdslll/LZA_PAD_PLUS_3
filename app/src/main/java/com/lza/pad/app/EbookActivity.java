package com.lza.pad.app;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.lza.pad.R;
import com.lza.pad.app.base.BaseActivity;
import com.lza.pad.fragment.EbookListFragment2;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/18/15.
 */
public class EbookActivity extends BaseActivity {

    private LinearLayout mMainContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        mMainContainer = (LinearLayout) findViewById(R.id.home);

        //获取屏幕尺寸
        int w, h, size = 4;
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        w = metrics.widthPixels;
        h = metrics.heightPixels;

        for (int i = 0; i < size; i++) {
            int id = (i + 1) << (i + 1);
            FrameLayout subContainer = new FrameLayout(this);
            subContainer.setLayoutParams(new ViewGroup.LayoutParams(w, h / size));
            subContainer.setId(id);
            mMainContainer.addView(subContainer);

            if (i == 0) {
                EbookListFragment2 fragment = new EbookListFragment2();
                launchFragment(fragment, id);
            } else if (i == 1) {
                EbookListFragment2 fragment = new EbookListFragment2();
                launchFragment(fragment, id);
            } else if (i == 2) {
                EbookListFragment2 fragment = new EbookListFragment2();
                launchFragment(fragment, id);
            } else {
                EbookListFragment2 fragment = new EbookListFragment2();
                launchFragment(fragment, id);
            }

        }
    }
}
