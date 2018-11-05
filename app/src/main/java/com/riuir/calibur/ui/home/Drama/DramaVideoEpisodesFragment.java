package com.riuir.calibur.ui.home.Drama;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.data.anime.AnimeShowVideosInfo;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.home.Drama.adapter.DramaVideoEpisodesListAdapter;
import com.riuir.calibur.ui.widget.emptyView.AppListEmptyView;

import java.util.List;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class DramaVideoEpisodesFragment extends BaseFragment {

    @BindView(R.id.drama_video_episodes_list_view)
    RecyclerView videoEpisodesListView;

    DramaVideoEpisodesListAdapter adapter;

    List<AnimeShowVideosInfo.AnimeShowVideosInfoDataEpisodes> data;

    AppListEmptyView emptyView;

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_drama_video_episodes;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        setAdapter();
    }

    public void setData(List<AnimeShowVideosInfo.AnimeShowVideosInfoVideos> dataList,int position){
        data = dataList.get(position).getData();
    }

    private void setAdapter() {
        adapter = new DramaVideoEpisodesListAdapter(R.layout.drama_video_episodes_list_item,data,getContext());
        if (videoEpisodesListView == null){
            videoEpisodesListView = getView().findViewById(R.id.drama_video_episodes_list_view);
        }
        videoEpisodesListView.setLayoutManager(new LinearLayoutManager(App.instance()));
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
        videoEpisodesListView.setAdapter(adapter);
        setEmptyView();
        //添加监听
        setListener();
    }

    private void setEmptyView(){
        if (data==null||data.size()==0){
            emptyView = new AppListEmptyView(getContext());
            emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            adapter.setEmptyView(emptyView);
        }
    }

    private void setListener() {
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                AnimeShowVideosInfo.AnimeShowVideosInfoDataEpisodes dataEpisodes = (AnimeShowVideosInfo.AnimeShowVideosInfoDataEpisodes) adapter.getData().get(position);
                Intent intent = new Intent(getContext(),DramaVideoPlayActivity.class);
                intent.putExtra("videoId",dataEpisodes.getId());
                startActivity(intent);
            }
        });
    }
}
