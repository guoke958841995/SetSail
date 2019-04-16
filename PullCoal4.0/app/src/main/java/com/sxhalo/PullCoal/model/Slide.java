package com.sxhalo.PullCoal.model;

import com.google.gson.Gson;

import java.util.Map;

/**
 * 轮播图实体
 * Created by amoldZhang on 2016/12/7.
 */
public class Slide {

//    "imageUrl": "1",
//            "url": "1"

    private String imageUrl;
    private String articleId;
    private String pushtime;
    private String title;
    private String source;
    private String clickNum;
    private String publishedUrl;
    private String isSpecial;
    private String accessCondition; //1 需要登录。0 不需要登录

    private String sendUrl;

    public Slide(){

    }


    public Slide getSlide(Map<String, Object> objectMap) {
        Slide slide = new Slide();
        if (objectMap.size() != 0){
            Gson gson = new Gson();
            String jsonString = gson.toJson(objectMap);
            slide = gson.fromJson(jsonString,Slide.class);
        }
        return slide;
    }

    public String getAccessCondition() {
        return accessCondition;
    }

    public void setAccessCondition(String accessCondition) {
        this.accessCondition = accessCondition;
    }

    public String getSendUrl() {
        return sendUrl;
    }

    public void setSendUrl(String sendUrl) {
        this.sendUrl = sendUrl;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPushtime() {
        return pushtime;
    }

    public void setPushtime(String pushtime) {
        this.pushtime = pushtime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getClickNum() {
        return clickNum;
    }

    public void setClickNum(String clickNum) {
        this.clickNum = clickNum;
    }

    public String getPublishedUrl() {
        return publishedUrl;
    }

    public void setPublishedUrl(String publishedUrl) {
        this.publishedUrl = publishedUrl;
    }

    public String getIsSpecial() {
        return isSpecial;
    }

    public void setIsSpecial(String isSpecial) {
        this.isSpecial = isSpecial;
    }
}
