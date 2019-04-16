package com.sxhalo.PullCoal.activity;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.db.entity.Dictionary;
import com.sxhalo.PullCoal.model.APPDataList;
import com.sxhalo.PullCoal.model.UserEntity;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.tools.BaseAdapterUtils;
import com.sxhalo.PullCoal.ui.CustomeProgressBar;
import com.sxhalo.PullCoal.ui.FilterPopupWindow;
import com.sxhalo.PullCoal.ui.ResetRatingBar;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.ui.smoothlistview.SmoothListView;
import com.sxhalo.PullCoal.utils.DeviceUtil;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import ch.ielse.view.SwitchView;

import static com.sxhalo.PullCoal.fragment.HomePagerFragment.RECEIVED_ACTION;

/**
 * 司机列表
 * Created by amoldZhang on 2016/12/27.
 */
public class DriverListAcrivity extends BaseActivity implements BaseAdapterUtils.BaseAdapterBack<UserEntity> {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.tv_filter)
    TextView tvFilter;
    @Bind(R.id.drivers_list)
    SmoothListView driversList;
    @Bind(R.id.layout_no_data)
    RelativeLayout relativeLayout;
    @Bind(R.id.tv_all)
    TextView tvAll;
    @Bind(R.id.view)
    View view;
    @Bind(R.id.switchView)
    SwitchView switchView;
    @Bind(R.id.title_bar)
    RelativeLayout layout1;
    @Bind(R.id.layout_filter)
    RelativeLayout layout2;
    @Bind(R.id.tv_ll_screen)
    LinearLayout layoutSearch;
    @Bind(R.id.tv_search)
    TextView tvSearch;

    private MyReceiver myReceiver;

    private FilterPopupWindow filterPopupWindow;
    private List<UserEntity> drivers = new ArrayList<UserEntity>();
    private List<Map<String, String>> stringList = new ArrayList<Map<String, String>>();
    private String[] strings = new String[]{"智能排序", "空闲排序", "接单排序", "信用排序"};
    private BaseAdapterUtils baseAdapterUtils;
    private Drawable drawable;
    private PopupWindow popupWindow;
    private int filterStatus = 0;//筛选条件 1司机空闲状态 2司机接单数 3信用等级
    private boolean isOpened = false;
    private Map<String, FilterEntity> map = new HashMap<String, FilterEntity>();
    private String specialLine;
    private boolean needRefresh = false;


    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_invite_drivers);
    }

    @Override
    protected void initTitle() {
        title.setText("找司机");
        tvSearch.setText("输入司机姓名");
        layoutSearch.setVisibility(View.VISIBLE);
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(RECEIVED_ACTION);
        registerReceiver(myReceiver, filter);
        initView();
    }

    private void createFilter() {
        List<Dictionary> data = new ArrayList<Dictionary>();
        Dictionary sys100005 = OrmLiteDBUtil.getQueryByWhere(Dictionary.class, "dictionary_data_type", new String[]{"sys100005"}).get(0);
        Dictionary sys100006 = OrmLiteDBUtil.getQueryByWhere(Dictionary.class, "dictionary_data_type", new String[]{"sys100006"}).get(0);
        FilterEntity filterEntity1 = new FilterEntity();
        filterEntity1.setChecked(true);
        filterEntity1.dictCode = "";
        filterEntity1.dictValue = "不限";
        FilterEntity filterEntity2 = new FilterEntity();
        filterEntity2.setChecked(true);
        filterEntity2.dictCode = "";
        filterEntity2.dictValue = "不限";
        sys100005.list.add(0, filterEntity1);
        sys100006.list.add(0, filterEntity2);
        data.add(sys100006);
        data.add(sys100005);
            filterPopupWindow = new FilterPopupWindow(this, data, false, false,false, driversList.getHeight());
            filterPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    refreshDataByFilter(filterPopupWindow.getMap());
                }
            });
    }

    @Override
    protected void getData() {
        Drawable basicDrawable = ContextCompat.getDrawable(mContext, R.drawable.nice_spiner_arrow);
        drawable = DrawableCompat.wrap(basicDrawable);
        tvAll.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        initmPopupWindowView();
        getDataPath(1,true);
    }

    private void initView() {
        baseAdapterUtils = new BaseAdapterUtils(this, driversList);
        baseAdapterUtils.setViewItemData(R.layout.driver_item, drivers);
        baseAdapterUtils.setBaseAdapterBack(this);
        switchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOpened = switchView.isOpened();
                specialLine = isOpened ? "1" : "";
            }
        });
        switchView.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(SwitchView view) {
                view.toggleSwitch(true); // or false
                baseAdapterUtils.onRefresh();
            }

            @Override
            public void toggleToOff(SwitchView view) {
                view.toggleSwitch(false); // or true
                baseAdapterUtils.onRefresh();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    /**
     * @param map 添加筛选条件后 根据筛选条件 请求数据
     */
    public void refreshDataByFilter(Map<String, FilterEntity> map) {
        this.map = map;
        baseAdapterUtils.onRefresh();
    }

    /**
     * 添加参数的筛选条件
     *
     * @return
     */
    private LinkedHashMap<String, String> setParams() {
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        if (map.size() > 0) {
            for (Map.Entry<String, FilterEntity> entry : map.entrySet()) {
                if (entry.getKey().equals("车辆长度") && !StringUtils.isEmpty(entry.getValue().dictCode)) {
                    //车辆长度
                    params.put("vehicleLength", entry.getValue().dictCode);
                }
                if (entry.getKey().equals("车辆类型") && !StringUtils.isEmpty(entry.getValue().dictCode)) {
                    //车辆类型
                    params.put("vehicleMode", entry.getValue().dictCode);
                }
            }
        }
        params.put("orderType", filterStatus + "");
        params.put("specialLine", specialLine);
        return params;
    }

    public void getDataPath(int currentPage,boolean flage) {
        try {
            LinkedHashMap<String, String> params = setParams();
            params.put("currentPage", currentPage + "");
            params.put("pageSize", "10");
            new DataUtils(this, params).getDriverList(new DataUtils.DataBack<APPDataList<UserEntity>>() {
                @Override
                public void getData(APPDataList<UserEntity> dataMemager) {
                    try {
                        if (dataMemager == null) {
                            return;
                        }
                        if (dataMemager.getList() != null) {
                            drivers = dataMemager.getList();
                            baseAdapterUtils.refreshData(drivers);
//                            baseAdapterUtils.refreshData(dataMemager.getList());
//                            drivers = baseAdapterUtils.getListData();
                        }
                        showEmptyView(baseAdapterUtils.getCount(), relativeLayout, driversList);
                    } catch (Exception e) {
                        MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                        GHLog.e("好友列表赋值", e.toString());
                    }
                }
            },flage);
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
        }
    }

    @OnClick({R.id.title_bar_left, R.id.tv_search, R.id.tv_all, R.id.layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.tv_search:  //搜索
                UIHelper.showSearch(DriverListAcrivity.this, Constant.Search_Driver);
                break;
            case R.id.tv_all:  //筛选条件
                try {
                    if (popupWindow != null && popupWindow.isShowing()) {
                        dismissDropDown();
                    } else {
                        showDropDown();
                    }
                } catch (Exception e) {
                    MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
                    GHLog.e("popwindow创建与显示", e.toString());
                }
                break;
            case R.id.layout:
                if (filterPopupWindow == null) {
                    createFilter();
                }
                if (!filterPopupWindow.isShowing()) {
                    filterPopupWindow.showAsDropDown(this.view);
                    tvFilter.setTextColor(getResources().getColor(R.color.actionsheet_blue));
                } else {
                    filterPopupWindow.dismiss();
                    tvFilter.setTextColor(getResources().getColor(R.color.black));
                }
                break;
        }
    }

    public void initmPopupWindowView() {
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
        View customView = getLayoutInflater().inflate(R.layout.driver_popview, null);
        // 创建PopupWindow实例,宽度和高度充满
        popupWindow = new PopupWindow(customView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        // 使其聚集
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(false);
        //刷新状态（必须刷新否则无效）
//        popupWindow.update();
        //设置背景透明才能显示
//        popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
        popupWindow.setAnimationStyle(R.style.AnimationFade);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (needRefresh) {
                    needRefresh = false;
                    baseAdapterUtils.onRefresh();
                }
                dismissDropDown();
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
                if (filterStatus == i) {
                    popupWindow.dismiss();
                    return;
                }
                needRefresh = true;
                filterStatus = i;
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
                popupWindow.dismiss();
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


    private boolean isArrowHide = false;
    private static final int MAX_LEVEL = 10000;

    private void animateArrow(boolean shouldRotateUp) {
        int start = shouldRotateUp ? 0 : MAX_LEVEL;
        int end = shouldRotateUp ? MAX_LEVEL : 0;
        ObjectAnimator animator = ObjectAnimator.ofInt(drawable, "level", start, end);
        animator.setInterpolator(new LinearOutSlowInInterpolator());
        animator.start();
    }

    public void dismissDropDown() {
        if (!isArrowHide) {
            animateArrow(false);
        }
        darkenBackground(1f);
    }

    public void showDropDown() {
        if (!isArrowHide) {
            animateArrow(true);
        }
        darkenBackground(0.7f);
        popupWindow.showAsDropDown(view);
    }

    @Override
    public void getItemViewData(BaseAdapterHelper helper, final UserEntity drivers, int pos) {
        try {
            String derverName = "";
            derverName = drivers.getRealName();
            helper.setText(R.id.drivert_name_num, derverName);
            ImageView imageView = (ImageView) helper.getView().findViewById(R.id.recommend_drivder_image);
            // 给 ImageView 设置一个 tag
            imageView.setTag(drivers.getHeadPic());
            // 预设一个图片
            imageView.setImageResource(R.mipmap.main_tab_item);
            // 通过 tag 来防止图片错位
            if (imageView.getTag() != null && imageView.getTag().equals(drivers.getHeadPic())) {
                ((BaseActivity) mContext).getImageManager().loadUrlDerverView(drivers.getHeadPic(), imageView);
            }

            //设置车牌号码
            TextView tvLicencePlate = (TextView) helper.getView().findViewById(R.id.tv_licence_plate);
            if (StringUtils.isEmpty(drivers.getPlateNumber())) {
                tvLicencePlate.setBackgroundDrawable(null);
                tvLicencePlate.setText("未填写");
                tvLicencePlate.setTextColor(getResources().getColor(R.color.setting_alt_text_normal));
            } else {
                tvLicencePlate.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_plate_number));
                tvLicencePlate.setText(StringUtils.setPlateNumber(drivers.getPlateNumber()));
                tvLicencePlate.setTextColor(getResources().getColor(R.color.text_color_plate_number));
            }

            //设置认证百分比
            CustomeProgressBar myProgressBar = (CustomeProgressBar) helper.getView().findViewById(R.id.text_progressbar);
            myProgressBar.setShowText(true);
            myProgressBar.setProgress(Integer.parseInt(StringUtils.isEmpty(drivers.getPercentage()) ? "0" : drivers.getPercentage()));
            ResetRatingBar dirver_ratingBar = (ResetRatingBar) helper.getView().findViewById(R.id.recommend_drivder_ratingbar1);
            dirver_ratingBar.setStar(Integer.valueOf(StringUtils.isEmpty(drivers.getCreditRating()) ? "1" : drivers.getCreditRating()));

            String carLength = drivers.getVehicleLength();
            if (StringUtils.isEmpty(carLength)) {
                carLength = "--  ";
            } else {
                carLength = carLength + "米  ";
            }

            Dictionary sys100005 = OrmLiteDBUtil.getQueryByWhere(Dictionary.class, "dictionary_data_type", new String[]{"sys100005"}).get(0);
            String vehicleModels = "";
            for (FilterEntity filterEntity : sys100005.list) {
                if (filterEntity.dictCode.equals(drivers.getVehicleMode())) {
                    vehicleModels = filterEntity.dictValue;
                    break;
                }
            }
            if (StringUtils.isEmpty(vehicleModels)) {
                vehicleModels = "--  ";
            } else {
                vehicleModels = vehicleModels + "  ";
            }

            String carLoad = drivers.getVehicleLoad();
            if (StringUtils.isEmpty(carLoad)) {
                carLoad = "--";
            } else {
                carLoad = "载重" + carLoad + "吨";
            }
            //车长 、车型、载重
            helper.setText(R.id.drivert_car_content, carLength + vehicleModels + "  " + carLoad);

            ImageView driverStatus = (ImageView) helper.getView().findViewById(R.id.driver_current_status);
            if (drivers.getDriverState().equals("2")) { //忙碌
                driverStatus.setImageResource(R.mipmap.be_busy);
            } else { //空闲
                driverStatus.setImageResource(R.mipmap.free);
            }

            //是否是专线司机
            if (drivers.getSpecialLine().equals("1")) {
                helper.getView().findViewById(R.id.tv_special_line).setVisibility(View.VISIBLE);
            } else {
                helper.getView().findViewById(R.id.tv_special_line).setVisibility(View.GONE);
            }

            helper.setText(R.id.drivert_common_singular, drivers.getCumulative());

            helper.getView().findViewById(R.id.derver_tel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("tel", drivers.getUserMobile());
                    map.put("callType", Constant.CALE_TYPE_DRIVER);
                    map.put("targetId", drivers.getUserId());
                    UIHelper.showCollTel(mContext, map, true);
                }
            });

            //  好友状态判断
            TextView friendIV = (TextView) helper.getView().findViewById(R.id.tv_is_friend);
            if (drivers.getIsFriend().equals("0")) {
                friendIV.setVisibility(View.GONE);
            } else {
                friendIV.setVisibility(View.VISIBLE);
            }
        } catch (NumberFormatException e) {
            GHLog.i("搜索司机赋值", e.toString());
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
        }
    }

    @Override
    public void getOnItemClickListener(int position, QuickAdapter<UserEntity> driversAdapter) {
        if (driversAdapter.getCount() != 0) {
            Intent intent = new Intent();
            intent.setClass(DriverListAcrivity.this, DetaileFriendActivity.class);
            intent.putExtra("UserEntity", driversAdapter.getItem(position - 1));
            startActivity(intent);
        }
    }

    @Override
    public void getOnRefresh(int page) {
        GHLog.i("刷新时", "page = " + page);
        getDataPath(page,false);
    }

    @Override
    public void getOnLoadMore(int page) {
        GHLog.i("加载更多", "page = " + page);
        getDataPath(page,false);
    }

    /**
     * 添加好友或者删除好友时收到通知 需要刷新界面
     */
    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (RECEIVED_ACTION.equals(intent.getAction()) && intent != null) {
                String friendId = intent.getStringExtra("friendId");
                for (int i = 0; i < baseAdapterUtils.getAdapter().getCount(); i++) {
                    UserEntity userEntity = (UserEntity) baseAdapterUtils.getAdapter().getItem(i);
                    if (userEntity.getUserId().equals(friendId)) {
                        userEntity.setIsFriend("1");
                        baseAdapterUtils.getAdapter().set(i, userEntity);
                        baseAdapterUtils.getAdapter().notifyDataSetChanged();
                        break;
                    }
                }
            }
        }
    }
}
