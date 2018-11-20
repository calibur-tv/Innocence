package calibur.core.http.error;

import java.io.Serializable;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/19 6:42 PM
 * version: 1.0
 * description:
 */
public class RespErrorModel implements Serializable{
  private int code;
  private String message;

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
