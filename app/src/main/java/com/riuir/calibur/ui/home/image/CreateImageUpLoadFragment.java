package com.riuir.calibur.ui.home.image;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.riuir.calibur.assistUtils.PermissionUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.params.QiniuImageParams;
import com.riuir.calibur.data.params.newImage.CreateNewImageForAlbum;
import com.riuir.calibur.data.params.newImage.CreateNewImageSingle;
import com.riuir.calibur.data.qiniu.QiniuUpToken;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.home.adapter.CreateCardImageGridAdapter;
import com.riuir.calibur.ui.home.card.CardCreateNewActivity;
import com.riuir.calibur.ui.home.card.CardShowInfoActivity;
import com.riuir.calibur.ui.home.choose.ChooseImageAlbumActivity;
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
public class CreateImageUpLoadFragment extends BaseFragment {

    @BindView(R.id.create_image_upload_is_single)
    SwitchButton isSingleSwitch;
    @BindView(R.id.create_image_upload_picture_name)
    EditText pictureName;
    @BindView(R.id.create_image_upload_choose_bangumi_layout)
    RelativeLayout bangumiLayout;
    @BindView(R.id.create_image_upload_choose_album_layout)
    RelativeLayout albumLayout;
    @BindView(R.id.create_image_upload_choose_bangumi_icon)
    RoundedImageView bangumiIcon;
    @BindView(R.id.create_image_upload_choose_bangumi_name)
    TextView bangumiNameText;
    @BindView(R.id.create_image_upload_choose_album_icon)
    RoundedImageView albumIcon;
    @BindView(R.id.create_image_upload_choose_album_name)
    TextView albumNameText;
    @BindView(R.id.create_image_upload_is_creator_layout)
    LinearLayout isCreatorLayout;
    @BindView(R.id.create_image_upload_is_creator)
    SwitchButton isCreatorSwitch;
    @BindView(R.id.create_image_upload_image_grid)
    RecyclerView imageGrid;
    @BindView(R.id.create_image_upload_send)
    Button sendBtn;

    boolean isSingle = false;
    boolean isCreator = false;


    Configuration config;
    UploadManager uploadManager;

    CreateCardImageGridAdapter imageGridAdapter;
    ArrayList<String> baseImagesUrl;
    ArrayList<String> imagesUrl;
    List<String> uploadUrlList;

    int bangumiId;
    String bangumiAvatar;
    String bangumiName;

    int albumId;
    String albumName;
    String albumPoster;

    SweetAlertDialog upLoadDialog;
    ArrayList<QiniuImageParams.QiniuImageParamsData> qiniuImageParamsDataList = new ArrayList<>();
    private int urlTag;
    private int userId;

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_create_image_up_load;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {

        userId = Constants.userInfoData.getId();
        setImageAdapter();
        isSingle = true;
        setIsSingle();
        setIsSingleListener();
        config = QiniuUtils.getQiniuConfig();
        uploadManager = new UploadManager(config);
        setListener();
    }

    private void setIsSingleListener() {
        isSingleSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                isSingle = isChecked;
                setIsSingle();
            }
        });
    }
    private void setIsSingle(){
        if (isSingleSwitch.isChecked()){
            pictureName.setVisibility(View.VISIBLE);
            bangumiLayout.setVisibility(View.VISIBLE);
            isCreatorLayout.setVisibility(View.VISIBLE);
            albumLayout.setVisibility(View.GONE);
        }else {
            pictureName.setVisibility(View.GONE);
            bangumiLayout.setVisibility(View.GONE);
            isCreatorLayout.setVisibility(View.GONE);
            albumLayout.setVisibility(View.VISIBLE);
        }
        baseImagesUrl.clear();
        imageGridAdapter.setNewData(baseImagesUrl);
        imageGridAdapter.addData("add");
    }

    private void setImageAdapter() {
        if (baseImagesUrl == null){
            baseImagesUrl = new ArrayList<>();
        }

        imageGridAdapter = new CreateCardImageGridAdapter(R.layout.reply_card_image_grid_item,baseImagesUrl,getContext());
        imageGrid.setLayoutManager(new GridLayoutManager(getContext(),4));

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
                        if (isSingle){
                            intent.putExtra("code",SelectorImagesActivity.IMAGE_CODE_SINGLE);
                            startActivityForResult(intent, SelectorImagesActivity.IMAGE_CODE_SINGLE);
                        }else {
                            intent.putExtra("code",SelectorImagesActivity.IMAGE_CODE);
                            startActivityForResult(intent, SelectorImagesActivity.IMAGE_CODE);
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
                Intent intent = new Intent(getContext(), ChooseNewCardBangumiActivity.class);
                intent.putExtra("code",ChooseNewCardBangumiActivity.IMAGE);
                startActivityForResult(intent,ChooseNewCardBangumiActivity.IMAGE);
            }
        });
        albumLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ChooseImageAlbumActivity.class);
                startActivityForResult(intent,ChooseImageAlbumActivity.ALBUM_CODE);
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
        uploadUrlList = imageGridAdapter.getData();
        uploadUrlList.remove("add");
        if (isSingle){
            if (pictureName.getText().toString()!=null&&pictureName.getText().toString().length()!=0&&
                    bangumiId!=0&&uploadUrlList.size()!=0){
                setUpLoadDialog();
            }else {
                if (bangumiId == 0){
                    ToastUtils.showShort(getContext(),"请选择所属番剧");
                }else if (uploadUrlList.size()==0){
                    ToastUtils.showShort(getContext(),"请选择图片");
                }else {
                    ToastUtils.showShort(getContext(),"请完善您的上传内容");
                }
            }
        }else {
            if (albumId!=0&&uploadUrlList.size()!=0){
                setUpLoadDialog();
            }else if (albumId == 0){
                ToastUtils.showShort(getContext(),"请选择相册");
            }else {
                ToastUtils.showShort(getContext(),"请添加图片");
            }
        }
    }

    private void setUpLoadDialog(){
        //检查完成 开启上传
        upLoadDialog = new SweetAlertDialog(getContext(),SweetAlertDialog.PROGRESS_TYPE);
        upLoadDialog.setTitle("上传中...");
        upLoadDialog.setContentText("您的图片正在上传中...");
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
                ToastUtils.showShort(getContext(),"网络异常，请稍后再试");
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
                            if (isSingle){
                                setUpLoadSingle();
                            }else {
                                setUpLoadAlbumImage();
                            }
                        }
                        LogUtils.d("newCardCreate","icon url = "+params.getData().getUrl());
                    }else if(info.isCancelled()){
                        ToastUtils.showShort(getContext(),"取消上传");
                        setUpLoadDiaLogFail();
                    }else if (info.isNetworkBroken()){
                        ToastUtils.showShort(getContext(),"网络异常，请稍后再试");
                        setUpLoadDiaLogFail();
                    }
                }
            },null);
        }
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

        //TODO 越过geetest
        long time = System.currentTimeMillis()/1000;
        apiPost.getCreateImageSingle(imageSingle,time+"",time+"Dark-Flame-Master")
                .enqueue(new Callback<Event<Integer>>() {
            @Override
            public void onResponse(Call<Event<Integer>> call, Response<Event<Integer>> response) {
                if (response!=null&&response.isSuccessful()){
                    //创建帖子成功
                    //跳转进入帖子
                    final int id = response.body().getData();
                    upLoadDialog.setTitleText("上传成功!")
                            .setContentText("您的图片上传成功!")
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
            public void onFailure(Call<Event<Integer>> call, Throwable t) {
                ToastUtils.showShort(getContext(),"网络异常，请稍后再试");
                setUpLoadDiaLogFail();
            }
        });
    }
    private void setUpLoadAlbumImage() {
        CreateNewImageForAlbum imageForAlbum = new CreateNewImageForAlbum();
        imageForAlbum.setAlbum_id(albumId);
        imageForAlbum.setImages(qiniuImageParamsDataList);
        apiPost.getCreateImageForAlbum(imageForAlbum).enqueue(new Callback<Event<Integer>>() {
            @Override
            public void onResponse(Call<Event<Integer>> call, Response<Event<Integer>> response) {
                if (response!=null&&response.isSuccessful()){
                    //创建帖子成功
                    //跳转进入帖子
//                    LogUtils.d("createImage","response.body = "+response.body()+"\n");
//                    final int id = response.body().getData();
                    upLoadDialog.setTitleText("上传成功!")
                            .setContentText("您的图片上传成功!")
                            .setConfirmText("好的")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    if (albumId!=0){
                                        Intent intent = new Intent(getContext(),ImageShowInfoActivity.class);
                                        intent.putExtra("imageID",albumId);
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
            public void onFailure(Call<Event<Integer>> call, Throwable t) {
                ToastUtils.showShort(getContext(),"网络异常，请稍后再试");
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
        GlideUtils.loadImageViewCircle(getContext(),
                GlideUtils.setImageUrlForAlbum(getContext(),bangumiAvatar,50),bangumiIcon);
        bangumiNameText.setText(bangumiName);
    }

    private void setAlbum() {
        GlideUtils.loadImageViewCircle(getContext(),
                GlideUtils.setImageUrlForAlbum(getContext(),albumPoster,50),albumIcon);
        albumNameText.setText(albumName);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取图片路径
        if ( resultCode == SelectorImagesActivity.IMAGE_CODE && data != null) {
            imagesUrl = data.getStringArrayListExtra(SelectorImagesActivity.RESULT_LIST_NAME);

            imageGridAdapter.remove(imageGridAdapter.getData().size()-1);
            if (imageGridAdapter.getData().size()+imagesUrl.size()>15){
                ToastUtils.showShort(getContext(),"单次上传图片最多15张，自动保存本次所选");
                imageGridAdapter.setNewData(imagesUrl);
            }else {
                imageGridAdapter.addData(imagesUrl);
            }
            imageGridAdapter.addData("add");
        }

        if ( resultCode == SelectorImagesActivity.IMAGE_CODE_SINGLE && data != null) {
            imagesUrl = data.getStringArrayListExtra(SelectorImagesActivity.RESULT_LIST_NAME);

            imageGridAdapter.remove(imageGridAdapter.getData().size()-1);
            if (imageGridAdapter.getData().size()!=0){
                baseImagesUrl.clear();
                imageGridAdapter.setNewData(baseImagesUrl);
                ToastUtils.showShort(getContext(),"单张模式，自动保存本次所选");
            }
            imageGridAdapter.addData(imagesUrl);

            imageGridAdapter.addData("add");
        }

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

    }



}
