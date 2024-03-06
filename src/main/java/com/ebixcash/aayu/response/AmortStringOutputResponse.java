package com.ebixcash.aayu.response;

public class AmortStringOutputResponse {
	
	private String header;
	
	private String responseData;

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getResponseData() {
		return responseData;
	}

	public void setResponseData(String responseData) {
		this.responseData = responseData;
	}

	@Override
	public String toString() {
		return "AmortStringOutputResponse [header=" + header + ", responseData=" + responseData + "]";
	}

}
