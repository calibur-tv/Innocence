package calibur.foundation.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2017/08/22/11:25 AM
 * version: 1.0
 * description:
 */
public final class URLEncoderUtil {
  public static String utf8Encode(String str) {
    try {
      str = URLEncoder.encode(str, "utf-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return str;
  }
}
