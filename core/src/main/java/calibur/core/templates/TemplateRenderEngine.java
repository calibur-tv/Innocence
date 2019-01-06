package calibur.core.templates;

import calibur.core.templates.renders.BookmarksTemplateRender;
import calibur.core.templates.renders.CommentItemTemplateRender;
import calibur.core.templates.renders.EditorTemplateRender;
import calibur.core.templates.renders.HomeTemplateRender;
import calibur.core.templates.renders.ITemplateRender;
import calibur.core.templates.renders.ImageDetailPageTemplateRender;
import calibur.core.templates.renders.NoticeTemplateRender;
import calibur.core.templates.renders.NotificationsTemplateRender;
import calibur.core.templates.renders.PostDetailPageTemplateRender;
import calibur.core.templates.renders.ReviewTemplateRender;
import calibur.core.templates.renders.RoleDetailTemplateRender;
import calibur.core.templates.renders.TransactionsTemplateRender;
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
 * description:模板渲染引擎
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class TemplateRenderEngine {

  //模板的类型
  public static final String EDITOR = "editor";
  public static final String IMAGEDETAIL = "image";
  public static final String BOOKMARKS = "bookmarks";
  public static final String NOTICE = "notice";
  //漫评详情页的模板
  public static final String REVIEW = "review";
  public static final String POST = "post";
  public static final String NOTIFICATIONS = "notifications";
  public static final String TRANSACTIONS = "transactions";
  public static final String HOME = "home";
  public static final String ROLE = "role";
  public static final String COMMENT = "comment";

  private static TemplateRenderEngine sInstance;
  private ITemplateRender editorTemplateRender;
  private ITemplateRender imageDetailPageTemplateRender;
  private ITemplateRender bookmarksTemplateRender;
  private ITemplateRender noticeTemplateRender;
  private ITemplateRender reviewTemplateRender;
  private ITemplateRender postDetailPageTemplateRender;
  private ITemplateRender notificationTemplateRender;
  private ITemplateRender transactionsTemplateRender;
  private ITemplateRender homeTemplateRender;
  private ITemplateRender roleDetailTemplateRender;
  private ITemplateRender commentItemTemplateRender;

  public static TemplateRenderEngine getInstance() {
    if (sInstance == null) {
      sInstance = new TemplateRenderEngine();
    }
    return sInstance;
  }

  private TemplateRenderEngine() {
  }

  public void setTemplateRender(ITemplateRender render) {
    if (render instanceof EditorTemplateRender) {
      render.setTemplateName("EditorPageTemplate");
      editorTemplateRender = render;
    } else if (render instanceof ImageDetailPageTemplateRender) {
      render.setTemplateName("ImageDetailPageTemplate");
      imageDetailPageTemplateRender = render;
    } else if (render instanceof BookmarksTemplateRender) {
      render.setTemplateName("BookmarksTemplate");
      bookmarksTemplateRender = render;
    } else if (render instanceof NoticeTemplateRender) {
      render.setTemplateName("NoticeTemplate");
      noticeTemplateRender = render;
    } else if (render instanceof ReviewTemplateRender) {
      render.setTemplateName("ReviewTemplate");
      reviewTemplateRender = render;
    }else if (render instanceof PostDetailPageTemplateRender) {
      render.setTemplateName("PostDetailPageTemplate");
      postDetailPageTemplateRender = render;
    }else if (render instanceof NotificationsTemplateRender) {
      render.setTemplateName("NotificationsTemplate");
      notificationTemplateRender = render;
    }else if (render instanceof TransactionsTemplateRender) {
      render.setTemplateName("TransactionsTemplate");
      transactionsTemplateRender = render;
    }else if (render instanceof HomeTemplateRender) {
      render.setTemplateName("HomeTemplate");
      homeTemplateRender = render;
    }else if (render instanceof RoleDetailTemplateRender) {
      render.setTemplateName("RoleDetailTemplate");
      roleDetailTemplateRender = render;
    }else if (render instanceof CommentItemTemplateRender) {
      render.setTemplateName("CommentItemPageTemplate");
      commentItemTemplateRender = render;
    }
  }

  public ITemplateRender getTemplateRender(String name) {
    switch (name) {
      case EDITOR:
        if(editorTemplateRender == null)
          editorTemplateRender = new EditorTemplateRender();
        return editorTemplateRender;
      case IMAGEDETAIL:
        if(imageDetailPageTemplateRender == null)
          imageDetailPageTemplateRender = new ImageDetailPageTemplateRender();
        return imageDetailPageTemplateRender;
      case BOOKMARKS:
        if(bookmarksTemplateRender == null)
          bookmarksTemplateRender = new BookmarksTemplateRender();
        return bookmarksTemplateRender;
      case NOTICE:
        if(noticeTemplateRender == null)
          noticeTemplateRender = new NoticeTemplateRender();
        return noticeTemplateRender;
      case REVIEW:
        if(reviewTemplateRender == null)
          reviewTemplateRender = new ReviewTemplateRender();
        return reviewTemplateRender;
      case POST:
        if(postDetailPageTemplateRender == null)
          postDetailPageTemplateRender = new PostDetailPageTemplateRender();
        return postDetailPageTemplateRender;
      case NOTIFICATIONS:
        if(notificationTemplateRender == null)
          notificationTemplateRender = new NotificationsTemplateRender();
        return notificationTemplateRender;
      case TRANSACTIONS:
        if(transactionsTemplateRender == null)
          transactionsTemplateRender = new TransactionsTemplateRender();
        return transactionsTemplateRender;
      case HOME:
        if(homeTemplateRender == null)
          homeTemplateRender = new HomeTemplateRender();
        return homeTemplateRender;
      case ROLE:
        if(roleDetailTemplateRender == null)
          roleDetailTemplateRender = new RoleDetailTemplateRender();
        return roleDetailTemplateRender;
      case COMMENT:
        if(commentItemTemplateRender == null)
          commentItemTemplateRender = new CommentItemTemplateRender();
        return commentItemTemplateRender;
      default:
        break;
    }
    return null;
  }

  public void checkAllTemplateForUpdate() {
    checkNotificationTemplateForUpdate();
    checkEditorTemplateForUpdate();
    checkImageDetailPageTemplateForUpdate();
    checkBookmarksTemplateForUpdate();
    checkNoticeTemplateForUpdate();
    checkReviewTemplateForUpdate();
    checkPostDetailPageTemplateForUpdate();
    checkTransactionTemplateForUpdate();
    checkHomeTemplateForUpdate();
    checkRoleDetailTemplateForUpdate();
    checkCommentItemTemplateForUpdate();
  }

  public void checkEditorTemplateForUpdate() {
    if (editorTemplateRender != null) editorTemplateRender.updateTemplateIfNecessary(EDITOR);
  }

  public void checkImageDetailPageTemplateForUpdate() {
    if (imageDetailPageTemplateRender != null) imageDetailPageTemplateRender.updateTemplateIfNecessary(IMAGEDETAIL);
  }

  public void checkBookmarksTemplateForUpdate() {
    if (bookmarksTemplateRender != null) bookmarksTemplateRender.updateTemplateIfNecessary(BOOKMARKS);
  }

  public void checkNoticeTemplateForUpdate() {
    if (noticeTemplateRender != null) noticeTemplateRender.updateTemplateIfNecessary(NOTICE);
  }

  public void checkReviewTemplateForUpdate() {
    if(reviewTemplateRender != null) reviewTemplateRender.updateTemplateIfNecessary(REVIEW);
  }

  public void checkPostDetailPageTemplateForUpdate() {
    if(getTemplateRender(TemplateRenderEngine.POST) != null) postDetailPageTemplateRender.updateTemplateIfNecessary(POST);
  }

  public void checkNotificationTemplateForUpdate() {
    if(notificationTemplateRender != null) notificationTemplateRender.updateTemplateIfNecessary(NOTIFICATIONS);
  }

  public void checkTransactionTemplateForUpdate() {
    if(transactionsTemplateRender != null) transactionsTemplateRender.updateTemplateIfNecessary(TRANSACTIONS);
  }

  public void checkHomeTemplateForUpdate() {
    if (homeTemplateRender != null) homeTemplateRender.updateTemplateIfNecessary(HOME);
  }

  public void checkRoleDetailTemplateForUpdate() {
    if (roleDetailTemplateRender != null) roleDetailTemplateRender.updateTemplateIfNecessary(ROLE);
  }

  public void checkCommentItemTemplateForUpdate() {
    if (commentItemTemplateRender != null) commentItemTemplateRender.updateTemplateIfNecessary(COMMENT);
  }


  public void initTemplateRender(final String templateName, final CallBack1<Template> callback) {
    Observable.create(new ObservableOnSubscribe<Template>() {
      @Override public void subscribe(ObservableEmitter<Template> emitter) {
        Template template;
        File articleTemplateFile = new File(TemplateDownloadManager.getInstance().getTemplatePath() + "/" + templateName + ".html");
        FileInputStream inputStream;
        if (articleTemplateFile.exists()) {
          try {
            inputStream = new FileInputStream(articleTemplateFile);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String temp = new String(buffer, "UTF-8");
            template = Mustache.compiler().defaultValue("").compile(temp);
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
