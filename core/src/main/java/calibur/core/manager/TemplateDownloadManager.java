package calibur.core.manager;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/22 11:06 PM
 * version: 1.0
 * description:模板下载管理类
 */
public class TemplateDownloadManager {
  private static TemplateDownloadManager sInstance;

  public static TemplateDownloadManager getInstance() {
    if (sInstance == null) {
      sInstance = new TemplateDownloadManager();
    }
    return sInstance;
  }

  private TemplateDownloadManager() {
  }
}
