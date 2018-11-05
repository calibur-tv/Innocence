package com.riuir.calibur.ui.home.choose;

import android.content.Intent;
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
import com.riuir.calibur.data.AnimeShowInfo;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.params.DramaTags;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.Drama.dramaConfig.choose.DramaMasterSettingChooseTagActivity;
import com.riuir.calibur.utils.Constants;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CardChooseTagsActivity extends BaseActivity {

    @BindView(R.id.drama_master_setting_choose_tags_back)
    ImageView backBtn;
    @BindView(R.id.drama_master_setting_choose_tags_refresh)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.drama_master_setting_choose_tags_grid)
    RecyclerView tagsGridView;
    @BindView(R.id.drama_master_setting_choose_tags_finish)
    Button finishBtn;

    private List<AnimeShowInfo.AnimeShowInfoTags> baseChoosedTagList;
    private List<AnimeShowInfo.AnimeShowInfoTags> choosedTagList = new ArrayList<>();

    private List<AnimeShowInfo.AnimeShowInfoTags> allTagList;
    private ArrayList<Integer> choosedTagsIds = new ArrayList<>();
    Call<DramaTags> tagsCall;

    TagsGridAdapter tagsGridAdapter;

    public static final int TAG_POST= 121;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_drama_master_setting_choose_tag;
    }

    @Override
    protected void onInit() {
        Intent intent = getIntent();
        choosedTagsIds = intent.getIntegerArrayListExtra("tagsIds");
        setListener();
        setNet();
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
                    //选择完成
                    ArrayList<String> tagsNameList = new ArrayList<>();
                    ArrayList<Integer> tagsIdsList = new ArrayList<>();
                    for (int i = 0; i < choosedTagList.size(); i++) {
                        tagsIdsList.add(i,choosedTagList.get(i).getId());
                        tagsNameList.add(i,choosedTagList.get(i).getName());
                    }
                    Intent intentRet = new Intent();
                    intentRet.putIntegerArrayListExtra("tagsIdsList",tagsIdsList);
                    intentRet.putStringArrayListExtra("tagsNameList",tagsNameList);
                    setResult(TAG_POST,intentRet);
                    finish();
                }else {
                    ToastUtils.showShort(CardChooseTagsActivity.this,"您还没有选择标签！");
                }

            }
        });
    }

    private void setNet() {

        tagsCall = apiGet.getCallPostTags();
        tagsCall.enqueue(new Callback<DramaTags>() {
            @Override
            public void onResponse(Call<DramaTags> call, Response<DramaTags> response) {
                if (response!=null&&response.isSuccessful()){
                    allTagList = response.body().getData();
                    LogUtils.d("dramaTags","tagsDataList = "+allTagList);
                    setTagsGridAdapter();

                }else if (!response.isSuccessful()){
                    String errorStr = "";
                    try {
                        errorStr = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    Event<String> info =gson.fromJson(errorStr,Event.class);
                    ToastUtils.showShort(CardChooseTagsActivity.this,info.getMessage());
                }else {
                    ToastUtils.showShort(CardChooseTagsActivity.this,"未知原因导致加载失败了！");

                }
            }

            @Override
            public void onFailure(Call<DramaTags> call, Throwable t) {
                ToastUtils.showShort(CardChooseTagsActivity.this,"请检查您的网络哟~");
                LogUtils.v("AppNetErrorMessage","drama tag t = "+t.getMessage());
                CrashReport.postCatchedException(t);
            }
        });
    }

    private void setTagsGridAdapter() {
        choosedTagList.clear();
        tagsGridAdapter = new TagsGridAdapter(R.layout.drama_tags_grid_tags_item,allTagList);
        tagsGridView.setLayoutManager(new GridLayoutManager(CardChooseTagsActivity.this,4));
        tagsGridView.setNestedScrollingEnabled(false);
        tagsGridView.setAdapter(tagsGridAdapter);
    }

    @Override
    protected void handler(Message msg) {

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
            if (choosedTagsIds!=null&&choosedTagsIds.size()!=0){
                for (int i = 0; i < choosedTagsIds.size(); i++) {
                    if (choosedTagsIds.get(i)==item.getId()){
                        choosedTagList.add(item);
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
}
