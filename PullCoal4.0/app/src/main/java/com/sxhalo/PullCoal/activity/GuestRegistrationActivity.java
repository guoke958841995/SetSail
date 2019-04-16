package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.model.APPDataList;
import com.sxhalo.PullCoal.model.OperateVisitorRegistration;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.pullrecyclerview.PullFooterLayout;
import com.sxhalo.PullCoal.ui.pullrecyclerview.PullHeadViewLayout;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.amoldzhang.library.PullToRefreshBase;
import com.sxhalo.amoldzhang.library.extras.recyclerview.PullToRefreshRecyclerView;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * 来客登记界面
 * Created by amoldZhang on 2019/2/26.
 */
public class GuestRegistrationActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.pull_to_refresh_recycler_view)
    PullToRefreshRecyclerView pullToRefreshRecyclerView;
    @Bind(R.id.tv_all)
    TextView tvAll;
    @Bind(R.id.tv_select_month)
    TextView tvSelectMonth;
    @Bind(R.id.iv_all)
    ImageView ivAll;
    @Bind(R.id.iv_month)
    ImageView ivMonth;
    @Bind(R.id.view)
    View view;
    //    @Bind(R.id.search_view)
//    SearchView searchView;
    @Bind(R.id.layout_no_data)
    RelativeLayout layoutNoData;


    private int currentPage = 1;  //当前请求页数
    private RecyclerView myRecyclerView; //当前界面显示控件布局
    private CommonAdapter<OperateVisitorRegistration> myAdapter; //当前数据显示控制器
    private List<OperateVisitorRegistration> myListData = new ArrayList<>();
    private List<Map<String, String>> stringList = new ArrayList<Map<String, String>>();
    private String[] strings = new String[]{"全部", "个人"};
    private int consultingType = 0; // 0全部 1个人
    private PopupWindow popupWindowOrder;

    private PopupWindow popupWindowTime;
    private List<Map<String, String>> stringTimeList = new ArrayList<Map<String, String>>();
    private String[] stringTime = new String[]{"创建时间", "更新时间"};
    private int timeRange = 0;//  当前选择位置 0：创建时间 1：更新时间

    private boolean needRefresh = false;//是否需要刷新

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_guest_registration);
    }

    @Override
    protected void initTitle() {
        title.setText("来客登记");
        initView();
        createOrderPopupWindow();
        createPopupWindow();
    }

    @Override
    protected void getData() {
        getDataType(true);
    }

    private void initView() {
        pullToRefreshRecyclerView.setMode(PullToRefreshBase.Mode.BOTH);
        pullToRefreshRecyclerView.setHeaderLayout(new PullHeadViewLayout(this));
        pullToRefreshRecyclerView.setFooterLayout(new PullFooterLayout(this));

        pullToRefreshRecyclerView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        currentPage = 1;
                        getDataType(false);
                    }
                },1000);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        currentPage ++ ;
                        getDataType(false);
                    }
                }, 1500);
            }
        });

        myRecyclerView = pullToRefreshRecyclerView.getRefreshableView();
        myRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        myAdapter = new CommonAdapter<OperateVisitorRegistration>(mContext,R.layout.list_item_guest_registration,myListData){
            @Override
            protected void convert(ViewHolder holder, OperateVisitorRegistration operateVisitorRegistration, int position) {
                setModeCoalInitItem(holder,operateVisitorRegistration,position);
            }
        };

        myAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                OperateVisitorRegistration operateVisitorRegistration = myAdapter.getDatas().get(position);
                onClockItem(operateVisitorRegistration);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        myRecyclerView.setAdapter(myAdapter);
    }

    /**
     * 当前布局联网获取数据
     * @param flage
     */
    private void getDataType(boolean flage) {
        try{
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();

//        //搜索条件
//        if (!StringUtils.isEmpty(searchValue)){
//            params.put("searchValue", searchValue);
//        }
            // 类别 0：全部，1：个人
            if (consultingType == 1){
                params.put("userId", SharedTools.getStringValue(mContext,"userId","-1"));
            }
            // 时间排序方式 0：创建时间 register 1：更新时间 update
            params.put("sortStr",(timeRange == 0 ? "register" : "update"));

            params.put("currentPage", currentPage + "");
            params.put("pageSize", "10");
            new DataUtils(this, params).getUserOperateVisitorList(new DataUtils.DataBack<APPDataList<OperateVisitorRegistration>>() {
                @Override
                public void getData(APPDataList<OperateVisitorRegistration> appDataList) {
                    try {
                        if (appDataList == null) {
                            return;
                        }
                        if (appDataList.getList() == null) {
                            myListData = new ArrayList<OperateVisitorRegistration>();
                        } else {
                            if (appDataList.getList() != null) {
                                if (currentPage == 1){
                                    refreshData(appDataList.getList());
                                }else{
                                    loadData(appDataList.getList());
                                }
                                myListData = myAdapter.getDatas();
                            }
                        }
                        pullToRefreshRecyclerView.onRefreshComplete();
                        myRecyclerView.getAdapter().notifyDataSetChanged();
                        showEmptyView(myAdapter.getItemCount(), layoutNoData, pullToRefreshRecyclerView);

                        if (appDataList.getList().size() <= 10){
                            pullToRefreshRecyclerView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        }else{
                            pullToRefreshRecyclerView.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                    } catch (Exception e) {
                        GHLog.e("货运列表联网", e.toString());
                    }
                }

                @Override
                public void getError(Throwable e) {
                    showEmptyView(myAdapter.getItemCount(), layoutNoData, pullToRefreshRecyclerView);
                }
            },flage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 界面数据刷新
     * @param entityList
     */
    private void refreshData(List<OperateVisitorRegistration> entityList) {
        myListData.clear();
        myRecyclerView.getAdapter().notifyDataSetChanged();
        myListData.addAll(entityList);
    }

    private void loadData(List<OperateVisitorRegistration> entityList) {
        try {
            if (entityList.size() != 0){
                int index = myListData.size() + 1;
                myListData.addAll(entityList);
                myRecyclerView.getAdapter().notifyItemInserted(index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 当前布局点击事件
     * @param operateVisitorRegistration
     */
    private void onClockItem(OperateVisitorRegistration operateVisitorRegistration) {
        operateVisitorRegistration.setRoleId(getIntent().getStringExtra("Entity"));
        UIHelper.jumpActForResult(mContext,GuestRegistrationInfoActivity.class,operateVisitorRegistration,Constant.AREA_CODE);
    }


    /**
     * 界面数据布局条目显示器
     * @param holder
     * @param operateVisitorRegistration
     * @param position
     */
    private void setModeCoalInitItem(ViewHolder holder, final OperateVisitorRegistration operateVisitorRegistration, int position) {
        holder.setText(R.id.customer_name,operateVisitorRegistration.getCustomerName());
        holder.setText(R.id.follow_name,"跟单人：" + operateVisitorRegistration.getFollowName());
        holder.setText(R.id.from_address,operateVisitorRegistration.getFromPlaceName());
        holder.setText(R.id.to_address,operateVisitorRegistration.getToPlaceName());

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
        holder.setText(R.id.dict_value,dictValue);
        holder.setText(R.id.quantity,operateVisitorRegistration.getQuantity());
        holder.setText(R.id.tv_unit,unit);

        holder.setText(R.id.status,"状态：" + (operateVisitorRegistration.getStatus().equals("1")?"已完成":"进行中"));
        holder.setText(R.id.registrar_name,"登记人：" + operateVisitorRegistration.getRegistrarName());
        holder.setText(R.id.update_time,"更新时间：" + operateVisitorRegistration.getDifferMinute());

        holder.getView(R.id.contact_number).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 打电话
                Map<String, String> map = new HashMap<String, String>();
                map.put("tel", operateVisitorRegistration.getContactNumber());
                map.put("callType", Constant.CALE_TYPE_FREIGHT);
                map.put("targetId", operateVisitorRegistration.getRegisterNum());
                UIHelper.showCollTel(mContext, map, false);
            }
        });
    }


    /**
     * 类型筛选
     */
    private void createOrderPopupWindow() {
        for (int i = 0; i < strings.length; i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("title", strings[i]);
            if (i == 0) {
                map.put("show", "true");
            } else {
                map.put("show", "false");
            }
            stringList.add(map);
        }

        // // 获取自定义布局文件pop.xml的视图
        View customView = getLayoutInflater().inflate(R.layout.driver_popview, null, false);
        // 创建PopupWindow实例,宽度和高度充满
        popupWindowOrder = new PopupWindow(customView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        // 使其聚集
        popupWindowOrder.setFocusable(true);
        // 设置允许在外点击消失
        popupWindowOrder.setOutsideTouchable(true);
        //刷新状态（必须刷新否则无效）
        popupWindowOrder.update();
        //设置背景透明才能显示
//        popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
        popupWindowOrder.setBackgroundDrawable(new BitmapDrawable());
        // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
        popupWindowOrder.setAnimationStyle(R.style.AnimationFade);

        popupWindowOrder.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (needRefresh) {
                    needRefresh = false;
                    currentPage = 1;
                    getDataType(true);
                }
                darkenBackground(1f);
                ivAll.setImageResource(R.mipmap.icon_arrow_down);
            }
        });
        /** 在这里可以实现自定义视图的功能 */
        ListView lv = (ListView) customView.findViewById(R.id.popview_listview);
        final QuickAdapter<Map<String, String>> adapter = new QuickAdapter<Map<String, String>>(this, R.layout.popupwindow_list_item, stringList) {
            @Override
            protected void convert(BaseAdapterHelper helper, Map<String, String> item, int position) {
                helper.setText(R.id.tv_list_item, item.get("title"));
                if (item.get("show").equals("true")) {
                    helper.getView().findViewById(R.id.img_select).setVisibility(View.VISIBLE);
                } else {
                    helper.getView().findViewById(R.id.img_select).setVisibility(View.GONE);
                }
            }
        };
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (consultingType == i) {
                    popupWindowOrder.dismiss();
                    return;
                }
                needRefresh = true;
                consultingType = i;
                for (int k = 0; k < stringList.size(); k++) {
                    Map<String, String> map = new HashMap<String, String>();
                    if (k == i) {
                        map.put("show", "true");
                    } else {
                        map.put("show", "false");
                    }
                    map.put("title", stringList.get(k).get("title"));
                    stringList.set(k, map);
                }
                tvAll.setText(strings[i]);
                adapter.replaceAll(stringList);
                popupWindowOrder.dismiss();
            }
        });
    }

    /**
     * 时间筛选
     */
    private void createPopupWindow() {
        for (int i = 0; i < stringTime.length; i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("title", stringTime[i]);
            if (i == 0) {
                map.put("show", "true");
            } else {
                map.put("show", "false");
            }
            stringTimeList.add(map);
        }

        // // 获取自定义布局文件pop.xml的视图
        View customView = getLayoutInflater().inflate(R.layout.driver_popview, null, false);
        // 创建PopupWindow实例,宽度和高度充满
        popupWindowTime = new PopupWindow(customView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        // 使其聚集
        popupWindowTime.setFocusable(true);
        // 设置允许在外点击消失
        popupWindowTime.setOutsideTouchable(true);
        //刷新状态（必须刷新否则无效）
        popupWindowTime.update();
        //设置背景透明才能显示
//        popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
        popupWindowTime.setBackgroundDrawable(new BitmapDrawable());
        // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
        popupWindowTime.setAnimationStyle(R.style.AnimationFade);

        popupWindowTime.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (needRefresh) {
                    needRefresh = false;
                    currentPage = 1;
                    getDataType(true);
                }
                darkenBackground(1f);
                ivMonth.setImageResource(R.mipmap.icon_arrow_down);
            }
        });
        /** 在这里可以实现自定义视图的功能 */
        ListView lv = (ListView) customView.findViewById(R.id.popview_listview);
        final QuickAdapter<Map<String, String>> adapter = new QuickAdapter<Map<String, String>>(this, R.layout.popupwindow_list_item, stringTimeList) {
            @Override
            protected void convert(BaseAdapterHelper helper, Map<String, String> item, int position) {
                helper.setText(R.id.tv_list_item, item.get("title"));
                if (item.get("show").equals("true")) {
                    helper.getView().findViewById(R.id.img_select).setVisibility(View.VISIBLE);
                } else {
                    helper.getView().findViewById(R.id.img_select).setVisibility(View.GONE);
                }
            }
        };
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (timeRange == i) {
                    popupWindowTime.dismiss();
                    return;
                }
                needRefresh = true;
                timeRange = i;
                for (int k = 0; k < stringTimeList.size(); k++) {
                    Map<String, String> map = new HashMap<String, String>();
                    if (k == i) {
                        map.put("show", "true");
                    } else {
                        map.put("show", "false");
                    }
                    map.put("title", stringTimeList.get(k).get("title"));
                    stringTimeList.set(k, map);
                }
                tvSelectMonth.setText(stringTime[i]);
                adapter.replaceAll(stringTimeList);
                popupWindowTime.dismiss();
            }
        });
    }

    /**
     * 改变背景颜色
     */
    private void darkenBackground(Float bgcolor) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgcolor;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }


    @OnClick({R.id.title_bar_left, R.id.rl_all_type,R.id.rl_select})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.rl_all_type:
                ivAll.setImageResource(R.mipmap.icon_arrow_up);
                if (popupWindowOrder.isShowing()) {
                    popupWindowOrder.dismiss();
                }else {
                    popupWindowOrder.showAsDropDown(this.view);
                    darkenBackground(0.7f);
                }
                break;
            case R.id.rl_select:
                ivMonth.setImageResource(R.mipmap.icon_arrow_up);
                if (popupWindowTime.isShowing()) {
                    popupWindowTime.dismiss();
                } else {
                    popupWindowTime.showAsDropDown(this.view);
                    darkenBackground(0.7f);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode){
                case Constant.AREA_CODE: //返回刷新
                    getDataType(true);
                    break;
            }
        }
    }
}
