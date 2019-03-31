package calibur.foundation.utils;

import java.net.URLDecoder;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/07/17 3:59 PM
 * version: 1.0
 * description:
 */
public class URLDecoderUtil {
  /**
   * 防止在decode包含%字符的数据时出现crash
   */
  public static String decodeWithUTF8(String s) {
    try {
      s = s.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
      s = s.replaceAll("\\+", "%2B");
      s = URLDecoder.decode(s, "utf-8");
    } catch (Throwable t) {
      t.printStackTrace();
    }
    return s;
  }
}
