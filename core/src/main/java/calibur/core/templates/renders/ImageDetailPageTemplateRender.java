package calibur.core.templates.renders;

import calibur.core.http.models.TemplateModel;
import calibur.core.templates.TemplateDownloadManager;
import calibur.core.utils.ISharedPreferencesKeys;
import com.samskivert.mustache.Template;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/22 11:59 PM
 * version: 1.0
 * description:
 */
public class ImageDetailPageTemplateRender extends BaseTemplateRender {

  @Override public String getTemplateRenderData(String renderStr) {
    return null;
  }

  @Override public Template getRenderTemplate() {
    if (mTemplate != null) return mTemplate;
    return mTemplate = getTemplateFromLocal(TEMPLATE_NAME);
  }

  @Override public void updateTemplateSuccess(TemplateModel templateModel) {
    TemplateModel model = TemplateDownloadManager.getInstance().getTemplate(ISharedPreferencesKeys.IMAGE_DETAIL_PAGE_TEMPLATE);
    if (model == null || !model.getUrl().equals(templateModel.getUrl())) {
      downloadUpdateFile(templateModel);
    } else {
      initTemplate();
    }
  }

  @Override public void saveTemplateModel2Local(String json) {
    TemplateDownloadManager.getInstance().saveTemplate(ISharedPreferencesKeys.IMAGE_DETAIL_PAGE_TEMPLATE, json);
  }
}
