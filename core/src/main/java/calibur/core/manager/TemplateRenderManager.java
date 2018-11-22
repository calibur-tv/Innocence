package calibur.core.manager;

import calibur.core.manager.templaterender.ArticleTemplateRender;
import calibur.core.manager.templaterender.ITemplateRender;
import calibur.core.manager.templaterender.ImageDetailPageTemplateRender;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/22 11:06 PM
 * version: 1.0
 * description:模板管理类
 */
public class TemplateRenderManager {

  public static final String RENDER1 = "render1";
  public static final String RENDER2 = "render2";
  public static final String RENDER3 = "render3";

  private static TemplateRenderManager sInstance;
  private ITemplateRender articleTemplateRender;
  private ITemplateRender imageDetailPageTemplateRender;

  public static TemplateRenderManager getInstance() {
    if (sInstance == null) {
      sInstance = new TemplateRenderManager();
    }
    return sInstance;
  }

  private TemplateRenderManager() {
  }

  public void setTemplateRender(ITemplateRender render) {
    if (render instanceof ArticleTemplateRender) {
      articleTemplateRender = render;
    } else if (render instanceof ImageDetailPageTemplateRender) {
      imageDetailPageTemplateRender = render;
    }
  }

  public ITemplateRender getTemplateRender(String name) {
    switch (name) {
      case RENDER1:
        return articleTemplateRender;
      case RENDER2:
        return imageDetailPageTemplateRender;
      default:
        break;
    }
    return null;
  }
}
