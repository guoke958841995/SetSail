package com.sxhalo.PullCoal.activity;

import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.MyAppLication;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.Dictionary;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.model.ThirdUserInfo;
import com.sxhalo.PullCoal.model.UserEntity;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.tools.SMSSdkTools;
import com.sxhalo.PullCoal.ui.ColaProgress;
import com.sxhalo.PullCoal.ui.daiglog.RLAlertDialog;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;
import com.sxhalo.PullCoal.wxapi.ThirdUserVerify;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 提现界面
 * Created by liz on 2018/3/15.
 */

public class WithdrawActivity extends BaseActivity implements SMSSdkTools.CoalBack {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.tv_bind)
    TextView tvBind;
    @Bind(R.id.tv_balance)
    TextView tvBalance;
    @Bind(R.id.fl_verification_code)
    FrameLayout frameLayoutCode;
    @Bind(R.id.et_money)
    EditText etMoney;//提现金额输入框
    @Bind(R.id.verification_code_btn)
    Button verificationCodeBtn;//验证码按钮
    @Bind(R.id.verification_code_et)
    EditText verificationCodeET;//验证码输入框
    @Bind(R.id.layout_bind_wechat)
    RelativeLayout layoutBind;

    /**
     * 时间计时器
     */
    private TimeCount timeCount;
    private SMSSdkTools smsSDK;
    private boolean flage; //当前验证码是否失效

    private boolean isBind = false;//是否已经授权微信登录
    private ColaProgress mColaProgress;
    private UserEntity userEntity;

    private int money;//用户余额 默认是分
    private int inputMoney;//用户输入的提现金额 默认是分
    private int withdrawQuota;//提现最大额度

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_withdraw);
    }

    @Override
    protected void initTitle() {
        try{
            title.setText("提现");
//            Dictionary sys100009 = OrmLiteDBUtil.getQueryByWhere(Dictionary.class, "dictionary_data_type", new String[]{"sys100009"}).get(0);
//            for(int i = 0; i < sys100009.list.size(); i++) {
//                if ("withdraw_quota".equals(sys100009.list.get(i).dictCode)) {
//                    withdrawQuota = Integer.valueOf(sys100009.list.get(i).dictValue)/100;
//                    break;
//                }
//            }
            userEntity = (UserEntity) getIntent().getSerializableExtra("UserEntity");
            if ("0".equals(userEntity.getWithdrawQuota())){
                etMoney.setHint("今日提现额度已用完");
            }else{
                withdrawQuota = Integer.valueOf(userEntity.getWithdrawQuota())/100;
                etMoney.setHint("请输入提现金额(提现额度为1-" + withdrawQuota + ")");
            }
            initListener();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initListener() {
        etMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //删除“.”后面超过2位后的数据
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        etMoney.setText(s);
                        etMoney.setSelection(s.length()); //光标移到最后
                    }
                }

                //如果"."在起始位置,则起始位置自动补0
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    etMoney.setText(s);
                    etMoney.setSelection(2);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });
    }

    @Override
    protected void getData() {
        if (SharedTools.getBooleanValue(this, "isWXBind", false)) {
            //已绑定微信
            tvBind.setText("已绑定");
            isBind = true;
            tvBind.setTextColor(getResources().getColor(R.color.actionsheet_red));
        } else {
            //未绑定微信
            tvBind.setText("绑定微信");
            tvBind.setTextColor(getResources().getColor(R.color.actionsheet_gray));
        }
        smsSDK = new SMSSdkTools(this);
        smsSDK.setCoalBack(this);
        tvBalance.setText(StringUtils.setNumLenth(Float.valueOf(userEntity.getPocketAmount())/100, 2));
        money = Integer.valueOf(userEntity.getPocketAmount());
    }

    @OnClick({R.id.title_bar_left, R.id.layout_bind_wechat, R.id.verification_code_btn, R.id.btn_top_up})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.layout_bind_wechat:
                //微信授权登录
                if (!isBind) {
                    //未绑定微信
                    if (BaseUtils.isNetworkConnected(this)) {
                        if (isWeinxinAvilible()) {
                            weChatLogin();
                        }
                    } else {
                        displayToast(getString(R.string.unable_net_work));
                    }
                } else {
                    displayToast("已经绑定过微信！");
                }
                break;
            case R.id.verification_code_btn:
                // 获取验证码
                showDialog();
                break;
            case R.id.btn_top_up:
                //确认提现
                if (!isBind) {
                    displayToast(getString(R.string.please_bind_wechat));
                    return;
                }
                if (TextUtils.isEmpty(verificationCodeET.getText())) {
                    displayToast(getString(R.string.empty_code));
                    return;
                }
                if (StringUtils.isEmpty(money)) {
                    displayToast(getString(R.string.withdraw_money_tips));
                    return;
                }
                String moneyString = etMoney.getText().toString().trim();
                if (StringUtils.isEmpty(moneyString)) {
                    displayToast(getString(R.string.withdraw_money_tips));
                    return;
                }else{
                    if (withdrawQuota == 0){
                        displayToast("今日提现额度已用完");
                        return;
                    }
                    String arr[] = (new BigDecimal(moneyString).floatValue()*100 + "").split("\\.");
                    inputMoney = Integer.valueOf(arr[0]);
                    if (inputMoney < 100) {
                        //输入金额小于1元 提示用户输入正确的金额
                        displayToast(getString(R.string.withdraw_money_min));
                        return;
                    }

                    if (inputMoney > withdrawQuota*100) {
                        //输入金额大于系统限制金额 提示用户输入正确的金额
                        String string = getResources().getString(R.string.withdraw_money_max);
                        String withdrawMoneyMax = String.format(string,withdrawQuota + "");
                        displayToast(withdrawMoneyMax);
                        return;
                    }

                    if (inputMoney > money) {
                        //输入金额大于余额 提示用户输入正确的金额
                        displayToast(getString(R.string.no_money_tips));
                        return;
                    }

                    //校验验证码是否和手机号匹配
                    checkCode();
                }
                break;
        }
    }

    private void showDialog() {
        new RLAlertDialog(this, "确认手机号码","短信验证码将发送到：" + StringUtils.setPhoneNumber(userEntity.getUserMobile())+ " 请注意查收！", "取消",
                "确定", new RLAlertDialog.Listener() {
            @Override
            public void onLeftClick() {
            }

            @Override
            public void onRightClick() {
                getPhoneCode();
            }
        }).show();
    }

    /**
     * 验校用户输入的验证码
     */
    private void checkCode() {
        try {
            smsSDK.setInputCode(userEntity.getUserMobile(), verificationCodeET.getText().toString().trim(), flage);
        } catch (Exception e) {
            GHLog.e("用户验校验证码", e.toString());
        }
    }

    /**
     * 获取验证码
     */
    public void getPhoneCode() {
        try {
            //请求获取短信验证码，在监听中返回
            smsSDK.getCode(userEntity.getUserMobile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getData(String country, String phone) {
        if (!country.equals("+86")) {
            //获取到验证码 启动计时器
            timeCount = new TimeCount(60000, 1000);
            timeCount.start();
            verificationCodeBtn.setEnabled(false);
        } else {
            getWithdraw();
        }
    }

    @Override
    public void getEorr(String showText) {
        displayToast(showText);
    }

    /**
     * 调提现接口
     */
    private void getWithdraw() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("amount", inputMoney + ""); //提现金额
            new DataUtils(this, params).getUserWithdrawRecord(new DataUtils.DataBack<String>() {
                @Override
                public void getData(String message) {
                    try {
                        if (message == null) {
                            return;
                        }
                        displayToast(message);
                        setResult(RESULT_OK);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void getError(Throwable e) {
                    if ("2060018".equals(e.getMessage())) {
                        if ("余额不足".equals(e.getCause().getMessage())){
                            displayToast("系统繁忙，请稍候再试！");
                        }
                    } else {
                        displayToast("系统繁忙，请稍候再试！");
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e("联网错误", e.toString());
        }
    }

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {//计时完毕时触发
            try {
                if (verificationCodeBtn != null) {
                    verificationCodeBtn.setText("获取验证码");
                    verificationCodeBtn.setEnabled(true);
                    verificationCodeBtn.setBackgroundResource(R.drawable.button_shape);
                    if (timeCount != null) {
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
                if (verificationCodeBtn != null) {
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
     * 判断是否安装微信
     *
     * @return
     */
    public boolean isWeinxinAvilible() {
        PackageManager packageManager = getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        displayToast(getString(R.string.uninstalled_wechat));
        return false;
    }

    /**
     * weChat第三方登陆
     */
    private void weChatLogin() {
        mColaProgress = ColaProgress.show(this, getString(R.string.authorization_wechat), false, false,
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                    }
                });
        new ThirdUserVerify().getThirdUserData(new ThirdUserVerify.ThirdUserData() {
            @Override
            public <T> void setThirdUserData(T t) {
                ThirdUserInfo userInfo = (ThirdUserInfo) t;
                switch (userInfo.getErrCode()) {
                    case ThirdUserInfo.ErrCode.ERR_OK:
                        checkIfBind(userInfo);
                        break;
                    default:
                        displayToast(userInfo.getErrText());
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
     *
     * @param thirdUser
     */
    public void checkIfBind(final ThirdUserInfo thirdUser) {
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("openId", thirdUser.getThirdID() != null ? thirdUser.getThirdID().toString() : "");
        params.put("thirdType", thirdUser.getType() != null ? thirdUser.getType().toString() : "");
        new DataUtils(this, params).getUserThirdPartyLogin(new DataUtils.DataBack<UserEntity>() {
            @Override
            public void getData(UserEntity dataMemager) {
                try {
                    if (dataMemager == null) {
                        return;
                    }
                    if (!StringUtils.isEmpty(dataMemager.getUserMobile())) {  //已经注册并绑定手机号
                        displayToast("当前登录微信已和其他账号绑定，请使用新的微信进行提现！");
                    } else {  //未绑定手机调用
                        bindPhone(thirdUser.getThirdID());
                    }
                } catch (Exception e) {
                    GHLog.e("第三方登录联网成功返回", e.toString());
                }
            }
        });
    }

    /**
     * 第三方登录网络链接注册
     */
    private void bindPhone(final String openId) {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("phoneNumber", userEntity.getUserMobile());
            params.put("openId", openId);
            params.put("nickname", StringUtils.isEmpty(userEntity.getNickname()) ? "nickname" : userEntity.getNickname());
            params.put("thirdType", "wechat");
            params.put("imageUrl", StringUtils.isEmpty(userEntity.getHeadPic()) ? "imageUrl" : userEntity.getHeadPic());
            new DataUtils(this, params).getUserThirdPartyBinding(new DataUtils.DataBack<UserEntity>() {
                @Override
                public void getData(UserEntity dataMemager) {
                    if (dataMemager == null) {
                        return;
                    }
                    SharedTools.putBooleanValue(WithdrawActivity.this, "isWXBind", true);
                    isBind = true;
                    tvBind.setText("已绑定");
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

    @Override
    public void onResume() {
        super.onResume();
        if (mColaProgress != null && mColaProgress.isShowing()) {
            mColaProgress.dismiss();
        }
    }

}
