package com.riuir.calibur.ui.widget.emptyView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;

public class AppListEmptyView extends RelativeLayout {

    View view;
    Context context;
    LayoutInflater layoutInflater;
    ImageView icon;
    TextView text;

    public AppListEmptyView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public AppListEmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public AppListEmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private void initView() {
        layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.app_list_empty_view,this,true);
        icon = view.findViewById(R.id.app_empty_view_icon);
        text = view.findViewById(R.id.app_empty_view_text);
    }
    public void setText(String textStr){
        if (text!=null){
            text.setText(textStr);
        }
    }

}
