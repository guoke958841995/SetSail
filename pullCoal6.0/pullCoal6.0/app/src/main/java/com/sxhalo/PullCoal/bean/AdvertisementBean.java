package com.sxhalo.PullCoal.bean;

import java.io.Serializable;

/**
 * 广告数据实体类
 *
 * @author Xiao_
 * @date 2019/4/11 0011
 */
public class AdvertisementBean implements Serializable {

    /**
     * adUrl : http://172.16.99.199:8080/sxhaloadm/articledetail/41.htm
     * adPicUrl : http://172.16.99.172:8080/lamadm/upload/1501579875821.jpg
     */

    private String adUrl;
    private String adPicUrl;

    public String getAdUrl() {
        return adUrl;
    }

    public void setAdUrl(String adUrl) {
        this.adUrl = adUrl;
    }

    public String getAdPicUrl() {
        return adPicUrl;
    }

    public void setAdPicUrl(String adPicUrl) {
        this.adPicUrl = adPicUrl;
    }

    @Override
    public String toString() {
        return "AdvertisementBean{" +
                "adUrl='" + adUrl + '\'' +
                ", adPicUrl='" + adPicUrl + '\'' +
                '}';
    }
}
