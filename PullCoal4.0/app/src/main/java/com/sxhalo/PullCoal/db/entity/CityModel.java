package com.sxhalo.PullCoal.db.entity;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.MapCollection;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;

import java.io.Serializable;
import java.util.List;

@Table("city_model")
public class CityModel implements Serializable {
	@PrimaryKey(AssignType.BY_MYSELF)
	@Column(value = "city_id")
	private String cityId;
	@Column(value = "city_name")
	private String cityName;
	@Mapping(Relation.OneToMany)
	@MapCollection(DistrictModel.class)
	private List<DistrictModel> areas;
	
	public CityModel() {
		super();
	}

	public CityModel(String cityId, String cityName, List<DistrictModel> areas) {
		this.cityId = cityId;
		this.cityName = cityName;
		this.areas = areas;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public List<DistrictModel> getAreas() {
		return areas;
	}

	public void setAreas(List<DistrictModel> areas) {
		this.areas = areas;
	}


	@Override
	public String toString() {
		return "CityModel{" +
				"cityId='" + cityId + '\'' +
				", cityName='" + cityName + '\'' +
				", areas=" + areas +
				'}';
	}
}
