package calibur.core.templates.renders;

import calibur.core.http.models.TemplateModel;
import calibur.core.templates.TemplateDownloadManager;
import calibur.core.utils.ISharedPreferencesKeys;
import com.samskivert.mustache.Template;
import java.util.HashMap;
import java.util.Map;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/22 11:48 PM
 * version: 1.0
 * description:
 */
public class EditorTemplateRender extends BaseTemplateRender {

  @Override public String getTemplateRenderData(String renderStr) {
    Map<String, String> data = new HashMap<>();
    data.put("data", renderStr);
    data.put("token", renderStr);
    Template template = getRenderTemplate();
    return template != null ? template.execute(data) : "";
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  @Override public Template getRenderTemplate() {
    if (mTemplate != null) return mTemplate;
    return mTemplate = getTemplateFromLocal(TEMPLATE_NAME);
  }

  @Override public void checkTemplateForUpdateSuccess(TemplateModel templateModel) {
    TemplateModel model = TemplateDownloadManager.getInstance().getTemplate(ISharedPreferencesKeys.NOTICE_PAGE_TEMPLATE);
    if (model == null || !model.getUrl().equals(templateModel.getUrl())) {
      downloadUpdateFile(templateModel);
    } else {
      initTemplate();
    }
  }

  @Override public void saveTemplateModel2Local(String json) {
    TemplateDownloadManager.getInstance().saveTemplate(ISharedPreferencesKeys.NOTICE_PAGE_TEMPLATE, json);
  }
}
