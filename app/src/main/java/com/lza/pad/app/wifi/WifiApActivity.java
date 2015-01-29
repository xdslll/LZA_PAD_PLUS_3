package com.lza.pad.app.wifi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.app.base.BaseActivity;
import com.lza.pad.app.wifi.admin.WifiAdmin;
import com.lza.pad.app.wifi.admin.WifiApAdmin;
import com.lza.pad.support.debug.AppLogger;
import com.lza.pad.support.utils.ToastUtils;
import com.lza.pad.support.utils.UniversalUtility;

import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/1/26.
 */
public class WifiApActivity extends BaseActivity implements WifiApAdmin.OnWifiApStartListener {

    Button mBtnOpenWifiAp, mBtnConnectWifi, mBtnScanWifi;
    TextView mTxtWifiApState, mTxtApWifiSSID, mTxtWifiApPassword, mTxtWifiState;
    ListView mListWifi;

    WifiManager mWifiManager;
    WifiAdmin mWifiAdmin;
    WifiApAdmin mWifiApAdmin;
    WifiConfiguration mWifiApConfig;
    Context mCtx;

    boolean mIsWifiApEnable = false;
    boolean mIsWifiEnable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCtx = this;
        mInflater = LayoutInflater.from(mCtx);

        setContentView(R.layout.wifi_hotpot);

        mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        mWifiApAdmin = new WifiApAdmin(mCtx);

        mTxtWifiApState = (TextView) findViewById(R.id.wifi_hotpot_ap_state);
        mTxtApWifiSSID = (TextView) findViewById(R.id.wifi_hotpot_ap_ssid);
        mTxtWifiApPassword = (TextView) findViewById(R.id.wifi_hotpot_ap_password);
        mTxtWifiState = (TextView) findViewById(R.id.wifi_hotpot_state);
        mListWifi = (ListView) findViewById(R.id.wifi_hotpot_wifi_list);

        mBtnOpenWifiAp = (Button) findViewById(R.id.wifi_hotpot_open);
        mIsWifiApEnable = WifiApAdmin.isWifiApEnable(mCtx);//获取热点的开启状态
        setWifiApState();//设置热点状态文字
        mBtnOpenWifiAp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsWifiApEnable) {
                    boolean isOff = WifiApAdmin.closeWifiAp(mCtx);
                    if (isOff) {
                        ToastUtils.showShort(mCtx, "热点关闭成功！");
                        mIsWifiApEnable = false;
                    }
                    updateWifiState();
                } else {
                    mWifiApAdmin.startWifiAp("南京大学图书馆大厅", "1234567890", WifiApActivity.this);
                }
            }
        });

        mBtnConnectWifi = (Button) findViewById(R.id.wifi_hotpot_connect);
        mIsWifiEnable = mWifiManager.isWifiEnabled();//获取Wifi开启状态
        setWifiState();//设置Wifi状态文字
        mWifiAdmin = new WifiAdmin(mCtx) {
            @Override
            public void onNotifyWifiConnected() {
                ToastUtils.showShort(mCtx, "Wifi连接成功！");
            }

            @Override
            public void onNotifyWifiConnectFailed() {
                ToastUtils.showShort(mCtx, "Wifi连接失败！");
            }
        };
        if (mIsWifiEnable) startWifiScan();//如果Wifi处于打开状态，则开始搜索Wifi列表
        mBtnConnectWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ret;
                if (mIsWifiEnable) {
                    ret = mWifiAdmin.closeWifi();
                    if (ret) {
                        mIsWifiEnable = mWifiManager.isWifiEnabled();
                        if (mIsWifiEnable) {
                            ToastUtils.showShort(mCtx, "Wifi关闭失败！");
                        } else {
                            updateWifiState();
                        }
                    } else {
                        ToastUtils.showShort(mCtx, "Wifi关闭失败！");
                    }
                } else {
                    ret = mWifiAdmin.openWifi();
                    if (ret) {
                        mIsWifiEnable = mWifiManager.isWifiEnabled();
                        if (!mIsWifiEnable) {
                            if (mIsWifiApEnable) {
                                UniversalUtility.showDialog(mCtx, "提示", "Wifi启动失败，是否关闭热点后重试？",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                WifiApAdmin.closeWifiAp(mCtx);
                                                boolean result = mWifiAdmin.openWifi();
                                                if (result) {
                                                    updateWifiState();
                                                } else {
                                                    ToastUtils.showShort(mCtx, "Wifi重新开启失败！");
                                                }
                                            }
                                        },
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                            } else {
                                ToastUtils.showShort(mCtx, "Wifi开启失败！");
                            }
                        } else {
                            updateWifiState();
                            startWifiScan();
                        }
                    } else {
                        ToastUtils.showShort(mCtx, "Wifi开启失败！");
                    }
                }
            }
        });

        mBtnScanWifi = (Button) findViewById(R.id.wifi_hotpot_scan);
        mBtnScanWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRetryCount = 0;
                startWifiScan();
            }
        });
    }

    private void setWifiApState() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mIsWifiApEnable) {
                    mWifiApConfig = mWifiApAdmin.getWifiApConfiguration();
                    mBtnOpenWifiAp.setText("关闭热点");
                    mTxtWifiApState.setText("开启");
                    if (mWifiApConfig != null) {
                        mTxtApWifiSSID.setText(mWifiApConfig.SSID);
                        mTxtWifiApPassword.setText(mWifiApConfig.preSharedKey);
                    }
                } else {
                    mBtnOpenWifiAp.setText("打开热点");
                    mTxtWifiApState.setText("关闭");
                    mTxtApWifiSSID.setText("无");
                    mTxtWifiApPassword.setText("无");
                }
            }
        });
    }

    private void setWifiState() {
        if (mIsWifiEnable) {
            mTxtWifiState.setText("开启");
            mBtnConnectWifi.setText("关闭Wifi");
        } else {
            mTxtWifiState.setText("关闭");
            mBtnConnectWifi.setText("开启Wifi");
            clearWifiList();
        }
    }

    @Override
    public void onWifiApSuccess() {
        updateWifiState();
    }

    @Override
    public void onWifiApFailed() {
        updateWifiState();
    }

    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    private void updateWifiState() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mIsWifiEnable = mWifiManager.isWifiEnabled();
                mIsWifiApEnable = WifiApAdmin.isWifiApEnable(mCtx);
                setWifiState();
                setWifiApState();
            }
        });
    }

    ProgressDialog mProgressDialog = null;
    private void startWifiScan() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(mCtx);
            mProgressDialog.setMessage("正在扫描Wifi列表...");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
        if (mRetryCount >= 0 && mRetryCount < MAX_RETRY_COUNT) {
            startWifiScan(1000);
        } else {
            mRetryCount = 0;
            ToastUtils.showShort(mCtx, "没有获取到Wifi列表！");
            if (mProgressDialog != null)
                mProgressDialog.dismiss();
        }
    }

    private void startWifiScan(int delay) {
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mWifiAdmin.startScan();
                mWifiList = mWifiAdmin.getWifiList();
                if (mWifiList != null && mWifiList.size() > 0) {
                    mAdapter = new WifiListApdater();
                    mListWifi.setAdapter(mAdapter);
                    mListWifi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ScanResult data = mWifiList.get(position);
                            String SSID = data.SSID;
                            String TYPE = data.capabilities;
                            AppLogger.e("SSID:" + SSID + ",TYPE:" + TYPE);
                            int type;
                            if (TYPE.contains("WPA") || TYPE.contains("WPA2")) {
                                type = WifiAdmin.TYPE_WPA;
                                showWifiPasswordDialog(SSID, type);
                            } else if (TYPE.contains("WEP")) {
                                type = WifiAdmin.TYPE_WEP;
                                showWifiPasswordDialog(SSID, type);
                            } else {
                                type = WifiAdmin.TYPE_NO_PASSWORD;
                                mWifiAdmin.addNetWork(SSID, "", type);
                            }

                        }
                    });
                    if (mProgressDialog != null)
                        mProgressDialog.dismiss();
                } else {
                    mRetryCount++;
                    startWifiScan();
                }
            }
        }, delay);
    }

    private void showWifiPasswordDialog(final String SSID, final int type) {
        View view = mInflater.inflate(R.layout.wifi_hotpot_list_password, null);
        final AlertDialog dialog = new AlertDialog.Builder(mCtx).setView(view).create();

        final EditText edtPassword = (EditText) view.findViewById(R.id.wifi_hotpot_list_item_password);
        Button btnConfirm = (Button) view.findViewById(R.id.wifi_hotpot_list_item_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = edtPassword.getText().toString();
                WifiConfiguration config = mWifiAdmin.createWifiInfo(SSID, password, type);
                mWifiAdmin.addNetwork(config);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void clearWifiList() {
        if (mWifiList != null && mAdapter != null) {
            mWifiList.clear();
            mAdapter.notifyDataSetChanged();
        }
    }

    List<ScanResult> mWifiList;
    LayoutInflater mInflater;
    WifiListApdater mAdapter;
    int mRetryCount = 0;
    static final int MAX_RETRY_COUNT = 3;

    private class WifiListApdater extends BaseAdapter {

        @Override
        public int getCount() {
            return mWifiList.size();
        }

        @Override
        public ScanResult getItem(int position) {
            return mWifiList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.wifi_hotpot_list_item, null);
                holder.txtSSID = (TextView) convertView.findViewById(R.id.wifi_hotpot_list_item_ssid);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.txtSSID.setText(getItem(position).SSID);
            return convertView;
        }
    }

    private static class ViewHolder {
        TextView txtSSID;
    }
}
