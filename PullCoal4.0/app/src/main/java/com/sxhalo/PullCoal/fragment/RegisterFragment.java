package com.sxhalo.PullCoal.fragment;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;
import com.sxhalo.PullCoal.wxapi.BaseUiListener;
import com.sxhalo.PullCoal.wxapi.ThirdUserVerify;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;


import java.util.LinkedHashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sxhalo.PullCoal.common.MyAppLication.getAlias;

/**
 * 注册界面
 * Created by amoldZhang on 2017/1/7.
 */
public class RegisterFragment extends Fragment implements SMSSdkTools.CoalBack{

    @Bind(R.id.phoneNember_et)
    EditText phoneNemberEt;
    @Bind(R.id.et_pwd)
    EditText etPwd;
    @Bind(R.id.verification_code_et)
    EditText verificationCodeEt;
    @Bind(R.id.verification_code_btn)
    Button verificationCodeBtn;
    @Bind(R.id.next_btn)
    Button nextBtn;
    @Bind(R.id.checkbox)
    CheckBox checkBox;
    @Bind(R.id.register_user_agreement)
    TextView tvAgreement;

    @Bind(R.id.iv_eye)
    ImageView ivEye;



    /**
     * 时间计时器
     */
    private TimeCount timeCount;
    private Activity myActivity;
    private SMSSdkTools smsSDK;
    private boolean flage; //当前验证码是否失效
    private String TAG_NAME = "注册界面";
    private boolean showPwd = false;//密码是否可见
    private ColaProgress mColaProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mBaseView = inflater.inflate(R.layout.frament_register, container, false);
        ButterKnife.bind(this, mBaseView);
        return mBaseView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            myActivity = getActivity();
            initAgreementText();
            smsSDK = new SMSSdkTools(myActivity);
            smsSDK.setCoalBack(this);
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
    public void onResume() {
        super.onResume();
        if (mColaProgress != null && mColaProgress.isShowing()) {
            mColaProgress.dismiss();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.verification_code_btn, R.id.next_btn, R.id.iv_eye, R.id.iv_qq, R.id.iv_wechat, R.id.register_user_agreement, R.id.checkbox})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.verification_code_btn://获取验证码按钮
                getPhoneCode();
                break;
            case R.id.next_btn:
                gotoNext();
                break;
            case R.id.iv_eye:
                String pwd = etPwd.getText().toString().trim();
                if (!showPwd && !StringUtils.isEmpty(pwd)) {
                    showPwd = true;
                    etPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ivEye.setImageResource(R.mipmap.icon_open);
                } else {
                    showPwd = false;
                    etPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ivEye.setImageResource(R.mipmap.icon_down);
                }
                etPwd.setSelection(pwd.length());
                break;
            case R.id.iv_qq:
                if(BaseUtils.isNetworkConnected(myActivity)){
                    if (isQQAvilible()) {
                        QQlogin();
                    }
                }else{
                    showText(getString(R.string.unable_net_work));
                }
                break;
            case R.id.iv_wechat:
                if(BaseUtils.isNetworkConnected(myActivity)){
                    if (isWeinxinAvilible()) {
                        weChatLogin();
                    }
                }else{
                    showText(getString(R.string.unable_net_work));
                }
                break;
            case R.id.register_user_agreement:
                gotoUserAgreement();
                break;
            case R.id.checkbox:
                if (checkBox.isChecked()) {
                    nextBtn.setClickable(true);
                    nextBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_shape));
                } else {
                    nextBtn.setClickable(false);
                    nextBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_shape_gray));
                }
                break;
        }
    }


    /**
     * 获取验证码
     */
    public void getPhoneCode() {
        if (TextUtils.isEmpty(phoneNemberEt.getText())) {
            showText(getString(R.string.empty_phone));
            return;
        } else if (BaseUtils.isMobileNO(phoneNemberEt.getText().toString().trim()) != true) {
            showText(getString(R.string.invalid_phone));
            return;
        } else {
            try {
//                //请求获取短信验证码，在监听中返回
                smsSDK.getCode(phoneNemberEt.getText().toString().trim());
            } catch (Exception e) {
                showText(getString(R.string.invalid_phone));
            }
        }
    }

    /**
     * 点击击下一步执行
     */
    private void gotoNext() {
        if (TextUtils.isEmpty(phoneNemberEt.getText())) {
            showText(getString(R.string.empty_phone));
            return;
        } else if (BaseUtils.isMobileNO(phoneNemberEt.getText().toString().trim()) != true) {
            showText(getString(R.string.invalid_phone));
            return;
        } else {
            if (StringUtils.isEmpty(etPwd.getText())) {
                ((BaseActivity)myActivity).displayToast(getString(R.string.input_pwd));
                return;
            }
            String fist = etPwd.getText().toString().trim();
            if (fist.length() < 6) {
                ((BaseActivity)myActivity).displayToast(getString(R.string.pwd_too_short));
            }
            else if(!BaseUtils.ifChina(fist)){
                ((BaseActivity)myActivity).displayToast(getString(R.string.illeg_pwd));
            }
            else{
                if (TextUtils.isEmpty(verificationCodeEt.getText())) {
                    showText(getString(R.string.empty_code));
                    return;
                } else {
                    try {
                        checkCode();
                    } catch (Exception e) {
                        GHLog.e(TAG_NAME, e.toString());
                    }
                }
            }
        }
    }

    /**
     *
     * @param codeNum +86 获取验证码  手机号时 校验验证码成功
     * @param phone
     */
    @Override
    public void getData(String codeNum, String phone) {
        if (!codeNum.equals("+86")){ //验证码获取成功
            //启动计时器
            timeCount = new TimeCount(60000, 1000);
            timeCount.start();
            verificationCodeBtn.setEnabled(false);
        }else{  //验证码校验成功
            afterSubmit(phone);
        }
    }

    @Override
    public void getEorr(String showText) {
        showText(showText);
    }

    private void checkCode() {
        smsSDK.setInputCode(phoneNemberEt.getText().toString().trim(), verificationCodeEt.getText().toString().trim(),flage);
    }

    protected void afterSubmit(String phone) {
        try {
            setUserPassWord(etPwd.getText().toString().trim(),phone);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 网络链接
     *   用户注册密码
     */
    private void setUserPassWord(final String passWord,final String phone){
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("phoneNumber",phone);  //手机号
            params.put("password",passWord);
            new DataUtils(myActivity,params).getUserRegister(new DataUtils.DataBack<UserEntity>() {
                @Override
                public void getData(UserEntity dataMemager) {
                    try {
                        if (dataMemager == null) {
                            return;
                        }
                        UIHelper.saveUserData(myActivity,dataMemager);

                        setAlias(dataMemager.getUserId());

                        Intent intent = new Intent(myActivity, FirstSetupPersonalInformationActivity.class);
                        intent.putExtra("registerType","0");// 0 注册 1 第三方登录时设置密码
                        startActivity(intent);

                        timeCount.onFinish();
                        myActivity.finish();

//                        new CallBack().onCallBack(new CallBack.Callback() {
//                            @Override
//                            public void onCallBack() {
//                                myActivity.finish();
//                            }
//                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void getError(Throwable e) {
                    if ("1030001".equals(e.getMessage())){

                    }else{
                        showText("服务器异常，请稍候再试！");
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
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
                        showText(userInfo.getErrText());
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

    /**
     * 执行第三方后台登陆或查询
     * @param thirdUser
     */
    public void loginFromWeiboAndQQ(final ThirdUserInfo thirdUser) {
        try {
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
//
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
        } catch (Exception e) {
            e.printStackTrace();
        }
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
     * 极光注册
     */
    private void setAlias(String userId) {
        String alias = userId;
        SettingJPush jspush = getAlias();
        jspush.setAlias(alias);
        sendLoginBroadcast();
    }
    /**
     * 用户登录成功 发送广播 刷新相关页面
     */
    private void sendLoginBroadcast() {
        Intent intent = new Intent(Constant.REFRESH_CODE + "");
        myActivity.sendBroadcast(intent);
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

    private Callback callback;
    //实现数据传递
    public void onCallBack(Callback callback) {
        this.callback = callback;
    }
    //创建接口
    public interface Callback {
        void onCallBack();
    }

    /**
     * Toase 提示
     *
     * @param text
     */
    private void showText(String text) {
        ((RegisterAddLoginActivity) myActivity).displayToast(text);
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
}
