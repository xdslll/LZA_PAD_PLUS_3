package com.lza.pad.app2.ui.widget.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.app.socket.model.MinaClient;
import com.lza.pad.app2.service.ServiceMode;
import com.lza.pad.db.model.DownloadFile;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.db.model.pad.PadEvent;
import com.lza.pad.db.model.pad.PadModule;
import com.lza.pad.db.model.pad.PadModuleType;
import com.lza.pad.db.model.pad.PadModuleWidget;
import com.lza.pad.db.model.pad.PadResource;
import com.lza.pad.db.model.pad.PadScene;
import com.lza.pad.db.model.pad.PadSceneModule;
import com.lza.pad.db.model.pad.PadSchool;
import com.lza.pad.db.model.pad.PadSwitching;
import com.lza.pad.db.model.pad.PadWidget;
import com.lza.pad.db.model.pad.PadWidgetData;
import com.lza.pad.db.model.pad.PadWidgetLayout;
import com.lza.pad.helper.RequestHelper;
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
 * @Date 15/3/17.
 */
public abstract class BaseFragment extends Fragment implements Consts {

    protected PadDeviceInfo mPadDeviceInfo;
    protected PadScene mPadScene;
    protected PadSchool mPadSchool;

    protected PadSceneModule mPadSceneModule;
    protected PadModule mPadModule;
    protected PadModuleType mPadModuleType;
    protected List<PadSceneModule> mPadSubpageModule;

    protected PadModuleWidget mPadModuleWidget;
    protected PadWidget mPadWidget;
    protected PadWidgetLayout mPadWidgetLayout;
    protected PadWidgetData mPadWidgetData;
    protected PadEvent mPadEvent;

    protected PadResource mPadResource;

    private PadSwitching mPadWidgetSwitching;
    private PadSwitching mPadWidgetUpdate;
    private int mSwitchingDelay;
    private int mUpdateDelay;
    private ScheduledExecutorService mWidgetSwitchingService;
    private ScheduledExecutorService mWidgetUpdateService;

    protected Activity mActivity;

    protected final static int RETRY_DELAY = 5 * 1000;

    protected ViewStub mViewStubLoading;
    protected LinearLayout mLayoutLoading;
    protected TextView mTxtLoadingText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        if (getArguments() != null) {
            mPadModuleWidget = getArguments().getParcelable(KEY_PAD_WIDGET);
            mPadDeviceInfo = getArguments().getParcelable(KEY_PAD_DEVICE_INFO);
            mPadScene = getArguments().getParcelable(KEY_PAD_SCENE);
            mPadSchool = getArguments().getParcelable(KEY_PAD_SCHOOL);
            mPadSceneModule = getArguments().getParcelable(KEY_PAD_MODULE_INFO);
            mPadSubpageModule = getArguments().getParcelableArrayList(KEY_PAD_MODULE_SUBPAGE);
            mPadResource = getArguments().getParcelable(KEY_PAD_RESOURCE_INFO);
        }
        if (mPadSceneModule != null) {
            mPadModule = pickFirst(mPadSceneModule.getModule_id());
            mPadModuleType = pickFirst(mPadSceneModule.getModule_type_id());
        }
        if (mPadModuleWidget != null) {
            mPadWidget = pickFirst(mPadModuleWidget.getWidget_id());
            mPadWidgetLayout = pickFirst(mPadModuleWidget.getWidget_layout_id());
            mPadWidgetData = pickFirst(mPadModuleWidget.getWidget_data_id());
            mPadEvent = pickFirst(mPadModuleWidget.getEvent_id());
        }
    }

    protected void resetSwitching() {
        log("重置切换服务");
        EventBus.getDefault().post(ServiceMode.MODE_RESET_SERVICE);
    }

    private Handler mHandler = new Handler();

    protected void send(String url, RequestHelper.OnRequestListener listener) {
        RequestHelper.getInstance(mActivity, url, listener).send();
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

    protected <T> boolean isEmpty(List<T> data) {
        return data == null || data.size() <= 0;
    }

    protected <T> T pickFirst(List<T> data) {
        if (isEmpty(data)) return null;
        return data.get(0);
    }

    protected void log(String msg) {
        AppLogger.e("---------------- " + msg + " ----------------");
    }

    protected void registerEventBus() {
        EventBus.getDefault().register(this);
    }

    protected void unregisterEventBus() {
        EventBus.getDefault().unregister(this);
    }

    protected Handler getMainHandler() {
        return mHandler;
    }

    protected String buildCodePath(String activityPath) {
        String packageName = mActivity.getPackageName();
        StringBuffer buffer = new StringBuffer();
        buffer.append(packageName).append(".").append(activityPath);
        return buffer.toString();
    }

    /**
     * 发出启动首页模块的广播
     */
    protected void launchHomeModule() {
        EventBus.getDefault().post(ServiceMode.MODE_START_HOME_MODULE);
    }

    protected void launchSubpageModule(int index) {
        ServiceMode mode = ServiceMode.MODE_START_SUBPAGE;
        mode.setIndex(index);
        EventBus.getDefault().post(mode);
    }

    /**
     * 启动组件切换服务
     */
    protected void startWidgetSwitchingService() {
        log("启动组件[" + mPadWidget.getDescription() + "]切换服务");
        mPadWidgetSwitching = pickFirst(mPadModuleWidget.getSwitching_id());
        if (mPadWidgetSwitching == null) {
            log("组件[" + mPadWidget.getDescription() + "]不支持切换服务");
            return;
        }
        if (mPadWidgetSwitching.getTigger_mode().equals(PadSwitching.TRIGGER_MODE_FIX_DELAY)) {
            mSwitchingDelay = parseInt(mPadWidgetSwitching.getTrigger_interval());
            log("组件[" + mPadWidget.getDescription() + "]切换延迟：" + mSwitchingDelay);
            mWidgetSwitchingService = Executors.newSingleThreadScheduledExecutor();
            mWidgetSwitchingService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    onWidgetSwitching();
                }
            }, mSwitchingDelay, mSwitchingDelay, TimeUnit.SECONDS);
        }
    }

    /**
     * 关闭组件切换服务
     */
    protected void stopWidgetSwitchingService() {
        log("关闭组件[" + mPadWidget.getDescription() + "]切换服务");
        if (isWidgetSwitchingServiceRunning()) {
            mWidgetSwitchingService.shutdownNow();
        }
    }

    protected boolean isWidgetSwitchingServiceRunning() {
        return mWidgetSwitchingService != null && !mWidgetSwitchingService.isShutdown();
    }

    /**
     * 启动组件更新服务
     */
    protected void startWidgetUpdateService() {
        log("启动组件[" + mPadWidget.getDescription() + "]更新服务");
        mPadWidgetUpdate = pickFirst(mPadModuleWidget.getUpdate_mode());
        if (mPadWidgetUpdate == null) {
            log("组件[" + mPadWidget.getDescription() + "]不支持更新服务");
            return;
        }
        if (mPadWidgetUpdate.getTigger_mode().equals(PadSwitching.TRIGGER_MODE_FIX_DELAY)) {
            mUpdateDelay = parseInt(mPadWidgetUpdate.getTrigger_interval());
            log("组件[" + mPadWidget.getDescription() + "]更新延迟：" + mUpdateDelay);
            mWidgetUpdateService = Executors.newSingleThreadScheduledExecutor();
            mWidgetUpdateService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    onWidgetUpdate();
                }
            }, mUpdateDelay, mUpdateDelay, TimeUnit.SECONDS);
        }
    }

    /**
     * 关闭组件更新服务
     */
    protected void stopWidgetUpdateService() {
        log("关闭组件[" + mPadWidget.getDescription() + "]更新服务");
        if (isWidgetUpdateServiceRunning()) {
            mWidgetUpdateService.shutdownNow();
        }
    }

    protected boolean isWidgetUpdateServiceRunning() {
        return mWidgetUpdateService != null && !mWidgetUpdateService.isShutdown();
    }

    protected void onWidgetSwitching() {}

    protected void onWidgetUpdate() {}

    protected int getWidgetWidth() {
        return mPadWidgetLayout.getWidget_width();
    }

    protected int getWidgetHeight() {
        return mPadWidgetLayout.getWidget_height();
    }

    /**
     * 访问内容页
     *
     * @param resource
     */
    protected void startContentPage(PadResource resource) {
        if (resource == null || mPadEvent == null
                || isEmpty(mPadEvent.getEvent_code_path())) return;
        String activityPath = buildCodePath(mPadEvent.getEvent_code_path());
        Intent intent = new Intent();
        intent.setClassName(mActivity, activityPath);
        intent.putExtra(KEY_PAD_DEVICE_INFO, mPadDeviceInfo);
        intent.putExtra(KEY_PAD_WIDGET, mPadModuleWidget);
        intent.putExtra(KEY_PAD_RESOURCE_INFO, resource);
        startActivity(intent);
    }

    /**
     * 显示Loading进度条
     *
     * @param view
     */
    protected void showLoadingView(View view) {
        if (mLayoutLoading == null) {
            if (view != null) {
                mViewStubLoading = (ViewStub) view.findViewById(R.id.common_viewstub);
                mViewStubLoading.inflate();
                mLayoutLoading = (LinearLayout) view.findViewById(R.id.common_loading_layout);
                mTxtLoadingText = (TextView) view.findViewById(R.id.common_loading_layout_text);
                mLayoutLoading.setVisibility(View.VISIBLE);
            }
        } else {
            mLayoutLoading.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏Loading进度条
     */
    protected void dismissLoadingView() {
        if (mLayoutLoading != null) {
            mLayoutLoading.setVisibility(View.GONE);
        }
    }

    /**
     * 设置Loading文本
     *
     * @param text
     */
    protected void setLoadingViewText(String text) {
        if (mTxtLoadingText != null) {
            mTxtLoadingText.setText(text);
        }
    }

    /**
     * 在消息线程处理Socket事件
     *
     * @param client
     */
    public void onEvent(MinaClient client) {

    }

    /**
     * 在主线程中处理Socket事件
     *
     * @param client
     */
    public void onEventMainThread(MinaClient client) {

    }

    /**
     * 异步处理Socket事件
     *
     * @param client
     */
    public void onEventAsync(MinaClient client) {

    }

    /**
     * 处理下载完成后的事件
     *
     * @param downloadFile
     */
    public void onEventAsync(DownloadFile downloadFile) {

    }
}
