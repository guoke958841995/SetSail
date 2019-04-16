package com.sxhalo.PullCoal.db.entity;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;

/**
 * Created by amoldZhang on 2018/8/15.
 */
@Table("user_contrast")
public class UserContrast implements Serializable{

    @PrimaryKey(AssignType.BY_MYSELF ) //主键自己设定的
    @Column(value = "goods_id")
    private String goodsId;     //煤id
    @Column(value = "coal_name")
    private String coalName;   // 煤名称


    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getCoalName() {
        return coalName;
    }

    public void setCoalName(String coalName) {
        this.coalName = coalName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserContrast){
            UserContrast userContrast = (UserContrast)obj;
            return this.goodsId.equals(userContrast.goodsId)&&
                    this.coalName.equals(userContrast.coalName);
        }
        return super.equals(obj);
    }
}
