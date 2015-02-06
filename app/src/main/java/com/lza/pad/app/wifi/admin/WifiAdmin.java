package com.lza.pad.app.wifi.admin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.lza.pad.support.debug.AppLogger;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/1/26.
 */
public class WifiAdmin {

    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;

    private List<ScanResult> mWifiList;
    private List<WifiConfiguration> mWifiConfiguration;

    private WifiManager.WifiLock mWifiLock;
    private String mSSID = "";

    private Context mContext;

    private ConnectivityManager mConnManager;

    /**
     * 连接超时时间
     */
    private static final int CONNECTION_TIME_OUT = 60 * 1000;

    public WifiAdmin(Context c) {
        mContext = c;

        mWifiManager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();

        AppLogger.e("ip address=" + mWifiInfo.getIpAddress());
    }

    public boolean openWifi() {
        /*if (!mWifiManager.isWifiEnabled())
            return mWifiManager.setWifiEnabled(true);
        else
            return true;*/
        return mWifiManager.setWifiEnabled(true);
    }

    public boolean closeWifi() {
        /*if (mWifiManager.isWifiEnabled())
            return mWifiManager.setWifiEnabled(false);
        else
            return true;*/
        return mWifiManager.setWifiEnabled(false);
    }

    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        return mContext.registerReceiver(receiver, filter);
    }

    public void unregisterReceiver(BroadcastReceiver receiver) {
        mContext.unregisterReceiver(receiver);
    }

    public void onWifiConnected() {}

    public void onWifiConnectTimeout() {}

    public void onWifiConnecting() {}

    /**
     * 添加一个网络并连接
     *
     * @param wcg
     */
    public boolean addNetwork(WifiConfiguration wcg) {
        register();
        int wcgID = mWifiManager.addNetwork(wcg);
        return mWifiManager.enableNetwork(wcgID, true);
    }

    public static final int TYPE_NO_PASSWORD = 0x11;
    public static final int TYPE_WEP = 0x12;
    public static final int TYPE_WPA = 0x13;

    public boolean addNetWork(String ssid, String password, int type) {
        if (TextUtils.isEmpty(ssid)) {
            AppLogger.e("ssid is null!");
            //ToastUtils.showShort(mContext, "ssid为空！");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            AppLogger.e("password is null!");
            //ToastUtils.showShort(mContext, "wifi密码为空！");
            return false;
        }
        if (type != TYPE_NO_PASSWORD && type != TYPE_WEP && type != TYPE_WPA) {
            AppLogger.e("unknown wifi type!type=" + type);
            //ToastUtils.showShort(mContext, "wifi密码为空！");
            return false;
        }

        stopTimer();
        unregister();

        return addNetwork(createWifiInfo(ssid, password, type));
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {
                AppLogger.e("RSSI changed");
                AppLogger.e("intent is " + WifiManager.RSSI_CHANGED_ACTION);
                /*int connectState = isWifiConnected(mContext);
                if (connectState == WIFI_CONNECTED) {
                    stopTimer();
                    onWifiConnected();
                    unregister();
                } else if (connectState == WIFI_CONNECT_FAILED) {
                    stopTimer();
                    //closeWifi();
                    onWifiConnectTimeout();
                    unregister();
                } else if (connectState == WIFI_CONNECTING) {
                    AppLogger.e("Wifi正在连接...");
                } else {
                    AppLogger.e("未知状态");
                }*/
                if (mConnManager == null)
                    mConnManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                final NetworkInfo wifiNetworkInfo = mConnManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                AppLogger.e("isConnectedOrConnecting = " + wifiNetworkInfo.isConnectedOrConnecting());
                AppLogger.e("wifiNetworkInfo.getDetailedState() = " + wifiNetworkInfo.getDetailedState());

                NetworkInfo.DetailedState detailedState = wifiNetworkInfo.getDetailedState();

                if (detailedState == NetworkInfo.DetailedState.CONNECTED) {
                    stopTimer();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onWifiConnected();
                        }
                    });
                    unregister();
                } else if (detailedState == NetworkInfo.DetailedState.CONNECTING
                        || detailedState == NetworkInfo.DetailedState.AUTHENTICATING
                        || detailedState == NetworkInfo.DetailedState.OBTAINING_IPADDR
                        || detailedState == NetworkInfo.DetailedState.SCANNING) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onWifiConnecting();
                        }
                    });
                }
            }
        }
    };

    public final static int STATE_REGISTRING = 0x01;
    public final static int STATE_REGISTERED = 0x02;
    public final static int STATE_UNREGISTERING = 0x03;
    public final static int STATE_UNREGISTERED = 0x04;
    private int mHaveRegister = STATE_UNREGISTERED;

    private synchronized void register() {
        AppLogger.e("mHaveRegister=" + mHaveRegister);

        if (mHaveRegister == STATE_REGISTRING || mHaveRegister == STATE_REGISTERED)
            return;

        mHaveRegister = STATE_REGISTRING;
        registerReceiver(mReceiver, new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
        mHaveRegister = STATE_REGISTERED;

        startTimer();
    }

    public synchronized void unregister() {
        AppLogger.e("mHaveRegister=" + mHaveRegister);

        if (mHaveRegister == STATE_UNREGISTERED || mHaveRegister == STATE_UNREGISTERING)
            return;

        mHaveRegister = STATE_UNREGISTERING;
        unregisterReceiver(mReceiver);
        mHaveRegister = STATE_REGISTERED;
    }

    public int getRegisterState() {
        return mHaveRegister;
    }

    private Timer mTimer = null;
    private void startTimer() {
        if (mTimer != null)
            stopTimer();

        mTimer = new Timer(true);
        mTimer.schedule(mTimerTask, CONNECTION_TIME_OUT);
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    AppLogger.e("WIFI connection time out!");
                    onWifiConnectTimeout();
                    if (mHaveRegister == STATE_REGISTERED || mHaveRegister == STATE_REGISTRING) {
                        try {
                            unregister();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
        }
    };

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            super.finalize();
            unregister();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public WifiConfiguration createWifiInfo(String SSID, String password, int type) {
        AppLogger.e("SSID=" + SSID + "##Password=" + password + "##Type=" + type);
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = isExists(SSID);
        if (tempConfig != null)
            mWifiManager.removeNetwork(tempConfig.networkId);

        if (type == TYPE_NO_PASSWORD) {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == TYPE_WEP) {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == TYPE_WPA) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }

        return config;
    }

    public static final int WIFI_CONNECTED = 0x01;
    public static final int WIFI_CONNECT_FAILED = 0x02;
    public static final int WIFI_CONNECTING = 0x013;

    public int isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        AppLogger.e("isConnectedOrConnecting = " + wifiNetworkInfo.isConnectedOrConnecting());
        AppLogger.e("wifiNetworkInfo.getDetailedState() = " + wifiNetworkInfo.getDetailedState());

        if (wifiNetworkInfo.getDetailedState() == NetworkInfo.DetailedState.OBTAINING_IPADDR
                || wifiNetworkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTING
                || wifiNetworkInfo.getDetailedState() == NetworkInfo.DetailedState.SCANNING
                || wifiNetworkInfo.getDetailedState() == NetworkInfo.DetailedState.AUTHENTICATING) {
            return WIFI_CONNECTING;
        } else if (wifiNetworkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
            return WIFI_CONNECTED;
        } else {
            AppLogger.e("getDetailedState()=" + wifiNetworkInfo.getDetailedState());
            return WIFI_CONNECT_FAILED;
        }
    }

    private WifiConfiguration isExists(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        if (existingConfigs != null) {
            for (WifiConfiguration config : existingConfigs) {
                if (config.SSID.equals("\"" + SSID + "\"")) {
                    return config;
                }
            }
        }
        return null;
    }

    public void disconnectWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }

    public int checkState() {
        return mWifiManager.getWifiState();
    }

    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    public void releaseWifiLock() {
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    public void createWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("Test");
    }

    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfiguration;
    }

    public void connectConfiguration(int index) {
        if (index > mWifiConfiguration.size())
            return;

        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId, true);
    }

    public void startScan() {
        boolean ret = mWifiManager.startScan();
        if (ret) {
            mWifiList = mWifiManager.getScanResults();
            mWifiConfiguration = mWifiManager.getConfiguredNetworks();
            /*for (ScanResult sr : mWifiList) {
                AppLogger.e("ssid-->" + sr.SSID);
            }*/
        } else {
            AppLogger.e("扫描Wifi列表失败！");
        }
    }

    public List<ScanResult> getWifiList() {
        return mWifiList;
    }

    public StringBuilder lookUpScan() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mWifiList.size(); i++) {
            sb.append("Index_")
                    .append((i + 1))
                    .append(":")
                    .append(mWifiList.get(i).toString())
                    .append("\n");
        }
        return sb;
    }

    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }

    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    public String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }

    public boolean isWifiEnabled() {
        return mWifiManager.isWifiEnabled();
    }

    public int getWifiState() {
        return mWifiManager.getWifiState();
    }

}
