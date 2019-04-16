package com.sxhalo.PullCoal.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sxhalo.PullCoal.utils.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by amoldZhang on 2018/7/30.
 */
public class ListCoalEntity implements Serializable{

    private List<AdvertisementEntity> advertisementEntityList = new ArrayList<>(); //广告数据
    private List<Coal> coal070004 = new ArrayList<>();   //精品煤炭信息
    private List<Coal> coal070001 = new ArrayList<>();   //煤炭信息

    private int count;

    private String address;
    private String regionName;
    private String contactPerson; //联系人
    private String contactPhone; //发布人电话


//         "mineMouthId":"10000185",
//        "address":"山西省阳泉市矿区中国,山西省,阳泉市,矿区 桃北西街赛鱼街道",
//        "regionName":"山西省阳泉市矿区",
//        "mineMouthName":"阳煤平舒矿",
//        "contactPerson":"",
//        "contactPhone":"",

    private String mineMouthId;
    private String mineMouthName;
    private String salesTotal;  //矿口下代理的信息部数
    private String goodsTotal; //矿口下的煤炭数

    private List<ListCoalEntity> coal010001 = new ArrayList<>();   //矿口列表模式


    //    "coalSalesId":"20000052",
//            "address":"山西省阳泉市矿区桃北西街",
//            "companyName":"浩博煤炭发给",
//            "contactPerson":"魏军华",
//            "contactPhone":"15333533697",

    private String coalSalesId;
    private String companyName;
    private String transportNum;
    private String coalGoodsNum;
    private String companiesNum; //信息部下代理的多少矿口
    private List<ListCoalEntity> coal070003 = new ArrayList<>();   //信息部列表模式


    private List<ListCoalEntity> coalList = new ArrayList<>();   //煤炭形式

    private String dataTape;
    //    private List<Coal> coal070005 = new ArrayList<>();   //收藏过的煤炭信息
//    private List<Coal> coal070006 = new ArrayList<>();   //预定过的煤炭信息
//    private List<Coal> coal070007 = new ArrayList<>();   //浏览过的煤炭信息
    private Coal coal = new Coal();   //浏览过的煤炭信息
    private List<ListCoalEntity> browseDataList = new ArrayList<>();   //煤炭记录


    //待支付的煤炭在列表的位置
    private int pos;
    //待支付的煤炭列表中的点击条目
    private int itemPos;

    public ListCoalEntity() {
    }

    public ListCoalEntity(List<APPData<Map<String, Object>>> data) {
        getListCoalEntity(data);
    }

    public ListCoalEntity(APPData<Map<String, Object>> data) {
        ListCoalEntity listCoalEntity = new ListCoalEntity();
        String dataTape = data.getDataType();
        if ("coal070001".equals(dataTape) ){
            setCount(Integer.valueOf(StringUtils.isEmpty(data.getRecordCount())?"0":data.getRecordCount().replace(".0","")));
            for (int k=0;k<data.getList().size();k++){
                Map<String, Object> objectMap = data.getList().get(k);
                if (objectMap.size() != 0){
                    Type type = new TypeToken<Coal>() {}.getType();
                    Coal coal= (Coal)getData(type,objectMap);
                    coal070001.add(coal);
                }
            }
            listCoalEntity.setCoal070001(coal070001);  // 搜索煤炭列表
            listCoalEntity.setCount(getCount());
        }
    }


    /**
     * 以煤炭形式返回
     * @param mapList
     * @return
     */
    public ListCoalEntity getListCoalEntity(List<APPData<Map<String, Object>>> mapList) {
        ListCoalEntity listCoalEntity = new ListCoalEntity();
        if (mapList != null && mapList.size() != 0){
            for (int i = 0;i< mapList.size();i++){
                APPData<Map<String, Object>> dataList = mapList.get(i);
                String dataTape = dataList.getDataType();
                if ("coal070001".equals(dataTape) || "coal070003".equals(dataTape) || "coal010001".equals(dataTape)){
                    setCount(Integer.valueOf(StringUtils.isEmpty(dataList.getRecordCount())?"0":dataList.getRecordCount().replace(".0","")));
                }
                if ("coal070007".equals(dataTape) || "coal070005".equals(dataTape) || "coal070006".equals(dataTape)){
                    Map<String, Object> objectMap = dataList.getEntity();
                    if (objectMap.size() != 0){
                        addData(dataTape,objectMap);
                    }
                }else{
                    for (int k=0;k<dataList.getList().size();k++){
                        Map<String, Object> objectMap = dataList.getList().get(k);
                        if (objectMap.size() != 0){
                            addData(dataTape,objectMap);
                        }
                    }
                }
            }
        }
        listCoalEntity.setAdvertisementEntityList(advertisementEntityList); //广告
        listCoalEntity.setCoal070004(coal070004);  //精品煤炭
        listCoalEntity.setBrowseDataList(browseDataList); // 煤炭信息记录
        listCoalEntity.setCoal070001(coal070001);  // 搜索煤炭列表
        listCoalEntity.setCoal070003(coal070003);  // 信息部列表模式
        listCoalEntity.setCoal010001(coal010001);  // 矿口列表模式
        listCoalEntity.setCoalList(coalList);      //在线买煤煤炭列表
        listCoalEntity.setCount(getCount());
        return listCoalEntity;
    }

    private void addData(String dataTape, Map<String, Object> objectMap) {
        if ("operate060003".equals(dataTape)){
            Type type = new TypeToken<AdvertisementEntity>() {}.getType();
            AdvertisementEntity advertisementEntity = (AdvertisementEntity)getData(type,objectMap);
            advertisementEntityList.add(advertisementEntity);
        }else if ("coal070004".equals(dataTape)){
            Type type = new TypeToken<Coal>() {}.getType();
            Coal coalEntity = (Coal)getData(type,objectMap);
            coal070004.add(coalEntity);
        }else if ("coal070005".equals(dataTape)){
            Type type = new TypeToken<Coal>() {}.getType();
            Coal coalEntity = (Coal)getData(type,objectMap);
            ListCoalEntity listCoalEntity= new ListCoalEntity();
            listCoalEntity.setDataTape(dataTape);
            listCoalEntity.setCoal(coalEntity);
            browseDataList.add(listCoalEntity);
        }else if ("coal070006".equals(dataTape)){
            Type type = new TypeToken<Coal>() {}.getType();
            Coal coalEntity = (Coal)getData(type,objectMap);
            ListCoalEntity listCoalEntity= new ListCoalEntity();
            listCoalEntity.setDataTape(dataTape);
            listCoalEntity.setCoal(coalEntity);
            browseDataList.add(listCoalEntity);
        }else if ("coal070007".equals(dataTape)){
            Type type = new TypeToken<Coal>() {}.getType();
            Coal coalEntity = (Coal)getData(type,objectMap);
            ListCoalEntity listCoalEntity= new ListCoalEntity();
            listCoalEntity.setDataTape(dataTape);
            listCoalEntity.setCoal(coalEntity);
            browseDataList.add(listCoalEntity);
        }else if ("coal070001".equals(dataTape)){
            ListCoalEntity listCoalEntity= new ListCoalEntity();
            Type type = new TypeToken<Coal>() {}.getType();
            Coal coal= (Coal)getData(type,objectMap);
            listCoalEntity.setCoal(coal);
            coalList.add(listCoalEntity);
        }else if ("coal070003".equals(dataTape)){
            Type type = new TypeToken<ListCoalEntity>() {}.getType();
            ListCoalEntity listCoalEntity= (ListCoalEntity)getData(type,objectMap);
            List<Coal> coal070001 = new ArrayList<>();
            if (null != objectMap.get("list")){
                List<Map<String, Object>> mapList = (List<Map<String, Object>>)objectMap.get("list");
                for (int i=0;i < mapList.size();i ++){
                    Type typeCoal = new TypeToken<Coal>() {}.getType();
                    Coal coalEntity = (Coal)getData(typeCoal,mapList.get(i));
                    coal070001.add(coalEntity);
                }
            }
            listCoalEntity.setCoal070001(coal070001);
            coal070003.add(listCoalEntity);
        }else if ("coal010001".equals(dataTape)){
            Type type = new TypeToken<ListCoalEntity>() {}.getType();
            ListCoalEntity listCoalEntity= (ListCoalEntity)getData(type,objectMap);
            List<Coal> coal070001 = new ArrayList<>();
            if (null != objectMap.get("list")){
                List<Map<String, Object>> mapList = (List<Map<String, Object>>)objectMap.get("list");
                for (int i=0;i < mapList.size();i ++){
                    Type typeCoal = new TypeToken<Coal>() {}.getType();
                    Coal coalEntity = (Coal)getData(typeCoal,mapList.get(i));
                    coal070001.add(coalEntity);
                }
            }
            listCoalEntity.setCoal070001(coal070001);
            coal010001.add(listCoalEntity);
        }
    }

    private Object getData(Type type,Map<String, Object> objectMap){
        Object t = null;
        if (objectMap.size() != 0) {
            if (objectMap.size() != 0) {
                Gson gson = new Gson();
                String jsonString = gson.toJson(objectMap);
                t = gson.fromJson(jsonString,type);
            }
        }
        return t;
    }

    public int getItemPos() {
        return itemPos;
    }

    public void setItemPos(int itemPos) {
        this.itemPos = itemPos;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getSalesTotal() {
        return salesTotal;
    }

    public String getCompaniesNum() {
        return companiesNum;
    }

    public void setCompaniesNum(String companiesNum) {
        this.companiesNum = companiesNum;
    }

    public void setSalesTotal(String salesTotal) {
        this.salesTotal = salesTotal;
    }

    public String getGoodsTotal() {
        return goodsTotal;
    }

    public void setGoodsTotal(String goodsTotal) {
        this.goodsTotal = goodsTotal;
    }

    public String getTransportNum() {
        return transportNum;
    }

    public void setTransportNum(String transportNum) {
        this.transportNum = transportNum;
    }

    public String getCoalGoodsNum() {
        return coalGoodsNum;
    }

    public void setCoalGoodsNum(String coalGoodsNum) {
        this.coalGoodsNum = coalGoodsNum;
    }

    public Coal getCoal() {
        return coal;
    }

    public void setCoal(Coal coal) {
        this.coal = coal;
    }

    public String getDataTape() {
        return dataTape;
    }

    public void setDataTape(String dataTape) {
        this.dataTape = dataTape;
    }

    public List<ListCoalEntity> getBrowseDataList() {
        return browseDataList;
    }

    public void setBrowseDataList(List<ListCoalEntity> browseDataList) {
        this.browseDataList = browseDataList;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getCoalSalesId() {
        return coalSalesId;
    }

    public void setCoalSalesId(String coalSalesId) {
        this.coalSalesId = coalSalesId;
    }

    public String getMineMouthName() {
        return mineMouthName;
    }

    public void setMineMouthName(String mineMouthName) {
        this.mineMouthName = mineMouthName;
    }

    public List<ListCoalEntity> getCoalList() {
        return coalList;
    }

    public void setCoalList(List<ListCoalEntity> coalList) {
        this.coalList = coalList;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getMineMouthId() {
        return mineMouthId;
    }

    public void setMineMouthId(String mineMouthId) {
        this.mineMouthId = mineMouthId;
    }

    public List<ListCoalEntity> getCoal010001() {
        return coal010001;
    }

    public void setCoal010001(List<ListCoalEntity> coal010001) {
        this.coal010001 = coal010001;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public List<ListCoalEntity> getCoal070003() {
        return coal070003;
    }

    public void setCoal070003(List<ListCoalEntity> coal070003) {
        this.coal070003 = coal070003;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<AdvertisementEntity> getAdvertisementEntityList() {
        return advertisementEntityList;
    }

    public void setAdvertisementEntityList(List<AdvertisementEntity> advertisementEntityList) {
        this.advertisementEntityList = advertisementEntityList;
    }

    public List<Coal> getCoal070004() {
        return coal070004;
    }

    public void setCoal070004(List<Coal> coal070004) {
        this.coal070004 = coal070004;
    }

    public List<Coal> getCoal070001() {
        return coal070001;
    }

    public void setCoal070001(List<Coal> coal070001) {
        this.coal070001 = coal070001;
    }
}
