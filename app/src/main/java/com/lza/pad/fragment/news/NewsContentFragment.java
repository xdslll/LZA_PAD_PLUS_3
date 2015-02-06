package com.lza.pad.fragment.news;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lza.pad.R;
import com.lza.pad.fragment.base.BaseFragment;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/21/15.
 */
public class NewsContentFragment extends BaseFragment {

    private LinearLayout mLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_content, container, false);
        //mLayout = (LinearLayout) view.findViewById(R.id.news_content);

        return view;
    }
}
