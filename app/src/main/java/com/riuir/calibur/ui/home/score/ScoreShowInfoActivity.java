package com.riuir.calibur.ui.home.score;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.hedgehog.ratingbar.RatingBar;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.activityUtils.RecyclerViewUtils;
import com.riuir.calibur.assistUtils.TimeUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.assistUtils.activityUtils.UserMainUtils;

import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.Drama.DramaActivity;
import com.riuir.calibur.ui.home.adapter.CommentAdapter;
import com.riuir.calibur.ui.home.adapter.MyLoadMoreView;
import com.riuir.calibur.ui.home.card.CardChildCommentActivity;
import com.riuir.calibur.ui.widget.BangumiForShowView;
import com.riuir.calibur.ui.widget.replyAndComment.ReplyAndCommentView;
import com.riuir.calibur.ui.widget.ScoreContentView;
import com.riuir.calibur.ui.widget.TrendingLikeFollowCollectionView;
import com.riuir.calibur.ui.widget.emptyView.AppListEmptyView;
import com.riuir.calibur.ui.widget.emptyView.AppListFailedView;
import com.riuir.calibur.ui.widget.popup.AppHeaderPopupWindows;
import com.riuir.calibur.utils.GlideUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import calibur.core.http.models.base.ResponseBean;
import calibur.core.http.models.comment.TrendingShowInfoCommentMain;
import calibur.core.http.models.followList.score.ScoreShowInfoPrimacy;
import calibur.core.http.observer.ObserverWrapper;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import retrofit2.Response;

public class ScoreShowInfoActivity extends BaseActivity {


    private ScoreShowInfoPrimacy primacyData;
    private TrendingShowInfoCommentMain commentMainData;
    private TrendingShowInfoCommentMain baseCommentMainData;
    private List<TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList> baseCommentMainList = new ArrayList<>();
    private List<TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList> commentMainList;
    private ArrayList<String> previewImagesList;

    int fetchId = 0;
    boolean isLoadMore = false;
    boolean isFirstLoad = false;
    boolean isRefresh = false;
    boolean isOnlySeeMaster = false;
    int onlySeeMaster = 0;

    private CommentAdapter commentAdapter;


    LinearLayout headerLayout;
    ScoreContentView headerContent;
    TextView headerCardTitle;
    ImageView headerUserIcon;
    TextView headerUserName;
    TextView headerTime;
    TextView headerScoreTotal;

    TextView scoreTitleLol;
    TextView scoreTitleCry;
    TextView scoreTitleFight;
    TextView scoreTitleMoe;
    TextView scoreTitleSound;
    TextView scoreTitleVision;
    TextView scoreTitleRole;
    TextView scoreTitleStory;
    TextView scoreTitleExpress;
    TextView scoreTitleStyle;

    RatingBar scoreRatingLol;
    RatingBar scoreRatingCry;
    RatingBar scoreRatingFight;
    RatingBar scoreRatingMoe;
    RatingBar scoreRatingSound;
    RatingBar scoreRatingVision;
    RatingBar scoreRatingRole;
    RatingBar scoreRatingStory;
    RatingBar scoreRatingExpress;
    RatingBar scoreRatingStyle;

    RadarChart scoreRadarChart;

    TrendingLikeFollowCollectionView trendingLFCView;

    BangumiForShowView headerBangumiView;

    private int scoreID;

    @BindView(R.id.score_show_info_list_view)
    RecyclerView scoreShowInfoListView;
    @BindView(R.id.score_show_info_refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.score_show_info_back_btn)
    ImageView backBtn;
    @BindView(R.id.score_show_info_list_header_more)
    AppHeaderPopupWindows headerMore;
    @BindView(R.id.score_show_info_comment_view)
    ReplyAndCommentView commentView;

    private int primacyId;

    private static final int NET_STATUS_PRIMACY = 0;
    private static final int NET_STATUS_MAIN_COMMENT = 1;


    AppListFailedView failedView;
    AppListEmptyView emptyView;

    private static ScoreShowInfoActivity instance;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_score_show_info;
    }

    @Override
    protected void onInit() {
        instance = this;
        Intent intent = getIntent();
        scoreID = intent.getIntExtra("scoreID",0);
        setBackBtn();
        setAdapter();
        isFirstLoad = true;
        refreshLayout.setRefreshing(true);
        setNet(NET_STATUS_MAIN_COMMENT);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    private void setNet(int NET_STATUS){

        if (NET_STATUS == NET_STATUS_PRIMACY){
            apiService.getCallScoreShowPrimacy(scoreID)
                    .compose(Rx2Schedulers.applyObservableAsync())
                    .subscribe(new ObserverWrapper<ScoreShowInfoPrimacy>(){

                        @Override
                        public void onSuccess(ScoreShowInfoPrimacy scoreShowInfoPrimacy) {
                            primacyData = scoreShowInfoPrimacy;
                            //两次网络请求都完成后开始加载数据
                            if (isFirstLoad){
                                isFirstLoad = false;
                                if (refreshLayout!=null&&scoreShowInfoListView!=null){
                                    refreshLayout.setRefreshing(false);
                                    setAdapter();
                                    setPrimacyView();
                                    setEmptyView();
                                }
                            }
                            if (isRefresh){
                                setRefresh();
                            }
                        }

                        @Override
                        public void onFailure(int code, String errorMsg) {
                            super.onFailure(code, errorMsg);
                            if (refreshLayout!=null){
                                if (isFirstLoad){
                                    isFirstLoad = false;
                                }
                                if (isRefresh){
                                    isRefresh = false;
                                }
                                refreshLayout.setRefreshing(false);
                                setFailedView();
                            }
                        }
                    });
        }

        if (NET_STATUS == NET_STATUS_MAIN_COMMENT){
            setFetchID();
            apiService.getCallMainComment("score",scoreID,fetchId,onlySeeMaster)
                    .compose(Rx2Schedulers.<Response<ResponseBean<TrendingShowInfoCommentMain>>>applyObservableAsync())
                    .subscribe(new ObserverWrapper<TrendingShowInfoCommentMain>() {
                        @Override
                        public void onSuccess(TrendingShowInfoCommentMain trendingShowInfoCommentMain) {
                            commentMainData = trendingShowInfoCommentMain;
                            commentMainList = trendingShowInfoCommentMain.getList();
                            if (isFirstLoad){
//                            isFirstLoad = false;
                                baseCommentMainData = trendingShowInfoCommentMain;
                                baseCommentMainList = trendingShowInfoCommentMain.getList();
                                //第一次网络请求结束后 开始第二次网络请求
                                setNet(NET_STATUS_PRIMACY);
                            }
                            if (isLoadMore){
                                setLoadMore();
                            }
                            if (isRefresh){
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
                                if (isFirstLoad){
                                    refreshLayout.setRefreshing(false);
                                    isFirstLoad = false;
                                }
                                if (isRefresh){
                                    refreshLayout.setRefreshing(false);
                                    isRefresh = false;
                                }
                                setFailedView();
                            }
                        }
                    });
        }
    }

    private void setAdapter() {

        if (primacyData!=null){
            primacyId = primacyData.getUser().getId();
        }
        commentAdapter = new CommentAdapter(R.layout.card_show_info_list_comment_item,baseCommentMainList,
                ScoreShowInfoActivity.this,apiPost,CommentAdapter.TYPE_SCORE,primacyId);
        scoreShowInfoListView.setLayoutManager(new LinearLayoutManager(ScoreShowInfoActivity.this));
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
        commentAdapter.disableLoadMoreIfNotFullPage(scoreShowInfoListView);



        scoreShowInfoListView.setAdapter(commentAdapter);
        commentAdapter.setHeaderAndEmpty(true);


    }

    private void setCommentView() {
        commentView.setStatus(ReplyAndCommentView.STATUS_MAIN_COMMENT);
        commentView.setApiPost(apiPost);
        commentView.setCommentAdapter(commentAdapter);
        commentView.setFromUserName("");
        commentView.setId(scoreID);
        commentView.setTitleId(primacyData.getUser().getId());
        commentView.setType(ReplyAndCommentView.TYPE_SCORE);
        commentView.setTargetUserId(0);
        commentView.setIs_creator(primacyData.isIs_creator());
        commentView.setLiked(primacyData.isLiked());
        commentView.setRewarded(primacyData.isRewarded());
        commentView.setMarked(primacyData.isMarked());
        commentView.setLikeCount(primacyData.getLike_users().getTotal());
        commentView.setMarkCount(primacyData.getMark_users().getTotal());
        commentView.setRewardCount(primacyData.getReward_users().getTotal());
        commentView.setOnLFCNetFinish(new ReplyAndCommentView.OnLFCNetFinish() {
            @Override
            public void onRewardFinish() {
                trendingLFCView.setRewarded(true);
            }

            @Override
            public void onLikeFinish(boolean isLike) {
                trendingLFCView.setLiked(isLike);
            }

            @Override
            public void onMarkFinish(boolean isMark) {
                trendingLFCView.setCollected(isMark);
            }
        });

        commentView.setNetAndListener();
    }

    private void setPrimacyView() {
        headerLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.score_show_info_list_header_view,null);

        headerCardTitle = headerLayout.findViewById(R.id.score_show_info_list_header_card_title);
        headerUserIcon = headerLayout.findViewById(R.id.score_show_info_list_header_user_icon);
        headerUserName = headerLayout.findViewById(R.id.score_show_info_list_header_user_name);
        headerTime = headerLayout.findViewById(R.id.score_show_info_list_header_time);
        headerScoreTotal = headerLayout.findViewById(R.id.score_show_info_list_header_total_score);

        headerScoreTotal.setText(primacyData.getTotal()+"分");
        scoreTitleLol = headerLayout.findViewById(R.id.score_show_info_list_header_score_lol_title);
        scoreTitleCry = headerLayout.findViewById(R.id.score_show_info_list_header_score_cry_title);
        scoreTitleFight = headerLayout.findViewById(R.id.score_show_info_list_header_score_fight_title);
        scoreTitleMoe = headerLayout.findViewById(R.id.score_show_info_list_header_score_moe_title);
        scoreTitleSound = headerLayout.findViewById(R.id.score_show_info_list_header_score_sound_title);
        scoreTitleVision = headerLayout.findViewById(R.id.score_show_info_list_header_score_vision_title);
        scoreTitleStory = headerLayout.findViewById(R.id.score_show_info_list_header_score_story_title);
        scoreTitleRole = headerLayout.findViewById(R.id.score_show_info_list_header_score_role_title);
        scoreTitleExpress = headerLayout.findViewById(R.id.score_show_info_list_header_score_express_title);
        scoreTitleStyle = headerLayout.findViewById(R.id.score_show_info_list_header_score_style_title);

        scoreRatingLol = headerLayout.findViewById(R.id.score_show_info_list_header_score_lol_rating);
        scoreRatingCry = headerLayout.findViewById(R.id.score_show_info_list_header_score_cry_rating);
        scoreRatingFight = headerLayout.findViewById(R.id.score_show_info_list_header_score_fight_rating);
        scoreRatingMoe = headerLayout.findViewById(R.id.score_show_info_list_header_score_moe_rating);
        scoreRatingSound = headerLayout.findViewById(R.id.score_show_info_list_header_score_sound_rating);
        scoreRatingVision = headerLayout.findViewById(R.id.score_show_info_list_header_score_vision_rating);
        scoreRatingStory = headerLayout.findViewById(R.id.score_show_info_list_header_score_story_rating);
        scoreRatingRole = headerLayout.findViewById(R.id.score_show_info_list_header_score_role_rating);
        scoreRatingExpress = headerLayout.findViewById(R.id.score_show_info_list_header_score_express_rating);
        scoreRatingStyle = headerLayout.findViewById(R.id.score_show_info_list_header_score_style_rating);

        scoreRadarChart = headerLayout.findViewById(R.id.score_show_info_list_header_score_radar_chart);

        setRadarChart();

        scoreTitleLol.setText("笑点："+(Double.parseDouble(primacyData.getLol())*2));
        scoreTitleCry.setText("泪点："+(Double.parseDouble(primacyData.getCry())*2));
        scoreTitleFight.setText("燃点："+(Double.parseDouble(primacyData.getFight())*2));
        scoreTitleMoe.setText("萌点："+(Double.parseDouble(primacyData.getMoe())*2));
        scoreTitleSound.setText("声音："+(Double.parseDouble(primacyData.getSound())*2));
        scoreTitleVision.setText("画面："+(Double.parseDouble(primacyData.getVision())*2));
        scoreTitleStory.setText("故事："+(Double.parseDouble(primacyData.getStory())*2));
        scoreTitleRole.setText("人设："+(Double.parseDouble(primacyData.getRole())*2));
        scoreTitleExpress.setText("内涵："+(Double.parseDouble(primacyData.getExpress())*2));
        scoreTitleStyle.setText("美感："+(Double.parseDouble(primacyData.getStyle())*2));

        scoreRatingLol.setStar((float) Double.parseDouble(primacyData.getLol()));
        scoreRatingCry.setStar((float) Double.parseDouble(primacyData.getCry()));
        scoreRatingFight.setStar((float) Double.parseDouble(primacyData.getFight()));
        scoreRatingMoe.setStar((float) Double.parseDouble(primacyData.getMoe()));
        scoreRatingSound.setStar((float) Double.parseDouble(primacyData.getSound()));
        scoreRatingVision.setStar((float) Double.parseDouble(primacyData.getVision()));
        scoreRatingStory.setStar((float) Double.parseDouble(primacyData.getStory()));
        scoreRatingRole.setStar((float) Double.parseDouble(primacyData.getRole()));
        scoreRatingExpress.setStar((float) Double.parseDouble(primacyData.getExpress()));
        scoreRatingStyle.setStar((float) Double.parseDouble(primacyData.getStyle()));

        /**
         * 先设置previewImages的List，再设置图文混排的content内容
         * 由于内部需要点击事件 顺序不可颠倒
         */
        setPreviewImageUrlList();
        headerContent = headerLayout.findViewById(R.id.score_show_info_list_header_score_content_layout);
        headerContent.setContent(primacyData.getContent(),previewImagesList);

        /**
         * 先设置trendingLFCView的各项属性，再设置开启监听和网络
         * 顺序不可颠倒
         */
        trendingLFCView = headerLayout.findViewById(R.id.score_show_info_list_header_lfc_view);
        trendingLFCView.setType("score");
        trendingLFCView.setApiPost(apiPost);
        trendingLFCView.setID(scoreID);
        trendingLFCView.setLiked(primacyData.isLiked());
        trendingLFCView.setCollected(primacyData.isMarked());
        trendingLFCView.setRewarded(primacyData.isRewarded());
        trendingLFCView.setIsCreator(primacyData.isIs_creator());
        trendingLFCView.setOnLFCNetFinish(new TrendingLikeFollowCollectionView.OnLFCNetFinish() {
            @Override
            public void onLikedFinish(boolean isLiked) {
                if (commentView!=null){
                    commentView.setLikedChange(isLiked);
                }
            }
            @Override
            public void onCollectedFinish(boolean isMarked) {
                if (commentView!=null){
                    commentView.setMarkedChange(isMarked);
                }
            }
            @Override
            public void onRewardFinish() {
                if (commentView!=null){
                    commentView.setRewardedChange(true);
                }
            }
        });
        trendingLFCView.startListenerAndNet();

        //所属番剧操作
        headerBangumiView = headerLayout.findViewById(R.id.score_show_info_list_header_bangumi_view);
        LogUtils.d("cardShowHeader","header Data = "+primacyData.toString());
        headerBangumiView.setName(primacyData.getBangumi().getName());
        headerBangumiView.setSummary(primacyData.getBangumi().getSummary());
        headerBangumiView.setImageView(ScoreShowInfoActivity.this,primacyData.getBangumi().getAvatar());

        headerCardTitle.setText(primacyData.getTitle());
        GlideUtils.loadImageViewCircle(ScoreShowInfoActivity.this,primacyData.getUser().getAvatar(),headerUserIcon);
        headerUserName.setText(primacyData.getUser().getNickname().replace("\n",""));
        headerTime.setText( TimeUtils.HowLongTimeForNow(primacyData.getCreated_at()));


        headerBangumiView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScoreShowInfoActivity.this, DramaActivity.class);
                intent.putExtra("animeId",primacyData.getBangumi().getId());
                startActivity(intent);

            }
        });


        commentAdapter.addHeaderView(headerLayout);

//        scoreShowInfoListView.getLayoutManager().smoothScrollToPosition(scoreShowInfoListView,null,0);
        RecyclerViewUtils.setScorllToTop(scoreShowInfoListView);

        setCommentView();
        setListener();
    }

    private void setRadarChart() {

        scoreRadarChart.getDescription().setEnabled(false);

        scoreRadarChart.setWebLineWidth(1f);
        scoreRadarChart.setWebColor(Color.LTGRAY);
        scoreRadarChart.setWebLineWidthInner(1f);
        scoreRadarChart.setWebColorInner(Color.LTGRAY);
        scoreRadarChart.setWebAlpha(150);
        setData();

        scoreRadarChart.animateXY(0, 0);

        XAxis xAxis = scoreRadarChart.getXAxis();
//        xAxis.setTypeface(mTfLight);
        xAxis.setTextSize(9f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            private String[] mActivities = new String[]{"笑点", "泪点", "燃点", "萌点", "声音","画面","故事","人设","内涵","美感"};
            @Override

            public String getFormattedValue(float value, AxisBase axis) {
                return mActivities[(int) value % mActivities.length];
            }
        });
        xAxis.setTextColor(getResources().getColor(R.color.color_FF9B9B9B));

        YAxis yAxis = scoreRadarChart.getYAxis();
//        yAxis.setTypeface(mTfLight);
        yAxis.setLabelCount(5, false);
        yAxis.setTextSize(9f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(80f);
        yAxis.setDrawLabels(false);
        Legend l = scoreRadarChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(true);
//        l.setTypeface(mTfLight);
        l.setXEntrySpace(0f);
        l.setYEntrySpace(0f);
        l.setTextColor(getResources().getColor(R.color.color_FF7B7B7B));

    }

    public void setData() {

        ArrayList<RadarEntry> entries1 = new ArrayList<RadarEntry>();
//        ArrayList<RadarEntry> entries2 = new ArrayList<RadarEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of

        // the chart.

        entries1.add(new RadarEntry((float) Double.parseDouble(primacyData.getLol())*20));
        entries1.add(new RadarEntry((float) Double.parseDouble(primacyData.getCry())*20));
        entries1.add(new RadarEntry((float) Double.parseDouble(primacyData.getFight())*20));
        entries1.add(new RadarEntry((float) Double.parseDouble(primacyData.getMoe())*20));
        entries1.add(new RadarEntry((float) Double.parseDouble(primacyData.getSound())*20));
        entries1.add(new RadarEntry((float) Double.parseDouble(primacyData.getVision())*20));
        entries1.add(new RadarEntry((float) Double.parseDouble(primacyData.getStory())*20));
        entries1.add(new RadarEntry((float) Double.parseDouble(primacyData.getRole())*20));
        entries1.add(new RadarEntry((float) Double.parseDouble(primacyData.getExpress())*20));
        entries1.add(new RadarEntry((float) Double.parseDouble(primacyData.getStyle())*20));




        RadarDataSet set1 = new RadarDataSet(entries1, "个人总评");
        set1.setColor(getResources().getColor(R.color.color_radar_chart));
        set1.setFillColor(getResources().getColor(R.color.color_radar_chart));
        set1.setDrawFilled(true);
        set1.setFillAlpha(180);
        set1.setLineWidth(1f);
        set1.setDrawHighlightCircleEnabled(true);
        set1.setDrawHighlightIndicators(false);

        ArrayList<IRadarDataSet> sets = new ArrayList<IRadarDataSet>();
        sets.add(set1);

        RadarData data = new RadarData(sets);
//        data.setValueTypeface(mTfLight);
        data.setValueTextSize(8f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.WHITE);

        scoreRadarChart.setData(data);
        scoreRadarChart.invalidate();

    }
    private void setBackBtn(){
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void setListener() {

        setHeaderMore();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                setNet(NET_STATUS_MAIN_COMMENT);
            }
        });

        headerUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserMainUtils.toUserMainActivity(ScoreShowInfoActivity.this,
                        primacyData.getUser().getId(),primacyData.getUser().getZone());
            }
        });

        commentAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                List<TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList> dataList =  adapter.getData();

                if (view.getId() == R.id.card_show_info_list_comment_item_user_icon){
                    UserMainUtils.toUserMainActivity(ScoreShowInfoActivity.this,
                            dataList.get(position).getFrom_user_id(),dataList.get(position).getFrom_user_zone());
                }

                if (view.getId() == R.id.card_show_info_list_comment_item_reply){
                    int commentId = dataList.get(position).getId();
                    Intent intent = new Intent(ScoreShowInfoActivity.this,CardChildCommentActivity.class);
                    intent.putExtra("id",commentId);
                    intent.putExtra("mainComment",dataList.get(position));
                    intent.putExtra("type","score");
                    startActivity(intent);
                }
            }
        });
        commentAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                List<TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList> dataList =  adapter.getData();
                int commentId = dataList.get(position).getId();
                Intent intent = new Intent(ScoreShowInfoActivity.this,CardChildCommentActivity.class);
                intent.putExtra("id",commentId);
                intent.putExtra("mainComment",dataList.get(position));
                intent.putExtra("type","score");
                startActivity(intent);
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
        }, scoreShowInfoListView);
    }

    private void setHeaderMore() {
        headerMore.setReportModelTag(AppHeaderPopupWindows.SCORE,primacyData.getId());
//        headerMore.setShareLayout(primacyData.getTitle(),AppHeaderPopupWindows.SCORE,primacyData.getId(),"");
        headerMore.setShareLayout(ScoreShowInfoActivity.this,primacyData.getShare_data(),AppHeaderPopupWindows.SCORE);

        headerMore.setDeleteLayout(AppHeaderPopupWindows.SCORE,primacyData.getId(),
                primacyData.getUser().getId(),primacyData.getUser().getId(),apiPost);
        headerMore.initOnlySeeMaster(AppHeaderPopupWindows.SCORE);
        headerMore.setOnlySeeMasterClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOnlySeeMaster){
                    isOnlySeeMaster = false;
                    onlySeeMaster = 0;
                }else {
                    isOnlySeeMaster = true;
                    onlySeeMaster = 1;
                }
                headerMore.setIsOnlySeeMaster(isOnlySeeMaster);
                //刷新
                isRefresh = true;
                setNet(NET_STATUS_MAIN_COMMENT);
            }
        });
        headerMore.setOnDeleteFinish(new AppHeaderPopupWindows.OnDeleteFinish() {
            @Override
            public void deleteFinish() {
                finish();
            }
        });
    }


    private void setPreviewImageUrlList() {
        if (previewImagesList == null||previewImagesList.size() == 0){
            previewImagesList = new ArrayList<>();
        }
        if (previewImagesList.size()!=0){
            previewImagesList.clear();
        }

        for (int i = 0; i <primacyData.getContent().size() ; i++) {
            if (primacyData.getContent().get(i).getType().equals("img")){
                previewImagesList.add(primacyData.getContent().get(i).getUrl());
            }
        }
    }

    private void setEmptyView(){
        if (baseCommentMainList==null||baseCommentMainList.size()==0){
            if (emptyView == null){
                emptyView = new AppListEmptyView(ScoreShowInfoActivity.this);
                emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
            commentAdapter.setEmptyView(emptyView);


        }
    }

    private void setFailedView(){
        //加载失败 点击重试
        if (failedView == null){
            failedView = new AppListFailedView(ScoreShowInfoActivity.this);
            failedView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        }

        commentAdapter.setEmptyView(failedView);

    }

    private void setRefresh(){
        commentAdapter.removeAllHeaderView();
        isRefresh = false;
        refreshLayout.setRefreshing(false);
        commentAdapter.setNewData(commentMainList);
        setPrimacyView();
        ToastUtils.showShort(ScoreShowInfoActivity.this,"刷新成功！");
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

    private void setFetchID() {
        if (isLoadMore){
            fetchId = commentAdapter.getData().get(commentAdapter.getData().size()-1).getId();
        }
        if (isFirstLoad){
            fetchId = 0;
        }
        if (isRefresh){
            fetchId = 0;
        }
        LogUtils.d("cardShow","fetchID = "+fetchId);
    }

    @Override
    protected void handler(Message msg) {

    }

    public CommentAdapter getCommentAdapter(){
        return commentAdapter;
    }

    public static ScoreShowInfoActivity getInstance(){
        return instance;
    }
}
