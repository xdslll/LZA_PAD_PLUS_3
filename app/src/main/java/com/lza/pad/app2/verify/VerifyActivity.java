package com.lza.pad.app2.verify;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import com.lza.pad.R;
import com.lza.pad.app2.base.BaseActivity;
import com.lza.pad.app2.guide.GuideActivity;
import com.lza.pad.db.model.DownloadFile;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.db.model.pad.PadVersionInfo;
import com.lza.pad.helper.DownloadHelper;
import com.lza.pad.helper.JsonParseHelper;
import com.lza.pad.helper.SimpleRequestListener;
import com.lza.pad.helper.UrlHelper;
import com.lza.pad.support.file.FileTools;
import com.lza.pad.support.utils.RuntimeUtility;
import com.lza.pad.support.utils.UniversalUtility;
import com.lza.pad.wifi.admin.WifiAdmin;
import com.lza.pad.wifi.admin.WifiApAdmin;

import java.io.File;
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
    WifiApAdmin mWifiApAdmin;

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

    /**
     * [P101]验证是否联网
     */
    private void verifyNetwork() {
        log("[P101]验证是否联网");
        showProgressDialog(R.string.verify_if_network_connected, false);
        mWifiAdmin = WifiAdmin.getInstance(mCtx);
        boolean isNetworkConnected = mWifiAdmin.isNetworkConnected();
        if (isNetworkConnected) {
            checkWifiState();
        } else {
            checkLocalAuthority();
        }
    }

    /**
     * [P102]验证Wi-Fi是否打开
     */
    private void checkWifiState() {
        log("[P102]验证Wi-Fi是否打开");
        boolean isWifiEnable = mWifiAdmin.isWifiEnable();
        if (isWifiEnable) {
            authorityDevice();
        } else {
            openWifi();
        }
    }

    /**
     * [P103]获取Mac地址并向服务器请求授权
     */
    private void authorityDevice() {
        log("[P103]向服务器请求设备授权");
        updateProgressDialog(R.string.getting_mac_address);
        String macAddress = mWifiAdmin.getMacAddress();
        updateProgressDialog(R.string.authority_device_info);
        String authorityUrl = UrlHelper.getDeviceUrl(macAddress);
        send(authorityUrl, new AuthorityDeviceListener());
    }

    /**
     * [P104]打开Wi-Fi
     */
    private void openWifi() {
        log("[P104]打开Wi-Fi");
        updateProgressDialog(R.string.opening_wifi);
        mWifiAdmin.openWifi(mWifiStateListener);
    }

    /**
     * [P105]验证授权结果
     */
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

        @Override
        public void handleResponseFailed() {
            verifyNetwork();
        }
    }

    /**
     * [P106]将设备授权信息保存至本地
     *
     * @param json
     */
    private void saveAuthorityInfo(String json) {
        log("[P106]保存设备授权信息");
        RuntimeUtility.putToDeviceSP(mCtx, KEY_PAD_DEVICE_INFO, json);
    }

    /**
     * [P107]验证服务是否过期
     *
     * @param content
     */
    private void handleAuthoritySuccessful(List<PadDeviceInfo> content) {
        log("[P107]验证服务是否过期");
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
            } else {
                checkAuthorityInfo();
            }
        } catch (ParseException e) {
            e.printStackTrace();
            handleAuthorityFailed(getString(R.string.authority_failed),
                    getString(R.string.authority_failed_empty_expiration));
        }
    }

    /**
     * [P108]设备信息验证失败后，重新请求授权
     */
    private void handleAuthorityFailed(String title, String message) {
        log("[P108]设备授权信息验证失败");
        dismissProgressDialog();
        UniversalUtility.showDialog(mCtx,
                title,
                message,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        authorityDevice();
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

    /**
     * [P109]检查设备授权信息
     */
    private void checkAuthorityInfo() {
        log("[P109]检查设备授权信息");
        if (mPadDeviceInfo == null) {
            handleAuthorityFailed(getString(R.string.authority_failed),
                    getString(R.string.authority_failed_detail));
            return;
        }
        checkVersionUpdate();
    }

    /**
     * [P110]检查是否需要升级版本
     */
    private void checkVersionUpdate() {
        log("[P110]检查是否需要升级版本");
        String deviceVersion = mPadDeviceInfo.getVersion();
        int currentVersion = UniversalUtility.getVersionCode(mCtx);
        if (!isEmpty(deviceVersion)) {
            log("当前版本号：" + currentVersion + ",新版本号：" + deviceVersion);
            if (currentVersion < parseInt(deviceVersion)) {
                mPadDeviceInfo.setVersion(String.valueOf(currentVersion));
                updateNewVersion();
            } else {
                checkWifiApState();
            }
        } else {
            mPadDeviceInfo.setVersion(String.valueOf(currentVersion));
            checkWifiApState();
        }
    }

    /**
     * [P111]自动升级当前版本
     */
    private void updateNewVersion() {
        log("[P111]自动升级新版本");
        String requestUrl = UrlHelper.getVersionUrl(mPadDeviceInfo);
        send(requestUrl, new UpdateNewVersionListener());
    }

    /**
     * [P112]检查是否需要打开Wi-Fi热点
     */
    private void checkWifiApState() {
        log("[P112]检查是否需要打开Wi-Fi热点");
        updateProgressDialog("正在检查Wi-Fi热点");
        String wifiApSwitch = mPadDeviceInfo.getHotspot_switch();
        if (wifiApSwitch.equals(PadDeviceInfo.TAG_HOTSPOT_ON)) {
            mWifiApAdmin = WifiApAdmin.getInstance(mCtx);
            openWifiAp();
        } else {
            updateDeviceInfo();
        }
    }

    /**
     * [P113]打开Wi-Fi热点
     */
    private void openWifiAp() {
        log("[P113]打开Wi-Fi热点");
        updateProgressDialog(getString(R.string.opening_wifi_ap));
        String ssid = mPadDeviceInfo.getName();
        String password = mPadDeviceInfo.getHotspot_password();
        mWifiApAdmin.startWifiAp(ssid, password, mWifiApStateListener);
    }

    /**
     * [P114]更新设备信息
     */
    private void updateDeviceInfo() {
        log("[P114]更新设备信息");
        updateProgressDialog(getString(R.string.updating_device_info));
        mPadDeviceInfo.setState(PadDeviceInfo.TAG_STATE_ON);
        mPadDeviceInfo.setLast_connect_time(String.valueOf(System.currentTimeMillis()));
        requestUpdateDeviceInfo(mPadDeviceInfo);
    }

    /**
     * [P116]前往引导页
     */
    private void gotoGuideActivity() {
        dismissProgressDialog();
        Intent intent = new Intent(mCtx, GuideActivity.class);
        intent.putExtra(KEY_PAD_DEVICE_INFO, mPadDeviceInfo);
        startActivity(intent);
        finish();
    }

    /**
     * [P117]检查本地授权信息
     */
    private void checkLocalAuthority() {
        log("[P117]检查本地授权信息");
    }

    @Override
    protected void onDeviceUpdateSuccess(PadDeviceInfo deviceInfo) {
        gotoGuideActivity();
    }

    @Override
    protected void onDeviceUpdateFailed(PadDeviceInfo deviceInfo) {
        gotoGuideActivity();
    }

    /**
     * 监听版本更新请求
     */
    private class UpdateNewVersionListener extends SimpleRequestListener<PadVersionInfo> {

        @Override
        public ResponseData<PadVersionInfo> parseJson(String json) {
            return JsonParseHelper.pareseVersionInfo(json);
        }

        @Override
        public void handleRespone(List<PadVersionInfo> content) {
            PadVersionInfo version = content.get(0);
            downloadApk(version);
        }

        @Override
        public void handleResponseFailed() {
            log("新版本信息获取失败");
            checkWifiApState();
        }
    }

    /**
     * 下载APK文件
     *
     * @param version
     */
    private void downloadApk(PadVersionInfo version) {
        log("开始更新版本...");
        updateProgressDialog("开始更新版本...");
        String fileName = mPadDeviceInfo.getVersion() + ".apk";
        File apkFile = FileTools.createCacheFile("version/" + fileName);
        try {
            if (apkFile.exists()) {
                apkFile.delete();
            }
        } catch (Exception ex) {

        }
        DownloadHelper helper = new DownloadHelper(mCtx, version, fileName, apkFile);
        registerEventBus();
        try {
            helper.download();
        } catch (Exception ex) {
            log("新版本APK下载失败！");
            unregisterEventBus();
            checkWifiApState();
        }
    }

    /**
     * 下载完成后开始安装文件
     *
     * @param downloadFile
     */
    public void onEventAsync(DownloadFile downloadFile) {
        unregisterEventBus();
        if (downloadFile == null) {
            log("新版本APK下载失败！");
            checkWifiApState();
            return;
        }
        String filePath = downloadFile.getFilePath();
        if (isEmpty(filePath)) {
            log("新版本APK下载失败！");
            checkWifiApState();
        } else {
            log("开始安装新版本");
            File file = new File(filePath);
            log("文件是否下载成功：" + file.exists());
            if (file.exists()) {
                installApk(file);
            }
        }
    }

    /**
     * 监控下载状态，更新进度条
     *
     * @param query
     */
    public void onEventAsync(DownloadHelper.DownloadQuery query) {
        int status = query.getStatus();
        if (status == DownloadManager.STATUS_FAILED) {
            log("新版本APK下载失败！");
            unregisterEventBus();
            checkWifiApState();
        } else if (status == DownloadManager.STATUS_RUNNING) {
            int percent = query.getPercent();
            updateProgressDialog("正在下载升级文件：" + percent + "%");
        }
    }

    /**
     * 处理Wi-Fi状态的变更
     */
    private void handleWifiStateChanged() {
        int wifiState = mWifiAdmin.getWifiState();
        if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
            authorityDevice();
        } else if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
            checkWifiApState();
        } else if (wifiState == WifiManager.WIFI_STATE_DISABLING) {
            updateProgressDialog(R.string.disabling_wifi);
        } else if (wifiState == WifiManager.WIFI_STATE_ENABLING) {
            updateProgressDialog(R.string.enabling_wifi);
        } else {

        }
    }

    /**
     * 监听WiFi连接状态
     */
    private WifiAdmin.OnWifiStateChange mWifiStateListener = new WifiAdmin.OnWifiStateChange() {

        @Override
        public void handle(String action, int wifiState) {
            if (action.equals(WifiAdmin.ACTION_OPEN_WIFI)) {
                if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
                    verifyNetwork();
                } else if (wifiState == WifiManager.WIFI_STATE_ENABLING) {
                    updateProgressDialog(R.string.enabling_wifi);
                }
            }
        }
    };

    /**
     * 监听WiFi热点连接状态
     */
    private WifiApAdmin.OnWifiApStateChange mWifiApStateListener = new WifiApAdmin.OnWifiApStateChange() {

        @Override
        public void handle(String action, int wifiApState) {
            if (action.equals(WifiApAdmin.ACTION_OPEN_WIFI_AP)) {
                if (wifiApState == WifiApAdmin.WIFI_AP_STATE_ENABLED) {
                    updateDeviceInfo();
                } else if (wifiApState == WifiApAdmin.WIFI_AP_STATE_ENABLING) {
                    updateProgressDialog(R.string.enabling_wifi_ap);
                }
            }
        }
    };

}
