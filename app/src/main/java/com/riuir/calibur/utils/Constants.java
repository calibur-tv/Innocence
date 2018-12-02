package com.riuir.calibur.utils;


import com.riuir.calibur.data.params.DramaTags;
import java.util.ArrayList;
import java.util.List;

import calibur.core.http.models.anime.AnimeShowInfo;
import calibur.core.http.models.anime.BangumiAllList;
import calibur.core.http.models.user.MineUserInfo;
import calibur.core.manager.UserSystem;

/**
 * ************************************
 * 预设静态变量存储类
 * ************************************
 */
public class Constants {
    //api总地址（前缀）
    //正式地址 https://api.calibur.tv/
    //测试地址 https://t-api.calibur.tv/
    public static final String API_BASE_URL = "https://api.calibur.tv/";

    //api图片地址（前缀）
    public static final String API_IMAGE_BASE_URL = "https://image.calibur.tv/";
    //API版本号
    public static final String API_VERSION = "v1";

    public static String AUTH_TOKEN = "";

    public static String QINIU_TOKEN = "";

    public static boolean ISLOGIN = false;

    public static MineUserInfo userInfoData = null;

    public static ArrayList<BangumiAllList> bangumiAllListData = null;

    public static List<AnimeShowInfo.AnimeShowInfoTags> allTagsList = null;

}
