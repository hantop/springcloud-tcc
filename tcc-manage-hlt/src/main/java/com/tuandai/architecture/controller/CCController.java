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
@RequestMapping(value = "/cc")
public class CCController {

	private static final Logger logger = LoggerFactory.getLogger(CCController.class);

	/**
	 * try
	 */
	@RequestMapping(value = "/tcc/test", method = RequestMethod.POST)
	public ResponseEntity<String> cctry(@RequestBody String body) {
		
		logger.info("===========tcc check============ {}",Constants.tryResult);
		return new ResponseEntity<String>(String.valueOf(Constants.tryResult), HttpStatus.OK);
	}

	/**
	 * try error
	 */
	@RequestMapping(value = "/tcc/error", method = RequestMethod.POST)
	public ResponseEntity<String> cctryerror(@RequestBody String body) {
		
		logger.info("===========tcc check============ {}",Constants.tryResult);
		return new ResponseEntity<String>(String.valueOf(Constants.tryResult), HttpStatus.NOT_FOUND);
	}
	
	/**
	 * confirm
	 */
	@RequestMapping(value = "/tcc/test", method = RequestMethod.PUT)
	public ResponseEntity<String> confirm(@RequestBody String body) {
		
		logger.info("===========tcc check============ {}",Constants.confirmResult);
		return new ResponseEntity<String>(String.valueOf(Constants.confirmResult), HttpStatus.OK);
	}

	/**
	 * confirm error
	 */
	@RequestMapping(value = "/tcc/error", method = RequestMethod.PUT)
	public ResponseEntity<String> confirmerror(@RequestBody String body) {
		
		logger.info("===========tcc check============ {}",Constants.confirmResult);
		return new ResponseEntity<String>(String.valueOf(Constants.confirmResult), HttpStatus.NOT_FOUND);
	}
	
	/**
	 * cancel
	 */
	@RequestMapping(value = "/tcc/test", method = RequestMethod.DELETE)
	public ResponseEntity<String> cancel(@RequestBody String body) {
		
		logger.info("===========tcc check============ {}",Constants.cancelResult);
		return new ResponseEntity<String>(String.valueOf(Constants.cancelResult), HttpStatus.OK);
	}
	
	/**
	 * cancel error
	 */
	@RequestMapping(value = "/tcc/error", method = RequestMethod.DELETE)
	public ResponseEntity<String> cancelerror(@RequestBody String body) {
		
		logger.info("===========tcc check============ {}",Constants.cancelResult);
		return new ResponseEntity<String>(String.valueOf(Constants.cancelResult), HttpStatus.NOT_FOUND);
	}

}