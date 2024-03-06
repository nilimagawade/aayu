package com.ebixcash.aayu.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AmortIRRResponse {

	@JsonProperty("IRR")
	public double  iRR = 0.0d;;
	@JsonProperty("XIRR")
	public double xIRR = 0.0d;;
	
	
	public double getiRR() {
		return iRR;
	}
	public void setiRR(double iRR) {
		this.iRR = iRR;
	}
	public double getxIRR() {
		return xIRR;
	}
	public void setxIRR(double xIRR) {
		this.xIRR = xIRR;
	}
	
	
	
}