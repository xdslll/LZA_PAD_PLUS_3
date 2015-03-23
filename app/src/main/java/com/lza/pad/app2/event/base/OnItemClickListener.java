package com.lza.pad.app2.event.base;

import android.view.View;
import android.widget.AdapterView;

import com.lza.pad.app2.service.ServiceMode;

import de.greenrobot.event.EventBus;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/3/20.
 */
public class OnItemClickListener implements AdapterView.OnItemClickListener {

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        EventBus.getDefault().post(ServiceMode.MODE_RESET_SERVICE);
    }
}
