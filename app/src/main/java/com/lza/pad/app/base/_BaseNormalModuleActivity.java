package com.lza.pad.app.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.db.model.pad._old.PadLayoutModule;
import com.lza.pad.db.model.pad._old.PadModuleControl;
import com.lza.pad.helper.JsonParseHelper;
import com.lza.pad.helper.SimpleRequestListener;
import com.lza.pad.helper.UrlHelper;
import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.utils.Utility;

import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/18/15.
 */
@Deprecated
public abstract class _BaseNormalModuleActivity extends _BaseActivity {

    private LinearLayout mMainContainer;
    private TextView mTxtBack, mTxtType, mTxtSearch, mTxtModName, mTxtDivider;

    private List<PadModuleControl> mPadControlInfos;
    private PadLayoutModule mPadModuleInfo;
    private PadDeviceInfo mPadDeviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            mPadDeviceInfo = getIntent().getParcelableExtra(KEY_PAD_DEVICE_INFO);
            mPadModuleInfo = getIntent().getParcelableExtra(KEY_PAD_MODULE_INFO);
        }

        setContentView(R.layout.common_list_container);
        mMainContainer = (LinearLayout) findViewById(R.id.home);
        mTxtBack = (TextView) findViewById(R.id.home_ebook_back);
        mTxtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
        setSearchText(getSearchText());
        //开始获取该模块下的所有控件
        showProgressDialog("开始获取控件");
        mMainHandler.sendEmptyMessage(REQUEST_GET_CONTROLS);
    }

    private static final int REQUEST_GET_CONTROLS = 0x01;
    private static final int REQUEST_DRAW_CONTROLS = 0x02;

    protected Handler mMainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == REQUEST_GET_CONTROLS) {
                requestModuleControls();
            } else if (msg.what == REQUEST_DRAW_CONTROLS) {
                drawControls();
            }
        }
    };

    private void drawControls() {
        //如果没有控件数据则直接退出
        if (mPadControlInfos == null || mPadControlInfos.size() <= 0) {
            dismissProgressDialog();
            return;
        }
        int width, height;
        width = mMainContainer.getWidth();
        height = mMainContainer.getHeight();
        //如果布局获取失败，则开始重试
        if (width == 0 && height == 0) {
            updateProgressDialog("准备重新绘制界面");
            mMainHandler.sendEmptyMessageDelayed(REQUEST_DRAW_CONTROLS, DEFAULT_RETRY_DELAY);
        }
        int totalHeight = getControlMaxHeight();

        for (int i = 0; i < mPadControlInfos.size(); i++) {
            log("正在绘制第" + (i + 1) + "个控件");
            PadModuleControl control = mPadControlInfos.get(i);
            //int controlHeight = Integer.parseInt(control.getControl_height());
            //拼接出控件代码所在的路径
            String controlType = control.getControl_type();
            String controlName = control.getControl_name();
            String controlIndex = control.getControl_index();
            String packageName = getPackageName();
            //将包名的首字母变成小写
            controlType = controlType.toLowerCase();
            //将文件名首字母变成大写
            StringBuffer buffer = new StringBuffer();
            buffer.append(packageName).append(".")
                    .append("fragment.")
                    .append(controlType).append(".")
                    .append(controlName.substring(0, 1).toUpperCase())
                    .append(controlName.substring(1, controlName.length()))
                    .append("Fragment");
            if (Utility.safeIntParse(controlIndex, 0) > 0) {
                buffer.append(controlIndex);
            }

            AppLogger.e("文件名：" + buffer.toString());

            try {
                //计算当前控件的宽度和高度
                int fragmentWidth = width;
                int controlHeight = Utility.safeIntParse(control.getControl_height(), 1);
                int fragmentHeight = (int) ((float) height / totalHeight * controlHeight);
                Class clazz = Class.forName(buffer.toString());
                Fragment frg = (Fragment) clazz.newInstance();
                Bundle arg = new Bundle();
                arg.putParcelable(KEY_PAD_DEVICE_INFO, mPadDeviceInfo);
                arg.putParcelable(KEY_PAD_MODULE_INFO, mPadModuleInfo);
                arg.putParcelable(KEY_PAD_CONTROL_INFO, control);
                arg.putInt(KEY_FRAGMENT_WIDTH, fragmentWidth);
                arg.putInt(KEY_FRAGMENT_HEIGHT, fragmentHeight);
                arg.putBoolean(KEY_IS_HOME, mIsHome);
                frg.setArguments(arg);

                int id = (i + 1) << (i + 1);
                FrameLayout subContainer = new FrameLayout(this);
                subContainer.setLayoutParams(new
                        ViewGroup.LayoutParams(fragmentWidth, fragmentHeight));
                subContainer.setId(id);
                mMainContainer.addView(subContainer);
                launchFragment(frg, id);

                AppLogger.e("width=" + fragmentWidth + ",height=" + fragmentHeight);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        dismissProgressDialog();
    }

    private void requestModuleControls() {
        if (mPadModuleInfo == null) return;
        String controlUrl = UrlHelper.getModuleControlUrl(mPadDeviceInfo, mPadModuleInfo);
        send(controlUrl, new RequestControlsListener());
    }

    private class RequestControlsListener extends SimpleRequestListener<PadModuleControl> {
        @Override
        public ResponseData<PadModuleControl> parseJson(String json) {
            return JsonParseHelper.parseModuleControlResponse(json);
        }

        @Override
        public void handleRespone(List<PadModuleControl> content) {
            mPadControlInfos = content;
            //开始绘制控件
            updateProgressDialog("开始绘制界面");
            mMainHandler.sendEmptyMessage(REQUEST_DRAW_CONTROLS);
        }

        @Override
        public void handleRespone(Throwable error) {
            dismissProgressDialog();
        }
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
            mTxtDivider.setVisibility(View.GONE);
        } else {
            mTxtSearch.setText(text);
            mTxtSearch.setVisibility(View.VISIBLE);
        }
    }

    protected String getTypeText() {
        if (mPadModuleInfo != null) {
            String key = mPadModuleInfo.getSubject();
            return mPadModuleInfo.getSubjectType(key);
        }
        return null;
    }

    protected String getSearchText() {
        if (mPadModuleInfo != null) {
            return mPadModuleInfo.getKeyword();
        }
        return null;
    }

    /**
     * 定义分类按钮的字体和点击事件
     *
     * @param type
     * @param listener
     */
    protected void setTypeListener(String type, View.OnClickListener listener) {
        mTxtType.setVisibility(View.VISIBLE);
        mTxtDivider.setVisibility(View.VISIBLE);
        mTxtType.setText(type);
        mTxtType.setOnClickListener(listener);
    }

    protected void setTypeListener(View.OnClickListener listener) {
        mTxtType.setOnClickListener(listener);
    }

    /**
     * 定义搜索按钮的文本和点击事件
     *
     * @param search
     * @param listener
     */
    protected void setSearchListener(String search, View.OnClickListener listener) {
        mTxtSearch.setVisibility(View.VISIBLE);
        mTxtSearch.setText(search);
        mTxtSearch.setOnClickListener(listener);
    }

    protected void setSearchListener(View.OnClickListener listener) {
        mTxtSearch.setOnClickListener(listener);
    }
}