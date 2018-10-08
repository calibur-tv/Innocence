package com.riuir.calibur.ui.widget.emptyView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.riuir.calibur.R;

public class AppListFailedView extends RelativeLayout{

    Context context;
    View view;

    public AppListFailedView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public AppListFailedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public AppListFailedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private void initView() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.app_list_failed_view,this,true);
    }
}
