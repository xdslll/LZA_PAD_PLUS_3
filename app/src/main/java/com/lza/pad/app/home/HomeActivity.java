package com.lza.pad.app.home;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.LinearLayout;

import com.lza.pad.R;
import com.lza.pad.app.base.BaseActivity;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.event.model.ResponseEventInfo;
import com.lza.pad.exception.DeviceNotFound;
import com.lza.pad.helper.RequestHelper;
import com.lza.pad.helper.UrlHelper;
import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.utils.UniversalUtility;

import java.util.Map;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/5/15.
 */
public class HomeActivity extends BaseActivity {

    private LinearLayout mMainContainer;

    private PadDeviceInfo mDeviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDeviceInfo = getIntent().getParcelableExtra(KEY_PAD_DEVICE_INFO);
        if (mDeviceInfo == null) throw new DeviceNotFound("设备未识别！请重试！");

        setContentView(R.layout.common_main_container);
        mMainContainer = (LinearLayout) findViewById(R.id.home);

        showProgressDialog("开始初始化布局");
        mMainHandler.sendEmptyMessageDelayed(REQUEST_INIT, 2000);
    }

    private static final int REQUEST_INIT = 0x01;
    private static final int REQUEST_GET_DEVICE_LAYOUT = 0x02;

    Handler mMainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == REQUEST_INIT) {
                getDeviceModule();
            } else if (msg.what == REQUEST_GET_DEVICE_LAYOUT) {
                getDeviceLayout((String) msg.obj);
            }
        }
    };

    /**
     * 向服务器请求模块信息
     */
    private void getDeviceModule() {

        Map<String, String> par = UrlHelper.getLayoutPar(mDeviceInfo);
        String url = UrlHelper.generateUrl(par);

        AppLogger.e(url);

        Message msg = Message.obtain();
        msg.what = REQUEST_GET_DEVICE_LAYOUT;
        msg.obj = url;
        mMainHandler.sendMessageDelayed(msg, 1000);
    }

    private void getDeviceLayout(String url) {
        RequestHelper.sendRequest(mCtx, url);
    }

    @Override
    public void onEventMainThread(ResponseEventInfo response) {
        updateProgressDialog("开始更新布局");
    }

    @Override
    public void onBackPressed() {
        //ToastUtils.showShort(this, "已经在首页，无法回退！");
        UniversalUtility.showDialog(this, "提示", "是否退出？",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
    }
}

//获取屏幕尺寸
        /*int w, h, size = 4;
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        w = metrics.widthPixels;
        h = metrics.heightPixels;

        for (int i = 0; i < size; i++) {
            if (i == 0) {
                int id = (i + 1) << (i + 1);
                FrameLayout subContainer = new FrameLayout(this);
                subContainer.setLayoutParams(new ViewGroup.LayoutParams(w, h / size));
                subContainer.setId(id);
                mMainContainer.addView(subContainer);
                TitleFragment fragment = new TitleFragment();
                launchFragment(fragment, id);
            } else if (i == 1) {
                int id = (i + 1) << (i + 1);
                FrameLayout subContainer = new FrameLayout(this);
                subContainer.setLayoutParams(new ViewGroup.LayoutParams(w, h / size));
                subContainer.setId(id);
                mMainContainer.addView(subContainer);
                _EbookListFragment fragment = new _EbookListFragment();
                launchFragment(fragment, id, w, h / size);
            } else if (i == 2) {
                int id = (i + 1) << (i + 1);
                FrameLayout subContainer = new FrameLayout(this);
                subContainer.setLayoutParams(new ViewGroup.LayoutParams(w, h / size));
                subContainer.setId(id);
                mMainContainer.addView(subContainer);
                EbookNormalListFragment fragment = new EbookNormalListFragment();
                launchFragment(fragment, id, w, h / size);
            } else {
                int id = (i + 1) << (i + 1);
                FrameLayout subContainer = new FrameLayout(this);
                subContainer.setLayoutParams(new ViewGroup.LayoutParams(w, h / size));
                subContainer.setId(id);
                mMainContainer.addView(subContainer);
                IrregularNewsFragment fragment = new IrregularNewsFragment();
                launchFragment(fragment, id);
            }*/
            /*else if (i == 2) {
                int id = (i + 1) << (i + 1);
                FrameLayout subContainer = new FrameLayout(this);
                subContainer.setLayoutParams(new ViewGroup.LayoutParams(w, h / 2));
                subContainer.setId(id);
                mMainContainer.addView(subContainer);
                LibraryMapFragment fragment = new LibraryMapFragment();
                launchFragment(fragment, id, w, h / 2);
            } else {

            }
        }*/