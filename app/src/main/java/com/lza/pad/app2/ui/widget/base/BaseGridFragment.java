package com.lza.pad.app2.ui.widget.base;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.app2.event.base.OnItemClickListener;
import com.lza.pad.db.model.pad.PadResource;
import com.lza.pad.widget.DefaultEbookCover;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 2/20/15.
 */
public class BaseGridFragment extends BaseImageFragment {

    GridView mGrid;
    ArrayList<PadResource> mPadResources;

    int mEachPageSize;
    int mTotalPage;
    int mPageSize;
    int mWidth, mHeight;

    String mSourceType = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEachPageSize = getArguments().getInt(KEY_PAGE_SIZE);
            mTotalPage = getArguments().getInt(KEY_TOTAL_PAGE);
            mPageSize = getArguments().getInt(KEY_DATA_SIZE);
            mPadResources = getArguments().getParcelableArrayList(KEY_PAD_RESOURCE_INFOS);
            mWidth = getArguments().getInt(KEY_FRAGMENT_WIDTH);
            mHeight = getArguments().getInt(KEY_FRAGMENT_HEIGHT);
        }
        mSourceType = mPadWidgetData.getType();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.common_grid, container, false);
        mGrid = (GridView) view.findViewById(R.id.common_grid_grid);
        mGrid.setNumColumns(mEachPageSize);
        mGrid.setOnScrollListener(new PauseOnScrollListener(getImageLoader(), true, false));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mGrid.setAdapter(new BookGridAdapter());
        mGrid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                super.onItemClick(parent, view, position, id);
                if (isEmpty(mPadResources) || mPadResources.get(position) == null) return;
                startContentPage(mPadResources.get(position));
                /*if (isEmpty(mPadResources) || mPadEvent == null || isEmpty(mPadEvent.getEvent_code_path())) return;
                PadResource res = mPadResources.get(position);
                if (res == null) return;
                String activityPath = buildCodePath(mPadEvent.getEvent_code_path());
                Intent intent = new Intent();
                intent.setClassName(mActivity, activityPath);
                intent.putExtra(KEY_PAD_DEVICE_INFO, mPadDeviceInfo);
                intent.putExtra(KEY_PAD_WIDGET, mPadModuleWidget);
                intent.putExtra(KEY_PAD_RESOURCE_INFO, res);
                startActivity(intent);*/
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private class BookGridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mEachPageSize;
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
            final PadResource data = mPadResources.get(position);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.ebook_list_item, null);
            }
            ViewHolder holder = getHolder(convertView);

            if (mSourceType.equals(PadResource.RESOURCE_NEWS)) {
                holder.layout.setLayoutParams(new GridView.LayoutParams(mWidth / getCount(), mHeight));
            } else {
                holder.layout.setLayoutParams(new GridView.LayoutParams(mWidth / mEachPageSize, mHeight));
            }

            //水平垂直边距,如果不在首页，则拉大间距，保持界面美观
            int paddingHor = (int) getResources().getDimension(R.dimen.small_padding);
            int paddingVer = (int) getResources().getDimension(R.dimen.small_padding);
            holder.layout.setPadding(paddingHor, paddingVer, paddingHor, paddingVer);

            if (mSourceType.equals(PadResource.RESOURCE_EBOOK) || mSourceType.equals(PadResource.RESOURCE_EBOOK_JC)
                    || mSourceType.equals(PadResource.RESOURCE_HOT_BOOK) || mSourceType.equals(PadResource.RESOURCE_NEW_BOOK)) {
                handleEbookCover(holder, data);
            } else if (mSourceType.equals(PadResource.RESOURCE_NEWS)) {
                handleNewsCover(holder, data);
            } else if (mSourceType.equals(PadResource.RESOURCE_JOURNAL)) {
                handleJournalCover(holder, data);
            }
            //holder.bookImg.setVisibility(View.VISIBLE);
            //holder.bookImg.setImageResource(R.drawable.default_ebook_cover);

            return convertView;
        }

    }

    private void handleEbookCover(final ViewHolder holder, final PadResource data) {
        //计算图片的尺寸
        int width = mWidth / mEachPageSize;
        int height = mHeight;
        ImageSize size = new ImageSize(width, height);
        String imgUrl = data.getIco();
        loadImage(imgUrl, size, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.bookImg.setImageBitmap(loadedImage);
            }

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                holder.book.setVisibility(View.GONE);
                holder.bookImg.setVisibility(View.VISIBLE);
                holder.bookImg.setImageResource(R.drawable.default_ebook_cover);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                holder.book.setCoverTitle(data.getTitle());
                holder.book.setCoverAuthor(data.getAuthor());

                holder.book.setVisibility(View.VISIBLE);
                holder.bookImg.setVisibility(View.GONE);
            }
        });
    }

    private void handleNewsCover(final ViewHolder holder, final PadResource data) {
        //计算图片的尺寸
        int width = mWidth / mEachPageSize;
        int height = mHeight;
        ImageSize size = new ImageSize(width, height);
        String imgUrl = data.getIco();
        loadImage(imgUrl, size, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (isDetached() || !isVisible()) return;
                BitmapDrawable drawable = new BitmapDrawable(getResources(), loadedImage);
                holder.newsLayout.setBackgroundDrawable(drawable);
                holder.newsLayout.setGravity(Gravity.BOTTOM);
                holder.newsTitle.setMaxLines(1);
                holder.newsTitle.setBackgroundColor(getResources().getColor(R.color.translucenter_dark));
                holder.newsTitle.setPadding(10, 15, 10, 15);
            }

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                holder.newsLayout.setVisibility(View.VISIBLE);
                holder.newsTitle.setText(data.getTitle());
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }
        });
    }

    private void handleJournalCover(final ViewHolder holder, final PadResource data) {
        //计算图片的尺寸
        int width = mWidth / mEachPageSize;
        int height = mHeight;
        ImageSize size = new ImageSize(width, height);
        String imgUrl = data.getIco();
        loadImage(imgUrl, size, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.bookImg.setImageBitmap(loadedImage);
            }

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                holder.book.setVisibility(View.GONE);
                holder.bookImg.setVisibility(View.VISIBLE);
                holder.bookImg.setImageResource(R.drawable.default_journal_cover1);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                holder.book.setCoverTitle(data.getTitle());
                holder.book.setCoverAuthor(data.getPubdate());
                BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.default_journal_cover1);
                holder.book.setDrawable(drawable);
                holder.book.setCoverTitleMargin(100.0f);
                holder.book.setCoverTitleColor(getResources().getColor(R.color.common_gray));

                holder.book.setVisibility(View.VISIBLE);
                holder.bookImg.setVisibility(View.GONE);
            }
        });
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
        RelativeLayout layout;
        DefaultEbookCover book;
        ImageView bookImg;
        TextView newsTitle;
        LinearLayout newsLayout;

        ViewHolder(View convertView) {
            layout = (RelativeLayout) convertView.findViewById(R.id.ebook_list_item);
            book = (DefaultEbookCover) convertView.findViewById(R.id.ebook_list_item_book);
            bookImg = (ImageView) convertView.findViewById(R.id.ebook_list_item_book_img);
            newsTitle = (TextView) convertView.findViewById(R.id.ebook_list_item_news);
            newsLayout = (LinearLayout) convertView.findViewById(R.id.ebook_list_item_news_layout);
        }
    }
}
