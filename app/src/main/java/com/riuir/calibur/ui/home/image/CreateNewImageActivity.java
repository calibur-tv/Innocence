package com.riuir.calibur.ui.home.image;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.DensityUtils;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.PermissionUtils;
import com.riuir.calibur.assistUtils.SharedPreferencesUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;

import com.riuir.calibur.data.qiniu.QiniuUpToken;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.DramaFragment;
import com.riuir.calibur.ui.home.DramaNewAnimeListFragment;
import com.riuir.calibur.ui.home.DramaRolesListFragment;
import com.riuir.calibur.ui.home.DramaTagsFragment;
import com.riuir.calibur.ui.home.DramaTimelineFragment;
import com.riuir.calibur.ui.home.MineFragment;
import com.riuir.calibur.ui.home.adapter.CreateCardImageGridAdapter;
import com.riuir.calibur.ui.home.card.CardCreateNewActivity;
import com.riuir.calibur.ui.home.choose.ChooseImageAlbumActivity;
import com.riuir.calibur.ui.home.choose.ChooseNewCardBangumiActivity;
import com.riuir.calibur.ui.view.MyPagerSlidingTabStrip;
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
import calibur.core.http.models.create.CreateCard;
import calibur.core.http.models.create.params.CreateNewImageForAlbum;
import calibur.core.http.models.create.params.CreateNewImageSingle;
import calibur.core.http.models.geetest.params.VerificationCodeBody;
import calibur.core.http.models.qiniu.params.QiniuImageParams;
import calibur.core.http.observer.ObserverWrapper;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateNewImageActivity extends BaseActivity {
    @BindView(R.id.create_new_image_activity_cancel)
    ImageView cancelBtn;


    @BindView(R.id.create_new_image_activity_picture_name)
    EditText pictureName;
    @BindView(R.id.create_new_image_activity_choose_bangumi_layout)
    RelativeLayout bangumiLayout;
    @BindView(R.id.create_new_image_activity_add_album_layout)
    RelativeLayout addAlbumLayout;
    @BindView(R.id.create_new_image_activity_choose_album_layout)
    RelativeLayout albumLayout;
    @BindView(R.id.create_new_image_activity_choose_bangumi_icon)
    RoundedImageView bangumiIcon;
    @BindView(R.id.create_new_image_activity_choose_bangumi_name)
    TextView bangumiNameText;
    @BindView(R.id.create_new_image_activity_choose_album_icon)
    RoundedImageView albumIcon;
    @BindView(R.id.create_new_image_activity_choose_album_name)
    TextView albumNameText;
    @BindView(R.id.create_new_image_activity_is_creator_layout)
    LinearLayout isCreatorLayout;
    @BindView(R.id.create_new_image_activity_is_creator)
    SwitchButton isCreatorSwitch;
    @BindView(R.id.create_new_image_activity_image_grid)
    RecyclerView imageGrid;
    @BindView(R.id.create_new_image_activity_send)
    TextView sendBtn;
    @BindView(R.id.create_new_image_activity_is_single_radio_group)
    RadioGroup singleRadioGroup;
    @BindView(R.id.create_new_image_activity_is_single_radio_single)
    RadioButton rBtnSingle;
    @BindView(R.id.create_new_image_activity_is_single_radio_album)
    RadioButton rBtnAlbum;



    boolean isSingle = false;
    boolean isCreator = false;

    //七牛
    Configuration config;
    UploadManager uploadManager;
    QiniuUtils qiniuUtils;

    //极验
    GT3GeetestUtilsBind gt3GeetestUtilsBind;
    private VerificationCodeBody.VerificationCodeBodyGeeTest verificationCodeBodyGeeTest;
    GeetestUtils geetestUtils;
    MyAlbumUtils myAlbumUtils;

    CreateCardImageGridAdapter imageGridAdapter;
    ArrayList<String> baseImagesUrl;
    ArrayList<String> imagesUrl;
    List<String> uploadUrlList;
    List<String> baseUploadUrlList;

    int bangumiId;
    String bangumiAvatar;
    String bangumiName;

    int albumId;
    String albumName;
    String albumPoster;

    AlertDialog cancelDialog;
    SweetAlertDialog upLoadDialog;

    ArrayList<QiniuImageParams.QiniuImageParamsData> qiniuImageParamsDataList = new ArrayList<>();
    private int urlTag;
    private int userId;

//    private CreateSingleImageReceiver rcceiver;
//    private IntentFilter intentFilter;

    /**
     * 获取当前屏幕的密度
     */
    private DisplayMetrics dm;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_create_new_image;
    }

    @Override
    protected void onInit() {
        if (!Constants.ISLOGIN){
            ToastUtils.showShort(this,"登录之后才能发帖");
            finish();
        }
        if (Constants.userInfoData==null){
            Constants.userInfoData = SharedPreferencesUtils.getUserInfoData(App.instance());
        }
        if (Constants.userInfoData == null||Constants.userInfoData.getId() == 0){
            ToastUtils.showShort(this,"用户数据异常丢失，请重启APP");
            finish();
        }

        dm = getResources().getDisplayMetrics();
        PermissionUtils.chekReadAndWritePermission(CreateNewImageActivity.this);
        myAlbumUtils = new MyAlbumUtils();
        setImageAdapter();
        isSingle = true;
        setIsSingle();
        setIsSingleListener();
        config = QiniuUtils.getQiniuConfig();
        uploadManager = new UploadManager(config);
        gt3GeetestUtilsBind = new GT3GeetestUtilsBind(this);
        verificationCodeBodyGeeTest = new VerificationCodeBody.VerificationCodeBodyGeeTest();
//        registerReceiver();
        setListener();
    }

//    private void registerReceiver() {
//        rcceiver = new CreateSingleImageReceiver();
//        intentFilter = new IntentFilter();
//        intentFilter.addAction(GeetestUtils.FailAction);
//        registerReceiver(rcceiver,intentFilter);
//    }

    @Override
    public void onDestroy() {
//        unregisterReceiver(rcceiver);
        super.onDestroy();
    }


    private void setIsSingleListener() {

        singleRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (radioGroup.getCheckedRadioButtonId()){
                    case R.id.create_new_image_activity_is_single_radio_single:
                        isSingle = true;
                        setIsSingle();
                        break;
                    case R.id.create_new_image_activity_is_single_radio_album:
                        isSingle = false;
                        setIsSingle();
                        break;
                    default:
                        break;
                }
            }
        });
    }


    private void setIsSingle(){
        if (isSingle){
            pictureName.setVisibility(View.VISIBLE);
            bangumiLayout.setVisibility(View.VISIBLE);
            isCreatorLayout.setVisibility(View.VISIBLE);
            albumLayout.setVisibility(View.GONE);
            addAlbumLayout.setVisibility(View.GONE);
            rBtnSingle.setTextColor(getResources().getColor(R.color.color_FFFFFFFF));
            rBtnAlbum.setTextColor(getResources().getColor(R.color.theme_magic_sakura_primary));
        }else {
            pictureName.setVisibility(View.GONE);
            bangumiLayout.setVisibility(View.GONE);
            isCreatorLayout.setVisibility(View.GONE);
            albumLayout.setVisibility(View.VISIBLE);
            addAlbumLayout.setVisibility(View.VISIBLE);
            rBtnSingle.setTextColor(getResources().getColor(R.color.theme_magic_sakura_primary));
            rBtnAlbum.setTextColor(getResources().getColor(R.color.color_FFFFFFFF));
        }
        baseImagesUrl.clear();
        if (imagesUrl!=null){
            imagesUrl.clear();
        }
        imageGridAdapter.setNewData(baseImagesUrl);
        imageGridAdapter.addData("add");
    }

    private void setImageAdapter() {
        if (baseImagesUrl == null){
            baseImagesUrl = new ArrayList<>();
        }
        imageGrid.setNestedScrollingEnabled(false);
        imageGridAdapter = new CreateCardImageGridAdapter(R.layout.reply_card_image_grid_item,baseImagesUrl,CreateNewImageActivity.this);
        imageGrid.setLayoutManager(new GridLayoutManager(CreateNewImageActivity.this,4));

        imageGrid.setAdapter(imageGridAdapter);
        imageGridAdapter.addData("add");

    }

    private void setListener() {
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCancelListener();
            }
        });
        imageGridAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                switch (view.getId()){
                    case R.id.reply_card_add_image_btn:

                        if (isSingle){
                            myAlbumUtils.setChooseImage(CreateNewImageActivity.this,1);
                            myAlbumUtils.setOnChooseImageFinishListener(new MyAlbumUtils.OnChooseImageFinishListener() {
                                @Override
                                public void onFinish(ArrayList<AlbumFile> albumFiles) {
                                    setChooseFinishImageLoad(albumFiles,1);
                                }
                            });
                        }else {

                            myAlbumUtils.setChooseImage(CreateNewImageActivity.this,15);
                            myAlbumUtils.setOnChooseImageFinishListener(new MyAlbumUtils.OnChooseImageFinishListener() {
                                @Override
                                public void onFinish(ArrayList<AlbumFile> albumFiles) {
                                    setChooseFinishImageLoad(albumFiles,15);
                                }
                            });
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

        isCreatorSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                isCreator = isChecked;
            }
        });
        bangumiLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateNewImageActivity.this, ChooseNewCardBangumiActivity.class);
                intent.putExtra("code",ChooseNewCardBangumiActivity.IMAGE);
                startActivityForResult(intent,ChooseNewCardBangumiActivity.IMAGE);
            }
        });
        albumLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateNewImageActivity.this, ChooseImageAlbumActivity.class);
                startActivityForResult(intent,ChooseImageAlbumActivity.ALBUM_CODE);
            }
        });
        addAlbumLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateNewImageActivity.this, CreateImageAlbumActivity.class);
                startActivityForResult(intent,CreateImageAlbumActivity.NEW_ALBUM_CODE);
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCheckInfo();
            }
        });
    }

    private void setCheckInfo() {
        baseUploadUrlList = imageGridAdapter.getData();
        uploadUrlList = baseUploadUrlList;
        uploadUrlList.remove("add");
        if (isSingle){
            if (pictureName.getText().toString()!=null&&pictureName.getText().toString().length()!=0&&
                    bangumiId!=0&&uploadUrlList.size()!=0){
                setUpLoadDialog();
            }else {
                if (bangumiId == 0){
                    ToastUtils.showShort(CreateNewImageActivity.this,"请选择所属番剧");
                }else if (uploadUrlList.size()==0){
                    ToastUtils.showShort(CreateNewImageActivity.this,"请选择图片");
                }else {
                    ToastUtils.showShort(CreateNewImageActivity.this,"请完善您的上传内容");
                }
            }
        }else {
            if (albumId!=0&&uploadUrlList.size()!=0){
                setUpLoadDialog();
            }else if (albumId == 0){
                ToastUtils.showShort(CreateNewImageActivity.this,"请选择相册");
            }else {
                ToastUtils.showShort(CreateNewImageActivity.this,"请添加图片");
            }
        }
    }

    private void setUpLoadDialog(){
        if (isSingle){
            sendBtn.setClickable(false);
            sendBtn.setTextColor(getResources().getColor(R.color.color_FFEEEEEE));
            setGeeTestUtils();
        }else {
            //检查完成 开启上传
            upLoadDialog = new SweetAlertDialog(CreateNewImageActivity.this,SweetAlertDialog.PROGRESS_TYPE);
            upLoadDialog.setTitle("上传中...");
            upLoadDialog.setContentText("您的图片正在上传中...");
            upLoadDialog.setCancelable(false);
            upLoadDialog.show();
            sendBtn.setClickable(false);
            sendBtn.setTextColor(getResources().getColor(R.color.color_FFEEEEEE));
            setQiniuUpload();
        }
    }
    private void setQiniuUpload(){
        qiniuUtils = new QiniuUtils();
        qiniuUtils.getQiniuUpToken(apiGetHasAuth,CreateNewImageActivity.this,uploadUrlList,userId);
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
                if (isSingle){
                    setUpLoadSingle();
                }else {
                    setUpLoadAlbumImage();
                }
            }
        });
    }

    private void setGeeTestUtils() {
        geetestUtils = new GeetestUtils();
        geetestUtils.setGeetestStart(CreateNewImageActivity.this,apiGet,
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
                upLoadDialog = new SweetAlertDialog(CreateNewImageActivity.this,SweetAlertDialog.PROGRESS_TYPE);
                upLoadDialog.setTitle("上传中...");
                upLoadDialog.setContentText("您的图片正在上传中...");
                upLoadDialog.setCancelable(false);
                upLoadDialog.show();
                gt3GeetestUtilsBind.gt3TestFinish();
                setQiniuUpload();
            }
        });
    }

    private void setUpLoadSingle() {
        CreateNewImageSingle imageSingle = new CreateNewImageSingle();
        imageSingle.setBangumi_id(bangumiId);
        imageSingle.setIs_creator(isCreator);
        String name = pictureName.getText().toString();
        imageSingle.setName(name);
        LogUtils.d("createImage","name = "+imageSingle.getName());
        QiniuImageParams.QiniuImageParamsData qiniuData =  qiniuImageParamsDataList.get(0);
        imageSingle.setUrl(qiniuData.getUrl());
        imageSingle.setHeight(qiniuData.getHeight());
        imageSingle.setWidth(qiniuData.getWidth());
        imageSingle.setSize(qiniuData.getSize()+"");
        imageSingle.setType(qiniuData.getType());
        imageSingle.setGeetest(verificationCodeBodyGeeTest);

        apiService.getCreateImageSingle(imageSingle)
                .compose(Rx2Schedulers.applyObservableAsync())
                .subscribe(new ObserverWrapper<CreateCard>(){
                    @Override
                    public void onSuccess(CreateCard createCard) {
                        //创建单张图片成功
                        //跳转进入图片
                        final int id = createCard.getData();
                        upLoadDialog.setTitleText("上传成功!")
                                .setContentText("您的图片上传成功!")
                                .setConfirmText("好的")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        if (id!=0){
                                            Intent intent = new Intent(CreateNewImageActivity.this,ImageShowInfoActivity.class);
                                            intent.putExtra("imageID",id);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                })
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        ToastUtils.showShort(CreateNewImageActivity.this,createCard.getMessage());
                        Intent intent = new Intent(MineFragment.EXPCHANGE);
                        intent.putExtra("expChangeNum",createCard.getExp());
                        sendBroadcast(intent);
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        super.onFailure(code, errorMsg);
                        setUpLoadDiaLogFail(errorMsg);
                    }
                });

    }
    private void setUpLoadAlbumImage() {
        CreateNewImageForAlbum imageForAlbum = new CreateNewImageForAlbum();
        imageForAlbum.setAlbum_id(albumId);
        imageForAlbum.setImages(qiniuImageParamsDataList);
        apiService.getCreateImageForAlbum(imageForAlbum)
                .compose(Rx2Schedulers.applyObservableAsync())
                .subscribe(new ObserverWrapper<Integer>(){
                    @Override
                    public void onSuccess(Integer integer) {
                        //相册添加图片成功
                        //跳转进入帖子
                        upLoadDialog.setTitleText("上传成功!")
                                .setContentText("您的图片上传成功!")
                                .setConfirmText("好的")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        if (albumId!=0){
                                            Intent intent = new Intent(CreateNewImageActivity.this,ImageShowInfoActivity.class);
                                            intent.putExtra("imageID",albumId);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                })
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
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
                content = "因网络原因，图片上传失败了";
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
        sendBtn.setTextColor(getResources().getColor(R.color.color_FFFFFFFF));
        urlTag = 0;
        sendBtn.setClickable(true);
    }

    private void setBangumi() {
        GlideUtils.loadImageView(CreateNewImageActivity.this,
                GlideUtils.setImageUrlForAlbum(CreateNewImageActivity.this,bangumiAvatar,50),bangumiIcon);
        bangumiNameText.setText(bangumiName);
    }

    private void setAlbum() {
        GlideUtils.loadImageView(CreateNewImageActivity.this,
                GlideUtils.setImageUrlForAlbum(CreateNewImageActivity.this,albumPoster,50),albumIcon);
        albumNameText.setText(albumName);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取图片路径

        if (resultCode == ChooseNewCardBangumiActivity.IMAGE && data!=null){
            bangumiId = data.getIntExtra("bangumiId",0);
            bangumiAvatar = data.getStringExtra("bangumiAvatar");
            bangumiName = data.getStringExtra("bangumiName");
            setBangumi();
        }
        if (resultCode == ChooseImageAlbumActivity.ALBUM_CODE&&data!=null){
            albumId = data.getIntExtra("album_id",0);
            albumName = data.getStringExtra("albumName");
            albumPoster = data.getStringExtra("albumPoster");
            setAlbum();
        }
        if (resultCode == CreateImageAlbumActivity.NEW_ALBUM_CODE&&data!=null){
            albumId = data.getIntExtra("album_id",0);
            albumName = data.getStringExtra("albumName");
            albumPoster = data.getStringExtra("albumPoster");
            setAlbum();
        }

    }

    private void setChooseFinishImageLoad(ArrayList<AlbumFile> albumFiles,int maxNum){
        if (imagesUrl == null){
            imagesUrl = new ArrayList<>();
        }
        imageGridAdapter.remove(imageGridAdapter.getData().size()-1);
        for (int i = 0; i <imagesUrl.size() ; i++) {
            if (imagesUrl.get(i).equals("add")){
                imagesUrl.remove(i);
            }
        }
        if ((albumFiles.size()+imagesUrl.size())>maxNum){
            if (maxNum == 1){
                ToastUtils.showShort(CreateNewImageActivity.this,"单张模式，自动保存本次所选");
            }else {
                ToastUtils.showShort(CreateNewImageActivity.this,"所选图片超出"+maxNum+"张，自动保存本次所选");
            }
            imagesUrl.clear();
        }
        for (int i = 0; i < albumFiles.size(); i++) {
            imagesUrl.add(albumFiles.get(i).getPath());
        }
        imageGridAdapter.setNewData(imagesUrl);
        imageGridAdapter.addData("add");

    }


    @Override
    public void onBackPressed() {
        setCancelListener();
    }

    private void setCancelListener() {
//        cancelDialog = new SweetAlertDialog(CreateNewImageActivity.this, SweetAlertDialog.WARNING_TYPE);
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
                .setTitle("退出传图")
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionUtils.REQUEST_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限被用户同意，可以做你要做的事情了。
                } else {
                    // 权限被用户拒绝了，可以提示用户,关闭界面等等。
                    ToastUtils.showShort(CreateNewImageActivity.this, "未取得授权，无法选择图片");
                    finish();
                }
                return;
            }
        }
    }

    @Override
    protected void handler(Message msg) {

    }

//    class CreateSingleImageReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            setUpLoadDiaLogFail("");
//        }
//    }
}
