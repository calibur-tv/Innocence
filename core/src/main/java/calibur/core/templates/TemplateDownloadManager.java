package calibur.core.templates;

import android.text.TextUtils;
import calibur.core.http.models.TemplateModel;
import calibur.core.utils.SharedPreferencesUtil;
import calibur.foundation.bus.BusinessBus;
import calibur.foundation.utils.FilePathUtil;
import calibur.foundation.utils.JSONUtil;
import com.orhanobut.logger.Logger;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import okhttp3.ResponseBody;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/22 11:06 PM
 * version: 1.0
 * description:模板下载管理类
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class TemplateDownloadManager {
  private static final String TEMPLATE_FOLDER_NAME = "templates";

  private static TemplateDownloadManager sInstance;

  public static TemplateDownloadManager getInstance() {
    if (sInstance == null) {
      sInstance = new TemplateDownloadManager();
    }
    return sInstance;
  }

  private TemplateDownloadManager() {
  }

  public String getTemplatePath() {
    File file = new File(FilePathUtil.getAppDataDir(), TEMPLATE_FOLDER_NAME);
    if (!file.exists()) {
      file.mkdir();
    }
    return file.getAbsolutePath();
  }

  public boolean serializeTemplateFileToDisk(ResponseBody body, String fileName) {
    fileName = fileName + ".html";
    final File templateFile = new File(getTemplatePath(), fileName);
    try {
      InputStream inputStream = null;
      OutputStream outputStream = null;
      try {
        long fileSize = body.contentLength();
        long fileSizeDownloaded = 0;
        inputStream = body.byteStream();
        outputStream = new FileOutputStream(templateFile);
        byte[] fileReader = new byte[inputStream.available()];
        while (true) {
          int read = inputStream.read(fileReader);
          if (read == -1) {
            break;
          }
          outputStream.write(fileReader, 0, read);
          fileSizeDownloaded += read;
          Logger.d("download: " + fileSizeDownloaded + " of " + fileSize);
        }
        outputStream.flush();
        return true;
      } catch (IOException e) {
        if (templateFile.exists()) {
          templateFile.delete();
        }
        BusinessBus.post(null, "mainModule/postException2Bugly", e);
        return false;
      } finally {
        if (inputStream != null) {
          inputStream.close();
        }
        if (outputStream != null) {
          outputStream.close();
        }
      }
    } catch (IOException e) {
      return false;
    }
  }

  public void saveTemplate(String key, String value) {
    if(!TextUtils.isEmpty(value))
      SharedPreferencesUtil.putString(key, value);
  }

  public TemplateModel getTemplate(String key) {
    TemplateModel model = null;
    try {
      String s = SharedPreferencesUtil.getString(key);
      model = JSONUtil.fromJson(s, TemplateModel.class);
    } catch (Throwable r) {
      r.printStackTrace();
    }
    return model;
  }

}
