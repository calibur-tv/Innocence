package com.riuir.calibur.net;

import android.text.TextUtils;

import com.riuir.calibur.utils.Constants;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class AuthWithOutGeetest implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        String body = bodyToString(request.body());
        long time = System.currentTimeMillis()/1000;


        Headers headers = request.headers().newBuilder()
                .add("Accept", "application/x.api."+ Constants.API_VERSION+"+json")
                .add("X-Auth-Time",time+"")
                .add("X-Auth-Value",md5(time+"Dark-Flame-Master"))
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
    public static String md5(String string) {

        if (TextUtils.isEmpty(string)) {
            return "";
        }

        MessageDigest md5 = null;

        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result.append(temp);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
