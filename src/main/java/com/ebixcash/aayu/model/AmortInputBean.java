package com.ebixcash.aayu.model;

import java.util.ArrayList;

import com.ebixcash.aayu.constant.AmortConstant;
import com.ebixcash.aayu.util.StepEMIInputBean;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Bean Object For Amortization Input
 * 
 * @author prabhanshu,sharma
 * @author Nilima Gawade
 * @since 1.0
 */
//@SuppressWarnings("unchecked")

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AmortInputBean {

	private double open_amount = 0.0d;

	@JsonProperty(value = "P")
	private double loan_amount = 0.0d;

	private double tot_round_int78 = 0.0d;
	private double add_int = 0.0d;
	private double loan_amount_ori = 0.0d;
	@JsonProperty(value = "T")
	private double tenor = 0.0d;
	private double tenor78 = 0.0d;
	private double open_tenor = 0.0d;
	private double totalinstallment = 0.0d;
	private double monthsperiod = 0.0d;
	@JsonProperty(value = "R")
	private double rate = 0.0d;
	private double originalRate = 0.0d;
	private double real_interest_rate = 0.0d;
	private double real_emi = 0.0d;
	private double real_loanamount = 0.0d;
	@JsonProperty(value = "emi")
	private double input_emi = 0;
	@JsonProperty(value = "PRECISION_VALUE")
	private double precision = 0.0d;
	@JsonProperty(value = "BP_Lastpayamt")
	private double BP_Lastpayamt = 0.0d;
	@JsonProperty(value = "EQUATEDPRINFRQ")
	private double EqPrinFreq = 0.0d;
	private double totaleqtprincinstment = 0.0d;
	@JsonProperty(value = "I_P")
	private double int_principal = 0.0d;
	@JsonProperty(value = "RV")
	private double RV = 0.0d;
	@JsonProperty(value = "T_IN")
	private int tenor_in = 0;
	private int tenorfactor = 0;
	@JsonProperty(value = "AT")
	private int amort_type = 0;
	@JsonProperty(value = "IT")
	private int interest_type = 0;
	@JsonProperty(value = "REST")
	private int rest = 0;
	@JsonProperty(value = "F")
	private int frequency = 0;
	private int interestFrequency = 0;
	private double ThreshValue = 0.0d;

	private int frequency78 = 0;
	private int frequencyPeriod = 0;
	private int frequencyfactor = 0;
	private int yearfactor = 0;
	private int restfactor = 0;
	@JsonProperty(value = "INST")
	private int installment_type = 0;
	@JsonProperty(value = "kse_advEMI")
	private int kse_advEMI = 0;
	@JsonProperty(value = "n_advEMI")
	private int n_advEMI = 0;
	@JsonProperty(value = "adj_advEMI")
	private int adj_advEMI = 0;
	@JsonProperty(value = "EMI_ro")
	private int EMI_ro = 0;
	@JsonProperty(value = "EMI_unit_ro")
	private int EMI_unit_ro = 1;
	@JsonProperty(value = "EMI_ro_part")
	private int EMI_ro_part = 0;
	@JsonProperty(value = "EMI_ro_to")
	private int EMI_ro_to = 0;
	@JsonProperty(value = "Adjust_Option")
	private int Adjust_Option = 0;
	@JsonProperty(value = "Others_ro")
	private int Others_ro = 0;
	@JsonProperty(value = "Others_unit_ro")
	private int Others_unit_ro = 1;
	@JsonProperty(value = "Others_ro_to")
	private int Others_ro_to = 0;
	@JsonProperty(value = "Others_ro_part")
	private int Others_ro_part = 0;
	@JsonProperty(value = "compFreq")
	private int compFreq = 0;
	@JsonProperty(value = "BPI_Recovery")
	private int BPI_Recovery = 0;
	//private int Step_EMI = 0;
	private int BP = 0;
	private int Skip_EMI = 0;
	private int NPVROWS = 0;
	private int Adjust_Rate = 0;
	@JsonProperty(value = "Int_Only")
	private int Int_Only = 0;
	@JsonProperty(value = "IB")
	private int Interest_Basis = 0;
	@JsonProperty(value = "EMI_IB")
	private int Interest_Basis_Emi = 0;
	private int Interest_Basis_Emi2 = 0;
	@JsonProperty(value = "INT_IB")
	private int Int_Interest_Basis = 0;
	private int adjterm = 0;
	private int P_STEP = 0;
	private int Fees = 0;
	private int recalMonth = 0;
	private int EmiOPValue = 0;
	private int OnbasisValue = 0;
	private int Adj = 0;

	@JsonProperty(value = "EMI_ADJ")
	private EMI_ADJ emi_adj;

	@JsonProperty(value = "BPI_B")
	private int BPI_Method = 0;

	private double AdjRate = 0;
	private double AccumulInt = 0;
	private double TotAccumulInt = 0;
	@JsonProperty(value = "TDS_PER")
	private double tdsPercentage = 0;

	//@JsonProperty(value = "adj_advEMI")
	//private int AdvEMIAdj = 0;
	@JsonProperty(value = "same_advEMI")
	private int SameAdvEMI = 0;
	@JsonProperty(value = "ADDINT")
	private String ADDINT;

	@JsonProperty(value = "BPI_CAP_YN")
	private String bpi_cap_yn;
	
	@JsonProperty(value = "Step_EMI")
	private StepEMIInputBean Step_EMI;

	public String getBpi_cap_yn() {
		return bpi_cap_yn;
	}

	public void setBpi_cap_yn(String bpi_cap_yn) {
		this.bpi_cap_yn = bpi_cap_yn;
	}

	public String getADDINT() {
		return ADDINT;
	}

	public void setADDINT(String aDDINT) {
		ADDINT = aDDINT;
	}

	private boolean isInterestRatepresent = false;
	private boolean isTenorpresent = false;
	private boolean isLoanAmountpresent = false;
	private boolean isEMIpresent = false;
	private boolean isEMIBasis = false;
	private boolean isEquPriFreqpresent = false;
	private boolean isStepAdjust = false;
	private boolean isSkipAdjust = false;
	private boolean isBPAdjust = false;
	private boolean isCapitalize = false;
	private boolean isAdvEmiCalculated = false;
	@JsonProperty(value = "DEDUCT_TDS")
	private boolean deductTDS = false;

	private boolean EOMAmort = false;

	private boolean SOMAmort = false;

	@JsonProperty(value = "SOM")
	private String som;
	@JsonProperty(value = "EOM")
	private String eom;

	private boolean isAddInt = false;
	private String real_lid = "";
	@JsonProperty(value = "DOD")
	private String DOD = "";
	@JsonProperty(value = "DOC")
	private String DOC = "";
	private String DOFP = "";
	@JsonProperty(value = "LAST_INST_RO")
	private String lastInstlRo = "";
	@JsonProperty(value = "REDUCE_BPI")
	private String reduceBpi = "";

	@NotBlank(message = "payFirst is mandatory")
	@JsonProperty(value = "PAY_FIRST")
	private String payFirst = "";
	@JsonProperty(value = "INT_AMORT")
	private String intAmort = "";
	private String apply_adv = "N";
	private String apply_adjrate = "N";
	private String PCD = "";
	private String CCD = "";
	@JsonProperty(value = "AdvEMI_FIRST_INST")
	private String AdvEMI_FIRST_INST = "";
	@JsonProperty(value = "dateformat")
	private String dateformat = AmortConstant.dateformat;
	private String Rule78 = "";
	@JsonProperty(value = "PIPESEPARATED")
	private String pipeseparted = "";
	@JsonProperty(value = "LAST_EMI_CONST")
	private String lastEMIAdj = "";
	@JsonProperty(value = "St_Date")
	private String dtstart = "";
	private String bpicapyn = "";
	@JsonProperty(value = "End_Date")
	private String dtend = "";
	private String AmortMethod = "";
	@JsonProperty(value = "INT_CAL")
	private String IntCalMethod = "";
	private ArrayList arrBP = null;
	private ArrayList arrEMI_ADJ = null;
	private ArrayList arrFees = null;
	private ArrayList arrROWS = null;
	private ArrayList arrStepEMI = null;
	private ArrayList arrSkipEMI = null;
	private ArrayList arrComp = null;
	private ArrayList arrStepPer = null;
	private ArrayList arrAdjustRate = null;

	// changes for % principle recovery
	private int stepprin = 0;
	private ArrayList arrPerPrinRcvr = null;
	@JsonProperty(value = "MAX_EMI")
	public String max_emi = "N";
	@JsonProperty(value = "CURR_EMI")
	public String curr_emi = "N";
	@JsonProperty(value = "interestrate")
	public double fixedRateofInterest = 0.0;

	/* set get functions for the creation of new row for last installment */
	public int getEmiOPValue() {
		return EmiOPValue;
	}

	public void setEmiOPValue(int EmiOPValue) {
		this.EmiOPValue = EmiOPValue;
	}

	public int getOnbasisValue() {
		return OnbasisValue;
	}

	public void setOnbasisValue(int OnbasisValue) {
		this.OnbasisValue = OnbasisValue;
	}

	public double getThreshValue() {
		return ThreshValue;
	}

	public void setThreshValue(double ThreshValue) {
		this.ThreshValue = ThreshValue;
	}

	public int getAdj() {
		return Adj;
	}

	public void setAdj(int adj) {
		Adj = adj;
	}

	public String getSom() {
		return som;
	}

	public void setSom(String som) {
		this.som = som;
	}

	public String getEom() {
		return eom;
	}

	public void setEom(String eom) {
		this.eom = eom;
	}

	public double getFixedRateofInterest() {
		return fixedRateofInterest;
	}

	public void setFixedRateofInterest(double fixedRateofInterest) {
		this.fixedRateofInterest = fixedRateofInterest / 100;
		;
	}

	public String getCurr_emi() {
		return curr_emi;
	}

	public void setCurr_emi(String curr_emi) {
		this.curr_emi = curr_emi;
	}

	public int getStepprin() {
		return stepprin;
	}

	public void setStepprin(int stepprin) {
		this.stepprin = stepprin;
	}

	public ArrayList getArrPerPrinRcvr() {
		return arrPerPrinRcvr;
	}

	public void setArrPerPrinRcvr(ArrayList arrPerPrinRcvr) {
		this.arrPerPrinRcvr = arrPerPrinRcvr;
	}

	//
	public int getCompoundFreq() {
		return compFreq;
	}

	public void setCompoundFreq(int f) {
		this.compFreq = f;
	}

	public int getAdjust_Rate() {
		return Adjust_Rate;
	}

	public void setAdjust_Rate(int adjust_Rate) {
		this.Adjust_Rate = adjust_Rate;
	}

	public ArrayList getArrAdjustRate() {
		return arrAdjustRate;
	}

	public void setArrAdjustRate(ArrayList arrAdjustRate) {
		this.arrAdjustRate = arrAdjustRate;
	}

	/*
	 * public int getStepEMI() { return Step_EMI; }
	 * 
	 * public void setStepEMI(int stepEMI) { this.Step_EMI = stepEMI; }
	 */

	public AmortInputBean() {
	}

	public double getLoanAmount() {
		return loan_amount;
	}

	public void setLoanAmount(double i) {
		this.loan_amount = i;
	}

	public double getTotalInterest_78() {
		return tot_round_int78;
	}

	public void setgetTotalInterest_78(double i) {
		this.tot_round_int78 = i;
	}

	public double getAddIntAmount() {
		return add_int;
	}

	public void setAddIntAmount(double i) {
		this.add_int = i;
	}

	public double getLoanAmountOri() {
		return loan_amount_ori;
	}

	public void setLoanAmountOri(double i) {
		this.loan_amount_ori = i;
	}

	public double getTenor() {
		return tenor;
	}

	public void setTenor(double i) {
		this.tenor = i;
	}

	public double getTenor78() {
		return tenor78;
	}

	public void setTenor78(double i) {
		this.tenor78 = i;
	}

	public double getOpenTenor() {
		return open_tenor;
	}

	public void setOpenTenor(double i) {
		this.open_tenor = i;
	}

	public double getTotalInstallment() {
		return totalinstallment;
	}

	public void setTotalInstallment(double tenor, double tenor_in, double tenorfactor, double frequency) {
		if (tenor_in != AmortConstant.tenor_inyear)
			tenor = tenor / tenorfactor;
		this.totalinstallment = tenor * frequency;
	}

	public double getInterestRate() {
		return rate;
	}

	public double getTotalInterest() {
		return rate;
	}

	public double getOriginalRate() {
		return originalRate;
	}

	public void setOriginalRate(double originalRate) {
		this.originalRate = originalRate * 100;
	}

	public void setInterestRate(double i) {

		this.rate = i / 100;

	}

	public void setTotalInterest(double i) {

		this.rate = i / 100;

	}

	public String getDateOfDisbursement() {
		return DOD;
	}

	public void setDateOfDisbursement(String i) {
		this.DOD = i;
	}

	public String getDateOfCycle() {
		return DOC;
	}

	public void setDateOfCycle(String i) {
		this.DOC = i;
	}

	public String getDateOfFirstPayment() {
		return DOFP;
	}

	public void setDateOfFirstPayment(String i) {
		this.DOFP = i;
	}

	public int getAmortType() {
		return amort_type;
	}

	public void setAmortType(int i) {
		this.amort_type = i;
	}

	public int getInterestType() {
		return interest_type;
	}

	public void setInterestType(int i) {
		this.interest_type = i;
	}

	public int getRepaymentFrequency() {
		return frequency;
	}

	public void setRepaymentFrequency(int i) {
		this.frequency = i;
		setMonthsPeriod(i);
	}

	public int getInterestFrequency() {
		return interestFrequency;
	}

	public void setInterestFrequency(int interestFrequency) {
		this.interestFrequency = interestFrequency;
	}

	public int getRepaymentFrequency78() {
		return frequency78;
	}

	public void setRepaymentFrequency78(int i) {
		this.frequency78 = i;

	}

	public int getInstallmentType() {
		return installment_type;
	}

	public void setInstallmentType(int i) {
		this.installment_type = i;
	}

	public int getSameAdvEMI() {
		return kse_advEMI;
	}

	public void setSameAdvEMI(int i) {
		this.kse_advEMI = i;
	}

	public int getNoOfAdvEMI() {
		return n_advEMI;
	}

	public void setNoOfAdvEMI(int i) {
		this.n_advEMI = i;
	}

	public int getAdjustAdvEMIAt() {
		return adj_advEMI;
	}

	public void setAdjustAdvEMIAt(int i) {
		this.adj_advEMI = i;
	}

	public String getCurrentCycleDate() {
		return CCD;
	}

	public void setCurrentCycleDate(String i) {
		setPreviousCycleDate(this.CCD);
		this.CCD = i;
	}

	public String getPreviousCycleDate() {
		return PCD;
	}

	public void setPreviousCycleDate(String i) {
		this.PCD = i;
	}

	public String getRule78() {
		return Rule78;
	}

	public void setRule78(String rule78) {
		this.Rule78 = rule78;
	}

	public int getBPI_Recovery() {
		return BPI_Recovery;
	}

	public void setBPI_Recovery(int recovery) {
		this.BPI_Recovery = recovery;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\nLoan Amount:").append(loan_amount);
		buffer.append("\nTenor:").append(tenor);
		buffer.append("\nInterest Rate:").append(rate);
		buffer.append("\nDisbursement Date:").append(DOD);
		buffer.append("\nCycle Date:").append(DOC);
		buffer.append("\nRepayment Frequency:").append(frequency);
		buffer.append("\nRest:").append(rest);
		buffer.append("\nAmort Type:").append(amort_type);
		buffer.append("\nInterest Type:").append(interest_type);
		buffer.append("\nInstallment Type:").append(installment_type);
		buffer.append("\nRounding Off:").append(EMI_ro);
		buffer.append("\nRounding Unit:").append(EMI_unit_ro);
		buffer.append("\nRule78:").append(Rule78);
		buffer.append("\nBPIRecovery:").append(BPI_Recovery);
		buffer.append("\nStep_EMI:").append(Step_EMI);
		buffer.append("\n arr of step emi:").append(arrStepEMI);
		buffer.append("\nSkip_EMI:").append(Skip_EMI);
		buffer.append("\n arr of skip emi:").append(arrSkipEMI);
		buffer.append("\n Interest Only:").append(Int_Only);
		buffer.append("\n BP EMI:").append(BP);
		buffer.append("\n arr of bp emi:").append(arrBP);
		buffer.append("\n Adjust Rate:").append(Adjust_Rate);
		buffer.append("\n arr of adjust rate:").append(arrAdjustRate);
		return buffer.toString();
	}

	public ArrayList getArrStepEMI() {
		return arrStepEMI;
	}

	public void setArrStepEMI(ArrayList arrStepEMI) {
		this.arrStepEMI = arrStepEMI;
	}

	public ArrayList getArrSkipEMI() {
		return arrSkipEMI;
	}

	public void setArrSkipEMI(ArrayList arrSkipEMI) {
		this.arrSkipEMI = arrSkipEMI;
	}

	public int getSkip_EMI() {
		return Skip_EMI;
	}

	public void setSkip_EMI(int skip_EMI) {
		this.Skip_EMI = skip_EMI;
	}

	public int getInt_Only() {
		return Int_Only;
	}

	public void setInt_Only(int int_Only) {
		this.Int_Only = int_Only;
	}

	public int getBP() {
		return BP;
	}

	public void setBP(int bp) {
		this.BP = bp;
	}

	public ArrayList getArrBP() {
		return arrBP;
	}

	public void setArrBP(ArrayList arrBP) {
		this.arrBP = arrBP;
	}

	public void setArrEMI_ADJ(ArrayList arrEMI_ADJ) {
		this.arrEMI_ADJ = arrEMI_ADJ;
	}

	public ArrayList getArrComp() {
		return arrComp;
	}

	public void setArrComp(ArrayList arrComp) {
		this.arrComp = arrComp;
	}

	public String getDateformat() {
		return dateformat;
	}

	public void setDateformat(String dateformat) {
		this.dateformat = dateformat;
	}

	public double getMonthsPeriod() {
		return monthsperiod;
	}

	public int getFrequencyFactor() {
		return frequencyfactor;
	}

	public int getFrequencyPeriod() {
		return frequencyPeriod;
	}

	public String getlastInstlRo() {
		return lastInstlRo;
	}

	public void setlastInstlRo(String i) {
		this.lastInstlRo = i;
	}

	public String getReduceBpi() {
		return reduceBpi;
	}

	public void setReduceBpi(String i) {
		this.reduceBpi = i;
	}

	public String getPayFirst() {
		return payFirst;
	}

	public void setPayFirst(String i) {
		this.payFirst = i;
	}

	public boolean isEOMAmort() {
		return EOMAmort;
	}

	public void setEOMAmort(String i) {
		if (i != null && i.equals("Y"))
			this.EOMAmort = true;
		else
			this.EOMAmort = false;
	}

	public boolean isSOMAmort() {
		return SOMAmort;
	}

	public void setSOMAmort(String i) {
		if (i != null && i.equals("Y"))
			this.SOMAmort = true;
		else
			this.SOMAmort = false;
	}

	public double getPrecision() {
		return precision;
	}

	public void setPrecision(double i) {
		this.precision = i;
	}

	//////////////
	public void setMonthsPeriod(int i) {
		switch (i) {
		case 1:
			this.monthsperiod = 12;
			this.frequencyPeriod = 2;
			this.frequencyfactor = 12;
			// this.tenorfactor = 12;
			break;
		case 2:
			this.monthsperiod = 6;
			this.frequencyPeriod = 2;
			this.frequencyfactor = 6;
			// this.tenorfactor = 6;
			break;
		case 3:
			this.monthsperiod = 3;
			this.frequencyPeriod = 2;
			this.frequencyfactor = 4;
			// this.tenorfactor = 6;
			break;
		case 4:
			this.monthsperiod = 3;
			this.frequencyPeriod = 2;
			this.frequencyfactor = 3;
			// this.tenorfactor = 3;
			break;
		case 6:
			this.monthsperiod = 2;
			this.frequencyPeriod = 2;
			this.frequencyfactor = 2;
			// this.tenorfactor = 2;
			break;
		case 12:
			this.monthsperiod = 1;
			this.frequencyPeriod = 2;
			this.frequencyfactor = 1;
			// this.tenorfactor = 1;
			break;

		// for weeks
		case 26:
			this.monthsperiod = 0.5;
			this.frequencyPeriod = 4;
			this.frequencyfactor = 2;
			// this.tenorfactor = 2;
			break;
		case 52:
			this.monthsperiod = 0.25;
			this.frequencyPeriod = 4;
			this.frequencyfactor = 1;
			// this.tenorfactor = 1;
			break;
		case 360:
			this.monthsperiod = 1;
			this.frequencyPeriod = 3;
			this.frequencyfactor = 1;
			// this.tenorfactor = 1;
			break;
		default:

		}
	}

	public int getRest() {
		return rest;
	}

	public void setRest(int i) {
		this.rest = i;
		setRestFactor(i);
	}

	public int getRestFactor() {
		return restfactor;
	}

	public void setRestFactor(int rest) {
		switch (rest) {
		case 1:
			this.restfactor = 12;
			break;
		case 2:
			this.restfactor = 6;
			break;
		case 4:
			this.restfactor = 3;
			break;
		case 6:
			this.restfactor = 2;
			break;
		case 12:
			this.restfactor = 1;
			break;
		case 52:
			this.restfactor = 1;
			break;
		case 26:
			this.restfactor = 2;
			break;
		case 360:
			this.restfactor = 1;
			break;
		default:
			this.restfactor = 1;
		}
	}

	public int getInterest_Basis() {
		return Interest_Basis;
	}

	public void setInterest_Basis(int interest_Basis) {
		this.Interest_Basis = interest_Basis;
	}

	public int getInterest_Basis_emi() {
		return Interest_Basis_Emi;
	}

	public void setInterest_Basis_emi(int interest_Basis_emi) {
		this.Interest_Basis_Emi = interest_Basis_emi;
	}

	public int getInterest_Basis_emi2() {
		return Interest_Basis_Emi2;
	}

	public void setInterest_Basis_emi2(int Interest_Basis) {
		this.Interest_Basis_Emi2 = Interest_Basis;
	}

	public double getInput_emi() {
		return input_emi;
	}

	public void setInput_emi(double input_emi) {
		this.input_emi = input_emi;
	}

	public double getOpenAmount() {
		return open_amount;
	}

	public void setOpenAmount(double open_amount) {
		this.open_amount = open_amount;
	}

	public String getApply_adv() {
		return apply_adv;
	}

	public void setApply_adv(String apply_adv) {
		this.apply_adv = apply_adv;
	}

	public String getApply_adjrate() {
		return apply_adjrate;
	}

	public void setApply_adjrate(String apply_adjrate) {
		this.apply_adjrate = apply_adjrate;
	}

	public int getTenor_in() {
		return tenor_in;
	}

	public void setTenor_in(int tenor_in) {
		this.tenor_in = tenor_in;

		switch (tenor_in) {
		case 1:
			this.tenorfactor = 12; // tenor in months
			this.yearfactor = 12;
			break;
		case 2:
			this.tenorfactor = 52; // tenor in weeks
			this.yearfactor = 52;
			break;
		case 3:
			this.tenorfactor = 365; // tenor in days
			// this.yearfactor = 12;
			break;
		case 4:
			this.tenorfactor = frequency; // tenor in Installments
			this.yearfactor = frequency;
			break;
		case 0:
			this.tenorfactor = 1;
			this.yearfactor = 1;

		}
	}

	public int getTenorfactor() {

		return this.tenorfactor;
	}

	public int getFees() {
		return Fees;
	}

	public void setFees(int fees) {
		Fees = fees;
	}

	public ArrayList getArrFees() {
		return arrFees;
	}

	public void setArrFees(ArrayList arrFees) {
		this.arrFees = arrFees;
	}

	public int getEMI_ro() {
		return EMI_ro;
	}

	public void setEMI_ro(int emi_ro) {
		EMI_ro = emi_ro;
	}

	public int getEMI_unit_ro() {
		return EMI_unit_ro;
	}

	public void setEMI_unit_ro(int emi_unit_ro) {
		EMI_unit_ro = emi_unit_ro;
	}

	public int getEMI_ro_part() {
		return EMI_ro_part;
	}

	public void setEMI_ro_part(int emi_ro_part) {
		EMI_ro_part = emi_ro_part;
	}

	public int getEMI_ro_to() {
		return EMI_ro_to;
	}

	public void setEMI_ro_to(int emi_ro_to) {
		EMI_ro_to = emi_ro_to;
	}

	public int getOthers_ro() {
		return Others_ro;
	}

	public void setOthers_ro(int others_ro) {
		Others_ro = others_ro;
	}

	public int getOthers_unit_ro() {
		return Others_unit_ro;
	}

	public void setOthers_unit_ro(int others_unit_ro) {
		Others_unit_ro = others_unit_ro;
	}

	public int getOthers_ro_to() {
		return Others_ro_to;
	}

	public void setOthers_ro_to(int others_ro_to) {
		Others_ro_to = others_ro_to;
	}

	public int getOthers_ro_part() {
		return Others_ro_part;
	}

	public void setOthers_ro_part(int others_ro_part) {
		Others_ro_part = others_ro_part;
	}

	public int getYearfactor() {
		return yearfactor;
	}

	public String getDtstart() {
		return dtstart;
	}

	public void setDtstart(String dtstart) {
		this.dtstart = dtstart;
	}

	public String getDtend() {
		return dtend;
	}

	public void setDtend(String dtend) {
		this.dtend = dtend;
	}

	public int getRecalMonth() {
		return recalMonth;
	}

	public void setRecalMonth(int recalMonth) {
		this.recalMonth = recalMonth;
	}

	public String getintAmort() {
		return intAmort;
	}

	public void setintAmort(String i) {
		this.intAmort = i;
	}

	public int getintIB() {
		return Int_Interest_Basis;
	}

	public void setintIB(int Int_Interest_Basis) {
		this.Int_Interest_Basis = Int_Interest_Basis;
	}

	public String getPipeseparated() {
		return pipeseparted;
	}

	public void setPipeseparated(String pipeseparted) {
		this.pipeseparted = pipeseparted;
	}

	public String getlastEMIAdj() {
		return lastEMIAdj;
	}

	public void setlastEMIAdj(String lastEMIAdj) {
		this.lastEMIAdj = lastEMIAdj;
	}

	public double getIntprincipal() {
		return int_principal;
	}

	public void setIntprincipal(double int_principal) {
		this.int_principal = int_principal;
	}

	public String getAmortMethod() {
		return AmortMethod;
	}

	public void setAmortMethod(String AmortMethod) {
		this.AmortMethod = AmortMethod;
	}

	public double getRVvalue() {
		return RV;
	}

	public void setRVvalue(double RV) {
		this.RV = RV;
	}

	public int getBPIMethod() {
		return BPI_Method;
	}

	public void setBPIMethod(int BPIMethod) {
		this.BPI_Method = BPIMethod;
	}

	public double getBP_Lastpayamt() {
		return BP_Lastpayamt;
	}

	public void setBP_Lastpayamt(double BP_Lastpayamt) {
		this.BP_Lastpayamt = BP_Lastpayamt;
	}

	// ABFL Equeated Principle Frequency Change start
	public double getEqPrinFreq() {
		return EqPrinFreq;
	}

	public void setEqPrinFreq(double i) {
		this.EqPrinFreq = i;
	}

	public double getTotaleqtInstment() {
		return this.totaleqtprincinstment;
	}

	public void setTotaleqtInstment(double tenor, double tenor_in, double tenorfactor, double EqPrinFreq) {
		if (tenor_in != AmortConstant.tenor_inyear)
			tenor = tenor / tenorfactor;
		this.totaleqtprincinstment = tenor * EqPrinFreq;
	}

	//// ABFL Equeated Principle Frequency Change Change End
	public ArrayList getArrROWS() {
		return arrROWS;
	}

	public void setArrROWS(ArrayList arrROWS) {
		this.arrROWS = arrROWS;
	}

	public int getNPVROWS() {
		return NPVROWS;
	}

	public void setNPVROWS(int rows) {
		NPVROWS = rows;
	}

	public String getAdvEMI_FIRST_INST() {
		return AdvEMI_FIRST_INST;
	}

	public void setAdvEMI_FIRST_INST(String advEMI_FIRST_INST) {
		AdvEMI_FIRST_INST = advEMI_FIRST_INST;
	}

	public ArrayList getArrStepPer() {
		return arrStepPer;
	}

	public void setArrStepPer(ArrayList arrStepPer) {
		this.arrStepPer = arrStepPer;
	}

	public int getP_STEP() {
		return P_STEP;
	}

	public void setP_STEP(int p_step) {
		this.P_STEP = p_step;
	}

	public int getAdjust_Option() {
		return Adjust_Option;
	}

	public void setAdjust_Option(int adjust_Option) {
		Adjust_Option = adjust_Option;
	}

	public boolean isEMIpresent() {
		return isEMIpresent;
	}

	public void setEMIpresent(boolean isEMIpresent) {
		this.isEMIpresent = isEMIpresent;
	}

	public boolean isEMIBasis() {
		return isEMIBasis;
	}

	public void setEMIBasis(boolean isEMIBasis) {
		this.isEMIBasis = isEMIBasis;
	}

	public boolean isEquPriFreqpresent() {
		return isEquPriFreqpresent;
	}

	public void setEquPriFreqpresent(boolean isEquPriFreqpresent) {
		this.isEquPriFreqpresent = isEquPriFreqpresent;
	}

	public boolean isInterestRatepresent() {
		return isInterestRatepresent;
	}

	public void setInterestRatepresent(boolean isInterestRatepresent) {
		this.isInterestRatepresent = isInterestRatepresent;
	}

	public boolean isLoanAmountpresent() {
		return isLoanAmountpresent;
	}

	public void setLoanAmountpresent(boolean isLoanAmountpresent) {
		this.isLoanAmountpresent = isLoanAmountpresent;
	}

	public boolean isTenorpresent() {
		return isTenorpresent;
	}

	public void setTenorpresent(boolean isTenorpresent) {
		this.isTenorpresent = isTenorpresent;
	}

	public int getAdjterm() {
		return adjterm;
	}

	public int getKse_advEMI() {
		return kse_advEMI;
	}

	public void setKse_advEMI(int kse_advEMI) {
		this.kse_advEMI = kse_advEMI;
	}

	public int getAdj_advEMI() {
		return adj_advEMI;
	}

	public void setAdj_advEMI(int adj_advEMI) {
		this.adj_advEMI = adj_advEMI;
	}

	public void setAdjterm(int adjterm) {
		this.adjterm = adjterm;
	}

	public boolean isStepAdjust() {
		return isStepAdjust;
	}

	public void setStepAdjust(boolean isStepAdjust) {
		this.isStepAdjust = isStepAdjust;
	}

	public boolean isSkipAdjust() {
		return isSkipAdjust;
	}

	public void setSkipAdjust(boolean isSkipAdjust) {
		this.isSkipAdjust = isSkipAdjust;
	}

	public boolean isBPAdjust() {
		return isBPAdjust;
	}

	public void setBPAdjust(boolean isBPAdjust) {
		this.isBPAdjust = isBPAdjust;
	}

	public double getReal_emi() {
		return real_emi;
	}

	public void setReal_emi(double real_emi) {
		this.real_emi = real_emi;
	}

	public double getReal_interest_rate() {
		return real_interest_rate;
	}

	public void setReal_interest_rate(double real_interest_rate) {
		this.real_interest_rate = real_interest_rate;
	}

	public String getReal_lid() {
		return real_lid;
	}

	public void setReal_lid(String real_lid) {
		this.real_lid = real_lid;
	}

	public double getReal_loanamount() {
		return real_loanamount;
	}

	public void setReal_loanamount(double real_loanamount) {
		this.real_loanamount = real_loanamount;
	}

	public boolean isCapitalize() {
		return isCapitalize;
	}

	public void setCapitalize(boolean isCapitalize) {
		this.isCapitalize = isCapitalize;
	}

	public boolean isAdvEmiCalculated() {
		return isAdvEmiCalculated;
	}

	public void setAdvEmiCalculated(boolean isAdvEmiCalculated) {
		this.isAdvEmiCalculated = isAdvEmiCalculated;
	}

	public boolean isDeductTDS() {
		return deductTDS;
	}

	public void setDeductTDS(boolean deductTDS) {
		this.deductTDS = deductTDS;
	}

	public EMI_ADJ getEmi_adj() {
		return emi_adj;
	}

	public void setEmi_adj(EMI_ADJ emi_adj) {
		this.emi_adj = emi_adj;
	}

	public double getAdjRate() {
		return AdjRate;
	}

	public void setAdjRate(double adjRate) {
		AdjRate = adjRate;
	}

	public double getAccumulInt() {
		return AccumulInt;
	}

	public void setAccumulInt(double accumulInt) {
		AccumulInt = accumulInt;
	}

	public double getTotAccumulInt() {
		return TotAccumulInt;
	}

	public void setTDSPercentage(double tdsPercentage) {
		this.tdsPercentage = tdsPercentage;
	}

	public double getTDSPercentage() {
		return tdsPercentage;
	}

	public void setTotAccumulInt(double totAccumulInt) {
		TotAccumulInt = totAccumulInt;
	}

	public void setIntCalMethod(String intCalMethod) {
		IntCalMethod = intCalMethod;
	}

	public String getIntCalMethod() {
		return IntCalMethod;
	}

	public boolean isAddInt() {
		return isAddInt;
	}

	public void setAddInt(boolean isAddInt) {
		this.isAddInt = isAddInt;
	}

	public String getBpiCapYN() {
		return bpicapyn;
	}

	public void setBpiCapYN(String bpicapyn) {
		this.bpicapyn = bpicapyn;
	}

	public String getMaxEmi() {
		return max_emi;
	}

	public void setMaxEmi(String max_emi) {
		this.max_emi = max_emi;
	}
}
