package com.lza.pad.app2.ui.base;

import android.content.DialogInterface;
import android.os.Bundle;

import com.lza.pad.app2.service.SceneSwitchingHandler;
import com.lza.pad.app2.service.ServiceMode;
import com.lza.pad.db.model.pad.PadAuthority;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.db.model.pad.PadModuleType;
import com.lza.pad.db.model.pad.PadScene;
import com.lza.pad.db.model.pad.PadSceneModule;
import com.lza.pad.db.model.pad.PadSchool;
import com.lza.pad.support.utils.UniversalUtility;

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

    protected List<PadSceneModule> mPadSceneModules = new ArrayList<PadSceneModule>();

    protected SceneSwitchingHandler mSceneSwitchingHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            mPadDeviceInfo = getIntent().getParcelableExtra(KEY_PAD_DEVICE_INFO);
            mPadScene = getIntent().getParcelableExtra(KEY_PAD_SCENE);
            mPadSchool = getIntent().getParcelableExtra(KEY_PAD_SCHOOL);
            mPadAuthority = getIntent().getParcelableExtra(KEY_PAD_AUTHORITY);
        }
        showProgressDialog("开始解析场景", false);
        startSceneSwitchingService();
        getSceneModules();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSceneSwitchingHandler.release();
    }

    /**
     * [P301]启动场景切换服务
     */
    protected void startSceneSwitchingService() {
        log("[P301]启动场景切换服务");
        /*Intent intent = new Intent();
        intent.setAction(ACTION_SCENE_SWITCHING_SERVICE);
        intent.putExtra(KEY_PAD_DEVICE_INFO, mPadDeviceInfo);
        intent.putExtra(KEY_PAD_SCENE, mPadScene);
        startService(intent);*/
        mSceneSwitchingHandler = new SceneSwitchingHandler(mCtx,
                mPadDeviceInfo, mPadScene, new OnSceneSwitchingListener());
        mSceneSwitchingHandler.startService();
    }

    protected void restartSceneSwitchingService(PadScene scene) {
        mSceneSwitchingHandler.init(scene);
        mSceneSwitchingHandler.startService();
    }

    protected void stopSceneSwitchingService() {
        //EventBus.getDefault().post(SwitchingServiceMode.MODE_STOP_SCENE_SERVICE);
        log("关闭场景切换服务");
        mSceneSwitchingHandler.stopService();
    }

    public void onEvent(ServiceMode mode) {
        log("接收到服务：" + mode);
        if (mode == ServiceMode.MODE_RESET_SERVICE) {
            mSceneSwitchingHandler.reset();
        } else if (mode == ServiceMode.MODE_STOP_SCENE_SERVICE) {
            stopSceneSwitchingService();
        }
    }

    private class OnSceneSwitchingListener implements SceneSwitchingHandler.OnSceneSwitching {

        @Override
        public void onSwitching(final PadScene nextScene) {
            getMainHandler().post(new Runnable() {
                @Override
                public void run() {
                    switchScene(nextScene);
                }
            });
        }
    }

    /**
     * 切换场景
     *
     * @param nextScene
     */
    private void switchScene(PadScene nextScene) {
        dismissProgressDialog();
        stopSceneSwitchingService();
        EventBus.getDefault().post(ServiceMode.MODE_SWITCH_SCENE);
        mPadScene = nextScene;
        if (mPadScene == null) return;
        log("加载切换后的场景:" + mPadScene.getName());
        showProgressDialog("开始解析场景", false);

        getRunningActivities();

        resetSceneData();
        getSceneModules();
        restartSceneSwitchingService(mPadScene);
    }



    /**
     * [P302]获取场景下的所有模块
     */
    protected abstract void getSceneModules();

    /**
     * 启动模块
     */
    protected abstract void launchModule(PadSceneModule module);

    protected void launchModuleByType(PadModuleType moduleType) {
        int type = parseInt(moduleType.getType());
        launchModuleByType(type);
    }

    protected void launchModuleByType(int type) {}

    /**
     * 重置场景切换后的数据
     */
    protected abstract void resetSceneData();


    protected void handleErrorProcess(String title, String message) {
        dismissProgressDialog();
        UniversalUtility.showDialog(mCtx, title, message,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startSceneSwitchingService();
                        getSceneModules();
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        backToDeviceAuthorityActivity();
                    }
                });
    }
}
/*protected void registerSceneSwitchingReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SCENE_SWITCHING_RECEIVER);
        filter.addAction(ACTION_MODULE_SWITCHING_RECEIVER);
        registerReceiver(mSwitchingReceiver, filter);
    }

    protected void unregisterSceneSwitchingReceiver() {
        try {
            unregisterReceiver(mSwitchingReceiver);
        } catch (Exception ex) {

        }
    }



    private class SceneSwitchingReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) return;
            if (intent.getAction().equals(ACTION_SCENE_SWITCHING_RECEIVER)) {
                dismissProgressDialog();
                mPadScene = intent.getParcelableExtra(KEY_PAD_SCENE);
                if (mPadScene == null) return;
                log("加载切换后的场景:" + mPadScene.getName());
                showProgressDialog("开始解析场景", false);
                resetSceneData();
                getSceneModules();
                startSceneSwitchingService();
            } else if (intent.getAction().equals(ACTION_MODULE_SWITCHING_RECEIVER)) {
                PadModuleType moduleType = intent.getParcelableExtra(KEY_PAD_MODULE_INFO);
                if (moduleType != null) {
                    log("加载切换后的模块类型:" + moduleType.getType());
                    launchModuleByType(moduleType);
                }
            }
        }
    }

    private SceneSwitchingReceiver mSwitchingReceiver = new SceneSwitchingReceiver();
    */
