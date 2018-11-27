package calibur.core.http.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import calibur.core.http.models.AppVersionCheckData;
import calibur.core.http.models.anime.AnimeListForTagsSearch;
import calibur.core.http.models.anime.AnimeListForTimeLine;
import calibur.core.http.models.anime.AnimeNewListForWeek;
import calibur.core.http.models.anime.AnimeScoreInfo;
import calibur.core.http.models.anime.AnimeShowInfo;
import calibur.core.http.models.anime.AnimeShowVideosInfo;
import calibur.core.http.models.anime.AnimeVideosActivityInfo;
import calibur.core.http.models.anime.BangumiAllList;
import calibur.core.http.models.anime.RoleFansListInfo;
import calibur.core.http.models.anime.RoleShowInfo;
import calibur.core.http.models.anime.params.BangumiEditParams;
import calibur.core.http.models.comment.ReplyCommentInfo;
import calibur.core.http.models.comment.TrendingChildCommentInfo;
import calibur.core.http.models.comment.TrendingShowInfoCommentItem;
import calibur.core.http.models.create.ChooseImageAlbum;
import calibur.core.http.models.create.CreateCard;
import calibur.core.http.models.create.CreateNewAlbumInfo;
import calibur.core.http.models.create.params.CreateNewAlbum;
import calibur.core.http.models.create.params.CreateNewImageForAlbum;
import calibur.core.http.models.create.params.CreateNewImageSingle;
import calibur.core.http.models.create.params.CreatePostParams;
import calibur.core.http.models.delete.DeleteInfo;
import calibur.core.http.models.followList.BannerLoopInfo;
import calibur.core.http.models.followList.image.ImageShowInfoPrimacy;
import calibur.core.http.models.anime.SearchAnimeInfo;
import calibur.core.http.models.comment.CreateMainCommentInfo;
import calibur.core.http.models.comment.TrendingShowInfoCommentMain;
import calibur.core.http.models.comment.params.CreateMainComment;
import calibur.core.http.models.followList.CartoonListInfo;
import calibur.core.http.models.followList.MainTrendingInfo;
import calibur.core.http.models.followList.params.FolllowListParams;
import calibur.core.http.models.followList.post.CardShowInfoPrimacy;
import calibur.core.http.models.followList.score.ScoreShowInfoPrimacy;
import calibur.core.http.models.geetest.GeeTestInfo;
import calibur.core.http.models.geetest.params.VerificationCodeBody;
import calibur.core.http.models.qiniu.QiniuUpToken;
import calibur.core.http.models.user.MineUserInfo;
import calibur.core.http.models.user.UserDaySign;
import calibur.core.http.models.user.UserFollowedBangumiInfo;
import calibur.core.http.models.user.UserMainInfo;
import calibur.core.http.models.user.UserNotificationInfo;
import calibur.core.http.models.user.UserReplyCardInfo;
import calibur.core.http.models.user.params.UpUserSetting;
import calibur.core.http.models.TemplateModel;
import calibur.core.test.TestModel;
import calibur.core.http.models.base.ResponseBean;
import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/18 12:22 PM
 * version: 1.0
 * description:
 */
public interface APIService {

    //for test
    @POST("flow/list")
    Observable<Response<ResponseBean<TestModel>>> getFollowListTest();

    @GET("app/version/check")
    Observable<Response<ResponseBean<AppVersionCheckData>>> getCallAppVersionCheck(@Query("type") int type, @Query("version") String version);

    @GET("app/template")
    Observable<Response<ResponseBean<TemplateModel>>> checkTemplateUpdate(@Query("page") String page, @Query("version") int version);

    //总列表flowList
    @POST("flow/list")
    Observable<Response<ResponseBean<MainTrendingInfo>>> getFollowList(@Body FolllowListParams folllowListParams);

    //用户未读消息个数
    @GET("user/notification/count")
    Observable<Response<ResponseBean<Integer>>> getUserNotificationCount();

    //搜索接口
    @GET("search/new")
    Observable<Response<ResponseBean<SearchAnimeInfo>>> getCallSearch(@Query("q") String q, @Query("type") String type, @Query("page") int page);

    //获取所有番剧列表
    @GET("search/bangumis")
    Observable<Response<ResponseBean<ArrayList<BangumiAllList>>>> getBangumiAllList();

    //获取番剧时间抽
    @GET("bangumi/timeline")
    Observable<Response<ResponseBean<AnimeListForTimeLine>>> getCallDramaTimeGet(@Query("year") int year, @Query("take") int take);

    //获取番剧周更新列表
    @GET("bangumi/released")
    Observable<Response<ResponseBean<List<List<AnimeNewListForWeek>>>>> getCallDramaNewForWeek();

    //番剧页面置顶帖
    @GET("bangumi/{id}/posts/top")
    Observable<Response<ResponseBean<List<MainTrendingInfo.MainTrendingInfoList>>>> getCallDramaTopPostList(@Path("id") int id);

    //漫画列表
    @GET("bangumi/{bangumi_id}/cartoon")
    Observable<Response<ResponseBean<CartoonListInfo>>> getCartoonList(@Path("bangumi_id") int bangumi_id, @Query("take") int take, @Query("page") int page, @Query("sort") String sort);

    //geetest验证码 api1
    @GET("image/captcha")
    Observable<Response<ResponseBean<GeeTestInfo>>> getCallGeeTestImageCaptcha();

    //获取七牛token
    @GET("image/uptoken")
    Observable<Response<ResponseBean<QiniuUpToken>>> getCallQiniuUpToken();

    //获取各种主评论
    @GET("comment/main/list")
    Observable<Response<ResponseBean<TrendingShowInfoCommentMain>>> getCallMainComment(@Query("type") String type, @Query("id") int id, @Query("fetchId") int fetchId, @Query("onlySeeMaster") int onlySeeMaster);

    //获取动漫详情
    @GET("bangumi/{bangumiId}/show")
    Observable<Response<ResponseBean<AnimeShowInfo>>> getCallAnimeShow(@Path("bangumiId") int bangumiId);

    //获取动漫标签
    @GET("bangumi/tags")
    Observable<Response<ResponseBean<List<AnimeShowInfo.AnimeShowInfoTags>>>> getCallDramaTags();

    //获取动漫视频列表
    @GET("bangumi/{bangumiId}/videos")
    Observable<Response<ResponseBean<AnimeShowVideosInfo>>> getCallAnimeShowVideos(@Path("bangumiId")int bangumiId);

    //获取动漫评分总评
    @GET("score/bangumis")
    Observable<Response<ResponseBean<AnimeScoreInfo>>> getCallAnimeShowAllScore(@Query("id") int id);

    //获取角色详情
    @GET("cartoon_role/{roleId}/show")
    Observable<Response<ResponseBean<RoleShowInfo>>> getCallRoleShowPrimacy(@Path("roleId")int roleId);

    //获取角色应援列表
    @GET("cartoon_role/{roleId}/fans")
    Observable<Response<ResponseBean<RoleFansListInfo>>> getCallRolesFansList(@Path("roleId")int roleId, @Query("sort")String sort, @Query("seenIds")String seenIds );

    //获取帖子标签
    @GET("post/tags")
    Observable<Response<ResponseBean<List<AnimeShowInfo.AnimeShowInfoTags>>>> getCallPostTags();

    //获取用户详情
    @GET("user/{zone}/show")
    Observable<Response<ResponseBean<UserMainInfo>>> getCallUserMainInfo(@Path("zone") String zone);

    //根据标签搜索动漫
    @GET("bangumi/category")
    Observable<Response<ResponseBean<AnimeListForTagsSearch>>> getCallSearchDramaForTags(@Query("id")String id, @Query("page")String page);

    //获取动漫视频资源
    @GET("video/{videoId}/show")
    Observable<Response<ResponseBean<AnimeVideosActivityInfo>>> getCallAnimeVideo(@Path("videoId")int videoId);

    //获取帖子详情
    @GET("post/{id}/show")
    Observable<Response<ResponseBean<CardShowInfoPrimacy>>> getCallCardShowPrimacy(@Path("id")int id);

    //获取图片详情
    @GET("image/{id}/show")
    Observable<Response<ResponseBean<ImageShowInfoPrimacy>>> getCallImageShowPrimacy(@Path("id")int id);

    //获取漫评详情
    @GET("score/{id}/show")
    Observable<Response<ResponseBean<ScoreShowInfoPrimacy>>> getCallScoreShowPrimacy(@Path("id")int id);

    //消息列表页跳转评论
    @GET("comment/main/item")
    Observable<Response<ResponseBean<TrendingShowInfoCommentItem>>> getCallMainItemComment(@Query("comment_id")int comment_id, @Query("reply_id")int reply_id, @Query("type")String type);

    //获取各种子评论
    @GET("comment/sub/list")
    Observable<Response<ResponseBean<TrendingChildCommentInfo>>> getCallChildComment(@Query("type")String type, @Query("id")int commentId, @Query("maxId")int maxId);

    //获取用户追番
    @GET("user/{zone}/followed/bangumi")
    Observable<Response<ResponseBean<List<UserFollowedBangumiInfo>>>> getCallUserFollowedBangumi(@Path("zone")String zone);

    //获取用户回复帖
    @GET("user/{zone}/posts/reply")
    Observable<Response<ResponseBean<UserReplyCardInfo>>> getCallUserReplyCard(@Path("zone") String zone, @Query("page")int page);

    //消息列表
    @GET("user/notification/list")
    Observable<Response<ResponseBean<UserNotificationInfo>>> getCallUserNotification(@Query("minId")int minId);

    //获取用户相册列表
    @GET("image/album/users")
    Observable<Response<ResponseBean<List<ChooseImageAlbum>>>> getUserAlbumList();

    @GET("cm/loop/list")
    Observable<Response<ResponseBean<List<BannerLoopInfo>>>> getCallBannerLoop();


    //登录接口
    @POST("door/login")
    Observable<Response<ResponseBean<String>>> getCallLogin(@Body Map<String, Object> argsMa);

    //发送验证码接口
    @POST("door/message")
    Observable<Response<ResponseBean<String>>> getGeeTestSendValidate(@Body VerificationCodeBody verificationCodeBody);

    //注册接口
    @POST("door/register")
    Observable<Response<ResponseBean<String>>> getCallRegister(@Body Map<String, Object> argsMa);

    //重置密码接口
    @POST("door/reset")
    Observable<Response<ResponseBean<String>>> getCallReSetPassWord(@Body Map<String, Object> argsMa);

    //获取用户信息
    @POST("door/refresh")
    Observable<Response<ResponseBean<MineUserInfo>>> getMineUserInfo();

    //新建主评论
    @POST("comment/main/create")
    Observable<Response<ResponseBean<CreateMainCommentInfo>>> getCreateMainComment(@Body CreateMainComment createMainComment);

    //新建子评论
    @POST("comment/main/reply")
    Observable<Response<ResponseBean<ReplyCommentInfo>>> getReplyComment(@Query("content")String content, @Query("type")String type,
                                                                         @Query("id")int id, @Query("targetUserId")int targetUserId);

    //举报接口
    @POST("report/send")
    Observable<Response<ResponseBean<String>>> getCallReportSend(@Query("id")int id, @Query("model")String model,
                                          @Query("type")int type, @Query("message")String message);
    //反馈接口
    @POST("user/feedback")
    Observable<Response<ResponseBean<String>>> getCallUserFeedback(@Query("type")int type,@Query("desc")String desc,@Query("ua")String ua);

    //用户登出
    @POST("door/logout")
    Observable<Response<ResponseBean<String>>> getMineUserLogOut();

    //签到
    @POST("user/daySign")
    Observable<Response<ResponseBean<UserDaySign>>> getCallUserDaySign();

    //发送关注番剧
    @POST("toggle/follow")
    Observable<Response<ResponseBean<Boolean>>> getCallBangumiToggleFollow(@Query("type")String type, @Query("id")int id);

    //发送给帖子点赞
    @POST("toggle/like")
    Observable<Response<ResponseBean<Boolean>>> getTrendingToggleLike(@Query("type")String type,@Query("id")int postId);

    //发送给帖子投食
    @POST("toggle/reward")
    Observable<Response<ResponseBean<Boolean>>> getTrendingToggleReward(@Query("type")String type,@Query("id")int postId);

    //发送收藏帖子
    @POST("toggle/mark")
    Observable<Response<ResponseBean<Boolean>>> getTrendingToggleCollection(@Query("type")String type,@Query("id")int postId);

    //给角色应援
    @POST("cartoon_role/{roleId}/star")
    Observable<Response<ResponseBean<String>>> getCallRolesStar(@Path("roleId")int roleId);

    //发送给主评论点赞
    @POST("comment/main/toggleLike")
    Observable<Response<ResponseBean<Boolean>>> getCardCommentToggleLike(@Query("id") int id,@Query("type")String type);

    //上传个人信息设置
    @POST("user/setting/profile")
    Observable<Response<ResponseBean<String>>> getCallUPLoadUserSetting(@Body UpUserSetting upUserSetting);

    //上传头像或者banner
    @POST("user/setting/image")
    Observable<Response<ResponseBean<String>>> getCallUpLoadUserAvatarAndBanner(@Query("type")String type,@Query("url")String url);

    //删除帖子 相册 漫评
    @POST("post/{postId}/deletePost")
    Observable<Response<ResponseBean<DeleteInfo>>> getCallDeletePost(@Path("postId")int postId);
    @POST("image/album/delete")
    Observable<Response<ResponseBean<DeleteInfo>>> getCallDeleteAlbum(@Query("id") int id);
    @POST("score/delete")
    Observable<Response<ResponseBean<DeleteInfo>>> getCallDeleteScore(@Query("id")int id);
    //删除主评论
    @POST("comment/main/delete")
    Observable<Response<ResponseBean<DeleteInfo>>> getCallDeleteCommentMain(@Query("type")String type,@Query("id")int id);
    //删除子评论
    @POST("comment/sub/delete")
    Observable<Response<ResponseBean<DeleteInfo>>> getCallDeleteCommentChild(@Query("type")String type,@Query("id")int id);

    //帖子加精、置顶 和取消
    @POST("post/manager/nice/set")
    Observable<Response<ResponseBean<String>>> getCallPostNiceSet(@Query("id")String id);
    @POST("post/manager/nice/remove")
    Observable<Response<ResponseBean<String>>> getCallPostNiceRemove(@Query("id")String id);
    @POST("post/manager/top/set")
    Observable<Response<ResponseBean<String>>> getCallPostTopSet(@Query("id")String id);
    @POST("post/manager/top/remove")
    Observable<Response<ResponseBean<String>>> getCallPostTopRemove(@Query("id")String id);

    //创建偶像
    @POST("cartoon_role/manager/create")
    Observable<Response<ResponseBean<Integer>>> getCallManagerCreateRole(@Query("bangumi_id")int bangumi_id,@Query("name")String name,
                                                  @Query("alias")String alias,@Query("intro")String intro,@Query("avatar")String avatar);

    //编辑番剧内容
    @POST("bangumi/{bangumiId}/edit")
    Observable<Response<ResponseBean<String>>> getCallEditBangumi(@Path("bangumiId")int bangumi_id, @Body BangumiEditParams params);

    //读取某条消息
    @POST("user/notification/read")
    Observable<Response<ResponseBean<String>>> getCallReadNotification(@Query("id")int id);
    //全部设为已读
    @POST("user/notification/clear")
    Observable<Response<ResponseBean<String>>> getCallAllReadNotification();

    //新建帖子
    @POST("post/create")
    Observable<Response<ResponseBean<CreateCard>>> getCreatPost(@Body CreatePostParams createPostParams);
    //新建单张图
    @POST("image/single/upload")
    Observable<Response<ResponseBean<CreateCard>>> getCreateImageSingle(@Body CreateNewImageSingle createNewImageSingle);
    //新建相册传图
    @POST("image/album/upload")
    Observable<Response<ResponseBean<String>>> getCreateImageForAlbum(@Body CreateNewImageForAlbum createNewImageForAlbum);
    //新建相册
    @POST("image/album/create")
    Observable<Response<ResponseBean<CreateNewAlbumInfo>>> getCreateIAlbum(@Body CreateNewAlbum createNewAlbum);
}
