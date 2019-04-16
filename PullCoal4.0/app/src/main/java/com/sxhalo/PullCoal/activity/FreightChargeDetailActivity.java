package com.sxhalo.PullCoal.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.enums.RoadClass;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLink;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapNaviStep;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.view.RouteOverLay;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.FreightChargeEntity;
import com.sxhalo.PullCoal.tools.map.AMapUtil;
import com.sxhalo.PullCoal.ui.NoScrollGridView;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.ui.slidinguppanel.SlidingUpPanelLayout;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;
import com.sxhalo.PullCoal.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 运费详情
 * Created by liz on 2017/11/22.
 *  货运费用计算:
 *   1.从货运详情信息进行计算时，按照三个点进行综合路径规划，计算距离和高速费用；
 *   2.从货运单进行计算时，仅按照出发地、目的地进行路径规划的距离计算；
 */

public class FreightChargeDetailActivity extends BaseActivity implements AMapNaviListener , AMapLocationListener {

    @Bind(R.id.title)
    TextView title;

    @Bind(R.id.sliding_layout)
    SlidingUpPanelLayout slidingLayout;

    @Bind(R.id.map)
    MapView mRouteMapView;

    @Bind(R.id.dravg_ll_bottom)
    LinearLayout dragLLBottom;

    @Bind(R.id.route_gridview)
    NoScrollGridView routeGridview;
    @Bind(R.id.charge_count)
    TextView chargeCount;
    @Bind(R.id.road_toll)
    TextView roadToll;
    @Bind(R.id.oilConsumption)
    TextView oilConsumption;

    @Bind(R.id.load_price)
    TextView loadPrice;
    @Bind(R.id.unload_price)
    TextView unloadPrice;
    @Bind(R.id.information_price)
    TextView informationPrice;
    @Bind(R.id.total_cost)
    TextView totalCost;


    @Bind(R.id.cargo_weight)
    TextView cargoWeight;
    @Bind(R.id.cargo_price)
    TextView cargoPrice;
    @Bind(R.id.gross_income)
    TextView grossIncome;

    @Bind(R.id.gross_profit)
    TextView grossProfit;
    @Bind(R.id.tv_freight_per_kilometer)
    TextView tvFreightPerKilometer;

    /**
     * 导航对象(单例)
     */
    private AMapNavi mAMapNavi;
    private AMap mAmap;
    private NaviLatLng endLatlng = new NaviLatLng(39.955846, 116.352765);
    private NaviLatLng startLatlng = new NaviLatLng(34.205164, 108.991739);  //  34.205164,108.991739
    private List<NaviLatLng> startList = new ArrayList<NaviLatLng>();
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private UiSettings mUiSettings;
    private String startText;  //出发地名称
    private String endText;   //目的地名称

    private List<Map<String, String>> routeList = new ArrayList<Map<String, String>>();
    private List<Map<String, String>> passingList = new ArrayList<Map<String, String>>();
    private QuickAdapter<Map<String, String>> gridViewAdapter;
    /**
     * 途径点坐标集合
     */
    private List<NaviLatLng> mWayPointList = new ArrayList<NaviLatLng>();
    /**
     * 终点坐标集合［建议就一个终点］
     */
    private List<NaviLatLng> endList = new ArrayList<NaviLatLng>();
    /**
     * 保存当前算好的路线
     */
    private SparseArray<RouteOverLay> routeOverlays = new SparseArray<RouteOverLay>();

    /**
     * 当前用户选中的路线，在下个页面进行导航
     */
    private int routeIndex;
    /**
     * 路线的权值，重合路线情况下，权值高的路线会覆盖权值低的路线
     **/
    private int zindex = 1;

    private boolean flage = false;
    private FreightChargeEntity freightChargeEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViewMapView(savedInstanceState);
    }
    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_freight_charge_detail);
    }

    @Override
    protected void initTitle() {
        title.setText("运费详情");
        initSlidingLayout();
        initView();
    }

    @Override
    protected void getData() { }

    /**
     * 加载拖动布局显示
     */
    private void initSlidingLayout() {
        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        /**
         * EXPANDED,  扩大
         * COLLAPSED, 折叠
         * ANCHORED,  固定
         * HIDDEN,    隐藏
         * DRAGGING   拖动
         */
        slidingLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View panel, float slideOffset) {
//                GHLog.i("PanelSlideListener", "onPanelSlide, offset " + slideOffset);
//                ivTest.setAlpha(slideOffset);
//                backImage.setAlpha(slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                GHLog.i("PanelSlideListener", "onPanelStateChanged = " + newState);
                if (slidingLayout != null){
                    if (slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.DRAGGING){
//                        fragmentTitleBar.setSuperView(0);
                    }
                    if (slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED){
//                        ivTest.setAlpha(0f);
//                        backImage.setVisibility(View.GONE);
//                        backImage.setAlpha(0f);
//                        fragmentTitleBar.setSuperView(1);
                    }
                    if (slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED){
//                        ivTest.setAlpha(1f);
//                        backImage.setVisibility(View.VISIBLE);
//                        backImage.setAlpha(1f);
//                        fragmentTitleBar.setSuperView(2);
                    }
                    if (slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED){
                        if (slidingLayout.getAnchorPoint() == 1.0f) {
                            slidingLayout.setAnchorPoint(0.7f);
                            slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                        }else{
                            slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                        }
                    }
                }
            }
        });
        slidingLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (slidingLayout != null &&
                (slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 加载默认布局显示
     */
    private void initView(){
        dragLLBottom.setVisibility(View.GONE);
        gridViewAdapter = new QuickAdapter<Map<String, String>>(this, R.layout.programme_gridview_item, routeList) {
            @Override
            protected void convert(BaseAdapterHelper helper, Map<String, String> itemMap, int position) {
                if (routeList.size() == 1){
                    helper.getView().findViewById(R.id.one_rouet).setVisibility(View.VISIBLE);
                    helper.setText(R.id.time_h,itemMap.get("Time"));
                    helper.setText(R.id.distance,itemMap.get("Length"));
                }else{
                    helper.getView().findViewById(R.id.more_rouet).setVisibility(View.VISIBLE);
                    String title = "";
                    if (position == 0) {
                        title = "时间最短";
                    }
                    if (position == 1) {
                        title = "拥堵较少";
                    }
                    if (position == 2) {
                        title = "方案3";
                    }

                    TextView titleTV = (TextView) helper.getView().findViewById(R.id.first_scheme_title);
                    LinearLayout rl = (LinearLayout) helper.getView().findViewById(R.id.first_scheme);
                    TextView time = (TextView) helper.getView().findViewById(R.id.first_scheme_time_consuming);
                    TextView distance = (TextView) helper.getView().findViewById(R.id.first_scheme_smileage);

                    titleTV.setText(title);
                    time.setText(itemMap.get("Time"));
                    distance.setText(itemMap.get("Length"));
                    if (routeIndex == position) {
                        titleTV.setBackgroundResource(R.drawable.shape_blue_top);
                        titleTV.setTextColor(Color.parseColor("#ffffff"));//#1e7aea
                        rl.setBackgroundResource(R.drawable.shape_blue_down);
                        time.setTextColor(Color.parseColor("#1e7aea"));//#333333
                        distance.setTextColor(Color.parseColor("#1e7aea"));//#747474
                    } else {
                        titleTV.setBackgroundResource(R.drawable.shape_gray_top);
                        titleTV.setTextColor(Color.parseColor("#747474"));//#747474
                        rl.setBackgroundResource(R.drawable.shape_gray_down);
                        time.setTextColor(Color.parseColor("#333333"));//#333333
                        distance.setTextColor(Color.parseColor("#747474"));//#747474
                    }
                }
            }
        };
        routeGridview.setAdapter(gridViewAdapter);
        routeGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GHLog.i("条目点击", "点击的是" + position);
                routeIndex = position;
                changeRoute();
                gridViewAdapter.replace();
            }
        });
    }

    @OnClick(R.id.title_bar_left)
    public void onViewClicked() {
        finish();
    }

    private void setViewMapView(Bundle bundle) {
        mRouteMapView.onCreate(bundle);
        if (mAmap == null) {
            mAmap = mRouteMapView.getMap();
            setUiMap();
        }
        initLocation();
        if (mLocationClient != null) {
            mLocationClient.startLocation();
        }
    }

    private void setUiMap() {
        mUiSettings = mAmap.getUiSettings();
        MyLocationStyle myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
//        // 自定义定位蓝点图标
//        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.location_marker));
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(Color.argb(180, 3, 145, 255));
        //自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(5);
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(Color.argb(10, 0, 0, 180));
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.anchor(0.5f, 0.5f);
        mAmap.setMyLocationStyle(myLocationStyle);// 将自定义的 myLocationStyle 对象添加到地图上
        mUiSettings.setMyLocationButtonEnabled(false); //设置默认定位按钮是否显示，非必需设置
        mAmap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
    }

    /**
     * 初始化定位相关东西
     */
    private void initLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
    }

    /**
     * 定位成功回调
     * @param aMapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (Utils.isNetWorkConnected(this)) {
            getDataPath(aMapLocation);
        } else {
            Utils.showText(this, 0, getString(R.string.route_no_net));
            finish();
        }
        setData();

        //当 AMapNavi 对象初始化成功后，会进入 onInitNaviSuccess 回调函数，在该回调函数中调用路径规划方法计算路径。
        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
        mAMapNavi.addAMapNaviListener(this);
    }

    /**
     * 获取导航起始位置
     */
    private void getDataPath(AMapLocation aMapLocation) {
        try {
            freightChargeEntity = (FreightChargeEntity) getIntent().getSerializableExtra("Entity");
            //1  三点计算 ; 0  两点计算
            String type = getIntent().getStringExtra("type");
            if (type.equals("1")){
                startLatlng.setLatitude(aMapLocation.getLatitude());
                startLatlng.setLongitude(aMapLocation.getLongitude());

                mWayPointList.clear();
                NaviLatLng wayPoint = new NaviLatLng();
                wayPoint.setLatitude(Double.valueOf(freightChargeEntity.getFromLatitude()));
                wayPoint.setLongitude(Double.valueOf(freightChargeEntity.getFromLongitude()));
                mWayPointList.add(wayPoint);
            }else{
                startLatlng.setLatitude(Double.valueOf(freightChargeEntity.getFromLatitude()));
                startLatlng.setLongitude(Double.valueOf(freightChargeEntity.getFromLongitude()));
            }
            endLatlng.setLatitude(Double.valueOf(freightChargeEntity.getToLatitude()));
            endLatlng.setLongitude(Double.valueOf(freightChargeEntity.getToLongitude()));
            //信息费
            if (freightChargeEntity.getInformationPrice() != 0){
                informationPrice.setText("¥" + freightChargeEntity.getInformationPrice() + "");
            }
            // 装货费
            if (freightChargeEntity.getLoadPrice() != 0){
                loadPrice.setText("¥" + freightChargeEntity.getLoadPrice() + "");
            }
            //卸货费
            if (freightChargeEntity.getUnloadPrice() != 0){
                unloadPrice.setText("¥" + freightChargeEntity.getUnloadPrice() + "");
            }

            cargoWeight.setText(freightChargeEntity.getCargoWeight() + "");
            cargoPrice.setText(freightChargeEntity.getCargoPrice() + "");

            grossIncome.setText(getGrossIncome());
        } catch (Exception e) {
            GHLog.e("获取路径规划起始位置", e.toString());
        }
    }

    /**
     * 初始化路径规划时的目的坐标
     */
    private void setData() {
        try {
            GHLog.e("途经点个数", "mWayPointList = " + mWayPointList.size() + "");

            // 初始化Marker添加到地图
            endList.clear();
            endList.add(endLatlng);

            startList.clear();
            startList.add(startLatlng);
        } catch (Exception e) {
            GHLog.e("初始化路径规划时的目的坐标", e.toString());
        }
    }


    /**
     * 方法:
     * int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute);
     * 参数:
     *
     * @congestion 躲避拥堵
     * @avoidhightspeed 不走高速
     * @cost 避免收费
     * @hightspeed 高速优先
     * @multipleroute 多路径
     * <p>
     * 说明:
     * 以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
     * 注意:
     * 不走高速与高速优先不能同时为true
     * 高速优先与避免收费不能同时为true
     */
    @Override
    public void onInitNaviSuccess() {
        showDelog(this, getString(R.string.route_tips));
        int strategy = 0;
        try {
            //躲避拥堵 不走高速 避免收费 高速优先 多路径
            //不走高速与高速优先不能同时为true
            //高速优先与避免收费不能同时为true
            strategy = mAMapNavi.strategyConvert(true, false, false, true, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean ser = mAMapNavi.calculateDriveRoute(startList, endList, mWayPointList, strategy);
        if (ser == false) {
            dismissDelog();
            displayToast(getString(R.string.route_failed));
            mWayPointList.clear();
        }
        GHLog.e("起点坐标", "（" + startList.get(0).getLatitude() + "," + startList.get(0).getLongitude() + "）");
        GHLog.e("终点坐标", "（" + endList.get(0).getLatitude() + "," + endList.get(0).getLongitude() + "）");
        GHLog.i("路径规划成功", "无起点路径规划" + ser);
    }

    /**
     * 当为多册略规划时会进入此方法
     * 显示路径或开启导航
     *
     * @param ints
     */
    @Override
    public void onCalculateRouteSuccess(int[] ints) {
        //清空上次计算的路径列表。
        clearRoute();
        gridViewAdapter.replaceAll(routeList);
        HashMap<Integer, AMapNaviPath> paths = mAMapNavi.getNaviPaths();
        for (int i = 0; i < ints.length; i++) {
            AMapNaviPath path = paths.get(ints[i]);
            if (path != null) {
                drawRoutes(ints[i], path);
                showRoutesDetaile(i, path);
            }
        }

        routeGridview.setNumColumns(routeList.size());
        gridViewAdapter.addAll(routeList);
        changeRoute();
        zoomToSpan();
        dismissDelog();
        dragLLBottom.setVisibility(View.VISIBLE);
    }

    /**
     * 将返回策略在地图中显示出来
     *
     * @param routeId
     * @param path
     */
    private void drawRoutes(int routeId, AMapNaviPath path) {
        mAmap.moveCamera(CameraUpdateFactory.changeTilt(10));
        RouteOverLay routeOverLay = new RouteOverLay(mAmap, path, this);
        routeOverLay.setTrafficLine(true);
        routeOverLay.addToMap();
        routeOverlays.put(routeId, routeOverLay);
    }

    private void showRoutesDetaile(int index, AMapNaviPath path) {
        String time = AMapUtil.getFriendlyTime(path.getAllTime());//用时
        String distance = AMapUtil.getFriendlyLength(path.getAllLength());//距离
        Map<String, String> routeMap = new HashMap<String, String>();
        routeMap.put("Length", distance);
        routeMap.put("Time", time);
        routeMap.put("TollCost", path.getTollCost() + "");
        routeMap.put("TrafficLights", getTrafficLights(path.getSteps()) + "");
        routeMap.put("highSpeedMileage", getHighSpeedMileage(path.getSteps()) + "");
        routeMap.put("OilConsumption", StringUtils.setNumLenth((getOilConsumption(Float.parseFloat(distance.replace("公里","").replace("米","")))),1) + "");
        routeList.add(index, routeMap);
    }

    /**
     * 计算红绿灯个数
     * @param list
     * @return
     */
    private int getTrafficLights(List<AMapNaviStep> list) {
        int totalLights = 0;
        for (int i = 0; i < list.size(); i++) {
            totalLights += list.get(i).getTrafficLightNumber();
        }
        return totalLights;
    }

    /**
     * 计算高速里程
     * @param list
     * @return
     */
    private String getHighSpeedMileage(List<AMapNaviStep> list) {
        int totalHightSpeedMileage = 0;
        for (AMapNaviStep aMapNaviStep : list) {
            List<AMapNaviLink> aMapNaviLinkList = aMapNaviStep.getLinks();
            for (AMapNaviLink aMapNaviLink : aMapNaviLinkList) {
                if (aMapNaviLink.getRoadClass() == RoadClass.HIGHWAY){
                    totalHightSpeedMileage += aMapNaviLink.getLength();
                }
            }
        }
        float distance = Float.parseFloat(totalHightSpeedMileage + "");
        return StringUtils.setNumLenth(distance/1000,1) ;
    }


    public void changeRoute() {
//        /**
//         * 计算出来的路径只有一条
//         */
//        if (routeOverlays.size() == 1) {
//            Toast.makeText(this, "导航距离:" + (mAMapNavi.getNaviPath()).getAllLength() + "m" + "\n" + "导航时间:" + (mAMapNavi.getNaviPath()).getAllTime() + "s", Toast.LENGTH_SHORT).show();
//            return;
//        }

        if (routeIndex >= routeOverlays.size())
            routeIndex = 0;
        int routeID = routeOverlays.keyAt(routeIndex);
        //突出选择的那条路
        for (int i = 0; i < routeOverlays.size(); i++) {
            int key = routeOverlays.keyAt(i);
            routeOverlays.get(key).setTransparency(0.5f);
        }
        routeOverlays.get(routeID).setTransparency(1.0f);
        /**把用户选择的那条路的权值弄高，使路线高亮显示的同时，重合路段不会变的透明**/
        routeOverlays.get(routeID).setZindex(zindex++);
        String distance = routeList.get(routeIndex).get("Length").replace("公里", "");
        chargeCount.setText("全程共" + routeList.get(routeIndex).get("Length") + ",收费高速里程"+ routeList.get(routeIndex).get("highSpeedMileage") + "公里");
        float highSpeedMileage = Float.parseFloat(routeList.get(routeIndex).get("highSpeedMileage"));
        String highSpeed = StringUtils.setNumLenth(Float.parseFloat( (highSpeedMileage * 2.5) + ""),2);
        roadToll.setText(highSpeed);

        oilConsumption.setText(routeList.get(routeIndex).get("OilConsumption"));

        totalCost.setText(getTotalCost(Float.parseFloat(highSpeed),Float.parseFloat(routeList.get(routeIndex).get("OilConsumption").replace(",",""))));

        tvFreightPerKilometer.setText(getFreightPerKilometer(distance));

        grossProfit.setText(getGrossProfit());

//        //必须告诉AMapNavi 你最后选择的哪条路
//        mAMapNavi.selectRouteId(routeID);

//        Toast.makeText(this, "导航距离:" + (mAMapNavi.getNaviPaths()).get(routeID).getStrategy() + "\n" + "导航时间:" + (mAMapNavi.getNaviPaths()).get(routeID).getAllTime() + "s", Toast.LENGTH_SHORT).show();
    }

    /**
     * 计算油耗
     * @param distance
     * @return
     */
    private float getOilConsumption(float distance){
        float OilConsumption = (distance/100) * freightChargeEntity.getOilConsumption() * freightChargeEntity.getGasPrice();
        return OilConsumption;
    }

    /**
     * 计算总成本
     * @param oilConsumption
     * @return
     */
    private String getTotalCost(float cost,float oilConsumption){
        return (cost + oilConsumption +
                freightChargeEntity.getLoadPrice() +
                freightChargeEntity.getUnloadPrice() +
                freightChargeEntity.getInformationPrice()) + "";
    }

    /**
     * 计算总收入
     * @return
     */
    private String getGrossIncome(){
        return (freightChargeEntity.getCargoWeight() * freightChargeEntity.getCargoPrice()) + "";
    }

    /**
     * 计算毛利润
     * @return
     */
    private String getGrossProfit(){
        return StringUtils.setNumLenth((Float.parseFloat(getGrossIncome()) - Float.parseFloat(totalCost.getText().toString().trim())),2);
    }

    /**
     * 计算每公里的运费
     * @return
     */
    private String getFreightPerKilometer(String distance){
        float price = freightChargeEntity.getCargoPrice()/Float.parseFloat(distance.replace("公里", "").replace("米", ""));
        return ((float)(Math.round(price*100)) / 100) + "";
    }



    /**
     * 清除当前地图上算好的路线
     */
    private void clearRoute() {
        for (int i = 0; i < routeOverlays.size(); i++) {
            RouteOverLay routeOverlay = routeOverlays.valueAt(i);
            routeOverlay.removeFromMap();
        }
        routeOverlays.clear();
        routeList.clear();
        mAmap.clear();
    }

    /**
     * 移动镜头到当前的视角。
     *
     * @since V2.1.0
     */
    public void zoomToSpan() {
        if (startLatlng != null) {
            if (mAmap == null)
                return;
            try {
                int zoom = 300;
                if (mWayPointList.size() != 0) {
                    zoom = 200;
                }
                LatLngBounds bounds = getLatLngBounds();
                mAmap.animateCamera(CameraUpdateFactory
                        .newLatLngBounds(bounds, zoom));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    protected LatLngBounds getLatLngBounds() {
        LatLngBounds.Builder b = LatLngBounds.builder();
        b.include(new LatLng(startLatlng.getLatitude(), startLatlng.getLongitude()));
        b.include(new LatLng(endLatlng.getLatitude(), endLatlng.getLongitude()));
        return b.build();
    }

    @Override
    public void onCalculateRouteFailure(int arg0) {
        dismissDelog();
        GHLog.i("error", "errorCode=" + arg0);
        displayToast(getString(R.string.route_failed));
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        getImageManager().clearImage();
        mRouteMapView.onResume();
    }



    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        try {
            mRouteMapView.onPause();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mRouteMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mRouteMapView.onDestroy();
            /**
             * 当前页面只是展示地图，activity销毁后不需要再回调导航的状态
             */
            mAMapNavi.removeAMapNaviListener(this);
            //注意：不要调用这个destory方法，因为在当前页面进行算路，算路成功的数据全部存在此对象中。到另外一个activity中只需要开始导航即可。
//            //如果用户是回退退出当前activity，可以调用下面的destory方法。
            if (flage == false) {
                mAMapNavi.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * ************************************************** 在算路页面，以下接口全不需要处理，在以后的版本中我们会进行优化***********************************************************************************************
     **/

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo arg0) {
    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo arg0) {
    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] arg0) {
    }

    @Override
    public void hideCross() {
    }

    @Override
    public void showModeCross(AMapModelCross aMapModelCross) {

    }

    @Override
    public void hideModeCross() {

    }

    @Override
    public void hideLaneInfo() {
    }

    @Override
    public void notifyParallelRoad(int arg0) {


    }

    @Override
    public void onArriveDestination() {


    }


    @Override
    public void onArrivedWayPoint(int arg0) {


    }

    @Override
    public void onEndEmulatorNavi() {


    }

    @Override
    public void onGetNavigationText(int arg0, String arg1) {


    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onGpsOpenStatus(boolean arg0) {


    }

    @Override
    public void onInitNaviFailure() {
        dismissDelog();
        flage = false;
        finish();
    }

    @Override
    public void onLocationChange(AMapNaviLocation arg0) {


    }

    @Override
    public void onNaviInfoUpdate(NaviInfo arg0) {


    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo arg0) {


    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

    }

    @Override
    public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {


    }

    @Override
    public void onReCalculateRouteForYaw() {


    }

    @Override
    public void onStartNavi(int arg0) {


    }

    @Override
    public void onTrafficStatusUpdate() {


    }

    @Override
    public void showCross(AMapNaviCross arg0) {


    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] arg0, byte[] arg1, byte[] arg2) {


    }

    @Override
    public void showLaneInfo(AMapLaneInfo aMapLaneInfo) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo arg0) {


    }

    @Override
    public void onPlayRing(int i) {

    }

    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {

    }

    @Override
    public void onCalculateRouteFailure(AMapCalcRouteResult aMapCalcRouteResult) {

    }

    @Override
    public void onNaviRouteNotify(AMapNaviRouteNotifyData aMapNaviRouteNotifyData) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat arg0) {


    }

}
