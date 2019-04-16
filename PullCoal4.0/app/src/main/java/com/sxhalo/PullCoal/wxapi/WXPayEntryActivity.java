package com.sxhalo.PullCoal.wxapi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.sxhalo.PullCoal.common.MyAppLication;
import com.sxhalo.PullCoal.model.ThirdUserInfo;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

/**
 * Created by amoldZhang on 2018/1/10.
 */

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;
    public ThirdUserInfo info;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.pay_result);

//        api = WXAPIFactory.createWXAPI(this, new Config().weChat_APPID);
        api = MyAppLication.getIWXAPI();
        api.handleIntent(getIntent(), this);
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        System.out.print("联网返回"+ req.toString());
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            info = new ThirdUserInfo();
            info.setErrCode(resp.errCode);
            info.setErrText(resp.toString());
            ThirdUserVerify. verifyUser(info);
            WXPayEntryActivity.this.finish();
        }
    }
}
