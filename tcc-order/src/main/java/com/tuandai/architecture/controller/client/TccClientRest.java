package com.tuandai.architecture.controller.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import com.tuandai.architecture.model.PatchTransModel;
import com.tuandai.architecture.model.PostTransModel;

/**
 * 
 */
@Service
public class TccClientRest {

	@Autowired
	RestTemplate restTemplate;

	/**
	 * eureka service name
	 */
	String SERVICE_ID = "tcc-manage";

	public String create(PostTransModel postTransModel) {

		HttpHeaders header = new HttpHeaders();
		ResponseEntity<String> response = restTemplate.exchange("http://" + SERVICE_ID + "/create", HttpMethod.POST,
				new HttpEntity<PostTransModel>(postTransModel, header), String.class);
		if (HttpStatus.OK.equals(response.getStatusCode())) {
			return response.getBody();
		}
		return null;
	}

	public String tryTrans(@RequestBody PatchTransModel patchTransModel) {
		HttpHeaders header = new HttpHeaders();
		ResponseEntity<String> response = restTemplate.exchange("http://" + SERVICE_ID + "/try", HttpMethod.POST,
				new HttpEntity<PatchTransModel>(patchTransModel, header), String.class);
		if (HttpStatus.OK.equals(response.getStatusCode())) {
			return response.getBody();
		}
		return null;
	}

	public String confirmTrans(@RequestBody String transId) {
		HttpHeaders header = new HttpHeaders();
		ResponseEntity<String> response = restTemplate.exchange("http://" + SERVICE_ID + "/confirm", HttpMethod.POST,
				new HttpEntity<String>(transId, header), String.class);
		if (HttpStatus.OK.equals(response.getStatusCode())) {
			return response.getBody();
		}
		return null;
	}

	public String cancelTrans(@RequestBody String transId) {
		HttpHeaders header = new HttpHeaders();
		ResponseEntity<String> response = restTemplate.exchange("http://" + SERVICE_ID + "/cancel", HttpMethod.POST,
				new HttpEntity<String>(transId, header), String.class);
		if (HttpStatus.OK.equals(response.getStatusCode())) {
			return response.getBody();
		}
		return null;
	}

}
