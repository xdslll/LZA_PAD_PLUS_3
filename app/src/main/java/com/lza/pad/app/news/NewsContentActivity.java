package com.lza.pad.app.news;

import android.app.Fragment;

import com.lza.pad.app.base.BaseContentActivity;
import com.lza.pad.fragment.news.NewsContentFragment;

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
        return new NewsContentFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mApp.setOnSensorShakeListener(mListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mApp.unRegisterSensorShake();
    }

    /*private _MainApplication.OnSensorShakeListener mListener = new _MainApplication.OnSensorShakeListener() {
        @Override
        public File onShake() {
            ToastUtils.showShort(NewsContentActivity.this, "新闻模块，接收到用户摇一摇！");
            File dir = Environment.getExternalStorageDirectory();
            File file = new File(dir, "spring.pdf");
            return file;
        }
    };*/
}
