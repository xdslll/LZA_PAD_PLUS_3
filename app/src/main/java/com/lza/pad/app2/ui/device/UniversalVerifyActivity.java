package com.lza.pad.app2.ui.device;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.app2.ui.base.BaseActivity;
import com.lza.pad.db.model.pad.PadDeviceInfo;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 5/18/15.
 */
public class UniversalVerifyActivity extends BaseActivity {

    DeviceVerifyHandler mDeviceVerifyHandler;

    TextView mTxtLoading;

    private static final int DEFAULT_DELAY = 5000;

    private static final int REQUEST_VERIFY_DEVICE_SUCCESS = 0x001;
    private static final int REQUEST_VERIFY_DEVICE_FAILED = 0x002;
    private static final int REQUEST_VERIFY_DEVICE_UPDATE = 0x003;

    boolean mIsClosed = false;
    private MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {

        WeakReference<UniversalVerifyActivity> weakActivity;

        public MyHandler(UniversalVerifyActivity activity) {
            super();
            this.weakActivity = new WeakReference<UniversalVerifyActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final UniversalVerifyActivity activity = weakActivity.get();
            if (activity != null) {
                if (msg.what == REQUEST_VERIFY_DEVICE_SUCCESS) {
                    handleVerifySuccess(msg, activity);
                } else if (msg.what == REQUEST_VERIFY_DEVICE_FAILED) {
                    handleVerifyFailed(msg, activity);
                } else if (msg.what == REQUEST_VERIFY_DEVICE_UPDATE) {
                    update(msg, activity);
                }
            }
        }

        private void update(Message msg, final UniversalVerifyActivity activity) {
            String message = (String) msg.obj;
            activity.mTxtLoading.setText(message);
        }

        private void handleVerifySuccess(Message msg, final UniversalVerifyActivity activity) {
            List<PadDeviceInfo> content = (List<PadDeviceInfo>) msg.obj;
            activity.mDeviceVerifyHandler.handleVerifySuccessful(content);
        }

        private void handleVerifyFailed(Message msg, final UniversalVerifyActivity activity) {
            String errorMessage = (String) msg.obj;
            activity.mTxtLoading.setText(errorMessage);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    activity.mTxtLoading.setText(R.string.verify_restart);
                    activity.authorityDevice();
                }
            }, DEFAULT_DELAY);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.universal_verify);
        mTxtLoading = (TextView) findViewById(R.id.universal_verify_text);

        mDeviceVerifyHandler = new DeviceVerifyHandler(mCtx);
        mDeviceVerifyHandler.setOnVerifyDeviceListener(new DeviceVerifyHandler.OnVerifyDeviceListener() {
            @Override
            public void onVerifyError(String errorMsg) {
                Message msg = Message.obtain(mHandler, REQUEST_VERIFY_DEVICE_FAILED, errorMsg);
                msg.sendToTarget();
            }

            @Override
            public void onVerifySuccess(List<PadDeviceInfo> content) {
                Message msg = Message.obtain(mHandler, REQUEST_VERIFY_DEVICE_SUCCESS, content);
                msg.sendToTarget();
            }

            @Override
            public void update(String message) {
                Message msg = Message.obtain(mHandler, REQUEST_VERIFY_DEVICE_UPDATE, message);
                msg.sendToTarget();
            }
        });
        authorityDevice();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsClosed = true;
        mHandler.removeMessages(REQUEST_VERIFY_DEVICE_SUCCESS);
        mHandler.removeMessages(REQUEST_VERIFY_DEVICE_FAILED);
    }

    /**
     * 验证设备信息
     */
    public void authorityDevice() {
        log("[P103]向服务器请求设备授权");
        if (mIsClosed)
            return ;

        if (!mDeviceVerifyHandler.checkMacAddress()) {
            mTxtLoading.setText(R.string.verify_error_mac_address);
        } else {
            mDeviceVerifyHandler.onlineVerifyDevice();
        }
    }
}
