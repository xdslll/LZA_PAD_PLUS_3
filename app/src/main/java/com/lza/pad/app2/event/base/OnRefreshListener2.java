package com.lza.pad.app2.event.base;

import android.view.View;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.lza.pad.app2.service.ServiceMode;

import de.greenrobot.event.EventBus;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 4/1/15.
 */
public class OnRefreshListener2<V extends View> implements PullToRefreshBase.OnRefreshListener2<V> {

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<V> refreshView) {
        EventBus.getDefault().post(ServiceMode.MODE_RESET_SERVICE);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<V> refreshView) {
        EventBus.getDefault().post(ServiceMode.MODE_RESET_SERVICE);
    }
}
