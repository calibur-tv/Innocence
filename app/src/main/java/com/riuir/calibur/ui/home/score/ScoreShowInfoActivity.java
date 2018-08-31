package com.riuir.calibur.ui.home.score;

import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.mikephil.charting.animation.Easing;
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
import com.google.gson.Gson;
import com.hedgehog.ratingbar.RatingBar;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.activityUtils.RecyclerViewUtils;
import com.riuir.calibur.assistUtils.TimeUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.assistUtils.activityUtils.UserMainUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.trending.ScoreShowInfoPrimacy;
import com.riuir.calibur.data.trending.TrendingShowInfoCommentMain;
import com.riuir.calibur.net.ApiGet;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.Drama.DramaActivity;
import com.riuir.calibur.ui.home.adapter.CommentAdapter;
import com.riuir.calibur.ui.home.adapter.MyLoadMoreView;
import com.riuir.calibur.ui.home.card.CardChildCommentActivity;
import com.riuir.calibur.assistUtils.activityUtils.LoginUtils;
import com.riuir.calibur.ui.widget.BangumiForShowView;
import com.riuir.calibur.ui.widget.ScoreContentView;
import com.riuir.calibur.ui.widget.TrendingLikeFollowCollectionView;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.GlideUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScoreShowInfoActivity extends BaseActivity {


    private ScoreShowInfoPrimacy.ScoreShowInfoPrimacyData primacyData;
    private TrendingShowInfoCommentMain.TrendingShowInfoCommentMainData commentMainData;
    private TrendingShowInfoCommentMain.TrendingShowInfoCommentMainData baseCommentMainData;
    private List<TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList> baseCommentMainList;
    private ArrayList<String> previewImagesList;

    int fetchId = 0;
    boolean isLoadMore = false;
    boolean isFirstLoad = false;

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
    @BindView(R.id.score_show_info_back_btn)
    ImageView backBtn;

    private static final int NET_STATUS_PRIMACY = 0;
    private static final int NET_STATUS_MAIN_COMMENT = 1;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_score_show_info;
    }

    @Override
    protected void onInit() {
        Intent intent = getIntent();
        scoreID = intent.getIntExtra("scoreID",0);
        setBackBtn();
        isFirstLoad = true;
        setNet(NET_STATUS_MAIN_COMMENT);
    }

    private void setNet(int NET_STATUS){
        ApiGet mApiGet;
        if (Constants.ISLOGIN){
            mApiGet = apiGetHasAuth;
        }else {
            mApiGet = apiGet;
        }
        LogUtils.d("scoreInfo","000000");
        if (NET_STATUS == NET_STATUS_PRIMACY){
            mApiGet.getCallScoreShowPrimacy(scoreID).enqueue(new Callback<ScoreShowInfoPrimacy>() {
                @Override
                public void onResponse(Call<ScoreShowInfoPrimacy> call, Response<ScoreShowInfoPrimacy> response) {
                    if (response!=null&&response.isSuccessful()){
                        primacyData = response.body().getData();
                        //两次网络请求都完成后开始加载数据
                        LogUtils.d("scoreInfo","222222");
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
                            ToastUtils.showShort(ScoreShowInfoActivity.this,info.getMessage());
                            LoginUtils.ReLogin(ScoreShowInfoActivity.this);
                        }else if (info.getCode() == 40401){
                            ToastUtils.showShort(ScoreShowInfoActivity.this,info.getMessage());
                        }
                    }else {
                        ToastUtils.showShort(ScoreShowInfoActivity.this,"未知错误出现了！");
                    }
                }

                @Override
                public void onFailure(Call<ScoreShowInfoPrimacy> call, Throwable t) {
                    ToastUtils.showShort(ScoreShowInfoActivity.this,"请检查您的网络！");
                    LogUtils.d("scoreInfo","t = "+t.getMessage());
                }
            });
        }

        if (NET_STATUS == NET_STATUS_MAIN_COMMENT){
            setFetchID();
            LogUtils.d("scoreInfo","111111");
            mApiGet.getCallMainComment("score",scoreID,fetchId).enqueue(new Callback<TrendingShowInfoCommentMain>() {
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
                            ToastUtils.showShort(ScoreShowInfoActivity.this,info.getMessage());
                        }else if (info.getCode() == 40003){
                            ToastUtils.showShort(ScoreShowInfoActivity.this,info.getMessage());
                        }
                        if (isLoadMore){
                            commentAdapter.loadMoreFail();
                            isLoadMore = false;
                        }
                    }else {
                        ToastUtils.showShort(ScoreShowInfoActivity.this,"未知错误出现了！");
                        if (isLoadMore){
                            commentAdapter.loadMoreFail();
                            isLoadMore = false;
                        }
                    }
                }

                @Override
                public void onFailure(Call<TrendingShowInfoCommentMain> call, Throwable t) {
                    ToastUtils.showShort(ScoreShowInfoActivity.this,"未知错误出现了！");
                    if (isLoadMore){
                        commentAdapter.loadMoreFail();
                        isLoadMore = false;
                    }
                }
            });
        }
    }

    private void setAdapter() {
        commentAdapter = new CommentAdapter(R.layout.card_show_info_list_comment_item,baseCommentMainList,ScoreShowInfoActivity.this,apiPost,CommentAdapter.TYPE_SCORE);
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

        if (commentMainData.isNoMore()){
            commentAdapter.setEnableLoadMore(false);
        }else {
            //添加底部footer
            commentAdapter.setEnableLoadMore(true);
            commentAdapter.setLoadMoreView(new MyLoadMoreView());
            commentAdapter.disableLoadMoreIfNotFullPage(scoreShowInfoListView);
        }

        scoreShowInfoListView.setAdapter(commentAdapter);

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
        trendingLFCView.startListenerAndNet();

        //所属番剧操作
        headerBangumiView = headerLayout.findViewById(R.id.score_show_info_list_header_bangumi_view);
        LogUtils.d("cardShowHeader","header Data = "+primacyData.toString());
        headerBangumiView.setName(primacyData.getBangumi().getName());
        headerBangumiView.setSummary(primacyData.getBangumi().getSummary());
        headerBangumiView.setImageView(ScoreShowInfoActivity.this,primacyData.getBangumi().getAvatar());

        headerCardTitle.setText(primacyData.getTitle());
        GlideUtils.loadImageViewCircle(ScoreShowInfoActivity.this,primacyData.getUser().getAvatar(),headerUserIcon);
        headerUserName.setText(primacyData.getUser().getNickname());
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


    private void setPreviewImageUrlList() {
        if (previewImagesList == null||previewImagesList.size() == 0){
            previewImagesList = new ArrayList<>();
        }

        for (int i = 0; i <primacyData.getContent().size() ; i++) {
            if (primacyData.getContent().get(i).getType().equals("img")){
                previewImagesList.add(primacyData.getContent().get(i).getUrl());
            }
        }
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
        LogUtils.d("cardShow","fetchID = "+fetchId);
    }

    @Override
    protected void handler(Message msg) {

    }
}
