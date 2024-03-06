package com.ebixcash.aayu.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AmortIntrestRateResponse {

	@JsonProperty(value = "real_interest_rate")
	private double interestrate;

	/**
	 * @return the interestrate
	 */
	public double getInterestrate() {
		return interestrate;
	}

	/**
	 * @param interestrate the interestrate to set
	 */
	public void setInterestrate(double interestrate) {
		this.interestrate = interestrate;
	}

}
