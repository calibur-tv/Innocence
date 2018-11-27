package com.riuir.calibur.ui.home.Drama.dramaConfig;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.SharedPreferencesUtils;
import com.riuir.calibur.assistUtils.ToastUtils;

import com.riuir.calibur.data.Event;

import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.card.CardCreateNewActivity;
import com.riuir.calibur.ui.home.card.CardShowInfoActivity;
import com.riuir.calibur.ui.home.role.RolesShowInfoActivity;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.GlideUtils;
import com.riuir.calibur.utils.QiniuUtils;
import com.riuir.calibur.utils.album.MyAlbumUtils;
import com.yanzhenjie.album.AlbumFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import calibur.core.http.models.anime.AnimeShowInfo;
import calibur.core.http.models.qiniu.params.QiniuImageParams;
import calibur.core.http.observer.ObserverWrapper;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DramaMasterRoleSettingActivity extends BaseActivity {

    @BindView(R.id.drama_master_role_setting_image_add)
    RelativeLayout chooseAvatarAdd;
    @BindView(R.id.drama_master_role_setting_image_src)
    ImageView chooseAvatarSrc;
    @BindView(R.id.drama_master_role_setting_back)
    ImageView backBtn;
    @BindView(R.id.drama_master_role_setting_finish)
    TextView finishBtn;
    @BindView(R.id.drama_master_role_setting_role_name)
    EditText nameEdit;
    @BindView(R.id.drama_master_role_setting_role_other_name)
    EditText otherNameEdit;
    @BindView(R.id.drama_master_role_setting_role_summary)
    EditText summaryEdit;
    @BindView(R.id.drama_master_role_setting_role_tips)
    TextView tipsText;

    MyAlbumUtils myAlbumUtils;
    List<String> urlList = new ArrayList<>();
    String avatarUrl = "";
    ArrayList<QiniuImageParams.QiniuImageParamsData> qiniuImageParamsDataList;

    SweetAlertDialog upLoadDialog;

    String nameStr = "";
    String otherNameStr = "";
    String summaryStr = "";

    QiniuUtils qiniuUtils;
    private int userId;
    private int bangumi_id = 0;
    private int responseRoleId = 0;
    AnimeShowInfo animeShowInfoData;

    Call<Event<Integer>> callCreateRole;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_drama_master_role_setting;
    }

    @Override
    protected void onInit() {
        //如果未登录 结束页面
        if (!Constants.ISLOGIN){
            ToastUtils.showShort(this,"登录之后才能创建偶像");
            finish();
        }
        Intent intent = getIntent();
        bangumi_id = intent.getIntExtra("bangumi_id",0);
        animeShowInfoData = (AnimeShowInfo) intent.getSerializableExtra("animeShowInfoData");
        setUserInfo();
        setListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (callCreateRole!=null){
            callCreateRole.cancel();
        }
    }

    private void setUserInfo() {
        if (Constants.userInfoData==null){
            Constants.userInfoData = SharedPreferencesUtils.getUserInfoData(App.instance());
        }

        if (Constants.userInfoData!=null){
            userId = Constants.userInfoData.getId();
        }else {
            ToastUtils.showShort(this,"用户数据异常丢失，请重启APP");
            finish();
        }

        if (animeShowInfoData!=null){
            tipsText.setText("1：请勿添加与《"+animeShowInfoData.getName()+"》无关的偶像.\n2：添加偶像之前，请先在偶像列表里查找是否已添加过，请勿重复添加." );
        }
    }

    private void setListener() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        chooseAvatarAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAlbum();
            }
        });
        chooseAvatarSrc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAlbum();
            }
        });
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInfo();
            }
        });
    }

    private void setAlbum() {
        myAlbumUtils = new MyAlbumUtils();
        myAlbumUtils.setOnChooseImageFinishListener(new MyAlbumUtils.OnChooseImageFinishListener() {
            @Override
            public void onFinish(ArrayList<AlbumFile> albumFiles) {
                if (albumFiles!=null&&albumFiles.size()!=0){
                    GlideUtils.loadImageView(DramaMasterRoleSettingActivity.this,
                            albumFiles.get(0).getPath(),chooseAvatarSrc);
                    chooseAvatarSrc.setVisibility(View.VISIBLE);
                    chooseAvatarAdd.setVisibility(View.GONE);
                    avatarUrl = albumFiles.get(0).getPath();
                    urlList.add(avatarUrl);
                }else {
                    chooseAvatarSrc.setVisibility(View.GONE);
                    chooseAvatarAdd.setVisibility(View.VISIBLE);
                    avatarUrl = "";
                    urlList.clear();
                }
            }
        });
        myAlbumUtils.setChooseImage(DramaMasterRoleSettingActivity.this,1);
    }

    private void checkInfo() {
        if (nameEdit!=null&&otherNameEdit!=null&&summaryEdit!=null){
            nameStr = nameEdit.getText().toString();
            otherNameStr = otherNameEdit.getText().toString();
            summaryStr = summaryEdit.getText().toString();
            if (nameStr!=null&&nameStr.length()!=0&&
                otherNameStr!=null&&otherNameStr.length()!=0&&
                summaryStr!=null&&summaryStr.length()!=0&&
                avatarUrl!=null&&avatarUrl.length()!=0){
                upLoadDialog = new SweetAlertDialog(DramaMasterRoleSettingActivity.this,SweetAlertDialog.PROGRESS_TYPE);
                upLoadDialog.setTitle("上传中...");
                upLoadDialog.setContentText("创建偶像中...");
                upLoadDialog.setCancelable(false);
                upLoadDialog.show();
                finishBtn.setClickable(false);
                finishBtn.setText("上传中");
                setQiniuUpLoad();
            }else {
                ToastUtils.showShort(DramaMasterRoleSettingActivity.this,"请完善输入的信息");
            }
        }else {
            ToastUtils.showShort(DramaMasterRoleSettingActivity.this,"请检查输入的内容");
        }
    }

    private void setQiniuUpLoad() {
        qiniuUtils = new QiniuUtils();
        qiniuUtils.getQiniuUpToken(apiGetHasAuth,DramaMasterRoleSettingActivity.this,urlList,userId);
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
                setUpLoadRole();
            }
        });
    }

    private void setUpLoadRole() {
        apiService.getCallManagerCreateRole(bangumi_id,nameStr,otherNameStr,summaryStr,qiniuImageParamsDataList.get(0).getUrl())
                .compose(Rx2Schedulers.applyObservableAsync())
                .subscribe(new ObserverWrapper<Integer>(){
                    @Override
                    public void onSuccess(Integer id) {
                        responseRoleId = id;
                        setUpLoadRoleFinish();
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        super.onFailure(code, errorMsg);
                        setUpLoadDiaLogFail("");
                    }
                });
    }

    private void setUpLoadDiaLogFail(String fialMessage) {
        if (upLoadDialog!=null){
            String content = "";
            if (fialMessage.contains("mimeType")){
                content = "所选部分图片格式不支持,\n 只支持jpg,jpeg,png,gif格式上传";
            }else {
                content = "因网络原因，偶像上传失败了";
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
        if (finishBtn!=null){
            finishBtn.setClickable(true);
            finishBtn.setText("确定");
        }
    }
    private void setUpLoadRoleFinish() {
        if (upLoadDialog!=null){
            upLoadDialog.setTitleText("创建成功!")
                    .setContentText("偶像创建成功!")
                    .setConfirmText("好的")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            //上传成功 点击事件 跳转到偶像详情页
                            Intent intent = new Intent(DramaMasterRoleSettingActivity.this, RolesShowInfoActivity.class);
                            intent.putExtra("roleId",responseRoleId);
                            startActivity(intent);
                            upLoadDialog.dismiss();
                            finish();
                        }
                    })
                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
        }
    }

    @Override
    protected void handler(Message msg) {

    }
}
