package com.lza.pad.app2.event.menu;

import android.content.Context;

import com.actionbarsherlock.view.MenuItem;
import com.lza.pad.app2.base.IWidget;

import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/9/15.
 */
public interface BaseMenuEvent {

    /**
     * 菜单的点击事件
     *
     * @param item
     * @param widgets
     * @param context
     */
    void event(MenuItem item, List<IWidget> widgets, Context context);

}
