package com.lza.pad.app.test;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.lza.pad.R;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 2/19/15.
 */
public class TestActivity2 extends Activity {

    ViewPager mViewPager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_module_container);
    }
}
