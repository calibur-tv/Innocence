package calibur.core.http.error;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/19 6:30 PM
 * version: 1.0
 * description:
 */
public interface HttpErrorCode {

  int BAD_NET_WORK = -101;
  int SSL_PEER_UNVERIFIED_EXCEPTION = -102;
  int CONNECT_TIMEOUT = -103;

  //statusCode 400
  int code1 = 40001;

  //statusCode 401
  int code2 = 40100;

  //statusCode 403
  int code3 = 40301;

  //statusCode 404
  int code4 = 40401;

  //statusCode 423
  int code5 = 42301;

  //statusCode 429
  int code6 = 42901;

  //statusCode 503
  int code7 = 50301;

  //刷新mobile token 失败
  int code8 = 40107;

  //token过期
  int TOKEN_EXPIRED_CODE = 40104;
}
