//package com.sxhalo.PullCoal.wxapi;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.Log;
//
////import com.sina.weibo.sdk.auth.Oauth2AccessToken;
////import com.sina.weibo.sdk.auth.WeiboAuthListener;
////import com.sina.weibo.sdk.exception.WeiboException;
////import com.sina.weibo.sdk.net.RequestListener;
//import com.sina.weibo.sdk.openapi.UsersAPI;
//import com.sina.weibo.sdk.openapi.models.User;
//import com.sxhalo.PullCoal.common.MyAppLication;
//import com.sxhalo.PullCoal.common.base.Config;
//import com.sxhalo.PullCoal.model.ThirdUserInfo;
//
//
///**
// * Created by amoldZhang on 2016/11/11.
// * 微博认证授权回调类。 1. SSO 授权时，需要在 {@link # onActivityResult} 中调用
// * {@link //SsoHandler # authorizeCallBack} 后， 该回调才会被执行。 2. 非 SSO
// * 授权时，当授权结束后，该回调就会被执行。 当授权成功后，请保存该 access_token、expires_in、uid 等信息到
// * SharedPreferences 中。
// */
//public class AuthListener implements WeiboAuthListener {
//    private  Activity mActivity;
//    private Oauth2AccessToken mAccessToken;
//    private ThirdUserInfo thirdUser;
//
//    public AuthListener(Activity mActivity) {
//        this.mActivity = mActivity;
//        MyAppLication.dismissDelog();
//    }
//
//    @Override
//    public void onWeiboException(WeiboException e) {
//    }
//
//    @Override
//    public void onCancel() {
//    }
//
//    @Override
//    public void onComplete(Bundle bundle) {
//        // 从 Bundle 中解析 Token
//        mAccessToken = Oauth2AccessToken.parseAccessToken(bundle);
//        if ( mAccessToken.isSessionValid()) {
////            MyProgressDialog. showDialogWithFalse(mActivity, "登陆","正在获取用户信息" );
//            thirdUser = new ThirdUserInfo();
//            thirdUser.setThirdID( mAccessToken.getUid());  //mAccessToken.getUid() ，获取到UID，作为身份的唯一标示
//            UsersAPI mUsersAPI = new UsersAPI(mActivity, new Config().Sina_APP_KEY , mAccessToken );
//            long uid = Long.parseLong(mAccessToken.getUid());
//            mUsersAPI.show(uid, mListener); //获取用户基本信息
//        } else {
//            // 以下几种情况，您会收到 Code：
//            // 1. 当您未在平台上注册的应用程序的包名与签名时；
//            // 2. 当您注册的应用程序包名与签名不正确时；
//            // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
//             String code = bundle.getString("code");
//             Log.e("微博权限获取错误返回",code);
//        }
//    }
//
//
//
//    //获取用户信息的回调
//    private RequestListener mListener = new RequestListener() {
//        @Override
//        public void onComplete(String response) {
//            if (!TextUtils. isEmpty(response)) {
//                // 调用 User#parse 将JSON串解析成User对象，所有的用户信息全部在这里面
//                User user = User.parse(response);
//                thirdUser.setNickName(user.name); // 昵称
//                thirdUser.setIcon(user.avatar_hd); // 头像
//                thirdUser.setUserSex(user.gender.equals( "m") ? "男" : "女" );
//                thirdUser.setCity(user.location);
//                thirdUser.setType("weibo");
//                ThirdUserVerify. verifyUser(thirdUser);
//            }
//        }
//
//        @Override
//        public void onWeiboException(WeiboException e) {
//
//        }
//    };
//}
