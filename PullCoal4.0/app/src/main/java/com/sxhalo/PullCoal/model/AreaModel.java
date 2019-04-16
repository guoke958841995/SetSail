package com.sxhalo.PullCoal.model;

import java.io.Serializable;

/**
 * Created by liz on 2017/8/17.
 */

public class AreaModel implements Serializable {

    private String areaName;

    private String areaId;

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

}
