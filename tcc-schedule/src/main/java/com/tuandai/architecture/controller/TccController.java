package com.tuandai.architecture.controller;

import java.util.regex.Pattern;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tuandai.architecture.constant.BZStatusCode;
import com.tuandai.architecture.domain.TransDetail;
import com.tuandai.architecture.exception.ServiceException;
import com.tuandai.architecture.service.TccService;
import com.tuandai.architecture.util.Result;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
public class TccController {

	private static final Logger logger = LoggerFactory.getLogger(TccController.class);

	@Autowired
	TccService tccService;
		 

	@ApiOperation(value = "INFO", notes = "获取事务详情")
	@ApiImplicitParam(name = "transId", value = "事务ID", paramType = "path", required = true, dataType = "String")
	@RequestMapping(value = "/tcc/get/{transId}", method = RequestMethod.POST)
	public ResponseEntity<Result<TransDetail>> tccGet(@PathVariable  String transId) {

		logger.debug("transId: {}", transId);
		if (null == transId || transId.isEmpty() || !Pattern.matches("[0-9]+$", transId) || transId.length() > 23) {
			logger.error("invalid  parameter, transId: {}", transId);
			throw new ServiceException(BZStatusCode.INVALID_MODEL_FIELDS);
		}
		
		TransDetail td = new TransDetail();

		td.setTrans(tccService.getTrans(Long.valueOf(transId)));
		td.setUrls(tccService.getTransUrl(Long.valueOf(transId)));

		return new ResponseEntity<Result<TransDetail>>(new Result<TransDetail>(td), HttpStatus.OK);
	}
	



	@ApiOperation(value = "FORCE CANCEL", notes = "强制取消事务")
	@ApiImplicitParam(name = "transId", value = "事务ID", paramType = "body", required = true, dataType = "String")
	@RequestMapping(value = "/tcc/cancel", method = RequestMethod.POST)
	public ResponseEntity<Result<String>> tccForceCancel(@Valid @RequestBody String transId) {

		logger.debug("transId: {}", transId);
		if (null == transId || transId.isEmpty() || !Pattern.matches("[0-9]+$", transId) || transId.length() > 23) {
			logger.error("invalid  parameter, transId: {}", transId);
			throw new ServiceException(BZStatusCode.INVALID_MODEL_FIELDS);
		}

		tccService.forceCancelTrans(Long.valueOf(transId));

		return new ResponseEntity<Result<String>>(new Result<String>(""), HttpStatus.OK);
	}

}