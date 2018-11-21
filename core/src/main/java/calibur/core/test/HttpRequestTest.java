package calibur.core.test;

import calibur.core.http.RetrofitManager;
import calibur.core.http.api.APIService;
import calibur.core.http.models.base.ResponseBean;
import calibur.core.http.observer.ObserverWrapper;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import retrofit2.Response;

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
        .compose(Rx2Schedulers.<Response<ResponseBean<TestModel>>>applyObservableAsync())
        .subscribe(new ObserverWrapper<TestModel>() {
          @Override public void onSuccess(TestModel o) {

          }
        });
  }
}
