package com.lza.pad.app2.ui.module;

import android.content.DialogInterface;
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
import com.lza.pad.support.utils.UniversalUtility;

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

        /*ImageView img = new ImageView(this);
        img.setImageResource(R.drawable.test_panoramic_p1);
        mMainLayout.addView(img);*/

        getModuleWidgets();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //startModuleSwitchingService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stopModuleSwitchingService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * [P406]启动模块切换服务
     */
    private void startModuleSwitchingService() {
        //Intent intent = new Intent();
        //intent.setAction(ACTION_MODULE_SWITCHING_SERVICE);
        //intent.putExtra(KEY_PAD_DEVICE_INFO, mPadDeviceInfo);
        //intent.putExtra(KEY_PAD_MODULE_INFO, pickFirst(mPadSceneModule.getModule_type_id()));
        //startService(intent);
    }

    private void stopModuleSwitchingService() {
        //EventBus.getDefault().post(SwitchingServiceMode.MODE_STOP_MODULE_SERVICE);
    }

    /**
     * [P401]获取模块组件
     */
    private void getModuleWidgets() {
        log("[P401]获取模块组件");
        final PadModule mod = pickFirst(mPadSceneModule.getModule_id());
        if (mod == null) {
            handleErrorProcess("提示", "获取模块失败，请重试！");
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
            } else {
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
            handleErrorProcess("提示", "获取组件失败，请重试！");
        }

        @Override
        public void handleRespone(List<PadModuleWidget> content) {
            PadModuleType moduleType = pickFirst(mPadSceneModule.getModule_type_id());
            if (moduleType == null) {
                handleErrorProcess("提示", "获取模块类型失败，请重试！");
                return;
            }
            mPadModuleWidgets = content;
            renderModule();
        }
    }

    /*private class SceneSwitchingReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_SCENE_SWITCHING_RECEIVER)) {
                log("场景切换，关闭：" + StandardModuleActivity.class.getSimpleName());
                finish();
            } else if (intent.getAction().equals(ACTION_MODULE_SWITCHING_RECEIVER)) {
                log("模块切换，关闭：" + StandardModuleActivity.class.getSimpleName());
                finish();
            }
        }
    }

    private SceneSwitchingReceiver mSwitchingReceiver = new SceneSwitchingReceiver();

    protected void registerSceneSwitchingReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SCENE_SWITCHING_RECEIVER);
        filter.addAction(ACTION_MODULE_SWITCHING_RECEIVER);
        registerReceiver(mSwitchingReceiver, filter);
    }

    protected void unregisterSceneSwitchingReceiver() {
        try {
            unregisterReceiver(mSwitchingReceiver);
        } catch (Exception ex) {

        }
    }*/

    protected void handleErrorProcess(String title, String message) {
        dismissProgressDialog();
        UniversalUtility.showDialog(mCtx, title, message,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getModuleWidgets();
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
    }
}
