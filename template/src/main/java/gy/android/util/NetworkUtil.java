package gy.android.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * https://blog.csdn.net/oQiHaoGongYuan/article/details/50911415
 */

public class NetworkUtil {

    public static final int NETWORK_NONE = 0;
    public static final int NETWORK_WIFI = 1;
    public static final int NETWORK_2G = 2;
    public static final int NETWORK_3G = 3;
    public static final int NETWORK_4G = 4;
    public static final int NETWORK_MOBILE = 5;

    public static int getNetworkState(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return NETWORK_NONE;
        }

        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            return NETWORK_NONE;
        }

        // wifi
        NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnectedOrConnecting()) {
            return NETWORK_WIFI;
        }

        // mobile
        NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileInfo != null && mobileInfo.isConnectedOrConnecting()) {
            switch (mobileInfo.getSubtype()) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return NETWORK_2G;
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                    return NETWORK_3G;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return NETWORK_4G;
                default:
                    String name = mobileInfo.getSubtypeName();
                    if (name.equalsIgnoreCase("TD-SCDMA") ||
                            name.equalsIgnoreCase("WCDMA") ||
                            name.equalsIgnoreCase("CDMA2000")) {
                        return NETWORK_3G;
                    }
                    return NETWORK_MOBILE;
            }
        }
        return NETWORK_NONE;
    }

    public static boolean isWifiConnected(Context context) {
        return getNetworkState(context) == NETWORK_WIFI;
    }

    public static boolean isMobileConnected(Context context) {
        int state = getNetworkState(context);
        return state == NETWORK_2G || state == NETWORK_3G || state == NETWORK_4G || state == NETWORK_MOBILE;
    }

    public static boolean isNetworkConnected(Context context) {
        return getNetworkState(context) != NETWORK_NONE;
    }
}
