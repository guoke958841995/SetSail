package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.model.APPDataList;
import com.sxhalo.PullCoal.model.SendCarEntity;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.ui.swipelistview.SwipeMenu;
import com.sxhalo.PullCoal.ui.swipelistview.SwipeMenuCreator;
import com.sxhalo.PullCoal.ui.swipelistview.SwipeMenuItem;
import com.sxhalo.PullCoal.ui.swipelistview.SwipeMenuListView;
import com.sxhalo.PullCoal.utils.DeviceUtil;
import com.sxhalo.PullCoal.utils.GHLog;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import butterknife.Bind;
import butterknife.OnClick;

import static com.sxhalo.PullCoal.common.base.Constant.UPDATE_SEND_CAR_ENTITY;
import static com.sxhalo.PullCoal.common.base.Constant.UPDATE_SEND_CAR_LIST;


/**
 * 添加派车
 * Created by liz on 2018/5/15.
 */

public class SendCarActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.map_type)
    TextView mapType;
    @Bind(R.id.iv_no_data)
    ImageView ivNoData;
    @Bind(R.id.listView)
    SwipeMenuListView listView;

    private SwipeMenuCreator creator;
    private QuickAdapter<SendCarEntity> adapter;
    private ArrayList<SendCarEntity> list = new ArrayList<>();
    private int editIndex;//点击编辑的是列表的哪一条位置
    private int number; // 还需派车数

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_send_car);
    }

    @Override
    protected void initTitle() {
        title.setText("派车");
        mapType.setText("确认派车");
        initView();
        number = getIntent().getIntExtra("number",0);
    }

    private void initView() {
        initSwipeMenu();
        adapter = new QuickAdapter<SendCarEntity>(this, R.layout.send_car_item, list) {
            @Override
            protected void convert(BaseAdapterHelper helper, SendCarEntity item, int position) {
                helper.setText(R.id.tv_name, item.getDriverRealName());
                helper.setText(R.id.tv_phone, item.getDriverPhone());
                helper.setText(R.id.tv_plate_number, item.getPlateNumber());
                if (item.isManualAdd()) {
                    helper.getView().findViewById(R.id.tv_edit).setVisibility(View.VISIBLE);
                } else {
                    helper.getView().findViewById(R.id.tv_edit).setVisibility(View.GONE);
                }
                if (item.getDriverAuthState().equals("2")) {
                    helper.getView().findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
                } else {
                    helper.getView().findViewById(R.id.tv_status).setVisibility(View.GONE);
                }
            }
        };
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.getItem(position).isManualAdd()) {
                    editIndex = position;
                    Intent intent = new Intent(SendCarActivity.this, ManualAddActivity.class);
                    intent.putExtra("entity", adapter.getItem(position));
                    startActivityForResult(intent, UPDATE_SEND_CAR_ENTITY);
                }
            }
        });
        listView.setMenuCreator(creator);
        listView.setAdapter(adapter);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                list.remove(list.get(position));
                if (list.size() == 0) {
                    listView.setVisibility(View.GONE);
                    mapType.setVisibility(View.GONE);
                    ivNoData.setVisibility(View.VISIBLE);
                }
                adapter.replaceAll(list);
                displayToast("删除成功！");
                return false;
            }
        });
    }

    private SwipeMenuCreator initSwipeMenu() {
        creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(DeviceUtil.dp2px(SendCarActivity.this, 80));
                deleteItem.setTitle("刪除");
                deleteItem.setTitleSize(16);
                deleteItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        return creator;
    }

    @Override
    protected void getData() {

    }

    @OnClick({R.id.title_bar_left, R.id.map_type, R.id.btn_manual, R.id.btn_select})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.map_type:
                if (list.size() > number){
                    displayToast("您当前派车已超出最大派车数");
                    return;
                }
                //确认添加
                completeCar();
                mapType.setClickable(false);
                break;
            case R.id.btn_manual:
                if (list.size() >= number){
                    displayToast("您当前派车已足够拉完预订吨数,不需要再次派车");
                    return;
                }
                //跳转至手动添加界面
                Intent intent = new Intent(this, ManualAddActivity.class);
                intent.putExtra("list", list);
                intent.putExtra("number", number);
                startActivityForResult(intent, UPDATE_SEND_CAR_LIST);
                break;
            case R.id.btn_select:
                if (list.size() >= number){
                    displayToast("您当前派车已足够拉完预订吨数,不需要再次派车");
                    return;
                }
                //跳转至好友选择列表界面
                Intent intent1 = new Intent(this, AddCarDriverActivity.class);
                intent1.putExtra("list", list);
                intent1.putExtra("number", number);
                startActivityForResult(intent1, UPDATE_SEND_CAR_LIST);
                break;
        }
    }

    /**
     * 确认添加派车司机
     */
    private void completeCar() {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("orderNumber", getIntent().getStringExtra("orderNumber"));  //订单编号
            String JsonString = new Gson().toJson(list).replace("\\", "");
            params.put("vehicles", JsonString);
            new DataUtils(this, params).getMyCoalOrderTransportCerate(new DataUtils.DataBack<APPDataList<SendCarEntity>>() {
                @Override
                public void getData(APPDataList<SendCarEntity> dataMemager) {
                    displayToast("添加成功");
                    setResult(RESULT_OK);
                    sendBroadcast();
                    finish();
                }

                @Override
                public void getError(Throwable e) {
                    MyException.uploadExceptionToServer(mContext,e);
                }
            });
        } catch (Exception e) {
            GHLog.e("联网错误", e.toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case UPDATE_SEND_CAR_LIST:
                    list = (ArrayList<SendCarEntity>) data.getSerializableExtra("list");
                    if (list.size() > 0) {
                        ivNoData.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                        mapType.setVisibility(View.VISIBLE);
                    }
                    //从手动添加界面返回的数据
                    adapter.replaceAll(list);
                    break;
                case UPDATE_SEND_CAR_ENTITY:
                    //从编辑界面返回的数据
                    SendCarEntity sendCarEntity = (SendCarEntity) data.getSerializableExtra("entity");
                    list.remove(editIndex);
                    list.add(editIndex, sendCarEntity);
                    adapter.replaceAll(list);
                    break;
            }
        }
    }

    /**
     * 操作成功，发送广播
     */
    private void sendBroadcast() {
        Intent intent = new Intent(Constant.UPDATE_SEND_CAR_ORDER_FRAGMENT + "");
        sendBroadcast(intent);
    }

}
