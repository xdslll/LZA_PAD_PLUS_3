package com.lza.pad.app2.ui.widget;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lza.pad.R;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/13/15.
 */
public class BaseImageResourceFragment2 extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_scene_container, container, false);
        view.setBackgroundColor(Color.YELLOW);
        return view;
    }
}
