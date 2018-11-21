package calibur.core.test;

import calibur.core.http.RetrofitManager;
import calibur.core.http.api.APIService;
import calibur.core.http.models.base.ResponseBean;
import calibur.core.http.observer.ObserverWrapper;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/20 6:17 PM
 * version: 1.0
 * description:
 */
public class HttpRequestTest {

  public static void test() {
    RetrofitManager.getInstance().getService(APIService.class).getFollowList()
        .subscribe(new ObserverWrapper<ResponseBean<TestModel>>() {
          @Override public void onSuccess(ResponseBean<TestModel> testModel) {
          }

          @Override public void onFailure(int code, String errorMsg) {
            super.onFailure(code, errorMsg);
          }
        });
  }
}
