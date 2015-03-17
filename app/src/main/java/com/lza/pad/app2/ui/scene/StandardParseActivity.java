package com.lza.pad.app2.ui.scene;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.lza.pad.R;
import com.lza.pad.app2.ui.base.BaseParseActivity;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.pad.PadModule;
import com.lza.pad.db.model.pad.PadModuleType;
import com.lza.pad.db.model.pad.PadModuleWidget;
import com.lza.pad.db.model.pad.PadSceneModule;
import com.lza.pad.db.model.pad.PadWidgetLayout;
import com.lza.pad.helper.JsonParseHelper;
import com.lza.pad.helper.SimpleRequestListener;
import com.lza.pad.helper.UrlHelper;
import com.lza.pad.support.utils.RuntimeUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * 标准版的场景解析类
 *
 * 具备以下功能：
 * 1、管理场景间的切换
 * 2、管理场景下的所有模块
 *
 * @author xiads
 * @Date 3/10/15.
 */
public class StandardParseActivity extends BaseParseActivity {

    ArrayList<PadSceneModule> mGuideModule = new ArrayList<PadSceneModule>();
    ArrayList<PadSceneModule> mHomeModule = new ArrayList<PadSceneModule>();
    ArrayList<PadSceneModule> mSubpageModule = new ArrayList<PadSceneModule>();
    ArrayList<PadSceneModule> mContentModule = new ArrayList<PadSceneModule>();
    ArrayList<PadSceneModule> mHelpModule = new ArrayList<PadSceneModule>();

    List<List<PadModuleWidget>> mGuideModuleWidgets = new ArrayList<List<PadModuleWidget>>();
    List<List<PadModuleWidget>> mHomeModuleWidgets = new ArrayList<List<PadModuleWidget>>();
    List<List<PadModuleWidget>> mSubpageModuleWidgets = new ArrayList<List<PadModuleWidget>>();
    List<List<PadModuleWidget>> mContentModuleWidgets = new ArrayList<List<PadModuleWidget>>();
    List<List<PadModuleWidget>> mHelpModuleWidgets = new ArrayList<List<PadModuleWidget>>();

    LinearLayout mMainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_scene_container);
        mMainLayout = (LinearLayout) findViewById(R.id.home);
    }

    /**
     * [P308]清空场景数据
     */
    @Override
    protected void resetSceneData() {
        log("[P308]获取场景下的所有模块");
        if (mMainLayout != null) {
            mMainLayout.removeAllViews();
        }
        clear(mGuideModule);
        clear(mHomeModule);
        clear(mSubpageModule);
        clear(mContentModule);
        clear(mHelpModule);

        clear(mGuideModuleWidgets);
        clear(mHomeModuleWidgets);
        clear(mSubpageModuleWidgets);
        clear(mContentModuleWidgets);
        clear(mHelpModuleWidgets);
    }

    /**
     * [P302]获取场景下的所有模块
     */
    @Override
    protected void getSceneModules() {
        log("[P302]获取场景下的所有模块");
        String getSceneModulesUrl = UrlHelper.getSceneModules(mPadDeviceInfo, mPadScene);
        send(getSceneModulesUrl, new GetSceneModulesListener());
    }

    private class GetSceneModulesListener extends SimpleRequestListener<PadSceneModule> {
        @Override
        public ResponseData<PadSceneModule> parseJson(String json) {
            return JsonParseHelper.parseSceneModulesResponse(json);
        }

        @Override
        public void handleRespone(List<PadSceneModule> content) {
            /**
             * [P303]模块数量大于等于1
             */
            log("[P303]模块数量大于等于1");
            mPadSceneModules = content;
            parseModuleList();
        }

        @Override
        public void handleResponseFailed() {
            handleErrorProcess("提示", "模块获取失败，请重试！");
        }
    }

    /**
     * [P304]检查是否存在引导页且数量等于1
     */
    private void checkGuidePage() {
        log("[P304]检查是否存在引导页且数量等于1");
        if (mGuideModule.size() > 1) {
            handleErrorProcess("提示", "引导页数量大于1，请检查后重试！");
        } else if (isEmpty(mGuideModule)) {
            checkHomePage();
        } else {
            renderModule(true);
        }
    }

    /**
     * [P305]检查是否存在首页
     */
    private void checkHomePage() {
        log("[P305]检查是否存在首页");
        if (isEmpty(mHomeModule)) {
            handleErrorProcess("提示", "引导页和首页都不存在，请检查后重试！");
        } else {
            renderModule(false);
        }
    }

    /**
     * [P306]渲染模块
     *
     * @param hasGuide 是否包含引导页
     */
    private void renderModule(boolean hasGuide) {
        log("[P306]渲染模块");
        dismissProgressDialog();
        if (hasGuide) {
            getModuleWidgets(mGuideModule.get(0).getModule_id().get(0), PadModuleType.MODULE_TYPE_GUIDE, 0);
            log("开始渲染引导页");
        } else {
            getModuleWidgets(mHomeModule.get(0).getModule_id().get(0), PadModuleType.MODULE_TYPE_HOME, 0);
            log("开始渲染首页");
        }

    }

    /**
     * [P307]解析所有模块
     */
    private void parseModuleList() {
        log("[P307]解析所有模块");
        for (int i = 0; i < mPadSceneModules.size(); i++) {
            PadModuleType moduleType = pickFirst(mPadSceneModules.get(i).getModule_type_id());
            if (moduleType == null) continue;
            int type = parseInt(moduleType.getType());
            if (type == PadModuleType.MODULE_TYPE_GUIDE) {
                mGuideModule.add(mPadSceneModules.get(i));
            } else if (type == PadModuleType.MODULE_TYPE_HOME) {
                mHomeModule.add(mPadSceneModules.get(i));
            } else if (type == PadModuleType.MODULE_TYPE_SUBPAGE) {
                mSubpageModule.add(mPadSceneModules.get(i));
            } else if (type == PadModuleType.MODULE_TYPE_CONTENT) {
                mContentModule.add(mPadSceneModules.get(i));
            } else if (type == PadModuleType.MODULE_TYPE_HELP) {
                mHelpModule.add(mPadSceneModules.get(i));
            }
        }
        checkGuidePage();
    }

    /**
     * [SP201]获取模块组件
     *
     * @param module
     * @param type
     * @param index
     */
    private void getModuleWidgets(PadModule module, int type, int index) {
        log("[SP201]获取模块组件");
        String getWidgetsUrl = UrlHelper.getMoudleWidgets(mPadDeviceInfo, module);
        send(getWidgetsUrl, new GetModuleWidgetsListener(type, index));
    }

    private class GetModuleWidgetsListener extends SimpleRequestListener<PadModuleWidget> {

        int type, index;
        public GetModuleWidgetsListener(int type, int index) {
            this.type = type;
            this.index = index;
        }

        @Override
        public ResponseData<PadModuleWidget> parseJson(String json) {
            return JsonParseHelper.parseModuleWidgetsResponse(json);
        }

        @Override
        public void handleResponseFailed() {
            handleErrorProcess("提示", "获取组件失败，请重试！");
        }

        @Override
        public void handleRespone(List<PadModuleWidget> content) {
            if (type == PadModuleType.MODULE_TYPE_GUIDE) {
                mGuideModuleWidgets.add(content);
                renderModule(content, PadModuleType.MODULE_TYPE_GUIDE);
            } else if (type == PadModuleType.MODULE_TYPE_HOME) {
                mHomeModuleWidgets.add(content);
                renderModule(content, PadModuleType.MODULE_TYPE_HOME);
            }
        }
    }

    private void renderModule(List<PadModuleWidget> content, int type) {
        int screenWidth = RuntimeUtility.getScreenWidth(this);
        int screenHeight = RuntimeUtility.getScreenHeight(this);
        int totalHeight = 0;
        for (int i = 0; i < content.size(); i++) {
            //计算高度
            PadWidgetLayout layout = pickFirst(content.get(i).getWidget_layout_id());
            if (layout == null) continue;
            String strHeight = layout.getHeight();
            if (strHeight.equals(PadWidgetLayout.MATCH_PARENT)) {
                layout.setWidget_width(screenWidth);
                layout.setWidget_height(screenHeight - totalHeight);
            } else {
                float rateHeight = Float.parseFloat(strHeight);
                layout.setWidget_width(screenWidth);
                layout.setWidget_height((int) (screenHeight * rateHeight));
            }
            FrameLayout subLayout = new FrameLayout(this);
            subLayout.setLayoutParams(new LinearLayout.LayoutParams(layout.getWidget_width(), layout.getWidget_height()));
            subLayout.setBackgroundColor(Color.YELLOW);
            mMainLayout.addView(subLayout);
        }
    }

    private <T> void clear(List<T> data) {
        if (isEmpty(data)) return;
        data.clear();
    }

}
