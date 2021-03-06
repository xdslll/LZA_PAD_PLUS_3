package com.lza.pad.fragment.base;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;

import com.lza.pad.support.utils.Consts;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/13/15.
 */
public class BaseDialogFragment extends DialogFragment implements Consts {

    protected Activity mActivity;
    protected boolean mIfHome = true;
    protected Bundle mArg;
    protected int W, H;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mArg = getArguments();
        if (mArg != null) {
            mIfHome = getArguments().getBoolean(KEY_IS_HOME);
            W = getArguments().getInt(KEY_FRAGMENT_WIDTH);
            H = getArguments().getInt(KEY_FRAGMENT_HEIGHT);
        }
    }
}
