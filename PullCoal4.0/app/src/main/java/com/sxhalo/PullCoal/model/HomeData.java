package com.sxhalo.PullCoal.model;

import com.sxhalo.PullCoal.ui.pullrecyclerview.BaseModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by amoldZhang on 2017/8/1.
 */
public class HomeData extends BaseModule{

    private String page;
    private String title;
    private String templateId;
    private List<HomeData> homeDataList = new ArrayList<HomeData>();

    //轮播图
    private List<Slide> slideList = new ArrayList<Slide>();
    //功能列表
    private List<Function> functionList = new ArrayList<Function>();
    //新闻推荐
    private List<Article> informationsList = new ArrayList<Article>();
    //煤炭推荐
    private List<Coal> coalList = new ArrayList<Coal>();
    //信息部推荐  index == 0
    private List<InformationDepartment> infoDepartsList = new ArrayList<InformationDepartment>();
    //明星司机推荐  index == 1
    private List<UserEntity> driverList = new ArrayList<UserEntity>();



    public HomeData(){
    }

    /**
     * 得到联网反回的数据并处理
     * @param mapList
     * @return
     */
    public HomeData getHomeData(List<APPData<Map<String, Object>>> mapList) {
        HomeData homeData = new HomeData();
        for (int i = 0;i< mapList.size();i++){
            HomeData homeData1 = new HomeData();
            APPData<Map<String, Object>> appDataList = mapList.get(i);
            String mapTitle = appDataList.getTitle();
            homeData1.setTitle(mapTitle);
            homeData1.setTemplateId(appDataList.getDataType());
            for (int k = 0; k < appDataList.getList().size(); k++) {
                Map<String, Object> objectMap = appDataList.getList().get(k);
                if (objectMap.size() != 0) {
                    addData(homeData1, objectMap);
                }
            }
            homeDataList.add(homeData1);
        }
        homeData.setHomeDataList(homeDataList);
        return homeData;
    }


    private HomeData addData(HomeData homeData,Map<String, Object> objectMap) {
        if (homeData.getTitle().equals("轮播图")){
            Slide slide = new Slide().getSlide(objectMap);
            slideList.add(slide);
            homeData.setSlideList(slideList);
        }else if (homeData.getTitle().equals("功能列表")){
            Function function = new Function().getFunction(objectMap);
            functionList.add(function);
            homeData.setFunctionList(functionList);
        }else if (homeData.getTitle().equals("头条资讯")){
            Article article = new Article().getArticle(objectMap);
            informationsList.add(article);
            homeData.setInformationsList(informationsList);
        }else if (homeData.getTitle().contains("精品煤炭")){
            Coal coals = new Coal().getCoal(objectMap);
            coalList.add(coals);
            homeData.setCoalList(coalList);
        }else if (homeData.getTitle().equals("推荐的信息部")){
            InformationDepartment informationDepartment = new InformationDepartment().getInformationDepartment(objectMap);
            infoDepartsList.add(informationDepartment);
            homeData.setInfoDepartsList(infoDepartsList);
        }else if (homeData.getTitle().equals("明星司机")){
            UserEntity driver = new UserEntity().getUserEntity(objectMap);
            driverList.add(driver);
            homeData.setDriverList(driverList);
        }
        return homeData;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<HomeData> getHomeDataList() {
        return homeDataList;
    }

    public void setHomeDataList(List<HomeData> homeDataList) {
        this.homeDataList = homeDataList;
    }

    public List<Coal> getCoalList() {
        return coalList;
    }

    public void setCoalList(List<Coal> coalList) {
        this.coalList = coalList;
    }

    public List<Slide> getSlideList() {
        return slideList;
    }

    public void setSlideList(List<Slide> slideList) {
        this.slideList = slideList;
    }

    public List<Function> getFunctionList() {
        return functionList;
    }

    public void setFunctionList(List<Function> functionList) {
        this.functionList = functionList;
    }

    public List<Article> getInformationsList() {
        return informationsList;
    }

    public void setInformationsList(List<Article> informationsList) {
        this.informationsList = informationsList;
    }

    public HomeData getInfoDepartsList(List<InformationDepartment> infoDepartsList) {
        HomeData homeData = new HomeData();
        homeData.setInfoDepartsList(infoDepartsList);
        return homeData;
    }

    public List<InformationDepartment> getInfoDepartsList() {
        return infoDepartsList;
    }

    public void setInfoDepartsList(List<InformationDepartment> infoDepartsList) {
        this.infoDepartsList = infoDepartsList;
    }

    public HomeData getDriverList(List<UserEntity> userEntities) {
        HomeData homeData = new HomeData();
        homeData.setDriverList(userEntities);
        return homeData;
    }

    public List<UserEntity> getDriverList() {
        return driverList;
    }

    public void setDriverList(List<UserEntity> driverList) {
        this.driverList = driverList;
    }
}
