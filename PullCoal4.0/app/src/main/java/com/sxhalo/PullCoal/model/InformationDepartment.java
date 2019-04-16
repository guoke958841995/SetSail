package com.sxhalo.PullCoal.model;

import com.google.gson.Gson;
import com.sxhalo.PullCoal.utils.GHLog;

import java.io.Serializable;
import java.security.PrivateKey;
import java.util.List;
import java.util.Map;

/**
 * 信息部
 * Created by amoldZhang on 2016/12/8.
 */
public class InformationDepartment implements Serializable {
//
//    "isFollow": "",
//            "creditRating": "3",
//            "coalSalePic": "",
//            "contactPhone": "13699655548",
//            "orderTotal": "",
//            "address": "复合管到发货速度发货",
//            "transTotal": "",
//            "longitude": "22.0",
//            "coalSalesId": "20000031",
//            "latitude": "11.0",
//            "companyName": "测试信息部",
//            "typeId": "2",
//            "contactPerson": "有人"


    private String creditRating;  //creditRating
    private String companyName;
    private String coalSalesId;
    private String contactPhone;  //联系电话
    private String contactPerson; //联系人
    private String address;   //address
    private String coalSalePic;  //imageUrl
    private String isFollow;  //是否关注
    private String orderTotal; //预约单数
    private String transOrderTotal; //货运单数

    private String typeId;     //信息部类别
    private String longitude;
    private String latitude;
    private String managementMode;  //0矿口直营1矿口代理2其他
    private String transTotal; //货运总条数
    private String goodsTotal;      // 煤炭总条数
    private String operatingStatus;  //营业状态： //1（营业）、2（停业）、3（关闭）、4（筹建）、5（其他）',
    private String reportEndDate;  //停业截至日期
    private String coalSalePicUrl1;  //
    private String coalSalePicUrl2;  //
    private String coalSalePicUrl3;  //
    private String provideTransport;  //是否可派车

//    private List<Coal>  coal;
//    private List<TransportMode>  transport;



    public InformationDepartment(){
    }


    public InformationDepartment getInformationDepartmentInfoData(List<APPData<Map<String, Object>>> mapList) {
        InformationDepartment  info = new InformationDepartment();
//        InformationDepartment  infoCoal = new InformationDepartment();
//        InformationDepartment  infoTransport = new InformationDepartment();
        try {
            if (mapList.size() != 0){
                for (int i=0;i<mapList.size();i++){
                    APPData<Map<String, Object>> mapData = mapList.get(i);
                    if (mapData.getTitle().equals("信息部详情")){
                        info = getInformationDepartment(mapData.getEntity());
                    }
//                    else if (mapData.getTitle().equals("煤炭列表")){
//                        List<Coal> coalList = new Coal().getCoalList(mapData.getList());
//                        infoCoal.setCoal(coalList);
//                    }else if (mapData.getTitle().equals("货运列表")){
//                        try {
//                            List<TransportMode> transportList = new TransportMode().getTransportList(mapData.getList());
//                            infoTransport.setTransport(transportList);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
                }
//                info.setCoal(infoCoal.getCoal());
//                info.setTransport(infoTransport.getTransport());
            }
        } catch (Exception e) {
            GHLog.e("数据解析",e.toString());
        }
        return info;
    }

    public InformationDepartment getInformationDepartment(Map<String, Object> stringObjectMap) {
        InformationDepartment  info = new InformationDepartment();
        if (stringObjectMap.size() != 0){
            try {
                Gson gson = new Gson();
                String jsonString = gson.toJson(stringObjectMap);
                info = gson.fromJson(jsonString,InformationDepartment.class);
            } catch (Exception e) {
                GHLog.e("数据解析有null",e.toString());
            }
        }
        return info;
    }

    public String getProvideTransport() {
        return provideTransport;
    }

    public void setProvideTransport(String provideTransport) {
        this.provideTransport = provideTransport;
    }

    public String getCoalSalePicUrl1() {
        return coalSalePicUrl1;
    }

    public void setCoalSalePicUrl1(String coalSalePicUrl1) {
        this.coalSalePicUrl1 = coalSalePicUrl1;
    }

    public String getCoalSalePicUrl2() {
        return coalSalePicUrl2;
    }

    public void setCoalSalePicUrl2(String coalSalePicUrl2) {
        this.coalSalePicUrl2 = coalSalePicUrl2;
    }

    public String getCoalSalePicUrl3() {
        return coalSalePicUrl3;
    }

    public void setCoalSalePicUrl3(String coalSalePicUrl3) {
        this.coalSalePicUrl3 = coalSalePicUrl3;
    }

    public String getTransOrderTotal() {
        return transOrderTotal;
    }

    public void setTransOrderTotal(String transOrderTotal) {
        this.transOrderTotal = transOrderTotal;
    }

    public String getGoodsTotal() {
        return goodsTotal;
    }

    public void setGoodsTotal(String goodsTotal) {
        this.goodsTotal = goodsTotal;
    }

    public String getOperatingStatus() {
        return operatingStatus;
    }

    public void setOperatingStatus(String operatingStatus) {
        this.operatingStatus = operatingStatus;
    }

    public String getReportEndDate() {
        return reportEndDate;
    }

    public void setReportEndDate(String reportEndDate) {
        this.reportEndDate = reportEndDate;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCoalSalesId() {
        return coalSalesId;
    }

    public void setCoalSalesId(String coalSalesId) {
        this.coalSalesId = coalSalesId;
    }

    public String getCoalSalePic() {
        return coalSalePic;
    }

    public void setCoalSalePic(String coalSalePic) {
        this.coalSalePic = coalSalePic;
    }

    public String getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(String isFollow) {
        this.isFollow = isFollow;
    }

    public String getManagementMode() {
        return managementMode;
    }

    public void setManagementMode(String managementMode) {
        this.managementMode = managementMode;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

//    public List<Coal> getCoal() {
//        return coal;
//    }
//
//    public void setCoal(List<Coal> coal) {
//        this.coal = coal;
//    }
//
//    public List<TransportMode> getTransport() {
//        return transport;
//    }
//
//    public void setTransport(List<TransportMode> transport) {
//        this.transport = transport;
//    }


    public String getCreditRating() {
        return creditRating;
    }

    public void setCreditRating(String creditRating) {
        this.creditRating = creditRating;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(String orderTotal) {
        this.orderTotal = orderTotal;
    }

    public String getTransTotal() {
        return transTotal;
    }

    public void setTransTotal(String transTotal) {
        this.transTotal = transTotal;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }


}
