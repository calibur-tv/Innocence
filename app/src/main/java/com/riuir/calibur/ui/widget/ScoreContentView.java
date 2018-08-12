package com.riuir.calibur.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.DensityUtils;
import com.riuir.calibur.assistUtils.activityUtils.PerviewImageUtils;
import com.riuir.calibur.data.trending.ScoreShowInfoPrimacy;
import com.riuir.calibur.utils.GlideUtils;

import java.util.ArrayList;
import java.util.List;

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

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
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
                    textView.setTextSize(14);
                    layout.addView(textView);
                }
                if (contentS.get(i).getType().equals("img")){
                    ImageView primacyImageView = new ImageView(context);
                    primacyImageView.setLayoutParams(params);
                    primacyImageView.setScaleType(ImageView.ScaleType.FIT_START);
                    GlideUtils.loadImageView(context,
                            GlideUtils.setImageUrl(context,contentS.get(i).getUrl(),GlideUtils.FULL_SCREEN),
                            primacyImageView);
                    final int finalI = i;
                    primacyImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String url = contentS.get(finalI).getUrl();
                            PerviewImageUtils.startPerviewImage(context, previewImagesLists,url,view);
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
