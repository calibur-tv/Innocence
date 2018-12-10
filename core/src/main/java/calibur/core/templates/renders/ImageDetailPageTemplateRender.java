package calibur.core.templates.renders;

import calibur.core.http.models.TemplateModel;
import calibur.core.manager.UserSystem;
import calibur.core.templates.TemplateDownloadManager;
import calibur.core.utils.ISharedPreferencesKeys;
import com.samskivert.mustache.Template;

import java.util.HashMap;
import java.util.Map;

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

  @SuppressWarnings("ResultOfMethodCallIgnored")
  @Override public Template getRenderTemplate() {
    if (mTemplate != null) return mTemplate;
    return mTemplate = getTemplateFromLocal();
  }

  @Override public TemplateModel getTemplateModelFromLocal() {
    return TemplateDownloadManager.getInstance().getTemplate(ISharedPreferencesKeys.IMAGE_DETAIL_PAGE_TEMPLATE);
  }

  @Override public void saveTemplateModel2Local(String json) {
    TemplateDownloadManager.getInstance().saveTemplate(ISharedPreferencesKeys.IMAGE_DETAIL_PAGE_TEMPLATE, json);
  }
}
