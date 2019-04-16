package com.sxhalo.PullCoal.model;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;

/**
 * Created by amoldZhang on 2017/8/22.
 */
@Table("tb_user_message_type")
public class UserMessage<T> implements Serializable {


    /**
     * content : 尊敬的用户：您于8月07日向聚亿煤炭信息部提交的220.0吨原煤的预约申请已受理，预约成功！请及时联系企业。
     * logId : 77
     * userId : 80000647
     * pushTime : 2017-08-07 14:26:25
     * header : 订单预约成功
     * logType : 1
     * preId : MT1499394365694
     * entity : {"receiptAddress":"","technologyName":"","orderState":"1","mineMouthName":"海湾煤矿","coalName":"水洗三八块","remark":"","imageUrl":"http://172.16.99.116:8080/lamapi/assets/image/coalType1.png","consignee":"","calorificValue":"8270","companyName":"聚亿煤炭信息部","accepTime":"2017-08-22 03:26:16","reservationTime":"2017-08-22 11:20:04","receiptRegionName":"","quote":"0.0","entrust":"0","orderNumber":"MT1503372004354","coalCategoryName":"块煤","longitude":"110.35593062639236","tradingVolume":"36.0","latitude":"39.0543262213148","ashValue":"","oneQuote":"100049.0","consigneePhone":""}
     */

    @Column(value = "logId")
    private String logId;
    @Column(value = "content")
    private String content;
    @Column(value = "user_id")
    private String userId;
    @Column(value = "push_time")
    private String pushTime;
    @Column(value = "header")
    private String header;
    @Column(value = "log_type")
    private String logType;
    @PrimaryKey(AssignType.BY_MYSELF)
    @Column(value = "pre_id")
    private String preId;
    @Column(value = "link_address")
    private String linkAddress;
    @Column(value = "is_read")
    private String isRead;  //是否已读 0否 1是
    @Column(value = "trade_no")
    private String tradeNo;  //是否已读 0否 1是

    private T entity;

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public String getLinkAddress() {
        return linkAddress;
    }

    public void setLinkAddress(String linkAddress) {
        this.linkAddress = linkAddress;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPushTime() {
        return pushTime;
    }

    public void setPushTime(String pushTime) {
        this.pushTime = pushTime;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getPreId() {
        return preId;
    }

    public void setPreId(String preId) {
        this.preId = preId;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }
}
