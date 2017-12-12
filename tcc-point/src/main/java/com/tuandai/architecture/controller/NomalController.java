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

import com.tuandai.architecture.model.TryPointModel;
import com.tuandai.architecture.service.PointService;
import com.tuandai.architecture.util.Result;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
public class NomalController {

	private static final Logger logger = LoggerFactory.getLogger(NomalController.class);

	@Autowired
	PointService pointService;

	@ApiOperation(value = "预添加积分", notes = "预添加积分")
	@ApiImplicitParam(name = "body", value = "事务信息", paramType = "body", required = true, dataType = "TryPointModel")
	@RequestMapping(value = "/tcc/point", method = RequestMethod.POST)
	public ResponseEntity<Result<String>> tccTry(@Valid @RequestBody TryPointModel body) {

		logger.debug("body: {}", body.toString());
		String name = body.getName();
		Integer transId = body.getTransId();

		pointService.transTry(name, transId);

		return new ResponseEntity<Result<String>>(new Result<String>(""), HttpStatus.OK);
	}
	


	@ApiOperation(value = "撤销添加积分", notes = "撤销添加积分")
	@ApiImplicitParam(name = "transId", value = "事务ID", paramType = "transId", required = true, dataType = "String")
	@RequestMapping(value = "/tcc/point", method = RequestMethod.DELETE)
	public ResponseEntity<Result<String>> tccCancel(@Valid @RequestBody String transId) {

		logger.debug("body: {}", transId);

		pointService.transCancel(Integer.valueOf(transId));

		return new ResponseEntity<Result<String>>(new Result<String>(""), HttpStatus.OK);
	}

	


	@ApiOperation(value = "确认事务", notes = "确认事务")
	@ApiImplicitParam(name = "transId", value = "事务ID", paramType = "transId", required = true, dataType = "String")
	@RequestMapping(value = "/tcc/point", method = RequestMethod.PUT)
	public ResponseEntity<Result<String>> tccConfirm(@Valid @RequestBody String transId) {

		logger.debug("body: {}", transId);

		pointService.transConfirm(Integer.valueOf(transId));

		return new ResponseEntity<Result<String>>(new Result<String>(""), HttpStatus.OK);
	}

}