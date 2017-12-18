package com.tuandai.architecture.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.tuandai.architecture.domain.Trans;
import com.tuandai.architecture.domain.TransUrls;

public interface TccService {
	Long createTrans(String serviceName);
	
	ResponseEntity<Object> patchTrans(Long transId,String transUrl,String transUrlParam);
	
	Boolean ccToRetry(Long transId,int state);
	
	Boolean ccOver(Long transId);
	
	Boolean ccOverList(List<Long> transId);
	
	void confrimTrans(Long transId);
	
	void cancelMark(Long transId);
	
	void forceCancelTrans(Long transId);
	
	Trans getTrans(Long transId);
	
	List<TransUrls> getTransUrl(Long transId);
	
}
