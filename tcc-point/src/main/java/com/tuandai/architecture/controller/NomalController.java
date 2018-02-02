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
	@RequestMapping(value = "/point/try", method = RequestMethod.POST)
	public ResponseEntity<Result<String>> tccTry(@Valid @RequestBody TryPointModel body) {

		logger.error("tccTry b: {}", body.toString());
		String name = body.getName();
		String transId = body.getTransId();

		int row = pointService.transTry(name, transId);

		logger.error("tccTry e: {}", transId);
		HttpStatus httpStatus = HttpStatus.OK;
		if (0 == row) {
			httpStatus = HttpStatus.FAILED_DEPENDENCY;
		} 
		return new ResponseEntity<Result<String>>(new Result<String>(transId), httpStatus);
		
	}

	@ApiOperation(value = "撤销添加积分", notes = "撤销添加积分")
	@ApiImplicitParam(name = "transId", value = "事务ID", paramType = "transId", required = true, dataType = "String")
	@RequestMapping(value = "/point/cancel", method = RequestMethod.POST)
	public ResponseEntity<Result<String>> tccCancel(@Valid @RequestBody String transId) {

		logger.error("tccCancel b: {}", transId);

		Boolean rs = pointService.transCancel(transId);

		logger.error("tccCancel e: {}", transId);

		HttpStatus httpStatus = HttpStatus.OK;
		if (!rs) {
			httpStatus = HttpStatus.FAILED_DEPENDENCY;
		} 
		return new ResponseEntity<Result<String>>(new Result<String>(transId), httpStatus);
		
	}

	@ApiOperation(value = "确认事务", notes = "确认事务")
	@ApiImplicitParam(name = "transId", value = "事务ID", paramType = "transId", required = true, dataType = "String")
	@RequestMapping(value = "/point/confirm", method = RequestMethod.POST)
	public ResponseEntity<Result<String>> tccConfirm(@Valid @RequestBody String transId) {

		logger.error("tccConfirm b: {}", transId);

		Boolean rs = pointService.transConfirm(transId);

		logger.error("tccConfirm e: {}", transId);

		HttpStatus httpStatus = HttpStatus.OK;
		if (!rs) {
			httpStatus = HttpStatus.FAILED_DEPENDENCY;
		} 
		return new ResponseEntity<Result<String>>(new Result<String>(transId), httpStatus);
		
	}

}