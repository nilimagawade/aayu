package com.ebixcash.aayu.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AmortEmiResponse {

	@JsonProperty(value = "EMI")
	private double amortEmi;

	public double getAmortEmi() {
		return amortEmi;
	}

	public void setAmortEmi(double amortEmi) {
		this.amortEmi = amortEmi;
	}

}
