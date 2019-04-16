package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.SendCarEntity;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by liz on 2018/5/26.
 * 手动添加派车界面
 */

public class ManualAddActivity extends BaseActivity{

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.et_driver_name)
    EditText etDriverName;
    @Bind(R.id.et_driver_phone)
    EditText etDriverPhone;
    @Bind(R.id.et_car_plate)
    EditText etCarPlate;
    @Bind(R.id.btn_add)
    Button btnAdd;
    private ArrayList<SendCarEntity> list;//从派车界面点击手动添加进来
    private SendCarEntity sendCarEntity;//从派车界面点击编辑进来
    private int number; // 还需派车数

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_manual_add);
    }

    @Override
    protected void initTitle() {
        title.setText("手动派车");
        list = (ArrayList<SendCarEntity>) getIntent().getSerializableExtra("list");
        sendCarEntity = (SendCarEntity) getIntent().getSerializableExtra("entity");
        number = getIntent().getIntExtra("number",0);
    }

    @Override
    protected void getData() {
        if (sendCarEntity != null) {
            btnAdd.setText("确认修改");
            etDriverName.setText(sendCarEntity.getDriverRealName());
            etDriverPhone.setText(sendCarEntity.getDriverPhone());
            etCarPlate.setText(sendCarEntity.getPlateNumber());
        } else {
            btnAdd.setText("添加");
        }
    }

    @OnClick({R.id.title_bar_left, R.id.btn_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.btn_add:
                if (number < 0 && sendCarEntity != null){
                    displayToast("您当前派车已足够拉完预订吨数,不需要再次派车");
                    return;
                }
                //手动添加或修改
                addOrEdit();
                break;
        }
    }

    private void addOrEdit() {
            SendCarEntity sendCarEntity = new SendCarEntity();
            String driverName = etDriverName.getText().toString().trim();
            String dirverPhone = etDriverPhone.getText().toString().trim();
            String carPlate = etCarPlate.getText().toString().trim();
            sendCarEntity.setDriverRealName(driverName);
            sendCarEntity.setDriverPhone(dirverPhone);
            sendCarEntity.setPlateNumber(carPlate);
            sendCarEntity.setManualAdd(true);//手动添加的标记
            sendCarEntity.setDriverAuthState("0");
            if (StringUtils.isEmpty(driverName)) {
                displayToast("请输入司机姓名");
                return;
            }
            if (StringUtils.isEmpty(dirverPhone)) {
                displayToast("请输入司机电话");
                return;
            }
            if (StringUtils.isEmpty(carPlate)) {
                displayToast("请输入车牌号");
                return;
            }
            if (!BaseUtils.isMobileNO(dirverPhone)) {
                displayToast(getString(R.string.invalid_phone));
                return;
            }
            if (!BaseUtils.isPlateNumberNO(carPlate)) {
                displayToast(getString(R.string.wrong_licence_plate_number));
                return;
            }
            if (this.sendCarEntity != null) {
                //编辑进来的情况
                if(driverName.equals(this.sendCarEntity.getDriverRealName()) && dirverPhone.equals(this.sendCarEntity.getDriverPhone()) && carPlate.equals(this.sendCarEntity.getPlateNumber())) {
                    displayToast("您并未做任何修改！");
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("entity", sendCarEntity);
                setResult(RESULT_OK, intent);
            } else {
                //手动添加进来的情况
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getDriverPhone().equals(dirverPhone) || list.get(i).getPlateNumber().equals(carPlate)) {
                        displayToast("您已经添加过该信息！");
                        return;
                    }
                }
                list.add(sendCarEntity);
                Intent intent = new Intent();
                intent.putExtra("list", list);
                setResult(RESULT_OK, intent);
            }
            finish();
    }
}
