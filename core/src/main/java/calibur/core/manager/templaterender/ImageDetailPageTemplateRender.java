package calibur.core.manager.templaterender;

import android.annotation.SuppressLint;
import calibur.core.http.OkHttpClientManager;
import calibur.core.http.models.TemplateModel;
import calibur.core.manager.TemplateDownloadManager;
import calibur.core.manager.TemplateRenderManager;
import calibur.core.utils.ISharedPreferencesKeys;
import calibur.foundation.callback.CallBack1;
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

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/22 11:59 PM
 * version: 1.0
 * description:
 */
public class ImageDetailPageTemplateRender extends BaseTemplateRender {

  private Template mImageDetailPageTemplate;

  @Override public String getTemplateRenderData(String renderStr) {
    return null;
  }

  @Override public Template getRenderTemplate() {
    if (mImageDetailPageTemplate != null) return mImageDetailPageTemplate;
    return mImageDetailPageTemplate = getTemplateFromLocal(TEMPLATE_NAME + ".mustache");
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
              return TemplateDownloadManager.getInstance().serializeTemplateFileToDisk(responseBody, TEMPLATE_NAME);
            }
          }).compose(Rx2Schedulers.<Boolean>applyObservableAsync()).subscribe(new Consumer<Boolean>() {
            @Override public void accept(Boolean isSuccess) {
              if (isSuccess) {
                String json = JSONUtil.toJson(model);
                TemplateDownloadManager.getInstance().saveTemplate(ISharedPreferencesKeys.IMAGE_DETAIL_PAGE_TEMPLATE, json);
                initTemplateRender();
              }
            }
          });
        }
      }

      @Override public void onFailure(Call call, IOException e) {
      }
    });
  }

  @Override public void checkTemplateForUpdateSuccess(TemplateModel templateModel) {
    TemplateModel model = TemplateDownloadManager.getInstance().getTemplate(ISharedPreferencesKeys.IMAGE_DETAIL_PAGE_TEMPLATE);
    if (model == null || !model.getUrl().equals(templateModel.getUrl())) {
      downloadUpdateFile(templateModel);
    } else {
      initTemplateRender();
    }
  }

  private void initTemplateRender() {
    TemplateRenderManager.getInstance().initTemplateRender(TEMPLATE_NAME,
        new CallBack1<Template>() {
          @Override public void success(Template template) {
            mImageDetailPageTemplate = template;
          }

          @Override public void fail(Template template) {
            mImageDetailPageTemplate = null;
          }
        });
  }
}
