package com.tuandai.architecture.tmm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.tuandai.architecture.service.PointService;

@Component
public class TmmConsumer {
	private static final Logger logger = LoggerFactory.getLogger(TmmConsumer.class);
	@Autowired
	PointService pointService;

	@RabbitListener(queues = "tccpoint")
	@RabbitHandler
	public void hadler(@Payload byte[] datas) {
		String msg = new String(datas);
		//System.out.println("消费消息:" + new String(msg));
		String transId = JSONObject.parseObject(msg).getString("uid");
		int state = JSONObject.parseObject(msg).getIntValue("state");
		Boolean rs = null;
		if (state == 0) {// 提交
			rs = pointService.transConfirm(transId);
			logger.error("tccConfirm point: {} is {}", transId, rs);
		} else if (state == 1) {// 回滚
			rs = pointService.transCancel(transId);
			logger.error("transCancel point: {} is {}", transId, rs);
		}

	}
}
