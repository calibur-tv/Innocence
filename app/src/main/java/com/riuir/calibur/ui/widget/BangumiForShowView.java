package com.riuir.calibur.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.riuir.calibur.R;
import com.riuir.calibur.utils.GlideUtils;

public class BangumiForShowView extends RelativeLayout implements View.OnClickListener {

    private TextView name;
    private TextView summary;
    private ImageView imageView;

    public BangumiForShowView(Context context) {
        super(context);
        initView(context);
    }

    public BangumiForShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public BangumiForShowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void initView(Context context){
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.layout_bangumi_show_info_view, this, true);
        imageView = view.findViewById(R.id.bangumi_view_image);
        name = view.findViewById(R.id.bangumi_view_name);
        summary = view.findViewById(R.id.bangumi_view_summary);

    }


    public final void setName(CharSequence text){
        name.setText(text);
    }
    public final void setSummary(CharSequence text){
        summary.setText(text);
    }
    public final void setImageView(Context context,String path){
        GlideUtils.loadImageView(context,path,imageView);
    }

    @Override
    public void onClick(View view) {

    }
}
