package com.riuir.calibur.ui.home;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.AnimeListForTagsSearch;

import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.params.DramaTags;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.home.Drama.DramaTagsSearchActivity;
import com.riuir.calibur.ui.home.Drama.adapter.DramaTagsAnimeListAdapter;
import com.riuir.calibur.ui.home.Drama.dramaConfig.choose.DramaMasterSettingChooseTagActivity;
import com.riuir.calibur.ui.widget.emptyView.AppListEmptyView;
import com.riuir.calibur.ui.widget.emptyView.AppListFailedView;
import com.riuir.calibur.utils.Constants;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import calibur.core.http.models.anime.AnimeShowInfo;
import calibur.core.http.observer.ObserverWrapper;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class DramaTagsFragment extends BaseFragment {


    @BindView(R.id.drama_tags_tags_grid)
    RecyclerView tagsGridFlowLayout;

    @BindView(R.id.drama_tags_tags_finish)
    Button finishBtn;

    @BindView(R.id.drama_tags_refresh_layout)
    SwipeRefreshLayout refreshLayout;
    //网络获取的tags总list
    List<AnimeShowInfo.AnimeShowInfoTags> tagsDataList = new ArrayList<>();

    boolean isTagsRefresh = false;


    DramaTagsGridAdapter dramaTagsGridAdapter;

    DramaTagsAnimeListAdapter dramaTagsAnimeListAdapter;


    //标记被选中标签的ID
    List<Integer> tagsIdList = new ArrayList<>();
    List<String> tagsNameList = new ArrayList<>();
    String tagsIDStr = "";
    String tagsNameStr = "";


    @Override
    protected int getContentViewID() {
        return R.layout.fragment_drama_tags;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {

        refreshLayout.setRefreshing(true);

        setNet();
        setOnClickListener();

    }

    private void setNet() {

        apiService.getCallDramaTags()
                .compose(Rx2Schedulers.applyObservableAsync())
                .subscribe(new ObserverWrapper<List<AnimeShowInfo.AnimeShowInfoTags>>(){
                    @Override
                    public void onSuccess(List<AnimeShowInfo.AnimeShowInfoTags> tags) {
                        if (refreshLayout!=null){
                            tagsDataList = tags;
                            Constants.allTagsList = tags;
                            setTagsGridAdapter();
                            refreshLayout.setRefreshing(false);
                        }
                    }
                    @Override
                    public void onFailure(int code, String errorMsg) {
                        super.onFailure(code, errorMsg);
                        if (refreshLayout!=null){
                            refreshLayout.setRefreshing(false);
                        }
                    }
                });

    }


    private void setTagsGridAdapter() {
        if (tagsGridFlowLayout == null)
            tagsGridFlowLayout = rootView.findViewById(R.id.drama_tags_tags_grid);

        dramaTagsGridAdapter = new DramaTagsGridAdapter(R.layout.drama_tags_grid_tags_item,tagsDataList);
        tagsGridFlowLayout.setLayoutManager(new GridLayoutManager(getContext(),4));
        tagsGridFlowLayout.setNestedScrollingEnabled(false);
        tagsGridFlowLayout.setAdapter(dramaTagsGridAdapter);
    }


    private void setOnClickListener() {

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTagsIdStr();
                if (tagsIDStr.length() == 0||tagsNameStr.length() == 0){
                    ToastUtils.showShort(getContext(),"请选择标签");
                }else {
                    //跳转
                    Intent intent = new Intent(getContext(), DramaTagsSearchActivity.class);
                    intent.putExtra("tagsIDStr",tagsIDStr);
                    intent.putExtra("tagsNameStr",tagsNameStr);
                    startActivity(intent);
                }

            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isTagsRefresh = true;
                setNet();
                if (tagsIdList!=null&&tagsIdList.size()!=0){
                    tagsIdList.clear();
                }
                if (tagsNameList!=null&&tagsNameList.size()!=0){
                    tagsNameList.clear();
                }
            }
        });

    }


    class DramaTagsGridAdapter extends BaseQuickAdapter<AnimeShowInfo.AnimeShowInfoTags,BaseViewHolder>{


        public DramaTagsGridAdapter(int layoutResId, @Nullable List<AnimeShowInfo.AnimeShowInfoTags> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, AnimeShowInfo.AnimeShowInfoTags item) {
            CheckBox tagText = helper.getView(R.id.drama_tags_grid_item_text) ;
            tagText.setText(item.getName());
            tagText.setTag(item.getId());
//            tagText.setEnabled(false);

            tagText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkedbtn = (CheckBox) v;
                    //判断是点击还是取消
//                    checkedbtn.toggle();
                    //做非空处理
                    if (tagsIdList == null) {
                        tagsIdList = new ArrayList<>();

                    }
                    if (tagsNameList == null){
                        tagsNameList = new ArrayList<>();
                    }

                    if (checkedbtn.isChecked()) {
                        //被点击
                        tagsIdList.add((Integer) checkedbtn.getTag());
                        tagsNameList.add(checkedbtn.getText().toString());

                    } else {
                        //取消
                        //遍历集合删除取消的标签ID
                        Iterator<Integer> tagsIdIterator = tagsIdList.iterator();
                        while (tagsIdIterator.hasNext()) {
                            Integer tagId = tagsIdIterator.next();
                            if (tagId == checkedbtn.getTag()) {
                                tagsIdIterator.remove();
                            }
                        }
                        //遍历集合删除取消的标签ID
                        Iterator<String> tagsNameIterator = tagsNameList.iterator();
                        while (tagsNameIterator.hasNext()) {
                            String tagName = tagsNameIterator.next();
                            if (tagName.equals(checkedbtn.getText().toString())) {
                                tagsNameIterator.remove();
                            }
                        }

                    }

                }
            });
        }

    }



    private void setTagsIdStr() {
        tagsIDStr = "";
        tagsNameStr = "";
        if (tagsIdList!=null&&tagsIdList.size()!=0){
            for (int position = 0; position <tagsIdList.size() ; position++) {
                int id = tagsIdList.get(position);
                if (position == 0){
                    if (tagsIDStr == null||tagsIDStr.length() == 0){
                        tagsIDStr = ""+id;
                    }else {
                        tagsIDStr = tagsIDStr+","+id;
                    }
                }else {
                    tagsIDStr = tagsIDStr+","+id;
                }
            }
        }
        if (tagsNameList!=null&&tagsNameList.size()!=0){
            for (int position = 0; position <tagsNameList.size() ; position++) {
                String name = tagsNameList.get(position);
                if (position == 0){
                    if (tagsNameStr == null||tagsNameStr.length() == 0){
                        tagsNameStr = name;
                    }else {
                        tagsNameStr = tagsNameStr+","+name;
                    }
                }else {
                    tagsNameStr = tagsNameStr+","+name;
                }
            }
        }

    }

}
