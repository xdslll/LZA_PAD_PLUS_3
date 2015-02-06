package com.lza.pad.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.app.base.BaseActivity;
import com.lza.pad.app.home.HomeActivity;
import com.lza.pad.app.socket.model.MinaClient;
import com.lza.pad.app.wifi.admin.WifiAdmin;
import com.lza.pad.app.wifi.admin.WifiApAdmin;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.event.model.ResponseEventInfo;
import com.lza.pad.event.state.ResponseEventTag;
import com.lza.pad.helper.JsonParseHelper;
import com.lza.pad.helper.RequestHelper;
import com.lza.pad.helper.UrlHelper;
import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.utils.ToastUtils;
import com.lza.pad.support.utils.UniversalUtility;

import java.util.Map;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/4/15.
 */
public class SplashActivity extends BaseActivity {

    TextView mTxtSplash;

    WifiAdmin mWifiAdmin;
    WifiApAdmin mWifiApAdmin;

    boolean mIsWifiEnable = false;
    boolean mIsWifiApEnable = false;
    int mWifiState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash);

        showProgressDialog("正在初始化...");

        mTxtSplash = (TextView) findViewById(R.id.splash_text);
        mTxtSplash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                finish();
            }
        });

        mMainHandler.sendEmptyMessageDelayed(REQUEST_INIT, 2000);
    }

    private static final int REQUEST_OPEN_WIFI = 0x1;
    private static final int REQUEST_CLOSE_WIFI = 0x2;
    private static final int REQUEST_HANDLE_OPEN_WIFI_STATE = 0x3;
    private static final int REQUEST_HANDLE_CLOSE_WIFI_STATE = 0x4;
    private static final int REQUEST_GET_DEVICE_INFO = 0x5;
    private static final int REQUEST_SEND_GET_DEVICE_REQUEST = 0x6;
    private static final int REQUEST_INIT = 0x07;

    private Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == REQUEST_OPEN_WIFI) {
                boolean ret = mWifiAdmin.openWifi();
                if (ret) {
                    mMainHandler.sendEmptyMessageDelayed(REQUEST_HANDLE_OPEN_WIFI_STATE, 1000);
                } else {
                    ToastUtils.showShort(mCtx, "Wifi设备启动失败，正在重试...");
                    mMainHandler.sendEmptyMessageDelayed(REQUEST_OPEN_WIFI, 1000);
                }
            } else if (msg.what == REQUEST_HANDLE_OPEN_WIFI_STATE) {
                handleWifiOpenState();
            } else if (msg.what == REQUEST_CLOSE_WIFI) {
                boolean ret = mWifiAdmin.closeWifi();
                if (ret) {
                    mMainHandler.sendEmptyMessage(REQUEST_HANDLE_CLOSE_WIFI_STATE);
                } else {
                    ToastUtils.showShort(mCtx, "Wifi设备关闭失败！");
                }
            } else if (msg.what == REQUEST_HANDLE_CLOSE_WIFI_STATE) {
                handleWifiClose();
            } else if (msg.what == REQUEST_GET_DEVICE_INFO) {
                getDeviceInfo();
            } else if (msg.what == REQUEST_SEND_GET_DEVICE_REQUEST) {
                String url = (String) msg.obj;
                RequestHelper.sendRequest(mCtx, url);
            } else if (msg.what == REQUEST_INIT) {
                checkWifi();
            }
        }
    };

    /**
     * 检查热点和Wifi状态（为了获取MacAddress）
     */
    private void checkWifi() {
        //检查热点是否打开，如果打开，则关闭热点
        mWifiApAdmin = WifiApAdmin.instance(mCtx);
        mIsWifiApEnable = mWifiApAdmin.isWifiApEnable();
        if (mIsWifiApEnable) {
            updateProgressDialog("正在关闭热点...");
            mWifiApAdmin.closeWifiAp();
            mIsWifiApEnable = mWifiApAdmin.isWifiApEnable();
        }

        mWifiAdmin = new WifiAdmin(mCtx);
        //打开Wifi，获取Mac地址
        mIsWifiEnable = mWifiAdmin.isWifiEnabled();
        if (!mIsWifiApEnable && !mIsWifiEnable) {
            openWifiDirectly();
        } else {
            getDeviceInfo();
        }
    }

    /**
     * 打开Wifi
     */
    private void openWifiDirectly() {
        updateProgressDialog("正在打开Wifi...");
        mMainHandler.sendEmptyMessageDelayed(REQUEST_OPEN_WIFI, 1000);
    }

    /**
     * 处理Wifi打开后的状态
     */
    private void handleWifiOpenState() {
        mWifiState = mWifiAdmin.getWifiState();
        mIsWifiEnable = mWifiAdmin.isWifiEnabled();
        if (mWifiState == WifiManager.WIFI_STATE_DISABLED) {
            mMainHandler.sendEmptyMessageDelayed(REQUEST_HANDLE_OPEN_WIFI_STATE, 1000);
        } else if (mWifiState == WifiManager.WIFI_STATE_ENABLING) {
            mMainHandler.sendEmptyMessageDelayed(REQUEST_HANDLE_OPEN_WIFI_STATE, 1000);
        } else if (mWifiState == WifiManager.WIFI_STATE_ENABLED) {
            AppLogger.e("Wifi打开成功！");
            mMainHandler.sendEmptyMessageDelayed(REQUEST_GET_DEVICE_INFO, 1000);
        } else {
            ToastUtils.showShort(mCtx, "未知状态，正在重试...");
            mMainHandler.sendEmptyMessageDelayed(REQUEST_HANDLE_OPEN_WIFI_STATE, 1000);
        }
    }

    /**
     * 向服务器请求设备信息
     */
    private void getDeviceInfo() {
        updateProgressDialog("正在验证设备信息...");
        String macAddress = UniversalUtility.getMacAddress(this);
        AppLogger.e("mac地址：" + macAddress);

        Map<String, String> par = UrlHelper.getDevicePar(macAddress);
        String url = UrlHelper.generateUrl(par);

        Message msg = Message.obtain();
        msg.what = REQUEST_SEND_GET_DEVICE_REQUEST;
        msg.obj = url;
        mMainHandler.sendMessageDelayed(msg, 1000);
        //RequestHelper.sendRequest(this, url);
    }

    /**
     * 处理Wifi关闭状态
     */
    private void handleWifiClose() {
        mWifiState = mWifiAdmin.getWifiState();
        mIsWifiEnable = mWifiAdmin.isWifiEnabled();
        if (mWifiState == WifiManager.WIFI_STATE_DISABLED) {

        } else if (mWifiState == WifiManager.WIFI_STATE_DISABLING) {

        } else if (mWifiState == WifiManager.WIFI_STATE_ENABLED) {
            mMainHandler.sendEmptyMessageDelayed(REQUEST_HANDLE_CLOSE_WIFI_STATE, 1000);
        } else {
            ToastUtils.showShort(mCtx, "未知状态，请重试！");
        }
    }

    /**
     * 处理手机客户端的连接请求
     *
     * @param client
     */
    @Override
    public void onEventMainThread(MinaClient client) {
        if (!checkMinaClient(client)) return;
        mTxtSplash.setText("您好！欢迎来自[" + client.getAcademy() + "]的朋友：" + client.getName());
    }

    /**
     * 处理提交的设备信息请求
     *
     * @param response
     */
    @Override
    public void onEventMainThread(ResponseEventInfo response) {
        dismissProgressDialog();

        ResponseEventTag tag = response.getTag();
        if (tag == ResponseEventTag.ON_RESONSE) {
            String json = response.getResponseData();
            final ResponseData<PadDeviceInfo> data = JsonParseHelper.parseDeviceResponse(json);
            String state = data.getState();
            String message = data.getMessage();
            if (state.equals(ResponseData.RESPONSE_STATE_OK)) {
                UniversalUtility.showDialog(this, "提示", message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (data.getContent() == null || data.getContent().size() == 0) return;
                                PadDeviceInfo deviceInfo = data.getContent().get(0);
                                showDeviceInfo(deviceInfo);
                            }
                        });
            } else if (state.equals(ResponseData.RESPONSE_STATE_NO_LAYOUT)) {
                UniversalUtility.showDialog(this, "提示", message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showProgressDialog("正在重新初始化...");
                                mMainHandler.sendEmptyMessageDelayed(REQUEST_INIT, 2000);
                            }
                        });
            } else if (state.equals(ResponseData.RESPONSE_STATE_NO_MAC_ADDRESS)) {
                UniversalUtility.showDialog(this, "提示", message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showProgressDialog("正在重新初始化...");
                                mMainHandler.sendEmptyMessageDelayed(REQUEST_INIT, 2000);
                            }
                        });
            }
        }
    }

    private void showDeviceInfo(final PadDeviceInfo deviceInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("----------- 设备清单 -----------\n");
        sb.append(deviceInfo.toString());
        UniversalUtility.showDialog(mCtx, "提示", sb.toString(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                intent.putExtra(KEY_PAD_DEVICE_INFO, deviceInfo);
                startActivity(intent);
                finish();
            }
        });
    }
}
