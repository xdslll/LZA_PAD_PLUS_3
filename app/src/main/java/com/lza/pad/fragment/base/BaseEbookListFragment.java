package com.lza.pad.fragment.base;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
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

import com.lza.pad.R;
import com.lza.pad.support.utils.RuntimeUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * 页面基本框架，包含一个ViewPager，ViewPager中默认包含一个GridView，GridView下可以填充各种Adapter
 *
 * @author xiads
 * @Date 1/7/15.
 */
public abstract class BaseEbookListFragment<T> extends BaseFragment {

    protected TextView mTxtMore, mTxtTitle;
    protected LinearLayout mLayoutTitle;
    protected RadioGroup mRadPages;
    protected ViewPager mViewPager;
    protected PagerTabStrip mViewPagerTab;
    protected ImageView mImgBottom;

    protected LayoutInflater mInflater;

    protected List<T> mPageDatas;
    protected List<View> mPageViews;
    protected List<String> mPageTitles;
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
     * 图书显示区域的高度
     */
    protected int mBookAreaHeight = 0;

    protected int mGridNumColumns = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInflater = LayoutInflater.from(mActivity);

        //生成ViewPager的数据源
        mPageViews = getPageViews();
        mPageTitles = getPageTitles();

        //获取每行的数据量
        if (mArg != null) {
            mGridNumColumns = mArg.getInt(KEY_EBOOK_NUM_COLUMNS);
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
        if (!mIfHome) {
            mLayoutTitle.setVisibility(View.GONE);
            mRadPages.setVisibility(View.GONE);
            mTitleHeight = 0;
            mPagesHeight = 0;
        } else {
            generateTitleView();
            generateRaidoButton();
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
        mViewPager.setAdapter(new EbookListPagerAdapter());

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
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return view;
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
     * 获取GridView的数据源
     *
     * @return
     */
    protected abstract List<T> getPageDatas();

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
    protected abstract List<View> getPageViews();

    /**
     * GridView的Adapter
     *
     * @return
     */
    protected BaseAdapter getAdapter(int index) {
        return null;
    }

    /**
     * 每行显示多少个Grid
     *
     * @return
     */
    protected int getGridNumColumns() {
        return mGridNumColumns == 0 ? 2 : mGridNumColumns;
    }

    /**
     * 生成标准的GridView
     *
     * @return
     */
    protected View generateGridView(int index) {
        View view = mInflater.inflate(R.layout.common_grid, null);
        GridView grid = (GridView) view.findViewById(R.id.common_grid);
        mGridNumColumns = getGridNumColumns();
        grid.setNumColumns(mGridNumColumns);
        mPageDatas = getPageDatas();
        BaseAdapter adapter = getAdapter(index);
        if (adapter != null) {
            grid.setAdapter(adapter);
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    onGridItemClick(parent, view, position, id);
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
        mBookAreaHeight = H - mTitleHeight - mPagesHeight - mBottomImgHeight;
    }

    /**
     * ViewPager的Adapter对象
     */
    protected class EbookListPagerAdapter extends PagerAdapter {

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
}
