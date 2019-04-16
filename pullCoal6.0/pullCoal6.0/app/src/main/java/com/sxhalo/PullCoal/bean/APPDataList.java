package com.sxhalo.PullCoal.bean;

import java.util.List;

/**
 * Created by amoldZhang on 2017/7/27.
 */
public class APPDataList<T> {

//     "dataType": "coal070001",
//             "title": "分页数据",
//             "currentPage": 0,
//             "pageSize": 10,
//             "pageCount": 1,
//             "recordCount": 7,

   //不带分页数据获取
    private String dataType;
    private String title;

    //带分页数据获取
    private int currentPage;
    private int pageSize;
    private int pageCount;
    private int recordCount;

    private List<T> list;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
