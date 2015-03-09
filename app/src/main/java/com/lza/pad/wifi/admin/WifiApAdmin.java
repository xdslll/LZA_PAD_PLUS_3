package com.lza.pad.wifi.admin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import com.lza.pad.support.debug.AppLogger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/1/26.
 */
public class WifiApAdmin {

    private static WifiApAdmin mInstance;
    private Context mCtx;
    private static WifiManager mWifiManager = null;
    private static WifiConfiguration mWifiConfig = null;

    public static WifiApAdmin getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new WifiApAdmin(context);
        }
        return mInstance;
    }

    private WifiApAdmin(Context context) {
        this.mCtx = context;
        mWifiManager = (WifiManager) mCtx.getSystemService(Context.WIFI_SERVICE);
        mCtx = context;
    }

    private String mSSID = "";
    private String mPassword = "";

    public void startWifiAp(String ssid, String password, OnWifiApStateChange listener) {
        if (isWifiApEnable()) return;
        setOnWifiApStateChange(listener);
        registerWifiApReceiver();
        mAction = ACTION_OPEN_WIFI_AP;
        mSSID = ssid;
        mPassword = password;
        WifiAdmin wifiAdmin = WifiAdmin.getInstance(mCtx);
        if (wifiAdmin.isWifiEnable()) {
            wifiAdmin.closeWifi(new WifiAdmin.OnWifiStateChange() {
                @Override
                public void handle(String action, int wifiState) {
                    if (action.equals(WifiAdmin.ACTION_CLOSE_WIFI) &&
                            wifiState == WifiManager.WIFI_STATE_DISABLED) {
                        startWifiAp();
                    }
                }
            });
        } else {
            startWifiAp();
        }
    }

    public boolean closeWifiAp(OnWifiApStateChange listener) {
        if (!isWifiApEnable()) return true;
        setOnWifiApStateChange(listener);
        registerWifiApReceiver();
        mAction = ACTION_CLOSE_WIFI_AP;
        return closeWifiAp();
    }

    private boolean startWifiAp() {
        try {
            Method method = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            WifiConfiguration netConfig = new WifiConfiguration();
            netConfig.SSID = mSSID;
            netConfig.preSharedKey = mPassword;
            netConfig.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            netConfig.allowedKeyManagement
                    .set(WifiConfiguration.KeyMgmt.WPA_PSK);
            netConfig.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            netConfig.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            netConfig.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.CCMP);
            netConfig.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.TKIP);

            return (Boolean) method.invoke(mWifiManager, netConfig, true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean closeWifiAp() {
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
            method.setAccessible(true);
            WifiConfiguration config = (WifiConfiguration) method.invoke(mWifiManager);

            Method method2 = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            return (Boolean) method2.invoke(mWifiManager, config, false);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isWifiApEnable() {
        try {
            Method method = mWifiManager.getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(mWifiManager);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 判断热点是否启动
     *
     * @return
     */
    public int getWifiApState() {
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApState");
            method.setAccessible(true);
            return (Integer) method.invoke(mWifiManager);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public WifiConfiguration getWifiApConfiguration() {
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
            method.setAccessible(true);
            return (WifiConfiguration) method.invoke(mWifiManager);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String checkWifiApState(int state) {
        if (state == WifiApAdmin.WIFI_AP_STATE_DISABLED) return "Wi-Fi热点已关闭";
        else if (state == WifiApAdmin.WIFI_AP_STATE_DISABLING) return "Wi-Fi热点正在关闭";
        else if (state == WifiApAdmin.WIFI_AP_STATE_ENABLED) return "Wi-Fi热点已打开";
        else if (state == WifiApAdmin.WIFI_AP_STATE_ENABLING) return "Wi-Fi热点正在打开";
        else if (state == WifiApAdmin.WIFI_AP_STATE_FAILED) return "Wi-Fi热点打开失败";

        else return "未知状态";
    }

    /**
     * Wi-Fi AP is currently being disabled. The state will change to
     * {@link #WIFI_AP_STATE_DISABLED} if it finishes successfully.
     *
     * @see #WIFI_AP_STATE_CHANGED_ACTION
     * @see #getWifiApState()
     *
     * @hide
     */
    public static final int WIFI_AP_STATE_DISABLING = 10;
    /**
     * Wi-Fi AP is disabled.
     *
     * @see #WIFI_AP_STATE_CHANGED_ACTION
     *
     * @hide
     */
    public static final int WIFI_AP_STATE_DISABLED = 11;
    /**
     * Wi-Fi AP is currently being enabled. The state will change to
     * {@link #WIFI_AP_STATE_ENABLED} if it finishes successfully.
     *
     * @see #WIFI_AP_STATE_CHANGED_ACTION
     * @see #getWifiApState()
     *
     * @hide
     */
    public static final int WIFI_AP_STATE_ENABLING = 12;
    /**
     * Wi-Fi AP is enabled.
     *
     * @see #WIFI_AP_STATE_CHANGED_ACTION
     * @see #getWifiApState()
     *
     * @hide
     */
    public static final int WIFI_AP_STATE_ENABLED = 13;
    /**
     * Wi-Fi AP is in a failed state. This state will occur when an error occurs during
     * enabling or disabling
     *
     * @see #WIFI_AP_STATE_CHANGED_ACTION
     * @see #getWifiApState()
     *
     * @hide
     */
    public static final int WIFI_AP_STATE_FAILED = 14;

    /**
     * Broadcast intent action indicating that Wi-Fi AP has been enabled, disabled,
     * enabling, disabling, or failed.
     *
     * @hide
     */
    public static final String WIFI_AP_STATE_CHANGED_ACTION =
            "android.net.wifi.WIFI_AP_STATE_CHANGED";

    private WifiApReceiver mWifiApReceiver = new WifiApReceiver();

    /**
     * 用于检查Wi-Fi状态的变更
     */
    private class WifiApReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(WifiApAdmin.WIFI_AP_STATE_CHANGED_ACTION)) {
                AppLogger.e("监听到Wi-Fi热点状态变更，当前状态：" + checkWifiApState(getWifiApState()));
                if (mOnWifiApStateChange != null) {
                    mOnWifiApStateChange.handle(mAction, getWifiApState());
                }
                if (getWifiApState() == WIFI_AP_STATE_ENABLED &&
                        mAction.equals(ACTION_OPEN_WIFI_AP)) {
                    unregisterWifiApReceiver();
                } else if (getWifiApState() == WIFI_AP_STATE_DISABLED &&
                        mAction.equals(ACTION_CLOSE_WIFI_AP)) {
                    unregisterWifiApReceiver();
                }
            }
        }
    }

    public String mAction = "";
    public static final String ACTION_OPEN_WIFI_AP = "action_open_wifiap";
    public static final String ACTION_CLOSE_WIFI_AP = "action_close_wifiap";

    public interface OnWifiApStateChange {
        void handle(String action, int wifiApState);
    }

    private OnWifiApStateChange mOnWifiApStateChange;

    public void setOnWifiApStateChange(OnWifiApStateChange onWifiApStateChange) {
        this.mOnWifiApStateChange = onWifiApStateChange;
    }

    private void registerWifiApReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WIFI_AP_STATE_CHANGED_ACTION);
        mCtx.registerReceiver(mWifiApReceiver, filter);
    }

    private void unregisterWifiApReceiver() {
        try {
            mCtx.unregisterReceiver(mWifiApReceiver);
        } catch (Exception ex) {

        }
    }
}
