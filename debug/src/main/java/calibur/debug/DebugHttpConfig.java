package calibur.debug;

import calibur.core.http.HttpConfig;
import calibur.core.http.IHttpContext;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Interceptor;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/26 12:38 AM
 * version: 1.0
 * description:
 */
public class DebugHttpConfig implements IHttpContext {
  @Override public HttpConfig provideHttpConfig(int key) {
    List<Interceptor> networkInterceptors = new ArrayList<>();
    return HttpConfig.newBuilder()
        .networkInterceptors(networkInterceptors)
        .build();
  }

  @Override public String provideUserAgent() {
    return null;
  }

  @Override public boolean isPrivateHost(String host) {
    return false;
  }
}
