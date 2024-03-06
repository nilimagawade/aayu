package com.ebixcash.aayu.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AmortBPIResponse {

	@JsonProperty(value = "BPI")
	private double bpi;

	public double getBpi() {
		return bpi;
	}

	public void setBpi(double bpi) {
		this.bpi = bpi;
	}

}
