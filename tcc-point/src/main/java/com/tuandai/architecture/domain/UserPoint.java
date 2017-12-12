package com.tuandai.architecture.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public final class UserPoint  {
	private Integer id;

	private String name;
	
	private Integer point;
	
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getpoint() {
		return point;
	}

	public void setpoint(Integer point) {
		this.point = point;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	

}