package calibur.core.http.error;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/19 6:50 PM
 * version: 1.0
 * description:
 */
public class HttpRespException extends RuntimeException {
  public int code;
  public String message;

  public HttpRespException(int code, String msg) {
    this.code = code;
    this.message = msg;
  }
}
