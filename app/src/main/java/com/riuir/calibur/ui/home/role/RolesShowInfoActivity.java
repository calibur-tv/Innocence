package com.riuir.calibur.ui.home.role;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.assistUtils.activityUtils.UserMainUtils;

import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.Drama.DramaActivity;
import com.riuir.calibur.ui.home.MineFragment;
import com.riuir.calibur.ui.home.adapter.MyLoadMoreView;
import com.riuir.calibur.ui.home.role.adapter.RoleFansListAdapter;
import com.riuir.calibur.ui.widget.BangumiForShowView;
import com.riuir.calibur.ui.widget.emptyView.AppListEmptyView;
import com.riuir.calibur.ui.widget.emptyView.AppListFailedView;
import com.riuir.calibur.ui.widget.popup.AppHeaderPopupWindows;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.GlideUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import calibur.core.http.models.anime.RoleFansListInfo;
import calibur.core.http.models.anime.RoleShowInfo;
import calibur.core.http.observer.ObserverWrapper;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;

public class RolesShowInfoActivity extends BaseActivity {


    private LinearLayout headerLayout;
    ImageView headerRoleIcon;
    ImageView headerLoverIcon;
    Button headerStarBtn;
    TextView headerRoleName;
    TextView headerLoverName;
    TextView headerRoleIntro;
    TextView headerRoleOtherName;
    TextView headerRoleFans;
    TextView headerMineHasStar;


    private int hasstar = 0;
    private int fansCount = 0;
    private int starCount = 0;

    BangumiForShowView bangumi;

    int roleId;

    String seenIds = "";
    List<Integer> seenIdList = new ArrayList<>();

    @BindView(R.id.role_show_info_refresh_layout)
    SwipeRefreshLayout roleShowInfoRefreshLayout;
    @BindView(R.id.role_show_info_list_view)
    RecyclerView roleShowInfoListView;
    @BindView(R.id.role_show_info_back_btn)
    ImageView backBtn;
    @BindView(R.id.role_show_info_header_more)
    AppHeaderPopupWindows headerMore;

    private RoleShowInfo primacyData;
    private RoleFansListInfo fansData;
    private List<RoleFansListInfo.RoleFansListInfoList> fansList;
    private List<RoleFansListInfo.RoleFansListInfoList> baseFansList = new ArrayList<>();


    private static final int NET_STATUS_PRIMACY = 0;
    private static final int NET_STATUS_USER_LIST = 1;
    private static final int NET_STATUS_STAR = 2;

    private RoleFansListAdapter fansListAdapter;

    private boolean isFirstLoad = false;
    boolean isLoadMore = false;
    boolean isRefresh = false;



    AppListFailedView failedView;
    AppListEmptyView emptyView;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_roles_show_info;
    }

    @Override
    protected void onInit() {
        Intent intent = getIntent();
        roleId = intent.getIntExtra("roleId",0);
        LogUtils.d("roleShowInfo","roleId = "+roleId);
        setAdapter();
        isFirstLoad = true;
        setBackBtn();
        setNet(NET_STATUS_PRIMACY);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setNet(int NET_STATUS) {

        if (NET_STATUS == NET_STATUS_PRIMACY){
            apiService.getCallRoleShowPrimacy(roleId)
                    .compose(Rx2Schedulers.applyObservableAsync())
                    .subscribe(new ObserverWrapper<RoleShowInfo>(){
                        @Override
                        public void onSuccess(RoleShowInfo roleShowInfo) {
                            primacyData = roleShowInfo;
                            setNet(NET_STATUS_USER_LIST);
                        }

                        @Override
                        public void onFailure(int code, String errorMsg) {
                            super.onFailure(code, errorMsg);
                            if (roleShowInfoRefreshLayout!=null){
                                setFailedView();
                            }
                        }
                    });
        }

        if (NET_STATUS == NET_STATUS_USER_LIST){
            setSeendIdS();
            apiService.getCallRolesFansList(roleId,"hot",seenIds)
                    .compose(Rx2Schedulers.applyObservableAsync())
                    .subscribe(new ObserverWrapper<RoleFansListInfo>(){
                        @Override
                        public void onSuccess(RoleFansListInfo roleFansListInfo) {
                            fansList = roleFansListInfo.getList();
                            fansData = roleFansListInfo;
                            if (isFirstLoad){
                                baseFansList = roleFansListInfo.getList();
                                if (roleShowInfoListView!=null){
                                    setAdapter();
                                    setPrimacyView();
                                    setEmptyView();
                                }
                            }
                            if (isLoadMore){
                                setLoadMore();
                            }
                            if (isRefresh){
                                setRefresh();
                            }
                            for (RoleFansListInfo.RoleFansListInfoList hotItem :fansList){
                                seenIdList.add(hotItem.getId());
                            }
                        }

                        @Override
                        public void onFailure(int code, String errorMsg) {
                            super.onFailure(code, errorMsg);
                            if (roleShowInfoRefreshLayout!=null){
                                if (isLoadMore){
                                    fansListAdapter.loadMoreFail();
                                    isLoadMore = false;
                                }
                                if (isRefresh){
                                    roleShowInfoRefreshLayout.setRefreshing(false);
                                    isRefresh = false;
                                }
                                setFailedView();
                            }
                        }
                    });
        }

        if (NET_STATUS == NET_STATUS_STAR){
            apiService.getCallRolesStar(roleId)
                    .compose(Rx2Schedulers.applyObservableAsync())
                    .subscribe(new ObserverWrapper<String>(){
                        @Override
                        public void onSuccess(String isStar) {
                            ToastUtils.showLong(RolesShowInfoActivity.this,"应援成功，消耗1团子,点击可继续应援！");
                            setStarFinish();
                            headerStarBtn.setText("为TA应援");
                            headerStarBtn.setClickable(true);
                        }

                        @Override
                        public void onFailure(int code, String errorMsg) {
                            super.onFailure(code, errorMsg);
                            if (headerStarBtn!=null){
                                headerStarBtn.setText("为TA应援");
                                headerStarBtn.setClickable(true);
                            }
                        }
                    });
        }

    }

    private void setStarFinish() {
        if (hasstar == 0){
            fansCount++;
        }
        hasstar++;
        starCount++;
        headerRoleFans.setText("粉丝：共有"+fansCount
                +"名粉丝，收获了"+starCount+"枚团子");
        headerMineHasStar.setText("我的应援次数："+hasstar);

        if (Constants.userInfoData.getBanlance().getCoin_count()>0){
            Constants.userInfoData.getBanlance().setCoin_count(Constants.userInfoData.getBanlance().getCoin_count()-1);
        }else if (Constants.userInfoData.getBanlance().getLight_count()>0){
            Constants.userInfoData.getBanlance().setLight_count(Constants.userInfoData.getBanlance().getLight_count()-1);
        }
        Intent intent = new Intent(MineFragment.COINCHANGE);
        sendBroadcast(intent);

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

        LogUtils.d("image_1","seenIds = "+seenIds );
    }

    private void setAdapter() {
        fansListAdapter = new RoleFansListAdapter(R.layout.role_fans_list_item,baseFansList,RolesShowInfoActivity.this);
        if (roleShowInfoListView == null){
            roleShowInfoListView = findViewById(R.id.role_show_info_list_view);
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(App.instance());
        roleShowInfoListView.setLayoutManager(layoutManager);
        fansListAdapter.setHasStableIds(true);
        /**
         * adapter动画效果
         * ALPHAIN 渐显
         *SCALEIN 缩放
         *SLIDEIN_BOTTOM 从下到上
         *SLIDEIN_LEFT 从左到右
         *SLIDEIN_RIGHT 从右到左
         */
        fansListAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        //开启上拉加载更多
        fansListAdapter.setEnableLoadMore(true);


        //添加底部footer
        fansListAdapter.setLoadMoreView(new MyLoadMoreView());
        fansListAdapter.disableLoadMoreIfNotFullPage(roleShowInfoListView);


        roleShowInfoListView.setAdapter(fansListAdapter);
        fansListAdapter.setHeaderAndEmpty(true);

        isFirstLoad = false;
    }

    private void setPrimacyView() {
        headerLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.role_show_info_header_view,null);
        headerRoleIcon = headerLayout.findViewById(R.id.role_show_info_header_role_icon);
        headerRoleName = headerLayout.findViewById(R.id.role_show_info_header_role_name);
        headerLoverIcon = headerLayout.findViewById(R.id.role_show_info_header_role_lover_icon);
        headerLoverName = headerLayout.findViewById(R.id.role_show_info_header_role_lover_name);
        headerStarBtn = headerLayout.findViewById(R.id.role_show_info_header_role_star_btn);
        headerRoleIntro = headerLayout.findViewById(R.id.role_show_info_header_role_intro);
        headerRoleOtherName = headerLayout.findViewById(R.id.role_show_info_header_role_other_name);
        headerRoleFans = headerLayout.findViewById(R.id.role_show_info_header_role_fans);
        headerMineHasStar = headerLayout.findViewById(R.id.role_show_info_header_role_mine_has_star);

        headerMore.setReportModelTag(AppHeaderPopupWindows.ROLE,primacyData.getData().getId());
//        headerMore.setShareLayout(primacyData.getData().getName(),AppHeaderPopupWindows.ROLE,primacyData.getData().getId(),"");
        headerMore.setShareLayout(RolesShowInfoActivity.this,primacyData.getShare_data(),AppHeaderPopupWindows.ROLE);

        bangumi = headerLayout.findViewById(R.id.role_show_info_header_bangmui);
        bangumi.setName(primacyData.getBangumi().getName());
        bangumi.setSummary(primacyData.getBangumi().getSummary());
        bangumi.setImageView(RolesShowInfoActivity.this,primacyData.getBangumi().getAvatar());

        GlideUtils.loadImageView(RolesShowInfoActivity.this,
                GlideUtils.setImageUrlForWidth(RolesShowInfoActivity.this,
                        primacyData.getData().getAvatar(),
                        headerRoleIcon.getLayoutParams().width),
                headerRoleIcon);
        if (primacyData.getData().getLover()!=null&&primacyData.getData().getLover().getAvatar()!=null){
            GlideUtils.loadImageViewCircle(RolesShowInfoActivity.this,
                    GlideUtils.setImageUrlForWidth(RolesShowInfoActivity.this,
                            primacyData.getData().getLover().getAvatar(),
                            headerLoverIcon.getLayoutParams().width),headerLoverIcon);
            headerLoverName.setText(primacyData.getData().getLover().getNickname());
        }
        headerRoleName.setText(primacyData.getData().getName());

        headerRoleIntro.setText("简介："+primacyData.getData().getIntro());
        headerRoleOtherName.setText(primacyData.getData().getAlias());
        headerRoleOtherName.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        hasstar = primacyData.getData().getHasStar();
        fansCount = primacyData.getData().getFans_count();
        starCount = primacyData.getData().getStar_count();

        headerRoleFans.setText("粉丝：共有"+primacyData.getData().getFans_count()
                +"名粉丝，收获了"+primacyData.getData().getStar_count()+"枚团子");
        headerMineHasStar.setText("我的应援次数："+primacyData.getData().getHasStar());

        fansListAdapter.addHeaderView(headerLayout);

        //添加监听
        setListener();
    }

    private void setEmptyView(){
        if (baseFansList==null||baseFansList.size()==0){
            if (emptyView == null){
                emptyView = new AppListEmptyView(RolesShowInfoActivity.this);
                emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
            fansListAdapter.setEmptyView(emptyView);


        }
    }

    private void setFailedView(){
        //加载失败 点击重试
        if (failedView == null){
            failedView = new AppListFailedView(RolesShowInfoActivity.this);
            failedView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        fansListAdapter.setEmptyView(failedView);
    }

    private void setLoadMore() {
        isLoadMore = false;

        if (fansData.isNoMore()) {
            fansListAdapter.addData(fansList);
            //数据全部加载完毕
            fansListAdapter.loadMoreEnd();
        } else {
            //成功获取更多数据
            fansListAdapter.addData(fansList);
            fansListAdapter.loadMoreComplete();
        }
    }

    private void setRefresh() {
        isRefresh = false;
        fansListAdapter.setNewData(fansList);
        if (roleShowInfoRefreshLayout!=null){
            roleShowInfoRefreshLayout.setRefreshing(false);
        }
        ToastUtils.showShort(RolesShowInfoActivity.this,"刷新成功！");
    }

    private void setBackBtn(){
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setListener() {



        fansListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                RoleFansListInfo.RoleFansListInfoList roleFansInfo = (RoleFansListInfo.RoleFansListInfoList) adapter.getData().get(position);
                UserMainUtils.toUserMainActivity(RolesShowInfoActivity.this,roleFansInfo.getId(),roleFansInfo.getZone());
            }
        });
        headerLoverIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RoleShowInfo.RoleShowInfoDataLover lover = primacyData.getData().getLover();
                UserMainUtils.toUserMainActivity(RolesShowInfoActivity.this,lover.getId(),lover.getZone());
            }
        });

        headerStarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                headerStarBtn.setClickable(false);
                headerStarBtn.setText("应援中...");
                setNet(NET_STATUS_STAR);
            }
        });

        bangumi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RolesShowInfoActivity.this, DramaActivity.class);
                intent.putExtra("animeId",primacyData.getBangumi().getId());
                startActivity(intent);

            }
        });

        //上拉加载监听
        fansListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override public void onLoadMoreRequested() {
                isLoadMore = true;
                setNet(NET_STATUS_USER_LIST);
            }
        }, roleShowInfoListView);

        //下拉刷新监听
        roleShowInfoRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                setNet(NET_STATUS_USER_LIST);
            }
        });

    }

    @Override
    protected void handler(Message msg) {

    }


}
