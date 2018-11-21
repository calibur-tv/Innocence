package calibur.core.http.models.base;

import java.io.Serializable;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/20 11:21 PM
 * version: 1.0
 * description:
 */
public class ResponseBean<T> implements Serializable, Cloneable{
  protected static final int CODE_SUCCESS = 200;
  protected int code;
  protected String message;
  private T data;

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

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public boolean isSuccessful() {
    return code == CODE_SUCCESS;
  }

  @Override public String toString() {
    return "ResponseBean{code="
        + code
        + ", message="
        + message
        + ", data="
        +
        + '}';
  }
}
