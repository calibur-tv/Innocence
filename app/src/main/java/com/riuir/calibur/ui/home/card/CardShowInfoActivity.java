package com.riuir.calibur.ui.home.card;

import android.content.Intent;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import calibur.core.http.models.base.ResponseBean;
import calibur.core.http.models.comment.TrendingShowInfoCommentMain;
import calibur.core.http.models.followList.post.CardShowInfoPrimacy;
import calibur.core.http.observer.ObserverWrapper;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.DensityUtils;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.TimeUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.assistUtils.activityUtils.PreviewImageUtils;
import com.riuir.calibur.assistUtils.activityUtils.RecyclerViewUtils;
import com.riuir.calibur.assistUtils.activityUtils.UserMainUtils;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.Drama.DramaActivity;
import com.riuir.calibur.ui.home.adapter.CommentAdapter;
import com.riuir.calibur.ui.home.adapter.MyLoadMoreView;
import com.riuir.calibur.ui.home.comment.CommentDetailActivity;
import com.riuir.calibur.ui.widget.BangumiForShowView;
import com.riuir.calibur.ui.widget.TrendingLikeFollowCollectionView;
import com.riuir.calibur.ui.widget.emptyView.AppListEmptyView;
import com.riuir.calibur.ui.widget.emptyView.AppListFailedView;
import com.riuir.calibur.ui.widget.popup.AppHeaderPopupWindows;
import com.riuir.calibur.ui.widget.replyAndComment.ReplyAndCommentView;
import com.riuir.calibur.utils.GlideUtils;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Response;

public class CardShowInfoActivity extends BaseActivity {

    int cardID;

    private static final int NET_STATUS_PRIMACY = 0;
    private static final int NET_STATUS_MAIN_COMMENT = 1;

    private CardShowInfoPrimacy primacyData;
    private TrendingShowInfoCommentMain commentMainData;
    private TrendingShowInfoCommentMain baseCommentMainData;
    private List<TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList> baseCommentMainList = new ArrayList<>();
    private List<TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList> commentMainList;
    private ArrayList<String> previewImagesList;

    private CommentAdapter commentAdapter;

    LinearLayout headerLayout;
    LinearLayout headerImageLayout;
    LinearLayout headerTagLayout;
    TextView headerTag1;
    TextView headerTag2;
    TextView headerTag3;
    TextView headerCardTitle;
    ImageView headerUserIcon;
    TextView headerUserName;
    TextView headerCardInfo;

    TextView headerCardSeeCount;
    TextView headerCardContent;

    TrendingLikeFollowCollectionView trendingLFCView;

    BangumiForShowView headerBangumiView;

    CheckBox commentUpvoteCheckBox;

    int fetchId = 0;
    boolean isLoadMore = false;
    boolean isFirstLoad = false;
    boolean isRefresh = false;
    boolean isOnlySeeMaster = false;
    int onlySeeMaster = 0;


    @BindView(R.id.card_show_info_list_view)
    RecyclerView cardShowInfoListView;
    @BindView(R.id.card_show_info_back_btn)
    ImageView backBtn;
    @BindView(R.id.card_show_info_comment_view)
    ReplyAndCommentView commentView;
    @BindView(R.id.card_show_info_refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.card_show_info_list_header_card_more)
    AppHeaderPopupWindows headerCardMore;

    private int primacyId;


    AppListFailedView failedView;
    AppListEmptyView emptyView;

    private static CardShowInfoActivity instance;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_card_show_info;
    }

    @Override
    protected void onInit() {
        instance = this;
        Intent intent = getIntent();
        cardID = intent.getIntExtra("cardID",0);
        LogUtils.d("cardInfo","cardID = "+cardID);
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

    private void setNet(int NET_STATUS) {

        if (NET_STATUS == NET_STATUS_PRIMACY){
            apiService.getCallCardShowPrimacy(cardID)
                    .compose(Rx2Schedulers.applyObservableAsync())
                    .subscribe(new ObserverWrapper<CardShowInfoPrimacy>(){
                        @Override
                        public void onSuccess(CardShowInfoPrimacy cardShowInfoPrimacy) {
                            primacyData = cardShowInfoPrimacy;
                            if (isFirstLoad){
                                //两次网络请求都完成后开始加载数据
                                isFirstLoad = false;
                                if (refreshLayout!=null&&cardShowInfoListView!=null){
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
                                refreshLayout.setRefreshing(false);
                                if (isFirstLoad){
                                    isFirstLoad = false;
                                }
                                if (isRefresh){
                                    isRefresh = false;
                                }
                                setFailedView();
                            }
                        }
                    });
        }

        if (NET_STATUS == NET_STATUS_MAIN_COMMENT){
            setFetchID();
            apiService.getCallMainComment("post",cardID,fetchId,onlySeeMaster)
                    .compose(Rx2Schedulers.<Response<ResponseBean<TrendingShowInfoCommentMain>>>applyObservableAsync())
                    .subscribe(new ObserverWrapper<TrendingShowInfoCommentMain>() {
                        @Override
                        public void onSuccess(TrendingShowInfoCommentMain trendingShowInfoCommentMain) {
                            commentMainData = trendingShowInfoCommentMain;
                            commentMainList = trendingShowInfoCommentMain.getList();
                            if (isFirstLoad){

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

    private void setEmptyView(){
        if (baseCommentMainList==null||baseCommentMainList.size()==0){
            emptyView = new AppListEmptyView(CardShowInfoActivity.this);
            emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            commentAdapter.setEmptyView(emptyView);
        }
    }

    private void setFailedView(){
        //加载失败 点击重试
        failedView = new AppListFailedView(CardShowInfoActivity.this);
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

    private void setRefresh(){
        isRefresh = false;
        refreshLayout.setRefreshing(false);
        commentAdapter.removeAllHeaderView();
        commentAdapter.setNewData(commentMainList);
        if (primacyData!=null){
            setPrimacyView();
        }
        ToastUtils.showShort(CardShowInfoActivity.this,"刷新成功！");
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

    private void setPrimacyView() {
        headerLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.card_show_info_list_header_view,null);

        headerCardTitle = headerLayout.findViewById(R.id.card_show_info_list_header_card_title);
        headerUserIcon = headerLayout.findViewById(R.id.card_show_info_list_header_user_icon);
        headerUserName = headerLayout.findViewById(R.id.card_show_info_list_header_user_name);
        headerCardInfo = headerLayout.findViewById(R.id.card_show_info_list_header_card_info);
        headerCardSeeCount = headerLayout.findViewById(R.id.card_show_info_list_header_card_see_count);
        headerCardContent = headerLayout.findViewById(R.id.card_show_info_list_header_card_content);
        headerImageLayout = headerLayout.findViewById(R.id.card_show_info_list_header_card_image_layout);

        headerTagLayout = headerLayout.findViewById(R.id.card_show_info_list_header_card_tag_layout);
        headerTag1 = headerLayout.findViewById(R.id.card_show_info_list_header_card_tag_1);
        headerTag2 = headerLayout.findViewById(R.id.card_show_info_list_header_card_tag_2);
        headerTag3 = headerLayout.findViewById(R.id.card_show_info_list_header_card_tag_3);

        if (primacyData.getPost().getTags()!=null&&primacyData.getPost().getTags().size()!=0){
            headerTagLayout.setVisibility(View.VISIBLE);
            if (primacyData.getPost().getTags().size()==1){
                headerTag1.setVisibility(View.VISIBLE);
                headerTag1.setText(primacyData.getPost().getTags().get(0).getName());
            }
            if (primacyData.getPost().getTags().size()==2){
                headerTag1.setVisibility(View.VISIBLE);
                headerTag2.setVisibility(View.VISIBLE);
                headerTag1.setText(primacyData.getPost().getTags().get(0).getName());
                headerTag2.setText(primacyData.getPost().getTags().get(1).getName());
            }
            if (primacyData.getPost().getTags().size()==3){
                headerTag1.setVisibility(View.VISIBLE);
                headerTag2.setVisibility(View.VISIBLE);
                headerTag3.setVisibility(View.VISIBLE);
                headerTag1.setText(primacyData.getPost().getTags().get(0).getName());
                headerTag2.setText(primacyData.getPost().getTags().get(1).getName());
                headerTag3.setText(primacyData.getPost().getTags().get(2).getName());
            }
        }else {
            headerTag1.setVisibility(View.GONE);
            headerTag2.setVisibility(View.GONE);
            headerTag3.setVisibility(View.GONE);
            headerTagLayout.setVisibility(View.GONE);
        }

        trendingLFCView = headerLayout.findViewById(R.id.card_show_info_list_header_lfc_view);
        trendingLFCView.setType("post");
        trendingLFCView.setApiPost(apiPost);
        trendingLFCView.setID(cardID);
        trendingLFCView.setLiked(primacyData.getPost().isLiked());
        trendingLFCView.setCollected(primacyData.getPost().isMarked());
        trendingLFCView.setRewarded(primacyData.getPost().isRewarded());
        trendingLFCView.setIsCreator(primacyData.getPost().isIs_creator());
        trendingLFCView.setOnLFCNetFinish(new TrendingLikeFollowCollectionView.OnLFCNetFinish() {
            @Override
            public void onLikedFinish(boolean isLiked) {
                if (commentView!=null){
                    commentView.setLiked(isLiked);
                }
            }

            @Override
            public void onCollectedFinish(boolean isMarked) {
                if (commentView!=null){
                    commentView.setMarked(isMarked);
                }
            }

            @Override
            public void onRewardFinish() {
                if (commentView!=null){
                    commentView.setRewarded(true);
                }
            }
        });
        trendingLFCView.startListenerAndNet();


        headerBangumiView = headerLayout.findViewById(R.id.card_show_info_list_header_bangumi_view);

        headerCardTitle.setText(primacyData.getPost().getTitle());
        GlideUtils.loadImageViewCircle(CardShowInfoActivity.this,primacyData.getUser().getAvatar(),headerUserIcon);
        headerUserName.setText(primacyData.getUser().getNickname().replace("\n",""));
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
        headerImageLayout.removeAllViews();
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
                primacyImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                GlideUtils.loadImageView(CardShowInfoActivity.this,
                        GlideUtils.setImageUrl(CardShowInfoActivity.this,
                                primacyData.getPost().getImages().get(i).getUrl(),
                                GlideUtils.FULL_SCREEN,
                                primacyData.getPost().getImages().get(i).getHeight()),
                        primacyImageView);

                final int finalI = i;
                primacyImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String url = primacyData.getPost().getImages().get(finalI).getUrl();
                        PreviewImageUtils.startPreviewImage(CardShowInfoActivity.this,previewImagesList,url,view);
                    }
                });

                headerImageLayout.addView(primacyImageView);

            }
        }

        commentAdapter.addHeaderView(headerLayout);
//        cardShowInfoListView.getLayoutManager().smoothScrollToPosition(cardShowInfoListView,null,0);
        RecyclerViewUtils.setScorllToTop(cardShowInfoListView);

        setPreviewImageUrlList();

        setCommentView();
        setListener();
    }

    private void setPreviewImageUrlList() {
        if (previewImagesList == null){
            previewImagesList = new ArrayList<>();
        }
        if (previewImagesList.size()!=0){
            previewImagesList.clear();
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
                        PreviewImageUtils.startPreviewImage(CardShowInfoActivity.this,previewImagesList,url,view);
                    }
                }
                if (view.getId() == R.id.card_show_info_list_comment_item_image2){
                    if (view.getVisibility() == View.VISIBLE){
                        String url = dataList.get(position).getImages().get(1).getUrl();
                        PreviewImageUtils.startPreviewImage(CardShowInfoActivity.this,previewImagesList,url,view);
                    }
                }
                if (view.getId() == R.id.card_show_info_list_comment_item_image3){
                    if (view.getVisibility() == View.VISIBLE){
                        String url = dataList.get(position).getImages().get(2).getUrl();
                        PreviewImageUtils.startPreviewImage(CardShowInfoActivity.this,previewImagesList,url,view);
                    }
                }
                if (view.getId() == R.id.card_show_info_list_comment_item_image4){
                    if (view.getVisibility() == View.VISIBLE){
                        String url = dataList.get(position).getImages().get(3).getUrl();
                        PreviewImageUtils.startPreviewImage(CardShowInfoActivity.this,previewImagesList,url,view);
                    }
                }
                if (view.getId() == R.id.card_show_info_list_comment_item_image5){
                    if (view.getVisibility() == View.VISIBLE){
                        String url = dataList.get(position).getImages().get(4).getUrl();
                        PreviewImageUtils.startPreviewImage(CardShowInfoActivity.this,previewImagesList,url,view);
                    }
                }
                if (view.getId() == R.id.card_show_info_list_comment_item_image6){
                    if (view.getVisibility() == View.VISIBLE){
                        String url = dataList.get(position).getImages().get(5).getUrl();
                        PreviewImageUtils.startPreviewImage(CardShowInfoActivity.this,previewImagesList,url,view);
                    }
                }
                if (view.getId() == R.id.card_show_info_list_comment_item_image7){
                    if (view.getVisibility() == View.VISIBLE){
                        String url = dataList.get(position).getImages().get(6).getUrl();
                        PreviewImageUtils.startPreviewImage(CardShowInfoActivity.this,previewImagesList,url,view);
                    }
                }
                if (view.getId() == R.id.card_show_info_list_comment_item_image8){
                    if (view.getVisibility() == View.VISIBLE){
                        String url = dataList.get(position).getImages().get(7).getUrl();
                        PreviewImageUtils.startPreviewImage(CardShowInfoActivity.this,previewImagesList,url,view);
                    }
                }
                if (view.getId() == R.id.card_show_info_list_comment_item_image9){
                    if (view.getVisibility() == View.VISIBLE){
                        String url = dataList.get(position).getImages().get(8).getUrl();
                        PreviewImageUtils.startPreviewImage(CardShowInfoActivity.this,previewImagesList,url,view);
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
        commentAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                List<TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList> dataList =  adapter.getData();
                int commentId = dataList.get(position).getId();
                Intent intent = new Intent(CardShowInfoActivity.this,CommentDetailActivity.class);
                intent.putExtra("id",commentId);
                intent.putExtra("mainComment",dataList.get(position));
                intent.putExtra("type","post");
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
        }, cardShowInfoListView);
    }

    private void setHeaderMore() {
        headerCardMore.setReportModelTag(AppHeaderPopupWindows.POST,primacyData.getPost().getId());
        headerCardMore.setShareLayout(primacyData.getPost().getTitle(),AppHeaderPopupWindows.POST,primacyData.getPost().getId(),"");
        headerCardMore.setDeleteLayout(AppHeaderPopupWindows.POST,primacyData.getPost().getId(),
                primacyData.getUser().getId(),primacyData.getUser().getId(),apiPost);
        headerCardMore.initOnlySeeMaster(AppHeaderPopupWindows.POST);
        headerCardMore.setOnlySeeMasterClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOnlySeeMaster){
                    isOnlySeeMaster = false;
                    onlySeeMaster = 0;
                }else {
                    isOnlySeeMaster = true;
                    onlySeeMaster = 1;
                }
                headerCardMore.setIsOnlySeeMaster(isOnlySeeMaster);
                //刷新
                isRefresh = true;
                setNet(NET_STATUS_MAIN_COMMENT);
            }
        });
        headerCardMore.setOnDeleteFinish(new AppHeaderPopupWindows.OnDeleteFinish() {
            @Override
            public void deleteFinish() {
                finish();
            }
        });
    }

    private void setAdapter() {

        if (primacyData!=null){
            primacyId = primacyData.getUser().getId();
        }
        commentAdapter = new CommentAdapter(R.layout.card_show_info_list_comment_item,baseCommentMainList,
                CardShowInfoActivity.this,apiPost,CommentAdapter.TYPE_POST,primacyId);
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
        commentAdapter.setHeaderAndEmpty(true);

    }

    private void setCommentView() {
        commentView.setStatus(ReplyAndCommentView.STATUS_MAIN_COMMENT);
        commentView.setApiPost(apiPost);
        commentView.setCommentAdapter(commentAdapter);
        commentView.setFromUserName("");
        commentView.setId(cardID);
        commentView.setTitleId(primacyData.getUser().getId());
        commentView.setType(ReplyAndCommentView.TYPE_POST);
        commentView.setTargetUserId(0);
        commentView.setIs_creator(primacyData.getPost().isIs_creator());
        commentView.setLiked(primacyData.getPost().isLiked());
        commentView.setRewarded(primacyData.getPost().isRewarded());
        commentView.setMarked(primacyData.getPost().isMarked());
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

    public CommentAdapter getCommentAdapter(){
        return commentAdapter;
    }

    public static CardShowInfoActivity getInstance(){
        return instance;
    }

}
