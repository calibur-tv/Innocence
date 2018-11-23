package calibur.core.http.api;

import calibur.core.http.models.AppVersionCheckData;
import calibur.core.http.models.TemplateModel;
import calibur.core.http.models.base.ResponseBean;
import calibur.core.test.TestModel;
import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/18 12:22 PM
 * version: 1.0
 * description:
 */
public interface APIService {

  //for test
  @POST("flow/list")
  Observable<Response<ResponseBean<TestModel>>> getFollowList();

  @GET("app/version/check")
  Observable<Response<ResponseBean<AppVersionCheckData>>> getCallAppVersionCheck(@Query("type") int type, @Query("version") String version);

  @GET("app/template")
  Observable<Response<ResponseBean<TemplateModel>>> checkTemplateUpdate(@Query("page") String page, @Query("version") int version);
}
