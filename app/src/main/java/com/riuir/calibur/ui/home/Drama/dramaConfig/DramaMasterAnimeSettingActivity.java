package com.riuir.calibur.ui.home.Drama.dramaConfig;

import android.content.Intent;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.makeramen.roundedimageview.RoundedImageView;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.SharedPreferencesUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;

import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.Drama.DramaActivity;
import com.riuir.calibur.ui.home.Drama.dramaConfig.adapter.AnimeSettingTagAdapter;
import com.riuir.calibur.ui.home.Drama.dramaConfig.choose.DramaMasterSettingChooseTagActivity;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.GlideUtils;
import com.riuir.calibur.utils.QiniuUtils;
import com.riuir.calibur.utils.album.MyAlbumUtils;
import com.yanzhenjie.album.AlbumFile;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import calibur.core.http.models.anime.AnimeShowInfo;
import calibur.core.http.models.anime.params.BangumiEditParams;
import calibur.core.http.models.qiniu.params.QiniuImageParams;
import calibur.core.http.observer.ObserverWrapper;
import calibur.core.manager.UserSystem;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;

public class DramaMasterAnimeSettingActivity extends BaseActivity {

    @BindView(R.id.drama_master_anime_setting_banner)
    ImageView bannerImage;
    @BindView(R.id.drama_master_anime_setting_name)
    TextView animeName;
    @BindView(R.id.drama_master_anime_setting_avatar)
    RoundedImageView avatarImage;
    @BindView(R.id.drama_master_anime_setting_back)
    ImageView backBtn;
    @BindView(R.id.drama_master_anime_setting_tag_recycler_view)
    RecyclerView tagListView;
    @BindView(R.id.drama_master_anime_setting_tag_text)
    TextView tagTextView;
    @BindView(R.id.drama_master_anime_setting_tag_btn)
    TextView tagTextBtn;
    @BindView(R.id.drama_master_anime_setting_summary_edit)
    EditText summaryEdit;
    @BindView(R.id.drama_master_anime_setting_finishBtn)
    Button finishBtn;

    private int userId;
    private AnimeSettingTagAdapter tagAdapter;
    private AnimeShowInfo animeShowInfoData;
    private DramaActivity dramaActivity;
    private int bangumi_id;

    private List<AnimeShowInfo.AnimeShowInfoTags> tagsList;

    private static DramaMasterAnimeSettingActivity instance;

    private String avatarUrl,bannerUrl;
    private boolean isAvatarChanged = false;
    private boolean isBannerChanged = false;
    private boolean isHasTags = false;
    private boolean isHasSummary = false;

    private String summaryStr = "";

    SweetAlertDialog upLoadDialog;

    Call<Event<String>> bangumiEditCall;

    public static final String EDIT_BANGUMI_ACTION = "MasterSettingBangumiEditFinish";

    @Override
    protected int getContentViewId() {
        return R.layout.activity_drama_master_anime_setting;
    }

    @Override
    protected void onInit() {
        //如果未登录 结束页面
        if (!UserSystem.getInstance().isLogin()){
            ToastUtils.showShort(this,"登录之后才能创建偶像");
            finish();
        }
        setUserInfo();
        Intent intent = getIntent();
        bangumi_id = intent.getIntExtra("bangumi_id",0);
        animeShowInfoData = (AnimeShowInfo) intent.getSerializableExtra("animeShowInfoData");
        LogUtils.d("bangumiEditSet","animeShowInfoData = "+animeShowInfoData.toString());
        instance = this;
        initView();
        setOnListener();
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

    @Override
    protected void onResume() {
        super.onResume();
        tagAdapter.notifyDataSetChanged();
        setListVisible();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bangumiEditCall!=null){
            bangumiEditCall.cancel();
        }
    }

    private void initView() {
        bannerUrl = animeShowInfoData.getBanner();
        avatarUrl = animeShowInfoData.getAvatar();
        GlideUtils.loadImageView(DramaMasterAnimeSettingActivity.this,
                GlideUtils.setImageUrl(DramaMasterAnimeSettingActivity.this,
                        animeShowInfoData.getAvatar(),GlideUtils.THIRD_SCREEN),
                avatarImage);
        GlideUtils.loadImageViewBlur(DramaMasterAnimeSettingActivity.this,
                GlideUtils.setImageUrl(DramaMasterAnimeSettingActivity.this,
                        animeShowInfoData.getBanner(),GlideUtils.FULL_SCREEN),
                bannerImage);
        summaryEdit.setText(animeShowInfoData.getSummary());
        animeName.setText(animeShowInfoData.getName());
        setTagAdapter();
        setListVisible();
    }

    private void setTagAdapter() {
        tagsList = animeShowInfoData.getTags();
        tagAdapter = new AnimeSettingTagAdapter(R.layout.drama_master_setting_tag_item,tagsList,
                DramaMasterAnimeSettingActivity.this);
        tagListView.setLayoutManager(new GridLayoutManager(DramaMasterAnimeSettingActivity.this,3));
        tagListView.setAdapter(tagAdapter);
    }

    private void setOnListener() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tagTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseTag();
            }
        });
        tagTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseTag();
            }
        });
        avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAvatar();
            }
        });
        bannerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBanner();
            }
        });
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCheckUpload();
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

    private void setListVisible() {
        if (tagAdapter.getData()!=null&&tagAdapter.getData().size()!=0){
            tagListView.setVisibility(View.VISIBLE);
            tagTextView.setVisibility(View.GONE);
        }else {
            tagListView.setVisibility(View.GONE);
            tagTextView.setVisibility(View.VISIBLE);
        }
    }

    private void setAvatar() {
        MyAlbumUtils myAlbumUtils = new MyAlbumUtils();
        myAlbumUtils.setChooseImage(DramaMasterAnimeSettingActivity.this,1);
        myAlbumUtils.setOnChooseImageFinishListener(new MyAlbumUtils.OnChooseImageFinishListener() {
            @Override
            public void onFinish(ArrayList<AlbumFile> albumFiles) {
                GlideUtils.loadImageView(DramaMasterAnimeSettingActivity.this,
                        albumFiles.get(0).getPath(),avatarImage);
                avatarUrl = albumFiles.get(0).getPath();
                isAvatarChanged = true;
            }
        });
    }

    private void setBanner() {
        MyAlbumUtils myAlbumUtils = new MyAlbumUtils();
        myAlbumUtils.setChooseImage(DramaMasterAnimeSettingActivity.this,1);
        myAlbumUtils.setOnChooseImageFinishListener(new MyAlbumUtils.OnChooseImageFinishListener() {
            @Override
            public void onFinish(ArrayList<AlbumFile> albumFiles) {
                GlideUtils.loadImageViewBlur(DramaMasterAnimeSettingActivity.this,
                        albumFiles.get(0).getPath(),bannerImage);
                bannerUrl = albumFiles.get(0).getPath();
                isBannerChanged = true;
            }
        });
    }

    private void chooseTag() {
        Intent intent = new Intent(this,DramaMasterSettingChooseTagActivity.class);
        startActivity(intent);
    }

    private void setCheckUpload() {
        if (tagAdapter!=null&&tagAdapter.getData()!=null&&tagAdapter.getData().size()!=0){
            isHasTags = true;
        }
        if (summaryEdit.getText().toString()!=null&&summaryEdit.getText().toString().length()!=0){
            isHasSummary = true;
            summaryStr = summaryEdit.getText().toString();
        }
        setCheckImageChanged();
    }

    private void setCheckImageChanged(){
        if (isHasTags&&isHasSummary){
            if (isAvatarChanged||isBannerChanged){
                setShowUploadDialog();
                if (isAvatarChanged&&isBannerChanged){
                    //两者都变了
                    setUploadAvatar();
                }else if (!isAvatarChanged&&isBannerChanged){
                    //封面不变 背景变了
                    setUploadBanner();
                }else {
                    //封面变了 背景不变
                    setUploadAvatar();
                }
            }else {
                //两者都不变
                setShowUploadDialog();
                setUploadInfo();
            }
        }else {
            ToastUtils.showShort(DramaMasterAnimeSettingActivity.this,"请完善信息！");
        }

    }

    private void setShowUploadDialog() {
        upLoadDialog = new SweetAlertDialog(DramaMasterAnimeSettingActivity.this,SweetAlertDialog.PROGRESS_TYPE);
        upLoadDialog.setTitle("上传中...");
        upLoadDialog.setContentText("番剧信息修改中...");
        upLoadDialog.setCancelable(false);
        upLoadDialog.show();
        finishBtn.setClickable(false);
        finishBtn.setText("上传中");
    }

    private void setUploadAvatar() {
        //两者都变了的时候先传avatar 传完之后判断banner有没有改变 进行后续操作
        QiniuUtils qiniuUtils = new QiniuUtils();
        ArrayList<String> avatarUrlList = new ArrayList<>();
        avatarUrlList.add(avatarUrl);
        qiniuUtils.getQiniuUpToken(DramaMasterAnimeSettingActivity.this,avatarUrlList, userId,"bangumi");
        qiniuUtils.setOnQiniuUploadSuccessedListnener(new QiniuUtils.OnQiniuUploadSuccessedListnener() {
            @Override
            public void onUploadSuccess(ArrayList<QiniuImageParams.QiniuImageParamsData> imageParamsDataList) {
                avatarUrl = imageParamsDataList.get(0).getUrl();
                if (isBannerChanged){
                    //有banner改动 上传banner
                    setUploadBanner();
                }else {
                    //无banner改动 直接上传
                    setUploadInfo();
                }
            }
        });
        qiniuUtils.setOnQiniuUploadFailedListnener(new QiniuUtils.OnQiniuUploadFailedListnener() {
            @Override
            public void onFailed(String fialMessage) {
                setUploadFailed(fialMessage);
            }
        });
    }


    private void setUploadBanner() {
        QiniuUtils qiniuUtils = new QiniuUtils();
        ArrayList<String> bannerUrlList = new ArrayList<>();
        bannerUrlList.add(bannerUrl);
        qiniuUtils.getQiniuUpToken(DramaMasterAnimeSettingActivity.this,bannerUrlList, userId,"bangumi");
        qiniuUtils.setOnQiniuUploadSuccessedListnener(new QiniuUtils.OnQiniuUploadSuccessedListnener() {
            @Override
            public void onUploadSuccess(ArrayList<QiniuImageParams.QiniuImageParamsData> imageParamsDataList) {
                //无需在此判断avatar 直接上传
                bannerUrl = imageParamsDataList.get(0).getUrl();
                setUploadInfo();
            }
        });
        qiniuUtils.setOnQiniuUploadFailedListnener(new QiniuUtils.OnQiniuUploadFailedListnener() {
            @Override
            public void onFailed(String fialMessage) {
                setUploadFailed(fialMessage);
            }
        });
    }

    private void setUploadInfo() {
        BangumiEditParams params = new BangumiEditParams();

        params.setAvatar(avatarUrl);
        params.setBanner(bannerUrl);
        params.setSummary(summaryStr);

        List<Integer> tagIdList = new ArrayList<>();
        List<AnimeShowInfo.AnimeShowInfoTags> paramsTagsList = tagAdapter.getData();
        for (int i = 0; i < paramsTagsList.size(); i++) {
            tagIdList.add(paramsTagsList.get(i).getId());
        }
        params.setTags(tagIdList);

        apiService.getCallEditBangumi(bangumi_id,params)
                .compose(Rx2Schedulers.applyObservableAsync())
                .subscribe(new ObserverWrapper<String>(){
                    @Override
                    public void onSuccess(String s) {
                        setUpLoadFinish();
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        super.onFailure(code, errorMsg);
                        setUploadFailed("");
                    }
                });
    }
    private void setUploadFailed(String failMessage) {
        if (upLoadDialog!=null){
            String content = "";
            if (failMessage.contains("mimeType")){
                content = "所选部分图片格式不支持,\n 只支持jpg,jpeg,png,gif格式上传";
            }else if (failMessage.length()!=0){
                content = failMessage;
            }else {
                content = "因网络原因，番剧信息修改失败了";
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
            finishBtn.setText("保存");
        }
    }
    private void setUpLoadFinish() {
        if (upLoadDialog!=null){
            upLoadDialog.setTitleText("修改成功!")
                    .setContentText("番剧信息修改成功，\n 重新进入番剧页面可查看改动!")
                    .setConfirmText("好的")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            //上传成功 点击事件
                            Intent intent = new Intent();
                            intent.setAction("MasterSettingBangumiEditFinish");
                            sendBroadcast(intent);
                            finish();
                        }
                    })
                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
        }
    }

    @Override
    protected void handler(Message msg) {

    }

    public static DramaMasterAnimeSettingActivity getInstance() {
        return instance;
    }

    private GetChoosedTagList getChoosedTagList;

    public void setGetChoosedTagList(GetChoosedTagList getChoosedTagList) {
        this.getChoosedTagList = getChoosedTagList;
        getChoosedTagList.chooseDTagList(tagAdapter.getData());

        //注册当选择页面完成时候的返回接口
        DramaMasterSettingChooseTagActivity chooseTagActivity = DramaMasterSettingChooseTagActivity.getInstance();
        chooseTagActivity.setGetChoosedTagList(new DramaMasterSettingChooseTagActivity.GetChoosedTagsList() {
            @Override
            public void chooseDTagList(List<AnimeShowInfo.AnimeShowInfoTags> tags) {
                tagAdapter.setNewData(tags);
                setListVisible();
            }
        });
    }

    public interface GetChoosedTagList{
        void chooseDTagList(List<AnimeShowInfo.AnimeShowInfoTags> tags);
    }
}
