package com.sxhalo.PullCoal.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Created by amoldZhang on 2017/8/4.
 */

public class MineMouth implements Serializable {

    /**
     * regionCode : 陕西省榆林市神木县
     * mineMouthName : 海湾煤矿
     * address : 神木县城北35KM处
     * mineMouthId : 10000058
     * mineMouthPic : http://login.sxhalo.com:80/sxhaloadm/upload/1495681305762.jpg
     * longitude : 110.35593062639236
     * coalSalesNum : 1
     * latitude : 39.0543262213148
     * typeId : 0
     */

    private String regionCode;
    private String mineMouthName;
    private String address;
    private String mineMouthId;
    private String regionName;
    private String mineMouthPic;
    private String longitude;
    private String coalSalesNum;
    private String latitude;
    private String typeId;
    private String contactPhone;
    private String contactPerson;
    private String operatingStatus;
    private String mineMouthPicUrl1;
    private String mineMouthPicUrl2;
    private String mineMouthPicUrl3;
    private String profile;
    private String reportEndDate;
    private String goodsTotal;
    private String transTotal;


    private List<InformationDepartment> informationDepartmentList;
    private MineDynamic mineDynamic = new MineDynamic();

    public MineMouth getMineMouthData(List<APPData<Map<String, Object>>> data) {
        MineMouth mineMouthData = new MineMouth();
        MineMouth mineMouthDataInformation = new MineMouth();
        MineMouth mineMouthDataDynamic = new MineMouth();
        for (int i=0;i<data.size();i++){
            APPData<Map<String, Object>> mapAPPData = data.get(i);
            if (mapAPPData.getDataType().equals("coal010002")){   //矿口信息
                mineMouthData = getMineMouthData(mineMouthData,mapAPPData.getEntity());
            }else if (mapAPPData.getDataType().equals("coal020005")){ //信息部信息
                Gson gson = new Gson();
                String jsonString = gson.toJson(mapAPPData.getList());
                Type type = new TypeToken<List<InformationDepartment>>(){}.getType();
                List<InformationDepartment> informationDepartmentList = gson.fromJson(jsonString,type);
                mineMouthDataInformation.setInformationDepartmentList(informationDepartmentList);
            }else if (mapAPPData.getDataType().equals("coal010004")){  //矿口动态信息
                mineDynamic = new MineDynamic();
                mineMouthDataDynamic.setMineDynamic(mineDynamic.getMineDynamic(mapAPPData.getEntity()));
            }
        }
        mineMouthData.setInformationDepartmentList(mineMouthDataInformation.getInformationDepartmentList());
        mineMouthData.setMineDynamic(mineMouthDataDynamic.getMineDynamic());
        return mineMouthData;
    }

    private MineMouth getMineMouthData(MineMouth mineMouthData, Map<String, Object> entity) {
        if (entity.size() != 0 ){
            Gson gson = new Gson();
            String jsonString = gson.toJson(entity);
            mineMouthData = gson.fromJson(jsonString,MineMouth.class);
        }
        return mineMouthData;
    }

    public String getGoodsTotal() {
        return goodsTotal;
    }

    public void setGoodsTotal(String goodsTotal) {
        this.goodsTotal = goodsTotal;
    }

    public String getTransTotal() {
        return transTotal;
    }

    public void setTransTotal(String transTotal) {
        this.transTotal = transTotal;
    }

    public String getReportEndDate() {
        return reportEndDate;
    }

    public void setReportEndDate(String reportEndDate) {
        this.reportEndDate = reportEndDate;
    }

    public String getOperatingStatus() {
        return operatingStatus;
    }

    public void setOperatingStatus(String operatingStatus) {
        this.operatingStatus = operatingStatus;
    }

    public String getMineMouthPicUrl1() {
        return mineMouthPicUrl1;
    }

    public void setMineMouthPicUrl1(String mineMouthPicUrl1) {
        this.mineMouthPicUrl1 = mineMouthPicUrl1;
    }

    public String getMineMouthPicUrl2() {
        return mineMouthPicUrl2;
    }

    public void setMineMouthPicUrl2(String mineMouthPicUrl2) {
        this.mineMouthPicUrl2 = mineMouthPicUrl2;
    }

    public String getMineMouthPicUrl3() {
        return mineMouthPicUrl3;
    }

    public void setMineMouthPicUrl3(String mineMouthPicUrl3) {
        this.mineMouthPicUrl3 = mineMouthPicUrl3;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public MineDynamic getMineDynamic() {
        return mineDynamic;
    }

    public void setMineDynamic(MineDynamic mineDynamic) {
        this.mineDynamic = mineDynamic;
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

    public List<InformationDepartment> getInformationDepartmentList() {
        return informationDepartmentList;
    }

    public void setInformationDepartmentList(List<InformationDepartment> informationDepartmentList) {
        this.informationDepartmentList = informationDepartmentList;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getMineMouthName() {
        return mineMouthName;
    }

    public void setMineMouthName(String mineMouthName) {
        this.mineMouthName = mineMouthName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMineMouthId() {
        return mineMouthId;
    }

    public void setMineMouthId(String mineMouthId) {
        this.mineMouthId = mineMouthId;
    }

    public String getMineMouthPic() {
        return mineMouthPic;
    }

    public void setMineMouthPic(String mineMouthPic) {
        this.mineMouthPic = mineMouthPic;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCoalSalesNum() {
        return coalSalesNum;
    }

    public void setCoalSalesNum(String coalSalesNum) {
        this.coalSalesNum = coalSalesNum;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }


}
