package com.lza.pad.app2.ui.widget;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lza.pad.app2.event.base.OnTouchListener;
import com.lza.pad.app2.ui.widget.base.BaseImageFragment;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/3/19.
 */
public class ImageFragment extends BaseImageFragment {

    ViewPager mViewPager;

    List<View> mViews = new ArrayList<View>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewPager = new ViewPager(mActivity);
        mViewPager.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        ));
        mViewPager.setOnTouchListener(new OnTouchListener());
        return mViewPager;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String imgUrl = mPadWidgetData.getUrl();
        if (imgUrl.contains(SEPERATOR)) {
            String[] imgUrls = imgUrl.split(SEPERATOR_SPLIT);

            ImageSize size = new ImageSize(mPadWidgetLayout.getWidget_width(),
                    mPadWidgetLayout.getWidget_height());
            for (int i = 0; i < imgUrls.length; i++) {
                final ImageView imgView = new ImageView(mActivity);
                imgView.setScaleType(ImageView.ScaleType.FIT_XY);
                imgView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                mViews.add(imgView);
                loadImage(imgUrls[i], size, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        imgView.setImageBitmap(loadedImage);
                    }
                });
            }
            mViewPager.setAdapter(new PagerAdapter() {
                @Override
                public int getCount() {
                    return mViews.size();
                }

                @Override
                public boolean isViewFromObject(View view, Object o) {
                    return view == o;
                }

                @Override
                public Object instantiateItem(ViewGroup container, int position) {
                    container.addView(mViews.get(position));
                    return mViews.get(position);
                }

                @Override
                public void destroyItem(ViewGroup container, int position, Object object) {
                    container.removeView(mViews.get(position));
                }
            });
        }
    }
}
