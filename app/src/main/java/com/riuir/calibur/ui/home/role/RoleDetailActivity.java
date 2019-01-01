package com.riuir.calibur.ui.home.role;

import android.os.Handler;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import butterknife.BindView;
import calibur.core.http.models.anime.RoleShowInfo;
import calibur.core.http.observer.ObserverWrapper;
import calibur.core.jsbridge.AbsJsBridge;
import calibur.core.jsbridge.interfaces.IH5JsCallApp;
import calibur.core.jsbridge.utils.JsBridgeUtil;
import calibur.core.templates.TemplateRenderEngine;
import calibur.foundation.rxjava.rxbus.Rx2Schedulers;
import calibur.foundation.utils.JSONUtil;
import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.PhoneSystemUtils;
import com.riuir.calibur.ui.common.BaseActivity;
import com.riuir.calibur.ui.jsbridge.CommonJsBridgeImpl;
import com.riuir.calibur.ui.web.WebTemplatesUtils;
import com.riuir.calibur.utils.Constants;
import java.util.Map;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

public class RoleDetailActivity extends BaseActivity implements IH5JsCallApp {

    private WebView mWebView;
    public AbsJsBridge mJavaScriptNativeBridge;
    private int roleId;

    @BindView(R.id.role_detail_back_btn)
    ImageView backBtn;

    RoleShowInfo roleShowInfo;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_role_detail;
    }

    @Override
    protected void onInit() {
        roleId = getIntent().getIntExtra("roleId", 0);
        initWebView();
        initView();
        TemplateRenderEngine.getInstance().checkRoleDetailTemplateForUpdate();
    }

    private void initView() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setLoadingView(findViewById(R.id.refresh_layout));
    }

    private void initWebView() {
        mWebView = findViewById(R.id.role_detail_webview);
        mJavaScriptNativeBridge = new CommonJsBridgeImpl(this, new Handler(), this, mWebView);
        mWebView.addJavascriptInterface(mJavaScriptNativeBridge, JsBridgeUtil.BRIDGE_NAME);
    }

    @Override
    protected void onLoadData() {
        showLoading();
        apiService.getRoleDetail(roleId)
                .compose(Rx2Schedulers.applyObservableAsync())
                .subscribe(new ObserverWrapper<Object>() {
                    @Override
                    public void onSuccess(Object data) {
                        JSONObject jsonObj = new JSONObject((Map) data);
                        WebTemplatesUtils.loadTemplates(mWebView, TemplateRenderEngine.ROLE, jsonObj.toString());
                        roleShowInfo = JSONUtil.fromJson(jsonObj.toString(), RoleShowInfo.class);
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        super.onFailure(code, errorMsg);
                    }

                    @Override
                    public void onComplete() {
                        hideLoading();
                    }
                });
    }

    @Nullable
    @Override
    public Object getDeviceInfo() {
        return PhoneSystemUtils.getDeviceInfo();
    }

    @Nullable
    @Override
    public Object getUserInfo() {
        return Constants.userInfoData;
    }

    @Override
    public void setUserInfo(@Nullable Object params) {

    }

    @Override
    public void createMainComment(@Nullable Object params) {

    }

    @Override
    public void createSubComment(@Nullable Object params) {

    }

    @Override
    public void toggleClick(@Nullable Object params) {

    }

    @Nullable
    @Override
    public Object showConfirm(@Nullable Object params) {
        return null;
    }
}
