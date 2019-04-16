package com.sxhalo.PullCoal.tools.map.search;

import android.app.Activity;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

/**
 * 使用高德地图点地理POI 搜索工具
 *
 * Created by amoldZhang on 2017/11/20.
 */
public class MapAddressTranslation implements GeocodeSearch.OnGeocodeSearchListener {

    /**
     * 创建搜索工具类
     */
    private GeocodeSearch geocoderSearch;

    /**
     * 搜索成功后数据反回回调抽象实体
     */
    private AddressTranslation addressTranslation;
    public abstract static class AddressTranslation {

        /**
         * 经纬度转地址回调
         * @param regeocodeResult
         * @param i
         */
        public void  onRegeocodeSearched(RegeocodeResult regeocodeResult, int i){ }

        /**
         *  地址转经纬度回调
         * @param geocodeResult
         * @param i
         */
        public void  onGeocodeSearched(GeocodeResult geocodeResult, int i){ }
    }


    public MapAddressTranslation(Activity mActivity){
        geocoderSearch = new GeocodeSearch(mActivity);
        geocoderSearch.setOnGeocodeSearchListener(this);

    }

    /**
     * 设置搜索回调监听
     * @param addressTranslation
     */
    public void searchListener(AddressTranslation addressTranslation){
        if (addressTranslation != null){
            this.addressTranslation = addressTranslation;
        }
    }

    /**
     * 按照搜索条件进行搜索 （地址转经纬度）
     * @param addressName   表示地址
     * @param cityCode 表示查询城市，中文或者中文全拼，citycode、adcode
     */
    public void searchAddressToCoordinate(String addressName,String cityCode){
        if (geocoderSearch != null){
            GeocodeQuery query = new GeocodeQuery(addressName, cityCode);
            geocoderSearch.getFromLocationNameAsyn(query);
        }
    }


    /**
     * 按照搜索条件进行搜索 （经纬度转地址）
     * @param latLonPoint  需要查找的经纬度
     * @param radius   范围距离
     */
    public void searchCoordinateToAddress(LatLonPoint latLonPoint, float radius){
        if (geocoderSearch != null){
            // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
            RegeocodeQuery query = new RegeocodeQuery(latLonPoint, radius,GeocodeSearch.AMAP);
            geocoderSearch.getFromLocationAsyn(query);
        }
    }


    /**
     *  逆地理编码（坐标转地址）回调
     * 通过 地理经纬度返回地址描述
     * @param regeocodeResult
     * @param i
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        if (addressTranslation != null){
            addressTranslation.onRegeocodeSearched(regeocodeResult,i);
        }
    }

    /**
     *  通过地理地址反回位置信息（地址转坐标）
     *  通过回调接口 解析返回的结果。
     * @param geocodeResult 可以在回调中解析geocodeResult，获取坐标信息。
     * @param i 返回结果成功或者失败的响应码。1000为成功，其他为失败
     */
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        if (addressTranslation != null){
            addressTranslation.onGeocodeSearched(geocodeResult,i);
        }
    }

}
