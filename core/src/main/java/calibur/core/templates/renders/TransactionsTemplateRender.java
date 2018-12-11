package calibur.core.templates.renders;

import com.samskivert.mustache.Template;

import java.util.HashMap;
import java.util.Map;

import calibur.core.http.models.TemplateModel;
import calibur.core.manager.UserSystem;
import calibur.core.templates.TemplateDownloadManager;
import calibur.core.utils.ISharedPreferencesKeys;

public class TransactionsTemplateRender extends BaseTemplateRender {
    @Override
    public String getTemplateRenderData(String renderStr) {
        Map<String, String> data = new HashMap<>();
        data.put("data", renderStr);
        data.put("token","Bearer "+ UserSystem.getInstance().getUserToken());
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
        return TemplateDownloadManager.getInstance().getTemplate(ISharedPreferencesKeys.TRANSACTIONS_PAGE_TEMPLATE);
    }

    @Override public void saveTemplateModel2Local(String json) {
        TemplateDownloadManager.getInstance().saveTemplate(ISharedPreferencesKeys.TRANSACTIONS_PAGE_TEMPLATE, json);
    }
}
