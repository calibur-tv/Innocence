package com.riuir.calibur.net;

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

        Headers headers = request.headers().newBuilder()
                .add("Authorization","Bearer "+Constants.AUTH_TOKEN)
                .add("Accept", "application/x.api."+ Constants.API_VERSION+"+json")
                .build();

        request = request.newBuilder()
                .get()
                .headers(headers)
                .build();

        return chain.proceed(request);
    }
}
