package com.sxhalo.PullCoal.model;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Map;

/**
 * 资讯
 * Created by amoldZhang on 2016/12/7.
 */
public class Article implements Serializable{


    /**
     * pushtime : 08-02 18:37
     * title : 榆林煤价突破500元/吨 专家预计春节过后将回落
     * source : 西部网
     * imageUrl : http://login.sxhalo.com:80/lamadm/upload/1484380908522.jpg
     * clickNum : 95
     * publishedUrl : http://172.16.99.199:8080/sxhaloadm/articledetail/5.htm
     * articleId : 5
     */

    private String pushtime;
    private String title;
    private String source;
    private String imageUrl;
    private String clickNum;
    private String publishedUrl;
    private String articleId;
    private String isSpecial;

    private String fromPlace;
    private String toPlace;
    private String content;

    /**
     *
     * @param objectMap
     * @return
     */
    public Article getArticle(Map<String, Object> objectMap) {
        Article article = new Article();
        try {
            Gson gson = new Gson();
            String jsonString = gson.toJson(objectMap);
            article = gson.fromJson(jsonString,Article.class);
        }   catch (Exception e) {
            e.printStackTrace();
        }
        return article;
    }

    public String getFromPlace() {
        return fromPlace;
    }

    public void setFromPlace(String fromPlace) {
        this.fromPlace = fromPlace;
    }

    public String getToPlace() {
        return toPlace;
    }

    public void setToPlace(String toPlace) {
        this.toPlace = toPlace;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }




    public String getIsSpecial() {
        return isSpecial;
    }

    public void setIsSpecial(String isSpecial) {
        this.isSpecial = isSpecial;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }


}
