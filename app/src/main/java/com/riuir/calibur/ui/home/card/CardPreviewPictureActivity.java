package com.riuir.calibur.ui.home.card;

import android.content.Intent;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.riuir.calibur.R;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.utils.GlideUtils;

import java.util.ArrayList;

import butterknife.BindView;

public class CardPreviewPictureActivity extends BaseActivity {

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

        setAdapter();
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

            String url = previewImaegUrlList.get(position);
            PhotoView photoView = new PhotoView(CardPreviewPictureActivity.this);
            photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            photoView.setLayoutParams(params);
            GlideUtils.loadImageViewpreview(CardPreviewPictureActivity.this,url,photoView);
            photoView.enable();

            photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CardPreviewPictureActivity.this.onBackPressed();
                }
            });

            container.addView(photoView);
            return photoView;

        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
            object=null;

        }
    }

}
