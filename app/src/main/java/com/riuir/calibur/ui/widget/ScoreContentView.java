package com.riuir.calibur.ui.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.DensityUtils;
import com.riuir.calibur.assistUtils.activityUtils.PreviewImageUtils;
import com.riuir.calibur.utils.GlideUtils;

import java.util.ArrayList;
import java.util.List;

import calibur.core.http.models.followList.score.ScoreShowInfoPrimacy;

public class ScoreContentView extends LinearLayout{

    Context context;
    LinearLayout layout;
    List<ScoreShowInfoPrimacy.ScoreShowInfoPrimacyContent> contentS;
    ArrayList<String> previewImagesLists;

    public ScoreContentView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public ScoreContentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public ScoreContentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private void initView() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.score_content_view_layout, this, true);
        layout = view.findViewById(R.id.score_content_view_layout_linear_layout);


    }

    public final void setContent(final List<ScoreShowInfoPrimacy.ScoreShowInfoPrimacyContent> contents, ArrayList<String> previewImagesList){
        contentS= contents;
        previewImagesLists =previewImagesList;


        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        params.setMargins(DensityUtils.dp2px(context,12),
                DensityUtils.dp2px(context,4),
                DensityUtils.dp2px(context,12),
                DensityUtils.dp2px(context,4));

        if (contentS!=null&&contentS.size()!=0){
            for (int i = 0; i < contentS.size(); i++) {
                if (contentS.get(i).getType().equals("txt")){
                    TextView textView = new TextView(context);
                    textView.setLayoutParams(params);
                    textView.setText(Html.fromHtml(contentS.get(i).getText()));
                    textView.setTextColor(getResources().getColor(R.color.color_FF7B7B7B));
                    textView.setTextIsSelectable(true);
                    textView.setTextSize(14);
                    layout.addView(textView);
                }
                if (contentS.get(i).getType().equals("img")){
                    ImageView primacyImageView = new ImageView(context);
                    int imgHeight = GlideUtils.getImageHeightDp(context,contentS.get(i).getHeight(),
                            contentS.get(i).getWidth(),24,1);

                    LayoutParams imgParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,imgHeight);
                    imgParams.setMargins(DensityUtils.dp2px(context,12),
                            DensityUtils.dp2px(context,4),
                            DensityUtils.dp2px(context,12),
                            DensityUtils.dp2px(context,4));

                    primacyImageView.setLayoutParams(imgParams);
                    primacyImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        primacyImageView.setTransitionName("ToPreviewImageActivity");
                    }
                    GlideUtils.loadImageView(context,
                            GlideUtils.setImageUrl(context,contentS.get(i).getUrl(),GlideUtils.FULL_SCREEN),
                            primacyImageView);
                    final int finalI = i;
                    primacyImageView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String url = contentS.get(finalI).getUrl();
                            PreviewImageUtils.startPreviewImage(context, previewImagesLists,url,view);
                        }
                    });
                    layout.addView(primacyImageView);

                    TextView textView = new TextView(context);
                    textView.setLayoutParams(params);
                    textView.setText(Html.fromHtml(contentS.get(i).getText()));
                    textView.setTextColor(getResources().getColor(R.color.color_FF9B9B9B));
                    textView.setTextSize(14);
                    textView.setGravity(Gravity.CENTER_HORIZONTAL);
                    layout.addView(textView);
                }
            }
        }

    }
}
