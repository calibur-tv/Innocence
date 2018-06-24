package com.riuir.calibur.ui.home.Drama;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.AnimeNewListForWeek;
import com.riuir.calibur.data.AnimeShowVideosInfo;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.utils.GlideUtils;

import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class DramaVideoFragment extends BaseFragment {

    @BindView(R.id.drama_video_episodes_list_view)
    ExpandableListView episodesListView;

    private int animeID;

    List<AnimeShowVideosInfo.AnimeShowVideosInfoVideos> animeShowVideosInfoVideos;
    AnimeShowVideosInfo.AnimeShowVideosInfoDataEpisodes episodesInfo;

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_drama_video;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        DramaActivity dramaActivity = (DramaActivity) getActivity();
//        animeID = dramaActivity.getAnimeID();
        animeID = 1;
        setNet();
    }

    private void setNet() {
        apiGet.getCallAnimeShowVideos(animeID).enqueue(new Callback<AnimeShowVideosInfo>() {
            @Override
            public void onResponse(Call<AnimeShowVideosInfo> call, Response<AnimeShowVideosInfo> response) {
                if (response!=null&&response.body()!=null&&response.body().getCode()==0){
                    animeShowVideosInfoVideos = response.body().getData().getVideos();
                }else {
                    ToastUtils.showShort(getContext(),"未知错误出现了QAQ");
                }
            }

            @Override
            public void onFailure(Call<AnimeShowVideosInfo> call, Throwable t) {

            }
        });
    }

    class DramaVideoListAdapter extends BaseExpandableListAdapter{

        @Override
        public int getGroupCount() {
            return animeShowVideosInfoVideos.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return animeShowVideosInfoVideos.get(groupPosition).getData().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return animeShowVideosInfoVideos.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return animeShowVideosInfoVideos.get(groupPosition).getData().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            return null;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            return null;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
