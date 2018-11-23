package calibur.core.http.models;

import calibur.core.http.models.base.IBaseResponse;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/23 11:35 PM
 * version: 1.0
 * description:
 */
public class TemplateModel implements IBaseResponse{
  private String url;

  private String file;
  private String build;

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getFile() {
    return file;
  }

  public void setFile(String file) {
    this.file = file;
  }

  public String getBuild() {
    return build;
  }

  public void setBuild(String build) {
    this.build = build;
  }
}
