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
import android.widget.TextView;

import com.lza.pad.R;
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

    protected LayoutInflater mInflater;
    protected List<Integer> mRadPageIds = new ArrayList<Integer>();

    protected int mPageSize, mEachPageSize, mTotalPage, mCurrentPage;

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

    private EbookListPagerAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.common_module_container, container, false);

        mTxtTitle = (TextView) view.findViewById(R.id.ebook_list_title_text);
        mTxtMore = (TextView) view.findViewById(R.id.ebook_list_more);
        mTxtMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showLong(mActivity, "点击更多按钮");
            }
        });
        mTxtTitle.setText(mPadModuleWidget.getLabel());

        mLayoutTitle = (LinearLayout) view.findViewById(R.id.ebook_list_title);
        mRadPages = (RadioGroup) view.findViewById(R.id.ebook_list_pages);
        mImgBottom = (ImageView) view.findViewById(R.id.ebook_list_bottom_img);

        mViewPager = (ViewPager) view.findViewById(R.id.ebook_list_viewpager);

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
                log("[" + mPadModuleWidget.getLabel() + "]当前页：" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

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
        String url = UrlHelper.getResourcesUrl(mPadDeviceInfo,
                mPadWidgetData.getType(), mPageSize, mTotalPage);
        send(url, new PadResourceListener());
    }

    private class PadResourceListener extends SimpleRequestListener<PadResource> {
        @Override
        public ResponseData<PadResource> parseJson(String json) {
            return JsonParseHelper.parseResourceResponse(json);
        }

        @Override
        public void handleRespone(List<PadResource> content) {
            mPadResources = content;
            generateTitleView();
            generateRaidoButton();
            //计算底部图片高度
            calcBottom();
            //计算图书区域的高度
            calcBook();
            //先计算ViewPager的宽度和高度后再填充Adapter
            mViewPager.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mBookAreaHeight));
            mAdapter = new EbookListPagerAdapter(getChildFragmentManager());
            mViewPager.setAdapter(mAdapter);
            //startSlideShowService();
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
        int buttonW = getResources().getDimensionPixelSize(R.dimen.width30);
        int buttonH = getResources().getDimensionPixelSize(R.dimen.width8);
        int paddingVer = RuntimeUtility.dip2px(mActivity, 8);
        mRadPages.setPadding(0, paddingVer, 0, paddingVer);
        mPageHeight = buttonH + (paddingVer * 2);
        for (int i = 0; i < mTotalPage; i++) {
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
        int end = start + mEachPageSize;
        List<PadResource> _data = mPadResources.subList(start, end);
        ArrayList<PadResource> data = new ArrayList<PadResource>(_data);
        //生成Fragment，填充ViewPager
        Fragment fragment = new BaseGridFragment();
        fragment.setArguments(createArgument(data));
        return fragment;
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
}
