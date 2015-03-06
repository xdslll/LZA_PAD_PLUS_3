package com.lza.pad.app.base;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lza.pad.R;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/18/15.
 */
@Deprecated
public abstract class _BaseModuleActivity extends _BaseActivity {

    private LinearLayout mMainContainer;
    private TextView mTxtBack, mTxtSubject, mTxtSearch, mTxtModName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_list_container);
        mMainContainer = (LinearLayout) findViewById(R.id.home);
        mTxtBack = (TextView) findViewById(R.id.home_ebook_back);
        mTxtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTxtSubject = (TextView) findViewById(R.id.home_ebook_subject);
        mTxtSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubjectClick(v);
            }
        });

        mTxtModName = (TextView) findViewById(R.id.home_ebook_mod_name);
        mTxtModName.setText(getModName());

        mTxtSearch = (TextView) findViewById(R.id.home_ebook_search);
        mTxtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearch(v);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        //获取屏幕尺寸
        int w, h;
        w = mMainContainer.getWidth();
        h = mMainContainer.getHeight();

        onDrawWindow(mMainContainer, w, h);
    }

    protected abstract String getModName();

    protected void onSearch(View v) {}

    protected void onSubjectClick(View v) {}

    protected abstract void onDrawWindow(LinearLayout container, int w, int h);

    protected void setSubjectText(String text) {
        mTxtSubject.setText(text);
    }
}