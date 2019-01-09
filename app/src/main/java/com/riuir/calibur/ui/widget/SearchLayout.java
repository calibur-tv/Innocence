package com.riuir.calibur.ui.widget;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.riuir.calibur.R;
import com.riuir.calibur.ui.home.search.DramaSearchActivity;

public class SearchLayout extends RelativeLayout{
    RelativeLayout searchLayout;
    TextView searchEdit;
    ImageView searchIcon;


    Context context;

    public SearchLayout(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public SearchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public SearchLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private void initView() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.search_layout_view, this, true);
        searchLayout = view.findViewById(R.id.search_layout_search_layout);
        searchEdit = view.findViewById(R.id.search_layout_search_edit_text);
        searchIcon = view.findViewById(R.id.search_layout_search_btn);
        setListener();
    }

    private void setListener() {
        searchEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toSearchActivityIntent = new Intent(getContext(), DramaSearchActivity.class);

                //版本大于5.0的时候带有动画
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    context.startActivity(toSearchActivityIntent, ActivityOptions.makeSceneTransitionAnimation((Activity) context,
//                            view, "ToSearchDramaActivity").toBundle());
//                }else {
                    context.startActivity(toSearchActivityIntent);
//                }
            }
        });
    }
}
