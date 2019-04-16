package com.sxhalo.PullCoal.model;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Map;

/**
 * 矿口动态实体
 * Created by amoldZhang on 2017/11/13.
 */
public class MineDynamic extends TransportMode implements Serializable{

    /**
     * scenePicUrl4 :
     * reportTime : 刚刚
     * summary : 钓鱼岛v给
     * scenePicUrl3 :
     * scenePicUrl2 :
     * scenePicUrl1 :
     * scenePicUrl8 :
     * scenePicUrl7 :
     * scenePicUrl6 :
     * scenePicUrl5 :
     * scenePicUrl9 :
     */

    private String scenePicUrl4;
    private String reportTime;
    private String mineMouthName;
    private String summary;
    private String scenePicUrl3;
    private String scenePicUrl2;
    private String scenePicUrl1;
    private String scenePicUrl8;
    private String scenePicUrl7;
    private String scenePicUrl6;
    private String scenePicUrl5;
    private String scenePicUrl9;



    public MineDynamic getMineDynamic(Map<String, Object> entity){
        MineDynamic mineDynamic = new MineDynamic();
        if (entity != null){
            Gson gson = new Gson();
            String jsonString = gson.toJson(entity);
            mineDynamic = gson.fromJson(jsonString,MineDynamic.class);
        }
        return mineDynamic;
    }

    public String getMineMouthName() {
        return mineMouthName;
    }

    public void setMineMouthName(String mineMouthName) {
        this.mineMouthName = mineMouthName;
    }

    public String getScenePicUrl4() {
        return scenePicUrl4;
    }

    public void setScenePicUrl4(String scenePicUrl4) {
        this.scenePicUrl4 = scenePicUrl4;
    }

    public String getReportTime() {
        return reportTime;
    }

    public void setReportTime(String reportTime) {
        this.reportTime = reportTime;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getScenePicUrl3() {
        return scenePicUrl3;
    }

    public void setScenePicUrl3(String scenePicUrl3) {
        this.scenePicUrl3 = scenePicUrl3;
    }

    public String getScenePicUrl2() {
        return scenePicUrl2;
    }

    public void setScenePicUrl2(String scenePicUrl2) {
        this.scenePicUrl2 = scenePicUrl2;
    }

    public String getScenePicUrl1() {
        return scenePicUrl1;
    }

    public void setScenePicUrl1(String scenePicUrl1) {
        this.scenePicUrl1 = scenePicUrl1;
    }

    public String getScenePicUrl8() {
        return scenePicUrl8;
    }

    public void setScenePicUrl8(String scenePicUrl8) {
        this.scenePicUrl8 = scenePicUrl8;
    }

    public String getScenePicUrl7() {
        return scenePicUrl7;
    }

    public void setScenePicUrl7(String scenePicUrl7) {
        this.scenePicUrl7 = scenePicUrl7;
    }

    public String getScenePicUrl6() {
        return scenePicUrl6;
    }

    public void setScenePicUrl6(String scenePicUrl6) {
        this.scenePicUrl6 = scenePicUrl6;
    }

    public String getScenePicUrl5() {
        return scenePicUrl5;
    }

    public void setScenePicUrl5(String scenePicUrl5) {
        this.scenePicUrl5 = scenePicUrl5;
    }

    public String getScenePicUrl9() {
        return scenePicUrl9;
    }

    public void setScenePicUrl9(String scenePicUrl9) {
        this.scenePicUrl9 = scenePicUrl9;
    }

}
