package com.riuir.calibur.ui.home.image;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
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
import com.riuir.calibur.data.params.newImage.CreateNewAlbum;
import com.riuir.calibur.data.qiniu.QiniuUpToken;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.home.adapter.CreateCardImageGridAdapter;
import com.riuir.calibur.ui.home.card.CardCreateNewActivity;
import com.riuir.calibur.ui.home.choose.ChooseNewCardBangumiActivity;
import com.riuir.calibur.ui.widget.SelectorImagesActivity;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.GlideUtils;
import com.riuir.calibur.utils.QiniuUtils;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateImageAlbumFragment extends BaseFragment {

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
    @BindView(R.id.create_image_album_image_grid)
    RecyclerView imageGrid;
    @BindView(R.id.create_image_album_send)
    Button sendBtn;

    Configuration config;
    UploadManager uploadManager;

    CreateCardImageGridAdapter imageGridAdapter;
    ArrayList<String> baseImagesUrl;
    ArrayList<String> imagesUrl;
    List<String> uploadUrlList;


    ArrayList<QiniuImageParams.QiniuImageParamsData> qiniuImageParamsDataList = new ArrayList<>();
    private int urlTag;
    private int userId;

    int bangumiId;
    String bangumiAvatar;
    String bangumiName;

    boolean isCreator = false;

    SweetAlertDialog upLoadDialog;

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_create_image_album;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        userId = Constants.userInfoData.getId();
        setImageAdapter();
        config = QiniuUtils.getQiniuConfig();
        uploadManager = new UploadManager(config);
        setListener();
    }

    private void setImageAdapter() {
        if (baseImagesUrl == null){
            baseImagesUrl = new ArrayList<>();
        }

        imageGridAdapter = new CreateCardImageGridAdapter(R.layout.reply_card_image_grid_item,baseImagesUrl,getContext());
        imageGrid.setLayoutManager(new GridLayoutManager(getContext(),3));

        imageGrid.setAdapter(imageGridAdapter);
        imageGridAdapter.addData("add");

    }

    private void setListener() {
        imageGridAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                switch (view.getId()){
                    case R.id.reply_card_add_image_btn:
                        Intent intent = new Intent(getContext(), SelectorImagesActivity.class);
                        intent.putExtra("code",SelectorImagesActivity.IMAGE_CODE_ALBUM_AVATAR);
                        startActivityForResult(intent, SelectorImagesActivity.IMAGE_CODE_ALBUM_AVATAR);

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
                Intent intent = new Intent(getContext(), ChooseNewCardBangumiActivity.class);
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
        uploadUrlList = imageGridAdapter.getData();
        uploadUrlList.remove("add");
        if (uploadUrlList.size()!=0&&albumNameEdit.getText().toString()!=null&&
                albumNameEdit.getText().toString().length()!=0&&bangumiId!=0){
            setUpLoadDialog();
        }else {
            if (bangumiId == 0){
                ToastUtils.showShort(getContext(),"请选择所属番剧");
            }else if (uploadUrlList.size() == 0){
                ToastUtils.showShort(getContext(),"请选择封面");
            }else {
                ToastUtils.showShort(getContext(),"请完善您的上传内容");
            }
        }
    }

    private void setUpLoadDialog(){
        //检查完成 开启上传
        upLoadDialog = new SweetAlertDialog(getContext(),SweetAlertDialog.PROGRESS_TYPE);
        upLoadDialog.setTitle("上传中...");
        upLoadDialog.setContentText("您的相册正在上传中...");
        upLoadDialog.setCancelable(false);
        upLoadDialog.show();
        sendBtn.setClickable(false);
        getQiniuToken();
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
                    ToastUtils.showShort(getContext(),info.getMessage());
                    setUpLoadDiaLogFail();
                }else {
                    ToastUtils.showShort(getContext(),"未知错误导致上传失败");
                    setUpLoadDiaLogFail();
                }
            }

            @Override
            public void onFailure(Call<QiniuUpToken> call, Throwable t) {
                ToastUtils.showShort(getContext(),"网络异常，请稍后再试1");
                setUpLoadDiaLogFail();
            }
        });
    }

    private void setQiniuUpLoadCheck(){
        uploadUrlList.remove("add");
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
                        ToastUtils.showShort(getContext(),"取消上传");
                        setUpLoadDiaLogFail();
                    }else if (info.isNetworkBroken()){
                        ToastUtils.showShort(getContext(),"网络异常，请稍后再试2");
                        setUpLoadDiaLogFail();
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
        //TODO 越过geetest
        long time = System.currentTimeMillis()/1000;
        apiPost.getCreateIAlbum(newAlbum,time+"",time+"Dark-Flame-Master")
                .enqueue(new Callback<CreateNewAlbumInfo>() {
            @Override
            public void onResponse(Call<CreateNewAlbumInfo> call, Response<CreateNewAlbumInfo> response) {
                if (response!=null&&response.isSuccessful()){
                    ChooseImageAlbum.ChooseImageAlbumData data = response.body().getData();
                    final int id = data.getId();
                    upLoadDialog.setTitleText("上传成功!")
                            .setContentText("您的相册上传成功!")
                            .setConfirmText("好的")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    if (id!=0){
                                        Intent intent = new Intent(getContext(),ImageShowInfoActivity.class);
                                        intent.putExtra("imageID",id);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }
                                }
                            })
                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

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
                    setUpLoadDiaLogFail();
                }else {
                    ToastUtils.showShort(getContext(),"未知错误导致上传失败");
                    setUpLoadDiaLogFail();
                }
            }

            @Override
            public void onFailure(Call<CreateNewAlbumInfo> call, Throwable t) {
                ToastUtils.showShort(getContext(),"网络异常，请稍后再试3");
                LogUtils.d("newCardCreate","album t = "+t.getMessage());
                setUpLoadDiaLogFail();
            }
        });


    }

    private void setUpLoadDiaLogFail(){
        if (upLoadDialog!=null){
            upLoadDialog.setTitleText("上传失败!")
                    .setContentText("因网络原因，帖子上传失败了QAQ")
                    .setConfirmText("确定")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            upLoadDialog.cancel();
                        }
                    })
                    .changeAlertType(SweetAlertDialog.ERROR_TYPE);
        }
        sendBtn.setClickable(true);
        uploadUrlList.add("add");
    }

    private void setBangumi() {
        GlideUtils.loadImageViewCircle(getContext(),bangumiAvatar,bangumiIcon);
        bangumiNameText.setText(bangumiName);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取图片路径

        if ( resultCode == SelectorImagesActivity.IMAGE_CODE_ALBUM_AVATAR && data != null) {
            imagesUrl = data.getStringArrayListExtra(SelectorImagesActivity.RESULT_LIST_NAME);

            imageGridAdapter.remove(imageGridAdapter.getData().size()-1);
            if (imageGridAdapter.getData().size()!=0){
                baseImagesUrl.clear();
                imageGridAdapter.setNewData(baseImagesUrl);
                ToastUtils.showShort(getContext(),"相册封面只能选一张，自动保存本次所选");
            }
            imageGridAdapter.addData(imagesUrl);

            imageGridAdapter.addData("add");
        }
        if (resultCode == ChooseNewCardBangumiActivity.IMAGE_ALBUM && data!=null){
            bangumiId = data.getIntExtra("bangumiId",0);
            bangumiAvatar = data.getStringExtra("bangumiAvatar");
            bangumiName = data.getStringExtra("bangumiName");
            setBangumi();
        }

    }
}
