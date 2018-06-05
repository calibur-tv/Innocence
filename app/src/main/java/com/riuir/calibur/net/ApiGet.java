package com.riuir.calibur.net;

import com.riuir.calibur.data.AnimeListForTimeLine;
import com.riuir.calibur.data.DramaListResp;
import com.riuir.calibur.data.MainCardInfo;
import com.riuir.calibur.data.ResponseWrapper;
import com.riuir.calibur.utils.Constants;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface ApiGet {

    //获取番剧时间抽
    @GET("bangumi/timeline")
    Call<AnimeListForTimeLine> getCallDramaTimeGet(@Query("year")int year, @Query("take")int take);

    //获取最新帖子
    @GET("post/trending/new")
    Call<MainCardInfo> getCallMainCardNewGet(@Query("page")int page);

    //获取最热帖子
    @GET("post/trending/hot")
    Call<MainCardInfo> getCallMainCardHotGet(@Query("seenIds")String seenIds);

}
