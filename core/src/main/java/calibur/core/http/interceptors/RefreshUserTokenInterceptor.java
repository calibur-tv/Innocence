package calibur.core.http.interceptors;

import calibur.core.manager.UserSystem;
import calibur.core.utils.ISharedPreferencesKeys;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/12/15 12:12 AM
 * version: 1.0
 * description:
 */
public class RefreshUserTokenInterceptor implements Interceptor {

  @Override public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    String requestUrl = request.url().encodedPath();
    Response response = chain.proceed(request);
    if ("/door/current_user".equalsIgnoreCase(requestUrl)) {
      String token = response.header(ISharedPreferencesKeys.MOBILE_TOKEN);
      UserSystem.getInstance().updateUserToken(token);
    }
    return response;
  }
}
