package com.riuir.calibur.ui.widget;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.PermissionUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.card.CardReplyActivity;
import com.riuir.calibur.utils.GlideUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;

public class SelectorImagesActivity extends BaseActivity {


    @BindView(R.id.select_image_cancel)
    ImageView cancelBtn;
    @BindView(R.id.select_image_ok)
    ImageView okBtn;
    @BindView(R.id.select_image_grid_view)
    GridView imageGridView;

    private static final int SCAN_OK = 1;

    public static final int BANNER_CODE = 10;
    public static final int ICON_CODE = 11;
    public static final int POST_CODE = 12;
    public static final int IMAGE_CODE = 13;
    public static final int IMAGE_CODE_SINGLE = 14;
    public static final int IMAGE_CODE_ALBUM_AVATAR = 15;


    public static final String RESULT_LIST_NAME = "selectionImageUrls";

    List<String> allImageUrls;
    ArrayList<String> selectorImageUrls;
    ImageGridViewAdpter gridViewAdpter;

    int code;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_selector_images;
    }

    @Override
    protected void onInit() {
        allImageUrls = new ArrayList<>();
        selectorImageUrls = new ArrayList<>();
        Intent intent = getIntent();
        code = intent.getIntExtra("code",0);
        getImages();

        setListener();
    }

    private void setListener() {
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 Intent intent = new Intent();
                if (code == POST_CODE){
                    intent.putStringArrayListExtra(RESULT_LIST_NAME,selectorImageUrls);
                    SelectorImagesActivity.this.setResult(POST_CODE,intent);
                    finish();
                }
                if (code == IMAGE_CODE){
                    intent.putStringArrayListExtra(RESULT_LIST_NAME,selectorImageUrls);
                    SelectorImagesActivity.this.setResult(IMAGE_CODE,intent);
                    finish();
                }
            }
        });
        if (code == ICON_CODE){
            okBtn.setVisibility(View.GONE);
        }
        if (code == BANNER_CODE){
            okBtn.setVisibility(View.GONE);
        }
        if (code == IMAGE_CODE_SINGLE){
            okBtn.setVisibility(View.GONE);
        }
        if (code == IMAGE_CODE_ALBUM_AVATAR){
            okBtn.setVisibility(View.GONE);
        }
    }

    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中
     */
    private void getImages() {
        PermissionUtils.chekReadAndWritePermission(SelectorImagesActivity.this);
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
                return;
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;//所有图片的信息
                ContentResolver mContentResolver = SelectorImagesActivity.this.getContentResolver();
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.MediaColumns.MIME_TYPE + "=? or "//设置条件，类似于SQL中的where
                                + MediaStore.MediaColumns.MIME_TYPE + "=?", new String[]{
                                "image/jpeg", "image/png"},//第三个参数里“？”代表的数据
                        MediaStore.MediaColumns.DATE_MODIFIED);//按照什么进行排序

                allImageUrls.clear();
                while (mCursor.moveToNext()) {
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.MediaColumns.DATA));//MediaColumns.DATA图片的绝对路径

                    if (allImageUrls.contains(path)){
                    }else {
                        allImageUrls.add(path);
                    }
                }
                mCursor.close();
                handler.sendEmptyMessage(SCAN_OK);
            }
        }).start();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionUtils.REQUEST_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限被用户同意，可以做你要做的事情了。
                    getImages();
                } else {
                    // 权限被用户拒绝了，可以提示用户,关闭界面等等。
                    ToastUtils.showShort(SelectorImagesActivity.this, "未取得授权，无法选择图片");
                }
                return;
            }
        }
    }


    @Override
    protected void handler(Message msg) {
        if (msg.what == SCAN_OK){
            setAdapter();
        }
    }

    private void setAdapter() {
        gridViewAdpter = new ImageGridViewAdpter();
        imageGridView.setAdapter(gridViewAdpter);
    }

    class ImageGridViewAdpter extends BaseAdapter{


        @Override
        public int getCount() {
            return allImageUrls.size();
        }

        @Override
        public Object getItem(int i) {
            return allImageUrls.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            final ImageViewHolder viewHolder;
            if (view == null){
                view = getLayoutInflater().inflate(R.layout.selector_image_gird_view_item,null);
                viewHolder = new ImageViewHolder();
                viewHolder.imageView = view.findViewById(R.id.selector_image_grid_item_image);
                viewHolder.checkBox = view.findViewById(R.id.selector_image_grid_item_check);
                view.setTag(viewHolder);
            }else {
                viewHolder = (ImageViewHolder) view.getTag();
            }

            if (code == POST_CODE||code == IMAGE_CODE){
                viewHolder.checkBox.setVisibility(View.VISIBLE);
            }else {
                viewHolder.checkBox.setVisibility(View.GONE);
            }

            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b){
                        int maxNum = 9;
                        if (code == POST_CODE){
                            maxNum = 9;
                        }else if(code == IMAGE_CODE){
                            maxNum = 15;
                        }else if (code == BANNER_CODE){
                            maxNum = 1;
                        }else if (code == ICON_CODE){
                            maxNum = 1;
                        }else if (code == IMAGE_CODE_SINGLE){
                            maxNum = 1;
                        }else if (code == IMAGE_CODE_ALBUM_AVATAR){
                            maxNum = 1;
                        }else {
                            maxNum = 9;
                        }

                        if (selectorImageUrls.size()>=maxNum){
                            viewHolder.checkBox.setChecked(false);
                            ToastUtils.showShort(SelectorImagesActivity.this,"最多只能选择"+maxNum+"张图哦");
                        }else {
                            selectorImageUrls.add(allImageUrls.get(i));
                        }
                    }else {
                        selectorImageUrls.remove(allImageUrls.get(i));
                    }
                }
            });
            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (code == POST_CODE||code == IMAGE_CODE){
                        viewHolder.checkBox.toggle();
                    }else {
                        selectorImageUrls.add(allImageUrls.get(i));
                        setMineSetImg();
                    }

                }
            });

            if (selectorImageUrls.contains(allImageUrls.get(i))){
                viewHolder.checkBox.setChecked(true);
            }else {
                viewHolder.checkBox.setChecked(false);
            }

            File file = new File(allImageUrls.get(i));
            GlideUtils.loadImageViewFromFile(SelectorImagesActivity.this,file,viewHolder.imageView);

            return view;
        }
    }

    private void setMineSetImg() {
        Intent intent = new Intent();
        if (code == ICON_CODE){
            intent.putStringArrayListExtra(RESULT_LIST_NAME,selectorImageUrls);
            SelectorImagesActivity.this.setResult(ICON_CODE,intent);
            finish();
        }
        if (code == BANNER_CODE){
            intent.putStringArrayListExtra(RESULT_LIST_NAME,selectorImageUrls);
            SelectorImagesActivity.this.setResult(BANNER_CODE,intent);
            finish();
        }
        if (code == IMAGE_CODE_SINGLE){
            intent.putStringArrayListExtra(RESULT_LIST_NAME,selectorImageUrls);
            SelectorImagesActivity.this.setResult(IMAGE_CODE_SINGLE,intent);
            finish();
        }
        if (code == IMAGE_CODE_ALBUM_AVATAR){
            intent.putStringArrayListExtra(RESULT_LIST_NAME,selectorImageUrls);
            SelectorImagesActivity.this.setResult(IMAGE_CODE_ALBUM_AVATAR,intent);
            finish();
        }
    }

    class ImageViewHolder {
        ImageView imageView;
        CheckBox checkBox;
    }
}
