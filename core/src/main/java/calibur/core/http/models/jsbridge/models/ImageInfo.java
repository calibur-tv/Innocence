package calibur.core.http.models.jsbridge.models;

import calibur.core.http.models.base.IBaseResponse;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2019/01/01 2:17 PM
 * version: 1.0
 * description:
 */
public class ImageInfo implements IBaseResponse {
  private String url;
  private int width;
  private int height;
  private long size;
  private String type;

  public String getUrl() {
    return url;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public long getSize() {
    return size;
  }

  public String getType() {
    return type;
  }
}
