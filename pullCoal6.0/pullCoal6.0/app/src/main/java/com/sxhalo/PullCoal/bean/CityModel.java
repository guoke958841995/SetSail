package com.sxhalo.PullCoal.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 行政区划数据 - 市
 */

public class CityModel implements Serializable {

    private String cityId;
    private String cityName;
    private List<AreaModel> areas;

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public List<AreaModel> getAreas() {
        return areas;
    }

    public void setAreas(List<AreaModel> areas) {
        this.areas = areas;
    }

    @Override
    public String toString() {
        return "CityModel{" +
                "cityId='" + cityId + '\'' +
                ", cityName='" + cityName + '\'' +
                ", areas=" + areas +
                '}';
    }
}
