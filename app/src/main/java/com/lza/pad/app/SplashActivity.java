package com.lza.pad.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.app.base._BaseActivity;
import com.lza.pad.app.home.HomeActivity;
import com.lza.pad.app.socket.model.MinaClient;
import com.lza.pad.db.model.DownloadFile;
import com.lza.pad.db.model.ResponseData;
import com.lza.pad.db.model.pad.PadDeviceInfo;
import com.lza.pad.db.model.pad.PadVersionInfo;
import com.lza.pad.helper.DownloadHelper;
import com.lza.pad.helper.JsonParseHelper;
import com.lza.pad.helper.RequestHelper;
import com.lza.pad.helper.SimpleRequestListener;
import com.lza.pad.helper.UrlHelper;
import com.lza.pad.helper.event.model.ResponseEventInfo;
import com.lza.pad.helper.event.state.ResponseEventTag;
import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.file.FileTools;
import com.lza.pad.support.utils.Consts;
import com.lza.pad.support.utils.ToastUtils;
import com.lza.pad.support.utils.Utility;
import com.lza.pad.wifi.admin._WifiAdmin;
import com.lza.pad.wifi.admin._WifiApAdmin;

import java.io.File;
import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/4/15.
 */
@Deprecated
public class SplashActivity extends _BaseActivity implements RequestHelper.OnRequestListener {

    TextView mTxtSplash;

    _WifiAdmin mWifiAdmin;
    _WifiApAdmin mWifiApAdmin;

    boolean mIsWifiEnable = false;
    boolean mIsWifiApEnable = false;
    int mWifiState;

    private PadDeviceInfo mPadDeviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash);

        showProgressDialog("正在初始化...");

        mTxtSplash = (TextView) findViewById(R.id.splash_text);
        mTxtSplash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                finish();
            }
        });

        //mMainHandler.sendEmptyMessageDelayed(REQUEST_INIT, 2000);

        //ImageHelper.getInstance(mCtx).clearDiskCache();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMainHandler.sendEmptyMessageDelayed(REQUEST_INIT, 2000);
    }

    private static final int REQUEST_OPEN_WIFI = 0x1;
    private static final int REQUEST_CLOSE_WIFI = 0x2;
    private static final int REQUEST_HANDLE_OPEN_WIFI_STATE = 0x3;
    private static final int REQUEST_HANDLE_CLOSE_WIFI_STATE = 0x4;
    private static final int REQUEST_GET_DEVICE_INFO = 0x5;
    private static final int REQUEST_SEND_GET_DEVICE_INFO = 0x6;
    private static final int REQUEST_INIT = 0x07;

    private Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == REQUEST_OPEN_WIFI) {
                boolean ret = mWifiAdmin.openWifi();
                if (ret) {
                    mMainHandler.sendEmptyMessageDelayed(REQUEST_HANDLE_OPEN_WIFI_STATE, DEFAULT_REQUEST_DELAY);
                } else {
                    ToastUtils.showShort(mCtx, "Wifi设备启动失败，正在重试...");
                    mMainHandler.sendEmptyMessageDelayed(REQUEST_OPEN_WIFI, DEFAULT_RETRY_DELAY);
                }
            } else if (msg.what == REQUEST_HANDLE_OPEN_WIFI_STATE) {
                handleWifiOpenState();
            } else if (msg.what == REQUEST_CLOSE_WIFI) {
                boolean ret = mWifiAdmin.closeWifi();
                if (ret) {
                    mMainHandler.sendEmptyMessage(REQUEST_HANDLE_CLOSE_WIFI_STATE);
                } else {
                    ToastUtils.showShort(mCtx, "Wifi设备关闭失败！");
                }
            } else if (msg.what == REQUEST_HANDLE_CLOSE_WIFI_STATE) {
                handleWifiClose();
            } else if (msg.what == REQUEST_GET_DEVICE_INFO) {
                getDeviceInfo();
            } else if (msg.what == REQUEST_SEND_GET_DEVICE_INFO) {
                String url = (String) msg.obj;
                RequestHelper.getInstance(mCtx, url, SplashActivity.this).send();
            } else if (msg.what == REQUEST_INIT) {
                checkWifi();
            }
        }
    };

    /**
     * 检查热点和Wifi状态（为了获取MacAddress）
     */
    private void checkWifi() {
        //检查热点是否打开，如果打开，则关闭热点
        mWifiApAdmin = _WifiApAdmin.instance(mCtx);
        mIsWifiApEnable = mWifiApAdmin.isWifiApEnable();
        if (mIsWifiApEnable) {
            updateProgressDialog("正在关闭热点...");
            mWifiApAdmin.closeWifiAp();
            mIsWifiApEnable = mWifiApAdmin.isWifiApEnable();
        }

        mWifiAdmin = new _WifiAdmin(mCtx);
        //打开Wifi，获取Mac地址
        mIsWifiEnable = mWifiAdmin.isWifiEnabled();
        if (!mIsWifiApEnable && !mIsWifiEnable) {
            openWifiDirectly();
        } else {
            getDeviceInfo();
        }
    }

    /**
     * 打开Wifi
     */
    private void openWifiDirectly() {
        updateProgressDialog("正在打开Wifi...");
        mMainHandler.sendEmptyMessageDelayed(REQUEST_OPEN_WIFI, DEFAULT_REQUEST_DELAY);
    }

    /**
     * 处理Wifi打开后的状态
     */
    private void handleWifiOpenState() {
        mWifiState = mWifiAdmin.getWifiState();
        mIsWifiEnable = mWifiAdmin.isWifiEnabled();
        if (mWifiState == WifiManager.WIFI_STATE_DISABLED) {
            mMainHandler.sendEmptyMessageDelayed(REQUEST_HANDLE_OPEN_WIFI_STATE, DEFAULT_REQUEST_DELAY);
        } else if (mWifiState == WifiManager.WIFI_STATE_ENABLING) {
            mMainHandler.sendEmptyMessageDelayed(REQUEST_HANDLE_OPEN_WIFI_STATE, DEFAULT_REQUEST_DELAY);
        } else if (mWifiState == WifiManager.WIFI_STATE_ENABLED) {
            AppLogger.e("Wifi打开成功！");
            mMainHandler.sendEmptyMessageDelayed(REQUEST_GET_DEVICE_INFO, DEFAULT_REQUEST_DELAY);
        } else {
            ToastUtils.showShort(mCtx, "未知状态，正在重试...");
            mMainHandler.sendEmptyMessageDelayed(REQUEST_HANDLE_OPEN_WIFI_STATE, DEFAULT_REQUEST_DELAY);
        }
    }

    /**
     * 向服务器请求设备信息
     */
    private void getDeviceInfo() {
        updateProgressDialog("正在验证设备信息...");
        String macAddress = Utility.getMacAddress(this);
        AppLogger.e("mac地址：" + macAddress);

        String url = UrlHelper.getDeviceUrl(macAddress);

        Message msg = Message.obtain();
        msg.what = REQUEST_SEND_GET_DEVICE_INFO;
        msg.obj = url;
        mMainHandler.sendMessageDelayed(msg, DEFAULT_REQUEST_DELAY);
        //RequestHelper.sendRequest(this, url);
    }

    /**
     * 处理Wifi关闭状态
     */
    private void handleWifiClose() {
        mWifiState = mWifiAdmin.getWifiState();
        mIsWifiEnable = mWifiAdmin.isWifiEnabled();
        if (mWifiState == WifiManager.WIFI_STATE_DISABLED) {

        } else if (mWifiState == WifiManager.WIFI_STATE_DISABLING) {

        } else if (mWifiState == WifiManager.WIFI_STATE_ENABLED) {
            mMainHandler.sendEmptyMessageDelayed(REQUEST_HANDLE_CLOSE_WIFI_STATE, DEFAULT_REQUEST_DELAY);
        } else {
            ToastUtils.showShort(mCtx, "未知状态，请重试！");
        }
    }

    /**
     * 处理手机客户端的连接请求
     *
     * @param client
     */
    @Override
    public void onEventMainThread(MinaClient client) {
        if (!checkMinaClient(client)) return;
        //mTxtSplash.setText("您好！欢迎来自[" + client.getAcademy() + "]的朋友：" + client.getName());
    }

    /**
     * 保存设备信息
     *
     * @param deviceInfo
     */
    private void saveDeviceInfo(PadDeviceInfo deviceInfo) {

    }

    /**
     * 通过Dialog展示获取到的设备信息
     *
     * @param deviceInfo
     */
    private void showDeviceInfo(final PadDeviceInfo deviceInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("----------- 设备清单 -----------\n");
        sb.append(deviceInfo.toString());
        Utility.showDialog(mCtx, "提示", sb.toString(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //发送更新启动状态的请求
                requestUpdateDeviceInfo(deviceInfo, "state", PadDeviceInfo.TAG_STATE_ON);
            }
        });
    }

    private void gotoHomeActivity(PadDeviceInfo deviceInfo) {
        dismissProgressDialog();
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        intent.putExtra(KEY_PAD_DEVICE_INFO, deviceInfo);
        startActivity(intent);
        finish();
    }

    /**
     * 处理提交的设备信息请求
     *
     * @param response
     */
    @Override
    public void onResponse(ResponseEventInfo response) {
        ResponseEventTag tag = response.getTag();
        if (tag == ResponseEventTag.ON_RESONSE) {
            String json = response.getResponseData();
            final ResponseData<PadDeviceInfo> data = JsonParseHelper.parseDeviceInfoResponse(json);
            if (data == null) {
                dismissProgressDialog();
                Utility.showDialog(mCtx, "提示", "服务器异常，点击确定重试", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mMainHandler.sendEmptyMessageDelayed(REQUEST_INIT, 2000);
                    }
                });
                return;
            }
            String state = data.getState();
            String message = data.getMessage();
            if (state.equals(ResponseData.RESPONSE_STATE_OK)) {
                if (data.getContent() == null || data.getContent().size() == 0) return;
                final PadDeviceInfo deviceInfo = data.getContent().get(0);
                mPadDeviceInfo = deviceInfo;
                //判断是否需要打开热点
                String isHotspotOn = deviceInfo.getHotspot_switch();
                if (isHotspotOn.equals(PadDeviceInfo.TAG_HOTSPOT_ON)) {
                    updateProgressDialog("正在打开热点");
                    //打开热点
                    _WifiApAdmin wifiApAdmin = _WifiApAdmin.instance(mCtx);
                    final String wifiApName = deviceInfo.getName();
                    String wifiApPassword = deviceInfo.getHotspot_password();
                    wifiApAdmin.startWifiAp(wifiApName, wifiApPassword, new _WifiApAdmin.OnWifiApStartListener() {
                        @Override
                        public void onWifiApSuccess() {
                            ToastUtils.showLong(mCtx, "[" + wifiApName + "]热点启动成功！");
                            log("[" + wifiApName + "]热点启动成功！");
                            //发送更新启动状态的请求
                            updateDeviceInfo(deviceInfo);
                            //requestUpdateDeviceInfo(deviceInfo, "state", PadDeviceInfo.TAG_STATE_ON);
                        }

                        @Override
                        public void onWifiApFailed() {
                            ToastUtils.showLong(mCtx, "[" + wifiApName + "]热点启动失败！");
                            log("[" + wifiApName + "]热点启动失败！");
                            //发送更新启动状态的请求
                            updateDeviceInfo(deviceInfo);
                            //requestUpdateDeviceInfo(deviceInfo, "state", PadDeviceInfo.TAG_STATE_ON);
                        }
                    });
                } else {
                    //发送更新启动状态的请求
                    updateDeviceInfo(deviceInfo);
                    //requestUpdateDeviceInfo(deviceInfo, "state", PadDeviceInfo.TAG_STATE_ON);
                }
            } else if (state.equals(ResponseData.RESPONSE_STATE_NO_LAYOUT)) {
                Utility.showDialog(this, "提示", message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                showProgressDialog("正在重新初始化...");
                                mMainHandler.sendEmptyMessageDelayed(REQUEST_INIT, DEFAULT_REQUEST_DELAY);
                            }
                        });
            } else if (state.equals(ResponseData.RESPONSE_STATE_NO_MAC_ADDRESS)) {
                Utility.showDialog(this, "提示", message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                showProgressDialog("正在重新初始化...");
                                mMainHandler.sendEmptyMessageDelayed(REQUEST_INIT, DEFAULT_REQUEST_DELAY);
                            }
                        });
            }
        } else {
            Utility.showDialog(mCtx, "提示", "请求设备信息失败！请重试！");
        }
    }

    private void updateDeviceInfo(PadDeviceInfo deviceInfo) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                updateProgressDialog("正在更新设备状态...");
            }
        });
        deviceInfo.setState(PadDeviceInfo.TAG_STATE_ON);
        deviceInfo.setLast_connect_time(String.valueOf(System.currentTimeMillis()));
        //读取设备的版本号，比对现有的版本号
        int currentVersionCode = Utility.getVersionCode(mCtx);
        String newVersionCode = deviceInfo.getVersion();
        log("当前版本号：" + currentVersionCode + ",新版本号：" + newVersionCode);
        if (isEmpty(newVersionCode)) {
            deviceInfo.setVersion(String.valueOf(currentVersionCode));
            requestUpdateDeviceInfo(deviceInfo);
        } else {
            if (currentVersionCode < parseInt(newVersionCode)) {
                //只有当前版本小于新版本号时，才开始更新版本
                updateNewVersion(deviceInfo);
            } else {
                deviceInfo.setVersion(String.valueOf(currentVersionCode));
                requestUpdateDeviceInfo(deviceInfo);
            }
        }
    }

    protected void updateNewVersion(PadDeviceInfo deviceInfo) {
        //查询该版本是否存在
        String requestUrl = UrlHelper.getVersionUrl(deviceInfo);
        //如果存在则下载该版本
        send(requestUrl, new UpdateNewVersionListener(deviceInfo));
    }

    private class UpdateNewVersionListener extends SimpleRequestListener<PadVersionInfo> {

        PadDeviceInfo deviceInfo;

        private UpdateNewVersionListener(PadDeviceInfo deviceInfo) {
            this.deviceInfo = deviceInfo;
        }

        @Override
        public ResponseData<PadVersionInfo> parseJson(String json) {
            return JsonParseHelper.pareseVersionInfo(json);
        }

        @Override
        public void handleRespone(List<PadVersionInfo> content) {
            log("开始更新版本...");
            PadVersionInfo version = content.get(0);
            String downloadUrl = version.getUrl();
            if (isEmpty(downloadUrl)) {
                gotoHomeActivity(deviceInfo);
            } else {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateProgressDialog("开始更新版本...");
                    }
                });

                String fileName = deviceInfo.getVersion() + ".apk";
                File filePath = FileTools.getCacheFile(Consts.CACHE_PATH + "/version/" + fileName);
                try {
                    if (filePath.exists()) {
                        filePath.delete();
                    }
                } catch (Exception ex) {

                }
                DownloadHelper.InternelDownloadFile downloadFile = new DownloadHelper.InternelDownloadFile();
                downloadFile.setFileName(fileName);
                downloadFile.setFilePath(filePath.getAbsolutePath());
                downloadFile.setFileType(PadVersionInfo.FILE_TYPE);
                DownloadHelper helper = new DownloadHelper(SplashActivity.this, downloadUrl, downloadFile);
                helper.download();
            }

        }

        @Override
        public void handleRespone(Throwable error) {
            log("下载失败");
            gotoHomeActivity(deviceInfo);
        }
    }

    @Override
    public void onEventAsync(DownloadFile downloadFile) {
        if (downloadFile == null) return;
        String filePath = downloadFile.getFilePath();
        if (isEmpty(filePath)) {
            log("更新文件下载失败！");
            gotoHomeActivity(mPadDeviceInfo);
        } else {
            File file = new File(filePath);
            log("文件是否下载成功：" + file.exists());
            if (file.exists()) {
                Uri uri = Uri.fromFile(new File(filePath));
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onDeviceUpdateSuccess(PadDeviceInfo deviceInfo) {
        gotoHomeActivity(deviceInfo);
    }

    @Override
    protected void onDeviceUpdateFailed(PadDeviceInfo deviceInfo) {
        gotoHomeActivity(deviceInfo);
    }
}
