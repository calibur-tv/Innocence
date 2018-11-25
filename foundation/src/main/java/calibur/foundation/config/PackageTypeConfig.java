package calibur.foundation.config;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/25 10:47 PM
 * version: 1.0
 * description:
 */
public class PackageTypeConfig {
  private static String sPackageType = AppPackageTypeManager.getInstance().getPackageType();

  public static boolean isProductEnv() {
    return "P".equals(sPackageType);
  }

  public static boolean isDebugEnv() {
    return "D".equals(sPackageType);
  }

  public static boolean isBetaEnv() {
    return "B".equals(sPackageType);
  }
}
