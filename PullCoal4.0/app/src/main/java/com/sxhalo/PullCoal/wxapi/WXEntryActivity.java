package com.sxhalo.PullCoal.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.AppManager;
import com.sxhalo.PullCoal.common.MyAppLication;
import com.sxhalo.PullCoal.common.base.Config;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.model.ThirdUserInfo;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.Utils;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import org.json.JSONObject;

import java.util.LinkedHashMap;


/**
 * 微信登陆数据返回接受
 * Created by amoldZhang on 2016/11/10.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {


    public ThirdUserInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            AppManager. getAppManager().addActivity(WXEntryActivity. this);
//        MyAppLication.mIWXAPI = WXAPIFactory.createWXAPI(this, Config.getWeChat_APPID());
            MyAppLication.getIWXAPI().handleIntent(getIntent(), WXEntryActivity.this);  //必须调用此句话
        } catch (Exception error) {
            MyException.uploadExceptionToServer(this,error.fillInStackTrace());
            GHLog.e("联网错误", error.toString());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        MyAppLication. mIWXAPI.handleIntent(intent, WXEntryActivity.this);//必须调用此句话
    }

    @Override
    public void onReq(BaseReq baseReq) {
        System.out.print("联网返回"+baseReq.toString());
    }

    /**
     * Title: onResp
     *
     * API：https://open.weixin.qq.com/ cgi- bin/showdocument ?action=dir_list&t=resource/res_list&verify=1&id=open1419317853 &lang=zh_CN
     * Description:在此处得到Code之后调用https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
     *  获取到token和openID。之后再调用https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID 获取用户个人信息
     *
     * @param baseResp
     */
    @Override
    public void onResp(BaseResp baseResp) {
        if(baseResp instanceof SendAuth.Resp){
            SendAuth.Resp newResp = (SendAuth.Resp) baseResp;
            //分享时
            if (newResp.transaction != null && newResp.transaction .equals(buildTransaction("webpage"))){
                return;
            }
            //第三方登录时
            switch (newResp.errCode){
                case BaseResp.ErrCode.ERR_OK://用户同意
                    //获取微信传回的code
                    String code = newResp.code;
                    if(MyAppLication.isWxLogin){
                        getToken(code);
                    } else{
                        WXEntryActivity. this.finish();
                    }
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED://用户拒绝授权
                    info = new ThirdUserInfo();
                    info.setErrCode(ThirdUserInfo.ErrCode.ERR_AUTH_DENIED);
                    info.setErrText(getString(R.string.refuse_authorization));
                    ThirdUserVerify. verifyUser(info);
                    WXEntryActivity. this.finish();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL://用户取消
                    info = new ThirdUserInfo();
                    info.setErrCode(ThirdUserInfo.ErrCode.ERR_USER_CANCEL);
                    info.setErrText(getString(R.string.cancel_authorization));
                    ThirdUserVerify. verifyUser(info);
                    WXEntryActivity. this.finish();
                    break;
                case BaseResp.ErrCode.ERR_BAN://签名错误
                    info = new ThirdUserInfo();
                    info.setErrCode(ThirdUserInfo.ErrCode.ERR_BAN);
                    info.setErrText(getString(R.string.wrong_signature));
                    ThirdUserVerify. verifyUser(info);
                    WXEntryActivity. this.finish();
                    break;
                default:
                    WXEntryActivity. this.finish();
                    break;
            }
        }else{
            WXEntryActivity. this.finish();
        }
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private void getToken(String code) {
        try {
            // https://api.weixin.qq.com/sns/oauth2/access_token
            String webUrl = "https://api.weixin.qq.com/";
            String url = "sns/oauth2/access_token";
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("appid",new Config().weChat_APPID);
            params.put("secret",new Config().weChat_APPSecret);
            params.put("code",code);
            params.put("grant_type","authorization_code");
            getData(0,webUrl,url,params);
        } catch (Exception e) {
            MyException.uploadExceptionToServer(this,e.fillInStackTrace());
            e.printStackTrace();
        }
    }

    //获取到token和openID之后，调用此接口得到身份信息
    private void getUserInfo(String access_token, String openid) {
        String webUrl = "https://api.weixin.qq.com/";
        String url = "sns/userinfo";
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("access_token",access_token);
        params.put("openid",openid);
        getData(1,webUrl,url,params);
    }

    /**
     * 联网
     * @param tag
     * @param url
     * @param params
     */
    private void getData(final int tag,String webUrl,String url, LinkedHashMap<String, String> params){
        try {
            new DataUtils(this,webUrl,params).getUserThirdParty(url,new DataUtils.DataBack<String>() {

                @Override
                public void getData(String resString) {
                    try {
                        if (resString == null) {
                            return;
                        }
                        GHLog.i("请求成功",resString);
                        JSONObject response = new JSONObject(resString);
                        setInfo(tag,response);
                    } catch (Exception e) {
                        MyException.uploadExceptionToServer(getParent(),e.fillInStackTrace());
                        GHLog.e("第三方登录联网成功返回",e.toString());
                    }
                }

                @Override
                public void getError(Throwable throwable) {
                    GHLog.e("联网错误", throwable.toString());
                    Utils.showText(getApplication(),0,"网络链接错误");
                }
            });
        } catch (Exception e) {
            MyException.uploadExceptionToServer(this,e.fillInStackTrace());
            e.printStackTrace();
        }
    }

    /**
     * 根据不同的操作执行数据分发
     * @param tag
     * @param response
     */
    private void setInfo(int tag,JSONObject response){
        switch (tag){
            case 0:
                info = new ThirdUserInfo();
                info.setThirdID(response.optString( "openid"));
                getUserInfo(response.optString( "access_token"),response.optString( "openid"));
                break;
            case 1:
                String nickName = "";
//                try {
                    nickName = response.optString( "nickname");
//                    GHLog.i("正常解析出来的值",nickName);
//                    byte[] bytes = nickName.getBytes("ISO-8859-1");
//                    String name = new String(bytes,"utf-8");
//                    if (name.equals())
//                    GHLog.i("解码后的值",nickName);
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
                info.setNickName(nickName);
                info.setUserSex(response.optInt( "sex")==1? "男": "女");
                info.setIcon(response.optString( "headimgurl"));
                info.setCity(response.optString( "city"));
                info.setType("wechat");
                ThirdUserVerify. verifyUser(info);
                WXEntryActivity. this.finish();
                break;
        }
    }

}
