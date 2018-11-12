package com.riuir.calibur.utils.album;

import android.content.Context;

import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.Filter;
import com.yanzhenjie.album.api.widget.Widget;

import java.util.ArrayList;


import io.reactivex.annotations.NonNull;

public class MyAlbumUtils {

    ArrayList<AlbumFile> mAlbumFiles;
    private OnChooseImageFinishListener onChooseImageFinishListener;
    private Widget widget;


    public void setChooseImage(Context context,int selectNum){

        widget = Widget.newDarkBuilder(context)
                .title("选择图片")
                .statusBarColor(context.getResources().getColor(R.color.theme_magic_sakura_primary))
                .toolBarColor(context.getResources().getColor(R.color.theme_magic_sakura_primary))
                .mediaItemCheckSelector(context.getResources().getColor(R.color.theme_magic_sakura_primary),
                        context.getResources().getColor(R.color.theme_magic_sakura_primary)) // Image or video selection box.
                .bucketItemCheckSelector(context.getResources().getColor(R.color.theme_magic_sakura_primary),
                        context.getResources().getColor(R.color.theme_magic_sakura_primary)) // Select the folder selection box.
                .buttonStyle( // Used to configure the style of button when the image/video is not found.
                        Widget.ButtonStyle.newLightBuilder(context) // With Widget's Builder model.
                                .setButtonSelector(context.getResources().getColor(R.color.theme_magic_sakura_primary),
                                        context.getResources().getColor(R.color.theme_magic_sakura_primary)) // Button selector.
                                .build()
                )
                .build();

        final String webp = "webp";
        final long maxSize = 5*1024*1024;
        mAlbumFiles = new ArrayList<>();
        Album.image(context) // Image selection.
                .multipleChoice()
                .widget(widget)
                .camera(true)
                .columnCount(3)
                .selectCount(selectNum)
                .checkedList(mAlbumFiles)
                .filterMimeType(new Filter<String>() {
                    @Override
                    public boolean filter(String attributes) {
                        //过滤webp格式
                        if (attributes!=null){
                            return attributes.contains(webp);
                        }else {
                            return false;
                        }
                    }
                })
                .filterSize(new Filter<Long>() {
                    @Override
                    public boolean filter(Long attributes) {
                        //过滤大于5mb的图
                        return attributes>maxSize;
                    }
                })
                .onResult(new Action<ArrayList<AlbumFile>>() {
                    @Override
                    public void onAction(@NonNull ArrayList<AlbumFile> result) {
                        mAlbumFiles = result;
                        if (onChooseImageFinishListener!=null){
                            onChooseImageFinishListener.onFinish(mAlbumFiles);
                        }
                    }
                })
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) {
                    }
                })
                .start();
    }

    public void setOnChooseImageFinishListener(OnChooseImageFinishListener onChooseImageFinishListener) {
        this.onChooseImageFinishListener = onChooseImageFinishListener;
    }

    public interface OnChooseImageFinishListener{
        void onFinish(ArrayList<AlbumFile> albumFiles);
    }


}
