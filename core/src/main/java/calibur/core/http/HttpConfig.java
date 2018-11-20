package calibur.core.http;

import java.util.List;
import okhttp3.Interceptor;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/20 11:12 AM
 * version: 1.0
 * description:
 */
public class HttpConfig {
  public static final int DEFAULT_TIME_OUT = 60;

  private static final long DEFAULT_KEEP_ALIVE_DURATION_MS = 5 * 60 * 1000;

  public final int readTimeout;

  public final int connectTimeout;

  public final int writeTimeout;

  public final List<Interceptor> interceptors;

  public final List<Interceptor> networkInterceptors;

  private HttpConfig(Builder builder) {
    readTimeout = builder.readTimeout;
    connectTimeout = builder.connectTimeout;
    writeTimeout = builder.writeTimeout;
    interceptors = builder.interceptors;
    networkInterceptors = builder.networkInterceptors;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static Builder newBuilder(HttpConfig copy) {
    Builder builder = new Builder();
    builder.readTimeout = copy.readTimeout;
    builder.connectTimeout = copy.connectTimeout;
    builder.writeTimeout = copy.writeTimeout;
    builder.interceptors = copy.interceptors;
    builder.networkInterceptors = copy.networkInterceptors;
    return builder;
  }

  public static final class Builder {
    private int readTimeout = DEFAULT_TIME_OUT;
    private int connectTimeout = DEFAULT_TIME_OUT;
    private int writeTimeout = DEFAULT_TIME_OUT;
    private List<Interceptor> interceptors;
    private List<Interceptor> networkInterceptors;

    private Builder() {
    }

    public Builder readTimeout(int val) {
      readTimeout = val;
      return this;
    }

    public Builder connectTimeout(int val) {
      connectTimeout = val;
      return this;
    }

    public Builder writeTimeout(int val) {
      writeTimeout = val;
      return this;
    }

    public Builder interceptors(List<Interceptor> val) {
      interceptors = val;
      return this;
    }

    public Builder networkInterceptors(List<Interceptor> val) {
      networkInterceptors = val;
      return this;
    }

    public HttpConfig build() {
      return new HttpConfig(this);
    }
  }
}
