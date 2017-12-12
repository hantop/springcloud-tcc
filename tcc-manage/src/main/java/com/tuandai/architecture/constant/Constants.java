package com.tuandai.architecture.constant;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.google.common.collect.ImmutableList;

public class Constants {
	public static final String CHECK_URL = "/tcc/check";

	public static final int RESTFUL_MAX_TIMEOUT_SECONDS = 2;

	public static final int MAX_THRESHOLD = 5;

	public static final String TCC_MANAGE_NAME = "tcc-manage";

	public static final String TCC_SCHEDULE_NAME = "tcc-schedule";

	public static HttpHeaders header = new HttpHeaders();

	static {
		header.setAccept(ImmutableList.of(MediaType.APPLICATION_JSON_UTF8));
		header.setContentType(MediaType.APPLICATION_JSON_UTF8);
	}
	

}
