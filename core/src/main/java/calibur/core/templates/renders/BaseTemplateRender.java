package calibur.core.templates.renders;

import android.annotation.SuppressLint;
import android.util.Log;
import calibur.core.http.OkHttpClientManager;
import calibur.core.http.RetrofitManager;
import calibur.core.http.api.APIService;
import calibur.core.http.models.TemplateModel;
import calibur.core.http.models.base.ResponseBean;
import calibur.core.http.observer.ObserverWrapper;
import calibur.core.templates.TemplateDownloadManager;
import calibur.core.templates.TemplateRenderEngine;
import calibur.foundation.FoundationContextHolder;
import calibur.foundation.callback.CallBack1;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import calibur.foundation.rxjava.rxbus.RxBus2Consumer;
import calibur.foundation.utils.JSONUtil;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import io.reactivex.Observable;
import io.reactivex.functions.Function;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/24 12:27 AM
 * version: 1.0
 * description:
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public abstract class BaseTemplateRender implements ITemplateRender{

  private String TEMPLATE_NAME;
  Template mTemplate;

  @Override public String getTemplateRenderData(String renderStr) {
    return null;
  }

  @Override public Template getRenderTemplate() {
    return null;
  }

  @Override public void updateTemplateIfNecessary(String businessName) {
    RetrofitManager.getInstance().getService(APIService.class).checkTemplateUpdate(businessName, 1)
        .compose(Rx2Schedulers.<Response<ResponseBean<TemplateModel>>>applyObservableAsync())
        .subscribe(new ObserverWrapper<TemplateModel>() {
          @Override public void onSuccess(TemplateModel newTemplateModel) {
            TemplateModel oldModel = getTemplateModelFromLocal();
            if (oldModel == null || (!oldModel.getUrl().equals(newTemplateModel.getUrl()))) {
              downloadUpdateFile(newTemplateModel);
            } else if (isTemplateFileNotExists()) {
              downloadUpdateFile(oldModel);
            } else {
              if(mTemplate == null)
                initTemplate();
            }
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
          Observable.just(body).map(new Function<ResponseBody, Boolean>() {
            @Override public Boolean apply(ResponseBody responseBody) {
              return TemplateDownloadManager.getInstance().serializeTemplateFileToDisk(responseBody, TEMPLATE_NAME);
            }
          }).compose(Rx2Schedulers.<Boolean>applyObservableAsync()).subscribe(new RxBus2Consumer<Boolean>() {
            @Override public void consume(Boolean isSuccess) {
              if (isSuccess) {
                String json = JSONUtil.toJson(model);
                saveTemplateModel2Local(json);
                initTemplate();
              }
            }
          });
        }
      }

      @Override public void onFailure(Call call, IOException e) {
      }
    });
  }

  Template getTemplateFromLocal() {
    try {
      InputStream inputStream = FoundationContextHolder.getContext().getAssets().open(TEMPLATE_NAME + ".mustache");
      int size = inputStream.available();
      byte[] buffer = new byte[size];
      inputStream.read(buffer);
      inputStream.close();
      String temp = new String(buffer, "UTF-8");
      return Mustache.compiler().defaultValue("").compile(temp);
    } catch (IOException e) {
      e.printStackTrace();
      Log.d("checkWebData","basedata e= "+e.toString());
    }
    return null;
  }

  @Override public void setTemplateName(String name) {
    TEMPLATE_NAME = name;
  }

  private void initTemplate() {
    TemplateRenderEngine.getInstance().initTemplateRender(TEMPLATE_NAME,
        new CallBack1<Template>() {
          @Override public void success(Template template) {
            mTemplate = template;
          }

          @Override public void fail(Template template) {
            mTemplate = null;
          }
        });
  }

  private boolean isTemplateFileNotExists() {
    final File templateFile = new File(TemplateDownloadManager.getInstance().getTemplatePath(), templateFileName(TEMPLATE_NAME));
    return (!templateFile.exists() || templateFile.length() <= 0);
  }

  private String templateFileName(String name) {
    return name + ".html";
  }

  public abstract TemplateModel getTemplateModelFromLocal();
  public abstract void saveTemplateModel2Local(String json);
}
