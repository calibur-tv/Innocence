package com.riuir.calibur.ui.home.Drama;

import android.app.ActivityOptions;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.assistUtils.activityUtils.LoginUtils;
import com.riuir.calibur.assistUtils.activityUtils.UserMainUtils;
import com.riuir.calibur.data.Event;

import com.riuir.calibur.net.ApiGet;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.MineFragment;
import com.riuir.calibur.ui.home.adapter.CommentAdapter;
import com.riuir.calibur.ui.home.adapter.MyLoadMoreView;
import com.riuir.calibur.ui.home.card.CardChildCommentActivity;
import com.riuir.calibur.ui.home.card.PostDetailActivity;
import com.riuir.calibur.ui.route.RouteUtils;
import com.riuir.calibur.ui.widget.BangumiForShowView;
import com.riuir.calibur.ui.widget.replyAndComment.ReplyAndCommentView;
import com.riuir.calibur.ui.widget.TrendingLikeFollowCollectionView;
import com.riuir.calibur.ui.widget.emptyView.AppListEmptyView;
import com.riuir.calibur.ui.widget.emptyView.AppListFailedView;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.DialogHelper;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
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
import calibur.core.http.models.anime.AnimeVideosActivityInfo;
import calibur.core.http.models.base.ResponseBean;
import calibur.core.http.models.comment.TrendingShowInfoCommentMain;
import calibur.core.http.observer.ObserverWrapper;
import calibur.core.manager.UserSystem;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Route(path = RouteUtils.videoDetailPath)
public class DramaVideoPlayActivity extends BaseActivity {


    @BindView(R.id.drama_video_play_recycle_view)
    RecyclerView recyclerView;

    @BindView(R.id.drama_video_play_player)
    StandardGSYVideoPlayer videoPlayer;

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

    AnimeVideosActivityInfo videoData;

    private TrendingShowInfoCommentMain commentMainData;
    private TrendingShowInfoCommentMain baseCommentMainData;
    private List<TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList> baseCommentMainList = new ArrayList<>();


    private CommentAdapter commentAdapter;

    private static final int NET_STATUS_PRIMACY = 0;
    private static final int NET_STATUS_MAIN_COMMENT = 1;

    LinearLayout headerLayout;
    LinearLayout baiduCloudLayout;
    TextView baiduCloudSrc;
    TextView baiduCloudPsd;
    BangumiForShowView headerBangumiView;
    TrendingLikeFollowCollectionView videoLFCView;
    TextView buyBangumiBtn;
    AlertDialog buyBangumiDialog;

    int fetchId = 0;
    boolean isLoadMore = false;
    boolean isFirstLoad = false;
    boolean isRefresh = false;

    boolean otherSite = false;
    boolean isRawarded = false;


    AppListFailedView failedView;
    AppListEmptyView emptyView;

    private static DramaVideoPlayActivity instance;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_drama_video_play;
    }

    @Override
    protected void onInit() {
        instance = this;
        isFirstLoad = true;
        Intent intent = getIntent();
        videoId = intent.getIntExtra("videoId",0);
        //设置旋转
        orientationUtils = new OrientationUtils(this, videoPlayer);
        setAdapter();
        setNet(NET_STATUS_MAIN_COMMENT);
    }


    private void setNet(int NET_STATUS) {

        if (NET_STATUS == NET_STATUS_PRIMACY){
            apiService.getCallAnimeVideo(videoId)
                    .compose(Rx2Schedulers.applyObservableAsync())
                    .subscribe(new ObserverWrapper<AnimeVideosActivityInfo>(){
                        @Override
                        public void onSuccess(AnimeVideosActivityInfo animeVideosActivityInfo) {
                            videoData = animeVideosActivityInfo;
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
                        }

                        @Override
                        public void onFailure(int code, String errorMsg) {
                            super.onFailure(code, errorMsg);
                            if (refreshLayout!=null){
                                if (isRefresh){
                                    isRefresh = false;
                                    refreshLayout.setRefreshing(false);
                                }
                                setFailedView();
                            }
                        }
                    });
        }
        if (NET_STATUS == NET_STATUS_MAIN_COMMENT){
            setFetchID();
            apiService.getCallMainComment("video",videoId,fetchId,0)
                    .compose(Rx2Schedulers.<Response<ResponseBean<TrendingShowInfoCommentMain>>>applyObservableAsync())
                    .subscribe(new ObserverWrapper<TrendingShowInfoCommentMain>() {
                        @Override
                        public void onSuccess(TrendingShowInfoCommentMain trendingShowInfoCommentMain) {
                            commentMainData = trendingShowInfoCommentMain;
                            if (isFirstLoad){
                                isFirstLoad = false;
                                baseCommentMainData = trendingShowInfoCommentMain;
                                baseCommentMainList = trendingShowInfoCommentMain.getList();
                                //第一次网络请求结束后 开始第二次网络请求
                                setNet(NET_STATUS_PRIMACY);
                            }
                            if (isLoadMore){
                                setLoadMore();
                            }
                            if (isRefresh){
                                refreshLayout.setRefreshing(false);
                                isRefresh = false;
                                baseCommentMainData = trendingShowInfoCommentMain;
                                baseCommentMainList = trendingShowInfoCommentMain.getList();
                                //第一次网络请求结束后 开始第二次网络请求
                                setNet(NET_STATUS_PRIMACY);
                            }
                        }

                        @Override
                        public void onFailure(int code, String errorMsg) {
                            super.onFailure(code, errorMsg);
                            if (refreshLayout!=null){
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
        baiduCloudLayout = headerLayout.findViewById(R.id.drama_video_play_baidu_cloud_layout);
        baiduCloudSrc = headerLayout.findViewById(R.id.drama_video_play_baidu_cloud_src);
        baiduCloudPsd = headerLayout.findViewById(R.id.drama_video_play_baidu_cloud_psd);
        videoLFCView = headerLayout.findViewById(R.id.drama_video_play_trending_LFC);
        buyBangumiBtn = headerLayout.findViewById(R.id.drama_video_activity_header_buy_btn);
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

        if (videoData.getInfo().isIs_baidu_cloud()){
            baiduCloudLayout.setVisibility(View.VISIBLE);
            baiduCloudSrc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = videoData.getInfo().getSrc();
                    // 获取系统剪贴板
                    final ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    final ClipData clipUrl = ClipData.newPlainText("calibur", url);
                    //把数据集设置（复制）到剪贴板
                    clipboard.setPrimaryClip(clipUrl);
                    ToastUtils.showLong(DramaVideoPlayActivity.this,"内容链接已复制到粘贴板");
                }
            });
            baiduCloudPsd.setText("密码："+videoData.getInfo().getBaidu_cloud_pwd());
        }

        if (videoData.isBuyed()){
            buyBangumiBtn.setText("已承包");
        }else {
            buyBangumiBtn.setText("我要承包");
        }
        setBuyDialog();
        buyBangumiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoData.isBuyed()){
                    ToastUtils.showShort(DramaVideoPlayActivity.this,"您已承包该季番剧~");
                }else {
                    buyBangumiDialog.show();
                }
            }
        });

        initLFCView();
        commentAdapter.addHeaderView(headerLayout);
        otherSite = videoData.getInfo().isOther_site();

        checkVideo();
        setCommentView();
        setListener();
    }

    private void setBuyDialog() {
        buyBangumiDialog = DialogHelper.getConfirmDialog(this,
                "承包本季番剧",
                "确定承包吗？\n 该操作将消耗您"+videoData.getBuy_price()+"个团子。\n承包后解锁本季全部视频。",
                "確定",
                "取消",
                false,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        buyBangumiBtn.setClickable(false);
                        buyBangumiBtn.setText("承包中");
                        buyBangumi();
                        buyBangumiDialog.dismiss();
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        buyBangumiDialog.dismiss();
                    }
                }
        ).create();
    }

    private void buyBangumi() {
        apiService.getBuyVideo(videoData.getSeason_id())
                .compose(Rx2Schedulers.applyObservableAsync())
                .subscribe(new ObserverWrapper<Integer>(){

                    @Override
                    public void onSuccess(Integer spend) {
                        LogUtils.d("bugBangmi","s = "+spend);
                        videoData.setBuyed(true);
                        checkVideo();
                        buyBangumiBtn.setClickable(true);
                        buyBangumiBtn.setText("已承包");

                        if (Constants.userInfoData.getBanlance().getCoin_count()>=spend){
                            Constants.userInfoData.getBanlance().setCoin_count(Constants.userInfoData.getBanlance().getCoin_count()-spend);
                        }else if (Constants.userInfoData.getBanlance().getCoin_count()!=0&&
                                Constants.userInfoData.getBanlance().getLight_count()>=(spend-Constants.userInfoData.getBanlance().getCoin_count())){
                            Constants.userInfoData.getBanlance().setCoin_count(0);
                            Constants.userInfoData.getBanlance().setLight_count(Constants.userInfoData.getBanlance().getLight_count()-
                                    (spend-Constants.userInfoData.getBanlance().getCoin_count()));
                        }else if (Constants.userInfoData.getBanlance().getCoin_count()==0&&
                                Constants.userInfoData.getBanlance().getLight_count()>= spend){
                            Constants.userInfoData.getBanlance().setLight_count(Constants.userInfoData.getBanlance().getLight_count()- spend);
                        }
                        Intent intent = new Intent(MineFragment.COINCHANGE);
                        sendBroadcast(intent);
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        LogUtils.d("bugBangmi","code = "+code+",errorMsg = "+errorMsg);
                        super.onFailure(code, errorMsg);
                    }
                });
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
                if (commentView!=null){
                    commentView.setRewardedChange(true);
                }
            }

            @Override
            public void onLikedFinish(boolean isLiked) {
                if (commentView!=null){
                    commentView.setLikedChange(isLiked);
                }
            }

            @Override
            public void onCollectedFinish(boolean isMarked) {
                if (commentView!=null){
                    if (commentView!=null){
                        commentView.setMarkedChange(isMarked);
                    }
                }
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
        commentView.setIs_creator(videoData.getInfo().isIs_creator());
        commentView.setLiked(videoData.getInfo().isLiked());
        commentView.setRewarded(videoData.getInfo().isRewarded());
        commentView.setMarked(videoData.getInfo().isMarked());
        commentView.setLikeCount(videoData.getInfo().getLike_users().getTotal());
        commentView.setMarkCount(videoData.getInfo().getMark_users().getTotal());
        commentView.setRewardCount(videoData.getInfo().getReward_users().getTotal());
        commentView.setOnLFCNetFinish(new ReplyAndCommentView.OnLFCNetFinish() {
            @Override
            public void onRewardFinish() {
                videoData.getInfo().setRewarded(true);
                checkVideo();
                if (videoLFCView!=null){
                    videoLFCView.setRewarded(true);
                }
            }
            @Override
            public void onLikeFinish(boolean isLike) {
                //视频只有投食 没有喜欢
            }
            @Override
            public void onMarkFinish(boolean isMark) {
                if (videoLFCView!=null){
                    videoLFCView.setCollected(isMark);
                }
            }
        });
        commentView.setNetAndListener();
    }

    private void checkVideo(){

        LogUtils.d("videoCheck","Must_reward = "+videoData.isMust_reward()
                +"\nneed_min_level = "+videoData.getNeed_min_level()
                +"\n reward = "+videoData.getInfo().isRewarded()
                +"\n Ip_blocked = "+videoData.isIp_blocked()
                +"\notherSite = "+otherSite);

        initVideo();

        if (UserSystem.getInstance().isLogin()&&Constants.userInfoData!=null){
            if (!otherSite){
                if (!videoData.isIp_blocked()){
                    //是否必须投食或承包
                    if (videoData.isMust_reward()){
                        if (videoData.getInfo().isRewarded()||videoData.isBuyed()){
                            initVideoUrl();
                            otherSiteInfo.setVisibility(View.GONE);
                            whyRewardLevel.setVisibility(View.GONE);
                        }else {
                            otherSiteInfo.setVisibility(View.VISIBLE);
                            otherSiteInfo.setText("该视频需要投食或承包才能进行观看");
                            whyRewardLevel.setVisibility(View.VISIBLE);
                            whyRewardLevel.setText("为什么要投食");
                            whyRewardLevel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(DramaVideoPlayActivity.this, PostDetailActivity.class);
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
                                    Intent intent = new Intent(DramaVideoPlayActivity.this, PostDetailActivity.class);
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
                if (videoData.getInfo().isIs_baidu_cloud()){
                    otherSiteInfo.setVisibility(View.VISIBLE);
                    otherSiteInfo.setText("该视频只提供百度云资源");
                    whyRewardLevel.setVisibility(View.GONE);
                }else {
                    otherSiteInfo.setVisibility(View.VISIBLE);
                    otherSiteInfo.setText("因版权等相关问题，该视频无法播放");
                    whyRewardLevel.setVisibility(View.GONE);
                }
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
        videoPlayer.setShowFullAnimation(false);

        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
        videoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (orientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
//                    LogUtils.d("videoPlayerAct","退出全屏");
//                    recyclerView.setVisibility(View.VISIBLE);
////                    showNavigation();
////                    videoPlayer.onBackFullscreen();
//                }else {
//                    LogUtils.d("videoPlayerAct","进入全屏");
//                    recyclerView.setVisibility(View.GONE);
////                    hideNavigation();
//                }
                orientationUtils.resolveByClick();
                videoPlayer.startWindowFullscreen(DramaVideoPlayActivity.this,true,true);
            }
        });
        videoPlayer.setVideoAllCallBack(new GSYSampleCallBack(){
            @Override
            public void onEnterFullscreen(String url, Object... objects) {
                super.onEnterFullscreen(url, objects);
                LogUtils.d("videoPlayerAct","进入全屏");
                recyclerView.setVisibility(View.GONE);
            }

            @Override
            public void onQuitFullscreen(String url, Object... objects) {
                super.onQuitFullscreen(url, objects);
                LogUtils.d("videoPlayerAct","退出全屏");
                recyclerView.setVisibility(View.VISIBLE);
                if (orientationUtils != null) {
                    orientationUtils.backToProtVideo();
                }
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
    private void showNavigation(){
        CommonUtil.showNavKey(this, videoPlayer.getSystemUiVisibility());
        //显示虚拟按键
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//            //低版本sdk
//            View v = getWindow().getDecorView();
//            v.setSystemUiVisibility(View.VISIBLE);
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            View decorView = getWindow().getDecorView();
//            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//            decorView.setSystemUiVisibility(uiOptions);
//        }
    }

    private void hideNavigation(){
        CommonUtil.hideNavKey(this);
        //隐藏虚拟按键
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//            View v = getWindow().getDecorView();
//            v.setSystemUiVisibility(View.GONE);
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            View decorView = getWindow().getDecorView();
//            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
//            decorView.setSystemUiVisibility(uiOptions);
//        }
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
        videoPlayer.onConfigurationChanged(this, newConfig, orientationUtils);
//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
//            recyclerView.setVisibility(View.VISIBLE);
//            commentView.setVisibility(View.VISIBLE);
////            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            showNavigation();
//        }else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
//            recyclerView.setVisibility(View.GONE);
//            commentView.setVisibility(View.GONE);
////            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            hideNavigation();
//        }
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

        GSYVideoManager.releaseAllVideos();

        if (orientationUtils != null)

            orientationUtils.releaseListener();
        super.onDestroy();
    }

    @Override

    public void onBackPressed() {
        //先返回正常状态
        try {
            if (videoPlayer.getCurrentPlayer().isIfCurrentIsFullscreen()) {
                orientationUtils.backToProtVideo();
                recyclerView.setVisibility(View.VISIBLE);
                return;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onBackPressed();
    }

    @Override
    protected void handler(Message msg) {
    }

    public CommentAdapter getCommentAdapter(){
        return commentAdapter;
    }

    public static DramaVideoPlayActivity getInstance(){
        return instance;
    }
}
