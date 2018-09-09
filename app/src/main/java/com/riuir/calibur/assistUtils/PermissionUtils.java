package com.riuir.calibur.assistUtils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

public class PermissionUtils {
    // Storage Permissions

    public static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {

            Manifest.permission.READ_EXTERNAL_STORAGE,

            Manifest.permission.WRITE_EXTERNAL_STORAGE

    };

    public static void chekReadAndWritePermission(Context context) {
        int permissionWrite = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionRead = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionWrite != PackageManager.PERMISSION_GRANTED ||
                permissionRead!= PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    (Activity) context,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
