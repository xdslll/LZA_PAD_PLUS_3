package com.lza.pad.app2.ui.parse;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.lza.pad.app2.ui.base.BaseActivity;
import com.lza.pad.app2.ui.device.DeviceAuthorityActivity;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.support.utils.ToastUtils;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/7/15.
 */
public class MainParseActivity extends BaseActivity {

    protected PadDeviceInfo mPadDeviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            mPadDeviceInfo = getIntent().getParcelableExtra(KEY_PAD_DEVICE_INFO);
            if (mPadDeviceInfo == null) {
                backtoDeviceActivity();
            }
        } else {
            backtoDeviceActivity();
        }
        getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ToastUtils.showLong(mCtx, "Hello Touch!");
                return false;
            }
        });
    }

    private void backtoDeviceActivity() {
        Intent intent = new Intent(mCtx, DeviceAuthorityActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        return super.onMenuItemSelected(featureId, item);
    }
}
