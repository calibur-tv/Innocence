package com.riuir.calibur.ui.home.image;

import android.content.Intent;
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
import com.riuir.calibur.data.trending.ImageShowInfoPrimacy;
import com.riuir.calibur.data.trending.TrendingShowInfoCommentMain;
import com.riuir.calibur.net.ApiGet;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.Drama.DramaActivity;
import com.riuir.calibur.ui.home.adapter.CommentAdapter;
import com.riuir.calibur.ui.home.adapter.MyLoadMoreView;
import com.riuir.calibur.ui.home.card.CardChildCommentActivity;
import com.riuir.calibur.assistUtils.activityUtils.LoginUtils;
import com.riuir.calibur.ui.home.card.CardShowInfoActivity;
import com.riuir.calibur.ui.widget.BangumiForShowView;
import com.riuir.calibur.ui.widget.ReplyAndCommentView;
import com.riuir.calibur.ui.widget.TrendingLikeFollowCollectionView;
import com.riuir.calibur.ui.widget.emptyView.AppListEmptyView;
import com.riuir.calibur.ui.widget.emptyView.AppListFailedView;
import com.riuir.calibur.ui.widget.popup.AppHeaderPopupWindows;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.GlideUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageShowInfoActivity extends BaseActivity {

    @BindView(R.id.image_show_info_list_view)
    RecyclerView imageShowInfoListView;
    @BindView(R.id.image_show_info_back_btn)
    ImageView backBtn;
    @BindView(R.id.image_show_info_refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.image_show_info_comment_view)
    ReplyAndCommentView commentView;
    @BindView(R.id.image_show_info_list_header_card_more)
    AppHeaderPopupWindows headerMore;

    private ImageShowInfoPrimacy.ImageShowInfoPrimacyData primacyData;
    private TrendingShowInfoCommentMain.TrendingShowInfoCommentMainData commentMainData;
    private TrendingShowInfoCommentMain.TrendingShowInfoCommentMainData baseCommentMainData;
    private List<TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList> baseCommentMainList;
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
    LinearLayout headerImageLayout;
    TextView noImgText;
    TextView headerCardTitle;
    ImageView headerUserIcon;
    TextView headerUserName;
    TextView headerTime;


    TrendingLikeFollowCollectionView trendingLFCView;

    BangumiForShowView headerBangumiView;

    private int imageID;

    private static final int NET_STATUS_PRIMACY = 0;
    private static final int NET_STATUS_MAIN_COMMENT = 1;

    private Call<ImageShowInfoPrimacy> primacyCall;
    private Call<TrendingShowInfoCommentMain> commentMainCall;

    AppListFailedView failedView;
    AppListEmptyView emptyView;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_image_show_info;
    }

    @Override
    protected void onInit() {
        Intent intent = getIntent();
        imageID = intent.getIntExtra("imageID",0);
        setBackBtn();
        setAdapter();
        isFirstLoad = true;
        refreshLayout.setRefreshing(true);
        setNet(NET_STATUS_MAIN_COMMENT);
    }

    @Override
    public void onDestroy() {
        if (primacyCall!=null){
            primacyCall.cancel();
        }
        if (commentMainCall!=null){
            commentMainCall.cancel();
        }
        super.onDestroy();
    }

    private void setNet(int NET_STATUS){
        ApiGet mApiGet;
        if (Constants.ISLOGIN){
            mApiGet = apiGetHasAuth;
        }else {
            mApiGet = apiGet;
        }

        if (NET_STATUS == NET_STATUS_PRIMACY){
            primacyCall = mApiGet.getCallImageShowPrimacy(imageID);
            mApiGet.getCallImageShowPrimacy(imageID).enqueue(new Callback<ImageShowInfoPrimacy>() {
                @Override
                public void onResponse(Call<ImageShowInfoPrimacy> call, Response<ImageShowInfoPrimacy> response) {
                    if (response!=null&&response.isSuccessful()){
                        primacyData = response.body().getData();
                        //两次网络请求都完成后开始加载数据
                        if (isFirstLoad){
                            isFirstLoad = false;
                            if (refreshLayout!=null&&imageShowInfoListView!=null){
                                refreshLayout.setRefreshing(false);
                                setAdapter();
                                setPrimacyView();
                                setEmptyView();
                            }
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
                            ToastUtils.showShort(ImageShowInfoActivity.this,info.getMessage());
                            LoginUtils.ReLogin(ImageShowInfoActivity.this);
                        }else if (info.getCode() == 40401){
                            ToastUtils.showShort(ImageShowInfoActivity.this,info.getMessage());
                        }
                        if (isFirstLoad){
                            isFirstLoad = false;
                        }
                        if (isRefresh){
                            isRefresh = false;
                        }
                        refreshLayout.setRefreshing(false);
                        setFailedView();
                    }else {
                        ToastUtils.showShort(ImageShowInfoActivity.this,"未知错误出现了！");
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

                @Override
                public void onFailure(Call<ImageShowInfoPrimacy> call, Throwable t) {
                    if (call.isCanceled()){
                    }else {
                        ToastUtils.showShort(ImageShowInfoActivity.this,"请检查您的网络！");
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
            commentMainCall = mApiGet.getCallMainComment("image",imageID,fetchId,onlySeeMaster);
            commentMainCall.enqueue(new Callback<TrendingShowInfoCommentMain>() {
                @Override
                public void onResponse(Call<TrendingShowInfoCommentMain> call, Response<TrendingShowInfoCommentMain> response) {
                    if (response!=null&&response.body()!=null&&response.body().getCode()==0){
                        commentMainData = response.body().getData();
                        commentMainList = response.body().getData().getList();
                        if (isFirstLoad){
//                            isFirstLoad = false;
                            baseCommentMainData = response.body().getData();
                            baseCommentMainList = response.body().getData().getList();
                            //第一次网络请求结束后 开始第二次网络请求
                            setNet(NET_STATUS_PRIMACY);
                        }
                        if (isLoadMore){
                            setLoadMore();
                        }
                        if (isRefresh){
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
                            ToastUtils.showShort(ImageShowInfoActivity.this,info.getMessage());
                        }else if (info.getCode() == 40003){
                            ToastUtils.showShort(ImageShowInfoActivity.this,info.getMessage());
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
                        setFailedView();
                    }else {
                        ToastUtils.showShort(ImageShowInfoActivity.this,"未知错误出现了！");
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

                @Override
                public void onFailure(Call<TrendingShowInfoCommentMain> call, Throwable t) {
                    if (call.isCanceled()){
                    }else {
                        ToastUtils.showShort(ImageShowInfoActivity.this,"请检查您的网络！");
                        LogUtils.d("AppNetErrorMessage","image show t = "+t.getMessage());
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
        commentAdapter = new CommentAdapter(R.layout.card_show_info_list_comment_item,baseCommentMainList,ImageShowInfoActivity.this,apiPost,CommentAdapter.TYPE_IMAGE);
        imageShowInfoListView.setLayoutManager(new LinearLayoutManager(ImageShowInfoActivity.this));
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
        commentAdapter.disableLoadMoreIfNotFullPage(imageShowInfoListView);


        imageShowInfoListView.setAdapter(commentAdapter);
        commentAdapter.setHeaderAndEmpty(true);


    }

    private void setCommentView() {
        commentView.setStatus(ReplyAndCommentView.STATUS_MAIN_COMMENT);
        commentView.setApiPost(apiPost);
        commentView.setCommentAdapter(commentAdapter);
        commentView.setFromUserName("");
        commentView.setId(imageID);
        commentView.setTitleId(primacyData.getUser().getId());
        commentView.setType(ReplyAndCommentView.TYPE_IMAGE);
        commentView.setTargetUserId(0);
        commentView.setNetAndListener();
    }

    private void setPrimacyView() {
        headerLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.image_show_info_list_header_view,null);

        headerCardTitle = headerLayout.findViewById(R.id.image_show_info_list_header_card_title);
        headerUserIcon = headerLayout.findViewById(R.id.image_show_info_list_header_user_icon);
        headerUserName = headerLayout.findViewById(R.id.image_show_info_list_header_user_name);
        headerTime = headerLayout.findViewById(R.id.image_show_info_list_header_time);

        headerImageLayout = headerLayout.findViewById(R.id.image_show_info_list_header_card_image_layout);
        noImgText = headerLayout.findViewById(R.id.image_show_info_list_header_card_no_img_text);


        /**
         * 先设置trendingLFCView的各项属性，再设置开启监听和网络
         * 顺序不可颠倒
         */
        trendingLFCView = headerLayout.findViewById(R.id.image_show_info_list_header_lfc_view);
        trendingLFCView.setType("image");
        trendingLFCView.setApiPost(apiPost);
        trendingLFCView.setID(imageID);
        trendingLFCView.setLiked(primacyData.isLiked());
        trendingLFCView.setCollected(primacyData.isMarked());
        trendingLFCView.setRewarded(primacyData.isRewarded());
        trendingLFCView.setIsCreator(primacyData.isIs_creator());
        trendingLFCView.startListenerAndNet();

        //所属番剧操作
        headerBangumiView = headerLayout.findViewById(R.id.image_show_info_list_header_bangumi_view);
        LogUtils.d("cardShowHeader","header Data = "+primacyData.toString());
        headerBangumiView.setName(primacyData.getBangumi().getName());
        headerBangumiView.setSummary(primacyData.getBangumi().getSummary());
        headerBangumiView.setImageView(ImageShowInfoActivity.this,primacyData.getBangumi().getAvatar());

        headerCardTitle.setText(primacyData.getName());
        GlideUtils.loadImageViewCircle(ImageShowInfoActivity.this,primacyData.getUser().getAvatar(),headerUserIcon);
        headerUserName.setText(primacyData.getUser().getNickname());
        headerTime.setText( TimeUtils.HowLongTimeForNow(primacyData.getCreated_at()));



        headerBangumiView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ImageShowInfoActivity.this, DramaActivity.class);
                intent.putExtra("animeId",primacyData.getBangumi().getId());
                startActivity(intent);

            }
        });

        if (!primacyData.isIs_album()){
            //显示单张 封面
            ImageView primacyImageView = new ImageView(ImageShowInfoActivity.this);
            int height = GlideUtils.getImageHeightDp(ImageShowInfoActivity.this,
                    primacyData.getSource().getHeight(),primacyData.getSource().getWidth(),24,1);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    height);
            params.setMargins(DensityUtils.dp2px(ImageShowInfoActivity.this,12),
                    DensityUtils.dp2px(ImageShowInfoActivity.this,4),
                    DensityUtils.dp2px(ImageShowInfoActivity.this,12),
                    DensityUtils.dp2px(ImageShowInfoActivity.this,4));
            primacyImageView.setLayoutParams(params);
            primacyImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                primacyImageView.setTransitionName("ToPreviewImageActivity");
            }
            GlideUtils.loadImageView(ImageShowInfoActivity.this,
                    GlideUtils.setImageUrl(ImageShowInfoActivity.this,primacyData.getSource().getUrl(),GlideUtils.FULL_SCREEN),
                    primacyImageView);

            primacyImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String url = primacyData.getSource().getUrl();
                    PerviewImageUtils.startPerviewImage(ImageShowInfoActivity.this,previewImagesList,url,view);
                }
            });

            headerImageLayout.addView(primacyImageView);
            noImgText.setVisibility(View.GONE);
        }else {
            //显示多张
            if (primacyData.getImages()!=null&&primacyData.getImages().size()!=0){

                for (int i = 0; i < primacyData.getImages().size(); i++) {
                    ImageShowInfoPrimacy.ImageShowInfoPrimacyImages primacyImage = primacyData.getImages().get(i);
                    ImageView primacyImageView = new ImageView(ImageShowInfoActivity.this);
                    int height = GlideUtils.getImageHeightDp(ImageShowInfoActivity.this,
                            primacyData.getImages().get(i).getHeight(),primacyData.getImages().get(i).getWidth(),24,1);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            height);
                    params.setMargins(DensityUtils.dp2px(ImageShowInfoActivity.this,12),
                            DensityUtils.dp2px(ImageShowInfoActivity.this,4),
                            DensityUtils.dp2px(ImageShowInfoActivity.this,12),
                            DensityUtils.dp2px(ImageShowInfoActivity.this,4));
                    primacyImageView.setLayoutParams(params);
                    primacyImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        primacyImageView.setTransitionName("ToPreviewImageActivity");
                    }

                    GlideUtils.loadImageView(ImageShowInfoActivity.this,
                            GlideUtils.setImageUrl(ImageShowInfoActivity.this,primacyData.getImages().get(i).getUrl(),GlideUtils.FULL_SCREEN),
                            primacyImageView);

                    final int finalI = i;
                    primacyImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String url = primacyData.getImages().get(finalI).getUrl();
                            PerviewImageUtils.startPerviewImage(ImageShowInfoActivity.this,previewImagesList,url,view);
                        }
                    });

                    headerImageLayout.addView(primacyImageView);
                }

                noImgText.setVisibility(View.GONE);
            }else {
                noImgText.setVisibility(View.VISIBLE);
            }
        }




        commentAdapter.addHeaderView(headerLayout);

        RecyclerViewUtils.setScorllToTop(imageShowInfoListView);

        setPreviewImageUrlList();

        setCommentView();
        setListener();
    }

    private void setPreviewImageUrlList() {
        if (previewImagesList == null||previewImagesList.size() == 0){
            previewImagesList = new ArrayList<>();
        }
        if (primacyData.getImages()!=null&&primacyData.getImages().size()!=0){

            for (int i = 0; i <primacyData.getImages().size() ; i++) {
                previewImagesList.add(primacyData.getImages().get(i).getUrl());
            }
        }
        if (primacyData.getSource()!=null&&primacyData.getSource().getUrl()!=null&&
            primacyData.getSource().getUrl().length()!=0){
            previewImagesList.add(primacyData.getSource().getUrl());
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
                isRefresh =true;
                setNet(NET_STATUS_MAIN_COMMENT);
            }
        });

        headerUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserMainUtils.toUserMainActivity(ImageShowInfoActivity.this,primacyData.getUser().getId(),primacyData.getUser().getZone());
            }
        });

        commentAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                List<TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList> dataList =  adapter.getData();

                if (view.getId() == R.id.card_show_info_list_comment_item_user_icon){
                    UserMainUtils.toUserMainActivity(ImageShowInfoActivity.this,
                            dataList.get(position).getFrom_user_id(),dataList.get(position).getFrom_user_zone());
                }
                if (view.getId() == R.id.card_show_info_list_comment_item_reply){
                    int commentId = dataList.get(position).getId();
                    Intent intent = new Intent(ImageShowInfoActivity.this,CardChildCommentActivity.class);
                    intent.putExtra("id",commentId);
                    intent.putExtra("mainComment",dataList.get(position));
                    intent.putExtra("type","image");
                    startActivity(intent);
                }
            }
        });
        commentAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                List<TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList> dataList =  adapter.getData();
                int commentId = dataList.get(position).getId();
                Intent intent = new Intent(ImageShowInfoActivity.this,CardChildCommentActivity.class);
                intent.putExtra("id",commentId);
                intent.putExtra("mainComment",dataList.get(position));
                intent.putExtra("type","image");
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
        }, imageShowInfoListView);
    }

    private void setHeaderMore() {
        headerMore.setReportModelTag(AppHeaderPopupWindows.IMAGE,primacyData.getId());
        headerMore.setDeleteLayout(AppHeaderPopupWindows.IMAGE,primacyData.getId(),
                primacyData.getUser().getId(),apiPost);
        headerMore.initOnlySeeMaster(AppHeaderPopupWindows.IMAGE);
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
    }

    private void setEmptyView(){
        if (baseCommentMainList==null||baseCommentMainList.size()==0){
            if (emptyView == null){
                emptyView = new AppListEmptyView(ImageShowInfoActivity.this);
                emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
            commentAdapter.setEmptyView(emptyView);


        }
    }

    private void setFailedView(){
        //加载失败 点击重试
        if (failedView == null){
            failedView = new AppListFailedView(ImageShowInfoActivity.this);
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
        ToastUtils.showShort(ImageShowInfoActivity.this,"刷新成功！");
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
}
