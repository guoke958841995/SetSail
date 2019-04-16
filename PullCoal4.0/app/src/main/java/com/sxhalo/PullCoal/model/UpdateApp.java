package com.sxhalo.PullCoal.model;

public class UpdateApp {

	/**
	 * appPath :
	 * appName : lmb
	 * verrifyState : 1
	 * appType : ios
	 * version : 3.3
	 */

	private String appPath;
	private String appName;
	private String verrifyState;
	private String appType;
	private String version;
	private String remark;

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getAppPath() {
		return appPath;
	}

	public void setAppPath(String appPath) {
		this.appPath = appPath;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getVerrifyState() {
		return verrifyState;
	}

	public void setVerrifyState(String verrifyState) {
		this.verrifyState = verrifyState;
	}

	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
