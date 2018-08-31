package com.riuir.calibur.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.assistUtils.activityUtils.UserMainUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.user.UserNotificationInfo;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.home.Drama.DramaVideoPlayActivity;
import com.riuir.calibur.ui.home.adapter.MyLoadMoreView;
import com.riuir.calibur.ui.home.adapter.UserMessageAdapter;
import com.riuir.calibur.ui.home.card.CardShowInfoActivity;
import com.riuir.calibur.ui.home.image.ImageShowInfoActivity;
import com.riuir.calibur.ui.home.score.ScoreShowInfoActivity;
import com.riuir.calibur.utils.ActivityUtils;
import com.riuir.calibur.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ************************************
 * 作者：韩宝坤
 * 日期：2017/12/24
 * 邮箱：hanbaokun@outlook.com
 * 描述：
 * ************************************
 */

/**
 * 消息fragment
 */
public class MessageFragment extends BaseFragment {

    private int minId = 0;
    private UserNotificationInfo.UserNotificationInfoData notificationData;
    private List<UserNotificationInfo.UserNotificationInfoList> notificationList;
    private List<UserNotificationInfo.UserNotificationInfoList> baseNotificationList = new ArrayList<>();

    private boolean isFirstLoad = false;
    private boolean isLoadMore = false;
    private boolean isRefresh = false;

    @BindView(R.id.main_user_message_list_view)
    RecyclerView messageListView;

    @BindView(R.id.main_user_message_refresh_layout)
    SwipeRefreshLayout messageRefreshLayout;

    UserMessageAdapter adapter;

    private final int MESSAGE_TYPE_POST_LIKE = 1,MESSAGE_TYPE_POST_REWARD = 2,MESSAGE_TYPE_POST_MARK = 3,
            MESSAGE_TYPE_POST_COMMENT = 4,MESSAGE_TYPE_POST_REPLY = 5,MESSAGE_TYPE_IMAGE_LIKE = 6,MESSAGE_TYPE_IMAGE_REWARD = 7,
            MESSAGE_TYPE_IMAGE_MARK = 8, MESSAGE_TYPE_IMAGE_COMMENT = 9,MESSAGE_TYPE_IMAGE_REPLY = 10,MESSAGE_TYPE_SCORE_LIKE = 11,
            MESSAGE_TYPE_SCORE_REWARD = 12,MESSAGE_TYPE_SCORE_MARK = 13, MESSAGE_TYPE_SCORE_COMMENT = 14,MESSAGE_TYPE_SCORE_REPLY = 15,
            MESSAGE_TYPE_VIDEO_COMMENT = 16,MESSAGE_TYPE_VIDEO_REPLY = 17,MESSAGE_TYPE_POST_COMMENT_LIKE = 18,
            MESSAGE_TYPE_POST_REPLY_LIKE = 19,MESSAGE_TYPE_IMAGE_COMMENT_LIKE = 20, MESSAGE_TYPE_IMAGE_REPLY_LIKE = 21,
            MESSAGE_TYPE_SCORE_COMMENT_LIKE = 22,MESSAGE_TYPE_SCORE_REPLY_LIKE = 23,MESSAGE_TYPE_VIDEO_COMMENT_LIKE = 24,
            MESSAGE_TYPE_VIDEO_REPLY_LIKE = 25;

    public static Fragment newInstance() {
        MessageFragment messageFragment = new MessageFragment();
        Bundle b = new Bundle();
        messageFragment.setArguments(b);
        return messageFragment;
    }

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_message;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        minId = 0;
        isFirstLoad = true;
        int stautsBarHeight = ActivityUtils.getStatusBarHeight(getContext());
        rootView.setPadding(0,stautsBarHeight,0,0);
        setNet();
        setAdapter();
    }

    private void setMinId() {
        if (notificationList!=null&&notificationList.size()!=0)
        minId = notificationList.get(notificationList.size()-1).getId();

        if (isRefresh){
            minId = 0;
        }
        if (isFirstLoad){
            minId = 0;
        }
    }

    private void setNet() {
        if (Constants.ISLOGIN){
            setMinId();
            apiGetHasAuth.getCallUserNotification(minId).enqueue(new Callback<UserNotificationInfo>() {
                @Override
                public void onResponse(Call<UserNotificationInfo> call, Response<UserNotificationInfo> response) {
                    if (response!=null&&response.isSuccessful()){
                        notificationData = response.body().getData();
                        notificationList = response.body().getData().getList();
                        if (isFirstLoad){
                            baseNotificationList = response.body().getData().getList();
                            setFirstData();
                        }
                        if (isLoadMore){
                            setLoadMore();
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
                        if (isLoadMore){
                            adapter.loadMoreFail();
                            isLoadMore = false;
                        }
                        if (isRefresh){
                            messageRefreshLayout.setRefreshing(false);
                            isRefresh = false;
                        }

                    }else {
                        ToastUtils.showShort(getContext(),"未知原因导致加载失败了！");
                        if (isLoadMore){
                            adapter.loadMoreFail();
                            isLoadMore = false;
                        }
                        if (isRefresh){
                            messageRefreshLayout.setRefreshing(false);
                            isRefresh = false;
                        }

                    }
                }

                @Override
                public void onFailure(Call<UserNotificationInfo> call, Throwable t) {
                    ToastUtils.showShort(getContext(),"未知原因导致加载失败了！");
                    if (isLoadMore){
                        adapter.loadMoreFail();
                        isLoadMore = false;
                    }
                    if (isRefresh){
                        messageRefreshLayout.setRefreshing(false);
                        isRefresh = false;
                    }

                }
            });
        }else {
            //未登录状态
            ToastUtils.showShort(getContext(),"登录之后才能查看消息哦！");

        }
    }

    private void setAdapter() {
        adapter = new UserMessageAdapter(R.layout.main_user_message_list_item,baseNotificationList,getContext());
        messageListView.setLayoutManager(new LinearLayoutManager(getContext()));
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
        adapter.disableLoadMoreIfNotFullPage(messageListView);
        messageListView.setAdapter(adapter);
        //添加监听
        setListener();

    }

    private void setFirstData(){
        isFirstLoad = false;
        adapter.addData(baseNotificationList);
    }

    private void setListener() {
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.user_message_list_item_user_icon){
                    UserNotificationInfo.UserNotificationInfoList list =
                            (UserNotificationInfo.UserNotificationInfoList) adapter.getData().get(position);
                    UserMainUtils.toUserMainActivity(getContext(),list.getUser().getId(),list.getUser().getZone());
                }
            }
        });
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                setJump((UserNotificationInfo.UserNotificationInfoList) adapter.getData().get(position));
            }
        });

        //上拉加载监听
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override public void onLoadMoreRequested() {
                isLoadMore = true;
                setNet();
            }
        }, messageListView);

        //下拉刷新监听
        messageRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                setNet();
            }
        });
    }

    private void setJump(UserNotificationInfo.UserNotificationInfoList data) {
        Intent intent = new Intent();
        int type =0;
        switch (data.getType()){
            case MESSAGE_TYPE_POST_LIKE:
//                break;
            case MESSAGE_TYPE_POST_REWARD:
//                break;
            case MESSAGE_TYPE_POST_MARK:
//                break;
            case MESSAGE_TYPE_POST_COMMENT:
//                break;
            case MESSAGE_TYPE_POST_REPLY:
                type = 1;
                break;
            case MESSAGE_TYPE_IMAGE_LIKE:
//                break;
            case MESSAGE_TYPE_IMAGE_REWARD:
//                break;
            case MESSAGE_TYPE_IMAGE_MARK:
//                break;
            case MESSAGE_TYPE_IMAGE_COMMENT:
//                break;
            case MESSAGE_TYPE_IMAGE_REPLY:
                type = 2;
                break;
            case MESSAGE_TYPE_SCORE_LIKE:
//                break;
            case MESSAGE_TYPE_SCORE_REWARD:
//                break;
            case MESSAGE_TYPE_SCORE_MARK:
//                break;
            case MESSAGE_TYPE_SCORE_COMMENT:
//                break;
            case MESSAGE_TYPE_SCORE_REPLY:
                type = 3;
                break;
            case MESSAGE_TYPE_VIDEO_COMMENT:
//                break;
            case MESSAGE_TYPE_VIDEO_REPLY:
                type = 4;
                break;
            case MESSAGE_TYPE_POST_COMMENT_LIKE:
//                break;
            case MESSAGE_TYPE_POST_REPLY_LIKE:
                type = 1;
                break;
            case MESSAGE_TYPE_IMAGE_COMMENT_LIKE:
//                break;
            case MESSAGE_TYPE_IMAGE_REPLY_LIKE:
                type =2;
                break;
            case MESSAGE_TYPE_SCORE_COMMENT_LIKE:
//                break;
            case MESSAGE_TYPE_SCORE_REPLY_LIKE:
                type = 3;
                break;
            case MESSAGE_TYPE_VIDEO_COMMENT_LIKE:
//                break;
            case MESSAGE_TYPE_VIDEO_REPLY_LIKE:
                type = 4;
                break;
            default:
                break;
        }
        switch (type){
            case 1:
                intent.setClass(getContext(), CardShowInfoActivity.class);
                intent.putExtra("cardID",data.getModel().getId());
                startActivity(intent);
                break;
            case 2:
                intent.setClass(getContext(), ImageShowInfoActivity.class);
                intent.putExtra("imageID",data.getModel().getId());
                startActivity(intent);
                break;
            case 3:
                intent.setClass(getContext(), ScoreShowInfoActivity.class);
                intent.putExtra("scoreID",data.getModel().getId());
                startActivity(intent);
                break;
            case 4:
                intent.setClass(getContext(), DramaVideoPlayActivity.class);
                intent.putExtra("videoId",data.getModel().getId());
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void setRefresh() {
        isRefresh = false;
        adapter.setNewData(notificationList);
        messageRefreshLayout.setRefreshing(false);
        ToastUtils.showShort(getContext(),"刷新成功！");
    }

    private void setLoadMore() {
        isLoadMore = false;
        if (notificationData.isNoMore()) {
            adapter.addData(notificationList);
            //数据全部加载完毕
            adapter.loadMoreEnd();
        } else {
            //成功获取更多数据
            adapter.addData(notificationList);
            adapter.loadMoreComplete();
        }
    }

}
