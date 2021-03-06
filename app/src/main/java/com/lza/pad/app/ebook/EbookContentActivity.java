package com.lza.pad.app.ebook;

import android.support.v4.app.Fragment;

import com.lza.pad.app.base._BaseContentActivity;
import com.lza.pad.fragment.ebook.EbookContentFragment;

/**
 * 显示电子书内容和豆瓣书评
 *
 * @author xiads
 * @Date 2/16/15.
 */
public class EbookContentActivity extends _BaseContentActivity {

    @Override
    protected String getModName() {
        return "简介";
    }

    @Override
    protected Fragment getFragment() {
        /*EbookContentFragment frg = new EbookContentFragment();
        Bundle arg = new Bundle();
        arg.putParcelable(KEY_PAD_DEVICE_INFO, mPadDeviceInfo);
        arg.putParcelable(KEY_PAD_RESOURCE_INFO, mPadResource);
        frg.setArguments(arg);
        return frg;*/
        return new EbookContentFragment();
    }
}
