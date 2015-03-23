package com.lza.pad.app2.ui.scene;

import android.content.Intent;
import android.os.Bundle;

import com.lza.pad.app2.base.IScene;
import com.lza.pad.app2.service.BaseIntentService;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.pad.PadAuthority;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.db.model.pad.PadModuleType;
import com.lza.pad.db.model.pad.PadScene;
import com.lza.pad.db.model.pad.PadSceneModule;
import com.lza.pad.db.model.pad.PadSceneSwitching;
import com.lza.pad.db.model.pad.PadSchool;
import com.lza.pad.db.model.pad.PadSwitching;
import com.lza.pad.helper.JsonParseHelper;
import com.lza.pad.helper.SimpleRequestListener;
import com.lza.pad.helper.UrlHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/3/20.
 */
public class StandardParseService extends BaseIntentService implements IScene {

    PadDeviceInfo mPadDeviceInfo;
    PadScene mPadScene;

    PadSchool mPadSchool;
    PadAuthority mPadAuthority;

    PadScene mNextScene;
    PadSceneSwitching mPadSceneSwitching;
    PadSwitching mPadSwitchingMode;

    List<PadSceneModule> mPadSceneModules = new ArrayList<PadSceneModule>();

    ArrayList<PadSceneModule> mGuideModule = new ArrayList<PadSceneModule>();
    ArrayList<PadSceneModule> mHomeModule = new ArrayList<PadSceneModule>();
    ArrayList<PadSceneModule> mSubpageModule = new ArrayList<PadSceneModule>();
    ArrayList<PadSceneModule> mContentModule = new ArrayList<PadSceneModule>();
    ArrayList<PadSceneModule> mHelpModule = new ArrayList<PadSceneModule>();

    /**
     * 已经走过的时间
     */
    private int mHasElapse = 0;
    /**
     * 隔多久执行一次服务，以秒的单位
     */
    private int FIXED_DELAY = 10;

    /**
     * 定时器服务
     */
    //private ScheduledExecutorService mService;

    public StandardParseService() {
        super("StandardParseService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            mPadDeviceInfo = intent.getParcelableExtra(KEY_PAD_DEVICE_INFO);
            mPadScene = intent.getParcelableExtra(KEY_PAD_SCENE);
            mPadSchool = intent.getParcelableExtra(KEY_PAD_SCHOOL);
            mPadAuthority = intent.getParcelableExtra(KEY_PAD_AUTHORITY);
            //startSceneSwitchingService();
            getSceneModules(mPadScene);
        }
    }

    /*public void onEvent(ServiceMode mode) {
        if (mode == ServiceMode.MODE_RESET_SERVICE) {
            mHasElapse = 0;
        } else if (mode == ServiceMode.MODE_STOP_SCENE_SERVICE) {
            release();
        } else if (mode == ServiceMode.MODE_START_HOME_MODULE) {
            launchModuleByType(PadModuleType.MODULE_TYPE_HOME);
        }
    }*/

    /**
     * [P302]获取场景下的所有模块
     */
    @Override
    public void getSceneModules(PadScene scene) {
        log("[P302]获取场景下的所有模块");
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
             * [P303]模块数量大于等于1
             */
            log("[P303]模块数量大于等于1");
            mPadSceneModules = content;
            parseModuleList();
        }

        @Override
        public void handleResponseFailed() {
            handleErrorProcess("模块获取失败，请重试！");
        }
    }

    /**
     * [P307]解析所有模块
     */
    private void parseModuleList() {
        log("[P307]解析所有模块");
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
            handleErrorProcess("引导页数量大于1，请检查后重试！");
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
            handleErrorProcess("引导页和首页都不存在，请检查后重试！");
        } else {
            renderModule(false);
        }
    }

    /**
     * [P306]渲染模块
     *
     * @param hasGuide 是否包含引导页
     */
    private void renderModule(boolean hasGuide) {
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

    @Override
    public void launchModule(PadSceneModule module) {
        String activityPath = buildCodePath(mPadAuthority.getModule_parse_code());
        log("activity path=" + activityPath);
        Intent intent = new Intent();
        intent.setClassName(getBaseContext(), activityPath);
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
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        Intent intent2 = new Intent(ACTION_START_PARSE_RECEIVER);
        sendBroadcast(intent2);
    }

    /**
     * [P308]清空场景数据
     */
    public void resetSceneData() {
        log("[P308]清空场景下的所有模块");

        clear(mGuideModule);
        clear(mHomeModule);
        clear(mSubpageModule);
        clear(mContentModule);
        clear(mHelpModule);
    }

    /**
     * 通过模块类型启动模块
     *
     */
    /*protected void launchModuleByType(int type) {
        if (type == PadModuleType.MODULE_TYPE_GUIDE) {
            if (!isEmpty(mGuideModule)) {
                launchModule(mGuideModule.get(0));
            } else {
                if (isEmpty(mHomeModule)) {
                    launchModule(mHomeModule.get(0));
                } else {
                    handleErrorProcess("缺少引导模块和首页，请检查后重试！");
                }
            }
        } else if (type == PadModuleType.MODULE_TYPE_HOME) {
            if (!isEmpty(mHomeModule)) {
                launchModule(mHomeModule.get(0));
            } else {
                if (isEmpty(mGuideModule)) {
                    launchModule(mGuideModule.get(0));
                } else {
                    handleErrorProcess("缺少引导模块和首页，请检查后重试！");
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
                    handleErrorProcess("缺少引导模块和首页，请检查后重试！");
                }
            }
        }
    }*/

    private void handleErrorProcess(String message) {
        Intent intent = new Intent(ACTION_START_PARSE_ERROR_RECEIVER);
        intent.putExtra(KEY_START_PARSE_ERROR, message);
        sendBroadcast(intent);
    }
}

/*private void release() {
        log("关闭场景服务");
        if (mService != null && !mService.isShutdown()) {
            mService.shutdownNow();
        }
    }*/

