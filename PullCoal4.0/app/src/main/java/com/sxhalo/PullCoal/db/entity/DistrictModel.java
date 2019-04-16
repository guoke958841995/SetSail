package com.sxhalo.PullCoal.db.entity;


import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;

@Table("district_model")
public class DistrictModel implements Serializable {

	@Column(value = "area_name")
	private String areaName;
	@PrimaryKey(AssignType.BY_MYSELF)
	@Column(value = "area_id")
	private Integer areaId;
	
	public DistrictModel() {
		super();
	}

	public DistrictModel(String areaName, Integer areaId) {
		this.areaName = areaName;
		this.areaId = areaId;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public Integer getAreaId() {
		return areaId;
	}

	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}

	@Override
	public String toString() {
		return "DistrictModel{" +
				"areaName='" + areaName + '\'' +
				", areaId='" + areaId + '\'' +
				'}';
	}
}
