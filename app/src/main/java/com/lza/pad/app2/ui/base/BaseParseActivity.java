package com.lza.pad.app2.ui.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.lza.pad.app2.service.SwitchingServiceMode;
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

    private SceneSwitchingReceiver mSwitchingReceiver = new SceneSwitchingReceiver();

    protected List<PadSceneModule> mPadSceneModules = new ArrayList<PadSceneModule>();

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
        registerSceneSwitchingReceiver();
        startSceneSwitchingService();
        getSceneModules();

        getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                log("触摸时重置场景切换服务");
                EventBus.getDefault().post(SwitchingServiceMode.MODE_RESET_SERVICE);
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterSceneSwitchingReceiver();
        stopSceneSwitchingService();
    }

    /**
     * [P301]启动场景切换服务
     */
    protected void startSceneSwitchingService() {
        log("[P301]启动场景切换服务");
        Intent intent = new Intent();
        intent.setAction(ACTION_SCENE_SWITCHING_SERVICE);
        intent.putExtra(KEY_PAD_DEVICE_INFO, mPadDeviceInfo);
        intent.putExtra(KEY_PAD_SCENE, mPadScene);
        startService(intent);
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

    protected void registerSceneSwitchingReceiver() {
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

    protected void stopSceneSwitchingService() {
        EventBus.getDefault().post(SwitchingServiceMode.MODE_STOP_SCENE_SERVICE);
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
