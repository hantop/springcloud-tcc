package com.tuandai.architecture.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

public class PatchTransModel {

    @NotNull
    @NotBlank
    @Size(min = 1, max = 24, message = "请输入事务ID")
    @JsonProperty("transId")
    @ApiModelProperty(value = "事务ID", example = "1353243596056", required = true)
	private String transId;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 1024, message = "请输入远程请求Url")
    @JsonProperty("transUrl")
    @ApiModelProperty(value = "远程请求Url", example = "http://{account}/tcc/bzname", required = true)
	private String transUrl;

    @NotNull
    @Size(min = 1, max = 1024, message = "请输入远程请求BODY")
    @JsonProperty("transUrlParam")
    @ApiModelProperty(value = "远程请求BODY", example = "{'name':'Lily','account':10}", required = true)
	private String transUrlParam;
    
	public String getTransId() {
		return transId;
	}

	public void setTransId(String transId) {
		this.transId = transId;
	}

	public String getTransUrl() {
		return transUrl;
	}

	public void setTransUrl(String transUrl) {
		this.transUrl = transUrl;
	}

	public String getTransUrlParam() {
		return transUrlParam;
	}

	public void setTransUrlParam(String transUrlParam) {
		this.transUrlParam = transUrlParam;
	}

	@Override
	public String toString() {
		return this.transId + " : " + this.transUrl + " : " + this.transUrlParam;
	}
	
}
