package com.riuir.calibur.ui.home.image;

import android.content.Intent;
import android.os.Build;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
import com.riuir.calibur.ui.widget.BangumiForShowView;
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

public class ImageShowInfoActivity extends BaseActivity {

    @BindView(R.id.image_show_info_list_view)
    RecyclerView imageShowInfoListView;
    @BindView(R.id.image_show_info_back_btn)
    ImageView backBtn;

    private ImageShowInfoPrimacy.ImageShowInfoPrimacyData primacyData;
    private TrendingShowInfoCommentMain.TrendingShowInfoCommentMainData commentMainData;
    private TrendingShowInfoCommentMain.TrendingShowInfoCommentMainData baseCommentMainData;
    private List<TrendingShowInfoCommentMain.TrendingShowInfoCommentMainList> baseCommentMainList;
    private ArrayList<String> previewImagesList;

    int fetchId = 0;
    boolean isLoadMore = false;
    boolean isFirstLoad = false;

    private CommentAdapter commentAdapter;


    LinearLayout headerLayout;
    LinearLayout headerImageLayout;
    TextView headerCardTitle;
    ImageView headerUserIcon;
    TextView headerUserName;
    TextView headerTime;
    TextView headerCardMore;

    TrendingLikeFollowCollectionView trendingLFCView;

    BangumiForShowView headerBangumiView;

    private int imageID;

    private static final int NET_STATUS_PRIMACY = 0;
    private static final int NET_STATUS_MAIN_COMMENT = 1;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_image_show_info;
    }



    @Override
    protected void onInit() {
        Intent intent = getIntent();
        imageID = intent.getIntExtra("imageID",0);
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

        if (NET_STATUS == NET_STATUS_PRIMACY){
            mApiGet.getCallImageShowPrimacy(imageID).enqueue(new Callback<ImageShowInfoPrimacy>() {
                @Override
                public void onResponse(Call<ImageShowInfoPrimacy> call, Response<ImageShowInfoPrimacy> response) {
                    if (response!=null&&response.isSuccessful()){
                        primacyData = response.body().getData();
                        //两次网络请求都完成后开始加载数据
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
                            ToastUtils.showShort(ImageShowInfoActivity.this,info.getMessage());
                            LoginUtils.ReLogin(ImageShowInfoActivity.this);
                        }else if (info.getCode() == 40401){
                            ToastUtils.showShort(ImageShowInfoActivity.this,info.getMessage());
                        }
                    }else {
                        ToastUtils.showShort(ImageShowInfoActivity.this,"未知错误出现了！");
                    }
                }

                @Override
                public void onFailure(Call<ImageShowInfoPrimacy> call, Throwable t) {
                    ToastUtils.showShort(ImageShowInfoActivity.this,"请检查您的网络！");
                }
            });
        }
        if (NET_STATUS == NET_STATUS_MAIN_COMMENT){
            setFetchID();
            mApiGet.getCallMainComment("image",imageID,fetchId).enqueue(new Callback<TrendingShowInfoCommentMain>() {
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
                            ToastUtils.showShort(ImageShowInfoActivity.this,info.getMessage());
                        }else if (info.getCode() == 40003){
                            ToastUtils.showShort(ImageShowInfoActivity.this,info.getMessage());
                        }
                        if (isLoadMore){
                            commentAdapter.loadMoreFail();
                            isLoadMore = false;
                        }
                    }else {
                        ToastUtils.showShort(ImageShowInfoActivity.this,"未知错误出现了！");
                        if (isLoadMore){
                            commentAdapter.loadMoreFail();
                            isLoadMore = false;
                        }
                    }
                }

                @Override
                public void onFailure(Call<TrendingShowInfoCommentMain> call, Throwable t) {
                    ToastUtils.showShort(ImageShowInfoActivity.this,"请检查您的网络！");

                    if (isLoadMore){
                        commentAdapter.loadMoreFail();
                        isLoadMore = false;
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

        if (commentMainData.isNoMore()){
            commentAdapter.setEnableLoadMore(false);
        }else {
            //添加底部footer
            commentAdapter.setEnableLoadMore(true);
            commentAdapter.setLoadMoreView(new MyLoadMoreView());
            commentAdapter.disableLoadMoreIfNotFullPage(imageShowInfoListView);
        }

        imageShowInfoListView.setAdapter(commentAdapter);

    }

    private void setPrimacyView() {
        headerLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.image_show_info_list_header_view,null);

        headerCardTitle = headerLayout.findViewById(R.id.image_show_info_list_header_card_title);
        headerUserIcon = headerLayout.findViewById(R.id.image_show_info_list_header_user_icon);
        headerUserName = headerLayout.findViewById(R.id.image_show_info_list_header_user_name);
        headerTime = headerLayout.findViewById(R.id.image_show_info_list_header_time);
        headerCardMore = headerLayout.findViewById(R.id.image_show_info_list_header_card_more);
        headerImageLayout = headerLayout.findViewById(R.id.image_show_info_list_header_card_image_layout);

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


        if (primacyData.getImages()!=null&&primacyData.getImages().size()!=0){

            for (int i = 0; i < primacyData.getImages().size(); i++) {
                ImageShowInfoPrimacy.ImageShowInfoPrimacyImages primacyImage = primacyData.getImages().get(i);
                ImageView primacyImageView = new ImageView(ImageShowInfoActivity.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(DensityUtils.dp2px(ImageShowInfoActivity.this,12),
                        DensityUtils.dp2px(ImageShowInfoActivity.this,4),
                        DensityUtils.dp2px(ImageShowInfoActivity.this,12),
                        DensityUtils.dp2px(ImageShowInfoActivity.this,4));
                primacyImageView.setLayoutParams(params);
                primacyImageView.setScaleType(ImageView.ScaleType.FIT_START);
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
        }

        commentAdapter.addHeaderView(headerLayout);

        RecyclerViewUtils.setScorllToTop(imageShowInfoListView);

        setPreviewImageUrlList();

        setListener();
    }

    private void setPreviewImageUrlList() {
        if (previewImagesList == null||previewImagesList.size() == 0){
            previewImagesList = new ArrayList<>();
        }

        for (int i = 0; i <primacyData.getImages().size() ; i++) {
            previewImagesList.add(primacyData.getImages().get(i).getUrl());
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
