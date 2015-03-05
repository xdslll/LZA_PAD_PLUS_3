package com.lza.pad.fragment.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.lza.pad.R;
import com.lza.pad.fragment.base.BaseImageFragment;
import com.lza.pad.helper.UrlHelper;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/2/15.
 */
public class SearchFragment extends BaseImageFragment {

    EditText mEdtSearchText;
    Button mBtnDoSearch;
    FrameLayout mFrmContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_list, container, false);
        mEdtSearchText = (EditText) view.findViewById(R.id.search_list_text);
        mBtnDoSearch = (Button) view.findViewById(R.id.search_list_search);
        mFrmContainer = (FrameLayout) view.findViewById(R.id.search_list_container);

        mBtnDoSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = mEdtSearchText.getText().toString();
                String requestUrl = UrlHelper.getOpacSearchListUrl(keyword);
                send(requestUrl, null);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
