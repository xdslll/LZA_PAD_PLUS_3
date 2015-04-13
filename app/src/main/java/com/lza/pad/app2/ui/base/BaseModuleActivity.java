package com.lza.pad.app2.ui.base;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import com.lza.pad.app2.service.BaseService;
import com.lza.pad.app2.service.ServiceMode;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.pad.PadAuthority;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.db.model.pad.PadModule;
import com.lza.pad.db.model.pad.PadModuleSwitching;
import com.lza.pad.db.model.pad.PadModuleType;
import com.lza.pad.db.model.pad.PadScene;
import com.lza.pad.db.model.pad.PadSceneModule;
import com.lza.pad.db.model.pad.PadSchool;
import com.lza.pad.db.model.pad.PadSwitching;
import com.lza.pad.helper.JsonParseHelper;
import com.lza.pad.helper.SimpleRequestListener;
import com.lza.pad.helper.UrlHelper;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 所有模块都必须继承的父类
 *
 * @author xiads
 * @Date 15/3/23.
 */
public class BaseModuleActivity extends BaseActivity {

    protected PadDeviceInfo mPadDeviceInfo;
    protected PadScene mPadScene;
    protected PadSchool mPadSchool;
    protected PadAuthority mPadAuthority;
    protected PadSceneModule mPadSceneModule;
    protected PadModule mPadModule;
    protected PadModuleType mPadModuleType;

    protected PadModuleType mNextModuleType;
    protected PadModuleSwitching mPadModuleSwitching;
    protected PadSwitching mPadModuleSwitchingMode;

    protected ArrayList<PadSceneModule> mGuideModule;
    protected ArrayList<PadSceneModule> mHomeModule;
    protected ArrayList<PadSceneModule> mSubpageModule;
    protected ArrayList<PadSceneModule> mContentModule;
    protected ArrayList<PadSceneModule> mHelpModule;

    private AlarmManager mAlarmManager;
    private PendingIntent mModuleSwitchingPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            mPadDeviceInfo = getIntent().getParcelableExtra(KEY_PAD_DEVICE_INFO);
            mPadScene = getIntent().getParcelableExtra(KEY_PAD_SCENE);
            mPadSchool = getIntent().getParcelableExtra(KEY_PAD_SCHOOL);
            mPadAuthority = getIntent().getParcelableExtra(KEY_PAD_AUTHORITY);
            mPadSceneModule = getIntent().getParcelableExtra(KEY_PAD_MODULE_INFO);

            mGuideModule = getIntent().getParcelableArrayListExtra(KEY_PAD_MODULE_GUIDE);
            mHomeModule = getIntent().getParcelableArrayListExtra(KEY_PAD_MODULE_HOME);
            mSubpageModule = getIntent().getParcelableArrayListExtra(KEY_PAD_MODULE_SUBPAGE);
            mContentModule = getIntent().getParcelableArrayListExtra(KEY_PAD_MODULE_CONTENT);
            mHelpModule = getIntent().getParcelableArrayListExtra(KEY_PAD_MODULE_HELP);

            mPadModule = pickFirst(mPadSceneModule.getModule_id());
            mPadModuleType = pickFirst(mPadSceneModule.getModule_type_id());
        }

        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        registerEventBus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getModuleSwitching();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelAlarmService(mModuleSwitchingPendingIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterEventBus();
    }

    public void onEvent(ServiceMode mode) {
        if (mode == ServiceMode.MODE_SWITCH_SCENE) {
            finish();
        } else if (mode == ServiceMode.MODE_START_HOME_MODULE) {
            if (isTopActivity()) {
                if (!isEmpty(mHomeModule)) {
                    launchModule(mHomeModule.get(0));
                    finish();
                }
            }
        } else if (mode == ServiceMode.MODE_START_SUBPAGE) {
            if (isTopActivity()) {
                if (!isEmpty(mSubpageModule) && mSubpageModule.size() > mode.getIndex()) {
                    launchModule(mSubpageModule.get(mode.getIndex()));
                }
            }
        } else if (mode == ServiceMode.MODE_RESET_SERVICE) {
            if (isTopActivity()) {
                cancelAlarmService(mModuleSwitchingPendingIntent);
                parseModuleSwitchingService();
            }
        } else if (mode == ServiceMode.MODE_START_GUIDE_MODULE) {
            if (isTopActivity()) {
                if (!isEmpty(mGuideModule)) {
                    launchModule(mGuideModule.get(mode.getIndex()));
                    finish();
                }
            }
        } else if (mode == ServiceMode.MODE_FINISH_HOME_MODULE) {
            int type = parseInt(mPadModuleType.getType());
            if (type == PadModuleType.MODULE_TYPE_HOME) {
                finish();
            }
        } else if (mode == ServiceMode.MODE_FINISH_SUBPAGE_MODULE) {
            int type = parseInt(mPadModuleType.getType());
            if (type == PadModuleType.MODULE_TYPE_SUBPAGE) {
                finish();
            }
        } else if (mode == ServiceMode.MODE_FINISH_CURRENT_MODULE) {
            if (isTopActivity()) {
                finish();
            }
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
        log("启动模块Alarm服务");
        long triggerAtTime = SystemClock.elapsedRealtime();

        triggerAtTime += delay * 1000;
        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME, triggerAtTime, pendingIntent);
    }

    public void cancelAlarmService(PendingIntent pendingIntent) {
        log("取消模块Alarm服务");
        if (pendingIntent == null) return;
        mAlarmManager.cancel(pendingIntent);
    }

    public static class ModuleSwitchingService extends BaseService {

        PadDeviceInfo mPadDeviceInfo;
        PadModuleType mNextModuleType, mCurrentModuleType;

        @Override
        public void onCreate() {
            super.onCreate();
            log("模块切换服务启动");
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            log("模块切换服务关闭");
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            log("启动模块切换服务");
            mPadDeviceInfo = intent.getParcelableExtra(KEY_PAD_DEVICE_INFO);
            mNextModuleType = intent.getParcelableExtra(KEY_PAD_NEXT_MODULE_TYPE);
            mCurrentModuleType = intent.getParcelableExtra(KEY_PAD_CURRENT_MODULE_TYPE);
            switchModule();
            return START_STICKY;
        }

        /**
         * 模块场景
         *
         */
        private void switchModule() {
            if (mNextModuleType == null || mPadDeviceInfo == null) return;
            //发送场景切换广播，关闭所有已经打开的模块
            EventBus.getDefault().post(ServiceMode.MODE_SWITCH_MODULE);
            log("加载切换后的模块:" + mNextModuleType.getName());
            //发送广播请求切换场景
            int nextType = parseInt(mNextModuleType.getType());
            int currentType = parseInt(mCurrentModuleType.getType());
            if (nextType == PadModuleType.MODULE_TYPE_GUIDE) {
                if (currentType == PadModuleType.MODULE_TYPE_HOME) {
                    EventBus.getDefault().post(ServiceMode.MODE_START_GUIDE_MODULE);
                } else {
                    EventBus.getDefault().post(ServiceMode.MODE_START_GUIDE_MODULE);
                    EventBus.getDefault().post(ServiceMode.MODE_FINISH_HOME_MODULE);
                }
            } else if (nextType == PadModuleType.MODULE_TYPE_HOME) {
                if (currentType == PadModuleType.MODULE_TYPE_GUIDE) {
                    EventBus.getDefault().post(ServiceMode.MODE_START_HOME_MODULE);
                } else {
                    EventBus.getDefault().post(ServiceMode.MODE_FINISH_SUBPAGE_MODULE);
                }
            }
            //关闭服务
            stopSelf();
        }
    }

    /**
     * [SP102]查询当前模块是否存在切换服务
     */
    private void getModuleSwitching() {
        log("[SP102]查询当前模块是否存在切换服务");
        String modSwitchingUrl = UrlHelper.getPadModuleSwitching(mPadDeviceInfo, mPadModuleType);
        send(modSwitchingUrl, new ModuleSwitchingListener());
    }

    private class ModuleSwitchingListener extends SimpleRequestListener<PadModuleSwitching> {

        @Override
        public ResponseData<PadModuleSwitching> parseJson(String json) {
            return JsonParseHelper.parsePadModuleSwitchingResponse(json);
        }

        @Override
        public void handleRespone(List<PadModuleSwitching> content) {
            mPadModuleSwitching = content.get(0);
            checkModuleSwitching();
        }

        @Override
        public void handleResponseFailed() {

        }
    }

    /**
     * [SP103]是否存在下一个场景
     */
    private void checkModuleSwitching() {
        log("[SP103]是否存在下一个模块");
        if (mPadModuleSwitching.getNext_module() == null ||
                mPadModuleSwitching.getNext_module().size() <= 0) return;
        mNextModuleType = mPadModuleSwitching.getNext_module().get(0);
        if (mNextModuleType == null) return;
        checkModuleDuplicated();
    }

    /**
     * [SP104]下一个场景是否为当前场景
     */
    private void checkModuleDuplicated() {
        log("[SP104]下一个模块是否为当前模块");
        if (mNextModuleType.equals(mPadModuleType)) return;
        parseModuleSwitchingService();
    }

    /**
     * [SP105]启动场景切换服务
     */
    private void parseModuleSwitchingService() {
        log("[SP105]启动模块切换服务");
        if (mPadModuleSwitching == null ||
                mPadModuleSwitching.getSwitching_mode() == null ||
                mPadModuleSwitching.getSwitching_mode().size() <= 0) return;
        mPadModuleSwitchingMode = mPadModuleSwitching.getSwitching_mode().get(0);
        String mode = mPadModuleSwitchingMode.getTigger_mode();
        if (mode.equals(PadSwitching.TRIGGER_MODE_FIX_DAY)) {
            log("固定日期时触发模块切换（不支持）");
        } else if (mode.equals(PadSwitching.TRIGGER_MODE_FIX_TIME)) {
            log("固定时间时触发模块切换（不支持）");
        } else if (mode.equals(PadSwitching.TRIGGER_MODE_FIX_DELAY)) {
            log("固定延迟后触发模块切换");
            int delay = parseInt(mPadModuleSwitchingMode.getTrigger_interval());
            switchingModule(delay);
        }
    }

    /**
     * 通过计时器转换模块
     *
     * @param delay
     */
    private void switchingModule(int delay) {
        log("启动模块定时器，延迟：" + delay + "s");
        Bundle arg = new Bundle();
        arg.putParcelable(KEY_PAD_DEVICE_INFO, mPadDeviceInfo);
        arg.putParcelable(KEY_PAD_NEXT_MODULE_TYPE, mNextModuleType);
        arg.putParcelable(KEY_PAD_CURRENT_MODULE_TYPE, mPadModuleType);
        mModuleSwitchingPendingIntent = initAlarmService(ACTION_MODULE_SWITCHING_SERVICE, arg);
        startAlarmService(mModuleSwitchingPendingIntent, delay);
    }

    /**
     * 启动模块
     *
     * @param module
     */
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

        startActivity(intent);
    }
}
