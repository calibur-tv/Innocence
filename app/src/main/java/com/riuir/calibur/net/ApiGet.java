package com.riuir.calibur.net;

import com.riuir.calibur.data.AnimeListForTagsSearch;
import com.riuir.calibur.data.AnimeListForTimeLine;
import com.riuir.calibur.data.AnimeNewListForWeek;
import com.riuir.calibur.data.AnimeShowInfo;
import com.riuir.calibur.data.AnimeShowVideosInfo;
import com.riuir.calibur.data.DramaListResp;
import com.riuir.calibur.data.GeeTestInfo;
import com.riuir.calibur.data.MainCardInfo;
import com.riuir.calibur.data.ResponseWrapper;
import com.riuir.calibur.data.card.CardChildCommentInfo;
import com.riuir.calibur.data.card.CardShowInfoCommentMain;
import com.riuir.calibur.data.card.CardShowInfoPrimacy;
import com.riuir.calibur.data.params.DramaTags;
import com.riuir.calibur.utils.Constants;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiGet {

    //获取番剧时间抽
    @GET("bangumi/timeline")
    Call<AnimeListForTimeLine> getCallDramaTimeGet(@Query("year")int year, @Query("take")int take);

    //获取番剧时间抽
    @GET("bangumi/released")
    Call<AnimeNewListForWeek> getCallDramaNewForWeek();

    //获取最新帖子
    @GET("post/trending/news")
    Call<MainCardInfo> getCallMainCardNewGet(@Query("minId")int minId);

    //获取最热帖子
    @GET("post/trending/hot")
    Call<MainCardInfo> getCallMainCardHotGet(@Query("seenIds")String seenIds);

    //获取最热帖子
    @GET("post/trending/active")
    Call<MainCardInfo> getCallMainCardActiveGet(@Query("seenIds")String seenIds);

    //geetest api1
    @GET("image/captcha")
    Call<GeeTestInfo> getCallGeeTestImageCaptcha();

    //获取动漫标签
    @GET("bangumi/tags")
    Call<DramaTags> getCallDramaTags();
    //获取动漫标签
    @GET("bangumi/category")
    Call<AnimeListForTagsSearch> getCallSearchDramaForTags(@Query("id")String id, @Query("page")String page);

    //获取动漫详情
    @GET("bangumi/{bangumiId}/show")
    Call<AnimeShowInfo> getCallAnimeShow(@Path("bangumiId")int bangumiId);

    //获取动漫视频列表
    @GET("bangumi/{bangumiId}/videos")
    Call<AnimeShowVideosInfo> getCallAnimeShowVideos(@Path("bangumiId")int bangumiId);

    //获取帖子详情
    @GET("post/{postId}/show")
    Call<CardShowInfoPrimacy> getCallCardShowPrimacy(@Path("postId")int postId);

    //获取各种主评论
    @GET("{type}/comment/{type_id}/main/list")
    Call<CardShowInfoCommentMain> getCallMainComment(@Path("type")String type, @Path("type_id")int type_id, @Query("fetchId")int fetchId);

    //获取各种子评论
    @GET("{type}/comment/{commentId}/sub/list")
    Call<CardChildCommentInfo> getCallChildComment(@Path("type")String type, @Path("commentId")int commentId, @Query("maxId")int maxId);
}
