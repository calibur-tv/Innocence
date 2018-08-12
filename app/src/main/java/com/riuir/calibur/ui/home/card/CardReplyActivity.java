package com.riuir.calibur.ui.home.card;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.widget.SelectorImagesActivity;
import com.riuir.calibur.utils.GlideUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class CardReplyActivity extends BaseActivity  {

    @BindView(R.id.card_reply_activity_title)
    TextView titleTextview;
    @BindView(R.id.card_reply_activity_send)
    TextView titleTextSend;
    @BindView(R.id.card_reply_activity_cancel)
    TextView titleTextCancel;
    @BindView(R.id.card_reply_activity_edit)
    EditText commentEdit;
    @BindView(R.id.card_reply_activity_image_grid)
    RecyclerView gridImage;


    ReplyImageGridAdapter imageGridAdapter;

    ArrayList<String> baseImagesUrl;
    ArrayList<String> imagesUrl;

    //调用系统相册-选择图片
    public static final int IMAGE = 1;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_card_comment;
    }

    @Override
    protected void onInit() {
        setAdapter();


    }

    private void setAdapter() {
        if (baseImagesUrl == null){
            baseImagesUrl = new ArrayList<>();
        }
        imageGridAdapter = new ReplyImageGridAdapter(R.layout.reply_card_image_grid_item,baseImagesUrl);
        gridImage.setLayoutManager(new GridLayoutManager(CardReplyActivity.this,4));
        gridImage.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 10;
            }
        });
        imageGridAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.reply_card_image_grid_item_image_delete){

                    adapter.setNewData((List) adapter.getData().remove(position));

                }
            }
        });
        gridImage.setAdapter(imageGridAdapter);


        setFooterView();


    }

    private void setFooterView() {
        View view = this.getLayoutInflater().inflate(R.layout.reply_card_add_image_footer_view,null);
        ImageView imageViewAdd = view.findViewById(R.id.reply_card_add_image_btn);
        imageViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //相册
                Intent intent = new Intent(CardReplyActivity.this, SelectorImagesActivity.class);
                startActivityForResult(intent, IMAGE);
            }
        });
        imageGridAdapter.setFooterView(view);

    }

    @Override
    protected void handler(Message msg) {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取图片路径
        if ( resultCode == IMAGE && data != null) {
            imagesUrl = data.getStringArrayListExtra("selectionImageUrls");
            if (imageGridAdapter.getData().size()+imagesUrl.size()>9){
                ToastUtils.showShort(CardReplyActivity.this,"所选图片超出9张，自动保存本次所选");
                imageGridAdapter.setNewData(imagesUrl);
            }else {
                imageGridAdapter.addData(imagesUrl);
            }
        }

    }

    class ReplyImageGridAdapter extends BaseQuickAdapter<String,BaseViewHolder>{

        public ReplyImageGridAdapter(int layoutResId, @Nullable List<String> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {

            File file = new File(item);
            GlideUtils.loadImageViewFromFile(CardReplyActivity.this,file, (ImageView) helper.getView(R.id.reply_card_image_grid_item_image));
            helper.addOnClickListener(R.id.reply_card_image_grid_item_image_delete);

        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
}
