package com.lza.pad.app.ebook;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.app.base.BaseActivity;
import com.lza.pad.fragment.ebook._EbookContentFragment;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/18/15.
 */
@Deprecated
public class _EbookContentActivity extends BaseActivity {

    private TextView mTxtClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_content_container);
        mTxtClose = (TextView) findViewById(R.id.ebook_content_close);
        mTxtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        launchFragment(new _EbookContentFragment(), R.id.ebook_content_container);
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

}

/*private _MainApplication.OnSensorShakeListener mListener = new _MainApplication.OnSensorShakeListener() {
        @Override
        public File onShake() {
            ToastUtils.showShort(EbookContentActivity.this, "电子书模块，接收到用户摇一摇！");
            File dir = Environment.getExternalStorageDirectory();
            File file = new File(dir, "test.epub");
            return file;
        }
    };*/
