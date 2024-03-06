package com.ebixcash.aayu.util;

public class StepPrinRecInputBean {
	private int		stepprin_frm  	= 0;
	public int getStepprin_frm() {
		return stepprin_frm;
	}
	public void setStepprin_frm(int stepprin_frm) {
		this.stepprin_frm = stepprin_frm;
	}
	public int getStepprin_to() {
		return stepprin_to;
	}
	public void setStepprin_to(int stepprin_to) {
		this.stepprin_to = stepprin_to;
	}
	public int getStepprin_in() {
		return stepprin_in;
	}
	public void setStepprin_in(int stepprin_in) {
		this.stepprin_in = stepprin_in;
	}
	public int getStepprin_by() {
		return stepprin_by;
	}
	public void setStepprin_by(int stepprin_by) {
		this.stepprin_by = stepprin_by;
	}
	public int getStepprin_freq() {
		return stepprin_freq;
	}
	public void setStepprin_freq(int stepprin_freq) {
		this.stepprin_freq = stepprin_freq;
	}
	public double getStepprin_rate() {
		return stepprin_rate;
	}
	public void setStepprin_rate(double stepprin_rate) {
		this.stepprin_rate = stepprin_rate;
	}
	private int 	stepprin_to 	= 0;
	private int 	stepprin_in 	= 0;// 0:% princ recovery amount,1:% princ recovery by %	
	private int 	stepprin_by  	= 0;
	private int 	stepprin_freq 	= 0;	
	private double	stepprin_rate	= 0.0d;
	

}
