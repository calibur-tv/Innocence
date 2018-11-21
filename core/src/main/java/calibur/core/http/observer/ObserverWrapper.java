package calibur.core.http.observer;

import calibur.core.R;
import calibur.core.http.NetworkManager;
import calibur.core.http.error.HttpErrorCode;
import calibur.core.http.error.HttpStatusCode;
import calibur.core.http.models.base.ResponseBean;
import calibur.foundation.FoundationContextHolder;
import calibur.foundation.bus.BusinessBus;
import com.orhanobut.logger.Logger;
import io.reactivex.observers.DisposableObserver;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import javax.net.ssl.SSLException;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/20 3:59 PM
 * version: 1.0
 * description:
 */
public abstract class ObserverWrapper<T extends ResponseBean> extends DisposableObserver<T> {

  @Override public void onNext(T response) {
    if(response == null) return;
    if (response.isSuccessful()) {
      try {
        onSuccess(response);
      } catch (Throwable throwable) {
        Logger.e(throwable.getMessage());
        BusinessBus.post(null, "mainModule/postException2Bugly", throwable);
      }
    } else {
      onFailure(response.getCode(), response.getMessage());
    }
  }

  @Override public void onError(Throwable e) {
    int errorCode;
    String errorMsg;
    if (e instanceof retrofit2.HttpException) {
      retrofit2.HttpException httpException = (retrofit2.HttpException) e;
      errorCode = httpException.code();
      errorMsg = httpException.message();
      if (!NetworkManager.isConnected()) {
        errorCode = HttpErrorCode.BAD_NET_WORK;
        errorMsg = FoundationContextHolder.getContext().getString(R.string.network_not_connected);
      } else if (errorCode == HttpStatusCode.SC_GATEWAY_TIMEOUT) {
        errorMsg = FoundationContextHolder.getContext().getString(R.string.network_not_connected);
      }
    } else if (e instanceof SSLException) {
      errorCode = HttpErrorCode.SSL_PEER_UNVERIFIED_EXCEPTION;
      errorMsg = FoundationContextHolder.getContext().getString(R.string.ssl_exception_network_connection);
    } else if (e instanceof SocketTimeoutException) {
      errorCode = HttpErrorCode.CONNECT_TIMEOUT;
      errorMsg = FoundationContextHolder.getContext().getString(R.string.time_out);
    } else if (e instanceof UnknownHostException) {
      errorCode = HttpErrorCode.BAD_NET_WORK;
      errorMsg = FoundationContextHolder.getContext().getString(R.string.unknow_host_exception);
    } else if (e instanceof ConnectException) {
      errorMsg = FoundationContextHolder.getContext().getString(R.string.network_connection_exception);
      errorCode = HttpErrorCode.BAD_NET_WORK;
    } else {
      errorCode = 0;
      errorMsg = e.getMessage();
    }
    onFailure(errorCode, errorMsg);
  }

  @Override public void onComplete() {
  }

  public abstract void onSuccess(T t);

  public void onFailure(int code, String errorMsg) {
    BusinessBus.post(null, "mainModule/showToast", errorMsg);
  }
}