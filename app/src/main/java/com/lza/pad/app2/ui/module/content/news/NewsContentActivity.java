package com.lza.pad.app2.ui.module.content.news;

import android.support.v4.app.Fragment;

import com.lza.pad.app2.ui.module.content.BaseContentActivity;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/3/19.
 */
public class NewsContentActivity extends BaseContentActivity {

    @Override
    protected String getModName() {
        return "新闻正文";
    }

    @Override
    protected Fragment getFragment() {
        return new NewsContentFragment();
    }
}
