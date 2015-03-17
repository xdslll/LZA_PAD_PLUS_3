package com.lza.pad.app2.ui.scene;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.lza.pad.app2.ui.base.BaseActivity;
import com.lza.pad.app2.ui.device.DeviceAuthorityActivity;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.pad.PadAuthority;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.db.model.pad.PadScene;
import com.lza.pad.db.model.pad.PadSchool;
import com.lza.pad.helper.JsonParseHelper;
import com.lza.pad.helper.SimpleRequestListener;
import com.lza.pad.helper.UrlHelper;
import com.lza.pad.support.utils.UniversalUtility;

import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/7/15.
 */
public class MainParseActivity extends BaseActivity {

    protected PadDeviceInfo mPadDeviceInfo;
    protected PadScene mPadScene;

    protected PadSchool mPadSchool;
    protected PadAuthority mPadAuthority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkDeviceParam();
        getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }

    /**
     * [P201]验证设备信息
     */
    private void checkDeviceParam() {
        log("[P201]验证设备信息");
        showProgressDialog("正在解析场景", false);
        if (getIntent() != null) {
            mPadDeviceInfo = getIntent().getParcelableExtra(KEY_PAD_DEVICE_INFO);
            if (mPadDeviceInfo == null) {
                backToDeviceActivity();
            } else {
                getPadScene();
            }
        } else {
            backToDeviceActivity();
        }
    }

    /**
     * [P202]返回设备验证流程
     */
    private void backToDeviceActivity() {
        log("[P202]返回设备验证流程");
        dismissProgressDialog();
        Intent intent = new Intent(mCtx, DeviceAuthorityActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * [P203]获取当前设备的场景
     */
    private void getPadScene() {
        log("[P203]获取当前设备的场景");
        updateProgressDialog("正在获取当前设备的场景");
        String getPadSceneUrl = UrlHelper.getDeviceSceneUrl(mPadDeviceInfo);
        send(getPadSceneUrl, new GetPadSceneListener());
    }

    /**
     * [P204]验证场景的学校编号，和当前设备对应的学校编号是否一致
     * 要求场景的学校编号和当前设备的学校编号一致，否则场景将无法使用
     */
    private void verifySchoolBh() {
        log("[P204]验证场景的学校编号");
        updateProgressDialog("正在校验学校编号");
        String deviceSchooBh = mPadDeviceInfo.getSchool_bh();
        String sceneSchoolBh = mPadScene.getSchool_bh();
        if (deviceSchooBh.equals(sceneSchoolBh)) {
            verifyScenceIsActivate();
        } else {
            handleSceneParseFailed("提示", "当前场景的学校编号与设备的学校编号不一致，请检查后重试！");
        }
    }

    /**
     * [P205]错误提示
     * @param title
     * @param message
     */
    private void handleSceneParseFailed(String title, String message) {
        log("[P205]错误提示");
        dismissProgressDialog();
        UniversalUtility.showDialog(mCtx,
                title,
                message,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        checkDeviceParam();
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        checkDeviceParam();
                    }
                });
    }

    /**
     * [P206]验证当前场景是否处于激活状态
     */
    private void verifyScenceIsActivate() {
        log("[P206]验证当前场景是否处于激活状态");
        updateProgressDialog("正在检查场景是否处于激活状态");
        String isActivate = mPadScene.getActivate();
        if (isActivate.equals(PadScene.IS_ACTIVATE)) {
            checkScenceAuthority();
        } else {
            handleSceneParseFailed("提示", "当前场景处于关闭状态，请在管理后台调整后重试！");
        }
    }

    /**
     * [P207]与学校的最高权限进行比对
     */
    private void checkScenceAuthority() {
        log("[P207]检查学校最高处理权限");
        updateProgressDialog("正在检查学校最高处理权限");
        String getSchoolInfoUrl = UrlHelper.getPadSchoolInfoUrl(mPadDeviceInfo);
        send(getSchoolInfoUrl, new GetSchoolInfoListener());
    }

    /**
     * [P207]与学校的最高权限进行比对
     */
    private void checkSchoolAuthority() {
        String maxAuth = mPadSchool.getMax_authority();
        String currentAuth = mPadScene.getAuthority();
        if (parseInt(currentAuth) > parseInt(maxAuth)) {
            handleSceneParseFailed("提示", "当前场景所需权限超过机构的最大权限，请联系管理员后重试！");
        } else {
            getSceneAuthority();
        }
    }

    /**
     * [P208]获取场景的处理权限
     */
    private void getSceneAuthority() {
        log("[P208]获取场景的处理权限");
        updateProgressDialog("正在获取场景的处理权限");
        String getAuthorityUrl = UrlHelper.getPadAuthority(mPadDeviceInfo, mPadScene);
        send(getAuthorityUrl, new GetAuthorityListener());
    }

    /**
     * [P208]处理场景
     */
    private void parseScene() {
        dismissProgressDialog();
        String activityPath = buildCodePath(mPadAuthority.getScene_parse_code());
        log("activity path=" + activityPath);
        Intent intent = new Intent();
        intent.setClassName(mCtx, activityPath);
        intent.putExtra(KEY_PAD_DEVICE_INFO, mPadDeviceInfo);
        intent.putExtra(KEY_PAD_SCHOOL, mPadSchool);
        intent.putExtra(KEY_PAD_SCENE, mPadScene);
        intent.putExtra(KEY_PAD_AUTHORITY, mPadAuthority);
        startActivity(intent);
        finish();
    }

    private class GetAuthorityListener extends SimpleRequestListener<PadAuthority> {
        @Override
        public ResponseData<PadAuthority> parseJson(String json) {
            return JsonParseHelper.parsePadAuthorityResponse(json);
        }

        @Override
        public void handleRespone(List<PadAuthority> content) {
            mPadAuthority = content.get(0);
            parseScene();
        }

        @Override
        public void handleResponseFailed() {
            handleSceneParseFailed("提示", "获取权场景处理限失败，请重试");
        }
    }

    private class GetSchoolInfoListener extends SimpleRequestListener<PadSchool> {
        @Override
        public ResponseData<PadSchool> parseJson(String json) {
            return JsonParseHelper.parsePadSchoolResponse(json);
        }

        @Override
        public void handleRespone(List<PadSchool> content) {
            mPadSchool = content.get(0);
            checkSchoolAuthority();
        }

        @Override
        public void handleResponseFailed() {
            handleSceneParseFailed("提示", "获取学校信息失败，请重试");
        }
    }

    private class GetPadSceneListener extends SimpleRequestListener<PadScene> {
        @Override
        public ResponseData<PadScene> parseJson(String json) {
            return JsonParseHelper.parseDeviceSceneResponse(json);
        }

        @Override
        public void handleRespone(List<PadScene> content) {
            mPadScene = content.get(0);
            verifySchoolBh();
        }

        @Override
        public void handleResponseFailed() {
            handleSceneParseFailed("提示", "场景解析失败，请重试");
        }
    }
}
