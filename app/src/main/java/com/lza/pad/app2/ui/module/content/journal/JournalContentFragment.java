package com.lza.pad.app2.ui.module.content.journal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.app2.ui.widget.base.BaseImageFragment;
import com.lza.pad.widget.DefaultEbookCover;
import com.lza.pad.widget.PagerSlidingTabStrip;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/3/19.
 */
public class JournalContentFragment extends BaseImageFragment {

    TextView mTxtTitle, mTxtAuthor, mTxtFrequency, mTxtPubplace, mTxtLang, mTxtFormat, mTxtISSN;
    DefaultEbookCover mJournalCover;
    PagerSlidingTabStrip mPagerTab;
    ViewPager mViewPager;

    List<String> mTitles = new ArrayList<String>();

    JournalContentAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitles.add("目录");
        mTitles.add("简介");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.journal_content, container, false);
        mTxtTitle = (TextView) view.findViewById(R.id.journal_content_title);
        mTxtAuthor = (TextView) view.findViewById(R.id.journal_content_author);
        mTxtFrequency = (TextView) view.findViewById(R.id.journal_content_frequency);
        mTxtPubplace = (TextView) view.findViewById(R.id.journal_content_pubplace);
        mTxtLang = (TextView) view.findViewById(R.id.journal_content_lang);
        mTxtFormat = (TextView) view.findViewById(R.id.journal_content_format);
        mTxtISSN = (TextView) view.findViewById(R.id.journal_content_issn);

        mJournalCover = (DefaultEbookCover) view.findViewById(R.id.journal_content_book_cover);

        mPagerTab = (PagerSlidingTabStrip) view.findViewById(R.id.journal_content_pager_tab);
        mViewPager = (ViewPager) view.findViewById(R.id.journal_content_view_pager);
        mPagerTab.setTextSize(getResources().getDimensionPixelSize(R.dimen.ebook_content_pager_tab_text_size));

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestCover();
        showJournalContent();
        mAdapter = new JournalContentAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mPagerTab.setViewPager(mViewPager);
    }

    /**
     * 展示期刊内容
     */
    private void showJournalContent() {
        mTxtTitle.setText(wrap(mPadResource.getTitle(), "-"));
        mTxtAuthor.setText(wrap(mPadResource.getPress(), "-"));
        mTxtFrequency.setText(wrap(null, "-"));
        mTxtPubplace.setText(wrap(null, "-"));
        mTxtLang.setText(wrap(null, "-"));
        mTxtFormat.setText(wrap(null, "-"));
        mTxtISSN.setText(wrap(mPadResource.getIsbn(), "-"));
    }

    /**
     * 请求期刊封面
     */
    private void requestCover() {
        //显示封面
        String imgUrl = mPadResource.getIco();
        loadImage(imgUrl, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.default_ebook_cover);
                mJournalCover.setDrawable(new BitmapDrawable(getResources(), bm));
                mJournalCover.postInvalidate();
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                mJournalCover.setCoverTitle(mPadResource.getTitle());
                mJournalCover.setCoverAuthor(mPadResource.getAuthor());
                mJournalCover.postInvalidate();
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mJournalCover.setDrawable(new BitmapDrawable(getResources(), loadedImage));
                mJournalCover.setCoverTitle("");
                mJournalCover.setCoverAuthor("");
                mJournalCover.postInvalidate();
            }
        });
    }

    private class JournalContentAdapter extends FragmentPagerAdapter {

        public JournalContentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return getFragment(position);
        }

        @Override
        public int getCount() {
            return mTitles.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }
    }

    private Fragment getFragment(int index) {
        if (index == 0) {
            JournalContentCatalogFragment frg = new JournalContentCatalogFragment();
            frg.setArguments(createArguments());
            return frg;
        } else if (index == 1) {
            JournalContentSummaryFragment frg = new JournalContentSummaryFragment();
            frg.setArguments(createArguments());
            return frg;
        }
        return new Fragment();
    }

    protected Bundle createArguments() {
        Bundle arg = new Bundle();
        arg.putParcelable(KEY_PAD_DEVICE_INFO, mPadDeviceInfo);
        arg.putParcelable(KEY_PAD_RESOURCE_INFO, mPadResource);
        arg.putParcelable(KEY_PAD_WIDGET, mPadModuleWidget);
        return arg;
    }
}
