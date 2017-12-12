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

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.tuandai.architecture.componet.ToInstancesIPUrl;
import com.tuandai.architecture.constant.BZStatusCode;
import com.tuandai.architecture.exception.ServiceException;
import com.tuandai.architecture.model.PatchTransModel;
import com.tuandai.architecture.model.PostTransModel;
import com.tuandai.architecture.model.TransDetail;
import com.tuandai.architecture.service.TccService;
import com.tuandai.architecture.util.Result;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
public class TccController {

	private static final Logger logger = LoggerFactory.getLogger(TccController.class);

	@Autowired
	TccService tccService;
	
	 @Autowired
	 ToInstancesIPUrl toInstancesIPUrl;
	 

	@ApiOperation(value = "CREATE", notes = "创建事务")
	@ApiImplicitParam(name = "body", value = "基础信息", paramType = "body", required = true, dataType = "PostTransModel")
	@HystrixCommand(commandKey = "TransactionPatchTransCmd", fallbackMethod = "tccCreateFallback", threadPoolKey = "TransactionPatchTransPool")
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<Result<String>> tccCreate(@Valid @RequestBody PostTransModel body) {

		logger.debug("body: {}", body.toString());

		String serviceName = body.getServiceName();

		Long transId = tccService.createTrans(serviceName);

		return new ResponseEntity<Result<String>>(new Result<String>(String.valueOf(transId)), HttpStatus.OK);
	}
	
	public ResponseEntity<Result<String>> tccCreateFallback(@Valid @RequestBody PostTransModel body) {
		String serviceName = body.getServiceName();
		logger.error("[fallback] tccCreate serviceName: {} ", serviceName);
		return new ResponseEntity<Result<String>>(new Result<String>("{}"), HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ApiOperation(value = "TRY", notes = "预处理")
	@ApiImplicitParam(name = "body", value = "事务信息", paramType = "body", required = true, dataType = "PatchTransModel")
	@HystrixCommand(commandKey = "TransactionPatchTransCmd", fallbackMethod = "tccTryFallback", threadPoolKey = "TransactionPatchTransPool")
	@RequestMapping(value = "/tcc", method = RequestMethod.POST)
	public ResponseEntity<Object> tccTry(@Valid @RequestBody PatchTransModel body) {

		logger.debug("body: {}", body.toString());
		
        if(null == toInstancesIPUrl.getIPUrl(body.getTransUrl()))
        {
        	logger.error("invalid serviceName  transId {} , transUrl  {}", body.getTransId(), body.getTransUrl());
			throw new ServiceException(BZStatusCode.INVALID_SERVICE_NAME); 
        }

		String transId = body.getTransId();
		String transUrl = body.getTransUrl();
		String transUrlParam = body.getTransUrlParam();

		return tccService.patchTrans(Long.valueOf(transId), transUrl, transUrlParam);
	}
	
	public ResponseEntity<Object> tccTryFallback(@Valid @RequestBody PatchTransModel body) {
		String transId = body.getTransId();
		String transUrl = body.getTransUrl();		
		logger.error("[fallback] patchTrans transId: {} , transUrl: {}  ", transId, transUrl);
		return new ResponseEntity<Object>("{}", HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ApiOperation(value = "CONFIRM", notes = "确认事务")
	@ApiImplicitParam(name = "transId", value = "事务ID", paramType = "body", required = true, dataType = "String")
	@HystrixCommand(commandKey = "TransactionCCTransCmd", fallbackMethod = "tccConfirmFallback", threadPoolKey = "TransactionCCTransPool")
	@RequestMapping(value = "/tcc", method = RequestMethod.PUT)
	public ResponseEntity<Result<String>> tccConfirm(@Valid @RequestBody String transId) {

		logger.debug("transId: {}", transId);
		if (null == transId || transId.isEmpty() || !Pattern.matches("[0-9]+$", transId) || transId.length() > 23) {
			logger.error("invalid  parameter, transId: {}", transId);
			throw new ServiceException(BZStatusCode.INVALID_MODEL_FIELDS);
		}

		tccService.confrimTrans(Long.valueOf(transId));

		return new ResponseEntity<Result<String>>(new Result<String>(""), HttpStatus.OK);
	}
	
	public ResponseEntity<Result<String>> tccConfirmFallback(@Valid @RequestBody String transId) {
		logger.error("[fallback] tccConfirm controller, transId: {}", transId);
		return new ResponseEntity<Result<String>>(new Result<String>(""), HttpStatus.OK);
	}
	

	@ApiOperation(value = "CANCEL", notes = "取消事务")
	@ApiImplicitParam(name = "transId", value = "事务ID", paramType = "body", required = true, dataType = "String")
	@HystrixCommand(commandKey = "TransactionCCTransCmd", fallbackMethod = "tccCancelFallback", threadPoolKey = "TransactionCCTransPool")
	@RequestMapping(value = "/tcc", method = RequestMethod.DELETE)
	public ResponseEntity<Result<String>> tccCancel(@Valid @RequestBody String transId) {

		logger.debug("transId: {}", transId);
		if (null == transId || transId.isEmpty() || !Pattern.matches("[0-9]+$", transId) || transId.length() > 23) {
			logger.error("invalid  parameter, transId: {}", transId);
			throw new ServiceException(BZStatusCode.INVALID_MODEL_FIELDS);
		}
		//【注： 主动标记 取消事务， 切记不可主动直接取消事务，CANCEL操作必须在Try之后执行,防止因为熔断降级，导致CC在前，Try在后；】！
		tccService.cancelMark(Long.valueOf(transId));

		return new ResponseEntity<Result<String>>(new Result<String>(""), HttpStatus.OK);
	}
	
	public ResponseEntity<Result<String>> tccCancelFallback(@Valid @RequestBody String transId) {
		logger.error("[fallback] tccCancel controller, transId: {}", transId);
		return new ResponseEntity<Result<String>>(new Result<String>(""), HttpStatus.OK);
	}
	


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
		td.setLogs(tccService.getTransLog(Long.valueOf(transId)));

		return new ResponseEntity<Result<TransDetail>>(new Result<TransDetail>(td), HttpStatus.OK);
	}
	



	@ApiOperation(value = "FORCE CANCEL", notes = "强制取消事务")
	@ApiImplicitParam(name = "transId", value = "事务ID", paramType = "body", required = true, dataType = "String")
	@RequestMapping(value = "/tcc/cancel", method = RequestMethod.DELETE)
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