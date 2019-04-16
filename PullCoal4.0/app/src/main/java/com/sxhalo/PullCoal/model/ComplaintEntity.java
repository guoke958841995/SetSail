package com.sxhalo.PullCoal.model;


import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by amoldZhang on 2018/3/12.
 */
public class ComplaintEntity {


    /**
     * createTime : 2018-08-02 12:32:56
     * headPic : http://login.sxhalo.com:80/lamadm/upload/1484380908522.jpg
     * complaintMemo : 投诉内容
     * type : 0
     */

    private String createTime;
    private String headPic;
    private String complaintMemo;
    private String type;  //0用户投诉，1运营回复

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getComplaintMemo() {
        return complaintMemo;
    }

    public void setComplaintMemo(String complaintMemo) {
        this.complaintMemo = complaintMemo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
