package com.lza.pad.support.utils;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.opengl.GLES10;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.HapticFeedbackConstants;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ShareActionProvider;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.lza.pad.R;
import com.lza.pad.support.debug.AppLogger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.NumberFormat;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public final class RuntimeUtility implements Consts {

    private RuntimeUtility() {
        // Forbidden being instantiated.
    }

    public static int dip2px(Context context, int dipValue) {
        float reSize = context.getResources().getDisplayMetrics().density;
        return (int) ((dipValue * reSize) + 0.5);
    }

    public static int px2dip(Context context, int pxValue) {
        float reSize = context.getResources().getDisplayMetrics().density;
        return (int) ((pxValue / reSize) + 0.5);
    }

    public static float sp2px(Context context, int spValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue,
                context.getResources().getDisplayMetrics());
    }

    public static int length(String paramString) {
        int i = 0;
        for (int j = 0; j < paramString.length(); j++) {
            if (paramString.substring(j, j + 1).matches("[Α-￥]")) {
                i += 2;
            } else {
                i++;
            }
        }

        if (i % 2 > 0) {
            i = 1 + i / 2;
        } else {
            i = i / 2;
        }

        return i;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }

    public static int getNetType(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return networkInfo.getType();
        }
        return -1;
    }

    public static boolean isGprs(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() != ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSystemRinger(Context context) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return manager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
    }

    public static String getPicPathFromUri(Uri uri, Activity activity) {
        String value = uri.getPath();

        if (value.startsWith("/external")) {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = activity.managedQuery(uri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else {
            return value;
        }
    }

    public static boolean isAllNotNull(Object... obs) {
        for (int i = 0; i < obs.length; i++) {
            if (obs[i] == null) {
                return false;
            }
        }
        return true;
    }

    public static boolean isIntentSafe(Activity activity, Uri uri) {
        Intent mapCall = new Intent(Intent.ACTION_VIEW, uri);
        PackageManager packageManager = activity.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(mapCall, 0);
        return activities.size() > 0;
    }

    public static boolean isIntentSafe(Activity activity, Intent intent) {
        PackageManager packageManager = activity.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        return activities.size() > 0;
    }

    public static boolean isGooglePlaySafe(Activity activity) {
        Uri uri = Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.gms");
        Intent mapCall = new Intent(Intent.ACTION_VIEW, uri);
        mapCall.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        mapCall.setPackage("com.android.vending");
        PackageManager packageManager = activity.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(mapCall, 0);
        return activities.size() > 0;
    }

    public static boolean isSinaWeiboSafe(Activity activity) {
        Intent mapCall = new Intent("com.sina.weibo.remotessoservice");
        PackageManager packageManager = activity.getPackageManager();
        List<ResolveInfo> services = packageManager.queryIntentServices(mapCall, 0);
        return services.size() > 0;
    }

    public static String buildTabText(int number) {

        if (number == 0) {
            return null;
        }

        String num;
        if (number < 99) {
            num = "(" + number + ")";
        } else {
            num = "(99+)";
        }
        return num;

    }

    /*
    public static boolean isJB() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean isJB1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    public static boolean isJB2() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    public static boolean isKK() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }*/

    public static boolean isL() {
        return Build.VERSION.SDK_INT >= 20;
    }

    private static DisplayMetrics metrics = null;
    public static int getScreenWidth(Activity activity) {
        if (metrics != null) return metrics.widthPixels;
        else metrics = new DisplayMetrics();
        if (activity != null) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            display.getMetrics(metrics);
            return metrics.widthPixels;
        }

        return 480;
    }

    public static int getScreenHeight(Activity activity) {
        if (metrics != null) return metrics.heightPixels;
        else metrics = new DisplayMetrics();
        if (activity != null) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            display.getMetrics(metrics);
            return metrics.heightPixels;
        }
        return 800;
    }

    public static Uri getLatestCameraPicture(Activity activity) {
        String[] projection = new String[]{MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE
        };
        final Cursor cursor = activity.getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection, null, null,
                        MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
        if (cursor.moveToFirst()) {
            String path = cursor.getString(1);
            return Uri.fromFile(new File(path));
        }
        return null;
    }

    public static void copyFile(InputStream in, File destFile) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
        FileOutputStream outputStream = new FileOutputStream(destFile);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = bufferedInputStream.read(buffer)) != -1) {
            bufferedOutputStream.write(buffer, 0, len);
        }
        UniversalUtility.closeSilently(bufferedInputStream);
        UniversalUtility.closeSilently(bufferedOutputStream);
    }

    public static Rect locateView(View v) {
        int[] location = new int[2];
        if (v == null) {
            return null;
        }
        try {
            v.getLocationOnScreen(location);
        } catch (NullPointerException npe) {
            //Happens when the view doesn't exist on screen anymore.
            return null;
        }
        Rect locationRect = new Rect();
        locationRect.left = location[0];
        locationRect.top = location[1];
        locationRect.right = locationRect.left + v.getWidth();
        locationRect.bottom = locationRect.top + v.getHeight();
        return locationRect;
    }

    public static int countWord(String content, String word, int preCount) {
        int count = preCount;
        int index = content.indexOf(word);
        if (index == -1) {
            return count;
        } else {
            count++;
            return countWord(content.substring(index + word.length()), word, count);
        }
    }

    public static void setShareIntent(Activity activity, ShareActionProvider mShareActionProvider,
            String content) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        if (RuntimeUtility.isIntentSafe(activity, shareIntent) && mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }

    }

    public static void buildTabCount(ActionBar.Tab tab, String tabStrRes, int count) {
        if (tab == null) {
            return;
        }
        String content = tab.getText().toString();
        int value = 0;
        int start = content.indexOf("(");
        int end = content.lastIndexOf(")");
        if (start > 0) {
            String result = content.substring(start + 1, end);
            value = Integer.valueOf(result);
        }
        if (value <= count) {
            tab.setText(tabStrRes + "(" + count + ")");
        }
    }

    public static void vibrate(Context context, View view) {
//        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//        vibrator.vibrate(30);
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
    }

    public static void playClickSound(View view) {
        view.playSoundEffect(SoundEffectConstants.CLICK);
    }

    public static View getListViewItemViewFromPosition(ListView listView, int position) {
        return listView.getChildAt(position - listView.getFirstVisiblePosition());
    }

    public static void setListViewSelectionFromTop(final ListView listView,
            final int positionAfterRefresh, final int top, final Runnable runnable) {
        listView.getViewTreeObserver()
                .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        listView.getViewTreeObserver().removeOnPreDrawListener(this);
                        listView.setSelectionFromTop(positionAfterRefresh, top);
                        if (runnable != null) {
                            runnable.run();
                        }
                        return false;
                    }
                });
    }

    public static void setListViewSelectionFromTop(final ListView listView,
            final int positionAfterRefresh, final int top) {
        setListViewSelectionFromTop(listView, positionAfterRefresh, top, null);
    }

    public static boolean isDevicePort(Context context) {
        return context.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT;
    }

    public static String convertStateNumberToString(Context context, String numberStr) {
        int thousandInt = 1000;
        int tenThousandInt = thousandInt * 10;
        int number = Integer.valueOf(numberStr);
        if (number == tenThousandInt) {
            return String
                    .valueOf((number / tenThousandInt) + context.getString(R.string.ten_thousand));
        }
        if (number > tenThousandInt) {
            String result = String
                    .valueOf((number / tenThousandInt) + context.getString(R.string.ten_thousand));
            if (number > tenThousandInt * 10) {
                return result;
            }
            String thousand = String.valueOf(numberStr.charAt(numberStr.length() - 4));
            if (Integer.valueOf(thousand) != 0) {
                result += thousand;
            }
            return result;
        }
        if (number > thousandInt) {
            NumberFormat nf = NumberFormat.getNumberInstance();
            return nf.format(Long.valueOf(number));
        }
        return String.valueOf(number);
    }

    public static String getMotionEventStringName(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                return "MotionEvent.ACTION_DOWN";
            case MotionEvent.ACTION_UP:
                return "MotionEvent.ACTION_UP";
            case MotionEvent.ACTION_CANCEL:
                return "MotionEvent.ACTION_CANCEL";
            case MotionEvent.ACTION_MOVE:
                return "MotionEvent.ACTION_MOVE";
            default:
                return "Other";
        }
    }

    public static int getMaxLeftWidthOrHeightImageViewCanRead(int heightOrWidth) {
        //1pixel==4bytes http://stackoverflow.com/questions/13536042/android-bitmap-allocating-16-bytes-per-pixel
        //http://stackoverflow.com/questions/15313807/android-maximum-allowed-width-height-of-bitmap
        int[] maxSizeArray = new int[1];
        GLES10.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, maxSizeArray, 0);

        if (maxSizeArray[0] == 0) {
            GLES10.glGetIntegerv(GL11.GL_MAX_TEXTURE_SIZE, maxSizeArray, 0);
        }
        int maxHeight = maxSizeArray[0];
        int maxWidth = maxSizeArray[0];

        return (maxHeight * maxWidth) / heightOrWidth;
    }

    //sometime can get value, sometime can't, so I define it is 2048x2048
    public static int getBitmapMaxWidthAndMaxHeight() {
        int[] maxSizeArray = new int[1];
        GLES10.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, maxSizeArray, 0);

        if (maxSizeArray[0] == 0) {
            GLES10.glGetIntegerv(GL11.GL_MAX_TEXTURE_SIZE, maxSizeArray, 0);
        }
//        return maxSizeArray[0];
        return 2048;
    }

    public static void recycleViewGroupAndChildViews(ViewGroup viewGroup, boolean recycleBitmap) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {

            View child = viewGroup.getChildAt(i);

            if (child instanceof WebView) {
                WebView webView = (WebView) child;
                webView.loadUrl("about:blank");
                webView.stopLoading();
                continue;
            }

            if (child instanceof ViewGroup) {
                recycleViewGroupAndChildViews((ViewGroup) child, true);
                continue;
            }

            if (child instanceof ImageView) {
                ImageView iv = (ImageView) child;
                Drawable drawable = iv.getDrawable();
                if (drawable instanceof BitmapDrawable) {
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    if (recycleBitmap && bitmap != null) {
                        bitmap.recycle();
                    }
                }
                iv.setImageBitmap(null);
                iv.setBackgroundDrawable(null);
                continue;
            }

            child.setBackgroundDrawable(null);

        }

        viewGroup.setBackgroundDrawable(null);
    }

    public static boolean doThisDeviceOwnNavigationBar(Context context) {
        boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        return !hasMenuKey && !hasBackKey;
    }

    /**
     * https://svn.apache.org/repos/asf/cayenne/main/branches/cayenne-jdk1.5-generics-unpublished/src/main/java/org/apache/cayenne/conf/Rot47PasswordEncoder.java
     */
    public static String rot47(String value) {
        int length = value.length();
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);

            // Process letters, numbers, and symbols -- ignore spaces.
            if (c != ' ') {
                // Add 47 (it is ROT-47, after all).
                c += 47;

                // If character is now above printable range, make it printable.
                // Range of printable characters is ! (33) to ~ (126).  A value
                // of 127 (just above ~) would therefore get rotated down to a
                // 33 (the !).  The value 94 comes from 127 - 33 = 94, which is
                // therefore the value that needs to be subtracted from the
                // non-printable character to put it into the correct printable
                // range.
                if (c > '~') {
                    c -= 94;
                }
            }

            result.append(c);
        }

        return result.toString();
    }


    //if app's certificate md5 is correct, enable Crashlytics crash log platform, you should not modify those md5 values
    public static boolean isCertificateFingerprintCorrect(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            String packageName = context.getPackageName();
            int flags = PackageManager.GET_SIGNATURES;

            PackageInfo packageInfo = pm.getPackageInfo(packageName, flags);

            Signature[] signatures = packageInfo.signatures;

            //Debug Code
            int index = 0;
            for (Signature s : signatures) {
                AppLogger.e("signatures[" + index + "]=" + new String(s.toChars()));
                index++;
            }

            byte[] cert = signatures[0].toByteArray();

            String strResult = "";

            MessageDigest md;

            md = MessageDigest.getInstance("MD5");
            md.update(cert);
            for (byte b : md.digest()) {
                strResult += Integer.toString(b & 0xff, 16);
            }
            strResult = strResult.toUpperCase();
            //debug
            if ("DE421D82D4BBF9042886E72AA31FE22".toUpperCase().equals(strResult)) {
                return false;
            }
            //relaease
            if ("C96155C3DAD4CA1069808FBAC813A69".toUpperCase().equals(strResult)) {
                return true;
            }
            AppLogger.e(strResult);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch (PackageManager.NameNotFoundException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public static void runUIActionDelayed(Runnable runnable, long delayMillis) {
        new Handler(Looper.getMainLooper()).postDelayed(runnable, delayMillis);
    }

    public static Bitmap getBitmapFromView(View view) {

        Bitmap b = Bitmap.createBitmap(
                view.getWidth(), view.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        c.translate(-view.getScrollX(), -view.getScrollY());
        view.draw(c);
        return b;
    }

    /**
     * 用字符串生成二维码
     *
     * @param str
     * @author zhouzhe@lenovo-cw.com
     * @return
     */
    public static Bitmap Create2DCode(String str, int mapWidth, int mapHeight) {
        // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败

        String utf8Str = str;
        try {
            utf8Str = new String(str.getBytes("utf-8"), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        BitMatrix matrix = null;
        try {
            matrix = new MultiFormatWriter().encode(utf8Str,
                    BarcodeFormat.QR_CODE, mapWidth, mapHeight);
            int width = matrix.getWidth();
            int height = matrix.getHeight();
            // 二维矩阵转为一维像素数组,也就是一直横着排了
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (matrix.get(x, y)) {
                        pixels[y * width + x] = 0xff000000;
                    }

                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            // 通过像素数组生成bitmap,具体参考api
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @author:baihe
     * @param context
     * @param key
     * @param defaultValue
     * @向sharePreference中存放值
     */
    public static void putToSP(Context context, String key, Object defaultValue) {
        SharedPreferences preferences = context.getSharedPreferences(SP_NAME, SP_MODE);
        SharedPreferences.Editor editor = preferences.edit();

        if (defaultValue instanceof String) {
            editor.putString(key, (String) defaultValue);
        } else if (defaultValue instanceof Boolean){
            editor.putBoolean(key, (Boolean) defaultValue);
        } else if (defaultValue instanceof Integer) {
            editor.putInt(key, (Integer) defaultValue);
        }
        editor.commit();
    }

    /**
     * @author:baihe
     * @param context
     * @param key
     * @取出sharePreference中存放的值
     */
    public static String getFromSP(Context context, String key, String defaultValue) {
        String value = null;
        SharedPreferences preferences = context.getSharedPreferences(SP_NAME, SP_MODE);
        value = preferences.getString(key, defaultValue);
        return value;
    }

    public static boolean getFromSP(Context context, String key, boolean defaultValue) {
        boolean value = false;
        SharedPreferences preferences = context.getSharedPreferences(SP_NAME, SP_MODE);
        value = preferences.getBoolean(key, defaultValue);
        return value;
    }

    /**
     * 生成带参数的文本
     *
     * @param resId
     * @return
     */
    public static String generateText(Context context, int resId, Object... args) {
        return String.format(context.getString(resId), args);
    }

    public static String parseSignature(byte[] signature) {
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(
                    new ByteArrayInputStream(signature));
            String pubKey = cert.getPublicKey().toString();
            String signNumber = cert.getSerialNumber().toString();
            AppLogger.e("signName:" + cert.getSigAlgName());
            AppLogger.e("pubKey:" + pubKey);
            AppLogger.e("signNumber:" + signNumber);
            AppLogger.e("subjectDN:"+ cert.getSubjectDN().toString());
            return pubKey;
        } catch (CertificateException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 隐藏键盘
     */
    public static void hideKeyBoard(Context _context, EditText searchText) {
        InputMethodManager immDefault = (InputMethodManager) _context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        immDefault.hideSoftInputFromWindow(searchText.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 隐藏键盘
     * @param _context
     */
    public static void hideKeyBoard(Activity _context) {
        View view = _context.getWindow().getDecorView();
        InputMethodManager immDefault = (InputMethodManager) _context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        immDefault.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 判断软键盘是否显示
     */
    public static boolean isKeyBoardShow(Context _context) {
        InputMethodManager immDefault = (InputMethodManager) _context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        return immDefault.isActive();
    }
    /**
     * 设置标准
     */
    public static void setKeyBoardFlag(Context _context){
        InputMethodManager imm = (InputMethodManager) _context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 显示键盘
     */
    public static void showKeyBoard(Activity _context) {//, EditText searchText) {
        InputMethodManager immDefault = (InputMethodManager) _context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        //immDefault.showSoftInputFromInputMethod(searchText.getWindowToken(), 0);
        View view = _context.getWindow().getDecorView();
        immDefault.showSoftInputFromInputMethod(view.getWindowToken(), InputMethodManager.SHOW_FORCED);
    }

    public static String getDateDiffrenceStr(long searchDate) {
        String dateStr;
        long nowDate = System.currentTimeMillis();

        long difference = nowDate - searchDate;
        long day = difference / ONE_DAY;
        if (day > 0) {
            if (day >= 365) {
                day = day % 365;
                dateStr = day + "年前";
            } else if (day >= 30 && day < 365) {
                day = day % 30;
                dateStr = day + "个月前";
            } else {
                dateStr = day + "天前";
            }
        } else {
            long hour = difference / ONE_HOUR;
            if (hour > 0) {
                dateStr = hour + "小时前";
            } else {
                long minute = difference / ONE_MINUTE;
                if (minute > 0) {
                    dateStr = minute + "分钟前";
                } else {
                    dateStr = "小于1分钟";
                }
            }
        }
        return dateStr;
    }

    public static boolean removeFragmentByTag(FragmentManager fm, String tag) {
        Fragment fragment = fm.findFragmentByTag(tag);
        boolean flag = false;
        if (fragment != null) {
            fm.beginTransaction()
                .remove(fragment)
                .commit();
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

    private static long lastClickTime = 0;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static boolean root() {
        boolean hasRoot = false;
        try {
            //获取Root权限
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("exit\n");
            os.flush();
            int exitValue = process.waitFor();
            if (exitValue == 0) {
                hasRoot = true;
            }
            process.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return hasRoot;
    }

    public static void execShellCmd(String cmd) {
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

