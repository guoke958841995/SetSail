package com.sxhalo.PullCoal.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.APPData;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.ui.daiglog.RLAlertDialog;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.SoftHideKeyBoardUtil;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.LinkedHashMap;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 司机登记界面
 * Created by amoldZhang on 2017/4/28.
 */
public class DriverRegistrationActivity extends BaseActivity {
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.map_type)
    TextView mapTyp;

    @Bind(R.id.car_name)
    EditText carName;
    @Bind(R.id.car_tel)
    EditText carTel;
    @Bind(R.id.platenumber_et)
    EditText plateNumber;



    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_drivers_registration);
        SoftHideKeyBoardUtil.assistActivity(this);
    }

    @Override
    protected void initTitle() {
        title.setText("司机信息登记");
        mapTyp.setVisibility(View.VISIBLE);
        mapTyp.setText("提交");

        plateNumber.addTextChangedListener(new TextWatcher() {
            int index = 0;
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
            @SuppressLint("DefaultLocale")
            @Override
            public void afterTextChanged(Editable s) {
                plateNumber.removeTextChangedListener(this);// 解除文字改变事件
                index = plateNumber.getSelectionStart();// 获取光标位置
                String text = s.toString();
                plateNumber.setText(text.toUpperCase());// 转换
//                if (!text.equals("")) {
//                    next_btn.setEnabled(true);
//                    next_btn.setBackgroundResource(R.drawable.button_shape);
//                } else {
//                    next_btn.setEnabled(false);
//                    next_btn.setBackgroundResource(R.drawable.button_shape_normal);
//                }
                plateNumber.setSelection(index);// 重新设置光标位置
                plateNumber.addTextChangedListener(this);// 重新绑定事件
            }
        });
    }



    /**
     * 更新司机登记信息
     */
    @Override
    protected void getData() {
    }

    @OnClick({R.id.title_bar_left, R.id.map_type})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
//                getSuccess();
//                showDaiLog(this,"是否保存您登记的信息?");
                break;
            case R.id.map_type:
                getSuccess();
                break;
        }
    }


    private void showDaiLog(Activity mActivity, String message){
//        LayoutInflater inflater1 = mActivity.getLayoutInflater();
//        View layout = inflater1.inflate(R.layout.dialog_updata, null);
//        ((TextView)layout.findViewById(R.id.updata_message)).setText(TEXT);
        new RLAlertDialog(mActivity, "系统提示" , message, "保存",
                "取消", new RLAlertDialog.Listener() {
            @Override
            public void onLeftClick() {
                getSuccess();
            }
            @Override
            public void onRightClick() {
                finish();
            }
        }).show();
    }

    /**
     * 提交司机登记信息
     */
    private void getSuccess(){
        if (StringUtils.isEmpty(carName.getText())) {
            displayToast(getString(R.string.input_driving_licence_name));
            return;
        }

        if (StringUtils.isEmpty(carTel.getText())) {
            displayToast(getString(R.string.input_driving_licence_tel));
            return;
        } else if (BaseUtils.isMobileNO(carTel.getText().toString().trim()) != true) {
            displayToast(getString(R.string.invalid_phone));
            return;
        }

        if (StringUtils.isEmpty(plateNumber.getText())) {
            displayToast(getString(R.string.input_licence_plate_number));
            return;
        }
        if (BaseUtils.isPlateNumberNO(plateNumber.getText().toString().trim()) == false) {
            displayToast(getString(R.string.wrong_licence_plate_number));
            return;
        }


        try {
            LinkedHashMap<String,String> myParams = new LinkedHashMap<String, String>();
            myParams.put("realName",carName.getText().toString().trim());
            myParams.put("phoneNumber",carTel.getText().toString().trim());
            myParams.put("plateNumber",plateNumber.getText().toString().trim());

            new DataUtils(this,myParams).getUserDriverRegister(new DataUtils.DataBack<APPData>() {
                @Override
                public void getData(APPData data) {
                    if (data == null){
                        return;
                    }
                    displayToast(data.getMessage());
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
