package com.riuir.calibur.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.riuir.calibur.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class Huder {
    /**
     * Grace period is the time (in milliseconds) that the invoked method may be run without
     * showing the HUD. If the task finishes before the grace time runs out, the HUD will
     * not be shown at all.
     * This may be used to prevent HUD display for very short tasks.
     * Defaults to 0 (no grace time).
     *
     * @param graceTimeMs Grace time in milliseconds
     * @return Current HUD
     */
    private static int graceInterval = 0;

    /**
     * Hud Duration is the time (in milliseconds) that the hud display.
     */
    private static int hudDuration = 1500;

    /**
     * 默认加载hud message
     */
    private static String defaultProgressMessage = "正在加载...";

    /**
     * 成功hud 弹框，显示"✔️" icon 和自定义message
     *
     * @param context
     * @param message
     * @param listener
     */
    public static void hudSuccess(@NonNull Context context,
                                  @NonNull String message,
                                  @Nullable OnHudDismissListener listener) {
        hudShow(context, message, R.mipmap.ic_hud_success, listener);
    }

    public static void hudSuccess(@NonNull Context context,
                                  @NonNull String message) {
        hudSuccess(context, message, null);
    }


    /**
     * 失败、错误hud 弹框，显示"❌" icon 和自定义message
     *
     * @param context
     * @param message
     * @param listener
     */
    public static void hudError(@NonNull Context context,
                                @NonNull String message,
                                @Nullable OnHudDismissListener listener) {
        hudShow(context, message, R.mipmap.ic_hud_error, listener);
    }

    public static void hudError(@NonNull Context context,
                                @NonNull String message) {
        hudError(context, message, null);
    }

    /**
     * 警告hud 弹窗，显示"❗️" icon 和自定义message
     *
     * @param context
     * @param message
     * @param listener
     */
    public static void hudWarning(@NonNull Context context,
                                  @NonNull String message,
                                  @Nullable OnHudDismissListener listener) {
        hudShow(context, message, R.mipmap.ic_hud_warning, listener);
    }

    public static void hudWarning(@NonNull Context context,
                                  @NonNull String message) {
        hudWarning(context, message, null);
    }

    private static void hudShow(@NonNull final Context context,
                                @NonNull final String message,
                                @NonNull int imageResource,
                                @Nullable final OnHudDismissListener listener) {

        if (context instanceof Activity
                && ((Activity) context).isFinishing()) {

            return;
        }
        final ImageView imageView = new ImageView(context);
        imageView.setImageResource(imageResource);
        final KProgressHUD hud = KProgressHUD.create(context)
                .setCustomView(imageView)
                .setLabel(message);
        MainLooper.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hud.show();
            }
        });

        Observable.timer(hudDuration, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        hud.dismiss();
                        if (null != listener) {
                            listener.onDismiss();
                        }
                    }
                });

    }

    /**
     * 显示无进度的progress hud 和自定义message
     *
     * @param context
     * @param message
     * @return
     */
    public static KProgressHUD hudProgress(@NonNull final Context context, @Nullable final String message) {
        final KProgressHUD hud = KProgressHUD.create(context)
                .setGraceTime(graceInterval)
                .setLabel(TextUtils.isEmpty(message) ? defaultProgressMessage : message);
        MainLooper.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hud.show();
            }
        });
        return hud;
    }

    /**
     * 安全dismiss 掉hud, 包括判空、是否在显示和在保证在主线程dismiss
     *
     * @param hud
     */
    public static void safeDismissHud(final KProgressHUD hud) {
        if (null != hud && hud.isShowing()) {
            MainLooper.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hud.dismiss();
                }
            });
        }
    }

    /**
     * 显示无进度的progress hud，不传message，使用默认message：${defaultProgressMessage}
     *
     * @param context
     * @return
     */
    public static KProgressHUD hudProgress(@NonNull Context context) {
        return hudProgress(context, null);
    }

    public interface OnHudDismissListener {
        void onDismiss();
    }
}
