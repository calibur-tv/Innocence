package com.riuir.calibur.net;

import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.SharedPreferencesUtils;
import com.riuir.calibur.assistUtils.VersionUtils;
import com.riuir.calibur.utils.Constants;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptorGetHasAuth implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        if (Constants.AUTH_TOKEN==null||Constants.AUTH_TOKEN.length()==0){
            Constants.AUTH_TOKEN = (String) SharedPreferencesUtils.get(App.instance(),"Authorization",new String());
        }
        Headers headers = request.headers().newBuilder()
                .add("Authorization","Bearer "+Constants.AUTH_TOKEN)
                .add("Accept", "application/x.api."+ Constants.API_VERSION+"+json")
                .add("X-APP-NAME","Sakura")
                .add("X-APP-VERSION", VersionUtils.getLocalVersionName())
                .build();

        request = request.newBuilder()
                .get()
                .headers(headers)
                .build();

        return chain.proceed(request);
    }
}
