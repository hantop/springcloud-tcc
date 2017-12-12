package com.tuandai.architecture.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tuandai.architecture.domain.TranOrder;
import com.tuandai.architecture.model.PatchTransModel;

@Service
public class OrderService {

	private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

	@Autowired
	private TccTransBaseService tccTransBaseService;

	// 冻结账户；
	public String transTryAccount(TranOrder tranOrder) {
		try {
			PatchTransModel patchTransModel = new PatchTransModel();
			patchTransModel.setTransId(String.valueOf(tranOrder.getTransId()));
			patchTransModel.setTransUrl("tcc-account/tcc/account");
			patchTransModel.setTransUrlParam("{'name':'Lily'}");
			String request = tccTransBaseService.tryTrans(patchTransModel);

			// 降级
			if (null == request) {
				tccTransBaseService.cancelTrans(tranOrder);
				return null;
			}
		} catch (Exception ex) {
			logger.error("transTryAccount: ", ex.getMessage());
			tccTransBaseService.cancelTrans(tranOrder);
			return null;
		}

		// TODO 返回结果业务处理
		return "";
	}

	// 添加积分；
	public String transTryPoint(TranOrder tranOrder) {
		try {
			PatchTransModel patchTransModel = new PatchTransModel();
			patchTransModel.setTransId(String.valueOf(tranOrder.getTransId()));
			patchTransModel.setTransUrl("tcc-point/tcc/point");
			patchTransModel.setTransUrlParam("{'name':'Lily'}");
			String request = tccTransBaseService.tryTrans(patchTransModel);

			// 降级
			if (null == request) {
				tccTransBaseService.cancelTrans(tranOrder);
				return null;
			}
		} catch (Exception ex) {
			logger.error("transTryPoint: ", ex.getMessage());
			tccTransBaseService.cancelTrans(tranOrder);
			return null;
		}

		// TODO 返回结果业务处理
		return "";
	}

}
