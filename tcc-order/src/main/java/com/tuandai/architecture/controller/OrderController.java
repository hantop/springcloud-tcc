package com.tuandai.architecture.controller;

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

import com.tuandai.architecture.constant.TransState;
import com.tuandai.architecture.domain.TranOrder;
import com.tuandai.architecture.service.OrderService;
import com.tuandai.architecture.service.TccTransBaseService;
import com.tuandai.architecture.util.Result;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
public class OrderController {

	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	OrderService orderService;

	@Autowired
	TccTransBaseService tccTransBaseService;

	@ApiOperation(value = "发起事务订单", notes = "发起事务订单")
	@RequestMapping(value = "/tcc", method = RequestMethod.POST)
	public ResponseEntity<Result<String>> tcc() {
		// 创建事务
		TranOrder to = tccTransBaseService.createTrans();
		if (null == to) {
			return new ResponseEntity<Result<String>>(new Result<String>("创建订单异常，已回滚"), HttpStatus.OK);
		}
		// 资金
		String result = orderService.transTryAccount(to);
		if (null == result) {
			return new ResponseEntity<Result<String>>(new Result<String>("资金处理异常，已回滚"), HttpStatus.OK);
		}
		// 积分
		result = orderService.transTryPoint(to);
		if (null == result) {
			return new ResponseEntity<Result<String>>(new Result<String>("积分处理异常，已回滚"), HttpStatus.OK);
		}

		// 确认事务
		tccTransBaseService.confirmTrans(to);

		return new ResponseEntity<Result<String>>(new Result<String>(""), HttpStatus.OK);
	}

	@ApiOperation(value = "检测事务订单", notes = "检测事务订单")
	@ApiImplicitParam(name = "transId", value = "事务ID", paramType = "transId", required = true, dataType = "String")
	@RequestMapping(value = "/tcc/check", method = RequestMethod.POST)
	public ResponseEntity<String> tccCheck(@Valid @RequestBody String transId) {

		logger.debug("body: {}", transId);
		TranOrder to = tccTransBaseService.check(transId);
		if (null == to) {
			return new ResponseEntity<String>(String.valueOf(TransState.CANCEL.code()), HttpStatus.OK);
		}
		
		return new ResponseEntity<String>(String.valueOf(to.getState()), HttpStatus.OK);
	}


}