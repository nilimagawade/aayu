
package com.ebixcash.aayu.util;

/**
 * Bean Object For Balloon Payment input
 * @author sheetal.parihar
 * @since 1.0
 */
public class BPInputBean {

	private int BP_Month 		= 0;
	private int BP_Adjust		= 0;
	private double BP_Amount	= 0.0d;
	public int getBP_Month() {
		return BP_Month;
	}
	public void setBP_Month(int month) {
		BP_Month = month;
	}
	public int getBP_Adjust() {
		return BP_Adjust;
	}
	public void setBP_Adjust(int adjust) {
		BP_Adjust = adjust;
	}
	public double getBP_Amount() {
		return BP_Amount;
	}
	public void setBP_Amount(double amount) {
		BP_Amount = amount;
	}

}
