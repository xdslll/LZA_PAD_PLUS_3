package com.lza.pad.fragment.news;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lza.pad.fragment.base.BaseFragment;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 2/23/15.
 */
public class NewsListFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TextView view = new TextView(mActivity);
        view.setText("Hello World!");
        return view;
    }
}
