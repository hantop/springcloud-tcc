package com.tuandai.architecture.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public final class TransUrls {
	private Long id;
	
	private Long transId;

	private String transUrl;

	private String transUrlParam;

	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	
	private Integer code;
		
	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTransId() {
		return transId;
	}

	public void setTransId(Long transId) {
		this.transId = transId;
	}

	public String getTransUrl() {
		return transUrl;
	}

	public void setTransUrl(String transUrl) {
		this.transUrl = transUrl;
	}

	public String getTransUrlParam() {
		return transUrlParam;
	}

	public void setTransUrlParam(String transUrlParam) {
		this.transUrlParam = transUrlParam;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}