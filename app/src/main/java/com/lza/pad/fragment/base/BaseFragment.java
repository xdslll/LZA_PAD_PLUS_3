package com.lza.pad.fragment.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.lza.pad.app.socket.model.MinaClient;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.db.model.pad.PadLayoutModule;
import com.lza.pad.db.model.pad.PadModuleControl;
import com.lza.pad.db.model.pad.PadResource;
import com.lza.pad.helper.RequestHelper;
import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.utils.Consts;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/13/15.
 */
public class BaseFragment extends Fragment implements Consts {

    protected Activity mActivity;
    protected boolean mIsHome = true;
    protected Bundle mArg;
    protected int W, H;
    protected PadDeviceInfo mPadDeviceInfo;
    protected PadModuleControl mPadControlInfo;
    protected PadResource mPadResource;
    /**
     * 所有模块
     */
    protected List<PadLayoutModule> mPadModuleInfos;
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
            mIsHome = getArguments().getBoolean(KEY_IF_HOME);
            W = getArguments().getInt(KEY_FRAGMENT_WIDTH);
            H = getArguments().getInt(KEY_FRAGMENT_HEIGHT);
            mPadDeviceInfo = getArguments().getParcelable(KEY_PAD_DEVICE_INFO);
            mPadControlInfo = getArguments().getParcelable(KEY_PAD_CONTROL_INFO);
            mPadResource = getArguments().getParcelable(KEY_PAD_RESOURCE_INFO);
            mPadModuleInfos = getArguments().getParcelableArrayList(KEY_PAD_MODULE_INFOS);
            mCurrentModuleIndex = getArguments().getInt(KEY_CURRENT_MODULE_INDEX);
        }

        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(MinaClient client) {

    }

    public void onEventMainThread(MinaClient client) {

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
}
