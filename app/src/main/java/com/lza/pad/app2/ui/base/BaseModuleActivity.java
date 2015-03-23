package com.lza.pad.app2.ui.base;

import android.content.Intent;
import android.os.Bundle;

import com.lza.pad.app2.base.IScene;
import com.lza.pad.app2.service.ServiceMode;
import com.lza.pad.db.model.pad.PadAuthority;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.db.model.pad.PadScene;
import com.lza.pad.db.model.pad.PadSceneModule;
import com.lza.pad.db.model.pad.PadSchool;
import com.lza.pad.support.utils.ToastUtils;

import java.util.ArrayList;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/3/23.
 */
public class BaseModuleActivity extends BaseActivity implements IScene {

    protected PadDeviceInfo mPadDeviceInfo;
    protected PadScene mPadScene;

    protected PadSchool mPadSchool;
    protected PadAuthority mPadAuthority;
    protected PadSceneModule mPadSceneModule;

    protected ArrayList<PadSceneModule> mGuideModule;
    protected ArrayList<PadSceneModule> mHomeModule;
    protected ArrayList<PadSceneModule> mSubpageModule;
    protected ArrayList<PadSceneModule> mContentModule;
    protected ArrayList<PadSceneModule> mHelpModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            mPadDeviceInfo = getIntent().getParcelableExtra(KEY_PAD_DEVICE_INFO);
            mPadScene = getIntent().getParcelableExtra(KEY_PAD_SCENE);
            mPadSchool = getIntent().getParcelableExtra(KEY_PAD_SCHOOL);
            mPadAuthority = getIntent().getParcelableExtra(KEY_PAD_AUTHORITY);
            mPadSceneModule = getIntent().getParcelableExtra(KEY_PAD_MODULE_INFO);

            mGuideModule = getIntent().getParcelableArrayListExtra(KEY_PAD_MODULE_GUIDE);
            mHomeModule = getIntent().getParcelableArrayListExtra(KEY_PAD_MODULE_HOME);
            mSubpageModule = getIntent().getParcelableArrayListExtra(KEY_PAD_MODULE_SUBPAGE);
            mContentModule = getIntent().getParcelableArrayListExtra(KEY_PAD_MODULE_CONTENT);
            mHelpModule = getIntent().getParcelableArrayListExtra(KEY_PAD_MODULE_HELP);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerEventBus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterEventBus();
    }

    @Override
    public void getSceneModules(PadScene scene) {

    }

    @Override
    public void launchModule(PadSceneModule module) {
        String activityPath = buildCodePath(mPadAuthority.getModule_parse_code());
        log("activity path=" + activityPath);
        Intent intent = new Intent();
        intent.setClassName(getBaseContext(), activityPath);
        Bundle arg = new Bundle();
        arg.putParcelable(KEY_PAD_DEVICE_INFO, mPadDeviceInfo);
        arg.putParcelable(KEY_PAD_SCHOOL, mPadSchool);
        arg.putParcelable(KEY_PAD_SCENE, mPadScene);
        arg.putParcelable(KEY_PAD_AUTHORITY, mPadAuthority);
        arg.putParcelable(KEY_PAD_MODULE_INFO, module);

        arg.putParcelableArrayList(KEY_PAD_MODULE_GUIDE, mGuideModule);
        arg.putParcelableArrayList(KEY_PAD_MODULE_HOME, mHomeModule);
        arg.putParcelableArrayList(KEY_PAD_MODULE_SUBPAGE, mSubpageModule);
        arg.putParcelableArrayList(KEY_PAD_MODULE_CONTENT, mContentModule);
        arg.putParcelableArrayList(KEY_PAD_MODULE_HELP, mHelpModule);
        intent.putExtras(arg);
        startActivity(intent);
    }

    public void onEvent(ServiceMode mode) {
        if (mode == ServiceMode.MODE_START_HOME_MODULE) {
            if (mHomeModule.size() > 0) {
                launchModule(mHomeModule.get(0));
            } else {
                ToastUtils.showLong(mCtx, "首页模块不存在，请检查！");
            }
        } else if (mode == ServiceMode.MODE_RESET_SERVICE) {
            log("执行重置业务");
        }
    }
}
