package com.ebixcash.aayu.util;

public class InternalAmortComponentBean {

	private double val = 0.0d;
	private String toadd = "";
	private String reqop = "";
	private String name = "";
	private String ctype = "";
	private String AmortMethod = "";

	public String getAmortMethod() {
		return AmortMethod;
	}

	public void setAmortMethod(String AmortMethod) {
		this.AmortMethod = AmortMethod;
	}

	public double getVal() {
		return val;
	}

	public void setVal(double val) {
		this.val = val;
	}

	public String getcType() {
		return ctype;
	}

	public void setcType(String ctype) {
		this.ctype = ctype;
	}

	/*
	 * public String getToadd() { return toadd; } public void setToadd(String toadd)
	 * { this.toadd =toadd; }
	 */
	public String getReqop() {
		return reqop;
	}

	public void setReqop(String reqop) {
		this.reqop = reqop;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
