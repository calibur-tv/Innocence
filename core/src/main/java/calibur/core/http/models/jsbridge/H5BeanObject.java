package calibur.core.http.models.jsbridge;

import calibur.core.http.models.base.IBaseResponse;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2019/01/01 2:42 PM
 * version: 1.0
 * description:
 */
public class H5BeanObject implements IBaseResponse {
  public String callbackId;
  public String func;
  public H5ParamsData params;
}
