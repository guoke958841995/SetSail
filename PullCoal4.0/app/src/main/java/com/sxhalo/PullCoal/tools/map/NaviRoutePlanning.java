package com.sxhalo.PullCoal.tools.map;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;

import static com.sxhalo.PullCoal.common.base.Constant.RECEIVE_MESSAGE;

public class NaviRoutePlanning {

	private  Context context;
	//声明AMapLocationClient类对象
	private AMapLocationClient locationClient = null;
	private AMapLocationClientOption locationOption = new AMapLocationClientOption();


	public NaviRoutePlanning(Context context){
		this.context = context;
		initLocation();
	}

	public NaviRoutePlanning(Context context,NaviDataCoalBack coalBack){
		this.context = context;
		this.coalBack = coalBack;
		initLocation();
	}

	private NaviDataCoalBack coalBack;
	public interface NaviDataCoalBack{
		void getNaviData(AMapLocation loc);
	}

	/**
	 * 初始化定位
	 *
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	private void initLocation(){
		//初始化client
		locationClient = new AMapLocationClient(context);
		//设置定位参数
		locationClient.setLocationOption(getDefaultOption());
		// 设置定位监听
		locationClient.setLocationListener(locationListener);
	}

	/**
	 * 默认的定位参数
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	private AMapLocationClientOption getDefaultOption(){
		AMapLocationClientOption mOption = new AMapLocationClientOption();
		mOption.setLocationMode(AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
		mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
		mOption.setHttpTimeOut(2000);//可选，设置网络请求超时时间。默认为2秒。在仅设备模式下无效
//		mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
		mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
		mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
		mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
		AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
		mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
		mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
		mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
		return mOption;
	}

	/**
	 * //声明定位回调监听器
	 */
	AMapLocationListener locationListener = new AMapLocationListener() {
		@Override
		public void onLocationChanged(AMapLocation loc) {
			if (null != loc) {
				if (coalBack != null){
					coalBack.getNaviData(loc);
				}else{
					//解析定位结果
					locationSuccess(loc);
				}
				stopLocation();
			} else {
				locationFailed();
			}
		}
	};

	private void locationFailed() {
		Intent intent = new Intent(RECEIVE_MESSAGE);
		intent.putExtra("city", "榆林");
		context.sendBroadcast(intent);
	}

	/**
	 * 定位成功
	 *
	 * @param loc
	 */
	private void locationSuccess(AMapLocation loc) {
		SharedTools.putBooleanValue(context, "if_location", true);
		// Log.i("定位进度", result+"");
		SharedTools.putStringValue(context, "longitude", loc.getLongitude() + "");
		SharedTools.putStringValue(context, "latitude", loc.getLatitude() + "");
		SharedTools.putStringValue(context, "address", loc.getAddress() + "");
		SharedTools.putStringValue(context, "city", loc.getCity() + "");
		SharedTools.putStringValue(context, "adCode", loc.getAdCode() + "000");
		SharedTools.putStringValue(context, "cityCode", loc.getCityCode() + "");
		Log.i("定位结果：", NaviUtils.getLocationStr(loc));
		Intent intent = new Intent(RECEIVE_MESSAGE);
		if (StringUtils.isEmpty(loc.getCity())) {
			loc.setCity("榆林");
		}
		intent.putExtra("city", loc.getCity());
		context.sendBroadcast(intent);
	}

	/**
	 * 开始定位
	 *
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	public void startLocation(){
		// 设置定位参数
		locationClient.setLocationOption(locationOption);
		// 启动定位
		locationClient.startLocation();
	}

	/**
	 * 停止定位
	 *
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	public void stopLocation(){
		// 停止定位
		locationClient.stopLocation();
	}

	/**
	 * 销毁定位
	 *
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	public void destroyLocation(){
		if (null != locationClient) {
			/**
			 * 如果AMapLocationClient是在当前Activity实例化的，
			 * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
			 */
			locationClient.onDestroy();
			locationClient = null;
			locationOption = null;
		}
	}

}
