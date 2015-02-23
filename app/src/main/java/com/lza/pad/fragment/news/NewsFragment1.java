package com.lza.pad.fragment.news;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.lza.pad.R;
import com.lza.pad.app.news._NewsActivity;
import com.lza.pad.app.news.NewsContentActivity;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.pad.PadResource;
import com.lza.pad.fragment.base.BaseFragment;
import com.lza.pad.helper.CommonRequestListener;
import com.lza.pad.helper.JsonParseHelper;
import com.lza.pad.helper.UrlHelper;
import com.lza.pad.support.network.VolleySingleton;
import com.lza.pad.widget.IrregularNews;

import java.util.Arrays;
import java.util.List;

/**
 * 不规则的新闻控件
 *
 * @author xiads
 * @Date 1/16/15.
 */
public class NewsFragment1 extends BaseFragment {

    TextView mTxtTitle, mTxtMore;
    IrregularNews mIrregularNews;

    int DEFAULT_PAGE_SIZE = 6;
    int DEFAULT_PAGE = 1;

    ImageLoader mImageLoader;
    List<PadResource> mPadResources;
    List<Bitmap> mListBitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageLoader = VolleySingleton.getInstance(mActivity).getImageLoader(TEMP_IMAGE_LOADER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news, container, false);
        mTxtTitle = (TextView) view.findViewById(R.id.news_home_title);
        if (mPadControlInfo != null) {
            mTxtTitle.setText(mPadControlInfo.getTitle());
        } else {
            mTxtTitle.setText("新闻动态");
        }

        mTxtMore = (TextView) view.findViewById(R.id.news_home_more);
        mTxtMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, _NewsActivity.class));
            }
        });

        mIrregularNews = (IrregularNews) view.findViewById(R.id.news_irregular_news);
        mIrregularNews.setOnNewsClickListener(new IrregularNews.OnNewsClickListener() {
            @Override
            public void onClick(View v, int position) {
                //打开新闻详细页面
                if (mPadResources == null || position > mPadResources.size()) return;
                PadResource resource = mPadResources.get(position - 1);
                Intent intent = new Intent(mActivity, NewsContentActivity.class);
                intent.putExtra(KEY_PAD_RESOURCE_INFO, resource);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //请求新闻数据
        String url = UrlHelper.getResourcesUrl(mPadDeviceInfo, PadResource.RESOURCE_NEWS, DEFAULT_PAGE_SIZE, DEFAULT_PAGE);
        send(url, mListener);
    }

    private CommonRequestListener<PadResource> mListener = new CommonRequestListener<PadResource>() {

        @Override
        public ResponseData<PadResource> parseJson(String json) {
            return JsonParseHelper.parseResourceResponse(json);
        }

        @Override
        public void handleRespone(List<PadResource> content) {
            mIrregularNews.setPadResources(content);
            mPadResources = content;

            int size = content.size();
            Bitmap[] bitmaps = new Bitmap[size];
            mListBitmap = Arrays.asList(bitmaps);

            for (int i = 0; i < size; i++) {
                PadResource r = content.get(i);
                final int index = new Integer(i);
                log("PadResource.getIco()=" + r.getIco());
                mImageLoader.get(r.getIco(), new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        if (response == null || response.getBitmap() == null) return;
                        mListBitmap.set(index, response.getBitmap());
                        mIrregularNews.setBitmaps(mListBitmap);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
            }

        }
    };
}
