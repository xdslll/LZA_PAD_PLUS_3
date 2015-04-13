package com.lza.pad.app2.event.base;

import android.support.v4.view.ViewPager;

import com.lza.pad.app2.service.ServiceMode;

import de.greenrobot.event.EventBus;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/26/15.
 */
public class OnPageChangeListener implements ViewPager.OnPageChangeListener {

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        EventBus.getDefault().post(ServiceMode.MODE_RESET_SERVICE);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
