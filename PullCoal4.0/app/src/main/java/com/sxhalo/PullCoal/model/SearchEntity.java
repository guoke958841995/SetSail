package com.sxhalo.PullCoal.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 搜索结果对象
 */

public class SearchEntity implements Serializable {

    private List<Discover> mineMouthList = new ArrayList<Discover>();
    private List<Coal> coalProductList = new ArrayList<Coal>();
    private List<Discover> infoDepartmentList = new ArrayList<Discover>();
    private List<UserEntity> driverList = new ArrayList<UserEntity>();
    private List<TransportMode> transportModeList = new ArrayList<TransportMode>();
    private List<UserDemand> userDemandEntityList = new ArrayList<UserDemand>();
    private String success;
    private String title;

    public SearchEntity(){
    }

    public SearchEntity getSearchEntity(List<APPData<Map<String, Object>>> data) {
        Gson gson =  new Gson();
        SearchEntity searchEntity = new SearchEntity();
        for (APPData<Map<String, Object>> listMap : data){
            if (listMap.getDataType().equals("coal070001")){
                String coalJSON =  gson.toJson(listMap.getList()).replace("\\", "");
                Type type = new TypeToken<List<Coal>>(){}.getType();
                List<Coal> coals = gson.fromJson(coalJSON,type);
                searchEntity.setCoalProduct(coals);
            }else if (listMap.getDataType().equals("coal010002")){
                String mineJSON =  gson.toJson(listMap.getList()).replace("\\", "");
                Type type = new TypeToken<List<Discover>>(){}.getType();
                List<Discover> mines = gson.fromJson(mineJSON,type);
                searchEntity.setMineMouthList(mines);
            }else if (listMap.getDataType().equals("coal020005")){
                String informationDepartmentJSON =  gson.toJson(listMap.getList()).replace("\\", "");
                Type type = new TypeToken<List<Discover>>(){}.getType();
                List<Discover> informationDepartmentS = gson.fromJson(informationDepartmentJSON,type);
                searchEntity.setInfoDepartmentList(informationDepartmentS);
            }else if (listMap.getDataType().equals("coal090001")){
                String informationDepartmentJSON =  gson.toJson(listMap.getList()).replace("\\", "");
                Type type = new TypeToken<List<TransportMode>>(){}.getType();
                List<TransportMode> informationDepartmentS = gson.fromJson(informationDepartmentJSON,type);
                searchEntity.setTransportModeList(informationDepartmentS);
            }else if (listMap.getDataType().equals("user040002")){
                String informationDepartmentJSON =  gson.toJson(listMap.getList()).replace("\\", "");
                Type type = new TypeToken<List<UserEntity>>(){}.getType();
                List<UserEntity> informationDepartmentS = gson.fromJson(informationDepartmentJSON,type);
                searchEntity.setDriverList(informationDepartmentS);
            }else if (listMap.getDataType().equals("coal130001")){
                String userDemandJSON =  gson.toJson(listMap.getList()).replace("\\", "");
                Type type = new TypeToken<List<UserDemand>>(){}.getType();
                List<UserDemand> userDemands = gson.fromJson(userDemandJSON,type);
                searchEntity.setUserDemandEntityList(userDemands);
            }
        }
        return searchEntity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<TransportMode> getTransportModeList() {
        return transportModeList;
    }

    public void setTransportModeList(List<TransportMode> transportModeList) {
        this.transportModeList = transportModeList;
    }

    public List<UserEntity> getDriverList() {
        return driverList;
    }

    public void setDriverList(List<UserEntity> driverList) {
        this.driverList = driverList;
    }

    public List<Discover> getMineMouthList() {
        return mineMouthList;
    }

    public void setMineMouthList(List<Discover> mineMouthList) {
        this.mineMouthList = mineMouthList;
    }

    public List<Coal> getCoalProductList() {
        return coalProductList;
    }

    public void setCoalProduct(List<Coal> coalProductList) {
        this.coalProductList = coalProductList;
    }

    public void setCoalProductList(List<ListCoalEntity> listCoalEntities) {
        List<Coal> coalList = new ArrayList<>();
        if (listCoalEntities != null && listCoalEntities.size() != 0){
            for (ListCoalEntity listCoalEntity : listCoalEntities){
                coalList.add(listCoalEntity.getCoal());
            }
        }
        this.coalProductList = coalList;
    }

    public List<Discover> getInfoDepartmentList() {
        return infoDepartmentList;
    }

    public void setInfoDepartmentList(List<Discover> infoDepartmentList) {
        this.infoDepartmentList = infoDepartmentList;
    }

    public List<UserDemand> getUserDemandEntityList() {
        return userDemandEntityList;
    }

    public void setUserDemandEntityList(List<UserDemand> userDemandEntityList) {
        this.userDemandEntityList = userDemandEntityList;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

}
