package com.lza.pad.app2.ui.module.content.journal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.app2.ui.widget.base.BaseImageFragment;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.pad.PadResource;
import com.lza.pad.db.model.pad.PadJournalArticle;
import com.lza.pad.helper.JsonParseHelper;
import com.lza.pad.helper.SimpleRequestListener;
import com.lza.pad.helper.UrlHelper;
import com.lza.pad.support.utils.ToastUtils;

import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 2/28/15.
 */
public class JournalContentCatalogFragment extends BaseImageFragment {

    ListView mListJournal;
    ViewStub mViewStubLoading;
    LinearLayout mLayoutLoading;
    TextView mTxtLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.journal_catalog, container, false);
        mListJournal = (ListView) view.findViewById(R.id.journal_catalog_list);

        mViewStubLoading = (ViewStub) view.findViewById(R.id.journal_content_viewstub_loading);
        if (mLayoutLoading == null) {
            mViewStubLoading.inflate();
            mLayoutLoading = (LinearLayout) view.findViewById(R.id.common_loading_layout);
            mTxtLoading = (TextView) view.findViewById(R.id.common_loading_layout_text);
        } else {
            mLayoutLoading.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String url = UrlHelper.getResourceDetailUrl(mPadDeviceInfo, mPadResource);
        send(url, new JournalCatalogaListener());

        TextView emptyView = new TextView(mActivity);
        emptyView.setText("暂无数据");
        emptyView.setTextSize(getResources().getDimension(R.dimen.journal_catalog_content_empty_size));
        mListJournal.setEmptyView(emptyView);

    }

    private class JournalCatalogaListener extends SimpleRequestListener<PadResource> {

        @Override
        public ResponseData<PadResource> parseJson(String json) {
            return JsonParseHelper.parseResourceResponse(json);
        }

        @Override
        public void handleRespone(List<PadResource> content) {
            PadResource data = content.get(0);
            List<PadJournalArticle> catalog = data.getArticle_list();
            ArrayAdapter<PadJournalArticle> adapter = new ArrayAdapter<PadJournalArticle>(mActivity, 0, catalog) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    PadJournalArticle data = getItem(position);
                    if (convertView == null) {
                        convertView = mInflater.inflate(R.layout.journal_catalog_item, parent, false);
                    }

                    TextView txtTitle = (TextView) convertView.findViewById(R.id.journal_catalog_item_title);
                    TextView txtAuthor = (TextView) convertView.findViewById(R.id.journal_catalog_item_author);
                    TextView txtQi = (TextView) convertView.findViewById(R.id.journal_catalog_item_qi);
                    TextView txtAbstract = (TextView) convertView.findViewById(R.id.journal_catalog_item_abstract);
                    TextView txtKeyword = (TextView) convertView.findViewById(R.id.journal_catalog_item_keyword);

                    txtTitle.setText(data.getTitle());
                    txtAuthor.setText(data.getAuthor());
                    txtQi.setText(data.getPubdate() + "-" + data.getQi());
                    txtAbstract.setText(data.getContents());
                    txtKeyword.setText(data.getKeywords());

                    return convertView;
                }
            };
            mListJournal.setAdapter(adapter);
            mLayoutLoading.setVisibility(View.GONE);
        }

        @Override
        public void handleResponseFailed() {
            ToastUtils.showLong(mActivity, "获取数据失败！");
        }
    }
}
