package com.riuir.calibur.net;

import com.riuir.calibur.utils.Constants;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class AuthInterceptorPostNoAuth implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        String body = bodyToString(request.body());

        Headers headers = request.headers().newBuilder()
                .add("Accept", "application/x.api."+ Constants.API_VERSION+"+json")
                .build();

        request = request.newBuilder()
                .post(RequestBody.create(MediaType.parse("application/json;charset=utf-8"), body))
                .headers(headers)
                .build();

        return chain.proceed(request);
    }

    private static String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null) {
                copy.writeTo(buffer);
            } else {
                return "";
            }
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}
