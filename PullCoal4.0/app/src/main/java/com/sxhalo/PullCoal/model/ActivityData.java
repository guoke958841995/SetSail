package com.sxhalo.PullCoal.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ActivityData implements Serializable{

    //轮播图
    private List<Slide> slideList = new ArrayList<Slide>();
    //矿口动态列表
    private List<MineDynamic> mineDynamicList = new ArrayList<MineDynamic>();


    /**
     * 得到联网反回的数据并处理
     * @param mapList
     * @return
     */
    public ActivityData getActivityData(List<APPData<Map<String, Object>>> mapList) {
        ActivityData activityData = new ActivityData();
        for (int i = 0;i< mapList.size();i++){
            APPData<Map<String, Object>> appDataList = mapList.get(i);
            String mapTitle = appDataList.getTitle();
            for (int k=0;k<appDataList.getList().size();k++){
                Map<String, Object> objectMap = appDataList.getList().get(k);
                if (objectMap.size() != 0){
                    addData(mapTitle,objectMap);
                }
            }
        }
        activityData.setSlideList(slideList);
        activityData.setMineDynamicList(mineDynamicList);
        return activityData;
    }

    private void addData(String mapTitle, Map<String, Object> objectMap) {
        if (mapTitle.equals("轮播图")){
            Slide slide = new Slide().getSlide(objectMap);
            slideList.add(slide);
        }else if (mapTitle.equals("矿口动态信息")){
            MineDynamic mineDynamic = new MineDynamic().getMineDynamic(objectMap);
            mineDynamicList.add(mineDynamic);
        }
    }

    public List<Slide> getSlideList() {
        return slideList;
    }

    public void setSlideList(List<Slide> slideList) {
        this.slideList = slideList;
    }

    public List<MineDynamic> getMineDynamicList() {
        return mineDynamicList;
    }

    public void setMineDynamicList(List<MineDynamic> mineDynamicList) {
        this.mineDynamicList = mineDynamicList;
    }
}