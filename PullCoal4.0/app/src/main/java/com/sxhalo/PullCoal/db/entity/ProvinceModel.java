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
@Table("province_model")
public class ProvinceModel implements Serializable {
	@PrimaryKey(AssignType.BY_MYSELF)
	@Column(value = "province_id")
	private String provinceId;
	@Column(value = "province_name")
	private String provinceName;
	@Mapping(Relation.OneToMany)   //一个省有多个城市
	@MapCollection(CityModel.class)
	private List<CityModel> citys;
	
	public ProvinceModel() {
		super();
	}

	public ProvinceModel(String provinceId,String provinceName, List<CityModel> citys) {
		super();
		this.provinceId = provinceId;
		this.provinceName = provinceName;
		this.citys = citys;
	}

	public String getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public List<CityModel> getCitys() {
		return citys;
	}

	public void setCitys(List<CityModel> citys) {
		this.citys = citys;
	}

	@Override
	public String toString() {
		return "ProvinceModel{" +
				"provinceId='" + provinceId + '\'' +
				", provinceName='" + provinceName + '\'' +
				", citys=" + citys +
				'}';
	}
}
