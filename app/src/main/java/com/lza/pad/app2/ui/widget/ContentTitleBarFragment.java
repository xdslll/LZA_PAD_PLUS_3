package com.lza.pad.app2.ui.widget;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.app2.ui.widget.base.BaseImageFragment;
import com.lza.pad.support.utils.ToastUtils;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/31/15.
 */
public class ContentTitleBarFragment extends BaseImageFragment {

    TextView mTxtHome, mTxtModName, mTxtSubject, mTxtSearch, mTxtDivider;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.common_content_titlebar, container, false);
        mTxtHome = (TextView) view.findViewById(R.id.common_content_titlebar_back);
        mTxtModName = (TextView) view.findViewById(R.id.common_content_titlebar_mod_name);
        mTxtSubject = (TextView) view.findViewById(R.id.common_content_titlebar_subject);
        mTxtSearch = (TextView) view.findViewById(R.id.common_content_titlebar_search);
        mTxtDivider = (TextView) view.findViewById(R.id.common_content_titlebar_divider);

        mTxtHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });

        mTxtModName.setText(mPadModuleWidget.getLabel());

        mTxtSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showLong(mActivity, "学科过滤");
            }
        });

        mTxtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showLong(mActivity, "搜索功能");
            }
        });

        mTxtSearch.setVisibility(View.GONE);
        mTxtSubject.setVisibility(View.GONE);
        mTxtDivider.setVisibility(View.GONE);

        return view;
    }
}
