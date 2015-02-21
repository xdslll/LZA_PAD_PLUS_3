package com.lza.pad.app.base;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.db.model.pad.PadLayoutModule;
import com.lza.pad.db.model.pad.PadModuleControl;

import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/18/15.
 */
public abstract class BaseNormalModuleActivity extends BaseActivity {

    private LinearLayout mMainContainer;
    private TextView mTxtBack, mTxtType, mTxtSearch, mTxtModName, mTxtDivider;

    private List<PadModuleControl> mPadControlInfos;
    private PadLayoutModule mPadModuleInfo;
    private PadDeviceInfo mPadDeviceInfo;

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
        mTxtType = (TextView) findViewById(R.id.home_ebook_subject);
        mTxtType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTypeClick(v);
            }
        });
        mTxtDivider = (TextView) findViewById(R.id.home_ebook_divider);

        mTxtModName = (TextView) findViewById(R.id.home_ebook_mod_name);
        mTxtModName.setText(getModName());

        mTxtSearch = (TextView) findViewById(R.id.home_ebook_search);
        mTxtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchClick(v);
            }
        });

        setTypeText(getTypeText());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        int w, h;
        w = mMainContainer.getWidth();
        h = mMainContainer.getHeight();

        int height = getControlMaxHeight();

    }

    /**
     * 获取控件的最大高度
     * @return
     */
    private int getControlMaxHeight() {
        int height = 0;
        if (mPadControlInfos != null) {
            for (int i = 0; i < mPadControlInfos.size(); i++) {
                try {
                    int h = Integer.parseInt(mPadControlInfos.get(i).getControl_height());
                    height += h;
                } catch (Exception ex) {

                }
            }
        }
        return height;
    }


    protected String getModName() {
        return mPadModuleInfo != null ? mPadModuleInfo.getModule_name() : null;
    }

    protected void onSearchClick(View v) {}

    protected void onTypeClick(View v) {}

    //protected abstract void onDrawWindow(LinearLayout container, int w, int h);

    protected void setTypeText(String text) {
        if (TextUtils.isEmpty(text)) {
            mTxtType.setVisibility(View.GONE);
            mTxtDivider.setVisibility(View.GONE);
        } else {
            mTxtType.setText(text);
            mTxtType.setVisibility(View.VISIBLE);
            mTxtDivider.setVisibility(View.VISIBLE);
        }
    }

    protected void setSearchText(String text) {
        if (TextUtils.isEmpty(text)) {
            mTxtSearch.setVisibility(View.GONE);
        } else {
            mTxtSearch.setText(text);
            mTxtSearch.setVisibility(View.VISIBLE);
        }
    }

    protected String getTypeText() {
        return null;
    }

    protected String getSearchText() {
        return null;
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
        mTxtDivider.setVisibility(View.VISIBLE);
        mTxtSearch.setVisibility(View.VISIBLE);
        mTxtSearch.setText(search);
        mTxtSearch.setOnClickListener(listener);
    }
}