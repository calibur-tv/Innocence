package com.riuir.calibur.ui.home.Drama;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.google.gson.Gson;
import com.hedgehog.ratingbar.RatingBar;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.MainTrendingInfo;
import com.riuir.calibur.data.anime.AnimeScoreInfo;
import com.riuir.calibur.data.params.FolllowListParams;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.home.Drama.adapter.DramaScoreListAdapter;
import com.riuir.calibur.ui.home.adapter.MyLoadMoreView;
import com.riuir.calibur.ui.home.adapter.ScoreListAdapter;
import com.riuir.calibur.ui.home.score.ScoreShowInfoActivity;
import com.riuir.calibur.ui.widget.emptyView.AppListEmptyView;
import com.riuir.calibur.ui.widget.emptyView.AppListFailedView;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class DramaScoreFragment extends BaseFragment {
    //fragment_drama_score
    String seenIds = "";
    List<Integer> seenIdList = new ArrayList<>();

    @BindView(R.id.drama_score_list_view)
    RecyclerView scoreListView;

    @BindView(R.id.drama_score_refresh_layout)
    SwipeRefreshLayout scoreRefreshLayout;

    LinearLayout headerLayout;
    RadarChart radarChart;
    PieChart pieChart;
    RatingBar ratingBar;
    TextView allScore;
    TextView peopleNum;
    NumberProgressBar bar1,bar2,bar3,bar4,bar5;

    //用来动态改变RecyclerView的变量
    private List<MainTrendingInfo.MainTrendingInfoList> listScore;
    //传给Adapter的值 首次加载后不可更改 不然会导致数据出错
    private List<MainTrendingInfo.MainTrendingInfoList> baseListScore = new ArrayList<>();

    private MainTrendingInfo.MainTrendingInfoData mainScoreInfoData;

    private AnimeScoreInfo.AnimeScoreInfoData animeScore;

    boolean isLoadMore = false;
    boolean isRefresh = false;
    boolean isFirstLoad = false;
    int bangumiID = 0;

    private DramaScoreListAdapter adapter;

    private static final int NET_LIST = 0;
    private static final int NET_PRIMACY = 1;

    private Call<MainTrendingInfo> listCall;
    private Call<AnimeScoreInfo> primacyCall;

    AppListFailedView failedView;
    AppListEmptyView emptyView;

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_drama_score;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        DramaActivity dramaActivity = (DramaActivity) getActivity();
        bangumiID = dramaActivity.getAnimeID();
        baseListScore.clear();
        setListAdapter();
        isFirstLoad = true;
        scoreRefreshLayout.setRefreshing(true);
        setNet(NET_LIST);
    }

    private void setSeendIdS() {
        if (seenIdList!=null&&seenIdList.size()!=0){
            for (int position = 0; position <seenIdList.size() ; position++) {
                int id = seenIdList.get(position);
                if (position == 0){
                    if (seenIds == null||seenIds.length() == 0){
                        seenIds = seenIds+id;
                    }else {
                        seenIds = seenIds+","+id;
                    }
                }else {
                    seenIds = seenIds+","+id;
                }
            }
        }
        if (isRefresh){
            seenIds = "";
            seenIdList.clear();
        }
        if (isFirstLoad){
            seenIds = "";
            seenIdList.clear();
        }

        LogUtils.d("image_1","seenIds = "+seenIds );
    }

    @Override
    public void onDestroy() {
        if (listCall!=null){
            listCall.cancel();
        }
        if (primacyCall!=null){
            primacyCall.cancel();
        }
        super.onDestroy();
    }

    private void setNet(int NET_STATUS) {
        if (NET_STATUS == NET_LIST){
            setSeendIdS();
            FolllowListParams folllowListParams = new FolllowListParams();
            folllowListParams.setType("score");
            folllowListParams.setSort("active");
            folllowListParams.setBangumiId(bangumiID);
            folllowListParams.setUserZone("");
            folllowListParams.setPage(0);
            folllowListParams.setTake(0);
            folllowListParams.setMinId(0);
            folllowListParams.setSeenIds(seenIds);
            listCall = apiPost.getFollowList(folllowListParams);
            listCall.enqueue(new Callback<MainTrendingInfo>() {
                @Override
                public void onResponse(Call<MainTrendingInfo> call, Response<MainTrendingInfo> response) {

                    if (response!=null&&response.isSuccessful()){

                        mainScoreInfoData = response.body().getData();
                        listScore = response.body().getData().getList();
                        if (isFirstLoad){
                            baseListScore = response.body().getData().getList();
                            if (baseListScore!=null&&baseListScore.size()!=0){
                                setNet(NET_PRIMACY);
                            }else {
                                if (scoreRefreshLayout!=null){
                                    scoreRefreshLayout.setRefreshing(false);
                                }
                            }
                            setEmptyView();
                        }
                        if (isLoadMore){
                            setLoadMore();
                        }
                        if (isRefresh){
                            if (listScore!=null&&listScore.size()!=0){
                                setNet(NET_PRIMACY);
                            }else {
                                if (scoreRefreshLayout!=null){
                                    scoreRefreshLayout.setRefreshing(false);
                                }
                                isRefresh = false;
                                ToastUtils.showShort(getContext(),"刷新成功！");
                            }
                        }
                        for (MainTrendingInfo.MainTrendingInfoList hotItem :listScore){
                            seenIdList.add(hotItem.getId());
                        }

                    }else if (!response.isSuccessful()){
                        String errorStr = "";
                        try {
                            errorStr = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Gson gson = new Gson();
                        Event<String> info =gson.fromJson(errorStr,Event.class);

                        ToastUtils.showShort(getContext(),info.getMessage());
                        if (isLoadMore){
                            adapter.loadMoreFail();
                            isLoadMore = false;
                        }
                        if (isRefresh){
                            if (scoreRefreshLayout!=null){
                                scoreRefreshLayout.setRefreshing(false);
                            }
                            isRefresh = false;
                        }
                        if (isFirstLoad){
                            isFirstLoad = false;
                            if (scoreRefreshLayout!=null){
                                scoreRefreshLayout.setRefreshing(false);
                            }
                        }
                        setFailedView();
                    }else {
                        ToastUtils.showShort(getContext(),"未知原因导致加载失败了！");
                        if (isLoadMore){
                            adapter.loadMoreFail();
                            isLoadMore = false;
                        }
                        if (isRefresh){
                            if (scoreRefreshLayout!=null){
                                scoreRefreshLayout.setRefreshing(false);
                            }
                            isRefresh = false;
                        }
                        if (isFirstLoad){
                            isFirstLoad = false;
                            if (scoreRefreshLayout!=null){
                                scoreRefreshLayout.setRefreshing(false);
                            }
                        }
                        setFailedView();
                    }
                }

                @Override
                public void onFailure(Call<MainTrendingInfo> call, Throwable t) {
                    if (call.isCanceled()){
                    }else {
                        ToastUtils.showShort(getContext(),"请检查您的网络！");
                        CrashReport.postCatchedException(t);
                        if (isLoadMore){
                            adapter.loadMoreFail();
                            isLoadMore = false;
                        }
                        if (isRefresh){
                            if (scoreRefreshLayout!=null){
                                scoreRefreshLayout.setRefreshing(false);
                            }
                            isRefresh = false;
                        }
                        if (isFirstLoad){
                            isFirstLoad = false;
                            if (scoreRefreshLayout!=null){
                                scoreRefreshLayout.setRefreshing(false);
                            }
                        }
                        setFailedView();
                    }
                }
            });
        }

        if (NET_STATUS == NET_PRIMACY){
            primacyCall = apiGet.getCallAnimeShowAllScore(bangumiID);
            primacyCall.enqueue(new Callback<AnimeScoreInfo>() {
                @Override
                public void onResponse(Call<AnimeScoreInfo> call, Response<AnimeScoreInfo> response) {
                    if (response!=null&&response.isSuccessful()){
                        animeScore = response.body().getData();
                        if (isFirstLoad){
                            isFirstLoad = false;
                            if (scoreRefreshLayout!=null&&scoreListView!=null){
                                if (scoreRefreshLayout!=null){
                                    scoreRefreshLayout.setRefreshing(false);
                                }
                                setListAdapter();
                                setPrimacy();
                            }
                        }
                        if (isRefresh){
                            setRefresh();
                        }

                    }else if (!response.isSuccessful()){
                        String errorStr = "";
                        try {
                            errorStr = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Gson gson = new Gson();
                        Event<String> info =gson.fromJson(errorStr,Event.class);

                        ToastUtils.showShort(getContext(),info.getMessage());
                        if (isFirstLoad){
                            isFirstLoad = false;
                            if (scoreRefreshLayout!=null){
                                scoreRefreshLayout.setRefreshing(false);
                            }
                        }
                        if (isRefresh){
                            isRefresh = false;
                            if (scoreRefreshLayout!=null){
                                scoreRefreshLayout.setRefreshing(false);
                            }
                        }
                        setFailedView();
                    }else {
                        ToastUtils.showShort(getContext(),"未知原因导致加载失败！");
                        if (isFirstLoad){
                            isFirstLoad = false;
                            if (scoreRefreshLayout!=null){
                                scoreRefreshLayout.setRefreshing(false);
                            }
                        }
                        if (isRefresh){
                            isRefresh = false;
                            if (scoreRefreshLayout!=null){
                                scoreRefreshLayout.setRefreshing(false);
                            }
                        }
                        setFailedView();
                    }
                }

                @Override
                public void onFailure(Call<AnimeScoreInfo> call, Throwable t) {
                    if (call.isCanceled()){
                    }else {
                        ToastUtils.showShort(getContext(),"请检查您的网络！");
                        if (isFirstLoad){
                            isFirstLoad = false;
                            if (scoreRefreshLayout!=null){
                                scoreRefreshLayout.setRefreshing(false);
                            }
                        }
                        if (isRefresh){
                            isRefresh = false;
                            if (scoreRefreshLayout!=null){
                                scoreRefreshLayout.setRefreshing(false);
                            }
                        }
                        setFailedView();
                    }
                }
            });
        }

    }

    private void setPrimacy() {
        headerLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.drama_score_list_header_view,null);
        radarChart = headerLayout.findViewById(R.id.drama_score_list_header_score_radar_chart);
        pieChart = headerLayout.findViewById(R.id.drama_score_list_header_score_pie_chart);
        ratingBar = headerLayout.findViewById(R.id.drama_score_list_header_score_all_score_rating);
        allScore = headerLayout.findViewById(R.id.drama_score_list_header_score_all_score_text);
        peopleNum = headerLayout.findViewById(R.id.drama_score_list_header_score_people_number);
        bar1 = headerLayout.findViewById(R.id.drama_score_list_header_score_starbar_1);
        bar2 = headerLayout.findViewById(R.id.drama_score_list_header_score_starbar_2);
        bar3 = headerLayout.findViewById(R.id.drama_score_list_header_score_starbar_3);
        bar4 = headerLayout.findViewById(R.id.drama_score_list_header_score_starbar_4);
        bar5 = headerLayout.findViewById(R.id.drama_score_list_header_score_starbar_5);

        if (animeScore!=null&&animeScore.getLadder()!=null){
            int one = animeScore.getLadder().get(0).getVal();
            int two = animeScore.getLadder().get(1).getVal();
            int three = animeScore.getLadder().get(2).getVal();
            int four = animeScore.getLadder().get(3).getVal();
            int five = animeScore.getLadder().get(4).getVal();
            int max = one+two+three+four+five;

            bar1.setMax(max);
            bar2.setMax(max);
            bar3.setMax(max);
            bar4.setMax(max);
            bar5.setMax(max);
            bar1.setProgress(one);
            bar2.setProgress(two);
            bar3.setProgress(three);
            bar4.setProgress(four);
            bar5.setProgress(five);

            peopleNum.setText("共"+animeScore.getCount()+"人评分");
            if (animeScore.getTotal()!=null){
                allScore.setText((Double.parseDouble(animeScore.getTotal())/10)+"");
                int a = (int) Double.parseDouble(animeScore.getTotal())/10;

                float b = a/2f;
                ratingBar.setStar(b);
            }
            setRadarChart();
//        setPieChart();


        }

        adapter.addHeaderView(headerLayout);

    }

    private void setPieChart() {

        float one = animeScore.getLadder().get(0).getVal();
        float two = animeScore.getLadder().get(1).getVal();
        float three = animeScore.getLadder().get(2).getVal();
        float four = animeScore.getLadder().get(3).getVal();
        float five = animeScore.getLadder().get(4).getVal();

        //这个方法为true就是环形图，为false就是饼图
        pieChart.setDrawHoleEnabled(true);
        //设置环形中间空白颜色是白色
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setRotationEnabled(false);//是否可以旋转
        Description desc = new Description();
        desc.setText("");
        pieChart.setDescription(desc);

        List<PieEntry> strings = new ArrayList<>();
        strings.add(new PieEntry(one,"1星"));
        strings.add(new PieEntry(two,"2星"));
        strings.add(new PieEntry(three,"3星"));
        strings.add(new PieEntry(four,"4星"));
        strings.add(new PieEntry(five,"5星"));

        PieDataSet dataSet = new PieDataSet(strings,"");
        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(getResources().getColor(R.color.color_radar_chart));
        colors.add(getResources().getColor(R.color.theme_magic_sakura_yellow));
        colors.add(getResources().getColor(R.color.theme_magic_sakura_blue));
        colors.add(getResources().getColor(R.color.theme_magic_sakura_primary));
        colors.add(getResources().getColor(R.color.color_FFB75EB7));
        dataSet.setColors(colors);
        PieData pieData = new PieData(dataSet);
        pieData.setDrawValues(true);
        pieData.setValueTextColor(Color.WHITE);
        pieChart.setData(pieData);
        pieChart.invalidate();


    }

    private void setRadarChart() {
        radarChart.getDescription().setEnabled(false);

        radarChart.setWebLineWidth(1f);
        radarChart.setWebColor(Color.LTGRAY);
        radarChart.setWebLineWidthInner(1f);
        radarChart.setWebColorInner(Color.LTGRAY);
        radarChart.setWebAlpha(150);
        radarChart.setRotationEnabled(false);
        Legend legend = radarChart.getLegend();
        legend.setEnabled(false);
        setData();

        radarChart.animateXY(0, 0);

        XAxis xAxis = radarChart.getXAxis();

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

        YAxis yAxis = radarChart.getYAxis();

//        yAxis.setTypeface(mTfLight);

        yAxis.setLabelCount(5, false);

        yAxis.setTextSize(9f);

        yAxis.setAxisMinimum(0f);

        yAxis.setAxisMaximum(80f);

        yAxis.setDrawLabels(false);


        Legend l = radarChart.getLegend();
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

        entries1.add(new RadarEntry((float) Double.parseDouble(animeScore.getRadar().getLol())*10));
        entries1.add(new RadarEntry((float) Double.parseDouble(animeScore.getRadar().getCry())*10));
        entries1.add(new RadarEntry((float) Double.parseDouble(animeScore.getRadar().getFight())*10));
        entries1.add(new RadarEntry((float) Double.parseDouble(animeScore.getRadar().getMoe())*10));
        entries1.add(new RadarEntry((float) Double.parseDouble(animeScore.getRadar().getSound())*10));
        entries1.add(new RadarEntry((float) Double.parseDouble(animeScore.getRadar().getVision())*10));
        entries1.add(new RadarEntry((float) Double.parseDouble(animeScore.getRadar().getStory())*10));
        entries1.add(new RadarEntry((float) Double.parseDouble(animeScore.getRadar().getRole())*10));
        entries1.add(new RadarEntry((float) Double.parseDouble(animeScore.getRadar().getExpress())*10));
        entries1.add(new RadarEntry((float) Double.parseDouble(animeScore.getRadar().getStyle())*10));




        RadarDataSet set1 = new RadarDataSet(entries1, "总评");
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

        radarChart.setData(data);
        radarChart.invalidate();

    }

    private void setListAdapter() {
        adapter = new DramaScoreListAdapter(R.layout.main_score_list_item,baseListScore,getContext());
        scoreListView.setLayoutManager(new LinearLayoutManager(App.instance()));
        adapter.setHasStableIds(true);
        /**
         * adapter动画效果
         * ALPHAIN 渐显
         *SCALEIN 缩放
         *SLIDEIN_BOTTOM 从下到上
         *SLIDEIN_LEFT 从左到右
         *SLIDEIN_RIGHT 从右到左
         */
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        //开启上拉加载更多
        adapter.setEnableLoadMore(true);


        //添加底部footer
        adapter.setLoadMoreView(new MyLoadMoreView());
        adapter.disableLoadMoreIfNotFullPage(scoreListView);

        scoreListView.setAdapter(adapter);

        //添加监听
        setListener();
    }

    private void setListener() {

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //item被点击，跳转页面
                Intent intent = new Intent(getActivity(), ScoreShowInfoActivity.class);
                MainTrendingInfo.MainTrendingInfoList scoreInfo = (MainTrendingInfo.MainTrendingInfoList) adapter.getData().get(position);
                int scoreID = scoreInfo.getId();
                intent.putExtra("scoreID",scoreID);
                startActivity(intent);

            }
        });
        //上拉加载监听
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override public void onLoadMoreRequested() {
                isLoadMore = true;
                setNet(NET_LIST);
            }
        }, scoreListView);

        //下拉刷新监听
        scoreRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                setNet(NET_LIST);
            }
        });
    }

    private void setEmptyView(){
        if (baseListScore==null||baseListScore.size()==0){
//            if (emptyView == null){
                emptyView = new AppListEmptyView(getContext());
                emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//            }
            adapter.setEmptyView(emptyView);
        }
    }
    private void setFailedView(){
        //加载失败 点击重试
        failedView = new AppListFailedView(getContext());
        failedView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        adapter.setEmptyView(failedView);

    }

    private void setLoadMore() {
        isLoadMore = false;

        if (mainScoreInfoData.isNoMore()) {
            adapter.addData(listScore);
            //数据全部加载完毕
            adapter.loadMoreEnd();
        } else {
            //成功获取更多数据
            adapter.addData(listScore);
            adapter.loadMoreComplete();
        }
    }

    private void setRefresh() {
        adapter.removeAllHeaderView();
        isRefresh = false;
        adapter.setNewData(listScore);
        setPrimacy();
        if (scoreRefreshLayout!=null){
            scoreRefreshLayout.setRefreshing(false);
        }
        ToastUtils.showShort(getContext(),"刷新成功！");
    }
}
