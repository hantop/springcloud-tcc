package com.tuandai.architecture.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.tuandai.architecture.domain.Trans;

/**
 * 
 */
@Service
public class TccServiceClient {

	@Autowired
	RestTemplate restTemplate;

	public String checkTrans(Trans trans) {
		HttpHeaders header = new HttpHeaders();
		ResponseEntity<String> response = restTemplate.exchange("http://" + trans.getServiceName() + trans.getCheckUrl(), HttpMethod.POST,
				new HttpEntity<String>(String.valueOf(trans.getTransId()), header), String.class);
		if (HttpStatus.OK.equals(response.getStatusCode())) {
			return response.getBody();
		}
		return null;
	}


	public String confirmTrans(String transUrl,String transId) {
		HttpHeaders header = new HttpHeaders();
		ResponseEntity<String> response = restTemplate.exchange("http://" + transUrl + "/confirm", HttpMethod.POST,
				new HttpEntity<String>(transId, header), String.class);
		if (HttpStatus.OK.equals(response.getStatusCode())) {
			return response.getStatusCode().toString();
		}
		return null;
	}

	public String cancelTrans(String transUrl,String transId) {
		HttpHeaders header = new HttpHeaders();
		ResponseEntity<String> response = restTemplate.exchange("http://" + transUrl + "/cancel", HttpMethod.POST,
				new HttpEntity<String>(transId, header), String.class);
		if (HttpStatus.OK.equals(response.getStatusCode())) {
			return response.getStatusCode().toString();
		}
		return null;
	}

}
