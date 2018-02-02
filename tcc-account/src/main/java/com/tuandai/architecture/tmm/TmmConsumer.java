package com.tuandai.architecture.tmm;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.tuandai.architecture.service.AccountService;

@Component
public class TmmConsumer {

	@Autowired
	AccountService accountService;

	@RabbitListener(queues = "tccaccount")
	@RabbitHandler
	public void hadler(@Payload byte[] datas) {
		String msg = new String(datas);
		String transId = JSONObject.parseObject(msg).getString("uid");
		int state = JSONObject.parseObject(msg).getIntValue("state");
		Boolean rs = null;
		if (state == 0) {// 提交
			rs = accountService.transConfirm(transId);
		} else if (state == 1) {// 回滚
			rs = accountService.transCancel(transId);
		}
	}
}
