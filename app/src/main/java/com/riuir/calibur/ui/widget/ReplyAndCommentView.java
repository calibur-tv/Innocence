package com.riuir.calibur.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.KeyBoardUtils;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.params.CreateMainComment;

import com.riuir.calibur.data.params.QiniuImageParams;
import com.riuir.calibur.data.trending.CreateMainCommentInfo;
import com.riuir.calibur.data.trending.ReplyCommentInfo;
import com.riuir.calibur.net.ApiPost;
import com.riuir.calibur.ui.home.MineFragment;
import com.riuir.calibur.ui.home.adapter.CommentAdapter;
import com.riuir.calibur.ui.home.card.CardChildCommentActivity;
import com.riuir.calibur.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReplyAndCommentView extends LinearLayout {

    Context context;
    EditText editRC;
//    TextView clearEdit;
    TextView sendBtn;

    String type;
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
    //创建主评论的列表adapter
    CommentAdapter commentAdapter;
    //回复评论的列表adapter
    BaseQuickAdapter childCommentAdapter;

    ReplyCommentInfo replyCommentInfo;
    CreateMainCommentInfo createMainCommentInfo;

    public static final int STATUS_MAIN_COMMENT = 0;
    public static final int STATUS_SUB_COMMENT = 1;

    public static final String TYPE_VIDEO = "video";
    public static final String TYPE_POST = "post";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_SCORE = "score";

    ApiPost apiPost;

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
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.reply_and_comment_view_layout, this, true);
        editRC = view.findViewById(R.id.reply_and_comment_edit);
//        clearEdit = view.findViewById(R.id.reply_and_comment_clear_edit);
        sendBtn = view.findViewById(R.id.reply_and_comment_send_btn);

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

    public void  setRequestFocus(){
        editRC.requestFocus();
        KeyBoardUtils.openKeybord(editRC,context);
        LogUtils.d("replycomment","targetUserId = "+targetUserId);
        if (fromUserName!=null&&fromUserName.length()!=0&&targetUserId!=0){
            editRC.setHint("回复 "+fromUserName+":");
//            clearEdit.setVisibility(VISIBLE);
        }else {
            editRC.setHint("回复 :");
        }
    }
    public void setNetAndListener(){
        editRC.setHint("回复 :");
        fromUserName = "";
        targetUserId = 0;
        editRC.setText("");
//        clearEdit.setVisibility(GONE);
        editRC.addTextChangedListener(textWatcher);

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


//        editRC.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int actionId , KeyEvent keyEvent) {
//                String content = editRC.getText().toString();
//                if (actionId  == EditorInfo.IME_ACTION_SEND){
//                    KeyBoardUtils.closeKeybord(editRC,context);
//                    if (TextUtils.isEmpty(content)){
//                        return true;
//                    }
//                    sendNet();
//                    return true;
//
//                }
//                return false;
//            }
//        });

//        clearEdit.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (editRC.getText().toString().length()!=0){
//                    editRC.setText("");
//                }else if (fromUserName!=null&&fromUserName.length()!=0&&targetUserId!=0
//                        &&editRC.getText().toString().length()==0){
//                    editRC.setHint("回复 :");
//                    fromUserName = "";
//                    targetUserId = 0;
//                    clearEdit.setVisibility(GONE);
//                }
//            }
//        });
    }

    private void sendNet() {

        if (status == STATUS_MAIN_COMMENT){

            CreateMainComment createMainComment = new CreateMainComment();
            createMainComment.setContent(editRC.getText().toString());
            createMainComment.setType(type);
            createMainComment.setId(id);
            createMainComment.setImages(new ArrayList<QiniuImageParams.QiniuImageParamsData>());
//            apiPost.getCreateMainComment(editRC.getText().toString(),type,id,new ArrayList<CreateMainCommentImg>())
            apiPost.getCreateMainComment(createMainComment).enqueue(new Callback<CreateMainCommentInfo>() {
                @Override
                public void onResponse(Call<CreateMainCommentInfo> call, Response<CreateMainCommentInfo> response) {
                    if (response!=null&&response.isSuccessful()){

                        createMainCommentInfo = response.body();
                        if (type.equals(TYPE_POST)){
                            commentAdapter.addData(createMainCommentInfo.getData().getData());
                        }else {
                            commentAdapter.addData(0,createMainCommentInfo.getData().getData());
                        }
                        if (Constants.userInfoData!=null&&Constants.userInfoData.getId()!=titleId){
                            ToastUtils.showShort(getContext(),createMainCommentInfo.getData().getMessage());
                            Intent intent = new Intent(MineFragment.EXPCHANGE);
                            intent.putExtra("expChangeNum",createMainCommentInfo.getData().getExp());
                            context.sendBroadcast(intent);
                        }else {
                            ToastUtils.showShort(getContext(),"评论成功！");
                        }
                        editRC.setText("");
                        editRC.clearFocus();


                    }else if (response!=null&&!response.isSuccessful()){
                        String errorStr = "";
                        try {
                            errorStr = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Gson gson = new Gson();
                        Event<String> info =gson.fromJson(errorStr,Event.class);

                        LogUtils.d("replycomment","code = "+info.getCode()+",me = "+info.getMessage());
                        ToastUtils.showShort(getContext(),info.getMessage());
                    }else {
                        ToastUtils.showShort(getContext(),"未知原因导致发送失败了！");
                    }
                    sendBtn.setClickable(true);
                }

                @Override
                public void onFailure(Call<CreateMainCommentInfo> call, Throwable t) {
                    ToastUtils.showShort(getContext(),"请检查您的网络！");
                    sendBtn.setClickable(true);
                }
            });
        }else if (status == STATUS_SUB_COMMENT){
            //如果回复的是父评论 设置父评论的用户ID，否则使用子评论用户ID
            if (targetUserId == 0)
                targetUserId = targetUserMainId;

            apiPost.getReplyComment(editRC.getText().toString(),type,mainCommentid,targetUserId).enqueue(new Callback<ReplyCommentInfo>() {
                @Override
                public void onResponse(Call<ReplyCommentInfo> call, Response<ReplyCommentInfo> response) {
                    if (response!=null&&response.isSuccessful()){
                        replyCommentInfo = response.body();
                        childCommentAdapter.addData(replyCommentInfo.getData().getData());
                        if (Constants.userInfoData!=null&&Constants.userInfoData.getId()!=targetUserId){
                            ToastUtils.showShort(getContext(),replyCommentInfo.getData().getMessage());
                            Intent intent = new Intent(MineFragment.EXPCHANGE);
                            intent.putExtra("expChangeNum",replyCommentInfo.getData().getExp());
                            context.sendBroadcast(intent);
                        }else {
                            ToastUtils.showShort(getContext(),"回复成功！");
                        }
                        editRC.setText("");
                        editRC.clearFocus();
                    }else if (response!=null&&!response.isSuccessful()){
                        String errorStr = "";
                        try {
                            errorStr = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Gson gson = new Gson();
                        Event<String> info =gson.fromJson(errorStr,Event.class);

                        ToastUtils.showShort(getContext(),info.getMessage());
                    }else {
                        ToastUtils.showShort(getContext(),"未知原因导致发送失败了！");
                    }
                    sendBtn.setClickable(true);
                }

                @Override
                public void onFailure(Call<ReplyCommentInfo> call, Throwable t) {
                    ToastUtils.showShort(getContext(),"请检查您的网络！");
                    sendBtn.setClickable(true);
                }
            });

        }

    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            if (charSequence.length()!=0){
//                clearEdit.setVisibility(View.VISIBLE);
//            }else if (fromUserName!=null&&fromUserName.length()!=0&&targetUserId!=0){
//                clearEdit.setVisibility(View.VISIBLE);
//            }else {
//                clearEdit.setVisibility(View.GONE);
//            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

}
