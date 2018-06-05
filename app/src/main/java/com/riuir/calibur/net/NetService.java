package com.riuir.calibur.net;


import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.utils.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.fastjson.FastJsonConverterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetService {

    private static String baseUrl = Constants.API_BASE_URL;

    /**
     * singleton start
     **/
    private NetService() {
    }

    private static class InstanceHolder {
        static final NetService instance = new NetService();
    }

    public static NetService getInstance() {
        return InstanceHolder.instance;
    }
    /** singleton end **/

    /**
     * 创建 OkHttp
     * 带有阻断的
     **/
    private OkHttpClient client = new OkHttpClient.Builder()
            /** OkHttp 日志
             *
             * 阻截interceptor 添加Header参数**/
            .addInterceptor(new AuthInterceptor())
            .addInterceptor(new HttpLoggingInterceptor(new NetLogger()).setLevel(HttpLoggingInterceptor.Level.BODY))
            //请求超时时间
            .readTimeout(30, TimeUnit.SECONDS)
            //请求失败是否重新请求
            .retryOnConnectionFailure(false)
            .build();

    /**
     * 创建 OkHttp
     * get
     * 阻断
     **/
    private OkHttpClient clientGet = new OkHttpClient.Builder()
            .addInterceptor(new AuthInterceptorGet())
            .addInterceptor(new HttpLoggingInterceptor(new NetLogger()).setLevel(HttpLoggingInterceptor.Level.BODY))
            .readTimeout(30,TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build();
    /**
     * 创建 Retrofit
     **/
    private Retrofit retrofit = new Retrofit.Builder()
            // 设置网络请求的api地址
            .baseUrl(baseUrl)
            // 设置数据解析器
            .addConverterFactory(GsonConverterFactory.create())
            // 支持RxJava平台
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            //传入OKHttpClient对象
            .client(client)
            .build();


    /**
     * 创建 Retrofit get
     **/
    private Retrofit retrofitGet = new Retrofit.Builder()
            // 设置网络请求的api地址
            .baseUrl(baseUrl)
            // 设置数据解析器
            .addConverterFactory(GsonConverterFactory.create())
            //传入OKHttpClient对象
            .client(clientGet)
            .build();


    /**
     * post API单例化
     */
    private ApiPost mApiPost = null;

    //Observable post
    public ApiPost createServicePost() {
        if (null == mApiPost) {
            // 创建 网络请求接口 的实例
            ApiPost apiPost = retrofit.create(ApiPost.class);
            LogUtils.d("drama","post");
            mApiPost = apiPost;
        }

        return mApiPost;
    }


    /**
     * get API单例化
     */
    private ApiGet mApiGet = null;
    //get
    public ApiGet createServiceGet() {
        if (null == mApiGet) {
            // 创建 网络请求接口 的实例
            ApiGet apiGet = retrofitGet.create(ApiGet.class);
            LogUtils.d("drama","get");
            mApiGet = apiGet;
        }
        return mApiGet;
    }
}
