package com.sxhalo.PullCoal.model;

import com.sxhalo.PullCoal.model.CityModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liz on 2017/8/17.
 */

public class ProvinceModel implements Serializable{

    private List<CityModel> citys;
    private String provinceId;
    private String provinceName;

    public List<CityModel> getCitys() {
        return citys;
    }

    public void setCitys(List<CityModel> citys) {
        this.citys = citys;
    }

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
}
