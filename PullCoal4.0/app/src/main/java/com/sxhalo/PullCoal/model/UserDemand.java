package com.sxhalo.PullCoal.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sxhalo.PullCoal.utils.GHLog;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 买煤求购实体
 * Created by amoldZhang on 2017/8/19.
 */
public class UserDemand  implements Serializable{


    /**
     * receiptAddress : 山东省日照市岚山区碑廓镇
     * regionCode : 371103100
     * contactPhone : 13699655548
     * price : 343.0
     * coalName : 大块煤
     * demandId : ST1501828614741
     * categoryId : 3
     * userId : 80000256
     * regionName : 山东省日照市岚山区碑廓镇
     * number : 343.0
     * contactPerson : 小宋
     * createTime : 2017-08-07 07:16:46
     * illustrate : 山东省日照市岚山
     * deliveryEndTime ："2017-08-07 07:16:46"
     * selectedDelivery ：20000001
     */

    private String receiptAddress;
    private String regionCode;
    private String contactPhone;
    private String price;
    private String coalName;
    private String demandId;
    private String categoryId;
    private String userId;
    private String regionName;
    private String number;
    private String contactPerson;
    private String createTime;
    private String bond;           // 保证金
    private String deliveryTotal;  // 信息部投递数量
    private String illustrate;     // 备注
    private String calorificValue; // 发热量
    private String totalSulfur;    // 含硫量
    private String headPic;        // 头像
    private String creditRating;   // 信用等级
    private String realnameAuthState;    // 是否已经实名认证  1-认证
    private String demandState;          // 状态：0、未发布；1、已发布（待投递，确认）；2、待选定；3、磋商；4、退款；5、完成；100、取消
    private String deliveryEndTime;      // 意向投递截止时间
    private String selectedDelivery;     // 选定的意向投递人id
    private String selectedSalesId;     // 选定的信息部id
    private String ifSelfRefund;        //是否本人申请退款0、否；1、是

    private List<DelivererInformationDepartment> informationDepartmentList;
    private List<CoalOrderStatusEntity> entityList;

    private String flage;                // 是否需要平台介入 1 介入 0不介入
    private String reasonString;         // 介入原因 或 退款原因


    public UserDemand getUserDemandInfoData(List<APPData<Map<String, Object>>> mapList){
        UserDemand userDemand = new UserDemand();
        UserDemand  infoUserDemand = new UserDemand();
        UserDemand  statusEntity = new UserDemand();
        try {
            if (mapList.size() != 0){
                for (int i=0;i<mapList.size();i++){
                    APPData<Map<String, Object>> mapData = mapList.get(i);
                    if (mapData.getTitle().equals("买煤求购信息")){
                        userDemand = getUserDemand(mapData.getEntity());
                    }else if (mapData.getTitle().equals("意向投递信息")){
                        List<DelivererInformationDepartment> informationDepartmentList = getInformationDepartmentList(mapData.getList());
                        infoUserDemand.setInformationDepartmentList(informationDepartmentList);
                    }else if (mapData.getTitle().equals("买煤求购流程信息")){
                        List<CoalOrderStatusEntity> entityList = getAPPDataEntityList(mapData.getList());
                        statusEntity.setEntityList(entityList);
                    }
                }
                userDemand.setInformationDepartmentList(infoUserDemand.getInformationDepartmentList());
                userDemand.setEntityList(statusEntity.getEntityList());
            }
        } catch (Exception e) {
            GHLog.e("数据解析",e.toString());
        }
        return userDemand;
    }

    private UserDemand getUserDemand(Map<String, Object> stringObjectMap){
        Type type = new TypeToken<UserDemand>() {}.getType();
        UserDemand  userDemand = new UserDemand();
        if (stringObjectMap.size() != 0){
            try {
                Gson gson = new Gson();
                String jsonString = gson.toJson(stringObjectMap);
                userDemand = gson.fromJson(jsonString,type);
            } catch (Exception e) {
                GHLog.e("买煤求购详情",e.toString());
            }
        }
        return userDemand;
    }

    public List<DelivererInformationDepartment> getInformationDepartmentList(List<Map<String, Object>> mapList) {
        Type type = new TypeToken<List<DelivererInformationDepartment>>() {}.getType();
        List<DelivererInformationDepartment>  informationDepartmentList = new ArrayList<>();
        if (mapList.size() != 0){
            try {
                Gson gson = new Gson();
                String jsonString = gson.toJson(mapList);
                informationDepartmentList = gson.fromJson(jsonString,type);
            } catch (Exception e) {
                GHLog.e("待确定意向信息部确定",e.toString());
            }
        }
        return informationDepartmentList;
    }

    public List<CoalOrderStatusEntity> getAPPDataEntityList(List<Map<String, Object>> mapList) {
        Type type = new TypeToken<List<CoalOrderStatusEntity>>() {}.getType();
        List<CoalOrderStatusEntity>  informationDepartmentList = new ArrayList<>();
        if (mapList.size() != 0){
            try {
                Gson gson = new Gson();
                String jsonString = gson.toJson(mapList);
                informationDepartmentList = gson.fromJson(jsonString,type);
            } catch (Exception e) {
                GHLog.e("退款详情解析",e.toString());
            }
        }
        return informationDepartmentList;
    }

    public String getIfSelfRefund() {
        return ifSelfRefund;
    }

    public void setIfSelfRefund(String ifSelfRefund) {
        this.ifSelfRefund = ifSelfRefund;
    }

    public String getSelectedSalesId() {
        return selectedSalesId;
    }

    public void setSelectedSalesId(String selectedSalesId) {
        this.selectedSalesId = selectedSalesId;
    }

    public String getFlage() {
        return flage;
    }

    public void setFlage(String flage) {
        this.flage = flage;
    }

    public String getReasonString() {
        return reasonString;
    }

    public void setReasonString(String reasonString) {
        this.reasonString = reasonString;
    }

    public List<CoalOrderStatusEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<CoalOrderStatusEntity> entityList) {
        this.entityList = entityList;
    }

    public List<DelivererInformationDepartment> getInformationDepartmentList() {
        return informationDepartmentList;
    }

    public void setInformationDepartmentList(List<DelivererInformationDepartment> informationDepartmentList) {
        this.informationDepartmentList = informationDepartmentList;
    }

    public String getDeliveryEndTime() {
        return deliveryEndTime;
    }

    public void setDeliveryEndTime(String deliveryEndTime) {
        this.deliveryEndTime = deliveryEndTime;
    }

    public String getSelectedDelivery() {
        return selectedDelivery;
    }

    public void setSelectedDelivery(String selectedDelivery) {
        this.selectedDelivery = selectedDelivery;
    }

    public String getDemandState() {
        return demandState;
    }

    public void setDemandState(String demandState) {
        this.demandState = demandState;
    }

    public String getDeliveryTotal() {
        return deliveryTotal;
    }

    public void setDeliveryTotal(String deliveryTotal) {
        this.deliveryTotal = deliveryTotal;
    }


    public String getBond() {
        return bond;
    }

    public void setBond(String bond) {
        this.bond = bond;
    }

    public String getRealnameAuthState() {
        return realnameAuthState;
    }

    public void setRealnameAuthState(String realnameAuthState) {
        this.realnameAuthState = realnameAuthState;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getCreditRating() {
        return creditRating;
    }

    public void setCreditRating(String creditRating) {
        this.creditRating = creditRating;
    }

    public String getTotalSulfur() {
        return totalSulfur;
    }

    public void setTotalSulfur(String totalSulfur) {
        this.totalSulfur = totalSulfur;
    }

    public String getCalorificValue() {
        return calorificValue;
    }

    public void setCalorificValue(String calorificValue) {
        this.calorificValue = calorificValue;
    }
    public String getReceiptAddress() {
        return receiptAddress;
    }

    public void setReceiptAddress(String receiptAddress) {
        this.receiptAddress = receiptAddress;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCoalName() {
        return coalName;
    }

    public void setCoalName(String coalName) {
        this.coalName = coalName;
    }

    public String getDemandId() {
        return demandId;
    }

    public void setDemandId(String demandId) {
        this.demandId = demandId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getIllustrate() {
        return illustrate;
    }

    public void setIllustrate(String illustrate) {
        this.illustrate = illustrate;
    }
}
