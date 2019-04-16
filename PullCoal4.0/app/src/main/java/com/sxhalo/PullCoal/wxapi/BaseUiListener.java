package com.sxhalo.PullCoal.wxapi;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.sxhalo.PullCoal.common.MyAppLication;
import com.sxhalo.PullCoal.model.ThirdUserInfo;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 腾讯登陆数据回掉
 * Created by amoldZhang on 2016/11/9.
 */
public class BaseUiListener implements IUiListener {

    private final Tencent mTencent;
    private final Activity mActivity;
    private ThirdUserInfo thirdUser;

    public BaseUiListener(Activity mActivity,Tencent mTencent) {
        this.mTencent = mTencent;
        this.mActivity = mActivity;
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onComplete(Object value) {
        System.out.println("有数据返回..");
        MyAppLication.dismissDelog();
        if(value==null){
            return;
        }
        Log.i("腾讯数据返回json= ",String.valueOf(value));
        initOpenidAndToken((JSONObject) value);
        updateUserInfo();
    }

    @Override
    public void onError(UiError arg0) {

    }

    /**
     * @Title: initOpenidAndToken
     * @Description: 初始化OPENID以及TOKEN身份验证。
     * @param @param jsonObject
     * @return void
     * @throws
     */
    private void initOpenidAndToken (JSONObject jsonObject) {
        thirdUser = new ThirdUserInfo();
        try {
            Log.i("身份验证json= ",String.valueOf(jsonObject));
            //这里的Constants类，是 com.tencent.connect.common.Constants类，下面的几个参数也是固定的
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN );
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN );
            //OPENID,作为唯一身份标识
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID );
            if (!TextUtils. isEmpty(token) && !TextUtils.isEmpty(expires)&& !TextUtils. isEmpty(openId)) {
                //设置身份的token
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
                thirdUser.setThirdID(openId);
            }
        } catch (Exception e) {
        }
    }

    /**
     * @Title: updateUserInfo
     * @Description: 在回调里面可以获取用户信息数据了
     * @param
     * @return void
     * @throws
     */
    private void updateUserInfo() {
        if ( mTencent != null && mTencent.isSessionValid()) {
            UserInfo mInfo = new UserInfo(mActivity,  mTencent.getQQToken());
            IUiListener listener = new IUiListener() {
                @Override
                public void onError(UiError e) {
                }
                // 用户的信息回调在此处
                @Override
                public void onComplete( final Object response) {

                    // 返回Bitmap对象。
                    if(response == null){
                        return;
                    }
                    try {
                        JSONObject obj = new JSONObject(response.toString());
                        Log.i("用户信息json= ",String.valueOf(obj));
                        thirdUser.setNickName(obj.optString( "nickname"));
                        thirdUser.setIcon(obj.optString( "figureurl_qq_2"));
                        thirdUser.setCity(obj.optString( "city"));
                        thirdUser.setUserSex(obj.optString( "gender"));
                        thirdUser.setType("qq");
                        ThirdUserVerify. verifyUser(thirdUser);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancel() {
                }
            };
            mInfo.getUserInfo(listener);
        }
    }
}
