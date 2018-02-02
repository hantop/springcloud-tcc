package com.tuandai.architecture.controller.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.alibaba.fastjson.JSONObject;
import com.tuandai.architecture.controller.PatchTransModel;

/**
 * 
 */
@Service
public class TccClientRest {

	//private static final Logger logger = LoggerFactory.getLogger(TccClientRest.class);
	
	@Autowired
	AccountTccClient accountTccClient;
	
	@Autowired
	PointTccClient pointTccClient;

	/**
	 * eureka service name
	 */
	public static String ACCOUNT_SERVICE = "tcc-account/account";

	public static String POINT_SERVICE = "tcc-point/point";

	public Boolean tryAccount(@RequestBody PatchTransModel patchTransModel) {
		try {
			String rel = accountTccClient.tryTrans(patchTransModel);
			JSONObject jo = JSONObject.parseObject(rel);
			if(200 == jo.getIntValue("status")){
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	public Boolean tryPoint(@RequestBody PatchTransModel patchTransModel) {
		try {
			String rel = pointTccClient.tryTrans(patchTransModel);
			JSONObject jo = JSONObject.parseObject(rel);
			if(200 == jo.getIntValue("status")){
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

}
