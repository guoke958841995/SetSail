package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.adapter.BaseSwipListAdapter;
import com.sxhalo.PullCoal.model.APPData;
import com.sxhalo.PullCoal.model.UserAuthenticationEntity;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.ui.MyProgressView;
import com.sxhalo.PullCoal.ui.StatusBarUtils;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.daiglog.TowButtonDialog;
import com.sxhalo.PullCoal.ui.swipelistview.SwipeMenu;
import com.sxhalo.PullCoal.ui.swipelistview.SwipeMenuCreator;
import com.sxhalo.PullCoal.ui.swipelistview.SwipeMenuItem;
import com.sxhalo.PullCoal.ui.swipelistview.SwipeMenuListView;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.DeviceUtil;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

import static com.sxhalo.PullCoal.common.base.Constant.REFRESH_CODE;

/**
 * 司机认证界面
 */
public class DriverCertificationActivity extends BaseActivity {

    @Bind(R.id.layout_top)
    RelativeLayout layoutHeader;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.listView)
    SwipeMenuListView listView;

    private List<UserAuthenticationEntity> carEntities = new ArrayList<UserAuthenticationEntity>();
    private APPData<UserAuthenticationEntity> appData;
    private MyAdapter mAdapter;
    private MyProgressView myProgressView;
    private View headerView;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_driver_certification);

        setStatusBar(0,-1);

        // 设置固定大小的占位符
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) layoutHeader.getLayoutParams(); //取控件View当前的布局参数
        linearParams.height = StatusBarUtils.getStatusBarHeight(this) + BaseUtils.dip2px(this,50f);// 控件的高强制设成当前手机状态栏高度
        layoutHeader.setLayoutParams(linearParams); //使设置好的布局参数应用到控件</pre>
    }

    @Override
    protected void initTitle() {
        tvTitle.setText("司机认证");
    }


    @Override
    protected void getData() {
        //获取司机认证信息
        getDriverCertificationInfor();
    }

    /**
     * 获取司机认证信息
     */
    private void getDriverCertificationInfor() {
        try {
            new DataUtils(this, new LinkedHashMap<String, String>()).getUserDriverAuthInfo(new DataUtils.DataBack<APPData<UserAuthenticationEntity>>() {
                @Override
                public void getData(APPData<UserAuthenticationEntity> data) {
                    if (data == null) {
                        return;
                    }
                    appData = data;
                    carEntities = data.getList();
                    initHeaderView();
                }

                @Override
                public void getError(Throwable e) {
                    displayToast("网络连接异常");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化列表头部
     */
    private void initHeaderView() {
        if (mAdapter == null) {
            setViewInit();
        }
        TextView vehicleTitle = (TextView) headerView.findViewById(R.id.vehicle_title);

        //司机资料完成度
        int percentage = Integer.valueOf(appData.getEntity().getPercentage());
        myProgressView.setProgressCurrent(percentage);

        if (percentage == 100) {
            vehicleTitle.setText("信息已完善");
        } else {
            vehicleTitle.setText("驾驶员和车辆信息尚不完善");
        }

        String notPerfect = "未完善";
        String perfect = "已完善";
        int iconNotPerfect = R.mipmap.icon_not_perfect;
        int iconPerfect = R.mipmap.icon_perfect;
        int textColorRed = R.color.actionsheet_red;
        int textColorGreen = R.color.search_text_color_green;
        //驾驶员资料
        TextView tvDriverInfoStatus = (TextView) headerView.findViewById(R.id.tv_driver_info_status);
        ImageView ivDriverInfoStatus = (ImageView) headerView.findViewById(R.id.iv_driver_info_status);
        if (appData.getEntity().getDriverComplete().equals("0")) {
            tvDriverInfoStatus.setText(notPerfect);
            tvDriverInfoStatus.setTextColor(getResources().getColor(textColorRed));
            ivDriverInfoStatus.setImageResource(iconNotPerfect);
        } else {
            tvDriverInfoStatus.setText(perfect);
            tvDriverInfoStatus.setTextColor(getResources().getColor(textColorGreen));
            ivDriverInfoStatus.setImageResource(iconPerfect);
        }

        //实名认证资料
        TextView realNameTile = (TextView) headerView.findViewById(R.id.real_name_title);
        TextView tvRealNameInfoStatus = (TextView) headerView.findViewById(R.id.tv_real_name_info_status);
        ImageView ivRealNameInfoStatus = (ImageView) headerView.findViewById(R.id.iv_real_name_info_status);
        if ("0".equals(appData.getEntity().getRealnameAuthState())) {
            realNameTile.setText("实名资料(在实名认证中完善)");
            tvRealNameInfoStatus.setText(notPerfect);
            tvRealNameInfoStatus.setTextColor(getResources().getColor(textColorRed));
            ivRealNameInfoStatus.setImageResource(iconNotPerfect);
        } else {
            realNameTile.setText("实名资料");
            tvRealNameInfoStatus.setText(perfect);
            tvRealNameInfoStatus.setTextColor(getResources().getColor(textColorGreen));
            ivRealNameInfoStatus.setImageResource(iconPerfect);
        }

        //车辆资料
        TextView tvCarInfoStatus = (TextView) headerView.findViewById(R.id.tv_car_info_status);
        ImageView ivCarInfoStatus = (ImageView) headerView.findViewById(R.id.iv_car_info_status);
        if (appData.getEntity().getVehicleComplete().equals("0")) {
            tvCarInfoStatus.setText(notPerfect);
            tvCarInfoStatus.setTextColor(getResources().getColor(textColorRed));
            ivCarInfoStatus.setImageResource(iconNotPerfect);
        } else {
            tvCarInfoStatus.setText(perfect);
            tvCarInfoStatus.setTextColor(getResources().getColor(textColorGreen));
            ivCarInfoStatus.setImageResource(iconPerfect);
        }

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 界面功能数据显示实现
     */
    private void setViewInit() {
        mAdapter = new MyAdapter();
        headerView = View.inflate(this, R.layout.listview_header_view, null);
        listView.addHeaderView(headerView);
        myProgressView = (MyProgressView) headerView.findViewById(R.id.my_progress_view);

        headerView.findViewById(R.id.ll_progress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 认证进度跳转
                UIHelper.jumpAct(DriverCertificationActivity.this, DriverAuthenticationInformationActivity.class, appData, false);
            }
        });
        // 添加车辆管理跳转
        headerView.findViewById(R.id.tv_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DriverCertificationActivity.this, VehicleInformationEditingActivity.class);
                intent.putExtra("Entity", appData);
                intent.putExtra("title", "车辆资料添加");
                startActivityForResult(intent, REFRESH_CODE);
            }
        });
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return;
                }
            }
        });
        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // start
                        if (carEntities.get(position).getVehicleState().equals("1")) {
                            displayToast("您当前车辆已是启用状态！");
                            //当前车辆是启用状态 无需提交至服务器
                            return false;
                        }
//                        else if (appData.getList().size() == 1){
//                            displayToast("您当前只有一辆车，不能停用！");
//                            return false;
//                        }
                        else{
                            //当前车辆不是启用状态 则需提交至服务器
                            start(position);
                        }
                        break;
                    case 1:
                        // delete
                        showDeleteDialog(position);
                        break;
                    case 2:
                        // edit
                        Intent intent = new Intent(DriverCertificationActivity.this, VehicleInformationEditingActivity.class);
                        intent.putExtra("Entity", appData);
                        intent.putExtra("title", "编辑车辆资料");
                        intent.putExtra("position", position);
                        startActivityForResult(intent, REFRESH_CODE);
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
    }

    SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            int width = 0;
            // Create different menus depending on the view type
            switch (menu.getViewType()) {
                case 0:
                    width = 0;
                    break;
                case 1:
                    width = 60;
                    break;
            }

            // create "start" item
            SwipeMenuItem startItem = new SwipeMenuItem(
                    getApplicationContext());
            startItem.setWidth(DeviceUtil.dp2px(DriverCertificationActivity.this, width));
            startItem.setTitle("启用");
            startItem.setIcon(R.mipmap.icon_start);
            startItem.setTitleSize(12);
            startItem.setTitleColor(getResources().getColor(R.color.actionsheet_blue));
            menu.addMenuItem(startItem);

            // create "delete" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getApplicationContext());
            deleteItem.setIcon(R.mipmap.icon_delete);
            deleteItem.setWidth(DeviceUtil.dp2px(DriverCertificationActivity.this, 60));
            deleteItem.setTitle("删除");
            deleteItem.setTitleSize(12);
            deleteItem.setTitleColor(getResources().getColor(R.color.text_color_black));
            menu.addMenuItem(deleteItem);

            // create "edit" item
            SwipeMenuItem editItem = new SwipeMenuItem(
                    getApplicationContext());
            editItem.setIcon(R.mipmap.icon_edit);
            editItem.setWidth(DeviceUtil.dp2px(DriverCertificationActivity.this, 60));
            editItem.setTitle("编辑");
            editItem.setTitleSize(12);
            editItem.setTitleColor(getResources().getColor(R.color.actionsheet_blue));
            menu.addMenuItem(editItem);
        }
    };

    /**
     * 启用车辆
     *
     * @param position 车牌号
     */
    private void start(final int position) {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("plateNumber", carEntities.get(position).getPlateNumber());
            new DataUtils(this, params).startVehicle(new DataUtils.DataBack<APPData<Object>>() {
                @Override
                public void getData(APPData<Object> data) {
                    try {
                        displayToast(data.getMessage());
                        for (int i = 0; i < carEntities.size(); i++) {
                            if (i == position) {   //车辆启用状态 0禁用、1启用
                                carEntities.get(i).setVehicleState("1");
                            } else {
                                carEntities.get(i).setVehicleState("0");
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        getDriverCertificationInfor();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void getError(Throwable e) {
                    super.getError(e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void showDeleteDialog(final int postion) {
        new TowButtonDialog(this, getString(R.string.delete_vehicle), "取消",
                "确定", new TowButtonDialog.Listener() {
            @Override
            public void onLeftClick() {
            }

            @Override
            public void onRightClick() {
                if ("2".equals(appData.getEntity().getDriverAuthState()) && "1".equals(carEntities.get(postion).getVehicleState())) {
                    //认证司机并且当前车正在启用中 不能删除
                    displayToast("不能删除正在启用的车辆!");
                } else {
                    // 可以随便删除车辆
                    deleteVehicle(postion);
                }
            }
        }).show();
    }

    /**
     * 删除车辆信息
     */
    private void deleteVehicle(final int position) {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("plateNumber", carEntities.get(position).getPlateNumber());
            new DataUtils(this, params).deleteVehicle(new DataUtils.DataBack<APPData<Object>>() {
                @Override
                public void getData(APPData<Object> data) {
                    displayToast(data.getMessage());
                    carEntities.remove(position);
                    mAdapter.notifyDataSetChanged();
                    getDriverCertificationInfor();
                }

                @Override
                public void getError(Throwable e) {
                    super.getError(e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.ib_back, R.id.tv_perfection_information})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ib_back:
                finish();
                break;
            case R.id.tv_perfection_information:
                // 跳转到完善资料界面
                UIHelper.jumpAct(DriverCertificationActivity.this, DriverCertificationSubmissionActivity.class, appData, false);
                break;
        }
    }

    class MyAdapter extends BaseSwipListAdapter {

        @Override
        public int getCount() {
            return carEntities.size();
        }

        @Override
        public UserAuthenticationEntity getItem(int position) {
            return carEntities.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            // menu type count
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            // current menu type

            if ("1".equals(getItem(position).getVehicleState())) {  //启用
                return 0;
            } else { //停用
                return 1;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(),
                        R.layout.item_car_information, null);
                new ViewHolder(convertView);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            UserAuthenticationEntity myCarEntity = carEntities.get(position);
            holder.tvCarPlateNumber.setText(myCarEntity.getPlateNumber());
            holder.tvCarType.setText("车型：" + (StringUtils.isEmpty(myCarEntity.getVehicleModeName()) ? "未填写" : myCarEntity.getVehicleModeName()));
            holder.tvCarLength.setText("车长：" + (StringUtils.isEmpty(myCarEntity.getVehicleLength()) ? "未填写" : myCarEntity.getVehicleLength()+"米"));
            holder.tvCarSelfWeight.setText("自重：" + (StringUtils.isEmpty(myCarEntity.getVehicleWeight()) ? "未填写" : myCarEntity.getVehicleWeight()+"吨"));
            holder.tvCarLoad.setText("载重：" + (StringUtils.isEmpty(myCarEntity.getVehicleLoad()) ? "未填写" : myCarEntity.getVehicleLoad()+"吨"));
            //车辆启用状态 0禁用、1启用
            if ("1".equals(myCarEntity.getVehicleState())) {
                holder.tvStatus.setText("启用");
                holder.tvStatus.setTextColor(getResources().getColor(R.color.actionsheet_blue));
            } else {
                holder.tvStatus.setText("停用");
                holder.tvStatus.setTextColor(getResources().getColor(R.color.actionsheet_gray));
            }
            return convertView;
        }

        class ViewHolder {
            TextView tvCarPlateNumber;
            TextView tvCarType;
            TextView tvCarLength;
            TextView tvCarSelfWeight;
            TextView tvCarLoad;
            TextView tvStatus;

            public ViewHolder(View view) {
                tvCarPlateNumber = (TextView) view.findViewById(R.id.tv_car_plate_number);
                tvCarType = (TextView) view.findViewById(R.id.tv_car_type);
                tvCarLength = (TextView) view.findViewById(R.id.tv_car_length);
                tvCarSelfWeight = (TextView) view.findViewById(R.id.tv_car_self_weight);
                tvCarLoad = (TextView) view.findViewById(R.id.tv_car_load);
                tvStatus = (TextView) view.findViewById(R.id.tv_status);
                view.setTag(this);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REFRESH_CODE:
                    getDriverCertificationInfor();
                    break;
            }

        }
    }
}
