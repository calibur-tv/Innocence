package com.riuir.calibur.ui.home.card;

import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.riuir.calibur.R;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.utils.GlideUtils;

import butterknife.BindView;

public class CardPreviewPictureActivity extends BaseActivity {

    @BindView(R.id.card_preview_viewpager)
    ViewPager previewViewpager;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_card_preview_picture;
    }

    @Override
    protected void onInit() {

    }

    @Override
    protected void handler(Message msg) {

    }

    class PerviewPictureViewPagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

//            String url = imageUrls.get(position);
//            PhotoView photoView = new PhotoView(CardPreviewPictureActivity.this);
//            GlideUtils.loadImageView(CardPreviewPictureActivity.this,url,photoView);
//            container.addView(photoView);
//            photoView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    finish();
//                }
//            });
//            return photoView;
            return super.instantiateItem(container, position);
        }
    }
}
