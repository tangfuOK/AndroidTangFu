package com.talon.screen.quick.util;

import android.util.Log;


/**
 * Created by talon on 2019/11/19
 * note: 日志类
 */
public class LogWrapper {

    /**
     * 调试开关，无论是自动还是手动都能很好的控制调试状态
     */
//    public static final boolean DEBUG = BuildConfig.DEBUG;
    public static final boolean DEBUG = true;
    private static final String TAG = "LogWrapper";

    public static void v(String tag, String msg) {
        if (DEBUG)
            Log.v(tag, msg);
    }

    public static void d( String msg) {
        if (DEBUG)
            Log.d(TAG, msg);
    }

    public static void d(String tag, String msg) {
        if (DEBUG)
            Log.d(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (DEBUG)
            Log.i(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (DEBUG)
            Log.w(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (DEBUG)
            Log.e(tag, msg);
    }

    public static void e(String msg) {
        if (DEBUG)
            Log.e(TAG, msg);
    }

    public static void w(String tag, String msg, Throwable ex) {
        if (DEBUG)
            Log.w(tag, msg, ex);
    }

    public static void e(String tag, String msg, Throwable ex) {
        if (DEBUG)
            Log.e(tag, msg, ex);
    }

}
