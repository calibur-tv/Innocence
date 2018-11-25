package calibur.core.manager.templaterender;

import calibur.core.http.RetrofitManager;
import calibur.core.http.api.APIService;
import calibur.core.http.models.TemplateModel;
import calibur.core.http.models.base.ResponseBean;
import calibur.core.http.observer.ObserverWrapper;
import calibur.foundation.FoundationContextHolder;
import calibur.foundation.bus.BusinessBus;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import java.io.IOException;
import java.io.InputStream;
import retrofit2.Response;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/24 12:27 AM
 * version: 1.0
 * description:
 */
public abstract class BaseTemplateRender implements ITemplateRender{

  String TEMPLATE_NAME;
  public abstract void checkTemplateForUpdateSuccess(TemplateModel templateModel);

  @Override public String getTemplateRenderData(String renderStr) {
    return null;
  }

  @Override public Template getRenderTemplate() {
    return null;
  }

  @Override public void checkForUpdate(String businessName) {
    RetrofitManager.getInstance().getService(APIService.class).checkTemplateUpdate(businessName, 1)
        .compose(Rx2Schedulers.<Response<ResponseBean<TemplateModel>>>applyObservableAsync())
        .subscribe(new ObserverWrapper<TemplateModel>() {
          @Override public void onSuccess(TemplateModel checkTemplateUpdateModel) {
            checkTemplateForUpdateSuccess(checkTemplateUpdateModel);
          }

          @Override public void onFailure(int code, String errorMsg) {
            super.onFailure(code, errorMsg);
          }
        });
  }

  @Override public void downloadUpdateFile(TemplateModel model) {
  }

  Template getTemplateFromLocal(String name) {
    try {
      InputStream inputStream = FoundationContextHolder.getContext().getAssets().open(name);
      int size = inputStream.available();
      byte[] buffer = new byte[size];
      inputStream.read(buffer);
      inputStream.close();
      String temp = new String(buffer, "UTF-8");
      return Mustache.compiler().compile(temp);
    } catch (IOException e) {
      e.printStackTrace();
      BusinessBus.post(null, "mainApps/postException2Bugly", e);
    }
    return null;
  }

  @Override public void setTemplateName(String name) {
    TEMPLATE_NAME = name;
  }
}
