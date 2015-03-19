package com.lza.pad.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import com.lza.pad.fragment.base._BaseFragment;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/21/15.
 */
public class SubjectFragment extends _BaseFragment {

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
        if (mArg != null) {
            mData = mArg.getStringArray(KEY_SUBJECT_DATA);
            mCurrentSubject = mArg.getInt(KEY_CURRENT_SUBJECT);
        }
        mInflater = LayoutInflater.from(mActivity);

        DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mMinWidth = metrics.widthPixels;

        int textSize = getResources().getDimensionPixelSize(R.dimen.subject_text_size);
        int padding = getResources().getDimensionPixelSize(R.dimen.subject_text_padding);
        int line = mData.length % NUM_LINES == 0 ? mData.length / NUM_LINES : mData.length / NUM_LINES + 1;
        mMinHeight = textSize * line + padding * 2 * (line + 2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.common_grid, container, false);
        mGrid = (GridView) view.findViewById(R.id.common_grid_grid);
        mGrid.setNumColumns(NUM_LINES);
        mGrid.setBackgroundColor(Color.WHITE);
        mGrid.setLayoutParams(new FrameLayout.LayoutParams(mMinWidth, mMinHeight));
        mGrid.setAdapter(new SubjectAdapter());
        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent data = new Intent();
                data.putExtra(KEY_CURRENT_SUBJECT, position);
                mActivity.setResult(Activity.RESULT_OK, data);
                mActivity.finish();
            }
        });
        return view;
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
