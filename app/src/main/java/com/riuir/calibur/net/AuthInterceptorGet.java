package com.riuir.calibur.net;




import android.provider.Settings;

import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.Installation;
import com.riuir.calibur.utils.Constants;

import java.io.IOException;
import java.util.Date;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;



//阻断，不需要usertoken  get请求方式
public class AuthInterceptorGet implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        Headers headers = request.headers().newBuilder()
                .add("Accept", "application/x.api."+ Constants.API_VERSION+"+json")
                .build();

        request = request.newBuilder()
                .get()
                .headers(headers)
                .build();

        return chain.proceed(request);
    }

}

