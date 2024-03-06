package com.ebixcash.aayu.model;

import java.util.HashMap;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Bean Object For Amortization Output
 * @author nilesh kedia
 * @author sheetal.parihar
 * @since 1.0 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Component
public class AmortOutputBean {
	
	private String cycleDate;
	private double openingBalance;
	private double restopeningBalance;
	private double emiAmount;
	private double interestEMI;
	private double principalEMI;
	private double closingBalance;
	@JsonProperty(value = "EMI")
	private double roundemi;
	@JsonProperty(value = "PRIEMI")
	private double roundprincipal;
	@JsonProperty(value = "INTEMI")
	private double roundinterest;
	@JsonProperty(value = "OPBAL")
	private double roundopen;
	@JsonProperty(value = "CLSBAL")
	private double roundclose;
	@JsonProperty(value = "RESTBAL")
	private double roundrestopenbal;
	
	@JsonProperty(value = "SKPINT")
	private double skipPartialInt;
	@JsonProperty(value = "SKPINTONINT")
	private double skipPartialIntonInt;
	@JsonProperty(value = "SKPINTOPEN")
	private double skipPartialIntOpen;
	@JsonProperty(value = "SKPINTCLS")
	private double skipPartialIntClose;
	@JsonProperty(value = "INTBEFPARTSKIP")
	private double skipIntBeforePartial;

	@JsonProperty(value = "NO")
	private String installment;

	
	private HashMap component = null;
	private HashMap comp = null;
	@JsonProperty(value = "ADJEMI")
	private double  adj_installment;
	@JsonProperty(value = "ACCI")
	private double  accruedinterest;

	private double  monthlyinterest;
	@JsonProperty(value = "EOMDT")
	private String endofMonthDate;
	
	@JsonProperty(value = "I_ACCI")
	private double intaccruedinterest;
	private double intmonthlyinterest;
	@JsonProperty(value = "I_OPBAL")
	private double intopeningBalance;
	@JsonProperty(value = "I_INTEMI")
	private double intInterestEMI;
	@JsonProperty(value = "I_PRIEMI")
	private double intprincipalEMI;
	@JsonProperty(value = "I_CLSBAL")
	private double intclosingBalance;
	@JsonProperty(value = "I_FIN")
	private double FinancialFee;
	@JsonProperty(value = "I_DEP")
	private double DepreciationFee;
	
	private double tds;
	private double round_tds;
	
	
	public AmortOutputBean(){}

	public void setOpeningBalance(double amt){this.openingBalance=amt;}
	public double getOpeningBalance(){return openingBalance;}
	public void setRestOpeningBalance(double amt){this.restopeningBalance=amt;}
	public double getRestOpeningBalance(){return restopeningBalance;}
	public void setEmiAmount(double amt){this.emiAmount=amt;}
	public double getEmiAmount(){return emiAmount;}
	public void setInterestEMI(double amt){this.interestEMI=amt;}
	public double getInterestEMI(){return interestEMI;}
	public void setPrincipalEMI(double amt){this.principalEMI=amt;}
	public double getPrincipalEMI(){return principalEMI;}
	public void setClosingBalance(double amt){this.closingBalance=amt;}
	public double getClosingBalance(){return closingBalance;}
	public void setCycleDate(String date){this.cycleDate=date;}
	public String getCycleDate(){return cycleDate;}

	public void setRoundEMI(double amt){this.roundemi=amt;}
	public double getRoundEMI(){return roundemi;}

	public void setRoundPrincipal(double amt){this.roundprincipal=amt;}
	public double getRoundPrincipal(){return roundprincipal;}
	public void setRoundInterest(double amt){this.roundinterest=amt;}
	public double getRoundInterest(){return roundinterest;}
	public void setRoundOpen(double amt){this.roundopen=amt;}
	public double getRoundOpen(){return roundopen;}
	public void setRoundClose(double amt){this.roundclose=amt;}
	public double getRoundClose(){return roundclose;}
	public void setRoundRestOpenBal(double amt){this.roundrestopenbal=amt;}
	public double getRoundRestOpenBal(){return roundrestopenbal;}
	public String getInstallment() {return installment;}
	public void setInstallment(String installment) {this.installment = installment;}

	public HashMap getComponent() {
		return component;
	}

	public void setComponent(HashMap component) {
		this.component = component;
	}
	
	public HashMap getComp() {
		return comp;
	}

	public void setComp(HashMap comp) {
		this.comp = comp;
	}
	
	
	public double getAdj_installment() {
		return adj_installment;
	}

	public void setAdj_installment(double adj_installment) {
		this.adj_installment = adj_installment;
	}
	public double getAccruedInterest(){return accruedinterest;}
	public void setAccruedInterest(double accruedinterest){this.accruedinterest=accruedinterest;}
	public double getMonthlyInterest(){return monthlyinterest;}
	public void setMonthlyInterest(double monthlyinterest){this.monthlyinterest=monthlyinterest;}
	public String getEndofMonthDate(){return endofMonthDate;}
	public void setEndofMonthDate(String endofMonthDate){this.endofMonthDate=endofMonthDate;}
	
	/////
	public double getIntAccruedInterest(){return intaccruedinterest;}
	public void setIntAccruedInterest(double intaccruedinterest){this.intaccruedinterest=intaccruedinterest;}

	public double getIntMonthlyInterest(){return intmonthlyinterest;}
	public void setIntMonthlyInterest(double intmonthlyinterest){this.intmonthlyinterest=intmonthlyinterest;}

	
	public double getIntOpeningBalance(){return intopeningBalance;}
	public void setIntOpeningBalance(double intopeningBalance){this.intopeningBalance=intopeningBalance;}

	
	public double getIntInterestEMI(){return intInterestEMI;}
	public void setIntInterestEMI(double intInterestEMI){this.intInterestEMI=intInterestEMI;}
	
	public double getIntPrincipalEMI(){return intprincipalEMI;}
	public void setIntPrincipalEMI(double intprincipalEMI){this.intprincipalEMI=intprincipalEMI;}
	
	public double getIntClosingBalance(){return intclosingBalance;}
	public void setIntClosingBalance(double intclosingBalance){this.intclosingBalance=intclosingBalance;}
	public double getFinancialFee(){return FinancialFee;}
	public void setFinancialFee(double FinancialFee){this.FinancialFee=FinancialFee;}
	public double getDepreciationFee(){return DepreciationFee;}
	public void setDepreciationFee(double DepreciationFee){this.DepreciationFee=DepreciationFee;}

	public double getTDS(){return round_tds;}
	public void setTDS(double tds){this.round_tds=tds;}
	public double getRoundTDS(){return round_tds;}
	public void setRoundTDS(double round_tds){this.round_tds=round_tds;}
	public double getSkipPartialInt() {
		return skipPartialInt;
	}

	public void setSkipPartialInt(double skipPartialInt) {
		this.skipPartialInt = skipPartialInt;
	}

	public double getSkipPartialIntOpen() {
		return skipPartialIntOpen;
	}

	public void setSkipPartialIntOpen(double skipPartialIntOpen) {
		this.skipPartialIntOpen = skipPartialIntOpen;
	}

	public double getSkipPartialIntClose() {
		return skipPartialIntClose;
	}

	public void setSkipPartialIntClose(double skipPartialIntClose) {
		this.skipPartialIntClose = skipPartialIntClose;
	}
	public double getSkipPartialIntonInt() {
		return skipPartialIntonInt;
	}

	public void setSkipPartialIntonInt(double skipPartialIntonInt) {
		this.skipPartialIntonInt = skipPartialIntonInt;
	}
	
	public double getSkipIntBeforePartial() {
		return skipIntBeforePartial;
	}

	public void setSkipIntBeforePartial(double skipIntBeforePartial) {
		this.skipIntBeforePartial = skipIntBeforePartial;
	}
	
	@Override
	public String toString() {
		return "AmortOutputBean [cycleDate=" + cycleDate + ", openingBalance=" + openingBalance
				+ ", restopeningBalance=" + restopeningBalance + ", emiAmount=" + emiAmount + ", interestEMI="
				+ interestEMI + ", principalEMI=" + principalEMI + ", closingBalance=" + closingBalance + ", roundemi="
				+ roundemi + ", roundprincipal=" + roundprincipal + ", roundinterest=" + roundinterest + ", roundopen="
				+ roundopen + ", roundclose=" + roundclose + ", roundrestopenbal=" + roundrestopenbal
				+ ", skipPartialInt=" + skipPartialInt + ", skipPartialIntonInt=" + skipPartialIntonInt
				+ ", skipPartialIntOpen=" + skipPartialIntOpen + ", skipPartialIntClose=" + skipPartialIntClose
				+ ", skipIntBeforePartial=" + skipIntBeforePartial + ", installment=" + installment + ", component="
				+ component + ", comp=" + comp + ", adj_installment=" + adj_installment + ", accruedinterest="
				+ accruedinterest + ", monthlyinterest=" + monthlyinterest + ", endofMonthDate=" + endofMonthDate
				+ ", intaccruedinterest=" + intaccruedinterest + ", intmonthlyinterest=" + intmonthlyinterest
				+ ", intopeningBalance=" + intopeningBalance + ", intInterestEMI=" + intInterestEMI
				+ ", intprincipalEMI=" + intprincipalEMI + ", intclosingBalance=" + intclosingBalance
				+ ", FinancialFee=" + FinancialFee + ", DepreciationFee=" + DepreciationFee + ", tds=" + tds
				+ ", round_tds=" + round_tds + "]";
	}

}
