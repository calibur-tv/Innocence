package calibur.core.manager.templaterender;

import android.annotation.SuppressLint;
import calibur.core.http.OkHttpClientManager;
import calibur.core.http.RetrofitManager;
import calibur.core.http.api.APIService;
import calibur.core.http.models.TemplateModel;
import calibur.core.http.models.base.ResponseBean;
import calibur.core.http.observer.ObserverWrapper;
import calibur.core.manager.TemplateDownloadManager;
import calibur.core.manager.TemplateRenderManager;
import calibur.core.utils.ISharedPreferencesKeys;
import calibur.core.utils.SharedPreferencesUtil;
import calibur.foundation.FoundationContextHolder;
import calibur.foundation.bus.BusinessBus;
import calibur.foundation.callback.CallBack1;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import calibur.foundation.utils.JSONUtil;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/22 11:48 PM
 * version: 1.0
 * description:
 */
public class EditorTemplateRender implements ITemplateRender {

  private Template mEditorTemplate;

  @Override public String getTemplateRenderData(String renderStr) {
    Map<String, String> data = new HashMap<>();
    data.put("data", renderStr);
    data.put("token", renderStr);
    Template template = getRenderTemplate();
    return template != null ? template.execute(data) : "";
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  @Override public Template getRenderTemplate() {
    if (mEditorTemplate != null) return mEditorTemplate;
    try {
      InputStream inputStream = FoundationContextHolder.getContext().getAssets().open("temple_editor.mustache");
      int size = inputStream.available();
      byte[] buffer = new byte[size];
      inputStream.read(buffer);
      inputStream.close();
      String temp = new String(buffer, "UTF-8");
      return mEditorTemplate = Mustache.compiler().compile(temp);
    } catch (IOException e) {
      e.printStackTrace();
      BusinessBus.post(null, "mainApps/postException2Bugly", e);
    }
    return null;
  }

  @Override public void checkForUpdate() {
    RetrofitManager.getInstance().getService(APIService.class).checkTemplateUpdate("editor", 1)
        .compose(Rx2Schedulers.<Response<ResponseBean<TemplateModel>>>applyObservableAsync())
        .subscribe(new ObserverWrapper<TemplateModel>() {
          @Override public void onSuccess(TemplateModel checkTemplateUpdateModel) {
            TemplateModel model = TemplateDownloadManager.getInstance().getTemplate(ISharedPreferencesKeys.EDITOR_PAGE_TEMPLATE);
            if(model == null || !model.getUrl().equals(checkTemplateUpdateModel.getUrl()))
              downloadUpdateFile(checkTemplateUpdateModel);
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
              return TemplateDownloadManager.getInstance().serializeTemplateFileToDisk(responseBody, "editorPageTemplate");
            }
          }).compose(Rx2Schedulers.<Boolean>applyObservableAsync()).subscribe(new Consumer<Boolean>() {
            @Override public void accept(Boolean isSuccess){
              if (isSuccess) {
                String json = JSONUtil.toJson(model);
                TemplateDownloadManager.getInstance().saveTemplate(ISharedPreferencesKeys.EDITOR_PAGE_TEMPLATE, json);
                TemplateRenderManager.getInstance().initTemplateRender("editorPageTemplate.htm", TemplateRenderManager.EDITOR,
                    new CallBack1<Template>() {
                      @Override public void success(Template template) {
                        mEditorTemplate = template;
                      }

                      @Override public void fail(Template template) {
                        mEditorTemplate = null;
                      }
                    });
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
