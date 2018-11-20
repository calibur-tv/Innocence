package calibur.core.http.interceptors;

import calibur.core.http.NetworkManager;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/20 11:06 AM
 * version: 1.0
 * description:
 */
public class CacheInterceptor implements Interceptor {
  private CacheControl mCacheControl;

  public CacheInterceptor() {
    CacheControl.Builder cacheBuilder = new CacheControl.Builder();
    cacheBuilder.maxAge(0, TimeUnit.SECONDS);
    cacheBuilder.maxStale(365, TimeUnit.DAYS);
    mCacheControl = cacheBuilder.build();
  }

  @Override
  public Response intercept(Chain chain) throws IOException {
    if (!NetworkManager.isConnected()) { //没有网络的时候强制取缓存
      Request.Builder builder = chain.request().newBuilder();
      builder.cacheControl(mCacheControl);
      return chain.proceed(builder.build());
    } else {
      return chain.proceed(chain.request());
    }
  }
}
