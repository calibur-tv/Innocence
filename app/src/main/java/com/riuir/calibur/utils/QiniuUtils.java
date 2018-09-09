package com.riuir.calibur.utils;

import android.content.Context;

import com.qiniu.android.common.AutoZone;
import com.qiniu.android.storage.Configuration;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.TimeUtils;
import com.riuir.calibur.data.qiniu.QiniuUpToken;
import com.riuir.calibur.net.ApiGet;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QiniuUtils {
    public static String getQiniuUpToken(ApiGet apiGetHasAuth, Context context){
        String token = "";
        apiGetHasAuth.getCallQiniuUpToken().enqueue(new Callback<QiniuUpToken>() {
            @Override
            public void onResponse(Call<QiniuUpToken> call, Response<QiniuUpToken> response) {
                if (response!=null&&response.isSuccessful()){
                }else if (response!=null&&!response.isSuccessful()){
                }else {
                }
            }

            @Override
            public void onFailure(Call<QiniuUpToken> call, Throwable t) {
            }
        });
        return token;
    }

    public static Configuration getQiniuConfig(){
        Configuration config = new Configuration.Builder()
                .chunkSize(512 * 1024)        // 分片上传时，每片的大小。 默认256K
                .putThreshhold(1024 * 1024)   // 启用分片上传阀值。默认512K
                .connectTimeout(10)           // 链接超时。默认10秒
                .useHttps(true)               // 是否使用https上传域名
                .responseTimeout(60)          // 服务器响应超时。默认60秒
                .recorder(null)           // recorder分片上传时，已上传片记录器。默认null
//                .recorder(recorder, keyGen)   // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
                .zone(AutoZone.autoZone)        // 设置区域，指定不同区域的上传域名、备用域名、备用IP。
                .build();

        return config;
    }
    //指定七牛存储文件名
    public static String getQiniuUpKey(int id,String type,String filePath){
        //user/{userId}/{prefix}/{nowTime}-{random}.{file.ext}
        Random random = new Random();
        int ran = random.nextInt(9999 - 1000) + 1000;
        if (filePath.contains(".")){
            String [] s = filePath.split("\\.");
            String ext = s[s.length-1];
            return "user/"+id+"/"+type+"/"+ TimeUtils.getCurTimeLong()+"/"+ran+"/."+ext;
        }else {
            return null;
        }

    }
}
