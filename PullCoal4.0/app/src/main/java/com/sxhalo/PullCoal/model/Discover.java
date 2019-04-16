package com.sxhalo.PullCoal.model;


import java.io.Serializable;

public class Discover implements Serializable {


	/**
	 * itemId : 10000058
	 * distance : 0
	 * address : 神木县城北35KM处
	 * itemName : 海湾煤矿
	 * longitude : 110.35593062639236
	 * latitude : 39.0543262213148
	 * itemType : 1
	 */

	private String itemId;
	private String distance;
	private String address;
	private String itemName;
	private String longitude;
	private String latitude;
	private String itemType;

	private String creditRating;

	public String getCreditRating() {
		return creditRating;
	}

	public void setCreditRating(String creditRating) {
		this.creditRating = creditRating;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
}
