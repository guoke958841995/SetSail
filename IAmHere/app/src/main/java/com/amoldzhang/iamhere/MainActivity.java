package com.amoldzhang.iamhere;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.amoldzhang.iamhere.ui.activity.BaseActivity;
import com.amoldzhang.iamhere.ui.activity.JSCoalAndroidActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements AMapLocationListener, AMap.OnMapLoadedListener {

    private  final int REQUESTCODE = 100;
    @Bind(R.id.my_mapview)
    MapView mapView;
    @Bind(R.id.back_bar)
    ImageView backBar;

    /**
     * 地图选项
     */
    private AMap aMap;
    private UiSettings mUiSettings;
    /**
     * 声明AMapLocationClient类对象
     */
    public AMapLocationClient mLocationClient = null;
    /**
     * 声明mLocationOption对象
     */
    public AMapLocationClientOption mLocationOption = null;
    private LatLng latLng = new LatLng(34.205521,108.99179);
    private Marker locationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setTitle();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUESTCODE);
            return;
        }else{
            initView(savedInstanceState);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUESTCODE){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //用户不同意，向用户展示该权限作用
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setMessage("该功能需要赋予定位的权限，不开启将无法正常工作！")
                            .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.parse("package:" + getPackageName())); // 根据包名打开对应的设置界面
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).create();
                    dialog.show();
                }
            }
        }
    }


    private void setTitle() {
        backBar.setImageDrawable(getResources().getDrawable(R.mipmap.icon_main));
    }

    private void initView(Bundle savedInstanceState) {
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
     * 设置地图的ui显示
     */
    private void setUiMap() {
        mUiSettings = aMap.getUiSettings();
        // //是否显示比例尺
        mUiSettings.setScaleControlsEnabled(true);
        // 设置指南针是否显示
        mUiSettings.setCompassEnabled(true);

        MyLocationStyle myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);//定位一次，且将视角移动到地图中心点。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.anchor(0.5f, 0.5f);
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        mUiSettings.setMyLocationButtonEnabled(true); //设置默认定位按钮是否显示，非必需设置
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // //启用缩放手势
        mUiSettings.setZoomGesturesEnabled(true);
        // 设置地图默认的缩放按钮是否显示
        mUiSettings.setZoomControlsEnabled(true);

        aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
//        aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
//        aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
//        aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
//        aMap.setOnMapTouchListener(this);// 设置地图点击监听
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
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
        mLocationOption.setWifiActiveScan(false);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
    }

    /**
     * 定位成功后会调用这个方法
     *
     * @param aMapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            //定位成功回调信息，设置相关消息
            if (aMapLocation.getErrorCode() == 0) {
                //取出经纬度
                latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                Log.i("定位后的 latitude ", latLng.latitude + "");
                Log.i("定位后的 longitude ", latLng.longitude + "");
                //添加Marker显示定位位置
//                if (locationMarker == null) {
//                    //如果是空的添加一个新的,icon方法就是设置定位图标，可以自定义
//                    locationMarker = aMap.addMarker(new MarkerOptions()
//                            .anchor(0.5f, 0.5f)
//                            .position(latLng)
//                            .setFlat(false)
//                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.location_marker)));
//                } else {
//                    //已经添加过了，修改位置即可
//                    locationMarker.setPosition(latLng);
//                }
                //  target  目标位置的屏幕中心点经纬度坐标。
                if (latLng == aMap.getCameraPosition().target) {
                    Log.i("位移后的 latitude ", (aMap.getCameraPosition().target).latitude + "");
                    Log.i("位移后的 longitude ", (aMap.getCameraPosition().target).longitude + "");
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
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
            bounds.include(latLng);
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 200));
            CameraUpdateFactory.zoomTo(20);
        } catch (Exception e) {
            Log.i("onMapLoaded()", e.toString());
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
        mapView.onDestroy();

        //退出界面的时候停止定位
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
        }
    }

    @OnClick(R.id.back_bar)
    public void onViewClicked() {
        goToWeb();
    }

    private void goToWeb() {
        Intent intent = new Intent(MainActivity.this, JSCoalAndroidActivity.class);
        startActivity(intent);
    }
}
