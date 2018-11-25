package calibur.core.manager.templaterender;

import calibur.core.http.models.TemplateModel;
import com.samskivert.mustache.Template;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/22 11:45 PM
 * version: 1.0
 * description:
 */
public interface ITemplateRender {
  /**
   * Get date which use template replace.
   * @param renderStr data
   * @return String
   */
  String getTemplateRenderData(String renderStr);

  /**
   * Return the render template
   * @return template
   */
  Template getRenderTemplate();

  /**
   * Check the template update which specified.
   * @param businessName
   */
  void checkForUpdate(String businessName);

  /**
   * Download template file when has update.
   * @param model model
   */
  void downloadUpdateFile(TemplateModel model);

  /**
   * Set Template name.
   * @param name name
   */
  void setTemplateName(String name);
}
