package com.lza.pad.app2.ui.module.content.ebook;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.app.socket.admin.file.MinaFileServerAdmin;
import com.lza.pad.app.socket.admin.server.MinaServerHelper;
import com.lza.pad.app.socket.model.MinaClient;
import com.lza.pad.app.socket.model.MinaServer;
import com.lza.pad.app2.event.base.OnClickListener;
import com.lza.pad.app2.ui.widget.base.BaseImageFragment;
import com.lza.pad.db.model.DownloadFile;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.douban.DoubanBook;
import com.lza.pad.db.model.douban.DoubanRating;
import com.lza.pad.db.model.pad.PadDeviceParam;
import com.lza.pad.db.model.pad.PadParam;
import com.lza.pad.db.model.pad.PadResource;
import com.lza.pad.db.model.pad.PadResourceDetail;
import com.lza.pad.helper._DownloadHelper;
import com.lza.pad.helper.JsonParseHelper;
import com.lza.pad.helper.SimpleRequestListener;
import com.lza.pad.helper.UrlHelper;
import com.lza.pad.support.file.FileTools;
import com.lza.pad.support.utils.Consts;
import com.lza.pad.support.utils.Utility;
import com.lza.pad.widget.DefaultEbookCover;
import com.lza.pad.widget.PagerSlidingTabStrip;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/3/19.
 */
public class EbookContentFragment extends BaseImageFragment {

    DefaultEbookCover mEbookCover;
    TextView mTxtTitle, mTxtAuthor, mTxtPubdate, mTxtPress, mTxtIsbn, mTxtTotalPages,
            mTxtBinding, mTxtPrice, mTxtRatingNum, mTxtDoubanRatingAvg, mTxtGetClient;
    RatingBar mRatingBarDouban;
    PagerSlidingTabStrip mPagerTab;
    ViewPager mViewPager;
    ViewStub mViewStubLoading;
    LinearLayout mLayoutLoading;
    TextView mTxtLoading;

    LinearLayout mLayoutQCode;
    ImageView mImgQCode;

    ArrayList<String> mTitles = new ArrayList<String>();
    ArrayList<View> mViews = new ArrayList<View>();

    public static final int INDEX_OVERVIEW = 0;
    public static final int INDEX_SUMMARY = 1;
    public static final int INDEX_CATALOG = 2;
    public static final int INDEX_REVIEWS = 3;
    public static final int INDEX_COLLECTIONS = 4;

    EbookContentAdapter mEbookContentAdapter;

    DoubanBook mTempDoubanBook;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        mViewPager = (ViewPager) view.findViewById(R.id.ebook_content_view_pager);

        mViewStubLoading = (ViewStub) view.findViewById(R.id.ebook_content_viewstub_loading);
        if (mLayoutLoading == null) {
            mViewStubLoading.inflate();
            mLayoutLoading = (LinearLayout) view.findViewById(R.id.common_loading_layout);
            mTxtLoading = (TextView) view.findViewById(R.id.common_loading_layout_text);
        } else {
            mLayoutLoading.setVisibility(View.VISIBLE);
        }

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
        if (mPadResource != null) {
            String sourceType = mPadResource.getSource_type();
            if (sourceType.equals(PadResource.RESOURCE_EBOOK)) {
                showQCode();
            } else {
                mLayoutQCode.setVisibility(View.GONE);
            }
        } else {
            mLayoutQCode.setVisibility(View.GONE);
        }
        registerEventBus();
        String paramUrl = UrlHelper.getDeviceParam(mPadDeviceInfo);
        send(paramUrl, mDeviceParamListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterEventBus();
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
                /*
                String sourceType = mPadResource.getSource_type();
                String title = mPadResource.getTitle();
                String author = mPadResource.getAuthor();
                String pubdate = mPadResource.getPubdate();
                String press = mPadResource.getPress();
                String isbn = mPadResource.getIsbn();
                String abs = mPadResource.getContents();
                String img = mPadResource.getIco();

                StringBuilder sb = new StringBuilder();
                sb.append("---").append(sourceType)
                        .append("---").append(title)
                        .append("---").append(author)
                        .append("---").append(pubdate)
                        .append("---").append(press)
                        .append("---").append(isbn)
                        .append("---").append("暂无简介")
                        .append("---").append(img)
                        .append("---").append(url);

                String urlEncode = Base64.encodeToString(sb.toString().getBytes(), Base64.DEFAULT);
                Bitmap qcodeImage = Utility.createQCode(urlEncode, mImgQCode.getWidth(), mImgQCode.getHeight());
                mImgQCode.setImageBitmap(qcodeImage);*/
            } catch (Exception ex) {
                mLayoutQCode.setVisibility(View.GONE);
            }
        }
    }

    private SimpleRequestListener<PadDeviceParam> mDeviceParamListener = new SimpleRequestListener<PadDeviceParam>() {

        @Override
        public ResponseData<PadDeviceParam> parseJson(String json) {
            return JsonParseHelper.parsePadDeviceParamResponse(json);
        }

        @Override
        public void handleRespone(List<PadDeviceParam> content) {
            for (PadDeviceParam deviceParam : content) {
                PadParam param = pickFirst(deviceParam.getParam_id());
                if (param.getKey().equals(PadParam.KEY_HAS_DOUBAN)) {
                    String value = param.getValue();
                    if (value.equals(PadParam.VALUE_HAS_DOUBAN)) {
                        requestFromDouban();
                    } else if (value.equals(PadParam.VALUE_NOT_HAS_DOUBAN)) {
                        showBookFromPadResource();
                    }
                } else {
                    showBookFromPadResource();
                }
            }
        }

        @Override
        public void handleResponseFailed() {
            showBookFromPadResource();
        }
    };

    private void requestFromDouban() {
        //先向豆瓣获取，如果没有数据，则向我们的服务器请求数据
        String doubanUrl = UrlHelper.createDoubanBookByIsbnUrl(mPadResource);
        send(doubanUrl, mDoubanListener);
    }

    private SimpleRequestListener<DoubanBook> mDoubanListener = new SimpleRequestListener<DoubanBook>() {

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
        public void handleResponseFailed() {
            showBookFromPadResource();
        }
    };

    private void createViewPager(DoubanBook book) {
        mLayoutLoading.setVisibility(View.GONE);
        //设置标题
        mTitles.add("概要");
        mTitles.add("简介");
        mTitles.add("目录");
        //mTitles.add("书评");
        //设置View
        if (mViews != null)
            mViews.clear();
        mViews.add(createOverview(book));
        mViews.add(createSummaryOrCatalogView(INDEX_SUMMARY, book));
        mViews.add(createSummaryOrCatalogView(INDEX_CATALOG, book));
        //mViews.add(createDoubanReviews(book));
        mEbookContentAdapter = new EbookContentAdapter();
        mViewPager.setAdapter(mEbookContentAdapter);
        mPagerTab.setViewPager(mViewPager);
    }

    private void createViewPager() {
        mLayoutLoading.setVisibility(View.GONE);
        //设置标题
        mTitles.add("简介");
        mTitles.add("目录");
        //设置View
        if (mViews != null)
            mViews.clear();
        mViews.add(createSummaryOrCatalogView(INDEX_SUMMARY));
        mViews.add(createSummaryOrCatalogView(INDEX_CATALOG));
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
        if (index == INDEX_SUMMARY) {
            requestEbookSummary(book, txtSummary);
        } else  if (index == INDEX_CATALOG) {
            requestEbookCatalog(book, txtSummary);
        }
        return view;
    }

    private View createSummaryOrCatalogView(int index) {
        View view = mInflater.inflate(R.layout.ebook_content_summary, null);
        TextView txtSummary = (TextView) view.findViewById(R.id.ebook_content_summary_text);
        if (index == INDEX_SUMMARY) {
            requestEbookSummary(null, txtSummary);
        } else  if (index == INDEX_CATALOG) {
            requestEbookCatalog(null, txtSummary);
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

    private class RequestListener extends SimpleRequestListener<PadResource> {

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
            if (resouceDetail == null || resouceDetail.size() <= 0) return;
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
                    return 1;
                }
            }
        }
    };

    private View createOverview(DoubanBook book) {
        View view = mInflater.inflate(R.layout.ebook_content2_item, null);
        if (getFragmentManager() == null) return null;
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

    private Bundle createArgument(DoubanBook book) {
        Bundle arg = new Bundle();
        arg.putParcelable(KEY_PAD_DEVICE_INFO, mPadDeviceInfo);
        arg.putParcelable(KEY_PAD_RESOURCE_INFO, mPadResource);
        arg.putParcelable(KEY_PAD_WIDGET, mPadModuleWidget);
        arg.putParcelable(KEY_DOUBAN_BOOK, book);
        return arg;
    }

    /**
     * 响应“概要”页面发起的广播
     *
     * @param instance
     */
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
        else requestCoverFromDouban(book, imgUrl);

        createViewPager(book);
    }

    /**
     * 不通过豆瓣显示图书信息
     */
    private void showBookFromPadResource() {
        createViewPager();
        mTxtTitle.setText(mPadResource.getTitle());
        mTxtAuthor.setText(mPadResource.getAuthor());
        mTxtPress.setText(mPadResource.getPress());
        mTxtPubdate.setText(mPadResource.getPubdate());
        mTxtIsbn.setText(mPadResource.getIsbn());
        mTxtPrice.setText("-");
        mTxtTotalPages.setText("-");
        mTxtBinding.setText("-");
        requestCoverFromPadResource();
    }

    /**
     * 通过豆瓣显示封面
     *
     * @param url
     */
    private void requestCoverFromDouban(final DoubanBook book, String url) {
        loadImage(url, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                try {
                    Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.default_ebook_cover);
                    mEbookCover.setDrawable(new BitmapDrawable(getResources(), bm));
                    mEbookCover.postInvalidate();
                } catch (Exception ex) {

                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                requestCoverFromPadResource();
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                try {
                    BitmapDrawable drawable = new BitmapDrawable(getResources(), loadedImage);
                    mEbookCover.setDrawable(drawable);
                    mEbookCover.setCoverTitle("");
                    mEbookCover.setCoverAuthor("");
                    mEbookCover.postInvalidate();
                } catch (Exception ex) {

                }
            }
        });
    }

    /**
     * 不通过豆瓣请求封面图片
     */
    private void requestCoverFromPadResource() {
        //显示封面
        String imgUrl = mPadResource.getIco();
        loadImage(imgUrl, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                try {
                    Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.default_ebook_cover);
                    mEbookCover.setDrawable(new BitmapDrawable(getResources(), bm));
                    mEbookCover.postInvalidate();
                } catch (Exception ex) {

                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                mEbookCover.setCoverTitle(mPadResource.getTitle());
                mEbookCover.setCoverAuthor(mPadResource.getAuthor());
                mEbookCover.postInvalidate();
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                try {
                    mEbookCover.setDrawable(new BitmapDrawable(getResources(), loadedImage));
                    mEbookCover.setCoverTitle("");
                    mEbookCover.setCoverAuthor("");
                    mEbookCover.postInvalidate();
                } catch (Exception ex) {

                }
            }
        });
    }

    private String wrap(String value) {
        return TextUtils.isEmpty(value) ? "-" : value;
    }

    /**
     * 响应“概要”页面发起的广播
     *
     * @param instance
     */
    public void onEventMainThread(com.lza.pad.fragment.ebook.EbookContentOverviewFragment instance) {
        int tag = instance.getClickTag();
        if (tag == com.lza.pad.fragment.ebook.EbookContentOverviewFragment.SHOW_BOOK_SUMMARY) {
            mViewPager.setCurrentItem(INDEX_SUMMARY);
        } else if (tag == com.lza.pad.fragment.ebook.EbookContentOverviewFragment.SHOW_BOOK_CATALOG) {
            mViewPager.setCurrentItem(INDEX_CATALOG);
        } else if (tag == com.lza.pad.fragment.ebook.EbookContentOverviewFragment.SHOW_BOOK_REVIEWS) {
            mViewPager.setCurrentItem(INDEX_REVIEWS);
        } else  if (tag == com.lza.pad.fragment.ebook.EbookContentOverviewFragment.SHOW_BOOK_REVIEWS_DETAIL) {

        }
    }

    /**
     * 响应MinaClient客户端发起的请求
     *
     * @param client
     */
    @Override
    public void onEventAsync(MinaClient client) {
        if (client.getAction().equals(MinaClient.ACTION_SHAKE)) {
            log("触发摇一摇！");
            //向客户端发起下载文件的申请，如果客户端同意才开始下载
            MinaServerHelper.instance().sendFile(client.getSession(), mPadResource);
        } else if (client.getAction().equals(MinaClient.ACTION_APPLY_FOR_DOWNLOAD_FILE)) {
            //客户端同意下载，开始下载并传输文件
            String downloadUrl = mPadResource.getFulltext();
            if (TextUtils.isEmpty(downloadUrl)) {
                //ToastUtils.showLong(mActivity, "下载地址为空！");
                MinaServerHelper.instance().sendFailed(client.getSession(),
                        MinaServer.ACTION_SEND_FILE_FAILED, "下载的电子书不存在，请联系管理员！");
            } else {
                String fileName = parseFileName(downloadUrl);
                File bookFile = FileTools.getCacheFile(Consts.CACHE_PATH + "/book/" + fileName);
                log(bookFile.getAbsolutePath());
                if (bookFile.exists()) {
                    log("文件已经存在，准备传给客户端！");
                    //设置服务端的下载文件路径，重要步骤
                    MinaFileServerAdmin.getInstance().setFilePath(bookFile.getAbsolutePath());
                    //告诉客户端下载完成，可以开始下载
                    MinaServerHelper.instance().sendFile(client.getSession(),
                            fileName, bookFile.length());
                } else {
                    _DownloadHelper.InternelDownloadFile downloadFile = new _DownloadHelper.InternelDownloadFile();
                    downloadFile.setFileName(fileName);
                    downloadFile.setFilePath(bookFile.getAbsolutePath());
                    downloadFile.setFileType(parseInt(mPadResource.getSource_type()));
                    _DownloadHelper helper = new _DownloadHelper(mActivity, downloadUrl, downloadFile);
                    helper.download();
                }
            }
            mClient = client;
        }
    }

    private MinaClient mClient;

    /**
     * 处理下载完成后的事件
     * @param downloadFile
     */
    @Override
    public void onEventAsync(DownloadFile downloadFile) {
        if (downloadFile != null) {
            String filePath = downloadFile.getFilePath();
            log("下载完成，下载路径为：" + filePath);
            //设置服务端的下载文件路径，重要步骤
            MinaFileServerAdmin.getInstance().setFilePath(filePath);
            File file = new File(filePath);
            //告诉客户端下载完成，可以开始下载
            MinaServerHelper.instance().sendFile(mClient.getSession(),
                    downloadFile.getFileName(), file.length());
        }
    }

    private String parseFileName(String url) {
        int index = url.lastIndexOf("/");
        String fileName = "";
        if (index > 0) {
            fileName = url.substring(index + 1, url.length());
        }
        return fileName;
    }
}
