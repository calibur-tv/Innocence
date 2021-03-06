package com.riuir.calibur.ui.home;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import calibur.core.http.models.user.UserNotificationInfo;
import calibur.core.http.observer.ObserverWrapper;
import calibur.core.manager.UserSystem;
import calibur.core.templates.TemplateRenderEngine;
import calibur.core.widget.webview.AthenaWebView;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.assistUtils.activityUtils.UserMainUtils;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.home.Drama.DramaVideoPlayActivity;
import com.riuir.calibur.ui.home.adapter.MyLoadMoreView;
import com.riuir.calibur.ui.home.adapter.UserMessageAdapter;
import com.riuir.calibur.ui.home.card.CardShowInfoActivity;
import com.riuir.calibur.ui.home.image.ImageShowInfoActivity;
import com.riuir.calibur.ui.home.message.MessageShowCommentActivity;
import com.riuir.calibur.ui.home.score.ScoreShowInfoActivity;
import com.riuir.calibur.ui.web.WebTemplatesUtils;
import com.riuir.calibur.ui.widget.SearchLayout;
import com.riuir.calibur.ui.widget.emptyView.AppListEmptyView;
import com.riuir.calibur.ui.widget.emptyView.AppListFailedView;
import com.riuir.calibur.utils.ActivityUtils;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;


/**
 * 消息fragment
 */
public class MessageFragment extends BaseFragment {

    private int minId = 0;
    private UserNotificationInfo notificationData;
    private List<UserNotificationInfo.UserNotificationInfoList> notificationList = new ArrayList<>();
    private List<UserNotificationInfo.UserNotificationInfoList> baseNotificationList = new ArrayList<>();

    private boolean isFirstLoad = false;
    private boolean isLoadMore = false;
    private boolean isRefresh = false;
    private boolean isFirstClick = false;

    private int noReadMsgCount;

    @BindView(R.id.main_user_message_list_view)
    RecyclerView messageListView;

    @BindView(R.id.main_user_message_webView)
    AthenaWebView msgWebView;

    @BindView(R.id.main_user_message_refresh_layout)
    SwipeRefreshLayout messageRefreshLayout;

    @BindView(R.id.message_search_layout)
    SearchLayout searchLayout;

    @BindView(R.id.main_user_message_set_all_read)
    TextView setAllReadBtn;

    UserMessageAdapter adapter;

    AppListFailedView failedView;
    AppListEmptyView emptyView;

    Call<UserNotificationInfo> userNotificationInfoCall;

    MainActivity mainActivity;

    private final int MESSAGE_TYPE_POST_LIKE = 1,MESSAGE_TYPE_POST_REWARD = 2,MESSAGE_TYPE_POST_MARK = 3,
            MESSAGE_TYPE_POST_COMMENT = 4,MESSAGE_TYPE_POST_REPLY = 5,MESSAGE_TYPE_IMAGE_LIKE = 6,MESSAGE_TYPE_IMAGE_REWARD = 7,
            MESSAGE_TYPE_IMAGE_MARK = 8, MESSAGE_TYPE_IMAGE_COMMENT = 9,MESSAGE_TYPE_IMAGE_REPLY = 10,MESSAGE_TYPE_SCORE_LIKE = 11,
            MESSAGE_TYPE_SCORE_REWARD = 12,MESSAGE_TYPE_SCORE_MARK = 13, MESSAGE_TYPE_SCORE_COMMENT = 14,MESSAGE_TYPE_SCORE_REPLY = 15,
            MESSAGE_TYPE_VIDEO_COMMENT = 16,MESSAGE_TYPE_VIDEO_REPLY = 17,MESSAGE_TYPE_POST_COMMENT_LIKE = 18,
            MESSAGE_TYPE_POST_REPLY_LIKE = 19,MESSAGE_TYPE_IMAGE_COMMENT_LIKE = 20, MESSAGE_TYPE_IMAGE_REPLY_LIKE = 21,
            MESSAGE_TYPE_SCORE_COMMENT_LIKE = 22,MESSAGE_TYPE_SCORE_REPLY_LIKE = 23,MESSAGE_TYPE_VIDEO_COMMENT_LIKE = 24,
            MESSAGE_TYPE_VIDEO_REPLY_LIKE = 25;

    public static MessageFragment newInstance() {
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
        isFirstClick = true;
        setAdapter();
        setNet();
        mainActivity = (MainActivity) getActivity();
        setOnRefreshMsgFromMain();
    }


    private void setOnRefreshMsgFromMain() {

        mainActivity.setOnRefreshMessageList(new MainActivity.OnRefreshMessageList() {
            @Override
            public void OnReFresh(int count) {
                noReadMsgCount = count;
                if (isFirstClick){
                    //此处拦截第一次点击底部tab切换到该fragment
                    //如果不拦截 fragment的加载机制会导致firstLoad和refresh一起加载 数据错乱
                    isFirstClick = false;
                }else if (noReadMsgCount!=0&&messageRefreshLayout!=null
                        &&!isRefresh){
                    //未读消息不为0的时候刷新列表
                    messageRefreshLayout.setRefreshing(true);
                    isRefresh = true;
                    setNet();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        int stautsBarHeight = ActivityUtils.getStatusBarHeight(getContext());
        rootView.setPadding(0,stautsBarHeight,0,0);
    }

    private void setMinId() {
        if (notificationList!=null&&notificationList.size()!=0){
            minId = notificationList.get(notificationList.size()-1).getId();
        }

        if (isRefresh){
            minId = 0;
        }
        if (isFirstLoad){
            minId = 0;
        }
    }

    private void setNet() {
        if (UserSystem.getInstance().isLogin()){
            setMinId();
            LogUtils.d("notificationData","minId = "+minId+",isFirstLoad = "+isFirstLoad+
                    ",isRefresh = "+isRefresh+",notificationList = "+notificationList.size());
            apiService.getCallUserNotification(minId)
                    .compose(Rx2Schedulers.applyObservableAsync())
                    .subscribe(new ObserverWrapper<UserNotificationInfo>(){
                        @Override
                        public void onSuccess(UserNotificationInfo userNotificationInfo) {
                            notificationData = userNotificationInfo;
                            notificationList = userNotificationInfo.getList();
                            for (int i = 0; i < notificationList.size(); i++) {
                                LogUtils.d("notificationData","msg list = "+notificationList.get(i).toString());
                            }
                            if (isFirstLoad){
                                baseNotificationList = userNotificationInfo.getList();
                                setFirstData();
                            }
                            if (isLoadMore){
                                setLoadMore();
                            }
                            if (isRefresh){
                                setRefresh();
                            }
//                            setWebView();
                            setEmptyView();
                        }

                        @Override
                        public void onFailure(int code, String errorMsg) {
                            super.onFailure(code, errorMsg);
                            if (messageRefreshLayout!=null){
                                if (isLoadMore){
                                    adapter.loadMoreFail();
                                    isLoadMore = false;
                                }
                                if (isRefresh){
                                    messageRefreshLayout.setRefreshing(false);
                                    isRefresh = false;
                                }
                                setFailedView();
                            }
                        }
                    });

        }else {
            //未登录状态
            ToastUtils.showShort(getContext(),"登录之后才能查看消息哦！");
        }
    }

    private void setWebView() {
        Gson gson= new Gson();
        String data = gson.toJson(notificationData,UserNotificationInfo.class);
        WebTemplatesUtils.loadTemplates(msgWebView,TemplateRenderEngine.NOTIFICATIONS,data);
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
        adapter.setNewData(baseNotificationList);
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
                UserNotificationInfo.UserNotificationInfoList notificationInfo =
                        (UserNotificationInfo.UserNotificationInfoList) adapter.getData().get(position);
                //先消除小红点 然后发送读取消息
                ImageView reddot = view.findViewById(R.id.user_message_list_item_msg_text_reddot);
                reddot.setVisibility(View.INVISIBLE);
                setReadMsg(notificationInfo.getId());
                setJump(notificationInfo);
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

        setAllReadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(noReadMsgCount!=0){
                    setAllReadBtn.setClickable(false);
                    setAllReadBtn.setText("设置中");
                    setAllMsgRead();
                }else {
                    ToastUtils.showShort(getContext(),"没有未读消息~");
                }

            }
        });
    }

    private void setReadMsg(int id) {
        apiService.getCallReadNotification(id)
                .compose(Rx2Schedulers.applyObservableAsync())
                .subscribe(new ObserverWrapper<String>(){
                    @Override
                    public void onSuccess(String s) {
                    }
                });
    }

    private void setAllMsgRead() {
        apiService.getCallAllReadNotification()
                .compose(Rx2Schedulers.applyObservableAsync())
                .subscribe(new ObserverWrapper<String>(){
                    @Override
                    public void onSuccess(String s) {
                        //设置成功 刷新消息列表
                        mainActivity.setNoReadMsgZero();
                        messageRefreshLayout.setRefreshing(true);
                        isRefresh = true;
                        setNet();
                        setAllReadBtn.setClickable(true);
                        setAllReadBtn.setText("全部设为已读");
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        super.onFailure(code, errorMsg);
                        setAllReadBtn.setClickable(true);
                        setAllReadBtn.setText("全部设为已读");
                    }
                });
    }

    private void setJump(UserNotificationInfo.UserNotificationInfoList data) {

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

        int comment_id = 0;
        int reply_id = 0;
        String[] links = data.getLink().split("\\?");
        if (links!=null&&links.length==2){
            String [] ids = links[1].split("\\&");
            if (ids!=null&&ids.length!=0){
                if (ids.length == 1){
                    String[] commentIdStr = ids[0].split("=");
                    comment_id = Integer.parseInt(commentIdStr[1]);
                }else {
                    String[] commentIdStr = ids[0].split("=");
                    comment_id = Integer.parseInt(commentIdStr[1]);
                    String[] replyIdStr = ids[1].split("=");
                    reply_id = Integer.parseInt(replyIdStr[1]);
                }
            }
        }
        Intent intent = new Intent();
        intent.putExtra("main_card_id",data.getModel().getId());
        intent.putExtra("comment_id",comment_id);
        intent.putExtra("reply_id",reply_id);

        switch (type){
            case 1:
                intent.putExtra("cardID",data.getModel().getId());
                intent.setClass(getContext(), CardShowInfoActivity.class);
                intent.putExtra("type","post");
                break;
            case 2:
                intent.putExtra("imageID",data.getModel().getId());
                intent.setClass(getContext(), ImageShowInfoActivity.class);
                intent.putExtra("type","image");
                break;
            case 3:
                intent.putExtra("scoreID",data.getModel().getId());
                intent.setClass(getContext(), ScoreShowInfoActivity.class);
                intent.putExtra("type","score");
                break;
            case 4:
                intent.putExtra("videoId",data.getModel().getId());
                intent.setClass(getContext(), DramaVideoPlayActivity.class);
                intent.putExtra("type","video");
                break;
            default:
                break;
        }
        if (data.getMessage().contains("问题")||data.getMessage().contains("回答")){
        }else if(data.getMessage().contains("评论了")||data.getMessage().contains("回复了")){
            LogUtils.d("messageJump","message = "+data.getMessage());
            intent.setClass(getContext(), MessageShowCommentActivity.class);
        }
        if (intent.getClass()!=null){
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                ToastUtils.showShort(getContext(),"该模块尚未解锁！");
            }
        }
    }


    private void setEmptyView(){
        if (baseNotificationList==null||baseNotificationList.size()==0){
            emptyView = new AppListEmptyView(getContext());
            emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            adapter.setEmptyView(emptyView);
        }
    }
    private void setFailedView(){
        //加载失败 下拉重试
        failedView = new AppListFailedView(getContext());
        failedView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        adapter.setEmptyView(failedView);
    }


    private void setRefresh() {
        isRefresh = false;
        adapter.setNewData(notificationList);
        messageRefreshLayout.setRefreshing(false);
        ToastUtils.showShort(getContext(),"消息列表刷新成功！");
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
