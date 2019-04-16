package com.sxhalo.PullCoal.model;

import java.io.Serializable;

/**
 * 留言列表bean
 */

public class UserGuestbook implements Serializable{


    /**
     *  "createTime": "2018-08-02",
        "message": "榆林煤价突破500元/吨 专家预计春节过后将回落",
        "nickname": "昵称",
        "headPic": "http://login.sxhalo.com:80/lamadm/upload/1484380908522.jpg",
        "remark": "处理结果",//处理结果
        "dealTime": "2018-08-02",//处理时间
        "dealState": "1"//处理状态：0、待处理；1、已处理
     */

    private String createTime;//留言时间
    private String message;//留言内容
    private String nickname;//昵称
    private String headPic;//头像
    private String remark;//处理结果
    private String dealTime;//处理时间
    private String dealState;//处理状态

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDealTime() {
        return dealTime;
    }

    public void setDealTime(String dealTime) {
        this.dealTime = dealTime;
    }

    public String getDealState() {
        return dealState;
    }

    public void setDealState(String dealState) {
        this.dealState = dealState;
    }
}
