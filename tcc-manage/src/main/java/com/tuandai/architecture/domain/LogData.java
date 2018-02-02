package com.tuandai.architecture.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;

public class LogData {
	@Id
	private String id;

	private Date time;

	private String type;

	private String serviceName;

	// --------------- 业务字段----------
	private String uid;

	private String message;

	private Integer state;

	private String checkUrl;
	private String resUrls;

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getCheckUrl() {
		return checkUrl;
	}

	public void setCheckUrl(String checkUrl) {
		this.checkUrl = checkUrl;
	}

	public String getResUrls() {
		return resUrls;
	}

	public void setResUrls(String resUrls) {
		this.resUrls = resUrls;
	}

}
