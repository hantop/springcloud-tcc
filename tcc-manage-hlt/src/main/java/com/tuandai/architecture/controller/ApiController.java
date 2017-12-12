package com.tuandai.architecture.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tuandai.architecture.service.Constants;

@RestController
public class ApiController {

	private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

	/**
	 * 事务消息回调
	 */
	@RequestMapping(value = "/tcc/check", method = RequestMethod.POST)
	public ResponseEntity<String> checkFailedListener(@RequestBody String body) {
		
		logger.info("===========tcc check============ {}",Constants.checkResult);
		return new ResponseEntity<String>(String.valueOf(Constants.checkResult), HttpStatus.OK);
	}

}