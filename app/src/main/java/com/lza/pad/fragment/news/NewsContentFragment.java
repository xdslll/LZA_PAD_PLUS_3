package com.lza.pad.fragment.news;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.fragment.base._BaseFragment;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/21/15.
 */
public class NewsContentFragment extends _BaseFragment {

    TextView mTxtTitle, mTxtDate;
    WebView mWebView;
    WebSettings mWebSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_content, container, false);
        mTxtTitle = (TextView) view.findViewById(R.id.news_content_title);
        mTxtDate = (TextView) view.findViewById(R.id.news_content_date);
        mWebView = (WebView) view.findViewById(R.id.news_content_text);

        if (mPadResource != null) {
            mTxtTitle.setText(mPadResource.getTitle());
            mTxtDate.setText(mPadResource.getPubdate());

            // 设置支持JavaScript等
            mWebSettings = mWebView.getSettings();
            mWebSettings.setJavaScriptEnabled(true);
            mWebSettings.setBuiltInZoomControls(true);
            mWebSettings.setLightTouchEnabled(true);
            mWebSettings.setSupportZoom(true);
            mWebSettings.setTextZoom(120);
            mWebView.setHapticFeedbackEnabled(false);

            mWebView.loadDataWithBaseURL(null, mPadResource.getContents(), "text/html", "UTF-8", null);
        }

        return view;
    }
}
