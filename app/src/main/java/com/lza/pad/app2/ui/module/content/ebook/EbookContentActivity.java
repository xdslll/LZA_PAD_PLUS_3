package com.lza.pad.app2.ui.module.content.ebook;

import android.support.v4.app.Fragment;

import com.lza.pad.app2.ui.module.content.BaseContentActivity;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/3/19.
 */
public class EbookContentActivity extends BaseContentActivity {

    @Override
    protected String getModName() {
        return "简介";
    }

    @Override
    protected Fragment getFragment() {
        return new EbookContentFragment();
    }
}
