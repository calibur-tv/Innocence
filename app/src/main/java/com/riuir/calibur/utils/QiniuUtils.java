package com.riuir.calibur.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.qiniu.android.common.AutoZone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.TimeUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;


import com.riuir.calibur.net.ApiGet;
import com.riuir.calibur.ui.home.image.CreateImageAlbumActivity;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import calibur.core.http.RetrofitManager;
import calibur.core.http.api.APIService;
import calibur.core.http.models.base.ResponseBean;
import calibur.core.http.models.qiniu.QiniuUpToken;
import calibur.core.http.models.qiniu.params.QiniuImageParams;
import calibur.core.http.observer.ObserverWrapper;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QiniuUtils {

    private int urlTag;
    private ArrayList<QiniuImageParams.QiniuImageParamsData> qiniuImageParamsDataList = new ArrayList<>();
    private List<String> urlList;
    private Context context;
    private int userId;
    private UploadManager uploadManager;

    private OnQiniuUploadFailedListnener onQiniuUploadFailedListnener;
    private OnQiniuUploadSuccessedListnener onQiniuUploadSuccessedListnener;

    public void getQiniuUpToken(ApiGet apiGetHasAuth, final Context context, List<String> urlList, int userId){
        this.urlList = urlList;
        this.context = context;
        this.userId = userId;
        uploadManager = new UploadManager(getQiniuConfig());
        RetrofitManager.getInstance().getService(APIService.class)
                .getCallQiniuUpToken()
                .compose(Rx2Schedulers.<Response<ResponseBean<QiniuUpToken>>>applyObservableAsync())
                .subscribe(new ObserverWrapper<QiniuUpToken>() {
                    @Override
                    public void onSuccess(QiniuUpToken qiniuUpToken) {
                        Constants.QINIU_TOKEN = qiniuUpToken.getUpToken();
                        setQiniuUpLoadCheck();
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        super.onFailure(code, errorMsg);
                        setUpLoadDiaLogFail(errorMsg);

                    }
                });

    }

    private void setQiniuUpLoadCheck(){
        if (urlList!=null&&urlList.size()!=0){
            urlTag = 0;
            qiniuImageParamsDataList.clear();
            setQiniuUpLoad(urlTag);
        }
    }

    private void setQiniuUpLoad(int tag) {
        //上传icon
        String iconkey = QiniuUtils.getQiniuUpKey(userId,"avatar",urlList.get(tag));
        File imgFile = new File(urlList.get(tag));
        if (iconkey!=null){
            uploadManager.put(imgFile, iconkey, Constants.QINIU_TOKEN, new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject response) {
                    LogUtils.d("newCardCreate","icon isOk = "+info.isOK());
                    if (info.isOK()){
                        Gson gson = new Gson();
                        QiniuImageParams params = gson.fromJson(response.toString(),QiniuImageParams.class);

                        qiniuImageParamsDataList.add(params.getData());
                        urlTag++;
                        if (urlTag<urlList.size()){
                            setQiniuUpLoad(urlTag);
                        }else {
                            //结束函数回调 发送上传到服务器请求
                            setUpLoadAlbum();
                        }
                        LogUtils.d("newCardCreate","icon url = "+params.getData().getUrl());
                    }else if(info.isCancelled()){
                        ToastUtils.showShort(context,"取消上传");
                        setUpLoadDiaLogFail("");
                    }else if (info.isNetworkBroken()){
                        ToastUtils.showShort(context,"网络异常，请稍后再试");
                        setUpLoadDiaLogFail("");
                    }else {
                        ToastUtils.showShort(context,"其他原因导致取消上传 \n info = "+info.error);
                        setUpLoadDiaLogFail(info.error);
                    }
                }
            },null);
        }
    }

    private void setUpLoadAlbum() {
        if (onQiniuUploadSuccessedListnener!=null){
            onQiniuUploadSuccessedListnener.onUploadSuccess(qiniuImageParamsDataList);
        }
    }

    private void setUpLoadDiaLogFail(String failedMessage) {
        if (onQiniuUploadFailedListnener!=null){
            onQiniuUploadFailedListnener.onFailed(failedMessage);
        }
    }

    public void setOnQiniuUploadFailedListnener(OnQiniuUploadFailedListnener onQiniuUploadFailedListnener) {
        this.onQiniuUploadFailedListnener = onQiniuUploadFailedListnener;
    }

    public void setOnQiniuUploadSuccessedListnener(OnQiniuUploadSuccessedListnener onQiniuUploadSuccessedListnener) {
        this.onQiniuUploadSuccessedListnener = onQiniuUploadSuccessedListnener;
    }

    public interface OnQiniuUploadFailedListnener{
        void onFailed(String fialMessage);
    }
    public interface OnQiniuUploadSuccessedListnener{
        void onUploadSuccess(ArrayList<QiniuImageParams.QiniuImageParamsData> imageParamsDataList);
    }


    //可直接调用的静态函数
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
        int ran1 = random.nextInt(9999 - 1000) + 1000;
        int ran2 = random.nextInt(9999 - 1000) + 1000;
        if (filePath.contains(".")){
            String [] s = filePath.split("\\.");
            String oldName = "caliburImage"+ran2;
            try {
                oldName = URLEncoder.encode(oldName, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String ext = s[s.length-1];
            return "user/"+id+"/"+type+"/"+ (TimeUtils.getCurTimeLong()-+ran1)+"."+oldName+"."+ext;
        }else {
            return null;
        }

    }
}
