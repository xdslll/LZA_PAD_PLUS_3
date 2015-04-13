package com.lza.pad.app2.ui.scene;

import android.content.Intent;
import android.os.Bundle;

import com.lza.pad.app2.base.IScene;
import com.lza.pad.app2.service.BaseService;
import com.lza.pad.app2.service.ServiceMode;
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
import com.lza.pad.support.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/3/20.
 */
public class StandardParseService extends BaseService implements IScene {

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
    private ScheduledExecutorService mService;

    @Override
    public void onCreate() {
        log("StandardParseService onCreate");
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log("StandardParseService onStartCommand");
        if (intent != null) {
            mPadDeviceInfo = intent.getParcelableExtra(KEY_PAD_DEVICE_INFO);
            mPadScene = intent.getParcelableExtra(KEY_PAD_SCENE);
            mPadSchool = intent.getParcelableExtra(KEY_PAD_SCHOOL);
            mPadAuthority = intent.getParcelableExtra(KEY_PAD_AUTHORITY);
        }
        if (mPadDeviceInfo == null || mPadScene == null) {
            log("服务启动异常，请检查！");
            return super.onStartCommand(intent, flags, startId);
        }
        getSceneModules();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        log("StandardParseService onDestroy");
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        stopSwitchingService();
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
            getSceneSwitching();
        }

        @Override
        public void handleResponseFailed() {
            handleErrorProcess("模块获取失败，请重试！");
        }
    }

    /**
     * [P303]解析所有模块
     */
    @Override
    public void parseModuleList() {
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

    @Override
    public void launchModule(PadSceneModule module) {
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
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }

    /**
     * [P308]清空场景数据
     */
    public void resetSceneData() {
        log("[P308]清空场景数据");

        clear(mGuideModule);
        clear(mHomeModule);
        clear(mSubpageModule);
        clear(mContentModule);
        clear(mHelpModule);

        mHasElapse = 0;
    }

    /**
     * [P309]处理失败流程
     *
     * @param message
     */
    protected void handleErrorProcess(String message) {
        ToastUtils.showLong(mCtx, message);
    }

    /**
     * [SP101]查询当前场景是否存在切换服务
     */
    private void getSceneSwitching() {
        log("[SP101]查询当前场景是否存在切换服务");
        String sceneSwitchingUrl = UrlHelper.getPadSceneSwitching(mPadDeviceInfo, mPadScene);
        send(sceneSwitchingUrl, new SceneSwitchingListener());
    }

    private class SceneSwitchingListener extends SimpleRequestListener<PadSceneSwitching> {

        @Override
        public ResponseData<PadSceneSwitching> parseJson(String json) {
            return JsonParseHelper.parsePadSceneSwitchingResponse(json);
        }

        @Override
        public void handleRespone(List<PadSceneSwitching> content) {
            if (content.size() > 1) {
                handleErrorProcess("切换场景数大于1，请查看并调整！");
            } else {
                mPadSceneSwitching = content.get(0);
                checkSceneSwitching();
            }
        }

        @Override
        public void handleResponseFailed() {
            log("当前场景不存在场景切换服务");
        }
    }

    /**
     * [SP102]是否存在下一个场景
     */
    private void checkSceneSwitching() {
        log("[SP102]是否存在下一个场景");
        if (mPadSceneSwitching == null ||
                mPadSceneSwitching.getNext_scene() == null ||
                mPadSceneSwitching.getNext_scene().size() <= 0) {
            log("不存在切换场景");
            return;
        }
        mNextScene = mPadSceneSwitching.getNext_scene().get(0);
        if (mNextScene == null) return;
        checkSceneDuplicated();
    }

    /**
     * [SP103]下一个场景是否为当前场景
     */
    private void checkSceneDuplicated() {
        log("[SP103]下一个场景是否为当前场景");
        if (mNextScene.equals(mPadScene)) return;
        startSwitchingService();
    }

    /**
     * [SP104]启动场景切换服务
     */
    private void startSwitchingService() {
        log("[SP104]启动场景切换服务");
        if (mPadSceneSwitching.getSwitching_mode() == null ||
                mPadSceneSwitching.getSwitching_mode().size() <= 0) return;
        mPadSwitchingMode = mPadSceneSwitching.getSwitching_mode().get(0);
        String mode = mPadSwitchingMode.getTigger_mode();
        if (mode.equals(PadSwitching.TRIGGER_MODE_FIX_DAY)) {
            log("固定日期时触发场景切换");
        } else if (mode.equals(PadSwitching.TRIGGER_MODE_FIX_TIME)) {
            log("固定时间时触发场景切换");
        } else if (mode.equals(PadSwitching.TRIGGER_MODE_FIX_DELAY)) {
            log("固定延迟后触发场景切换");
            int delay = parseInt(mPadSwitchingMode.getTrigger_interval());
            switchingScene(delay);
        }
    }

    /**
     * 通过计时器转换场景
     *
     * @param delay
     */
    private void switchingScene(final int delay) {
        log("启动定时器，延迟：" + delay + "s");
        stopSwitchingService();
        mService = Executors.newSingleThreadScheduledExecutor();
        mService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                log("当前延迟（计算前）：" + mHasElapse);
                mHasElapse += FIXED_DELAY;
                log("当前延迟（计算后）：" + mHasElapse);
                if (mHasElapse >= delay) {
                    switchScene(mNextScene);
                    log("关闭场景切换服务");
                    mService.shutdownNow();
                }
            }
        }, FIXED_DELAY, FIXED_DELAY, TimeUnit.SECONDS);
    }

    /**
     * 切换场景
     *
     * @param nextScene
     */
    private void switchScene(PadScene nextScene) {
        //发送场景切换广播，关闭所有已经打开的模块
        EventBus.getDefault().post(ServiceMode.MODE_SWITCH_SCENE);
        mPadScene = nextScene;
        if (mPadScene == null) return;
        log("加载切换后的场景:" + mPadScene.getName());
        //resetSceneData();
        //getSceneModules();
        //发送广播请求切换场景
        mPadDeviceInfo.setScene_id(mPadScene.getId());
        EventBus.getDefault().post(mPadDeviceInfo);
        //关闭服务
        stopSelf();
    }

    private void stopSwitchingService() {
        if (mService != null && !mService.isShutdown()) {
            mService.shutdownNow();
        }
    }

    /**
     * 监听器
     *
     * @param mode
     */
    public void onEvent(ServiceMode mode) {
        if (mode == ServiceMode.MODE_RESET_SERVICE) {
            log("启动重置服务");
            mHasElapse = 0;
        } else if (mode == ServiceMode.MODE_START_HOME_MODULE) {
            log("启动首页");
            launchModuleByType(PadModuleType.MODULE_TYPE_HOME);
        }
    }

    protected void launchModuleByType(int type) {
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
    }
}
