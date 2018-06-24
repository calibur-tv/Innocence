package com.riuir.calibur.ui.home.card;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.DensityUtils;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.TimeUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.card.CardShowInfoCommentMain;
import com.riuir.calibur.data.card.CardShowInfoPrimacy;
import com.riuir.calibur.data.card.CardToggleInfo;
import com.riuir.calibur.net.ApiGet;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.loginAndRegister.LoginActivity;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.GlideUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CardShowInfoActivity extends BaseActivity {

    int cardID;

    private static final int NET_STATUS_PRIMACY = 0;
    private static final int NET_STATUS_MAIN_COMMENT = 1;

    private static final int NET_STATUS_TOGGLE_LIKE = 0;
    private static final int NET_STATUS_TOGGLE_COLLENTION = 1;

    private CardShowInfoPrimacy.CardShowInfoPrimacyData primacyData;
    private CardShowInfoCommentMain.CardShowInfoCommentMainData commentMainData;
    private CardShowInfoCommentMain.CardShowInfoCommentMainData baseCommentMainData;
    private List<CardShowInfoCommentMain.CardShowInfoCommentMainList> baseCommentMainList;
    private ArrayList<String> previewImagesList;

    private CardShowInfoCommentAdapter commentAdapter;

    LinearLayout headerLayout;
    LinearLayout headerImageLayout;
    TextView headerCardTitle;
    ImageView headerUserIcon;
    TextView headerUserName;
    TextView headerCardInfo;
    TextView headerCardMore;
    TextView headerCardSeeCount;
    TextView headerCardContent;
    CheckBox headerCardLike;
    CheckBox headerCardCollection;
    Button headerCardreply;

    CheckBox commentUpvoteCheckBox;

    int fetchId = 0;
    boolean isLoadMore = false;
    boolean isFristLoad = false;


    @BindView(R.id.card_show_info_list_view)
    RecyclerView cardShowInfoListView;
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
        isFristLoad = true;
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
                            ToastUtils.showShort(CardShowInfoActivity.this,info.getMessage());
                        }else if (info.getCode() == 40401){
                            ToastUtils.showShort(CardShowInfoActivity.this,info.getMessage());
                        }
                    }else {
                        ToastUtils.showShort(CardShowInfoActivity.this,"未知错误出现了！");
                    }
                }

                @Override
                public void onFailure(Call<CardShowInfoPrimacy> call, Throwable t) {
                    ToastUtils.showShort(CardShowInfoActivity.this,"未知错误出现了！");
                }
            });
        }

        if (NET_STATUS == NET_STATUS_MAIN_COMMENT){
            setFetchID();
            mApiGet.getCallMainComment("post",cardID,fetchId).enqueue(new Callback<CardShowInfoCommentMain>() {
                @Override
                public void onResponse(Call<CardShowInfoCommentMain> call, Response<CardShowInfoCommentMain> response) {
                    if (response!=null&&response.body()!=null&&response.body().getCode()==0){
                        commentMainData = response.body().getData();
                        if (isFristLoad){
                            isFristLoad = false;
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
                            ToastUtils.showShort(CardShowInfoActivity.this,info.getMessage());
                        }else if (info.getCode() == 40003){
                            ToastUtils.showShort(CardShowInfoActivity.this,info.getMessage());
                        }
                        if (isLoadMore){
                            commentAdapter.loadMoreFail();
                            isLoadMore = false;
                        }
                    }else {
                        ToastUtils.showShort(CardShowInfoActivity.this,"未知错误出现了！");
                        if (isLoadMore){
                            commentAdapter.loadMoreFail();
                            isLoadMore = false;
                        }
                    }
                }

                @Override
                public void onFailure(Call<CardShowInfoCommentMain> call, Throwable t) {
                    ToastUtils.showShort(CardShowInfoActivity.this,"未知错误出现了！");

                    if (isLoadMore){
                        commentAdapter.loadMoreFail();
                        isLoadMore = false;
                    }
                }
            });
        }
    }

    private void setNetToToggle(int NET_TOGGLE_STATUS){
        Map<String,String> map = new HashMap<>();

        if (NET_TOGGLE_STATUS == NET_STATUS_TOGGLE_LIKE){
            apiPost.getCardToggleLike(cardID).enqueue(new Callback<CardToggleInfo>() {
                @Override
                public void onResponse(Call<CardToggleInfo> call, Response<CardToggleInfo> response) {
                    if (response!=null&&response.body()!=null){
                        if (response.body().getCode() == 0){
                            if (response.body().isData()){
                                ToastUtils.showShort(CardShowInfoActivity.this,"点赞成功");
                            }else {
                                ToastUtils.showShort(CardShowInfoActivity.this,"取消赞成功");
                            }
                        }
                    }else {
                        String errorStr = "";
                        try {
                            errorStr = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Gson gson = new Gson();
                        CardToggleInfo toggleInfo=gson.fromJson(errorStr,CardToggleInfo.class);
                        if (toggleInfo.getCode() == 40104){
                            ToastUtils.showShort(CardShowInfoActivity.this,toggleInfo.getMessage());
                        }else if (toggleInfo.getCode() == 40301){
                            ToastUtils.showShort(CardShowInfoActivity.this,toggleInfo.getMessage());
                        }else if (toggleInfo.getCode() == 40401){
                            ToastUtils.showShort(CardShowInfoActivity.this,toggleInfo.getMessage());
                        }
                    }
                    headerCardLike.setClickable(true);
                }

                @Override
                public void onFailure(Call<CardToggleInfo> call, Throwable t) {
                    ToastUtils.showShort(CardShowInfoActivity.this,"请检查您的网络");
//                    headerCardLike.toggle();
                    headerCardLike.setClickable(true);
                }
            });
        }
        if (NET_TOGGLE_STATUS == NET_STATUS_TOGGLE_COLLENTION){
            apiPost.getCardToggleCollection(cardID).enqueue(new Callback<CardToggleInfo>() {
                @Override
                public void onResponse(Call<CardToggleInfo> call, Response<CardToggleInfo> response) {
                    if (response!=null&&response.body()!=null){
                        if (response.body().getCode() == 0){
                            if (response.body().isData()){
                                ToastUtils.showShort(CardShowInfoActivity.this,"收藏成功");
                            }else {
                                ToastUtils.showShort(CardShowInfoActivity.this,"取消收藏成功");
                            }
                        }
                    }else {
                        String errorStr = "";
                        try {
                            errorStr = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Gson gson = new Gson();
                        CardToggleInfo toggleInfo=gson.fromJson(errorStr,CardToggleInfo.class);
                        if (toggleInfo.getCode() == 40104){
                            ToastUtils.showShort(CardShowInfoActivity.this,toggleInfo.getMessage());
                        }else if (toggleInfo.getCode() == 40301){
                            ToastUtils.showShort(CardShowInfoActivity.this,toggleInfo.getMessage());
                        }else if (toggleInfo.getCode() == 40401){
                            ToastUtils.showShort(CardShowInfoActivity.this,toggleInfo.getMessage());
                        }
                    }
                    headerCardCollection.setClickable(true);
                }

                @Override
                public void onFailure(Call<CardToggleInfo> call, Throwable t) {
                    ToastUtils.showShort(CardShowInfoActivity.this,"请检查您的网络");
//                    headerCardCollection.toggle();
                    headerCardCollection.setClickable(true);
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

    private void setFetchID() {
        if (isLoadMore){
            fetchId = commentAdapter.getData().get(commentAdapter.getData().size()-1).getId();
        }
        if (isFristLoad){
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

        headerCardLike = headerLayout.findViewById(R.id.card_show_info_list_header_card_like);
        headerCardCollection = headerLayout.findViewById(R.id.card_show_info_list_header_card_collection);
        headerCardreply = headerLayout.findViewById(R.id.card_show_info_list_header_card_reply);

        headerCardTitle.setText(primacyData.getPost().getTitle());
        GlideUtils.loadImageViewCircle(CardShowInfoActivity.this,primacyData.getUser().getAvatar(),headerUserIcon);
        headerUserName.setText(primacyData.getUser().getNickname());
        int alllCount = primacyData.getPost().getComment_count()+1;
        headerCardInfo.setText("第1楼·共"+alllCount+"楼·"+TimeUtils.HowLongTimeForNow(primacyData.getPost().getCreated_at()));
        headerCardSeeCount.setText(primacyData.getPost().getView_count()+"");
        headerCardContent.setText(Html.fromHtml(primacyData.getPost().getContent()));

        if (primacyData.getPost().isLiked()){
            headerCardLike.setChecked(true);
        }else {
            headerCardLike.setChecked(false);
        }
        if (primacyData.getPost().isMarked()){
            headerCardCollection.setChecked(true);
        }else {
            headerCardCollection.setChecked(false);
        }
        LogUtils.d("cardShow","isliked = "+primacyData.getPost().isLiked()+"\n isMarked = "+primacyData.getPost().isMarked());

        headerCardLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (Constants.ISLOGIN){
                    if (b){
                        setNetToToggle(NET_STATUS_TOGGLE_LIKE);
                    }else {
                        setNetToToggle(NET_STATUS_TOGGLE_LIKE);
                    }
                    headerCardLike.setClickable(false);
                }else {
                    startActivity(LoginActivity.class);
                }
            }
        });
        headerCardCollection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (Constants.ISLOGIN){
                    if (b){
                        setNetToToggle(NET_STATUS_TOGGLE_COLLENTION);
                    }else {
                        setNetToToggle(NET_STATUS_TOGGLE_COLLENTION);
                    }
                    headerCardCollection.setClickable(false);
                }else {
                    startActivity(LoginActivity.class);
                }
            }
        });
        headerCardreply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constants.ISLOGIN){
                    Intent intent = new Intent(CardShowInfoActivity.this,CardReplyActivity.class);
                    intent.putExtra("cardID",cardID);
                    startActivity(intent);
                }else {
                    startActivity(LoginActivity.class);
                }
            }
        });

        if (primacyData.getPost().getImages()!=null&&primacyData.getPost().getImages().size()!=0){

            for (int i = 0; i < primacyData.getPost().getImages().size(); i++) {
                CardShowInfoPrimacy.CardShowInfoPrimacyImages primacyImage = primacyData.getPost().getImages().get(i);
                ImageView primacyImageView = new ImageView(CardShowInfoActivity.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(DensityUtils.dp2px(CardShowInfoActivity.this,12),
                        DensityUtils.dp2px(CardShowInfoActivity.this,4),
                        DensityUtils.dp2px(CardShowInfoActivity.this,12),
                        DensityUtils.dp2px(CardShowInfoActivity.this,4));
                primacyImageView.setLayoutParams(params);
                primacyImageView.setScaleType(ImageView.ScaleType.FIT_START);
                GlideUtils.loadImageView(CardShowInfoActivity.this,primacyData.getPost().getImages().get(i).getUrl(),primacyImageView);

                final int finalI = i;
                primacyImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent toPreviewActivityIntent = new Intent(CardShowInfoActivity.this,CardPreviewPictureActivity.class);
                        toPreviewActivityIntent.putStringArrayListExtra("previewImagesList", previewImagesList);
                        String url = primacyData.getPost().getImages().get(finalI).getUrl();
                        toPreviewActivityIntent.putExtra("imageUrl",url);
                        //版本大于5.0的时候带有动画
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            startActivity(toPreviewActivityIntent, ActivityOptions.makeSceneTransitionAnimation(CardShowInfoActivity.this,
                                    view, "ToPreviewImageActivity").toBundle());
                        }else {
                            startActivity(toPreviewActivityIntent);
                        }
                    }
                });

                headerImageLayout.addView(primacyImageView);

            }
        }

        commentAdapter.addHeaderView(headerLayout);
        cardShowInfoListView.getLayoutManager().smoothScrollToPosition(cardShowInfoListView,null,0);

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

    private void setListener() {

        commentAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                Intent toPreviewActivityIntent = new Intent(CardShowInfoActivity.this,CardPreviewPictureActivity.class);

                toPreviewActivityIntent.putStringArrayListExtra("previewImagesList", previewImagesList);

                List<CardShowInfoCommentMain.CardShowInfoCommentMainList> dataList =  adapter.getData();

                if (view.getId() == R.id.card_show_info_list_comment_item_image1){
                    if (view.getVisibility() == View.VISIBLE){
                        String url = dataList.get(position).getImages().get(0).getUrl();
                        toPreviewActivityIntent.putExtra("imageUrl",url);
                        //版本大于5.0的时候带有动画
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            startActivity(toPreviewActivityIntent, ActivityOptions.makeSceneTransitionAnimation(CardShowInfoActivity.this,
                                    view, "ToPreviewImageActivity").toBundle());
                        }else {
                            startActivity(toPreviewActivityIntent);
                        }
                    }
                }
                if (view.getId() == R.id.card_show_info_list_comment_item_image2){
                    if (view.getVisibility() == View.VISIBLE){
                        String url = dataList.get(position).getImages().get(1).getUrl();
                        toPreviewActivityIntent.putExtra("imageUrl",url);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            startActivity(toPreviewActivityIntent, ActivityOptions.makeSceneTransitionAnimation(CardShowInfoActivity.this,
                                    view, "ToPreviewImageActivity").toBundle());
                        }else {
                            startActivity(toPreviewActivityIntent);
                        }
                    }
                }
                if (view.getId() == R.id.card_show_info_list_comment_item_image3){
                    if (view.getVisibility() == View.VISIBLE){
                        String url = dataList.get(position).getImages().get(2).getUrl();
                        toPreviewActivityIntent.putExtra("imageUrl",url);
                        //版本大于5.0的时候带有动画
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            startActivity(toPreviewActivityIntent, ActivityOptions.makeSceneTransitionAnimation(CardShowInfoActivity.this,
                                    view, "ToPreviewImageActivity").toBundle());
                        }else {
                            startActivity(toPreviewActivityIntent);
                        }
                    }
                }
                if (view.getId() == R.id.card_show_info_list_comment_item_image4){
                    if (view.getVisibility() == View.VISIBLE){
                        String url = dataList.get(position).getImages().get(3).getUrl();
                        toPreviewActivityIntent.putExtra("imageUrl",url);
                        //版本大于5.0的时候带有动画
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            startActivity(toPreviewActivityIntent, ActivityOptions.makeSceneTransitionAnimation(CardShowInfoActivity.this,
                                    view, "ToPreviewImageActivity").toBundle());
                        }else {
                            startActivity(toPreviewActivityIntent);
                        }
                    }
                }
                if (view.getId() == R.id.card_show_info_list_comment_item_image5){
                    if (view.getVisibility() == View.VISIBLE){
                        String url = dataList.get(position).getImages().get(4).getUrl();
                        toPreviewActivityIntent.putExtra("imageUrl",url);
                        //版本大于5.0的时候带有动画
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            startActivity(toPreviewActivityIntent, ActivityOptions.makeSceneTransitionAnimation(CardShowInfoActivity.this,
                                    view, "ToPreviewImageActivity").toBundle());
                        }else {
                            startActivity(toPreviewActivityIntent);
                        }
                    }
                }
                if (view.getId() == R.id.card_show_info_list_comment_item_image6){
                    if (view.getVisibility() == View.VISIBLE){
                        String url = dataList.get(position).getImages().get(5).getUrl();
                        toPreviewActivityIntent.putExtra("imageUrl",url);
                        //版本大于5.0的时候带有动画
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            startActivity(toPreviewActivityIntent, ActivityOptions.makeSceneTransitionAnimation(CardShowInfoActivity.this,
                                    view, "ToPreviewImageActivity").toBundle());
                        }else {
                            startActivity(toPreviewActivityIntent);
                        }
                    }
                }
                if (view.getId() == R.id.card_show_info_list_comment_item_image7){
                    if (view.getVisibility() == View.VISIBLE){
                        String url = dataList.get(position).getImages().get(6).getUrl();
                        toPreviewActivityIntent.putExtra("imageUrl",url);
                        //版本大于5.0的时候带有动画
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            startActivity(toPreviewActivityIntent, ActivityOptions.makeSceneTransitionAnimation(CardShowInfoActivity.this,
                                    view, "ToPreviewImageActivity").toBundle());
                        }else {
                            startActivity(toPreviewActivityIntent);
                        }
                    }
                }
                if (view.getId() == R.id.card_show_info_list_comment_item_image8){
                    if (view.getVisibility() == View.VISIBLE){
                        String url = dataList.get(position).getImages().get(7).getUrl();
                        toPreviewActivityIntent.putExtra("imageUrl",url);
                        //版本大于5.0的时候带有动画
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            startActivity(toPreviewActivityIntent, ActivityOptions.makeSceneTransitionAnimation(CardShowInfoActivity.this,
                                    view, "ToPreviewImageActivity").toBundle());
                        }else {
                            startActivity(toPreviewActivityIntent);
                        }
                    }
                }
                if (view.getId() == R.id.card_show_info_list_comment_item_image9){
                    if (view.getVisibility() == View.VISIBLE){
                        String url = dataList.get(position).getImages().get(8).getUrl();
                        toPreviewActivityIntent.putExtra("imageUrl",url);
                        //版本大于5.0的时候带有动画
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            startActivity(toPreviewActivityIntent, ActivityOptions.makeSceneTransitionAnimation(CardShowInfoActivity.this,
                                    view, "ToPreviewImageActivity").toBundle());
                        }else {
                            startActivity(toPreviewActivityIntent);
                        }
                    }
                }

                if (view.getId() == R.id.card_show_info_list_comment_item_reply){
                    int commentId = dataList.get(position).getId();
                    Intent intent = new Intent(CardShowInfoActivity.this,CardChildCommentActivity.class);
                    intent.putExtra("commentId",commentId);
                    intent.putExtra("mainComment",dataList.get(position));
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


        commentAdapter = new CardShowInfoCommentAdapter(R.layout.card_show_info_list_comment_item,baseCommentMainList);
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

        if (commentMainData.isNoMore()){
            commentAdapter.setEnableLoadMore(false);
        }else {
            //添加底部footer
            commentAdapter.setEnableLoadMore(true);
            commentAdapter.setLoadMoreView(new CardLoadMoreView());
            commentAdapter.disableLoadMoreIfNotFullPage(cardShowInfoListView);
        }

        cardShowInfoListView.setAdapter(commentAdapter);


    }
    @Override
    protected void handler(Message msg) {

    }

    class CardShowInfoCommentAdapter extends BaseQuickAdapter<CardShowInfoCommentMain.CardShowInfoCommentMainList,BaseViewHolder>{


        public CardShowInfoCommentAdapter(int layoutResId, @Nullable List<CardShowInfoCommentMain.CardShowInfoCommentMainList> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, final CardShowInfoCommentMain.CardShowInfoCommentMainList item) {

            final int commentId = item.getId();

            helper.setText(R.id.card_show_info_list_comment_item_user_name,item.getFrom_user_name());
            GlideUtils.loadImageViewCircle(CardShowInfoActivity.this,item.getFrom_user_avatar(),
                    (ImageView) helper.getView(R.id.card_show_info_list_comment_item_user_icon));
            helper.setText(R.id.card_show_info_list_comment_item_comment_info,"第"+item.getFloor_count()+"楼·"+ TimeUtils.HowLongTimeForNow(item.getCreated_at()));
            helper.setText(R.id.card_show_info_list_comment_item_comment, Html.fromHtml(item.getContent()));
            helper.addOnClickListener(R.id.card_show_info_list_comment_item_card_more);

            ImageView commentMainImageView1 = helper.getView(R.id.card_show_info_list_comment_item_image1);
            ImageView commentMainImageView2 = helper.getView(R.id.card_show_info_list_comment_item_image2);
            ImageView commentMainImageView3 = helper.getView(R.id.card_show_info_list_comment_item_image3);
            ImageView commentMainImageView4 = helper.getView(R.id.card_show_info_list_comment_item_image4);
            ImageView commentMainImageView5 = helper.getView(R.id.card_show_info_list_comment_item_image5);
            ImageView commentMainImageView6 = helper.getView(R.id.card_show_info_list_comment_item_image6);
            ImageView commentMainImageView7 = helper.getView(R.id.card_show_info_list_comment_item_image7);
            ImageView commentMainImageView8 = helper.getView(R.id.card_show_info_list_comment_item_image8);
            ImageView commentMainImageView9 = helper.getView(R.id.card_show_info_list_comment_item_image9);

            helper.addOnClickListener(R.id.card_show_info_list_comment_item_image1);
            helper.addOnClickListener(R.id.card_show_info_list_comment_item_image2);
            helper.addOnClickListener(R.id.card_show_info_list_comment_item_image3);
            helper.addOnClickListener(R.id.card_show_info_list_comment_item_image4);
            helper.addOnClickListener(R.id.card_show_info_list_comment_item_image5);
            helper.addOnClickListener(R.id.card_show_info_list_comment_item_image6);
            helper.addOnClickListener(R.id.card_show_info_list_comment_item_image7);
            helper.addOnClickListener(R.id.card_show_info_list_comment_item_image8);
            helper.addOnClickListener(R.id.card_show_info_list_comment_item_image9);
            helper.addOnClickListener(R.id.card_show_info_list_comment_item_reply);

            commentUpvoteCheckBox= helper.getView(R.id.card_show_info_list_comment_item_upovte);

            if (item.isLiked()){
                commentUpvoteCheckBox.setChecked(true);
            }else {
                commentUpvoteCheckBox.setChecked(false);
            }

            if (commentUpvoteCheckBox.isChecked()){
                commentUpvoteCheckBox.setChecked(true);
            }else {
                commentUpvoteCheckBox.setChecked(false);
            }

            commentUpvoteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {
                    if (Constants.ISLOGIN){
                        commentUpvoteCheckBox.setClickable(false);
                        apiPost.getCardCommentToggleLike(commentId).enqueue(new Callback<Event<String>>() {
                            @Override
                            public void onResponse(Call<Event<String>> call, Response<Event<String>> response) {
                                if (response!=null&&response.body()!=null){
                                    if (response.body().getCode() == 0){
                                        ToastUtils.showShort(CardShowInfoActivity.this,"点赞或取消点赞成功");
                                    }else if (response.body().getCode() == 40003){
                                        ToastUtils.showShort(CardShowInfoActivity.this,response.body().getMessage());
//                                        commentUpvoteCheckBox.toggle();
                                    }
                                }else {
                                    ToastUtils.showShort(CardShowInfoActivity.this,"点赞/取消点赞失败了");
//                                    commentUpvoteCheckBox.toggle();
                                }
                                commentUpvoteCheckBox.setClickable(true);
                            }

                            @Override
                            public void onFailure(Call<Event<String>> call, Throwable t) {
                                commentUpvoteCheckBox.setClickable(true);
                                ToastUtils.showShort(CardShowInfoActivity.this,"点赞/取消点赞失败了");
//                                commentUpvoteCheckBox.toggle();
                            }
                        });
                    }else {
                        startActivity(LoginActivity.class);
                    }


                }
            });


            TextView commentChild1 = helper.getView(R.id.card_show_info_list_comment_item_child_comment1);
            TextView commentChild2 = helper.getView(R.id.card_show_info_list_comment_item_child_comment2);
            TextView commentChildMore = helper.getView(R.id.card_show_info_list_comment_item_child_comment_more);
            LinearLayout commentChildLayout = helper.getView(R.id.card_show_info_list_comment_item_child_comment_layout);
            //设置TextView部分变蓝并且可点击之前需要调用的方法
            commentChild1.setMovementMethod(LinkMovementMethod.getInstance());
            commentChild2.setMovementMethod(LinkMovementMethod.getInstance());

            if (item.getImages()!=null&&item.getImages().size()!=0){
                if (item.getImages().size() == 1){
                    commentMainImageView1.setVisibility(View.VISIBLE);
                    commentMainImageView2.setVisibility(View.GONE);
                    commentMainImageView3.setVisibility(View.GONE);
                    commentMainImageView4.setVisibility(View.GONE);
                    commentMainImageView5.setVisibility(View.GONE);
                    commentMainImageView6.setVisibility(View.GONE);
                    commentMainImageView7.setVisibility(View.GONE);
                    commentMainImageView8.setVisibility(View.GONE);
                    commentMainImageView9.setVisibility(View.GONE);

                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(0).getUrl(),commentMainImageView1);

                }else if (item.getImages().size() == 2){
                    commentMainImageView1.setVisibility(View.VISIBLE);
                    commentMainImageView2.setVisibility(View.VISIBLE);
                    commentMainImageView3.setVisibility(View.GONE);
                    commentMainImageView4.setVisibility(View.GONE);
                    commentMainImageView5.setVisibility(View.GONE);
                    commentMainImageView6.setVisibility(View.GONE);
                    commentMainImageView7.setVisibility(View.GONE);
                    commentMainImageView8.setVisibility(View.GONE);
                    commentMainImageView9.setVisibility(View.GONE);


                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(0).getUrl(),commentMainImageView1);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(1).getUrl(),commentMainImageView2);
                }else if (item.getImages().size() == 3){
                    commentMainImageView1.setVisibility(View.VISIBLE);
                    commentMainImageView2.setVisibility(View.VISIBLE);
                    commentMainImageView3.setVisibility(View.VISIBLE);
                    commentMainImageView4.setVisibility(View.GONE);
                    commentMainImageView5.setVisibility(View.GONE);
                    commentMainImageView6.setVisibility(View.GONE);
                    commentMainImageView7.setVisibility(View.GONE);
                    commentMainImageView8.setVisibility(View.GONE);
                    commentMainImageView9.setVisibility(View.GONE);

                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(0).getUrl(),commentMainImageView1);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(1).getUrl(),commentMainImageView2);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(2).getUrl(),commentMainImageView3);
                }else if (item.getImages().size() == 4){
                    commentMainImageView1.setVisibility(View.VISIBLE);
                    commentMainImageView2.setVisibility(View.VISIBLE);
                    commentMainImageView3.setVisibility(View.VISIBLE);
                    commentMainImageView4.setVisibility(View.VISIBLE);
                    commentMainImageView5.setVisibility(View.GONE);
                    commentMainImageView6.setVisibility(View.GONE);
                    commentMainImageView7.setVisibility(View.GONE);
                    commentMainImageView8.setVisibility(View.GONE);
                    commentMainImageView9.setVisibility(View.GONE);

                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(0).getUrl(),commentMainImageView1);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(1).getUrl(),commentMainImageView2);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(2).getUrl(),commentMainImageView3);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(3).getUrl(),commentMainImageView4);
                }else if (item.getImages().size() == 5){
                    commentMainImageView1.setVisibility(View.VISIBLE);
                    commentMainImageView2.setVisibility(View.VISIBLE);
                    commentMainImageView3.setVisibility(View.VISIBLE);
                    commentMainImageView4.setVisibility(View.VISIBLE);
                    commentMainImageView5.setVisibility(View.VISIBLE);
                    commentMainImageView6.setVisibility(View.GONE);
                    commentMainImageView7.setVisibility(View.GONE);
                    commentMainImageView8.setVisibility(View.GONE);
                    commentMainImageView9.setVisibility(View.GONE);

                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(0).getUrl(),commentMainImageView1);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(1).getUrl(),commentMainImageView2);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(2).getUrl(),commentMainImageView3);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(3).getUrl(),commentMainImageView4);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(4).getUrl(),commentMainImageView5);
                }else if (item.getImages().size() == 6){
                    commentMainImageView1.setVisibility(View.VISIBLE);
                    commentMainImageView2.setVisibility(View.VISIBLE);
                    commentMainImageView3.setVisibility(View.VISIBLE);
                    commentMainImageView4.setVisibility(View.VISIBLE);
                    commentMainImageView5.setVisibility(View.VISIBLE);
                    commentMainImageView6.setVisibility(View.VISIBLE);
                    commentMainImageView7.setVisibility(View.GONE);
                    commentMainImageView8.setVisibility(View.GONE);
                    commentMainImageView9.setVisibility(View.GONE);

                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(0).getUrl(),commentMainImageView1);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(1).getUrl(),commentMainImageView2);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(2).getUrl(),commentMainImageView3);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(3).getUrl(),commentMainImageView4);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(4).getUrl(),commentMainImageView5);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(5).getUrl(),commentMainImageView6);
                }else if (item.getImages().size() == 7){
                    commentMainImageView1.setVisibility(View.VISIBLE);
                    commentMainImageView2.setVisibility(View.VISIBLE);
                    commentMainImageView3.setVisibility(View.VISIBLE);
                    commentMainImageView4.setVisibility(View.VISIBLE);
                    commentMainImageView5.setVisibility(View.VISIBLE);
                    commentMainImageView6.setVisibility(View.VISIBLE);
                    commentMainImageView7.setVisibility(View.VISIBLE);
                    commentMainImageView8.setVisibility(View.GONE);
                    commentMainImageView9.setVisibility(View.GONE);

                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(0).getUrl(),commentMainImageView1);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(1).getUrl(),commentMainImageView2);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(2).getUrl(),commentMainImageView3);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(3).getUrl(),commentMainImageView4);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(4).getUrl(),commentMainImageView5);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(5).getUrl(),commentMainImageView6);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(6).getUrl(),commentMainImageView7);
                }else if (item.getImages().size() == 8){
                    commentMainImageView1.setVisibility(View.VISIBLE);
                    commentMainImageView2.setVisibility(View.VISIBLE);
                    commentMainImageView3.setVisibility(View.VISIBLE);
                    commentMainImageView4.setVisibility(View.VISIBLE);
                    commentMainImageView5.setVisibility(View.VISIBLE);
                    commentMainImageView6.setVisibility(View.VISIBLE);
                    commentMainImageView7.setVisibility(View.VISIBLE);
                    commentMainImageView8.setVisibility(View.VISIBLE);
                    commentMainImageView9.setVisibility(View.GONE);

                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(0).getUrl(),commentMainImageView1);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(1).getUrl(),commentMainImageView2);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(2).getUrl(),commentMainImageView3);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(3).getUrl(),commentMainImageView4);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(4).getUrl(),commentMainImageView5);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(5).getUrl(),commentMainImageView6);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(6).getUrl(),commentMainImageView7);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(7).getUrl(),commentMainImageView8);
                }else if (item.getImages().size() >= 9){
                    commentMainImageView1.setVisibility(View.VISIBLE);
                    commentMainImageView2.setVisibility(View.VISIBLE);
                    commentMainImageView3.setVisibility(View.VISIBLE);
                    commentMainImageView4.setVisibility(View.VISIBLE);
                    commentMainImageView5.setVisibility(View.VISIBLE);
                    commentMainImageView6.setVisibility(View.VISIBLE);
                    commentMainImageView7.setVisibility(View.VISIBLE);
                    commentMainImageView8.setVisibility(View.VISIBLE);
                    commentMainImageView9.setVisibility(View.VISIBLE);

                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(0).getUrl(),commentMainImageView1);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(1).getUrl(),commentMainImageView2);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(2).getUrl(),commentMainImageView3);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(3).getUrl(),commentMainImageView4);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(4).getUrl(),commentMainImageView5);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(5).getUrl(),commentMainImageView6);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(6).getUrl(),commentMainImageView7);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(7).getUrl(),commentMainImageView8);
                    GlideUtils.loadImageView(CardShowInfoActivity.this,item.getImages().get(8).getUrl(),commentMainImageView9);
                }
            }else {
                commentMainImageView1.setVisibility(View.GONE);
                commentMainImageView2.setVisibility(View.GONE);
                commentMainImageView3.setVisibility(View.GONE);
                commentMainImageView4.setVisibility(View.GONE);
                commentMainImageView5.setVisibility(View.GONE);
                commentMainImageView6.setVisibility(View.GONE);
                commentMainImageView7.setVisibility(View.GONE);
                commentMainImageView8.setVisibility(View.GONE);
                commentMainImageView9.setVisibility(View.GONE);
            }
            if (item.getComments().getList()!=null&&item.getComments().getList().size()!=0){
                if (item.getComments().getList().size()==1){
                    commentChildLayout.setVisibility(View.VISIBLE);
                    commentChild1.setVisibility(View.VISIBLE);
                    commentChild2.setVisibility(View.GONE);
                    commentChildMore.setVisibility(View.GONE);
                    SpannableString fromUserName1 = new SpannableString(item.getComments().getList().get(0).getFrom_user_name());
                    fromUserName1.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(View view) {
                            ToastUtils.showShort(CardShowInfoActivity.this,"点击了A");
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setColor(getResources().getColor(R.color.theme_magic_sakura_blue));
                            ds.setUnderlineText(false);
                        }
                    }, 0, fromUserName1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    commentChild1.setText(fromUserName1);
                    commentChild1.append(" 回复 ");
                    SpannableString toUserName1 = new SpannableString(item.getComments().getList().get(0).getTo_user_name());
                    toUserName1.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(View view) {
                            ToastUtils.showShort(CardShowInfoActivity.this,"点击了B");
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setColor(getResources().getColor(R.color.theme_magic_sakura_blue));
                            ds.setUnderlineText(false);
                        }
                    },0,toUserName1.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    commentChild1.append(toUserName1);
                    commentChild1.append(" : "+item.getComments().getList().get(0).getContent());

                }else if(item.getComments().getList().size() >= 2){
                    commentChildLayout.setVisibility(View.VISIBLE);
                    commentChild1.setVisibility(View.VISIBLE);
                    commentChild2.setVisibility(View.VISIBLE);
                    commentChildMore.setVisibility(View.VISIBLE);

                    SpannableString fromUserName1 = new SpannableString(item.getComments().getList().get(0).getFrom_user_name());
                    fromUserName1.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(View view) {
                            ToastUtils.showShort(CardShowInfoActivity.this,"点击了A");
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setColor(getResources().getColor(R.color.theme_magic_sakura_blue));
                            ds.setUnderlineText(false);
                        }
                    }, 0, fromUserName1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    commentChild1.setText(fromUserName1);
                    commentChild1.append(" 回复 ");
                    SpannableString toUserName1 = new SpannableString(item.getComments().getList().get(0).getTo_user_name());
                    toUserName1.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(View view) {
                            ToastUtils.showShort(CardShowInfoActivity.this,"点击了B");
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setColor(getResources().getColor(R.color.theme_magic_sakura_blue));
                            ds.setUnderlineText(false);
                        }
                    },0,toUserName1.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    commentChild1.append(toUserName1);
                    commentChild1.append(" : "+item.getComments().getList().get(0).getContent());

                    //回复2
                    SpannableString fromUserName2 = new SpannableString(item.getComments().getList().get(1).getFrom_user_name());
                    fromUserName2.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(View view) {
                            ToastUtils.showShort(CardShowInfoActivity.this,"点击了A");
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setColor(getResources().getColor(R.color.theme_magic_sakura_blue));
                            ds.setUnderlineText(false);
                        }
                    }, 0, fromUserName2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    commentChild2.setText(fromUserName2);
                    commentChild2.append(" 回复 ");
                    SpannableString toUserName2 = new SpannableString(item.getComments().getList().get(1).getTo_user_name());
                    toUserName2.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(View view) {
                            ToastUtils.showShort(CardShowInfoActivity.this,"点击了B");
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setColor(getResources().getColor(R.color.theme_magic_sakura_blue));
                            ds.setUnderlineText(false);
                        }
                    },0,toUserName2.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    commentChild2.append(toUserName2);
                    commentChild2.append(" : "+item.getComments().getList().get(1).getContent());

                    //更多回复
                    commentChildMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(CardShowInfoActivity.this,CardChildCommentActivity.class);
                            intent.putExtra("commentId",commentId);
                            intent.putExtra("mainComment",item);
                            startActivity(intent);
                        }
                    });

                }else {
                    commentChildLayout.setVisibility(View.GONE);
                    commentChild1.setVisibility(View.GONE);
                    commentChild2.setVisibility(View.GONE);
                    commentChildMore.setVisibility(View.GONE);
                }
            }else{
                commentChildLayout.setVisibility(View.GONE);
                commentChild1.setVisibility(View.GONE);
                commentChild2.setVisibility(View.GONE);
                commentChildMore.setVisibility(View.GONE);
            }

        }

    }


    public  class CardLoadMoreView extends LoadMoreView {

        @Override public int getLayoutId() {
            return R.layout.brvah_quick_view_load_more;
        }

        /**
         * 如果返回true，数据全部加载完毕后会隐藏加载更多
         * 如果返回false，数据全部加载完毕后会显示getLoadEndViewId()布局
         */
        @Override public boolean isLoadEndGone() {
            return false;
        }

        @Override protected int getLoadingViewId() {
            return R.id.load_more_loading_view;
        }

        @Override protected int getLoadFailViewId() {
            return R.id.load_more_load_fail_view;
        }

        /**
         * isLoadEndGone()为true，可以返回0
         * isLoadEndGone()为false，不能返回0
         */
        @Override protected int getLoadEndViewId() {
            return R.id.load_more_load_end_view;
        }
    }

}
