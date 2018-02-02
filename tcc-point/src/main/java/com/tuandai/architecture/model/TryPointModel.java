package com.tuandai.architecture.model;

public class TryPointModel {
	private String name;
	private String transId;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	
	public String getTransId() {
		return transId;
	}
	public void setTransId(String transId) {
		this.transId = transId;
	}
	@Override
	public String toString() {
		return this.name + " : " + this.transId;
	}
	
}
