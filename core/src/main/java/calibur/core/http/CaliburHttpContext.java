package calibur.core.http;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/20 2:21 PM
 * version: 1.0
 * description:
 */
public class CaliburHttpContext implements IHttpContext {

  @Override public HttpConfig provideHttpConfig(int key) {
    return HttpConfig.newBuilder().build();
  }

  @Override public String provideUserAgent() {
    return null;
  }

  @Override public boolean isPrivateHost(String host) {
    return false;
  }
}
