package com.lza.pad.fragment.ebook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.lza.pad.R;
import com.lza.pad.app.ebook.EbookActivity;
import com.lza.pad.app.ebook.EbookContentActivity;
import com.lza.pad.db.model.pad.PadResource;
import com.lza.pad.fragment.base.BaseResourceListFragment;
import com.lza.pad.helper.UrlHelper;
import com.lza.pad.support.network.VolleySingleton;
import com.lza.pad.widget.DefaultEbookCover;

import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/19/15.
 */
public class EbookFragment4 extends BaseResourceListFragment {

    @Override
    protected void onMoreButtonClick() {
        startActivity(new Intent(mActivity, EbookActivity.class));
    }

    @Override
    protected String getTitleText() {
        return "电子书推荐";
    }

    @Override
    protected String getUrl() {
        return UrlHelper.getResourcesUrl(mPadDeviceInfo, PadResource.RESOURCE_EBOOK, mDefaultPageSize, mDefaultPage);
    }

    @Override
    protected BaseAdapter getAdapter(int index, List<PadResource> data) {
        return new BaseEbookAdapter(index, data);
    }

    private class BaseEbookAdapter extends BaseAdapter {

        List<PadResource> datas;
        int index;
        ImageLoader imgLoader;
        int layoutWidth, layoutHeight;

        public BaseEbookAdapter(int index, List<PadResource> data) {
            this.index = index;
            this.datas = data;
            this.layoutWidth = getBookAreaWidth() / getCount();
            this.layoutHeight = getBookAreaHeight();
            this.imgLoader = VolleySingleton.getInstance(mActivity).getImageLoader("temp", layoutWidth, layoutHeight);
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public PadResource getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final PadResource data = datas.get(position);
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

            holder.layout.setLayoutParams(new GridView.LayoutParams(layoutWidth, layoutHeight));

            //水平垂直边距,如果不在首页，则拉大间距，保持界面美观
            int paddingHor, paddingVer;
            if (mIsHome) {
                paddingHor = (int) getResources().getDimension(R.dimen.ebook_list_book_padding_hor);
                paddingVer = (int) getResources().getDimension(R.dimen.ebook_list_book_padding_ver);
            } else {
                paddingHor = (int) getResources().getDimension(R.dimen.ebook_list_book_padding_hor2);
                paddingVer = (int) getResources().getDimension(R.dimen.ebook_list_book_padding_ver2);
            }
            holder.layout.setPadding(paddingHor, paddingVer, paddingHor, paddingVer);

            //默认显示自定义封面
            holder.book.setCoverTitle(data.getTitle());
            holder.book.setCoverAuthor(data.getAuthor());
            holder.book.setVisibility(View.VISIBLE);
            holder.bookImg.setVisibility(View.GONE);

            imgLoader.get(data.getIco(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    Bitmap bm = response.getBitmap();
                    if (bm == null) return;

                    holder.bookImg.setImageBitmap(bm);
                    holder.bookImg.setVisibility(View.VISIBLE);
                    holder.book.setVisibility(View.GONE);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    holder.bookImg.setVisibility(View.GONE);
                    holder.book.setVisibility(View.VISIBLE);
                }
            });

            return convertView;
        }
    }

    private class ViewHolder {
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
        Intent intent = new Intent(mActivity, EbookContentActivity.class);
        intent.putExtra(KEY_PAD_DEVICE_INFO, mPadDeviceInfo);
        intent.putExtra(KEY_PAD_RESOURCE_INFO, mPadResources.get(position));
        startActivity(intent);
    }
}
/*
            int paddingHor, paddingVer;
            //水平垂直边距,如果不在首页，则拉大间距，保持界面美观
            if (mIsHome) {
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