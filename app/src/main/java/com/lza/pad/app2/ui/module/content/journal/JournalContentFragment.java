package com.lza.pad.app2.ui.module.content.journal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.app2.event.base.OnClickListener;
import com.lza.pad.app2.ui.widget.base.BaseImageFragment;
import com.lza.pad.db.model.pad.PadResource;
import com.lza.pad.helper.UrlHelper;
import com.lza.pad.support.utils.Utility;
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

    /**
     * 二维码相关
     */
    LinearLayout mLayoutQCode;
    ImageView mImgQCode;
    TextView mTxtGetClient;

    List<String> mTitles = new ArrayList<String>();

    JournalContentAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitles.add("目录");
        //mTitles.add("简介");
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

        mLayoutQCode = (LinearLayout) view.findViewById(R.id.ebook_content2_qcode_layout);
        mImgQCode = (ImageView) view.findViewById(R.id.ebook_content2_qcode);

        mTxtGetClient = (TextView) view.findViewById(R.id.ebook_content2_get_client);
        mTxtGetClient.setText(Html.fromHtml("<u>" + mTxtGetClient.getText() + "</u>"));
        mTxtGetClient.setTextColor(Color.BLUE);
        mTxtGetClient.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                showDownloadDialog();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mPadResource != null) {
            String sourceType = mPadResource.getSource_type();
            if (sourceType.equals(PadResource.RESOURCE_JOURNAL)) {
                showQCode();
            } else {
                mLayoutQCode.setVisibility(View.GONE);
            }
        } else {
            mLayoutQCode.setVisibility(View.GONE);
        }

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

    private void showDownloadDialog() {
        Bitmap bm = Utility.createQCode("http://114.212.7.87/book_center/upload/version//app-release.apk",
                mImgQCode.getWidth() * 2, mImgQCode.getHeight() * 2);
        LinearLayout layout = new LinearLayout(mActivity);
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.WHITE);
        layout.setGravity(Gravity.CENTER);

        ImageView img = new ImageView(mActivity);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(mImgQCode.getWidth(), mImgQCode.getHeight());
        lp.setMargins(20, 20, 20, 20);
        img.setLayoutParams(lp);
        img.setImageBitmap(bm);

        layout.addView(img);
        AlertDialog dialog = new AlertDialog.Builder(mActivity)
                .setTitle("请扫描二维码下载Pad+客户端")
                .setView(layout)
                .setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    private void showQCode() {
        String fulltextUrl = mPadResource.getFulltext();
        if (isEmpty(fulltextUrl)) {
            mLayoutQCode.setVisibility(View.GONE);
        } else {
            mLayoutQCode.setVisibility(View.VISIBLE);
            try {
                String ebookUrl = UrlHelper.getPadResourceDetail(mPadResource);
                log("二维码（加密前）：" + ebookUrl);
                ebookUrl = Base64.encodeToString(ebookUrl.getBytes(), Base64.DEFAULT);
                log("二维码（加密后）：" + ebookUrl);
                Bitmap qcodeImage = Utility.createQCode(ebookUrl, mImgQCode.getWidth(), mImgQCode.getHeight());
                mImgQCode.setImageBitmap(qcodeImage);
            } catch (Exception ex) {
                mLayoutQCode.setVisibility(View.GONE);
            }
        }
    }
}
