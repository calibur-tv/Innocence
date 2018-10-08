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



//阻断，需要userToken Post请求方式
public class AuthInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        String body = bodyToString(request.body());

//        long time = System.currentTimeMillis();
//        final Date now = new Date(time * 1000);

//        String constAuthToken = md5("md5_salt" + time);
//        String ANDROID_ID = Installation.id(App.instance());

        Headers headers = request.headers().newBuilder()
                .add("Authorization","Bearer "+Constants.AUTH_TOKEN)
                .add("Accept", "application/x.api."+ Constants.API_VERSION+"+json")
                .build();

        request = request.newBuilder()
                .post(RequestBody.create(MediaType.parse("application/json;charset=utf-8"), body))
//                .get()
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
