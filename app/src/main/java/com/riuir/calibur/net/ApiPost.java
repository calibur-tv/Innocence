package com.riuir.calibur.net;

import com.riuir.calibur.data.DramaListResp;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.MineUserInfo;
import com.riuir.calibur.data.role.RoleFansListInfo;
import com.riuir.calibur.data.trending.TrendingToggleInfo;
import com.riuir.calibur.data.params.VerificationCodeBody;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
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

    @POST("door/user")
    Call<MineUserInfo> getMineUserInfo();

    @POST("door/logout")
    Call<Event<String>> getMineUserLogOut();

    //发送给帖子点赞
    @POST("toggle/like")
    Call<TrendingToggleInfo> getTrendingToggleLike(@Query("type")String type,@Query("id")int postId);

    //发送给帖子打赏
    @POST("toggle/reward")
    Call<TrendingToggleInfo> getTrendingToggleReward(@Query("type")String type,@Query("id")int postId);

    //给角色应援
    @POST("cartoon_role/{roleId}/star")
    Call<TrendingToggleInfo> getCallRolesStar(@Path("roleId")int roleId);

    //发送收藏帖子
    @POST("toggle/mark")
    Call<TrendingToggleInfo> getTrendingToggleCollection(@Query("type")String type,@Query("id")int postId);

    //发送给帖子主评论点赞
    @POST("post/comment/main/toggleLike/{commentId}")
    Call<Event<String>> getCardCommentToggleLike(@Path("commentId")int commentId);

    //回复帖子评论
    @POST("post/comment/{commentId}/reply")
    Call<Event<String>> geCalltReplyChildComment(@Path("commentId")int commentId,@Query("targetUserId")int targetUserId,@Query("content")String content);
}
