package com.sxhalo.PullCoal.model;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Map;

/**
 * 广告entity
 * Created by amoldZhang on 2017/8/3.
 */
public class AdvertisementEntity implements Serializable{

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

    public AdvertisementEntity getAdvertisementEntity(Map<String, Object> objectMap) {
        AdvertisementEntity advertisementEntity = new AdvertisementEntity();
        if (objectMap.size() != 0) {
            if (objectMap.size() != 0) {
                Gson gson = new Gson();
                String jsonString = gson.toJson(objectMap);
                advertisementEntity = gson.fromJson(jsonString, AdvertisementEntity.class);
            }
        }
        return advertisementEntity;
    }
}
