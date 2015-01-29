package com.lza.pad.fragment;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lza.pad.R;
import com.lza.pad.app.EbookActivity;
import com.lza.pad.app.EbookContentActivity;
import com.lza.pad.fragment.base.BaseEbookListFragment;
import com.lza.pad.widget.DefaultEbookCover;

import java.util.ArrayList;
import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/19/15.
 */
public class EbookNormalListFragment extends BaseEbookListFragment<String> {

    private int[][] DATAS = new int[][] {
            new int[] {R.drawable.test_cover1, R.drawable.test_cover2, R.drawable.test_cover3, R.drawable.test_cover4},
            new int[] {R.drawable.test_cover5, R.drawable.test_cover6, R.drawable.test_cover7, R.drawable.test_cover8},
            new int[] {R.drawable.test_cover9, R.drawable.test_cover10, R.drawable.test_cover11, R.drawable.test_cover12},
            new int[] {R.drawable.test_cover13, R.drawable.test_cover14, R.drawable.test_cover15, R.drawable.test_cover16}
    };

    @Override
    protected void onMoreButtonClick() {
        startActivity(new Intent(mActivity, EbookActivity.class));
    }

    @Override
    protected String getTitleText() {
        return "电子书推荐";
    }

    @Override
    protected List<String> getPageDatas() {
        return new ArrayList<String>();
    }

    @Override
    protected List<View> getPageViews() {
        List<View> views = new ArrayList<View>();
        views.add(generateGridView(0));
        views.add(generateGridView(1));
        views.add(generateGridView(2));
        views.add(generateGridView(3));
        return views;
    }

    @Override
    protected BaseAdapter getAdapter(int index) {
        return new BaseEbookAdapter(index);
    }

    private class BaseEbookAdapter extends BaseAdapter {

        private int index;

        public BaseEbookAdapter(int index) {
            this.index = index;
        }

        @Override
        public int getCount() {
            return DATAS.length;
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
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.ebook_list_item, null);
                holder.layout = (RelativeLayout) convertView.findViewById(R.id.ebook_list_item);
                holder.book = (DefaultEbookCover) convertView.findViewById(R.id.ebook_list_item_book);
                holder.bookImg = (ImageView) convertView.findViewById(R.id.ebook_list_item_book_img);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.layout.setLayoutParams(new GridView.LayoutParams(W / getGridNumColumns(), mBookAreaHeight));
            int paddingHor, paddingVer;
            //水平垂直边距,如果不在首页，则拉大间距，保持界面美观
            if (mIfHome) {
                paddingHor = (int) getResources().getDimension(R.dimen.ebook_list_book_padding_hor);
                paddingVer = (int) getResources().getDimension(R.dimen.ebook_list_book_padding_ver);
            } else {
                paddingHor = (int) getResources().getDimension(R.dimen.ebook_list_book_padding_hor2);
                paddingVer = (int) getResources().getDimension(R.dimen.ebook_list_book_padding_ver2);
            }
            holder.layout.setPadding(paddingHor, paddingVer, paddingHor, paddingVer);

            holder.book.setVisibility(View.GONE);
            holder.bookImg.setImageResource(DATAS[index][position]);
            return convertView;
        }
    }

    private static class ViewHolder {
        RelativeLayout layout;
        DefaultEbookCover book;
        ImageView bookImg;
    }

    @Override
    protected int getGridNumColumns() {
        return mGridNumColumns == 0 ? 4 : mGridNumColumns;
    }

    @Override
    protected void onGridItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(mActivity, EbookContentActivity.class));
    }
}
/*
            int paddingHor, paddingVer;
            //水平垂直边距,如果不在首页，则拉大间距，保持界面美观
            if (mIfHome) {
                paddingHor = (int) getResources().getDimension(R.dimen.ebook_list_book_padding_hor);
                paddingVer = (int) getResources().getDimension(R.dimen.ebook_list_book_padding_ver);
            } else {
                paddingHor = (int) getResources().getDimension(R.dimen.ebook_list_book_padding_hor2);
                paddingVer = (int) getResources().getDimension(R.dimen.ebook_list_book_padding_ver2);
            }
            int bookW = W / getGridNumColumns() - paddingHor;
            int bookH = mBookAreaHeight - paddingVer;

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(bookW, bookH);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            holder.book.setLayoutParams(params);
            */
            /*holder.book.setOnClickListener(new View.OnClickListener() {
                @Override
                public void captureScreen(View v) {
                    startActivity(new Intent(mActivity, EbookContentActivity.class));
                }
            });*/