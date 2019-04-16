package com.sxhalo.PullCoal.ui.freight.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.base.BaseActivity;
import com.sxhalo.PullCoal.base.BaseFragment;
import com.sxhalo.PullCoal.bean.Dictionary;
import com.sxhalo.PullCoal.bean.FilterEntity;
import com.sxhalo.PullCoal.bean.TransportMode;
import com.sxhalo.PullCoal.common.AppConstant;
import com.sxhalo.PullCoal.common.Constant;
import com.sxhalo.PullCoal.dagger.component.ApplicationComponent;
import com.sxhalo.PullCoal.dagger.component.DaggerHttpComponent;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.ui.freight.TransportDetailActivity;
import com.sxhalo.PullCoal.utils.LogUtil;
import com.sxhalo.PullCoal.utils.PaperUtil;
import com.sxhalo.PullCoal.utils.StringUtils;
import com.sxhalo.PullCoal.utils.UIHelper;
import com.sxhalo.PullCoal.weight.AppEmptyView;
import com.sxhalo.PullCoal.weight.popwidow.FilterPopupWindow;
import com.sxhalo.PullCoal.weight.popwidow.SelectAreaPopupWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

public class FreightSearchFragment extends BaseFragment<FreightPresenter> implements FreightContract.View{

    @BindView(R.id.tv_from_area)
    TextView tvFromArea;
    @BindView(R.id.iv_from)
    ImageView ivFrom;
    @BindView(R.id.layout_from)
    RelativeLayout layoutFrom;
    @BindView(R.id.tv_to_area)
    TextView tvToArea;
    @BindView(R.id.iv_to)
    ImageView ivTo;
    @BindView(R.id.layout_to)
    RelativeLayout layoutTo;
    @BindView(R.id.tv_filter)
    TextView tvFilter;
    @BindView(R.id.layout_filter)
    LinearLayout layoutFilter;
    @BindView(R.id.layout_top)
    LinearLayout layoutTop;
    @BindView(R.id.ll)
    LinearLayout ll;
    @BindView(R.id.view)
    View view;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.mRefreshLayout)
    SmartRefreshLayout mRefreshLayout;

    public final int TYPE_START = 0;//出发地
    public final int TYPE_END = 1;//目的地
    private int currentType = 0;//用来判断点击的是出发地还是目的地
    private int ponFreightType;//货运分类排序位置

    private Activity myActivity;
    private List<TransportMode> transports = new ArrayList<TransportMode>();
    private FilterPopupWindow filterPopupWindow;
    private int currentPage = 1;
    // 地址选择空间
    private SelectAreaPopupWindow areaPopupWindow;
    private String startCode = "-1"; //默认全国
    private String endCode = "-1";   //默认全国
    private List<Dictionary> data;

    private Map<String, FilterEntity> map = new HashMap<String, FilterEntity>();
    private boolean flage = false; // 是否需要进度条
    private BaseQuickAdapter<TransportMode, BaseViewHolder> baseQuickAdapter;

    public static Fragment newInstance() {
        return new FreightSearchFragment();
    }

    @Override
    public int getContentLayout() {
        return R.layout.fragment_freight_search;
    }

    @Override
    public void initInjector(ApplicationComponent appComponent) {
        DaggerHttpComponent.builder()
                .applicationComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void bindView(View view, Bundle savedInstanceState) {
        myActivity = getActivity();
        createAreaWindow();
        initView();
    }

    @Override
    public void initData() {
        getData(true);
    }

    /**
     * 数据联网
     * @param flag
     */
    private void getData(boolean flag) {
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
            LogUtil.i("当前获取数据页数", "currentPage = " + currentPage);
            params.put("currentPage", currentPage + "");
            params.put("pageSize", "10");
            mPresenter.getCoalTransportList(mActivity, params,flag);
        } catch (Exception e) {
            LogUtil.e("货运列表联网", e.toString());
        }
    }

    @Override
    public void getCoalTransportList(List<TransportMode> transportModeList, Throwable e) {
        if (transportModeList == null) {
            // 网络连接错误
        } else {
            if (currentPage == 1){  //当是刷新时
                transports.clear();
                baseQuickAdapter.notifyDataSetChanged();
                transports.addAll(transportModeList);
                baseQuickAdapter.notifyDataSetChanged();
                if (baseQuickAdapter.getData().size() < 10){
                    mRefreshLayout.setNoMoreData(true);
                }else{
                    mRefreshLayout.setNoMoreData(false);
                }
            }else{  //当是加载更多时
                if (transportModeList.size() != 0){
                    int index = baseQuickAdapter.getData().size() + 1;
                    transports.addAll(transportModeList);
                    baseQuickAdapter.notifyItemInserted(index);
                }
                if (transportModeList.size() < 10){
                    mRefreshLayout.setNoMoreData(true);
                }
            }
        }
    }

    /**
     * 列表数据点击
     * @param mAdapter
     * @param position
     */
    private void onClickItem(BaseQuickAdapter mAdapter, int position) {
        try {
            String userId = PaperUtil.get("userId", "-1");
            if ("-1".equals(userId)) {
                //未登录点击跳转登录界面
                UIHelper.jumpActLogin(myActivity);
            } else {
                TransportMode transportMode = (TransportMode)mAdapter.getItem(position);
                Intent intent = new Intent(myActivity, TransportDetailActivity.class);
                intent.putExtra("waybillId", transportMode.getTransportId());//货运id
                if ("0".equals(transportMode.getConsultingFee()) || PaperUtil.get("coalSalesId", "-1").equals(transportMode.getCoalSalesId())) {
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

    /**
     * 列表数据赋值
     * @param helper
     * @param item
     */
    private void setViewItem(BaseViewHolder helper, TransportMode item,int position) {
        try {
            View view_line = helper.getView(R.id.layout_divider);
            if (position == 0) {
                view_line.setVisibility(View.GONE);
            } else {
                view_line.setVisibility(View.VISIBLE);
            }
            // 0 短期煤炭货运；1 长期煤炭货运；2 普通货运；
            String transportType = item.getTransportType();
            if ("0".equals(transportType)) {
                helper.getView(R.id.ll_surplus).setVisibility(View.VISIBLE);
                helper.setText(R.id.tv_surplus, item.getSurplusNum());
                helper.setText(R.id.freight_type, " / 煤炭");
            } else if ("1".equals(transportType)) {
                helper.getView(R.id.ll_surplus).setVisibility(View.GONE);
                helper.setText(R.id.freight_type, "    长期货运 / 煤炭");
            } else if ("2".equals(transportType)) {
                helper.setText(R.id.freight_type, " / 普货");

                helper.getView(R.id.ll_surplus).setVisibility(View.VISIBLE);
                helper.setText(R.id.tv_surplus, item.getSurplusNum());
            }
            helper.setText(R.id.pubilshTime, item.getDifferMinute());
            helper.setText(R.id.tv_source, item.getCompanyName());
            helper.setText(R.id.tv_transport_cost, item.getCost());
            helper.setText(R.id.tv_start_address, item.getFromPlace());
            helper.setText(R.id.tv_end_address, item.getToPlace());

            final String userId = PaperUtil.get("userId", "-1");
            helper.getView(R.id.iv_phone).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ("0".equals(item.getConsultingFee())) {
                        //免费信息或者已支付 可以直接打电话
                        callPhone(item);
                    } else {
                        //先判断是否登录
                        if ("-1".equals(userId)) {
                            //未登录 先去登录
                            UIHelper.jumpActLogin(getActivity());
                        } else {
                            //已登录 判断是否支付
                            if ("1".equals(item.getIsPay())) {
                                //已支付 直接打电话
                                callPhone(item);
                            } else {
                                //未支付 弹框提示
                                ((BaseActivity) getActivity()).showPayDialog(item, "1");
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
                helper.getView(R.id.ll_information_free).setVisibility(View.GONE);
                //设置货运标签
                TextView publishTag0 = (TextView) helper.getView(R.id.publishTag0);
                TextView publishTag1 = (TextView) helper.getView(R.id.publishTag1);
                TextView publishTag2 = (TextView) helper.getView(R.id.publishTag2);
                TextView publishTag3 = (TextView) helper.getView(R.id.publishTag3);
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
                ImageView typeImage = (ImageView) helper.getView(R.id.free_type_image);
                TextView typeText = (TextView) helper.getView(R.id.free_type);
                //当前登录用户和发布人同属于一个信息部
                if (PaperUtil.get("coalSalesId", "-1").equals(item.getCoalSalesId())) {
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
                    ((ImageView) helper.getView(R.id.free_type_image)).setImageResource(R.mipmap.icon_about_app);
                    typeText.setText(item.getLicenseMinute());
                    typeText.setTextColor(payColor);
                }

                //收费信息 隐藏货运标签 显示信息提示
                helper.getView(R.id.ll_information_free).setVisibility(View.VISIBLE);
                //隐藏货运标签
                helper.getView(R.id.publishTag0).setVisibility(View.GONE);
                helper.getView(R.id.publishTag1).setVisibility(View.GONE);
                helper.getView(R.id.publishTag2).setVisibility(View.GONE);
                helper.getView(R.id.publishTag3).setVisibility(View.GONE);
            }
            helper.setText(R.id.information_free, payName);
            ((TextView) helper.getView(R.id.information_free)).setTextColor(getResources().getColor(payColor));
        } catch (Exception e) {
            LogUtil.e("货运展示", "货运赋值错误");
        }
    }

    private void callPhone(TransportMode transportMode) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("tel", transportMode.getPublishUserPhone());
        map.put("callType", Constant.CALE_TYPE_FREIGHT);
        map.put("targetId", transportMode.getTransportId());
        UIHelper.showCollTel(myActivity, map, true);
    }

    @OnClick({R.id.layout_from, R.id.layout_to, R.id.layout_filter})
    public void onViewClicked(View view) {
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

    /**
     * RecyclerView 界面初始化
     */
    private void initView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        baseQuickAdapter = new BaseQuickAdapter<TransportMode, BaseViewHolder>(R.layout.view_freight_search_list_item,transports){
            @Override
            protected void convert(BaseViewHolder helper, TransportMode item) {
                int position = helper.getLayoutPosition();
                setViewItem(helper,item,position);
            }
        };
        baseQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                onClickItem(adapter,position);
            }
        });
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                currentPage = 1;
                getData(false);
                //刷新更多玩成
                mRefreshLayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                currentPage ++;
                getData(false);
                //加载更多完成
                mRefreshLayout.finishLoadMore(2000/*,false*/);//传入false表示刷新失败
            }


        });


        AppEmptyView emptyView = new AppEmptyView(getActivity(),new AppEmptyView.ClickDoView() {
            @Override
            public void onClick() {
                currentPage = 1;
                getData(true);
            }
        });
        //添加空视图
        baseQuickAdapter.setEmptyView(emptyView.getEmptyView());
        mRecyclerView.setAdapter(baseQuickAdapter);
    }

    /**
     * 地区筛选布局初始化
     */
    private void createAreaWindow() {
        areaPopupWindow = new SelectAreaPopupWindow(getActivity(), 0);
        areaPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                switch (currentType) {
                    case TYPE_START:
                        PaperUtil.put(AppConstant.SELECT_CODE_KEY, startCode);
                        PaperUtil.put(AppConstant.SELECT_CITY_KEY, tvFromArea.getText().toString());
                        ivFrom.setImageResource(R.mipmap.icon_arrow_down);
                        if (!startCode.equals(StringUtils.isEmpty(areaPopupWindow.getStartCode())?"-1":areaPopupWindow.getStartCode())){
                            startCode = StringUtils.isEmpty(areaPopupWindow.getStartCode())?"-1":areaPopupWindow.getStartCode();
                            flage = true;
                            getData(flage);
                        }
                        break;
                    case TYPE_END:
                        ivTo.setImageResource(R.mipmap.icon_arrow_down);
                        if (!endCode.equals(StringUtils.isEmpty(areaPopupWindow.getEndCode())?"-1":areaPopupWindow.getEndCode())){
                            endCode = StringUtils.isEmpty(areaPopupWindow.getEndCode())?"-1":areaPopupWindow.getEndCode();
                            flage = true;
                            getData(flage);
                        }
                        break;
                }
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
     * 根据筛选条件刷新数据
     *
     * @param map
     */
    private void refreshDataByFilter(Map<String, FilterEntity> map) {
        this.map = map;
        this.flage = true;
        currentPage = 1;
        getData(true);
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
//        if (((MainActivity) getActivity()).getFlage()) {
//            filterEntity0.setChecked(false);
//        } else {
//            filterEntity0.setChecked(true);
//        }
        filterEntity0.dictCode = "";
        filterEntity0.dictValue = "不限";
        FilterEntity filterEntity1 = new FilterEntity();
//        if (((MainActivity) myActivity).getFlage()) {
//            filterEntity1.setChecked(true);
//        } else {
//            filterEntity1.setChecked(false);
//        }
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

}
