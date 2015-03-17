package com.lza.pad.app.home;

import android.os.Bundle;

import com.lza.pad.R;
import com.lza.pad.app.base._BaseActivity;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.helper.RequestHelper;
import com.lza.pad.helper.UrlHelper;
import com.lza.pad.support.utils.ToastUtils;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/2/9.
 */
@Deprecated
public class _HomeActivity extends _BaseActivity {

    private PadDeviceInfo mDeviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_scene_container);

        mDeviceInfo = getIntent().getParcelableExtra("key_pad_device");
        if (mDeviceInfo == null) {
            ToastUtils.showLong(this, "未识别到设备，请重试！");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String url = UrlHelper.getLayoutModuleUrl(mDeviceInfo);
        RequestHelper.getInstance(this).send(url);
    }
}
