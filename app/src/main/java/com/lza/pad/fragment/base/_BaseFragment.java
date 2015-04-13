package com.lza.pad.fragment.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.lza.pad.app.socket.model.MinaClient;
import com.lza.pad.db.model.DownloadFile;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.db.model.pad._old.PadLayoutModule;
import com.lza.pad.db.model.pad._old.PadModuleControl;
import com.lza.pad.db.model.pad.PadResource;
import com.lza.pad.helper.RequestHelper;
import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.utils.Consts;
import com.lza.pad.support.utils.Utility;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/13/15.
 */
@Deprecated
public class _BaseFragment extends Fragment implements Consts {

    protected Activity mActivity;
    protected boolean mIsHome = true;
    protected Bundle mArg;
    protected int W, H;
    protected PadDeviceInfo mPadDeviceInfo;
    protected PadModuleControl mPadControlInfo;
    protected PadLayoutModule mPadModuleInfo;
    protected PadResource mPadResource;
    /**
     * 所有模块
     */
    protected ArrayList<PadLayoutModule> mPadModuleInfos;
    /**
     * 当前模块序号
     */
    protected int mCurrentModuleIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mArg = getArguments();
        if (mArg != null) {
            mIsHome = getArguments().getBoolean(KEY_IS_HOME);
            W = getArguments().getInt(KEY_FRAGMENT_WIDTH);
            H = getArguments().getInt(KEY_FRAGMENT_HEIGHT);
            mPadDeviceInfo = getArguments().getParcelable(KEY_PAD_DEVICE_INFO);
            mPadControlInfo = getArguments().getParcelable(KEY_PAD_CONTROL_INFO);
            mPadResource = getArguments().getParcelable(KEY_PAD_RESOURCE_INFO);
            mPadModuleInfos = getArguments().getParcelableArrayList(KEY_PAD_MODULE_INFOS);
            mCurrentModuleIndex = getArguments().getInt(KEY_CURRENT_MODULE_INDEX);
            mPadModuleInfo = getArguments().getParcelable(KEY_PAD_MODULE_INFO);
        }

        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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

    protected void log(String msg) {
        AppLogger.e("=============== " + msg + " ===============" );
    }

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

    /**
     * 根据模块信息，拼接出该模块所对应的Activity文件路径
     *
     * @param module
     * @return
     */
    protected String getModuleJavaFileName(PadLayoutModule module) {
        String moduleType = module.getModule_type();
        String moduleStyle = module.getModule_style();
        String moduleIndex = module.getModule_index();
        String packageName = mActivity.getPackageName();
        StringBuffer buffer = new StringBuffer();
        buffer.append(packageName).append(".").append("app.");
        //将包名的首字母变成小写
        if (!TextUtils.isEmpty(moduleType)) {
            moduleType = moduleType.toLowerCase();
            buffer.append(moduleType).append(".");
        }
        //将文件名首字母变成大写
        if (moduleStyle != null && moduleStyle.length() > 1) {
            buffer.append(moduleStyle.substring(0, 1).toUpperCase())
                    .append(moduleStyle.substring(1, moduleStyle.length()));
        } else if (moduleStyle != null && moduleStyle.length() == 1){
            buffer.append(moduleStyle.toUpperCase());
        }
        buffer.append("Activity");
        if (parseInt(moduleIndex) > 0) {
            buffer.append(moduleIndex);
        }
        log("activity:" + buffer.toString());
        return buffer.toString();
    }

    protected Bundle createArguments() {
        Bundle arg = new Bundle();
        arg.putParcelable(KEY_PAD_DEVICE_INFO, mPadDeviceInfo);
        arg.putParcelable(KEY_PAD_RESOURCE_INFO, mPadResource);
        arg.putParcelable(KEY_PAD_MODULE_INFO, mPadModuleInfo);
        arg.putParcelableArrayList(KEY_PAD_MODULE_INFOS, mPadModuleInfos);
        arg.putParcelable(KEY_PAD_CONTROL_INFO, mPadControlInfo);
        return arg;
    }
}
