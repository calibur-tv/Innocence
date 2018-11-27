package com.riuir.calibur.ui.home.search;

import android.content.Intent;

import android.graphics.Color;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.DensityUtils;
import com.riuir.calibur.assistUtils.KeyBoardUtils;
import com.riuir.calibur.assistUtils.SharedPreferencesUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.assistUtils.activityUtils.BangumiAllListUtils;
import com.riuir.calibur.data.Event;

import com.riuir.calibur.data.anime.SearchAnimeInfo;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.Drama.DramaActivity;
import com.riuir.calibur.ui.home.Drama.DramaCardFragment;
import com.riuir.calibur.ui.home.Drama.DramaCartoonFragment;
import com.riuir.calibur.ui.home.Drama.DramaImageFragment;
import com.riuir.calibur.ui.home.Drama.DramaRoleFragment;
import com.riuir.calibur.ui.home.Drama.DramaScoreFragment;
import com.riuir.calibur.ui.home.Drama.DramaSeasonVideoFragment;
import com.riuir.calibur.ui.home.search.adapter.DramaSearchAdapter;
import com.riuir.calibur.ui.view.MyPagerSlidingTabStrip;
import com.riuir.calibur.ui.widget.emptyView.AppListEmptyView;
import com.riuir.calibur.ui.widget.emptyView.AppListFailedView;
import com.riuir.calibur.utils.Constants;
import com.riuir.calibur.utils.GlideUtils;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import calibur.core.http.models.anime.BangumiAllList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DramaSearchActivity extends BaseActivity {

    @BindView(R.id.drama_search_activity_edit_text)
    EditText searchEdit;
    @BindView(R.id.drama_search_activity_btn)
    ImageView searchBtn;
    @BindView(R.id.drama_search_activity_back)
    ImageView backBtn;

    @BindView(R.id.drama_search_activity_pager_tabs)
    MyPagerSlidingTabStrip tabStrip;
    @BindView(R.id.drama_search_activity_view_pager)
    ViewPager viewPager;
    @BindView(R.id.drama_search_activity_clear_edit)
    TextView editClear;

    SearchBangumiFragment searchBangumiFragment;
    SearchPostFragment searchPostFragment;
    SearchScoreFragment searchScoreFragment;
    SearchVideoFragment searchVideoFragment;
    SearchRoleFragment searchRoleFragment;
    SearchUserFragment searchUserFragment;

    private PopupWindow searchPopup;
    private RecyclerView popopRecycler;
    private String editContent;


    ArrayList<BangumiAllList> bangumiAllLists = new ArrayList<>();
    ArrayList<BangumiAllList> bangumiSearchedLists = new ArrayList<>();
    SearchPopupListAdapter popupListAdapter;

    private List<String> titles = new ArrayList<>();

    private DisplayMetrics dm;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_drama_search;
    }

    @Override
    protected void onInit() {
        dm = getResources().getDisplayMetrics();
        searchEdit.requestFocus();
        if (Constants.bangumiAllListData == null){
            Constants.bangumiAllListData = SharedPreferencesUtils.getBangumiAllListList(App.instance(),"bangumiAllListData");
        }
        if (Constants.bangumiAllListData == null){
            BangumiAllListUtils.setBangumiAllList(this,apiGet);
        }

        bangumiAllLists = Constants.bangumiAllListData;
        initPopupWindow();
        setEditListener();
        setViewPager();
    }

    @Override
    protected void onPause() {
        if (searchPopup!=null){
            searchPopup.dismiss();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        searchEdit.requestFocus();
    }

    private void initPopupWindow() {
        View view = getLayoutInflater().inflate(R.layout.drama_search_popup_layout,null);
        searchPopup = new PopupWindow(view, DensityUtils.dp2px(DramaSearchActivity.this,300),
                400,false);
        popopRecycler = view.findViewById(R.id.drama_search_popup_list_view);
        popopRecycler.setLayoutManager(new LinearLayoutManager(DramaSearchActivity.this));
        popupListAdapter = new SearchPopupListAdapter(R.layout.drama_search_popup_list_item,bangumiSearchedLists);
        popopRecycler.setAdapter(popupListAdapter);
        searchPopup.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_search_drama_popup_window));
        searchPopup.setOutsideTouchable(true);
        searchPopup.setFocusable(false);

        popupListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                BangumiAllList data = (BangumiAllList) adapter.getItem(position);
                if (data!=null){
                    editContent = data.getName();
                    searchEdit.setText(data.getName());

                    setStartSearch();
                }
                searchPopup.dismiss();
            }
        });

        TextView emptyView = new TextView(DramaSearchActivity.this);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = DensityUtils.dp2px(DramaSearchActivity.this,60);
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setText("没有搜索内容...");
        emptyView.setTextColor(getResources().getColor(R.color.color_FF9B9B9B));
        emptyView.setLayoutParams(params);
        popupListAdapter.setEmptyView(emptyView);
    }

    private void setEditListener() {
        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    KeyBoardUtils.closeKeybord(searchEdit,DramaSearchActivity.this);
                    editContent = searchEdit.getText().toString();
                    if (TextUtils.isEmpty(editContent)){
                        ToastUtils.showShort(DramaSearchActivity.this,"搜索内容为空，请输入搜索内容哦");
                        return true;
                    }
                    setStartSearch();
                    return true;
                }
                return false;
            }
        });
        searchEdit.addTextChangedListener(textWatcher);
        editClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEdit.setText("");
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editContent = searchEdit.getText().toString();
                if (TextUtils.isEmpty(editContent)){
                    ToastUtils.showShort(DramaSearchActivity.this,"搜索内容为空，请输入搜索内容哦");
                }else {
                    KeyBoardUtils.closeKeybord(searchEdit,DramaSearchActivity.this);
                    setStartSearch();
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setStartSearch(){
        if (searchBangumiStartListener!=null){
            searchBangumiStartListener.startSearch(editContent);
        }
        if (searchPostStartListener!=null){
            searchPostStartListener.startSearch(editContent);
        }
        if (searchScoreStartListener!=null){
            searchScoreStartListener.startSearch(editContent);
        }
        if (searchVideoStartListener!=null){
            searchVideoStartListener.startSearch(editContent);
        }
        if (searchRoleStartListener!=null){
            searchRoleStartListener.startSearch(editContent);
        }
        if (searchUserStartListener!=null){
            searchUserStartListener.startSearch(editContent);
        }
    }

    private void setViewPager() {

        titles.add("番剧");
        titles.add("视频");
        titles.add("帖子");
        titles.add("漫评");
        titles.add("偶像");
        titles.add("用户");
//

        viewPager.setAdapter(new SearchActivityPagerAdapter(getSupportFragmentManager()));
        tabStrip.setViewPager(viewPager);
        viewPager.setOffscreenPageLimit(6);
        setSearchTabs();
    }
    private void setSearchTabs() {
        // 设置Tab是自动填充满屏幕的
        tabStrip.setShouldExpand(false);
        // 设置Tab的分割线是透明的
        tabStrip.setDividerColor(Color.TRANSPARENT);
        tabStrip.setBackgroundResource(R.color.color_FFFFFFFF);
        //设置underLine
        tabStrip.setUnderlineHeight(0);
        tabStrip.setUnderlineColorResource(R.color.color_FFFFFFFF);
        //设置Tab Indicator的高度
        tabStrip.setIndicatorColorResource(R.color.theme_magic_sakura_primary);
        // 设置Tab Indicator的高度
        tabStrip.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, dm));
        // 设置Tab标题文字的大小
        tabStrip.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, dm));
        //设置textcolor
        tabStrip.setTextColorResource(R.color.color_FF5B5B5B);
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        tabStrip.setSelectedTextColorResource(R.color.theme_magic_sakura_primary);
        //设置滚动条圆角（这是我自定义的一个方法，同时修改了滚动条长度，使其与文字等宽）
        tabStrip.setRoundRadius(2.5f);

        // 取消点击Tab时的背景色
        tabStrip.setTabBackground(0);
    }


    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if (charSequence.length()!=0){
                editClear.setVisibility(View.VISIBLE);
                if (searchEdit!=null){
                    searchPopup.showAsDropDown(searchEdit,0,0);
                }
            }else {
                editClear.setVisibility(View.GONE);
                searchPopup.dismiss();
            }
            setPopupWindowChanged(charSequence);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void setPopupWindowChanged(CharSequence charSequence){
        bangumiSearchedLists.clear();
        String editStr =  charSequence.toString();
        editStr = editStr.toLowerCase();

        if (bangumiAllLists!=null&&bangumiAllLists.size()!=0){
            for (BangumiAllList data:bangumiAllLists) {
                String dataStr = data.getName().toLowerCase();
                if (dataStr.contains(editStr)){
                    bangumiSearchedLists.add(data);
                }
            }
        }
        popupListAdapter.notifyDataSetChanged();
    }


    @Override
    protected void handler(Message msg) {

    }

    class SearchPopupListAdapter extends BaseQuickAdapter<BangumiAllList,BaseViewHolder>{

        public SearchPopupListAdapter(int layoutResId, @Nullable List<BangumiAllList> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, BangumiAllList item) {
            helper.setText(R.id.drama_search_popup_list_item_name,item.getName());
            ImageView avatar = helper.getView(R.id.drama_search_popup_list_item_avatar);
            GlideUtils.loadImageView(DramaSearchActivity.this,
                    GlideUtils.setImageUrl(DramaSearchActivity.this,item.getAvatar(),GlideUtils.THIRD_SCREEN),
                    avatar);
        }
    }


    public class SearchActivityPagerAdapter extends FragmentPagerAdapter {

        public SearchActivityPagerAdapter(FragmentManager fm) {
            super(fm);
        }



        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        @Override
        public int getCount() {
            return titles.size();
        }

        @Override
        public Fragment getItem(int position) {
            switch (titles.get(position)) {
                case "番剧":
                    if (searchBangumiFragment == null) {
                        searchBangumiFragment = new SearchBangumiFragment();
                    }
                    return searchBangumiFragment;
                case "视频":
                    if (searchVideoFragment == null) {
                        searchVideoFragment = new SearchVideoFragment();
                    }
                    return searchVideoFragment;
                case "帖子":
                    if (searchPostFragment == null) {
                        searchPostFragment = new SearchPostFragment();
                    }
                    return searchPostFragment;
                case "漫评":
                    if (searchScoreFragment == null) {
                        searchScoreFragment = new SearchScoreFragment();
                    }
                    return searchScoreFragment;
                case "偶像":
                    if (searchRoleFragment == null) {
                        searchRoleFragment = new SearchRoleFragment();
                    }
                    return searchRoleFragment;
                case "用户":
                    if (searchUserFragment == null) {
                        searchUserFragment = new SearchUserFragment();
                    }
                    return searchUserFragment;
                default:
                    return null;
            }
        }

    }

    private SearchStartListener searchBangumiStartListener;
    private SearchStartListener searchPostStartListener;
    private SearchStartListener searchScoreStartListener;
    private SearchStartListener searchVideoStartListener;
    private SearchStartListener searchRoleStartListener;
    private SearchStartListener searchUserStartListener;

    public void setSearchUserStartListener(SearchStartListener searchUserStartListener) {
        this.searchUserStartListener = searchUserStartListener;
    }

    public void setSearchRoleStartListener(SearchStartListener searchRoleStartListener) {
        this.searchRoleStartListener = searchRoleStartListener;
    }

    public void setSearchVideoStartListener(SearchStartListener searchVideoStartListener) {
        this.searchVideoStartListener = searchVideoStartListener;
    }

    public void setSearchScoreStartListener(SearchStartListener searchScoreStartListener) {
        this.searchScoreStartListener = searchScoreStartListener;
    }

    public void setSearchBangumiStartListener(SearchStartListener searchBangumiStartListener) {
        this.searchBangumiStartListener = searchBangumiStartListener;
    }

    public void setSearchPostStartListener(SearchStartListener searchStartListener) {
        this.searchPostStartListener = searchStartListener;
    }

    public interface SearchStartListener{
        void startSearch(String searchContent);
    }
}
