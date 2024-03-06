package com.ebixcash.aayu.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TotalIntrestRespose {

	@JsonProperty(value = "TOT_INT")
	private double totalIntrest;

	public double getTotalIntrest() {
		return totalIntrest;
	}

	public void setTotalIntrest(double totalIntrest) {
		this.totalIntrest = totalIntrest;
	}

}
