package com.lza.pad.app2.ui.module.content.ebook;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lza.pad.R;
import com.lza.pad.app2.ui.widget.base.BaseImageFragment;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/3/19.
 */
public class EbookContentFragment extends BaseImageFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.common_module_container, container, false);
        view.setBackgroundColor(Color.RED);
        return view;
    }
}
