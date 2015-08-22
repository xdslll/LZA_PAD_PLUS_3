package com.lza.pad.app2.ui.widget;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.support.utils.Consts;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/21/15.
 */
public class SubjectFragment extends DialogFragment implements Consts {

    private GridView mGrid;
    private String[] mData;
    private int mCurrentSubject;
    private LayoutInflater mInflater;
    private int mMinWidth, mMinHeight;

    /**
     * 每行3个学科
     */
    private static final int NUM_LINES = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mData = getArguments().getStringArray(KEY_SUBJECT_DATA);
            mCurrentSubject = getArguments().getInt(KEY_CURRENT_SUBJECT);
        }
        mInflater = LayoutInflater.from(getActivity());

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mMinWidth = metrics.widthPixels;

        int textSize = getResources().getDimensionPixelSize(R.dimen.subject_text_size);
        int padding = getResources().getDimensionPixelSize(R.dimen.subject_text_padding);
        if (mData != null) {
            int line = mData.length % NUM_LINES == 0 ? mData.length / NUM_LINES : mData.length / NUM_LINES + 1;
            mMinHeight = textSize * line + padding * 2 * (line + 2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.subject_grid, container, false);
        mGrid = (GridView) view.findViewById(R.id.subject_grid);
        mGrid.setNumColumns(NUM_LINES);
        mGrid.setBackgroundColor(Color.WHITE);
        mGrid.setLayoutParams(new FrameLayout.LayoutParams(mMinWidth, ViewGroup.LayoutParams.MATCH_PARENT));
        mGrid.setAdapter(new SubjectAdapter());
        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mOnSubjectClick != null) {
                    mOnSubjectClick.onSubjectClick(position);
                    SubjectFragment.this.dismiss();
                }
            }
        });
        return view;
    }

    public void setOnSubjectClick(OnSubjectClick onSubjectClick) {
        this.mOnSubjectClick = onSubjectClick;
    }

    OnSubjectClick mOnSubjectClick = null;

    public interface OnSubjectClick {
        void onSubjectClick(int position);
    }

    private class SubjectAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mData.length;
        }

        @Override
        public String getItem(int position) {
            return mData[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.subject, null);
                holder.textView = (TextView) convertView.findViewById(R.id.subject_text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textView.setText(getItem(position));
            if (position == mCurrentSubject) {
                Drawable drawable = getResources().getDrawable(R.drawable.subject_bg);
                holder.textView.setBackgroundDrawable(drawable);
                holder.textView.setTextColor(Color.RED);
                holder.textView.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                holder.textView.setBackgroundDrawable(null);
                holder.textView.setTextColor(getResources().getColor(R.color.common_blue));
                holder.textView.setTypeface(Typeface.DEFAULT);
            }
            return convertView;
        }
    }

    private static class ViewHolder {
        TextView textView;
    }
}
