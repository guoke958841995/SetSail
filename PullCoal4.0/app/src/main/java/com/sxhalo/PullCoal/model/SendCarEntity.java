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
 * Created by liz on 2018/5/19.
 */
public class SendCarEntity implements Serializable{

    /**
     *  "plateNumber": "陕AV5B88",
     "transportOrderCode": "派车单编号",
     "driverRealName": "郭先僧",
     "driverPhone": "13753060133",
     "orderType": "00",//派车单类型：00、买家派单；01、信息部派单；02、物流公司派单
     "certificate": "1Q2W3E",//提货码
     "orderState": "0",//货运状态：0、待处理(派车中)；1、装车中；2、已起运；3、已完成；100、取消
     "createTime": "2017-08-07 07:16:46",//时间
     "driverAuthState": "0",//司机身份认证状态：0、未认证，1、登记，2、认证成功
     "carryWeight": "0",//净重，单位：吨
     "grossWeight": "0",//毛重，单位：吨
     "updateTime": "0",//更新时间
     "carryWeightDocPicUrl": "http://172.16.99.116:8080/lamapi/assets/image/coalType3.png"
     "publishUser": "郭二强",//卖家信息
     "publishUserPhone": "13133385545",//卖家信息
     "coalSalesAddress": "曲江池北路曲江公馆和园",//卖家信息
     "coalSalesLatitude": "39.0543262213148",//卖家信息
     "coalSalesLongitude": "110.35593062639236",//卖家信息
     "mineMouthAddress": "陕西省榆林市神木县神木县城北35KM处",//矿口地址及经纬度
     "mineMouthLatitude": "39.0543262213148",//矿口地址及经纬度
     "mineMouthLongitude": "110.35593062639236",//矿口地址及经纬度
     "contactPerson": "刘华",//买家信息
     "contactPhone": "18965236522",//买家信息
     "fromToPlace": "从哪到哪货运",
     "receiptAddress": "北京市市辖区东城区东十四条"//买家信息
     "carryWeight": "0",//净重，单位：吨
     "price": "200",//结算单价，单位：元
     "priceLock": "200",//锁定单价，单位：元
     "realTimePrice": "200",//实时单价，单位：元
     "quoteAdjust": "20",//订单浮动，单位：元
     "priceAdjust": "20",//派车单浮动，单位：元
     "paymentAmount": "20000",//实付煤款，单位：元
     "accountAmount": "50000",//煤款总额，单位：元
     "freezeAmount": "10000",//已冻结煤款，单位：元
     * **/

    private String plateNumber;
    private String transportOrderCode;
    private String driverRealName;
    private String driverPhone;
    private String orderType; //00、买家派单；01、信息部派单；02、物流公司派单
    private String certificate;
    private String orderState;
    private String createTime;
    private String driverAuthState;
    private String grossWeight;  //毛重，单位：吨
    private String carryWeight;  //净重，单位：吨
    private String priceLock;  //锁定单价，单位：元
    private String realTimePrice;  //实时单价，单位：元
    private String quoteAdjust;  //订单浮动，单位：元
    private String priceAdjust;  //派车单浮动，单位：元
    private String paymentAmount;  //实付煤款，单位：元
    private String accountAmount;  //煤款总额，单位：元
    private String freezeAmount;  //已冻结煤款，单位：元
    private String updateTime;
    private String carryWeightDocPicUrl;
    private String contactPerson;
    private String contactPhone;
    private String fromToPlace;
    private String receiptAddress;
    private String price;  //核算单价，单位：分
    private String userId;//预订人编号，买家用户编号，买家必须是实名认证用户
    private boolean isManualAdd;//是否手动添加派车司机 如果是手动添加 则显示编辑字样

    private List<CoalOrderStatusEntity> entityList;
    private InformationDepartment informationDepartment;

    private MineMouth mineMouth;

    public String getFromToPlace() {
        return fromToPlace;
    }

    public void setFromToPlace(String fromToPlace) {
        this.fromToPlace = fromToPlace;
    }

    public String getAccountAmount() {
        return accountAmount;
    }

    public void setAccountAmount(String accountAmount) {
        this.accountAmount = accountAmount;
    }

    public String getFreezeAmount() {
        return freezeAmount;
    }

    public void setFreezeAmount(String freezeAmount) {
        this.freezeAmount = freezeAmount;
    }

    public String getPriceLock() {
        return priceLock;
    }

    public void setPriceLock(String priceLock) {
        this.priceLock = priceLock;
    }

    public String getRealTimePrice() {
        return realTimePrice;
    }

    public void setRealTimePrice(String realTimePrice) {
        this.realTimePrice = realTimePrice;
    }

    public String getQuoteAdjust() {
        return quoteAdjust;
    }

    public void setQuoteAdjust(String quoteAdjust) {
        this.quoteAdjust = quoteAdjust;
    }

    public String getPriceAdjust() {
        return priceAdjust;
    }

    public void setPriceAdjust(String priceAdjust) {
        this.priceAdjust = priceAdjust;
    }

    public String getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(String paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    private String driverId;   //手动添加，接口没有返回

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getTransportOrderCode() {
        return transportOrderCode;
    }

    public void setTransportOrderCode(String transportOrderCode) {
        this.transportOrderCode = transportOrderCode;
    }

    public String getDriverRealName() {
        return driverRealName;
    }

    public void setDriverRealName(String driverRealName) {
        this.driverRealName = driverRealName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDriverAuthState() {
        return driverAuthState;
    }

    public void setDriverAuthState(String driverAuthState) {
        this.driverAuthState = driverAuthState;
    }

    public String getCarryWeight() {
        return carryWeight;
    }

    public void setCarryWeight(String carryWeight) {
        this.carryWeight = carryWeight;
    }

    public String getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(String grossWeight) {
        this.grossWeight = grossWeight;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getCarryWeightDocPicUrl() {
        return carryWeightDocPicUrl;
    }

    public void setCarryWeightDocPicUrl(String carryWeightDocPicUrl) {
        this.carryWeightDocPicUrl = carryWeightDocPicUrl;
    }


    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getReceiptAddress() {
        return receiptAddress;
    }

    public void setReceiptAddress(String receiptAddress) {
        this.receiptAddress = receiptAddress;
    }

    public boolean isManualAdd() {
        return isManualAdd;
    }

    public void setManualAdd(boolean manualAdd) {
        isManualAdd = manualAdd;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<CoalOrderStatusEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<CoalOrderStatusEntity> entityList) {
        this.entityList = entityList;
    }

    public void setInformationDepartment(InformationDepartment informationDepartment) {
        this.informationDepartment = informationDepartment;
    }

    public void setMineMouth(MineMouth mineMouth) {
        this.mineMouth = mineMouth;
    }

    public InformationDepartment getInformationDepartment() {
        return informationDepartment;
    }

    public MineMouth getMineMouth() {
        return mineMouth;
    }

    public SendCarEntity getEntity(List<APPData<Map<String,Object>>> dataList) {
        SendCarEntity sendCarEntity = new SendCarEntity();
        SendCarEntity sendCarEntity1 = new SendCarEntity();
        InformationDepartment informationDepartment = new InformationDepartment();
        MineMouth mineMouth = new MineMouth();
        for (APPData<Map<String,Object>> data : dataList) {
            if (data.getDataType().equals("coal080007")) {
                //派车单详情
                sendCarEntity = getEntity(data.getEntity());
            }
            if (data.getDataType().equals("coal080003")) {
                //派车单流程信息
                List<CoalOrderStatusEntity> entityList = getAPPDataEntityList(data.getList());
                sendCarEntity1.setEntityList(entityList);
            }
            if (data.getDataType().equals("coal010002")) {
                //矿口信息
                mineMouth = getMineMouthEntity(data.getEntity());
            } if (data.getDataType().equals("coal020003")) {
                //信息部详情
                informationDepartment = getInformationDepartmentEntity(data.getEntity());
            }
        }
        sendCarEntity.setEntityList(sendCarEntity1.getEntityList());
        sendCarEntity.setInformationDepartment(informationDepartment);
        sendCarEntity.setMineMouth(mineMouth);
        return sendCarEntity;
    }

    private SendCarEntity getEntity(Map<String, Object> stringObjectMap) {
        Type type = new TypeToken<SendCarEntity>() {}.getType();
        SendCarEntity  sendCarEntity = new SendCarEntity();
        if (stringObjectMap.size() != 0){
            try {
                Gson gson = new Gson();
                String jsonString = gson.toJson(stringObjectMap);
                sendCarEntity = gson.fromJson(jsonString,type);
            } catch (Exception e) {
                GHLog.e("派车单详情解析",e.toString());
            }
        }
        return sendCarEntity;
    }
    private MineMouth getMineMouthEntity(Map<String, Object> stringObjectMap) {
        Type type = new TypeToken<MineMouth>() {}.getType();
        MineMouth  mineMouth = new MineMouth();
        if (stringObjectMap.size() != 0){
            try {
                Gson gson = new Gson();
                String jsonString = gson.toJson(stringObjectMap);
                mineMouth = gson.fromJson(jsonString,type);
            } catch (Exception e) {
                GHLog.e("矿口信息解析",e.toString());
            }
        }
        return mineMouth;
    }
    private InformationDepartment getInformationDepartmentEntity(Map<String, Object> stringObjectMap) {
        Type type = new TypeToken<InformationDepartment>() {}.getType();
        InformationDepartment  informationDepartment = new InformationDepartment();
        if (stringObjectMap.size() != 0){
            try {
                Gson gson = new Gson();
                String jsonString = gson.toJson(stringObjectMap);
                informationDepartment = gson.fromJson(jsonString,type);
            } catch (Exception e) {
                GHLog.e("信息部详情解析",e.toString());
            }
        }
        return informationDepartment;
    }

    public List<CoalOrderStatusEntity> getAPPDataEntityList(List<Map<String, Object>> mapList) {
        Type type = new TypeToken<List<CoalOrderStatusEntity>>() {}.getType();
        List<CoalOrderStatusEntity>  list = new ArrayList<>();
        if (mapList.size() != 0){
            try {
                Gson gson = new Gson();
                String jsonString = gson.toJson(mapList);
                list = gson.fromJson(jsonString,type);
            } catch (Exception e) {
                GHLog.e("派车单流程信息",e.toString());
            }
        }
        return list;
    }

    @Override
    public boolean equals(Object arg0) {
        // TODO Auto-generated method stub
        SendCarEntity p = (SendCarEntity) arg0;
        return getPlateNumber().equals(p.getPlateNumber()) && getDriverPhone().equals(p.getDriverPhone());
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        String str = driverPhone + plateNumber;
        return str.hashCode();
    }
}
