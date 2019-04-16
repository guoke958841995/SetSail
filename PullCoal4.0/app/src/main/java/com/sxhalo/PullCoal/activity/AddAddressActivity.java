package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.model.APPData;
import com.sxhalo.PullCoal.model.UserAddress;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;
import java.util.LinkedHashMap;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * 地址添加界面
 * Created by amoldZhang on 2018/5/5.
 */
public class AddAddressActivity extends BaseActivity {


    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.et_name_value)
    EditText etNameValue;
    @Bind(R.id.et_phone_value)
    EditText etPhoneValue;
    @Bind(R.id.tv_select_address)
    TextView tvSelectAddress;
    @Bind(R.id.et_label_value)
    EditText etLabelValue;

    @Bind(R.id.tv_cancel)
    TextView tvCancel;

    private String selectCode = "";
    private String address = "";
    private String longitude = "";
    private String latitude = "";
    private UserAddress userAddress;
    private boolean ifDetermine = true;


    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_add_address);
    }

    @Override
    protected void initTitle() {
        title.setText("编辑地址");
    }

    @Override
    protected void getData() {
        if (null != getIntent().getSerializableExtra("UserAddress")){
            userAddress = (UserAddress)getIntent().getSerializableExtra("UserAddress");
            title.setText("编辑地址");
            etNameValue.setText(userAddress.getContactPerson());
            etNameValue.setSelection(etNameValue.getText().length());

            etPhoneValue.setText(userAddress.getContactPhone());
            etPhoneValue.setSelection(etPhoneValue.getText().length());

            tvSelectAddress.setText(userAddress.getFullRegionName());
            selectCode = userAddress.getRegionCode();
            address = userAddress.getAddress();

            etLabelValue.setText(userAddress.getAddressName());
            etLabelValue.setSelection(etLabelValue.getText().length());

            tvCancel.setVisibility(View.VISIBLE);
        }else{
            title.setText("新增地址");
            tvCancel.setVisibility(View.GONE);
        }
    }


    @OnClick({R.id.title_bar_left, R.id.layout_address, R.id.tv_determine,R.id.tv_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.layout_address:
                Intent intent = new Intent(this,SelectAddressActivity.class);
                intent.putExtra("UserAddress",userAddress);
                startActivityForResult(intent,Constant.AREA_CODE);
                break;
            case R.id.tv_determine:
                if (ifDetermine){
                    ifDetermine = false;
                    setDetermine();
                }
                break;
            case R.id.tv_cancel:
                if (userAddress != null){
                    getCancelSubmission(userAddress.getAddressId());
                }
                break;
        }
    }

    /**
     * 提交用户当前输入内容
     */
    private void setDetermine() {
        if (StringUtils.isEmpty(etNameValue.getText().toString().trim())){
            displayToast("请输入姓名");
            return;
        }
        if (StringUtils.isEmpty(etPhoneValue.getText().toString().trim())){
            displayToast("请输入手机号");
            return;
        }
        if (!BaseUtils.isMobileNO(etPhoneValue.getText().toString().trim())) {
            displayToast(getString(R.string.invalid_phone));
            return;
        }
        if (StringUtils.isEmpty(selectCode)){
            displayToast("请选择地址");
            return;
        }
        if ("0".equals(selectCode)){
            displayToast("地址不能选全国");
            return;
        }
        if (StringUtils.isEmpty(etLabelValue.getText().toString().trim())){
            displayToast("请输入标签");
            return;
        }
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("contactPerson", etNameValue.getText().toString().trim());
        params.put("contactPhone", etPhoneValue.getText().toString().trim());
        params.put("regionCode", selectCode );
        params.put("address", address);
        params.put("addressName", etLabelValue.getText().toString().trim());
        params.put("isDefault", "0" );

        if (null != getIntent().getSerializableExtra("UserAddress")){
            getUpdataSubmission(params);
        }else {
            getSubmission(params);
        }
    }

    /**
     * 新增
     * @param params
     */
    private void getSubmission(LinkedHashMap<String, String> params) {
        try {
            new DataUtils(this, params).getUserAddressCreate(new DataUtils.DataBack<UserAddress>() {
                @Override
                public void getData(UserAddress appDataList) {
                    try {
                        ifDetermine = true;
                        if (appDataList == null) {
                            return;
                        }
                        displayToast("地址新增成功");
                        setResult(RESULT_OK, null);
                        finish();
                    } catch (Exception e) {
                        MyException.uploadExceptionToServer(mContext,e.fillInStackTrace(),false);
                        GHLog.e("货运列表联网", e.toString());
                    }
                }

                @Override
                public void getError(Throwable e) {
                    ifDetermine = true;
                }
            });
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace(),false);
            e.printStackTrace();
        }
    }

    /**
     * 修改
     * @param params
     */
    private void getUpdataSubmission(LinkedHashMap<String, String> params) {
        try {
            params.put("addressId",userAddress.getAddressId());
            new DataUtils(this, params).getUserAddressUpData(new DataUtils.DataBack<UserAddress>() {
                @Override
                public void getData(UserAddress appDataList) {
                    try {
                        if (appDataList == null) {
                            return;
                        }
                        displayToast("地址编辑成功");
                        setResult(RESULT_OK, null);
                        finish();
                    } catch (Exception e) {
                        MyException.uploadExceptionToServer(mContext,e.fillInStackTrace(),false);
                        GHLog.e("货运列表联网", e.toString());
                    }
                }

                @Override
                public void getError(Throwable e) {
                }
            });
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace(),false);
            e.printStackTrace();
        }


    }

    /**
     * 修改
     * @param addressId
     */
    private void getCancelSubmission(String addressId) {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("addressId",addressId);
            new DataUtils(this, params).getUserAddressDelete(new DataUtils.DataBack<APPData>() {
                @Override
                public void getData(APPData appData) {
                    try {
                        displayToast("地址删除成功");
                        setResult(RESULT_OK, null);
                        finish();
                    } catch (Exception e) {
                        MyException.uploadExceptionToServer(mContext,e.fillInStackTrace(),false);
                        GHLog.e("货运列表联网", e.toString());
                    }
                }

                @Override
                public void getError(Throwable e) {
                }
            });
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace(),false);
            e.printStackTrace();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Constant.AREA_CODE) {
            if (data != null){
                userAddress = (UserAddress)data.getSerializableExtra("UserAddress");
                tvSelectAddress.setText(userAddress.getRegionName());
                selectCode = userAddress.getRegionCode();
                address = userAddress.getAddress();
            }
        }
    }


}
