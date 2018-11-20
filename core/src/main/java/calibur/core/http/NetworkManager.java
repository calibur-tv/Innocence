package calibur.core.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import calibur.foundation.FoundationContextHolder;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/20 10:56 AM
 * version: 1.0
 * description:
 */
public class NetworkManager {

  public static String getConnectNetwork() {
    try {
      NetworkInfo networkInfo = ((ConnectivityManager) FoundationContextHolder.getContext().getSystemService(
          Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
      if (networkInfo != null && networkInfo.isConnected()) {
        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
          return "WiFi";
        } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
          TelephonyManager mTelephonyManager =
              (TelephonyManager) FoundationContextHolder.getContext().getSystemService(Context.TELEPHONY_SERVICE);
          int networkType = mTelephonyManager.getNetworkType();
          return getNetworkTypeName(networkType);
        } else {
          return "UNKNOWN";
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return "";
  }

  public static String getNetworkTypeName(int type) {
    switch (type) {
      case TelephonyManager.NETWORK_TYPE_GPRS:
        return "GPRS";
      case TelephonyManager.NETWORK_TYPE_EDGE:
        return "EDGE";
      case TelephonyManager.NETWORK_TYPE_UMTS:
        return "UMTS";
      case TelephonyManager.NETWORK_TYPE_HSDPA:
        return "HSDPA";
      case TelephonyManager.NETWORK_TYPE_HSUPA:
        return "HSUPA";
      case TelephonyManager.NETWORK_TYPE_HSPA:
        return "HSPA";
      case TelephonyManager.NETWORK_TYPE_CDMA:
        return "CDMA";
      case TelephonyManager.NETWORK_TYPE_EVDO_0:
        return "CDMA - EvDo rev. 0";
      case TelephonyManager.NETWORK_TYPE_EVDO_A:
        return "CDMA - EvDo rev. A";
      case TelephonyManager.NETWORK_TYPE_EVDO_B:
        return "CDMA - EvDo rev. B";
      case TelephonyManager.NETWORK_TYPE_1xRTT:
        return "CDMA - 1xRTT";
      case TelephonyManager.NETWORK_TYPE_LTE:
        return "LTE";
      case TelephonyManager.NETWORK_TYPE_EHRPD:
        return "CDMA - eHRPD";
      case TelephonyManager.NETWORK_TYPE_IDEN:
        return "iDEN";
      case TelephonyManager.NETWORK_TYPE_HSPAP:
        return "HSPA+";
      default:
        return "UNKNOWN";
    }
  }

  public static boolean isConnected() {
    ConnectivityManager conManager = (ConnectivityManager) FoundationContextHolder.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    if (conManager != null) {
      try {
        NetworkInfo e = conManager.getActiveNetworkInfo();
        if (e != null) {
          return e.isAvailable();
        }
      } catch (Exception ignored) {
      }
    }

    return false;
  }

}
