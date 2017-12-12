package com.tuandai.architecture.model;

public class TryAccountModel {
	private String name;
	private Integer transId;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getTransId() {
		return transId;
	}
	public void setTransId(Integer transId) {
		this.transId = transId;
	}
	
	@Override
	public String toString() {
		return this.name + " : " + this.transId;
	}
	
}
