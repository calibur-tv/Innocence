package com.riuir.calibur.net;

import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.logging.HttpLoggingInterceptor;

/**
 * 判断接口类型
 */

public class NetLogger implements HttpLoggingInterceptor.Logger {

    @Override
    public void log(String message) {
        if (isJsonArray(message) || isJsonObject(message)) {
            Logger.json(message);
        } else if (isXml(message)) {
            Logger.xml(message);
        } else {
            HttpLoggingInterceptor.Logger.DEFAULT.log(message);
        }
    }

    private boolean isJsonArray(String input) {
        boolean isJsonArray = true;
        try {
            new JSONArray(input);
        } catch (JSONException e) {
            isJsonArray = false;
        }
        return isJsonArray;
    }

    private boolean isJsonObject(String input) {
        boolean isJsonObject = true;
        try {
            new JSONObject(input);
        } catch (JSONException e) {
            isJsonObject = false;
        }
        return isJsonObject;
    }

    private boolean isXml(String input) {
        return false;
    }

}
