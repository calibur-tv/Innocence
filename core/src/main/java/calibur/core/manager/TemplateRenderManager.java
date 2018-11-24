package calibur.core.manager;

import calibur.core.manager.templaterender.BookmarksTemplateRender;
import calibur.core.manager.templaterender.EditorTemplateRender;
import calibur.core.manager.templaterender.ITemplateRender;
import calibur.core.manager.templaterender.ImageDetailPageTemplateRender;
import calibur.core.manager.templaterender.NoticeTemplateRender;
import calibur.foundation.callback.CallBack1;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.observers.DefaultObserver;
import java.io.File;
import java.io.FileInputStream;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/22 11:06 PM
 * version: 1.0
 * description:模板管理类
 */
public class TemplateRenderManager {

  public static final String EDITOR = "editor";
  public static final String IMAGEDETAIL = "imageDetail";
  public static final String BOOKMARKS = "bookmarks";
  public static final String NOTICE = "notice";

  private static TemplateRenderManager sInstance;
  private ITemplateRender editorTemplateRender;
  private ITemplateRender imageDetailPageTemplateRender;
  private ITemplateRender bookmarksTemplateRender;
  private ITemplateRender noticeTemplateRender;

  public static TemplateRenderManager getInstance() {
    if (sInstance == null) {
      sInstance = new TemplateRenderManager();
    }
    return sInstance;
  }

  private TemplateRenderManager() {
  }

  public void setTemplateRender(ITemplateRender render) {
    if (render instanceof EditorTemplateRender) {
      editorTemplateRender = render;
    } else if (render instanceof ImageDetailPageTemplateRender) {
      imageDetailPageTemplateRender = render;
    } else if (render instanceof BookmarksTemplateRender) {
      bookmarksTemplateRender = render;
    } else if (render instanceof NoticeTemplateRender) {
      noticeTemplateRender = render;
    }
  }

  public ITemplateRender getTemplateRender(String name) {
    switch (name) {
      case EDITOR:
        return editorTemplateRender;
      case IMAGEDETAIL:
        return imageDetailPageTemplateRender;
      case BOOKMARKS:
        return bookmarksTemplateRender;
      case NOTICE:
        return noticeTemplateRender;
      default:
        break;
    }
    return null;
  }

  public void checkForUpdate() {
    if (editorTemplateRender != null) editorTemplateRender.checkForUpdate(EDITOR);
    if (imageDetailPageTemplateRender != null) imageDetailPageTemplateRender.checkForUpdate(IMAGEDETAIL);
    if (noticeTemplateRender != null) noticeTemplateRender.checkForUpdate(NOTICE);
    if (bookmarksTemplateRender != null) bookmarksTemplateRender.checkForUpdate(BOOKMARKS);
  }

  public void initTemplateRender(final String templateName, final String businessName, final CallBack1<Template> callback) {
    Observable.create(new ObservableOnSubscribe<Template>() {
      @Override public void subscribe(ObservableEmitter<Template> emitter) {
        Template template = null;
        File articleTemplateFile = new File(TemplateDownloadManager.getInstance().getTemplatePath() + "/" + templateName);
        FileInputStream inputStream;
        if (articleTemplateFile.exists()) {
          try {
            inputStream = new FileInputStream(articleTemplateFile);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String temp = new String(buffer, "UTF-8");
            switch (businessName) {
              case EDITOR:
                template = Mustache.compiler().compile(temp);
                break;
              case IMAGEDETAIL:
                template = Mustache.compiler().compile(temp);
                break;
              case BOOKMARKS:
                template = Mustache.compiler().compile(temp);
                break;
            }
            emitter.onNext(template);
          } catch (Throwable e) {
            e.printStackTrace();
            emitter.onError(e);
          }
        }
      }
    }).compose(Rx2Schedulers.<Template>applyObservableAsync()).subscribe(new DefaultObserver<Template>() {
      @Override public void onNext(Template template) {
        if(callback != null) callback.success(template);
      }

      @Override public void onError(Throwable e) {
        if(callback != null) callback.fail(null);
      }

      @Override public void onComplete() {
      }
    });
  }

}
