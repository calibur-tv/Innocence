package calibur.core.http.models;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/21 2:34 PM
 * version: 1.0
 * description:
 */
public class AppVersionCheckData {
  private String latest_version;
  private boolean force_update;
  private String download_url;

  public String getLatest_version() {
    return latest_version;
  }

  public void setLatest_version(String latest_version) {
    this.latest_version = latest_version;
  }

  public boolean isForce_update() {
    return force_update;
  }

  public void setForce_update(boolean force_update) {
    this.force_update = force_update;
  }

  public String getDownload_url() {
    return download_url;
  }

  public void setDownload_url(String download_url) {
    this.download_url = download_url;
  }

  @Override
  public String toString() {
    return "AppVersionCheckData{" +
        "latest_version='" + latest_version + '\'' +
        ", force_update=" + force_update +
        ", download_url='" + download_url + '\'' +
        '}';
  }
}
