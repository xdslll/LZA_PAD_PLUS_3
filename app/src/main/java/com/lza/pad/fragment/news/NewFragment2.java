package com.lza.pad.fragment.news;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.lza.pad.R;
import com.lza.pad.app.ebook.EbookActivity;
import com.lza.pad.app.ebook._EbookContentActivity;
import com.lza.pad.db.model.pad.PadResource;
import com.lza.pad.fragment.base.BaseResourceListFragment;
import com.lza.pad.widget.DefaultEbookCover;

import java.util.ArrayList;
import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/20/15.
 */
public class NewFragment2 extends BaseResourceListFragment {

    @Override
    protected void onMoreButtonClick() {
        startActivity(new Intent(mActivity, EbookActivity.class));
    }

    @Override
    protected String getTitleText() {
        return "到馆新书";
    }

    @Override
    protected String getUrl() {
        return null;
    }

    @Override
    protected List<View> getPageViews() {
        List<View> views = new ArrayList<View>();
        views.add(generateGridView(0, null));
        views.add(generateGridView(1, null));
        views.add(generateGridView(2, null));
        views.add(generateGridView(3, null));
        views.add(generateGridView(4, null));
        return views;
    }

    @Override
    protected BaseAdapter getAdapter(int index, List<PadResource> data) {
        return new BaseNewbookAdapter(index);
    }

    private class BaseNewbookAdapter extends BaseAdapter {

        public BaseNewbookAdapter(int index) {}

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
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.newbook_list_item, null);
                holder.layout = (FrameLayout) convertView.findViewById(R.id.newbook_list_item);
                holder.book = (DefaultEbookCover) convertView.findViewById(R.id.newbook_list_item_book);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            int w = W / getGridNumColumns();
            int h = mBookAreaHeight / (getCount() / getGridNumColumns());
            holder.layout.setLayoutParams(new GridView.LayoutParams(w, h));
            int paddingHor, paddingVer;
            //水平垂直边距,如果不在首页，则拉大间距，保持界面美观
            if (mIsHome) {
                paddingHor = (int) getResources().getDimension(R.dimen.newbook_list_book_padding_hor);
                paddingVer = (int) getResources().getDimension(R.dimen.newbook_list_book_padding_ver);
            } else {
                paddingHor = (int) getResources().getDimension(R.dimen.newbook_list_book_padding_hor2);
                paddingVer = (int) getResources().getDimension(R.dimen.newbook_list_book_padding_ver2);
            }
            if (position == 0) {
                holder.layout.setPadding(paddingHor, paddingVer, 0, 0);
            } else if (position == 1) {
                holder.layout.setPadding(paddingHor, paddingVer, paddingHor, 0);
            } else if (position == 2) {
                holder.layout.setPadding(paddingHor, paddingVer, 0, paddingVer);
            } else if (position == 3) {
                holder.layout.setPadding(paddingHor, paddingVer, paddingHor, paddingVer);
            }

            return convertView;
        }
    }

    private static class ViewHolder {
        FrameLayout layout;
        DefaultEbookCover book;
    }

    @Override
    protected void onGridItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(mActivity, _EbookContentActivity.class));
    }
}
