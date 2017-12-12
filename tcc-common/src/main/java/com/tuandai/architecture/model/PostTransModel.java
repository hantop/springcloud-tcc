package com.tuandai.architecture.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

public class PostTransModel {

    @NotNull
    @NotBlank
    @Size(min = 1, max = 50, message = "请输入服务名")
    @JsonProperty("serviceName")
    @ApiModelProperty(value = "服务名", example = "order", required = true)
	private String serviceName;


	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}


	@Override
	public String toString() {
		return this.serviceName ;
	}
	
}
