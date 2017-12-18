package com.tuandai.architecture.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableList;
import com.tuandai.architecture.constant.TransState;
import com.tuandai.architecture.controller.client.TccClientRest;
import com.tuandai.architecture.domain.TranOrder;
import com.tuandai.architecture.model.PatchTransModel;
import com.tuandai.architecture.model.PostTransModel;
import com.tuandai.architecture.repository.TranOrderRepository;

@Service
public class TccTransBaseService {
	private static final Logger logger = LoggerFactory.getLogger(TccTransBaseService.class);

	@Autowired
	private TranOrderRepository tranOrderRepository;

//	@Autowired
//	TccClient tccClient;
	
	@Autowired
	TccClientRest tccClient;

	@Value("${spring.application.name}")
	String serviceName;

	static final String urlTccCreate ="http://tcc-manage/create";
	static final String urlTry ="http://tcc-manage/try";
	static final String urlConfirm ="http://tcc-manage/confirm";
	static final String urlCancel ="http://tcc-manage/cancel";

	static HttpHeaders header = new HttpHeaders();

	static {
		header.setAccept(ImmutableList.of(MediaType.APPLICATION_JSON_UTF8));
		header.setContentType(MediaType.APPLICATION_JSON_UTF8);
	}
	
	// 新建事务
	public TranOrder createTrans() {
		String transId = null;
		PostTransModel postTransModel = new PostTransModel();
		postTransModel.setServiceName(this.serviceName);
		JSONObject joRequest = JSONObject.parseObject(tccClient.create(postTransModel));
		transId = joRequest.getString("data");
				
		if(null == transId || Long.valueOf(transId) < 1){
			return null;
		}
		
		logger.info("[create] > transId:{}",transId);

		// 持久化事务状态
		TranOrder tranOrder = new TranOrder();
		tranOrder.setTransId(Long.valueOf(transId));
		tranOrder.setState(TransState.PENDING.code());
		tranOrder.setCreateTime(new Date());
		tranOrderRepository.insert(tranOrder);

		logger.info("[insert] > transId:{}, state:{}",tranOrder.getTransId(),tranOrder.getState());
		return tranOrder;
	}

	//预处理资源
	public String tryTrans(PatchTransModel patchTransModel) {
		return  tccClient.tryTrans(patchTransModel);
	}
	
	// 取消事务
	public void cancelTrans(TranOrder tranOrder) {
		tranOrder.setState(TransState.CANCEL.code());
		tranOrderRepository.updateState(tranOrder);
		tccClient.cancelTrans(String.valueOf(tranOrder.getTransId()));

	}
	
	// 提交事务
	public void confirmTrans(TranOrder tranOrder) {
		tranOrder.setState(TransState.CONFIRM.code());
		tranOrderRepository.updateState(tranOrder);
		tccClient.confirmTrans(String.valueOf(tranOrder.getTransId()));
	}
	
	// 检查事务
	public TranOrder check(String transId) {
		TranOrder to = tranOrderRepository.getByTranId(transId);
		return to;
	}
	
}
