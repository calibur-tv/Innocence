package calibur.foundation.utils;

import android.content.Context;
import calibur.foundation.FoundationContextHolder;
import java.io.File;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/20 1:30 PM
 * version: 1.0
 * description:
 */
public class FilePathUtil {
  /**
   * app's sd card file root folder
   *
   * @return app's sd card file root folder
   */
  public static File getSDFileDir() {
    return getSDFileDir(null);
  }

  /**
   * app's sd card file root folder and sub path
   *
   * @param subPath the sub folder
   * @return app's sd card file root folder/subPath
   */
  /**
   * app's sd card file root folder and sub path
   *
   * @param subPath the sub folder
   * @return app's sd card file root folder/subPath
   */
  public static File getSDFileDir(String subPath) {

    File returnFile = null;

    if (LocalCacheUtil.isSDCardMounted()) {
      try {
        if (StringUtil.isBlank(subPath)) {
          returnFile = FoundationContextHolder.getContext().getExternalFilesDir(null);
        } else {
          returnFile = new File(FoundationContextHolder.getContext().getExternalFilesDir(null), subPath);
        }
        if (!returnFile.exists()) {
          returnFile.mkdirs();
        }
        return returnFile;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    if (StringUtil.isBlank(subPath)) {
      returnFile = FoundationContextHolder.getContext().getFilesDir();
    } else {
      returnFile = new File(FoundationContextHolder.getContext().getFilesDir(), subPath);
    }

    if (!returnFile.exists()) {
      returnFile.mkdirs();
    }
    return returnFile;
  }

  /**
   * app's sd card file root folder and sub path
   *
   * @param subPath the sub folder
   * @return app's sd card file root folder/subPath
   */
  public static File getSDFileDir(String subPath, Context context) {

    File returnFile = null;

    if (LocalCacheUtil.isSDCardMounted()) {
      if (StringUtil.isBlank(subPath)) {
        returnFile = context.getExternalFilesDir(null);
      } else {
        returnFile = new File(context.getExternalFilesDir(null), subPath);
      }
    } else {
      if (StringUtil.isBlank(subPath)) {
        returnFile = context.getFilesDir();
      } else {
        returnFile = new File(context.getFilesDir(), subPath);
      }
    }

    if (!returnFile.exists()) {
      returnFile.mkdirs();
    }

    return returnFile;
  }

  /**
   * app's sd card file root folder
   *
   * @return app's sd card file root folder
   */
  public static File getSDCacheDir() {
    return getSDCacheDir(null);
  }

  /**
   * app's sd card file root folder and sub path
   *
   * @param subPath the sub folder
   * @return app's sd card file root folder/subPath
   */
  public static File getSDCacheDir(String subPath) {
    if (LocalCacheUtil.isSDCardMounted()) {
      if (StringUtil.isBlank(subPath)) {
        return FoundationContextHolder.getContext().getExternalCacheDir();
      } else {
        return new File(FoundationContextHolder.getContext().getExternalCacheDir(), subPath);
      }
    } else {
      if (StringUtil.isBlank(subPath)) {
        return FoundationContextHolder.getContext().getFilesDir();
      } else {
        return new File(FoundationContextHolder.getContext().getFilesDir(), subPath);
      }
    }
  }

  /**
   * to save tracks m4a files
   *
   * @return track downloads files
   */
  public static File getTrackFilesDir() {
    String subPath = "tracks";
    if (StringUtil.isBlank(subPath)) {
      return FoundationContextHolder.getContext().getFilesDir();
    } else {
      return new File(FoundationContextHolder.getContext().getFilesDir(), subPath);
    }
  }

  public static File getTrackFilesDir(Context context) {
    String subPath = "tracks";
    if (StringUtil.isBlank(subPath)) {
      return context.getFilesDir();
    } else {
      return new File(context.getFilesDir(), subPath);
    }
  }

  public static File getZipDownloadDir() {
    return getSDFileDir("zip");
  }

  /**
   * to save tracks m4a files
   *
   * @return track downloads files
   */
  public static File getTrackDownloadDir() {
    return getSDFileDir("tracks");
  }

  public static File getTrackDownloadDir(Context context) {
    return getSDFileDir("tracks",context);
  }

  /**
   * local user created videos save path
   *
   * @return local user created videos save path
   */
  public static File getLocalVideoDir() { /*return new File(sApplication.getCacheDir(), "localvideos");*/
    return getSDFileDir("localvideos");
  }

  /**
   * @return musical video download path
   */
  public static File getVideoDownloadDir() {
    return getSDFileDir("videos");
  }

  /**
   * @return the cache which never clear
   */

  public static File getNoClearDir() {
    return getSDFileDir("noclear");
  }

  /**
   * @return the cache which never clear with context
   */

  public static File getNoClearDir(Context context) {
    return getSDFileDir("noclear", context);
  }

  /**
   * @return musical cover download path
   */
  public static File getImageDownloadDir() {
    return getSDFileDir("images");
  }

}
