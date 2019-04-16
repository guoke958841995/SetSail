package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.MyAppLication;
import com.sxhalo.PullCoal.common.base.Config;
import com.sxhalo.PullCoal.model.ThirdUserInfo;
import com.sxhalo.PullCoal.model.UserEntity;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.tools.CallBack;
import com.sxhalo.PullCoal.tools.SMSSdkTools;
import com.sxhalo.PullCoal.tools.image.CircleImageView;
import com.sxhalo.PullCoal.tools.jpush.SettingJPush;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.DateUtil;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.Date;
import java.util.LinkedHashMap;

import butterknife.Bind;
import butterknife.OnClick;

import static com.sxhalo.PullCoal.common.MyAppLication.getAlias;

/**
 * 忘记密码
 * Created by amoldZhang on 2017/1/9.
 */
public class ForgetPassWordActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.phoneNember_et)
    EditText phoneNemberEt;
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
    @Bind(R.id.framelayout_poi)
    FrameLayout framelayoutPoi;
    @Bind(R.id.iv_head)
    CircleImageView ivHead;
    @Bind(R.id.user_nike)
    TextView userNike;


    /**
     * 时间计时器
     */
    private TimeCount timeCount;
    private SMSSdkTools smsSDK;
    private String TAG_NAME = "";
    private boolean flage;  //当前验证码是否失效

    private String sendType;
    private ThirdUserInfo thirdUser;


    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_forget_pass_word);
    }

    @Override
    protected void initTitle() {
        TAG_NAME = getIntent().getStringExtra("Type");
    }

    @Override
    protected void getData() {
        smsSDK = new SMSSdkTools(this);
        try {
            initAgreementText();
            if (TAG_NAME.equals("忘记密码")) {
                title.setText(TAG_NAME);
                nextBtn.setText("下 一 步");
                sendType = "2";
                framelayoutPoi.setVisibility(View.VISIBLE);
                ivHead.setVisibility(View.GONE);
                userNike.setVisibility(View.GONE);
            } else if (TAG_NAME.equals("第三方注册")) {
                framelayoutPoi.setVisibility(View.GONE);
                ivHead.setVisibility(View.VISIBLE);
                userNike.setVisibility(View.VISIBLE);
                thirdUser =  (ThirdUserInfo)getIntent().getSerializableExtra("ThirdUserInfo");
                // qq，weibo，wechat
                if (thirdUser.getType().equals("qq")){
                    title.setText("QQ登录");
                }else if (thirdUser.getType().equals("wechat")){
                    title.setText("微信登录");
                }else {
                    title.setText("微博登陆");
                }
                String iconImageUrl = thirdUser.getIcon()!= null?thirdUser.getIcon().toString():"";
                if(StringUtils.isEmpty(iconImageUrl)){
                    ivHead.setImageResource(R.mipmap.icon_login);
                }else{
                    getImageManager().loadItemUrlImage(iconImageUrl,ivHead);//个人圆图加载
                }
                userNike.setText(thirdUser.getNickName());
                nextBtn.setText("确   定");
                sendType = "3";
            }
        } catch (Exception e) {
            GHLog.e(this.getPackageCodePath()+"界面初始化",e.toString());
        }
    }

    private void initAgreementText() {

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(getString(R.string.register_show));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(mContext, WebViewActivity.class);
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


    @OnClick({R.id.title_bar_left, R.id.verification_code_btn, R.id.next_btn, R.id.register_user_agreement, R.id.checkbox})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.verification_code_btn://获取验证码按钮
                getPhoneCode();
                break;
            case R.id.next_btn:
                gotoNext();
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
            displayToast("请输入手机号");
            return;
        } else if (BaseUtils.isMobileNO(phoneNemberEt.getText().toString().trim()) != true) {
            displayToast("请输入有效的手机号");
            return;
        } else {
            //账号验校
            Login(phoneNemberEt.getText().toString().trim(),"lameibao");
        }
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
            new DataUtils(this,params).getUserLogin(new DataUtils.DataBack<UserEntity>() {
                @Override
                public void getData(UserEntity dataMemager) {
                    try {
                        if (dataMemager == null) {
                            return;
                        }
                        getCode();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void getError(Throwable e) {
                    if ("1030002".equals(e.getMessage())){
//                        disShow(e.getCause().getMessage()); //用户未注册
                    }else if ("1030004".equals(e.getMessage())){  //密码输入错误
                        getCode();
                    }else if ("1030003".equals(e.getMessage())){ //用户禁用

                    }else{
                        displayToast("服务器繁忙请稍候再试！");
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e("联网错误", e.toString());
        }
    }

    private void getCode(){
        try {
            //请求获取短信验证码，在监听中返回
            smsSDK.getCode(phoneNemberEt.getText().toString().trim());
            smsSDK.setCoalBack(new SMSSdkTools.CoalBack() {
                @Override
                public void getData(String country, String phone) {
                    //启动计时器
                    timeCount = new TimeCount(60000, 1000);
                    timeCount.start();
                    verificationCodeBtn.setEnabled(false);
                }

                @Override
                public void getEorr(String showText) {
                    displayToast(showText);
                }
            });
        } catch (Exception e) {
            displayToast("请输入有效的手机号");
        }
    }


    /**
     * 点击击下一步执行
     */
    private void gotoNext() {
        if (TextUtils.isEmpty(phoneNemberEt.getText())) {
            displayToast("请输入手机号");
            return;
        } else if (BaseUtils.isMobileNO(phoneNemberEt.getText().toString().trim()) != true) {
            displayToast("请输入有效的手机号");
            return;
        } else {
            if (TextUtils.isEmpty(verificationCodeEt.getText())) {
                displayToast("请输入验证码");
                return;
            } else {
                try {
                    //提交短信验证码，在监听中返回
                    smsSDK.setCoalBack(new SMSSdkTools.CoalBack() {
                        @Override
                        public void getData(String country, String phoneName) {
                            afterSubmit(country, phoneName);
                        }

                        @Override
                        public void getEorr(String showText) {
                            displayToast(showText);
                        }
                    });
                    smsSDK.setInputCode(phoneNemberEt.getText().toString().trim(), verificationCodeEt.getText().toString().trim(),flage);
                } catch (Exception e) {
                    GHLog.e(TAG_NAME, e.toString());
                }
            }
        }
    }

    /**
     * 短信验证成功操作
     *
     * @param country
     * @param phone
     */
    protected void afterSubmit(String country, String phone) {
        try {
            if (TAG_NAME.equals("忘记密码")) {
                Intent intent = new Intent(this, UpDatePassWordActivity.class);
                intent.putExtra("country", country);
                intent.putExtra("phone", phone);
                intent.putExtra("Type", 1);
                startActivity(intent);
                new CallBack().onCallBack(new CallBack.Callback() {
                    @Override
                    public void onCallBack() {
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            } else if (TAG_NAME.equals("第三方注册")) {
                setUserPassWord(phone);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 第三方登录网络链接注册
     */
    private void setUserPassWord(String phone) {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("phoneNumber", phone);
            params.put("openId", thirdUser.getThirdID()!= null?thirdUser.getThirdID().toString():"");
            params.put("nickname", thirdUser.getNickName()!= null?thirdUser.getNickName().toString():"");
            params.put("thirdType", thirdUser.getType()!= null?thirdUser.getType().toString():"");
            params.put("imageUrl", thirdUser.getIcon()!= null?thirdUser.getIcon().toString():"imageUrl");

            new DataUtils(this,params).getUserThirdPartyBinding(new DataUtils.DataBack<UserEntity>() {
                @Override
                public void getData(UserEntity dataMemager) {
                    if(dataMemager == null){
                        return;
                    }
                    displayToast("登录成功");
                    UIHelper.saveUserData(ForgetPassWordActivity.this,dataMemager);
                    setAlias(dataMemager.getUserId());

                    long mTime = DateUtil.dateDiff(dataMemager.getCreateTime(),DateUtil.dateToStrLong(new Date()) + "","yyyy-MM-dd HH:mm:ss","m");
                    if (mTime < 5){
                        Intent intent = new Intent(ForgetPassWordActivity.this, FirstSetupPersonalInformationActivity.class);
                        intent.putExtra("registerType","1"); // 0 注册 1 第三方登录时设置密码
                        startActivity(intent);
                    }

                    if (CallBack.callback != null) {
                        CallBack.callback.onCallBack();
                        finish();
                    }
                }

                @Override
                public void getError(Throwable e) {
                    displayToast("服务器异常，请稍候再试！");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 极光注册
     */
    private void setAlias(String userId) {
        String alias = userId;
        SettingJPush jspush = getAlias();
        jspush.setAlias(alias);
    }


    /**
     * 跳转用户协议
     */
    private void gotoUserAgreement() {
        Intent intent = new Intent(ForgetPassWordActivity.this, WebViewActivity.class);
        intent.putExtra("URL", new Config().getUSER_AGREEMENT());
        intent.putExtra("title", "用户协议");
        startActivity(intent);
    }


    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {//计时完毕时触发
            verificationCodeBtn.setText("获取验证码");
            verificationCodeBtn.setEnabled(true);
            verificationCodeBtn.setBackgroundResource(R.drawable.button_shape);
            timeCount.cancel();
            flage = true;
        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程显示
            verificationCodeBtn.setEnabled(false);
            verificationCodeBtn.setBackgroundResource(R.drawable.button_shape_normal);
            verificationCodeBtn.setText("验证码" + millisUntilFinished / 1000 + "秒");
            flage = false;
        }
    }

}
