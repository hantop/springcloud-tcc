package com.tuandai.architecture.hlt.create;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tuandai.architecture.constant.TransState;
import com.tuandai.architecture.service.Constants;

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

	//TODO
	
	/**
	 * 场景：【模拟order异常返回】创建TransId成功，生成orderId异常，此order服务，CHECK对应状态值： unkown，CHECK超过阀值，人工干预，回滚； 
	 */
	@Test
	public void createOrder_check_unkown() throws Exception {
		logger.info("createOrder_check_unkown start!");
		
		HashMap<String, Object> httpbodyMap = new HashMap<String, Object>();
		httpbodyMap.put("serviceName", testOrder);

		// 创建TransId成功  {"status":200,"data":"277","message":"请求成功"}
		ResponseEntity<String> response = this.restTemplate.exchange(serviceUrl + "/create", HttpMethod.POST,
				new HttpEntity<HashMap<String, Object>>(httpbodyMap, null), String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		String transId = JSONObject.parseObject(response.getBody()).getString("data");
		
		//生成orderId异常
		Constants.checkResult = TransState.UNKNOW.code();
		
		//等待回调超过阀值
		Thread.sleep(40000);
		
		//查询CHECK异常数据
		response = this.restTemplate.exchange(serviceUrl + "/tcc/get/" + transId, HttpMethod.POST,null, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		JSONObject trans = JSONObject.parseObject(response.getBody()).getJSONObject("data").getJSONObject("trans");
		assertThat(trans.getInteger("checkThreshold")).isEqualTo(0);
		
		//人工干预 强制回滚
		response = this.restTemplate.exchange(serviceUrl + "/tcc/cancel", HttpMethod.DELETE,
				new HttpEntity<String>(transId, null), String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		//回滚确认
		response = this.restTemplate.exchange(serviceUrl + "/tcc/get/" + transId, HttpMethod.POST,null, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		trans = JSONObject.parseObject(response.getBody()).getJSONObject("data").getJSONObject("trans");
		assertThat(trans).isNull();
		JSONArray transUrl = JSONObject.parseObject(response.getBody()).getJSONObject("data").getJSONArray("urls");
		assertThat(transUrl.size()).isEqualTo(0);
	}
	

	/**
	 * 场景：创建订单，生成transId 熔断，自动回滚；
	 */
	@Test
	public void createOrder_check_faild() throws Exception {
		logger.info("createOrder_check_faild start!");
		
		HashMap<String, Object> httpbodyMap = new HashMap<String, Object>();
		httpbodyMap.put("serviceName", testOrder);

		// 创建TransId 模拟熔断，不主动回滚
		ResponseEntity<String> response = this.restTemplate.exchange(serviceUrl + "/create", HttpMethod.POST,
				new HttpEntity<HashMap<String, Object>>(httpbodyMap, null), String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		String transId = JSONObject.parseObject(response.getBody()).getString("data");
		
		//状态回滚
		Constants.checkResult = TransState.CANCEL.code();
		
		//等待回调超过阀值
		Thread.sleep(12000);
		
		//回滚确认
		response = this.restTemplate.exchange(serviceUrl + "/tcc/get/" + transId, HttpMethod.POST,null, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		JSONObject trans = JSONObject.parseObject(response.getBody()).getJSONObject("data").getJSONObject("trans");
		assertThat(trans).isNull();
		JSONArray transUrl = JSONObject.parseObject(response.getBody()).getJSONObject("data").getJSONArray("urls");
		assertThat(transUrl.size()).isEqualTo(0);
	}

	
	/**
	 * 场景：创建订单成功；
	 */
	@Test
	public void createOrder_success() throws Exception {
		logger.info("createOrder_check_faild start!");
		
		HashMap<String, Object> httpbodyMap = new HashMap<String, Object>();
		httpbodyMap.put("serviceName", testOrder);

		// 创建TransId 成功
		ResponseEntity<String> response = this.restTemplate.exchange(serviceUrl + "/create", HttpMethod.POST,
				new HttpEntity<HashMap<String, Object>>(httpbodyMap, null), String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		String transId = JSONObject.parseObject(response.getBody()).getString("data");
		
		// 添加事务调用链
		
		
		//状态回滚
		Constants.checkResult = TransState.CANCEL.code();
		
		//等待回调超过阀值
		Thread.sleep(12000);
		
		//回滚确认
		response = this.restTemplate.exchange(serviceUrl + "/tcc/get/" + transId, HttpMethod.POST,null, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		JSONObject trans = JSONObject.parseObject(response.getBody()).getJSONObject("data").getJSONObject("trans");
		assertThat(trans).isNull();
		JSONArray transUrl = JSONObject.parseObject(response.getBody()).getJSONObject("data").getJSONArray("urls");
		assertThat(transUrl.size()).isEqualTo(0);
	}

		
	
	//TODO
	
	/**
	 * 场景：添加事务请求，熔断异常，主动cancel，transId标记为cancel， tcc任务调度回滚；
	 */
	
	/**
	 * 场景：添加事务请求，熔断异常，tcc回调失败，check为：cancel， tcc回滚；
	 */

	/**
	 * 场景：添加事务请求，熔断异常，tcc回调异常，tcc回调CHECK超过阀值，人工干预；【线上order 必须自动清理机制，及时cancel 或 confirm，不能长期存在此类事务】
	 */

	/**
	 * 场景：添加事务请求，远程事务请求异常，tcc返回失败，check为：cancel， tcc回滚；
	 */

	/**
	 * 场景：添加事务请求，远程事务请求熔断，tcc返回失败，check为：cancel， tcc回滚；
	 */

	

	//TODO
	
	
	/**
	 * 场景：添加事务请求，远程事务请求成功，order主动确认confirm成功；
	 */

	/**
	 * 场景：添加事务请求，远程事务请求成功，order主动确认confirm失败，tcc定时check成功；
	 */

	/**
	 * 场景：添加事务请求，远程事务请求成功，order主动确认confirm失败，tcc定时check失败超过阀值，人工干预；
	 */


}

