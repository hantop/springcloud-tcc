package com.tuandai.flume.sink.rabbitmq.transaction;

import java.util.Date;

import org.apache.flume.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;


public class DealEvents {
	private static final Logger logger = LoggerFactory.getLogger(DealEvents.class);

	public static final String TYPE_FILED="type";
	public static final String UID_FILED="uid";
	public static final String SERVICE_NAME_FILED="serviceName";
	public static final String TIME_FILED="time";
	public static final String STATE_FILED="state";
	public static final String RESURLS_FILED="resUrls";
	
	
	public static JSONObject deal(Event event){
		
		byte[] body = event.getBody();
		JSONObject eventJson = JSONObject.parseObject(new String(body));
		
		if(!(eventJson.containsKey(UID_FILED) && eventJson.containsKey(SERVICE_NAME_FILED))){
			logger.error("can't find uid and  serviceName : {}", eventJson.toJSONString());
			return null;
		}
		if(eventJson.containsKey(TIME_FILED)){
			eventJson.remove(TIME_FILED);
		}
		Date dt = new Date();
		eventJson.put(TIME_FILED, dt.getTime());
		
		//添加beginMap
		if(eventJson.containsKey(DealEvents.TYPE_FILED)){
			JSONObject sendData = LogMapFile.endMap.get(eventJson.getString(UID_FILED));
			if(null != sendData){
				eventJson.put(STATE_FILED, sendData.getIntValue(STATE_FILED));
				LogMapFile.endMap.remove(eventJson.getString(UID_FILED));
				return eventJson;
			}
			LogMapFile.addLog(eventJson.getString(UID_FILED), eventJson);
			return null;
		}
		
		//分析state log
		return dealStateLog(eventJson);
	}

	private static JSONObject dealStateLog(JSONObject eventJson){
		JSONObject sendData = null;
		if(!eventJson.containsKey(STATE_FILED)){
			logger.error("can't find state : {}", eventJson.toJSONString());
			return null;
		}

		sendData = LogMapFile.beginMap.get(eventJson.getString(UID_FILED));
		if(null == sendData){
			//添加endMap
			LogMapFile.endMap.put(eventJson.getString(UID_FILED), eventJson);
			return null;
		}
		
		sendData.put(STATE_FILED, eventJson.getIntValue(STATE_FILED));
		//清理beginMap
		LogMapFile.beginMap.remove(eventJson.getString(UID_FILED));
		
		return sendData;
	}
}
