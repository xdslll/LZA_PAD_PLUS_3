package com.lza.pad.app2.ui.widget;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lza.pad.R;
import com.lza.pad.app2.event.base.OnItemClickListener;
import com.lza.pad.app2.event.base.OnRefreshListener2;
import com.lza.pad.app2.event.base.PauseOnScrollListener;
import com.lza.pad.app2.ui.widget.base.BaseImageFragment;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.pad.PadResource;
import com.lza.pad.helper.JsonParseHelper;
import com.lza.pad.helper.SimpleRequestListener;
import com.lza.pad.helper.UrlHelper;
import com.lza.pad.support.utils.ToastUtils;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 新闻列表
 *
 * @author xiads
 * @Date 2/23/15.
 */
public class ListResourceFragment extends BaseImageFragment {

    public static final int DEFAULT_START_PAGE = 1;
    protected int mDataSize = 0;
    protected int mCurrentPage = DEFAULT_START_PAGE;

    protected PullToRefreshListView mRefreshList;
    protected List<PadResource> mPadResources = new ArrayList<PadResource>();

    protected PadResourceListAdapter mAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mPadWidgetData != null) {
            mDataSize = parseInt(mPadWidgetData.getData_size());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.common_resfresh_list, container, false);
        mRefreshList = (PullToRefreshListView) view.findViewById(R.id.pull_to_refresh_list);

        mRefreshList.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mRefreshList.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                super.onPullDownToRefresh(refreshView);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                super.onPullDownToRefresh(refreshView);
                mCurrentPage++;
                getPadResource();
            }
        });
        mRefreshList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                super.onItemClick(parent, view, position, id);
                if (isEmpty(mPadResources) || mPadResources.get(position) == null) return;
                startContentPage(mPadResources.get(position - 1));
            }
        });
        mRefreshList.setOnScrollListener(new PauseOnScrollListener(getImageLoader(), true, false));

        showLoadingView(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getPadResource();
    }

    private void getPadResource() {
        if (mPadWidgetData != null) {
            String url = UrlHelper.getResourcesUrl(mPadDeviceInfo, mPadWidgetData.getType(), mDataSize, mCurrentPage);
            send(url, new PadResourceListener());
        } else {
            dismissLoadingView();
        }
    }

    private class PadResourceListener extends SimpleRequestListener<PadResource> {

        @Override
        public ResponseData<PadResource> parseJson(String json) {
            return JsonParseHelper.parseResourceResponse(json);
        }

        @Override
        public void handleRespone(List<PadResource> content) {
            //mPadResources = content;
            //mRefreshList.setAdapter(new PadResourceList());
            mPadResources.addAll(content);
            if (mAdapter == null) {
                mAdapter = new PadResourceListAdapter();
                mRefreshList.setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
                mRefreshList.onRefreshComplete();
            }
            dismissLoadingView();
        }

        @Override
        public void handleResponseFailed() {
            reachPageEnd();
        }
    }

    private void reachPageEnd() {
        mRefreshList.onRefreshComplete();
        ToastUtils.showLong(mActivity, "已到达最后一页！");
    }

    private class PadResourceListAdapter extends BaseAdapter {

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
                convertView = mInflater.inflate(R.layout.news_list_item, null);
            }
            final ViewHolder holder = getHolder(convertView);
            PadResource data = getItem(position);
            int imgWidth = holder.img.getWidth();
            int imgHeight = holder.img.getHeight();
            ImageSize size = new ImageSize(imgWidth, imgHeight);
            loadImage(data.getIco(), size, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    holder.img.setVisibility(View.VISIBLE);
                    holder.img.setImageBitmap(loadedImage);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    holder.img.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    holder.img.setVisibility(View.GONE);
                }
            });
            holder.title.setText(data.getTitle());
            holder.date.setText(data.getPubdate());
            String abs = data.getAbs();
            holder.content.setText(Html.fromHtml(abs).toString());
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
        ImageView img;
        TextView title, date, content;

        public ViewHolder(View view) {
            img = (ImageView) view.findViewById(R.id.news_list_item_img);
            title = (TextView) view.findViewById(R.id.news_list_item_title);
            date = (TextView) view.findViewById(R.id.news_list_item_date);
            content = (TextView) view.findViewById(R.id.news_list_item_content);
        }
    }
}
