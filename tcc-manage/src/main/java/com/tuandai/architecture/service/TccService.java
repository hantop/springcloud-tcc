package com.tuandai.architecture.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.tuandai.architecture.domain.Trans;
import com.tuandai.architecture.domain.TransLogs;
import com.tuandai.architecture.domain.TransUrls;

public interface TccService {
	Long createTrans(String serviceName);
	
	ResponseEntity<Object> patchTrans(Long transId,String transUrl,String transUrlParam);

	void ccTrans(Long transId,int state);
	
	void confrimTrans(Long transId);
	
	void confrimMark(Long transId);
	
	void cancelTrans(Long transId);
	
	void cancelMark(Long transId);
	
	void forceCancelTrans(Long transId);
	
	Trans getTrans(Long transId);
	
	List<TransUrls> getTransUrl(Long transId);
	
	List<TransLogs> getTransLog(Long transId);
	
}
