package com.ebixcash.aayu.response;

public class ErrorResponse {

	private String errorCode;
	
	private String errorDesc;

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorDesc() {
		return errorDesc;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	@Override
	public String toString() {
		return "ErrorResponse [errorCode=" + errorCode + ", errorDesc=" + errorDesc + "]";
	}
	
}

