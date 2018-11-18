package calibur.foundation.callback;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/18 9:34 AM
 * version: 1.0
 * description:
 */
public interface CallBack1<T> {
    void success(T t);
    void fail(T t);
}
