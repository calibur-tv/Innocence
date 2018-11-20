package calibur.core.http.interceptors;

import calibur.core.http.error.HttpRespException;
import calibur.core.http.error.RespErrorModel;
import calibur.foundation.utils.JSONUtil;
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
public class GlobalErrorInterceptor implements Interceptor {

  @Override public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    Response response = chain.proceed(request);
    if (!response.isSuccessful()) {
      ResponseBody responseBody = response.body();
      if (responseBody != null) {
        int code = -1;
        String msg = "";
        RespErrorModel respErrorModel = null;
        String data = responseBody.string();
        try {
          respErrorModel = JSONUtil.fromJson(data, RespErrorModel.class);
        } catch (Throwable r) {
          r.printStackTrace();
        }

        if (respErrorModel != null) {
          code = respErrorModel.getCode();
          msg = respErrorModel.getMessage();
        }

        throw new HttpRespException(code, msg);
      }
    }
    return response;
  }
}
