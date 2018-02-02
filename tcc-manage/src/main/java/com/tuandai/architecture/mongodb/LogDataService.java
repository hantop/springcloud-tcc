package com.tuandai.architecture.mongodb;

import java.util.List;

import com.tuandai.architecture.domain.LogData;
import com.tuandai.architecture.util.CheckPointUtil.CheckPoint;
import com.tuandai.transaction.constant.TCCState;

public interface LogDataService {
	public TCCState analysis(LogData logData);

	public List<LogData> checkData(CheckPoint startCheckPoint, CheckPoint endCheckPoint);
}
