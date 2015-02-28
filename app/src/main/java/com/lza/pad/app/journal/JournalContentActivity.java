package com.lza.pad.app.journal;

import android.support.v4.app.Fragment;

import com.lza.pad.app.base.BaseContentActivity;
import com.lza.pad.fragment.journal.JournalContentFragment;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 2/27/15.
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
