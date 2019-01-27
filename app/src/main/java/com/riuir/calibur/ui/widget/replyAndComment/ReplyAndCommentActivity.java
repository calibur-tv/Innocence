package com.riuir.calibur.ui.widget.replyAndComment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.KeyBoardUtils;
import com.riuir.calibur.assistUtils.PermissionUtils;
import com.riuir.calibur.assistUtils.SharedPreferencesUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.Drama.DramaCartoonCommentActivity;
import com.riuir.calibur.ui.home.Drama.DramaVideoPlayActivity;
import com.riuir.calibur.ui.home.MineFragment;
import com.riuir.calibur.ui.home.adapter.CommentAdapter;
import com.riuir.calibur.ui.home.card.CardChildCommentActivity;
import com.riuir.calibur.ui.home.message.MessageShowCommentActivity;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.QiniuUtils;
import com.riuir.calibur.utils.album.MyAlbumUtils;
import com.yanzhenjie.album.AlbumFile;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import calibur.core.http.models.base.ResponseBean;
import calibur.core.http.models.comment.CreateMainCommentInfo;
import calibur.core.http.models.comment.ReplyCommentInfo;
import calibur.core.http.models.comment.params.CreateMainComment;
import calibur.core.http.models.qiniu.params.QiniuImageParams;
import calibur.core.http.models.user.MineUserInfo;
import calibur.core.http.observer.ObserverWrapper;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Response;

public class ReplyAndCommentActivity extends BaseActivity {

    boolean imageVisibility = false;
    boolean picturePermission = true;

    @BindView(R.id.reply_and_comment_activity_empty)
    RelativeLayout emptyLayout;
    @BindView(R.id.reply_and_comment_activity_edit_layout)
    RelativeLayout editLayout;
    @BindView(R.id.reply_and_comment_activity_edit)
    EditText editText;
    @BindView(R.id.reply_and_comment_activity_button_layout)
    RelativeLayout btnLayout;
    @BindView(R.id.reply_and_comment_activity_send_btn)
    TextView sendBtn;
    @BindView(R.id.reply_and_comment_activity_image_btn)
    ImageView imgBtn;
    @BindView(R.id.reply_and_comment_activity_image_layout)
    RecyclerView imageRecycler;
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
    String content;

    //创建主评论的列表adapter
    CommentAdapter commentAdapter;
    //回复评论的列表adapter
    BaseQuickAdapter childCommentAdapter;

    ReplyCommentInfo replyCommentInfo;
    CreateMainCommentInfo createMainCommentInfo;

    ReplyAndCommentView RCView;

    ReplyAndCommentImageChoosedAdapter imageChoosedAdapter;

    List<String> baseChoosedImgList = new ArrayList<>();
    List<String> choosedImgList;
    List<String> uploadUmgUrlList;

    MyAlbumUtils myAlbumUtils;
    QiniuUtils qiniuUtils;
    ArrayList<QiniuImageParams.QiniuImageParamsData> qiniuImgList;

    MineUserInfo userInfoData;

    SweetAlertDialog upLoadDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_reply_and_comment;
    }

    @Override
    protected void onInit() {
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        subType = intent.getStringExtra("subType");
        status = intent.getIntExtra("status", 0);
        id = intent.getIntExtra("id", 0);
        titleId = intent.getIntExtra("titleId", 0);
        mainCommentid = intent.getIntExtra("mainCommentid", 0);
        targetUserId = intent.getIntExtra("targetUserId", 0);
        targetUserMainId = intent.getIntExtra("targetUserMainId", 0);
        fromUserName = intent.getStringExtra("fromUserName");
        content = intent.getStringExtra("content");
        myAlbumUtils = new MyAlbumUtils();
        qiniuUtils = new QiniuUtils();

        if (Constants.userInfoData != null) {
            userInfoData = Constants.userInfoData;
        } else {
            Constants.userInfoData = SharedPreferencesUtils.getUserInfoData(App.instance());
            userInfoData = Constants.userInfoData;
        }

        setView();
        setChoosedImgAdapter();
        setListener();
        setViewChangedListener();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                editText.setTransitionName("ToReplyAndCommentActivity");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setView() {
        RCView = ReplyAndCommentView.getInstance();
        if (status == ReplyAndCommentView.STATUS_MAIN_COMMENT) {
            //主评论
            getCommentAdapter();
        } else if (status == ReplyAndCommentView.STATUS_SUB_COMMENT) {
            //子评论
            getChildCommentAdapter();
        }

        if (type.equals(ReplyAndCommentView.TYPE_POST)) {
            imgBtn.setVisibility(View.VISIBLE);
            editText.setMaxLines(1000);
        } else {
            imgBtn.setVisibility(View.GONE);
            editText.setMaxLines(200);
        }

        if (content != null && content.length() != 0) {
            editText.setText(content);
            editText.setSelection(editText.getText().toString().length());
        }
    }

    private void getCommentAdapter() {
        switch (type) {
            case ReplyAndCommentView.TYPE_POST:
//                CardShowInfoActivity card = CardShowInfoActivity.getInstance();
//                commentAdapter = card.getCommentAdapter();
                break;
            case ReplyAndCommentView.TYPE_IMAGE:
//                ImageShowInfoActivity img = ImageShowInfoActivity.getInstance();
//                commentAdapter = img.getCommentAdapter();
                break;
            case ReplyAndCommentView.TYPE_CARTOON:
                DramaCartoonCommentActivity dcc = DramaCartoonCommentActivity.getInstance();
                commentAdapter = dcc.getCommentAdapter();
                break;
            case ReplyAndCommentView.TYPE_SCORE:
//                ScoreShowInfoActivity score = ScoreShowInfoActivity.getInstance();
//                commentAdapter = score.getCommentAdapter();
                break;
            case ReplyAndCommentView.TYPE_VIDEO:
                DramaVideoPlayActivity video = DramaVideoPlayActivity.getInstance();
                commentAdapter = video.getCommentAdapter();
                break;
            default:
                break;
        }
    }

    private void getChildCommentAdapter() {
        switch (subType) {
            case ReplyAndCommentView.TYPE_SUB_COMMENT:
                CardChildCommentActivity childComment = CardChildCommentActivity.getInstance();
                childCommentAdapter = childComment.getCommentAdapter();
                break;
            case ReplyAndCommentView.TYPE_SUB_MESSAGE:
                MessageShowCommentActivity msgComment = MessageShowCommentActivity.getInstance();
                childCommentAdapter = msgComment.getCommentAdapter();
                break;
            default:
                break;
        }
    }

    private void setChoosedImgAdapter() {

        imageChoosedAdapter = new ReplyAndCommentImageChoosedAdapter(R.layout.reply_and_comment_choosed_image_list_item,
                baseChoosedImgList, ReplyAndCommentActivity.this);
        LinearLayoutManager lm = new LinearLayoutManager(ReplyAndCommentActivity.this);
        lm.setOrientation(LinearLayoutManager.HORIZONTAL);
        imageRecycler.setLayoutManager(lm);
        imageRecycler.setAdapter(imageChoosedAdapter);
        imageChoosedAdapter.addData("add");
    }

    private void setViewChangedListener() {
//        editText.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
//            //当键盘弹出隐藏的时候会 调用此方法。
//            @Override
//            public void onGlobalLayout() {
//                Rect r = new Rect();
//                //获取当前界面可视部分
//                ReplyAndCommentActivity.this.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
//                //获取屏幕的高度
//                int realScreenHeight = DeviceInfoUtil.getScreenHeight();
//                int screenHeight =  ScreenUtils.getScreenHeight();
//                int diff = realScreenHeight - screenHeight;
//                LogUtils.d("JASON","realScreenHeight = " + realScreenHeight);
//                LogUtils.d("JASON","screenHeight = " + screenHeight);
//                //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
//                int heightDifference = realScreenHeight - diff - (r.bottom);
//                LogUtils.d("JASON", "Size: " + heightDifference+",screenHeight = "+screenHeight+",bottom = "+r.bottom+",top = "+r.top);
//                if (heightDifference!=0){
//                    //键盘开启
//                    bottomLayout.setTranslationY(-heightDifference);
//                }else {
//                    //键盘关闭 高度为0
//                    bottomLayout.setTranslationY(0);
//                }
//            }
//        });
    }

    private void setListener() {

        KeyboardVisibilityEvent.setEventListener(
                ReplyAndCommentActivity.this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        // some code depending on keyboard visiblity status
                        if (isOpen) {
                            imageRecycler.setVisibility(View.GONE);
                            imageVisibility = false;
                        }
                    }
                });

        emptyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyBoardUtils.closeKeybord(editText, ReplyAndCommentActivity.this);
                onBackPressed();
            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = editText.getText().toString();
                if (TextUtils.isEmpty(content)) {
                } else {
                    KeyBoardUtils.closeKeybord(editText, ReplyAndCommentActivity.this);
                    sendBtn.setClickable(false);
                    setCheckUpLoad();
                }
            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                String content = editText.getText().toString();
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    KeyBoardUtils.closeKeybord(editText, ReplyAndCommentActivity.this);
                    if (TextUtils.isEmpty(content)) {
                        return true;
                    }
                    setCheckUpLoad();
                    return true;

                }
                return false;
            }
        });

        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageVisibility) {
                    imageRecycler.setVisibility(View.GONE);
                    imageVisibility = false;
                } else {
                    PermissionUtils.chekReadAndWritePermission(ReplyAndCommentActivity.this);
                    KeyBoardUtils.closeKeybord(editText, ReplyAndCommentActivity.this);
                    imageRecycler.setVisibility(View.VISIBLE);
                    imageVisibility = true;
                }

            }
        });

        imageChoosedAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                switch (view.getId()) {
                    case R.id.reply_and_comment_choosed_image_add_image_btn:
                        if (picturePermission) {
                            //相册
                            myAlbumUtils.setChooseImage(ReplyAndCommentActivity.this, 9);
                            myAlbumUtils.setOnChooseImageFinishListener(new MyAlbumUtils.OnChooseImageFinishListener() {
                                @Override
                                public void onFinish(ArrayList<AlbumFile> albumFiles) {
                                    setChooseFinishImageLoad(albumFiles);
                                }
                            });
                        }

                        break;
                    case R.id.reply_and_comment_choosed_image_item_image_delete:
                        ArrayList<String> newUrls = (ArrayList<String>) adapter.getData();
                        newUrls.remove(position);
                        adapter.setNewData(newUrls);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void setCheckUpLoad() {

        upLoadDialog = new SweetAlertDialog(ReplyAndCommentActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        upLoadDialog.setTitle("发送中...");
        upLoadDialog.setContentText("您的评论正在发送中...");
        upLoadDialog.setCancelable(false);
        upLoadDialog.show();

        uploadUmgUrlList = imageChoosedAdapter.getData();
        uploadUmgUrlList.remove("add");
        if (uploadUmgUrlList.size() > 0) {
//            new ArrayList<QiniuImageParams.QiniuImageParamsData>()
            setQiniuUpLoad();
        } else {
            qiniuImgList = new ArrayList<>();
            sendNet();
        }
    }

    private void setQiniuUpLoad() {
        qiniuUtils.getQiniuUpToken(ReplyAndCommentActivity.this, uploadUmgUrlList, userInfoData.getId(), "all");
        qiniuUtils.setOnQiniuUploadFailedListnener(new QiniuUtils.OnQiniuUploadFailedListnener() {
            @Override
            public void onFailed(String fialMessage) {
                setUpLoadDiaLogFail(fialMessage);
            }
        });
        qiniuUtils.setOnQiniuUploadSuccessedListnener(new QiniuUtils.OnQiniuUploadSuccessedListnener() {
            @Override
            public void onUploadSuccess(ArrayList<QiniuImageParams.QiniuImageParamsData> imageParamsDataList) {
                qiniuImgList = imageParamsDataList;
                sendNet();
            }
        });
    }

    private void setChooseFinishImageLoad(ArrayList<AlbumFile> albumFiles) {
        if (choosedImgList == null) {
            choosedImgList = new ArrayList<>();
        }
        imageChoosedAdapter.remove(imageChoosedAdapter.getData().size() - 1);
        if ((albumFiles.size() + choosedImgList.size()) > 9) {
            ToastUtils.showShort(ReplyAndCommentActivity.this, "所选图片超出9张，自动保存本次所选");
            choosedImgList.clear();
        }
        for (int i = 0; i < albumFiles.size(); i++) {
            choosedImgList.add(albumFiles.get(i).getPath());
        }
        imageChoosedAdapter.setNewData(choosedImgList);
        imageChoosedAdapter.addData("add");

    }

    private void sendNet() {

        String upType;
        if (type.equals(ReplyAndCommentView.TYPE_CARTOON)) {
            upType = "image";
        } else {
            upType = type;
        }
        if (status == ReplyAndCommentView.STATUS_MAIN_COMMENT) {

            CreateMainComment createMainComment = new CreateMainComment();
            createMainComment.setContent(editText.getText().toString());
            createMainComment.setType(upType);
            createMainComment.setId(id);
            createMainComment.setImages(qiniuImgList);
            apiService.getCreateMainComment(createMainComment)
                    .compose(Rx2Schedulers.<Response<ResponseBean<CreateMainCommentInfo>>>applyObservableAsync())
                    .subscribe(new ObserverWrapper<CreateMainCommentInfo>() {
                        @Override
                        public void onSuccess(CreateMainCommentInfo info) {
                            createMainCommentInfo = info;
                            if (commentAdapter != null) {
                                if (type.equals(ReplyAndCommentView.TYPE_POST)) {
                                    commentAdapter.addData(info.getData());
                                } else {
                                    commentAdapter.addData(0, info.getData());
                                }
                            }
                            RCView.setMainCommentSuccessResult(info);

                            if (Constants.userInfoData != null && Constants.userInfoData.getId() != titleId) {
                                ToastUtils.showShort(ReplyAndCommentActivity.this, info.getMessage());
                                Intent intent = new Intent(MineFragment.EXPCHANGE);
                                intent.putExtra("expChangeNum", info.getExp());
                                sendBroadcast(intent);
                            } else {
                                ToastUtils.showShort(ReplyAndCommentActivity.this, "评论成功！");
                            }
                            editText.setText("");
                            editText.clearFocus();
                            upLoadDialog.setTitleText("发送成功!")
                                    .setContentText("您的评论发送成功!")
                                    .setConfirmText("好的")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            upLoadDialog.dismiss();
                                            onBackPressed();
                                        }
                                    })
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            sendBtn.setClickable(true);
                        }

                        @Override
                        public void onFailure(int code, String errorMsg) {
                            super.onFailure(code, errorMsg);
                            setUpLoadDiaLogFail(errorMsg);
                            sendBtn.setClickable(true);
                        }
                    });
        } else if (status == ReplyAndCommentView.STATUS_SUB_COMMENT) {
            //如果回复的是父评论 设置父评论的用户ID，否则使用子评论用户ID
            if (targetUserId == 0)
                targetUserId = targetUserMainId;
            apiService.getReplyComment(editText.getText().toString(), upType, mainCommentid, targetUserId)
                    .compose(Rx2Schedulers.applyObservableAsync())
                    .subscribe(new ObserverWrapper<ReplyCommentInfo>() {
                        @Override
                        public void onSuccess(ReplyCommentInfo info) {
                            replyCommentInfo = info;
                            childCommentAdapter.addData(info.getData());
                            if (Constants.userInfoData != null && Constants.userInfoData.getId() != targetUserId) {
                                ToastUtils.showShort(ReplyAndCommentActivity.this, info.getMessage());
                                Intent intent = new Intent(MineFragment.EXPCHANGE);
                                intent.putExtra("expChangeNum", info.getExp());
                                sendBroadcast(intent);
                            } else {
                                ToastUtils.showShort(ReplyAndCommentActivity.this, "回复成功！");
                            }
                            editText.setText("");
                            editText.clearFocus();
                            sendBtn.setClickable(true);
                        }

                        @Override
                        public void onFailure(int code, String errorMsg) {
                            super.onFailure(code, errorMsg);
                            if (sendBtn != null) {
                                setUpLoadDiaLogFail("");
                                sendBtn.setClickable(true);
                            }
                        }
                    });
        }

    }

    private void setUpLoadDiaLogFail(String fialMessage) {

        if (fialMessage.equals("")) {
            fialMessage = "网络异常";
        }
        upLoadDialog.setTitleText("上传失败!")
                .setContentText(fialMessage)
                .setConfirmText("确定")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        upLoadDialog.cancel();
                    }
                })
                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
    }

    @Override
    public void onBackPressed() {
        if (RCView != null) {
            RCView.setResultContent(editText.getText().toString());
        }
        super.onBackPressed();
    }

    @Override
    protected void handler(Message msg) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionUtils.REQUEST_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限被用户同意，可以做你要做的事情了。
                    picturePermission = true;
                } else {
                    // 权限被用户拒绝了，可以提示用户,关闭界面等等。
                    ToastUtils.showShort(ReplyAndCommentActivity.this, "未取得授权，无法选择图片");
                    picturePermission = false;
                    imageRecycler.setVisibility(View.GONE);
                    imageVisibility = false;
                }
                return;
            }
        }
    }

}
