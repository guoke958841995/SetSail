package com.sxhalo.PullCoal.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.model.UserAddress;
import com.sxhalo.PullCoal.retrofithttp.api.MyException;
import com.sxhalo.PullCoal.tools.map.search.MapAddressTranslation;
import com.sxhalo.PullCoal.ui.SearchView;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.SoftHideKeyBoardUtil;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 带地图的地址选择
 * Created by amoldZhang on 2018/5/7.
 */
public class SelectAddressActivity extends BaseActivity implements AMapLocationListener, AMap.OnMapLoadedListener,
        AMap.OnMarkerClickListener{


    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.tv_select_address)
    TextView tvSelectAddress;
    @Bind(R.id.et_detailed_address)
    EditText etDetailedAddress;
    @Bind(R.id.rl)
    LinearLayout rl;

    @Bind(R.id.map_view)
    MapView mapView;

    private String selectCode = "";

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
    private LatLng selectLatLng;  //自定义出的当前位置经纬度
    private Marker myLocationMarker; //重新定位当前位置
    private Marker compassMarker;  //指南针
    private Marker myCountMarker; //屏幕中心点
    float getZoomB = 15f;  //初始缩放比例
    private UserAddress userAddress;
    private String cityName = "";

    private String cityNiveaddress = "";//当前的详细地址

    private LatLng moveEndLatlng = new LatLng(0.0,0.0);
    private Handler handler = new Handler();

    /**
     * 延迟线程，看是否还有下一个字符输入
     */
    private Runnable delayRun = new Runnable() {

        @Override
        public void run() {
            setPosition(moveEndLatlng,1);
        }
    };


    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_select_address);
        SoftHideKeyBoardUtil.assistActivity(this);
        this.savedInstanceState = savedInstanceState;
    }

    private float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    @Override
    protected void initTitle() {
        title.setText("地址选择");
        if (getIntent().getSerializableExtra("UserAddress") != null){
            userAddress = (UserAddress)getIntent().getSerializableExtra("UserAddress");
        }else{
            userAddress = new UserAddress();
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);// 设置输入软键盘隐藏
        initMap();

        if (!StringUtils.isEmpty(getIntent().getStringExtra("TTPE"))){
            if (mLocationClient != null) {
                mLocationClient.startLocation();
            }
        }
        etDetailedAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,KeyEvent event) {
                if ((actionId == 0 || actionId == 6) && event != null) {
                    if (!StringUtils.isEmpty(v.getText().toString().trim()) && !cityNiveaddress.equals(v.getText().toString().trim())){
                        setIvNavi(etDetailedAddress.getText().toString().trim(),cityName);
                    }
                    return true;
                }
                return false;
            }

        });

        //监听键盘的消失和显示
        rl.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = rl.getRootView().getHeight() - rl.getHeight();
                if (heightDiff > dpToPx(mContext, 200)) { // if more than 200 dp, it's probably a keyboard...
                    // ... do something here  //显示
                }else {  //消失
                    if (!StringUtils.isEmpty(etDetailedAddress.getText().toString()) && !cityNiveaddress.equals(etDetailedAddress.getText().toString().trim())){
                        setIvNavi(etDetailedAddress.getText().toString().trim(),cityName);
                    }
                }
            }
        });
    }

    @Override
    protected void getData() {
    }

    @OnClick({R.id.title_bar_left,R.id.iv_my_address, R.id.layout_address,R.id.tv_determine})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_bar_left:
                finish();
                break;
            case R.id.iv_my_address:
//                if (!StringUtils.isEmpty(tvSelectAddress.getText())){
//                    setIvNavi(tvSelectAddress.getText().toString().trim());
//                }
                break;
            case R.id.layout_address:
                startActivityForResult(new Intent(this,SelectAreaActivity.class), Constant.AREA_CODE);
                break;
            case R.id.tv_determine:
                setDetermine();
                break;
        }
    }

    /**
     * 提交用户当前输入内容
     */
    private void setDetermine() {
        if (StringUtils.isEmpty(selectCode)){
            displayToast("请选择地址");
            return;
        }
        if ("0".equals(selectCode)){
            displayToast("地址不能选全国");
            return;
        }
        if (StringUtils.isEmpty(etDetailedAddress.getText().toString().trim())){
            displayToast("请输入详细地址");
            return;
        }

        Intent intent = new Intent();
        userAddress.setRegionCode(selectCode);
        userAddress.setRegionName(tvSelectAddress.getText().toString().trim());
        userAddress.setAddress(etDetailedAddress.getText().toString().trim());
        userAddress.setLatitude(selectLatLng.latitude + "");
        userAddress.setLongitude(selectLatLng.longitude + "");
        userAddress.setCityName(cityName);
        intent.putExtra("UserAddress",userAddress);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Constant.AREA_CODE) {
            if (data != null){
                String lastSelectEndArea = data.getStringExtra("name");
                selectCode = data.getStringExtra("code");
                cityName = data.getStringExtra("strCity");
                if ("0".equals(selectCode)) {
                    displayToast("目的地不能选\"全国\",请重新选择");
                }else{
                    if (StringUtils.isEmpty(lastSelectEndArea)) {
                        tvSelectAddress.setText("请选择地区");
                    } else {
                        tvSelectAddress.setText(lastSelectEndArea);
                    }
                    setIvNavi(tvSelectAddress.getText().toString().trim(),cityName);
                    cityNiveaddress = "";
                    etDetailedAddress.setText("");
                }
            }
        }
    }


    /**
     * 通过经纬度逆地理编码
     */
    private void setPosition(final LatLng latLng,final int moveing){
        final MapAddressTranslation mapAddressTranslation = new MapAddressTranslation(this);
        mapAddressTranslation.searchListener(new MapAddressTranslation.AddressTranslation(){
            boolean flage;
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i){
                super.onRegeocodeSearched(regeocodeResult, i);
                if (i == 1000){
                    RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
                    String regionName = "";
                    if (regeocodeAddress.getProvince().equals(regeocodeAddress.getCity())){
                        regionName = regeocodeAddress.getCity() + regeocodeAddress.getDistrict();
                    }else{
                        regionName = regeocodeAddress.getProvince() + regeocodeAddress.getCity() + regeocodeAddress.getDistrict();
                    }

                    cityName = regeocodeAddress.getCity();
                    userAddress.setRegionName(regionName);
                    userAddress.setAddress(regeocodeAddress.getFormatAddress().replace(userAddress.getRegionName(),""));
                    userAddress.setRegionCode(regeocodeResult.getRegeocodeAddress().getAdCode()+ "000");

                    tvSelectAddress.setText(userAddress.getRegionName());
                    cityNiveaddress = userAddress.getAddress();
                    etDetailedAddress.setText("");
                    etDetailedAddress.setText(cityNiveaddress);
                    etDetailedAddress.setSelection(regeocodeAddress.getFormatAddress().replace(tvSelectAddress.getText().toString().trim(),"").length());//将光标移至文字末尾
                    selectCode = userAddress.getRegionCode();

                    if (myCountMarker != null){
                        myCountMarker.setPosition(latLng);
                    }else{
                        addMarkersToMap(latLng);
                    }
                    selectLatLng = latLng;
                    if (latLng != null && moveing == 0) {
                        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f));
                    }
                    flage = true;
                }else{
                    if (flage){
                        LatLonPoint latLonPoint = new LatLonPoint(latLng.longitude,latLng.latitude);
                        latLonPoint.setLatitude(latLng.latitude);
                        latLonPoint.setLongitude(latLng.longitude);
                        mapAddressTranslation.searchCoordinateToAddress(latLonPoint,200);
                        flage = false;
                    }
                }

            }
        });
        LatLonPoint latLonPoint = new LatLonPoint(latLng.longitude,latLng.latitude);
        latLonPoint.setLatitude(latLng.latitude);
        latLonPoint.setLongitude(latLng.longitude);
        mapAddressTranslation.searchCoordinateToAddress(latLonPoint,200);
    }

    /**
     * 创建工具类将地址转换为经纬度
     */
    boolean flage = true;
    private void setIvNavi(final String searchPlaceCode, final String cityName1){
        final MapAddressTranslation mapAddressTranslation = new MapAddressTranslation(this);
        mapAddressTranslation.searchListener(new MapAddressTranslation.AddressTranslation(){
            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
                super.onGeocodeSearched(geocodeResult, i);
                if (i == 1000){
                    if (geocodeResult.getGeocodeAddressList().size() != 0){
                        LatLonPoint latLonPoint = geocodeResult.getGeocodeAddressList().get(0).getLatLonPoint();
                        selectLatLng = new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
                        cityName = geocodeResult.getGeocodeAddressList().get(0).getCity();

                        userAddress.setLatitude(selectLatLng.latitude + "");
                        userAddress.setLatitude(selectLatLng.longitude + "");

                        if (myCountMarker != null){
                            myCountMarker.setPosition(selectLatLng);
                            if (selectLatLng != null) {
                                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectLatLng, getZoomB));
                            }
                        }else{
                            addMarkersToMap(selectLatLng);
                        }
                        flage = true;
                    }else{
                        displayToast("您当前输入内容未查找到对应的地址，请重新输入");
                        etDetailedAddress.setText("");
                    }
                }else{
                    if (flage){
                        //当地址搜索反回失败时在以行政区划搜索
                        mapAddressTranslation.searchAddressToCoordinate(searchPlaceCode,cityName1);
                        flage = false;
                    }
                }
            }
        });
        //默认先以地址搜索
        mapAddressTranslation.searchAddressToCoordinate(searchPlaceCode,cityName1);
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
            if (null == getIntent().getSerializableExtra("UserAddress")){
                if (mLocationClient != null) {
                    mLocationClient.startLocation();
                }
            }else{
                if (userAddress.getAddress()==null && !StringUtils.isEmpty(userAddress.getLatitude()) && !StringUtils.isEmpty(userAddress.getLongitude())){
                    moveEndLatlng.clone();
                    moveEndLatlng = new LatLng(Double.valueOf(userAddress.getLatitude()),Double.valueOf(userAddress.getLongitude()));
                    setPosition(moveEndLatlng,0);
                }else{
                    setIvNavi(userAddress.getFullRegionName() + userAddress.getAddress(),StringUtils.isEmpty(userAddress.getFullRegionName())?"":userAddress.getRegionName());
                    tvSelectAddress.setText(userAddress.getFullRegionName());
                    cityNiveaddress = userAddress.getAddress();
                    etDetailedAddress.setText(cityNiveaddress);
                    selectCode = userAddress.getRegionCode();
                }

                if (selectLatLng != null) {
                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectLatLng, getZoomB));
                }
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
//        MyLocationStyle myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
//        // 自定义定位蓝点图标
//        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.location_marker));
//        // 自定义精度范围的圆形边框颜色
//        myLocationStyle.strokeColor(Color.argb(180, 3, 145, 255));
//        //自定义精度范围的圆形边框宽度
//        myLocationStyle.strokeWidth(5);
//        // 设置圆形的填充颜色
//        myLocationStyle.radiusFillColor(Color.argb(10, 0, 0, 180));
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。
//        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
//        myLocationStyle.anchor(0.5f, 0.5f);
//        aMap.setMyLocationStyle(myLocationStyle);// 将自定义的 myLocationStyle 对象添加到地图上
//        mUiSettings.setMyLocationButtonEnabled(false); //设置默认定位按钮是否显示，非必需设置
        aMap.setMyLocationEnabled(false);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

        // //启用缩放手势
        mUiSettings.setZoomGesturesEnabled(true);
        // 设置地图默认的缩放按钮是否显示
        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER); //设置默认缩放按钮居于右中

        aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
        aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器

        //监听地图的移动和缩放监听
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (myCountMarker != null){
                    myCountMarker.setPosition(cameraPosition.target);
                }else {
                    addMarkersToMap(cameraPosition.target);
                }
                LatLng pt = myCountMarker.getPosition();
                GHLog.i("地图移动监听","当前经纬度坐标（latitude = " + pt.latitude +",longitude = "+ pt.longitude+")");
            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                GHLog.i("地图缩放级别",cameraPosition.zoom + "");
                if (getZoomB != cameraPosition.zoom){
                    getZoomB = cameraPosition.zoom;
//                    LatLng pt =  getMapCenterPoint(myCountMarker.getGeoPoint());
//                    GHLog.i("地图移动监听","当前经纬度坐标（latitude = " + pt.latitude +",longitude = "+ pt.longitude+")");
                }else{
                    GHLog.i("地图停止时","CameraPosition经纬度坐标（latitude = " + cameraPosition.target.latitude +",longitude = "+ cameraPosition.target.longitude+")");
                    if(delayRun!=null){
                        //每次editText有变化的时候，则移除上次发出的延迟线程
                        handler.removeCallbacks(delayRun);
                    }
                    moveEndLatlng.clone();
                    moveEndLatlng = cameraPosition.target;
                    //延迟800ms，如果不再输入字符，则执行该线程的run方法
                    handler.postDelayed(delayRun, 800);
                }
            }
        });
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
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
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
                selectLatLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                cityName = amapLocation.getCity();
                String regionName = amapLocation.getProvince() + amapLocation.getCity() + amapLocation.getDistrict();

                userAddress.setRegionName(regionName);
                String address = amapLocation.getAddress();
                userAddress.setAddress(address.replace(regionName,""));
                userAddress.setRegionCode(amapLocation.getAdCode() + "000");

                tvSelectAddress.setText(userAddress.getRegionName());
                cityNiveaddress = userAddress.getAddress();
                etDetailedAddress.setText("");
                etDetailedAddress.setText(cityNiveaddress);
                etDetailedAddress.setSelection(userAddress.getAddress().length());//将光标移至文字末尾
                selectCode = userAddress.getRegionCode();

//                if (aMap.getCameraPosition().bearing == 0) {
//                    compassMarker.setVisible(false);
//                } else {
//                    compassMarker.setVisible(true);
//                }

                if (myCountMarker != null){
                    myCountMarker.setPosition(selectLatLng);
                }else{
                    addMarkersToMap(selectLatLng);
                }
                if (selectLatLng != null) {
                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectLatLng, getZoomB));
                }
                if (mLocationClient != null) {
                    mLocationClient.stopLocation();
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
    private void addMarkersToMap(LatLng latLng) {
        //清空地图上的所有标注
        aMap.clear();

        //指南针Marker
        compassMarker = aMap.addMarker(new MarkerOptions()
                .anchor(0.5f, 0.5f)
                .draggable(false)
                .setFlat(true)
                .title("指南针")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.compass)));
        // 获得空件将在屏幕中的位置
        int x0 = BaseUtils.dip2px(this,20);
        int y0 = BaseUtils.dip2px(this,20);
        compassMarker.setPositionByPixels(x0, y0);

        //定位Marker
        myLocationMarker = aMap.addMarker(new MarkerOptions()
                .anchor(0.5f, 0.5f)
                .draggable(false)
                .setFlat(false)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.regression_pressed)));
        int y = BaseUtils.getScreenHeight(this) - BaseUtils.dip2px(this,230);
        int x = BaseUtils.getWindowsWidth(this) - BaseUtils.dip2px(this,20);
        myLocationMarker.setPositionByPixels(x, y);

        myCountMarker = aMap.addMarker(new MarkerOptions()
                .anchor(0.5f, 1f)
                .draggable(false)
                .setFlat(false)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.amap_mark_point)));

        int left = mapView.getLeft();
        int top = mapView.getTop();
        int right = mapView.getRight();
        int bottom = mapView.getBottom();
        // 获得屏幕中心的位置
        int x2 = (int) (mapView.getX() + (right - left) / 2);
        int y2 = (int) (mapView.getY() + (bottom - top) / 2) + BaseUtils.dip2px(this,30);
        myCountMarker.setPositionByPixels(x2, y2);

        myCountMarker.setPosition(latLng);

        if (latLng != null) {
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, getZoomB));
        }
    }


    /**
     * 监听amap地图加载成功事件回调
     */
    @Override
    public void onMapLoaded() {
        // 设置所有maker显示在当前可视区域地图中
        try {
            LatLngBounds.Builder bounds = new LatLngBounds.Builder();
            bounds.include(selectLatLng);
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(),1000));
            aMap.moveCamera(CameraUpdateFactory.zoomTo(Float.valueOf(15)));
//            CameraUpdateFactory.zoomTo(20);
        } catch (Exception e) {
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
                if (mLocationClient != null) {
                    mLocationClient.startLocation();
                }
            } else if (marker.getId().equals(compassMarker.getId())){
                aMap.moveCamera(CameraUpdateFactory.changeBearing(0f));
            }
            return true;
        }
        if (marker.getPosition() == selectLatLng) {
            return true;
        }
        return false;
    }
}
