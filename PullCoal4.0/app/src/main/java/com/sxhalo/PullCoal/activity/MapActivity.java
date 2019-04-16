package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
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
import com.sxhalo.PullCoal.model.InformationDepartment;
import com.sxhalo.PullCoal.model.MineMouth;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.ui.SearchView;
import com.sxhalo.PullCoal.ui.slidinguppanel.SlidingUpPanelLayout;
import com.sxhalo.PullCoal.ui.slidinguppanel.SlidingUpPanelLayout.PanelState;
import com.sxhalo.PullCoal.ui.titlebar.FragmentTitleBar;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by amoldZhang on 2017/10/13.
 */
public class MapActivity extends BaseActivity implements SearchView.SearchViewListener, AMapLocationListener, AMap.OnMapLoadedListener,
        AMap.OnMarkerClickListener{

    @Bind(R.id.map)
    MapView mapView;
    @Bind(R.id.search_layout)
    SearchView mSearchView;

    @Bind(R.id.sliding_layout)
    SlidingUpPanelLayout slidingLayout;
    @Bind(R.id.fragment_title_bar)
    FragmentTitleBar fragmentTitleBar;

    @Bind(R.id.image_view)
    ImageView imageView;
    @Bind(R.id.back_image)
    ImageView backImage;


    /**
     * 地图选项
     */
    private AMap aMap;
    private Bundle savedInstanceState;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private UiSettings mUiSettings;
    private LatLng latLng;  //定位出的当前位置经纬度
    private LatLng endLatLng;  //

    private Marker myLocationMarker; //重新定位当前位置
    private Marker compassMarker;  //指南针
    private Marker endMarker;  //搜索点
    //搜索内容
    private String searchText = "";
    private String type = "";
    //信息部详情实体
    private InformationDepartment informationDepartment;
    //矿口详情实体
    private MineMouth minMouth;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        try {
            setContentView(R.layout.activity_map);
            this.savedInstanceState = savedInstanceState;
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            e.printStackTrace();
        }
    }

    @Override
    protected void initTitle() {
        getInitData();
        initMap();
    }

    @Override
    protected void getData() {
        addMarkersToMap();
        setListView();
    }

    /**
     * 界面初始化
     */
    private void setListView() {
//        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mapView.getLayoutParams();
//        layoutParams.topMargin = BaseUtils.dip2px(getApplicationContext(), 60);
//        mapView.setLayoutParams(layoutParams);
        String imageURL = "";
        if (type.equals("MineMouth")){
            imageURL = minMouth.getMineMouthPic();
        }else if (type.equals("InformationDepartment")){
            imageURL = informationDepartment.getCoalSalePic();
        }
        if (StringUtils.isEmpty(imageURL)) {
            Bitmap bmp= BitmapFactory.decodeResource(getResources(), android.R.color.transparent);
            imageView.setImageBitmap(bmp);
            imageView.setVisibility(View.GONE);
            imageView.setAlpha(0f);
        } else {
            imageView.setVisibility(View.VISIBLE);
            imageView.setAlpha(0f);
            getImageManager().loadMinebgUrlImage(imageURL, imageView);
        }
        backImage.setVisibility(View.GONE);
        backImage.setAlpha(0f);
        slidingLayout.setPanelState(PanelState.COLLAPSED);

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
                imageView.setAlpha(slideOffset);
                backImage.setAlpha(slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
                GHLog.i("PanelSlideListener", "onPanelStateChanged = " + newState);
                if (slidingLayout != null){
                    if (slidingLayout.getPanelState() == PanelState.DRAGGING){
                        fragmentTitleBar.setSuperView(0);
                    }
                    if (slidingLayout.getPanelState() == PanelState.COLLAPSED){
                        imageView.setAlpha(0f);
                        backImage.setVisibility(View.GONE);
                        backImage.setAlpha(0f);
                        fragmentTitleBar.setSuperView(1);
                    }
                    if (slidingLayout.getPanelState() == PanelState.ANCHORED){
                        imageView.setAlpha(1f);
                        backImage.setVisibility(View.VISIBLE);
                        backImage.setAlpha(1f);
                        fragmentTitleBar.setSuperView(2);
                    }
                    if (slidingLayout.getPanelState() == PanelState.EXPANDED){
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
                slidingLayout.setPanelState(PanelState.COLLAPSED);
            }
        });

        if (type.equals("MineMouth")){
            fragmentTitleBar.setInItViewMinMouth(this,minMouth,endLatLng);
        }else if (type.equals("InformationDepartment")){
            fragmentTitleBar.setInItView(this,informationDepartment,endLatLng);
        }
        fragmentTitleBar.setLeftText("附近信息部");
        fragmentTitleBar.setRightText("附近的矿口");

    }

    /**
     * 获取初始数据来显示界面初始样式
     */
    private void getInitData() {
        type = getIntent().getStringExtra("Type");
        double latitude = 0.0;
        double longitude = 0.0;
        if (type.equals("MineMouth")){
            minMouth = (MineMouth)getIntent().getSerializableExtra("Entity");
            searchText = minMouth.getMineMouthName();

            latitude = Double.valueOf(minMouth.getLatitude());
            longitude = Double.valueOf(minMouth.getLongitude());

        }else if (type.equals("InformationDepartment")){
            informationDepartment = (InformationDepartment)getIntent().getSerializableExtra("Entity");
            searchText = informationDepartment.getCompanyName();
            latitude = Double.valueOf(informationDepartment.getLatitude());
            longitude = Double.valueOf(informationDepartment.getLongitude());
        }

        if (!searchText.equals("")) {
            mSearchView.setText(searchText);
            mSearchView.setPositionEnd(searchText.length());
        }
        endLatLng = new LatLng(latitude, longitude);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);// 设置输入软键盘隐藏
        //设置监听
        mSearchView.setSearchViewListener(this);
    }

    /**
     * 地图初始化
     */
    private void initMap() {
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
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            Log.i("地图初始化", e.toString());
        }
    }

    /**
     * 设置默认地图显示和基本设置
     */
    private void setUiMap() {
        mUiSettings = aMap.getUiSettings();
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
        aMap.setMyLocationStyle(myLocationStyle);// 将自定义的 myLocationStyle 对象添加到地图上
        mUiSettings.setMyLocationButtonEnabled(false); //设置默认定位按钮是否显示，非必需设置
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

        // //启用缩放手势
        mUiSettings.setZoomGesturesEnabled(true);
        // 设置地图默认的缩放按钮是否显示
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER); //设置默认缩放按钮居于右中

        aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
        aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
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
        this.searchText = text;
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

    /**
     * 定位成功监听回调
     * 1.当是点击回到我的位置时
     * 2.当初次进来时
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
                SharedTools.putStringValue(this,"latitude",latLng.latitude + "");
                SharedTools.putStringValue(this,"longitude",latLng.longitude + "");
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

    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap() {
        //指南针Marker
        compassMarker = aMap.addMarker(new MarkerOptions()
                .anchor(0.5f, 0.5f)
                .draggable(false)
                .setFlat(true)
                .title("指南针")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.compass)));
        compassMarker.setPositionByPixels(70, 260);

        //定位Marker
        myLocationMarker = aMap.addMarker(new MarkerOptions()
                .anchor(0.5f, 0.5f)
                .draggable(false)
                .setFlat(false)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.regression_pressed)));
        int y = BaseUtils.getScreenHeight(this) - 600;
        myLocationMarker.setPositionByPixels(70, y);

        endMarker = aMap.addMarker(new MarkerOptions()
                .draggable(false)
                .setFlat(false)
                .position(endLatLng)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.my_address)));
//        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(endLatLng, 20));
    }


    /**
     * 监听amap地图加载成功事件回调
     */
    @Override
    public void onMapLoaded() {
        // 设置所有maker显示在当前可视区域地图中
        try {
            LatLngBounds.Builder bounds = new LatLngBounds.Builder();
            bounds.include(endLatLng);
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(),1000));
            aMap.moveCamera(CameraUpdateFactory.zoomTo(Float.valueOf(10)));
//            CameraUpdateFactory.zoomTo(20);
        } catch (Exception e) {
            MyException.uploadExceptionToServer(mContext,e.fillInStackTrace());
            Log.i("onMapLoaded()", e.toString());
        }
    }

    /**
     * 对marker标注点点击响应事件
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (marker.getId() != null) {
                GHLog.i("onMarkerClick()", "Marker点击" + "  ID= " + marker.getId());
                if (marker.getId().equals(myLocationMarker.getId())) {
                    if (latLng != null) {
                        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 70));
                    }
                } else if (marker.getId().equals(compassMarker.getId())){
                    aMap.moveCamera(CameraUpdateFactory.changeBearing(0f));
                }
                return true;
        }
        if (marker.getPosition() == latLng) {
            return true;
        }
        return false;
    }

    @OnClick({R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
//            case R.id.back_image:
//                finish();
//                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (data != null){
                if (aMap != null){
                    aMap.clear();
                }
                setIntent(data);
                initTitle();
                getData();
            }
        }
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
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (aMap == null){

        }
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
        mapView.onDestroy();

        //退出界面的时候停止定位
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
        }
    }
}
