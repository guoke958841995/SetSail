package com.sxhalo.PullCoal.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 行政区划数据 - 省
 */

public class ProvinceModel implements Serializable {

    private String provinceId;
    private String provinceName;
    private List<CityModel> citys;

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public List<CityModel> getCitys() {
        return citys;
    }

    public void setCitys(List<CityModel> citys) {
        this.citys = citys;
    }

    @Override
    public String toString() {
        return "ProvinceModel{" +
                "provinceId='" + provinceId + '\'' +
                ", provinceName='" + provinceName + '\'' +
                ", citys=" + citys +
                '}';
    }
}
