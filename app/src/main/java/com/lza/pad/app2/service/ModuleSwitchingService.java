package com.lza.pad.app2.service;

import android.content.Intent;

import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.db.model.pad.PadModuleSwitching;
import com.lza.pad.db.model.pad.PadModuleType;
import com.lza.pad.db.model.pad.PadSwitching;
import com.lza.pad.helper.JsonParseHelper;
import com.lza.pad.helper.SimpleRequestListener;
import com.lza.pad.helper.UrlHelper;

import java.util.List;
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
public class ModuleSwitchingService extends BaseIntentService {

    PadDeviceInfo mPadDeviceInfo;
    PadModuleType mPadModule;

    PadModuleType mNextModule;

    PadModuleSwitching mPadModuleSwitching;

    PadSwitching mPadSwitchingMode;

    public ModuleSwitchingService() {
        super("ModuleSwitchingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) return;
        /**
         * [SP101]接收当前模块
         */
        log("[SP101]接收当前模块");
        mPadModule = intent.getParcelableExtra(KEY_PAD_MODULE_INFO);
        mPadDeviceInfo = intent.getParcelableExtra(KEY_PAD_DEVICE_INFO);
        if (mPadModule == null || mPadDeviceInfo == null) return;
        getModuleSwitching();
    }

    /**
     * [SP102]查询当前模块是否存在切换服务
     */
    private void getModuleSwitching() {
        log("[SP102]查询当前模块是否存在切换服务");
        String modSwitchingUrl = UrlHelper.getPadModuleSwitching(mPadDeviceInfo, mPadModule);
        send(modSwitchingUrl, new SceneSwitchingListener());
    }

    /**
     * [SP103]是否存在下一个场景
     */
    private void checkModuleSwitching() {
        log("[SP103]是否存在下一个模块");
        if (mPadModuleSwitching.getNext_module() == null ||
                mPadModuleSwitching.getNext_module().size() <= 0) return;
        mNextModule = mPadModuleSwitching.getNext_module().get(0);
        if (mNextModule == null) return;
        checkModuleDuplicated();
    }

    /**
     * [SP104]下一个场景是否为当前场景
     */
    private void checkModuleDuplicated() {
        log("[SP104]下一个模块是否为当前模块");
        if (mNextModule.equals(mPadModule)) return;
        startSwitchingService();
    }

    /**
     * [SP105]启动场景切换服务
     */
    private void startSwitchingService() {
        log("[SP105]启动模块切换服务");
        if (mPadModuleSwitching.getSwitching_mode() == null ||
                mPadModuleSwitching.getSwitching_mode().size() <= 0) return;
        mPadSwitchingMode = mPadModuleSwitching.getSwitching_mode().get(0);
        String mode = mPadSwitchingMode.getTigger_mode();
        if (mode.equals(PadSwitching.TRIGGER_MODE_FIX_DAY)) {
            log("固定日期时触发模块切换");
        } else if (mode.equals(PadSwitching.TRIGGER_MODE_FIX_TIME)) {
            log("固定时间时触发模块切换");
        } else if (mode.equals(PadSwitching.TRIGGER_MODE_FIX_DELAY)) {
            log("固定延迟后触发模块切换");
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
        EventBus.getDefault().register(this);
        mService = Executors.newSingleThreadScheduledExecutor();
        mService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                mHasElapse += FIXED_DELAY;
                if (mHasElapse >= delay) {
                    sendModuleSwitchingBroadcast();
                    mService.shutdownNow();
                    stopSelf();
                }
                log("当前延迟：" + mHasElapse);
            }
        }, 0, FIXED_DELAY, TimeUnit.SECONDS);
    }

    /**
     * 发送场景切换的请求
     */
    private void sendModuleSwitchingBroadcast() {
        Intent intent = new Intent();
        intent.setAction(ACTION_MODULE_SWITCHING_RECEIVER);
        intent.putExtra(KEY_PAD_MODULE_INFO, mNextModule);
        sendBroadcast(intent);
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(SwitchingServiceMode mode) {
        if (mode == SwitchingServiceMode.MODE_RESET_SERVICE) {
            mHasElapse = 0;
        } else if (mode == SwitchingServiceMode.MODE_STOP_MODULE_SERVICE) {
            if (mService != null) {
                mService.shutdownNow();
            }
        }
    }

    private class SceneSwitchingListener extends SimpleRequestListener<PadModuleSwitching> {

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

}
