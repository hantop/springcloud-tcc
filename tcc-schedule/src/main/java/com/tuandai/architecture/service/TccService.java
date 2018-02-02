package com.tuandai.architecture.service;

import java.util.List;

import com.tuandai.architecture.domain.Trans;
import com.tuandai.architecture.domain.TransUrls;

public interface TccService {
	Boolean ccTrans(Trans trans);
	
	void decreaseCCThreshold(Trans trans);

	Boolean ccToRetry(String transId,int state);
	
	Boolean ccOver(String transId);
	
	Boolean ccOverList(List<String> transId);
		
	void confrimMark(String transId);
		
	void cancelMark(String transId);
	
	void forceCancelTrans(String transId);
	
	Trans getTrans(String transId);
	
	List<TransUrls> getTransUrl(String transId);
	
}
