package com.lza.pad.app.wifi.admin;

import android.content.Context;
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

    public static WifiApAdmin instance(Context context) {
        if (mInstance == null) {
            mInstance = new WifiApAdmin(context);
        }
        return mInstance;
    }

    private static WifiManager mWifiManager = null;

    private static WifiConfiguration mWifiConfig = null;

    private Context mContext = null;

    private WifiApAdmin(Context context) {
        this.mContext = context;
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
    }

    private String mSSID = "";
    private String mPassword = "";

    public void startWifiAp(String ssid, String password, OnWifiApStartListener listener) {
        setOnWifiApStartListener(listener);
        startWifiAp(ssid, password);
    }

    public void startWifiAp(String ssid, String password) {
        mSSID = ssid;
        mPassword = password;

        if (mWifiManager.isWifiEnabled())
            mWifiManager.setWifiEnabled(false);

        startWifiAp();

        WifiTimerCheck timerCheck = new WifiTimerCheck() {
            @Override
            public void onTimerCheck() {
                if (isWifiApEnable(mWifiManager)) {
                    AppLogger.e("Wifi enabled successfully!");
                    //ToastUtils.showShort(mContext, "热点启动成功！");
                    exit();
                    if (mListener != null)
                        mListener.onWifiApSuccess();
                } else {

                }
            }

            @Override
            public void onTimeout() {
                exit();
                AppLogger.e("Wifi enabled failed!");
                //ToastUtils.showShort(mContext, "热点启动失败！");
                if (mListener != null)
                    mListener.onWifiApFailed();
            }
        };
        timerCheck.start(15, 1000);
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

    private void startWifiAp() {
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

            method.invoke(mWifiManager, netConfig, true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private boolean closeWifiAp(WifiManager wifiManager) {
        if (isWifiApEnable(wifiManager)) {
            try {
                Method method = wifiManager.getClass().getMethod("getWifiApConfiguration");
                method.setAccessible(true);

                WifiConfiguration config = (WifiConfiguration) method.invoke(wifiManager);

                Method method2 = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                return (Boolean) method2.invoke(wifiManager, config, false);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean isWifiApEnable() {
        //if (mWifiManager == null)
        //    mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return isWifiApEnable(mWifiManager);
    }

    private boolean isWifiApEnable(WifiManager wifiManager) {
        try {
            Method method = wifiManager.getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifiManager);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean closeWifiAp() {
        //if (mWifiManager == null)
        //    mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return closeWifiAp(mWifiManager);
    }

    public interface OnWifiApStartListener {
        void onWifiApSuccess();
        void onWifiApFailed();
    }

    private OnWifiApStartListener mListener;

    public void setOnWifiApStartListener(OnWifiApStartListener listener) {
        mListener = listener;
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
}
