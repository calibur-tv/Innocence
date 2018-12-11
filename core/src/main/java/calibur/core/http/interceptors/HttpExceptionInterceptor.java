package calibur.core.http.interceptors;

import android.text.TextUtils;
import calibur.core.http.error.HttpErrorCode;
import calibur.core.http.models.base.ResponseBean;
import calibur.core.http.util.NetworkHost;
import calibur.core.manager.UserSystem;
import calibur.core.utils.ISharedPreferencesKeys;
import calibur.core.utils.SharedPreferencesUtil;
import calibur.foundation.bus.BusinessBus;
import calibur.foundation.utils.JSONUtil;
import java.io.IOException;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/12/07 3:59 PM
 * version: 1.0
 * description:
 */
public class HttpExceptionInterceptor implements Interceptor {
  @Override public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    Response response = chain.proceed(request);
    if (!response.isSuccessful()) {
      ResponseBody responseBody = response.body();
      if (responseBody != null) {
        byte[] respBytes = responseBody.bytes();
        String data = new String(respBytes);
        ResponseBean errorInfo = JSONUtil.fromJson(data, ResponseBean.class);
        //处理token失效后，同步刷新token并继续请求数据的逻辑
        if (errorInfo != null && errorInfo.getCode() == HttpErrorCode.TOKEN_EXPIRED_CODE) {
          Response modifiedResponse = tokenExpired(chain, request, response, errorInfo.getMessage());
          if (modifiedResponse != null) return modifiedResponse;
        } else {// 还原 response
          response = response.newBuilder()
              .body(ResponseBody.create(null, respBytes))
              .build();
        }
      }
    }
    return response;
  }

  private okhttp3.Response tokenExpired(Interceptor.Chain chain, Request request, okhttp3.Response response, String errorMessage)
      throws IOException {
    Headers headers = request.headers();
    String newMobileToken = refreshToken(headers);
    if (TextUtils.isEmpty(newMobileToken)) {//token 刷新失败，也需要重新登录
      BusinessBus.post(null, "mainApps/mobileTokenRefreshFailed", errorMessage);
      return response;
    }
    // Add new header to rejected request and retry it
    HttpUrl url = request.url();
    Request modifiedRequest = request.newBuilder()
        .url(url)
        .headers(headers)
        .header("Authorization", newMobileToken)// Add new user token
        .build();

    okhttp3.Response modifiedResponse = chain.proceed(modifiedRequest);
    if (!modifiedResponse.isSuccessful()) {
      BusinessBus.post(null, "mainApps/userVerificationFailed", errorMessage);
      return response;
    }
    return modifiedResponse;
  }

  private synchronized String refreshToken(Headers headers) {
    try {
      OkHttpClient client = new OkHttpClient();
      String freshUrl = NetworkHost.PRO_HOST + "door/refresh_token";
      Request.Builder builder = new Request.Builder().url(freshUrl)
          .post(RequestBody.create(MediaType.parse(""), ""))
          .headers(headers)
          .header("Authorization","Bearer "+ UserSystem.getInstance().getUserToken());
      Request request = builder.build();
      okhttp3.Response response = client.newCall(request).execute();
      if (response.isSuccessful()) {
        String newToken = response.header(ISharedPreferencesKeys.MOBILE_TOKEN);
        if (!TextUtils.isEmpty(newToken)) {
          UserSystem.getInstance().updateUserToken(newToken);
          SharedPreferencesUtil.putString(ISharedPreferencesKeys.MOBILE_TOKEN, newToken);
          return newToken;
        }
      }else {
          if (response.message()!=null){
            BusinessBus.post(null, "mainApps/postException2Bugly", response.message());
          }
      }
    } catch (Throwable e) {
      e.printStackTrace();
      BusinessBus.post(null, "mainApps/postException2Bugly", e);
    }
    return "";
  }
}
