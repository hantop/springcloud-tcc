package com.tuandai.architecture.hlt.create;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * tcc-manage启动参数：java  -Dtcc.check.thresholds=1,1,1  -Dtcc.cc.thresholds=1,1,1 -jar 
 * @author jianggq
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext
@TestPropertySource(properties = { "server.port=11011" })
public class CreateTransactionTest {
	private static final Logger logger = LoggerFactory.getLogger(CreateTransactionTest.class);

	@Autowired
	private TestRestTemplate restTemplate;

	private String serviceUrl;
	
	private String testOrder;
	
	ResponseEntity<String> responseOk = null;

	@Before
	public void init() {
		//HLT服务
		testOrder = "172.22.209.70:11011";
		//tcc manage 服务url
		serviceUrl = "http://192.168.52.191:9393/";
	}

	
	//TODO  业务A方  调用  TCC
	
	/** A > T create-err-cancel-suc
	 * 场景：创建订单，生成transId 熔断，主动回滚成功；
	 */
	
	/** A > T create-err-cancel-err-check-suc
	 * 场景：创建订单，生成transId 熔断，主动回滚失败，任务调度 CHECK 调用成功，CANCEL 成功； 
	 */
	
	/** A > T create-err-cancel-err-check-err-man-suc
	 * 场景：创建订单，生成transId 熔断，主动回滚失败，任务调度 CHECK 调用无响应，CHECK超过阀值，人工干预，强制回滚； 
	 */
	
	/** A > T create-suc
	 * 场景：创建订单成功；
	 */
	
	/** A > T create-suc-check-trying
	 * 场景：创建订单成功；任务调度CHECK，事务状态 处理中；
	 */	
	
	/** create-suc
	 *  A > T-B try-err-cancel-suc
	 * 场景：创建订单成功，添加事务请求业务A方 调用TCC 熔断异常，主动cancel事务，TCC标记transId为cancel成功，  tcc任务调度回滚成功；
	 * 【此流程易出问题】 如果 TCC调用业务B方 patch降级时间   比  CC任务调度阀值 大的话，业务B方会出现先cancel，再try的错误流程！  
	 */
	
	/** create-suc
	 *  A > T-B try-err-cancel-err-check-suc
	 * 场景：创建订单成功，添加事务请求业务A方 调用TCC 熔断异常，主动cancel事务异常，任务调度 CHECK 调用成功，CANCEL 成功； 
	 */
	
	/** create-suc
	 * A > T-B try-suc-confirm-err-check-suc
	 * 场景：创建订单成功，添加事务请求业务A方 调用TCC 成功，TCC调用业务B方 patch 成功，业务A方 主动confirm TCC失败， 任务调度CHECK 状态为 confirm 成功，任务调度confirm 业务B方成功；
	 */

	/** create-suc
	 *  A > T-B try-suc-confirm-suc
	 * 场景：创建订单成功，添加事务请求业务A方 调用TCC 成功，TCC调用业务B方 patch 成功，业务A方 主动confirm TCC成功， TCC confirm 业务B方成功，事务正常流程处理完成；
	 */


	//TODO  TCC 调用   业务B方

	
	/** A-T trying
	 *  A-T > B try-err-cancel-suc
	 * 场景：添加事务请求，调用业务B方  try 熔断异常，主动cancel，transId标记为cancel， tcc任务调度回滚；
	 */

	/** A-T try-suc
	 *  A-T > B try-suc-confirm-err-job-suc
	 * 场景：添加事务请求，远程事务请求成功，调用业务B方 主动确认confirm失败，定时任务调度confirm成功；
	 */


	/** A-T try-suc
	 *  A-T > B try-suc-confirm-err-job-err-man-suc
	 * 场景：添加事务请求，远程事务请求成功，调用业务B方 主动确认confirm失败，定时任务调度confirm 失败超过阀值，人工干预-成功；
	 */

	/** A-T try-err
	 *  A-T > B job-cancel-err-man-suc
	 * 场景：添加事务请求失败，transId标记为cancel， 定时任务调用业务B方  cancel失败超过阀值，人工干预-成功；
	 */

	/** A-T try-err
	 *  A-T > B job-cancel-suc
	 * 场景：添加事务请求失败，transId标记为cancel， 定时任务调用业务B方  cancel成功；
	 */


}

