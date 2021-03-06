package com.riuir.calibur.ui.home.choose;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;

import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.choose.adapter.ChooseBangumiAdapter;
import com.riuir.calibur.ui.home.choose.adapter.ChooseImageAlbumAdapter;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import calibur.core.http.models.create.ChooseImageAlbum;
import calibur.core.http.observer.ObserverWrapper;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseImageAlbumActivity extends BaseActivity {
    @BindView(R.id.choose_image_album_back)
    ImageView backBtn;
    @BindView(R.id.choose_image_album_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.choose_image_album_refresh)
    SwipeRefreshLayout refreshLayout;
    List<ChooseImageAlbum> baseAlbumList = new ArrayList<>();
    List<ChooseImageAlbum> albumList;

    Call<ChooseImageAlbum> callImageAlbum;

    ChooseImageAlbumAdapter albumAdapter;

    boolean isFirst = false;
    boolean isRefresh = false;

    public static final int ALBUM_CODE = 200;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_choose_image_album;
    }

    @Override
    protected void onInit() {
        isFirst = true;
        refreshLayout.setRefreshing(true);
        setAdapter();
        setNet();
    }

    private void setAdapter() {
        albumAdapter = new ChooseImageAlbumAdapter(R.layout.choose_all_bangumi_list_item,baseAlbumList,ChooseImageAlbumActivity.this);

        recyclerView.setLayoutManager(new LinearLayoutManager(App.instance()));
        albumAdapter.setHasStableIds(true);
        /**
         * adapter动画效果
         * ALPHAIN 渐显
         *SCALEIN 缩放
         *SLIDEIN_BOTTOM 从下到上
         *SLIDEIN_LEFT 从左到右
         *SLIDEIN_RIGHT 从右到左
         */
        albumAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);

        recyclerView.setAdapter(albumAdapter);

        //添加监听
        setListener();
    }

    @Override
    public void onDestroy() {
        if (callImageAlbum!=null){
            callImageAlbum.cancel();
        }
        super.onDestroy();
    }

    private void setNet() {
        apiService.getUserAlbumList()
                .compose(Rx2Schedulers.applyObservableAsync())
                .subscribe(new ObserverWrapper<List<ChooseImageAlbum>>(){
                    @Override
                    public void onSuccess(List<ChooseImageAlbum> albums) {
                        albumList = albums;
                        if (isFirst){
                            isFirst = false;
                            refreshLayout.setRefreshing(false);
                            baseAlbumList = albums;
                            setAdapter();
                        }
                        if (isRefresh){
                            setRefresh();
                        }
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        super.onFailure(code, errorMsg);
                        if (refreshLayout!=null){
                            if (isFirst){
                                isFirst = false;
                                refreshLayout.setRefreshing(false);
                            }
                            if (isRefresh){
                                isRefresh = false;
                                refreshLayout.setRefreshing(false);
                            }

                        }
                    }
                });
    }

    private void setRefresh() {
        isRefresh = false;
        refreshLayout.setRefreshing(false);
        albumAdapter.setNewData(albumList);
        ToastUtils.showShort(ChooseImageAlbumActivity.this,"刷新成功！");
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
                isRefresh = true;
                setNet();
            }
        });
        albumAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ChooseImageAlbum data = (ChooseImageAlbum) adapter.getData().get(position);
                Intent intent = new Intent();
                intent.putExtra("album_id",data.getId());
                intent.putExtra("albumName",data.getName());
                intent.putExtra("albumPoster",data.getPoster());
                setResult(ALBUM_CODE,intent);
                finish();
            }
        });
    }

    @Override
    protected void handler(Message msg) {

    }
}
