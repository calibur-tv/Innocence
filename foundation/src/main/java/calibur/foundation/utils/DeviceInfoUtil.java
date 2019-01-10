package calibur.foundation.utils;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import calibur.foundation.FoundationContextHolder;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/12/25 12:05 AM
 * version: 1.0
 * description:
 */
public final class DeviceInfoUtil {

  private static int mScreenHeight;
  private static int mScreenWidth;

  public static int getScreenHeight() {
    if (mScreenHeight == 0) {
      initDeviceDimension(FoundationContextHolder.getContext());
    }
    return mScreenHeight;
  }

  public static int getScreenWidth() {
    if (mScreenWidth == 0) {
      initDeviceDimension(FoundationContextHolder.getContext());
    }
    return mScreenWidth;
  }

  private static void initDeviceDimension(Context context) {
    try {
      WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
      if (windowManager != null) {
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
          display.getRealMetrics(metrics);
          mScreenWidth = metrics.widthPixels;
          mScreenHeight = metrics.heightPixels;
        } else {
          Point size = new Point();
          display.getSize(size);
          mScreenWidth = size.x;
          mScreenHeight = size.y;
        }
      }
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
  }
}
