package com.lza.pad.app2.ui.widget;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.app2.ui.widget.base.BaseImageFragment;
import com.lza.pad.db.model.old.OldBook;
import com.lza.pad.db.model.old.OldResponse;
import com.lza.pad.db.model.old.PingLun;
import com.lza.pad.db.model.old.SearchBookDetail;
import com.lza.pad.helper.JsonParseHelper;
import com.lza.pad.helper.SimpleRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/8/13.
 */
public class LibrarySearchFragment extends BaseImageFragment {

    ListView mListResult;
    EditText mEdtSearchText;
    ImageButton mBtnSearch;
    Button mBtnPrev, mBtnNext;
    TextView mTxtTotalPage;

    View mViewEmptyList;

    String mRequestListUrl, mRequestDetailUrl;

    int mCurrentPage = 1;
    int mTotalPage = 0;

    List<OldBook> mOldBooks = new ArrayList<OldBook>();

    OldBookAdapter mAdapter;

    int mSchoolId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mPadWidgetData != null && !isEmpty(mPadWidgetData.getUrl())) {
            String[] urls = mPadWidgetData.getUrl().split("\\|\\|\\|\\|");
            mRequestListUrl = urls[0];
            mRequestDetailUrl = urls[1];
        }

        mSchoolId = parseInt(mPadSchool != null && mPadSchool.getNumber() != null
                ? mPadSchool.getNumber() : "0");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lib_search, container, false);

        mListResult = (ListView) view.findViewById(R.id.lib_search_list);
        mEdtSearchText = (EditText) view.findViewById(R.id.lib_search_text);
        mBtnSearch = (ImageButton) view.findViewById(R.id.lib_search_search);

        mViewEmptyList = inflater.inflate(R.layout.empty_list_view, container, false);

        ((ViewGroup)mListResult.getParent()).addView(mViewEmptyList);
        mListResult.setEmptyView(mViewEmptyList);

        mBtnPrev = (Button) view.findViewById(R.id.lib_search_prev_page);
        mBtnNext = (Button) view.findViewById(R.id.lib_search_next_page);
        mTxtTotalPage = (TextView) view.findViewById(R.id.lib_search_total_page);

        mEdtSearchText.requestFocus();
        showKeyboard();

        initListener();
        return view;
    }

    private void initListener() {
        checkPageBtn();

        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPage = 1;
                doSearch();
            }
        });

        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPage++;
                doSearch();
            }
        });

        mBtnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPage--;
                doSearch();
            }
        });
    }

    private void checkPageBtn() {
        if (mTotalPage == 0) {
            mBtnPrev.setEnabled(false);
            mBtnNext.setEnabled(false);
            mCurrentPage = 1;
            mTxtTotalPage.setText("");
            return;
        }
        mTxtTotalPage.setText("第" + mCurrentPage + "页，共" + mTotalPage + "页");
        if (mCurrentPage <= 1) {
            mBtnPrev.setEnabled(false);
        }  else {
            mBtnPrev.setEnabled(true);
        }
        if (mTotalPage > 1 && mCurrentPage < mTotalPage) {
            mBtnNext.setEnabled(true);
        } else {
            mBtnNext.setEnabled(false);
        }
    }

    ProgressDialog mProgressDialog;

    private void doSearch() {
        if (!isEmpty(mRequestListUrl)) {
            hideKeyboard();

            String keyWord = mEdtSearchText.getText().toString();
            try {
                mProgressDialog = new ProgressDialog(mActivity);
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setMessage("正在搜索，请稍候...");
                mProgressDialog.show();
                String url = String.format(mRequestListUrl, mCurrentPage,
                        URLEncoder.encode(keyWord, "utf-8"), mSchoolId);
                search(url);
            } catch (Exception ex) {
                mProgressDialog.dismiss();
            }
        }
    }

    private void search(String url) {
        send(url, new SearchListener());
    }

    private class SearchListener extends SimpleRequestListener {
        @Override
        public boolean handlerJson(String json) {
            OldResponse response = JsonParseHelper.parseOldBookSearchResponse(json);
            if (response != null && !isEmpty(response.getContents())) {
                mTotalPage = response.getYe();
                refreshList(response.getContents());
            } else {
                if (mAdapter != null) {
                    mOldBooks.clear();
                    mAdapter.notifyDataSetChanged();
                }
                mTotalPage = 0;
                mCurrentPage = 1;
            }
            checkPageBtn();
            mProgressDialog.dismiss();
            return true;
        }

        @Override
        public void handleResponseFailed() {
            mTotalPage = 0;
            mCurrentPage = 1;
            checkPageBtn();
            mProgressDialog.dismiss();
        }
    }

    private void refreshList(List<OldBook> books) {
        mOldBooks = books;
        if (mAdapter == null) {
            mAdapter = new OldBookAdapter();
            mListResult.setAdapter(mAdapter);
            mListResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    clickList(mOldBooks.get(position));
                }
            });
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void clickList(OldBook book) {
        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMessage("正在查询馆藏状态，请稍候...");
        mProgressDialog.show();

        hideKeyboard();

        try {
            String marcNo = book.getMarc_no();
            String url = String.format(mRequestDetailUrl, marcNo, mSchoolId);
            send(url, new BookDetailListener(book));
        } catch (Exception ex) {
            mProgressDialog.dismiss();
        }
    }

    private class BookDetailListener extends SimpleRequestListener {

        OldBook book;

        public BookDetailListener(OldBook book) {
            this.book = book;
        }

        @Override
        public boolean handlerJson(String json) {
            SearchBookDetail bookDetail = getSearchBookDetailJson(json);
            if (bookDetail != null) {
                showBookDetail(book, bookDetail);
            }
            mProgressDialog.dismiss();
            return true;
        }

        @Override
        public void handleResponseFailed() {
            mProgressDialog.dismiss();
        }
    }

    private void showBookDetail(OldBook book, SearchBookDetail bookDetail) {
        String title = book.getTitle();
        String author = book.getAuthor();
        String pub = book.getPublisher();
        String htmlCtn1 = bookDetail.getCnt1();
        String htmlCtn2 = bookDetail.getCnt2();

        View view = mActivity.getLayoutInflater().inflate(R.layout.lib_search_detail, null);
        TextView txtTitle = (TextView) view.findViewById(R.id.lib_search_detail_title);
        TextView txtAuthor = (TextView) view.findViewById(R.id.lib_search_detail_author);
        TextView txtPub = (TextView) view.findViewById(R.id.lib_search_detail_pub);
        WebView cnt1 = (WebView) view.findViewById(R.id.lib_search_detail_cnt1);
        WebView cnt2 = (WebView) view.findViewById(R.id.lib_search_detail_cnt2);

        txtTitle.setText(title);
        txtAuthor.setText("作者：" + author);
        txtPub.setText("出版社：" + pub);

        cnt1.loadDataWithBaseURL(null, htmlCtn1, "text/html", "utf-8", null);
        cnt2.loadDataWithBaseURL(null, htmlCtn2, "text/html", "utf-8", null);

        AlertDialog dialog = new AlertDialog.Builder(mActivity)
                .setView(view)
                .setCancelable(true)
                .create();
        dialog.show();
    }

    // 评论详情
    private static final String usernameLabel = "username";
    private static final String pubdateLabel = "pubdate";
    private static final String contentLabel = "content";
    private static final String rkLabel = "rk";
    private static final String brkLabel = "brk";
    private static final String is_delLabel = "is_del";
    private static final String sp_userLabel = "sp_user";
    private static final String contents1Label = "contents1";
    private static final String cnt1Label = "cnt1";
    private static final String cnt2Label = "cnt2";
    private static final String tubiaoLabel = "tubiao";
    private static final String isbnLabel = "isbn";
    private static final String plNumberLabel = "ps";
    // 书刊详情
    private static final String DETAILAUS = "detailaus";
    private static final String DETAILCNTS = "detailcnts";
    private static final String DETAILMS = "detailms";
    // 书刊检索
    private static final String pagesizeLabel = "pagesize";
    private static final String statusLabel = "status";
    private static final String returnnumLabel = "returnnum";
    private static final String contentsLabel = "contents";
    private static final String pagenumLabel = "pagenum";
    private static final String yeLabel = "ye";
    private static final String titleLabel = "title";
    private static final String marc_noLabel = "marc_no";
    private static final String authorLabel = "author";
    private static final String typeLabel = "type";
    private static final String haoLabel = "hao";
    private static final String fb1Label = "fb1";
    private static final String fb2Label = "fb2";
    private static final String publisherLabel = "publisher";
    private static final String xhLabel = "xh";

    SearchBookDetail getSearchBookDetailJson(String json) {
        SearchBookDetail bookDetail = new SearchBookDetail();
        try {
            JSONObject jsonOb = new JSONObject(json);
            if (jsonOb.has(returnnumLabel)) {
                bookDetail.setReturnnum(jsonOb.getString(returnnumLabel));
            }
            if (jsonOb.has(statusLabel)) {
                bookDetail.setStatus(jsonOb.getString(statusLabel));
            }
            if (jsonOb.has(contentsLabel)) {
                ArrayList<PingLun> pingLunList = new ArrayList<PingLun>();
                JSONArray contentsArray = jsonOb.getJSONArray(contentsLabel);
                for (int i = 0, iLen = contentsArray.length(); i < iLen; i++) {
                    PingLun pingLun = new PingLun();
                    JSONObject conJson = contentsArray.getJSONObject(i);
                    if (conJson.has(usernameLabel)) {
                        pingLun.setUsername(conJson.getString(usernameLabel));
                    }
                    if (conJson.has(authorLabel)) {
                        pingLun.setAuthor(conJson.getString(authorLabel));
                    }
                    if (conJson.has(pubdateLabel)) {
                        pingLun.setPubdate(conJson.getString(pubdateLabel));
                    }
                    if (conJson.has(contentLabel)) {
                        pingLun.setContent(conJson.getString(contentLabel));
                    }
                    if (conJson.has(rkLabel)) {
                        pingLun.setRk(conJson.getString(rkLabel));
                    }
                    if (conJson.has(brkLabel)) {
                        pingLun.setBrk(conJson.getString(brkLabel));
                    }
                    if (conJson.has(is_delLabel)) {
                        pingLun.setIs_del(conJson.getString(is_delLabel));
                    }
                    if (conJson.has(marc_noLabel)) {
                        pingLun.setMarc_no(conJson.getString(marc_noLabel));
                    }
                    if (conJson.has(sp_userLabel)) {
                        pingLun.setSp_user(conJson.getString(sp_userLabel));
                    }

                    pingLunList.add(pingLun);
                }

                bookDetail.setPinglunList(pingLunList);
            }

            if (jsonOb.has(contents1Label)) {
                JSONArray contents1Array = jsonOb.getJSONArray(contents1Label);
                JSONObject contents1Json = contents1Array.getJSONObject(0);
                if (contents1Json.has(cnt1Label)) {
                    bookDetail.setCnt1(contents1Json.getString(cnt1Label));
                }
                if (contents1Json.has(cnt2Label)) {
                    bookDetail.setCnt2(contents1Json.getString(cnt2Label));
                }
                if (contents1Json.has(tubiaoLabel)) {
                    bookDetail.setTubiao(contents1Json.getString(tubiaoLabel));
                }
                if (contents1Json.has(plNumberLabel)) {
                    bookDetail.setPinglunNumber(contents1Json
                            .getString(plNumberLabel));
                }
                if (contents1Json.has(DETAILAUS)) {
                    bookDetail.setDetailaus(contents1Json.getString(DETAILAUS));
                }
                if (contents1Json.has(DETAILCNTS)) {
                    bookDetail.setDetailcnts(contents1Json
                            .getString(DETAILCNTS));
                }
                if (contents1Json.has(DETAILMS)) {
                    bookDetail.setDetailms(contents1Json.getString(DETAILMS));
                }
            }
        } catch (JSONException e) {
            Log.e("TAG", " detail e----> " + e.getMessage().toString());
            e.printStackTrace();
        }

        return bookDetail;
    }

    private class OldBookAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mOldBooks.size();
        }

        @Override
        public OldBook getItem(int position) {
            return mOldBooks.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mActivity.getLayoutInflater().inflate(R.layout.lib_search_list_item, null);
                holder.mTxtTitle = (TextView) convertView.findViewById(R.id.lib_search_list_item_title);
                holder.mTxtAuthor = (TextView) convertView.findViewById(R.id.lib_search_list_item_author);
                holder.mTxtFb1 = (TextView) convertView.findViewById(R.id.lib_search_list_item_fb1);
                holder.mTxtFb2 = (TextView) convertView.findViewById(R.id.lib_search_list_item_fb2);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            OldBook oldBook = getItem(position);
            holder.mTxtTitle.setText(oldBook.getTitle());
            holder.mTxtAuthor.setText(oldBook.getAuthor());
            holder.mTxtFb1.setText(oldBook.getFb1());
            holder.mTxtFb2.setText(oldBook.getFb2());

            return convertView;
        }
    }

    private class ViewHolder {
        TextView mTxtTitle, mTxtAuthor, mTxtFb1, mTxtFb2;
    }
}
