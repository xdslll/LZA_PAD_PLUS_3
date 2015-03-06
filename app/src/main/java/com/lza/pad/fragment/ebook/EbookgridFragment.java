package com.lza.pad.fragment.ebook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.app.ebook.EbookContentActivity2;
import com.lza.pad.app.journal.JournalContentActivity;
import com.lza.pad.app.news.NewsContentActivity;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.pad.PadResource;
import com.lza.pad.fragment.base.BaseImageFragment;
import com.lza.pad.helper.SimpleRequestListener;
import com.lza.pad.helper.JsonParseHelper;
import com.lza.pad.helper.UrlHelper;
import com.lza.pad.widget.DefaultEbookCover;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 2/22/15.
 */
public class EbookgridFragment extends BaseImageFragment {

    public static final int DEFAULT_START_PAGE = 1;
    int mGridNumColumns;
    int mDataSize;
    int mCurrentPage = DEFAULT_START_PAGE;

    GridView mGrid;
    TextView mTxtPrevPage, mTxtNextPage;

    List<PadResource> mPadResources;

    int mBookWidth, mBookHeight, mRowCount, mColumnCount, mPaddingHor, mPaddingVer;

    /**
     * 总页数
     */
    int mTotalPages, mTotalNums;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mPadControlInfo != null) {
            mGridNumColumns = parseInt(mPadControlInfo.getControl_data_each());
            mDataSize = parseInt(mPadControlInfo.getControl_data_size());
            log("数据量：" + mDataSize + "，每行显示：" + mGridNumColumns +
                    "，width=" + W + "，height=" + H);
        }
        mPaddingHor = (int) getResources().getDimension(R.dimen.ebookgrid_book_hor_padding);
        mPaddingVer = (int) getResources().getDimension(R.dimen.ebookgrid_book_ver_padding);

        mRowCount = (int) Math.ceil((double) mDataSize / mGridNumColumns);
        mColumnCount = mGridNumColumns;
        //垂直总的偏移量
        int totalVerticalPadding = getTotalPadding(mRowCount, mPaddingVer);
        int totalHorizontalPadding = getTotalPadding(mColumnCount, mPaddingHor);
        mBookWidth = (W - totalHorizontalPadding) / mColumnCount;
        mBookHeight = (H - totalVerticalPadding) / mRowCount;
    }

    /**
     * 计算垂直的空白尺寸
     *
     * @param rowCount
     * @param padding
     * @return
     */
    private int getTotalPadding(int rowCount, int padding) {
        if (rowCount <= 0) return 0;
        return (rowCount - 1) * padding;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.common_grid, container, false);
        mGrid = (GridView) view.findViewById(R.id.common_grid_grid);
        mGrid.setNumColumns(mGridNumColumns);
        mGrid.setOnScrollListener(new PauseOnScrollListener(getImageLoader(), true, false));
        mGrid.setBackgroundColor(getResources().getColor(R.color.whitesmoke));

        mTxtPrevPage = (TextView) view.findViewById(R.id.common_grid_prev_page);
        mTxtNextPage = (TextView) view.findViewById(R.id.common_grid_next_page);

        mTxtNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPage++;
                getPadResource();
            }
        });

        mTxtPrevPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPage--;
                getPadResource();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getPadResource();
        //String url = UrlHelper.getResourcesUrl(mPadDeviceInfo, mPadControlInfo.getSource_type(), mDataSize, mCurrentPage);
        //send(url, new PadResourceListener());
    }

    private void getPadResource() {
        String url = UrlHelper.getResourcesUrl(mPadDeviceInfo, mPadControlInfo.getSource_type(), mDataSize, mCurrentPage);
        send(url, new PadResourceListener());
    }

    private void showPageButton() {
        if (mCurrentPage <= DEFAULT_START_PAGE) {
            //如果是第一页，并且有下一页，则显示下一页
            mTxtPrevPage.setVisibility(View.GONE);
        } else {
            //如果不是第一页，则显示上一页
            mTxtPrevPage.setVisibility(View.VISIBLE);
        }
        if (mCurrentPage >= mTotalPages) {
            //如果当前也大于最后一页，则不显示下一页
            mTxtNextPage.setVisibility(View.GONE);
        } else {
            mTxtNextPage.setVisibility(View.VISIBLE);
        }
    }

    private class PadResourceListener extends SimpleRequestListener<PadResource> {

        @Override
        public ResponseData<PadResource> parseJson(String json) {
            return JsonParseHelper.parseResourceResponse(json);
        }

        @Override
        public boolean handlerResponse(ResponseData<PadResource> data) {
            mTotalPages = parseInt(data.getPages());
            mTotalNums = parseInt(data.getTotal_nums());
            return super.handlerResponse(data);
        }

        @Override
        public void handleRespone(List<PadResource> content) {
            mPadResources = content;
            mGrid.setAdapter(new EbookGridAdapter());
            mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (mPadResources == null || mPadResources.size() <= position) return;
                    PadResource res = mPadResources.get(position);
                    if (res == null) return;
                    Intent intent = null;
                    String sourceType = res.getSource_type();
                    if (sourceType.equals(PadResource.RESOURCE_EBOOK) || sourceType.equals(PadResource.RESOURCE_EBOOK_JC)
                            || sourceType.equals(PadResource.RESOURCE_HOT_BOOK) || sourceType.equals(PadResource.RESOURCE_NEW_BOOK)) {
                        intent = new Intent(mActivity, EbookContentActivity2.class);
                    } else if (sourceType.equals(PadResource.RESOURCE_NEWS)) {
                        intent = new Intent(mActivity, NewsContentActivity.class);
                    } else if (sourceType.equals(PadResource.RESOURCE_JOURNAL)) {
                        intent = new Intent(mActivity, JournalContentActivity.class);
                    }
                    if (intent != null) {
                        intent.putExtra(KEY_PAD_DEVICE_INFO, mPadDeviceInfo);
                        intent.putExtra(KEY_PAD_CONTROL_INFO, mPadControlInfo);
                        intent.putExtra(KEY_PAD_RESOURCE_INFO, res);
                        startActivity(intent);
                    }
                }
            });
            showPageButton();//数据加载完毕后，开始展示翻页按钮
        }
    }

    private class EbookGridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mPadResources.size();
        }

        @Override
        public PadResource getItem(int position) {
            return mPadResources.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.ebook_list_item, null);
            }
            final PadResource data = getItem(position);
            final ViewHolder holder = getHolder(convertView);

            holder.layout.setLayoutParams(new GridView.LayoutParams(mBookWidth, mBookHeight));
            holder.layout.setPadding(mPaddingHor, mPaddingVer, mPaddingHor, mPaddingVer);
            ImageSize size = new ImageSize(mBookWidth, mBookHeight);
            loadImage(data.getIco(), size, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    holder.book.setVisibility(View.GONE);
                    holder.img.setVisibility(View.VISIBLE);
                    holder.img.setImageResource(R.drawable.default_ebook_cover);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    holder.book.setVisibility(View.VISIBLE);
                    holder.img.setVisibility(View.GONE);
                    holder.book.setCoverTitle(data.getTitle());
                    holder.book.setCoverAuthor(data.getAuthor());
                    holder.book.setDrawable(getBitmapDrawable(R.drawable.default_ebook_cover));
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    holder.img.setImageBitmap(loadedImage);
                }
            });
            return convertView;
        }
    }

    private ViewHolder getHolder(View view) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        return holder;
    }

    private class ViewHolder {

        DefaultEbookCover book;
        RelativeLayout layout;
        ImageView img;

        public ViewHolder(View view) {
            img = (ImageView) view.findViewById(R.id.ebook_list_item_book_img);
            book = (DefaultEbookCover) view.findViewById(R.id.ebook_list_item_book);
            layout = (RelativeLayout) view.findViewById(R.id.ebook_list_item);
        }
    }
}
