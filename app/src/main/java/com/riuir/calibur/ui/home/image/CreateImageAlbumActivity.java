package com.riuir.calibur.ui.home.image;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.album.ChooseImageAlbum;
import com.riuir.calibur.data.album.CreateNewAlbumInfo;
import com.riuir.calibur.data.params.QiniuImageParams;
import com.riuir.calibur.data.params.VerificationCodeBody;
import com.riuir.calibur.data.params.newImage.CreateNewAlbum;
import com.riuir.calibur.data.qiniu.QiniuUpToken;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.MineFragment;
import com.riuir.calibur.ui.home.card.CardCreateNewActivity;
import com.riuir.calibur.ui.home.choose.ChooseNewCardBangumiActivity;
import com.riuir.calibur.ui.widget.SelectorImagesActivity;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.GlideUtils;
import com.riuir.calibur.utils.QiniuUtils;
import com.riuir.calibur.utils.geetest.GeetestUtils;
import com.suke.widget.SwitchButton;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateImageAlbumActivity extends BaseActivity {

    @BindView(R.id.create_image_album_picture_name)
    EditText albumNameEdit;
    @BindView(R.id.create_image_album_choose_bangumi_layout)
    RelativeLayout bangumiLayout;
    @BindView(R.id.create_image_album_choose_bangumi_icon)
    RoundedImageView bangumiIcon;
    @BindView(R.id.create_image_album_choose_bangumi_name)
    TextView bangumiNameText;
    @BindView(R.id.create_image_album_is_creator)
    SwitchButton isCreatorSwitch;

    @BindView(R.id.create_image_album_send)
    TextView sendBtn;
    @BindView(R.id.create_image_album_image_add)
    RelativeLayout albumAddImageLayout;
    @BindView(R.id.create_image_album_image_src)
    ImageView albumSrcImage;
    @BindView(R.id.create_image_album_back)
    ImageView backBtn;

    //七牛
    Configuration config;
    UploadManager uploadManager;

    //极验
    GT3GeetestUtilsBind gt3GeetestUtilsBind;
    GT3GeetestBindListener bindListener;
    private VerificationCodeBody.VerificationCodeBodyGeeTest verificationCodeBodyGeeTest;

    ArrayList<String> baseImagesUrl;
    ArrayList<String> imagesUrlList;
    List<String> uploadUrlList;



    ArrayList<QiniuImageParams.QiniuImageParamsData> qiniuImageParamsDataList = new ArrayList<>();
    private int urlTag;
    private int userId;

    int bangumiId;
    String bangumiAvatar;
    String bangumiName;

    boolean isCreator = false;

    SweetAlertDialog upLoadDialog;

    private CreateAlbumBroadcastReceiver receiver;
    private IntentFilter intentFilter;

    AlertDialog cancelDialog;

    public static final int NEW_ALBUM_CODE = 201;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_create_image_album;
    }

    @Override
    protected void onInit() {
        userId = Constants.userInfoData.getId();
//        setImageAdapter();
        config = QiniuUtils.getQiniuConfig();
        uploadManager = new UploadManager(config);
        gt3GeetestUtilsBind = new GT3GeetestUtilsBind(this);
        verificationCodeBodyGeeTest = new VerificationCodeBody.VerificationCodeBodyGeeTest();
        registerReceiver();
        initGeetestBindListener();
        setListener();
    }

    private void registerReceiver() {
        receiver = new CreateAlbumBroadcastReceiver();
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

    @Override
    public void onBackPressed() {
        setCancelListener();
    }

    private void setCancelListener() {
//        cancelDialog = new SweetAlertDialog(CreateImageAlbumActivity.this, SweetAlertDialog.WARNING_TYPE);
//        cancelDialog.setTitleText("退出发图")
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
                .setTitle("退出创建相册")
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
                    upLoadDialog = new SweetAlertDialog(CreateImageAlbumActivity.this,SweetAlertDialog.PROGRESS_TYPE);
                    upLoadDialog.setTitle("上传中...");
                    upLoadDialog.setContentText("您的相册正在上传中...");
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

    private void setListener() {

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCancelListener();

            }
        });
        albumAddImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateImageAlbumActivity.this, SelectorImagesActivity.class);
                intent.putExtra("code",SelectorImagesActivity.IMAGE_CODE_ALBUM_AVATAR);
                startActivityForResult(intent, SelectorImagesActivity.IMAGE_CODE_ALBUM_AVATAR);
            }
        });
        albumSrcImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateImageAlbumActivity.this, SelectorImagesActivity.class);
                intent.putExtra("code",SelectorImagesActivity.IMAGE_CODE_ALBUM_AVATAR);
                startActivityForResult(intent, SelectorImagesActivity.IMAGE_CODE_ALBUM_AVATAR);
            }
        });

        isCreatorSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                isCreator = isChecked;
            }
        });
        bangumiLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateImageAlbumActivity.this, ChooseNewCardBangumiActivity.class);
                intent.putExtra("code",ChooseNewCardBangumiActivity.IMAGE_ALBUM);
                startActivityForResult(intent,ChooseNewCardBangumiActivity.IMAGE_ALBUM);
            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCheckInfo();
            }
        });
    }

    private void setCheckInfo(){
        uploadUrlList = imagesUrlList;
//        uploadUrlList.remove("add");
        if (uploadUrlList!=null&&uploadUrlList.size()!=0&&
                albumNameEdit.getText().toString()!=null&&
                albumNameEdit.getText().toString().length()!=0&&bangumiId!=0){
            setUpLoadDialog();
        }else {
            if (bangumiId == 0){
                ToastUtils.showShort(CreateImageAlbumActivity.this,"请选择所属番剧");
            }else if (uploadUrlList.size() == 0){
                ToastUtils.showShort(CreateImageAlbumActivity.this,"请选择封面");
            }else {
                ToastUtils.showShort(CreateImageAlbumActivity.this,"请完善您的上传内容");
            }
        }
    }

    private void setUpLoadDialog(){

        sendBtn.setClickable(false);
        sendBtn.setTextColor(getResources().getColor(R.color.color_FFEEEEEE));
        GeetestUtils.setGeetestStart(CreateImageAlbumActivity.this,apiGet,bindListener,
                verificationCodeBodyGeeTest,
                gt3GeetestUtilsBind);

    }

    private void getQiniuToken(){
        apiGetHasAuth.getCallQiniuUpToken().enqueue(new Callback<QiniuUpToken>() {
            @Override
            public void onResponse(Call<QiniuUpToken> call, Response<QiniuUpToken> response) {
                if (response!=null&&response.isSuccessful()){
                    Constants.QINIU_TOKEN = response.body().getData().getUpToken();
                    setQiniuUpLoadCheck();
                }else if (response!=null&&!response.isSuccessful()){
                    String errorStr = "";
                    try {
                        errorStr = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    Event<String> info =gson.fromJson(errorStr,Event.class);
                    ToastUtils.showShort(CreateImageAlbumActivity.this,info.getMessage());
                    setUpLoadDiaLogFail(info.getMessage());
                }else {
                    ToastUtils.showShort(CreateImageAlbumActivity.this,"未知错误导致上传失败");
                    setUpLoadDiaLogFail("返回值为空");
                }
            }

            @Override
            public void onFailure(Call<QiniuUpToken> call, Throwable t) {
                ToastUtils.showShort(CreateImageAlbumActivity.this,"网络异常，请稍后再试1");
                setUpLoadDiaLogFail("网络异常，请稍后再试 t ="+t.getMessage());
            }
        });
    }

    private void setQiniuUpLoadCheck(){
        if (uploadUrlList.size()!=0){
            urlTag = 0;
            qiniuImageParamsDataList.clear();
            setQiniuUpLoad(urlTag);
        }
    }

    private void setQiniuUpLoad(int tag) {
        //上传icon
        String iconkey = QiniuUtils.getQiniuUpKey(userId,"avatar",uploadUrlList.get(tag));
        File imgFile = new File(uploadUrlList.get(tag));
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
                        if (urlTag<uploadUrlList.size()){
                            setQiniuUpLoad(urlTag);
                        }else {
                            //结束函数回调 发送上传到服务器请求
                            setUpLoadAlbum();
                        }
                        LogUtils.d("newCardCreate","icon url = "+params.getData().getUrl());
                    }else if(info.isCancelled()){
                        ToastUtils.showShort(CreateImageAlbumActivity.this,"取消上传");
                        setUpLoadDiaLogFail("");
                    }else if (info.isNetworkBroken()){
                        ToastUtils.showShort(CreateImageAlbumActivity.this,"网络异常，请稍后再试2");
                        setUpLoadDiaLogFail("");
                    }else {
                        ToastUtils.showShort(CreateImageAlbumActivity.this,"其他原因导致取消上传 \n info = "+info.error);
                        setUpLoadDiaLogFail(info.error);
                    }
                }
            },null);
        }
    }

    private void setUpLoadAlbum() {
        CreateNewAlbum newAlbum = new CreateNewAlbum();
        newAlbum.setBangumi_id(bangumiId);
        newAlbum.setName(albumNameEdit.getText().toString());
        newAlbum.setIs_cartoon(false);
        newAlbum.setIs_creator(isCreator);
        QiniuImageParams.QiniuImageParamsData qiniuData = qiniuImageParamsDataList.get(0);
        newAlbum.setUrl(qiniuData.getUrl());
        newAlbum.setHeight(qiniuData.getHeight());
        newAlbum.setWidth(qiniuData.getWidth());
        newAlbum.setSize(qiniuData.getSize()+"");
        newAlbum.setType(qiniuData.getType());
        newAlbum.setGeetest(verificationCodeBodyGeeTest);

        apiPost.getCreateIAlbum(newAlbum)
                .enqueue(new Callback<CreateNewAlbumInfo>() {
                    @Override
                    public void onResponse(Call<CreateNewAlbumInfo> call, Response<CreateNewAlbumInfo> response) {
                        if (response!=null&&response.isSuccessful()){
                            final ChooseImageAlbum.ChooseImageAlbumData data = response.body().getData();
                            final int id = data.getId();
                            upLoadDialog.setTitleText("上传成功!")
                                    .setContentText("您的相册上传成功!")
                                    .setConfirmText("好的")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            if (id!=0){
//                                                Intent intent = new Intent(CreateImageAlbumActivity.this,ImageShowInfoActivity.class);
//                                                intent.putExtra("imageID",id);
//                                                startActivity(intent);
//                                                finish();
                                                Intent intent = new Intent();
                                                intent.putExtra("album_id",data.getId());
                                                intent.putExtra("albumName",data.getName());
                                                intent.putExtra("albumPoster",data.getPoster());
                                                setResult(NEW_ALBUM_CODE,intent);
                                                finish();
                                            }
                                        }
                                    })
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            ToastUtils.showShort(CreateImageAlbumActivity.this,"相册创建成功，经验+3!");
                            Intent intent = new Intent(MineFragment.EXPCHANGE);
                            intent.putExtra("expChangeNum",3);
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
                            ToastUtils.showShort(CreateImageAlbumActivity.this,info.getMessage());
                            setUpLoadDiaLogFail(info.getMessage());
                        }else {
                            ToastUtils.showShort(CreateImageAlbumActivity.this,"未知错误导致上传失败");
                            setUpLoadDiaLogFail("返回值为空");
                        }
                    }

                    @Override
                    public void onFailure(Call<CreateNewAlbumInfo> call, Throwable t) {
                        ToastUtils.showShort(CreateImageAlbumActivity.this,"网络异常，请稍后再试3");
                        LogUtils.d("newCardCreate","album t = "+t.getMessage());
                        setUpLoadDiaLogFail("网络异常，请稍后再试 t = "+t.getMessage());
                    }
                });
    }

    private void setUpLoadDiaLogFail(String error){
        if (upLoadDialog!=null){
            String content = "";
            if (error.contains("mimeType")){
                content = "所选部分图片格式不支持,\n 只支持jpg,jpeg,png,gif格式上传";
            }else {
                content = "因网络原因，相册创建失败了";
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

    }

    private void setBangumi() {
        GlideUtils.loadImageView(CreateImageAlbumActivity.this,bangumiAvatar,bangumiIcon);
        bangumiNameText.setText(bangumiName);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取图片路径

        if ( resultCode == SelectorImagesActivity.IMAGE_CODE_ALBUM_AVATAR && data != null) {

            if (data.getStringArrayListExtra(SelectorImagesActivity.RESULT_LIST_NAME)!=null&&
                    data.getStringArrayListExtra(SelectorImagesActivity.RESULT_LIST_NAME).size() ==1){
                imagesUrlList = data.getStringArrayListExtra(SelectorImagesActivity.RESULT_LIST_NAME);
                albumSrcImage.setVisibility(View.VISIBLE);
                albumAddImageLayout.setVisibility(View.GONE);
                GlideUtils.loadImageView(CreateImageAlbumActivity.this,imagesUrlList.get(0),albumSrcImage);
            }
        }
        if (resultCode == ChooseNewCardBangumiActivity.IMAGE_ALBUM && data!=null){
            bangumiId = data.getIntExtra("bangumiId",0);
            bangumiAvatar = data.getStringExtra("bangumiAvatar");
            bangumiName = data.getStringExtra("bangumiName");
            setBangumi();
        }

    }



    @Override
    protected void handler(Message msg) {

    }

    public class CreateAlbumBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            setUpLoadDiaLogFail("");
        }
    }
}
