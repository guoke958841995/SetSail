package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amoldzhang.hightlight.GuideView;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.model.APPData;
import com.sxhalo.PullCoal.model.OperateVisitorRegistration;
import com.sxhalo.PullCoal.model.UserAddress;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.ui.daiglog.RLAlertDialog;
import com.sxhalo.PullCoal.ui.popwin.CommonPopupWindow;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 登记司机详情页
 * Created by amoldZhang on 2019/2/28.
 */
public class GuestRegistrationInfoActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView titleText;
    @Bind(R.id.et_customer_name)
    EditText etCustomerName;
    @Bind(R.id.et_contact_number)
    EditText etContactNumber;
    @Bind(R.id.tv_from_area)
    TextView tvFromArea;
    @Bind(R.id.tv_to_area)
    TextView tvToArea;
    @Bind(R.id.et_quantity)
    EditText etQuantity;
    @Bind(R.id.et_cost)
    EditText etCost;
    @Bind(R.id.et_loading_charge)
    EditText etLoadingCharge;
    @Bind(R.id.tv_dict_value)
    TextView tvDictValue;
    @Bind(R.id.et_calorific_value)
    EditText etCalorificValue;
    @Bind(R.id.et_other_needs)
    EditText etOtherNeeds;
    @Bind(R.id.layout_register_type)
    LinearLayout layoutRegisterType;
    @Bind(R.id.tv_register_type)
    TextView tvRegisterType;
    @Bind(R.id.iv_register_type)
    ImageView ivRegisterType;
    @Bind(R.id.layout_status)
    LinearLayout layoutStatus;
    @Bind(R.id.tv_status)
    TextView tvStatus;
    @Bind(R.id.iv_status)
    ImageView ivStatus;
    @Bind(R.id.et_remark)
    EditText etRemark;
    @Bind(R.id.layout_follow)
    LinearLayout layoutFollow;
    @Bind(R.id.tv_follow_name)
    TextView tvFollowName;
    @Bind(R.id.iv_follow)
    ImageView ivFollow;

    private OperateVisitorRegistration operateVisitorRegistration;
    private String registerNum;

    private String[] stringStatus = new String[]{"进行中", "已完成"};
    private String[] stringRegisterType = new String[]{"找煤", "找车"};
    private boolean ifEditor; // 地址是否编辑
    private String roleId;
    private CommonPopupWindow popupWindowFollow;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_guest_registration_info);
    }

    @Override
    protected void initTitle() {
        OperateVisitorRegistration operateVisitorRegistration = (OperateVisitorRegistration) getIntent().getSerializableExtra("Entity");
        registerNum = operateVisitorRegistration.getRegisterNum();
        roleId = operateVisitorRegistration.getRoleId();
        titleText.setText("登记详情");
    }

    @Override
    protected void getData() {
        getDataType(true);
    }

    /**
     * 当前布局联网获取数据
     *
     * @param flage
     */
    private void getDataType(boolean flage) {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("registerNum", registerNum);
            params.put("roleId", roleId);
            new DataUtils(this, params).getUserOperateVisitorInfo(new DataUtils.DataBack<OperateVisitorRegistration>() {
                @Override
                public void getData(OperateVisitorRegistration appData) {
                    try {
                        if (appData == null) {
                            return;
                        }
                        operateVisitorRegistration = appData;
                        initView();
                    } catch (Exception e) {
                        GHLog.e("货运列表联网", e.toString());
                    }
                }

                @Override
                public void getError(Throwable e) {
                }
            }, flage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        etCustomerName.setText(operateVisitorRegistration.getCustomerName());
        etCustomerName.setSelection(etCustomerName.getText().length());
        etContactNumber.setText(operateVisitorRegistration.getContactNumber());
        etContactNumber.setSelection(etContactNumber.getText().length());
        tvFromArea.setText(operateVisitorRegistration.getFromPlaceName() + operateVisitorRegistration.getFromAddress());
        tvToArea.setText(operateVisitorRegistration.getToPlaceName() + operateVisitorRegistration.getToAddress());
        //类型
        String dictValue;
        //单位
        String unit;
        //0找煤   1找车
        if ("0".equals(operateVisitorRegistration.getRegisterType())){
            dictValue = "找煤";
            unit = "吨";
        }else{
            dictValue = "找车";
            unit = "车";
        }
        tvRegisterType.setText(dictValue);
        etQuantity.setText(operateVisitorRegistration.getQuantity());
        etQuantity.setSelection(etQuantity.getText().length());
        etCost.setText(operateVisitorRegistration.getCost());
        etCost.setSelection(etCost.getText().length());
        etLoadingCharge.setText(operateVisitorRegistration.getLoadingCharge());
        etLoadingCharge.setSelection(etLoadingCharge.getText().length());
        tvDictValue.setText(operateVisitorRegistration.getDictValue());
        etCalorificValue.setText(operateVisitorRegistration.getCalorificValue());
        etCalorificValue.setSelection(etCalorificValue.getText().length());
        etOtherNeeds.setText(StringUtils.isEmpty(operateVisitorRegistration.getOtherNeeds())?"":operateVisitorRegistration.getOtherNeeds());
        etOtherNeeds.setSelection(etOtherNeeds.getText().length());
        tvStatus.setText(operateVisitorRegistration.getStatus().equals("1")?"已完成":"进行中");
        etRemark.setText(StringUtils.isEmpty(operateVisitorRegistration.getRemark())?"":operateVisitorRegistration.getRemark());
        etRemark.setSelection(etRemark.getText().length());
        tvFollowName.setText(operateVisitorRegistration.getFollowName() + "("+operateVisitorRegistration.getFollowNum() + "单)");
    }


    @OnClick({R.id.title_bar_left, R.id.layout_from_area, R.id.layout_to_area, R.id.layout_register_type, R.id.layout_dict_value, R.id.layout_status,R.id.layout_follow, R.id.tv_cancel, R.id.tv_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.layout_from_area:
                UserAddress userAddressFrom = new UserAddress();
                userAddressFrom.setAddress(operateVisitorRegistration.getFromAddress());
                userAddressFrom.setLongitude(operateVisitorRegistration.getFromLongitude());
                userAddressFrom.setLatitude(operateVisitorRegistration.getFromLatitude());
                userAddressFrom.setRegionName(operateVisitorRegistration.getFromPlaceName());
                userAddressFrom.setFullRegionName(operateVisitorRegistration.getFromPlaceName());
                userAddressFrom.setRegionCode(operateVisitorRegistration.getFromPlace());
                userAddressFrom.setAddressId("from");
                Intent intent = new Intent(this,SelectAddressActivity.class);
                intent.putExtra("UserAddress",userAddressFrom);
                startActivityForResult(intent, Constant.AREA_CODE);
                break;
            case R.id.layout_to_area:
                UserAddress userAddressTo = new UserAddress();
                userAddressTo.setAddress(operateVisitorRegistration.getToAddress());
                userAddressTo.setLongitude(operateVisitorRegistration.getToLongitude());
                userAddressTo.setLatitude(operateVisitorRegistration.getToLatitude());
                userAddressTo.setRegionName(operateVisitorRegistration.getToPlaceName());
                userAddressTo.setFullRegionName(operateVisitorRegistration.getToPlaceName());
                userAddressTo.setRegionCode(operateVisitorRegistration.getToPlace());
                userAddressTo.setAddressId("to");
                Intent intentTo = new Intent(this,SelectAddressActivity.class);
                intentTo.putExtra("UserAddress",userAddressTo);
                startActivityForResult(intentTo, Constant.AREA_CODE);
                break;
            case R.id.layout_register_type:
                initPopWindow(tvRegisterType,getRegisterTypeList());
                ivRegisterType.setRotation(180f);
                break;
            case R.id.layout_status:
                initPopWindow(tvStatus,getStatusList());
                ivStatus.setRotation(180f);
                break;
            case R.id.layout_follow:
                initPopWindow(tvFollowName,getFollowList());
                ivFollow.setRotation(0f);
                break;
            case R.id.tv_cancel:
//                if (ifEditor){
//                    showPayDialog("确认是否取消?");
//                }else{
                finish();
//                }
                break;
            case R.id.tv_save:
                saveData();
//                if (ifEditor){
//                    showPayDialog("确认是否保存?");
//                }else{
//                    displayToast("请修改后再保存!");
//                }
                break;
        }
    }

    /**
     * 获取跟单人数据
     * @return
     */
    private ArrayList<String> getFollowList(){
        ArrayList<String> data = new ArrayList<>();
        for (OperateVisitorRegistration.FollowUsers followUsers:operateVisitorRegistration.getFollowUsersList()){
            data.add(followUsers.realName + "("+followUsers.num +")");
        }
        return data;
    }

    /**
     * 获取需求类型数据数据
     * @return
     */
    private ArrayList<String> getRegisterTypeList(){
        ArrayList<String> data = new ArrayList<>();
        for (String text:stringRegisterType){
            data.add(text);
        }
        return data;
    }

    /**
     * 获取状态变更数据
     * @return
     */
    private ArrayList<String> getStatusList(){
        ArrayList<String> data = new ArrayList<>();
        for (String text:stringStatus){
            data.add(text);
        }
        return data;
    }

    /**
     * 初始化更改跟单人弹框
     * @param showWiew
     */
    private void initPopWindow(final TextView showWiew,final ArrayList<String> data){
        if (popupWindowFollow != null && popupWindowFollow.isShowing()) return;
        popupWindowFollow = new CommonPopupWindow.Builder(this)
                //设置PopupWindow布局
                .setView(R.layout.purchase_release_popwin_view)
                //设置宽高
                .setWidthAndHeight(showWiew.getWidth(),
                        ViewGroup.LayoutParams.WRAP_CONTENT)
//                //设置动画
//                .setAnimationStyle(R.style.AnimDown)
                //设置背景颜色，取值范围0.0f-1.0f 值越小越暗 1.0f为透明
                .setBackGroundLevel(0.5f)
                //设置PopupWindow里的子View及点击事件
                .setViewOnclickListener(new CommonPopupWindow.ViewInterface() {
                    @Override
                    public void getChildView(View view, int layoutResId) {
                        setChildView(showWiew,view,data);
                    }
                })
                //设置外部是否可点击 默认是true
                .setOutsideTouchable(true)
                //开始构建
                .create();
        int height;
        if (showWiew == tvFollowName){
            height = - (data.size() * popupWindowFollow.getHeight()) - showWiew.getTop()-100;
        }else{
            height = showWiew.getHeight() - 20;
        }
//        //弹出PopupWindow
        popupWindowFollow.showAsDropDown(showWiew,0,height);
        popupWindowFollow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                  if (showWiew == tvFollowName){
                      ivFollow.setRotation(90f);
                  }else{
                      ivStatus.setRotation(90f);
                      ivRegisterType.setRotation(90f);
                  }
            }
        });
    }

    /**
     * 弹框数据展示布局
     * @param showWiew
     * @param view
     * @param data
     */
    private void setChildView(final TextView showWiew,View view,final ArrayList<String> data) {
        //获得PopupWindow布局里的View
        ListView listView = (ListView)view.findViewById(R.id.listview);
        QuickAdapter mAdapter = new QuickAdapter<String>(this, R.layout.purchase_release_popwin_view_item,data) {
            @Override
            protected void convert(BaseAdapterHelper helper, String item, int pos) {
                try{
                    helper.setText(R.id.text_view,item);
                }catch (Exception e){
                    GHLog.e(getClass().getName(),e.toString());
                }
            }
        };
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (popupWindowFollow != null){
                    showWiew.setText(data.get(position) + "");
                    popupWindowFollow.dismiss();
                }
            }
        });
    }

    /**
     * 弹出框
     */
    public void showPayDialog(String message) {
        new RLAlertDialog(this, "系统提示",message , "取消",
                "确定", new RLAlertDialog.Listener() {
            @Override
            public void onLeftClick() {
                saveData();
            }

            @Override
            public void onRightClick() {
                finish();
            }
        }).show();
    }

    /**
     * 更新登记详情
     */
    private void saveData() {
        try {
            new DataUtils(this, getParams()).getUserOperateVisitorUpdate(new DataUtils.DataBack<APPData>() {
                @Override
                public void getData(APPData appData) {
                    try {
                        if (appData == null) {
                            return;
                        }
                        displayToast("保存成功");
                        setResult(RESULT_OK);
                        finish();
                    } catch (Exception e) {
                        GHLog.e("货运列表联网", e.toString());
                    }
                }

                @Override
                public void getError(Throwable e) {

                }
            }, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Constant.AREA_CODE) {
            if (data != null){
                ifEditor = true;
                UserAddress userAddress = (UserAddress)data.getSerializableExtra("UserAddress");
                if ("to".equals(userAddress.getAddressId())){
                    operateVisitorRegistration.setToAddress(userAddress.getAddress());
                    operateVisitorRegistration.setToLongitude(userAddress.getLongitude());
                    operateVisitorRegistration.setToLatitude(userAddress.getLatitude());
                    operateVisitorRegistration.setToPlaceName(userAddress.getRegionName());
                    operateVisitorRegistration.setToPlace(userAddress.getRegionCode());

                    tvToArea.setText(operateVisitorRegistration.getToPlaceName() + operateVisitorRegistration.getToAddress());
                }else{
                    operateVisitorRegistration.setFromAddress(userAddress.getAddress());
                    operateVisitorRegistration.setFromLongitude(userAddress.getLongitude());
                    operateVisitorRegistration.setFromLatitude(userAddress.getLatitude());
                    operateVisitorRegistration.setFromPlaceName(userAddress.getRegionName());
                    operateVisitorRegistration.setFromPlace(userAddress.getRegionCode());

                    tvFromArea.setText(operateVisitorRegistration.getFromPlaceName() + operateVisitorRegistration.getFromAddress());
                }
            }
        }
    }

    /**
     * 获取访客登记详情信息
     * @return
     */
    public LinkedHashMap<String,String> getParams() {
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("registerNum", registerNum);

        if (!operateVisitorRegistration.getCustomerName().equals(etCustomerName.getText().toString())){
            if (!StringUtils.isEmpty(etCustomerName.getText())){
                params.put("customerName", etCustomerName.getText().toString().trim());
            }else{
                displayToast("请输入联系人");
            }
        }
        if (!operateVisitorRegistration.getContactNumber().equals(etContactNumber.getText().toString())){
            if (!StringUtils.isEmpty(etContactNumber.getText())){
                params.put("contactNumber", etContactNumber.getText().toString().trim());
            }else{
                displayToast("请输入联系电话");
            }
        }

        if (ifEditor){
            if (!StringUtils.isEmpty(operateVisitorRegistration.getFromPlace())){
                params.put("fromPlace", operateVisitorRegistration.getFromPlace());
            }else{
                displayToast("请选择出发地区");
            }
            if (!StringUtils.isEmpty(operateVisitorRegistration.getFromAddress())){
                params.put("fromAddress", operateVisitorRegistration.getFromAddress());
            }else{
                displayToast("请选择出发地详细地址");
            }
            if (!StringUtils.isEmpty(operateVisitorRegistration.getFromLatitude()) && !StringUtils.isEmpty(operateVisitorRegistration.getFromLongitude())){
                params.put("fromLatitude", operateVisitorRegistration.getFromLatitude());
                params.put("fromLongitude", operateVisitorRegistration.getFromLongitude());
            }else{
                displayToast("请重新选择出发地");
            }

            if (!StringUtils.isEmpty(operateVisitorRegistration.getToPlace())){
                params.put("toPlace", operateVisitorRegistration.getToPlace());
            }else{
                displayToast("请选择目的地区");
            }
            if (!StringUtils.isEmpty(operateVisitorRegistration.getToAddress())){
                params.put("toAddress", operateVisitorRegistration.getToAddress());
            }else{
                displayToast("请选择目的地详细地址");
            }
            if (!StringUtils.isEmpty(operateVisitorRegistration.getToLatitude()) && !StringUtils.isEmpty(operateVisitorRegistration.getToLongitude())){
                params.put("toLatitude", operateVisitorRegistration.getToLatitude());
                params.put("toLongitude", operateVisitorRegistration.getToLongitude());
            }else{
                displayToast("请重新选择目的地");
            }
        }

        //0找煤   1找车
        String registerType = "找煤".equals(tvRegisterType.getText().toString().trim())?"0":"1";
        if (!operateVisitorRegistration.getRegisterType().equals(registerType)){
            params.put("registerType", registerType);
        }

        if (!operateVisitorRegistration.getQuantity().equals(etQuantity.getText().toString())){
            if (!StringUtils.isEmpty(etQuantity.getText())){
                params.put("quantity", etQuantity.getText().toString().trim());
            }else{
                displayToast("请输入需求量");
            }
        }
        if (!operateVisitorRegistration.getCost().equals(etCost.getText().toString())){
            if (!StringUtils.isEmpty(etCost.getText())){
                params.put("cost", etCost.getText().toString().trim());
            }else{
                params.put("cost", "");
            }
        }
        if (!etLoadingCharge.getText().toString().equals(operateVisitorRegistration.getLoadingCharge())){
            if (!StringUtils.isEmpty(etLoadingCharge.getText())){
                params.put("loadingCharge", etLoadingCharge.getText().toString().trim());
            }
        }
        if (!operateVisitorRegistration.getCalorificValue().equals(etCalorificValue.getText().toString())){
            if (!StringUtils.isEmpty(etCalorificValue.getText())){
                params.put("calorificValue", etCalorificValue.getText().toString().trim());
            }else{
                params.put("calorificValue", "");
            }
        }
        if (!operateVisitorRegistration.getOtherNeeds().equals(etOtherNeeds.getText().toString())){
            if (!StringUtils.isEmpty(etOtherNeeds.getText())){
                params.put("otherNeeds", etOtherNeeds.getText().toString().trim());
            }else{
                params.put("otherNeeds", "");
            }
        }
        if (!operateVisitorRegistration.getStatus().equals(tvStatus.getText().toString().equals("已完成")?"1":"0")){
            params.put("status", tvStatus.getText().equals("已完成")?"1":"0");
        }
        if (!operateVisitorRegistration.getRemark().equals(etRemark.getText().toString())){
            if (!StringUtils.isEmpty(etRemark.getText())){
                params.put("remark", etRemark.getText().toString().trim());
            }else{
                params.put("remark", "");
            }
        }

        if (!tvFollowName.getText().toString().contains(operateVisitorRegistration.getFollowName())){
            if (!StringUtils.isEmpty(tvFollowName.getText())){
                for (OperateVisitorRegistration.FollowUsers followUsers:operateVisitorRegistration.getFollowUsersList()){
                    if (tvFollowName.getText().toString().equals(followUsers.realName + "("+followUsers.num+")")){
                        params.put("followPerson", followUsers.userId);
                        break;
                    }
                }
            }
        }
        return params;
    }
}

