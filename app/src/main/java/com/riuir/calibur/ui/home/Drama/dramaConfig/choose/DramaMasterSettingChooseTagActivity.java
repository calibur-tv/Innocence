package com.riuir.calibur.ui.home.Drama.dramaConfig.choose;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ToastUtils;

import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.params.DramaTags;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.Drama.dramaConfig.DramaMasterAnimeSettingActivity;
import com.riuir.calibur.ui.home.DramaTagsFragment;
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

public class DramaMasterSettingChooseTagActivity extends BaseActivity {

    @BindView(R.id.drama_master_setting_choose_tags_back)
    ImageView backBtn;
    @BindView(R.id.drama_master_setting_choose_tags_refresh)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.drama_master_setting_choose_tags_grid)
    RecyclerView tagsGridView;
    @BindView(R.id.drama_master_setting_choose_tags_finish)
    Button finishBtn;

    DramaMasterAnimeSettingActivity animeSettingActivity;
    private List<AnimeShowInfo.AnimeShowInfoTags> baseChoosedTagList;
    private List<AnimeShowInfo.AnimeShowInfoTags> choosedTagList;
    private List<AnimeShowInfo.AnimeShowInfoTags> allTagList;

    TagsGridAdapter tagsGridAdapter;

    Call<DramaTags> tagsCall;


    private static DramaMasterSettingChooseTagActivity instance;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_drama_master_setting_choose_tag;
    }

    @Override
    protected void onInit() {
        animeSettingActivity = DramaMasterAnimeSettingActivity.getInstance();
        instance = this;

        setChooseTags();
        setAllTags();
        setListener();
    }

    private void setListener() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        refreshLayout.setEnabled(false);
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (choosedTagList!=null&&choosedTagList.size()!=0){
                    if (getChoosedTagList!=null){
                        getChoosedTagList.chooseDTagList(choosedTagList);
                    }
                    finish();
                }else {
                    ToastUtils.showShort(DramaMasterSettingChooseTagActivity.this,"您还没有选择标签！");
                }

            }
        });
    }

    private void setAllTags() {
        if (Constants.allTagsList!=null&&Constants.allTagsList.size()!=0){
            allTagList = Constants.allTagsList;
            setTagsGridAdapter();
        }else {
            setNet();
        }
    }

    private void setNet() {

        apiService.getCallDramaTags()
                .compose(Rx2Schedulers.applyObservableAsync())
                .subscribe(new ObserverWrapper<List<AnimeShowInfo.AnimeShowInfoTags>>(){
                    @Override
                    public void onSuccess(List<AnimeShowInfo.AnimeShowInfoTags> tags) {
                        if (tagsGridView!=null){
                            allTagList = tags;
                            Constants.allTagsList = tags;
                            setTagsGridAdapter();
                        }
                    }
                    @Override
                    public void onFailure(int code, String errorMsg) {
                        super.onFailure(code, errorMsg);
                    }
                });

    }

    private void setTagsGridAdapter() {

        tagsGridAdapter = new TagsGridAdapter(R.layout.drama_tags_grid_tags_item,allTagList);
        tagsGridView.setLayoutManager(new GridLayoutManager(DramaMasterSettingChooseTagActivity.this,4));
        tagsGridView.setNestedScrollingEnabled(false);
        tagsGridView.setAdapter(tagsGridAdapter);
    }

    private void setChooseTags() {
        animeSettingActivity.setGetChoosedTagList(new DramaMasterAnimeSettingActivity.GetChoosedTagList() {
            @Override
            public void chooseDTagList(List<AnimeShowInfo.AnimeShowInfoTags> tags) {
                baseChoosedTagList = tags;
                choosedTagList = baseChoosedTagList;
            }
        });
    }

    class TagsGridAdapter extends BaseQuickAdapter<AnimeShowInfo.AnimeShowInfoTags,BaseViewHolder> {


        public TagsGridAdapter(int layoutResId, @Nullable List<AnimeShowInfo.AnimeShowInfoTags> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, final AnimeShowInfo.AnimeShowInfoTags item) {
            CheckBox tagText = helper.getView(R.id.drama_tags_grid_item_text) ;
            tagText.setText(item.getName());
            tagText.setTag(item.getId());
//            tagText.setEnabled(false);
            if (choosedTagList!=null&&choosedTagList.size()!=0){
                for (int i = 0; i < choosedTagList.size(); i++) {
                    if (choosedTagList.get(i).getId()==item.getId()){
                        tagText.setChecked(true);
                    }
                }
            }

            tagText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkedbtn = (CheckBox) v;
                    //判断是点击还是取消
//                    checkedbtn.toggle();
                    //做非空处理
                    if (choosedTagList == null) {
                        choosedTagList = new ArrayList<>();
                    }

                    if (checkedbtn.isChecked()) {
                        //被点击
                        choosedTagList.add(item);
                    } else {
                        //取消
                        //遍历集合删除取消的标签ID
                        Iterator<AnimeShowInfo.AnimeShowInfoTags> tagsIdIterator = choosedTagList.iterator();
                        while (tagsIdIterator.hasNext()) {
                            Integer tagId = tagsIdIterator.next().getId();
                            if (tagId == checkedbtn.getTag()) {
                                tagsIdIterator.remove();
                            }
                        }
                    }

                }
            });
        }
    }

    @Override
    protected void handler(Message msg) {

    }

    public static DramaMasterSettingChooseTagActivity getInstance() {
        return instance;
    }

    private GetChoosedTagsList getChoosedTagList;

    public void setGetChoosedTagList(GetChoosedTagsList getChoosedTagList) {
        this.getChoosedTagList = getChoosedTagList;

    }

    public interface GetChoosedTagsList{
        void chooseDTagList(List<AnimeShowInfo.AnimeShowInfoTags> tags);
    }
}
