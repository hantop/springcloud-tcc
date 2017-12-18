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

import com.tuandai.architecture.model.TryAccountModel;
import com.tuandai.architecture.service.AccountService;
import com.tuandai.architecture.util.Result;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
public class TCCController {

	private static final Logger logger = LoggerFactory.getLogger(TCCController.class);

	@Autowired
	AccountService accountService;

	@ApiOperation(value = "冻结资金", notes = "冻结资金")
	@ApiImplicitParam(name = "body", value = "事务信息", paramType = "body", required = true, dataType = "TryAccountModel")
	@RequestMapping(value = "/account/try", method = RequestMethod.POST)
	public ResponseEntity<Result<String>> tccTry(@Valid @RequestBody TryAccountModel body) {

		logger.error("tccTry b: {}", body.toString());
		String name = body.getName();
		Integer transId = body.getTransId();

		accountService.transTry(name, transId);

		logger.error("tccTry e: {}", transId);
		return new ResponseEntity<Result<String>>(new Result<String>("account try: " + transId), HttpStatus.OK);
	}
	


	@ApiOperation(value = "解冻资金", notes = "解冻资金")
	@ApiImplicitParam(name = "transId", value = "事务ID", paramType = "transId", required = true, dataType = "String")
	@RequestMapping(value = "/account/cancel", method = RequestMethod.POST)
	public ResponseEntity<Result<String>> tccCancel(@Valid @RequestBody String transId) {

		logger.error("tccCancel b: {}", transId);

		accountService.transCancel(Integer.valueOf(transId));

		logger.error("tccCancel e: {}", transId);
		return new ResponseEntity<Result<String>>(new Result<String>("account cancel: " + transId), HttpStatus.OK);
	}

	


	@ApiOperation(value = "确认事务", notes = "确认事务")
	@ApiImplicitParam(name = "transId", value = "事务ID", paramType = "transId", required = true, dataType = "String")
	@RequestMapping(value = "/account/confirm", method = RequestMethod.POST)
	public ResponseEntity<Result<String>> tccConfirm(@Valid @RequestBody String transId) {

		logger.error("tccConfirm b: {}", transId);

		accountService.transConfirm(Integer.valueOf(transId));

		logger.error("tccConfirm e: {}", transId);
		return new ResponseEntity<Result<String>>(new Result<String>("account confirm: " + transId), HttpStatus.OK);
	}

}