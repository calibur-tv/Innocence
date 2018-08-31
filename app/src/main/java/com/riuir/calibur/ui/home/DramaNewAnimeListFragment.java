package com.riuir.calibur.ui.home;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.AnimeNewListForWeek;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.home.Drama.DramaActivity;
import com.riuir.calibur.ui.view.MyPagerSlidingTabStrip;
import com.riuir.calibur.ui.view.NewAnimePagerSlidingTabStrip;
import com.riuir.calibur.utils.GlideUtils;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class DramaNewAnimeListFragment extends BaseFragment {

    @BindView(R.id.drama_new_anime_list_tab)
    NewAnimePagerSlidingTabStrip pagerSlidingTabStrip;

    @BindView(R.id.drama_new_anime_list_view_pager)
    ViewPager newAnimeViewPager;
    @BindView(R.id.drama_new_anime_list_refresh_layout)
    SwipeRefreshLayout refreshLayout;

    AnimeNewListForWeek animeNewListForWeek;

    NewAnimeListViewPagerAdapter newAnimeListViewPagerAdapter;


    /**
     * 获取当前屏幕的密度
     */
    private DisplayMetrics dm;

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_drama_new_anime_list;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        dm = getResources().getDisplayMetrics();
        refreshLayout.setRefreshing(true);
        setNet();
        setListener();
    }

    private void setListener() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setNet();
            }
        });
    }

    private void setViewPagerAdapter() {
        newAnimeListViewPagerAdapter = new NewAnimeListViewPagerAdapter();

        if (newAnimeViewPager == null){
            newAnimeViewPager = getView().findViewById(R.id.drama_new_anime_list_view_pager);
        }
        newAnimeViewPager.setAdapter(newAnimeListViewPagerAdapter);
        pagerSlidingTabStrip.setViewPager(newAnimeViewPager);
        setDramaTabs();

    }


    private void setDramaTabs() {
        // 设置Tab是自动填充满屏幕的
        pagerSlidingTabStrip.setShouldExpand(false);
        // 设置Tab的分割线是透明的
        pagerSlidingTabStrip.setDividerColor(Color.TRANSPARENT);
        pagerSlidingTabStrip.setBackgroundResource(R.color.color_00FFFFFF);
        //滚动条的间隔
//        pagerSlidingTabStrip.setTabPaddingLeftRight(8);
        //设置underLine
        pagerSlidingTabStrip.setUnderlineColorResource(R.color.color_FFFFFFFF);
        pagerSlidingTabStrip.setUnderlineHeight(0);
        //设置Tab Indicator的颜色
        pagerSlidingTabStrip.setIndicatorColorResource(R.color.theme_magic_sakura_primary);
        // 设置Tab Indicator的高度
        pagerSlidingTabStrip.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, dm));
        // 设置Tab标题文字的大小
        pagerSlidingTabStrip.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, dm));
        //设置textclolo
        pagerSlidingTabStrip.setTextColorResource(R.color.color_FF9B9B9B);
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        pagerSlidingTabStrip.setSelectedTextColorResource(R.color.theme_magic_sakura_primary);
        //设置滚动条圆角（这是我自定义的一个方法，同时修改了滚动条长度，使其与文字等宽）
        pagerSlidingTabStrip.setRoundRadius(2.5f);
        // 取消点击Tab时的背景色
        pagerSlidingTabStrip.setTabBackground(0);
    }

    private void setNet() {
        apiGet.getCallDramaNewForWeek().enqueue(new Callback<AnimeNewListForWeek>() {
            @Override
            public void onResponse(Call<AnimeNewListForWeek> call, Response<AnimeNewListForWeek> response) {
                if (response!=null&&response.isSuccessful()){

                    animeNewListForWeek = response.body();
                    setViewPagerAdapter();

                }else if (!response.isSuccessful()){
                    String errorStr = "";
                    try {
                        errorStr = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    Event<String> info =gson.fromJson(errorStr,Event.class);

                    ToastUtils.showShort(getContext(),info.getMessage());

                }else {
                    ToastUtils.showShort(getContext(),"未知原因导致加载失败了！");

                }

                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<AnimeNewListForWeek> call, Throwable t) {
                ToastUtils.showShort(getContext(),"请检查您的网络！");
                refreshLayout.setRefreshing(false);
            }
        });
    }

    class NewAnimeListViewPagerAdapter extends PagerAdapter{

        private final String[] titles = { "最新","一","二", "三","四","五","六","日" };
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {

            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = getLayoutInflater().inflate(R.layout.drama_new_anime_view_pager_item,null);
            RecyclerView newAnimeList = view.findViewById(R.id.drama_new_anime_view_pager_item_recycler);
            DramaNewAnimeListAdapter newAnimeListAdapter = new DramaNewAnimeListAdapter(R.layout.drama_new_week_pager_item_list,animeNewListForWeek.getData().get(position));
            newAnimeListAdapter.setHasStableIds(true);
            /**
             * adapter动画效果
             * ALPHAIN 渐显
             *SCALEIN 缩放
             *SLIDEIN_BOTTOM 从下到上
             *SLIDEIN_LEFT 从左到右
             *SLIDEIN_RIGHT 从右到左
             */
            newAnimeListAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
            newAnimeList.setLayoutManager(new GridLayoutManager(App.instance(),3));
            newAnimeList.setAdapter(newAnimeListAdapter);

            newAnimeListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int positionItem) {
                    int animeId = animeNewListForWeek.getData().get(position).get(positionItem).getId();
                    Intent intent = new Intent(getActivity(),DramaActivity.class);
                    intent.putExtra("animeId",animeId);
                    startActivity(intent);
                }
            });

            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            object = null;
        }
    }

    class DramaNewAnimeListAdapter extends BaseQuickAdapter<AnimeNewListForWeek.AnimeNewListForWeekData,BaseViewHolder> {


        public DramaNewAnimeListAdapter(int layoutResId, @Nullable List<AnimeNewListForWeek.AnimeNewListForWeekData>  data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, AnimeNewListForWeek.AnimeNewListForWeekData item) {
            helper.setText(R.id.drama_new_week_pager_item_list_name,item.getName());
            helper.setText(R.id.drama_new_week_pager_item_list_part,"更新至第"+item.getReleased_part()+"集");

            GlideUtils.loadImageViewRoundedCorners(getContext(),item.getAvatar(), (ImageView) helper.getView(R.id.drama_new_week_pager_item_list_image),10);

        }
    }
}
