package com.ebixcash.aayu.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AmortNPVResponse {

	@JsonProperty(value = "NPV")
	private double npvValue;

	public double getNpvValue() {
		return npvValue;
	}

	public void setNpvValue(double npvValue2) {
		this.npvValue = npvValue2;
	}

}
