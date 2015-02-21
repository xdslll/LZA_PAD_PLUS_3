package com.lza.pad.fragment.base;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.lza.pad.R;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.pad.PadModuleControl;
import com.lza.pad.db.model.pad.PadResource;
import com.lza.pad.helper.CommonRequestListener;
import com.lza.pad.helper.JsonParseHelper;
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
@Deprecated
public abstract class _BaseResourceListFragment extends BaseFragment {

    protected TextView mTxtMore, mTxtTitle;
    protected LinearLayout mLayoutTitle;
    protected RadioGroup mRadPages;
    protected ViewPager mViewPager;
    protected PagerTabStrip mViewPagerTab;
    protected ImageView mImgBottom;

    protected LayoutInflater mInflater;

    protected List<PadResource> mPadResources;
    protected List<View> mPageViews;
    protected List<String> mPageTitles;
    protected List<Integer> mRadPageIds = new ArrayList<Integer>();

    /**
     * 当前翻到第几页
     */
    protected int mCurrentPageNumber = 0;

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
    protected int mDefaultPageSize = 20;

    /**
     * 默认请求的数据量
     */
    protected int mDefaultEveryPageSize = 4;

    /**
     * 默认请求页数
     */
    protected int mDefaultPage = 1;

    protected boolean mIfSlideShow = true;

    protected int mSlideShowTime = 0;

    protected int mSlideShowPeriod = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInflater = LayoutInflater.from(mActivity);

        //生成ViewPager的数据源
        //mPageViews = getPageViews();
        //mPageTitles = getPageTitles();

        //获取每行的数据量
        if (mArg != null) {
            mGridNumColumns = mArg.getInt(KEY_EBOOK_NUM_COLUMNS);
        }
        if (mGridNumColumns <= 0) {
            //mGridNumColumns = getGridNumColumns();
            int eachData = getGridNumColumns();
            setGridNumColumns(eachData);
        }
        mDefaultPageSize = getGridDataSize();
        if (mPadControlInfo != null) {
            String ifSlideShow = UniversalUtility.wrap(mPadControlInfo.getIf_show_slide(), "0");
            if (ifSlideShow.equals(PadModuleControl.BOOLEAN_SHOW_SLIDE)) {
                mIfSlideShow = true;
            } else if (ifSlideShow.equals(PadModuleControl.BOOLEAN_NOT_SHOW_SLIDE)) {
                mIfSlideShow = false;
            }
            mSlideShowTime = Integer.valueOf(UniversalUtility.wrap(mPadControlInfo.getSlide_show_time(), "0"));
            mSlideShowPeriod = Integer.valueOf(UniversalUtility.wrap(mPadControlInfo.getSlide_show_period(), "0"));
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
        mViewPagerTab = (PagerTabStrip) view.findViewById(R.id.ebook_list_viewpager_title);
        mViewPagerTab.setDrawFullUnderline(false);
        mViewPagerTab.setTextColor(getResources().getColor(R.color.common_blue));
        mViewPagerTab.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        mViewPagerTab.setTabIndicatorColor(getResources().getColor(R.color.white));
        //默认不显示ViewPager的Title，如果需要，可以手动打开
        mViewPagerTab.setVisibility(View.GONE);
        //默认显示第一页
        mViewPager.setCurrentItem(0);
        //mViewPager.setAdapter(new EbookListPagerAdapter());

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
                mCurrentPageNumber = position;
                log("[" + mPadControlInfo.getTitle() + "]当前页数：" + mCurrentPageNumber);
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
        send(getUrl(), mResourceListener);
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

    private CommonRequestListener<PadResource> mResourceListener = new CommonRequestListener<PadResource>() {

        @Override
        public void handleRespone(List<PadResource> content) {
            //生成ViewPager的数据源
            mPadResources = content;
            mPageViews = getPageViews();
            mPageTitles = getPageTitles();
            mViewPager.setAdapter(new EbookListPagerAdapter());

            if (mIsHome) {
                generateTitleView();
                generateRaidoButton();
            }
            startSlideShowService();
        }

        @Override
        public void handleRespone(VolleyError error) {

        }

        @Override
        public ResponseData<PadResource> parseJson(String json) {
            return JsonParseHelper.parseResourceResponse(json);
        }
    };

    private void startSlideShowService() {
        mSlideShowService = new SlideShowService(mSlideShowTime, mSlideShowPeriod, mIfSlideShow) {
            @Override
            public void show() {
                log("开始展示幻灯片");
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mCurrentPageNumber++;
                        if (mCurrentPageNumber >= mViewPager.getAdapter().getCount()) {
                            mCurrentPageNumber = 0;
                        }
                        mViewPager.setCurrentItem(mCurrentPageNumber);
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
    protected abstract String getTitleText();

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
    protected abstract String getUrl();

    /**
     * 获取翻页按钮文本
     *
     * @return
     */
    protected List<String> getPageTitles() {
        int length = mPageViews.size();
        List<String> titles = new ArrayList<String>();
        for (int i = 1; i <= length; i++) {
            titles.add(String.valueOf(i));
        }
        return titles;
    }

    /**
     * 获取ViewPager的View
     *
     * @return
     */
    protected List<View> getPageViews() {
        if (mPadResources == null) return null;
        int totalSize = mPadResources.size();
        List<View> views = new ArrayList<View>();
        for (int i = 0; i < totalSize; i++) {
            int start = i * mGridNumColumns;
            int end = start + mGridNumColumns;
            if (start >= totalSize) break;
            if (end > totalSize) end = totalSize;
            List<PadResource> subData = mPadResources.subList(start, end);
            views.add(generateGridView(i, subData));
            //views.add(generateGridView(i, mPageDatas));
        }
        return views;
    }

    /**
     * GridView的Adapter
     *
     * @return
     */
    protected BaseAdapter getAdapter(int index, List<PadResource> data) {
        return null;
    }

    /**
     * 每行显示多少个Grid
     *
     * @return
     */
    protected int getGridNumColumns() {
        return mDefaultEveryPageSize;
    }

    /**
     * 设置每行显示的数据量
     * @param eachData
     */
    private void setGridNumColumns(int eachData) {
        if (eachData < 0) mGridNumColumns = 0;
        else mGridNumColumns = eachData;
    }

    /**
     * 总数据量
     * @return
     */
    protected int getGridDataSize() {
        return mDefaultPageSize;
    }

    /**
     * 生成标准的GridView
     *
     * @return
     */
    protected View generateGridView(int index, List<PadResource> data) {
        View view = mInflater.inflate(R.layout.common_grid, null);
        GridView grid = (GridView) view.findViewById(R.id.common_grid);

        grid.setNumColumns(mGridNumColumns);
        //mPageDatas = getPageDatas();
        //mPageDatas = data;
        BaseAdapter adapter = getAdapter(index, data);
        if (adapter != null) {
            grid.setAdapter(adapter);
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    onGridItemClick(parent, view, mGridNumColumns * mCurrentPageNumber + position, id);
                }
            });
        }
        return view;
    }

    protected void onGridItemClick(AdapterView<?> parent, View view, int position, long id) {}

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
        int size = mPageViews.size();
        //设定并计算翻页控件高度
        int buttonW = RuntimeUtility.dip2px(mActivity, 30);
        int buttonH = RuntimeUtility.dip2px(mActivity, 8);
        int paddingVer = RuntimeUtility.dip2px(mActivity, 8);
        mRadPages.setPadding(0, paddingVer, 0, paddingVer);
        mPagesHeight = buttonH + (paddingVer * 2);
        for (int i = 0; i < size; i++) {
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

    /**
     * ViewPager的Adapter对象
     */
    private class EbookListPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mPageViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mPageViews.get(position));
            return mPageViews.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mPageViews.get(position));
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mPageTitles.get(position);
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
