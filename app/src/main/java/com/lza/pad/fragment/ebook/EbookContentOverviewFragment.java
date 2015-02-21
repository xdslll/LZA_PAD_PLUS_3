package com.lza.pad.fragment.ebook;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.lza.pad.R;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.douban.DoubanAuthor;
import com.lza.pad.db.model.douban.DoubanBook;
import com.lza.pad.db.model.douban.DoubanRating;
import com.lza.pad.db.model.douban.DoubanReview;
import com.lza.pad.db.model.pad.PadResource;
import com.lza.pad.db.model.pad.PadResourceDetail;
import com.lza.pad.fragment.base.BaseFragment;
import com.lza.pad.helper.CommonRequestListener;
import com.lza.pad.helper.DoubanRequestListener;
import com.lza.pad.helper.JsonParseHelper;
import com.lza.pad.helper.UrlHelper;
import com.lza.pad.support.network.VolleySingleton;
import com.lza.pad.widget.ListViewForScrollView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 2/17/15.
 */
public class EbookContentOverviewFragment extends BaseFragment {

    TextView mTxtAbstract, mTxtContent, mTxtAbstractMore, mTxtContentMore,
            mTxtDoubanReviewsTitle, mTxtDoubanReviewsCount, mTxtDoubanReviewsAll,
            mTxtAbstractTitle, mTxtContentTitle;
    ListViewForScrollView mListDoubanReviews;
    ScrollView mScrollView;

    DoubanBook mDoubanBook;

    private static final int DEFALUT_MAX_LINES = 5;

    ArrayAdapter<DoubanReview> mDoubanReviewsAdapter = null;
    List<DoubanReview> mDoubanReviewsData;

    String mBookSummary, mBookCatalog;

    int mClickTag;

    public static final int SHOW_BOOK_SUMMARY = 1;
    public static final int SHOW_BOOK_CATALOG = 2;
    public static final int SHOW_BOOK_REVIEWS = 3;
    public static final int SHOW_BOOK_REVIEWS_DETAIL = 4;

    /**
     * 默认展开豆瓣导航栏
     */
    private boolean mIsDoubanBannerExpand = true;

    /**
     * 默认从第一条评论开始获取
     */
    String DEFAULT_DOUBAN_REVIEWS_START = "0";

    /**
     * 默认每次获取评论的数量
     */
    String DEFAULT_DOUBAN_REVIEWS_COUNT = "10";

    /**
     * 默认显示几条评论
     */
    int DEFAULT_MAX_DOUBAN_REVIEWS_SHOW_SIZE = 10;

    /**
     * 临时存放评论数据
     */
    private DoubanReview mTempDoubanReview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mDoubanBook = getArguments().getParcelable(KEY_DOUBAN_BOOK);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ebook_content_overview2, container, false);
        mTxtAbstract = (TextView) view.findViewById(R.id.ebook_content_overview_abstract);
        mTxtAbstractTitle = (TextView) view.findViewById(R.id.ebook_content_overview_abstract_title);
        mTxtAbstractMore = (TextView) view.findViewById(R.id.ebook_content_overview_abstract_more);
        mTxtContent = (TextView) view.findViewById(R.id.ebook_content_overview_content);
        mTxtContentTitle = (TextView) view.findViewById(R.id.ebook_content_overview_content_title);
        mTxtContentMore = (TextView) view.findViewById(R.id.ebook_content_overview_content_more);
        mTxtDoubanReviewsTitle = (TextView) view.findViewById(R.id.ebook_content_overview_douban_reviews_title);
        mTxtDoubanReviewsCount = (TextView) view.findViewById(R.id.ebook_content_overview_douban_reviews_count);
        mTxtDoubanReviewsAll = (TextView) view.findViewById(R.id.ebook_content_overview_douban_reviews_all);
        mListDoubanReviews = (ListViewForScrollView) view.findViewById(R.id.ebook_content_overview_douban_reviews);
        mScrollView = (ScrollView) view.findViewById(R.id.ebook_content_overview_scroll);

        mTxtAbstractMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickTag = SHOW_BOOK_SUMMARY;
                EventBus.getDefault().post(EbookContentOverviewFragment.this);
            }
        });

        mTxtContentMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickTag = SHOW_BOOK_CATALOG;
                EventBus.getDefault().post(EbookContentOverviewFragment.this);
            }
        });

        mTxtDoubanReviewsAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickTag = SHOW_BOOK_REVIEWS;
                EventBus.getDefault().post(EbookContentOverviewFragment.this);
            }
        });

        //点都豆瓣评论导航栏
        mTxtDoubanReviewsTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Drawable drawable;
                if (mIsDoubanBannerExpand) {
                    mIsDoubanBannerExpand = false;
                    drawable = getResources().getDrawable(R.drawable.about_item_arrow);
                    mListDoubanReviews.setVisibility(View.GONE);
                } else {
                    mIsDoubanBannerExpand = true;
                    drawable = getResources().getDrawable(R.drawable.about_item_arrow_down);
                    mListDoubanReviews.setVisibility(View.VISIBLE);
                }
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                mTxtDoubanReviewsTitle.setCompoundDrawables(null, null, drawable, null);*/
            }
        });

        mTxtAbstractMore.requestFocus();
        mListDoubanReviews.setFocusable(false);
        mListDoubanReviews.setFocusableInTouchMode(false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //填充简介和目录数据
        if (mDoubanBook != null) {
            mBookSummary = mDoubanBook.getSummary();
            mBookCatalog = mDoubanBook.getCatalog();
            mTxtAbstract.setText(mBookSummary);
            mTxtContent.setText(mBookCatalog);
            if (TextUtils.isEmpty(mBookSummary) || TextUtils.isEmpty(mBookCatalog))
                requestFromPadResource();
        } else {
            requestFromPadResource();
        }
        //填充豆瓣书评
        requestDoubanReviews();
    }

    private void requestDoubanReviews() {
        String doubanReviewsUrl = UrlHelper.createDoubanReviewsByIsbnUrl(mPadResource, DEFAULT_DOUBAN_REVIEWS_START, DEFAULT_DOUBAN_REVIEWS_COUNT);
        send(doubanReviewsUrl, mDoubanReviewListener);
    }

    private DoubanRequestListener<DoubanBook> mDoubanReviewListener = new DoubanRequestListener<DoubanBook>() {
        @Override
        public DoubanBook parseJson(String json) {
            return JsonParseHelper.parseDoubanBook(json);
        }

        @Override
        public void handleRespone(DoubanBook book) {
            List<DoubanReview> reviews = book.getReviews();
            if (reviews == null || reviews.size() <= 0) return;
            mDoubanReviewsData = reviews;
            int reviewCount = Integer.parseInt(book.getCount());
            int reviewTotal = book.getTotal();
            mTxtDoubanReviewsCount.setText("（" + reviewTotal + "条）");
            if (reviewTotal > DEFAULT_MAX_DOUBAN_REVIEWS_SHOW_SIZE) {
                mTxtDoubanReviewsAll.setVisibility(View.VISIBLE);
            } else {
                mTxtDoubanReviewsAll.setVisibility(View.GONE);
            }
            mDoubanReviewsAdapter = createAdapter(true);
            mListDoubanReviews.setAdapter(mDoubanReviewsAdapter);
        }
    };

    private void requestFromPadResource() {
        String url = UrlHelper.getResourceDetailUrl(mPadDeviceInfo, mPadResource);
        send(url, mPadResourceListener);
    }

    private CommonRequestListener<PadResource> mPadResourceListener = new CommonRequestListener<PadResource>() {
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
            if (TextUtils.isEmpty(mBookSummary)) {
                mBookSummary = resource.getContents();
                mTxtAbstract.setText(mBookSummary);
            }
            if (TextUtils.isEmpty(mBookCatalog)) {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < resouceDetail.size(); i++) {
                    Spanned sp = Html.fromHtml(resouceDetail.get(i).getTitle());
                    builder.append(sp);
                    builder.append("\n");
                }
                mBookCatalog = builder.toString();
                mTxtContent.setText(mBookCatalog);
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

    /**
     * 创建豆瓣评论Adapter
     *
     * @param ifHasMax 是否有最大行数限制
     * @return
     */
    private ArrayAdapter<DoubanReview> createAdapter(final boolean ifHasMax) {
        return new ArrayAdapter<DoubanReview>(
                mActivity, 0, mDoubanReviewsData) {

            @Override
            public int getCount() {
                if (ifHasMax) {
                    int size = mDoubanReviewsData.size();
                    if (size > DEFAULT_MAX_DOUBAN_REVIEWS_SHOW_SIZE) {
                        return DEFAULT_MAX_DOUBAN_REVIEWS_SHOW_SIZE;
                    } else {
                        return size;
                    }
                } else {
                    return mDoubanReviewsData.size();
                }
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                final DoubanReview review = mDoubanReviewsData.get(position);
                ReviewViewHolder holder;
                if (convertView == null) {
                    holder = new ReviewViewHolder();
                    LayoutInflater inflater = LayoutInflater.from(mActivity);
                    convertView = inflater.inflate(R.layout.ebook_content_douban_reviews, null);
                    holder.mImgAvatar = (NetworkImageView) convertView.findViewById(R.id.opac_book_detail_review_avatar);
                    holder.mTxtReviewTitle = (TextView) convertView.findViewById(R.id.opac_book_detail_review_title);
                    holder.mTxtUsername = (TextView) convertView.findViewById(R.id.opac_book_detail_review_username);
                    holder.mTxtReviewContent = (TextView) convertView.findViewById(R.id.opac_book_detail_review_content);
                    holder.mTxtReviewDate = (TextView) convertView.findViewById(R.id.opac_book_detail_review_date);
                    holder.mTxtReviewLike = (TextView) convertView.findViewById(R.id.opac_book_detail_review_like);
                    holder.mRabReview = (RatingBar) convertView.findViewById(R.id.opac_book_detail_review_rating);
                    holder.mLayout = (LinearLayout) convertView.findViewById(R.id.opac_book_detail_review_layout);
                    convertView.setTag(holder);
                } else {
                    holder = (ReviewViewHolder) convertView.getTag();
                }

                holder.mLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onReviewListItemClick(review);
                    }
                });
                //设置头像和用户名
                holder.mImgAvatar.setDefaultImageResId(R.drawable.ic_avatar);
                holder.mImgAvatar.setErrorImageResId(R.drawable.ic_avatar);
                DoubanAuthor author = review.getAuthor();
                if (author != null) {
                    String avatarUrl = author.getAvatar();
                    ImageLoader imageLoader = VolleySingleton.getInstance(mActivity).getImageLoader(TEMP_IMAGE_LOADER);
                    holder.mImgAvatar.setImageUrl(avatarUrl, imageLoader);
                    holder.mTxtUsername.setText(author.getName());
                }

                holder.mTxtReviewTitle.setText(review.getTitle());
                holder.mTxtReviewContent.setText(review.getSummary());
                holder.mTxtReviewDate.setText(review.getUpdated());
                holder.mTxtReviewLike.setText(review.getVotes());

                DoubanRating rating = review.getRating();
                if (rating != null) {
                    int max = rating.getMax();
                    float value = Float.parseFloat(rating.getValue());
                    float ratingValue = value * MAX_DOUBAN_RATING / max;
                    holder.mRabReview.setRating(ratingValue);
                }

                return convertView;
            }
        };
    }

    private class ReviewViewHolder {
        NetworkImageView mImgAvatar;
        TextView mTxtReviewTitle, mTxtUsername, mTxtReviewContent, mTxtReviewDate, mTxtReviewLike;
        RatingBar mRabReview;
        LinearLayout mLayout;
    }

    /**
     * 点击书评列表时触发事件，跳转到书评的详细页
     *
     * @param data
     */
    private void onReviewListItemClick(DoubanReview data) {
        /*Bundle arg = createArguments();
        arg.putParcelable(KEY_DOUBAN_REVIEW, data);
        OpacBookDetailReview dialogFragment = new OpacBookDetailReview();
        dialogFragment.setArguments(arg);
        dialogFragment.setStyle(R.style.FullScreenDialog, R.style.FullScreenDialog);
        dialogFragment.show(mFm, null);*/
        //mClickTag = SHOW_BOOK_REVIEWS_DETAIL;
        //mTempDoubanReview = data;
        //EventBus.getDefault().post(EbookContentOverviewFragment.this);
    }

    public String getBookAbstract() {
        return mBookSummary;
    }

    public String getBookCatalog() {
        return mBookCatalog;
    }

    public int getClickTag() {
        return mClickTag;
    }

    public DoubanReview getTempDoubanReview() {
        return mTempDoubanReview;
    }
}
