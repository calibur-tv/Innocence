package com.riuir.calibur.ui.home.report;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.assistUtils.PhoneSystemUtils;
import com.riuir.calibur.assistUtils.ToastUtils;
import com.riuir.calibur.assistUtils.VersionUtils;
import com.riuir.calibur.data.Event;
import com.riuir.calibur.ui.common.BaseActivity;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.IOException;

import butterknife.BindView;
import calibur.core.http.observer.ObserverWrapper;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackActivity extends BaseActivity {


    //功能建议
    private final int TYPE_1 = 1;
    //遇到错误
    private final int TYPE_2 = 2;
    //其他
    private final int TYPE_3 = 3;
    //资源报错
    private final int TYPE_4 = 4;
    //求资源
    private final int TYPE_5 = 5;
    //求偶像
    private final int TYPE_6 = 6;

    @BindView(R.id.feedback_activity_back_btn)
    ImageView backBtn;
    @BindView(R.id.feedback_activity_feedback_btn)
    Button feedbackBtn;
    @BindView(R.id.feedback_activity_radio_group)
    RadioGroup radioGroup;
    @BindView(R.id.feedback_activity_report_message_edit)
    EditText feedbackEdit;

    int type = -1;
    String message = "";
    //设备信息
    String ua = "";

    Call<Event<String>> callUserFeedback;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_feedback;
    }

    @Override
    protected void onInit() {
        //获取手机厂商 ----- 手机型号
        ua = PhoneSystemUtils.getDeviceBrand()+"-----"+PhoneSystemUtils.getSystemModel()
                +"-----"+ VersionUtils.getLocalVersionName()+"-----"+PhoneSystemUtils.getSystemVersion();
        LogUtils.d("feedback","ua = "+ua);
        setListener();
        setRadioListener();
    }

    @Override
    public void onDestroy() {
        if (callUserFeedback!=null){
            callUserFeedback.cancel();
        }
        super.onDestroy();
    }

    private void setNet() {
        apiService.getCallUserFeedback(type,message,ua)
                .compose(Rx2Schedulers.applyObservableAsync())
                .subscribe(new ObserverWrapper<String>(){
                    @Override
                    public void onSuccess(String s) {
                        ToastUtils.showShort(FeedbackActivity.this,"反馈成功");
                        finish();
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        super.onFailure(code, errorMsg);
                        setReportFailed();
                    }
                });
    }

    private void setListener() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        feedbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = feedbackEdit.getText().toString();
                if (type!=-1&&message!=null&&message.length()!=0){
                    feedbackBtn.setClickable(false);
                    feedbackBtn.setText("提交中...");
                    setNet();
                }else if (type == -1){
                    ToastUtils.showShort(FeedbackActivity.this,"请选择反馈类型");
                }else {
                    ToastUtils.showShort(FeedbackActivity.this,"请输入反馈内容");
                }
            }
        });
    }

    private void setRadioListener() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (radioGroup.getCheckedRadioButtonId()){
                    case R.id.feedback_activity_radio_btn1:
                        type = TYPE_1;
                        break;
                    case R.id.feedback_activity_radio_btn2:
                        type = TYPE_2;
                        break;
                    case R.id.feedback_activity_radio_btn3:
                        type = TYPE_4;
                        break;
                    case R.id.feedback_activity_radio_btn4:
                        type = TYPE_5;
                        break;
                    case R.id.feedback_activity_radio_btn5:
                        type = TYPE_6;
                        break;
                    case R.id.feedback_activity_radio_btn6:
                        type = TYPE_3;
                        break;
                }
            }
        });
    }

    private void setReportFailed() {
        if (feedbackBtn!=null){
            feedbackBtn.setClickable(true);
            feedbackBtn.setText("提交");
        }
    }

    @Override
    protected void handler(Message msg) {

    }
}
