package com.lza.pad.app2.service;

import android.content.Intent;

import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.db.model.pad.PadScene;
import com.lza.pad.db.model.pad.PadSceneSwitching;
import com.lza.pad.db.model.pad.PadSwitching;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/12/15.
 */
@Deprecated
public class SceneSwitchingService extends BaseIntentService {

    PadDeviceInfo mPadDeviceInfo;
    PadScene mPadScene;

    PadScene mNextScene;

    PadSceneSwitching mPadSceneSwitching;

    PadSwitching mPadSwitchingMode;

    public SceneSwitchingService() {
        super("SceneSwitchingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) return;
        /**
         * [SP101]接收当前场景
         */
        log("[SP101]接收当前场景");
        mPadScene = intent.getParcelableExtra(KEY_PAD_SCENE);
        mPadDeviceInfo = intent.getParcelableExtra(KEY_PAD_DEVICE_INFO);
        mPadSceneSwitching = intent.getParcelableExtra(KEY_PAD_SCENE_SWITCHING);
        if (mPadScene == null || mPadDeviceInfo == null) return;
        checkSceneSwitching();
    }

    /**
     * [SP103]是否存在下一个场景
     */
    private void checkSceneSwitching() {
        log("[SP103]是否存在下一个场景");
        if (mPadSceneSwitching == null ||
                mPadSceneSwitching.getNext_scene() == null ||
                mPadSceneSwitching.getNext_scene().size() <= 0) return;
        mNextScene = mPadSceneSwitching.getNext_scene().get(0);
        if (mNextScene == null) return;
        checkSceneDuplicated();
    }

    /**
     * [SP104]下一个场景是否为当前场景
     */
    private void checkSceneDuplicated() {
        log("[SP104]下一个场景是否为当前场景");
        if (mNextScene.equals(mPadScene)) return;
        startSwitchingService();
    }

    /**
     * [SP105]启动场景切换服务
     */
    private void startSwitchingService() {
        log("[SP105]启动场景切换服务");
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

    /**
     * 通过计时器转换场景
     *
     * @param delay
     */
    private void switchingScene(final int delay) {
        log("启动定时器，延迟：" + delay + "s");
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        mService = Executors.newSingleThreadScheduledExecutor();
        mService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                log("当前延迟（计算前）：" + mHasElapse);
                mHasElapse += FIXED_DELAY;
                log("当前延迟（计算后）：" + mHasElapse);
                if (mHasElapse >= delay) {
                    sendSceneSwitchingBroadcast();
                    log("关闭场景切换服务");
                    mService.shutdownNow();
                }
            }
        }, FIXED_DELAY, FIXED_DELAY, TimeUnit.SECONDS);
    }

    /**
     * 发送场景切换的请求
     */
    private void sendSceneSwitchingBroadcast() {
        log("发送场景切换广播");
        Intent intent = new Intent();
        intent.setAction(ACTION_SCENE_SWITCHING_RECEIVER);
        intent.putExtra(KEY_PAD_SCENE, mNextScene);
        sendBroadcast(intent);
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public void onEvent(ServiceMode mode) {
        if (mode == ServiceMode.MODE_RESET_SERVICE) {
            mHasElapse = 0;
        } else if (mode == ServiceMode.MODE_STOP_SCENE_SERVICE) {
            if (mService != null) {
                mService.shutdownNow();
            }
        }
    }

}
