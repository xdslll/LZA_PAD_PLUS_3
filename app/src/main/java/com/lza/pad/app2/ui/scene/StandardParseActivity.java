package com.lza.pad.app2.ui.scene;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lza.pad.R;
import com.lza.pad.app2.service.ServiceMode;
import com.lza.pad.app2.ui.base.BaseParseActivity;
import com.lza.pad.app2.ui.device.UniversalVerifyActivity;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.db.model.pad.PadModuleType;
import com.lza.pad.db.model.pad.PadSceneModule;
import com.lza.pad.helper.JsonParseHelper;
import com.lza.pad.helper.SimpleRequestListener;
import com.lza.pad.helper.UrlHelper;
import com.lza.pad.support.utils.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;

/**
 * 标准版的场景解析类
 *
 * 具备以下功能：
 * 1、管理场景间的切换
 * 2、管理场景下的所有模块
 *
 * @author xiads
 * @Date 3/10/15.
 */
public class StandardParseActivity extends BaseParseActivity {

    ArrayList<PadSceneModule> mGuideModule = new ArrayList<PadSceneModule>();
    ArrayList<PadSceneModule> mHomeModule = new ArrayList<PadSceneModule>();
    ArrayList<PadSceneModule> mSubpageModule = new ArrayList<PadSceneModule>();
    ArrayList<PadSceneModule> mContentModule = new ArrayList<PadSceneModule>();
    ArrayList<PadSceneModule> mHelpModule = new ArrayList<PadSceneModule>();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopDeviceUpdateService();
    }

    /**
     * [P303]解析所有模块
     */
    @Override
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
            handleModuleErrorProcess(R.string.dialog_prompt, R.string.module_error_guide_page_number);
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
            handleModuleErrorProcess(R.string.dialog_prompt, R.string.module_error_guide_and_home_page);
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
        dismissProgressDialog();
        PadSceneModule module;
        if (hasGuide) {
            module = mGuideModule.get(0);
            log("开始渲染引导页");
        } else {
            module = mHomeModule.get(0);
            log("开始渲染首页");
        }
        dismissLoadingView();
        launchModule(module);
        activateRestartButton();
        startDeviceUpdateService();
    }

    protected void activateRestartButton() {
        mBtnRestartModule.setVisibility(View.VISIBLE);
    }

    /**
     * 启动设备更新服务
     */
    ScheduledExecutorService mUpdateDeviceService;
    public int UPDATE_DEVICE_DELAY = 10;
    boolean mIsUpdating = false;

    public void startDeviceUpdateService() {
        UPDATE_DEVICE_DELAY = parseInt(mPadDeviceInfo.getUpdate_time(), UPDATE_DEVICE_DELAY);
        log("启动更新程序，[" + UPDATE_DEVICE_DELAY + "]秒轮回一次");
        mUpdateDeviceService = Executors.newSingleThreadScheduledExecutor();
        mUpdateDeviceService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                //在主线程中处理
                try {
                    getMainHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (!mIsUpdating) {
                                mIsUpdating = true;
                                log("开始检查更新状态");
                                checkDeviceInfo();
                            } else {
                                log("正在更新");
                            }
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, UPDATE_DEVICE_DELAY, UPDATE_DEVICE_DELAY, TimeUnit.SECONDS);
    }

    private void checkDeviceInfo() {
        String macAddress = Utility.getMacAddress(mCtx);
        String getDeviceInfoUrl = UrlHelper.getDeviceUrl(macAddress);
        send(getDeviceInfoUrl, mDeviceListener);
    }

    private SimpleRequestListener<PadDeviceInfo> mDeviceListener = new SimpleRequestListener<PadDeviceInfo>() {
        @Override
        public void handleRespone(List<PadDeviceInfo> content) {
            mPadDeviceInfo = content.get(0);
            log("开始检查版本更新状况");
            if (checkVersion()) {
                log("需要更新版本，开始启动更新程序");
                updateVersion();
            } else {
                log("不需要更新版本，开始检查界面更新");
                if (checkUpdateUI()) {
                    log("需要更新界面，开始更新");
                    updateUI();
                } else {
                    updateDeviceInfo();
                }
            }
        }

        @Override
        public void handleResponseFailed() {
            log("获取设备信息失败");
            mIsUpdating = false;
        }

        @Override
        public ResponseData<PadDeviceInfo> parseJson(String json) {
            return JsonParseHelper.parseDeviceInfoResponse(json);
        }
    };

    /**
     * 更新版本
     *
     * @return true - 需要更新，false - 不需要更新
     */
    private boolean checkVersion() {
        String deviceVersion = mPadDeviceInfo.getVersion();
        int currentVersion = Utility.getVersionCode(mCtx);
        if (!isEmpty(deviceVersion)) {
            log("当前版本号：" + currentVersion + ",新版本号：" + deviceVersion);
            if (currentVersion < parseInt(deviceVersion)) {
                mPadDeviceInfo.setVersion(String.valueOf(currentVersion));
                return true;
            } else {
                return false;
            }
        } else {
            mPadDeviceInfo.setVersion(String.valueOf(currentVersion));
            return false;
        }
    }

    /**
     * 回到设备验证界面更新版本
     */
    private void updateVersion() {
        EventBus.getDefault().post(ServiceMode.MODE_UPDATE_VERSION);
        Intent intent = new Intent(mCtx, UniversalVerifyActivity.class);
        startActivity(intent);
    }

    /**
     * 更新标识
     */
    String mUpdateTag;
    int mUpdateTime;

    /**
     * 检查是否需要更新当前界面
     */
    private boolean checkUpdateUI() {
        mUpdateTag = mPadDeviceInfo.getUpdate_tag();
        //获取更新时间
        mUpdateTime = parseInt(mPadDeviceInfo.getUpdate_time()) * 1000;
        if (mUpdateTag.equals(PadDeviceInfo.TAG_NEED_UDPATE)) {
            log("需要更新");
            //检查自动更新标识
            String autoUpdateTag = mPadDeviceInfo.getAuto_update();
            if (autoUpdateTag.equals(PadDeviceInfo.TAG_AUTO_UPDATE)) {
                return true;
            } else {
                //不允许自动更新
                log("不允许自动更新，界面将不会更新");
                return false;
            }
        } else if (mUpdateTag.equals(PadDeviceInfo.TAG_HAVE_UDPATE)) {
            log("已经更新");
            return false;
        } else {
            log("未知状态");
            return false;
        }
    }

    /**
     * 开始更新UI
     */
    public void updateUI() {
        mPadDeviceInfo.setUpdate_tag(PadDeviceInfo.TAG_HAVE_UDPATE);
        mPadDeviceInfo.setLast_connect_time(String.valueOf(System.currentTimeMillis()));
        mPadDeviceInfo.setState(PadDeviceInfo.TAG_STATE_ON);
        //允许自动更新
        requestUpdateDeviceInfo(mPadDeviceInfo, new SimpleRequestListener() {
            @Override
            public boolean handleResponseStatusOK(String json) {
                log("设备状态更新成功");
                log("开始更新程序");
                EventBus.getDefault().post(ServiceMode.MODE_SWITCH_SCENE);
                gotoParseActivity();
                return true;
            }

            @Override
            public void handleResponseFailed() {
                log("设备状态更新失败");
                mIsUpdating = false;
            }
        });
    }

    /**
     * 更新设备信息
     */
    public void updateDeviceInfo() {
        log("开始更新设备信息");
        mPadDeviceInfo.setLast_connect_time(String.valueOf(System.currentTimeMillis()));
        mPadDeviceInfo.setState(PadDeviceInfo.TAG_STATE_ON);
        //允许自动更新
        requestUpdateDeviceInfo(mPadDeviceInfo, new SimpleRequestListener() {
            @Override
            public boolean handleResponseStatusOK(String json) {
                log("设备状态更新成功");
                mIsUpdating = false;
                return true;
            }

            @Override
            public void handleResponseFailed() {
                log("设备状态更新失败");
                mIsUpdating = false;
            }
        });
    }

    public void stopDeviceUpdateService() {
        try {
            log("关闭设备巡查服务");
            mUpdateDeviceService.shutdownNow();
        } catch (Exception ex) {

        }
    }

    @Override
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

        overridePendingTransition(0, 0);
        startActivity(intent);
        //finish();
    }

    /**
     * [P308]清空场景数据
     */
    @Override
    protected void resetSceneData() {
        log("[P308]获取场景下的所有模块");

        clear(mGuideModule);
        clear(mHomeModule);
        clear(mSubpageModule);
        clear(mContentModule);
        clear(mHelpModule);

        stopDeviceUpdateService();
        cancelAlarmService(mSceneSwitchingPendingIntent);
    }

    @Override
    protected void launchModuleByType(int type) {
        if (type == PadModuleType.MODULE_TYPE_GUIDE) {
            if (!isEmpty(mGuideModule)) {
                launchModule(mGuideModule.get(0));
            } else {
                if (isEmpty(mHomeModule)) {
                    launchModule(mHomeModule.get(0));
                } else {
                    handleModuleErrorProcess(R.string.dialog_prompt, R.string.module_error_guide_and_home_page);
                }
            }
        } else if (type == PadModuleType.MODULE_TYPE_HOME) {
            if (!isEmpty(mHomeModule)) {
                launchModule(mHomeModule.get(0));
            } else {
                if (isEmpty(mGuideModule)) {
                    launchModule(mGuideModule.get(0));
                } else {
                    handleModuleErrorProcess(R.string.dialog_prompt, R.string.module_error_guide_and_home_page);
                }
            }
        } else if (type == PadModuleType.MODULE_TYPE_HELP) {
            if (!isEmpty(mHelpModule)) {
                launchModule(mHelpModule.get(0));
            } else {
                if (isEmpty(mGuideModule)) {
                    launchModule(mGuideModule.get(0));
                } else if (isEmpty(mHomeModule)) {
                    launchModule(mHomeModule.get(0));
                } else {
                    handleModuleErrorProcess(R.string.dialog_prompt, R.string.module_error_guide_and_home_page);
                }
            }
        }
    }
}
