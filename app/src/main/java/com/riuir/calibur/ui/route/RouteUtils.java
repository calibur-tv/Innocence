package com.riuir.calibur.ui.route;

import com.alibaba.android.arouter.exception.HandlerException;
import com.alibaba.android.arouter.launcher.ARouter;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.ui.web.WebViewActivity;

import java.util.ArrayList;

import calibur.foundation.bus.BusinessBus;

public class RouteUtils {

    public static final String userColumnPath = "/user/column";
    public static final String posterDetailPath = "/poster/detail";
    public static final String scoreDetailPath = "/score/detail";
    public static final String imageDetailPath = "/image/detail";
    public static final String bangumiDetailPath = "/bangumi/detail";
    public static final String videoDetailPath = "/video/detail";
    public static final String roleDetailPath = "/idol/detail";
    public static final String commentDetailPath = "/comment/detail";
    public static final String userLoginPath = "/user/login";
    public static final String userForgetPswPath = "/user/forgetPsw";
    public static final String userBookmarkPath = "/user/bookmark";
    public static final String userBulletinPath = "/user/bulletin";
    public static final String userTrasactionRecordPath = "/user/trasaction/record";
    public static final String browserBase = "/browser/base";

    public static void toPage(String path){
        LogUtils.d("routeLog","path = "+path);
        if (path.contains("calibur:/")){
            path = path.replace("calibur:/","");
        }
        String uri;
        String zone = "";
        String idName = "";
        String idName2 = "";
        String commentType = "";
        String baseUrl = "";
        int id = 0;
        int id2 = 0;
        if (path.contains("?")){
            String[] str = path.split("\\?");
            uri = str[0];
            if (str.length>1){
                switch (uri){
                    case userColumnPath:
                        zone = str[1].replace("zone=","");
                        break;
                    case posterDetailPath:
                        idName = "cardID";
                        id = Integer.parseInt(str[1].replace("poster_id=",""));
                        break;
                    case scoreDetailPath:
                        idName = "scoreID";
                        id = Integer.parseInt(str[1].replace("cid=",""));
                        break;
                    case imageDetailPath:
                        idName = "imageID";
                        id = Integer.parseInt(str[1].replace("image_id=",""));
                        break;
                    case bangumiDetailPath:
                        idName = "animeId";
                        id = Integer.parseInt(str[1].replace("bangumi_id=",""));
                        break;
                    case videoDetailPath:
                        idName = "videoId";
                        id = Integer.parseInt(str[1].replace("video_id=",""));
                        break;
                    case roleDetailPath:
                        idName = "videoId";
                        id = Integer.parseInt(str[1].replace("role_id=",""));
                        break;
                    case commentDetailPath:
                        idName = "id";
                        idName2 = "reply_id";
                        String[] params = str[1].split("&");
                        commentType = params[0].replace("type=","");
                        id = Integer.parseInt(params[1].replace("comment_id=",""));
                        id2 = Integer.parseInt(params[2].replace("reply_id=",""));
                        break;
                    case browserBase:
                        baseUrl = str[1].replace("url=","");
                        break;

                }
            }
        }else {
            uri = path;
        }
        //根据zone和commentType判断是进入普通页面还是用户主页还是评论页面

        try {
            if (zone.length()!=0){
                ARouter.getInstance().build(uri)
                        .withInt(idName,id)
                        .withString("zone",zone)
                        .navigation();
            }else if (commentType.length()!=0){
                ARouter.getInstance().build(uri)
                        .withString("type",commentType)
                        .withInt(idName,id)
                        .withInt(idName2,id2)
                        .navigation();
            }else if (baseUrl.length()!=0){
                ARouter.getInstance().build(uri)
                        .withString("type",WebViewActivity.TYPE_BROWSER_BASE)
                        .withString("baseUrl",baseUrl)
                        .navigation();
            }else {
                ARouter.getInstance().build(uri)
                        .withInt(idName,id)
                        .navigation();
            }
        } catch (HandlerException|NullPointerException e) {
            BusinessBus.post(null, "mainModule/postException2Bugly", e);
            e.printStackTrace();
        }

    }

}
