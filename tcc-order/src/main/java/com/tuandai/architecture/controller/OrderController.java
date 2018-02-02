package com.tuandai.architecture.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tuandai.architecture.controller.client.TccClientRest;
import com.tuandai.architecture.util.Result;
import com.tuandai.transaction.client.TCCClient;
import com.tuandai.transaction.constant.TCCState;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
public class OrderController {

	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	TccClientRest tccClientRest;

	// 创建事务
	private  String serviceName = "tcc-order";
	
	@ApiOperation(value = "发起事务订单", notes = "发起事务订单")
	@RequestMapping(value = "/tcc", method = RequestMethod.POST)
	public ResponseEntity<Result<String>> tcc(String name) {
		String uid = UUID.randomUUID().toString();
		/**
		 * 开始事务日志  
		 * @param id  业务唯一ID （ UUID） ,必选
		 * @param serviceName  业务应用名称,必选
		 * @param checkUrl 回调地址,必选
		 * @param resUrls  资源列表,必选
		 */
		String resUrls = TCCClient.RES_SEPARATOR_CHAR + TccClientRest.ACCOUNT_SERVICE + TCCClient.RES_SEPARATOR_CHAR + TccClientRest.POINT_SERVICE + TCCClient.RES_SEPARATOR_CHAR ;
		
		TCCClient.sendTransBeginToFlume(uid, serviceName, "tcc-order/check",resUrls);


		PatchTransModel patchTransModel = new PatchTransModel();
		patchTransModel.setName(name);
		patchTransModel.setTransId(uid);
		// 冻结 资金 ， 积分
		if (!(tccClientRest.tryAccount(patchTransModel) && tccClientRest.tryPoint(patchTransModel))) {
			// 取消事务;
			TCCClient.sendTransEndToFlume(TCCState.CANCEL, uid, serviceName);
		} else {
			/**
			 * 结束事务日志
			 * @param state 状态： 成功，失败，重试 ,必选
			 * @param id  业务唯一ID （ UUID） ,必选
			 * @param serviceName  业务应用名称,必选
			 * @return
			 */
			TCCClient.sendTransEndToFlume(TCCState.COMMIT, uid, serviceName);
		}


		return new ResponseEntity<Result<String>>(new Result<String>(""), HttpStatus.OK);
	}

	@ApiOperation(value = "检测事务订单", notes = "检测事务订单")
	@ApiImplicitParam(name = "transId", value = "事务ID", paramType = "transId", required = true, dataType = "String")
	@RequestMapping(value = "/check", method = RequestMethod.POST)
	public  HashMap<String, Object> tccCheck(@Valid @RequestBody String transId) {
		
    	logger.info("check: {}",transId);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("uid", transId);
		map.put("serviceName", serviceName);
		map.put("state", TCCState.UNKNOW);
		
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		String dateString = formatter.format(currentTime);
		map.put("ctime", dateString);
		
		return map;
	}


}