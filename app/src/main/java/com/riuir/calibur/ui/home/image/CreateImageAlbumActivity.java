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
import com.riuir.calibur.assistUtils.PermissionUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.album.ChooseImageAlbum;

import com.riuir.calibur.data.qiniu.QiniuUpToken;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.MineFragment;
import com.riuir.calibur.ui.home.card.CardCreateNewActivity;
import com.riuir.calibur.ui.home.choose.ChooseNewCardBangumiActivity;
import com.riuir.calibur.ui.widget.SelectorImagesActivity;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.GlideUtils;
import com.riuir.calibur.utils.QiniuUtils;
import com.riuir.calibur.utils.album.MyAlbumUtils;
import com.riuir.calibur.utils.geetest.GeetestUtils;
import com.suke.widget.SwitchButton;
import com.tencent.bugly.crashreport.CrashReport;
import com.yanzhenjie.album.AlbumFile;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import calibur.core.http.models.create.CreateNewAlbumInfo;
import calibur.core.http.models.create.params.CreateNewAlbum;
import calibur.core.http.models.geetest.params.VerificationCodeBody;
import calibur.core.http.models.qiniu.params.QiniuImageParams;
import calibur.core.http.observer.ObserverWrapper;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
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
//    Configuration config;
//    UploadManager uploadManager;

    //极验
    GT3GeetestUtilsBind gt3GeetestUtilsBind;
    private VerificationCodeBody.VerificationCodeBodyGeeTest verificationCodeBodyGeeTest;
    GeetestUtils geetestUtils;

    ArrayList<String> baseImagesUrl;
    ArrayList<String> imagesUrlList = new ArrayList<>();
    ArrayList<String> uploadUrlList = new ArrayList<>();



    ArrayList<QiniuImageParams.QiniuImageParamsData> qiniuImageParamsDataList = new ArrayList<>();
    private int urlTag;
    private int userId;

    int bangumiId;
    String bangumiAvatar;
    String bangumiName;

    boolean isCreator = false;

    SweetAlertDialog upLoadDialog;

//    private CreateAlbumBroadcastReceiver receiver;
//    private IntentFilter intentFilter;

    AlertDialog cancelDialog;

    public static final int NEW_ALBUM_CODE = 201;

    QiniuUtils qiniuUtils;
    MyAlbumUtils myAlbumUtils;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_create_image_album;
    }

    @Override
    protected void onInit() {
        userId = Constants.userInfoData.getId();
        PermissionUtils.chekReadAndWritePermission(CreateImageAlbumActivity.this);
        myAlbumUtils = new MyAlbumUtils();

        gt3GeetestUtilsBind = new GT3GeetestUtilsBind(this);
        verificationCodeBodyGeeTest = new VerificationCodeBody.VerificationCodeBodyGeeTest();
//        registerReceiver();
        setListener();
    }

//    private void registerReceiver() {
//        receiver = new CreateAlbumBroadcastReceiver();
//        intentFilter = new IntentFilter();
//        intentFilter.addAction(GeetestUtils.FailAction);
//        registerReceiver(receiver,intentFilter);
//    }

    @Override
    public void onDestroy() {
        gt3GeetestUtilsBind.cancelUtils();
//        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        setCancelListener();
    }

    private void setCancelListener() {

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
                myAlbumUtils.setChooseImage(CreateImageAlbumActivity.this,1);
                myAlbumUtils.setOnChooseImageFinishListener(new MyAlbumUtils.OnChooseImageFinishListener() {
                    @Override
                    public void onFinish(ArrayList<AlbumFile> albumFiles) {
                        setChooseFinishImageLoad(albumFiles);
                    }
                });

            }
        });
        albumSrcImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myAlbumUtils.setChooseImage(CreateImageAlbumActivity.this,1);
                myAlbumUtils.setOnChooseImageFinishListener(new MyAlbumUtils.OnChooseImageFinishListener() {
                    @Override
                    public void onFinish(ArrayList<AlbumFile> albumFiles) {
                        setChooseFinishImageLoad(albumFiles);
                    }
                });

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
            }else if (uploadUrlList!=null&&uploadUrlList.size() == 0){
                ToastUtils.showShort(CreateImageAlbumActivity.this,"请选择封面");
            }else {
                ToastUtils.showShort(CreateImageAlbumActivity.this,"请完善您的上传内容");
            }
        }
    }

    private void setUpLoadDialog(){

        sendBtn.setClickable(false);
        sendBtn.setTextColor(getResources().getColor(R.color.color_FFEEEEEE));
        setGeeTestUtils();

    }

    private void setGeeTestUtils() {
        geetestUtils = new GeetestUtils();
        geetestUtils.setGeetestStart(CreateImageAlbumActivity.this,apiGet,
                gt3GeetestUtilsBind);
        geetestUtils.setOnGeetestFailedListener(new GeetestUtils.OnGeetestFailedListener() {
            @Override
            public void onFailed(String failedMessage) {
                setUpLoadDiaLogFail("");
            }
        });
        geetestUtils.setOnGeetestSuccessListener(new GeetestUtils.OnGeetestSuccessListener() {
            @Override
            public void onSuccess(VerificationCodeBody.VerificationCodeBodyGeeTest verificationCodeBodyGee) {
                verificationCodeBodyGeeTest = verificationCodeBodyGee;
                upLoadDialog = new SweetAlertDialog(CreateImageAlbumActivity.this,SweetAlertDialog.PROGRESS_TYPE);
                upLoadDialog.setTitle("上传中...");
                upLoadDialog.setContentText("您的相册正在上传中...");
                upLoadDialog.setCancelable(false);
                upLoadDialog.show();
                gt3GeetestUtilsBind.gt3TestFinish();
                setQiniuUpload();
            }
        });
    }

    private void setQiniuUpload(){
        qiniuUtils = new QiniuUtils();
        qiniuUtils.getQiniuUpToken(CreateImageAlbumActivity.this,uploadUrlList,userId,"image");
        qiniuUtils.setOnQiniuUploadFailedListnener(new QiniuUtils.OnQiniuUploadFailedListnener() {
            @Override
            public void onFailed(String fialMessage) {
                setUpLoadDiaLogFail(fialMessage);
            }
        });
        qiniuUtils.setOnQiniuUploadSuccessedListnener(new QiniuUtils.OnQiniuUploadSuccessedListnener() {
            @Override
            public void onUploadSuccess(ArrayList<QiniuImageParams.QiniuImageParamsData> imageParamsDataList) {
                qiniuImageParamsDataList = imageParamsDataList;
                setUpLoadAlbum();
            }
        });
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

        apiService.getCreateIAlbum(newAlbum)
                .compose(Rx2Schedulers.applyObservableAsync())
                .subscribe(new ObserverWrapper<CreateNewAlbumInfo>(){
                    @Override
                    public void onSuccess(CreateNewAlbumInfo info) {
                        final CreateNewAlbumInfo data = info;
                        final int id = data.getData().getId();
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
                                            intent.putExtra("album_id",data.getData().getId());
                                            intent.putExtra("albumName",data.getData().getName());
                                            intent.putExtra("albumPoster",data.getData().getPoster());
                                            setResult(NEW_ALBUM_CODE,intent);
                                            finish();
                                        }
                                    }
                                })
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        ToastUtils.showShort(CreateImageAlbumActivity.this,data.getMessage());
                        Intent intent = new Intent(MineFragment.EXPCHANGE);
                        intent.putExtra("expChangeNum",data.getExp());
                        sendBroadcast(intent);

                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        super.onFailure(code, errorMsg);
                        setUpLoadDiaLogFail(errorMsg);
                    }
                });
    }

    private void setUpLoadDiaLogFail(String error){
        if (upLoadDialog!=null){
            String content = "";
            if (error.contains("mimeType")){
                content = "所选部分图片格式不支持,\n 只支持jpg,jpeg,png,gif格式上传";
            }else if (error.length()!=0){
                content = error;
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

        if (resultCode == ChooseNewCardBangumiActivity.IMAGE_ALBUM && data!=null){
            bangumiId = data.getIntExtra("bangumiId",0);
            bangumiAvatar = data.getStringExtra("bangumiAvatar");
            bangumiName = data.getStringExtra("bangumiName");
            setBangumi();
        }
    }

    private void setChooseFinishImageLoad(ArrayList<AlbumFile> albumFiles){
        if (imagesUrlList == null){
            imagesUrlList = new ArrayList<>();
        }

        if (albumFiles.size()==1){
            imagesUrlList.clear();
            imagesUrlList.add(albumFiles.get(0).getPath());

            albumSrcImage.setVisibility(View.VISIBLE);
            albumAddImageLayout.setVisibility(View.GONE);
            GlideUtils.loadImageView(CreateImageAlbumActivity.this,imagesUrlList.get(0),albumSrcImage);
        }
    }


    @Override
    protected void handler(Message msg) {

    }

//    public class CreateAlbumBroadcastReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            setUpLoadDiaLogFail("");
//        }
//    }
}
