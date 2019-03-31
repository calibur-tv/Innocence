package com.riuir.calibur.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import calibur.core.http.OkHttpClientManager;
import calibur.core.http.models.base.ResponseBean;
import calibur.core.http.models.login.WeChatLoginModel;
import calibur.foundation.utils.JSONUtil;
import calibur.share.R;
import calibur.share.util.WXShareUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.IOException;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI api;
    public static final String WX_LOGIN_CALLBACK_DATA = "wxLoginCallBackData";
    Intent intentBroadCast;

    private String wxGetTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxentry);
        api = WXAPIFactory.createWXAPI(this, WXShareUtils.APP_ID);
        intentBroadCast = new Intent();
        intentBroadCast.setAction(WX_LOGIN_CALLBACK_DATA);
        boolean handleIntent = api.handleIntent(getIntent(), this);
        if(!handleIntent) finish();
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        Log.d("wxLoginCallBack","type = "+baseResp.getType());
        if (baseResp.getType() == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {
//            Intent intent = new Intent("SHARE_WX_ACTION");
//            intent.putExtra("errCode", baseResp.errCode);
//            sendBroadcast(intent);
            finish();
        }else if (baseResp.getType() == ConstantsAPI.COMMAND_SENDAUTH){
            Log.d("wxLoginCallBack","errCode = "+baseResp.errCode);
            switch (baseResp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    String code = ((SendAuth.Resp) baseResp).code;
                    //获取用户信息
                    getAccessToken(code);
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED://用户拒绝授权
                    intentBroadCast.putExtra("wxLoginSuccess",false);
                    sendBroadcast(intentBroadCast);
                    finish();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL://用户取消
                    intentBroadCast.putExtra("wxLoginSuccess",false);
                    sendBroadcast(intentBroadCast);
                    finish();
                    break;
                default:
                    finish();
                    break;
            }
        }
    }

    private void getAccessToken(String code) {
        StringBuffer loginUrl = new StringBuffer();
        loginUrl.append(wxGetTokenUrl)
                .append("?appid=")
                .append(WXShareUtils.APP_ID)
                .append("&secret=")
                .append(WXShareUtils.APP_SECRET)
                .append("&code=")
                .append(code)
                .append("&grant_type=authorization_code");
        Request request = new Request.Builder().url(loginUrl.toString()).build();
        OkHttpClientManager.getDefaultClient().newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    ResponseBody body = response.body();
                    String dataStr = body.string();
                    WeChatLoginModel wxLoginData = JSONUtil.fromJson(dataStr,WeChatLoginModel.class);
                    intentBroadCast.putExtra("wxLoginSuccess",true);
                    intentBroadCast.putExtra("wxLoginData",wxLoginData);
                    sendBroadcast(intentBroadCast);
                    finish();
                }else {
                    //请求失败
                    intentBroadCast.putExtra("wxLoginSuccess",false);
                    sendBroadcast(intentBroadCast);
                    finish();
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {

            }
        });
    }
}
