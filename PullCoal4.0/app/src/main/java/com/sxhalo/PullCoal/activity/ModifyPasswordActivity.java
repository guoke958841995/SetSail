package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.UserEntity;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.tools.SMSSdkTools;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 修改密码
 * Created by amoldZhang on 2017/4/6.
 */
public class ModifyPasswordActivity extends BaseActivity implements SMSSdkTools.CoalBack{

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.next_btn)
    Button nextBtn;
    @Bind(R.id.new_first_passWord_et)
    TextView newFirstPassWordEt;
    @Bind(R.id.verification_code_et)
    EditText verificationCodeEt;
    @Bind(R.id.verification_code_btn)
    Button verificationCodeBtn;

    private UserEntity users;

    /**
     * 时间计时器
     */
    private TimeCount timeCount;
    private SMSSdkTools smsSDK;
    private boolean flage; //当前验证码是否失效

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_modify_password);
        smsSDK = new SMSSdkTools(this);
        smsSDK.setCoalBack(this);
    }

    @Override
    protected void initTitle() {
        title.setText("修改密码");
        String mobile = SharedTools.getStringValue(this, "user_mobile", "-1");
        newFirstPassWordEt.setText(mobile);
    }

    @Override
    protected void getData() {
        users = (UserEntity) getIntent().getSerializableExtra("Entity");
    }


    @OnClick({R.id.title_bar_left, R.id.next_btn,R.id.verification_code_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.next_btn:
                getEmptyData();
                break;
            case R.id.verification_code_btn:
                getPhoneCode();
                break;
        }
    }

    /**
     * 获取验证码
     */
    public void getPhoneCode() {
        if (TextUtils.isEmpty(newFirstPassWordEt.getText())) {
            displayToast(getString(R.string.empty_phone));
            return;
        } else if (BaseUtils.isMobileNO(newFirstPassWordEt.getText().toString().trim()) != true) {
            displayToast(getString(R.string.invalid_phone));
            return;
        } else {
            try {
//                //请求获取短信验证码，在监听中返回
                smsSDK.getCode(newFirstPassWordEt.getText().toString().trim());
            } catch (Exception e) {
                displayToast(getString(R.string.invalid_phone));
            }
        }
    }

    /**
     * 获取界面空件数据
     */
    private void getEmptyData() {
        if (TextUtils.isEmpty(newFirstPassWordEt.getText())) {
            displayToast(getString(R.string.empty_phone));
            return;
        } else if (BaseUtils.isMobileNO(newFirstPassWordEt.getText().toString().trim()) != true) {
            displayToast(getString(R.string.invalid_phone));
            return;
        } else {
            if (TextUtils.isEmpty(verificationCodeEt.getText())) {
                displayToast(getString(R.string.empty_code));
                return;
            } else {
                checkCode();
            }
        }
    }

    /**
     * 验校用户输入的验证码
     */
    private void checkCode() {
        try {
            smsSDK.setInputCode(newFirstPassWordEt.getText().toString().trim(), verificationCodeEt.getText().toString().trim(),flage);
        } catch (Exception e) {
            GHLog.e("用户验校验证码", e.toString());
        }
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
     * 短信验证码验校成功
     * @param s
     * @param phone
     */
    private void afterSubmit(String s,final String phone) {
        Intent intent1 = new Intent(this, SettingUserNewPasswordActivity.class);
        intent1.putExtra("type", "0");
        startActivityForResult(intent1, 0x01);
    }

    @Override
    public void getEorr(String showText) {
        displayToast(showText);
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
                GHLog.e(getClass().getName(),e.toString());
                MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
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
                GHLog.e(getClass().getName(),e.toString());
                MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0x01:
                    finish();
                    break;
            }
        }
    }
}
