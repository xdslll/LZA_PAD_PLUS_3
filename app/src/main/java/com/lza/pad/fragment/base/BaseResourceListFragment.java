package com.lza.pad.fragment.base;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.pad.PadModuleControl;
import com.lza.pad.db.model.pad.PadResource;
import com.lza.pad.helper.CommonRequestListener;
import com.lza.pad.helper.JsonParseHelper;
import com.lza.pad.helper.UrlHelper;
import com.lza.pad.support.utils.RuntimeUtility;
import com.lza.pad.support.utils.UniversalUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 页面基本框架，包含一个ViewPager，ViewPager中默认包含一个GridView，GridView下可以填充各种Adapter
 *
 * @author xiads
 * @Date 1/7/15.
 */
public abstract class BaseResourceListFragment extends BaseFragment {

    protected TextView mTxtMore, mTxtTitle;
    protected LinearLayout mLayoutTitle;
    protected RadioGroup mRadPages;
    protected ViewPager mViewPager;
    protected PagerTabStrip mViewPagerTab;
    protected ImageView mImgBottom;

    protected LayoutInflater mInflater;
    protected List<Integer> mRadPageIds = new ArrayList<Integer>();

    /**
     * 头部的高度
     */
    protected int mTitleHeight = 0;

    /**
     * 翻页控件的高度
     */
    protected int mPagesHeight = 0;

    /**
     * 底部图片高度
     */
    protected int mBottomImgHeight = 0;

    /**
     * 图书显示区域的宽度和高度
     */
    protected int mBookAreaWidth = 0;
    protected int mBookAreaHeight = 0;

    protected int mGridNumColumns = 0;

    /**
     * 请求总数据量
     */
    protected int mDataSize = 20;

    /**
     * 默认请求的数据量
     */
    protected int DEFAULT_EVERY_PAGE_SIZE = 4;

    /**
     * 默认请求页数
     */
    protected int DEFAULT_PAGE = 1;

    /**
     * 共需要请求多少页
     */
    protected int mTotalPageSize = 0;


    protected boolean mIfSlideShow = true;

    protected int mSlideShowTime = 0;

    protected int mSlideShowPeriod = 0;

    private EbookListPagerAdapter mAdapter;

    protected List<PadResource> mDataSource;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInflater = LayoutInflater.from(mActivity);

        //设置每行的数据量
        mGridNumColumns = getGridNumColumns();
        //获取默认请求的总数据量
        mDataSize = getGridDataSize();
        //计算ViewPager共多少页
        mTotalPageSize = (int) Math.ceil((float) mDataSize / mGridNumColumns);

        if (mPadControlInfo != null) {
            //判断是否启动幻灯片模式
            String ifSlideShow = UniversalUtility.wrap(mPadControlInfo.getIf_show_slide(), "0");
            if (ifSlideShow.equals(PadModuleControl.BOOLEAN_SHOW_SLIDE)) {
                mIfSlideShow = true;
            } else if (ifSlideShow.equals(PadModuleControl.BOOLEAN_NOT_SHOW_SLIDE)) {
                mIfSlideShow = false;
            }
            //获取幻灯片播放的参数
            //多久没有用户交互时开始播放
            mSlideShowTime = Integer.valueOf(UniversalUtility.wrap(mPadControlInfo.getSlide_show_time(), "60"));
            //多久开始翻页
            mSlideShowPeriod = Integer.valueOf(UniversalUtility.wrap(mPadControlInfo.getSlide_show_period(), "5"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.common_module_container, container, false);

        mTxtTitle = (TextView) view.findViewById(R.id.ebook_list_title_text);
        mTxtMore = (TextView) view.findViewById(R.id.ebook_list_more);
        mTxtMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMoreButtonClick();
            }
        });
        mTxtTitle.setText(getTitleText());
        mTxtMore.setText(getMoreText());

        mLayoutTitle = (LinearLayout) view.findViewById(R.id.ebook_list_title);
        mRadPages = (RadioGroup) view.findViewById(R.id.ebook_list_pages);
        //如果不在首页显示，则隐藏标题和翻页按钮
        if (!mIsHome) {
            mLayoutTitle.setVisibility(View.GONE);
            mRadPages.setVisibility(View.GONE);
            mTitleHeight = 0;
            mPagesHeight = 0;
        }
        mImgBottom = (ImageView) view.findViewById(R.id.ebook_list_bottom_img);
        //计算底部图片高度
        calcBottom();
        //计算图书区域的高度
        calcBook();
        //加载ViewPager
        mViewPager = (ViewPager) view.findViewById(R.id.ebook_list_viewpager);
        mViewPager.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mBookAreaHeight));

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mRadPages.getVisibility() == View.VISIBLE) {
                    int checkId = mRadPageIds.get(position);
                    mRadPages.check(checkId);
                }
                log("[" + mPadControlInfo.getTitle() + "]当前页：" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mSlideShowService != null) {
                    mSlideShowService.initSlideCurrentDelay();
                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String url = getUrl();
        send(url, new PadResourceListener());
    }

    private class PadResourceListener extends CommonRequestListener<PadResource> {
        @Override
        public ResponseData<PadResource> parseJson(String json) {
            return JsonParseHelper.parseResourceResponse(json);
        }

        @Override
        public void handleRespone(List<PadResource> content) {
            mDataSource = content;
            mAdapter = new EbookListPagerAdapter(getChildFragmentManager());
            mViewPager.setAdapter(mAdapter);

            if (mIsHome) {
                generateTitleView();
                generateRaidoButton();
            }
            startSlideShowService();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSlideShowService != null)
            mSlideShowService.stopSlideShowService();
    }

    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    SlideShowService mSlideShowService;

    private void startSlideShowService() {
        mSlideShowService = new SlideShowService(mSlideShowTime, mSlideShowPeriod, mIfSlideShow) {
            @Override
            public void show() {
                log("开始展示幻灯片");
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mViewPager == null) return;
                        int currentItem = mViewPager.getCurrentItem();
                        currentItem++;
                        if (currentItem == mTotalPageSize) {
                            currentItem = 0;
                        }
                        mViewPager.setCurrentItem(currentItem, true);
                    }
                });
            }
        };
        mSlideShowService.startSlideShowService();
    }

    /**
     * 实现更多按钮的点击事件
     */
    protected abstract void onMoreButtonClick();

    /**
     * 获取标题名称
     *
     * @return
     */
    protected String getTitleText() {
        if (mPadControlInfo != null) return mPadControlInfo.getTitle();
        return "未知模块";
    }

    /**
     * 获取更多按钮的文本
     *
     * @return
     */
    protected String getMoreText() {
        return "更多";
    }

    /**
     * 获取请求数据的Url
     *
     * @return
     */
    protected String getUrl() {
        if (mPadDeviceInfo == null || mPadControlInfo == null) return null;
        return UrlHelper.getResourcesUrl(mPadDeviceInfo,
                mPadControlInfo.getSource_type(), mDataSize, DEFAULT_PAGE);
    }

    /**
     * 获取翻页按钮文本
     *
     * @return
     */
    protected List<String> getPageTitles() {
        //int length = mPageViews.size();
        int length = mAdapter.getCount();
        List<String> titles = new ArrayList<String>();
        for (int i = 1; i <= length; i++) {
            titles.add(String.valueOf(i));
        }
        return titles;
    }

    /**
     * 每行显示多少个Grid
     *
     * @return
     */
    protected int getGridNumColumns() {
        if (mPadControlInfo != null) {
            return UniversalUtility.safeIntParse(mPadControlInfo.getControl_data_each(), DEFAULT_EVERY_PAGE_SIZE);
        }
        return DEFAULT_EVERY_PAGE_SIZE;
    }

    /**
     * 总数据量
     * @return
     */
    protected int getGridDataSize() {
        if (mPadControlInfo != null) {
            return UniversalUtility.safeIntParse(mPadControlInfo.getControl_data_size(), mDataSize);
        }
        return mDataSize;
    }

    /**
     * 获取总页数
     * @return
     */
    protected int getTotalPage() {
        return mTotalPageSize;
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
        //if (mAdapter == null || mAdapter.getCount() <= 0) return;
        //int length = mAdapter.getCount();
        int length = mTotalPageSize;
        //设定并计算翻页控件高度
        int buttonW = RuntimeUtility.dip2px(mActivity, 30);
        int buttonH = RuntimeUtility.dip2px(mActivity, 8);
        int paddingVer = RuntimeUtility.dip2px(mActivity, 8);
        mRadPages.setPadding(0, paddingVer, 0, paddingVer);
        mPagesHeight = buttonH + (paddingVer * 2);
        for (int i = 0; i < length; i++) {
            //添加翻页按钮
            RadioButton button = new RadioButton(mActivity);
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(buttonW, buttonH);
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
        mBookAreaWidth = W;
        mBookAreaHeight = H - mTitleHeight - mPagesHeight - mBottomImgHeight;
    }

    protected int getBookAreaWidth() {
        return mBookAreaWidth;
    }

    protected int getBookAreaHeight() {
        return mBookAreaHeight;
    }

    protected abstract Fragment getFragment(int position);

    protected List<PadResource> getDataSource() {
        return mDataSource;
    }

    /**
     * ViewPager的Adapter对象
     */
    protected class EbookListPagerAdapter extends FragmentPagerAdapter {

        public EbookListPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            if (mDataSource == null || mDataSource.size() <= 0) {
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
            return mTotalPageSize;
        }
    }

    protected abstract class SlideShowService {

        /**
         * 多久没人操作时触发幻灯片播放
         */
        int SLIDE_SHOW_START = 20;

        /**
         * 多久切换一次幻灯片
         */
        int SLIDE_SHOW_DELAY = 5;

        /**
         * 当前多久没人操作
         */
        int mSlideCurrentDelay = 0;

        /**
         * 是否可以展示幻灯片
         */
        boolean mCanSlideShow = true;

        /**
         * 是否可以开始展示幻灯片
         */
        boolean mCanSlideShowNow = false;

        ScheduledExecutorService mService;

        public void startSlideShowService() {
            //先判断是否可以展示幻灯片
            if (!mCanSlideShow || SLIDE_SHOW_DELAY <= 0 || SLIDE_SHOW_START <= 0) return;
            //如果服务已经启动，先停止服务
            if (mService != null)
                mService.shutdownNow();
            //初始化服务
            mService = Executors.newSingleThreadScheduledExecutor();
            mService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    //如果可以开始展示，则进行展示
                    if (mSlideCurrentDelay >= SLIDE_SHOW_START) {
                        try {
                            show();
                        } catch (Exception ex) {

                        }
                    } else {
                        mSlideCurrentDelay += SLIDE_SHOW_DELAY;
                        log("当前幻灯片等待时间：" + mSlideCurrentDelay);
                    }
                }
            }, 0, SLIDE_SHOW_DELAY, TimeUnit.SECONDS);
        }

        public void stopSlideShowService() {
            if (mService != null)
                mService.shutdownNow();
        }

        public void initSlideCurrentDelay() {
            mSlideCurrentDelay = 0;
        }

        public abstract void show();

        public void setSlideShowStart(int slideShowStart) {
            SLIDE_SHOW_START = slideShowStart;
        }

        public void setSlideShowDelay(int slideShowDelay) {
            SLIDE_SHOW_DELAY = slideShowDelay;
        }

        public SlideShowService(int slideShowStart, int slideShowDelay, boolean canShow) {
            setSlideShowStart(slideShowStart);
            setSlideShowDelay(slideShowDelay);
            mCanSlideShow = canShow;
        }

        public SlideShowService() {}
    }
}
