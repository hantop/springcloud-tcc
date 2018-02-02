package com.tuandai.architecture.domain.filter;

import java.util.Date;

public class LogDataFilter {
	private Date start;
	public final static String TIME = "time";
	private Date end;
	private int size;
	public final static String SIZE = "size";

	public Date start() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date end() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public int size() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
}
