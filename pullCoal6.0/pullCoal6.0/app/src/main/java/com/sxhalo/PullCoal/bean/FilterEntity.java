package com.sxhalo.PullCoal.bean;


import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;

@Table("tb_filter_type")
public class FilterEntity implements Serializable {

    @PrimaryKey(AssignType.AUTO_INCREMENT)  //主键自增长   AssignType.BY_MYSELF //自己设置主键
    @Column(value = "type_id")  //指定列名
    public int id;

    @Column(value = "dictionary_dict_value")
    public String dictValue;

    @Column(value = "dictionary_dict_code")
    public String dictCode;

//    public ArrayList<FilterEntity> list;

    private boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getDictValue() {
        return dictValue;
    }

    public void setDictValue(String dictValue) {
        this.dictValue = dictValue;
    }

    public String getDictCode() {
        return dictCode;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    @Override
    public String toString() {
        return "FilterEntity{" +
                "dictValue='" + dictValue + '\'' +
                ", dictCode='" + dictCode + '\'' +
                '}';
    }
}
