package com.sxhalo.PullCoal.bean;


import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 数据字典
 */
@Table("tb_dictionary_type")
public class Dictionary implements Serializable {

    @PrimaryKey(AssignType.BY_MYSELF)
    @Column(value = "dictionary_data_type")
    public String dataType;
    @Column(value = "dictionary_title")
    public String title;
    @Mapping(Relation.OneToMany)
    public ArrayList<FilterEntity> list;

    //默认true 则全部显示  false 则只显示三条
    private boolean nameIsChecked = true;

    public boolean isNameIsChecked() {
        return nameIsChecked;
    }

    public void setNameIsChecked(boolean nameIsChecked) {
        this.nameIsChecked = nameIsChecked;
    }


    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<FilterEntity> getList() {
        return list;
    }

    public void setList(ArrayList<FilterEntity> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "Dictionary{" +
                "dataType='" + dataType + '\'' +
                ", title='" + title + '\'' +
                ", list=" + list +
                '}';
    }
}
