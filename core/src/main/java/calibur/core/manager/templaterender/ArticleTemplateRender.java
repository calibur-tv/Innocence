package calibur.core.manager.templaterender;

import calibur.foundation.FoundationContextHolder;
import calibur.foundation.bus.BusinessBus;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/22 11:48 PM
 * version: 1.0
 * description:
 */
public class ArticleTemplateRender implements ITemplateRender {

  private Template mArticleTemplate;

  @Override public String getTemplateRenderData(String renderStr) {
    Map<String, String> data = new HashMap<>();
    data.put("data", renderStr);
    data.put("token", renderStr);
    Template template = getRenderTemplate();
    return template != null ? template.execute(data) : "";
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  @Override public Template getRenderTemplate() {
    if (mArticleTemplate != null) return mArticleTemplate;
    try {
      InputStream inputStream = FoundationContextHolder.getContext().getAssets().open("temple_article.mustache");
      int size = inputStream.available();
      byte[] buffer = new byte[size];
      inputStream.read(buffer);
      inputStream.close();
      String temp = new String(buffer, "UTF-8");
      mArticleTemplate = Mustache.compiler().compile(temp);
      return mArticleTemplate;
    } catch (IOException e) {
      e.printStackTrace();
      BusinessBus.post(null, "mainApps/postException2Bugly", e);
    }
    return null;
  }
}
