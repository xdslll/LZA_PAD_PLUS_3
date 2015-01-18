package com.lza.pad.fragment.base;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.lza.pad.support.utils.Consts;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/13/15.
 */
public class BaseFragment extends Fragment implements Consts {

    protected Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }
}
