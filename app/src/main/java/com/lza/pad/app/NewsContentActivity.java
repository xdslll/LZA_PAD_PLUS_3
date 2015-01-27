package com.lza.pad.app;

import android.app.Fragment;
import android.os.Bundle;

import com.lza.pad.app.base.BaseContentActivity;
import com.lza.pad.fragment.NewsContentFragment;
import com.lza.pad.support.utils.RuntimeUtility;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/21/15.
 */
public class NewsContentActivity extends BaseContentActivity {

    @Override
    protected String getModName() {
        return "新闻正文";
    }

    @Override
    protected Fragment getFragment() {
        NewsContentFragment fragment = new NewsContentFragment();
        Bundle arg = new Bundle();
        arg.putInt(KEY_FRAGMENT_WIDTH, RuntimeUtility.getScreenWidth(this));
        arg.putInt(KEY_FRAGMENT_HEIGHT, RuntimeUtility.getScreenHeight(this));
        fragment.setArguments(arg);
        return fragment;
    }
}
