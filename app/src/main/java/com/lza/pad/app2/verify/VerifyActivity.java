package com.lza.pad.app2.verify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import com.lza.pad.R;
import com.lza.pad.app.wifi.admin.WifiAdmin;
import com.lza.pad.app2.base.BaseActivity;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.helper.JsonParseHelper;
import com.lza.pad.helper.SimpleRequestListener;
import com.lza.pad.helper.UrlHelper;
import com.lza.pad.support.utils.RuntimeUtility;
import com.lza.pad.support.utils.UniversalUtility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 系统运行的第一步，验证设备信息
 *
 * @author xiads
 * @Date 3/5/15.
 */
public class VerifyActivity extends BaseActivity {

    WifiAdmin mWifiAdmin;

    private NetworkReceiver mNetworkReceiver = new NetworkReceiver();

    private PadDeviceInfo mPadDeviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        verifyNetwork();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void register() {
        IntentFilter filter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(mNetworkReceiver, filter);
    }

    private void unregister() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (Exception ex) {

        }
    }

    /**
     * 判断是否联网
     * 如果联网则进行在线验证
     * 如果未联网则检查本地授权信息
     */
    private void verifyNetwork() {
        showProgressDialog(R.string.verify_if_network_connected, false);
        mWifiAdmin = WifiAdmin.getInstance(mCtx);
        boolean isNetworkConnected = mWifiAdmin.isNetworkConnected();
        boolean isWifiConnected = mWifiAdmin.isWifiConnected();
        boolean isWifiEnable = mWifiAdmin.isWifiEnable();
        if (isNetworkConnected) {
            if (isWifiConnected) {
                authorityDevice();
            } else {
                if (isWifiEnable) {
                    authorityDevice();
                } else {
                    updateProgressDialog(R.string.opening_wifi);
                    register();
                    mWifiAdmin.openWifi();
                }
            }
        } else {

        }
    }

    /**
     * 请求设备授权
     */
    private void authorityDevice() {
        updateProgressDialog(R.string.getting_mac_address);
        String macAddress = mWifiAdmin.getMacAddress();
        updateProgressDialog(R.string.authority_device_info);
        String authorityUrl = UrlHelper.getDeviceUrl(macAddress);
        send(authorityUrl, new AuthorityDeviceListener());
    }

    private class AuthorityDeviceListener extends SimpleRequestListener<PadDeviceInfo> {
        @Override
        public ResponseData<PadDeviceInfo> parseJson(String json) {
            return JsonParseHelper.parseDeviceInfoResponse(json);
        }

        @Override
        public void onResponseStateError(ResponseData<PadDeviceInfo> response) {
                handleAuthorityFailed(getString(R.string.authority_failed),
                        response.getMessage());
        }

        @Override
        public void handleRespone(List<PadDeviceInfo> content) {
            handleAuthoritySuccessful(content);
        }

        @Override
        public boolean handleResponseStatusOK(String json) {
            saveAuthorityInfo(json);
            return super.handleResponseStatusOK(json);
        }
    }

    /**
     * 将设备信息保存至本地
     *
     * @param json
     */
    private void saveAuthorityInfo(String json) {
        RuntimeUtility.putToDeviceSP(mCtx, KEY_PAD_DEVICE_INFO, json);
    }

    /**
     * 处理验证成功的情况
     *
     * @param content
     */
    private void handleAuthoritySuccessful(List<PadDeviceInfo> content) {
        mPadDeviceInfo = content.get(0);
        String date = mPadDeviceInfo.getEnd_pubdate();
        SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance();
        format.applyPattern("yyyy-MM-dd");
        try {
            long endDate = format.parse(date).getTime();
            long currentDate = System.currentTimeMillis();
            if (currentDate > endDate) {
                handleAuthorityFailed(getString(R.string.service_expiration),
                        getString(R.string.service_expiration_detail));
            }
        } catch (ParseException e) {
            e.printStackTrace();
            handleAuthorityFailed(getString(R.string.authority_failed),
                    getString(R.string.authority_failed_empty_expiration));
        }
    }

    /**
     * 处理设备验证失败
     *
     */
    private void handleAuthorityFailed(String title, String message) {
        dismissProgressDialog();
        UniversalUtility.showDialog(mCtx,
                title,
                message,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        verifyNetwork();
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
    }

    private class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                handleWifiStateChanged();
            }
        }
    }

    /**
     * 处理Wi-Fi状态
     */
    private void handleWifiStateChanged() {
        int wifiState = mWifiAdmin.checkState();
        if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
            authorityDevice();
            unregister();
        } else if (wifiState == WifiManager.WIFI_STATE_DISABLED) {

        } else if (wifiState == WifiManager.WIFI_STATE_DISABLING) {

        } else if (wifiState == WifiManager.WIFI_STATE_ENABLING) {
            updateProgressDialog(R.string.enabling_wifi);
        } else {

        }
    }

}
