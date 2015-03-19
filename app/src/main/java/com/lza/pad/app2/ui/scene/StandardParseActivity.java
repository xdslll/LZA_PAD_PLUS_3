package com.lza.pad.app2.ui.scene;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.lza.pad.app2.ui.base.BaseParseActivity;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.pad.PadModuleType;
import com.lza.pad.db.model.pad.PadSceneModule;
import com.lza.pad.helper.JsonParseHelper;
import com.lza.pad.helper.SimpleRequestListener;
import com.lza.pad.helper.UrlHelper;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerLaunchModuleReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterLaunchModuleReceiver();
    }

    /**
     * [P308]清空场景数据
     */
    @Override
    protected void resetSceneData() {
        log("[P308]获取场景下的所有模块");

        clear(mGuideModule);
        clear(mHomeModule);
        clear(mSubpageModule);
        clear(mContentModule);
        clear(mHelpModule);
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
        PadSceneModule module;
        if (hasGuide) {
            module = mGuideModule.get(0);
            log("开始渲染引导页");
        } else {
            module = mHomeModule.get(0);
            log("开始渲染首页");
        }
        launchModule(module);
    }

    @Override
    protected void launchModule(PadSceneModule module) {
        String activityPath = buildCodePath(mPadAuthority.getModule_parse_code());
        log("activity path=" + activityPath);
        Intent intent = new Intent();
        intent.setClassName(mCtx, activityPath);
        Bundle arg = new Bundle();
        arg.putParcelable(KEY_PAD_DEVICE_INFO, mPadDeviceInfo);
        arg.putParcelable(KEY_PAD_SCHOOL, mPadSchool);
        arg.putParcelable(KEY_PAD_SCENE, mPadScene);
        arg.putParcelable(KEY_PAD_AUTHORITY, mPadAuthority);
        arg.putParcelable(KEY_PAD_MODULE_INFO, module);
        arg.putParcelableArrayList(KEY_PAD_MODULE_INFOS, mSubpageModule);
        intent.putExtras(arg);

        startActivity(intent);
    }

    @Override
    protected void launchModuleByType(int type) {
        if (type == PadModuleType.MODULE_TYPE_GUIDE) {
            if (!isEmpty(mGuideModule)) {
                launchModule(mGuideModule.get(0));
            } else {
                if (isEmpty(mHomeModule)) {
                    launchModule(mHomeModule.get(0));
                } else {
                    handleErrorProcess("提示", "缺少引导模块和首页，请检查后重试！");
                }
            }
        } else if (type == PadModuleType.MODULE_TYPE_HOME) {
            if (!isEmpty(mHomeModule)) {
                launchModule(mHomeModule.get(0));
            } else {
                if (isEmpty(mGuideModule)) {
                    launchModule(mGuideModule.get(0));
                } else {
                    handleErrorProcess("提示", "缺少引导模块和首页，请检查后重试！");
                }
            }
        } else if (type == PadModuleType.MODULE_TYPE_HELP) {
            if (!isEmpty(mHelpModule)) {
                launchModule(mHelpModule.get(0));
            } else {
                if (isEmpty(mGuideModule)) {
                    launchModule(mGuideModule.get(0));
                } else if (isEmpty(mHomeModule)) {
                    launchModule(mHomeModule.get(0));
                } else {
                    handleErrorProcess("提示", "缺少引导模块和首页，请检查后重试！");
                }
            }
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

    private <T> void clear(List<T> data) {
        if (isEmpty(data)) return;
        data.clear();
    }

    LaunchModuleReceiver mLauncheModuleReceiver = new LaunchModuleReceiver();

    private void registerLaunchModuleReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_START_HOME_MODULE);
        registerReceiver(mLauncheModuleReceiver, filter);
    }

    private void unregisterLaunchModuleReceiver() {
        try {
            unregisterReceiver(mLauncheModuleReceiver);
        } catch (Exception ex) {

        }
    }

    private class LaunchModuleReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_START_HOME_MODULE)) {
                launchModuleByType(PadModuleType.MODULE_TYPE_HOME);
            }
        }
    }
}
