package com.lza.pad.app2.ui.device;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import com.lza.pad.R;
import com.lza.pad.app2.ui.base.BaseActivity;
import com.lza.pad.db.model.DownloadFile;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.pad.PadAuthority;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.db.model.pad.PadScene;
import com.lza.pad.db.model.pad.PadSchool;
import com.lza.pad.db.model.pad.PadVersionInfo;
import com.lza.pad.helper.DownloadHelper;
import com.lza.pad.helper.JsonParseHelper;
import com.lza.pad.helper.SimpleRequestListener;
import com.lza.pad.helper.UrlHelper;
import com.lza.pad.support.file.FileTools;
import com.lza.pad.support.utils.RuntimeUtility;
import com.lza.pad.support.utils.Utility;
import com.lza.pad.wifi.admin.WifiAdmin;
import com.lza.pad.wifi.admin.WifiApAdmin;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 5/18/15.
 */
public class UniversalVerifyActivity extends BaseActivity {

    WifiAdmin mWifiAdmin;
    WifiApAdmin mWifiApAdmin;

    PadDeviceInfo mPadDeviceInfo;

    PadScene mPadScene;

    PadSchool mPadSchool;
    PadAuthority mPadAuthority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.universal_verify);

        authorityDevice();
    }

    /**
     * 验证设备信息
     */
    public void authorityDevice() {
        log("[P103]向服务器请求设备授权");
        showLoadingView();
        setLoadingViewText(R.string.verify_start);

        mWifiAdmin = new WifiAdmin(mCtx);
        String macAddress = mWifiAdmin.getMacAddress();

        String authorityUrl = UrlHelper.getDeviceUrl(macAddress);
        send(authorityUrl, new AuthorityDeviceListener());
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
            handleAuthorityFailed(getString(R.string.authority_failed),
                    getString(R.string.authority_failed_detail));
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
     * [P109]检查设备授权信息
     */
    private void checkAuthorityInfo() {
        log("[P109]检查设备授权信息");
        if (mPadDeviceInfo == null) {
            handleAuthorityFailed(getString(R.string.authority_failed),
                    getString(R.string.authority_failed_detail));
        } else {
            checkVersionUpdate();
        }
    }


    /**
     * [P110]检查是否需要升级版本
     */
    private void checkVersionUpdate() {
        log("[P110]检查是否需要升级版本");
        String deviceVersion = mPadDeviceInfo.getVersion();
        int currentVersion = Utility.getVersionCode(mCtx);
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
        setLoadingViewText(R.string.verify_start_updating_version);
        String fileName = mPadDeviceInfo.getVersion() + ".apk";
        File apkFile = FileTools.createCacheFile("version/" + fileName);
        try {
            if (apkFile != null && apkFile.exists()) {
                apkFile.delete();
            }
        } catch (Exception ex) {

        }
        DownloadHelper helper = new DownloadHelper(mCtx, version, fileName, apkFile);
        if (!isRegisterEventBus()) {
            registerEventBus();
        }
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
        if (isRegisterEventBus()) {
            unregisterEventBus();
        }
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
            final int percent = query.getPercent();
            getMainHandler().post(new Runnable() {
                @Override
                public void run() {
                    setLoadingViewText(String.format(getString(R.string.verify_updating_version), percent));
                }
            });
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

                } else if (wifiState == WifiManager.WIFI_STATE_ENABLING) {

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
                    setLoadingViewText(R.string.opening_wifi_ap);
                }
            }
        }
    };

    /**
     * [P112]检查是否需要打开Wi-Fi热点
     */
    private void checkWifiApState() {
        log("[P112]检查是否需要打开Wi-Fi热点");
        setLoadingViewText(R.string.verify_check_wifi_ap);
        String wifiApSwitch = mPadDeviceInfo.getHotspot_switch();
        if (wifiApSwitch.equals(PadDeviceInfo.TAG_HOTSPOT_ON)) {
            mWifiApAdmin = new WifiApAdmin(mCtx);
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
        setLoadingViewText(R.string.opening_wifi_ap);
        String ssid = mPadDeviceInfo.getName();
        String password = mPadDeviceInfo.getHotspot_password();
        mWifiApAdmin.startWifiAp(ssid, password, mWifiApStateListener);
    }

    /**
     * [P114]更新设备信息
     */
    private void updateDeviceInfo() {
        log("[P114]更新设备信息");
        setLoadingViewText(R.string.updating_device_info);
        mPadDeviceInfo.setState(PadDeviceInfo.TAG_STATE_ON);
        mPadDeviceInfo.setLast_connect_time(String.valueOf(System.currentTimeMillis()));
        requestUpdateDeviceInfo(mPadDeviceInfo);
    }

    /**
     * [P115]处理上传成功逻辑
     *
     * @param deviceInfo
     */
    @Override
    protected void onDeviceUpdateSuccess(PadDeviceInfo deviceInfo) {
        gotoParseActivity();
    }

    /**
     * [P115]处理上传失败逻辑
     *
     * @param deviceInfo
     */
    @Override
    protected void onDeviceUpdateFailed(PadDeviceInfo deviceInfo) {
        gotoParseActivity();
    }

    /**
     * [P116]前往主解析页
     */
    private void gotoParseActivity() {
        log("设备信息解析成功！");
        /*Intent intent = new Intent(mCtx, MainParseActivity.class);
        intent.putExtra(KEY_PAD_DEVICE_INFO, mPadDeviceInfo);
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();

        overridePendingTransition(0, 0);
        startActivity(intent);*/
        getPadScene();
    }

    /**
     * [P108]设备信息验证失败后，重新请求授权
     */
    private void handleAuthorityFailed(String title, String message) {
        log("[P108]设备授权信息验证失败");
        handleErrorProcess(title, message, new Runnable() {
            @Override
            public void run() {
                showProgressDialog(R.string.getting_mac_address);
                authorityDevice();
            }
        });
    }

    /**
     * [P203]获取当前设备的场景
     */
    private void getPadScene() {
        log("[P203]获取当前设备的场景");
        setLoadingViewText(R.string.verify_start_get_scene);
        String getPadSceneUrl = UrlHelper.getDeviceSceneUrl(mPadDeviceInfo);
        send(getPadSceneUrl, new GetPadSceneListener());
    }

    private class GetPadSceneListener extends SimpleRequestListener<PadScene> {
        @Override
        public ResponseData<PadScene> parseJson(String json) {
            return JsonParseHelper.parseDeviceSceneResponse(json);
        }

        @Override
        public void handleRespone(List<PadScene> content) {
            mPadScene = content.get(0);
            verifySchoolBh();
        }

        @Override
        public void handleResponseFailed() {
            handleSceneParseFailed(R.string.dialog_prompt, R.string.verify_error_scene_parse);
        }
    }

    /**
     * [P204]验证场景的学校编号，和当前设备对应的学校编号是否一致
     * 要求场景的学校编号和当前设备的学校编号一致，否则场景将无法使用
     */
    private void verifySchoolBh() {
        log("[P204]验证场景的学校编号");
        setLoadingViewText(R.string.verify_start_parse_school_code);
        String deviceSchooBh = mPadDeviceInfo.getSchool_bh();
        String sceneSchoolBh = mPadScene.getSchool_bh();
        String scenePrivacy = mPadScene.getPrivacy();
        //如果场景编号为空，则为共享场景
        if (scenePrivacy.equals(PadScene.IS_NOT_PRIVACY)) {
            verifyScenceIsActivate();
        } else {
            if (deviceSchooBh.equals(sceneSchoolBh)) {
                verifyScenceIsActivate();
            } else {
                handleSceneParseFailed(R.string.dialog_prompt, R.string.verify_error_school_code);
            }
        }
    }

    /**
     * [P205]错误提示
     * @param title
     * @param message
     */
    private void handleSceneParseFailed(int title, int message) {
        log("[P205]错误提示 : " + message);
        handleErrorProcess(getString(title), getString(message), new Runnable() {
            @Override
            public void run() {
                getPadScene();
            }
        });
    }

    /**
     * [P206]验证当前场景是否处于激活状态
     */
    private void verifyScenceIsActivate() {
        log("[P206]验证当前场景是否处于激活状态");
        setLoadingViewText(R.string.verify_scene_is_activate);
        String isActivate = mPadScene.getActivate();
        if (isActivate.equals(PadScene.IS_ACTIVATE)) {
            checkScenceAuthority();
        } else {
            handleSceneParseFailed(R.string.dialog_prompt, R.string.verify_error_scene_closed);
        }
    }

    /**
     * [P207]与学校的最高权限进行比对
     */
    private void checkScenceAuthority() {
        log("[P207]检查学校最高处理权限");
        setLoadingViewText(R.string.verify_scene_top_authority);
        String getSchoolInfoUrl = UrlHelper.getPadSchoolInfoUrl(mPadDeviceInfo);
        send(getSchoolInfoUrl, new GetSchoolInfoListener());
    }

    private class GetSchoolInfoListener extends SimpleRequestListener<PadSchool> {
        @Override
        public ResponseData<PadSchool> parseJson(String json) {
            return JsonParseHelper.parsePadSchoolResponse(json);
        }

        @Override
        public void handleRespone(List<PadSchool> content) {
            mPadSchool = content.get(0);
            checkSchoolAuthority();
        }

        @Override
        public void handleResponseFailed() {
            handleSceneParseFailed(R.string.dialog_prompt, R.string.verify_error_get_school_info);
        }
    }

    /**
     * [P207]与学校的最高权限进行比对
     */
    private void checkSchoolAuthority() {
        String maxAuth = mPadSchool.getMax_authority();
        String currentAuth = mPadScene.getAuthority();
        if (parseInt(currentAuth) > parseInt(maxAuth)) {
            handleSceneParseFailed(R.string.dialog_prompt, R.string.verify_error_top_authority);
        } else {
            getSceneAuthority();
        }
    }

    /**
     * [P208]获取场景的处理权限
     */
    private void getSceneAuthority() {
        log("[P208]获取场景的处理权限");
        setLoadingViewText(R.string.verify_start_get_authority);
        String getAuthorityUrl = UrlHelper.getPadAuthority(mPadDeviceInfo, mPadScene);
        send(getAuthorityUrl, new GetAuthorityListener());
    }

    private class GetAuthorityListener extends SimpleRequestListener<PadAuthority> {
        @Override
        public ResponseData<PadAuthority> parseJson(String json) {
            return JsonParseHelper.parsePadAuthorityResponse(json);
        }

        @Override
        public void handleRespone(List<PadAuthority> content) {
            mPadAuthority = content.get(0);
            parseScene();
        }

        @Override
        public void handleResponseFailed() {
            handleSceneParseFailed(R.string.dialog_prompt, R.string.verify_error_get_authority);
        }
    }

    /**
     * [P208]处理场景
     */
    private void parseScene() {
        setLoadingViewText(R.string.verify_start_parse_scene);
        String activityPath = buildCodePath(mPadAuthority.getScene_parse_code());
        log("activity path=" + activityPath);
        Intent intent = new Intent();
        intent.setClassName(mCtx, activityPath);
        intent.putExtra(KEY_PAD_DEVICE_INFO, mPadDeviceInfo);
        intent.putExtra(KEY_PAD_SCHOOL, mPadSchool);
        intent.putExtra(KEY_PAD_SCENE, mPadScene);
        intent.putExtra(KEY_PAD_AUTHORITY, mPadAuthority);
        dismissProgressDialog();

        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();

        overridePendingTransition(0, 0);
        startActivity(intent);
    }
}
