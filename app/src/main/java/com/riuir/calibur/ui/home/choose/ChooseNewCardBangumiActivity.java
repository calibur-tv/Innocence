package com.riuir.calibur.ui.home.choose;

import android.content.Intent;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.app.App;
import com.riuir.calibur.assistUtils.KeyBoardUtils;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.SharedPreferencesUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.data.anime.BangumiAllList;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.home.MainActivity;
import com.riuir.calibur.ui.home.choose.adapter.ChooseBangumiAdapter;
import com.riuir.calibur.utils.Constants;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseNewCardBangumiActivity extends BaseActivity {

    public static final int CARD = 111;
    public static final int IMAGE = 112;
    public static final int IMAGE_ALBUM = 113;
    public static final int SCORE = 114;

    @BindView(R.id.choose_new_card_bangumi_cancel)
    ImageView cancelBtn;
    @BindView(R.id.choose_new_card_bangumi_list_view)
    RecyclerView bangumiListView;
    @BindView(R.id.choose_new_card_bangumi_search_edit_text)
    EditText searchEdit;
    @BindView(R.id.choose_new_card_bangumi_search_btn)
    ImageView searchBtn;
    @BindView(R.id.choose_new_card_bangumi_search_clear_edit)
    TextView clearSearchBtn;

    ChooseBangumiAdapter bangumiAdapter;

    ArrayList<BangumiAllList.BangumiAllListData> baseBangumiList;
    ArrayList<BangumiAllList.BangumiAllListData> bangumiList = new ArrayList<>();

    String searchContent;

    private int code;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_choose_new_card_bangumi;
    }

    @Override
    protected void onInit() {
        Intent intent = getIntent();
        code = intent.getIntExtra("code",0);

        if (Constants.bangumiAllListData==null)
            Constants.bangumiAllListData = (ArrayList<BangumiAllList.BangumiAllListData>)
                    SharedPreferencesUtils.get(this,"bangumiAllListData",new ArrayList<BangumiAllList.BangumiAllListData>());
        if (Constants.bangumiAllListData!=null){
            baseBangumiList = Constants.bangumiAllListData;
            LogUtils.d("baseBangumiList","baseBangumiList size = "+baseBangumiList.size());
            setAdapter();
        }else{
            setNet();
        }
    }

    private void setNet() {
        apiGet.getBangumiAllList().enqueue(new Callback<BangumiAllList>() {
            @Override
            public void onResponse(Call<BangumiAllList> call, Response<BangumiAllList> response) {
                if (response!=null&&response.isSuccessful()){
                    Constants.bangumiAllListData = response.body().getData();
                    SharedPreferencesUtils.put(App.instance(),"bangumiAllListData",response.body().getData());
                    baseBangumiList = Constants.bangumiAllListData;
                    setAdapter();
                }else  if (!response.isSuccessful()){
                    String errorStr = "";
                    try {
                        errorStr = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    Event<String> info =gson.fromJson(errorStr,Event.class);
                    ToastUtils.showShort(ChooseNewCardBangumiActivity.this,info.getMessage());

                }else {
                    ToastUtils.showShort(ChooseNewCardBangumiActivity.this,"未知原因导致加载失败了");
                }

            }

            @Override
            public void onFailure(Call<BangumiAllList> call, Throwable t) {
                ToastUtils.showShort(ChooseNewCardBangumiActivity.this,"网络异常,请检查您的网络");
                CrashReport.postCatchedException(t);
            }
        });
    }


    private void setAdapter() {
        bangumiAdapter = new ChooseBangumiAdapter(R.layout.choose_all_bangumi_list_item,baseBangumiList,ChooseNewCardBangumiActivity.this);

        bangumiListView.setLayoutManager(new LinearLayoutManager(App.instance()));
        bangumiAdapter.setHasStableIds(true);
        /**
         * adapter动画效果
         * ALPHAIN 渐显
         *SCALEIN 缩放
         *SLIDEIN_BOTTOM 从下到上
         *SLIDEIN_LEFT 从左到右
         *SLIDEIN_RIGHT 从右到左
         */
        bangumiAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);

        bangumiListView.setAdapter(bangumiAdapter);

        //添加监听
        setListener();
    }

    private void setListener() {
        bangumiAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                KeyBoardUtils.closeKeybord(searchEdit,ChooseNewCardBangumiActivity.this);
                BangumiAllList.BangumiAllListData data = (BangumiAllList.BangumiAllListData)adapter.getData().get(position);
                Intent intent = new Intent();
                intent.putExtra("bangumiId",data.getId());
                intent.putExtra("bangumiAvatar",data.getAvatar());
                intent.putExtra("bangumiName",data.getName());
                if (code == CARD){
                    setResult(CARD,intent);
                }else if (code == IMAGE){
                    setResult(IMAGE,intent);
                }else if (code == IMAGE_ALBUM){
                    setResult(IMAGE_ALBUM,intent);
                }else if (code == SCORE){
                    setResult(SCORE,intent);
                }

                finish();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyBoardUtils.closeKeybord(searchEdit,ChooseNewCardBangumiActivity.this);
                searchEdit.clearFocus();
            }
        });
        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    KeyBoardUtils.closeKeybord(searchEdit,ChooseNewCardBangumiActivity.this);
                    searchEdit.clearFocus();
                    return true;
                }
                return false;
            }
        });
        searchEdit.addTextChangedListener(textWatcher);

        clearSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEdit.setText("");
            }
        });
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            searchContent = searchEdit.getText().toString();
            if (searchContent.length() == 0){
                clearSearchBtn.setVisibility(View.GONE);
            }else {
                clearSearchBtn.setVisibility(View.VISIBLE);
            }
            setSearch();
        }
    };

    private void setSearch() {
        bangumiList.clear();
        Iterator<BangumiAllList.BangumiAllListData> iterator = baseBangumiList.iterator();
        while (iterator.hasNext()){
            BangumiAllList.BangumiAllListData data = iterator.next();
            String name = data.getName();
            if (name.contains(searchContent)){
                bangumiList.add(data);
            }
        }
        bangumiAdapter.setNewData(bangumiList);

    }

    @Override
    protected void handler(Message msg) {

    }
}
