package com.lza.pad.app2.ui.module;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.lza.pad.R;
import com.lza.pad.app2.ui.base.BaseModuleActivity;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.pad.PadModule;
import com.lza.pad.db.model.pad.PadModuleType;
import com.lza.pad.db.model.pad.PadModuleWidget;
import com.lza.pad.db.model.pad.PadWidget;
import com.lza.pad.db.model.pad.PadWidgetLayout;
import com.lza.pad.helper.JsonParseHelper;
import com.lza.pad.helper.SimpleRequestListener;
import com.lza.pad.helper.UrlHelper;
import com.lza.pad.support.utils.RuntimeUtility;
import com.lza.pad.support.utils.ToastUtils;

import java.util.List;

/**
 * 生成模块界面
 *
 * @author xiads
 * @Date 15/3/18.
 */
public class StandardModuleActivity extends BaseModuleActivity {

    private LinearLayout mMainLayout;

    private List<PadModuleWidget> mPadModuleWidgets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_scene_container);
        mMainLayout = (LinearLayout) findViewById(R.id.home);
        getModuleWidgets();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * [P401]获取模块组件
     */
    private void getModuleWidgets() {
        log("[P401]获取模块组件");
        final PadModule mod = pickFirst(mPadSceneModule.getModule_id());
        if (mod == null) {
            handleModuleErrorProcess("提示", "获取模块失败，请重试！");
            return;
        }
        String getWidgetsUrl = UrlHelper.getMoudleWidgets(mPadDeviceInfo, mod);
        send(getWidgetsUrl, new GetModuleWidgetsListener());
    }

    private void renderModule() {
        int screenWidth = RuntimeUtility.getScreenWidth(this);
        int screenHeight = RuntimeUtility.getScreenHeight(this);
        int totalHeight = 0;
        for (int i = 0; i < mPadModuleWidgets.size(); i++) {
            /**
             * [P402]计算布局
             */
            PadWidgetLayout layout = pickFirst(mPadModuleWidgets.get(i).getWidget_layout_id());
            if (layout == null) continue;
            String strHeight = layout.getHeight();
            if (strHeight.equals(PadWidgetLayout.MATCH_PARENT)) {
                layout.setWidget_width(screenWidth);
                layout.setWidget_height(screenHeight - totalHeight);
            } else{
                float rateHeight = Float.parseFloat(strHeight);
                layout.setWidget_width(screenWidth);
                int height = (int) (screenHeight * rateHeight);
                layout.setWidget_height(height);
                totalHeight += height;
            }
            FrameLayout subLayout = new FrameLayout(this);
            //避免使用0作为id号
            subLayout.setId(i + 1);
            subLayout.setLayoutParams(new LinearLayout.LayoutParams(layout.getWidget_width(), layout.getWidget_height()));
            /**
             * [P403]解析组件
             */
            PadWidget widget = pickFirst(mPadModuleWidgets.get(i).getWidget_id());
            if (widget == null) continue;
            String fragmentPath = buildCodePath(widget.getParse_code());
            /**
             * [P405]渲染组件
             */
            try {
                Fragment frg = (Fragment) Class.forName(fragmentPath).newInstance();
                Bundle arg = new Bundle();
                arg.putParcelable(KEY_PAD_DEVICE_INFO, mPadDeviceInfo);
                arg.putParcelable(KEY_PAD_SCHOOL, mPadSchool);
                arg.putParcelable(KEY_PAD_SCENE, mPadScene);
                arg.putParcelable(KEY_PAD_AUTHORITY, mPadAuthority);
                arg.putParcelable(KEY_PAD_MODULE_INFO, mPadSceneModule);
                arg.putParcelable(KEY_PAD_WIDGET, mPadModuleWidgets.get(i));
                arg.putParcelableArrayList(KEY_PAD_MODULE_SUBPAGE, mSubpageModule);
                frg.setArguments(arg);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(i + 1, frg)
                        .commit();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            mMainLayout.addView(subLayout);
        }
    }

    private class GetModuleWidgetsListener extends SimpleRequestListener<PadModuleWidget> {

        @Override
        public ResponseData<PadModuleWidget> parseJson(String json) {
            return JsonParseHelper.parseModuleWidgetsResponse(json);
        }

        @Override
        public void handleResponseFailed() {
            handleModuleErrorProcess("提示", "获取组件失败，请重试！");
        }

        @Override
        public void handleRespone(List<PadModuleWidget> content) {
            PadModuleType moduleType = pickFirst(mPadSceneModule.getModule_type_id());
            if (moduleType == null) {
                handleModuleErrorProcess("提示", "获取模块类型失败，请重试！");
                return;
            }
            mPadModuleWidgets = content;
            renderModule();
        }
    }

    protected void handleModuleErrorProcess(String title, String message) {
        if (isTopActivity()) {
            handleErrorProcess(title, message, new Runnable() {
                @Override
                public void run() {
                    getModuleWidgets();
                }
            });
        } else {
            ToastUtils.showLong(mCtx, message);
        }
    }
}
