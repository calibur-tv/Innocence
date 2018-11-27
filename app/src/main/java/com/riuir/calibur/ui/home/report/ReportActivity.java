package com.riuir.calibur.ui.home.report;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.ToastUtils;
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

public class ReportActivity extends BaseActivity {

    //其他
    private final int TYPE_OTHER = 0;
    //违法违规
    private final int TYPE_1 = 1;
    //色情低俗
    private final int TYPE_2 = 2;
    //赌博诈骗
    private final int TYPE_3 = 3;
    //人身攻击
    private final int TYPE_4 = 4;
    //侵犯隐私
    private final int TYPE_5 = 5;
    //内容抄袭
    private final int TYPE_6 = 6;
    //垃圾广告
    private final int TYPE_7 = 7;
    //恶意引战
    private final int TYPE_8 = 8;
    //重复内容/刷屏
    private final int TYPE_9 = 9;
    //内容不相关
    private final int TYPE_10 = 10;
    //互刷团子
    private final int TYPE_11 = 11;

    @BindView(R.id.report_activity_back_btn)
    ImageView backBtn;
    @BindView(R.id.report_activity_report_btn)
    Button reportBtn;
    @BindView(R.id.report_activity_radio_group)
    RadioGroup radioGroup;
    @BindView(R.id.report_activity_report_message_edit)
    EditText messageEdit;

    Call<Event<String>> callReport;

    int type = -1;
    String model = "";
    int id;
    String message = "";

    @Override
    protected int getContentViewId() {
        return R.layout.activity_report;
    }

    @Override
    protected void onInit() {
        Intent intent = getIntent();
        model = intent.getStringExtra("reportModel");
        id = intent.getIntExtra("reportId",0);
        setRadioListener();
        setListener();
    }

    private void setRadioListener() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (radioGroup.getCheckedRadioButtonId()){
                    case R.id.report_activity_radio_btn12:
                        type = TYPE_OTHER;
                        break;
                    case R.id.report_activity_radio_btn1:
                        type = TYPE_1;
                        break;
                    case R.id.report_activity_radio_btn2:
                        type = TYPE_2;
                        break;
                    case R.id.report_activity_radio_btn3:
                        type = TYPE_3;
                        break;
                    case R.id.report_activity_radio_btn4:
                        type = TYPE_4;
                        break;
                    case R.id.report_activity_radio_btn5:
                        type = TYPE_5;
                        break;
                    case R.id.report_activity_radio_btn6:
                        type = TYPE_6;
                        break;
                    case R.id.report_activity_radio_btn7:
                        type = TYPE_7;
                        break;
                    case R.id.report_activity_radio_btn8:
                        type = TYPE_8;
                        break;
                    case R.id.report_activity_radio_btn9:
                        type = TYPE_9;
                        break;
                    case R.id.report_activity_radio_btn10:
                        type = TYPE_10;
                        break;
                    case R.id.report_activity_radio_btn11:
                        type = TYPE_11;
                        break;
                }
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

        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type!=-1){
                    if (id!=0&&model.length()!=0){
                        reportBtn.setClickable(false);
                        reportBtn.setText("提交中...");
                        setNet();
                    }else {
                        ToastUtils.showShort(ReportActivity.this,"举报来源丢失，\n请退出重新进入该页面");
                    }
                }else {
                    ToastUtils.showShort(ReportActivity.this,"请选择举报类型");
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        if (callReport!=null){
            callReport.cancel();
        }
        super.onDestroy();
    }

    private void setMessage() {
        if (messageEdit.getText()!=null&&messageEdit.getText().toString()!=null&&
                messageEdit.getText().toString().length()!=0){
            message = messageEdit.getText().toString();
        }else {
            message = "";
        }
    }

    private void setNet() {
        setMessage();
        apiService.getCallReportSend(id,model,type,message)
                .compose(Rx2Schedulers.applyObservableAsync())
                .subscribe(new ObserverWrapper<String>(){
                    @Override
                    public void onSuccess(String s) {
                        ToastUtils.showShort(ReportActivity.this,"举报成功");
                        finish();
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        super.onFailure(code, errorMsg);
                        setReportFailed();
                    }
                });
    }

    private void setReportFailed() {
        if (reportBtn!=null){
            reportBtn.setClickable(true);
            reportBtn.setText("提交");
        }
    }

    @Override
    protected void handler(Message msg) {

    }
}
