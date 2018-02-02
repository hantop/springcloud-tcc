package com.tuandai.architecture.service;

import com.tuandai.architecture.domain.Trans;

public interface TccService {

	public Boolean insertTrans(Trans trans);

	public boolean insertTransRetry(Trans trans);

	public boolean updateTrans(Trans trans);

	public boolean updateTransRetry(Trans trans);

	public void forceCancelTrans(String transId);

	public Trans getTrans(String transId);

}
