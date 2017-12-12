package com.tuandai.architecture.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public final class Trans {
	private Long transId;
	
	private String serviceName;

	private String checkUrl;

	// {[pending|cancel|confirm]}
	private Integer transState;

	private Integer checkTimes;

	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date checkTime;

	private Integer checkThreshold;

	private Integer ccTimes;

	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date ccTime;

	private Integer ccThreshold;

	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;

	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	public Long getTransId() {
		return transId;
	}

	public void setTransId(Long transId) {
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