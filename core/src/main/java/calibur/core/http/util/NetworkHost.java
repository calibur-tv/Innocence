package calibur.core.http.util;

import calibur.foundation.config.PackageTypeConfig;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/18 12:32 PM
 * version: 1.0
 * description:
 */
public interface NetworkHost {
  String HTTPS = "https://";
  String HTTP = "http://";
  String HOST = "api.calibur.tv/";
  String PRO_HOST = PackageTypeConfig.isProductEnv() ? HTTPS : HTTP + HOST;
}
