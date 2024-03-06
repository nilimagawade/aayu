package com.ebixcash.aayu.util;

public class AmortOverviewBean {
	private double total_emi = 0.0d;
	private double total_interest = 0.0d;
	private double total_principal = 0.0d;
	private double total_round_emi = 0.0d;
	private double total_round_interest = 0.0d;
	private double total_round_principal = 0.0d;
	private String disburse_date = "";
	private double advance_EMI = 0.0d;
	private double bpiamount = 0.0d;
	private double totalInstallments = 0.0d;
	private double totalInterestRate = 0.0d;

	public AmortOverviewBean() {
	}

	public double getTotalEMI() {
		return total_emi;
	}

	public void setTotalEMI(double i) {
		total_emi = i;
	}

	public double getTotalInterest() {
		return total_interest;
	}

	public void setTotalInterest(double i) {
		total_interest = i;
	}

	public double getInterestEMI() {
		return total_interest;
	}

	public void setInterestEMI(double i) {
		total_interest = i;
	}

	public double getTotalPrincipal() {
		return total_principal;
	}

	public void setTotalPrincipal(double i) {
		total_principal = i;
	}

	public double getIntPrincipalEMI() {
		return total_principal;
	}

	public void setIntPrincipalEMI(double i) {
		total_principal = i;
	}

	public double getTotalRoundEMI() {
		return total_round_emi;
	}

	public void setTotalRoundEMI(double i) {
		total_round_emi = i;
	}

	public double getTotalRoundInterest() {
		return total_round_interest;
	}

	public void setTotalRoundInterest(double i) {
		total_round_interest = i;
	}

	public double getTotalRoundPrincipal() {
		return total_round_principal;
	}

	public void setTotalRoundPrincipal(double i) {
		total_round_principal = i;
	}

	public String getDisburseDate() {
		return disburse_date;
	}

	public void setDisburseDate(String i) {
		disburse_date = i;
	}

	public double getAdvanceEMI() {
		return advance_EMI;
	}

	public void setAdvanceEMI(double i) {
		advance_EMI = i;
	}

	public double getBPIAmount() {
		return bpiamount;
	}

	public void setBPIAmount(double i) {
		bpiamount = i;
	}

	public double getTotalInstallments() {
		return totalInstallments;
	}

	public void setTotalInstallments(double i) {
		totalInstallments = i;
	}

	public double getInterestRate() {
		return totalInterestRate;
	}

	public void setInterestRate(double i) {
		totalInterestRate = i;
	}
}
