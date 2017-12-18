package com.tuandai.architecture.controller.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tuandai.architecture.model.PatchTransModel;
import com.tuandai.architecture.model.PostTransModel;


/**
 * 
 */
@FeignClient(name = TccClient.SERVICE_ID, fallback = TccClientFallback.class)
public interface TccClient {
    /**
     * eureka service name
     */
    String SERVICE_ID = "tcc-manage";
    /**
     * common api prefix
     */
    String CREATE_PATH = "/create";
    String TCC_TRY = "/try";
    String TCC_CONFIRM = "/confirm";
    String TCC_CANCEL = "/cancel";

    @RequestMapping(value = CREATE_PATH , method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    String create(@RequestBody PostTransModel postTransModel);

    @RequestMapping(value = TCC_TRY , method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    String tryTrans(@RequestBody PatchTransModel patchTransModel);

    @RequestMapping(value = TCC_CONFIRM , method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    String confirmTrans(@RequestBody String transId);

    @RequestMapping(value = TCC_CANCEL , method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    String cancelTrans(@RequestBody String transId);

}
