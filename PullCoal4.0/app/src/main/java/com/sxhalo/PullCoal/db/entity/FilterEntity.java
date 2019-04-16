package com.sxhalo.PullCoal.db.entity;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by liz on 2017/8/17.
 */

@Table("tb_filter_type")
public class FilterEntity implements Serializable{
    @PrimaryKey(AssignType.AUTO_INCREMENT)  //主键自增长   AssignType.BY_MYSELF //自己设置主键
    @Column(value = "type_id")  //指定列名
    public int id;
    @Column(value = "dictionary_dict_value")
    public String dictValue;
    @Column(value = "dictionary_dict_code")
    public String dictCode;

    public ArrayList<FilterEntity> list;
    private boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
