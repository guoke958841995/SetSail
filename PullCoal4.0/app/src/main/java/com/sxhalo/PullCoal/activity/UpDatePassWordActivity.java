package com.sxhalo.PullCoal.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.UserEntity;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.tools.CallBack;
import com.sxhalo.PullCoal.tools.jpush.SettingJPush;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.LinkedHashMap;

import butterknife.Bind;
import butterknife.OnClick;

import static com.sxhalo.PullCoal.common.MyAppLication.getAlias;

/**
 * Created by amoldZhang on 2017/1/10.
 */
public class UpDatePassWordActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.passWord_et)
    EditText passWordEt;
    @Bind(R.id.second_passWord_et)
    EditText secondPassWordEt;


    private int Type;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_updata_pass_word);
    }

    @Override
    protected void initTitle() {
        Type  =  getIntent().getIntExtra("Type",-1);
        switch (Type){
            case 0:
                title.setText("设置密码");
                break;
            case 1:
                title.setText("忘记密码");
                break;
        }
    }

    @Override
    protected void getData() {

    }


    @OnClick({R.id.title_bar_left, R.id.next_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.next_btn:
                //判断网络
                if(BaseUtils.isNetworkConnected(getApplicationContext())){
                    gotoNext();
                }else {
                    displayToast(getString(R.string.unable_net_work));
                }
                break;
        }
    }

    /**
     * 点击设置修改密码
     */
    private void gotoNext() {
        if (StringUtils.isEmpty(passWordEt.getText())) {
            displayToast(getString(R.string.input_pwd));
            return;
        }
        if (StringUtils.isEmpty(secondPassWordEt.getText())) {
            displayToast(getString(R.string.input_pwd_again));
            return;
        }
        String fist = passWordEt.getText().toString().trim();
        Log.i("新密码", fist);
        String second = secondPassWordEt.getText().toString().trim();

        if (fist.equals(second)) {
            if (fist.length() < 6) {
                displayToast(getString(R.string.pwd_too_short));
            }else if(!BaseUtils.ifChina(second)){
                displayToast(getString(R.string.illeg_pwd));
            }else{
                if (Type == 1){  //忘记密码
                    submitPassword ();
                }else{  //注册
                    setUserPassWord(second);
                }
            }
        } else {
            displayToast(getString(R.string.pwd_twice_different));
            return;
        }
    }

    /**
     * 忘记密码
     */
    private void submitPassword () {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("phoneNumber", getIntent().getStringExtra("phone"));
            params.put("password", secondPassWordEt.getText().toString().trim());
            new DataUtils(this, params).getUserModifyPassword(new DataUtils.DataBack<UserEntity>() {
                @Override
                public void getData(UserEntity dataMemager) {
                    try {
                        if (dataMemager == null) {
                            return;
                        }
                        UIHelper.saveUserData(getApplicationContext(),dataMemager);
                        displayToast("修改成功！");
                        if (CallBack.callback != null){
                            CallBack.callback.onCallBack();
                            finish();
                        }
                    } catch (Exception e) {
                        GHLog.e(getClass().getName(),e.toString());
                        MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                    }
                }

                @Override
                public void getError(Throwable e) {
                    if ("2030007".equals(e.getMessage())){
                        displayToast("此用户不存在！");
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e(getClass().getName(),e.toString());
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
        }
    }

    /**
     * 网络链接
     *   用户注册密码
     */
    private void setUserPassWord(String passWord){
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("phoneNumber",getIntent().getStringExtra("phone"));  //手机号
            params.put("password",passWord);
            String recommend = getIntent().getStringExtra("recommend") == null ? "":getIntent().getStringExtra("recommend");
            if (!StringUtils.isEmpty(recommend)){
                params.put("recommend",recommend);
            }
            new DataUtils(this,params).getUserRegister(new DataUtils.DataBack<UserEntity>() {
                @Override
                public void getData(UserEntity dataMemager) {
                    try {
                        if (dataMemager == null) {
                            return;
                        }
                        UIHelper.saveUserData(UpDatePassWordActivity.this,dataMemager);
                        setAlias(dataMemager.getUserId());
                        if (CallBack.callback != null){
                            CallBack.callback.onCallBack();
                            finish();
                        }
                    } catch (Exception e) {
                        GHLog.e(getClass().getName(),e.toString());
                        MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                    }
                }
            });
        } catch (Exception e) {
            GHLog.e(getClass().getName(),e.toString());
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
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
}
