package com.riuir.calibur.net.ssl;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/15 4:28 PM
 * version: 1.0
 * description:
 */
public class HttpSSL {
  public static SSLSocketFactory getSSLSocketFactory() {
    try {
      SSLContext sslContext = SSLContext.getInstance("SSL");
      sslContext.init(null, getTrustManager(), new SecureRandom());
      return sslContext.getSocketFactory();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static TrustManager[] getTrustManager() {
    return new TrustManager[] {
        new X509TrustManager() {
          @Override public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
          }

          @Override public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
          }

          @Override public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
          }
        }
    };
  }

  public static HostnameVerifier getHostnameVerifier() {
    return new HostnameVerifier() {
      @Override public boolean verify(String hostname, SSLSession session) {
        return true;
      }
    };
  }
}