package com.sxhalo.PullCoal.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sxhalo.PullCoal.utils.LogUtil;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by amoldZhang on 2016/12/21.
 */
public class Orders implements Serializable {

    /**
     *
     "orderNumber": "MT1499394365698",
     "iouUse": "0",//是否白条订单：0否、1是
     "orderState": "0",//订单状态：0、提交订单；1、已受理磋商中；2、拒绝订单；3、受理超期；4、磋商完成待确认成交； 5、已确认成交，开始派车提货；6、超期未确认；7、定单完成；100、取消订单；
     "tradingVolume": "500",//预订量，单位：吨
     "lastAccepTime": "2017-08-07 07:16:46",//受理截至时间
     "createTime": "2017-08-07 07:16:46",//预订时间
     "accepTime": "2017-08-07 07:16:46",//受理时间
     "contactPerson": "刘华",
     "contactPhone": "18965236522",
     "receiptRegionName": "北京市市辖区东城区",//收货地区名称
     "receiptAddress": "北京市市辖区东城区东十四条",//收货详细地址
     "remark": "",//备注
     "coalName": "三八块",
     "coalCategoryName": "原煤",
     "imageUrl": "http://172.16.99.116:8080/lamapi/assets/image/coalType2.png",
     "oneQuote": "450",
     "technologyName": "综采",//加工工艺
     "calorificValue": "6000",//发热量
     "ashValue": "100",//灰分
     "totalMoisture": "50",//全水分
     "totalSulfur": "25",//全硫分
     "volatileValue": "30",//挥发份
     "fixedCarbon": "300",//固定碳
     "coalReportPicUrl": "http://172.16.99.116:8080/lamapi/assets/image/coalType3.png",
     "coalReportInstitution": "陕西华泰能源有限公司",
     "coalReportTime": "2017-08-07 07:16:46",
     "publishUser": "郭二强",
     "publishUserPhone": "13133385545",
     "mineMouthName": "神广煤矿",
     "mineMouthId": "10000060",
     "mineMouthAddress": "陕西省榆林市神木县店塔镇石包墕村",
     "coalSalesName": "光环代理信息部",
     "coalSalesId": "20000001",
     "coalSalesAddress": "曲江池北路曲江公馆和园"
     *
     * */

    private String orderNumber;
    private String orderState;
    private String tradingVolume; //预订量，单位：吨
    private String lastAccepTime;
    private String createTime;
    private String accepTime;
    private String contactPerson;
    private String contactPhone;
    private String receiptRegionName;
    private String receiptAddress;
    private String remark;
    private String coalName;
    private String coalCategoryName;
    private String imageUrl;
    private String categoryImage;   // 默认图片
    private String oneQuote;
    private String technologyName;
    private String calorificValue;
    private String ashValue;
    private String totalMoisture;
    private String totalSulfur;
    private String volatileValue;
    private String fixedCarbon;
    private String coalReportPicUrl;
    private String coalReportInstitution;
    private String coalReportTime;
    private String publishUser;
    private String publishUserPhone;
    private String mineMouthName;
    private String mineMouthId;
    private String mineMouthAddress;
    private String coalSalesName;
    private String coalSalesId;
    private String releaseNum;   //煤炭票据
    private String goodsId; //煤炭id
    private String coalSalesAddress;
    private String transportType;
    private String alreadyVolume; //已完成总量，单位：吨
    private String alreadyAmount; //已完成总金额，单位：分
    private String freezeMoney;   //冻结金额
    private String realTimePrice;   //实时价格
    private String quoteAdjust;   //浮动价格
    private String iouUse;   //是否白条订单：0否、1是


    private List<CoalOrderStatusEntity> entityList;
    private List<SendCarEntity> orderFreightList;

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public String getIouUse() {
        return iouUse;
    }

    public void setIouUse(String iouUse) {
        this.iouUse = iouUse;
    }

    public String getReleaseNum() {
        return releaseNum;
    }

    public void setReleaseNum(String releaseNum) {
        this.releaseNum = releaseNum;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
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

    public String getAlreadyVolume() {
        return alreadyVolume;
    }

    public void setAlreadyVolume(String alreadyVolume) {
        this.alreadyVolume = alreadyVolume;
    }

    public String getAlreadyAmount() {
        return alreadyAmount;
    }

    public void setAlreadyAmount(String alreadyAmount) {
        this.alreadyAmount = alreadyAmount;
    }

    public String getFreezeMoney() {
        return freezeMoney;
    }

    public void setFreezeMoney(String freezeMoney) {
        this.freezeMoney = freezeMoney;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }

    public String getTradingVolume() {
        return tradingVolume;
    }

    public void setTradingVolume(String tradingVolume) {
        this.tradingVolume = tradingVolume;
    }

    public String getLastAccepTime() {
        return lastAccepTime;
    }

    public void setLastAccepTime(String lastAccepTime) {
        this.lastAccepTime = lastAccepTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getAccepTime() {
        return accepTime;
    }

    public void setAccepTime(String accepTime) {
        this.accepTime = accepTime;
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

    public String getReceiptRegionName() {
        return receiptRegionName;
    }

    public void setReceiptRegionName(String receiptRegionName) {
        this.receiptRegionName = receiptRegionName;
    }

    public String getReceiptAddress() {
        return receiptAddress;
    }

    public void setReceiptAddress(String receiptAddress) {
        this.receiptAddress = receiptAddress;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCoalName() {
        return coalName;
    }

    public void setCoalName(String coalName) {
        this.coalName = coalName;
    }

    public String getCoalCategoryName() {
        return coalCategoryName;
    }

    public void setCoalCategoryName(String coalCategoryName) {
        this.coalCategoryName = coalCategoryName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getOneQuote() {
        return oneQuote;
    }

    public void setOneQuote(String oneQuote) {
        this.oneQuote = oneQuote;
    }

    public String getTechnologyName() {
        return technologyName;
    }

    public void setTechnologyName(String technologyName) {
        this.technologyName = technologyName;
    }

    public String getCalorificValue() {
        return calorificValue;
    }

    public void setCalorificValue(String calorificValue) {
        this.calorificValue = calorificValue;
    }

    public String getAshValue() {
        return ashValue;
    }

    public void setAshValue(String ashValue) {
        this.ashValue = ashValue;
    }

    public String getTotalMoisture() {
        return totalMoisture;
    }

    public void setTotalMoisture(String totalMoisture) {
        this.totalMoisture = totalMoisture;
    }

    public String getTotalSulfur() {
        return totalSulfur;
    }

    public void setTotalSulfur(String totalSulfur) {
        this.totalSulfur = totalSulfur;
    }

    public String getVolatileValue() {
        return volatileValue;
    }

    public void setVolatileValue(String volatileValue) {
        this.volatileValue = volatileValue;
    }

    public String getFixedCarbon() {
        return fixedCarbon;
    }

    public void setFixedCarbon(String fixedCarbon) {
        this.fixedCarbon = fixedCarbon;
    }

    public String getCoalReportPicUrl() {
        return coalReportPicUrl;
    }

    public void setCoalReportPicUrl(String coalReportPicUrl) {
        this.coalReportPicUrl = coalReportPicUrl;
    }

    public String getCoalReportInstitution() {
        return coalReportInstitution;
    }

    public void setCoalReportInstitution(String coalReportInstitution) {
        this.coalReportInstitution = coalReportInstitution;
    }

    public String getCoalReportTime() {
        return coalReportTime;
    }

    public void setCoalReportTime(String coalReportTime) {
        this.coalReportTime = coalReportTime;
    }

    public String getPublishUser() {
        return publishUser;
    }

    public void setPublishUser(String publishUser) {
        this.publishUser = publishUser;
    }

    public String getPublishUserPhone() {
        return publishUserPhone;
    }

    public void setPublishUserPhone(String publishUserPhone) {
        this.publishUserPhone = publishUserPhone;
    }

    public String getMineMouthName() {
        return mineMouthName;
    }

    public void setMineMouthName(String mineMouthName) {
        this.mineMouthName = mineMouthName;
    }

    public String getMineMouthId() {
        return mineMouthId;
    }

    public void setMineMouthId(String mineMouthId) {
        this.mineMouthId = mineMouthId;
    }

    public String getMineMouthAddress() {
        return mineMouthAddress;
    }

    public void setMineMouthAddress(String mineMouthAddress) {
        this.mineMouthAddress = mineMouthAddress;
    }

    public String getCoalSalesName() {
        return coalSalesName;
    }

    public void setCoalSalesName(String coalSalesName) {
        this.coalSalesName = coalSalesName;
    }

    public String getCoalSalesId() {
        return coalSalesId;
    }

    public void setCoalSalesId(String coalSalesId) {
        this.coalSalesId = coalSalesId;
    }

    public String getCoalSalesAddress() {
        return coalSalesAddress;
    }

    public void setCoalSalesAddress(String coalSalesAddress) {
        this.coalSalesAddress = coalSalesAddress;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public Orders getOrders(List<APPData<Map<String,Object>>> dataList) {
        Orders orders = new Orders();
        Orders orders2 = new Orders();
        Orders orders3 = new Orders();
        for (APPData<Map<String,Object>> data : dataList) {
            if (data.getDataType().equals("coal080002")) {
                orders = getOrders(data.getEntity());
            }
            if (data.getDataType().equals("coal080003")) {
                List<CoalOrderStatusEntity> entityList = getAPPDataEntityList(data.getList());
                orders2.setEntityList(entityList);
            }
            if (data.getDataType().equals("coal080004")) {
                List<SendCarEntity> entityList = getOrderFreightList(data.getList());
                orders3.setOrderFreightList(entityList);
            }
        }
        orders.setEntityList(orders2.getEntityList());
        orders.setOrderFreightList(orders3.getOrderFreightList());
        return orders;
    }

    private List<SendCarEntity> getOrderFreightList(List<Map<String, Object>> list) {
        Type type = new TypeToken<List<SendCarEntity>>() {}.getType();
        List<SendCarEntity> orderFreightList = new ArrayList<>();
        if (list.size() != 0){
            try {
                Gson gson = new Gson();
                String jsonString = gson.toJson(list);
                orderFreightList = gson.fromJson(jsonString,type);
            } catch (Exception e) {
                LogUtil.e("退款详情解析",e.toString());
            }
        }
        return orderFreightList;
    }

    public List<CoalOrderStatusEntity> getAPPDataEntityList(List<Map<String, Object>> mapList) {
        Type type = new TypeToken<List<CoalOrderStatusEntity>>() {}.getType();
        List<CoalOrderStatusEntity> informationDepartmentList = new ArrayList<>();
        if (mapList.size() != 0){
            try {
                Gson gson = new Gson();
                String jsonString = gson.toJson(mapList);
                informationDepartmentList = gson.fromJson(jsonString,type);
            } catch (Exception e) {
                LogUtil.e("退款详情解析",e.toString());
            }
        }
        return informationDepartmentList;
    }

    private InformationDepartment getInformationDepartment(Map<String, Object> stringObjectMap) {
        Type type = new TypeToken<InformationDepartment>() {}.getType();
        InformationDepartment  informationDepartment = new InformationDepartment();
        if (stringObjectMap.size() != 0){
            try {
                Gson gson = new Gson();
                String jsonString = gson.toJson(stringObjectMap);
                informationDepartment = gson.fromJson(jsonString,type);
            } catch (Exception e) {
                LogUtil.e("买煤求购详情",e.toString());
            }
        }
        return informationDepartment;
    }

    private Orders getOrders(Map<String, Object> stringObjectMap) {
        Type type = new TypeToken<Orders>() {}.getType();
        Orders  orders = new Orders();
        if (stringObjectMap.size() != 0){
            try {
                Gson gson = new Gson();
                String jsonString = gson.toJson(stringObjectMap);
                orders = gson.fromJson(jsonString,type);
            } catch (Exception e) {
                LogUtil.e("买煤求购详情",e.toString());
            }
        }
        return orders;
    }


    public Orders() {

    }

    public List<SendCarEntity> getOrderFreightList() {
        return orderFreightList;
    }

    public void setOrderFreightList(List<SendCarEntity> orderFreightList) {
        this.orderFreightList = orderFreightList;
    }

    public List<CoalOrderStatusEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<CoalOrderStatusEntity> entityList) {
        this.entityList = entityList;
    }
}
