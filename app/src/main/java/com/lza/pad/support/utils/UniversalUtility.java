package com.lza.pad.support.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;

import com.lza.pad.support.debug.AppLogger;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;

/**
 * Created by xiads on 14-9-7.
 */
public class UniversalUtility {

    /**
     * 关闭IO
     *
     * @param io
     */
    public static void close(Closeable io) {
        if (io != null) {
            try {
                io.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭IO,不抛出异常
     *
     * @param closeable
     */
    public static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {

            }
        }
    }

    /**
     * 打印数组
     *
     * @param arr
     */
    public static void printArray(String[] arr) {
        for (String str : arr) {
            AppLogger.e("str-->" + str);
        }
    }

    /**
     * 遍历Cursor中的信息，并打印字段名和字段值到Logcat中
     *
     * @param cursor
     */
    public static void printCursor(Cursor cursor) {
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int colCount = cursor.getColumnCount();
                    for (int i = 0; i < colCount; i++) {
                        String colName = cursor.getColumnName(i);
                        AppLogger.e(colName + "-->" + cursor.getString(i));
                    }
                } while (cursor.moveToNext());
                cursor.moveToFirst();
            }
        }
    }

    /**
     * 遍历文件夹，打印所有的文件和文件夹
     *
     * @param dir
     */
    public static void printDir(File dir) {
        if (dir != null && dir.exists() && dir.isDirectory()) {
            printFiles(dir.listFiles());
        }
    }

    /**
     * 打印文件数组
     *
     * @param files
     */
    public static void printFiles(File[] files) {
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    printFiles(f.listFiles());
                }else if (f.isFile()) {
                    AppLogger.d("[" + f.getName() + "]-->[" + f.getAbsolutePath() + "]");
                }else {
                    continue;
                }
            }
        }
    }

    /**
     * URL编码
     *
     * @param s
     * @return
     */
    public static Bundle decodeUrl(String s) {
        Bundle params = new Bundle();
        if (s != null) {
            String array[] = s.split("&");
            for (String parameter : array) {
                String v[] = parameter.split("=");
                try {
                    params.putString(URLDecoder.decode(v[0], "UTF-8"),
                            URLDecoder.decode(v[1], "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();

                }
            }
        }
        return params;
    }

    /**
     * URL解码
     *
     * @param param
     * @return
     */
    public static String encodeUrl(Map<String, String> param) {
        if (param == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Set<String> keys = param.keySet();
        boolean first = true;

        for (String key : keys) {
            String value = param.get(key);
            //pain...EditMyProfileDao params' values can be empty
            if (value != null || key.equals("description") || key.equals("url")) {
                if (first) {
                    first = false;
                } else {
                    sb.append("&");
                }
                try {
                    sb.append(URLEncoder.encode(key, "UTF-8")).append("=")
                            .append(URLEncoder.encode(param.get(key), "UTF-8"));
                } catch (UnsupportedEncodingException e) {

                }
            }


        }

        return sb.toString();
    }

    /**
     * Parse a URL query and fragment parameters into a key-value bundle.
     */
    public static Bundle parseUrl(String url) {
        // hack to prevent MalformedURLException
        url = url.replace("weiboconnect", "http");
        try {
            URL u = new URL(url);
            Bundle b = decodeUrl(u.getQuery());
            b.putAll(decodeUrl(u.getRef()));
            return b;
        } catch (MalformedURLException e) {
            return new Bundle();
        }
    }

    /**
     * 判断map是否为空
     *
     * @param map
     * @return
     */
    public static boolean isMapEmpty(Map map) {
        return map == null || map.size() == 0;
    }

    /**
     * 判断当前的时间是白天还是黑夜
     *
     * @return
     */
    public static boolean isDayOrNight() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 0 && hour < 18)
            return true;
        else
            return false;
    }

    /**
     * 获取屏幕尺寸
     *
     * @param activity
     * @return
     */
    public static Point getPoint(Activity activity) {
        //获取屏幕分辨率
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        return point;
    }

    /**
     * 设备ID
     */
    private static String sDeviceId = null;

    /**
     * 版本名称
     */
    private static String sVersionName = null;

    /**
     * 网络类型
     */
    private static String sNetworkType = null;

    /**
     * 获取设备ID
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        if (sDeviceId == null){
            TelephonyManager telManager = (TelephonyManager)
                    context.getSystemService(Context.TELEPHONY_SERVICE);
            sDeviceId = telManager.getDeviceId();
        }
        return sDeviceId;
    }

    /**
     * 获取版本名称
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        if (sVersionName == null) {
            PackageManager pm = context.getPackageManager();
            try {
                PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
                sVersionName = pi.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return sVersionName;
    }

    /**
     * 获取网络类型
     *
     * @param context
     * @return
     */
    public static String getNetworkType(Context context) {
        if (sNetworkType == null) {
            ConnectivityManager connManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkinfo = connManager.getActiveNetworkInfo();
            if (networkinfo != null) {
                sNetworkType = networkinfo.getTypeName();
            }
        }
        return sNetworkType;
    }

    /**
     * 设置View是否可见,0-不可见,1-可见
     *
     * @param view
     * @param isViewVisble
     */
    public static void setViewVisibility(View view, int isViewVisble) {
        if (isViewVisble == 1)
            setViewVisbility(view, true);
        else
            setViewVisbility(view, false);
    }

    /**
     * 设置View是否可见,true-可见,false-不可见
     *
     * @param view
     * @param isVisble
     */
    public static void setViewVisbility(View view, boolean isVisble) {
        if (view != null) {
            if (isVisble)
                view.setVisibility(View.VISIBLE);
            else
                view.setVisibility(View.GONE);
        }
    }

    /**
     * 显示Dialog
     */
    public static AlertDialog createDialog(Context context, int title, int message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("确定", okListener)
                .setNegativeButton("取消", cancelListener)
                .create();
    }

    public static void showDialog(Context context, int title, int message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("确定", okListener)
                .setNegativeButton("取消", cancelListener)
                .show();
    }

    public static void showDialog(Context context, String title, String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("确定", okListener)
                .setNegativeButton("取消", cancelListener)
                .show();
    }

    public static void showDialog(Context context, String title, String message, DialogInterface.OnClickListener okListener) {
        showDialog(context, title, message, okListener,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
        });
    }

    public static void showDialog(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public static int getIntHalfUp(float f) {
        BigDecimal bd = new BigDecimal(f).setScale(BigDecimal.ROUND_HALF_UP);
        return bd.intValue();
    }

    public static float getFloatByHalfUp(float f) {
        BigDecimal bd = new BigDecimal(f).setScale(2, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    /**
     * 获得超类的参数类型，取第一个参数类型
     * @param <T> 类型参数
     * @param clazz 超类类型
     */
    @SuppressWarnings("rawtypes")
    public static <T> Class<T> getClassGenricType(final Class clazz) {
        return getClassGenricType(clazz, 0);
    }

    /**
     * 根据索引获得超类的参数类型
     * @param clazz 超类类型
     * @param index 索引
     */
    @SuppressWarnings("rawtypes")
    public static Class getClassGenricType(final Class clazz, final int index) {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] params = ((ParameterizedType)genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return (Class) params[index];
    }

    /**
     * Mac地址
     */
    private static String sMacAddress = null;

    /**
     * 获取mac地址
     *
     * @param context
     * @return
     */
    public static String getMacAddress(Context context) {
        if (sMacAddress == null) {
            sMacAddress = getLocalMacAddress(context);
            //sMacAddress = getMacAddressWithoutNetwork(context);
        }
        return sMacAddress;
    }

    private static String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    public static String wrap(String value, String defaultValue) {
        return TextUtils.isEmpty(value) ? defaultValue : value;
    }

    public static int safeIntParse(String digitValue, int defaultValue) {
        try {
            return Integer.parseInt(digitValue);
        } catch (Exception ex) {
            return defaultValue;
        }
    }
}
