package com.tuandai.architecture.service.impl;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.expression.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.tuandai.architecture.componet.ThresholdsTimeManage;
import com.tuandai.architecture.componet.ToInstancesIPUrl;
import com.tuandai.architecture.config.RestTemplateHelper;
import com.tuandai.architecture.constant.Constants;
import com.tuandai.architecture.constant.TransState;
import com.tuandai.architecture.domain.Trans;
import com.tuandai.architecture.domain.TransUrls;
import com.tuandai.architecture.repository.TransLogsRepository;
import com.tuandai.architecture.repository.TransRepository;
import com.tuandai.architecture.repository.TransUrlsRepository;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TccServiceImpl.class,RestTemplateHelper.class,RestTemplate.class})
public class TccServiceImplTest {
	

	@InjectMocks
	private TccServiceImpl tccService;

	@Mock
	TransRepository transRepository;

	@Mock
	TransUrlsRepository transUrlsRepository;

	@Mock
	TransLogsRepository transLogsRepository;

	@Mock
	ThresholdsTimeManage thresholdsTimeManage;

	@Mock
	ToInstancesIPUrl toInstancesIPUrl;

	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 小写的mm表示的是分钟

	ResponseEntity<String> responseOk = new ResponseEntity<String>("{'status':200}", HttpStatus.OK);

	Trans testTrans = new Trans();
	
	List<TransUrls> transUrlList = new ArrayList<TransUrls>();
	
	/**
	 * @throws ParseException
	 */
	@Before
	public void setUp() throws ParseException {

		Date dt = new Date();
		//init trans
		testTrans.setTransId(1000001L);
		testTrans.setServiceName("test");
		testTrans.setCheckUrl(Constants.CHECK_URL);
		testTrans.setTransState(TransState.PENDING.code());
		testTrans.setCheckTimes(0);
		testTrans.setCheckThreshold(Constants.MAX_THRESHOLD);
		testTrans.setCheckTime(dt);
		testTrans.setCcThreshold(Constants.MAX_THRESHOLD);
		testTrans.setCcTimes(0);
		testTrans.setCcTime(dt);
		testTrans.setCreateTime(dt);
		testTrans.setUpdateTime(dt);
		
		TransUrls tu1 = new TransUrls();
		tu1.setId(1L);
		tu1.setTransId(1000001L);
		tu1.setCreateTime(dt);
		tu1.setTransUrl("http://www.baidu.com");
		tu1.setTransUrlParam("{}");
		tu1.setUpdateTime(dt);
		transUrlList.add(tu1);

		TransUrls tu2 = new TransUrls();
		tu2.setId(2L);
		tu2.setTransId(1000001L);
		tu2.setCreateTime(dt);
		tu2.setTransUrl("http://www.baidu.com");
		tu2.setTransUrlParam("{}");
		tu2.setUpdateTime(dt);
		transUrlList.add(tu2);
		
		
		
		
		tccService = new TccServiceImpl();
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() throws Exception {
		reset(transRepository);
		reset(transUrlsRepository);
		reset(transLogsRepository);
		reset(thresholdsTimeManage);
		reset(toInstancesIPUrl);
	}

	/**
	 * [预发送回调]请求多线程调启
	 * 
	 * @throws Exception
	 */
	@Test
	public void createTrans_Success() throws Exception {
		// stubbing
		when(transRepository.insert(anyObject())).thenReturn(1);

		when(transLogsRepository.insert(anyObject())).thenReturn(1);

		tccService.createTrans("test");
	}


	@Test
	public void patchTrans_Success() throws Exception {
		// stubbing
		when(transRepository.getByTransId(anyObject())).thenReturn(testTrans);
		when(transLogsRepository.insert(anyObject())).thenReturn(1);
		when(toInstancesIPUrl.getIPUrl(anyString())).thenReturn("http://www.sina.net");
		RestTemplateHelper.initRestTemplate(10);
		
		tccService.patchTrans(100001L, "http://spring-cloud-account/tcc", "{}");
	}
		


	@Test
	public void ccTrans_Success() throws Exception {
		when(transRepository.getByTransId(anyObject())).thenReturn(testTrans);
		when(transUrlsRepository.getByTransId(anyObject())).thenReturn(transUrlList);
		when(toInstancesIPUrl.getIPUrl(anyString())).thenReturn("http://www.sina.net");
		tccService.ccTrans(100001L,1);
	}
	
}
