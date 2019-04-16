package com.sxhalo.PullCoal.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.BaseActivity;
import com.sxhalo.PullCoal.activity.FirstSetupPersonalInformationActivity;
import com.sxhalo.PullCoal.activity.ForgetPassWordActivity;
import com.sxhalo.PullCoal.activity.RegisterAddLoginActivity;
import com.sxhalo.PullCoal.activity.WebViewActivity;
import com.sxhalo.PullCoal.common.MyAppLication;
import com.sxhalo.PullCoal.common.base.Config;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.model.ThirdUserInfo;
import com.sxhalo.PullCoal.model.UserEntity;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.tools.CallBack;
import com.sxhalo.PullCoal.tools.SMSSdkTools;
import com.sxhalo.PullCoal.tools.jpush.SettingJPush;
import com.sxhalo.PullCoal.ui.ColaProgress;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.DateUtil;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;
import com.sxhalo.PullCoal.wxapi.BaseUiListener;
import com.sxhalo.PullCoal.wxapi.ThirdUserVerify;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sxhalo.PullCoal.common.MyAppLication.getAlias;

/**
 * Created by amoldZhang on 2017/1/7.
 */
public class LoginFragment extends Fragment implements SMSSdkTools.CoalBack{

    @Bind(R.id.login_username)
    EditText loginUsername;
    @Bind(R.id.login_password)
    EditText loginPassword;
    @Bind(R.id.framelayout_pwd)
    FrameLayout frameLayoutPwd;//密码登录
    @Bind(R.id.framelayout_verification_code)
    FrameLayout frameLayoutVerificationCode;//验证码登录
    @Bind(R.id.verification_code_et)
    EditText verificationCodeET;//验证码输入框
    @Bind(R.id.verification_code_btn)
    Button verificationCodeBtn;//验证码按钮

    @Bind(R.id.tv_login_type)
    TextView tvLoginType;//默认是验证码登录  点击切换登录方式
    @Bind(R.id.forget_password)
    TextView forgetPassword;//忘记密码按钮
    @Bind(R.id.user_agreement_view)
    RelativeLayout userAgreementView;// 用户协议布局
    @Bind(R.id.checkbox)
    CheckBox checkBox;
    @Bind(R.id.register_user_agreement)
    TextView tvAgreement;
    @Bind(R.id.login_submit)
    Button loginSubmit;
    @Bind(R.id.iv_eye)
    ImageView ivEye;

    private boolean loginByPwd = false;  // true 账号密码登录 false 免密码登录

    private boolean showPwd = false;//密码是否可见
    /**
     * 当前活动界面
     */
    private Activity myActivity;
    /**
     * 时间计时器
     */
    private TimeCount timeCount;
    private SMSSdkTools smsSDK;
    private boolean flage; //当前验证码是否失效

    private ColaProgress mColaProgress;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mBaseView = inflater.inflate(R.layout.frament_login, container, false);
        ButterKnife.bind(this, mBaseView);
        return mBaseView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            myActivity = getActivity();
            smsSDK = new SMSSdkTools(myActivity);
            smsSDK.setCoalBack(this);
            initAgreementText();
            hintKbOne();
            if (loginByPwd) { //账号密码登录
                tvLoginType.setText("免密登录");
                frameLayoutPwd.setVisibility(View.VISIBLE);
                frameLayoutVerificationCode.setVisibility(View.GONE);

                forgetPassword.setVisibility(View.VISIBLE);
                userAgreementView.setVisibility(View.GONE);
            } else {  //免密码登录
                tvLoginType.setText("账号密码登录");
                frameLayoutPwd.setVisibility(View.GONE);
                frameLayoutVerificationCode.setVisibility(View.VISIBLE);

                forgetPassword.setVisibility(View.GONE);
                userAgreementView.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Log.i("登陆初始化", e.toString());
        }
    }

    private void initAgreementText() {

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(getString(R.string.register_show));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(myActivity, WebViewActivity.class);
                intent.putExtra("URL", new Config().getUSER_AGREEMENT());
                intent.putExtra("title", "用户协议");
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        spannableStringBuilder.setSpan(clickableSpan, 12, 28, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置点击字体的颜色
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.app_title_text_color));
        spannableStringBuilder.setSpan(colorSpan, 12, 28, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvAgreement.setText(spannableStringBuilder);
        tvAgreement.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.verification_code_btn,R.id.forget_password, R.id.login_submit, R.id.tv_login_type, R.id.iv_qq, R.id.iv_wechat, R.id.checkbox,R.id.register_user_agreement, R.id.iv_eye})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.verification_code_btn:// 获取验证码
                getPhoneCode();
                break;
            case R.id.forget_password:// 忘记密码按钮
                Intent intent = new Intent(getActivity(),ForgetPassWordActivity.class);
                intent.putExtra("Type","忘记密码");
                myActivity.startActivityForResult(intent, RegisterAddLoginActivity.FOUND_PWD);
                break;
            case R.id.iv_eye:
                String pwd = loginPassword.getText().toString().trim();
                if (!showPwd && !StringUtils.isEmpty(pwd)) {
                    showPwd = true;
                    loginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ivEye.setImageResource(R.mipmap.icon_open);
                } else {
                    showPwd = false;
                    loginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ivEye.setImageResource(R.mipmap.icon_down);
                }
                loginPassword.setSelection(pwd.length());
                break;
            case R.id.login_submit:// 登录按钮
                // 判断网络
                if (BaseUtils.isNetworkConnected(myActivity)) {
                    if (loginByPwd){
                        loginSubmit();
                    }else{
                        loginSubmitNoPassword();
                    }
                } else {
                    disShow(getString(R.string.unable_net_work));
                }
                break;
            case R.id.iv_qq:
                if(BaseUtils.isNetworkConnected(myActivity)){
                    if (isQQAvilible()) {
                        QQlogin();
                    }
                }else{
                    disShow(getString(R.string.unable_net_work));
                }
                break;
            case R.id.tv_login_type:
                loginByPwd = !loginByPwd;
                if (loginByPwd) {
                    tvLoginType.setText("免密登录");
                    frameLayoutPwd.setVisibility(View.VISIBLE);
                    frameLayoutVerificationCode.setVisibility(View.GONE);

                    forgetPassword.setVisibility(View.VISIBLE);
                    userAgreementView.setVisibility(View.GONE);
                } else {
                    tvLoginType.setText("账号密码登录");
                    frameLayoutPwd.setVisibility(View.GONE);
                    frameLayoutVerificationCode.setVisibility(View.VISIBLE);

                    forgetPassword.setVisibility(View.GONE);
                    userAgreementView.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.iv_wechat:
                if(BaseUtils.isNetworkConnected(myActivity)){
                    if (isWeinxinAvilible()) {
                        weChatLogin();
                    }
                }else{
                    disShow(getString(R.string.unable_net_work));
                }
                break;
            case R.id.checkbox:
                if (checkBox.isChecked()) {
                    loginSubmit.setClickable(true);
                    loginSubmit.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_shape));
                } else {
                    loginSubmit.setClickable(false);
                    loginSubmit.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_shape_gray));
                }
                break;
            case R.id.register_user_agreement:
                gotoUserAgreement();
                break;
        }
    }

    /**
     * 获取验证码
     */
    public void getPhoneCode() {
        if (TextUtils.isEmpty(loginUsername.getText())) {
            disShow(getString(R.string.empty_phone));
            return;
        } else if (BaseUtils.isMobileNO(loginUsername.getText().toString().trim()) != true) {
            disShow(getString(R.string.invalid_phone));
            return;
        } else {
            try {
//                //请求获取短信验证码，在监听中返回
                smsSDK.getCode(loginUsername.getText().toString().trim());
            } catch (Exception e) {
                disShow(getString(R.string.invalid_phone));
            }
        }
    }

    /**
     * 跳转用户协议
     */
    private void gotoUserAgreement() {
        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        intent.putExtra("URL", new Config().getUSER_AGREEMENT());
        intent.putExtra("title", "用户协议");
        startActivity(intent);
    }

    /**
     * 判断是否安装微信
     * @return
     */
    public boolean isWeinxinAvilible () {
        PackageManager packageManager = myActivity.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        ((BaseActivity)myActivity).displayToast(getString(R.string.uninstalled_wechat));
        return false;
    }
    /**
     * 判断是否安装QQ
     * @return
     */
    public boolean isQQAvilible () {
        PackageManager packageManager = myActivity.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        ((BaseActivity)myActivity).displayToast(getString(R.string.uninstalled_qq));
        return false;
    }

    /**
     * 登录操作
     */
    protected void loginSubmit() {
        if (TextUtils.isEmpty(loginUsername.getText())) {
            disShow(getString(R.string.empty_phone));
            return;
        }
        if (!BaseUtils.isMobileNO(loginUsername.getText().toString().trim())) {
            disShow(getString(R.string.invalid_phone));
            return;
        }
        if (TextUtils.isEmpty(loginPassword.getText())) {
            disShow(getString(R.string.empty_pwd));
            return;
        }
        String phone = loginUsername.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();
        Login(phone, password);
    }

    /**
     * 用户免密码登录
     */
    private void loginSubmitNoPassword() {
        if (TextUtils.isEmpty(loginUsername.getText())) {
            disShow(getString(R.string.empty_phone));
            return;
        } else if (BaseUtils.isMobileNO(loginUsername.getText().toString().trim()) != true) {
            disShow(getString(R.string.invalid_phone));
            return;
        } else {
            if (TextUtils.isEmpty(verificationCodeET.getText())) {
                disShow(getString(R.string.empty_code));
                return;
            } else {
//                afterSubmit("",loginUsername.getText().toString().trim());
                checkCode();
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mColaProgress != null && mColaProgress.isShowing()) {
            mColaProgress.dismiss();
        }
    }

    /**
     * qq第三方登陆
     */
    private void QQlogin(){
        mColaProgress = ColaProgress.show(myActivity, getString(R.string.authorization_qq), false, false,
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                    }
                });
        MyAppLication.getTencent().logout(myActivity);
        //如果session无效，就开始登录  
        if(!MyAppLication.getTencent().isSessionValid()){
            new ThirdUserVerify().getThirdUserData(new ThirdUserVerify.ThirdUserData() {
                @Override
                public <T> void setThirdUserData(T t) {
                    ThirdUserInfo userInfo = (ThirdUserInfo)t;
                    loginFromWeiboAndQQ(userInfo);
                }
            });
            // QQ发起登陆
            MyAppLication.getTencent().login(myActivity, "all", new BaseUiListener(myActivity,MyAppLication.getTencent()));
        }
    }

    /**
     * weChat第三方登陆
     */
    private void weChatLogin() {
        mColaProgress = ColaProgress.show(myActivity, getString(R.string.authorization_wechat), false, false,
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                    }
                });
        new ThirdUserVerify().getThirdUserData(new ThirdUserVerify.ThirdUserData() {
            @Override
            public <T> void setThirdUserData(T t) {
                ThirdUserInfo userInfo = (ThirdUserInfo)t;
                switch (userInfo.getErrCode()){
                    case ThirdUserInfo.ErrCode.ERR_OK:
                        loginFromWeiboAndQQ(userInfo);
                        break;
                    default:
                        disShow(userInfo.getErrText());
                        break;
                }
            }
        });
        MyAppLication.isWxLogin = true;
        IWXAPI api = MyAppLication.getIWXAPI();
        SendAuth.Req req = new SendAuth.Req();
        //授权读取用户信息
        req.scope = "snsapi_userinfo";
        //自定义信息
        req.state = "wechat_sdk_demo_test";
        //向微信发送请求
        api.sendReq(req);
    }
//    /**
//     * siNa第三方登陆
//     */
//    private void siNaLogin() {
//        ((RegisterAddLoginActivity)myActivity).mSsoHandler = new SsoHandler(myActivity,MyAppLication.getAuthInfo());
//        mColaProgress = ColaProgress.show(myActivity, getString(R.string.authorization_bolg), false, false,
//                new DialogInterface.OnCancelListener() {
//                    @Override
//                    public void onCancel(DialogInterface dialog) {
//                    }
//                });
//        new ThirdUserVerify().getThirdUserData(new ThirdUserVerify.ThirdUserData() {
//            @Override
//            public <T> void setThirdUserData(T t) {
//                ThirdUserInfo userInfo = (ThirdUserInfo)t;
//                switch (userInfo.getErrCode()){
//                    case ThirdUserInfo.ErrCode.ERR_OK:
//                        loginFromWeiboAndQQ(userInfo);
//                        Log.i("微博", "回掉成功");
//                        break;
//                    default:
//                        disShow(userInfo.getErrText());
//                        break;
//                }
//            }
//        });
//        ((RegisterAddLoginActivity)myActivity).mSsoHandler.authorize( new AuthListener(myActivity));
//    }

    /**
     * 执行第三方后台登陆或查询
     * @param thirdUser
     */
    public void loginFromWeiboAndQQ(final ThirdUserInfo thirdUser) {
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("openId", thirdUser.getThirdID()!= null?thirdUser.getThirdID().toString():"");
        params.put("thirdType", thirdUser.getType()!= null?thirdUser.getType().toString():"");
        new DataUtils(getActivity(),params).getUserThirdPartyLogin(new DataUtils.DataBack<UserEntity>() {
            @Override
            public void getData(UserEntity dataMemager) {
                try {
                    if (dataMemager == null) {
                        return;
                    }
                    if (!StringUtils.isEmpty(dataMemager.getUserMobile())){  //已经注册并绑定手机号
                        UIHelper.saveUserData(getActivity(),dataMemager);
                        setAlias(dataMemager.getUserId());
                        if (callback != null){
                            callback.onCallBack();
                        }
                    }else{  //未绑定手机调用
                        Intent intent = new Intent(myActivity,ForgetPassWordActivity.class);
                        intent.putExtra("Type","第三方注册");
                        intent.putExtra("ThirdUserInfo",thirdUser);
                        startActivity(intent);
                        new CallBack().onCallBack(new CallBack.Callback(){
                            @Override
                            public void onCallBack() {
                                myActivity.finish();
                            }
                        });
                    }
                } catch (Exception e) {
                    GHLog.e("第三方登录联网成功返回",e.toString());
                }
            }
        });
    }

    /**
     * 登陆联网
     * @param phone
     * @param password
     */
    private void Login(final String phone,final String password) {
        try {
            MyAppLication.ifLogin = 0;
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("phoneNumber",phone);  //手机号
            params.put("password",password);
            new DataUtils(getActivity(),params).getUserLogin(new DataUtils.DataBack<UserEntity>() {
                @Override
                public void getData(UserEntity dataMemager) {
                    try {
                        if (dataMemager == null) {
                            return;
                        }
                        UIHelper.saveUserData(getActivity(),dataMemager);
                        setAlias(dataMemager.getUserId());
                        if (callback != null){
                            callback.onCallBack();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void getError(Throwable e) {
                    if ("1030002".equals(e.getMessage())){
//                        disShow(e.getCause().getMessage()); //用户未注册
                    }else if ("1030004".equals(e.getMessage())){  //密码输入错误
                        disShow(getResources().getString(R.string.wrong_pwd_tips));
                    }else if ("1030003".equals(e.getMessage())){ //用户禁用

                    }else{
                        disShow("服务器繁忙请稍候再试！");
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e("联网错误", e.toString());
        }
    }

    /**
     * 用户登录成功 发送广播 刷新相关页面
     */
    private void sendLoginBroadcast() {
        Intent intent = new Intent(Constant.REFRESH_CODE + "");
        myActivity.sendBroadcast(intent);
    }


    /**
     * 极光注册
     */
    private void setAlias(String userId) {
        String alias = userId;
        SettingJPush jspush = getAlias();
        jspush.setAlias(alias);
        sendLoginBroadcast();
    }

    @Override
    public void getData(String country, String phone) {
        if (!country.equals("+86")){
            //启动计时器
            timeCount = new TimeCount(60000, 1000);
            timeCount.start();
            verificationCodeBtn.setEnabled(false);
        }else{
            afterSubmit("+86", phone);
        }
    }

    /**
     * 验校用户输入的验证码
     */
    private void checkCode() {
        try {
            smsSDK.setInputCode(loginUsername.getText().toString().trim(), verificationCodeET.getText().toString().trim(),flage);
        } catch (Exception e) {
            GHLog.e("用户验校验证码", e.toString());
        }
    }

    /**
     * 短信验证码验校成功
     * @param s
     * @param phone
     */
    private void afterSubmit(String s,final String phone) {
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("phoneNumber",phone);  //手机号
        new DataUtils(getActivity(),params).getUserAvoidLogin(new DataUtils.DataBack<UserEntity>() {
            @Override
            public void getData(UserEntity dataMemager) {
                try {
                    if (dataMemager == null) {
                        return;
                    }
                    UIHelper.saveUserData(getActivity(),dataMemager);
                    setAlias(dataMemager.getUserId());
                    long mTime = DateUtil.dateDiff(dataMemager.getCreateTime(),DateUtil.dateToStrLong(new Date()) + "","yyyy-MM-dd HH:mm:ss","m");
                    if (mTime < 5){
                        Intent intent = new Intent(myActivity, FirstSetupPersonalInformationActivity.class);
                        intent.putExtra("registerType","1"); // 0 注册 1 第三方登录时设置密码
                        startActivity(intent);
                    }
                    if (callback != null){
                        callback.onCallBack();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getError(Throwable e) {
                disShow("服务器异常，请稍候再试！");
            }
        });
    }

    @Override
    public void getEorr(String showText) {
        disShow(showText);
    }

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {//计时完毕时触发
            try {
                if (verificationCodeBtn != null){
                    verificationCodeBtn.setText("获取验证码");
                    verificationCodeBtn.setEnabled(true);
                    verificationCodeBtn.setBackgroundResource(R.drawable.button_shape);
                    if (timeCount != null){
                        timeCount.cancel();
                    }
                    flage = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程显示
            try {
                if (verificationCodeBtn != null){
                    verificationCodeBtn.setEnabled(false);
                    verificationCodeBtn.setBackgroundResource(R.drawable.button_shape_normal);
                    verificationCodeBtn.setText("验证码" + millisUntilFinished / 1000 + "秒");
                    flage = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 界面弹出通知显示框
     *
     * @param showText
     */
    private void disShow(String showText) {
        ((RegisterAddLoginActivity)myActivity).displayToast(showText);
    }

    private Callback callback;
    //实现数据传递
    public void onCallBack(Callback callback) {
        this.callback = callback;
    }
    //创建接口
    public interface Callback {
        void onCallBack();
    }

    //此方法，如果显示则隐藏，如果隐藏则显示
    private void hintKbOne() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//        // 得到InputMethodManager的实例
//        if (imm.isActive()) {
//            // 如果开启
//            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
//                    InputMethodManager.HIDE_NOT_ALWAYS);
//        }
    }

}
