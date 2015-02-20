package com.lza.pad.fragment.ebook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.lza.pad.R;
import com.lza.pad.app.ebook.EbookActivity;
import com.lza.pad.app.ebook.EbookContentActivity;
import com.lza.pad.app.news.NewsContentActivity;
import com.lza.pad.db.model.pad.PadResource;
import com.lza.pad.fragment.base._BaseResourceListFragment;
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
public class EbookFragment1 extends _BaseResourceListFragment {

    @Override
    protected void onMoreButtonClick() {
        startActivity(new Intent(mActivity, EbookActivity.class));
    }

    @Override
    protected String getTitleText() {
        if (mPadControlInfo == null) {
            return "电子书推荐";
        } else {
            return mPadControlInfo.getTitle();
        }
    }

    @Override
    protected String getUrl() {
        if (mPadDeviceInfo == null || mPadControlInfo == null) return null;
        return UrlHelper.getResourcesUrl(mPadDeviceInfo,
                mPadControlInfo.getSource_type(), mDefaultPageSize, mDefaultPage);
    }

    BaseEbookAdapter mAdapter = null;
    @Override
    protected BaseAdapter getAdapter(int index, List<PadResource> data) {
        mAdapter = new BaseEbookAdapter(index, data);
        return mAdapter;
    }

    private class BaseEbookAdapter extends BaseAdapter {

        List<PadResource> datas;
        int index;
        ImageLoader imgLoader;
        int layoutWidth, layoutHeight;

        public BaseEbookAdapter(int index, List<PadResource> data) {
            this.index = index;
            this.datas = data;
            if (datas.size() == 0) {
                this.layoutWidth = getBookAreaWidth();
            } else {
                this.layoutWidth = getBookAreaWidth() / datas.size();
            }
            this.layoutHeight = getBookAreaHeight();
            this.imgLoader = VolleySingleton.getInstance(mActivity).getImageLoader(
                    TEMP_IMAGE_LOADER, layoutWidth, layoutHeight);
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
                holder.newsTitle = (TextView) convertView.findViewById(R.id.ebook_list_item_news);
                holder.newsLayout = (LinearLayout) convertView.findViewById(R.id.ebook_list_item_news_layout);
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

            if (mPadControlInfo != null) {
                String sourceType = mPadControlInfo.getSource_type();
                if (sourceType.equals(PadResource.RESOURCE_EBOOK) || sourceType.equals(PadResource.RESOURCE_EBOOK_JC)
                        || sourceType.equals(PadResource.RESOURCE_HOT_BOOK) || sourceType.equals(PadResource.RESOURCE_NEW_BOOK)) {
                    handleEbookCover(holder, data, imgLoader);
                } else if (sourceType.equals(PadResource.RESOURCE_NEWS)) {
                    handleNewsCover(holder, data, imgLoader);
                } else if (sourceType.equals(PadResource.RESOURCE_JOURNAL)) {
                    handleJournalCover(holder, data, imgLoader);
                }
            }
            return convertView;
        }
    }

    private void handleNewsCover(final ViewHolder holder, final PadResource data, ImageLoader imgLoader) {
        holder.newsTitle.setText(data.getTitle());

        holder.book.setVisibility(View.GONE);
        holder.bookImg.setVisibility(View.GONE);
        holder.newsLayout.setVisibility(View.VISIBLE);

        imgLoader.get(data.getIco(), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                Bitmap bm = response.getBitmap();
                if (bm == null) return;
                BitmapDrawable drawable = new BitmapDrawable(getResources(), bm);
                holder.newsLayout.setBackgroundDrawable(drawable);
                holder.newsLayout.setGravity(Gravity.BOTTOM);
                holder.newsTitle.setMaxLines(1);
                holder.newsTitle.setBackgroundColor(getResources().getColor(R.color.translucenter_dark));
                holder.newsTitle.setPadding(10, 15, 10, 15);
            }

            @Override
            public void onErrorResponse(VolleyError error) {}
        });
    }

    private void handleEbookCover(final ViewHolder holder, final PadResource data, final ImageLoader imgLoader) {
        //默认显示自定义封面
        holder.book.setCoverTitle(data.getTitle());
        holder.book.setCoverAuthor(data.getAuthor());

        holder.book.setVisibility(View.VISIBLE);
        holder.bookImg.setVisibility(View.GONE);
        holder.newsLayout.setVisibility(View.GONE);

        requestEbookCover(holder, data.getIco(), imgLoader);
        /*String doubanUrl = UrlHelper.createDoubanBookByIsbnUrl(data);
        DoubanRequestListener<DoubanBook> mDoubanListener = new DoubanRequestListener<DoubanBook>() {
            @Override
            public DoubanBook parseJson(String json) {
                return JsonParseHelper.parseDoubanBook(json);
            }

            @Override
            public void handleRespone(DoubanBook book) {
                String imgUrl = book.getImageUrl();
                if (TextUtils.isEmpty(imgUrl)) requestEbookCover(holder, data.getIco(), imgLoader);
                else requestEbookCover(holder, imgUrl, imgLoader);
            }
        };
        send(doubanUrl, mDoubanListener);*/
    }

    private void requestEbookCover(final ViewHolder holder, String url, ImageLoader imgLoader) {
        imgLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                Bitmap bm = response.getBitmap();
                if (bm == null) return;
                holder.book.setVisibility(View.GONE);
                holder.bookImg.setVisibility(View.VISIBLE);
                holder.bookImg.setImageBitmap(bm);
            }
            @Override
            public void onErrorResponse(VolleyError error) {}
        });
    }

    private void handleJournalCover(final ViewHolder holder, PadResource data, ImageLoader imgLoader) {
        //默认显示自定义封面
        holder.book.setCoverTitle(data.getTitle());
        holder.book.setCoverAuthor(data.getPubdate());
        BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.default_journal_cover1);
        holder.book.setDrawable(drawable);
        holder.book.setCoverTitleMargin(100.0f);
        holder.book.setCoverTitleColor(getResources().getColor(R.color.common_gray));

        holder.book.setVisibility(View.VISIBLE);
        holder.bookImg.setVisibility(View.GONE);
        holder.newsLayout.setVisibility(View.GONE);

        imgLoader.get(data.getIco(), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                Bitmap bm = response.getBitmap();
                if (bm == null) return;
                holder.book.setVisibility(View.GONE);
                holder.bookImg.setVisibility(View.VISIBLE);

                holder.bookImg.setImageBitmap(bm);
            }

            @Override
            public void onErrorResponse(VolleyError error) {}
        });
    }

    private class ViewHolder {
        RelativeLayout layout;
        DefaultEbookCover book;
        ImageView bookImg;
        TextView newsTitle;
        LinearLayout newsLayout;
    }

    @Override
    protected int getGridNumColumns() {
        if (mPadControlInfo != null) {
            String dataEach = mPadControlInfo.getControl_data_each();
            if (!TextUtils.isEmpty(dataEach)) {
                try {
                    return Integer.valueOf(dataEach);
                } catch (Exception ex) {

                }
            }
        }
        return super.getGridNumColumns();
    }

    @Override
    protected int getGridDataSize() {
        if (mPadControlInfo != null) {
            String dataSize = mPadControlInfo.getControl_data_size();
            if (!TextUtils.isEmpty(dataSize)) {
                try {
                    return Integer.valueOf(dataSize);
                } catch (Exception ex) {

                }
            }
        }
        return super.getGridDataSize();
    }

    @Override
    protected void onGridItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mPadResources == null || mPadResources.size() <= position) return;
        PadResource res = mPadResources.get(position);
        if (res == null) return;
        Intent intent = null;
        String sourceType = res.getSource_type();
        if (sourceType.equals(PadResource.RESOURCE_EBOOK) || sourceType.equals(PadResource.RESOURCE_EBOOK_JC)
                || sourceType.equals(PadResource.RESOURCE_HOT_BOOK) || sourceType.equals(PadResource.RESOURCE_NEW_BOOK)) {
            intent = new Intent(mActivity, EbookContentActivity.class);
        } else if (sourceType.equals(PadResource.RESOURCE_NEWS)) {
            intent = new Intent(mActivity, NewsContentActivity.class);
        } else if (sourceType.equals(PadResource.RESOURCE_JOURNAL)) {

        }
        if (intent != null) {
            intent.putExtra(KEY_PAD_DEVICE_INFO, mPadDeviceInfo);
            intent.putExtra(KEY_PAD_CONTROL_INFO, mPadControlInfo);
            intent.putExtra(KEY_PAD_RESOURCE_INFO, res);
            startActivity(intent);
        }
    }

}