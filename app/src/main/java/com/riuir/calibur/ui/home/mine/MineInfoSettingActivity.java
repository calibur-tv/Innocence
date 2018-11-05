package com.riuir.calibur.ui.home.mine;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.SwitchCompat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.qiniu.android.common.AutoZone;
import com.qiniu.android.common.FixedZone;
import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.PermissionUtils;
import com.riuir.calibur.assistUtils.TimeUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.assistUtils.activityUtils.UriUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.MineUserInfo;
import com.riuir.calibur.data.params.QiniuImageParams;
import com.riuir.calibur.data.params.UpUserSetting;
import com.riuir.calibur.data.qiniu.QiniuUpToken;
import com.riuir.calibur.ui.common.BaseActivity;

import com.riuir.calibur.ui.home.card.CardCreateNewActivity;
import com.riuir.calibur.ui.widget.SelectorImagesActivity;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.GlideUtils;
import com.riuir.calibur.utils.QiniuUtils;
import com.riuir.calibur.utils.album.MyAlbumUtils;
import com.suke.widget.SwitchButton;
import com.tencent.bugly.crashreport.CrashReport;
import com.yalantis.ucrop.UCrop;
import com.yanzhenjie.album.AlbumFile;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.zip.Inflater;

import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MineInfoSettingActivity extends BaseActivity {

    public static final int SETTING_CODE = 88;

    @BindView(R.id.user_setting_banner)
    ImageView banner;
    @BindView(R.id.user_setting_icon)
    RoundedImageView icon;
    @BindView(R.id.user_setting_user_name)
    EditText nameEdit;
    @BindView(R.id.user_setting_birthday)
    TextView birthdayChoose;
    @BindView(R.id.user_setting_birthday_status)
    TextView birthdayStatus;
    @BindView(R.id.user_setting_birthday_switch)
    SwitchButton birthdaySwitch;
    @BindView(R.id.user_setting_sex)
    TextView sexChoose;
    @BindView(R.id.user_setting_sex_status)
    TextView sexStatus;
    @BindView(R.id.user_setting_sex_switch)
    SwitchButton sexSwitch;
    @BindView(R.id.user_setting_signature)
    TextView signature;
    @BindView(R.id.user_setting_back)
    ImageView backBtn;
    @BindView(R.id.user_setting_finishBtn)
    Button finishBtn;

    //七牛config
    Configuration config;
    UploadManager uploadManager;

    QiniuUtils qiniuUtils;
    MyAlbumUtils myAlbumUtils;

    MineUserInfo.MinEUserInfoData userData;

    DatePickerDialog datePic;
    int cYear,cMonth,cDay;

    Dialog sexDialog;
    TextView sexNan,sexNv,sexWeiniang,sexYaoniang,sexFuta;
    String userSex;
    int userSexInt = 0;
    boolean isBirthdaySecret = false;
    boolean isSexSecret = false;

    String iconUrl;
    String bannerUrl;
    File iconFile,bannerFile;

    ArrayList<String> iconImagesUrl;
    ArrayList<String> bannerImagesUrl;
    //标记符
    boolean isIcon = false;
    boolean isBanner = false;

    boolean isChanged = false;

    boolean picturePermission = true;

    QiniuImageParams.QiniuImageParamsData iconImageQiniu;
    QiniuImageParams.QiniuImageParamsData bannerImageQiniu;

    Call<Event<String>> uploadCall;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_mine_info_setting;
    }

    @Override
    protected void onInit() {
        Intent intent = getIntent();
        userData = (MineUserInfo.MinEUserInfoData) intent.getSerializableExtra("userData");
        if (userData == null&&Constants.userInfoData!=null){
            userData = Constants.userInfoData;
        }

        qiniuUtils = new QiniuUtils();
        myAlbumUtils = new MyAlbumUtils();
        config = QiniuUtils.getQiniuConfig();
        uploadManager = new UploadManager(config);
        //检查权限
        PermissionUtils.chekReadAndWritePermission(MineInfoSettingActivity.this);

        setView();
        setListener();
    }

    @Override
    public void onDestroy() {
        if(uploadCall!=null){
            uploadCall.cancel();
        }
        super.onDestroy();
    }

    private void setView() {

        GlideUtils.loadImageViewBlur(MineInfoSettingActivity.this,
                GlideUtils.setImageUrl(MineInfoSettingActivity.this,userData.getBanner(),GlideUtils.FULL_SCREEN),banner);
        GlideUtils.loadImageViewCircle(MineInfoSettingActivity.this,userData.getAvatar(),icon);
        nameEdit.setText(userData.getNickname());
        signature.setText(userData.getSignature());
        isBirthdaySecret =userData.isBirthSecret();
        isSexSecret = userData.isSexSecret();
        if (userData.isSexSecret()){
            sexStatus.setText("状态：私密");
            sexSwitch.setChecked(true);
        }else {
            sexStatus.setText("状态：公开");
            sexSwitch.setChecked(false);
        }
        if (userData.isBirthSecret()){
            birthdayStatus.setText("状态：私密");
            birthdaySwitch.setChecked(true);
        }else {
            birthdayStatus.setText("状态：公开");
            birthdaySwitch.setChecked(false);
        }
        if (userData.getBirthday()!=null&&userData.getBirthday().length()!=0){
            String birStr = TimeUtils.getTimestamp2Date(
                    TimeUtils.getDate2Timestamp(userData.getBirthday()+"",
                            "yyyy-MM-dd HH:mm:ss"),
                    "yyyy年MM月dd日");
            birthdayChoose.setText(birStr);
        }
        userSexInt = userData.getSex();
        switch (userSexInt){
            case 0:
                sexChoose.setText("未知");
                break;
            case 1:
                sexChoose.setText("男");
                break;
            case 2:
                sexChoose.setText("女");
                break;
            case 3:
                sexChoose.setText("伪娘");
                break;
            case 4:
                sexChoose.setText("药娘");
                break;
            case 5:
                sexChoose.setText("扶她");
                break;
        }
        setDatePicker();
        setSexPicker();
    }

    private void setSexPicker() {
        View view = getInflater().inflate(R.layout.setting_sex_pick_dialog,null);
        sexNan = view.findViewById(R.id.setting_sex_dialog_nan);
        sexNv = view.findViewById(R.id.setting_sex_dialog_nv);
        sexWeiniang = view.findViewById(R.id.setting_sex_dialog_weiniang);
        sexYaoniang = view.findViewById(R.id.setting_sex_dialog_yaoniang);
        sexFuta = view.findViewById(R.id.setting_sex_dialog_futa);
        sexDialog = new Dialog(MineInfoSettingActivity.this);
        Window window = sexDialog.getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        sexDialog.setContentView(view);
        setSexListener();
        //设置弹出窗口大小
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //设置显示位置
        window.setGravity(Gravity.BOTTOM);

    }

    private void setSexListener() {
        sexNan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userSex = "男";
                userSexInt = 1;
                sexChoose.setText(userSex);
                sexDialog.hide();
            }
        });
        sexNv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userSex = "女";
                userSexInt = 2;
                sexChoose.setText(userSex);
                sexDialog.hide();
            }
        });
        sexWeiniang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userSex = "伪娘";
                userSexInt = 3;
                sexChoose.setText(userSex);
                sexDialog.hide();
            }
        });
        sexYaoniang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userSex = "药娘";
                userSexInt = 4;
                sexChoose.setText(userSex);
                sexDialog.hide();
            }
        });
        sexFuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userSex = "扶她";
                userSexInt = 5;
                sexChoose.setText(userSex);
                sexDialog.hide();
            }
        });
    }

    private void setDatePicker() {
        Calendar ca = Calendar.getInstance();
        cYear = ca.get(Calendar.YEAR);
        cMonth = ca.get(Calendar.MONTH);
        cDay = ca.get(Calendar.DAY_OF_MONTH);

        datePic = new DatePickerDialog(MineInfoSettingActivity.this, AlertDialog.THEME_HOLO_LIGHT,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayForMonth) {
                        cYear = year;
                        cMonth = month+1;
                        cDay = dayForMonth;
                        birthdayChoose.setText(cYear+"年"+cMonth+"月"+cDay+"日");
                    }
                },cYear,cMonth,cDay);
        //设置起始日期和结束日期
        DatePicker datePicker = datePic.getDatePicker();
        long min = TimeUtils.getDate2Timestamp("1978-01-01 08:00:00","yyyy-MM-dd HH:mm:ss");
        LogUtils.d("birthday111","birthday222 = "+min);
        datePicker.setMinDate(min);
        datePicker.setMaxDate(System.currentTimeMillis()-311040000000l);

    }

    private void setListener() {

        sexSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                isSexSecret =isChecked;
                if (isChecked){
                    sexStatus.setText("状态：私密");

                }else {
                    sexStatus.setText("状态：公开");
                }

            }
        });
        birthdaySwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                isBirthdaySecret = isChecked;
                if (isChecked){
                    birthdayStatus.setText("状态：私密");

                }else {
                    birthdayStatus.setText("状态：公开");
                }
            }
        });

        birthdayChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePic.show();
            }
        });
        sexChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sexDialog.show();
            }
        });

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //相册
                if (picturePermission){
                    myAlbumUtils.setChooseImage(MineInfoSettingActivity.this,1);
                    myAlbumUtils.setOnChooseImageFinishListener(new MyAlbumUtils.OnChooseImageFinishListener() {
                        @Override
                        public void onFinish(ArrayList<AlbumFile> albumFiles) {
                            setChooseFinishImageLoad(albumFiles,"avatar");
                        }
                    });
                }else {
                    ToastUtils.showShort(MineInfoSettingActivity.this,"未取得授权，无法选择图片");
                }

            }
        });
        banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //相册
                if (picturePermission){
                    myAlbumUtils.setChooseImage(MineInfoSettingActivity.this,1);
                    myAlbumUtils.setOnChooseImageFinishListener(new MyAlbumUtils.OnChooseImageFinishListener() {
                        @Override
                        public void onFinish(ArrayList<AlbumFile> albumFiles) {
                            setChooseFinishImageLoad(albumFiles,"banner");
                        }
                    });
                }else {
                    ToastUtils.showShort(MineInfoSettingActivity.this,"未取得授权，无法选择图片");
                }

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isChanged){
                    setResult(SETTING_CODE);
                }
                finish();
            }
        });
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCheck();
            }
        });
    }

    private void setCheck(){
        if (nameEdit.getText().toString()!=null&&nameEdit.getText().toString().length()!=0&&
                !nameEdit.getText().toString().contains("\n")&&!nameEdit.getText().toString().contains(" ")){
            finishBtn.setClickable(false);
            finishBtn.setText("上传中");
            setUpLoadInfo();
        }else {
            ToastUtils.showShort(MineInfoSettingActivity.this,"您的昵称格式不符合规范");
        }
    }

    private void setUpLoadInfo() {
        int upSex = 0;
        String upSignature = "";
        String upNickName = "";
        long upBirthday = 0;
        boolean birth_secret;
        boolean sex_secret;

        upSex = userSexInt;

        if (signature.getText().toString()==null||signature.getText().toString().length()==0){
            signature.setText("这个人还很神秘...");
        }
        upSignature = signature.getText().toString();

        upNickName = nameEdit.getText().toString();

        upBirthday = TimeUtils.getDate2Timestamp(birthdayChoose.getText().toString()+" 08时00分00秒",
                "yyyy年MM月dd日 HH时mm分ss秒")/1000;

        LogUtils.d("birthday111","birthday111 = "+upBirthday);
        birth_secret = isBirthdaySecret;
        sex_secret = isSexSecret;

        UpUserSetting upUserSetting = new UpUserSetting();
        upUserSetting.setSex(upSex);
        upUserSetting.setNickname(upNickName);
        upUserSetting.setBirthday(upBirthday);
        upUserSetting.setSignature(upSignature);
        upUserSetting.setBirth_secret(birth_secret);
        upUserSetting.setSex_secret(sex_secret);

        uploadCall = apiPost.getCallUPLoadUserSetting(upUserSetting);
        uploadCall.enqueue(new Callback<Event<String>>() {
            @Override
            public void onResponse(Call<Event<String>> call, Response<Event<String>> response) {
                if (response!=null&&response.isSuccessful()){
                    LogUtils.d("mineSetting","info success");
                    finishBtn.setClickable(true);
                    finishBtn.setText("保存");
                    ToastUtils.showShort(MineInfoSettingActivity.this,"保存成功！");
                    setResult(SETTING_CODE);
                    finish();
                }else if (response!=null&&!response.isSuccessful()){
                    String errorStr = "";
                    try {
                        errorStr = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    Event<String> info =gson.fromJson(errorStr,Event.class);
                    ToastUtils.showShort(MineInfoSettingActivity.this,info.getMessage());
                    finishBtn.setClickable(true);
                    finishBtn.setText("保存");
                }else {
                    ToastUtils.showShort(MineInfoSettingActivity.this,"未知错误导致上传失败");
                    finishBtn.setClickable(true);
                    finishBtn.setText("保存");
                }
            }

            @Override
            public void onFailure(Call<Event<String>> call, Throwable t) {
                if (call.isCanceled()){
                }else {
                    ToastUtils.showShort(MineInfoSettingActivity.this,"网络异常，请稍后再试");
                    CrashReport.postCatchedException(t);
                    finishBtn.setClickable(true);
                    finishBtn.setText("保存");
                }
            }
        });



    }

    private void getQiniuTokenAvatar(){

        List<String> avatarUrlList = new ArrayList<>();
        avatarUrlList.add(iconUrl);
        qiniuUtils.getQiniuUpToken(apiGetHasAuth,MineInfoSettingActivity.this,avatarUrlList,userData.getId());

        qiniuUtils.setOnQiniuUploadFailedListnener(new QiniuUtils.OnQiniuUploadFailedListnener() {
            @Override
            public void onFailed(String fialMessage) {
            }
        });
        qiniuUtils.setOnQiniuUploadSuccessedListnener(new QiniuUtils.OnQiniuUploadSuccessedListnener() {
            @Override
            public void onUploadSuccess(ArrayList<QiniuImageParams.QiniuImageParamsData> imageParamsDataList) {
                iconImageQiniu = imageParamsDataList.get(0);
                setUpLoadImageNet("avatar");
            }
        });

    }

    private void getQiniuTokenBanner(){

        List<String> bannerUrlList = new ArrayList<>();
        bannerUrlList.add(bannerUrl);
        qiniuUtils.getQiniuUpToken(apiGetHasAuth,MineInfoSettingActivity.this,bannerUrlList,userData.getId());

        qiniuUtils.setOnQiniuUploadFailedListnener(new QiniuUtils.OnQiniuUploadFailedListnener() {
            @Override
            public void onFailed(String fialMessage) {
            }
        });
        qiniuUtils.setOnQiniuUploadSuccessedListnener(new QiniuUtils.OnQiniuUploadSuccessedListnener() {
            @Override
            public void onUploadSuccess(ArrayList<QiniuImageParams.QiniuImageParamsData> imageParamsDataList) {
                bannerImageQiniu = imageParamsDataList.get(0);
                setUpLoadImageNet("banner");
            }
        });

    }


    private void setUpLoadImageNet(final String type){
        String url = "";
        if (type.equals("avatar")){
            url = iconImageQiniu.getUrl();
        }
        if (type.equals("banner")){
            url = bannerImageQiniu.getUrl();
        }
        apiPost.getCallUpLoadUserAvatarAndBanner(type,url).enqueue(new Callback<Event<String>>() {
            @Override
            public void onResponse(Call<Event<String>> call, Response<Event<String>> response) {
                if (response!=null&&response.isSuccessful()){
                    File file;
                    if (type.equals("avatar")){
                        file = new File(iconUrl);
                        GlideUtils.loadImageViewFromFileCircle(MineInfoSettingActivity.this,
                                file,icon);
                    }
                    if (type.equals("banner")){
                        file = new File(bannerUrl);
                        GlideUtils.loadImageViewFromFileBlur(MineInfoSettingActivity.this,
                                file,banner);
                    }
                    isChanged = true;
                    ToastUtils.showShort(MineInfoSettingActivity.this,"图片上传成功！");

                }else if (response!=null&&!response.isSuccessful()){
                    String errorStr = "";
                    try {
                        errorStr = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    Event<String> info =gson.fromJson(errorStr,Event.class);
                    ToastUtils.showShort(MineInfoSettingActivity.this,info.getMessage());
                }else {
                    ToastUtils.showShort(MineInfoSettingActivity.this,"未知错误导致上传失败");
                }
            }

            @Override
            public void onFailure(Call<Event<String>> call, Throwable t) {
                ToastUtils.showShort(MineInfoSettingActivity.this,"网络异常，请稍后再试");
                CrashReport.postCatchedException(t);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //剪裁后返回剪裁好了的图
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            if (data!=null){
                final Uri resultUri = UCrop.getOutput(data);
                if (resultUri!=null){
                    setCropResult(resultUri);
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }

    }

    private void setChooseFinishImageLoad(ArrayList<AlbumFile> albumFiles,String type){
        if (type.equals("avatar")){
            if (iconImagesUrl == null){
                iconImagesUrl = new ArrayList<>();
            }
            iconImagesUrl.clear();
            iconImagesUrl.add(albumFiles.get(0).getPath());
            setIconResult();
        }else if (type.equals("banner")){
            if (bannerImagesUrl == null){
                bannerImagesUrl = new ArrayList<>();
            }
            bannerImagesUrl.clear();
            bannerImagesUrl.add(albumFiles.get(0).getPath());
            setBannerResult();
        }

    }


    //选择icon之后调用，并且启动剪裁图片
    private void setIconResult() {
        File sourceFile = new File(iconImagesUrl.get(0));
        isIcon =true;
        isBanner =false;

        Uri sourceUri = Uri.fromFile(sourceFile);
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), TimeUtils.getCurTimeLong()+".jpeg"));
        //调用UCropActivity
        UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(9, 9)
                .withMaxResultSize(300, 300)
                .start(this);
    }

    //选择banner之后调用，并且启动剪裁图片
    private void setBannerResult() {
        File sourceFile = new File(bannerImagesUrl.get(0));
        isIcon =false;
        isBanner =true;
        Uri sourceUri = Uri.fromFile(sourceFile);
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), TimeUtils.getCurTimeLong()+".jpeg"));
        //调用UCropActivity
        UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(16, 9)
                .withMaxResultSize(300, 300)
                .start(this);
    }

    private void setCropResult(Uri cropReUri) {
        File file = UriUtils.uri2file(cropReUri);
        if (isBanner){
            bannerUrl = file.getPath();
            bannerFile= file;
            getQiniuTokenBanner();

        }
        if (isIcon){
            iconUrl = file.getPath();
            iconFile= file;
            getQiniuTokenAvatar();
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
                    ToastUtils.showShort(MineInfoSettingActivity.this, "未取得授权，无法设置头像或背景图");
                    picturePermission = false;
                }
                return;
            }
        }
    }


    @Override
    protected void handler(Message msg) {

    }
}
