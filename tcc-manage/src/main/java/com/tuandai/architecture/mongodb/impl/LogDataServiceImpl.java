package com.tuandai.architecture.mongodb.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tuandai.architecture.component.ThresholdsTimeManage;
import com.tuandai.architecture.constant.Constants;
import com.tuandai.architecture.dao.LogDataDao;
import com.tuandai.architecture.domain.LogData;
import com.tuandai.architecture.domain.TableType;
import com.tuandai.architecture.domain.Trans;
import com.tuandai.architecture.domain.filter.LogDataFilter;
import com.tuandai.architecture.mongodb.LogDataService;
import com.tuandai.architecture.service.TccService;
import com.tuandai.architecture.util.CheckPointUtil.CheckPoint;
import com.tuandai.transaction.constant.TCCState;

@Component
public class LogDataServiceImpl implements LogDataService {

	@Autowired
	private LogDataDao logDataDao;

	@Autowired
	private TccService tccService;

	@Autowired
	ThresholdsTimeManage thresholdsTimeManage;

	@Override
	public TCCState analysis(LogData logData) {
		String uid = logData.getUid();
		LogData endLog = logDataDao.findByUId(uid, TableType.TCC_END);
		TCCState state = null;
		if (endLog == null) {
			state = TCCState.UNKNOW;
		} else {
			if (endLog.getState() != null) {
				if (TCCState.COMMIT == TCCState.findByValue(endLog.getState())) {
					state = TCCState.COMMIT;
				} else if (TCCState.CANCEL == TCCState.findByValue(endLog.getState())) {
					state = TCCState.CANCEL;
				}
			}
		}
		update(logData, state);
		return state;
	}

	protected void update(LogData logData, TCCState state) {
		Date dt = new Date();
		Trans trans = new Trans();
		trans.setServiceName(logData.getServiceName());
		trans.setTransId(logData.getUid());
		trans.setCheckUrl(logData.getCheckUrl());
		trans.setResUrls(logData.getResUrls());

		trans.setCheckTimes(0);
		trans.setCheckThreshold(Constants.MAX_THRESHOLD);
		trans.setCheckTime(thresholdsTimeManage.createTccCheckTime(Constants.MAX_THRESHOLD));

		trans.setCcThreshold(Constants.MAX_THRESHOLD);
		trans.setCcTimes(0);
		trans.setCcTime(dt);

		trans.setCreateTime(dt);
		trans.setUpdateTime(dt);

		trans.setTransState(state.value());
		// 不同类型做不同的逻辑处理
		switch (state) {
		case COMMIT:
			tccService.updateTransRetry(trans);
			break;
		case UNKNOW:
			tccService.updateTrans(trans);
			break;
		case CANCEL:
			tccService.updateTransRetry(trans);
			break;
		default:
			break;
		}
	}

	public List<LogData> checkData(CheckPoint startCheckPoint, CheckPoint endCheckPoint) {
		// 加载当前时间前10秒到checkoutPoint之间的数据
		LogDataFilter filter = new LogDataFilter();
		filter.setStart(new Date(startCheckPoint.getTime()));
		filter.setEnd(new Date(endCheckPoint.getTime()));
		filter.setSize(CheckPoint.size);
		List<LogData> list = logDataDao.findByFilter(filter);
		return list;
	}
}
