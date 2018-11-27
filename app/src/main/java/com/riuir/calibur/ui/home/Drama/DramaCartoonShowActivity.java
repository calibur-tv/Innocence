package com.riuir.calibur.ui.home.Drama;

import android.content.Intent;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;

import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.Drama.adapter.DramaCartoonShowAdapter;
import com.riuir.calibur.ui.home.image.ImageShowInfoActivity;
import com.riuir.calibur.ui.widget.emptyView.AppListEmptyView;
import com.riuir.calibur.ui.widget.emptyView.AppListFailedView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import calibur.core.http.models.followList.image.ImageShowInfoPrimacy;
import calibur.core.http.observer.ObserverWrapper;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DramaCartoonShowActivity extends BaseActivity {

    @BindView(R.id.drama_cartoon_show_refresh)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.drama_cartoon_show_list_view)
    RecyclerView cartoonShowList;
    @BindView(R.id.drama_cartoon_show_title_layout)
    RelativeLayout titleLayout;
    @BindView(R.id.drama_cartoon_show_back)
    ImageView backBtn;
    @BindView(R.id.drama_cartoon_show_title)
    TextView titleText;
    @BindView(R.id.drama_cartoon_show_comment_btn)
    TextView titleCommentBtn;

    AppListFailedView failedView;

    boolean isTitleVisibility = true;
    int cartoonId;

    private Call<ImageShowInfoPrimacy> cartoonCall;
    private ImageShowInfoPrimacy cartoonData;
    private List<ImageShowInfoPrimacy.ImageShowInfoPrimacyImages> baeCartoonList = new ArrayList<>();
    private List<ImageShowInfoPrimacy.ImageShowInfoPrimacyImages> cartoonList;

    DramaCartoonShowAdapter cartoonShowAdapter;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_drama_cartoon_show;
    }

    @Override
    protected void onInit() {
        Intent intent = getIntent();
        cartoonId = intent.getIntExtra("cartoonId",0);
        setListener();
        setNet();
        setAdapter();
    }

    @Override
    public void onDestroy() {
        if (cartoonCall!=null){
            cartoonCall.cancel();
        }
        if (handler!=null){
            handler.removeMessages(0);
            handler.removeMessages(1);
        }
        super.onDestroy();
    }

    private void setNet() {
        apiService.getCallImageShowPrimacy(cartoonId)
                .compose(Rx2Schedulers.applyObservableAsync())
                .subscribe(new ObserverWrapper<ImageShowInfoPrimacy>(){
                    @Override
                    public void onSuccess(ImageShowInfoPrimacy imageShowInfoPrimacy) {
                        cartoonData = imageShowInfoPrimacy;
                        setView();
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        super.onFailure(code, errorMsg);
                        if (refreshLayout!=null){
                            setFailedView();
                        }
                    }
                });
    }

    private void setView() {
        if (refreshLayout!=null){
            refreshLayout.setRefreshing(false);
            int partInt = (int) cartoonData.getPart();
            float chengedPart = partInt;
            if (chengedPart == cartoonData.getPart()){
                titleText.setText("第"+partInt+"话："+cartoonData.getName());
            }else {
                titleText.setText("第"+cartoonData.getPart()+"话："+cartoonData.getName());
            }
            setNewData();
        }
    }

    private void setNewData() {
        cartoonList = cartoonData.getImages();
        cartoonShowAdapter.setNewData(cartoonList);
    }

    private void setAdapter() {
        cartoonShowAdapter = new DramaCartoonShowAdapter(R.layout.drama_cartoon_show_item,baeCartoonList,DramaCartoonShowActivity.this);
        cartoonShowList.setLayoutManager(new LinearLayoutManager(DramaCartoonShowActivity.this));
        cartoonShowAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        cartoonShowAdapter.setHasStableIds(true);
        cartoonShowList.setAdapter(cartoonShowAdapter);
        cartoonShowAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.drama_cartoon_show_item_photo_view){
                    if (isTitleVisibility){
                        isTitleVisibility = false;
                        titleLayout.setVisibility(View.GONE);
                        handler.sendEmptyMessage(1);
                    }else {
                        isTitleVisibility = true;
                        titleLayout.setVisibility(View.VISIBLE);
                        handler.sendEmptyMessageDelayed(0,1000);
                    }
                }
            }
        });
        handler.sendEmptyMessageDelayed(0,1000);
    }

    private void setListener() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setNet();
            }
        });
        titleCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DramaCartoonShowActivity.this,DramaCartoonCommentActivity.class);
                intent.putExtra("cartoonData",cartoonData);
                startActivity(intent);
            }
        });
    }

    private void setFailedView(){
        //加载失败 下拉重试
        failedView = new AppListFailedView(DramaCartoonShowActivity.this);
        failedView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        cartoonShowAdapter.setEmptyView(failedView);
    }

    int gongSec = 0;
    @Override
    protected void handler(Message msg) {
        switch (msg.what){
            case 0:
                if (gongSec>=4){
                    handler.sendEmptyMessage(1);
                }else {
                    gongSec++;
                    handler.sendEmptyMessageDelayed(0,1000);
                }
                break;
            case 1:
                gongSec = 0;
                isTitleVisibility = false;
                if (titleLayout!=null){
                    titleLayout.setVisibility(View.GONE);
                }
                handler.removeMessages(0);
                break;
            default:
                break;
        }
    }


}
