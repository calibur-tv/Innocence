package com.riuir.calibur.ui.home.Drama.dramaInfo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.makeramen.roundedimageview.RoundedImageView;
import com.riuir.calibur.R;

import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.Drama.DramaActivity;
import com.riuir.calibur.ui.home.Drama.DramaTagsSearchActivity;
import com.riuir.calibur.ui.home.Drama.dramaConfig.DramaMasterAnimeSettingActivity;
import com.riuir.calibur.ui.home.Drama.dramaConfig.DramaMasterConfigActivity;
import com.riuir.calibur.ui.home.DramaTagsFragment;
import com.riuir.calibur.ui.home.user.UserMainActivity;
import com.riuir.calibur.ui.web.WebViewActivity;
import com.riuir.calibur.ui.widget.popup.AppHeaderPopupWindows;
import com.riuir.calibur.utils.GlideUtils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import calibur.core.http.models.anime.AnimeShowInfo;

public class DramaInfoActivity extends BaseActivity {

    @BindView(R.id.drama_info_back_btn)
    ImageView backBtn;
    @BindView(R.id.drama_info_banner)
    ImageView bannerImg;
    @BindView(R.id.drama_info_avatar)
    ImageView avatarImg;
    @BindView(R.id.drama_info_name)
    TextView nameText;
    @BindView(R.id.drama_info_power_title)
    TextView powerTitle;
    @BindView(R.id.drama_info_power_count)
    TextView powerText;
    @BindView(R.id.drama_info_alias)
    TextView aliasText;
    @BindView(R.id.drama_info_summary)
    TextView summaryText;
    @BindView(R.id.drama_info_master_list)
    RecyclerView masterList;
    @BindView(R.id.drama_info_tag_list)
    RecyclerView tagsList;
    @BindView(R.id.drama_info_master_setting_btn)
    Button masterSettingBtn;
    @BindView(R.id.drama_info_activity_more)
    AppHeaderPopupWindows headerMore;

    DramaTagsAdapter dramaTagsAdapter;
    DramaMasterAdapter dramaMasterAdapter;

    AnimeShowInfo animeShowInfoData;

    String tagsIDStr;
    String tagsNameStr;


    AnimeSettingFinishReceiver finishReceiver;
    IntentFilter intentFilter;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_drama_info;
    }

    @Override
    protected void onInit() {
        Intent intent = getIntent();
        animeShowInfoData = (AnimeShowInfo) intent.getSerializableExtra("animeShowInfoData");
        if (animeShowInfoData!=null){
            setView();
            setListener();
        }

        registerFinishReceiver();
    }

    private void registerFinishReceiver() {
        finishReceiver = new AnimeSettingFinishReceiver();
        intentFilter = new IntentFilter(DramaMasterAnimeSettingActivity.EDIT_BANGUMI_ACTION);
        registerReceiver(finishReceiver,intentFilter);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(finishReceiver);
        super.onDestroy();
    }

    private void setView() {
        nameText.setText(animeShowInfoData.getName());
//        if (animeShowInfoData.getScore()==0.0){
//            scoreAllTitle.setVisibility(View.GONE);
//            scoreAllText.setVisibility(View.GONE);
//        }else {
//            NumberFormat nbf=NumberFormat.getInstance();
//            nbf.setMinimumFractionDigits(2);
//            String c = nbf.format(animeShowInfoData.getScore()/10);
//            scoreAllTitle.setVisibility(View.VISIBLE);
//            scoreAllText.setVisibility(View.VISIBLE);
//            scoreAllText.setText(c);
//        }
        powerText.setText(animeShowInfoData.getPower()+"");

        aliasText.setText("别名: "+animeShowInfoData.getAlias());
        summaryText.setText(animeShowInfoData.getSummary());

        GlideUtils.loadImageView(this,
                GlideUtils.setImageUrl(this,animeShowInfoData.getAvatar(),GlideUtils.THIRD_SCREEN),
                avatarImg);
        GlideUtils.loadImageViewBlur(this,
                GlideUtils.setImageUrl(this,animeShowInfoData.getBanner(),GlideUtils.FULL_SCREEN),
                bannerImg);

        if (animeShowInfoData.isIs_master()){
            masterSettingBtn.setVisibility(View.VISIBLE);
        }else {
            masterSettingBtn.setVisibility(View.GONE);
        }


        headerMore.setReportModelTag(AppHeaderPopupWindows.BANGUMI,animeShowInfoData.getId());
        headerMore.setShareLayout(animeShowInfoData.getName(),AppHeaderPopupWindows.BANGUMI,animeShowInfoData.getId(),"");

        headerMore.setMasterLayout(animeShowInfoData.isIs_master(),5,animeShowInfoData.getId(),animeShowInfoData);

        setAdapter();

    }

    private void setAdapter() {
        dramaTagsAdapter = new DramaTagsAdapter(R.layout.drama_info_tag_list_item,animeShowInfoData.getTags());
        tagsList.setLayoutManager(new GridLayoutManager(this,4));
        tagsList.setAdapter(dramaTagsAdapter);
        List<AnimeShowInfo.AnimeInfoManagersUserList> managerLists = animeShowInfoData.getManager_users().getList();

        AnimeShowInfo.AnimeInfoManagersUserList add = new AnimeShowInfo.AnimeInfoManagersUserList();
        add.setCreated_at("add");
        add.setIs_leader(true);
        add.setUser(null);
        managerLists.add(add);

        dramaMasterAdapter = new DramaMasterAdapter(R.layout.drama_info_master_list_item,managerLists);
        masterList.setLayoutManager(new GridLayoutManager(this,4));
        masterList.setAdapter(dramaMasterAdapter);
    }

    private void setListener() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        masterSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DramaInfoActivity.this, DramaMasterConfigActivity.class);
                intent.putExtra("bangumi_id",animeShowInfoData.getId());
                intent.putExtra("animeShowInfoData",animeShowInfoData);
                startActivity(intent);
            }
        });
        dramaTagsAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()){
                    case R.id.drama_info_tags_list_item_tag:
                        AnimeShowInfo.AnimeShowInfoTags tags = (AnimeShowInfo.AnimeShowInfoTags) adapter.getItem(position);
                        tagsIDStr = tags.getId()+"";
                        tagsNameStr = tags.getName();
                        Intent intent = new Intent(DramaInfoActivity.this, DramaTagsSearchActivity.class);
                        intent.putExtra("tagsIDStr",tagsIDStr);
                        intent.putExtra("tagsNameStr",tagsNameStr);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });

        dramaMasterAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()){
                    case R.id.drama_info_master_list_item_add_layout:
                        Intent intentWeb = new Intent(DramaInfoActivity.this, WebViewActivity.class);
                        intentWeb.putExtra("type",WebViewActivity.TYPE_RULE);
                        intentWeb.putExtra("index",5);
                        startActivity(intentWeb);
                        break;
                    case R.id.drama_info_master_list_item_avatar:
                        AnimeShowInfo.AnimeInfoManagersUserList managersUserList = (AnimeShowInfo.AnimeInfoManagersUserList) adapter.getItem(position);
                        Intent intent = new Intent(DramaInfoActivity.this, UserMainActivity.class);
                        intent.putExtra("userId",managersUserList.getUser().getId());
                        intent.putExtra("zone",managersUserList.getUser().getZone());
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void handler(Message msg) {

    }

    class DramaTagsAdapter extends BaseQuickAdapter<AnimeShowInfo.AnimeShowInfoTags,BaseViewHolder>{

        public DramaTagsAdapter(int layoutResId, @Nullable List<AnimeShowInfo.AnimeShowInfoTags> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, AnimeShowInfo.AnimeShowInfoTags item) {
            helper.setText(R.id.drama_info_tags_list_item_tag,item.getName());
            helper.addOnClickListener(R.id.drama_info_tags_list_item_tag);
        }
    }

    class DramaMasterAdapter extends BaseQuickAdapter<AnimeShowInfo.AnimeInfoManagersUserList,BaseViewHolder>{

        public DramaMasterAdapter(int layoutResId, @Nullable List<AnimeShowInfo.AnimeInfoManagersUserList> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, AnimeShowInfo.AnimeInfoManagersUserList item) {
            RelativeLayout addLayout = helper.getView(R.id.drama_info_master_list_item_add_layout);
            RoundedImageView avatarImg = helper.getView(R.id.drama_info_master_list_item_avatar);
            if (item.getCreated_at().equals("add")){
                addLayout.setVisibility(View.VISIBLE);
                avatarImg.setVisibility(View.GONE);
                helper.addOnClickListener(R.id.drama_info_master_list_item_add_layout);
                helper.setText(R.id.drama_info_master_list_item_name,"申请吧主");
            }else {
                addLayout.setVisibility(View.GONE);
                avatarImg.setVisibility(View.VISIBLE);
                helper.setText(R.id.drama_info_master_list_item_name,item.getUser().getNickname());
                GlideUtils.loadImageView(DramaInfoActivity.this,
                        GlideUtils.setImageUrl(DramaInfoActivity.this,item.getUser().getAvatar(),GlideUtils.THIRD_SCREEN),
                        avatarImg);
                helper.addOnClickListener(R.id.drama_info_master_list_item_avatar);
                if (item.isIs_leader()){
                    avatarImg.setBorderWidth(2f);
                    avatarImg.setBorderColor(getResources().getColor(R.color.gold));
                }else {
                    avatarImg.setBorderWidth(0f);
                }
            }

        }
    }

    public class AnimeSettingFinishReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }
}
