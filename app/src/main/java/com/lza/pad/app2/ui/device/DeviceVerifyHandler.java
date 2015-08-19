package com.lza.pad.app2.ui.device;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.lza.pad.R;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.pad.PadAuthority;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.db.model.pad.PadModuleType;
import com.lza.pad.db.model.pad.PadScene;
import com.lza.pad.db.model.pad.PadSceneModule;
import com.lza.pad.db.model.pad.PadSchool;
import com.lza.pad.db.model.pad.PadVersionInfo;
import com.lza.pad.helper.DownloadHelper;
import com.lza.pad.helper.JsonParseHelper;
import com.lza.pad.helper.RequestHelper;
import com.lza.pad.helper.SimpleRequestListener;
import com.lza.pad.helper.UrlHelper;
import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.file.FileTools;
import com.lza.pad.support.utils.Consts;
import com.lza.pad.support.utils.RuntimeUtility;
import com.lza.pad.support.utils.Utility;
import com.lza.pad.wifi.admin.WifiAdmin;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 验证设备信息
 *
 * @author xiads
 * @Date 15/7/30.
 */
public class DeviceVerifyHandler implements Consts {

    WifiAdmin mWifiAdmin;
    Context mCtx;

    PadDeviceInfo mPadDeviceInfo;
    PadScene mPadScene;
    PadAuthority mPadAuthority;
    PadSchool mPadSchool;
    List<PadSceneModule> mPadSceneModules = new ArrayList<PadSceneModule>();

    ArrayList<PadSceneModule> mGuideModule = new ArrayList<PadSceneModule>();
    ArrayList<PadSceneModule> mHomeModule = new ArrayList<PadSceneModule>();
    ArrayList<PadSceneModule> mSubpageModule = new ArrayList<PadSceneModule>();
    ArrayList<PadSceneModule> mContentModule = new ArrayList<PadSceneModule>();
    ArrayList<PadSceneModule> mHelpModule = new ArrayList<PadSceneModule>();

    public DeviceVerifyHandler(Context c) {
        mWifiAdmin = new WifiAdmin(c);
        mCtx = c;
    }

    public boolean checkMacAddress() {
        String macAddress = RuntimeUtility.getFromDeviceSP(mCtx, KEY_MAC_ADDRESS, "");
        if (isEmpty(macAddress)) {
            macAddress = mWifiAdmin.getMacAddress();
            if (isEmpty(macAddress) || macAddress.equals("NULL")) {
                return false;
            } else {
                RuntimeUtility.putToDeviceSP(mCtx, KEY_MAC_ADDRESS, macAddress);
                return true;
            }
        } else {
            return true;
        }
    }

    public void onlineVerifyDevice() {
        String macAddress = RuntimeUtility.getFromDeviceSP(mCtx, KEY_MAC_ADDRESS, "");
        String authorityUrl = UrlHelper.getDeviceUrl(macAddress);
        send(authorityUrl, new VerifyDeviceListener());
    }

    public interface OnVerifyDeviceListener {
        void onVerifyError(String errorMsg);

        void onVerifySuccess(List<PadDeviceInfo> content);

        void update(String msg);
    }

    private OnVerifyDeviceListener mVerifyDeviceListener;

    public void setOnVerifyDeviceListener(OnVerifyDeviceListener listener) {
        this.mVerifyDeviceListener = listener;
    }

    /**
     * [P105]验证授权结果
     */
    private class VerifyDeviceListener extends SimpleRequestListener<PadDeviceInfo> {

        @Override
        public ResponseData<PadDeviceInfo> parseJson(String json) {
            return JsonParseHelper.parseDeviceInfoResponse(json);
        }

        @Override
        public void onResponseStateError(ResponseData<PadDeviceInfo> response) {
            error(response.getMessage());
        }

        @Override
        public void handleRespone(List<PadDeviceInfo> content) {
            update(R.string.authority_successful);
            handleVerifySuccessful(content);
        }

        @Override
        public boolean handleResponseStatusOK(String json) {
            saveDeviceInfo(json);
            return super.handleResponseStatusOK(json);
        }

        @Override
        public void handleResponseFailed() {
            error(R.string.authority_failed_detail);
        }
    }

    /**
     * [P106]将设备授权信息保存至本地
     *
     * @param json
     */
    private void saveDeviceInfo(String json) {
        log("[P106]保存设备授权信息");
        RuntimeUtility.putToDeviceSP(mCtx, KEY_PAD_DEVICE_INFO, json);
    }

    /**
     * [P107]验证服务是否过期
     *
     * @param content
     */
    public void handleVerifySuccessful(List<PadDeviceInfo> content) {
        log("[P107]验证服务是否过期");
        mPadDeviceInfo = content.get(0);

        String date = mPadDeviceInfo.getEnd_pubdate();
        SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance();
        format.applyPattern("yyyy-MM-dd");
        try {
            long endDate = format.parse(date).getTime();
            long currentDate = System.currentTimeMillis();
            if (currentDate > endDate) {
                error(R.string.service_expiration_detail);
            } else {
                checkAuthorityInfo();
            }
        } catch (ParseException e) {
            e.printStackTrace();
            error(R.string.authority_failed_empty_expiration);
        }
    }

    /**
     * [P109]检查设备授权信息
     */
    private void checkAuthorityInfo() {
        log("[P109]检查设备授权信息");
        if (mPadDeviceInfo == null) {
            error(R.string.authority_failed_detail);
        } else {
            checkVersionUpdate();
        }
    }

    /**
     * [P110]检查是否需要升级版本
     */
    private void checkVersionUpdate() {
        log("[P110]检查是否需要升级版本");
        int currentVersion = Utility.getVersionCode(mCtx);
        int newVersion = parseInt(mPadDeviceInfo.getVersion());
        log("当前版本号：" + currentVersion + "，服务端版本号：" + newVersion);
        if (newVersion > currentVersion) {
            updateVersion();
        } else {
            mPadDeviceInfo.setVersion(String.valueOf(currentVersion));
            updateDeviceInfo();
        }
    }

    private void updateVersion() {
        log("开始更新版本");
        update(R.string.update_start);

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
            downloadApkAndInstall(version);
        }

        @Override
        public void handleResponseFailed() {
            int currentVersion = Utility.getVersionCode(mCtx);
            mPadDeviceInfo.setVersion(String.valueOf(currentVersion));
            updateDeviceInfo();
        }
    }

    private void downloadApkAndInstall(PadVersionInfo version) {
        String fileName = mPadDeviceInfo.getVersion() + ".apk";
        final File apkFile = FileTools.createCacheFile("version/" + fileName);
        try {
            if (apkFile != null && apkFile.exists()) {
                apkFile.delete();
            }
            DownloadHelper helper = DownloadHelper.getInstance(mCtx);
            helper.download(version.getUrl(), apkFile.getAbsolutePath(), new DownloadHelper.OnDownloadListener() {
                @Override
                public void onDownloadComplete(long reference) {
                    installApk(apkFile);
                }

                @Override
                public void onDownloadProgress(DownloadHelper.DownloadQuery query) {
                    update("已下载" + query.getPercent() + "%");
                }
            });
        } catch (Exception ex) {
            int currentVersion = Utility.getVersionCode(mCtx);
            mPadDeviceInfo.setVersion(String.valueOf(currentVersion));
            updateDeviceInfo();
        }
    }

    /**
     * [P114]更新设备信息
     */
    private void updateDeviceInfo() {
        log("[P114]更新设备信息");
        update(R.string.updating_device_info);
        mPadDeviceInfo.setState(PadDeviceInfo.TAG_STATE_ON);
        mPadDeviceInfo.setLast_connect_time(String.valueOf(System.currentTimeMillis()));
        requestUpdateDeviceInfo(mPadDeviceInfo);
    }

    protected void requestUpdateDeviceInfo(PadDeviceInfo deviceInfo) {
        String url = UrlHelper.updateDeviceInfoUrl(deviceInfo);
        send(url, new UpdateDeviceInfoListener(deviceInfo));
    }

    private class UpdateDeviceInfoListener extends SimpleRequestListener {

        PadDeviceInfo deviceInfo;

        private UpdateDeviceInfoListener(PadDeviceInfo deviceInfo) {
            this.deviceInfo = deviceInfo;
        }

        @Override
        public boolean handleResponseStatusOK(String json) {
            log("设备状态更新成功");
            gotoParseActivity();
            return true;
        }

        @Override
        public void handleResponseFailed() {
            log("设备状态更新失败");
            gotoParseActivity();
        }
    }

    /**
     * [P116]前往主解析页
     */
    private void gotoParseActivity() {
        log("设备信息解析成功！");
        getPadScene();
    }

    /**
     * [P203]获取当前设备的场景
     */
    private void getPadScene() {
        log("[P203]获取当前设备的场景");
        update(R.string.verify_start_get_scene);
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
            error(R.string.verify_error_scene_parse);
        }
    }

    /**
     * [P204]验证场景的学校编号，和当前设备对应的学校编号是否一致
     * 要求场景的学校编号和当前设备的学校编号一致，否则场景将无法使用
     */
    private void verifySchoolBh() {
        log("[P204]验证场景的学校编号");
        update(R.string.verify_start_parse_school_code);
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
                error(R.string.verify_error_school_code);
            }
        }
    }

    /**
     * [P206]验证当前场景是否处于激活状态
     */
    private void verifyScenceIsActivate() {
        log("[P206]验证当前场景是否处于激活状态");
        update(R.string.verify_scene_is_activate);
        String isActivate = mPadScene.getActivate();
        if (isActivate.equals(PadScene.IS_ACTIVATE)) {
            checkScenceAuthority();
        } else {
            error(R.string.verify_error_scene_closed);
        }
    }

    /**
     * [P207]与学校的最高权限进行比对
     */
    private void checkScenceAuthority() {
        log("[P207]检查学校最高处理权限");
        update(R.string.verify_scene_top_authority);
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
            getSceneAuthority();
        }

        @Override
        public void handleResponseFailed() {
            error(R.string.verify_error_get_school_info);
        }
    }

    /**
     * [P208]获取场景的处理权限
     */
    private void getSceneAuthority() {
        log("[P208]获取场景的处理权限");
        update(R.string.verify_start_get_authority);
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
            error(R.string.verify_error_get_authority);
        }
    }

    /**
     * [P208]处理场景
     */
    private void parseScene() {
        update(R.string.verify_start_parse_scene);
        getSceneModules();
    }

    /**
     * [P301]获取场景下的所有模块
     */
    protected void getSceneModules() {
        log("[P301]获取场景下的所有模块");
        String getSceneModulesUrl = UrlHelper.getSceneModules(mPadDeviceInfo, mPadScene);
        send(getSceneModulesUrl, new GetSceneModulesListener());
    }

    private class GetSceneModulesListener extends SimpleRequestListener<PadSceneModule> {
        @Override
        public ResponseData<PadSceneModule> parseJson(String json) {
            return JsonParseHelper.parseSceneModulesResponse(json);
        }

        @Override
        public void handleRespone(List<PadSceneModule> content) {
            /**
             * [P302]判断模块数量是否大于等于1
             */
            log("[P302]模块数量大于0");
            mPadSceneModules = content;
            parseModuleList();
        }

        @Override
        public void handleResponseFailed() {
            error(R.string.module_error_get_module);
        }
    }

    /**
     * [P303]解析所有模块
     */
    protected void parseModuleList() {
        log("[P303]解析所有模块");
        for (int i = 0; i < mPadSceneModules.size(); i++) {
            PadModuleType moduleType = pickFirst(mPadSceneModules.get(i).getModule_type_id());
            if (moduleType == null) continue;
            int type = parseInt(moduleType.getType());
            if (type == PadModuleType.MODULE_TYPE_GUIDE) {
                mGuideModule.add(mPadSceneModules.get(i));
            } else if (type == PadModuleType.MODULE_TYPE_HOME) {
                mHomeModule.add(mPadSceneModules.get(i));
            } else if (type == PadModuleType.MODULE_TYPE_SUBPAGE) {
                mSubpageModule.add(mPadSceneModules.get(i));
            } else if (type == PadModuleType.MODULE_TYPE_CONTENT) {
                mContentModule.add(mPadSceneModules.get(i));
            } else if (type == PadModuleType.MODULE_TYPE_HELP) {
                mHelpModule.add(mPadSceneModules.get(i));
            }
        }
        checkGuidePage();
    }

    /**
     * [P304]检查是否存在引导页且数量等于1
     */
    private void checkGuidePage() {
        log("[P304]检查是否存在引导页且数量等于1");
        if (mGuideModule.size() > 1) {
            error(R.string.module_error_guide_page_number);
        } else if (isEmpty(mGuideModule)) {
            checkHomePage();
        } else {
            renderModule(true);
        }
    }

    /**
     * [P305]检查是否存在首页
     */
    private void checkHomePage() {
        log("[P305]检查是否存在首页");
        if (isEmpty(mHomeModule)) {
            error(R.string.module_error_guide_and_home_page);
        } else {
            renderModule(false);
        }
    }

    /**
     * [P306]渲染模块
     *
     * @param hasGuide 是否包含引导页
     */
    public void renderModule(boolean hasGuide) {
        log("[P306]渲染模块");
        PadSceneModule module;
        if (hasGuide) {
            module = mGuideModule.get(0);
            log("开始渲染引导页");
        } else {
            module = mHomeModule.get(0);
            log("开始渲染首页");
        }
        launchModule(module);
    }

    protected void launchModule(PadSceneModule module) {
        String activityPath = buildCodePath(mPadAuthority.getModule_parse_code());
        log("activity path=" + activityPath);
        Intent intent = new Intent();
        intent.setClassName(mCtx, activityPath);
        Bundle arg = new Bundle();
        arg.putParcelable(KEY_PAD_DEVICE_INFO, mPadDeviceInfo);
        arg.putParcelable(KEY_PAD_SCHOOL, mPadSchool);
        arg.putParcelable(KEY_PAD_SCENE, mPadScene);
        arg.putParcelable(KEY_PAD_AUTHORITY, mPadAuthority);
        arg.putParcelable(KEY_PAD_MODULE_INFO, module);

        arg.putParcelableArrayList(KEY_PAD_MODULE_GUIDE, mGuideModule);
        arg.putParcelableArrayList(KEY_PAD_MODULE_HOME, mHomeModule);
        arg.putParcelableArrayList(KEY_PAD_MODULE_SUBPAGE, mSubpageModule);
        arg.putParcelableArrayList(KEY_PAD_MODULE_CONTENT, mContentModule);
        arg.putParcelableArrayList(KEY_PAD_MODULE_HELP, mHelpModule);

        intent.putExtras(arg);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        ((Activity) mCtx).overridePendingTransition(0, 0);
        ((Activity) mCtx).finish();

        ((Activity) mCtx).overridePendingTransition(0, 0);
        mCtx.startActivity(intent);
    }

    private boolean isEmpty(String str) {
        return TextUtils.isEmpty(str);
    }

    protected <T> boolean isEmpty(List<T> data) {
        return data == null || data.size() <= 0;
    }

    private void send(String url, RequestHelper.OnRequestListener listener) {
        RequestHelper.getInstance(mCtx, url, listener).send();
    }

    private void log(String msg) {
        AppLogger.e("---------------- " + msg + " ----------------");
    }

    private int parseInt(String value) {
        return Utility.safeIntParse(value, 0);
    }

    private int parseInt(String value, int defaultValue) {
        return Utility.safeIntParse(value, defaultValue);
    }

    private void installApk(File file) {
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        mCtx.startActivity(intent);
    }

    private void error(String msg) {
        if (mVerifyDeviceListener != null) {
            mVerifyDeviceListener.onVerifyError(msg);
        }
    }

    private void error(int resId) {
        if (mVerifyDeviceListener != null) {
            mVerifyDeviceListener.onVerifyError(getString(resId));
        }
    }

    private void update(String msg) {
        if (mVerifyDeviceListener != null) {
            mVerifyDeviceListener.update(msg);
        }
    }

    private void update(int resId) {
        if (mVerifyDeviceListener != null) {
            mVerifyDeviceListener.update(getString(resId));
        }
    }

    private String getString(int resId) {
        return mCtx.getString(resId);
    }

    private <T> T pickFirst(List<T> data) {
        if (isEmpty(data)) return null;
        return data.get(0);
    }

    private  <T> void clear(List<T> data) {
        if (isEmpty(data)) return;
        data.clear();
    }

    private String buildCodePath(String activityPath) {
        String packageName = mCtx.getPackageName();
        StringBuffer buffer = new StringBuffer();
        buffer.append(packageName).append(".").append(activityPath);
        return buffer.toString();
    }

}
