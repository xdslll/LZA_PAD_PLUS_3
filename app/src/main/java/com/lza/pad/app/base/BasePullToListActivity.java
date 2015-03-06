package com.lza.pad.app.base;

import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lza.pad.R;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/2/14.
 */
public abstract class BasePullToListActivity extends _BaseActivity {

    private PullToRefreshListView mRefreshListView;
    private TextView mTxtHome, mTxtModule, mTxtType, mTxtSearch;
    private View mViewDivider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_list_pull_list);

        mRefreshListView = (PullToRefreshListView) findViewById(R.id.module_refresh_list);
        mTxtHome = (TextView) findViewById(R.id.module_home);
        mTxtModule = (TextView) findViewById(R.id.module_name);
        mTxtType = (TextView) findViewById(R.id.module_type);
        mTxtSearch = (TextView) findViewById(R.id.module_search);

        mRefreshListView.setAdapter(getAdapter());
        mRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        mRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
    }

    /**
     * 定义分类按钮的字体和点击事件
     *
     * @param type
     * @param listener
     */
    public void setType(String type, View.OnClickListener listener) {
        mTxtType.setVisibility(View.VISIBLE);
        mTxtType.setText(type);
        mTxtType.setOnClickListener(listener);
    }

    /**
     * 定义搜索按钮的文本和点击事件
     *
     * @param search
     * @param listener
     */
    public void setSearch(String search, View.OnClickListener listener) {
        mViewDivider.setVisibility(View.VISIBLE);
        mTxtSearch.setVisibility(View.VISIBLE);
        mTxtSearch.setText(search);
        mTxtSearch.setOnClickListener(listener);
    }

    public abstract ListAdapter getAdapter();

}
