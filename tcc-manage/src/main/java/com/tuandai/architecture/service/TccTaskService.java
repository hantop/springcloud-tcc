package com.tuandai.architecture.service;

import java.util.List;

import com.tuandai.architecture.domain.Trans;

public interface TccTaskService {

	List<Trans> getCCTrans(int state);
	
	List<Trans> getCheckTrans();

	int checkTran(Trans tran);

	List<Trans> checkTrans(List<Trans> trans);
}
