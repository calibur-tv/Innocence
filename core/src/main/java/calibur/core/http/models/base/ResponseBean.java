package calibur.core.http.models.base;

import calibur.core.http.error.HttpStatusCode;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/20 11:21 PM
 * version: 1.0
 * description:
 */
public class ResponseBean<T> implements IBaseResponse {
  /** HTTP status code. */
  protected int code;
  /** HTTP status message or null if unknown. */
  protected String message;
  /** The response from the HTTP client. */
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
    return code >= HttpStatusCode.SC_OK && code < HttpStatusCode.SC_MULTIPLE_CHOICES;
  }

  @Override public String toString() {
    return "ResponseBean{code="
        + code
        + ", message="
        + message
        + ", data=???"
        +'}';
  }
}
