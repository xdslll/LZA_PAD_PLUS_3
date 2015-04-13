package com.lza.pad.app2.service;

import android.content.Context;
import android.text.TextUtils;

import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.db.model.pad.PadScene;
import com.lza.pad.db.model.pad.PadSceneSwitching;
import com.lza.pad.db.model.pad.PadSwitching;
import com.lza.pad.helper.JsonParseHelper;
import com.lza.pad.helper.RequestHelper;
import com.lza.pad.helper.SimpleRequestListener;
import com.lza.pad.helper.UrlHelper;
import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.utils.Consts;
import com.lza.pad.support.utils.Utility;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/3/19.
 */
@Deprecated
public class SceneSwitchingHandler implements Consts {

    PadDeviceInfo mPadDeviceInfo;
    PadScene mPadScene;

    PadScene mNextScene;

    PadSceneSwitching mPadSceneSwitching;

    PadSwitching mPadSwitchingMode;

    Context mCtx;

    boolean mIsServiceStarted = false;

    OnSceneSwitching mOnSceneSwitching;

    public SceneSwitchingHandler(Context c, PadDeviceInfo padDeviceInfo, PadScene padScene, OnSceneSwitching onSceneSwitching) {
        this.mPadDeviceInfo = padDeviceInfo;
        this.mPadScene = padScene;
        this.mCtx = c;
        this.mOnSceneSwitching = onSceneSwitching;

        /**
         * [SP101]接收当前场景
         */
        log("[SP101]接收当前场景");
        if (mPadScene == null || mPadDeviceInfo == null) return;

        register();
    }

    public void startService() {
        if (!mIsServiceStarted) {
            stopService();
            mService = Executors.newSingleThreadScheduledExecutor();
            getSceneSwitching();
        }
    }

    public void stopService() {
        if (mService != null && !mService.isShutdown()) {
            mService.shutdownNow();
        }
        mIsServiceStarted = false;
    }

    /**
     * [SP102]查询当前场景是否存在切换服务
     */
    private void getSceneSwitching() {
        log("[SP102]查询当前场景是否存在切换服务");
        String sceneSwitchingUrl = UrlHelper.getPadSceneSwitching(mPadDeviceInfo, mPadScene);
        send(sceneSwitchingUrl, new SceneSwitchingListener());
    }

    /**
     * [SP103]是否存在下一个场景
     */
    private void checkSceneSwitching() {
        log("[SP103]是否存在下一个场景");
        if (mPadSceneSwitching.getNext_scene() == null ||
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
        mService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                log("当前延迟(计算前)：" + mHasElapse);
                mHasElapse += FIXED_DELAY;
                log("当前延迟(计算后)：" + mHasElapse);
                if (mHasElapse >= delay) {
                    sceneSwitching();
                    stopService();
                }
            }
        }, FIXED_DELAY, FIXED_DELAY, TimeUnit.SECONDS);
    }

    /**
     * 发送场景切换的请求
     */
    private void sceneSwitching() {
        if (mOnSceneSwitching != null) {
            mOnSceneSwitching.onSwitching(mNextScene);
        }
    }

    public interface OnSceneSwitching {
        void onSwitching(PadScene nextScene);
    }

    public void reset() {
        mHasElapse = 0;
    }

    public void init(PadScene scene) {
        mPadScene = scene;
        mNextScene = null;
        mPadSwitchingMode = null;
        mPadSceneSwitching = null;
        mHasElapse = 0;
    }

    public void register() {
        EventBus.getDefault().register(this);
    }

    public void release() {
        stopService();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(ServiceMode mode) {
        if (mode == ServiceMode.MODE_RESET_SERVICE) {
            reset();
        }
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

    protected void send(String url, RequestHelper.OnRequestListener listener) {
        RequestHelper.getInstance(mCtx, url, listener).send();
    }

    protected String wrap(String value, String defaultValue) {
        return TextUtils.isEmpty(value) ? defaultValue : value;
    }

    protected int parseInt(String value) {
        return Utility.safeIntParse(value, 0);
    }

    protected boolean isEmpty(String str) {
        return TextUtils.isEmpty(str);
    }

    protected void log(String msg) {
        AppLogger.e("---------------- " + msg + " ----------------");
    }
}
