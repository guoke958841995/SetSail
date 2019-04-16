package com.sxhalo.PullCoal.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.model.APPData;
import com.sxhalo.PullCoal.model.APPDataList;
import com.sxhalo.PullCoal.model.Coal;
import com.sxhalo.PullCoal.model.TransportMode;
import com.sxhalo.PullCoal.model.UserAddress;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.tools.map.NaviRoutePlanning;
import com.sxhalo.PullCoal.tools.permissionutil.PermissionListener;
import com.sxhalo.PullCoal.tools.permissionutil.PermissionUtil;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.popwin.CommonPopupWindow;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.SoftHideKeyBoardUtil;
import com.sxhalo.PullCoal.utils.StringUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.RecyclerViewSpacesItemDecoration;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.srecyclerview.SRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 我的货运发布
 * Created by amoldZhang on 2018/8/14.
 */

public class MyTransportReleaseActivity extends BaseActivity {


    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.transport_type)
    TextView transportType;
    @Bind(R.id.transport_from_pace)
    TextView transportFromPace;
    @Bind(R.id.transport_from_address)
    TextView transportFromAddress;
    @Bind(R.id.transport_to_pace)
    TextView transportToPace;
    @Bind(R.id.transport_to_address)
    TextView transportToAddress;
    @Bind(R.id.transport_pice_et)
    EditText transportPiceEt;
    @Bind(R.id.transport_name_et)
    EditText transportNameEt;
    @Bind(R.id.transport_car_num_et)
    EditText transportCarNumEt;
    @Bind(R.id.loading_charge_et)
    EditText loadingChargeET;
    @Bind(R.id.unloading_charge_et)
    EditText unloadingChargeET;
    @Bind(R.id.remarks_et)
    EditText remarksEt;

    @Bind(R.id.toble_view)
    RecyclerView tebleView;
    @Bind(R.id.transport_release)
    TextView transportRelease;


    private CommonPopupWindow popupWindow;
    private ArrayList<String> bondList;  //货运类型
    private UserAddress userAddressFrom,userAddressTo;
    private CommonAdapter<Map<String, Object>> mAdapter;

    private int transportTypeSize;
    private String publishTag = "";

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_transport_release);
        SoftHideKeyBoardUtil.assistActivity(this);
    }

    @Override
    protected void initTitle() {
        title.setText("货运发布");
    }

    @Override
    protected void getData() {
        bondList = getBondData();
        initView();
    }

    private void initView() {
        List<String> dataList = new ArrayList<>();
        dataList.add("跪求");
        dataList.add("秒装");
        dataList.add("大量要车");
        dataList.add("现货");
        dataList.add("免装卸");

        List<Map<String,Object>> mapList = new ArrayList<>();
        for(int i=0;i<dataList.size();i++){
            Map<String,Object> map = new HashMap<>();
            map.put("text",dataList.get(i));
            map.put("select",false);
            mapList.add(map);
        }
        RecyclerView.LayoutManager manager = new GridLayoutManager(this, 3);
        tebleView.setLayoutManager(manager);
//        tebleView.addItemDecoration(new RecyclerViewSpacesItemDecoration(5,0,5,0));

        mAdapter = new CommonAdapter<Map<String,Object>>(this, R.layout.layout_teble_item, mapList){
            @Override
            protected void convert(ViewHolder holder, final Map<String,Object> map, final int position){
                TextView textTeble = (TextView)holder.getView(R.id.teble_text);
                textTeble.setText(map.get("text").toString());
                if (map.get("select").toString().equals("false")){
                    textTeble.setTextColor(mContext.getResources().getColor(R.color.gary));
                    textTeble.setBackgroundResource(R.drawable.dialog_bg);
                }else{
                    textTeble.setTextColor(mContext.getResources().getColor(R.color.actionsheet_blue));
                    textTeble.setBackgroundResource(R.drawable.bull_send_car);
                }
            }
        };

        mAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                Map<String,Object> map =  mAdapter.getDatas().get(position);
                if (map.get("select").toString().equals("false")){
                    map.put("select",true);
                }else{
                    map.put("select",false);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        tebleView.setAdapter(mAdapter);
    }

    @OnClick({R.id.title_bar_left, R.id.transport_type_view, R.id.transport_from_pace_view, R.id.transport_to_pace_view, R.id.transport_release})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.transport_type_view:
                initPopWindow(findViewById(R.id.transport_type_view));
                break;
            case R.id.transport_from_pace_view:
                if (userAddressFrom == null){
                    userAddressFrom = new UserAddress();
                }
                setaddress(userAddressFrom,Constant.AREA_CODE_FROM);
                break;
            case R.id.transport_to_pace_view:
                if (userAddressTo == null){
                    userAddressTo = new UserAddress();
                }
                setaddress(userAddressTo,Constant.AREA_CODE_TO);
                break;
            case R.id.transport_release:
                release();
                break;
        }
    }


    private void setaddress(final UserAddress userAddress,final int code){
        new PermissionUtil().requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, new PermissionListener() {
            @Override
            public void onGranted() { //用户同意授权
                Intent intentto = new Intent(MyTransportReleaseActivity.this,SelectAddressActivity.class);
                intentto.putExtra("UserAddress",userAddress);
                intentto.putExtra("TTPE","-1");
                startActivityForResult(intentto,code);
            }

            @Override
            public void onDenied(Context context, List<String> permissions) { //用户拒绝授权

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode){
                case Constant.AREA_CODE_FROM:
                    userAddressFrom = (UserAddress)data.getSerializableExtra("UserAddress");
                    transportFromPace.setText(userAddressFrom.getRegionName() + userAddressFrom.getAddress());
                    break;
                case Constant.AREA_CODE_TO:
                    userAddressTo = (UserAddress)data.getSerializableExtra("UserAddress");
                    transportToPace.setText(userAddressTo.getRegionName() + userAddressTo.getAddress());
                    break;
            }
        }
    }

    /**
     * 发布提交
     */
    private void release() {
        if (transportType.getText() == null  || StringUtils.isEmpty(transportType.getText().toString().trim())){
            displayToast("您尚未选择货运类型");
            return;
        }
        if (userAddressFrom == null){
            displayToast("您尚未选择出发地址");
            return;
        }
        if (userAddressTo == null){
            displayToast("您尚未选择到达地址");
            return;
        }
        if (transportPiceEt.getText() == null  || StringUtils.isEmpty(transportPiceEt.getText().toString().trim())){
            displayToast("请输入运输费用");
            return;
        }
        if ("0".equals(transportPiceEt.getText().toString().trim())){
            displayToast("运输费不能为 0 哦");
            return;
        }
        if (transportNameEt.getText() == null  || StringUtils.isEmpty(transportNameEt.getText().toString().trim())){
            displayToast("请输入货运名称");
            return;
        }
        if (transportNameEt.getText() == null  || StringUtils.isEmpty(transportNameEt.getText().toString().trim())){
            displayToast("请输入货运名称");
            return;
        }
        if (transportType.getText() != null && !StringUtils.isEmpty(transportType.getText().toString().trim())){
            if ("长期货运(煤炭)".equals(transportType.getText().toString().trim())){

            }else{
                if (transportCarNumEt.getText() == null  || StringUtils.isEmpty(transportCarNumEt.getText().toString().trim())){
                    displayToast("请输入所需车辆数");
                    return;
                }
                if ("0".equals(transportCarNumEt.getText().toString().trim())){
                    displayToast("所需车辆数不能为 0 哦");
                    return;
                }
            }
        }
        userTransportCreate();
    }

    /**
     * 货运发布
     */
    private void userTransportCreate() {
        transportRelease.setClickable(false);
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("transportType", transportTypeSize + ""); //货运类型 0 短期煤炭货运；1长期煤炭货运；2其他货运
        params.put("fromPlace", userAddressFrom.getRegionCode() + ""); //出发地区
        params.put("fromLongitude", userAddressFrom.getLongitude() + ""); //出发地区经度
        params.put("fromLatitude", userAddressFrom.getLatitude() + ""); //出发地区纬度
        params.put("toPlace", userAddressTo.getRegionCode() + ""); //目的地区
        params.put("toLongitude", userAddressTo.getLongitude() + ""); //目的地经度
        params.put("toLatitude", userAddressTo.getLatitude() + "");  //目的地纬度
        params.put("cost", transportPiceEt.getText().toString().trim() + "");  //运输费用
        params.put("goodsName", transportNameEt.getText().toString().trim() + "");  //货物名称


        if (!StringUtils.isEmpty(userAddressFrom.getAddress())){
            params.put("fromAddress", userAddressFrom.getAddress() + "");  //出发地址
        }
        if (!StringUtils.isEmpty(userAddressTo.getAddress())){
            params.put("toAddress", userAddressTo.getAddress() + "");  //目的地址
        }

        if (!"长期货运".equals(transportType.getText().toString().trim())){
            params.put("totalNum", transportCarNumEt.getText().toString().trim() + "");  //共需车数
        }

        if (!StringUtils.isEmpty(loadingChargeET.getText().toString().trim())){
            params.put("loadingCharge", loadingChargeET.getText().toString().trim() + "");  //装车费
        }
        if (!StringUtils.isEmpty(unloadingChargeET.getText().toString().trim())){
            params.put("unloadingCharge", unloadingChargeET.getText().toString().trim()+ "");  //卸车费
        }

        for (Map<String,Object> map : mAdapter.getDatas()){
            if (map.get("select").toString().equals("true")){
                if ("".equals(publishTag)){
                    publishTag = map.get("text").toString();
                }else{
                    publishTag = publishTag  + ","+ map.get("text").toString();
                }
            }
        }
        if (!StringUtils.isEmpty(publishTag)){
            params.put("publishTag", publishTag + "");  //标签
        }

        if (!StringUtils.isEmpty(remarksEt.getText().toString().trim())){
            params.put("remark", remarksEt.getText().toString().trim() + "");  //备注
        }

        new DataUtils(mContext, params).userTransportCreate(new DataUtils.DataBack<APPData<TransportMode>>() {
            @Override
            public void getData(APPData<TransportMode> appDataList) {
                if (appDataList == null) {
                    return;
                }
                displayToast(appDataList.getMessage());
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void getError(Throwable e) {
                transportRelease.setClickable(true);
            }
        });
    }

    /**
     * 初始化弹框
     */
    private void initPopWindow(final View showWiew){
        if (popupWindow != null && popupWindow.isShowing()) return;
        popupWindow = new CommonPopupWindow.Builder(this)
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
                        setChildView(view,bondList);
                    }
                })
                //设置外部是否可点击 默认是true
                .setOutsideTouchable(true)
                //开始构建
                .create();
//        //弹出PopupWindow
        popupWindow.showAsDropDown(showWiew);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (transportType.getText() != null && !StringUtils.isEmpty(transportType.getText().toString().trim())){
                    if ("长期货运(煤炭)".equals(transportType.getText().toString().trim())){
                        findViewById(R.id.transport_car_num_view).setVisibility(View.GONE);
                        findViewById(R.id.transport_car_num_line).setVisibility(View.GONE);
                    }else{
                        findViewById(R.id.transport_car_num_view).setVisibility(View.VISIBLE);
                        findViewById(R.id.transport_car_num_line).setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    /**
     * 获取保证金额度数目列表
     * @return
     */
    private ArrayList<String> getBondData(){
        //筛选数据   0 短期煤炭货运；1长期煤炭货运；2其他货运
        ArrayList<String> bondList = new ArrayList<String>();
        bondList.add("短期货运(煤炭)");
        bondList.add("长期货运(煤炭)");
        bondList.add("其他货运");
        return bondList;
    }

    /**
     * 弹框数据展示布局
     * @param view
     * @param data
     */
    private void setChildView(View view,final ArrayList<String> data) {
        //获得PopupWindow布局里的View
        ListView listView = (ListView)view.findViewById(R.id.listview);
        QuickAdapter mAdapter = new QuickAdapter<String>(this, R.layout.purchase_release_popwin_view_item,data) {
            @Override
            protected void convert(BaseAdapterHelper helper, String item, int pos) {
                try{
                    helper.setText(R.id.text_view,item);
                }catch (Exception e){
                    MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                    GHLog.e("资讯展示",e.toString());
                }
            }
        };
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (popupWindow != null){
                    String type = data.get(position);
                    transportTypeSize = position;
                    transportType.setText(type);
                    popupWindow.dismiss();
                }
            }
        });
    }
}
