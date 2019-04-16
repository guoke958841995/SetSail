package com.sxhalo.PullCoal.model;

import java.io.Serializable;
import java.util.Date;


public class ToolData implements Serializable{


	public Integer getActivityId() {
		return activityId;
	}

	public void setActivityId(Integer activityId) {
		this.activityId = activityId;
	}

	public String getActiveName() {
		return activeName;
	}

	public void setActiveName(String activeName) {
		this.activeName = activeName;
	}

	private Integer activityId;  //工具Id
	private String activeName;  //工具名称
}