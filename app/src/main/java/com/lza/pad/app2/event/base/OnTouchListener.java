package com.lza.pad.app2.event.base;

import android.view.MotionEvent;
import android.view.View;

import com.lza.pad.app2.service.ServiceMode;

import de.greenrobot.event.EventBus;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/3/20.
 */
public class OnTouchListener implements View.OnTouchListener {
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        EventBus.getDefault().post(ServiceMode.MODE_RESET_SERVICE);
        return false;
    }
}
