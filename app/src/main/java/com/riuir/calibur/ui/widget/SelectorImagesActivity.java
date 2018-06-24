package com.riuir.calibur.ui.widget;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
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
    TextView cancelTextView;
    @BindView(R.id.select_image_ok)
    TextView okTextView;
    @BindView(R.id.select_image_grid_view)
    GridView imageGridView;

    private static final int SCAN_OK = 1;

    List<String> allImageUrls;
    ArrayList<String> selectorImageUrls;

    ImageGridViewAdpter gridViewAdpter;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_selector_images;
    }

    @Override
    protected void onInit() {
        allImageUrls = new ArrayList<>();
        selectorImageUrls = new ArrayList<>();
        getImages();

        setListener();
    }

    private void setListener() {
        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        okTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.putStringArrayListExtra("selectionImageUrls",selectorImageUrls);
                SelectorImagesActivity.this.setResult(CardReplyActivity.IMAGE,intent);
                finish();
            }
        });
    }

    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中
     */
    private void getImages() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
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

            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b){
                        if (selectorImageUrls.size()>=9){
                            viewHolder.checkBox.setChecked(false);
                            ToastUtils.showShort(SelectorImagesActivity.this,"最多可以选择9张图哦");
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
                    viewHolder.checkBox.toggle();
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

    class ImageViewHolder {
        ImageView imageView;
        CheckBox checkBox;
    }
}
