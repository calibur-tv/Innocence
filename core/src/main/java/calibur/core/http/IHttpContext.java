package calibur.core.http;

import io.reactivex.annotations.NonNull;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/20 2:20 PM
 * version: 1.0
 * description:
 */
public interface IHttpContext {
  int CLIENT_DEFAULT = 0;

  @NonNull
  HttpConfig provideHttpConfig(int key);

  @NonNull
  String provideUserAgent();

  boolean isPrivateHost(String host);
}
