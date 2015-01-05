package com.lza.pad.support.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast的工具类
 *
 * @author Sam
 * @Date 2014-8-19
 */
public class ToastUtils {

    public static final int S = Toast.LENGTH_SHORT;
    public static final int L = Toast.LENGTH_LONG;

    /**
     * 内部方法，显示指定内容、延迟的Toast
     *
     * @param c
     * @param m
     * @param d
     */
    public static void t(Context c, String m, int d) {
        Toast.makeText(c, m, d).show();
    }

    public static void t(Context c, int r, int d) {
        Toast.makeText(c, r, d).show();
    }

    /**
     * 展示较短延迟的Toast
     *
     * @param c
     * @param m
     */
    public static void showShort(Context c, String m) {
        t(c, m, S);
    }

    /**
     * 展示较短延迟的Toast
     *
     * @param c
     * @param r
     */
    public static void showShort(Context c, int r) {
        t(c, r, S);
    }

    /**
     * 展示较长延迟的Toast
     *
     * @param c
     * @param m
     */
    public static void showLong(Context c, String m) {
        t(c, m, L);
    }

    /**
     * 展示较长延迟的Toast
     *
     * @param c
     * @param r
     */
    public static void showLong(Context c, int r) {
        t(c, r, L);
    }

    /**
     * 展示用于测试的Toast
     *
     * @param c
     */
    public static void test(Context c) {
        t(c, "It's work!", S);
    }

}
