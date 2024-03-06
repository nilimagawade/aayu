package com.ebixcash.aayu.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AmortLoanAmountResponse {

	private double p;

	public double getP() {
		return p;
	}

	public void setP(double p) {
		this.p = p;
	}

	@Override
	public String toString() {
		return "LoanAmountResponse [p=" + p + "]";
	}

}
