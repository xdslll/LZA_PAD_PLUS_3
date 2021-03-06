package com.lza.pad.fragment.ebook;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.app.ebook._EbookActivity;
import com.lza.pad.app.ebook._EbookContentActivity;
import com.lza.pad.fragment.base._BaseFragment;
import com.lza.pad.widget.DefaultEbookCover;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/7/15.
 */
@Deprecated
public class _EbookFragment5 extends _BaseFragment {

    private TextView mTxtMore;
    private LinearLayout mLayoutTitle, mLayoutPages;
    private DefaultEbookCover mEbookCover1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout._ebook_list2, container, false);

        mTxtMore = (TextView) view.findViewById(R.id.ebook_list2_more);
        mTxtMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, _EbookActivity.class));
            }
        });

        if (!mIsHome) {
            mLayoutTitle = (LinearLayout) view.findViewById(R.id.ebook_list2_title);
            mLayoutPages = (LinearLayout) view.findViewById(R.id.common_container_pages);
            mLayoutTitle.setVisibility(View.INVISIBLE);
            mLayoutPages.setVisibility(View.GONE);
        }

        mEbookCover1 = (DefaultEbookCover) view.findViewById(R.id.ebook_list2_book1);
        mEbookCover1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, _EbookContentActivity.class));
            }
        });

        return view;
    }
}
