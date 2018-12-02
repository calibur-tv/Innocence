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
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.PermissionUtils;
import com.riuir.calibur.assistUtils.SharedPreferencesUtils;
import com.riuir.calibur.assistUtils.ToastUtils;

import com.riuir.calibur.data.Event;

import com.riuir.calibur.data.qiniu.QiniuUpToken;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.Drama.dramaConfig.DramaMasterAnimeSettingActivity;
import com.riuir.calibur.ui.home.Drama.dramaConfig.adapter.AnimeSettingTagAdapter;
import com.riuir.calibur.ui.home.Drama.dramaConfig.choose.DramaMasterSettingChooseTagActivity;
import com.riuir.calibur.ui.home.MineFragment;
import com.riuir.calibur.ui.home.choose.CardChooseTagsActivity;
import com.riuir.calibur.ui.home.choose.ChooseNewCardBangumiActivity;
import com.riuir.calibur.ui.home.adapter.CreateCardImageGridAdapter;

import com.riuir.calibur.ui.home.image.CreateImageAlbumActivity;
import com.riuir.calibur.ui.widget.SelectorImagesActivity;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.GlideUtils;
import com.riuir.calibur.utils.QiniuUtils;
import com.riuir.calibur.utils.album.MyAlbumUtils;
import com.riuir.calibur.utils.geetest.GeetestUtils;
import com.suke.widget.SwitchButton;
import com.yanzhenjie.album.AlbumFile;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import calibur.core.http.models.anime.AnimeShowInfo;
import calibur.core.http.models.create.CreateCard;
import calibur.core.http.models.create.params.CreatePostParams;
import calibur.core.http.models.geetest.params.VerificationCodeBody;
import calibur.core.http.models.qiniu.params.QiniuImageParams;
import calibur.core.http.observer.ObserverWrapper;
import calibur.core.manager.UserSystem;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
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
    @BindView(R.id.card_create_new_activity_post_tag_recycler_view)
    RecyclerView tagListView;
    @BindView(R.id.card_create_new_activity_post_tag_text)
    TextView tagTextView;
    @BindView(R.id.card_create_new_activity_post_tag_btn)
    TextView tagTextBtn;
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

    private AnimeSettingTagAdapter tagAdapter;
    private List<AnimeShowInfo.AnimeShowInfoTags> tagsList;
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
    QiniuUtils qiniuUtils;

    //极验
    GT3GeetestUtilsBind gt3GeetestUtilsBind;
    private VerificationCodeBody.VerificationCodeBodyGeeTest verificationCodeBodyGeeTest;
    GeetestUtils geetestUtils;

//    private CreateCardBroadCastReceiver receiver;
//    private IntentFilter intentFilter;

    MyAlbumUtils myAlbumUtils;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_card_create_new;
    }

    @Override
    protected void onInit() {
        //如果未登录 结束页面
        if (!UserSystem.getInstance().isLogin()){
            ToastUtils.showShort(this,"登录之后才能发帖");
            finish();
        }
        setUserInfo();
        setView();
        PermissionUtils.chekReadAndWritePermission(CardCreateNewActivity.this);
        myAlbumUtils = new MyAlbumUtils();

//        registerReceiver();
        gt3GeetestUtilsBind = new GT3GeetestUtilsBind(this);
        verificationCodeBodyGeeTest = new VerificationCodeBody.VerificationCodeBodyGeeTest();

        setListener();
        config = QiniuUtils.getQiniuConfig();
        uploadManager = new UploadManager(config);

    }

    @Override
    public void onDestroy() {
        gt3GeetestUtilsBind.cancelUtils();
        super.onDestroy();
    }

    private void setView() {
        if (baseImagesUrl == null){
            baseImagesUrl = new ArrayList<>();
        }

        imageGridAdapter = new CreateCardImageGridAdapter(R.layout.reply_card_image_grid_item,baseImagesUrl,CardCreateNewActivity.this);
        addImageGrid.setLayoutManager(new GridLayoutManager(CardCreateNewActivity.this,4));

        addImageGrid.setAdapter(imageGridAdapter);
        imageGridAdapter.addData("add");
        setTagAdapter();

    }
    private void setTagAdapter() {
        tagsList = new ArrayList<>();
        tagAdapter = new AnimeSettingTagAdapter(R.layout.drama_master_setting_tag_item,tagsList,
                CardCreateNewActivity.this);
        tagListView.setLayoutManager(new GridLayoutManager(CardCreateNewActivity.this,3));
        tagListView.setAdapter(tagAdapter);
    }
    private void setListVisible() {
        if (tagAdapter.getData()!=null&&tagAdapter.getData().size()!=0){
            tagListView.setVisibility(View.VISIBLE);
            tagTextView.setVisibility(View.GONE);
        }else {
            tagListView.setVisibility(View.GONE);
            tagTextView.setVisibility(View.VISIBLE);
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
    }

    private void setCancelListener() {

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

                            myAlbumUtils.setChooseImage(CardCreateNewActivity.this,9);
                            myAlbumUtils.setOnChooseImageFinishListener(new MyAlbumUtils.OnChooseImageFinishListener() {
                                @Override
                                public void onFinish(ArrayList<AlbumFile> albumFiles) {
                                    setChooseFinishImageLoad(albumFiles);
                                }
                            });
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
        tagTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseTag();
            }
        });
        tagTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseTag();
            }
        });
        tagAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()){
                    case R.id.drama_master_anime_setting_item_delete_btn:
                        adapter.remove(position);
                        setListVisible();
                        break;
                    default:
                        break;
                }
            }
        });

    }

    private void chooseTag() {
        Intent intent = new Intent(this,CardChooseTagsActivity.class);
        if (tagAdapter.getData()!=null&&tagAdapter.getData().size()!=0){
            ArrayList<Integer> tagsIds = new ArrayList<>();
            for (AnimeShowInfo.AnimeShowInfoTags tags:tagAdapter.getData()){
                tagsIds.add(tags.getId());
            }
            intent.putIntegerArrayListExtra("tagsIds",tagsIds);
        }
        startActivityForResult(intent,CardChooseTagsActivity.TAG_POST);
    }

    private void setBangumi() {
        GlideUtils.loadImageView(CardCreateNewActivity.this,bangumiAvatar,chooseBangumiIcon);
        chooseBangumiName.setText(bangumiName);
    }

    private void checkInfo() {

        if (titleEdit.getText()!=null&&titleEdit.getText().toString().length()!=0&&
                cardContentEdit.getText()!=null&&cardContentEdit.getText().toString().length()!=0&&
                bangumiId!=0){
            baseUrlList = imageGridAdapter.getData();
            urlList = baseUrlList;
            urlList.remove("add");
            sendBtn.setClickable(false);
            sendBtn.setTextColor(getResources().getColor(R.color.color_FFEEEEEE));
            setGeeTestUtils();


        }else {
            if (bangumiId == 0){
                ToastUtils.showShort(CardCreateNewActivity.this,"请选择所属番剧");
            }else {
                ToastUtils.showShort(CardCreateNewActivity.this,"请输入标题/内容");
            }
        }
    }

    private void setGeeTestUtils() {
        geetestUtils = new GeetestUtils();
        geetestUtils.setGeetestStart(CardCreateNewActivity.this,apiGet,
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
                upLoadDialog = new SweetAlertDialog(CardCreateNewActivity.this,SweetAlertDialog.PROGRESS_TYPE);
                upLoadDialog.setTitle("上传中...");
                upLoadDialog.setContentText("您的帖子正在上传中...");
                upLoadDialog.setCancelable(false);
                upLoadDialog.show();
                gt3GeetestUtilsBind.gt3TestFinish();
                if (urlList!=null&&urlList.size()!=0){
                    setQiniuUpload();
                }else {
                    setUpLoadPost();
                }
            }
        });
    }
    private void setQiniuUpload(){
        qiniuUtils = new QiniuUtils();
        qiniuUtils.getQiniuUpToken(CardCreateNewActivity.this,urlList,userId,"post");
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
                setUpLoadPost();
            }
        });
    }


    private void setUpLoadPost() {
        createPostParams = new CreatePostParams();
        createPostParams.setBangumiId(bangumiId);
        createPostParams.setContent(cardContentEdit.getText().toString());
        createPostParams.setImages(qiniuImageParamsDataList);
        createPostParams.setIs_creator(isCreator);
        createPostParams.setTitle(titleEdit.getText().toString());
        createPostParams.setGeetest(verificationCodeBodyGeeTest);

        List<Integer> tagIdList = new ArrayList<>();
        List<AnimeShowInfo.AnimeShowInfoTags> paramsTagsList = tagAdapter.getData();
        for (int i = 0; i < paramsTagsList.size(); i++) {
            tagIdList.add(paramsTagsList.get(i).getId());
        }
        createPostParams.setTags(tagIdList);
        apiService.getCreatPost(createPostParams)
                .compose(Rx2Schedulers.applyObservableAsync())
                .subscribe(new ObserverWrapper<CreateCard>(){
                    @Override
                    public void onSuccess(CreateCard createCard) {
                        //创建帖子成功
                        //跳转进入帖子
                        final int id = createCard.getData();
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
                        ToastUtils.showShort(CardCreateNewActivity.this,createCard.getMessage());
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

    private void setUpLoadDiaLogFail(String error){
        if (upLoadDialog!=null){
            String content = "";
            if (error.contains("mimeType")){
                content = "所选部分图片格式不支持,\n 只支持jpg,jpeg,png,gif格式上传";
            }else if (error.length()!=0){
                content = error;
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
    }

    @Override
    public void onBackPressed() {
        setCancelListener();
    }

    @Override
    protected void handler(Message msg) {

    }

    private void setChooseFinishImageLoad(ArrayList<AlbumFile> albumFiles){
        if (imagesUrl == null){
            imagesUrl = new ArrayList<>();
        }
        imageGridAdapter.remove(imageGridAdapter.getData().size()-1);
        if ((albumFiles.size()+imagesUrl.size())>9){
            ToastUtils.showShort(CardCreateNewActivity.this,"所选图片超出9张，自动保存本次所选");
            imagesUrl.clear();
        }
        for (int i = 0; i < albumFiles.size(); i++) {
            imagesUrl.add(albumFiles.get(i).getPath());
        }
        imageGridAdapter.setNewData(imagesUrl);
        imageGridAdapter.addData("add");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取图片路径

        if (resultCode == ChooseNewCardBangumiActivity.CARD&& data != null){
            bangumiId = data.getIntExtra("bangumiId",0);
            bangumiAvatar = data.getStringExtra("bangumiAvatar");
            bangumiName = data.getStringExtra("bangumiName");
            setBangumi();
        }
        if (requestCode == CardChooseTagsActivity.TAG_POST&&data!=null){
            List<AnimeShowInfo.AnimeShowInfoTags> tags = new ArrayList<>();
            ArrayList<String> tagsNameList = data.getStringArrayListExtra("tagsNameList");
            ArrayList<Integer> tagsIdsList = data.getIntegerArrayListExtra("tagsIdsList");
            if (tagsNameList!=null&&tagsNameList.size()!=0
                    &&tagsIdsList!=null&&tagsIdsList.size()!=0
                    &&tagsNameList.size() == tagsIdsList.size()){
                for (int i = 0; i < tagsIdsList.size(); i++) {
                    AnimeShowInfo.AnimeShowInfoTags tag = new AnimeShowInfo.AnimeShowInfoTags();
                    tag.setId(tagsIdsList.get(i));
                    tag.setName(tagsNameList.get(i));
                    tags.add(tag);
                }
                tagAdapter.setNewData(tags);
                setListVisible();
            }else {
                ToastUtils.showShort(CardCreateNewActivity.this, "选择标签异常丢失");
            }
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

}
