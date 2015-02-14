package com.lza.pad.fragment.base;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.lza.pad.app.socket.model.MinaClient;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.db.model.pad.PadModuleControl;
import com.lza.pad.db.model.pad.PadResource;
import com.lza.pad.helper.RequestHelper;
import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.utils.Consts;

import de.greenrobot.event.EventBus;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/13/15.
 */
public class BaseFragment extends Fragment implements Consts {

    protected Activity mActivity;
    protected boolean mIfHome = true;
    protected Bundle mArg;
    protected int W, H;
    protected PadDeviceInfo mPadDeviceInfo;
    protected PadModuleControl mPadControlInfo;
    protected PadResource mPadResourceInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mArg = getArguments();
        if (mArg != null) {
            mIfHome = getArguments().getBoolean(KEY_IF_HOME);
            W = getArguments().getInt(KEY_FRAGMENT_WIDTH);
            H = getArguments().getInt(KEY_FRAGMENT_HEIGHT);
            mPadDeviceInfo = getArguments().getParcelable(KEY_PAD_DEVICE_INFO);
            mPadControlInfo = getArguments().getParcelable(KEY_PAD_CONTROL_INFO);
            mPadResourceInfo = getArguments().getParcelable(KEY_PAD_RESOURCE_INFO);
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
}
