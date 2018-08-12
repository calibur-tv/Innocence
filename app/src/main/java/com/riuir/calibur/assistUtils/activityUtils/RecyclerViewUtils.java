package com.riuir.calibur.assistUtils.activityUtils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class RecyclerViewUtils {

    public static final void setScorllToTop(RecyclerView recyclerView){
        recyclerView.scrollToPosition(0);
        LinearLayoutManager mLayoutManager =
                (LinearLayoutManager) recyclerView.getLayoutManager();
        mLayoutManager.scrollToPositionWithOffset(0, 0);
    }
}
