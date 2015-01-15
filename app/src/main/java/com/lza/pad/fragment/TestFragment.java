package com.lza.pad.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lza.pad.app.ContentActivity;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/5/15.
 */
public class TestFragment extends Fragment {

    private TextView mTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String data = getArguments().getString("data");
        int color = getArguments().getInt("color");
        mTextView = new TextView(getActivity());
        mTextView.setText(data);
        mTextView.setTextSize(30);
        mTextView.setBackgroundResource(color);
        mTextView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ContentActivity.class));
            }
        });
        return mTextView;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

}
