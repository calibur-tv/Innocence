package com.riuir.calibur.ui.home;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.example.library.AutoFlowLayout;
import com.example.library.FlowAdapter;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.AnimeListForTagsSearch;
import com.riuir.calibur.data.params.DramaTags;
import com.riuir.calibur.ui.common.BaseFragment;
import com.riuir.calibur.ui.home.Drama.DramaActivity;
import com.riuir.calibur.ui.home.adapter.MyLoadMoreView;
import com.riuir.calibur.utils.GlideUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class DramaTagsFragment extends BaseFragment {

    @BindView(R.id.drama_tags_search_edit_text)
    EditText searchEdit;
    @BindView(R.id.drama_tags_search_btn)
    ImageView searchBtn;
    @BindView(R.id.drama_tags_refresh_tags_text)
    TextView refreshTagsBtn;
    @BindView(R.id.drama_tags_tags_grid)
    AutoFlowLayout tagsGridFlowLayout;
    @BindView(R.id.drama_tags_anime_list)
    RecyclerView tagsAnimeListView;
    //网络获取的tags总list
    List<DramaTags.DramaTagsData> tagsDataList;
    //换一批添加的tags list
    List<DramaTags.DramaTagsData> someTagsList;

    AnimeListForTagsSearch animeListForTagsSearch;
    //用来动态改变RecyclerView的变量
    List<AnimeListForTagsSearch.AnimeListForTagsSearchData> animeListForTagsSearchesData;
    //传给Adapter的值 首次加载后不可更改
    List<AnimeListForTagsSearch.AnimeListForTagsSearchData> baseAnimeListForTagsSearchesData;

    public static final int NET_GET_TAGS = 0;
    public static final int NET_SEARCH_ANIME_FOR_TAGS= 1;
    public static final int NET_SEARCH_ANIME_FOR_EDIT = 2;

    boolean isLoadMore = false;

    int minTagsId = 0;
    int totalTagsId = 0;

    int page = 1;

    DramaTagsGridAdapter dramaTagsGridAdapter;

    DramaTagsAnimeListAdapter dramaTagsAnimeListAdapter;


    //标记被选中标签的ID
    List<Integer> tagsIdList = new ArrayList<>();
    String tagsIDStr = "";

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_drama_tags;
    }

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {

        setNet(NET_GET_TAGS);
        initFlowLayoutGrid();

        setOnClickListener();

    }

    private void setNet(int NET_STAUTS) {
        if (NET_STAUTS == NET_GET_TAGS){
            apiGet.getCallDramaTags().enqueue(new Callback<DramaTags>() {
                @Override
                public void onResponse(Call<DramaTags> call, Response<DramaTags> response) {
                    if (response!=null&&response.body()!=null){
                        if (response.body().getCode() == 0){
                            tagsDataList = response.body().getData();
                            LogUtils.d("dramaTags","tagsDataList = "+tagsDataList);
                            setTagsGridAdapter();
                        }else {
                            ToastUtils.showShort(getContext(),"获取番剧标签失败QAQ");
                        }
                    }else {
                        ToastUtils.showShort(getContext(),"获取番剧标签失败QAQ");
                    }
                }

                @Override
                public void onFailure(Call<DramaTags> call, Throwable t) {
                    ToastUtils.showShort(getContext(),"请检查您的网络哟~");
                }
            });
        }

        if (NET_STAUTS == NET_SEARCH_ANIME_FOR_TAGS){
            setTagsIdStr();
            LogUtils.d("tagSearch","id = "+tagsIDStr+",page = "+page);
            apiGet.getCallSearchDramaForTags(tagsIDStr,page+"").enqueue(new Callback<AnimeListForTagsSearch>() {
                @Override
                public void onResponse(Call<AnimeListForTagsSearch> call, Response<AnimeListForTagsSearch> response) {
                    if (response!=null&&response.body()!=null){
                        if (response.body().getCode() == 0){
                            animeListForTagsSearch = response.body();
                            animeListForTagsSearchesData = response.body().getData().getList();
                            setTagsAnimeAdapter();
                        }else {
                            ToastUtils.showShort(getContext(),"根据标签搜索番剧失败了");
                            if (isLoadMore){
                                isLoadMore =false;
                                dramaTagsAnimeListAdapter.loadMoreComplete();
                            }
                        }
                    }else {
                        ToastUtils.showShort(getContext(),"根据标签搜索番剧失败了");
                        if (isLoadMore){
                            isLoadMore =false;
                            dramaTagsAnimeListAdapter.loadMoreComplete();
                        }
                    }
                }

                @Override
                public void onFailure(Call<AnimeListForTagsSearch> call, Throwable t) {
                    ToastUtils.showShort(getContext(),"请检查您的网络哟~123");
                    LogUtils.d("tagSearch","t = "+t.getMessage());
                    if (isLoadMore){
                        isLoadMore =false;
                        dramaTagsAnimeListAdapter.loadMoreComplete();
                    }
                }
            });
        }

    }

    private void initFlowLayoutGrid() {
        //不单行  多行
        tagsGridFlowLayout.setSingleLine(false);
        tagsGridFlowLayout.setMaxLines(4);
        //支持多选
        tagsGridFlowLayout.setMultiChecked(true);
        tagsGridFlowLayout.setHorizontalSpace(15);
        tagsGridFlowLayout.setVerticalSpace(20);
    }

    private void setTagsGridAdapter() {
        if (someTagsList == null){
            someTagsList = new ArrayList<>();
        }
        if (someTagsList.size()!=0){
            someTagsList.clear();
            tagsGridFlowLayout.clearViews();
        }

        if (minTagsId == totalTagsId &&totalTagsId==0){
            minTagsId = 0;
            totalTagsId = 15;
        }else {
            minTagsId = totalTagsId;
            totalTagsId = minTagsId+15;
        }
        LogUtils.d("tagSearch","minTagsId = "+ minTagsId+",totalTagsId = "+totalTagsId);
        LogUtils.d("tagSearch","tagsIdList size= "+tagsIdList.size());
        if (minTagsId<tagsDataList.size()&&totalTagsId<tagsDataList.size()){
            for (int i = minTagsId; i <totalTagsId ; i++) {
                someTagsList.add(tagsDataList.get(i));
            }
            LogUtils.d("tagSearch","都小于");
        }else{
            for (int i = minTagsId; i <tagsDataList.size() ; i++) {
                someTagsList.add(tagsDataList.get(i));
            }
            minTagsId = 0;
            totalTagsId = 0;
            LogUtils.d("tagSearch","又一个不小于");
        }

        dramaTagsGridAdapter = new DramaTagsGridAdapter(someTagsList);
        tagsGridFlowLayout.setAdapter(dramaTagsGridAdapter);
    }

    private void setTagsAnimeAdapter() {


        if (dramaTagsAnimeListAdapter == null){
            baseAnimeListForTagsSearchesData = animeListForTagsSearchesData;

            dramaTagsAnimeListAdapter = new DramaTagsAnimeListAdapter(R.layout.drama_timeline_list_item,baseAnimeListForTagsSearchesData);
            dramaTagsAnimeListAdapter.setHasStableIds(true);
            /**
             * adapter动画效果
             * ALPHAIN 渐显
             *SCALEIN 缩放
             *SLIDEIN_BOTTOM 从下到上
             *SLIDEIN_LEFT 从左到右
             *SLIDEIN_RIGHT 从右到左
             */
            dramaTagsAnimeListAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
            //开启上拉加载更多
            dramaTagsAnimeListAdapter.setEnableLoadMore(true);


            //添加底部footer
            dramaTagsAnimeListAdapter.setLoadMoreView(new MyLoadMoreView());
            dramaTagsAnimeListAdapter.disableLoadMoreIfNotFullPage(tagsAnimeListView);
            tagsAnimeListView.setLayoutManager(new LinearLayoutManager(App.instance()));
            tagsAnimeListView.setAdapter(dramaTagsAnimeListAdapter);
            //设置监听
            setAnimeListListener();

        }else {
            if (isLoadMore){
                isLoadMore = false;
                dramaTagsAnimeListAdapter.addData(animeListForTagsSearchesData);
                dramaTagsAnimeListAdapter.loadMoreComplete();
            }else {
                page = 1;
                dramaTagsAnimeListAdapter.setNewData(animeListForTagsSearchesData);
            }
        }

    }

    private void setOnClickListener() {
        refreshTagsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTagsGridAdapter();
                if (tagsIdList!=null&&tagsIdList.size()!=0){
                    tagsIdList.clear();
                }
                if (dramaTagsAnimeListAdapter!=null){
                    animeListForTagsSearchesData.clear();
                    dramaTagsAnimeListAdapter.setNewData(animeListForTagsSearchesData);
                }

            }
        });

    }
    private void setAnimeListListener(){
        dramaTagsAnimeListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                int animeId = dramaTagsAnimeListAdapter.getData().get(position).getId();
                Intent intent = new Intent(getActivity(),DramaActivity.class);
                intent.putExtra("animeId",animeId);
                startActivity(intent);
            }
        });

        //上拉加载监听
        dramaTagsAnimeListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override public void onLoadMoreRequested() {
                if (dramaTagsAnimeListAdapter.getData().size()>=animeListForTagsSearch.getData().getTotal()){
                    isLoadMore = false;
                    dramaTagsAnimeListAdapter.loadMoreEnd();
                }else {
                    isLoadMore = true;
                    page++;
                    setNet(NET_SEARCH_ANIME_FOR_TAGS);
                }
            }
        }, tagsAnimeListView);

    }

    class DramaTagsGridAdapter extends FlowAdapter<DramaTags.DramaTagsData>{

        public DramaTagsGridAdapter(List<DramaTags.DramaTagsData> datas) {
            super(datas);
        }

        @Override
        public View getView(int i) {
            View item = getLayoutInflater().inflate(R.layout.drama_tags_grid_tags_item,null);
            CheckedTextView tagText = item.findViewById(R.id.drama_tags_grid_item_text) ;
            tagText.setText(someTagsList.get(i).getName());
            tagText.setTag(someTagsList.get(i).getId());


            tagText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckedTextView checkedTextView = (CheckedTextView) v;
                    //判断是点击还是取消
                    checkedTextView.toggle();
                    //做非空处理
                    if (tagsIdList == null){
                        tagsIdList = new ArrayList<>();
                    }

                    if (checkedTextView.isChecked()){
                        //被点击
                        tagsIdList.add((Integer) checkedTextView.getTag());

                    }else {
                        //取消
//
                        //遍历集合删除取消的标签ID
                        Iterator<Integer> tagsIdIterator = tagsIdList.iterator();
                        while (tagsIdIterator.hasNext()) {
                            Integer tagId = tagsIdIterator.next();
                            if (tagId == checkedTextView.getTag()){
                                tagsIdIterator.remove();
                            }
                        }

                    }
                    if (tagsIdList.size() == 0){
                        if (dramaTagsAnimeListAdapter == null){
                            //如果是初次进页面 不做操作
                        }else {
                            //如果不是初次进页面 并且取消了所有的tag 清空list
                            animeListForTagsSearchesData.clear();
                            dramaTagsAnimeListAdapter.setNewData(animeListForTagsSearchesData);
                        }
                    }
                    if (tagsIdList.size()!=0){
                        setNet(NET_SEARCH_ANIME_FOR_TAGS);
                    }
                }
            });

            return item;
        }
    }

    class DramaTagsAnimeListAdapter extends BaseQuickAdapter<AnimeListForTagsSearch.AnimeListForTagsSearchData,BaseViewHolder>{


        public DramaTagsAnimeListAdapter(int layoutResId, @Nullable List<AnimeListForTagsSearch.AnimeListForTagsSearchData> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, AnimeListForTagsSearch.AnimeListForTagsSearchData item) {
            helper.setText(R.id.drama_timeline_list_item_name,item.getName());
            helper.setText(R.id.drama_timeline_list_item_summary,item.getSummary());
            GlideUtils.loadImageView(getContext(),item.getAvatar(), (ImageView) helper.getView(R.id.drama_timeline_list_item_image));
        }
    }


    private void setTagsIdStr() {
        tagsIDStr = "";
        if (tagsIdList!=null&&tagsIdList.size()!=0){
            for (int position = 0; position <tagsIdList.size() ; position++) {
                int id = tagsIdList.get(position);
                if (position == 0){
                    if (tagsIDStr == null||tagsIDStr.length() == 0){
                        tagsIDStr = tagsIDStr+id;
                    }else {
                        tagsIDStr = tagsIDStr+","+id;
                    }
                }else {
                    tagsIDStr = tagsIDStr+","+id;
                }
            }
        }

        LogUtils.d("tagsSearch","seenIds = "+tagsIDStr );
    }

}
