package com.sxhalo.PullCoal.db.entity;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;

/**
 * Created by amoldZhang on 2018/8/17.
 */
@Table("user_search_history")
public class UserSearchHistory implements Serializable{

    @PrimaryKey(AssignType.BY_MYSELF ) //主键自己设定的
    @Column(value = "search_text")
    private String searchText;     //搜索内容

    @Column(value = "search_type")
    private String searchType = "1";     //搜索类型 1 首页搜索 2采样化验搜索

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
}
