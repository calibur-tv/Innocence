package calibur.core.manager.templaterender;

import com.samskivert.mustache.Template;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/22 11:45 PM
 * version: 1.0
 * description:
 */
public interface ITemplateRender {
  String getTemplateRenderData(String renderStr);
  Template getRenderTemplate();
}
