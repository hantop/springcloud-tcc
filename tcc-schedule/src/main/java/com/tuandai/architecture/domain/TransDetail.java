package com.tuandai.architecture.domain;

import java.util.List;

public final class TransDetail {
	
	private Trans trans;
	
	private List<TransUrls> urls;
	

	public Trans getTrans() {
		return trans;
	}

	public void setTrans(Trans trans) {
		this.trans = trans;
	}

	public List<TransUrls> getUrls() {
		return urls;
	}

	public void setUrls(List<TransUrls> urls) {
		this.urls = urls;
	}
	
}