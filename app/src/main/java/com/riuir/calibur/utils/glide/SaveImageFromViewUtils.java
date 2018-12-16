package com.riuir.calibur.utils.glide;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import calibur.foundation.FoundationContextHolder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.TimeUtils;
import com.riuir.calibur.utils.GlideApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import static butterknife.internal.Utils.arrayOf;

public class SaveImageFromViewUtils {

    private OnStartSaveListener onStartSaveListener;
    private OnFinishSaveListener onFinishSaveListener;

    String[] PERMISSIONS = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };
    Context context;

    public  void checkPermissionAndSaveImage(Context context,String url){
        this.context = context;
        //检测是否有写的权限
        int permission = ContextCompat.checkSelfPermission(context,
                "android.permission.WRITE_EXTERNAL_STORAGE");
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 没有写的权限，去申请写的权限，会弹出对话框
            ActivityCompat.requestPermissions((Activity) context, PERMISSIONS,1);
        }else {
            if (onStartSaveListener!=null){
                onStartSaveListener.onStartSave();
            }
            new getImageCacheAsyncTask(context).execute(url);
        }
    }

    private class getImageCacheAsyncTask extends AsyncTask<String, Void, Bitmap> {

        private final Context context;

        public getImageCacheAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                String imgUrl =  params[0];
                File imgPath = Glide.with(context)
                        .load(imgUrl)
                        .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get();
                return BitmapFactory.decodeFile(imgPath.getAbsolutePath());
            } catch (OutOfMemoryError ex) {
                Glide.get(FoundationContextHolder.getContext()).clearMemory();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result == null) {
                return;
            }
            try {
                // 插入图库
                Random random = new Random();
                int ran = random.nextInt(9999 - 1000) + 1000;
                String reFileUrl =MediaStore.Images.Media.insertImage(App.instance().getContentResolver(), result, "caliburImg"+ran, null);
                Uri uri = Uri.parse(reFileUrl);
                String absolutePath = getPath(uri);
//                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, reFileUri));
                String[] paths = arrayOf(absolutePath);
                String[] types = arrayOf("image/jpeg","image/jpg","image/gif");
                MediaScannerConnection.scanFile(App.instance(),paths , types, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String s, Uri uri) {
                        LogUtils.d("saveImageFinished","s = "+s+", uri = "+uri.getPath());
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                onFinishSaveListener.onFinishSave();
            }
        }
    }

    public void setOnStartSaveListener(OnStartSaveListener onStartSaveListener) {
        this.onStartSaveListener = onStartSaveListener;
    }

    public void setOnFinishSaveListener(OnFinishSaveListener onFinishSaveListener) {
        this.onFinishSaveListener = onFinishSaveListener;
    }

    public interface OnStartSaveListener{
        void onStartSave();
    }
    public interface OnFinishSaveListener{
        void onFinishSave();
    }

    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
