package com.sxhalo.PullCoal.ui.daiglog;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.ui.wheelcity.AddressData;
import com.sxhalo.PullCoal.ui.wheelcity.OnWheelChangedListener;
import com.sxhalo.PullCoal.ui.wheelcity.WheelView;
import com.sxhalo.PullCoal.ui.wheelcity.adapters.AbstractWheelTextAdapter;
import com.sxhalo.PullCoal.ui.wheelcity.adapters.ArrayWheelAdapter;
import com.sxhalo.PullCoal.ui.wheelview.DateUtils;
import com.sxhalo.PullCoal.ui.wheelview.JudgeDate;
import com.sxhalo.PullCoal.ui.wheelview.ScreenInfo;
import com.sxhalo.PullCoal.ui.wheelview.WheelMain;
import com.sxhalo.PullCoal.utils.DateUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by amoldZhang on 2016/12/19.
 */

public class SelectAddress {

    private  Activity mActivity;
    private String cityTxt;
    private String cityCode;
    private String currentTime;

    private WheelMain wheelMainDate;

    public SelectAddress (Activity mActivity){
        this.mActivity = mActivity;
    }

    public View getAddressDialogView() {
        View addressDialogView = LayoutInflater.from(mActivity).inflate(
                R.layout.wheelcity_cities_layout, null);
        final WheelView country = (WheelView) addressDialogView
                .findViewById(R.id.wheelcity_country);
        country.setVisibleItems(3);
        country.setViewAdapter(new CountryAdapter(mActivity));

        final String citys[][] = AddressData.CITIES;  //城市信息
        final String ccities[][][] = AddressData.COUNTIES;  // 区县信息

        final WheelView city = (WheelView) addressDialogView
                .findViewById(R.id.wheelcity_city);
        city.setVisibleItems(0);

        // 地区选择
        final WheelView ccity = (WheelView) addressDialogView
                .findViewById(R.id.wheelcity_ccity);
        ccity.setVisibleItems(0);// 不限城市

        country.addChangingListener(new OnWheelChangedListener() {

            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                updateCities(city, citys, newValue);
                cityTxt = ""
//                        + AddressData.PROVINCES[country.getCurrentItem()]
//                         + "|"
                        + AddressData.CITIES[country.getCurrentItem()][city.getCurrentItem()]
                        + "|"
                        + AddressData.COUNTIES[country.getCurrentItem()][city
                        .getCurrentItem()][ccity.getCurrentItem()];

                cityCode = ""
//                        + AddressData.P_ID[country.getCurrentItem()]
//                        + "|"
//                        + AddressData.C_ID[country.getCurrentItem()][city.getCurrentItem()]
//                        + "|"
                        + AddressData.C_C_ID[country.getCurrentItem()][city
                        .getCurrentItem()][ccity.getCurrentItem()];

                if(getCity != null){
                    getCity.getCity(cityTxt,cityCode);
                }
            }
        });

        city.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                updatecCities(ccity, ccities, country.getCurrentItem(),
                        newValue);
                cityTxt =""
//                        +AddressData.PROVINCES[country.getCurrentItem()]
//                         + "|"
                        + AddressData.CITIES[country.getCurrentItem()][city .getCurrentItem()]
                        + "|"
                        + AddressData.COUNTIES[country.getCurrentItem()][city
                        .getCurrentItem()][ccity.getCurrentItem()];

                cityCode = ""
//                        + AddressData.P_ID[country.getCurrentItem()]
//                        + "|"
//                        + AddressData.C_ID[country.getCurrentItem()][city.getCurrentItem()]
//                        + "|"
                        + AddressData.C_C_ID[country.getCurrentItem()][city
                        .getCurrentItem()][ccity.getCurrentItem()];

                if(getCity != null){
                    getCity.getCity(cityTxt,cityCode);
                }
            }
        });

        ccity.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                cityTxt =""
//                         AddressData.PROVINCES[country.getCurrentItem()]
//                  + "|"
                        + AddressData.CITIES[country.getCurrentItem()][city
                        .getCurrentItem()]
                        + "|"
                        + AddressData.COUNTIES[country.getCurrentItem()][city
                        .getCurrentItem()][ccity.getCurrentItem()];

                cityCode = ""
//                        + AddressData.P_ID[country.getCurrentItem()]
//                        + "|"
//                        + AddressData.C_ID[country.getCurrentItem()][city.getCurrentItem()]
//                        + "|"
                        + AddressData.C_C_ID[country.getCurrentItem()][city
                        .getCurrentItem()][ccity.getCurrentItem()];

                if(getCity != null){
                    getCity.getCity(cityTxt,cityCode);
                }
            }
        });

        country.setCurrentItem(1);// 设置北京
        city.setCurrentItem(1);
        ccity.setCurrentItem(1);
        return addressDialogView;
    }

    /**
     * Adapter for countries
     */
    private class CountryAdapter extends AbstractWheelTextAdapter {
        // Countries names
        private String countries[] = AddressData.PROVINCES;

        /**
         * Constructor
         */
        protected CountryAdapter(Context context) {
            super(context, R.layout.wheelcity_country_layout, NO_RESOURCE);
            setItemTextResource(R.id.wheelcity_country_name);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return countries.length;
        }

        @Override
        protected CharSequence getItemText(int index) {
            return countries[index];
        }
    }

    /**
     * Updates the city wheel
     */
    private void updateCities(WheelView city, String cities[][], int index) {
        ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(mActivity,
                cities[index]);
        adapter.setTextSize(18);
        city.setViewAdapter(adapter);
        city.setCurrentItem(0);
    }

    /**
     * Updates the ccity wheel
     */
    private void updatecCities(WheelView city, String ccities[][][], int index,
                               int index2) {
        ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(mActivity,
                ccities[index][index2]);
        adapter.setTextSize(18);
        city.setViewAdapter(adapter);
        city.setCurrentItem(0);
    }


    /**
     * 时间选择器
     *
     * @return
     */
    public View dialogTime() {
        View contentView = LayoutInflater.from(mActivity).inflate(
                R.layout.show_popup_window, null);
        ScreenInfo screenInfoDate = new ScreenInfo(mActivity);
        wheelMainDate = new WheelMain(contentView, true);
        wheelMainDate.screenheight = screenInfoDate.getHeight();
        String time = DateUtils.currentMonth().toString();
        Calendar calendar = Calendar.getInstance();
        if (JudgeDate.isDate(time, "yyyy-MM-DD")) {
            try {
                calendar.setTime(new Date(time));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        wheelMainDate.initDateTimePicker(year, month, day);
        currentTime = wheelMainDate.getTime_y_m_d().toString();
        if(getCity != null){
            getCity.getCity(currentTime + DateUtil.getNewTimeType(" HH:mm:ss"),"");
        }
        return contentView;
    }

    public String getSelectedTime() {
        if (wheelMainDate != null) {
            currentTime = wheelMainDate.getTime_y_m_d() + DateUtil.getNewTimeType(" HH:mm:ss");
        }
        return currentTime;
    }


    /**
     * 添加点击外接接口
     */
    private GetCityListener getCity;
    public void getCity(GetCityListener getCity) {
        this.getCity = getCity;
    }
    public interface GetCityListener {
        void getCity(String textCity, String cityCode);
    }
}
