package com.riuir.calibur.ui.home.Drama;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
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
import com.riuir.calibur.ui.widget.BangumiForShowView;
import com.riuir.calibur.utils.ActivityUtils;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.GlideUtils;
import com.shuyu.gsyvideoplayer.GSYBaseActivityDetail;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DramaVideoPlayActivity extends BaseActivity {

    private String url1 = "https://image.calibur.tv/bangumi/sword-art-online/video/720/1.mp4?sign=35dad00521c724b2222b95beb5346044&t=5b73cfa6";

    @BindView(R.id.drama_video_play_recycle_view)
    RecyclerView recyclerView;

    @BindView(R.id.drama_video_play_player)
    StandardGSYVideoPlayer videoPlayer;

    OrientationUtils orientationUtils;



    @BindView(R.id.drama_video_play_other_site_info)
    TextView otherSiteInfo;

    private int videoId;

    AnimeVideosActivityInfo.AnimeVideosActivityInfoData videoData;

    private TrendingShowInfoCommentMain.TrendingShowInfoCommentMainData commentMainData;
    private TrendingShowInfoCommentMain.TrendingShowInfoCommentMainData baseCommentMainData;
    private List<TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList> baseCommentMainList;


    private CommentAdapter commentAdapter;

    private static final int NET_STATUS_PRIMACY = 0;
    private static final int NET_STATUS_MAIN_COMMENT = 1;

    LinearLayout headerLayout;
    BangumiForShowView headerBangumiView;

    int fetchId = 0;
    boolean isLoadMore = false;
    boolean isFirstLoad = false;

    boolean otherSite = false;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_drama_video_play;
    }

    @Override
    protected void onInit() {
        isFirstLoad = true;
        Intent intent = getIntent();
        videoId = intent.getIntExtra("videoId",0);
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
            mApiGet.getCallAnimeVideo(videoId).enqueue(new Callback<AnimeVideosActivityInfo>() {
                @Override
                public void onResponse(Call<AnimeVideosActivityInfo> call, Response<AnimeVideosActivityInfo> response) {
                    if (response!=null&&response.isSuccessful()){
                        videoData = response.body().getData();
//                        initVideo();
                        //两次网络请求都完成后开始加载数据
                        LogUtils.d("ijkPlayer","网络请求完成");
                        setAdapter();
                        setPrimacyView();
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
                    }else {
                        ToastUtils.showShort(DramaVideoPlayActivity.this,"未知错误出现了！");
                    }
                }

                @Override
                public void onFailure(Call<AnimeVideosActivityInfo> call, Throwable t) {
                    ToastUtils.showShort(DramaVideoPlayActivity.this,"请检查您的网络！");
                }
            });
        }
        if (NET_STATUS == NET_STATUS_MAIN_COMMENT){
            setFetchID();
            mApiGet.getCallMainComment("video",videoId,fetchId).enqueue(new Callback<TrendingShowInfoCommentMain>() {
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
                    }else {
                        ToastUtils.showShort(DramaVideoPlayActivity.this,"未知错误出现了！");
                        if (isLoadMore){
                            commentAdapter.loadMoreFail();
                            isLoadMore = false;
                        }
                    }
                }

                @Override
                public void onFailure(Call<TrendingShowInfoCommentMain> call, Throwable t) {
                    ToastUtils.showShort(DramaVideoPlayActivity.this,"请检查您的网络！");

                    if (isLoadMore){
                        commentAdapter.loadMoreFail();
                        isLoadMore = false;
                    }
                }
            });
        }

    }

    private void setPrimacyView() {
        headerLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.drama_video_activity_header_view,null);
        //所属番剧操作
        headerBangumiView = headerLayout.findViewById(R.id.drama_video_activity_header_bangumi_view);
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

        commentAdapter.addHeaderView(headerLayout);
        otherSite = videoData.getInfo().isOther_site();

        initVideo();
        setListener();

    }

    private void initVideo() {
        Map<String,String> videoHeader = new HashMap<>();
        videoHeader.put("Referer","https://android.calibur.tv");
        if (!otherSite){
            videoPlayer.setUp(videoData.getInfo().getSrc(), false,null,videoHeader, videoData.getInfo().getName());
            otherSiteInfo.setVisibility(View.GONE);
        }else {
            otherSiteInfo.setVisibility(View.VISIBLE);
        }
        //增加title
        videoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
        //设置返回键
        videoPlayer.getBackButton().setVisibility(View.VISIBLE);

        //设置旋转
        orientationUtils = new OrientationUtils(this, videoPlayer);
        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
        videoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                    LogUtils.d("videoPlayerAct","推出全屏");
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

        if (!otherSite){
            videoPlayer.startPlayLogic();
        }else {
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setVisibility(View.VISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            recyclerView.setVisibility(View.GONE);
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

    private void setAdapter() {
        commentAdapter = new CommentAdapter(R.layout.card_show_info_list_comment_item,baseCommentMainList,DramaVideoPlayActivity.this,apiPost,CommentAdapter.TYPE_VIDEO);
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

        if (commentMainData.isNoMore()){
            commentAdapter.setEnableLoadMore(false);
        }else {
            //添加底部footer
            commentAdapter.setEnableLoadMore(true);
            commentAdapter.setLoadMoreView(new MyLoadMoreView());
            commentAdapter.disableLoadMoreIfNotFullPage(recyclerView);
        }

        recyclerView.setAdapter(commentAdapter);

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

        super.onDestroy();

        GSYVideoManager.releaseAllVideos();

        if (orientationUtils != null)

            orientationUtils.releaseListener();

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
