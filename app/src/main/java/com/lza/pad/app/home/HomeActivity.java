package com.lza.pad.app.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.lza.pad.R;
import com.lza.pad.app.base._BaseActivity;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.pad._PadDeviceInfo;
import com.lza.pad.db.model.pad.PadLayoutModule;
import com.lza.pad.db.model.pad.PadModuleControl;
import com.lza.pad.helper.event.model.ResponseEventInfo;
import com.lza.pad.helper.event.state.ResponseEventTag;
import com.lza.pad.helper.JsonParseHelper;
import com.lza.pad.helper.RequestHelper;
import com.lza.pad.helper.UrlHelper;
import com.lza.pad.service.UpdateDeviceService;
import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.utils.RuntimeUtility;
import com.lza.pad.support.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/5/15.
 */
public class HomeActivity extends _BaseActivity implements RequestHelper.OnRequestListener {

    private LinearLayout mMainContainer;

    private _PadDeviceInfo mDeviceInfo;
    private List<PadLayoutModule> mLayoutsModules;
    private List<List<PadModuleControl>> mMoudleControls = new ArrayList<List<PadModuleControl>>();

    /**
     * 模块的数量
     */
    private int mModuleSize = -1;

    /**
     * 当前更新模块的序号
     */
    private int mCurrentModuleIndex = -1;

    /**
     * 首页共有几个控件
     */
    private int mHomeControlSize = -1;

    /**
     * 屏幕尺寸
     */
    private int W, H;

    /**
     * 模块的Json数据
     */
    private String mJsonLayoutModules = "";

    /**
     * 控件的Json数据
     */
    private List<String> mJsonModuleControls = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //标识当前Activity是首页
        mIsHome = true;

        mDeviceInfo = getIntent().getParcelableExtra(KEY_PAD_DEVICE_INFO);
        if (mDeviceInfo == null) {
            //throw new DeviceNotFound("设备未识别！请重试！");
            ToastUtils.showLong(mCtx, "未识别到设备，请重试！");
            return;
        }

        setContentView(R.layout.common_main_container);
        mMainContainer = (LinearLayout) findViewById(R.id.home);

        //启动更新
        initLayout();
    }

    private static final int REQUEST_INIT = 0x01;
    private static final int REQUEST_GET_LAYOUT_MODULE = 0x02;
    private static final int REQUEST_GET_MODULE_CONTROL = 0x03;
    private static final int REQUEST_START_DRAW_LAYOUT = 0x04;

    Handler mMainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == REQUEST_INIT) {
                //状态切换为正在获取数据
                mActivityUpdateState = ACTIVITY_STATE_GETTING_DATA;
                logState();
                getLayoutModule();
            } else if (msg.what == REQUEST_GET_LAYOUT_MODULE) {
                requestLayoutModule((String) msg.obj);
            } else if (msg.what == REQUEST_GET_MODULE_CONTROL) {
                updateProgressDialog("开始更新模块" + (mCurrentModuleIndex + 1));
                log("开始更新模块" + (mCurrentModuleIndex + 1));
                PadLayoutModule layout = mLayoutsModules.get(mCurrentModuleIndex);
                requestModuleControls(layout);
            } else if (msg.what == REQUEST_START_DRAW_LAYOUT) {
                updateProgressDialog("开始绘制界面");
                startDrawLayout();
                //更新状态为正在绘制成功
                mActivityUpdateState = ACTIVITY_STATE_HAVE_UPDATED;
                logState();
                dismissProgressDialog();
                //回传给更新界面服务，界面更新成功
                UpdateDeviceService.UpdateCallback callback = new UpdateDeviceService.UpdateCallback();
                callback.isRunning = true;
                callback.isUpdating = false;
                EventBus.getDefault().post(callback);
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //停止更新
        stopUpdateLayout();
        //发送停止请求
        requestUpdateDeviceInfo(mDeviceInfo, "state", _PadDeviceInfo.TAG_STATE_OFF);
    }

    @Override
    protected void onDeviceUpdateSuccess(_PadDeviceInfo deviceInfo) {
        log("设置设备关闭状态成功");
    }

    private int mUpdateDeviceRetryCount = 0;
    private int MAX_RETRY_COUNT = 3;

    @Override
    protected void onDeviceUpdateFailed(_PadDeviceInfo deviceInfo) {
        log("设置设备关闭状态失败");
        if (mUpdateDeviceRetryCount > MAX_RETRY_COUNT) return;
        mUpdateDeviceRetryCount++;
        requestUpdateDeviceInfo(mDeviceInfo, "state", _PadDeviceInfo.TAG_STATE_OFF);
    }

    private void initLayout() {
        String updateTag = mDeviceInfo.getUpdate_tag();
        if (updateTag.equals(_PadDeviceInfo.TAG_NEED_UDPATE)) {
            //如果需要更新，先清除存储的布局数据
            boolean result = RuntimeUtility.clearUiSp(mCtx);
            if (result) log("清除布局数据成功！");
            else log("清除布局数据失败！");
            //未存储更新数据，开始更新
            mActivityUpdateState = ACTIVITY_STATE_NOT_UPDATE;
            logState();
            showProgressDialog("开始初始化布局");
            log("开始初始化布局");
            mMainHandler.sendEmptyMessageDelayed(REQUEST_INIT, DEFAULT_REQUEST_DELAY);
        } else if (updateTag.equals(_PadDeviceInfo.TAG_HAVE_UDPATE)) {
            showProgressDialog("开始获取布局数据");
            //更新状态
            mActivityUpdateState = ACTIVITY_STATE_GETTING_DATA;
            logState();
            //本地有更新数据，直接从json中加载界面
            mJsonLayoutModules = RuntimeUtility.getFromUiSP(mCtx, JSON_LAYOUT_MODULE, "");
            log(mJsonLayoutModules);
            if (TextUtils.isEmpty(mJsonLayoutModules)) {
                mDeviceInfo.setUpdate_tag(_PadDeviceInfo.TAG_NEED_UDPATE);
                initLayout();
            } else {
                handleLayoutModuleJson(mJsonLayoutModules, updateTag);
            }
        }
    }

    private void stopUpdateLayout() {

    }

    /**
     * 向服务器请求设备的所有模块
     */
    private void getLayoutModule() {
        String url = UrlHelper.getLayoutModuleUrl(mDeviceInfo);
        Message msg = Message.obtain();
        msg.what = REQUEST_GET_LAYOUT_MODULE;
        msg.obj = url;
        mMainHandler.sendMessageDelayed(msg, DEFAULT_REQUEST_DELAY);
    }

    private void requestLayoutModule(String url) {
        RequestHelper.getInstance(mCtx, url, HomeActivity.this).send();
    }

    /**
     * 向服务器请求模块下的所有控件
     */
    private void requestModuleControls(PadLayoutModule deviceLayout) {
        String url = UrlHelper.getModuleControlUrl(mDeviceInfo, deviceLayout);
        RequestHelper.getInstance(mCtx, url, HomeActivity.this).send();
    }

    /**
     * 开始根据数据布局
     */
    private void startDrawLayout() {
        //更新状态为正在绘制界面
        mActivityUpdateState = ACTIVITY_STATE_UPDATING;
        logState();
        //遍历出首页
        //PadLayoutModule homeLayout;
        int homeIndex = -1;
        for (int i = 0; i < mLayoutsModules.size(); i++) {
            PadLayoutModule plm = mLayoutsModules.get(i);
            int px = Integer.valueOf(plm.getPx());
            //如果序号为1，就是首页
            if (px == 1) {
                //homeLayout = plm;
                homeIndex = i;
                break;
            }
        }
        List<PadModuleControl> homeControls = mMoudleControls.get(homeIndex);
        mHomeControlSize = homeControls.size();
        W = RuntimeUtility.getScreenWidth(this);
        H = RuntimeUtility.getScreenHeight(this);
        log("屏幕尺寸：" + W + "*" + H);
        //获取总体的高度
        int totalHeight = getTotalHeight(homeControls);
        for (int i = 0; i < homeControls.size(); i++) {
            log("正在绘制第" + (i + 1) + "个控件");
            PadModuleControl control = homeControls.get(i);
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
                    .append("fragment.");
            if (!TextUtils.isEmpty(controlType)) {
                buffer.append(controlType).append(".");
            }
            if (!TextUtils.isEmpty(controlName) && controlName.length() > 1) {
                buffer.append(controlName.substring(0, 1).toUpperCase())
                        .append(controlName.substring(1, controlName.length()));
            } else if (!TextUtils.isEmpty(controlName) && controlName.length() > 0) {
                buffer.append(controlName.toUpperCase());
            }
                buffer.append("Fragment");
            if (parseInt(controlIndex) > 0) {
                buffer.append(controlIndex);
            }

            AppLogger.e("文件名：" + buffer.toString());

            int width = W;
            try {
                //int width = W;
                //int height = H / mHomeControlSize * controlHeight;
                //计算当前控件的高度
                int height = (int) ((Float.parseFloat(control.getControl_height()) / totalHeight) * H);
                Class clazz = Class.forName(buffer.toString());
                Fragment frg = (Fragment) clazz.newInstance();
                Bundle arg = new Bundle();
                arg.putParcelable(KEY_PAD_DEVICE_INFO, mDeviceInfo);
                arg.putParcelable(KEY_PAD_CONTROL_INFO, control);
                arg.putParcelableArrayList(KEY_PAD_MODULE_INFOS, (ArrayList<PadLayoutModule>) mLayoutsModules);
                arg.putInt(KEY_FRAGMENT_WIDTH, width);
                arg.putInt(KEY_FRAGMENT_HEIGHT, height);
                arg.putBoolean(KEY_IS_HOME, mIsHome);
                arg.putInt(KEY_CURRENT_MODULE_INDEX, INDEX_HOME_MODULE);
                frg.setArguments(arg);

                int id = (i + 1) << (i + 1);
                FrameLayout subContainer = new FrameLayout(this);
                subContainer.setLayoutParams(new
                        ViewGroup.LayoutParams(width, height));
                subContainer.setId(id);
                mMainContainer.addView(subContainer);
                launchFragment(frg, id);

                AppLogger.e("width=" + width + ",height=" + height);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private int getTotalHeight(List<PadModuleControl> controls) {
        int height = 0;
        for (int i = 0; i < controls.size(); i++) {
            try {
                int h = Integer.parseInt(controls.get(i).getControl_height());
                height += h;
            } catch (Exception ex) {

            }
        }
        return height;
    }

    @Override
    public void onResponse(ResponseEventInfo response) {
        ResponseEventTag tag = response.getTag();
        String url = response.getUrl();
        String control = UrlHelper.parseControl(url);
        AppLogger.e("control=" + control);
        if (control.equals(CONTROL_GET_LAYOUT_MODULE)) {
            updateProgressDialog("开始更新布局");
            //处理获取设备布局后的逻辑
            if (tag == ResponseEventTag.ON_RESONSE) {
                log("成功获取布局数据");
                //保存布局下的所有模块数据
                mJsonLayoutModules = response.getResponseData();
                //保存Json文件
                RuntimeUtility.putToUiSP(mCtx, JSON_LAYOUT_MODULE, mJsonLayoutModules);

                handleLayoutModuleJson(mJsonLayoutModules, _PadDeviceInfo.TAG_NEED_UDPATE);
            } else {
                //更新状态为获取数据失败
                mActivityUpdateState = ACTIVITY_STATE_GET_DATA_SUCCESS;
                logState();
                log("获取布局数据失败");
                dismissProgressDialog();
                ToastUtils.showLong(mCtx, "获取设备布局信息失败，请重试或联系管理员！");
            }
        } else if (control.equals(CONTROL_GET_MODULE_CONTROL)) {
            //处理获取模块后的逻辑
            if (tag == ResponseEventTag.ON_RESONSE) {
                log("成功获取模块" + (mCurrentModuleIndex + 1) + "数据");
                String json = response.getResponseData();
                //保存模块下的所有控件数据
                mJsonModuleControls.add(json);

                ResponseData<PadModuleControl> data = JsonParseHelper.parseModuleControlResponse(json);
                List<PadModuleControl> controls = data.getContent();
                if (controls == null) {
                    controls = new ArrayList<PadModuleControl>();
                }
                mMoudleControls.add(controls);
                mCurrentModuleIndex++;
                getModuleControlsFromNet();
            } else {
                //只要有一个模块数据获取失败，就更新状态为获取数据失败
                mActivityUpdateState = ACTIVITY_STATE_GET_DATA_SUCCESS;
                logState();
                dismissProgressDialog();
                ToastUtils.showLong(mCtx, "获取模块信息失败，请重试或联系管理员！");
                log("获取模块数据失败");
            }

        }
    }

    /**
     * 根据模块json开始获取控件
     *
     * @param json
     */
    private void handleLayoutModuleJson(String json, String updateTag) {
        //解析模块json文件
        ResponseData<PadLayoutModule> data = JsonParseHelper.parseDeviceLayoutResponse(json);
        //获取所有模块对象，保存为一个序列
        List<PadLayoutModule> layouts = data.getContent();
        //如果模块不存在，则直接退出
        if (layouts == null || layouts.size() == 0) {
            dismissProgressDialog();
            ToastUtils.showLong(mCtx, "模块不存在，请联系管理员！");
            return;
        }
        //保存模块对象
        mLayoutsModules = layouts;
        //获取设备模块的数量
        mModuleSize = mLayoutsModules.size();
        //当前更新模块序号为0
        mCurrentModuleIndex = 0;
        //获取所有控件
        if (updateTag.equals(_PadDeviceInfo.TAG_NEED_UDPATE)) {
            //通过网络请求所有控件
            getModuleControlsFromNet();
        } else if (updateTag.equals(_PadDeviceInfo.TAG_HAVE_UDPATE)) {
            //读取Json
            getModuleControlsFromSp();
        }
    }

    private void getModuleControlsFromSp() {
        if (mModuleSize < 0) return;
        mJsonModuleControls.clear();
        for (int i = 0; i < mModuleSize; i++) {
            //从SP中获取Json
            String json = RuntimeUtility.getFromUiSP(mCtx, JSON_MODULE_CONTROL + i, "");
            mJsonModuleControls.add(json);
            log(json);
            //解析Json
            ResponseData<PadModuleControl> data = JsonParseHelper.parseModuleControlResponse(json);
            List<PadModuleControl> controls = data.getContent();
            if (controls == null) {
                mMoudleControls.add(new ArrayList<PadModuleControl>());
            }
            mMoudleControls.add(controls);
        }
        //如果所有模块都更新完毕，则状态切换为获取数据成功
        mActivityUpdateState = ACTIVITY_STATE_GET_DATA_SUCCESS;
        logState();
        //发起绘制界面的请求
        mMainHandler.sendEmptyMessageDelayed(REQUEST_START_DRAW_LAYOUT, DEFAULT_REQUEST_DELAY);
    }

    /**
     * 准备请求模块下所有的控件
     */
    private void getModuleControlsFromNet() {
        //如果模块不存在，直接退出
        if (mCurrentModuleIndex < 0 || mModuleSize < 0) return;
        if (mCurrentModuleIndex < mModuleSize) {
            //如果还有没更新的模块，继续开始更新
            Message m = Message.obtain(mMainHandler, REQUEST_GET_MODULE_CONTROL);
            mMainHandler.sendMessageDelayed(m, DEFAULT_REQUEST_DELAY);
        } else {
            //如果所有模块都更新完毕，则状态切换为获取数据成功
            mActivityUpdateState = ACTIVITY_STATE_GET_DATA_SUCCESS;
            logState();
            //dismissProgressDialog();
            //ToastUtils.showLong(mCtx, "模块更新完毕！");
            AppLogger.e(mMoudleControls.toString());
            //将控件数据保存到SharedPreferences
            for (int i = 0; i < mJsonModuleControls.size(); i++) {
                RuntimeUtility.putToUiSP(mCtx, JSON_MODULE_CONTROL + i, mJsonModuleControls.get(i));
            }
            //向服务器更新设备状态
            updateDeviceUpdateTag();
            //发起绘制界面的请求
            mMainHandler.sendEmptyMessageDelayed(REQUEST_START_DRAW_LAYOUT, DEFAULT_REQUEST_DELAY);
        }
    }

    private void updateDeviceUpdateTag() {
        mDeviceInfo.setUpdate_tag(_PadDeviceInfo.TAG_HAVE_UDPATE);
        String url = UrlHelper.updateDeviceInfoUrl(mDeviceInfo);
        log("开始更新设备状态");
        RequestHelper.OnRequestListener listener = new RequestHelper.OnRequestListener() {
            @Override
            public void onResponse(ResponseEventInfo response) {
                handleUpdateTagRequest(response);
            }
        };
        RequestHelper.getInstance(mCtx, url, listener).send();
    }

    private void handleUpdateTagRequest(ResponseEventInfo response) {
        if (response == null) return;
        if (response.getTag() == ResponseEventTag.ON_RESONSE) {
            String json = response.getResponseData();
            ResponseData data = JsonParseHelper.parseSimpleResponse(json);
            String state = data.getState();
            if (state.equals(ResponseData.RESPONSE_STATE_OK)) {
                log("设备状态更新成功！");
            } else {
                log("设备状态更新失败！");
            }
        } else if (response.getTag() == ResponseEventTag.ON_ERROR) {

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onUpdateReceive(Context context, Intent intent) {
        log("收到更新请求！");
        if (mActivityUpdateState == ACTIVITY_STATE_HAVE_UPDATED ||
                mActivityUpdateState == ACTIVITY_STATE_NOT_UPDATE ||
                mActivityUpdateState == ACTIVITY_STATE_GET_DATA_FAILED) {
            if (intent == null) return;
            mDeviceInfo = intent.getParcelableExtra(KEY_PAD_DEVICE_INFO);
            if (mDeviceInfo == null) return;
            //判断当前Activity是否在栈顶，如果不在栈顶，不能更新
            String topActivity = getTopActivity();
            String currentActivity = HomeActivity.class.getSimpleName();
            log("top activity : " + topActivity);
            log("current activity : " + currentActivity);
            if (!topActivity.equals(currentActivity)) return;
            log("准备重新更新界面");
            /* 初始化数据 */
            //删除保存的模块数据
            mLayoutsModules.clear();
            //删除保存的控件数据
            mMoudleControls.clear();
            //重置模块数
            mModuleSize = -1;
            //重置当前模块编号
            mCurrentModuleIndex = -1;
            //重置首页控件数目
            mHomeControlSize = -1;
            //移除所有布局
            mMainContainer.removeAllViews();
            //清空Json数据
            mJsonModuleControls.clear();
            mJsonLayoutModules = "";

            //重新加载界面
            initLayout();
        }
    }
}
