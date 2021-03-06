package com.riuir.calibur.ui.web;

import android.webkit.WebView;
import calibur.core.templates.TemplateRenderEngine;

import com.orhanobut.logger.Logger;
import com.riuir.calibur.assistUtils.LogUtils;

public class WebTemplatesUtils {
    /**
     *
     * @param webView 加载模板的webview对象
     * @param tempName 模板名称
     *   EDITOR = "editor";
     *   IMAGEDETAIL = "image";
     *   BOOKMARKS = "bookmarks";
     *   NOTICE = "notice";
     *   //漫评详情页的模板
     *   REVIEW = "review";
     * @param renderStr 需要给模板中插入的数据
     */
    public static void loadTemplates(WebView webView,String tempName,String renderStr){
        String baseData = TemplateRenderEngine.getInstance().getTemplateRender(tempName).getTemplateRenderData(renderStr);
        LogUtils.d("checkWebData","baseData = "+baseData);
        webView.loadDataWithBaseURL(null,baseData,"text/html","utf-8",null);
    }
}
