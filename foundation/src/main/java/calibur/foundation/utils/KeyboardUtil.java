package calibur.foundation.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Author：J.Chou
 * Date：  2016.07.20 18:58.
 * Email： who_know_me@163.com
 * Describe:
 */
public final class KeyboardUtil {

  public static void showKeyboard(Context mContext) {
    if (mContext == null) return;

    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
    if(imm != null) imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
  }

  public static void hideKeyboard(Context context) {
    View view = ((Activity) context).getCurrentFocus();
    if (view != null) {
      InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
      if(inputManager != null) inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
  }

  public static void hideKeyboard(Context mContext, View v) {
    if (mContext == null) return;
    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
    if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
  }

  public static void hideKeyBoard(View view) {
    try {
      Context context = view.getContext();
      ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
  }

  public static void showKeyboard(final View view) {
    new Handler().postDelayed(new Runnable() {
      public void run() {
        view.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
        view.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
      }
    }, 200);
  }

  public static void showKeyboardWithCallback(final View view /*, final SingleCallback<Object,Object> cb*/) {
    new Handler().postDelayed(new Runnable() {
      public void run() {
        view.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
        view.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
        //if(cb != null)
        //cb.onSingleCallback(null,null);
      }
    }, 200);
  }
}
