package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.UserEntity;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.LinkedHashMap;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 新用户 设置密码
 * Created by amoldZhang on 2018/1/16.
 */
public class SettingUserNewPasswordActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.et_pwd)
    EditText etPwd;
    @Bind(R.id.pass_word_view)
    LinearLayout passWordView;
    @Bind(R.id.et_referee)
    EditText etReferee;
    @Bind(R.id.referee_view)
    LinearLayout refereeView;

    @Bind(R.id.iv_eye)
    ImageView ivEye;

    private boolean showPwd = false;//密码是否可见
    private String type;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_setting_user_new_password);
    }

    @Override
    protected void initTitle() {
        type = getIntent().getStringExtra("type");
        if ("0".equals(type)) {
            title.setText("设置新密码");
            passWordView.setVisibility(View.VISIBLE);
            refereeView.setVisibility(View.GONE);
        } else {
            title.setText("添加推荐人");
            refereeView.setVisibility(View.VISIBLE);
            passWordView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void getData() {

    }


    @OnClick({R.id.title_bar_left, R.id.iv_eye, R.id.next_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
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
            case R.id.next_btn:
                if ("0".equals(type)){
                    if (StringUtils.isEmpty(etPwd.getText())) {
                        displayToast(getString(R.string.input_pwd));
                        return;
                    }
                    String fist = etPwd.getText().toString().trim();
                    if (fist.length() < 6) {
                        displayToast(getString(R.string.pwd_too_short));
                    }
                    else if(!BaseUtils.ifChina(fist)){
                        displayToast(getString(R.string.illeg_pwd));
                    }
                    else {
                        setSubmit();
                    }
                }else{
                    if (TextUtils.isEmpty(etReferee.getText())) {
                        displayToast(getString(R.string.empty_phone));
                        return;
                    } else if (BaseUtils.isMobileNO(etReferee.getText().toString().trim()) != true) {
                        displayToast(getString(R.string.invalid_phone));
                        return;
                    } else {
                        Intent intent = new Intent(this, FirstSetupPersonalInformationActivity.class);
                        intent.putExtra("recommend", etReferee.getText().toString().trim());
                        setResult(PersonUpDataActivity.PERSON_HOME, intent);
                        finish();
                    }
                }
                break;
        }
    }

    private void setSubmit() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            String mobile = SharedTools.getStringValue(this, "user_mobile", "-1");
            params.put("phoneNumber", mobile);
            params.put("password", etPwd.getText().toString().trim());
            new DataUtils(this, params).getUserModifyPassword(new DataUtils.DataBack<UserEntity>() {
                @Override
                public void getData(UserEntity dataMemager) {
                    try {
                        if (dataMemager == null) {
                            return;
                        }
                        displayToast(getString(R.string.pwd_modify_success));
                        setResult(RESULT_OK);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
