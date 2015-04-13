package com.lza.pad.app2.event.base;

import android.widget.AbsListView;

import com.lza.pad.app2.service.ServiceMode;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.greenrobot.event.EventBus;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 4/1/15.
 */
public class PauseOnScrollListener extends com.nostra13.universalimageloader.core.listener.PauseOnScrollListener {

    public PauseOnScrollListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling) {
        super(imageLoader, pauseOnScroll, pauseOnFling);
    }

    public PauseOnScrollListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling, AbsListView.OnScrollListener customListener) {
        super(imageLoader, pauseOnScroll, pauseOnFling, customListener);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        super.onScrollStateChanged(view, scrollState);
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            EventBus.getDefault().post(ServiceMode.MODE_RESET_SERVICE);
        }
    }

}
