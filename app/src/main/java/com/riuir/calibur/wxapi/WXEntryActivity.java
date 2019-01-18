package com.riuir.calibur.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.riuir.calibur.R;
import com.riuir.calibur.assistUtils.LogUtils;
import com.riuir.calibur.ui.share.WXShareUtils;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxentry);
        api = WXAPIFactory.createWXAPI(this, WXShareUtils.APP_ID);
        api.handleIntent(getIntent(), this);
        LogUtils.d("WXShare", " handleIntent:" + api.handleIntent(getIntent(), this));

    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        if (baseResp.getType() == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {
//            Intent intent = new Intent("SHARE_WX_ACTION");
//            intent.putExtra("errCode", baseResp.errCode);
//            sendBroadcast(intent);
            LogUtils.d("WXShare", " errCode:" + baseResp.errCode+" errorStr:"+baseResp.errStr);
            finish();
        }
    }
}
