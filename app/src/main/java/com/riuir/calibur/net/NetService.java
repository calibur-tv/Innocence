package com.riuir.calibur.net;


import com.riuir.calibur.utils.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.fastjson.FastJsonConverterFactory;

public class NetService {

    private static String baseUrl = Constants.API_BASE_URL + "/";

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
     **/
    private OkHttpClient client = new OkHttpClient.Builder()
            /** OkHttp 日志 interceptor **/
            .addInterceptor(new AuthInterceptor())
            .addInterceptor(new HttpLoggingInterceptor(new NetLogger()).setLevel(HttpLoggingInterceptor.Level.BODY))
            .readTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build();

    /**
     * 创建 Retrofit
     **/
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(FastJsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build();

    private Api mApi = null;

    public Api createService() {
        if (null == mApi) {
            Api api = retrofit.create(Api.class);

            mApi = api;
        }
        return mApi;
    }
}
