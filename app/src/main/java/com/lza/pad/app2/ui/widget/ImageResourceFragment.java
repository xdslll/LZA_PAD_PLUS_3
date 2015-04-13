package com.lza.pad.app2.ui.widget;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.app2.event.base.OnPageChangeListener;
import com.lza.pad.app2.ui.widget.base.BaseGridFragment;
import com.lza.pad.app2.ui.widget.base.BaseImageFragment;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.pad.PadResource;
import com.lza.pad.helper.JsonParseHelper;
import com.lza.pad.helper.SimpleRequestListener;
import com.lza.pad.helper.UrlHelper;
import com.lza.pad.support.utils.RuntimeUtility;
import com.lza.pad.support.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/13/15.
 */
public class ImageResourceFragment extends BaseImageFragment {

    protected TextView mTxtMore, mTxtTitle;
    protected LinearLayout mLayoutTitle;
    protected RadioGroup mRadPages;
    protected ViewPager mViewPager;
    protected PagerTabStrip mViewPagerTab;
    protected ImageView mImgBottom;
    protected LinearLayout mLayoutProgress;

    protected LayoutInflater mInflater;
    protected List<Integer> mRadPageIds = new ArrayList<Integer>();

    protected int mPageSize, mEachPageSize, mTotalPage, mCurrentPage = 0, mActualDataSize;

    protected List<PadResource> mPadResources;

    /**
     * 头部的高度
     */
    protected int mTitleHeight = 0;

    /**
     * 翻页控件的高度
     */
    protected int mPageHeight = 0;

    /**
     * 底部图片高度
     */
    protected int mBottomImgHeight = 0;
    /**
     * 图书显示区域的宽度和高度
     */
    protected int mBookAreaWidth = 0;
    protected int mBookAreaHeight = 0;

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

    private EbookListPagerAdapter mAdapter;

    /**
     * 翻页控件按钮宽度和高度
     */
    private int mRaidoButtonWidth = 0, mRadioButtonHeight = 0;

    /**
     * 是否请求过数据
     */
    protected boolean mHasRequest = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.common_module_container, container, false);

        mTxtTitle = (TextView) view.findViewById(R.id.common_container_title_text);
        mTxtTitle.setText(mPadModuleWidget.getLabel());
        mTxtMore = (TextView) view.findViewById(R.id.common_container_more);
        mTxtMore.setText("更多");
        mTxtMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showLong(mActivity, "点击更多按钮");
            }
        });

        mLayoutTitle = (LinearLayout) view.findViewById(R.id.ebook_list_title);
        mRadPages = (RadioGroup) view.findViewById(R.id.common_container_pages);
        mImgBottom = (ImageView) view.findViewById(R.id.common_container_bottom_bg);
        mLayoutProgress = (LinearLayout) view.findViewById(R.id.common_container_progressbar);

        mViewPager = (ViewPager) view.findViewById(R.id.ebook_list_viewpager);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                log("页面翻页前,mIsAutoSwitching=" + mIsAutoSwitching + ",mIsTouchView=" + mIsTouchView);
                /**
                 * 如果不是手动操作，将会触发重置场景、模块的切换事件
                 */
                if (!mIsAutoSwitching) {
                    super.onPageSelected(position);
                }
                mIsAutoSwitching = false;
                if (mRadPages.getVisibility() == View.VISIBLE) {
                    int checkId = mRadPageIds.get(position);
                    mRadPages.check(checkId);
                }
                log("页面翻页后,mIsAutoSwitching=" + mIsAutoSwitching + ",mIsTouchView=" + mIsTouchView);
                log("[" + mPadModuleWidget.getLabel() + "]当前页：" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                log("页面状态变更,state=" + state);
                if (state == ViewPager.SCROLL_STATE_SETTLING) {
                    log("页面状态变更前,mIsAutoSwitching=" + mIsAutoSwitching + ",mIsTouchView=" + mIsTouchView);
                    if (!mIsAutoSwitching) {
                        mIsTouchView = true;
                    } else {
                        mIsTouchView = false;
                    }
                    log("页面状态变更后,mIsAutoSwitching=" + mIsAutoSwitching + ",mIsTouchView=" + mIsTouchView);
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPageSize = parseInt(mPadWidgetData.getData_size());
        mEachPageSize = parseInt(mPadWidgetData.getData_each());
        mTotalPage = (int) Math.ceil((float) mPageSize / mEachPageSize);
        requestViewData();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHasRequest) {
            stopWidgetSwitchingService();
            stopWidgetUpdateService();
            startWidgetSwitchingService();
            startWidgetUpdateService();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopWidgetSwitchingService();
        stopWidgetUpdateService();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private class PadResourceListener extends SimpleRequestListener<PadResource> {
        @Override
        public ResponseData<PadResource> parseJson(String json) {
            return JsonParseHelper.parseResourceResponse(json);
        }

        @Override
        public boolean handlerResponse(ResponseData<PadResource> data) {
            mActualDataSize = parseInt(data.getTotal_nums());
            return super.handlerResponse(data);
        }

        @Override
        public void handleRespone(List<PadResource> content) {
            mPadResources = content;
            try {
                generateTitleView();
                generateRaidoButton();
                //计算底部图片高度
                calcBottom();
                //计算图书区域的高度
                calcBook();
                //先计算ViewPager的宽度和高度后再填充Adapter
                mViewPager.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mBookAreaHeight));
                mAdapter = new EbookListPagerAdapter(getChildFragmentManager());
                mViewPager.setAdapter(mAdapter);
                mLayoutProgress.setVisibility(View.GONE);
                //启动组件切换服务
                startWidgetSwitchingService();
                //启动组件更新服务
                startWidgetUpdateService();
                //将发送请求标识置为true
                mHasRequest = true;
            } catch (Exception ex) {

            }
        }

        @Override
        public void handleResponseFailed() {
            getMainHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestViewData();
                }
            }, RETRY_DELAY);
        }
    }

    /**
     * 计算标题高度，并设定布局
     */
    private void generateTitleView() {
        int titleTextSize = (int) mTxtTitle.getTextSize();
        int paddingVer = RuntimeUtility.dip2px(mActivity, 10);
        int paddingHor = RuntimeUtility.dip2px(mActivity, 30);
        mTitleHeight = titleTextSize + (paddingVer * 2);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mTitleHeight);
        mLayoutTitle.setLayoutParams(params);
        mLayoutTitle.setPadding(paddingHor, 0, paddingHor, 0);
        mTxtTitle.setVisibility(View.VISIBLE);
        mTxtMore.setVisibility(View.VISIBLE);
    }

    /**
     * 生成翻页按钮
     */
    private void generateRaidoButton() {
        //设定并计算翻页控件高度
        if (mRaidoButtonWidth == 0 || mRadioButtonHeight == 0) {
            if (mActivity == null || isDetached()) {
                return;
            } else {
                mRaidoButtonWidth = getResources().getDimensionPixelSize(R.dimen.width30);
                mRadioButtonHeight = getResources().getDimensionPixelSize(R.dimen.width8);
            }
        }
        int paddingVer = RuntimeUtility.dip2px(mActivity, 8);
        mRadPages.setPadding(0, paddingVer, 0, paddingVer);
        mPageHeight = mRadioButtonHeight + (paddingVer * 2);
        for (int i = 0; i < mTotalPage; i++) {
            //添加翻页按钮
            RadioButton button = new RadioButton(mActivity);
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(mRaidoButtonWidth, mRadioButtonHeight);
            params.setMargins(paddingVer, 0, paddingVer, 0);
            button.setLayoutParams(params);
            button.setBackgroundResource(R.drawable.page_selector);
            button.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            button.setId(i + 999);
            //添加点击事件
            button.setClickable(true);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = v.getId();
                    int position = mRadPageIds.indexOf(id);
                    mViewPager.setCurrentItem(position);
                }
            });
            //将RadioButton添加到RadioGroup
            mRadPageIds.add(button.getId());
            mRadPages.addView(button);
            if (i == 0) {
                mRadPages.check(button.getId());
            }
        }
    }

    /**
     * 计算底部图片所占高度
     */
    private void calcBottom() {
        BitmapDrawable drawable = (BitmapDrawable) mImgBottom.getDrawable();
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mImgBottom.getLayoutParams();
        int marginTop = params.topMargin;
        int marginBottom = params.bottomMargin;
        mBottomImgHeight = drawable.getBitmap().getHeight() + marginTop + marginBottom;
    }

    private void calcBook() {
        mBookAreaWidth = mPadWidgetLayout.getWidget_width();
        mBookAreaHeight = mPadWidgetLayout.getWidget_height() - mTitleHeight - mPageHeight - mBottomImgHeight;
    }

    /**
     * ViewPager的Adapter对象
     */
    private class EbookListPagerAdapter extends FragmentPagerAdapter {

        public EbookListPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            if (isEmpty(mPadResources)) {
                fragment = new Fragment();
            } else {
                fragment = getFragment(position);
                if (fragment == null) {
                    fragment = new Fragment();
                }
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return mTotalPage;
        }
    }

    protected Fragment getFragment(int position) {
        //计算当前是第几页
        mCurrentPage = position;
        //获取数据源
        int start = mCurrentPage * mEachPageSize;
        int end = 0;
        if (mActualDataSize < mEachPageSize) {
            end = start + mActualDataSize;
        } else {
            end = start + mEachPageSize;
        }
        if (start >= end) return new Fragment();

        try {
            log("[" + mPadModuleWidget.getLabel() + "]组件：start=" + start +
                    ",end=" + end + ",mCurrentPage=" + mCurrentPage +
                    ",mEachPageSize=" + mEachPageSize);
            List<PadResource> _data = mPadResources.subList(start, end);
            ArrayList<PadResource> data = new ArrayList<PadResource>(_data);
            //生成Fragment，填充ViewPager
            Fragment fragment = new BaseGridFragment();
            fragment.setArguments(createArgument(data));
            return fragment;
        } catch (Exception ex) {
            return new Fragment();
        }
    }

    private Bundle createArgument(ArrayList<PadResource> data) {
        Bundle arg = new Bundle();
        arg.putParcelable(KEY_PAD_DEVICE_INFO, mPadDeviceInfo);
        arg.putParcelable(KEY_PAD_WIDGET, mPadModuleWidget);
        arg.putInt(KEY_FRAGMENT_WIDTH, mBookAreaWidth);
        arg.putInt(KEY_FRAGMENT_HEIGHT, mBookAreaHeight);
        arg.putInt(KEY_TOTAL_PAGE, mTotalPage);
        arg.putInt(KEY_PAGE_SIZE, mEachPageSize);
        arg.putInt(KEY_CURRENT_PAGE, mTotalPage);
        arg.putInt(KEY_DATA_SIZE, mPageSize);
        arg.putParcelableArrayList(KEY_PAD_RESOURCE_INFOS, data);
        return arg;
    }

    /**
     * 定义组件切换时的事件
     */
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

    @Override
    protected void onWidgetUpdate() {
        if (mIsTouchView) {
            log("用户触摸了View，跳过一轮切换");
            mIsTouchView = false;
        } else {
            mIsAutoSwitching = true;
            updateWidget();
        }
    }

    private void updateWidget() {
        log("更新组件服务启动");
        getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                resetView();
                stopWidgetSwitchingService();
                stopWidgetUpdateService();
                requestViewData();
            }
        });
    }

    private void resetView() {
        mLayoutProgress.setVisibility(View.VISIBLE);
        if (mRadPages != null) {
            mRadPages.removeAllViews();
        }
        if (mViewPager != null) {
            mViewPager.removeAllViews();
        }
    }

    private void requestViewData() {
        mHasRequest = false;
        String url = UrlHelper.getResourcesUrl(mPadDeviceInfo,
                mPadWidgetData.getType(), mPageSize, mCurrentPage);
        log("[" + mPadModuleWidget.getLabel() + "]组件请求数据：" + url);
        send(url, new PadResourceListener());
    }
}
