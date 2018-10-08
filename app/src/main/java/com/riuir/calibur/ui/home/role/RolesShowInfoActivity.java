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
import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.assistUtils.activityUtils.UserMainUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.MainTrendingInfo;
import com.riuir.calibur.data.role.RoleFansListInfo;
import com.riuir.calibur.data.role.RoleShowInfo;
import com.riuir.calibur.data.trending.TrendingToggleInfo;
import com.riuir.calibur.net.ApiGet;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.Drama.DramaActivity;
import com.riuir.calibur.ui.home.MineFragment;
import com.riuir.calibur.ui.home.adapter.MyLoadMoreView;
import com.riuir.calibur.ui.home.image.ImageShowInfoActivity;
import com.riuir.calibur.ui.home.role.adapter.RoleFansListAdapter;
import com.riuir.calibur.ui.home.user.UserMainActivity;
import com.riuir.calibur.assistUtils.activityUtils.LoginUtils;
import com.riuir.calibur.ui.widget.BangumiForShowView;
import com.riuir.calibur.ui.widget.emptyView.AppListEmptyView;
import com.riuir.calibur.ui.widget.emptyView.AppListFailedView;
import com.riuir.calibur.ui.widget.popup.AppHeaderPopupWindows;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.GlideUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    private RoleShowInfo.RoleShowInfoData primacyData;
    private RoleFansListInfo.RoleFansListInfoData fansData;
    private List<RoleFansListInfo.RoleFansListInfoList> fansList;
    private List<RoleFansListInfo.RoleFansListInfoList> baseFansList = new ArrayList<>();


    private static final int NET_STATUS_PRIMACY = 0;
    private static final int NET_STATUS_USER_LIST = 1;
    private static final int NET_STATUS_STAR = 2;

    private RoleFansListAdapter fansListAdapter;

    private boolean isFirstLoad = false;
    boolean isLoadMore = false;
    boolean isRefresh = false;

    private Call<RoleShowInfo> primacyCall;
    private Call<RoleFansListInfo> fansListInfoCall;

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
        if (primacyCall!=null){
            primacyCall.cancel();
        }
        if (fansListInfoCall!=null){
            fansListInfoCall.cancel();
        }
        super.onDestroy();
    }

    private void setNet(int NET_STATUS) {

        ApiGet mApiGet;
        if (Constants.ISLOGIN){
            mApiGet = apiGetHasAuth;
        }else {
            mApiGet = apiGet;
        }

        if (NET_STATUS == NET_STATUS_PRIMACY){
            primacyCall = mApiGet.getCallRoleShowPrimacy(roleId);
            primacyCall.enqueue(new Callback<RoleShowInfo>() {
                @Override
                public void onResponse(Call<RoleShowInfo> call, Response<RoleShowInfo> response) {
                    if (response!=null&&response.isSuccessful()){
                        primacyData = response.body().getData();
                        setNet(NET_STATUS_USER_LIST);

                    }else if (response!=null&&response.isSuccessful()==false){
                        String errorStr = "";
                        try {
                            errorStr = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Gson gson = new Gson();
                        Event<String> info =gson.fromJson(errorStr,Event.class);
                        if (info.getCode() == 40104){
                            //用户登录状态过时的时候，清空UserToken,将登录置为false
                            ToastUtils.showShort(RolesShowInfoActivity.this,info.getMessage());
                            LoginUtils.ReLogin(RolesShowInfoActivity.this);
                        }else if (info.getCode() == 40401){
                            ToastUtils.showShort(RolesShowInfoActivity.this,info.getMessage());
                        }
                        setFailedView();
                    }else {
                        ToastUtils.showShort(RolesShowInfoActivity.this,"未知错误出现了！");
                        setFailedView();
                    }
                }
                @Override
                public void onFailure(Call<RoleShowInfo> call, Throwable t) {
                    if (call.isCanceled()){
                    }else {
                        ToastUtils.showShort(RolesShowInfoActivity.this,"请检查您的网络哟~");
                        setFailedView();
                    }
                }
            });
        }

        if (NET_STATUS == NET_STATUS_USER_LIST){
            setSeendIdS();
            fansListInfoCall = mApiGet.getCallRolesFansList(roleId,"hot",seenIds);
            fansListInfoCall.enqueue(new Callback<RoleFansListInfo>() {
                @Override
                public void onResponse(Call<RoleFansListInfo> call, Response<RoleFansListInfo> response) {
                    if (response!=null&&response.isSuccessful()){
                        fansList = response.body().getData().getList();
                        fansData = response.body().getData();
                        if (isFirstLoad){
                            baseFansList = response.body().getData().getList();
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
                    }else if (response!=null&&response.isSuccessful()==false){
                        String errorStr = "";
                        try {
                            errorStr = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Gson gson = new Gson();
                        Event<String> info =gson.fromJson(errorStr,Event.class);
                        if (info.getCode() == 40104){
                            //用户登录状态过时的时候，清空UserToken,将登录置为false
                            ToastUtils.showShort(RolesShowInfoActivity.this,info.getMessage());
                            LoginUtils.ReLogin(RolesShowInfoActivity.this);
                        }else if (info.getCode() == 40401){
                            ToastUtils.showShort(RolesShowInfoActivity.this,info.getMessage());
                        }
                        if (isLoadMore){
                            fansListAdapter.loadMoreFail();
                            isLoadMore = false;
                        }
                        if (isRefresh){
                            roleShowInfoRefreshLayout.setRefreshing(false);
                            isRefresh = false;
                        }
                        setFailedView();
                    }else {
                        ToastUtils.showShort(RolesShowInfoActivity.this,"未知错误出现了！");
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

                @Override
                public void onFailure(Call<RoleFansListInfo> call, Throwable t) {
                    if (call.isCanceled()){
                    }else {
                        ToastUtils.showShort(RolesShowInfoActivity.this,"请检查您的网络哟~");
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
            apiPost.getCallRolesStar(roleId).enqueue(new Callback<TrendingToggleInfo>() {
                @Override
                public void onResponse(Call<TrendingToggleInfo> call, Response<TrendingToggleInfo> response) {

                    if (response!=null&&response.isSuccessful()){
                        ToastUtils.showLong(RolesShowInfoActivity.this,"应援成功，消耗1金币,点击可继续应援！");
                        setStarFinish();
                    }else if (!response.isSuccessful()){

                        String errorStr = "";
                        try {
                            errorStr = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Gson gson = new Gson();
                        Event<String> info =gson.fromJson(errorStr,Event.class);
                        if (info.getCode() == 40401){
                            ToastUtils.showShort(RolesShowInfoActivity.this,"不存在的角色！");
                        }
                        if (info.getCode() == 40301){
                            ToastUtils.showShort(RolesShowInfoActivity.this,"没有足够的金币！");
                        }
                    }else {
                        ToastUtils.showShort(RolesShowInfoActivity.this,"未知原因导致应援失败了！");

                    }
                    headerStarBtn.setText("为TA应援");
                    headerStarBtn.setClickable(true);
                }

                @Override
                public void onFailure(Call<TrendingToggleInfo> call, Throwable t) {
                    ToastUtils.showShort(RolesShowInfoActivity.this,"请检查您的网络再重试哟~");
                    headerStarBtn.setText("为TA应援");
                    headerStarBtn.setClickable(true);
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
                +"名粉丝，收获了"+starCount+"枚金币");
        headerMineHasStar.setText("我的应援次数："+hasstar);
        LogUtils.d("testCoin","coin role 1 = "+Constants.userInfoData.getCoin());

        Constants.userInfoData.setCoin(Constants.userInfoData.getCoin()-1);
        Intent intent = new Intent(MineFragment.COINCHANGE);
        sendBroadcast(intent);
        LogUtils.d("testCoin","coin role 2 = "+Constants.userInfoData.getCoin());

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
                +"名粉丝，收获了"+primacyData.getData().getStar_count()+"枚金币");
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
