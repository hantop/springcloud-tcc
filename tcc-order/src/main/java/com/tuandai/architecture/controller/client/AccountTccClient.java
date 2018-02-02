package com.tuandai.architecture.controller.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tuandai.architecture.controller.PatchTransModel;

/** * */
@FeignClient(name = AccountTccClient.SERVICE_ID, fallback = AccountTccClientFallback.class)
public interface AccountTccClient {
	/** * eureka service name */
	String SERVICE_ID = "tcc-account";
	/** * common api prefix */
	String TCC_TRY = "/account/try";

	@RequestMapping(value = TCC_TRY, method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_UTF8_VALUE }, consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE })
	String tryTrans(@RequestBody PatchTransModel patchTransModel);

}