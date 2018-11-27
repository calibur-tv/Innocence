package com.riuir.calibur.ui.widget.replyAndComment;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.riuir.calibur.R;
import com.riuir.calibur.utils.GlideUtils;

import java.io.File;
import java.util.List;

public class ReplyAndCommentImageChoosedAdapter extends BaseQuickAdapter<String,BaseViewHolder> {

    Context context;
    //reply_and_comment_choosed_image_list_item

    public ReplyAndCommentImageChoosedAdapter(int layoutResId, @Nullable List<String> data, Context context) {
        super(layoutResId, data);
        this.context = context;
    }

    public ReplyAndCommentImageChoosedAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        ImageView delete = helper.getView(R.id.reply_and_comment_choosed_image_item_image_delete);
        RelativeLayout add = helper.getView(R.id.reply_and_comment_choosed_image_add_image_btn);
        ImageView img = helper.getView(R.id.reply_and_comment_choosed_image_item_image);
        if (item.equals("add")){
            delete.setVisibility(View.GONE);
            img.setVisibility(View.GONE);
            add.setVisibility(View.VISIBLE);
        }else {
            delete.setVisibility(View.VISIBLE);
            img.setVisibility(View.VISIBLE);
            add.setVisibility(View.GONE);
            File file = new File(item);
            GlideUtils.loadImageViewFromFile(context,file, img);
        }
        helper.addOnClickListener(R.id.reply_and_comment_choosed_image_item_image_delete);
        helper.addOnClickListener(R.id.reply_and_comment_choosed_image_add_image_btn);
    }
}
