package com.riuir.calibur.ui.home;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.print.PrinterId;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bm.library.PhotoView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.MainCardInfo;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.home.card.CardPreviewPictureActivity;
import com.riuir.calibur.ui.home.card.CardShowInfoActivity;
import com.riuir.calibur.utils.GlideUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainCardHotFragment extends BaseFragment {

    String seenIds = "";
    List<Integer> seenIdList = new ArrayList<>();

    @BindView(R.id.main_card_hot_list_view)
    RecyclerView mainCardHotListView;

    @BindView(R.id.main_card_hot_refresh_layout)
    SwipeRefreshLayout mainCardHotRefreshLayout;
    //用来动态改变RecyclerView的变量
    private List<MainCardInfo.MainCardInfoList> listHot;
    //传给Adapter的值 首次加载后不可更改 不然会导致数据出错
    private List<MainCardInfo.MainCardInfoList> baseListHot;

    private MainCardInfo.MainCardInfoData mainCardInfoData;

    private MainCardHotAdapter adapter;

    //帖子总条目数
    int TOTAL_COUNTER = 0;
    //list里面拥有的
    int listDataCounter = 0;
    boolean isLoadMore = false;
    boolean isRefresh = false;
    boolean isFristLoad = true;

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_main_card_hot;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        setNet();
    }

    private void setNet() {

        setSeendIdS();
        apiGet.getCallMainCardHotGet(seenIds).enqueue(new Callback<MainCardInfo>() {
            @Override
            public void onResponse(Call<MainCardInfo> call, Response<MainCardInfo> response) {

                if (response == null||response.body()==null||response.body().getData() == null||response.body().getData().getList().size() == 0){
                    ToastUtils.showShort(getContext(),"网络异常，请稍后再试");
                    if (isLoadMore){
                        adapter.loadMoreFail();
                        isLoadMore = false;
                    }
                    if (isRefresh){
                        mainCardHotRefreshLayout.setRefreshing(false);
                        isRefresh = false;
                    }
                }else {
                    listHot = response.body().getData().getList();
                    mainCardInfoData = response.body().getData();
                    if (isFristLoad){
                        baseListHot = response.body().getData().getList();
                        setListAdapter();
                    }
                    if (isLoadMore){
                        setLoadMore();
                    }
                    if (isRefresh){
                        setRefresh();
                    }

                    for (MainCardInfo.MainCardInfoList hotItem :listHot){
                        seenIdList.add(hotItem.getId());
                    }
                }


            }

            @Override
            public void onFailure(Call<MainCardInfo> call, Throwable t) {

                    LogUtils.d("cardHot","t = "+t);
                if (isLoadMore){
                    adapter.loadMoreFail();
                    isLoadMore = false;
                    ToastUtils.showShort(getContext(),"网络异常，请稍后再试");
                }
                if (isRefresh){
                    mainCardHotRefreshLayout.setRefreshing(false);
                    isRefresh = false;
                    ToastUtils.showShort(getContext(),"网络异常，请稍后再试");
                }
            }
        });
    }

    private void setSeendIdS() {
        if (seenIdList!=null&&seenIdList.size()!=0){
            for (int position = 0; position <seenIdList.size() ; position++) {
                int id = seenIdList.get(position);
                if (position == 0){
                    if (seenIds == null||seenIds.length() == 0){
                        seenIds = seenIds+id;
                    }else {
                        seenIds = seenIds+","+id;
                    }
                }else {
                    seenIds = seenIds+","+id;
                }
            }
        }
        if (isRefresh){
            seenIds = "";
            seenIdList.clear();
        }

        LogUtils.d("CardHot","seenIds = "+seenIds );
    }

    private void setLoadMore() {
        isLoadMore = false;


        if (mainCardInfoData.isNoMore()) {
            adapter.addData(listHot);
            //数据全部加载完毕
           adapter.loadMoreEnd();
        } else {
            //成功获取更多数据
            adapter.addData(listHot);
            adapter.loadMoreComplete();

        }
    }

    private void setRefresh() {
        isRefresh = false;
        adapter.setNewData(listHot);
        mainCardHotRefreshLayout.setRefreshing(false);
    }

    private void setListAdapter() {


        adapter = new MainCardHotAdapter(R.layout.main_card_list_item,baseListHot);
        mainCardHotListView.setLayoutManager(new LinearLayoutManager(App.instance()));
        adapter.setHasStableIds(true);
        /**
         * adapter动画效果
         * ALPHAIN 渐显
         *SCALEIN 缩放
         *SLIDEIN_BOTTOM 从下到上
         *SLIDEIN_LEFT 从左到右
         *SLIDEIN_RIGHT 从右到左
         */
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        //开启上拉加载更多
        adapter.setEnableLoadMore(true);


        //添加底部footer
        adapter.setLoadMoreView(new CardLoadMoreView());
        adapter.disableLoadMoreIfNotFullPage(mainCardHotListView);

        mainCardHotListView.setAdapter(adapter);

        //添加监听
        setListener();
        isFristLoad = false;
    }

    private void setListener() {

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //item被点击，跳转页面
                Intent intent = new Intent(getActivity(), CardShowInfoActivity.class);
                MainCardInfo.MainCardInfoList cardInfo = (MainCardInfo.MainCardInfoList) adapter.getData().get(position);
                int cardID = cardInfo.getId();
                intent.putExtra("cardID",cardID);
                startActivity(intent);
            }
        });

        //上拉加载监听
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override public void onLoadMoreRequested() {
                isLoadMore = true;
                setNet();
            }
        }, mainCardHotListView);

        //下拉刷新监听
        mainCardHotRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                setNet();
            }
        });
    }

    class MainCardHotAdapter extends BaseQuickAdapter<MainCardInfo.MainCardInfoList,BaseViewHolder> {
        public MainCardHotAdapter(int layoutResId, @Nullable List<MainCardInfo.MainCardInfoList> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, MainCardInfo.MainCardInfoList item) {
            helper.setText(R.id.main_card_list_item_anime_name, item.getBangumi().getName());
            helper.setText(R.id.main_card_list_item_user_name, item.getUser().getNickname());
            helper.setText(R.id.main_card_list_item_card_title, item.getTitle());
            helper.setText(R.id.main_card_list_item_card_desc, item.getDesc());
            helper.setText(R.id.main_card_list_item_last_comment_time, item.getUpdated_at());

            helper.setText(R.id.main_card_list_item_upvote_count, ""+item.getView_count());
            helper.setText(R.id.main_card_list_item_last_comment_count, ""+item.getComment_count());
            //为点赞按钮添加点击事件
            helper.addOnClickListener(R.id.main_card_list_item_upvote_icon);


            GlideUtils.loadImageView(getContext(), item.getBangumi().getAvatar(), (ImageView) helper.getView(R.id.main_card_list_item_anime_cover));

            ImageView bigOne, little1, little2, little3;
            LinearLayout littleGroup;
            bigOne = helper.getView(R.id.main_card_list_item_big_image);
            littleGroup = helper.getView(R.id.main_card_list_item_little_image_group);
            little1 = helper.getView(R.id.main_card_list_item_little_image_1);
            little2 = helper.getView(R.id.main_card_list_item_little_image_2);
            little3 = helper.getView(R.id.main_card_list_item_little_image_3);

            //可以通过helper.getLayoutPosition() 获取当前item的position

            if (item.getImages() == null || item.getImages().size() == 0) {
                littleGroup.setVisibility(View.GONE);
                bigOne.setVisibility(View.GONE);
            } else if (item.getImages().size() == 1) {
                littleGroup.setVisibility(View.GONE);
                bigOne.setVisibility(View.VISIBLE);
                GlideUtils.loadImageView(getContext(), item.getImages().get(0).getUrl(), bigOne);
            } else {
                littleGroup.setVisibility(View.VISIBLE);
                bigOne.setVisibility(View.GONE);
                little1.setVisibility(View.VISIBLE);
                little2.setVisibility(View.VISIBLE);
                GlideUtils.loadImageView(getContext(), item.getImages().get(0).getUrl(), little1);
                GlideUtils.loadImageView(getContext(), item.getImages().get(1).getUrl(), little2);
                if (item.getImages().size() == 2) {
                    little3.setVisibility(View.INVISIBLE);
                } else {
                    little3.setVisibility(View.VISIBLE);
                    GlideUtils.loadImageView(getContext(), item.getImages().get(2).getUrl(), little3);
                }


            }


        }

    }

    public  class CardLoadMoreView extends LoadMoreView {

        @Override public int getLayoutId() {
            return R.layout.brvah_quick_view_load_more;
        }

        /**
         * 如果返回true，数据全部加载完毕后会隐藏加载更多
         * 如果返回false，数据全部加载完毕后会显示getLoadEndViewId()布局
         */
        @Override public boolean isLoadEndGone() {
            return false;
        }

        @Override protected int getLoadingViewId() {
            return R.id.load_more_loading_view;
        }

        @Override protected int getLoadFailViewId() {
            return R.id.load_more_load_fail_view;
        }

        /**
         * isLoadEndGone()为true，可以返回0
         * isLoadEndGone()为false，不能返回0
         */
        @Override protected int getLoadEndViewId() {
            return R.id.load_more_load_end_view;
        }
    }

}
