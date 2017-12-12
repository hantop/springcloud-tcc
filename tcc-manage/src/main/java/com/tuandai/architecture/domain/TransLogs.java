package com.tuandai.architecture.domain;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;

public final class TransLogs {
	
	private Long id;

	private Long transId;

	private Integer transState;

	private String transUrl;

	private String log;

	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

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

	public Integer getTransState() {
		return transState;
	}

	public void setTransState(Integer transState) {
		this.transState = transState;
	}

	public String getTransUrl() {
		return transUrl;
	}

	public void setTransUrl(String transUrl) {
		this.transUrl = transUrl;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}


	// date format
	private static SerializeConfig mapping = new SerializeConfig();    
	private static String dateFormat;    
	static {    
	    dateFormat = "yyyy-MM-dd HH:mm:ss";    
	    mapping.put(Date.class, new SimpleDateFormatSerializer(dateFormat));    
	}  
	
	@Override
	public String toString() {
		return JSONObject.toJSONString(this,mapping);
	}

}