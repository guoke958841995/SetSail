package com.sxhalo.PullCoal.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 订阅货运
 */
public class RouteEntity implements Serializable {
    /**
     * createTime : 2017-08-19 10:11:03
     * fromPlace : 140822203
     * userId : 80000245
     * toPlace : 140822203
     * toPlaceName : 山西省运城市万荣县南张乡
     * fromPlaceName : 山西省运城市万荣县南张乡
     */

    private String createTime;
    private String fromPlace;
    private String userId;
    private String toPlace;
    private String toPlaceName;
    private String fromPlaceName;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getFromPlace() {
        return fromPlace;
    }

    public void setFromPlace(String fromPlace) {
        this.fromPlace = fromPlace;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToPlace() {
        return toPlace;
    }

    public void setToPlace(String toPlace) {
        this.toPlace = toPlace;
    }

    public String getToPlaceName() {
        return toPlaceName;
    }

    public void setToPlaceName(String toPlaceName) {
        this.toPlaceName = toPlaceName;
    }

    public String getFromPlaceName() {
        return fromPlaceName;
    }

    public void setFromPlaceName(String fromPlaceName) {
        this.fromPlaceName = fromPlaceName;
    }
}
