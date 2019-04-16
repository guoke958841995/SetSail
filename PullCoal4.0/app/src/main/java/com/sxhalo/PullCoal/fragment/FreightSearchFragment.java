package com.sxhalo.PullCoal.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.activity.BaseActivity;
import com.sxhalo.PullCoal.activity.MainActivity;
import com.sxhalo.PullCoal.activity.RegisterAddLoginActivity;
import com.sxhalo.PullCoal.activity.TransportDetailActivity;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.db.entity.Dictionary;
import com.sxhalo.PullCoal.model.APPDataList;
import com.sxhalo.PullCoal.model.TransportMode;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.tools.BaseAdapterUtils;
import com.sxhalo.PullCoal.ui.FilterPopupWindow;
import com.sxhalo.PullCoal.ui.popwin.SelectAreaPopupWindow;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.ui.smoothlistview.SmoothListView;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
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
 * 货运搜索界面展示
 * Created by amoldZhang on 2016/12/6.
 */
public class FreightSearchFragment extends Fragment implements BaseAdapterUtils.BaseAdapterBack<TransportMode> {

    @Bind(R.id.layout_no_data)
    RelativeLayout relativeLayout;
    @Bind(R.id.freight_list)
    SmoothListView freightList;
    @Bind(R.id.tv_filter)
    TextView tvFilter;
    @Bind(R.id.view)
    View view;
    @Bind(R.id.tv_from_area)
    TextView tvFromArea;
    @Bind(R.id.iv_from)
    ImageView ivFrom;
    @Bind(R.id.iv_to)
    ImageView ivTo;
    @Bind(R.id.tv_to_area)
    TextView tvToArea;
    @Bind(R.id.layout_top)
    LinearLayout layout1;
    @Bind(R.id.ll)
    LinearLayout ll;

    public final int TYPE_START = 0;//出发地
    public final int TYPE_END = 1;//目的地
    private int currentType = 0;//用来判断点击的是出发地还是目的地
    private int ponFreightType;//货运分类排序位置

    private Activity myActivity;
    private List<TransportMode> transports = new ArrayList<TransportMode>();
    private BaseAdapterUtils baseAdapterUtils;
    private FilterPopupWindow filterPopupWindow;
    private int currentPage = 1;
    private SelectAreaPopupWindow areaPopupWindow;
    private String startCode;
    private String endCode;
    private List<Dictionary> data;
    private MyRefreshReceiver myRefreshReceiver;

    private Map<String, FilterEntity> map = new HashMap<String, FilterEntity>();
    private boolean flage = false; // 是否需要进度条

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        try {
            view = inflater.inflate(R.layout.fragment_freight_search, container, false);
            ButterKnife.bind(this, view);
        } catch (Exception e) {
            GHLog.e("货运功能界面展示", e.toString());
        }
        return view;
    }

    /**
     * 注册登录成功后刷新界面的广播
     */
    public void registerMyReceiver() {
        myRefreshReceiver = new MyRefreshReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(Constant.REFRESH_CODE + "");
        myActivity.registerReceiver(myRefreshReceiver, filter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        myActivity.unregisterReceiver(myRefreshReceiver);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myActivity = getActivity();
        createAreaWindow();
        registerMyReceiver();
        initData();
        getData(true);
    }


    /**
     * 根据筛选条件刷新数据
     *
     * @param map
     */
    private void refreshDataByFilter(Map<String, FilterEntity> map) {
        this.map = map;
        this.flage = true;
        baseAdapterUtils.onRefresh();
    }

    private void initData() {
        if (((MainActivity) myActivity).getFlage()) {
            tvFromArea.setText(SharedTools.getStringValue(myActivity, "city", "榆林").replace("市", ""));
        }
        baseAdapterUtils = new BaseAdapterUtils(myActivity, freightList);
        baseAdapterUtils.setViewItemData(R.layout.freight_transport_item, transports);
        baseAdapterUtils.setBaseAdapterBack(this);
        //解决第一次进入时，app不能正常筛选
        onHiddenChanged(true);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            if (myActivity == null){
                myActivity = getActivity();
            }
            boolean ifFlage = ((MainActivity) myActivity).getFlage();
            if (ifFlage) {
                if (StringUtils.isEmpty(SharedTools.getStringValue(myActivity, "city", "榆林"))) {
                    tvFromArea.setText("榆林");
                    startCode = "610800000";
                } else {
                    tvFromArea.setText(SharedTools.getStringValue(myActivity, "city", "榆林").replace("市", ""));
                    startCode = SharedTools.getStringValue(myActivity, "adCode", "");
                }
                //市级6101 00000  详细 6101 13000
                if (!StringUtils.isEmpty(startCode)) {
                    startCode = startCode.substring(0, 4) + "00000";  //将当前定位出来的详细地区编号截取到市级状态。
                }
                SharedTools.putStringValue(myActivity, "select_city", "全国");
                SharedTools.putStringValue(myActivity, "select_code", "");
                flage = true;
            } else {
                tvFromArea.setText(SharedTools.getStringValue(myActivity, "select_city", "全国"));
                startCode = SharedTools.getStringValue(myActivity, "select_code", "");
                flage = true;
            }
            if (filterPopupWindow != null) {
                map.clear();
                filterPopupWindow.resetFilter();
                data.set(ponFreightType, getFreightType());
            }
            baseAdapterUtils.onRefresh();
        } else {
            if (getActivity() != null) {
                if (filterPopupWindow != null) {
                    map.clear();
                    filterPopupWindow.resetFilter();
                }
                ((MainActivity) getActivity()).setFlage(false);
            }
        }
    }

    public void getData(boolean flage) {
        try {
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("freeInfor", "0");
            if (map.size() > 0) {
                for (Map.Entry<String, FilterEntity> entry : map.entrySet()) {
                    if (entry.getKey().equals("信息类型") && !StringUtils.isEmpty(entry.getValue().dictCode)) {
                        //信息类型 免费信息 0：全部 1：免费 2：收费
                        params.put("freeInfor", entry.getValue().dictCode);
                    }
                    if (entry.getKey().equals("排序") && !StringUtils.isEmpty(entry.getValue().dictCode)) {
                        //排序 0：综合排序 1：时间排序 2：价格排序
                        params.put("orderType", entry.getValue().dictCode);
                    }
                    if (entry.getKey().equals("货运分类") && !StringUtils.isEmpty(entry.getValue().dictCode)) {
                        //货运分类
                        params.put("transportType", entry.getValue().dictCode);
                    }
                    if (entry.getKey().equals("路线分类") && !StringUtils.isEmpty(entry.getValue().dictCode)) {
                        //路线分类  路线类型 1：长期货运 2：短期货运
                        params.put("routeType", entry.getValue().dictCode);
                    }
                    if (entry.getKey().equals("车辆类型") && !StringUtils.isEmpty(entry.getValue().dictCode)) {
                        //车辆类型
                        params.put("vehicleMode", entry.getValue().dictCode);
                    }
                    if (entry.getKey().equals("车辆长度") && !StringUtils.isEmpty(entry.getValue().dictCode)) {
                        //车辆长度
                        params.put("vehicleLength", entry.getValue().dictCode);
                    }
                }
            }
            if (!StringUtils.isEmpty(startCode) && !startCode.equals("-1")) {
                params.put("fromPlace", startCode);
            }
            if (!StringUtils.isEmpty(endCode) && !endCode.equals("-1")) {
                params.put("toPlace", endCode);
            }
            GHLog.i("当前获取数据页数", "currentPage = " + currentPage);
            params.put("currentPage", currentPage + "");
            params.put("pageSize", "10");
            new DataUtils(getActivity(), params).getCoalTransportList(new DataUtils.DataBack<APPDataList<TransportMode>>() {
                @Override
                public void getData(APPDataList<TransportMode> appDataList) {
                    if (appDataList == null) {
                        return;
                    }
                    if (appDataList.getList() == null) {
                        transports = new ArrayList<TransportMode>();
                    } else {
                        transports = appDataList.getList();
                    }
                    baseAdapterUtils.refreshData(transports);
                    ((BaseActivity) myActivity).showEmptyView(baseAdapterUtils.getCount(), relativeLayout, freightList);
                }

                @Override
                public void getError(Throwable e) {
                    ((BaseActivity) myActivity).showEmptyView(baseAdapterUtils.getCount(), relativeLayout, freightList);
                }
            },flage);
        } catch (Exception e) {
            GHLog.e("货运列表联网", e.toString());
        }
    }


    @Override
    public void getItemViewData(BaseAdapterHelper helper, final TransportMode item, int pos) {
        try {
            View view_line = helper.getView().findViewById(R.id.layout_divider);
            if (pos == 0) {
                view_line.setVisibility(View.GONE);
            } else {
                view_line.setVisibility(View.VISIBLE);
            }
            // 0 短期煤炭货运；1 长期煤炭货运；2 普通货运；
            String transportType = item.getTransportType();
            if ("0".equals(transportType)) {
                helper.getView().findViewById(R.id.ll_surplus).setVisibility(View.VISIBLE);
                helper.setText(R.id.tv_surplus, item.getSurplusNum());
                helper.setText(R.id.freight_type, " / 煤炭");
            } else if ("1".equals(transportType)) {
                helper.getView().findViewById(R.id.ll_surplus).setVisibility(View.GONE);
                helper.setText(R.id.freight_type, "    长期货运 / 煤炭");
            } else if ("2".equals(transportType)) {
                helper.setText(R.id.freight_type, " / 普货");

                helper.getView().findViewById(R.id.ll_surplus).setVisibility(View.VISIBLE);
                helper.setText(R.id.tv_surplus, item.getSurplusNum());
            }
            helper.setText(R.id.pubilshTime, item.getDifferMinute());
            helper.setText(R.id.tv_source, item.getCompanyName());
            helper.setText(R.id.tv_transport_cost, item.getCost());
            helper.setText(R.id.tv_start_address, item.getFromPlace());
            helper.setText(R.id.tv_end_address, item.getToPlace());

            final String userId = SharedTools.getStringValue(myActivity, "userId", "-1");
            helper.getView().findViewById(R.id.iv_phone).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ("0".equals(item.getConsultingFee())) {
                        //免费信息或者已支付 可以直接打电话
                        callPhone(item);
                    } else {
                        //先判断是否登录
                        if ("-1".equals(userId)) {
                            //未登录 先去登录
                            UIHelper.jumpActLogin(myActivity, false);
                        } else {
                            //已登录 判断是否支付
                            if ("1".equals(item.getIsPay())) {
                                //已支付 直接打电话
                                callPhone(item);
                            } else {
                                //未支付 弹框提示
                                ((BaseActivity) myActivity).showPayDialog(item, "1");
                            }
                        }
                    }
                }
            });
            /****************************信息费********************************/

            String payName;
            int payColor;
            //当资讯信息是免费的
            if ("0".equals(item.getConsultingFee())) {
                payName = "免费信息";
                payColor = R.color.blue;
                //免费信息 隐藏信息提示 显示货运标签
                helper.getView().findViewById(R.id.ll_information_free).setVisibility(View.GONE);
                //设置货运标签
                TextView publishTag0 = (TextView) helper.getView().findViewById(R.id.publishTag0);
                TextView publishTag1 = (TextView) helper.getView().findViewById(R.id.publishTag1);
                TextView publishTag2 = (TextView) helper.getView().findViewById(R.id.publishTag2);
                TextView publishTag3 = (TextView) helper.getView().findViewById(R.id.publishTag3);
                String publishTag = item.getPublishTag() == "" ? null : item.getPublishTag();
                if (!StringUtils.isEmpty(publishTag)) {
                    String[] strings = publishTag.split(",");
                    int size = strings.length;
                    if (strings.length > 0) {
                        if (size >= 1 && !StringUtils.isEmpty(strings[0])) {
                            publishTag0.setVisibility(View.VISIBLE);
                            publishTag0.setText(strings[0]);
                        } else {
                            publishTag0.setVisibility(View.GONE);
                        }
                        if (size >= 2 && !StringUtils.isEmpty(strings[1])) {
                            publishTag1.setVisibility(View.VISIBLE);
                            publishTag1.setText(strings[1]);
                        } else {
                            publishTag1.setVisibility(View.GONE);
                        }
                        if (size >= 3 && !StringUtils.isEmpty(strings[2])) {
                            publishTag2.setVisibility(View.VISIBLE);
                            publishTag2.setText(strings[2]);
                        } else {
                            publishTag2.setVisibility(View.GONE);
                        }
                        if (size >= 4 && !StringUtils.isEmpty(strings[3])) {
                            publishTag3.setVisibility(View.VISIBLE);
                            publishTag3.setText(strings[3]);
                        } else {
                            publishTag3.setVisibility(View.GONE);
                        }
                    }
                } else {
                    publishTag0.setVisibility(View.GONE);
                    publishTag1.setVisibility(View.GONE);
                    publishTag2.setVisibility(View.GONE);
                    publishTag3.setVisibility(View.GONE);
                }
            } else {
                ImageView typeImage = (ImageView) helper.getView().findViewById(R.id.free_type_image);
                TextView typeText = (TextView) helper.getView().findViewById(R.id.free_type);
                //当前登录用户和发布人同属于一个信息部
                if (SharedTools.getStringValue(myActivity, "coalSalesId", "-1").equals(item.getCoalSalesId())) {
                    payName = "¥" + (Double.valueOf(item.getConsultingFee()) / 100) + "收费信息";
                    payColor = R.color.actionsheet_red;
                    typeImage.setImageResource(R.mipmap.icon_payment_invalid);
                    typeText.setText("发布人：" + item.getPublishUser());
                    typeText.setTextColor(getResources().getColor(R.color.actionsheet_gray));
                } else if ("1".equals(item.getIsPay())) {  //0未支付，1已支付
                    //当资讯信息已经付过费
                    payName = "(已支付)收费信息";
                    payColor = R.color.actionsheet_red;
                    typeImage.setImageResource(R.mipmap.icon_buyer_app);
                    typeText.setText(item.getLicenseMinute());
                    typeText.setTextColor(payColor);
                } else {
                    //当资讯信息尚未付费
                    payName = "¥" + (Double.valueOf(item.getConsultingFee()) / 100) + "收费信息";
                    payColor = R.color.actionsheet_red;
                    ((ImageView) helper.getView().findViewById(R.id.free_type_image)).setImageResource(R.mipmap.icon_about_app);
                    typeText.setText(item.getLicenseMinute());
                    typeText.setTextColor(payColor);
                }

                //收费信息 隐藏货运标签 显示信息提示
                helper.getView().findViewById(R.id.ll_information_free).setVisibility(View.VISIBLE);
                //隐藏货运标签
                helper.getView().findViewById(R.id.publishTag0).setVisibility(View.GONE);
                helper.getView().findViewById(R.id.publishTag1).setVisibility(View.GONE);
                helper.getView().findViewById(R.id.publishTag2).setVisibility(View.GONE);
                helper.getView().findViewById(R.id.publishTag3).setVisibility(View.GONE);
            }
            helper.setText(R.id.information_free, payName);
            ((TextView) helper.getView().findViewById(R.id.information_free)).setTextColor(getResources().getColor(payColor));
        } catch (Exception e) {
            GHLog.e("货运展示", "货运赋值错误");
        }
    }

    private void callPhone(TransportMode transportMode) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("tel", transportMode.getPublishUserPhone());
        map.put("callType", Constant.CALE_TYPE_FREIGHT);
        map.put("targetId", transportMode.getTransportId());
        UIHelper.showCollTel(myActivity, map, true);
    }

    @Override
    public void getOnItemClickListener(int position, QuickAdapter<TransportMode> mAdapter) {
        try {
            String userId = SharedTools.getStringValue(myActivity, "userId", "-1");
            if ("-1".equals(userId)) {
                //未登录点击跳转登录界面
                UIHelper.jumpActLogin(myActivity, false);
            } else {
                TransportMode transportMode = mAdapter.getItem(position - 1);
                Intent intent = new Intent(myActivity, TransportDetailActivity.class);
                intent.putExtra("waybillId", mAdapter.getItem(position - 1).getTransportId());//货运id
                if ("0".equals(transportMode.getConsultingFee()) || SharedTools.getStringValue(myActivity, "coalSalesId", "-1").equals(transportMode.getCoalSalesId())) {
                    //免费信息或者已支付 可以直接查看详情
                    startActivity(intent);
                } else {
                    //不免费
                    //已登录 判断是否支付
                    if ("1".equals(transportMode.getIsPay())) {
                        //已支付 直接查看
                        startActivity(intent);
                    } else {
                        //未支付 弹框提示
                        ((BaseActivity) myActivity).showPayDialog(transportMode, "1");
                    }
                }
            }
        } catch (Exception e) {
            Log.i("煤炭列表点击", e.toString());
        }
    }

    @Override
    public void getOnRefresh(int page) {
        this.currentPage = page;
        getData(flage);
        flage = false;
    }

    @Override
    public void getOnLoadMore(int page) {
        this.currentPage = page;
        getData(flage);
        flage = false;
    }

    @OnClick({R.id.layout_filter, R.id.layout_from, R.id.layout_to})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.layout_filter:
                if (filterPopupWindow == null) {
                    createFilterWindow();
                }
                if (!filterPopupWindow.isShowing()) {
                    filterPopupWindow.showAsDropDown(this.view);
                    tvFilter.setTextColor(getResources().getColor(R.color.actionsheet_blue));
                } else {
                    filterPopupWindow.dismiss();
                    tvFilter.setTextColor(getResources().getColor(R.color.black));
                }
                break;
            case R.id.layout_from:
                currentType = TYPE_START;
                areaPopupWindow.showPopupWindow(tvFromArea, currentType, this.view);
                ivFrom.setImageResource(R.mipmap.icon_arrow_up);
                break;
            case R.id.layout_to:
                currentType = TYPE_END;
                areaPopupWindow.showPopupWindow(tvToArea, currentType, this.view);
                ivTo.setImageResource(R.mipmap.icon_arrow_up);
                break;
        }
    }

    private void createAreaWindow() {
        areaPopupWindow = new SelectAreaPopupWindow((BaseActivity) myActivity, 0);
        areaPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                switch (currentType) {
                    case TYPE_START:
                        ivFrom.setImageResource(R.mipmap.icon_arrow_down);
                        break;
                    case TYPE_END:
                        ivTo.setImageResource(R.mipmap.icon_arrow_down);
                        break;
                }
                startCode = areaPopupWindow.getStartCode();
                SharedTools.putStringValue(myActivity, "select_code", startCode);
                SharedTools.putStringValue(myActivity, "select_city", tvFromArea.getText().toString());
                endCode = areaPopupWindow.getEndCode();
                flage = true;
                baseAdapterUtils.onRefresh();
            }
        });
    }

    /**
     * 初始化筛选界面
     */
    private void createFilterWindow() {
        try {
            getTapeData();
            filterPopupWindow = new FilterPopupWindow(myActivity, data, false, false,false, ll.getHeight());
            filterPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    refreshDataByFilter(filterPopupWindow.getMap());
                }
            });
        } catch (Exception e) {
            Log.i("", e.toString());
        }

    }

    /**
     * 数据初始化
     *
     * @return
     */
    private void getTapeData() {
        data = new ArrayList<Dictionary>();
        try {
            //信息类型
            Dictionary dictionary0 = getInformationType();
            //排序
            Dictionary dictionary1 = getOrderDictonary();
            //货运分类
            Dictionary dictionary2 = getFreightType();
            //路线分类
            Dictionary dictionary3 = getRouteType();
            //车辆类型
            Dictionary dictionary4 = OrmLiteDBUtil.getQueryByWhere(Dictionary.class, "dictionary_data_type", new String[]{"sys100005"}).get(0);
            FilterEntity filterEntity1 = new FilterEntity();
            filterEntity1.setChecked(true);
            filterEntity1.dictCode = "";
            filterEntity1.dictValue = "不限";
            dictionary4.list.add(0, filterEntity1);

            //车辆长度
            Dictionary dictionary5 = OrmLiteDBUtil.getQueryByWhere(Dictionary.class, "dictionary_data_type", new String[]{"sys100006"}).get(0);
            FilterEntity filterEntity2 = new FilterEntity();
            filterEntity2.setChecked(true);
            filterEntity2.dictCode = "";
            filterEntity2.dictValue = "不限";
            dictionary5.list.add(0, filterEntity2);
            data.add(0, dictionary0);
            data.add(1, dictionary1);
            data.add(2, dictionary2);
            data.add(3, dictionary3);
            data.add(4, dictionary4);
            data.add(5, dictionary5);
            ponFreightType = 2; //标记货运分类的排序位置
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加路线筛选条目
     * 路线类型1：长期货运 2：短期货运
     *
     * @return
     */
    private Dictionary getRouteType() {
        Dictionary freightType = new Dictionary();
        freightType.title = "路线分类";
        FilterEntity filterEntity0 = new FilterEntity();
        filterEntity0.setChecked(true);
        filterEntity0.dictCode = "";
        filterEntity0.dictValue = "不限";
        FilterEntity filterEntity1 = new FilterEntity();
        filterEntity1.dictCode = "1";
        filterEntity1.dictValue = "长期";
        FilterEntity filterEntity2 = new FilterEntity();
        filterEntity2.dictCode = "2";
        filterEntity2.dictValue = "短期";
        freightType.list = new ArrayList<FilterEntity>();
        freightType.list.add(0, filterEntity0);
        freightType.list.add(1, filterEntity1);
        freightType.list.add(2, filterEntity2);
        return freightType;
    }

    /**
     * 添加排序筛选条目
     * 0：综合排序 1：时间排序 2：价格排序
     *
     * @return
     */
    private Dictionary getOrderDictonary() {
        Dictionary dictionary = new Dictionary();
        dictionary.title = "排序";
        FilterEntity filterEntity0 = new FilterEntity();
        filterEntity0.setChecked(true);
        filterEntity0.dictCode = "0";
        filterEntity0.dictValue = "综合排序";
        FilterEntity filterEntity1 = new FilterEntity();
        filterEntity1.dictCode = "1";
        filterEntity1.dictValue = "时间排序";
        FilterEntity filterEntity2 = new FilterEntity();
        filterEntity2.dictCode = "2";
        filterEntity2.dictValue = "价格排序";
        dictionary.list = new ArrayList<FilterEntity>();
        dictionary.list.add(0, filterEntity0);
        dictionary.list.add(1, filterEntity1);
        dictionary.list.add(2, filterEntity2);
        return dictionary;
    }

    /**
     * 添加货运分类筛选条目
     * 货运类型1：煤炭货运 2：普通货运
     *
     * @return
     */
    private Dictionary getFreightType() {
        Dictionary freightType = new Dictionary();
        freightType.title = "货运分类";
        FilterEntity filterEntity0 = new FilterEntity();
        if (((MainActivity) getActivity()).getFlage()) {
            filterEntity0.setChecked(false);
        } else {
            filterEntity0.setChecked(true);
        }
        filterEntity0.dictCode = "";
        filterEntity0.dictValue = "不限";
        FilterEntity filterEntity1 = new FilterEntity();
        if (((MainActivity) myActivity).getFlage()) {
            filterEntity1.setChecked(true);
        } else {
            filterEntity1.setChecked(false);
        }
        filterEntity1.dictCode = "1";
        filterEntity1.dictValue = "煤炭";
        FilterEntity filterEntity2 = new FilterEntity();
        filterEntity2.dictCode = "2";
        filterEntity2.dictValue = "普货";
        freightType.list = new ArrayList<FilterEntity>();
        freightType.list.add(0, filterEntity0);
        freightType.list.add(1, filterEntity1);
        freightType.list.add(2, filterEntity2);
        return freightType;
    }

    /**
     * 添加信息类型筛选条件
     *
     * @return
     */
    public Dictionary getInformationType() {
        Dictionary informationType = new Dictionary();
        informationType.title = "信息类型";
        FilterEntity filterEntity0 = new FilterEntity();
        filterEntity0.dictCode = "0";
        filterEntity0.dictValue = "全部信息";
        filterEntity0.setChecked(true);

        FilterEntity filterEntity1 = new FilterEntity();
        filterEntity1.dictCode = "1";
        filterEntity1.dictValue = "免费信息";

        FilterEntity filterEntity2 = new FilterEntity();
        filterEntity2.dictCode = "2";
        filterEntity2.dictValue = "收费信息";

        informationType.list = new ArrayList<FilterEntity>();
        informationType.list.add(0, filterEntity0);
        informationType.list.add(1, filterEntity1);
        informationType.list.add(2, filterEntity2);
        return informationType;
    }

    /**
     * 登录成功后的广播接收者
     */
    class MyRefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ((Constant.REFRESH_CODE + "").equals(intent.getAction())) {
                flage = true;
                baseAdapterUtils.onRefresh();
            }
        }
    }
}
