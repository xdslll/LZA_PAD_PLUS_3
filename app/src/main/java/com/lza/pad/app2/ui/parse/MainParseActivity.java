package com.lza.pad.app2.ui.parse;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.lza.pad.app2.ui.base.BaseActivity;
import com.lza.pad.app2.ui.device.DeviceAuthorityActivity;
import com.lza.pad.db.model.pad._PadDeviceInfo;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/7/15.
 */
public class MainParseActivity extends BaseActivity {

    protected _PadDeviceInfo mPadDeviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkDeviceParam();

        getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }

    private void checkDeviceParam() {
        if (getIntent() != null) {
            mPadDeviceInfo = getIntent().getParcelableExtra(KEY_PAD_DEVICE_INFO);
            if (mPadDeviceInfo == null) {
                backtoDeviceActivity();
            }
        } else {
            backtoDeviceActivity();
        }
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
