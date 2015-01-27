package com.lza.pad.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.app.EbookActivity;
import com.lza.pad.fragment.base.BaseFragment;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/7/15.
 */
@Deprecated
public class _NewbookListFragment extends BaseFragment {

    private TextView mTxtMore;
    private LinearLayout mLayoutTitle, mLayoutPages;
    private GridView mGrid;
    private NewbookListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.common_module_container, container, false);

        mTxtMore = (TextView) view.findViewById(R.id.ebook_list_more);
        mTxtMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, EbookActivity.class));
            }
        });

        if (!mIfHome) {
            mLayoutTitle = (LinearLayout) view.findViewById(R.id.ebook_list2_title);
            mLayoutPages = (LinearLayout) view.findViewById(R.id.ebook_list_pages);
            mLayoutTitle.setVisibility(View.INVISIBLE);
            mLayoutPages.setVisibility(View.GONE);
        }

        mGrid = (GridView) view.findViewById(R.id.common_grid);
        mGrid.setAdapter(new NewbookListAdapter(mActivity));
        return view;
    }

    private static class NewbookListAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        private NewbookListAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Object getItem(int position) {
            return "";
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = mInflater.inflate(R.layout.newbook_list_item, null);
            return view;
        }
    }
}
