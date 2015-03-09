package com.lza.pad.app2.guide;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.lza.pad.app2.base.BaseActivity;
import com.lza.pad.app2.verify.VerifyActivity;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.support.utils.ToastUtils;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/7/15.
 */
public class GuideActivity extends BaseActivity {

    protected PadDeviceInfo mPadDeviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            mPadDeviceInfo = getIntent().getParcelableExtra(KEY_PAD_DEVICE_INFO);
            if (mPadDeviceInfo == null) {
                backtoVerifyActivity();
            }
        } else {
            backtoVerifyActivity();
        }
        getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ToastUtils.showLong(mCtx, "Hello Touch!");
                return false;
            }
        });
    }

    private void backtoVerifyActivity() {
        Intent intent = new Intent(mCtx, VerifyActivity.class);
        startActivity(intent);
        finish();
    }
}
