package com.lza.pad.app2.event.menu;

import android.content.Context;

import com.actionbarsherlock.view.MenuItem;
import com.lza.pad.app2.base.IWidget;
import com.lza.pad.support.utils.ToastUtils;

import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/9/15.
 */
public class SubjectMenuEvent implements BaseMenuEvent {

    @Override
    public void event(MenuItem item, List<IWidget> widgets, Context context) {
        ToastUtils.showLong(context, "Hello World!");
    }
}
