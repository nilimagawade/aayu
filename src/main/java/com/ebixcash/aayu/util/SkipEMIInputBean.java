
package com.ebixcash.aayu.util;

/**
 * Bean Object For Skip EMI Input
 * @author sheetal.parihar
 * @since 1.0
 */
public class SkipEMIInputBean
{

	private int skipadjust = 0;
	private int frm_month  = 0;
	private int no_month   = 0;
	private String skip_capital = "";
	private String skipPartPay = ""; 
	private String skipPartPayIn = "";
	private double skipPartInterest = 0.0;

	public double getSkipPartInterest() {
		return skipPartInterest;
	}
	public void setSkipPartInterest(double skipPartInterest) {
		this.skipPartInterest = skipPartInterest;
	}
	public String getSkipPartPayIn() {
		return skipPartPayIn;
	}
	public void setSkipPartPayIn(String skipPartPayIn) {
		this.skipPartPayIn = skipPartPayIn;
	}
	public String getSkipPartPay() {
		return skipPartPay;
	}
	public void setSkipPartPay(String skipPartPay) {
		this.skipPartPay = skipPartPay;
	}
	public int getSkipadjust() {
		return skipadjust;
	}
	public void setSkipadjust(int skipadjust) {
		this.skipadjust = skipadjust;
	}
	public int getFrm_month() {
		return frm_month;
	}
	public void setFrm_month(int frm_month) {
		this.frm_month = frm_month;
	}
	public int getNo_month() {
		return no_month;
	}
	public void setNo_month(int no_month) {
		this.no_month = no_month;
	}
	public String getSkip_capital() {
		return skip_capital;
	}
	public void setSkip_capital(String skip_capital) {
		this.skip_capital = skip_capital;
	}


}
