package calibur.core.http.interceptors;

import android.util.Log;

import calibur.core.manager.UserSystem;
import calibur.foundation.utils.AppUtil;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/20 10:42 AM
 * version: 1.0
 * description:
 */
public class HeaderInterceptor implements Interceptor {

  @Override public Response intercept(Chain chain) throws IOException{
    Request.Builder requestBuilder = chain.request().newBuilder()
        .addHeader("X-APP-NAME", "Sakura")
        .addHeader("X-APP-VERSION", AppUtil.getAppVersionName())
        .addHeader("Accept", "application/x.api."+ "v1+json");
    if (UserSystem.getInstance().isLogin()) {
      requestBuilder.addHeader("Authorization", UserSystem.getInstance().getUserToken());
    }
    Request request = requestBuilder.build();
    return chain.proceed(request);
  }
}
