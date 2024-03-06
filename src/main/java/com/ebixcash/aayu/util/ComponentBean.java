package com.ebixcash.aayu.util;

public class ComponentBean {

	private int basis = 0;
	private String param = "";
	private double value = 0.0d;
	private int calc = 0;
	private String dependent = "";
	private String type = "";
	private double percentvalue = 0.0d;
	private String range = "";
	private String feename = "";
	// private String flag = "";

	public int getBasis() {
		return basis;
	}

	public void setBasis(int basis) {
		this.basis = basis;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDependent() {
		return dependent;
	}

	public void setDependent(String dependent) {
		this.dependent = dependent;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public int getCalc() {
		return calc;
	}

	public void setCalc(int calc) {
		this.calc = calc;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

	public double getPercentvalue() {
		return percentvalue;
	}

	public void setPercentvalue(double percentvalue) {
		this.percentvalue = percentvalue;
	}

	public String getFeename() {
		return feename;
	}

	public void setFeename(String feename) {
		this.feename = feename;
	}
	/*
	 * public String getFlag() { return flag; } public void setFlag(String flag) {
	 * this.flag = flag; }
	 */
}
