package com.riuir.calibur.net;

import com.riuir.calibur.data.DramaListResp;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.MineUserInfo;
import com.riuir.calibur.data.album.ChooseImageAlbum;
import com.riuir.calibur.data.album.CreateNewAlbumInfo;
import com.riuir.calibur.data.anime.AnimeFollowInfo;
import com.riuir.calibur.data.params.CreateMainComment;
import com.riuir.calibur.data.params.newImage.CreateNewAlbum;
import com.riuir.calibur.data.params.newImage.CreateNewImageForAlbum;
import com.riuir.calibur.data.params.newImage.CreateNewImageSingle;
import com.riuir.calibur.data.params.newPost.CreatePostParams;
import com.riuir.calibur.data.params.UpUserSetting;
import com.riuir.calibur.data.trending.CreateMainCommentInfo;
import com.riuir.calibur.data.trending.ReplyCommentInfo;
import com.riuir.calibur.data.trending.TrendingToggleInfo;
import com.riuir.calibur.data.params.VerificationCodeBody;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiPost {


    @POST("drama/list")
    Observable<DramaListResp> getObservable(@Body Map<String, Object> argsMap);

    @POST("drama/list")
    Call<DramaListResp> getCall(@Body Map<String, Object> argsMap);

    //注册接口
    @POST("door/register")
    Call<Event<String>> getCallRegister(@Body Map<String, Object> argsMa);

    //登录接口
    @POST("door/login")
    Call<Event<String>> getCallLogin(@Body Map<String, Object> argsMa);

    //重置密码接口
    @POST("door/reset")
    Call<Event<String>> getCallReSetPassWord(@Body Map<String, Object> argsMa);

    //发送验证码接口
    @POST("door/message")
    Call<Event<String>> getGeeTestSendValidate(@Body VerificationCodeBody verificationCodeBody);

    //发送验证码接口
    @POST("door/message")
    Call<Event<String>> getGeeTestSendValidateNoGee(@Query("type") String type,@Query("phone_number")String phone_number);

    //举报接口
    @POST("report/send")
    Call<Event<String>> getCallReportSend(@Query("id")int id,@Query("model")String model,
                                          @Query("type")int type,@Query("message")String message);
    //反馈接口
    @POST("user/feedback")
    Call<Event<String>> getCallUserFeedback(@Query("type")int type,@Query("desc")String desc,@Query("ua")String ua);

    @POST("door/refresh")
    Call<MineUserInfo> getMineUserInfo();

    @POST("door/logout")
    Call<Event<String>> getMineUserLogOut();

    @POST("user/daySign")
    Call<Event<String>> getCallUserDaySign();

    //发送关注番剧
    @POST("toggle/follow")
    Call<AnimeFollowInfo> getCallBangumiToggleFollow(@Query("type")String type, @Query("id")int id);
    //发送给帖子点赞
    @POST("toggle/like")
    Call<TrendingToggleInfo> getTrendingToggleLike(@Query("type")String type,@Query("id")int postId);

    //发送给帖子投食
    @POST("toggle/reward")
    Call<TrendingToggleInfo> getTrendingToggleReward(@Query("type")String type,@Query("id")int postId);

    //给角色应援
    @POST("cartoon_role/{roleId}/star")
    Call<TrendingToggleInfo> getCallRolesStar(@Path("roleId")int roleId);

    //发送收藏帖子
    @POST("toggle/mark")
    Call<TrendingToggleInfo> getTrendingToggleCollection(@Query("type")String type,@Query("id")int postId);

    //发送给主评论点赞
    @POST("comment/main/toggleLike")
    Call<TrendingToggleInfo> getCardCommentToggleLike(@Query("id") int id,@Query("type")String type);

    //新建主评论
//    @POST("comment/main/create")
//    Call<CreateMainCommentInfo> getCreateMainComment(@Query("content")String content, @Query("type")String type,
//                                                     @Query("id")int id, @Query("images")ArrayList<CreateMainComment> images);
    @POST("comment/main/create")
    Call<CreateMainCommentInfo> getCreateMainComment(@Body CreateMainComment createMainComment);

    //新建子评论
    @POST("comment/main/reply")
    Call<ReplyCommentInfo> getReplyComment(@Query("content")String content, @Query("type")String type,
                                           @Query("id")int id,@Query("targetUserId")int targetUserId);


    //新建帖子
    @POST("post/create")
    Call<Event<Integer>> getCreatPost(@Body CreatePostParams createPostParams);
    //新建单张图
    @POST("image/single/upload")
    Call<Event<Integer>> getCreateImageSingle(@Body CreateNewImageSingle createNewImageSingle);
    //新建相册传图
    @POST("image/album/upload")
    Call<Event<Integer>> getCreateImageForAlbum(@Body CreateNewImageForAlbum createNewImageForAlbum);
    //新建相册
    @POST("image/album/create")
    Call<CreateNewAlbumInfo> getCreateIAlbum(@Body CreateNewAlbum createNewAlbum);

    //回复帖子评论
    @POST("post/comment/{commentId}/reply")
    Call<Event<String>> geCallReplyChildComment(@Path("commentId")int commentId,@Query("targetUserId")int targetUserId,@Query("content")String content);

    //上传个人信息设置
    @POST("user/setting/profile")
    Call<Event<String>> getCallUPLoadUserSetting(@Body UpUserSetting upUserSetting);

    //上传头像或者banner
    @POST("user/setting/image")
    Call<Event<String>> getCallUpLoadUserAvatarAndBanner(@Query("type")String type,@Query("url")String url);

    //删除帖子 相册 漫评
    @POST("post/{postId}/deletePost")
    Call<Event<String>> getCallDeletePost(@Path("postId")int postId);
    @POST("image/album/delete")
    Call<Event<String>> getCallDeleteAlbum(@Query("id") int id);
    @POST("score/delete")
    Call<Event<String>> getCallDeleteScore(@Query("id")int id);

}
