package com.ebixcash.aayu.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AmortIntrestResponse {
	@JsonProperty(value = "interest")
	private double amortIntrest;

	public double getAmortIntrest() {
		return amortIntrest;
	}

	public void setAmortIntrest(double amortIntrest) {
		this.amortIntrest = amortIntrest;
	}

}
