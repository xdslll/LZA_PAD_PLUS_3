package com.lza.pad.app2.ui.widget.base;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.lza.pad.app2.service.ServiceMode;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.db.model.pad.PadEvent;
import com.lza.pad.db.model.pad.PadModule;
import com.lza.pad.db.model.pad.PadModuleType;
import com.lza.pad.db.model.pad.PadModuleWidget;
import com.lza.pad.db.model.pad.PadResource;
import com.lza.pad.db.model.pad.PadScene;
import com.lza.pad.db.model.pad.PadSceneModule;
import com.lza.pad.db.model.pad.PadSchool;
import com.lza.pad.db.model.pad.PadWidget;
import com.lza.pad.db.model.pad.PadWidgetData;
import com.lza.pad.db.model.pad.PadWidgetLayout;
import com.lza.pad.helper.RequestHelper;
import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.utils.Consts;
import com.lza.pad.support.utils.UniversalUtility;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/3/17.
 */
public class BaseFragment extends Fragment implements Consts {

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

    protected Activity mActivity;

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
        return UniversalUtility.safeIntParse(value, 0);
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
}
