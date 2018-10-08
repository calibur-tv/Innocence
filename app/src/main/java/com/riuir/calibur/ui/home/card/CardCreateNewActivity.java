package com.riuir.calibur.ui.home.card;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.geetest.sdk.Bind.GT3GeetestBindListener;
import com.geetest.sdk.Bind.GT3GeetestUtilsBind;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.PermissionUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.params.VerificationCodeBody;
import com.riuir.calibur.data.params.newPost.CreatePostParams;
import com.riuir.calibur.data.params.QiniuImageParams;
import com.riuir.calibur.data.qiniu.QiniuUpToken;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.MineFragment;
import com.riuir.calibur.ui.home.choose.ChooseNewCardBangumiActivity;
import com.riuir.calibur.ui.home.adapter.CreateCardImageGridAdapter;

import com.riuir.calibur.ui.widget.SelectorImagesActivity;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.GlideUtils;
import com.riuir.calibur.utils.QiniuUtils;
import com.riuir.calibur.utils.geetest.GeetestUtils;
import com.suke.widget.SwitchButton;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CardCreateNewActivity extends BaseActivity {


    @BindView(R.id.card_create_new_activity_send)
    TextView sendBtn;
    @BindView(R.id.card_create_new_activity_cancel)
    ImageView cancelBtn;
    @BindView(R.id.card_create_new_activity_post_content_edit)
    EditText cardContentEdit;
    @BindView(R.id.card_create_new_activity_image_grid)
    RecyclerView addImageGrid;
    @BindView(R.id.card_create_new_activity_post_title_edit)
    EditText titleEdit;
    @BindView(R.id.card_create_new_activity_choose_bangumi_layout)
    RelativeLayout chooseBangumiLayout;
    @BindView(R.id.card_create_new_activity_choose_bangumi_icon)
    RoundedImageView chooseBangumiIcon;
    @BindView(R.id.card_create_new_activity_choose_bangumi_name)
    TextView chooseBangumiName;
    @BindView(R.id.card_create_new_activity_choose_bangumi_choose_btn)
    TextView chooseBangumiBtn;

    @BindView(R.id.card_create_new_activity_post_is_creator)
    SwitchButton isCreatorBtn;

    boolean isCreator;

    private int userId;

    ArrayList<String> baseImagesUrl;
    ArrayList<String> imagesUrl;

    List<String> baseUrlList = new ArrayList<>();
    List<String> urlList = new ArrayList<>();
    int urlTag = 0;

    boolean picturePermission = true;

    CreateCardImageGridAdapter imageGridAdapter;

    AlertDialog cancelDialog;
    SweetAlertDialog upLoadDialog;

    int bangumiId;
    String bangumiAvatar;
    String bangumiName;

    ArrayList<QiniuImageParams.QiniuImageParamsData> qiniuImageParamsDataList = new ArrayList<>();
    CreatePostParams createPostParams;
    //七牛
    Configuration config;
    UploadManager uploadManager;

    //极验
    GT3GeetestUtilsBind gt3GeetestUtilsBind;
    GT3GeetestBindListener bindListener;
    private VerificationCodeBody.VerificationCodeBodyGeeTest verificationCodeBodyGeeTest;

    private CreateCardBroadCastReceiver receiver;
    private IntentFilter intentFilter;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_card_create_new;
    }

    @Override
    protected void onInit() {
        //如果未登录 结束页面
        if (!Constants.ISLOGIN){
            ToastUtils.showShort(this,"登录之后才能发帖");
            finish();
        }
        setUserInfo();
        setView();
        PermissionUtils.chekReadAndWritePermission(CardCreateNewActivity.this);

        registerReceiver();
        gt3GeetestUtilsBind = new GT3GeetestUtilsBind(this);
        verificationCodeBodyGeeTest = new VerificationCodeBody.VerificationCodeBodyGeeTest();
        initGeetestBindListener();
        setListener();
        config = QiniuUtils.getQiniuConfig();
        uploadManager = new UploadManager(config);

    }

    private void registerReceiver() {
        receiver = new CreateCardBroadCastReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(GeetestUtils.FailAction);
        registerReceiver(receiver,intentFilter);
    }

    @Override
    public void onDestroy() {
        gt3GeetestUtilsBind.cancelUtils();
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void initGeetestBindListener() {
        bindListener = new GT3GeetestBindListener() {
            @Override
            public void gt3CloseDialog(int i) {
                super.gt3CloseDialog(i);
                setUpLoadDiaLogFail("");
            }

            @Override
            public void gt3DialogReady() {
                super.gt3DialogReady();
            }

            //用户是否自定义二次验证
            @Override
            public boolean gt3SetIsCustom() {
                return true;
            }

            @Override
            public void gt3GeetestStatisticsJson(JSONObject jsonObject) {
                super.gt3GeetestStatisticsJson(jsonObject);
            }

            /**
             * 自定义二次验证，也就是当gtSetIsCustom为ture时才执行
             * 拿到第二个url（API2）需要的数据
             * 在该回调里面自行请求api2
             * 对api2的结果进行处理
             * status 如果是true执行自定义接口2请求
             */
            @Override
            public void gt3GetDialogResult(boolean status, String result) {
//
                if (status){
                    //基本使用方法：

                    // 1.取出该接口返回的三个参数用于自定义二次验证
                    JSONObject res_json = null;
                    try {
                        res_json = new JSONObject(result);
                        verificationCodeBodyGeeTest.setGeetest_challenge(res_json.getString("geetest_challenge"));
                        verificationCodeBodyGeeTest.setGeetest_validate(res_json.getString("geetest_validate"));
                        verificationCodeBodyGeeTest.setGeetest_seccode(res_json.getString("geetest_seccode"));
                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                    }

                    LogUtils.d("registerLog","verificationCodeBodyGeeTest = "+verificationCodeBodyGeeTest.toString());
                    //检查和验证完成 开启上传
                    upLoadDialog = new SweetAlertDialog(CardCreateNewActivity.this,SweetAlertDialog.PROGRESS_TYPE);
                    upLoadDialog.setTitle("上传中...");
                    upLoadDialog.setContentText("您的帖子正在上传中...");
                    upLoadDialog.setCancelable(false);
                    upLoadDialog.show();
                    gt3GeetestUtilsBind.gt3TestFinish();
                    getQiniuToken();

                }

            }

            @Override
            public void gt3DialogOnError(String s) {
                super.gt3DialogOnError(s);
                setUpLoadDiaLogFail("");
            }
        };

    }

    private void setView() {
        if (baseImagesUrl == null){
            baseImagesUrl = new ArrayList<>();
        }

        imageGridAdapter = new CreateCardImageGridAdapter(R.layout.reply_card_image_grid_item,baseImagesUrl,CardCreateNewActivity.this);
        addImageGrid.setLayoutManager(new GridLayoutManager(CardCreateNewActivity.this,4));

        addImageGrid.setAdapter(imageGridAdapter);
        imageGridAdapter.addData("add");

    }

    private void setUserInfo() {
        if (Constants.userInfoData!=null){
            userId = Constants.userInfoData.getId();
        }else {
            ToastUtils.showShort(this,"用户数据异常丢失，请重启APP");
            finish();
        }
    }

    private void setCancelListener() {
//        cancelDialog = new SweetAlertDialog(CardCreateNewActivity.this, SweetAlertDialog.WARNING_TYPE);
//        cancelDialog.setTitleText("退出发帖")
//                .setContentText("退出的话，已编辑的数据不会保存哦")
//                .setCancelText("取消")
//                .setConfirmText("退出")
//                .showCancelButton(true)
//                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog sDialog) {
//                        sDialog.cancel();
//                    }
//                })
//                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog sweetAlertDialog) {
//                        finish();
//                    }
//                })
//                .show();

        cancelDialog = new AlertDialog.Builder(this)
                .setTitle("退出发帖")
                .setMessage("如果退出，此次编辑的内容不会保存，确定退出？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelDialog.cancel();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelDialog.dismiss();
                        finish();
                    }
                })
                .create();
        cancelDialog.show();

    }

    private void setListener() {


        chooseBangumiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CardCreateNewActivity.this, ChooseNewCardBangumiActivity.class);
                intent.putExtra("code",ChooseNewCardBangumiActivity.CARD);
                startActivityForResult(intent,ChooseNewCardBangumiActivity.CARD);
            }
        });
        imageGridAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                switch (view.getId()){
                    case R.id.reply_card_add_image_btn:
                        //相册
                        if (picturePermission){
                            Intent intent = new Intent(CardCreateNewActivity.this, SelectorImagesActivity.class);
                            intent.putExtra("code",SelectorImagesActivity.POST_CODE);
                            startActivityForResult(intent, SelectorImagesActivity.POST_CODE);
                        }else {
                            ToastUtils.showShort(CardCreateNewActivity.this,"未取得权限，无法选择图片");
                        }
                        break;
                    case R.id.reply_card_image_grid_item_image_delete:
                        ArrayList<String> newUrls = (ArrayList<String>) adapter.getData();
                        newUrls.remove(position);
                        adapter.setNewData(newUrls);
                        break;
                    default:
                        break;
                }
            }
        });
        isCreatorBtn.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                isCreator = isChecked;
            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInfo();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCancelListener();

            }
        });

    }


    private void setBangumi() {
        GlideUtils.loadImageView(CardCreateNewActivity.this,bangumiAvatar,chooseBangumiIcon);
        chooseBangumiName.setText(bangumiName);
    }

    private void checkInfo() {

        if (titleEdit.getText()!=null&&titleEdit.getText().toString().length()!=0&&
                cardContentEdit.getText()!=null&&cardContentEdit.getText().toString().length()!=0&&
                bangumiId!=0){
            sendBtn.setClickable(false);
            sendBtn.setTextColor(getResources().getColor(R.color.color_FFEEEEEE));
            GeetestUtils.setGeetestStart(CardCreateNewActivity.this,apiGet,bindListener,
                    verificationCodeBodyGeeTest,
                    gt3GeetestUtilsBind);

        }else {
            if (bangumiId == 0){
                ToastUtils.showShort(CardCreateNewActivity.this,"请选择所属番剧");
            }else {
                ToastUtils.showShort(CardCreateNewActivity.this,"请输入标题/内容");
            }
        }
    }

    private void getQiniuToken(){
        apiGetHasAuth.getCallQiniuUpToken().enqueue(new Callback<QiniuUpToken>() {
            @Override
            public void onResponse(Call<QiniuUpToken> call, Response<QiniuUpToken> response) {
                if (response!=null&&response.isSuccessful()){
                    Constants.QINIU_TOKEN = response.body().getData().getUpToken();
                    setCheckAndUpLoadImage();
                }else if (response!=null&&!response.isSuccessful()){
                    String errorStr = "";
                    try {
                        errorStr = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    Event<String> info =gson.fromJson(errorStr,Event.class);
                    ToastUtils.showShort(CardCreateNewActivity.this,info.getMessage());
                    setUpLoadDiaLogFail(info.getMessage());
                }else {
                    ToastUtils.showShort(CardCreateNewActivity.this,"未知错误导致上传失败");
                    setUpLoadDiaLogFail("返回值为空");
                }
            }

            @Override
            public void onFailure(Call<QiniuUpToken> call, Throwable t) {
                ToastUtils.showShort(CardCreateNewActivity.this,"网络异常，请稍后再试 \n t = "+t.getMessage());
                setUpLoadDiaLogFail(t.getMessage());
            }
        });
    }

    private void setCheckAndUpLoadImage() {
        if (config == null){
            config = QiniuUtils.getQiniuConfig();
        }
        if (uploadManager==null){
            // 重用uploadManager。一般地，只需要创建一个uploadManager对象
            uploadManager = new UploadManager(config);
        }

        if (urlList!=null&&urlList.size()!=0){
            urlList.clear();
        }

        baseUrlList = imageGridAdapter.getData();
        urlList = baseUrlList;
        urlList.remove("add");
        if (urlList!=null&&urlList.size()!=0){
            //有图片 上传图片
            urlTag = 0;
            qiniuImageParamsDataList.clear();
            setQiniuUpLoad(urlTag);
        }else {
            //无图片 上传帖子
            setUpLoadPost();
        }

    }

    private void setQiniuUpLoad(int tag) {
        //上传icon
        String iconkey = QiniuUtils.getQiniuUpKey(userId,"avatar",urlList.get(tag));
        File imgFile = new File(urlList.get(tag));
        if (iconkey!=null){
            uploadManager.put(imgFile, iconkey, Constants.QINIU_TOKEN, new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject response) {
                    LogUtils.d("newCardCreate","icon isOk = "+info.isOK());
                    if (info.isOK()){
                        Gson gson = new Gson();
                        QiniuImageParams params = gson.fromJson(response.toString(),QiniuImageParams.class);

                        qiniuImageParamsDataList.add(params.getData());
                        urlTag++;
                        if (urlTag<urlList.size()){
                            setQiniuUpLoad(urlTag);
                        }else {
                            //结束函数回调 发送上传到服务器请求
                            setUpLoadPost();
                        }
                        LogUtils.d("newCardCreate","icon url = "+params.getData().getUrl());
                    }else if(info.isCancelled()){
                        ToastUtils.showLong(CardCreateNewActivity.this,"取消上传");
                        setUpLoadDiaLogFail("取消上传");
                    }else if (info.isNetworkBroken()){
                        ToastUtils.showLong(CardCreateNewActivity.this,"网络异常，请稍后再试");
                        setUpLoadDiaLogFail("网络异常，请稍后再试");
                    }else{
                        ToastUtils.showLong(CardCreateNewActivity.this,"其他原因导致取消上传 \n info = "+info.error);

                        setUpLoadDiaLogFail(info.error);
                    }
                }
            },null);
        }
    }

    private void setUpLoadPost() {
        createPostParams = new CreatePostParams();
        createPostParams.setBangumiId(bangumiId);
        createPostParams.setContent(cardContentEdit.getText().toString());
        createPostParams.setImages(qiniuImageParamsDataList);
        createPostParams.setIs_creator(isCreator);
        createPostParams.setTitle(titleEdit.getText().toString());
        createPostParams.setGeetest(verificationCodeBodyGeeTest);
        //TODO 越过geetest
//        long time = System.currentTimeMillis()/1000;
        apiPost.getCreatPost(createPostParams).enqueue(new Callback<Event<Integer>>() {
            @Override
            public void onResponse(Call<Event<Integer>> call, Response<Event<Integer>> response) {
                if (response!=null&&response.isSuccessful()){
                    //创建帖子成功
                    //跳转进入帖子
                    final int id = response.body().getData();
                    upLoadDialog.setTitleText("上传成功!")
                            .setContentText("您的帖子上传成功!")
                            .setConfirmText("好的")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    if (id!=0){
                                        Intent intent = new Intent(CardCreateNewActivity.this,CardShowInfoActivity.class);
                                        intent.putExtra("cardID",id);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            })
                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    ToastUtils.showShort(CardCreateNewActivity.this,"发帖成功，经验+4!");
                    Intent intent = new Intent(MineFragment.EXPCHANGE);
                    intent.putExtra("expChangeNum",4);
                    sendBroadcast(intent);

                }else if (response!=null&&!response.isSuccessful()){
                    String errorStr = "";
                    try {
                        errorStr = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    Event<String> info =gson.fromJson(errorStr,Event.class);
                    ToastUtils.showShort(CardCreateNewActivity.this,info.getMessage());
                    setUpLoadDiaLogFail(info.getMessage());
                }else {
                    ToastUtils.showShort(CardCreateNewActivity.this,"未知错误导致上传失败");
                    setUpLoadDiaLogFail("返回值为空");
                }
            }

            @Override
            public void onFailure(Call<Event<Integer>> call, Throwable t) {
                ToastUtils.showShort(CardCreateNewActivity.this,"网络异常，请稍后再试 \n t = "+t.getMessage());
                setUpLoadDiaLogFail(t.getMessage());
            }
        });
    }

    private void setUpLoadDiaLogFail(String error){
        if (upLoadDialog!=null){
            String content = "";
            if (error.contains("mimeType")){
                content = "所选部分图片格式不支持,\n 只支持jpg,jpeg,png,gif格式上传";
            }else {
                content = "因网络原因，帖子上传失败了";
            }
            upLoadDialog.setTitleText("上传失败!")
                    .setContentText(content)
                    .setConfirmText("确定")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            upLoadDialog.cancel();
                        }
                    })
                    .changeAlertType(SweetAlertDialog.ERROR_TYPE);
        }
        urlTag = 0;
        sendBtn.setClickable(true);
        sendBtn.setTextColor(getResources().getColor(R.color.color_FFFFFFFF));
//        urlList.remove("add");
//        urlList.add("add");
    }

    @Override
    public void onBackPressed() {
        setCancelListener();
    }

    @Override
    protected void handler(Message msg) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取图片路径
        if ( resultCode == SelectorImagesActivity.POST_CODE && data != null) {
            imagesUrl = data.getStringArrayListExtra(SelectorImagesActivity.RESULT_LIST_NAME);

            imageGridAdapter.remove(imageGridAdapter.getData().size()-1);

            if (imageGridAdapter.getData().size()+imagesUrl.size()>9){
                ToastUtils.showShort(CardCreateNewActivity.this,"所选图片超出9张，自动保存本次所选");
                imageGridAdapter.setNewData(imagesUrl);
            }else {
                imageGridAdapter.addData(imagesUrl);
            }
            imageGridAdapter.addData("add");
        }
        if (resultCode == ChooseNewCardBangumiActivity.CARD&& data != null){
            bangumiId = data.getIntExtra("bangumiId",0);
            bangumiAvatar = data.getStringExtra("bangumiAvatar");
            bangumiName = data.getStringExtra("bangumiName");
            setBangumi();
        }

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
                    ToastUtils.showShort(CardCreateNewActivity.this, "未取得授权，无法选择图片");
                    picturePermission = false;
                }
                return;
            }
        }
    }

    public class CreateCardBroadCastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            setUpLoadDiaLogFail("");
        }
    }
}
