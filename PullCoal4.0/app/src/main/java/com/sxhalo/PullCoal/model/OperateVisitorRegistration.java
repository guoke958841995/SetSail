package com.sxhalo.PullCoal.model;

import com.google.gson.Gson;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class OperateVisitorRegistration implements Serializable {

//         "dictValue":"焦炭",  //煤种
//                 "quantity":"",    //需求量
//                 "registerTime":1550645498000,  // 登记时间
//                 "followName":"郭振家",  //  跟单人
//                 "registrarName":"姚进",  //  登记人
//                 "contactNumber":"18792652286",  // 联系人电话
//                 "updateTime":1550716740000,  // 更新时间
//                 "fromAddress":"北京市",  //  出发地
//                 "registerNum":1902200000,  // id
//                 "toAddress":"汉中汉台区",  // 目的地
//                 "customerName":"康",  // 联系人
//                 "status":""  //状态
//                 "differMinute":"3分钟前"  //状态

    private String registerNum; //id
    private String roleId; // 角色id
    private String customerName; //联系人
    private String contactNumber; //联系人电话
    private String registerType; //0找煤   1找车  默认0

    private String fromPlaceName; //出发地城市名称
    private String fromAddress; //出发地详细地址
    private String toPlaceName; //目的地城市名称
    private String toAddress; //目的地详细地址
    private String dictValue; //煤种
    private String quantity; //需求量
    private String followName; //跟单人
    private String followNum; //跟单人进行中的单数
    private String followPerson; //跟单人Id
    private String registrarName; //登记人
    private String registerTime; //登记时间
    private String updateTime; //更新时间
    private String differMinute; //处理后时间
    private String status; //状态  0进行中  1完成

    private String otherNeeds; //其他需求
    private String cost; //运费
    private String loadingCharge; //装卸费
    private String calorificValue; //热量
    private String coalCategoryId; //煤种id
    private String remark; //备注

    private String fromLongitude; //出发地经度
    private String fromLatitude; //出发地 纬度
    private String fromPlace; //出发地 地区

    private String toLongitude; //目的地经度
    private String toLatitude; //目的地 纬度
    private String toPlace; //目的地 地区

    private List<FollowUsers> followUsersList;


    public class FollowUsers{
        public String realName; //跟单人员姓名
        public String num; //进行中的单数
        public String userId; //跟单人id
    }



    public OperateVisitorRegistration getOperateVisitorRegistration(APPData<Map<String, Object>> data) {
        OperateVisitorRegistration operateVisitorRegistration = new OperateVisitorRegistration();
        followUsersList = new ArrayList<>();
      if (data != null){
          if (data.getEntity() != null ){
              Gson gson = new Gson();
              String jsonString = gson.toJson(data.getEntity());
              operateVisitorRegistration = gson.fromJson(jsonString,OperateVisitorRegistration.class);

              if (data.getFollowUsers() != null && data.getFollowUsers().size() != 0){
                  for (Map<String, Object> map : data.getFollowUsers()){
                      String jsonFollow = gson.toJson(map);
                      FollowUsers followUsers = gson.fromJson(jsonFollow,FollowUsers.class);
                      followUsersList.add(followUsers);
                  }
              }
          }
          operateVisitorRegistration.setFollowUsersList(followUsersList);
      }
      return operateVisitorRegistration;
    }

    public String getLoadingCharge() {
        return loadingCharge;
    }

    public void setLoadingCharge(String loadingCharge) {
        this.loadingCharge = loadingCharge;
    }

    public String getRegisterType() {
        return registerType;
    }

    public void setRegisterType(String registerType) {
        this.registerType = registerType;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getFromPlaceName() {
        return fromPlaceName;
    }

    public void setFromPlaceName(String fromPlaceName) {
        this.fromPlaceName = fromPlaceName;
    }

    public String getToPlaceName() {
        return toPlaceName;
    }

    public void setToPlaceName(String toPlaceName) {
        this.toPlaceName = toPlaceName;
    }

    public String getFollowNum() {
        return followNum;
    }

    public void setFollowNum(String followNum) {
        this.followNum = followNum;
    }

    public String getCalorificValue() {
        return calorificValue;
    }

    public void setCalorificValue(String calorificValue) {
        this.calorificValue = calorificValue;
    }

    public String getFollowPerson() {
        return followPerson;
    }

    public void setFollowPerson(String followPerson) {
        this.followPerson = followPerson;
    }

    public String getOtherNeeds() {
        return otherNeeds;
    }

    public void setOtherNeeds(String otherNeeds) {
        this.otherNeeds = otherNeeds;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getCoalCategoryId() {
        return coalCategoryId;
    }

    public void setCoalCategoryId(String coalCategoryId) {
        this.coalCategoryId = coalCategoryId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getFromLongitude() {
        return fromLongitude;
    }

    public void setFromLongitude(String fromLongitude) {
        this.fromLongitude = fromLongitude;
    }

    public String getFromLatitude() {
        return fromLatitude;
    }

    public void setFromLatitude(String fromLatitude) {
        this.fromLatitude = fromLatitude;
    }

    public String getFromPlace() {
        return fromPlace;
    }

    public void setFromPlace(String fromPlace) {
        this.fromPlace = fromPlace;
    }

    public String getToLongitude() {
        return toLongitude;
    }

    public void setToLongitude(String toLongitude) {
        this.toLongitude = toLongitude;
    }

    public String getToLatitude() {
        return toLatitude;
    }

    public void setToLatitude(String toLatitude) {
        this.toLatitude = toLatitude;
    }

    public String getToPlace() {
        return toPlace;
    }

    public void setToPlace(String toPlace) {
        this.toPlace = toPlace;
    }

    public List<FollowUsers> getFollowUsersList() {
        return followUsersList;
    }

    public void setFollowUsersList(List<FollowUsers> followUsersList) {
        this.followUsersList = followUsersList;
    }

    public String getRegisterNum() {
        return registerNum;
    }

    public void setRegisterNum(String registerNum) {
        this.registerNum = registerNum;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getDictValue() {
        return dictValue;
    }

    public void setDictValue(String dictValue) {
        this.dictValue = dictValue;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getFollowName() {
        return followName;
    }

    public void setFollowName(String followName) {
        this.followName = followName;
    }

    public String getRegistrarName() {
        return registrarName;
    }

    public void setRegistrarName(String registrarName) {
        this.registrarName = registrarName;
    }

    public String getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDifferMinute() {
        return differMinute;
    }

    public void setDifferMinute(String differMinute) {
        this.differMinute = differMinute;
    }
}