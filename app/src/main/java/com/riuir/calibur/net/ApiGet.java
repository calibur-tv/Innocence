package com.riuir.calibur.net;

import com.riuir.calibur.data.AnimeListForRole;
import com.riuir.calibur.data.AnimeListForTagsSearch;
import com.riuir.calibur.data.AnimeListForTimeLine;
import com.riuir.calibur.data.AnimeNewListForWeek;
import com.riuir.calibur.data.AnimeShowInfo;
import com.riuir.calibur.data.AnimeShowVideosInfo;
import com.riuir.calibur.data.GeeTestInfo;
import com.riuir.calibur.data.MainCardInfo;
import com.riuir.calibur.data.MainTrendingInfo;
import com.riuir.calibur.data.role.RoleFansListInfo;
import com.riuir.calibur.data.role.RoleShowInfo;
import com.riuir.calibur.data.trending.ImageShowInfoPrimacy;
import com.riuir.calibur.data.trending.ScoreShowInfoPrimacy;
import com.riuir.calibur.data.trending.TrendingChildCommentInfo;
import com.riuir.calibur.data.trending.TrendingShowInfoCommentMain;
import com.riuir.calibur.data.trending.CardShowInfoPrimacy;
import com.riuir.calibur.data.params.DramaTags;
import com.riuir.calibur.data.user.UserFollowedBangumiInfo;
import com.riuir.calibur.data.user.UserFollowedRoleInfo;
import com.riuir.calibur.data.user.UserMainInfo;
import com.riuir.calibur.data.user.UserReplyCardInfo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiGet {



    //获取最新帖子
    @GET("post/trending/news")
    Call<MainCardInfo> getCallMainCardNewGet(@Query("minId")int minId);

    //获取最热帖子
    @GET("post/trending/hot")
    Call<MainTrendingInfo> getCallMainCardHotGet(@Query("seenIds")String seenIds);

    //获取最活跃
    @GET("post/trending/active")
    Call<MainCardInfo> getCallMainCardActiveGet(@Query("seenIds")String seenIds);

    //获取trending 首页帖子/图片/漫评
    @GET("trending/active")
    Call<MainTrendingInfo> getCallTrendingActiveGet(@Query("type")String type, @Query("seenIds")String seenIds, @Query("bangumiId")int bangumiId);

    //获取trending 首页帖子/图片/漫评
    @GET("trending/hot")
    Call<MainTrendingInfo> getCallTrendingHotGet(@Query("type")String type, @Query("seenIds")String seenIds, @Query("bangumiId")int bangumiId);

    //geetest验证码 api1
    @GET("image/captcha")
    Call<GeeTestInfo> getCallGeeTestImageCaptcha();


    //获取番剧时间抽
    @GET("bangumi/timeline")
    Call<AnimeListForTimeLine> getCallDramaTimeGet(@Query("year")int year, @Query("take")int take);

    //获取番剧更新列表
    @GET("bangumi/released")
    Call<AnimeNewListForWeek> getCallDramaNewForWeek();

    //获取动漫标签
    @GET("bangumi/tags")
    Call<DramaTags> getCallDramaTags();

    //获取动漫标签
    @GET("bangumi/category")
    Call<AnimeListForTagsSearch> getCallSearchDramaForTags(@Query("id")String id, @Query("page")String page);

    //偶像排行接口
    @GET("trending/cartoon_role")
    Call<AnimeListForRole> getCallAnimeRoles();

    //获取动漫详情
    @GET("bangumi/{bangumiId}/show")
    Call<AnimeShowInfo> getCallAnimeShow(@Path("bangumiId")int bangumiId);

    //获取动漫视频列表
    @GET("bangumi/{bangumiId}/videos")
    Call<AnimeShowVideosInfo> getCallAnimeShowVideos(@Path("bangumiId")int bangumiId);

    //获取帖子详情
    @GET("post/{id}/show")
    Call<CardShowInfoPrimacy> getCallCardShowPrimacy(@Path("id")int id);

    //获取图片详情
    @GET("image/{id}/show")
    Call<ImageShowInfoPrimacy> getCallImageShowPrimacy(@Path("id")int id);

    //获取评分详情
    @GET("score/{id}/show")
    Call<ScoreShowInfoPrimacy> getCallScoreShowPrimacy(@Path("id")int id);

    //获取角色详情
    @GET("cartoon_role/{roleId}/show")
    Call<RoleShowInfo> getCallRoleShowPrimacy(@Path("roleId")int roleId);

    //获取角色应援列表
    @GET("cartoon_role/{roleId}/fans")
    Call<RoleFansListInfo> getCallRolesFansList(@Path("roleId")int roleId,@Query("sort")String sort,@Query("seenIds")int seenIds );

    //获取各种主评论
    @GET("comment/main/list")
    Call<TrendingShowInfoCommentMain> getCallMainComment(@Query("type")String type, @Query("id")int id, @Query("fetchId")int fetchId);

    //获取各种子评论
    @GET("comment/sub/list")
    Call<TrendingChildCommentInfo> getCallChildComment(@Query("type")String type, @Query("id")int commentId, @Query("maxId")int maxId);

    //获取用户详情
    @GET("user/{zone}/show")
    Call<UserMainInfo> getCallUserMainInfo(@Path("zone")String zone);

    //获取用户追番
    @GET("user/{zone}/followed/bangumi")
    Call<UserFollowedBangumiInfo> getCallUserFollowedBangumi(@Path("zone")String zone);

    //获取用户应援角色
    @GET("user/{zone}/followed/role")
    Call<UserFollowedRoleInfo> getCallUserFollowedRole(@Path("zone")String zone);

    //获取用户相册
    @GET("image/users")
    Call<MainTrendingInfo> getCallUserFollowedImage(@Query("zone") String zone,@Query("page")int page);

    //获取用户主题帖
    @GET("user/{zone}/posts/mine")
    Call<MainTrendingInfo> getCallUserReleseCard(@Path("zone") String zone,@Query("page")int page);

    //获取用户回复帖
    @GET("user/{zone}/posts/reply")
    Call<UserReplyCardInfo> getCallUserReplyCard(@Path("zone") String zone, @Query("page")int page);
}