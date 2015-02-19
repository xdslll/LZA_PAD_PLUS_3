package com.lza.pad.fragment.ebook;

import android.app.Fragment;
import android.app.FragmentTransaction;
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
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.lza.pad.R;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.douban.DoubanBook;
import com.lza.pad.db.model.douban.DoubanRating;
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
        mTxtTotalPages, mTxtBinding, mTxtPrice, mTxtRatingNum, mTxtDoubanRatingAvg;
    RatingBar mRatingBarDouban;
    PagerSlidingTabStrip mPagerTab;
    ViewPager mViewPager;

    ArrayList<String> mTitles = new ArrayList<String>();
    ArrayList<View> mViews = new ArrayList<View>();

    public static final int INDEX_OVERVIEW = 0;
    public static final int INDEX_SUMMARY = 1;
    public static final int INDEX_CATALOG = 2;
    public static final int INDEX_REVIEWS = 3;
    public static final int INDEX_COLLECTIONS = 4;

    LayoutInflater mInflater;
    ImageLoader mImgLoader;

    EbookContentAdapter mEbookContentAdapter;

    DoubanBook mTempDoubanBook;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInflater = LayoutInflater.from(mActivity);

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
        mTxtRatingNum = (TextView) view.findViewById(R.id.ebook_content_rating_number);
        mTxtDoubanRatingAvg = (TextView) view.findViewById(R.id.ebook_content_rating_avg);
        mRatingBarDouban = (RatingBar) view.findViewById(R.id.ebook_content_rating);

        mPagerTab = (PagerSlidingTabStrip) view.findViewById(R.id.ebook_content_pager_tab);
        mPagerTab.setTextSize(getResources().getDimensionPixelSize(R.dimen.ebook_content_pager_tab_text_size));
        //mPagerTab.setVisibility(View.GONE);
        mViewPager = (ViewPager) view.findViewById(R.id.ebook_content_view_pager);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //先向豆瓣获取，如果没有数据，则向服务器请求数据
        String doubanUrl = UrlHelper.createDoubanBookByIsbnUrl(mPadResource);
        send(doubanUrl, mDoubanListener);
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

    /**
     * 不通过豆瓣请求封面图片
     */
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

    /**
     * 通过豆瓣数据显示图书
     *
     * @param book
     */
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

        //获取豆瓣评分
        DoubanRating rating = book.getRating();
        if (rating != null) {
            float averageRating = Float.parseFloat(rating.getAverage());
            int ratingNumber = rating.getNumRaters();
            int maxRate = rating.getMax();

            mTxtDoubanRatingAvg.setText("评分：" + averageRating);
            //以5星为标准，换算出新的评分
            averageRating = averageRating * MAX_DOUBAN_RATING / maxRate;

            mRatingBarDouban.setRating(averageRating);
            mTxtRatingNum.setText("（" + ratingNumber + "人评分）");
        }

        //获取封面
        String imgUrl = book.getImageUrl();
        if (TextUtils.isEmpty(imgUrl)) requestCoverFromPadResource();
        else requestCoverFromDouban(imgUrl);

        createViewPager(book);
    }

    /**
     * 通过豆瓣显示封面
     *
     * @param url
     */
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
            mTempDoubanBook = book;
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

    private void createViewPager(DoubanBook book) {
        //设置标题
        mTitles.add("概要");
        mTitles.add("简介");
        mTitles.add("目录");
        mTitles.add("书评");
        //设置View
        if (mViews != null)
            mViews.clear();
        mViews.add(createOverview(book));
        mViews.add(createSummaryOrCatalogView(INDEX_SUMMARY, book));
        mViews.add(createSummaryOrCatalogView(INDEX_CATALOG, book));
        mViews.add(createDoubanReviews(book));
        mEbookContentAdapter = new EbookContentAdapter();
        mViewPager.setAdapter(mEbookContentAdapter);

        mPagerTab.setViewPager(mViewPager);
    }

    private View createDoubanReviews(DoubanBook book) {
        return mInflater.inflate(R.layout.ebook_content2_item, null);
    }

    private View createSummaryOrCatalogView(int index, DoubanBook book) {
        View view = mInflater.inflate(R.layout.ebook_content_summary, null);
        TextView txtSummary = (TextView) view.findViewById(R.id.ebook_content_summary_text);
        if (index == EbookContentFragment.INDEX_SUMMARY) {
            requestEbookSummary(book, txtSummary);
        } else  if (index == EbookContentFragment.INDEX_CATALOG) {
            requestEbookCatalog(book, txtSummary);
        }
        return view;
    }

    private void requestEbookCatalog(DoubanBook book, TextView text) {
        if (book != null) {
            String bookCatalog = book.getCatalog();
            text.setText(bookCatalog);
            if (TextUtils.isEmpty(bookCatalog)) {
                requestCatalogFromPadResource(text);
            }
        } else {
            requestCatalogFromPadResource(text);
        }
    }

    private void requestEbookSummary(DoubanBook book, TextView text) {
        if (book != null) {
            String bookSummary = book.getSummary();
            text.setText(bookSummary);
            if (TextUtils.isEmpty(bookSummary)) {
                requestSummaryFromPadResource(text);
            }
        } else {
            requestSummaryFromPadResource(text);
        }
    }

    private void requestSummaryFromPadResource(TextView text) {
        if (mPadResource == null) return;
        String bookSummary = mPadResource.getContents();
        if (TextUtils.isEmpty(bookSummary)) return;
        text.setText(bookSummary);
    }

    private void requestCatalogFromPadResource(TextView text) {
        String url = UrlHelper.getResourceDetailUrl(mPadDeviceInfo, mPadResource);
        send(url, new RequestListener(text));
    }

    private class RequestListener extends CommonRequestListener<PadResource> {

        TextView text;
        public RequestListener(TextView text) {
            this.text = text;
        }

        @Override
        public ResponseData<PadResource> parseJson(String json) {
            return JsonParseHelper.parseResourceResponse(json);
        }

        @Override
        public void handleRespone(List<PadResource> content) {
            if (content == null || content.size() == 0) return;
            PadResource resource = content.get(0);
            if (resource == null) return;
            List<PadResourceDetail> resouceDetail = resource.getMr();
            if (resouceDetail == null) return;
            Collections.sort(resouceDetail, new SortById());
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < resouceDetail.size(); i++) {
                Spanned sp = Html.fromHtml(resouceDetail.get(i).getTitle());
                builder.append(sp);
                builder.append("\n");
            }
            String bookCatalog = builder.toString();
            text.setText(bookCatalog);
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

    private View createOverview(DoubanBook book) {
        View view = mInflater.inflate(R.layout.ebook_content2_item, null);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment frg = new EbookContentOverviewFragment();
        Bundle arg = createArgument(book);
        frg.setArguments(arg);
        ft.replace(R.id.ebook_content2_container, frg);
        ft.commit();
        return view;
    }

    private class EbookContentAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mViews.size();
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

    private Bundle createArgument(DoubanBook book) {
        Bundle arg = new Bundle();
        arg.putParcelable(KEY_PAD_DEVICE_INFO, mPadDeviceInfo);
        arg.putParcelable(KEY_PAD_RESOURCE_INFO, mPadResource);
        arg.putParcelable(KEY_PAD_CONTROL_INFO, mPadControlInfo);
        arg.putParcelable(KEY_DOUBAN_BOOK, book);
        return arg;
    }

    public void onEventMainThread(EbookContentOverviewFragment instance) {
        int tag = instance.getClickTag();
        if (tag == EbookContentOverviewFragment.SHOW_BOOK_SUMMARY) {
            mViewPager.setCurrentItem(INDEX_SUMMARY);
        } else if (tag == EbookContentOverviewFragment.SHOW_BOOK_CATALOG) {
            mViewPager.setCurrentItem(INDEX_CATALOG);
        } else if (tag == EbookContentOverviewFragment.SHOW_BOOK_REVIEWS) {
            mViewPager.setCurrentItem(INDEX_REVIEWS);
        } else  if (tag == EbookContentOverviewFragment.SHOW_BOOK_REVIEWS_DETAIL) {

        }
    }
}
