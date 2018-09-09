package com.riuir.calibur.utils;

import com.riuir.calibur.data.MineUserInfo;
import com.riuir.calibur.data.anime.BangumiAllList;

import java.util.ArrayList;
import java.util.List;

/**
 * ************************************
 * 预设静态变量存储类
 * ************************************
 */
public class Constants {
    //api总地址（前缀）
    public static final String API_BASE_URL = "https://api.calibur.tv/";

    //api图片地址（前缀）
    public static final String API_IMAGE_BASE_URL = "https://image.calibur.tv/";
    //API版本号
    public static final String API_VERSION = "v1";

    public static String AUTH_TOKEN = "";

    public static String QINIU_TOKEN = "";

    public static boolean ISLOGIN = false;

    public static MineUserInfo.MinEUserInfoData userInfoData = null;

    public static ArrayList<BangumiAllList.BangumiAllListData> bangumiAllListData = null;

}
