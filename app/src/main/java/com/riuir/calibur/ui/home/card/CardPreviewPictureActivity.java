package com.riuir.calibur.ui.home.card;

import android.content.Intent;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.utils.GlideUtils;
import com.riuir.calibur.utils.glide.SaveImageFromViewUtils;

import java.util.ArrayList;

import butterknife.BindView;

public class CardPreviewPictureActivity extends BaseActivity {

    @BindView(R.id.preview_image_back_btn)
    ImageView backBtn;
    @BindView(R.id.card_preview_viewpager)
    ViewPager previewViewpager;
    String selectImageUrl;
    ArrayList<String> previewImaegUrlList;
    PerviewPictureViewPagerAdapter pagerAdapter;



    @Override
    protected int getContentViewId() {
        return R.layout.activity_card_preview_picture;
    }

    @Override
    protected void onInit() {
        Intent intent = getIntent();
        selectImageUrl = intent.getStringExtra("imageUrl");
        previewImaegUrlList = intent.getStringArrayListExtra("previewImagesList");
        setLog();
        setAdapter();
        setListener();
    }

    private void setLog() {
        for (int i = 0; i < previewImaegUrlList.size(); i++) {
            LogUtils.d("perImage","list "+i+" url = "+previewImaegUrlList.get(i));
        }
    }


    private void setListener() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setAdapter() {
        pagerAdapter = new PerviewPictureViewPagerAdapter();
        previewViewpager.setAdapter(pagerAdapter);
        setSelectPagerCount();
    }

    private void setSelectPagerCount() {
        for (int i = 0; i <previewImaegUrlList.size() ; i++){
            if (previewImaegUrlList.get(i).equals(selectImageUrl)){
                previewViewpager.setCurrentItem(i);
            }
        }
    }

    @Override
    protected void handler(Message msg) {

    }

    class PerviewPictureViewPagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return previewImaegUrlList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            final String url = previewImaegUrlList.get(position);
            View view = getLayoutInflater().inflate(R.layout.preview_view_pager_item_view,null);
            final PhotoView photoView = view.findViewById(R.id.preview_view_pager_item_photo_view);
            final TextView saveBtn = view.findViewById(R.id.preview_view_pager_item_save_btn);
            GlideUtils.loadImageView(CardPreviewPictureActivity.this,
                    GlideUtils.setImageUrl(CardPreviewPictureActivity.this,url,GlideUtils.FULL_SCREEN),photoView);

            photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CardPreviewPictureActivity.this.onBackPressed();
                }
            });

            //初始化和注册保存图片工具类
            final SaveImageFromViewUtils saveUtils = new SaveImageFromViewUtils();
            saveUtils.setOnStartSaveListener(new SaveImageFromViewUtils.OnStartSaveListener() {
                @Override
                public void onStartSave() {
                    saveBtn.setText("保存中...");
                    saveBtn.setClickable(false);
                }
            });
            saveUtils.setOnFinishSaveListener(new SaveImageFromViewUtils.OnFinishSaveListener() {
                @Override
                public void onFinishSave() {
                    saveBtn.setText("保存完成");
                    ToastUtils.showShort(CardPreviewPictureActivity.this,"图片已保存到本地相册~");
                }
            });
            //点击保存之后  开始保存
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveUtils.checkPermissionAndSaveImage(CardPreviewPictureActivity.this,url);
                }
            });

            container.addView(view);
            return view;

        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
            object=null;

        }
    }

}
