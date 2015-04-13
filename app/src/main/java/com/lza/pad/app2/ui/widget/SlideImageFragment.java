package com.lza.pad.app2.ui.widget;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lza.pad.R;
import com.lza.pad.app2.event.base.OnPageChangeListener;
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
public class SlideImageFragment extends BaseImageFragment {

    ViewPager mViewPager;

    List<View> mViews = new ArrayList<View>();

    /**
     * 是否为系统自动切换
     * 如果为系统自动切换则会跳过重置场景、模块切换服务
     *
     */
    protected boolean mIsAutoSwitching = false;

    /**
     * 用户是否触摸了View，如果触摸了View，则会跳过一轮自动切换和自动更新
     */
    protected boolean mIsTouchView = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.common_slide_img, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.common_slide_img_viewpager);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (!mIsAutoSwitching) {
                    super.onPageSelected(position);
                }
                mIsAutoSwitching = false;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (state == ViewPager.SCROLL_STATE_SETTLING) {
                    if (!mIsAutoSwitching) {
                        mIsTouchView = true;
                    } else {
                        mIsTouchView = false;
                    }
                }
            }
        });

        showLoadingView(view);
        return view;
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
                View view1 = mInflater.inflate(R.layout.common_imageview, null);
                final ImageView imgView = (ImageView) view1.findViewById(R.id.common_img);
                ViewStub loadingViewStub = (ViewStub) view1.findViewById(R.id.common_img_viewstub);
                loadingViewStub.inflate();
                final LinearLayout loadingLayout = (LinearLayout) view1.findViewById(R.id.common_loading_layout);
                mViews.add(view1);
                log("加载图片：" + imgUrls[i]);
                loadImage(imgUrls[i], size, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        imgView.setImageBitmap(loadedImage);
                        loadingLayout.setVisibility(View.GONE);
                        dismissLoadingView();
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
            startWidgetSwitchingService();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopWidgetSwitchingService();
    }

    @Override
    protected void onWidgetSwitching() {
        if (mIsTouchView) {
            log("用户触摸了View，跳过一轮切换");
            mIsTouchView = false;
        } else {
            mIsAutoSwitching = true;
            switchWidget();
        }
    }

    private void switchWidget() {
        log("切换组件服务启动");
        if (mViewPager == null || mViewPager.getChildCount() == 0) return;
        int count = mViewPager.getChildCount();
        int currentItem = mViewPager.getCurrentItem();
        log("切换前，第" + currentItem + "页");
        currentItem++;
        if (currentItem > count) {
            currentItem = 0;
        }
        log("切换后，第" + currentItem + "页");
        final int index = new Integer(currentItem);
        getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                mViewPager.setCurrentItem(index, true);
            }
        });
    }
}
