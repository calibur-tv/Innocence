package calibur.foundation.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2017/08/22/10:13 AM
 * version: 1.0
 * description:
 */
public final class Md5Util {

  public static String get32MD5(String s) {
    char hexDigits[] = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };
    try {
      byte[] strTemp = s.getBytes("UTF-8");
      // 使用MD5创建MessageDigest对象
      MessageDigest mdTemp = MessageDigest.getInstance("MD5");
      mdTemp.update(strTemp);
      byte[] md = mdTemp.digest();
      int j = md.length;
      char str[] = new char[j * 2];
      int k = 0;
      for (int i = 0; i < j; i++) {
        byte b = md[i];
        // System.out.println((int)b);
        // 将没个数(int)b进行双字节加密
        str[k++] = hexDigits[b >> 4 & 0xf];
        str[k++] = hexDigits[b & 0xf];
      }
      return new String(str);
    } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return "";
  }

  public static String getMD5(String md5) {
    try {
      java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
      byte[] array = md.digest(md5.getBytes());
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < array.length; ++i) {
        sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
      }
      return sb.toString();
    } catch (java.security.NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return null;
  }
}
