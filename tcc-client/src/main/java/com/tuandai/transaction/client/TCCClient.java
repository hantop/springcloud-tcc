package com.tuandai.transaction.client;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.tuandai.transaction.constant.TCCState;

public class TCCClient {
	private static final Logger logger = LoggerFactory.getLogger(TCCClient.class);

	private static final String TYPE = "TCC";
	
	public static final char RES_SEPARATOR_CHAR = '.';

	/**
	 * 初始化事务消息接入方案
	 * 
	 * @param isLogFile
	 *            true  日志文件方式输出  , false RPC方式输出
	 * @param ip
	 *            日志方式，为null即可； 
	 *            RPC方式，必须指明RPC服务所在IP地址，默认端口： 41414 ，41415
	 */
	public static void initAsLog4j() {
		ClientFacade.init(true, "","");
	}

	public static void initAsRpc(String rpcPath) {
		ClientFacade.init(false, "127.0.0.1",rpcPath);
	}



	/**
	 * 开始事务日志  
	 * @param id  业务唯一ID （ UUID） ,必选
	 * @param serviceName  业务应用名称,必选
	 * @param checkUrl 回调地址,必选
	 * @param resUrls  资源列表,必选
	 */
	public static Boolean sendTransBeginToFlume(String id, String serviceName, String checkUrl, String resUrls) {
		logger.debug("uid:{} , serviceName:{} ,type:{} ,resUrls:{}", id, serviceName, resUrls);
		JSONObject jsData = new JSONObject();
		jsData.put("type", TYPE);
		jsData.put("uid", id);
		jsData.put("serviceName", serviceName);
		jsData.put("resUrls", resUrls);
		jsData.put("checkUrl", checkUrl);
		
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		String dateString = formatter.format(currentTime);
		jsData.put("ctime", dateString);

		return ClientFacade.sendDataToFlume(jsData.toJSONString());
	}

	/**
	 * 结束事务日志
	 * @param state 状态： 成功，失败，重试 ,必选
	 * @param id  业务唯一ID （ UUID） ,必选
	 * @param serviceName  业务应用名称,必选
	 * @return
	 */
	public static Boolean sendTransEndToFlume(TCCState state,String id, String serviceName) {
		logger.debug("uid:{} , serviceName:{}  ,state:{}", id, serviceName, state);
		JSONObject jsData = new JSONObject();
		jsData.put("state", state.value());
		jsData.put("uid", id);
		jsData.put("serviceName", serviceName);
		
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		String dateString = formatter.format(currentTime);
		jsData.put("ctime", dateString);

		return ClientFacade.sendDataToFlume(jsData.toJSONString());
	}
}
