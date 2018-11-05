package com.riuir.calibur.ui.home.Drama;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.assistUtils.activityUtils.LoginUtils;
import com.riuir.calibur.assistUtils.activityUtils.UserMainUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.anime.AnimeVideosActivityInfo;
import com.riuir.calibur.data.trending.TrendingShowInfoCommentMain;
import com.riuir.calibur.net.ApiGet;
import com.riuir.calibur.net.NetService;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.adapter.CommentAdapter;
import com.riuir.calibur.ui.home.adapter.MyLoadMoreView;
import com.riuir.calibur.ui.home.card.CardChildCommentActivity;
import com.riuir.calibur.ui.home.card.CardShowInfoActivity;
import com.riuir.calibur.ui.home.score.ScoreShowInfoActivity;
import com.riuir.calibur.ui.widget.BangumiForShowView;
import com.riuir.calibur.ui.widget.ReplyAndCommentView;
import com.riuir.calibur.ui.widget.TrendingLikeFollowCollectionView;
import com.riuir.calibur.ui.widget.emptyView.AppListEmptyView;
import com.riuir.calibur.ui.widget.emptyView.AppListFailedView;
import com.riuir.calibur.utils.ActivityUtils;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.GlideUtils;
import com.shuyu.gsyvideoplayer.GSYBaseActivityDetail;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DramaVideoPlayActivity extends BaseActivity {


    @BindView(R.id.drama_video_play_recycle_view)
    RecyclerView recyclerView;

    @BindView(R.id.drama_video_play_player)
    StandardGSYVideoPlayer videoPlayer;

    TrendingLikeFollowCollectionView videoLFCView;

    OrientationUtils orientationUtils;

    @BindView(R.id.drama_video_play_comment_view)
    ReplyAndCommentView commentView;

    @BindView(R.id.drama_video_play_other_site_info)
    TextView otherSiteInfo;
    @BindView(R.id.drama_video_play_why_reward_level)
    TextView whyRewardLevel;
    @BindView(R.id.drama_video_play_refresh_layout)
    SwipeRefreshLayout refreshLayout;

    private int videoId;
    private int primacyId;

    AnimeVideosActivityInfo.AnimeVideosActivityInfoData videoData;

    private TrendingShowInfoCommentMain.TrendingShowInfoCommentMainData commentMainData;
    private TrendingShowInfoCommentMain.TrendingShowInfoCommentMainData baseCommentMainData;
    private List<TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList> baseCommentMainList = new ArrayList<>();


    private CommentAdapter commentAdapter;

    private static final int NET_STATUS_PRIMACY = 0;
    private static final int NET_STATUS_MAIN_COMMENT = 1;

    LinearLayout headerLayout;
    BangumiForShowView headerBangumiView;

    int fetchId = 0;
    boolean isLoadMore = false;
    boolean isFirstLoad = false;
    boolean isRefresh = false;

    boolean otherSite = false;
    boolean isRawarded = false;

    private Call<AnimeVideosActivityInfo> primacyCall;
    private Call<TrendingShowInfoCommentMain> commentMainCall;

    AppListFailedView failedView;
    AppListEmptyView emptyView;



    @Override
    protected int getContentViewId() {
        return R.layout.activity_drama_video_play;
    }

    @Override
    protected void onInit() {
        isFirstLoad = true;
        Intent intent = getIntent();
        videoId = intent.getIntExtra("videoId",0);
        //设置旋转
        orientationUtils = new OrientationUtils(this, videoPlayer);
        setAdapter();
        setNet(NET_STATUS_MAIN_COMMENT);
    }


    private void setNet(int NET_STATUS) {
        ApiGet mApiGet;
        if (Constants.ISLOGIN){
            mApiGet = apiGetHasAuth;
        }else {
            mApiGet = apiGet;
        }

        if (NET_STATUS == NET_STATUS_PRIMACY){
            primacyCall = mApiGet.getCallAnimeVideo(videoId);
            primacyCall.enqueue(new Callback<AnimeVideosActivityInfo>() {
                @Override
                public void onResponse(Call<AnimeVideosActivityInfo> call, Response<AnimeVideosActivityInfo> response) {
                    if (response!=null&&response.isSuccessful()){
                        videoData = response.body().getData();
//                        initVideo();
                        //两次网络请求都完成后开始加载数据
                        LogUtils.d("ijkPlayer","网络请求完成");
                        if (recyclerView!=null){
                            setAdapter();
                            setPrimacyView();
                            setEmptyView();
                            isFirstLoad = false;
                            refreshLayout.setEnabled(false);
                        }
                    }else if (response!=null&&response.isSuccessful()==false){
                        String errorStr = "";
                        try {
                            errorStr = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Gson gson = new Gson();
                        Event<String> info =gson.fromJson(errorStr,Event.class);

                        if (info.getCode() == 40104){
                            //用户登录状态过时的时候，清空UserToken,将登录置为false
                            ToastUtils.showShort(DramaVideoPlayActivity.this,info.getMessage());
                            LoginUtils.ReLogin(DramaVideoPlayActivity.this);
                        }else if (info.getCode() == 40401){
                            ToastUtils.showShort(DramaVideoPlayActivity.this,info.getMessage());
                        }
                        if (isRefresh){
                            isRefresh = false;
                            refreshLayout.setRefreshing(false);
                        }
                        setFailedView();
                    }else {
                        if (isRefresh){
                            isRefresh = false;
                            refreshLayout.setRefreshing(false);
                        }
                        ToastUtils.showShort(DramaVideoPlayActivity.this,"未知错误出现了！");
                        setFailedView();
                    }
                }

                @Override
                public void onFailure(Call<AnimeVideosActivityInfo> call, Throwable t) {
                    if (call.isCanceled()){
                    }else {
                        if (isRefresh){
                            isRefresh = false;
                            refreshLayout.setRefreshing(false);
                        }
                        ToastUtils.showShort(DramaVideoPlayActivity.this,"请检查您的网络!");
                        LogUtils.d("videoEEEEE"," t = "+t.getMessage());
                        CrashReport.postCatchedException(t);
                        setFailedView();
                    }
                }
            });
        }
        if (NET_STATUS == NET_STATUS_MAIN_COMMENT){
            setFetchID();
            commentMainCall = mApiGet.getCallMainComment("video",videoId,fetchId,0);
            commentMainCall.enqueue(new Callback<TrendingShowInfoCommentMain>() {
                @Override
                public void onResponse(Call<TrendingShowInfoCommentMain> call, Response<TrendingShowInfoCommentMain> response) {
                    if (response!=null&&response.body()!=null&&response.body().getCode()==0){
                        commentMainData = response.body().getData();
                        if (isFirstLoad){
                            isFirstLoad = false;
                            baseCommentMainData = response.body().getData();
                            baseCommentMainList = response.body().getData().getList();
                            //第一次网络请求结束后 开始第二次网络请求
                            setNet(NET_STATUS_PRIMACY);
                        }
                        if (isLoadMore){
                            setLoadMore();
                        }
                        if (isRefresh){
                            refreshLayout.setRefreshing(false);
                            isRefresh = false;
                            baseCommentMainData = response.body().getData();
                            baseCommentMainList = response.body().getData().getList();
                            //第一次网络请求结束后 开始第二次网络请求
                            setNet(NET_STATUS_PRIMACY);
                        }
                    }else  if (response!=null&&response.isSuccessful()==false){
                        String errorStr = "";
                        try {
                            errorStr = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Gson gson = new Gson();
                        Event<String> info =gson.fromJson(errorStr,Event.class);
                        if (info.getCode() == 40104){
                            ToastUtils.showShort(DramaVideoPlayActivity.this,info.getMessage());
                        }else if (info.getCode() == 40003){
                            ToastUtils.showShort(DramaVideoPlayActivity.this,info.getMessage());
                        }
                        if (isLoadMore){
                            commentAdapter.loadMoreFail();
                            isLoadMore = false;
                        }
                        if (isRefresh){
                            isRefresh = false;
                            refreshLayout.setRefreshing(false);
                        }
                        setFailedView();
                    }else {
                        ToastUtils.showShort(DramaVideoPlayActivity.this,"未知错误出现了！");
                        if (isLoadMore){
                            commentAdapter.loadMoreFail();
                            isLoadMore = false;
                        }
                        if (isRefresh){
                            isRefresh = false;
                            refreshLayout.setRefreshing(false);
                        }
                        setFailedView();
                    }
                }

                @Override
                public void onFailure(Call<TrendingShowInfoCommentMain> call, Throwable t) {
                    if (call.isCanceled()){
                    }else {
                        ToastUtils.showShort(DramaVideoPlayActivity.this,"请检查您的网络！ ");
                        CrashReport.postCatchedException(t);
                        if (isLoadMore){
                            commentAdapter.loadMoreFail();
                            isLoadMore = false;
                        }
                        if (isRefresh){
                            isRefresh = false;
                            refreshLayout.setRefreshing(false);
                        }
                        setFailedView();
                    }
                }
            });
        }

    }

    private void setPrimacyView() {
        headerLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.drama_video_activity_header_view,null);
        //所属番剧操作
        headerBangumiView = headerLayout.findViewById(R.id.drama_video_activity_header_bangumi_view);
        videoLFCView = headerLayout.findViewById(R.id.drama_video_play_trending_LFC);
        LogUtils.d("cardShowHeader","header Data = "+videoData.toString());
        headerBangumiView.setName(videoData.getBangumi().getName());
        headerBangumiView.setSummary(videoData.getBangumi().getSummary());
        headerBangumiView.setImageView(DramaVideoPlayActivity.this,videoData.getBangumi().getAvatar());

        headerBangumiView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DramaVideoPlayActivity.this, DramaActivity.class);
                intent.putExtra("animeId",videoData.getBangumi().getId());
                startActivity(intent);
            }
        });

        initLFCView();
        commentAdapter.addHeaderView(headerLayout);
        otherSite = videoData.getInfo().isOther_site();

        checkVideo();
        setCommentView();
        setListener();
    }

    private void initLFCView() {
        videoLFCView.setType("video");
        videoLFCView.setApiPost(apiPost);
        videoLFCView.setID(videoId);
        videoLFCView.setLiked(videoData.getInfo().isLiked());
        videoLFCView.setCollected(videoData.getInfo().isMarked());
        videoLFCView.setRewarded(videoData.getInfo().isRewarded());
        videoLFCView.setIsCreator(videoData.getInfo().isIs_creator());
        videoLFCView.setOnLFCNetFinish(new TrendingLikeFollowCollectionView.OnLFCNetFinish() {
            @Override
            public void onRewardFinish() {
                videoData.getInfo().setRewarded(true);
                checkVideo();
            }
        });
        videoLFCView.startListenerAndNet();
    }

    private void setCommentView() {
        commentView.setStatus(ReplyAndCommentView.STATUS_MAIN_COMMENT);
        commentView.setApiPost(apiPost);
        commentView.setCommentAdapter(commentAdapter);
        commentView.setFromUserName("");
        commentView.setId(videoId);
        commentView.setType(ReplyAndCommentView.TYPE_VIDEO);
        commentView.setTargetUserId(0);
        commentView.setNetAndListener();
    }

    private void checkVideo(){

        LogUtils.d("videoCheck","Must_reward = "+videoData.isMust_reward()
                +"\nneed_min_level = "+videoData.getNeed_min_level()
                +"\n reward = "+videoData.getInfo().isRewarded()
                +"\n Ip_blocked = "+videoData.isIp_blocked()
                +"\notherSite = "+otherSite);

        initVideo();

        if (Constants.ISLOGIN&&Constants.userInfoData!=null){
            if (!otherSite){
                if (!videoData.isIp_blocked()){
                    //是否必须投食
                    if (videoData.isMust_reward()){
                        if (videoData.getInfo().isRewarded()){
                            initVideoUrl();
                            otherSiteInfo.setVisibility(View.GONE);
                            whyRewardLevel.setVisibility(View.GONE);
                        }else {
                            otherSiteInfo.setVisibility(View.VISIBLE);
                            otherSiteInfo.setText("该视频需要投食之后才能进行观看");
                            whyRewardLevel.setVisibility(View.VISIBLE);
                            whyRewardLevel.setText("为什么要投食");
                            whyRewardLevel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(DramaVideoPlayActivity.this, CardShowInfoActivity.class);
                                    intent.putExtra("cardID",2282);
                                    startActivity(intent);
                                }
                            });

                        }
                    }else {
                        //不是必须投食的判断等级决定能否观看
                        if (Constants.userInfoData.getExp().getLevel()>=videoData.getNeed_min_level()){
                            //符合等级要求(大于等于3级)，直接观看
                            initVideoUrl();
                            otherSiteInfo.setVisibility(View.GONE);
                            whyRewardLevel.setVisibility(View.GONE);
                        }else {
                            //不符合等级要求（小于3级）
                            otherSiteInfo.setVisibility(View.VISIBLE);
                            otherSiteInfo.setText("您的等级小于3级，不能观看该视频");
                            whyRewardLevel.setVisibility(View.VISIBLE);
                            whyRewardLevel.setText("怎么提升等级");
                            whyRewardLevel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(DramaVideoPlayActivity.this, CardShowInfoActivity.class);
                                    intent.putExtra("cardID",2279);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                }else {
                    otherSiteInfo.setVisibility(View.VISIBLE);
                    otherSiteInfo.setText("你已被禁止看视频功能，请加QQ群解禁");
                    whyRewardLevel.setVisibility(View.GONE);
                }
            }else {
                otherSiteInfo.setVisibility(View.VISIBLE);
                otherSiteInfo.setText("因版权等相关问题，该视频无法播放");
                whyRewardLevel.setVisibility(View.GONE);
            }
        }else {
            otherSiteInfo.setVisibility(View.VISIBLE);
            otherSiteInfo.setText("请登录后再尝试观看视频");
            whyRewardLevel.setVisibility(View.GONE);
        }

    }
    private void initVideo() {

        //增加title
        videoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
        //设置返回键
        videoPlayer.getBackButton().setVisibility(View.VISIBLE);

        //隐藏非弹出的底部进度条
        videoPlayer.setBottomProgressBarDrawable(null);


        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
        videoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                    LogUtils.d("videoPlayerAct","退出全屏");
                    recyclerView.setVisibility(View.VISIBLE);
                }else {
                    LogUtils.d("videoPlayerAct","进入全屏");
                    recyclerView.setVisibility(View.GONE);
                }
                orientationUtils.resolveByClick();
            }
        });
        //是否可以滑动调整
        videoPlayer.setIsTouchWiget(true);
        videoPlayer.setSeekRatio(10);
        //设置返回按键功能
        videoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //软硬解码
        //性能不足使用硬解码
        GSYVideoType.enableMediaCodec();
        //硬解码渲染优化
        GSYVideoType.enableMediaCodecTexture();

    }
    private void  initVideoUrl(){
        Map<String,String> videoHeader = new HashMap<>();
        videoHeader.put("Referer","https://android.calibur.tv");

        videoPlayer.setUp(videoData.getInfo().getSrc(), false,null,videoHeader, videoData.getInfo().getName());
        otherSiteInfo.setVisibility(View.GONE);
        //WIFI流量提示
        videoPlayer.startPlayLogic();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setVisibility(View.VISIBLE);
            commentView.setVisibility(View.VISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            recyclerView.setVisibility(View.GONE);
            commentView.setVisibility(View.GONE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    private void setListener() {

        commentAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                List<TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList> dataList =  adapter.getData();

                if (view.getId() == R.id.card_show_info_list_comment_item_user_icon){
                    UserMainUtils.toUserMainActivity(DramaVideoPlayActivity.this,
                            dataList.get(position).getFrom_user_id(),dataList.get(position).getFrom_user_zone());
                }
                if (view.getId() == R.id.card_show_info_list_comment_item_reply){
                    int commentId = dataList.get(position).getId();
                    Intent intent = new Intent(DramaVideoPlayActivity.this,CardChildCommentActivity.class);
                    intent.putExtra("id",commentId);
                    intent.putExtra("mainComment",dataList.get(position));
                    intent.putExtra("type","video");
                    startActivity(intent);
                }
            }
        });

        //上拉加载监听
        commentAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override public void onLoadMoreRequested() {
                if (commentMainData.isNoMore()) {
                    //数据全部加载完毕
                    commentAdapter.loadMoreEnd();
                }else {
                    isLoadMore = true;

                    setNet(NET_STATUS_MAIN_COMMENT);
                }
            }
        }, recyclerView);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                setNet(NET_STATUS_MAIN_COMMENT);
            }
        });
    }

    private void setFetchID() {
        if (isLoadMore){
            fetchId = commentAdapter.getData().get(commentAdapter.getData().size()-1).getId();
        }
        if (isFirstLoad){
            fetchId = 0;
        }

        LogUtils.d("cardShow","fetchID = "+fetchId);
    }

    private void setEmptyView(){
        if (baseCommentMainList==null||baseCommentMainList.size()==0){
            emptyView = new AppListEmptyView(DramaVideoPlayActivity.this);
            emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            commentAdapter.setEmptyView(emptyView);
        }
    }

    private void setFailedView(){
        //加载失败 点击重试
        failedView = new AppListFailedView(DramaVideoPlayActivity.this);
        failedView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        commentAdapter.setEmptyView(failedView);

    }

    private void setLoadMore() {
        isLoadMore = false;
        if (commentMainData.isNoMore()) {
            //最后一次加载
            commentAdapter.addData(commentMainData.getList());
            //数据全部加载完毕
            commentAdapter.loadMoreEnd();
        } else {
            //成功获取更多数据
            LogUtils.d("cardShow","list = "+commentMainData.getList().toString());
            commentAdapter.addData(commentMainData.getList());
            commentAdapter.loadMoreComplete();
        }
    }

    private void setNewData(){

        commentAdapter.setNewData(baseCommentMainList);
    }

    private void setAdapter() {

        if (videoData!=null){
            primacyId = videoData.getInfo().getUser_id();
        }

        commentAdapter = new CommentAdapter(R.layout.card_show_info_list_comment_item,baseCommentMainList,
                DramaVideoPlayActivity.this,apiPost,CommentAdapter.TYPE_VIDEO,primacyId);
        recyclerView.setLayoutManager(new LinearLayoutManager(DramaVideoPlayActivity.this));
        commentAdapter.setHasStableIds(true);
        /**
         * adapter动画效果
         * ALPHAIN 渐显
         *SCALEIN 缩放
         *SLIDEIN_BOTTOM 从下到上
         *SLIDEIN_LEFT 从左到右
         *SLIDEIN_RIGHT 从右到左
         */
        commentAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        //添加底部footer
        commentAdapter.setEnableLoadMore(true);
        commentAdapter.setLoadMoreView(new MyLoadMoreView());
        commentAdapter.disableLoadMoreIfNotFullPage(recyclerView);

        recyclerView.setAdapter(commentAdapter);
        commentAdapter.setHeaderAndEmpty(true);

    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.onVideoPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoPlayer.onVideoResume();
    }

    @Override
    public void onDestroy() {

        if (primacyCall!=null){
            primacyCall.cancel();
        }
        if (commentMainCall!=null){
            commentMainCall.cancel();
        }

        GSYVideoManager.releaseAllVideos();

        if (orientationUtils != null)

            orientationUtils.releaseListener();
        super.onDestroy();
    }

    @Override

    public void onBackPressed() {
        //先返回正常状态
        if (orientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            videoPlayer.getFullscreenButton().performClick();
            recyclerView.setVisibility(View.VISIBLE);
            return;
        }
        //释放所有
        videoPlayer.setVideoAllCallBack(null);
        super.onBackPressed();
    }

    @Override
    protected void handler(Message msg) {
    }
}
