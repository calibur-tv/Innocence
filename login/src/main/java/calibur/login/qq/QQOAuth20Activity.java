package calibur.login.qq;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import calibur.core.http.models.QQOpenIdResponse;
import calibur.core.widget.webview.AthenaWebView;
import calibur.foundation.utils.JSONUtil;
import calibur.foundation.utils.URLEncoderUtil;
import calibur.login.R;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Author：J.Chou
 * Date：  2017.05.08 5:02 PM
 * Email： who_know_me@163.com
 * Describe:
 */
public class QQOAuth20Activity extends AppCompatActivity {

  public static final String QQ_API_ID = "101524534";
  public static final String QQ_SCOPE = "get_user_info";
  public static final String QQ_REDIRECT_URL = "http://www.jianshu.com/users/auth/qq_connect/callback";
  public static final String QQ_URL_OAUTH2_AUTHORIZATION_CODE =
      "https://graph.qq.com/oauth2.0/authorize?response_type=token&display=mobile&client_id=[YOUR_APPID]&redirect_uri=[YOUR_REDIRECT_URI]&scope=[THE_SCOPE]";
  public static final String QQ_URL_OAUTH2_OPENID = "https://graph.qq.com/oauth2.0/me?access_token=[YOUR_ACCESS_TOKEN]";

  private AthenaWebView mWebView;
  public static String mUrl;
  private String mAccessToken;

  public static void call(Activity context) {
    QQOAuth20Activity.mUrl = buildUrl();
    Intent intent = new Intent(context, QQOAuth20Activity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
    context.startActivity(new Intent(context, QQOAuth20Activity.class));
  }

  private static String buildUrl() {
    String encodeUrl, url = "";
    try {
      encodeUrl = URLEncoderUtil.utf8Encode(QQ_REDIRECT_URL);
      url = QQ_URL_OAUTH2_AUTHORIZATION_CODE;
      url = url.replace("[YOUR_APPID]", QQ_API_ID);
      url = url.replace("[YOUR_REDIRECT_URI]", encodeUrl);//简书重定向链接
      url = url.replace("[THE_SCOPE]", QQ_SCOPE);
    } catch (Throwable e) {
      e.printStackTrace();
    }
    return url;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_qq_login_author);
    setupWebView();
  }

  @SuppressLint("SetJavaScriptEnabled")
  private void setupWebView() {
    mWebView = this.findViewById(R.id.webview);
    mWebView.setWebViewClient(new QQWebViewClient());
    mWebView.loadUrl(mUrl);
    mWebView.requestFocus();
  }

  private class QQWebViewClient extends WebViewClient {

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      if (url.startsWith(QQ_REDIRECT_URL)) {
        try {
          handlerLoginUrl(url);
        } catch (Exception e) {
          e.printStackTrace();
        }
        return true;
      }
      return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
      super.onReceivedError(view, errorCode, description, failingUrl);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
      if (isFinishing()) {
        return;
      }
      super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
      super.onPageFinished(view, url);
      if (isFinishing()) {
        return;
      }
      mWebView.setVisibility(View.VISIBLE);
    }
  }

  private void handlerLoginUrl(String url) throws IOException {
    if (TextUtils.isEmpty(url)) {
      return;
    }
    String newUrl = url.replace("?#", "=");
    newUrl = newUrl.replace("&", "=");
    String[] params = newUrl.split("=");
    mAccessToken = params[2];//
    if (!TextUtils.isEmpty(mAccessToken)) {
      useOkhttpAsyncGetOpenIdWithAccessToken(mAccessToken);
    } else {
    }
  }

  private void useOkhttpAsyncGetOpenIdWithAccessToken(String token) {
    String url = QQ_URL_OAUTH2_OPENID;
    url = url.replace("[YOUR_ACCESS_TOKEN]", token);
    Uri.Builder urlBuilder = Uri.parse(url).buildUpon();
    final String realUrl = urlBuilder.build().toString();

    Request.Builder builder = new Request.Builder().url(realUrl).get();

    final Request request = builder.build();
    OkHttpClient okHttpClient = new OkHttpClient();
    final Call call = okHttpClient.newCall(request);

    Observable.create(new ObservableOnSubscribe<QQOpenIdResponse>() {
      @Override
      public void subscribe(final ObservableEmitter<QQOpenIdResponse> emitter) {
        call.enqueue(new Callback() {
          @Override
          public void onFailure(Call call, IOException e) {
            emitter.onError(e);
          }

          @Override
          public void onResponse(Call call, Response response) throws IOException {
            // 获取到用户OpenID，返回包如下：
            // callback( {"client_id":"YOUR_APPID","openid":"YOUR_OPENID"} );
            ResponseBody body = response.body();
            String respStr = new String(body.bytes(), "utf-8");
            int start = respStr.indexOf("{");
            int end = respStr.indexOf("}") + 1;
            String substring = respStr.substring(start, end);
            QQOpenIdResponse callbackResponse = JSONUtil.fromJson(substring, QQOpenIdResponse.class);
            emitter.onNext(callbackResponse);
          }
        });
      }
    }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<QQOpenIdResponse>() {
          @Override
          public void onSubscribe(Disposable d) {

          }

          @Override
          public void onNext(QQOpenIdResponse callbackResponse) {
          }

          @Override
          public void onError(Throwable e) {
          }

          @Override
          public void onComplete() {

          }
        });
  }
}
