package gy.android.util;

import android.util.Log;

import gy.android.BuildConfig;

// http://www.jianshu.com/p/325e8f025c98
public class LogUtil {

    public static String TAG = "template.";

    public static final int VERBOSE = 1;

    public static final int DEBUG = 2;

    public static final int INFO = 3;

    public static final int WARN = 4;

    public static final int ERROR = 5;

    public static final int NOTHING = 6;

    public static int LEVEL = BuildConfig.DEBUG ? VERBOSE : WARN;

    public static void v(Object target, String msg) {
        log(VERBOSE, convertTag(target), msg);
    }

    public static void d(Object target, String msg) {
        log(DEBUG, convertTag(target), msg);
    }

    public static void i(Object target, String msg) {
        log(INFO, convertTag(target), msg);
    }

    public static void w(Object target, String msg) {
        log(WARN, convertTag(target), msg);
    }

    public static void e(Object target, String msg) {
        log(ERROR, convertTag(target), msg);
    }

    public static void v(Object target, String msg, Throwable tr) {
        log(VERBOSE, convertTag(target), msg, tr);
    }

    public static void d(Object target, String msg, Throwable tr) {
        log(DEBUG, convertTag(target), msg, tr);
    }

    public static void i(Object target, String msg, Throwable tr) {
        log(INFO, convertTag(target), msg, tr);
    }

    public static void w(Object target, String msg, Throwable tr) {
        log(WARN, convertTag(target), msg, tr);
    }

    public static void e(Object target, String msg, Throwable tr) {
        log(ERROR, convertTag(target), msg, tr);
    }

    private static String convertTag(Object target) {
        if (target instanceof String) {
            return TAG + target.toString();
        } else {
            return TAG + target.getClass().getSimpleName();
        }
    }

    private static void log(int type, String tag, String msg) {
        log(type, tag, msg, null);
    }

    private static void log(int type, String tag, String msg, Throwable tr) {
        switch (type) {
            case VERBOSE:
                if (LEVEL <= VERBOSE) {
                    if (tr == null) {
                        Log.v(tag, msg);
                    } else {
                        Log.v(tag, msg, tr);
                    }
                }
                break;
            case DEBUG:
                if (LEVEL <= DEBUG) {
                    if (tr == null) {
                        Log.d(tag, msg);
                    } else {
                        Log.d(tag, msg, tr);
                    }
                }
                break;
            case INFO:
                if (LEVEL <= INFO) {
                    if (tr == null) {
                        Log.i(tag, msg);
                    } else {
                        Log.i(tag, msg, tr);
                    }
                }
                break;
            case WARN:
                if (LEVEL <= WARN) {
                    if (tr == null) {
                        Log.w(tag, msg);
                    } else {
                        Log.w(tag, msg, tr);
                    }
                }
                break;
            case ERROR:
                if (LEVEL <= ERROR) {
                    if (tr == null) {
                        Log.w(tag, msg);
                    } else {
                        Log.w(tag, msg, tr);
                    }
                }
                break;
        }
    }

}
