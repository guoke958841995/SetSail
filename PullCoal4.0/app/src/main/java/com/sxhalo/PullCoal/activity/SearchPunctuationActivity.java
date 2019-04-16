package com.sxhalo.PullCoal.activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMap.OnMapTouchListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.model.Discover;
import com.sxhalo.PullCoal.model.APPDataList;
import com.sxhalo.PullCoal.retrofithttp.api.DataUtils;
import com.sxhalo.PullCoal.ui.SearchView;
import com.sxhalo.PullCoal.ui.UIHelper;
import com.sxhalo.PullCoal.ui.quickadapter.BaseAdapterHelper;
import com.sxhalo.PullCoal.ui.quickadapter.QuickAdapter;
import com.sxhalo.PullCoal.ui.scrolllayout.ScrollLayout;
import com.sxhalo.PullCoal.ui.scrolllayout.content.ContentListView;
import com.sxhalo.PullCoal.ui.spinerpop.PopWindowUtils;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 地图搜索
 * Created by amoldZhang on 2017/4/13.
 */
public class SearchPunctuationActivity extends BaseActivity implements AMapLocationListener,
        SearchView.SearchViewListener, OnMarkerClickListener, OnMapLoadedListener, OnInfoWindowClickListener, InfoWindowAdapter, OnMapTouchListener {

    @Bind(R.id.map)
    MapView mapView;
    @Bind(R.id.list_view)
    ContentListView listView;
    @Bind(R.id.text_foot)
    TextView text_foot;
    @Bind(R.id.scroll_down_layout)
    ScrollLayout mScrollLayout;
    @Bind(R.id.search_layout)
    SearchView mSearchView;
    @Bind(R.id.item_select)
    LinearLayout itemSelect;

    @Bind(R.id.search_name)
    TextView searchName;
    @Bind(R.id.search_dis)
    TextView searchDis;
    @Bind(R.id.search_address)
    TextView searchAddress;
    @Bind(R.id.spinner_view)
    LinearLayout spinnerView;

    @Bind(R.id.spinner_distance_ll)
    LinearLayout spinnerDistanceLL;
    @Bind(R.id.spinner_distance_text)
    TextView spinnerDistanceText;
    @Bind(R.id.spinner_condition_ll)
    LinearLayout spinnerConditionll;
    @Bind(R.id.spinner_condition_text)
    TextView spinnerConditionText;

    /**
     * 地图选项
     */
    private AMap aMap;
    private Bundle savedInstanceState;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private Marker locationMarker; //当前位置的点
    private Marker myLocationMarker; //重新定位当前位置
    private Marker compassMarker;  //指南针
    private UiSettings mUiSettings;
    private LatLng latLng;

    private List<Discover> mineSynchronous = new ArrayList<Discover>();
    private Discover discoverSelect;
    private ArrayList<LatLng> latLngPoint = new ArrayList<LatLng>();
    private QuickAdapter<Discover> mAdapter;
    LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
    private int TYPE = -1;
    //判断是否显示选中数据
    private boolean ifShow;
    //搜索内容
    private String searchText = "";
    private String distanceNum = "800000";
    private Drawable drawable1, drawable2;
    private boolean flageView = false;
    private int pos;

    private ScrollLayout.OnScrollChangedListener mOnScrollChangedListener = new ScrollLayout.OnScrollChangedListener() {
        @Override
        public void onScrollProgressChanged(float currentProgress) {
            if (currentProgress >= 0) {
                float precent = 255 * currentProgress;
                if (precent > 255) {
                    precent = 255;
                } else if (precent < 0) {
                    precent = 0;
                }
                mScrollLayout.getBackground().setAlpha(255 - (int) precent);
            }
            if (text_foot.getVisibility() == View.VISIBLE) {
                text_foot.setVisibility(View.GONE);
            }
        }

        @Override
        public void onScrollFinished(ScrollLayout.Status currentStatus) {
            if (currentStatus.equals(ScrollLayout.Status.EXIT)) {
                spinnerView.setVisibility(View.GONE);
                text_foot.setVisibility(View.VISIBLE);
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mapView.getLayoutParams();
                layoutParams.bottomMargin = BaseUtils.dip2px(SearchPunctuationActivity.this, 50);
                mapView.setLayoutParams(layoutParams);
            } else if (currentStatus.equals(ScrollLayout.Status.CLOSED)) {
                spinnerView.setVisibility(View.VISIBLE);
            } else if (currentStatus.equals(ScrollLayout.Status.OPENED)) {
                spinnerView.setVisibility(View.GONE);
            }

        }

        @Override
        public void onChildScroll(int top) {
            GHLog.i("onChildScroll", top + "");
        }
    };
    private int i;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search_punctuation);
        this.savedInstanceState = savedInstanceState;
    }

    @Override
    protected void initTitle() {
        try {
            searchText = getIntent().getStringExtra("search") == null ? "" : getIntent().getStringExtra("search");
            if (!searchText.equals("")) {
                //TODO 打开数据列表
                mSearchView.setText(searchText);
                mSearchView.setPositionEnd(searchText.length());
//                mScrollLayout.setToOpen();
            }
            //距离数据
            final ArrayList<String> distanceList = new ArrayList<String>();
            distanceList.add("100");
            distanceList.add("200");
            distanceList.add("500");
            distanceList.add("800");
            distanceList.add("不限");

            //筛选数据
            final ArrayList<String> filterList = new ArrayList<String>();
            filterList.add("全部");
            filterList.add("矿口");
            filterList.add("信息部");

            drawable1 = DrawableCompat.wrap(ContextCompat.getDrawable(mContext, R.drawable.nice_spiner_arrow));
            drawable2 = DrawableCompat.wrap(ContextCompat.getDrawable(mContext, R.drawable.nice_spiner_arrow));
            spinnerDistanceText.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable1, null);
            spinnerConditionText.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable2, null);


            final PopWindowUtils popWindowUtils = new PopWindowUtils(SearchPunctuationActivity.this);
            popWindowUtils.setMyDismissListener(new PopWindowUtils.MyDismissListener() {
                @Override
                public void onDismiss() {
                    switch (pos) {
                        case 0:
                            animateArrow(false, drawable2);
                            break;
                        case 3:
                            animateArrow(false, drawable1);
                            break;
                    }
                }
            });
            popWindowUtils.getData(new PopWindowUtils.DataBack<View>() {
                @Override
                public void setdata(String text, View view) {
                    flageView = true;
                    if (view == spinnerDistanceLL) {
                        if (text.equals("不限")) {
                            spinnerDistanceText.setText(text);
                            distanceNum = text;
                        } else {
                            spinnerDistanceText.setText(text + "km");
                            distanceNum = text + "000";
                        }
                        animateArrow(false, drawable1);
                        onSearch(searchText);
                    } else {
                        spinnerConditionText.setText(text);
                        if (text.equals("全部")) {
                            TYPE = 0;
                        } else if (text.equals("信息部")) {
                            TYPE = 2;
                        } else if (text.equals("矿口")) {
                            TYPE = 1;
                        }
                        animateArrow(false, drawable2);
                        onSearch(searchText);
                    }
                }
            });
            spinnerDistanceLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    animateArrow(true, drawable1);
                    popWindowUtils.showSpinWindow(distanceList, 3, spinnerDistanceLL);
                    pos = 3;
                }
            });

            spinnerConditionll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    animateArrow(true, drawable2);
                    popWindowUtils.showSpinWindow(filterList, 0, spinnerConditionll);
                    pos = 0;
                }
            });


            String type = getIntent().getStringExtra("TYPE");  //类型0全部1矿口2信息部
            if (type.equals("地图导航")) {
                TYPE = 0;
                spinnerConditionText.setText("全部");
            } else if (type.equals("查信息部")) {
                TYPE = 2;
                spinnerConditionText.setText("信息部");
            } else if (type.equals("查找矿口")) {
                TYPE = 1;
                spinnerConditionText.setText("矿口");
            }
            spinnerDistanceText.setText(800 + "km");

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);// 设置输入软键盘隐藏
            //设置监听
            mSearchView.setSearchViewListener(this);
            mAdapter = new QuickAdapter<Discover>(this, R.layout.find_search_list_item, mineSynchronous) {
                @Override
                protected void convert(BaseAdapterHelper helper, final Discover t, final int pos) {
                    try {
                        helper.setText(R.id.search_name, StringUtils.setHighLightColor(mContext, t.getItemName(), searchText));
                        int distance = Integer.valueOf(t.getDistance()) / 1000;//转换成Km
                        helper.setText(R.id.search_dis, distance == 0 ? t.getDistance() + "m" : distance + "Km");
                        helper.setText(R.id.search_address, StringUtils.setHighLightColor(mContext, t.getAddress(), searchText));
                        helper.getView().findViewById(R.id.find_search_goto).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                UIHelper.showRoutNavi(SearchPunctuationActivity.this, t.getLatitude(), t.getLongitude(), t.getAddress());
                            }
                        });
                    } catch (Exception e) {
                        GHLog.e("赋值", e.toString());
                    }
                }
            };
            listView.setAdapter(mAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                    try {
                        Intent intent = new Intent();
                        String TYPE = mineSynchronous.get(pos).getItemType();
                        // 2信息部  1 矿口
                        if (TYPE.equals("2")) {
                            intent.setClass(SearchPunctuationActivity.this, DetaileInformationDepartmentActivity.class);
                        } else {
                            intent.setClass(SearchPunctuationActivity.this, DetaileMineActivity.class);
                        }
                        intent.putExtra("InfoDepartId", mineSynchronous.get(pos).getItemId() + "");
                        startActivity(intent);
                    } catch (Exception e) {
                        GHLog.e("Item点击", e.toString());
                    }
                }
            });
            /**设置 setting*/
            mScrollLayout.setMinOffset(0);
            mScrollLayout.setMaxOffset((int) (BaseUtils.getScreenHeight(this) * 0.5));
            mScrollLayout.setExitOffset(BaseUtils.dip2px(this, 100));
            mScrollLayout.setIsSupportExit(true);
            mScrollLayout.setAllowHorizontalScroll(true);
            mScrollLayout.setOnScrollChangedListener(mOnScrollChangedListener);
            mScrollLayout.setToExit();
            mScrollLayout.getBackground().setAlpha(0);
        } catch (Exception e) {
            GHLog.e("界面初始化", e.toString());
        }
    }

    private static final int MAX_LEVEL = 10000;

    private void animateArrow(boolean shouldRotateUp, Drawable myDrawable) {
        int start = shouldRotateUp ? 0 : MAX_LEVEL;
        int end = shouldRotateUp ? MAX_LEVEL : 0;
        ObjectAnimator animator = ObjectAnimator.ofInt(myDrawable, "level", start, end);
        animator.setInterpolator(new LinearOutSlowInInterpolator());
        animator.start();
    }

    @Override
    protected void getData() {
        try {
        /*
         * 设置离线地图存储目录，在下载离线地图或初始化地图设置; 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
		 * 则需要在离线地图下载和使用地图页面都进行路径设置
		 */
            // Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
            // MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
            mapView.onCreate(savedInstanceState);// 此方法必须重写
            if (aMap == null) {
                aMap = mapView.getMap();
                aMap.setMinZoomLevel(Float.valueOf(3));
                aMap.setMaxZoomLevel(Float.valueOf(19));
                setUiMap();
            }
            initLocation();
            if (mLocationClient != null) {
                mLocationClient.startLocation();
            }
        } catch (Exception e) {
            Log.i("地图初始化", e.toString());
        }
    }

    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap() {
        compassMarker = aMap.addMarker(new MarkerOptions()
                .anchor(0.5f, 0.5f)
                .draggable(false)
                .setFlat(true)
                .title("指南针")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.compass)));
        compassMarker.setPositionByPixels(70, 200);

        myLocationMarker = aMap.addMarker(new MarkerOptions()
                .anchor(0.5f, 0.5f)
                .draggable(false)
                .setFlat(false)
                .title("我的位置")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.regression_pressed)));
        int y = BaseUtils.getScreenHeight(this) - 250;
        myLocationMarker.setPositionByPixels(70, y);
    }

    private void setUiMap() {
        mUiSettings = aMap.getUiSettings();
        // //是否显示比例尺
        mUiSettings.setScaleControlsEnabled(false);
        // 设置指南针是否显示
        mUiSettings.setCompassEnabled(false);

        MyLocationStyle myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);//定位一次，且将视角移动到地图中心点。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.anchor(0.5f, 0.5f);
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        mUiSettings.setMyLocationButtonEnabled(false); //设置默认定位按钮是否显示，非必需设置
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // //启用缩放手势
        mUiSettings.setZoomGesturesEnabled(true);
        // 设置地图默认的缩放按钮是否显示
        mUiSettings.setZoomControlsEnabled(true);

        aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
        aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
        aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
        aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
        aMap.setOnMapTouchListener(this);// 设置地图点击监听
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
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(1000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
    }

    /**
     * 当搜索框 文本改变时 触发的回调 ,更新自动补全数据
     *
     * @param text 传入补全后的文本
     */
    @Override
    public void onRefreshAutoComplete(String text) {
        //当用户输入时
    }

    /**
     * 点击搜索键时edit text触发的回调
     *
     * @param text 传入输入框的文本
     */
    @Override
    public void onSearch(String text) {
        if (ifShow) {
            itemSelect.setVisibility(View.GONE);
            mScrollLayout.setVisibility(View.VISIBLE);
            mScrollLayout.setToOpen();
            ifShow = false;
        }
        //开始搜索时
        mineSynchronous.clear();
        locationMarker = null;
        //清空地图上的所有标注
        aMap.clear();
        this.searchText = text;
//        setData();
    }

    @Override
    public void clearContent() {
        this.searchText = "";
    }

    @Override
    public void onTipsItemClick(String text) {

    }

    @Override
    public void onSearchHasFocus(boolean hasFocus) {

    }


    public void setData() {
        try {
            i++;
            GHLog.i("setData()执行次数", "弟" + i + "次");

            params.clear();
            if (!StringUtils.isEmpty(searchText)) {
                params.put("searchValue", searchText);
            }
            params.put("searchType", TYPE + "");

            params.put("longitude", latLng.longitude + "");
            params.put("latitude", latLng.latitude + "");
            if (!distanceNum.equals("不限")) {
                params.put("distance", distanceNum);
            }
            params.put("currentPage", 1 + "");
            params.put("pageSize", "-1");
            new DataUtils(this, params).getDiscoverList(new DataUtils.DataBack<APPDataList<Discover>>() {
                @Override
                public void getData(APPDataList<Discover> data) {
                    if (data == null) {
                        GHLog.e("服务器出错", "Success = 1");
                        return;
                    }
                    mineSynchronous = data.getList();
                    addMarkersToMap();
                    setMapAddMarkers();
                    mAdapter.replaceAll(mineSynchronous);
                    if (mineSynchronous.size() == 0) {
                        displayToast(getString(R.string.no_result_in_map));
                        if (!flageView) {
                            mScrollLayout.setVisibility(View.GONE);
                            flageView = false;
                        }

                        MapView.LayoutParams layoutParams = (MapView.LayoutParams) mapView.getLayoutParams();
                        layoutParams.bottomMargin = 0;//将默认的距离底部20dp，改为0，这样底部区域全被listview填满。
                        mapView.setLayoutParams(layoutParams);
                    } else {
                        mScrollLayout.setVisibility(View.VISIBLE);
                        MapView.LayoutParams layoutParams = (MapView.LayoutParams) mapView.getLayoutParams();
                        layoutParams.bottomMargin = 50;//将默认的距离底部20dp，改为0，这样底部区域全被listview填满。
                        mapView.setLayoutParams(layoutParams);
                        if (!flageView) {
                            mScrollLayout.setToOpen();
                            flageView = false;
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

//        dataUtils.getDiscover(AppModel.class, params, TYPE);
//        dataUtils.setDataBack(new DataUtils.DataBack<AppModel>() {
//            @Override
//            public void getData(AppModel data) {
//
//            }
//        });
    }

    private void setMapAddMarkers() {
        if (mineSynchronous.size() != 0) {
            latLngPoint = new ArrayList<LatLng>();
            for (int i = 0; i < mineSynchronous.size(); i++) {
                Discover mineProduct = mineSynchronous.get(i);
//                Log.d("搜索出的矿点","CompanyName: "+mineProduct.getName());
                latLngPoint.add(i, new LatLng(Double.valueOf(mineProduct.getLatitude()), Double.valueOf(mineProduct.getLongitude())));
            }
            //					Log.i("查找的矿点",latLngPoint.size()+"");
            addMarkersToMap(mineSynchronous);// 往地图上添加marker
            onMapLoaded();
        } else {
            //然后可以移动到定位点,使用animateCamera就有动画效果
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        }
    }


    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap(List<Discover> mineSynchronous) {
        try {
            if (latLngPoint.size() != 0) {
                ArrayList<MarkerOptions> markerOptionlst = new ArrayList<MarkerOptions>();
                for (int i = 0; i < latLngPoint.size(); i++) {
                    int imageId;
                    // 1矿口2信息部
                    if (mineSynchronous.get(i).getItemType().equals("1")) {
                        imageId = R.mipmap.amap_mark_point_mine;
                    } else {
                        imageId = R.mipmap.amap_mark_point_infor;
                    }
                    MarkerOptions markerOption = new MarkerOptions()
                            .position(latLngPoint.get(i))
                            .draggable(true)
                            .title(mineSynchronous.get(i).getItemName())
                            .icon(BitmapDescriptorFactory.fromResource(imageId));
                    markerOptionlst.add(markerOption);
                }
                aMap.addMarkers(markerOptionlst, true);
            } else {
                //然后可以移动到定位点,使用animateCamera就有动画效果
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            }
        } catch (Exception e) {
            GHLog.i("addMarkersToMap()", e.toString());
        }
    }

    /**
     * 对marker标注点点击响应事件
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (!marker.getId().equals(compassMarker.getId()) || !marker.getId().equals(locationMarker.getId())) {
            GHLog.i("onMarkerClick()", "Marker点击" + "  ID= " + marker.getId());
            if (marker.getId().equals(myLocationMarker.getId())) {
                if (latLng != null) {
                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                }
            } else if (marker.getId().equals(compassMarker.getId())){
                GHLog.i("onMarkerClick()", "指南针Marker点击" + "  ID= " + marker.getId());
                aMap.moveCamera(CameraUpdateFactory.changeBearing(0f));
            }else {
                ifShow = true;
                onRefreshListView(marker.getTitle());

                if (discoverSelect != null ){
                    itemSelect.setVisibility(View.VISIBLE);
                    mScrollLayout.setVisibility(View.GONE);

                    searchName.setText(StringUtils.setHighLightColor(mContext, discoverSelect.getItemName(), searchText));
                    searchDis.setText(StringUtils.setHighLightColor(mContext, Integer.valueOf(discoverSelect.getDistance()) / 1000 + "Km", searchText));
                    searchAddress.setText(StringUtils.setHighLightColor(mContext, discoverSelect.getAddress(), searchText));
                    itemSelect.setBackgroundColor(getResources().getColor(R.color.white));
                    itemSelect.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent();
                            String TYPE = discoverSelect.getItemType();
                            // 2信息部   1 矿口
                            if (TYPE.equals("2")) {
                                intent.setClass(SearchPunctuationActivity.this, DetaileInformationDepartmentActivity.class);
                            } else {
                                intent.setClass(SearchPunctuationActivity.this, DetaileMineActivity.class);
                            }
                            intent.putExtra("InfoDepartId", discoverSelect.getItemId() + "");
                            startActivity(intent);
                        }
                    });

                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mapView.getLayoutParams();
                    layoutParams.bottomMargin = BaseUtils.dip2px(SearchPunctuationActivity.this, 0);
                    mapView.setLayoutParams(layoutParams);
                    mScrollLayout.scrollToExit();
                }
            }
        }
        return false;
    }

    private void onRefreshListView(String mineTitle) {
        if (mineTitle != null){
            for (int i = 0; i < mineSynchronous.size(); i++) {
                Discover discover = mineSynchronous.get(i);
                if (mineTitle.equals(discover.getItemName())) {
                    this.discoverSelect = discover;
                }
            }
        }
    }

    /**
     * 监听点击infowindow窗口事件回调
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        GHLog.i("onInfoWindowClick()", "Marker点击");
        //Log.i("marker.getId()", marker.getId());//Marker9
        //		showText(0, "你点击了infoWindow窗口" + marker.getId());
        //		showText(0, "你点击了infoWindow窗口" + marker.getTitle());
        //		showText(0, "当前地图可视区域内Marker数量:" + aMap.getMapScreenMarkers().size());
    }

    /**
     * 监听amap地图加载成功事件回调
     */
    @Override
    public void onMapLoaded() {
        // 设置所有maker显示在当前可视区域地图中
        try {
            LatLngBounds.Builder bounds = new LatLngBounds.Builder();
            if (latLngPoint.size() != 0) {
                int count = 0;
                if (latLngPoint.size() < 10) {
                    count = latLngPoint.size();
                } else {
                    count = 10;
                }
                for (int i = 0; i < count; i++) {
                    bounds.include(latLngPoint.get(i));
                }
                bounds.include(locationMarker.getPosition());
                aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 300));
            }
        } catch (Exception e) {
            Log.i("onMapLoaded()", e.toString());
        }
    }

    /**
     * 监听自定义infoContent窗口的infocontents事件回调
     */
    @Override
    public View getInfoContents(Marker marker) {
        if (StringUtils.isEmpty(marker.getTitle()) || marker.getTitle().equals("我的位置") || marker.getTitle().equals("指南针"))
            return null;
        View infoContent = getLayoutInflater().inflate(
                R.layout.custom_info_contents, null);
        render(marker, infoContent);
        return infoContent;
    }

    /**
     * 监听自定义infowindow窗口的infowindow事件回调
     */
    @Override
    public View getInfoWindow(Marker marker) {
        if (StringUtils.isEmpty(marker.getTitle()) || marker.getTitle().equals("我的位置") || marker.getTitle().equals("指南针"))
            return null;
        View infoWindow = getLayoutInflater().inflate(
                R.layout.custom_info_window, null);
        render(marker, infoWindow);
        return infoWindow;
    }

    /**
     * 自定义infowinfow窗口
     */
    public void render(final Marker marker, View view) {
        String title = marker.getTitle();
        TextView titleUi = ((TextView) view.findViewById(R.id.title));
        if (title != null) {
            SpannableString titleText = new SpannableString(title);
            titleText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, titleText.length(), 0);
            titleUi.setTextSize(15);
            titleUi.setText(titleText);
        } else {
            titleUi.setText("");
        }
        String snippet = marker.getSnippet();
        TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
        if (snippet != null) {
            SpannableString snippetText = new SpannableString(snippet);
            snippetText.setSpan(new ForegroundColorSpan(Color.GREEN), 0, snippetText.length(), 0);
            snippetUi.setTextSize(15);
            snippetUi.setText(snippetText);
        } else {
            snippetUi.setText("");
        }
        view.findViewById(R.id.badge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mineSynchronous.size(); i++) {
                    if (mineSynchronous.get(i).getItemName().equals(marker.getTitle())) {
                        Intent intent = new Intent();
                        String TYPE = mineSynchronous.get(i).getItemType();
                        // 1信息部   2 矿口
                        if (TYPE.equals("1")) {
                            intent.setClass(SearchPunctuationActivity.this, DetaileInformationDepartmentActivity.class);
                        } else {
                            intent.setClass(SearchPunctuationActivity.this, DetaileMineActivity.class);
                        }
                        intent.putExtra("InfoDepartId", mineSynchronous.get(i).getItemId() + "");
                        startActivity(intent);
                        break;
                    }
                }
            }
        });
    }

    @OnClick({R.id.back, R.id.text_foot, R.id.find_search_goto})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                if (ifShow) {
                    itemSelect.setVisibility(View.GONE);
                    mScrollLayout.setVisibility(View.VISIBLE);
                    onSearch(searchText);
                    mScrollLayout.setToOpen();
                    ifShow = false;
                } else {
                    finish();
                }
                break;
            case R.id.text_foot:
                mScrollLayout.setToOpen();
                break;
            case R.id.find_search_goto:
                if (discoverSelect != null) {
                    String endAddress = discoverSelect.getAddress();
                    UIHelper.showRoutNavi(SearchPunctuationActivity.this, discoverSelect.getLatitude(), discoverSelect.getLongitude(), endAddress);
                }
                break;
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

//        RefWatcher refWatcher = MyAppLication.getRefWatcher(this);
//        refWatcher.watch(this);

        if (mapView != null){
            mapView.onDestroy();
        }

        //退出界面的时候停止定位
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
        }
    }


    /**
     * 定位成功后会调用这个方法
     *
     * @param amapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息

                //取出经纬度
                latLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                //添加Marker显示定位位置
                if (locationMarker == null) {
                    //如果是空的添加一个新的,icon方法就是设置定位图标，可以自定义
                    locationMarker = aMap.addMarker(new MarkerOptions()
                            .anchor(0.5f, 0.5f)
                            .position(latLng)
                            .setFlat(false)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.location_marker)));
                    setData();
                } else {
                    //已经添加过了，修改位置即可
                    locationMarker.setPosition(latLng);
                }
                if (aMap.getCameraPosition().bearing == 0) {
                    compassMarker.setVisible(false);
                } else {
                    compassMarker.setVisible(true);
                }
                //  target  目标位置的屏幕中心点经纬度坐标。
                if (latLng == aMap.getCameraPosition().target) {
                    GHLog.i("位移后的 latitude ", (aMap.getCameraPosition().target).latitude + "");
                    GHLog.i("位移后的 longitude ", (aMap.getCameraPosition().target).longitude + "");
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        mScrollLayout.scrollToExit();
    }
}
