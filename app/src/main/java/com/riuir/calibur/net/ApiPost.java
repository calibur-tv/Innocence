package com.riuir.calibur.net;

import com.riuir.calibur.data.DramaListResp;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.card.CardToggleInfo;
import com.riuir.calibur.data.params.VerificationCodeBody;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
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

    //注册接口
    @POST("door/reset")
    Call<Event<String>> getCallReSetPassWord(@Body Map<String, Object> argsMa);

    //发送验证码接口
    @POST("door/message")
    Call<Event<String>> getGeeTestSendValidate(@Body VerificationCodeBody verificationCodeBody);

    //发送给帖子点赞
    @POST("post/{postId}/toggleLike")
    Call<CardToggleInfo> getCardToggleLike(@Path("postId")int postId);

    //发送收藏帖子
    @POST("post/{postId}/toggleMark")
    Call<CardToggleInfo> getCardToggleCollection(@Path("postId")int postId);

    //发送给帖子主评论点赞
    @POST("post/comment/main/toggleLike/{commentId}")
    Call<Event<String>> getCardCommentToggleLike(@Path("commentId")int commentId);
}
