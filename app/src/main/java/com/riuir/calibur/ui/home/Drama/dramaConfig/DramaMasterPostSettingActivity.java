package com.riuir.calibur.ui.home.Drama.dramaConfig;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.ui.common.BaseActivity;

import java.io.IOException;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DramaMasterPostSettingActivity extends BaseActivity {

    @BindView(R.id.drama_master_post_setting_back)
    ImageView backBtn;
    @BindView(R.id.drama_master_post_setting_jing_edit)
    EditText jingEdit;
    @BindView(R.id.drama_master_post_setting_jing_btn)
    TextView jingBtn;
    @BindView(R.id.drama_master_post_setting_cancel_jing_edit)
    EditText cancelJingEdit;
    @BindView(R.id.drama_master_post_setting_cancel_jing_btn)
    TextView cancelJingBtn;
    @BindView(R.id.drama_master_post_setting_top_edit)
    EditText topEdit;
    @BindView(R.id.drama_master_post_setting_top_btn)
    TextView topBtn;
    @BindView(R.id.drama_master_post_setting_cancel_top_edit)
    EditText cancelTopEdit;
    @BindView(R.id.drama_master_post_setting_cancel_top_btn)
    TextView cancelTopBtn;

    String jingStr = "";
    String cancelJingStr = "";
    String topStr = "";
    String cancelTopStr = "";

    String id = "";
    public static final String Nice = "nice";
    public static final String CancelNice = "removeNice";
    public static final String Top = "top";
    public static final String CancelTop = "removeTop";

    private String baseUrl = "https://www.calibur.tv/post/";

    private Call<Event<String>> settingCall;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_drama_master_post_setting;
    }

    @Override
    protected void onInit() {
        setListener();
    }

    @Override
    public void onDestroy() {
        if (settingCall!=null){
            settingCall.cancel();
        }
        super.onDestroy();
    }

    private void setListener() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        jingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setJing();
            }
        });
        cancelJingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCancelJing();
            }
        });
        topBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTop();
            }
        });
        cancelTopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCancelTop();
            }
        });
    }

    private void setJing() {
        jingStr = jingEdit.getText().toString();
        if (jingStr!=null&&jingStr.length()!=0){
            if (jingStr.contains(baseUrl)){
                jingBtn.setClickable(false);
                jingBtn.setText("加精中");
                id = jingStr.replace(baseUrl,"");
                setNet(Nice);
            }else {
                ToastUtils.showLong(DramaMasterPostSettingActivity.this,"请输入正确的链接");
            }
        }else {
            ToastUtils.showLong(DramaMasterPostSettingActivity.this,"请输入链接");
        }
    }

    private void setCancelJing() {
        cancelJingStr = cancelJingEdit.getText().toString();
        if (cancelJingStr!=null&&cancelJingStr.length()!=0){
            if (cancelJingStr.contains(baseUrl)){
                cancelJingBtn.setClickable(false);
                cancelJingBtn.setText("取消中");
                id = cancelJingStr.replace(baseUrl,"");
                setNet(CancelNice);
            }else {
                ToastUtils.showLong(DramaMasterPostSettingActivity.this,"请输入正确的链接");
            }
        }else {
            ToastUtils.showLong(DramaMasterPostSettingActivity.this,"请输入链接");
        }
    }

    private void setTop() {
        topStr = topEdit.getText().toString();
        if (topStr!=null&&topStr.length()!=0){
            if (topStr.contains(baseUrl)){
                topBtn.setClickable(false);
                topBtn.setText("置顶中");
                id = topStr.replace(baseUrl,"");
                setNet(Top);
            }else {
                ToastUtils.showLong(DramaMasterPostSettingActivity.this,"请输入正确的链接");
            }
        }else {
            ToastUtils.showLong(DramaMasterPostSettingActivity.this,"请输入链接");
        }
    }

    private void setCancelTop() {
        cancelTopStr = cancelTopEdit.getText().toString();
        if (cancelTopStr!=null&&cancelTopStr.length()!=0){
            if (cancelTopStr.contains(baseUrl)){
                cancelTopBtn.setClickable(false);
                cancelTopBtn.setText("取消中");
                id = cancelTopStr.replace(baseUrl,"");
                setNet(CancelTop);
            }else {
                ToastUtils.showLong(DramaMasterPostSettingActivity.this,"请输入正确的链接");
            }
        }else {
            ToastUtils.showLong(DramaMasterPostSettingActivity.this,"请输入链接");
        }
    }

    private void setNet(String type) {
        if (type.equals(Nice)){
            settingCall = apiPost.getCallPostNiceSet(id);
        }else if (type.equals(CancelNice)){
            settingCall = apiPost.getCallPostNiceRemove(id);
        }else if (type.equals(Top)){
            settingCall = apiPost.getCallPostTopSet(id);
        }else if (type.equals(CancelTop)){
            settingCall = apiPost.getCallPostTopRemove(id);
        }else{
            ToastUtils.showLong(DramaMasterPostSettingActivity.this,"数据出错");
            setFailed();
            return;
        }
        settingCall.enqueue(new Callback<Event<String>>() {
            @Override
            public void onResponse(Call<Event<String>> call, Response<Event<String>> response) {
                if (response!=null&&response.isSuccessful()){
                    ToastUtils.showLong(DramaMasterPostSettingActivity.this,"设置成功,可继续设置！");
                    setFinish();
                }else if (response!=null&&!response.isSuccessful()){
                    String errorStr = "";
                    try {
                        errorStr = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();

                    Event<String> info = null;
                    try {
                        info = gson.fromJson(errorStr,Event.class);
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                    if (info!=null){
                        ToastUtils.showShort(DramaMasterPostSettingActivity.this,info.getMessage());
                    }else {
                        ToastUtils.showShort(DramaMasterPostSettingActivity.this,"服务器出现异常，请稍后再试！");
                    }
                    setFailed();
                }else {
                    ToastUtils.showShort(DramaMasterPostSettingActivity.this,"获取不到服务器，请稍后再试！");
                    setFailed();
                }
            }

            @Override
            public void onFailure(Call<Event<String>> call, Throwable t) {
                if (call.isCanceled()){
                }else {
                    ToastUtils.showShort(DramaMasterPostSettingActivity.this,"网络异常，请稍后再试！");
                    setFailed();
                }

            }
        });
    }
    private void setFinish(){
        if (jingEdit!=null&&cancelJingEdit!=null&&topEdit!=null&&cancelTopEdit!=null){
            jingEdit.setText("");
            cancelJingEdit.setText("");
            topEdit.setText("");
            cancelTopEdit.setText("");
            setFailed();
        }
    }

    private void setFailed(){
        if (jingBtn!=null&&cancelJingBtn!=null&&topBtn!=null&&cancelTopBtn!=null){
            jingBtn.setClickable(true);
            jingBtn.setText("确定");
            cancelJingBtn.setClickable(true);
            cancelJingBtn.setText("确定");
            topBtn.setClickable(true);
            topBtn.setText("确定");
            cancelTopBtn.setClickable(true);
            cancelTopBtn.setText("确定");
        }
    }

    @Override
    protected void handler(Message msg) {

    }
}
