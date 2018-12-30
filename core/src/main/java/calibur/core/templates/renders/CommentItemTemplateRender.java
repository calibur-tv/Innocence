package calibur.core.templates.renders;

import com.samskivert.mustache.Template;

import java.util.HashMap;
import java.util.Map;

import calibur.core.http.models.TemplateModel;
import calibur.core.manager.UserSystem;
import calibur.core.templates.TemplateDownloadManager;
import calibur.core.utils.ISharedPreferencesKeys;
import calibur.foundation.utils.AppUtil;

public class CommentItemTemplateRender extends BaseTemplateRender {

    public CommentItemTemplateRender(){
        setTemplateName("CommentItemPageTemplate");
    }

    @Override
    public String getTemplateRenderData(String renderStr) {
        Map<String, String> data = new HashMap<>();
        data.put("data", renderStr);
        data.put("token", UserSystem.getInstance().getUserToken());
        data.put("name", "Android");
        data.put("version", AppUtil.getAppVersionName());
        Template template = getRenderTemplate();
        return template != null ? template.execute(data) : "";
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public Template getRenderTemplate() {
        if (mTemplate != null) return mTemplate;
        return mTemplate = getTemplateFromLocal();
    }

    @Override public TemplateModel getTemplateModelFromLocal() {
        return TemplateDownloadManager.getInstance().getTemplate(ISharedPreferencesKeys.POST_DETAIL_PAGE_TEMPLATE);
    }

    @Override public void saveTemplateModel2Local(String json) {
        TemplateDownloadManager.getInstance().saveTemplate(ISharedPreferencesKeys.POST_DETAIL_PAGE_TEMPLATE, json);
    }
}
