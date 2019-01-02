package com.riuir.calibur.utils.glide;

import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.LogUtils;

import java.io.File;
import java.math.BigDecimal;

public class GlideClearCatchUtils {

    public static String getCacheSize(){
        try {
            //注释部分是获取Glide缓存路径
//            LogUtils.d("clearCache","file path = "+GlideApp.getPhotoCacheDir(App.instance()).getPath());
//            return getFormatSize(getFolderSize(GlideApp.getPhotoCacheDir(App.instance())));
            //这里是calibur总文件路径
            return getFormatSize(getFolderSize(App.instance().getCacheDir()));
        } catch (Exception e) {
            e.printStackTrace();
            return "获取失败";
        }

    }

    private static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte";
        }
        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }
        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1){
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

    // 获取指定文件夹内所有文件大小的和
    private static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File aFileList : fileList) {
                if (aFileList.isDirectory()) {
                    size = size + getFolderSize(aFileList);
                } else {
                    size = size + aFileList.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.d("clearCache","size = "+size);
//        return size;
        return size/2;
    }
}
