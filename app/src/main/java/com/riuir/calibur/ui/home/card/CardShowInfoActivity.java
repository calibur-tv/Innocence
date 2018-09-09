package com.riuir.calibur.ui.home.card;

import android.content.Intent;
import android.os.Build;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.DensityUtils;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.activityUtils.PerviewImageUtils;
import com.riuir.calibur.assistUtils.activityUtils.RecyclerViewUtils;
import com.riuir.calibur.assistUtils.TimeUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.assistUtils.activityUtils.UserMainUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.trending.TrendingShowInfoCommentMain;
import com.riuir.calibur.data.trending.CardShowInfoPrimacy;
import com.riuir.calibur.net.ApiGet;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.Drama.DramaActivity;
import com.riuir.calibur.ui.home.adapter.CommentAdapter;
import com.riuir.calibur.ui.home.adapter.MyLoadMoreView;
import com.riuir.calibur.assistUtils.activityUtils.LoginUtils;
import com.riuir.calibur.ui.widget.BangumiForShowView;
import com.riuir.calibur.ui.widget.ReplyAndCommentView;
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

public class CardShowInfoActivity extends BaseActivity {

    int cardID;

    private static final int NET_STATUS_PRIMACY = 0;
    private static final int NET_STATUS_MAIN_COMMENT = 1;


    private CardShowInfoPrimacy.CardShowInfoPrimacyData primacyData;
    private TrendingShowInfoCommentMain.TrendingShowInfoCommentMainData commentMainData;
    private TrendingShowInfoCommentMain.TrendingShowInfoCommentMainData baseCommentMainData;
    private List<TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList> baseCommentMainList = new ArrayList<>();
    private List<TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList> commentMainList;
    private ArrayList<String> previewImagesList;

    private CommentAdapter commentAdapter;

    LinearLayout headerLayout;
    LinearLayout headerImageLayout;
    TextView headerCardTitle;
    ImageView headerUserIcon;
    TextView headerUserName;
    TextView headerCardInfo;
    TextView headerCardMore;
    TextView headerCardSeeCount;
    TextView headerCardContent;

    TrendingLikeFollowCollectionView trendingLFCView;

    BangumiForShowView headerBangumiView;

    CheckBox commentUpvoteCheckBox;

    int fetchId = 0;
    boolean isLoadMore = false;
    boolean isFirstLoad = false;
    boolean isRefresh = false;


    @BindView(R.id.card_show_info_list_view)
    RecyclerView cardShowInfoListView;
    @BindView(R.id.card_show_info_back_btn)
    ImageView backBtn;
    @BindView(R.id.card_show_info_comment_view)
    ReplyAndCommentView commentView;
    @BindView(R.id.card_show_info_refresh_layout)
    SwipeRefreshLayout refreshLayout;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_card_show_info;
    }

    @Override
    protected void onInit() {
        Intent intent = getIntent();
        cardID = intent.getIntExtra("cardID",0);
        LogUtils.d("cardInfo","cardID = "+cardID);
        LogUtils.d("cardInfo","userToken = "+Constants.AUTH_TOKEN);
        setBackBtn();
        setAdapter();
        isFirstLoad = true;
        refreshLayout.setRefreshing(true);
        setNet(NET_STATUS_MAIN_COMMENT);
    }

    private void setNet(int NET_STATUS) {
        ApiGet mApiGet;
        LogUtils.d("cardShow","isLogin = "+Constants.ISLOGIN);
        if (Constants.ISLOGIN){
            mApiGet = apiGetHasAuth;
        }else {
            mApiGet = apiGet;
        }
        if (NET_STATUS == NET_STATUS_PRIMACY){

            mApiGet.getCallCardShowPrimacy(cardID).enqueue(new Callback<CardShowInfoPrimacy>() {
                @Override
                public void onResponse(Call<CardShowInfoPrimacy> call, Response<CardShowInfoPrimacy> response) {
                    if (response!=null&&response.body()!=null&&response.isSuccessful()){
                        primacyData = response.body().getData();
                        if (isFirstLoad){
                            //两次网络请求都完成后开始加载数据
                            isFirstLoad = false;
                            setAdapter();
                            setPrimacyView();
                            refreshLayout.setRefreshing(false);
                        }
                        if (isRefresh){

                            setRefresh();
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
                            ToastUtils.showShort(CardShowInfoActivity.this,info.getMessage());
                            LoginUtils.ReLogin(CardShowInfoActivity.this);
                        }else if (info.getCode() == 40401){
                            ToastUtils.showShort(CardShowInfoActivity.this,info.getMessage());
                        }
                        if (isFirstLoad){
                            isFirstLoad = false;
                        }
                        if (isRefresh){
                            isRefresh = false;
                        }
                        refreshLayout.setRefreshing(false);
                    }else {
                        ToastUtils.showShort(CardShowInfoActivity.this,"未知错误出现了！");
                        refreshLayout.setRefreshing(false);
                        if (isFirstLoad){
                            isFirstLoad = false;
                        }
                        if (isRefresh){
                            isRefresh = false;
                        }
                    }
                }

                @Override
                public void onFailure(Call<CardShowInfoPrimacy> call, Throwable t) {
                    ToastUtils.showShort(CardShowInfoActivity.this,"网络异常，请稍后再试！");
                    refreshLayout.setRefreshing(false);
                    if (isFirstLoad){
                        isFirstLoad = false;
                    }
                    if (isRefresh){
                        isRefresh = false;
                    }
                }
            });
        }

        if (NET_STATUS == NET_STATUS_MAIN_COMMENT){
            setFetchID();
            mApiGet.getCallMainComment("post",cardID,fetchId).enqueue(new Callback<TrendingShowInfoCommentMain>() {
                @Override
                public void onResponse(Call<TrendingShowInfoCommentMain> call, Response<TrendingShowInfoCommentMain> response) {
                    if (response!=null&&response.body()!=null&&response.body().getCode()==0){
                        commentMainData = response.body().getData();
                        commentMainList = response.body().getData().getList();
                        if (isFirstLoad){

                            baseCommentMainData = response.body().getData();
                            baseCommentMainList = response.body().getData().getList();
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
                            ToastUtils.showShort(CardShowInfoActivity.this,info.getMessage());
                        }else if (info.getCode() == 40003){
                            ToastUtils.showShort(CardShowInfoActivity.this,info.getMessage());
                        }
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
                    }else {
                        ToastUtils.showShort(CardShowInfoActivity.this,"未知错误出现了！");
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
                    }
                }

                @Override
                public void onFailure(Call<TrendingShowInfoCommentMain> call, Throwable t) {
                    ToastUtils.showShort(CardShowInfoActivity.this,"未知错误出现了！");

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
                }
            });
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

    private void setRefresh(){
        isRefresh = false;
        refreshLayout.setRefreshing(false);
        commentAdapter.removeAllHeaderView();
        commentAdapter.setNewData(commentMainList);
        setPrimacyView();
        ToastUtils.showShort(CardShowInfoActivity.this,"刷新成功！");
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

    private void setPrimacyView() {
        headerLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.card_show_info_list_header_view,null);

        headerCardTitle = headerLayout.findViewById(R.id.card_show_info_list_header_card_title);
        headerUserIcon = headerLayout.findViewById(R.id.card_show_info_list_header_user_icon);
        headerUserName = headerLayout.findViewById(R.id.card_show_info_list_header_user_name);
        headerCardInfo = headerLayout.findViewById(R.id.card_show_info_list_header_card_info);
        headerCardMore = headerLayout.findViewById(R.id.card_show_info_list_header_card_more);
        headerCardSeeCount = headerLayout.findViewById(R.id.card_show_info_list_header_card_see_count);
        headerCardContent = headerLayout.findViewById(R.id.card_show_info_list_header_card_content);
        headerImageLayout = headerLayout.findViewById(R.id.card_show_info_list_header_card_image_layout);

        trendingLFCView = headerLayout.findViewById(R.id.card_show_info_list_header_lfc_view);
        trendingLFCView.setType("post");
        trendingLFCView.setApiPost(apiPost);
        trendingLFCView.setID(cardID);
        trendingLFCView.setLiked(primacyData.getPost().isLiked());
        trendingLFCView.setCollected(primacyData.getPost().isMarked());
        trendingLFCView.setRewarded(primacyData.getPost().isRewarded());
        trendingLFCView.setIsCreator(primacyData.getPost().isIs_creator());
        trendingLFCView.startListenerAndNet();


        headerBangumiView = headerLayout.findViewById(R.id.card_show_info_list_header_bangumi_view);

        headerCardTitle.setText(primacyData.getPost().getTitle());
        GlideUtils.loadImageViewCircle(CardShowInfoActivity.this,primacyData.getUser().getAvatar(),headerUserIcon);
        headerUserName.setText(primacyData.getUser().getNickname());
        int alllCount = primacyData.getPost().getComment_count()+1;
        headerCardInfo.setText("第1楼·共"+alllCount+"楼·"+TimeUtils.HowLongTimeForNow(primacyData.getPost().getCreated_at()));
        headerCardSeeCount.setText(primacyData.getPost().getView_count()+"");
        headerCardContent.setText(Html.fromHtml(primacyData.getPost().getContent()));

        //所属番剧操作
        LogUtils.d("cardShowHeader","header Data = "+primacyData.toString());
        headerBangumiView.setName(primacyData.getBangumi().getName());
        headerBangumiView.setSummary(primacyData.getBangumi().getSummary());
        headerBangumiView.setImageView(CardShowInfoActivity.this,primacyData.getBangumi().getAvatar());

        headerBangumiView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CardShowInfoActivity.this, DramaActivity.class);
                intent.putExtra("animeId",primacyData.getBangumi().getId());
                startActivity(intent);

            }
        });






        LogUtils.d("cardShow","isliked = "+primacyData.getPost().isLiked()+"\n isMarked = "+primacyData.getPost().isMarked());

        if (primacyData.getPost().getImages()!=null&&primacyData.getPost().getImages().size()!=0){

            for (int i = 0; i < primacyData.getPost().getImages().size(); i++) {
                CardShowInfoPrimacy.CardShowInfoPrimacyImages primacyImage = primacyData.getPost().getImages().get(i);
                ImageView primacyImageView = new ImageView(CardShowInfoActivity.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        GlideUtils.getImageHeightDp(CardShowInfoActivity.this,
                                Integer.parseInt(primacyData.getPost().getImages().get(i).getHeight()),
                                Integer.parseInt(primacyData.getPost().getImages().get(i).getWidth())
                        ,24,1));
                params.setMargins(DensityUtils.dp2px(CardShowInfoActivity.this,12),
                        DensityUtils.dp2px(CardShowInfoActivity.this,4),
                        DensityUtils.dp2px(CardShowInfoActivity.this,12),
                        DensityUtils.dp2px(CardShowInfoActivity.this,4));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    primacyImageView.setTransitionName("ToPreviewImageActivity");
                }
                primacyImageView.setLayoutParams(params);
                primacyImageView.setScaleType(ImageView.ScaleType.FIT_START);

                GlideUtils.loadImageView(CardShowInfoActivity.this,
                        GlideUtils.setImageUrl(CardShowInfoActivity.this,primacyData.getPost().getImages().get(i).getUrl(),GlideUtils.FULL_SCREEN),
                        primacyImageView);

                final int finalI = i;
                primacyImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String url = primacyData.getPost().getImages().get(finalI).getUrl();
                        PerviewImageUtils.startPerviewImage(CardShowInfoActivity.this,previewImagesList,url,view);
                    }
                });

                headerImageLayout.addView(primacyImageView);

            }
        }

        commentAdapter.addHeaderView(headerLayout);
//        cardShowInfoListView.getLayoutManager().smoothScrollToPosition(cardShowInfoListView,null,0);
        RecyclerViewUtils.setScorllToTop(cardShowInfoListView);

        setPreviewImageUrlList();

        setListener();
    }

    private void setPreviewImageUrlList() {
        if (previewImagesList == null){
            previewImagesList = new ArrayList<>();
        }

        for (int i = 0; i <primacyData.getPost().getPreview_images().size() ; i++) {
            previewImagesList.add(primacyData.getPost().getPreview_images().get(i).getUrl());
        }
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
                UserMainUtils.toUserMainActivity(CardShowInfoActivity.this,
                        primacyData.getUser().getId(),primacyData.getUser().getZone());
            }
        });

        commentAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                List<TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList> dataList =  adapter.getData();

                if (view.getId() == R.id.card_show_info_list_comment_item_user_icon){
                    UserMainUtils.toUserMainActivity(CardShowInfoActivity.this,
                            dataList.get(position).getFrom_user_id(),dataList.get(position).getFrom_user_zone());
                }

                if (view.getId() == R.id.card_show_info_list_comment_item_image1){
                    if (view.getVisibility() == View.VISIBLE){
                        String url = dataList.get(position).getImages().get(0).getUrl();
                        PerviewImageUtils.startPerviewImage(CardShowInfoActivity.this,previewImagesList,url,view);
                    }
                }
                if (view.getId() == R.id.card_show_info_list_comment_item_image2){
                    if (view.getVisibility() == View.VISIBLE){
                        String url = dataList.get(position).getImages().get(1).getUrl();
                        PerviewImageUtils.startPerviewImage(CardShowInfoActivity.this,previewImagesList,url,view);
                    }
                }
                if (view.getId() == R.id.card_show_info_list_comment_item_image3){
                    if (view.getVisibility() == View.VISIBLE){
                        String url = dataList.get(position).getImages().get(2).getUrl();
                        PerviewImageUtils.startPerviewImage(CardShowInfoActivity.this,previewImagesList,url,view);
                    }
                }
                if (view.getId() == R.id.card_show_info_list_comment_item_image4){
                    if (view.getVisibility() == View.VISIBLE){
                        String url = dataList.get(position).getImages().get(3).getUrl();
                        PerviewImageUtils.startPerviewImage(CardShowInfoActivity.this,previewImagesList,url,view);
                    }
                }
                if (view.getId() == R.id.card_show_info_list_comment_item_image5){
                    if (view.getVisibility() == View.VISIBLE){
                        String url = dataList.get(position).getImages().get(4).getUrl();
                        PerviewImageUtils.startPerviewImage(CardShowInfoActivity.this,previewImagesList,url,view);
                    }
                }
                if (view.getId() == R.id.card_show_info_list_comment_item_image6){
                    if (view.getVisibility() == View.VISIBLE){
                        String url = dataList.get(position).getImages().get(5).getUrl();
                        PerviewImageUtils.startPerviewImage(CardShowInfoActivity.this,previewImagesList,url,view);
                    }
                }
                if (view.getId() == R.id.card_show_info_list_comment_item_image7){
                    if (view.getVisibility() == View.VISIBLE){
                        String url = dataList.get(position).getImages().get(6).getUrl();
                        PerviewImageUtils.startPerviewImage(CardShowInfoActivity.this,previewImagesList,url,view);
                    }
                }
                if (view.getId() == R.id.card_show_info_list_comment_item_image8){
                    if (view.getVisibility() == View.VISIBLE){
                        String url = dataList.get(position).getImages().get(7).getUrl();
                        PerviewImageUtils.startPerviewImage(CardShowInfoActivity.this,previewImagesList,url,view);
                    }
                }
                if (view.getId() == R.id.card_show_info_list_comment_item_image9){
                    if (view.getVisibility() == View.VISIBLE){
                        String url = dataList.get(position).getImages().get(8).getUrl();
                        PerviewImageUtils.startPerviewImage(CardShowInfoActivity.this,previewImagesList,url,view);
                    }
                }

                if (view.getId() == R.id.card_show_info_list_comment_item_reply){
                    int commentId = dataList.get(position).getId();
                    Intent intent = new Intent(CardShowInfoActivity.this,CardChildCommentActivity.class);
                    intent.putExtra("id",commentId);
                    intent.putExtra("mainComment",dataList.get(position));
                    intent.putExtra("type","post");
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
        }, cardShowInfoListView);
    }

    private void setAdapter() {


        commentAdapter = new CommentAdapter(R.layout.card_show_info_list_comment_item,baseCommentMainList,CardShowInfoActivity.this,apiPost,CommentAdapter.TYPE_POST);
        if (cardShowInfoListView == null){
            cardShowInfoListView = findViewById(R.id.card_show_info_list_view);
        }
        cardShowInfoListView.setLayoutManager(new LinearLayoutManager(CardShowInfoActivity.this));
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
        commentAdapter.disableLoadMoreIfNotFullPage(cardShowInfoListView);


        cardShowInfoListView.setAdapter(commentAdapter);
        setCommentView();

    }

    private void setCommentView() {
        commentView.setStatus(ReplyAndCommentView.STATUS_MAIN_COMMENT);
        commentView.setApiPost(apiPost);
        commentView.setCommentAdapter(commentAdapter);
        commentView.setFromUserName("");
        commentView.setId(cardID);
        commentView.setType(ReplyAndCommentView.TYPE_POST);
        commentView.setTargetUserId(0);
        commentView.setNetAndListener();
    }
    @Override
    protected void handler(Message msg) {

    }




}
