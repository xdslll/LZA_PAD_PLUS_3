package com.lza.pad.app2.ui.module.content.ebook;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.app2.ui.widget.base.BaseFragment;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.douban.DoubanBook;
import com.lza.pad.db.model.pad.PadResource;
import com.lza.pad.db.model.pad.PadResourceDetail;
import com.lza.pad.fragment.ebook.EbookContentFragment;
import com.lza.pad.helper.JsonParseHelper;
import com.lza.pad.helper.SimpleRequestListener;
import com.lza.pad.helper.UrlHelper;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 2/18/15.
 */
public class EbookContentSummaryFragment extends BaseFragment {

    DoubanBook mDoubanBook;
    TextView mTxtSummary;

    int mIndex;

    String mBookSummary, mBookCatalog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mDoubanBook = getArguments().getParcelable(KEY_DOUBAN_BOOK);
            mIndex = getArguments().getInt(KEY_FRAGMENT_INDEX);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ebook_content_summary, container, false);
        mTxtSummary = (TextView) view.findViewById(R.id.ebook_content_summary_text);

        if (mIndex == com.lza.pad.fragment.ebook.EbookContentFragment.INDEX_SUMMARY) {
            requestEbookSummary();
        } else  if (mIndex == EbookContentFragment.INDEX_CATALOG) {
            requestEbookCatalog();
        }

        return view;
    }

    private void requestEbookCatalog() {
        if (mDoubanBook != null) {
            mBookCatalog = mDoubanBook.getCatalog();
            mTxtSummary.setText(mBookCatalog);
            if (TextUtils.isEmpty(mBookCatalog)) {
                requestCatalogFromPadResource();
            }
        } else {
            requestCatalogFromPadResource();
        }
    }

    private void requestEbookSummary() {
        if (mDoubanBook != null) {
            mBookSummary = mDoubanBook.getSummary();
            mTxtSummary.setText(mBookSummary);
            if (TextUtils.isEmpty(mBookSummary)) {
                requestSummaryFromPadResource();
            }
        } else {
            requestSummaryFromPadResource();
        }
    }

    private void requestSummaryFromPadResource() {
        if (mPadResource == null) return;
        mBookSummary = mPadResource.getContents();
        if (TextUtils.isEmpty(mBookSummary)) return;
        mTxtSummary.setText(mBookSummary);
    }

    private void requestCatalogFromPadResource() {
        String url = UrlHelper.getResourceDetailUrl(mPadDeviceInfo, mPadResource);
        send(url, mPadResourceListener);
    }

    private SimpleRequestListener<PadResource> mPadResourceListener = new SimpleRequestListener<PadResource>() {
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
            /*if (TextUtils.isEmpty(mBookSummary)) {
                mBookSummary = resource.getContents();
                mTxtSummary.setText(mBookSummary);
            }*/
            if (TextUtils.isEmpty(mBookCatalog)) {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < resouceDetail.size(); i++) {
                    Spanned sp = Html.fromHtml(resouceDetail.get(i).getTitle());
                    builder.append(sp);
                    builder.append("\n");
                }
                mBookCatalog = builder.toString();
                mTxtSummary.setText(mBookCatalog);
            } else {
                mTxtSummary.setText(mBookCatalog);
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


}
