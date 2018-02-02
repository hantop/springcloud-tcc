package com.tuandai.flume.sink.mysql.transaction;

import java.util.Date;

public final class Trans {
	private String transId;
	
	private String serviceName;

	private String checkUrl;

	private Integer transState;

	private Integer checkTimes;

	private Date checkTime;

	private Integer checkThreshold;

	private Integer ccTimes;

	private Date ccTime;

	private Integer ccThreshold;

	private Date updateTime;

	private Date createTime;

	
	private String resUrls;

	public String getResUrls() {
		return resUrls;
	}

	public void setResUrls(String resUrls) {
		this.resUrls = resUrls;
	}
	
	public String getTransId() {
		return transId;
	}

	public void setTransId(String transId) {
		this.transId = transId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getCheckUrl() {
		return checkUrl;
	}

	public void setCheckUrl(String checkUrl) {
		this.checkUrl = checkUrl;
	}

	public Integer getTransState() {
		return transState;
	}

	public void setTransState(Integer transState) {
		this.transState = transState;
	}

	public Integer getCheckTimes() {
		return checkTimes;
	}

	public void setCheckTimes(Integer checkTimes) {
		this.checkTimes = checkTimes;
	}

	public Date getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(Date checkTime) {
		this.checkTime = checkTime;
	}

	public Integer getCheckThreshold() {
		return checkThreshold;
	}

	public void setCheckThreshold(Integer checkThreshold) {
		this.checkThreshold = checkThreshold;
	}

	public Integer getCcTimes() {
		return ccTimes;
	}

	public void setCcTimes(Integer ccTimes) {
		this.ccTimes = ccTimes;
	}

	public Date getCcTime() {
		return ccTime;
	}

	public void setCcTime(Date ccTime) {
		this.ccTime = ccTime;
	}

	public Integer getCcThreshold() {
		return ccThreshold;
	}

	public void setCcThreshold(Integer ccThreshold) {
		this.ccThreshold = ccThreshold;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}