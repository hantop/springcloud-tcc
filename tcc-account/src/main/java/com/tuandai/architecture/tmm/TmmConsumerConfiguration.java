package com.tuandai.architecture.tmm;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tuandai.transaction.client.consumer.DeclareDeadQueue;

@Configuration
public class TmmConsumerConfiguration {
	@Bean
	public DeclareDeadQueue createAmqpConfig() {
		return new DeclareDeadQueue("tccaccount", "myVhost2", "tccExchange");
	}

	// 你需要自己定义绑定交换机和你的业务队列

	@Bean
	public TopicExchange mychange() {
		return new TopicExchange("tccExchange");
	}

	// 业务队列的beanName是
	// "AmqpConfig" + "_p", 如本实例的ampqConfig的beanName是
	// createAmqpConfig，则业务队列的beanName是 createAmqpConfig_p
	@Bean
	public Binding bingdingqueue(@Qualifier("tccaccount") Queue queue,
			@Qualifier("mychange") TopicExchange topicExchange) {
		return BindingBuilder.bind(queue).to(topicExchange).with("#.tcc-account/account.#");
	}
}
