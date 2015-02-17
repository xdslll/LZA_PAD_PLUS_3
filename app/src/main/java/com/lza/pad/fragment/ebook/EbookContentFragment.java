package com.lza.pad.fragment.ebook;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.lza.pad.R;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.douban.DoubanBook;
import com.lza.pad.db.model.pad.PadResource;
import com.lza.pad.db.model.pad.PadResourceDetail;
import com.lza.pad.event.model.ResponseEventInfo;
import com.lza.pad.fragment.base.BaseFragment;
import com.lza.pad.helper.CommonRequestListener;
import com.lza.pad.helper.JsonParseHelper;
import com.lza.pad.helper.UrlHelper;
import com.lza.pad.support.network.VolleySingleton;
import com.lza.pad.widget.DefaultEbookCover;
import com.lza.pad.widget.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/18/15.
 */
public class EbookContentFragment extends BaseFragment {

    DefaultEbookCover mEbookCover;
    TextView mTxtTitle, mTxtAuthor, mTxtPubdate, mTxtPress, mTxtIsbn,
        mTxtTotalPages, mTxtBinding, mTxtPrice;
    PagerSlidingTabStrip mPagerTab;
    ViewPager mViewPager;

    ArrayList<String> mTitles = new ArrayList<String>();
    ArrayList<View> mViews = new ArrayList<View>();
    ArrayList<Fragment> mFragments = new ArrayList<Fragment>();

    public static final int INDEX_OVERVIEW = 0;
    public static final int INDEX_ABSTRACT = 1;
    public static final int INDEX_CONTENTS = 2;
    public static final int INDEX_COMMENTS = 3;
    public static final int INDEX_COLLECTIONS = 4;

    LayoutInflater mInflater;
    ImageLoader mImgLoader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInflater = LayoutInflater.from(mActivity);
        mTitles.add("概要");
        mTitles.add("简介");
        mTitles.add("目录");
        mTitles.add("豆瓣书评");
        mTitles.add("馆藏");
        mViews.add(createView());
        mViews.add(createView());
        mViews.add(createView());
        mViews.add(createView());
        mViews.add(createView());

        mImgLoader = VolleySingleton.getInstance(mActivity).getImageLoader(TEMP_IMAGE_LOADER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ebook_content2, container, false);

        mEbookCover = (DefaultEbookCover) view.findViewById(R.id.ebook_content_book_cover);
        mTxtTitle = (TextView) view.findViewById(R.id.ebook_content_title);
        mTxtAuthor = (TextView) view.findViewById(R.id.ebook_content_author);
        mTxtPubdate = (TextView) view.findViewById(R.id.ebook_content_pubdate);
        mTxtPress = (TextView) view.findViewById(R.id.ebook_content_press);
        mTxtIsbn = (TextView) view.findViewById(R.id.ebook_content_isbn);
        mTxtTotalPages = (TextView) view.findViewById(R.id.ebook_content_total_pages);
        mTxtBinding = (TextView) view.findViewById(R.id.ebook_content_binding);
        mTxtPrice = (TextView) view.findViewById(R.id.ebook_content_price);

        mPagerTab = (PagerSlidingTabStrip) view.findViewById(R.id.ebook_content_pager_tab);
        mPagerTab.setTextSize(getResources().getDimensionPixelSize(R.dimen.ebook_content_pager_tab_text_size));
        mViewPager = (ViewPager) view.findViewById(R.id.ebook_content_view_pager);

        mViewPager.setAdapter(new EbookContentAdapter());
        mPagerTab.setViewPager(mViewPager);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //先向豆瓣获取，如果没有数据，则向服务器请求数据
        String doubanUrl = UrlHelper.createDoubanBookByIsbnUrl(mPadResource);
        send(doubanUrl, mDoubanListener);
        //String url = UrlHelper.getResourceDetailUrl(mPadDeviceInfo, mPadResource);
        //send(url, mListener);
    }

    /**
     * 不通过豆瓣显示图书信息
     */
    private void showBookFromPadResource() {
        mTxtTitle.setText(mPadResource.getTitle());
        mTxtAuthor.setText(mPadResource.getAuthor());
        mTxtPress.setText(mPadResource.getPress());
        mTxtPubdate.setText(mPadResource.getPubdate());
        mTxtIsbn.setText(mPadResource.getIsbn());
    }

    private void requestCoverFromPadResource() {
        //显示封面
        String imgUrl = mPadResource.getIco();
        if (!TextUtils.isEmpty(imgUrl)) {
            mImgLoader.get(imgUrl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    if (imageContainer == null) return;
                    Bitmap bm = imageContainer.getBitmap();
                    if (bm == null) return;
                    mEbookCover.setDrawable(new BitmapDrawable(getResources(), bm));
                    mEbookCover.setCoverTitle("");
                    mEbookCover.setCoverAuthor("");
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }
    }

    private void showBookFromDouban(DoubanBook book) {
        String title = book.getTitle();
        String author = "";
        if (book.getAuthor() != null && book.getAuthor().length > 0)
            author = book.getAuthor()[0];
        String publisher = book.getPublisher();
        String pubdate = book.getPubdate();
        String isbn = book.getIsbn13();
        String price = book.getPrice();
        String binding = book.getBinding();
        String pages = book.getPages();

        if (TextUtils.isEmpty(title))
            title = mPadResource.getTitle();
        if (TextUtils.isEmpty(author))
            author = mPadResource.getAuthor();
        if (TextUtils.isEmpty(publisher))
            publisher = mPadResource.getPress();
        if (TextUtils.isEmpty(isbn))
            isbn = mPadResource.getIsbn();
        if (TextUtils.isEmpty(pubdate))
            pubdate = mPadResource.getPubdate();

        mTxtTitle.setText(wrap(title));
        mTxtAuthor.setText(wrap(author));
        mTxtPress.setText(wrap(publisher));
        mTxtPubdate.setText(wrap(pubdate));
        mTxtIsbn.setText(wrap(isbn));
        mTxtPrice.setText(wrap(price));
        mTxtBinding.setText(wrap(binding));
        mTxtTotalPages.setText(wrap(pages));

        /*DoubanBookImage imgs = book.getImages();
        String imgUrl = imgs.getLarge();
        if (TextUtils.isEmpty(imgUrl)) imgUrl = imgs.getMedium();*/
        String imgUrl = book.getImageUrl();
        if (TextUtils.isEmpty(imgUrl)) requestCoverFromPadResource();
        else requestCoverFromDouban(imgUrl);
    }

    private void requestCoverFromDouban(String url) {
        mImgLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                if (imageContainer == null) return;
                if (imageContainer.getBitmap() == null) return;
                Bitmap bm = imageContainer.getBitmap();
                BitmapDrawable drawable = new BitmapDrawable(getResources(), bm);
                mEbookCover.setDrawable(drawable);
                mEbookCover.setCoverTitle("");
                mEbookCover.setCoverAuthor("");
                mEbookCover.postInvalidate();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                requestCoverFromPadResource();
            }
        });
    }

    private CommonRequestListener<DoubanBook> mDoubanListener = new CommonRequestListener<DoubanBook>() {

        @Override
        public boolean handlerJson(String json) {
            DoubanBook book = JsonParseHelper.parseDoubanBook(json);
            if (book == null) {
                showBookFromPadResource();
                return true;
            }
            showBookFromDouban(book);
            return true;
        }

        @Override
        public void handleRespone(VolleyError error) {
            showBookFromPadResource();
        }

        @Override
        public void handleUnknownRespone(ResponseEventInfo response) {
            showBookFromPadResource();
        }
    };

    private CommonRequestListener<PadResource> mListener = new CommonRequestListener<PadResource>() {
        @Override
        public ResponseData<PadResource> parseJson(String json) {
            return JsonParseHelper.parseResourceResponse(json);
        }

        @Override
        public void handleRespone(List<PadResource> content) {
            if (content != null) {
                PadResource resource = content.get(0);
                List<PadResourceDetail> resouceDetail = resource.getMr();
                Collections.sort(resouceDetail, new SortById());
                if (resouceDetail != null) {
                    View view = mViews.get(INDEX_CONTENTS);
                    TextView text = (TextView) view.findViewById(R.id.ebook_content_text);
                    for (int i = 0; i < resouceDetail.size(); i++) {
                        Spanned sp = Html.fromHtml(resouceDetail.get(i).getTitle());
                        text.append(sp);
                        text.append("\n");
                    }
                }
            }
        }

        class SortById implements Comparator<PadResourceDetail> {

            @Override
            public int compare(PadResourceDetail o1, PadResourceDetail o2) {
                try {
                    int id1 = Integer.parseInt(o1.getId());
                    int id2 = Integer.parseInt(o2.getId());
                    if (id1 < id2) {
                        return -1;
                    } else if (id1 == id2) {
                        return 0;
                    } else {
                        return 1;
                    }
                } catch (Exception ex) {

                }
                return o1.getId().compareTo(o2.getId());
            }
        }
    };

    private View createView() {
        return mInflater.inflate(R.layout.ebook_content2_item, null);
    }

    private class EbookContentAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mTitles.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
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

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }
    }

    private String wrap(String value) {
        return TextUtils.isEmpty(value) ? "-" : value;
    }
}
