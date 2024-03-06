package com.ebixcash.aayu.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AmortInstallmentResponse {
	@JsonProperty("NO")
	private String installment = "";
	@JsonProperty("CYLDT")
	private String cycleDate = "";
	@JsonProperty("RESTBAL")
	private double roundrestopenbal = 0.0d;
	@JsonProperty("OPBAL")
	private double roundopen = 0.0d;
	@JsonProperty("EMI")
	private double roundemi = 0.0d;
	@JsonProperty("INTEMI")
	private double roundinterest = 0.0d;
	@JsonProperty("PRIEMI")
	private double roundprincipal = 0.0d;
	@JsonProperty("CLSBAL")
	private double roundclose = 0.0d;
	@JsonProperty("ADJEMI")
	private double adj_installment = 0.0d;
	@JsonProperty("INTBEFPARTSKIP")
	private double skipIntBeforePartial = 0.0d;
	@JsonProperty("SKPINTOPEN")
	private double skipPartialIntOpen = 0.0d;
	@JsonProperty("SKPINT")
	private double skipPartialInt = 0.0d;
	@JsonProperty("SKPINTONINT")
	private double skipPartialIntonInt = 0.0d;
	@JsonProperty("SKPINTCLS")
	private double skipPartialIntClose = 0.0d;

	public AmortInstallmentResponse() {
	}

	public String getInstallment() {
		return installment;
	}

	public void setInstallment(String installment) {
		this.installment = installment;
	}

	public String getCycleDate() {
		return cycleDate;
	}

	public void setCycleDate(String cycleDate) {
		this.cycleDate = cycleDate;
	}

	public double getRoundrestopenbal() {
		return roundrestopenbal;
	}

	public void setRoundrestopenbal(double roundrestopenbal) {
		this.roundrestopenbal = roundrestopenbal;
	}

	public double getRoundopen() {
		return roundopen;
	}

	public void setRoundopen(double roundopen) {
		this.roundopen = roundopen;
	}

	public double getRoundemi() {
		return roundemi;
	}

	public void setRoundemi(double roundemi) {
		this.roundemi = roundemi;
	}

	public double getRoundinterest() {
		return roundinterest;
	}

	public void setRoundinterest(double roundinterest) {
		this.roundinterest = roundinterest;
	}

	public double getRoundprincipal() {
		return roundprincipal;
	}

	public void setRoundprincipal(double roundprincipal) {
		this.roundprincipal = roundprincipal;
	}

	public double getRoundclose() {
		return roundclose;
	}

	public void setRoundclose(double roundclose) {
		this.roundclose = roundclose;
	}

	public double getAdj_installment() {
		return adj_installment;
	}

	public void setAdj_installment(double adj_installment) {
		this.adj_installment = adj_installment;
	}

	public double getSkipIntBeforePartial() {
		return skipIntBeforePartial;
	}

	public void setSkipIntBeforePartial(double skipIntBeforePartial) {
		this.skipIntBeforePartial = skipIntBeforePartial;
	}

	public double getSkipPartialIntOpen() {
		return skipPartialIntOpen;
	}

	public void setSkipPartialIntOpen(double skipPartialIntOpen) {
		this.skipPartialIntOpen = skipPartialIntOpen;
	}

	public double getSkipPartialInt() {
		return skipPartialInt;
	}

	public void setSkipPartialInt(double skipPartialInt) {
		this.skipPartialInt = skipPartialInt;
	}

	public double getSkipPartialIntonInt() {
		return skipPartialIntonInt;
	}

	public void setSkipPartialIntonInt(double skipPartialIntonInt) {
		this.skipPartialIntonInt = skipPartialIntonInt;
	}

	public double getSkipPartialIntClose() {
		return skipPartialIntClose;
	}

	public void setSkipPartialIntClose(double skipPartialIntClose) {
		this.skipPartialIntClose = skipPartialIntClose;
	}

	public class AmortInstallmentResponseFirstSce {

		@JsonProperty("NO")
		private String installment = "";
		@JsonProperty("CYLDT")
		private String cycleDate = "";
		@JsonProperty("RESTBAL")
		private double roundrestopenbal = 0.0d;
		@JsonProperty("OPBAL")
		private double roundopen = 0.0d;
		@JsonProperty("EMI")
		private double roundemi = 0.0d;
		@JsonProperty("INTEMI")
		private double roundinterest = 0.0d;
		@JsonProperty("PRIEMI")
		private double roundprincipal = 0.0d;
		@JsonProperty("CLSBAL")
		private double roundclose = 0.0d;
		@JsonProperty("I_MNTH_INT")
		private double intmonthlyinterest = 0;
		@JsonProperty(value = "I_ACCI")
		private double intaccruedinterest = 0;
		@JsonProperty(value = "I_CLSBAL")
		private double intclosingBalance = 0;
		@JsonProperty(value = "I_PRIEMI")
		private double intprincipalEMI = 0;
		@JsonProperty(value = "I_INTEMI")
		private double intInterestEMI = 0;
		@JsonProperty(value = "I_OPBAL")
		private double intopeningBalance = 0;
		@JsonProperty(value = "MNTH_INT")
		private double monthlyinterest = 0.0d;
		@JsonProperty(value = "ADJEMI")
		private double adj_installment = 0.0d;
		@JsonProperty(value = "ACCI")
		private double accruedinterest = 0.0d;

		public double getIntmonthlyinterest() {
			return intmonthlyinterest;
		}

		public void setIntmonthlyinterest(double intmonthlyinterest) {
			this.intmonthlyinterest = intmonthlyinterest;
		}

		public double getIntaccruedinterest() {
			return intaccruedinterest;
		}

		public void setIntaccruedinterest(double intaccruedinterest) {
			this.intaccruedinterest = intaccruedinterest;
		}

		public double getIntclosingBalance() {
			return intclosingBalance;
		}

		public void setIntclosingBalance(double intclosingBalance) {
			this.intclosingBalance = intclosingBalance;
		}

		public double getIntprincipalEMI() {
			return intprincipalEMI;
		}

		public void setIntprincipalEMI(double intprincipalEMI) {
			this.intprincipalEMI = intprincipalEMI;
		}

		public double getIntInterestEMI() {
			return intInterestEMI;
		}

		public void setIntInterestEMI(double intInterestEMI) {
			this.intInterestEMI = intInterestEMI;
		}

		public double getIntopeningBalance() {
			return intopeningBalance;
		}

		public void setIntopeningBalance(double intopeningBalance) {
			this.intopeningBalance = intopeningBalance;
		}

		public double getMonthlyinterest() {
			return monthlyinterest;
		}

		public void setMonthlyinterest(double monthlyinterest) {
			this.monthlyinterest = monthlyinterest;
		}

		public double getAdj_installment() {
			return adj_installment;
		}

		public void setAdj_installment(double adj_installment) {
			this.adj_installment = adj_installment;
		}

		public double getAccruedinterest() {
			return accruedinterest;
		}

		public void setAccruedinterest(double accruedinterest) {
			this.accruedinterest = accruedinterest;
		}

		public String getInstallment() {
			return installment;
		}

		public void setInstallment(String installment) {
			this.installment = installment;
		}

		public String getCycleDate() {
			return cycleDate;
		}

		public void setCycleDate(String cycleDate) {
			this.cycleDate = cycleDate;
		}

		public double getRoundrestopenbal() {
			return roundrestopenbal;
		}

		public void setRoundrestopenbal(double roundrestopenbal) {
			this.roundrestopenbal = roundrestopenbal;
		}

		public double getRoundopen() {
			return roundopen;
		}

		public void setRoundopen(double roundopen) {
			this.roundopen = roundopen;
		}

		public double getRoundemi() {
			return roundemi;
		}

		public void setRoundemi(double roundemi) {
			this.roundemi = roundemi;
		}

		public double getRoundinterest() {
			return roundinterest;
		}

		public void setRoundinterest(double roundinterest) {
			this.roundinterest = roundinterest;
		}

		public double getRoundprincipal() {
			return roundprincipal;
		}

		public void setRoundprincipal(double roundprincipal) {
			this.roundprincipal = roundprincipal;
		}

		public double getRoundclose() {
			return roundclose;
		}

		public void setRoundclose(double roundclose) {
			this.roundclose = roundclose;
		}
	}

	public class AmortInstallmentResponseSecondSce {
		@JsonProperty("NO")
		private String installment = "";
		@JsonProperty("CYLDT")
		private String cycleDate = "";
		@JsonProperty("EMI")
		private double roundemi = 0.0d;
		@JsonProperty(value = "I_DEP")
		private double DepreciationFee = 0.0d;
		@JsonProperty(value = "I_FIN")
		private double FinancialFee = 0.0d;
		@JsonProperty(value = "I_ACCI")
		private double intaccruedinterest = 0;

		public double getDepreciationFee() {
			return DepreciationFee;
		}

		public void setDepreciationFee(double depreciationFee) {
			DepreciationFee = depreciationFee;
		}

		public double getFinancialFee() {
			return FinancialFee;
		}

		public void setFinancialFee(double financialFee) {
			FinancialFee = financialFee;
		}

		public double getIntaccruedinterest() {
			return intaccruedinterest;
		}

		public void setIntaccruedinterest(double intaccruedinterest) {
			this.intaccruedinterest = intaccruedinterest;
		}

		public String getInstallment() {
			return installment;
		}

		public void setInstallment(String installment) {
			this.installment = installment;
		}

		public String getCycleDate() {
			return cycleDate;
		}

		public void setCycleDate(String cycleDate) {
			this.cycleDate = cycleDate;
		}

		public double getRoundemi() {
			return roundemi;
		}

		public void setRoundemi(double roundemi) {
			this.roundemi = roundemi;
		}
	}

}
