package calibur.core.http.interceptors;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/15 5:53 PM
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
        String data = responseBody.string();
      }
    }
    return response;
  }
}
