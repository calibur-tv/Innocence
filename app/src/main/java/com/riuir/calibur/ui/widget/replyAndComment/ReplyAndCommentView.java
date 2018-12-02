package com.riuir.calibur.ui.widget.replyAndComment;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.KeyBoardUtils;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;

import com.riuir.calibur.data.trending.TrendingToggleInfo;
import com.riuir.calibur.net.ApiPost;
import com.riuir.calibur.ui.home.MineFragment;
import com.riuir.calibur.ui.home.adapter.CommentAdapter;
import com.riuir.calibur.ui.home.search.DramaSearchActivity;
import com.riuir.calibur.ui.widget.TrendingLikeFollowCollectionView;
import com.riuir.calibur.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;

import calibur.core.http.RetrofitManager;
import calibur.core.http.api.APIService;
import calibur.core.http.models.base.ResponseBean;
import calibur.core.http.models.comment.CreateMainCommentInfo;
import calibur.core.http.models.comment.ReplyCommentInfo;
import calibur.core.http.models.comment.params.CreateMainComment;
import calibur.core.http.models.qiniu.params.QiniuImageParams;
import calibur.core.http.observer.ObserverWrapper;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReplyAndCommentView extends LinearLayout{

    private boolean mHasInit;
    private boolean mHasKeybord;
    private int mHeight;


    Context context;

    TextView jumpBtn;
    EditText editRC;
    TextView sendBtn;
    LinearLayout btnLayout;
    LinearLayout editLayout;
    ImageView zanIcon;
    ImageView markIcon;
    ImageView rewardIcon;
    private  AlertDialog rewardDialog;

    String type;
    String subType;
    //根据status状态不同决定设置的值也不同
    int status;
    //创建主评论需要的id
    int id;
    //发布主题的用户的id
    int titleId;
    //回复评论的id
    int mainCommentid;
    int targetUserId;
    int targetUserMainId;
    String fromUserName;

    boolean liked = false;
    boolean rewarded = false;
    boolean marked = false;
    boolean is_creator = false;

    //创建主评论的列表adapter
    CommentAdapter commentAdapter;
    //回复评论的列表adapter
    BaseQuickAdapter childCommentAdapter;

    ReplyCommentInfo replyCommentInfo;
    CreateMainCommentInfo createMainCommentInfo;

    public static final int STATUS_MAIN_COMMENT = 0;
    public static final int STATUS_SUB_COMMENT = 1;

    public static final String TYPE_SUB_COMMENT = "sub_comment";
    public static final String TYPE_SUB_MESSAGE = "sub_message";

    public static final String TYPE_VIDEO = "video";
    public static final String TYPE_POST = "post";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_CARTOON = "cartoon";
    public static final String TYPE_SCORE = "score";

    private static final int NET_STATUS_TOGGLE_LIKE = 0;
    private static final int NET_STATUS_TOGGLE_COLLENTION = 1;
    private static final int NET_STATUS_TOGGLE_REWARDED = 2;

    ApiPost apiPost;

    private static ReplyAndCommentView instance;

    public ReplyAndCommentView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public ReplyAndCommentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public ReplyAndCommentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private void initView() {
        instance = this;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.reply_and_comment_view_layout, this, true);
        editRC = view.findViewById(R.id.reply_and_comment_edit);
        sendBtn = view.findViewById(R.id.reply_and_comment_send_btn);
        jumpBtn = view.findViewById(R.id.reply_and_comment_jump);
        btnLayout = view.findViewById(R.id.reply_and_comment_button_layout);
        editLayout = view.findViewById(R.id.reply_and_comment_edit_layout);
        zanIcon = view.findViewById(R.id.reply_and_comment_zan_icon);
        rewardIcon = view.findViewById(R.id.reply_and_comment_reward_icon);
        markIcon = view.findViewById(R.id.reply_and_comment_marked_icon);
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitleId(int titleId){
        this.titleId = titleId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMainCommentid(int mainCommentid) {
        this.mainCommentid = mainCommentid;
    }

    public void setTargetUserId(int targetUserId) {
        this.targetUserId = targetUserId;
    }

    public void setIs_creator(boolean is_creator) {
        this.is_creator = is_creator;
        setLFCStatus();
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
        setLFCStatus();
    }

    public void setRewarded(boolean rewarded) {
        this.rewarded = rewarded;
        setLFCStatus();
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
        setLFCStatus();
    }

    public void setTargetUserMainId(int targetUserMainId) {
        this.targetUserMainId = targetUserMainId;
    }

    public void setApiPost(ApiPost apiPost) {
        this.apiPost = apiPost;
    }

    public void setChildCommentAdapter(BaseQuickAdapter childCommentAdapter) {
        this.childCommentAdapter = childCommentAdapter;
    }

    public void setCommentAdapter(CommentAdapter commentAdapter) {
        this.commentAdapter = commentAdapter;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }


    private void setLFCStatus() {
        if (is_creator){
            zanIcon.setVisibility(GONE);
            rewardIcon.setVisibility(VISIBLE);
        }else {
            zanIcon.setVisibility(VISIBLE);
            rewardIcon.setVisibility(GONE);
        }
        if (liked){
            zanIcon.setImageDrawable(getResources().getDrawable(R.mipmap.ic_zan_active));
        }else {
            zanIcon.setImageDrawable(getResources().getDrawable(R.mipmap.ic_zan_normal));
        }
        if (marked){
            markIcon.setImageDrawable(getResources().getDrawable(R.mipmap.ic_mark_active));
        }else {
            markIcon.setImageDrawable(getResources().getDrawable(R.mipmap.ic_mark_normal));
        }
    }

    public void  setRequestFocus(){
        editRC.requestFocus();
        KeyBoardUtils.openKeybord(editRC,context);
        if (fromUserName!=null&&fromUserName.length()!=0&&targetUserId!=0){
            editRC.setHint("回复 "+fromUserName+":");
        }else {
            editRC.setHint("回复 :");
        }
    }

    public void setNetAndListener(){

        setView();
        jumpBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = jumpBtn.getText().toString();
                Intent toSearchActivityIntent = new Intent(getContext(), ReplyAndCommentActivity.class);
                toSearchActivityIntent.putExtra("type",type);
                toSearchActivityIntent.putExtra("subType",subType);
                toSearchActivityIntent.putExtra("id",id);
                toSearchActivityIntent.putExtra("titleId",titleId);
                toSearchActivityIntent.putExtra("status",status);
                toSearchActivityIntent.putExtra("mainCommentid",mainCommentid);
                toSearchActivityIntent.putExtra("targetUserId",targetUserId);
                toSearchActivityIntent.putExtra("targetUserMainId",targetUserMainId);
                toSearchActivityIntent.putExtra("fromUserName",fromUserName);
                toSearchActivityIntent.putExtra("content",content);

                //版本大于5.0的时候带有动画
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    context.startActivity(toSearchActivityIntent, ActivityOptions.makeSceneTransitionAnimation((Activity) context,
                            view, "ToReplyAndCommentActivity").toBundle());
                }else {
                    context.startActivity(toSearchActivityIntent);
                }
            }
        });
        sendBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = editRC.getText().toString();
                if (TextUtils.isEmpty(content)){
                }else {
                    KeyBoardUtils.closeKeybord(editRC,context);
                    sendBtn.setClickable(false);
                    sendNet();
                }
            }
        });

        zanIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setNetToToggle(NET_STATUS_TOGGLE_LIKE);
                zanIcon.setClickable(false);
            }
        });
        markIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setNetToToggle(NET_STATUS_TOGGLE_COLLENTION);
                markIcon.setClickable(false);
            }
        });
        rewardIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rewarded){
                    ToastUtils.showShort(context,"只能投食一次哦，您已经投食过了，请勿重复投食~");
                }else {

                    rewardDialog = new AlertDialog.Builder(context)
                            .setTitle("投食")
                            .setMessage("确认投食将会消耗您1团子哦")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    rewardDialog.dismiss();
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    rewardIcon.setClickable(false);
                                    setNetToToggle(NET_STATUS_TOGGLE_REWARDED);
                                    rewardDialog.dismiss();
                                }
                            })
                            .create();
                    rewardDialog.show();
                }
            }
        });
    }

    private void setView() {
        editRC.setHint("说点什么吧...");
        jumpBtn.setHint("说点什么吧...");

        if (status == STATUS_MAIN_COMMENT){
            btnLayout.setVisibility(VISIBLE);
            editLayout.setVisibility(GONE);
        }else if (status == STATUS_SUB_COMMENT){
            btnLayout.setVisibility(GONE);
            editLayout.setVisibility(VISIBLE);
        }
        fromUserName = "";
        targetUserId = 0;
        jumpBtn.setText("");

        setLFCStatus();

    }

    private void setNetToToggle(int NET_TOGGLE_STATUS) {

        if (NET_TOGGLE_STATUS == NET_STATUS_TOGGLE_LIKE){
            RetrofitManager.getInstance().getService(APIService.class)
                    .getTrendingToggleLike(type,id)
                    .compose(Rx2Schedulers.applyObservableAsync())
                    .subscribe(new ObserverWrapper<Boolean>(){
                        @Override
                        public void onSuccess(Boolean isLike) {
                            if (isLike){
                                ToastUtils.showShort(context,"点赞成功");
                            }else {
                                ToastUtils.showShort(context,"取消赞成功");
                            }
                            setLikeSuccess(isLike);
                            zanIcon.setClickable(true);
                        }

                        @Override
                        public void onFailure(int code, String errorMsg) {
                            super.onFailure(code, errorMsg);
                            if (zanIcon!=null){
                                zanIcon.setClickable(true);
                                setLikeFail();
                            }
                        }
                    });
        }
        if (NET_TOGGLE_STATUS == NET_STATUS_TOGGLE_COLLENTION) {
            RetrofitManager.getInstance().getService(APIService.class)
                    .getTrendingToggleCollection(type,id)
                    .compose(Rx2Schedulers.applyObservableAsync())
                    .subscribe(new ObserverWrapper<Boolean>(){
                        @Override
                        public void onSuccess(Boolean isMark) {
                            if (isMark) {
                                ToastUtils.showShort(context, "收藏成功");
                            } else {
                                ToastUtils.showShort(context, "取消收藏成功");
                            }
                            setMarkSuccess(isMark);
                            markIcon.setClickable(true);
                        }

                        @Override
                        public void onFailure(int code, String errorMsg) {
                            super.onFailure(code, errorMsg);
                            if (markIcon!=null){
                                markIcon.setClickable(true);
                                setMarkFail();
                            }
                        }
                    });
        }

        if (NET_TOGGLE_STATUS == NET_STATUS_TOGGLE_REWARDED) {
            RetrofitManager.getInstance().getService(APIService.class)
                    .getTrendingToggleReward(type,id)
                    .compose(Rx2Schedulers.applyObservableAsync())
                    .subscribe(new ObserverWrapper<Boolean>(){
                        @Override
                        public void onSuccess(Boolean isRewarded) {
                            if (isRewarded){
                                ToastUtils.showShort(context, "投食成功，消耗1团子");
                                rewarded = true;
                                Constants.userInfoData.setCoin(Constants.userInfoData.getCoin()-1);
                                Intent intent = new Intent(MineFragment.COINCHANGE);
                                context.sendBroadcast(intent);
                                setRewardSuccess();
                                rewardIcon.setClickable(true);
                            }else {
                                ToastUtils.showShort(context,"请勿重复投食");
                            }
                        }

                        @Override
                        public void onFailure(int code, String errorMsg) {
                            super.onFailure(code, errorMsg);
                            if (rewardIcon!=null){
                                rewardIcon.setClickable(true);
                                setRewardFail();
                            }
                        }
                    });
        }

    }


    private void sendNet() {

        if (status == STATUS_MAIN_COMMENT){

            CreateMainComment createMainComment = new CreateMainComment();
            createMainComment.setContent(editRC.getText().toString());
            createMainComment.setType(type);
            createMainComment.setId(id);
            createMainComment.setImages(new ArrayList<QiniuImageParams.QiniuImageParamsData>());
            RetrofitManager.getInstance().getService(APIService.class)
                    .getCreateMainComment(createMainComment)
                    .compose(Rx2Schedulers.<Response<ResponseBean<CreateMainCommentInfo>>>applyObservableAsync())
                    .subscribe(new ObserverWrapper<CreateMainCommentInfo>() {
                        @Override
                        public void onSuccess(CreateMainCommentInfo info) {
                            createMainCommentInfo = info;
                            if (type.equals(TYPE_POST)){
                                commentAdapter.addData(info.getData());
                            }else {
                                commentAdapter.addData(0,info.getData());
                            }
                            if (Constants.userInfoData!=null&&Constants.userInfoData.getId()!=titleId){
                                ToastUtils.showShort(getContext(),info.getMessage());
                                Intent intent = new Intent(MineFragment.EXPCHANGE);
                                intent.putExtra("expChangeNum",info.getExp());
                                context.sendBroadcast(intent);
                            }else {
                                ToastUtils.showShort(getContext(),"评论成功！");
                            }
                            editRC.setText("");
                            editRC.clearFocus();
                            sendBtn.setClickable(true);
                        }

                        @Override
                        public void onFailure(int code, String errorMsg) {
                            super.onFailure(code, errorMsg);
                            if (sendBtn!=null){
                                sendBtn.setClickable(true);
                            }
                        }
                    });
        }else if (status == STATUS_SUB_COMMENT){
            //如果回复的是父评论 设置父评论的用户ID，否则使用子评论用户ID
            if (targetUserId == 0)
                targetUserId = targetUserMainId;
            RetrofitManager.getInstance().getService(APIService.class)
                    .getReplyComment(editRC.getText().toString(),type,mainCommentid,targetUserId)
                    .compose(Rx2Schedulers.applyObservableAsync())
                    .subscribe(new ObserverWrapper<ReplyCommentInfo>(){
                        @Override
                        public void onSuccess(ReplyCommentInfo info) {
                            replyCommentInfo = info;
                            childCommentAdapter.addData(info.getData());
                            if (Constants.userInfoData!=null&&Constants.userInfoData.getId()!=targetUserId){
                                ToastUtils.showShort(getContext(),info.getMessage());
                                Intent intent = new Intent(MineFragment.EXPCHANGE);
                                intent.putExtra("expChangeNum",info.getExp());
                                context.sendBroadcast(intent);
                            }else {
                                ToastUtils.showShort(getContext(),"回复成功！");
                            }
                            editRC.setText("");
                            editRC.clearFocus();
                            sendBtn.setClickable(true);
                        }

                        @Override
                        public void onFailure(int code, String errorMsg) {
                            super.onFailure(code, errorMsg);
                            if (sendBtn!=null){
                                sendBtn.setClickable(true);
                            }
                        }
                    });
        }

    }


    private void setLikeSuccess(boolean isLiked) {
        if (onLFCNetFinish!=null){
            onLFCNetFinish.onLikeFinish(isLiked);
        }
        if (isLiked){
            zanIcon.setImageDrawable(getResources().getDrawable(R.mipmap.ic_zan_active));
        }else {
            zanIcon.setImageDrawable(getResources().getDrawable(R.mipmap.ic_zan_normal));
        }
    }
    private void setMarkSuccess(boolean isMarked) {
        if (onLFCNetFinish!=null){
            onLFCNetFinish.onMarkFinish(isMarked);
        }
        if (isMarked){
            markIcon.setImageDrawable(getResources().getDrawable(R.mipmap.ic_mark_active));
        }else {
            markIcon.setImageDrawable(getResources().getDrawable(R.mipmap.ic_mark_normal));
        }
    }
    private void setRewardSuccess() {
        if (onLFCNetFinish!=null){
            onLFCNetFinish.onRewardFinish();
        }
    }
    private void setLikeFail() {

    }
    private void setMarkFail() {

    }
    private void setRewardFail() {

    }

    public static ReplyAndCommentView getInstance() {
        return instance;
    }

    private OnLFCNetFinish onLFCNetFinish;

    public void setOnLFCNetFinish(OnLFCNetFinish onLFCNetFinish){
        this.onLFCNetFinish = onLFCNetFinish;
    }

    public interface OnLFCNetFinish{
        void onRewardFinish();
        void onLikeFinish(boolean isLike);
        void onMarkFinish(boolean isMark);
    }

    private OnStartActivityListener onStartActivityListener;

    public void setOnStartActivityListener(OnStartActivityListener onStartActivityListener) {
        this.onStartActivityListener = onStartActivityListener;
    }

    public interface OnStartActivityListener{
        void onStartActivity(Intent intent,View view);
    }

    public void setResultContent(String contentStr){
        jumpBtn.setText(contentStr);
    }


}
