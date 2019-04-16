package com.sxhalo.PullCoal.model;

/**
 * Created by amoldZhang on 2018/4/24.
 */

public class UserDemandBBS {

//    {
//        "differMinute": "3天前",
//            "headPic": "http://login.sxhalo.com:80/lamadm/upload/1484297937146.jpg",
//            "contactPhone": "13699655548",
//            "contactPerson": "小宋",
//            "content": "XX月XX日至XX月XX日，从XX省XX市XX县到XX省XX市XX县有载重XX吨的半挂车有空拉货，有意向请联系，非诚勿扰！",
//            "realnameAuthState": "1"
//    }

    private String differMinute;
    private String headPic;
    private String contactPhone;
    private String contactPerson;
    private String content;
    private String realnameAuthState;
    private String recordId;

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getDifferMinute() {
        return differMinute;
    }

    public void setDifferMinute(String differMinute) {
        this.differMinute = differMinute;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRealnameAuthState() {
        return realnameAuthState;
    }

    public void setRealnameAuthState(String realnameAuthState) {
        this.realnameAuthState = realnameAuthState;
    }
}
