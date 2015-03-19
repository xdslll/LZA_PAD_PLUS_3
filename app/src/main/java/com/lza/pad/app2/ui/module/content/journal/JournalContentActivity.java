package com.lza.pad.app2.ui.module.content.journal;

import android.support.v4.app.Fragment;

import com.lza.pad.app2.ui.module.content.BaseContentActivity;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/3/19.
 */
public class JournalContentActivity extends BaseContentActivity {

    @Override
    protected String getModName() {
        return "期刊简介";
    }

    @Override
    protected Fragment getFragment() {
        return new JournalContentFragment();
    }
}
