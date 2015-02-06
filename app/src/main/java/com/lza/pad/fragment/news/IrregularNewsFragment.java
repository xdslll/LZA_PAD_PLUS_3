package com.lza.pad.fragment.news;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.app.news.NewsActivity;
import com.lza.pad.app.news.NewsContentActivity;
import com.lza.pad.fragment.base.BaseFragment;
import com.lza.pad.support.utils.ToastUtils;
import com.lza.pad.widget.IrregularNews;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/16/15.
 */
public class IrregularNewsFragment extends BaseFragment {

    private TextView mTxtMore;
    private IrregularNews mNews;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news, container, false);
        mTxtMore = (TextView) view.findViewById(R.id.news_home_more);
        mTxtMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, NewsActivity.class));
            }
        });

        mNews = (IrregularNews) view.findViewById(R.id.news_irregular_news);
        mNews.setOnNewsClickListener(new IrregularNews.OnNewsClickListener() {
            @Override
            public void onClick(View v, int position) {
                ToastUtils.showShort(mActivity, "点击了：区域" + position);
                startActivity(new Intent(mActivity, NewsContentActivity.class));
            }
        });
        return view;
    }
}
