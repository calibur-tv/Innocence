package calibur.core.manager.templaterender;

import android.annotation.SuppressLint;
import calibur.core.http.OkHttpClientManager;
import calibur.core.http.RetrofitManager;
import calibur.core.http.api.APIService;
import calibur.core.http.models.TemplateModel;
import calibur.core.http.models.base.ResponseBean;
import calibur.core.http.observer.ObserverWrapper;
import calibur.core.manager.TemplateDownloadManager;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import calibur.foundation.utils.JSONUtil;
import com.samskivert.mustache.Template;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/22 11:59 PM
 * version: 1.0
 * description:
 */
public class ImageDetailPageTemplateRender implements ITemplateRender {
  @Override public String getTemplateRenderData(String renderStr) {

    return null;
  }

  @Override public Template getRenderTemplate() {

    return null;
  }

  @Override public void checkForUpdate() {
    RetrofitManager.getInstance().getService(APIService.class).checkTemplateUpdate("image", 1)
        .compose(Rx2Schedulers.<Response<ResponseBean<TemplateModel>>>applyObservableAsync())
        .subscribe(new ObserverWrapper<TemplateModel>() {
          @Override public void onSuccess(TemplateModel checkTemplateUpdateModel) {

          }

          @Override public void onFailure(int code, String errorMsg) {
            super.onFailure(code, errorMsg);
          }
        });
  }


  @Override public void downloadUpdateFile(final TemplateModel model) {
    Request request = new Request.Builder().url(model.getUrl()).build();
    OkHttpClientManager.getDefaultClient().newCall(request).enqueue(new Callback() {

      @SuppressLint("CheckResult")
      @Override public void onResponse(Call call, okhttp3.Response response) throws IOException {
        ResponseBody body = response.body();
        if (body != null) {
          io.reactivex.Observable.just(body).map(new Function<ResponseBody, Boolean>() {
            @Override public Boolean apply(ResponseBody responseBody) {
              return TemplateDownloadManager.getInstance().serializeTemplateFileToDisk(responseBody, "imageDetailPageTemplate");
            }
          }).compose(Rx2Schedulers.<Boolean>applyObservableAsync()).subscribe(new Consumer<Boolean>() {
            @Override public void accept(Boolean isSuccess){
              if (isSuccess) {
                String json = JSONUtil.toJson(model);
                //TempleManagerModel.saveTemplateModel(json, templateType);
                //if (oldTemplate != null) {
                //  if (!TextUtils.isEmpty(oldTemplate.getBuild())) {
                //    if (!oldTemplate.getBuild().equals(newestTemplate.getBuild())) {
                //      deleteTemplateFile(getTemplateFileName(oldTemplate.getBuild()));
                //    }
                //  }
                //}
              }
            }
          });
        }
      }

      @Override public void onFailure(Call call, IOException e) {
      }
    });
  }
}
