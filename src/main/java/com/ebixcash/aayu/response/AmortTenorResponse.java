package com.ebixcash.aayu.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AmortTenorResponse {
	@JsonProperty(value = "T")
	private double t;
	@JsonProperty(value = "LID")
	private String real_lid;

	public String getReal_lid() {
		return real_lid;
	}

	public void setReal_lid(String string) {
		this.real_lid = string;
	}

	public double getT() {
		return t;
	}

	public void setT(double t) {
		this.t = t;
	}

}
