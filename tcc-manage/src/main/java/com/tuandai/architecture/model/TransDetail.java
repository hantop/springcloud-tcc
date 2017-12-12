package com.tuandai.architecture.model;

import java.util.List;

import com.tuandai.architecture.domain.Trans;
import com.tuandai.architecture.domain.TransLogs;
import com.tuandai.architecture.domain.TransUrls;

public final class TransDetail {
	
	private Trans trans;
	
	private List<TransUrls> urls;
	
	private List<TransLogs> logs;

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

	public List<TransLogs> getLogs() {
		return logs;
	}

	public void setLogs(List<TransLogs> logs) {
		this.logs = logs;
	}
	
}