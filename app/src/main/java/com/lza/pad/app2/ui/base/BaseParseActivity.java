package com.lza.pad.app2.ui.base;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import com.lza.pad.app2.service.BaseService;
import com.lza.pad.app2.service.ServiceMode;
import com.lza.pad.app2.ui.scene.MainParseActivity;
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

import de.greenrobot.event.EventBus;

/**
 * 核心类，处理场景解析
 *
 * @author xiads
 * @Date 3/12/15.
 */
public abstract class BaseParseActivity extends BaseActivity {

    protected PadDeviceInfo mPadDeviceInfo;
    protected PadScene mPadScene;

    protected PadSchool mPadSchool;
    protected PadAuthority mPadAuthority;

    protected PadScene mNextScene;
    protected PadSwitching mPadSceneSwitchingMode;
    protected PadSceneSwitching mPadSceneSwitching;

    protected List<PadSceneModule> mPadSceneModules = new ArrayList<PadSceneModule>();

    private AlarmManager mAlarmManager;
    private PendingIntent mSceneSwitchingPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log("onCreate");
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            mPadDeviceInfo = getIntent().getParcelableExtra(KEY_PAD_DEVICE_INFO);
            mPadScene = getIntent().getParcelableExtra(KEY_PAD_SCENE);
            mPadSchool = getIntent().getParcelableExtra(KEY_PAD_SCHOOL);
            mPadAuthority = getIntent().getParcelableExtra(KEY_PAD_AUTHORITY);
        }
        showProgressDialog("开始解析场景", true);
        getSceneSwitching();
        getSceneModules();
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        registerEventBus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelAlarmService(mSceneSwitchingPendingIntent);
        unregisterEventBus();
    }

    public void onEvent(ServiceMode mode) {
        if (mode == ServiceMode.MODE_RESET_SERVICE) {
            cancelAlarmService(mSceneSwitchingPendingIntent);
            parseSceneSwitchingService();
        } else if (mode == ServiceMode.MODE_SWITCH_SCENE) {
            finish();
        } else if (mode == ServiceMode.MODE_UPDATE_VERSION) {
            finish();
        }
    }

    public PendingIntent initAlarmService(String action, Bundle arg) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtras(arg);
        return PendingIntent.getService(mCtx,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void startAlarmService(PendingIntent pendingIntent, int delay) {
        log("启动场景Alarm服务");
        long triggerAtTime = SystemClock.elapsedRealtime();

        triggerAtTime += delay * 1000;
        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME, triggerAtTime, pendingIntent);
    }

    public void cancelAlarmService(PendingIntent pendingIntent) {
        log("取消场景Alarm服务");
        if (pendingIntent == null) return;
        mAlarmManager.cancel(pendingIntent);
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
            dismissProgressDialog();
            mPadSceneModules = content;
            parseModuleList();
        }

        @Override
        public void handleResponseFailed() {
            handleErrorProcess("提示", "模块获取失败，请重试！");
        }
    }

    /**
     * [P303]解析所有模块
     */
    protected abstract void parseModuleList();

    /**
     * [P306]启动模块
     */
    protected abstract void launchModule(PadSceneModule module);

    /**
     * [P308]重置场景切换后的数据
     */
    protected abstract void resetSceneData();

    /**
     * [P309]处理失败流程
     *
     * @param title
     * @param message
     */
    protected void handleErrorProcess(String title, String message) {
        /*dismissProgressDialog();
        UniversalUtility.showDialog(mCtx, title, message,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getSceneModules();
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        backToDeviceAuthorityActivity();
                    }
                });*/
        handleErrorProcess(title, message, new Runnable() {
            @Override
            public void run() {
                getSceneModules();
            }
        });
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
            mPadSceneSwitching = content.get(0);
            checkSceneSwitching();
        }

        @Override
        public void handleResponseFailed() {

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
        if (mNextScene == null) {
            log("不存在下一个场景");
            return;
        }
        checkSceneDuplicated();
    }

    /**
     * [SP103]下一个场景是否为当前场景
     */
    private void checkSceneDuplicated() {
        log("[SP103]下一个场景是否为当前场景");
        if (mNextScene.equals(mPadScene)) return;
        parseSceneSwitchingService();
    }

    /**
     * [SP104]启动场景切换服务
     */
    private void parseSceneSwitchingService() {
        log("[SP104]启动场景切换服务");
        if (mPadSceneSwitching == null ||
                mPadSceneSwitching.getSwitching_mode() == null ||
                mPadSceneSwitching.getSwitching_mode().size() <= 0) return;
        mPadSceneSwitchingMode = mPadSceneSwitching.getSwitching_mode().get(0);
        String mode = mPadSceneSwitchingMode.getTigger_mode();
        if (mode.equals(PadSwitching.TRIGGER_MODE_FIX_DAY)) {
            log("固定日期时触发场景切换");
        } else if (mode.equals(PadSwitching.TRIGGER_MODE_FIX_TIME)) {
            log("固定时间时触发场景切换");
        } else if (mode.equals(PadSwitching.TRIGGER_MODE_FIX_DELAY)) {
            log("固定延迟后触发场景切换");
            int delay = parseInt(mPadSceneSwitchingMode.getTrigger_interval());
            switchingScene(delay);
        }
    }

    /**
     * 通过计时器转换场景
     *
     * @param delay
     */
    private void switchingScene(int delay) {
        log("启动场景定时器，延迟：" + delay + "s");
        Bundle arg = new Bundle();
        arg.putParcelable(KEY_PAD_DEVICE_INFO, mPadDeviceInfo);
        arg.putParcelable(KEY_PAD_NEXT_SCENE, mNextScene);
        mSceneSwitchingPendingIntent = initAlarmService(ACTION_SCENE_SWITCHING_SERVICE, arg);
        startAlarmService(mSceneSwitchingPendingIntent, delay);
    }

    protected void launchModuleByType(PadModuleType moduleType) {
        int type = parseInt(moduleType.getType());
        launchModuleByType(type);
    }

    /**
     * 通过模块类型启动模块
     *
     * @param type
     */
    protected void launchModuleByType(int type) {}

    public static class SceneSwitchingService extends BaseService {

        PadDeviceInfo mPadDeviceInfo;
        PadScene mNextScene;

        @Override
        public void onCreate() {
            super.onCreate();
            log("场景切换服务启动");
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            log("场景切换服务关闭");
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            log("启动场景切换服务");
            mPadDeviceInfo = intent.getParcelableExtra(KEY_PAD_DEVICE_INFO);
            mNextScene = intent.getParcelableExtra(KEY_PAD_NEXT_SCENE);
            switchScene();
            return START_STICKY;
        }

        /**
         * 切换场景
         *
         */
        private void switchScene() {
            if (mNextScene == null || mPadDeviceInfo == null) return;
            log("加载切换后的场景:" + mNextScene.getName());
            //发送场景切换广播，关闭所有已经打开的模块
            EventBus.getDefault().post(ServiceMode.MODE_SWITCH_SCENE);
            //发送广播请求切换场景
            mPadDeviceInfo.setScene_id(mNextScene.getId());
            gotoParseActivity();
            //关闭服务
            stopSelf();
        }

        private void gotoParseActivity() {
            Intent intent = new Intent(mCtx, MainParseActivity.class);
            intent.putExtra(KEY_PAD_DEVICE_INFO, mPadDeviceInfo);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    protected void gotoParseActivity() {
        Intent intent = new Intent(mCtx, MainParseActivity.class);
        intent.putExtra(KEY_PAD_DEVICE_INFO, mPadDeviceInfo);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
