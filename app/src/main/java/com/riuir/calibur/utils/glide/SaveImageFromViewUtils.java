package com.riuir.calibur.utils.glide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.TimeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    private class getImageCacheAsyncTask extends AsyncTask<String, Void, File> {

        private final Context context;

        public getImageCacheAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected File doInBackground(String... params) {

            String imgUrl =  params[0];
            try {
                return Glide.with(context)
                        .load(imgUrl)
                        .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get();
            } catch (Exception ex) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(File result) {
            if (result == null) {
                return;
            }
            try {
                // 插入图库
                String reFileUrl =MediaStore.Images.Media.insertImage(App.instance().getContentResolver(), result.getAbsolutePath(), result.getName(), null);
                LogUtils.d("saveImageFinished","url = "+reFileUrl);
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(reFileUrl)));
            } catch (FileNotFoundException e) {
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

}
