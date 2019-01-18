package com.riuir.calibur.ui.share;

import android.app.Activity;
import android.os.Bundle;

import com.riuir.calibur.app.CaliburInitializer;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.Tencent;

import java.util.ArrayList;

import calibur.core.http.models.share.ShareDataModel;

public class QQShareUtils {
    /**
     * 分享到QQ
     * @param activity
     *
     * 传入的activity中要在onActivityResult中注册mTencent.onActivityResult(requestCode, resultCode, data);
     * 否则接收不到回调QQBaseUiListener
     */
    public static void toShareQQ(Activity activity, ShareDataModel shareData){

        try {
            Tencent mTencent = CaliburInitializer.getmTencent();
            Bundle params = new Bundle();
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);//分享的类型
            params.putString(QQShare.SHARE_TO_QQ_TITLE, shareData.getTitle());//分享标题
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY,shareData.getDesc());//要分享的内容摘要
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,shareData.getLink());//内容地址
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,shareData.getImage());//分享的图片URL
            params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "calibur");//应用名称
            mTencent.shareToQQ(activity, params , new QQBaseUiListener());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 分享到QQ空间
     * @param activity
     * 传入的activity中要在onActivityResult中注册mTencent.onActivityResult(requestCode, resultCode, data);
     * 否则接收不到回调QQBaseUiListener
     */
    public static void toShareQzone(Activity activity, ShareDataModel shareData){

        try {
            Tencent mTencent = CaliburInitializer.getmTencent();
            Bundle params = new Bundle();
            params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);//分享的类型
            params.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareData.getTitle());//分享标题
            params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY,shareData.getDesc());//要分享的内容摘要
            params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL,shareData.getLink());//内容地址
            //分享的图片, 以ArrayList<String>的类型传入，以便支持多张图片（注：图片最多支持9张图片，多余的图片会被丢弃）
            ArrayList<String> imageUrls = new ArrayList<>();
            imageUrls.add(shareData.getImage());//添加一个图片地址
            params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);//分享的图片URL
            params.putString(QzoneShare.SHARE_TO_QQ_APP_NAME, "calibur");//应用名称
            mTencent.shareToQzone(activity, params , new QQBaseUiListener());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
