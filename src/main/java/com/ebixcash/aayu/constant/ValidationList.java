package com.ebixcash.aayu.constant;

import java.util.ArrayList;

import com.ebixcash.aayu.constant.AmortConstant.AmortTypeConstant;



public class ValidationList {
	public static ArrayList AmortType() {
		ArrayList retList = new ArrayList();
		retList.add(Integer.toString(AmortConstant.AmortType_Reducing_bal));
		retList.add(Integer.toString(AmortTypeConstant.AmortType_Rule78));
		retList.add(Integer.toString(AmortTypeConstant.AmortType_Equatorial));
		return retList;
	}
	public static ArrayList InterestBasis(int amortTyp) {
		ArrayList retList = new ArrayList();
		retList.add(Integer.toString(AmortTypeConstant.IB_TT));
		retList.add(Integer.toString(AmortTypeConstant.IB_AA));
		retList.add(Integer.toString(AmortTypeConstant.IB_AA360));
		retList.add(Integer.toString(AmortTypeConstant.BPI_IB));
		retList.add(Integer.toString(AmortTypeConstant.BPI_AA365IB));
		if(amortTyp==0)
		{
			retList.add(Integer.toString(AmortTypeConstant.IB_AA));
			retList.add(Integer.toString(AmortTypeConstant.IB_AA360));
			retList.add(Integer.toString(AmortTypeConstant.IB_AA365));
			retList.add(Integer.toString(AmortTypeConstant.IB_AAISMA));
		}
		return retList;
	}
	
	public static ArrayList BPIInterestBasis(int amortTyp) {
		ArrayList retList = new ArrayList();
		retList.add(Integer.toString(AmortTypeConstant.IB_TT));
		retList.add(Integer.toString(AmortTypeConstant.IB_AA));
		retList.add(Integer.toString(AmortTypeConstant.BPI_IB));
		if(amortTyp==0)
		{
			retList.add(Integer.toString(AmortTypeConstant.IB_AA));
			retList.add(Integer.toString(AmortTypeConstant.IB_AA360));
			retList.add(Integer.toString(AmortTypeConstant.IB_AA365));
			retList.add(Integer.toString(AmortTypeConstant.IB_AAISMA));
		}
		return retList;
	}
	
	
	public static ArrayList InterestType(int amortTyp) {
		ArrayList retList = new ArrayList();
		if(amortTyp==0){
			retList.add(Integer.toString(AmortTypeConstant.ITFixed));
			retList.add(Integer.toString(AmortTypeConstant.ITVari));}
		else if(amortTyp==1){
			retList.add(Integer.toString(AmortTypeConstant.ITVF));
		}
		else if(amortTyp==2){
			retList.add(Integer.toString(AmortTypeConstant.ITFixed));
		}
		return retList;
	}
	public static ArrayList Rest(int amortTyp) {
		ArrayList retList = new ArrayList();
		retList.add(Integer.toString(AmortTypeConstant.Rest_W));
		retList.add(Integer.toString(AmortTypeConstant.Rest_BW));
		retList.add(Integer.toString(AmortTypeConstant.Rest_D));
		retList.add(Integer.toString(AmortTypeConstant.Rest_D365));
	    retList.add(Integer.toString(AmortTypeConstant.Rest_M));
		retList.add(Integer.toString(AmortTypeConstant.Rest_Q));
		retList.add(Integer.toString(AmortTypeConstant.Rest_HY));
		retList.add(Integer.toString(AmortTypeConstant.Rest_BiM));
		retList.add(Integer.toString(AmortTypeConstant.Rest_Y));
		if(amortTyp!=0){
			retList.add(Integer.toString(AmortTypeConstant.Rest_F));
		}
		return retList;
	}
	public static ArrayList RepaymentFreq(int amortTyp) {
		ArrayList retList = new ArrayList();
		retList.add(Integer.toString(AmortTypeConstant.RF_W));
		retList.add(Integer.toString(AmortTypeConstant.RF_FN));
		retList.add(Integer.toString(AmortTypeConstant.RF_M));
		retList.add(Integer.toString(AmortTypeConstant.RF_D));
		retList.add(Integer.toString(AmortTypeConstant.RF_D365));
		retList.add(Integer.toString(AmortTypeConstant.RF_BiM));
		retList.add(Integer.toString(AmortTypeConstant.RF_Term));
		retList.add(Integer.toString(AmortTypeConstant.RF_Q));
		retList.add(Integer.toString(AmortTypeConstant.RF_HY));
		retList.add(Integer.toString(AmortTypeConstant.RF_Y));
		return retList;
	}

	public static int FreqFactor(int repayfreq) {
		int frequencyfactor = 0;
		switch(repayfreq)
		{
		case 1:
	    	frequencyfactor = 12;
	    	break;
		case 2:

			frequencyfactor=6;
	    	break;
		case 4:
			frequencyfactor=3;
	    	break;
		case 6:
			frequencyfactor=2;
	    	break;
		case 12:
			frequencyfactor=1;
	    	break;
		case 26:
			frequencyfactor=2;
	    	break;
		case 52:
			frequencyfactor=1;
	    	break;
	    default :frequencyfactor =  0;

		}
		return frequencyfactor;
	}

	public static ArrayList InstallmentType(int amortTyp) {
		ArrayList retList = new ArrayList();
		//Installment Type
		if(amortTyp==0){
			retList.add(Integer.toString(AmortTypeConstant.Installment_EquatedPrincipal));
			retList.add(Integer.toString(AmortTypeConstant.Installment_InterestOnly));
			retList.add(Integer.toString(AmortTypeConstant.Installment_FirstPrincipal));
		}
		retList.add(Integer.toString(AmortTypeConstant.Installment_EquatedInstallment));
		retList.add(Integer.toString(AmortTypeConstant.Installment_EquatedPrincipal));
		return retList;
	}
	public static ArrayList RoundEMI(int amortTyp) {
		ArrayList retList = new ArrayList();
		//Rounding
		retList.add(Integer.toString(AmortTypeConstant.NoRound));
		retList.add(Integer.toString(AmortTypeConstant.EMIOnly));
		//retList.add(Integer.toString(AmortTypeConstant.RoundAll));
		return retList;
	}

	public static ArrayList RoundOthers(int amortTyp) {
		ArrayList retList = new ArrayList();
		//Rounding
		retList.add(Integer.toString(AmortTypeConstant.NoRound));
		retList.add(Integer.toString(AmortTypeConstant.RoundAll));
		return retList;
	}
	public static ArrayList RoundTo(int amortTyp) {
		ArrayList retList = new ArrayList();
		//Rounding
		retList.add(Integer.toString(AmortTypeConstant.RoundEMI_No));
		retList.add(Integer.toString(AmortTypeConstant.RoundEMI_Up));
		retList.add(Integer.toString(AmortTypeConstant.RoundEMI_Down));
		retList.add(Integer.toString(AmortTypeConstant.RoundEMI_Nearest));
		return retList;
	}
	public static ArrayList OtherRoundTo(int amortTyp) {
		ArrayList retList = new ArrayList();
		//Rounding
		retList.add(Integer.toString(AmortTypeConstant.RoundEMI_No));
		retList.add(Integer.toString(AmortTypeConstant.RoundEMI_Up));
		retList.add(Integer.toString(AmortTypeConstant.RoundEMI_Down));
		retList.add(Integer.toString(AmortTypeConstant.RoundEMI_Nearest));
		return retList;
	}
	public static ArrayList BPInterest(int amortTyp) {
		ArrayList retList = new ArrayList();
		//BP Interest
		retList.add(Integer.toString(AmortTypeConstant.BP_TT));
		retList.add(Integer.toString(AmortTypeConstant.BP_AA));
		return retList;
	}
	public static ArrayList BPIRecovery(int amortTyp) {
		ArrayList retList = new ArrayList();
		//BPI Recovery
		retList.add(Integer.toString(AmortTypeConstant.BPIRecovery_UF));
		retList.add(Integer.toString(AmortTypeConstant.BPIRecovery_AFI));
		return retList;
	}
	public static ArrayList AdvEMI(int amortTyp) {
		ArrayList retList = new ArrayList();
		//AdvEMI
		retList.add(Integer.toString(AmortTypeConstant.AdvEMI_Front));
		retList.add(Integer.toString(AmortTypeConstant.AdvEMI_End));
		retList.add(Integer.toString(AmortTypeConstant.AdvEMI_None));
		return retList;
	}
	public static ArrayList CompoundFreq(int amortTyp) {
		ArrayList retList = new ArrayList();
		//CF
		//if(amortTyp==0){
			retList.add(Integer.toString(AmortTypeConstant.CF_NA));
			retList.add(Integer.toString(AmortTypeConstant.CF_Q));
			retList.add(Integer.toString(AmortTypeConstant.CF_HY));
			retList.add(Integer.toString(AmortTypeConstant.CF_Y));
			retList.add(Integer.toString(AmortTypeConstant.CF_M));
		//}
		return retList;
	}
	public static ArrayList IntermediateBP(int amortTyp) {
		ArrayList retList = new ArrayList();
		//Intermediate Balloon Payment
		if(amortTyp==0){
			retList.add(Integer.toString(AmortTypeConstant.IBP_AT));
			retList.add(Integer.toString(AmortTypeConstant.IBP_AE));
			retList.add(Integer.toString(AmortTypeConstant.IBP_NONE));
		}
		return retList;
	}
	public static ArrayList InterestRateChange(int amortTyp) {
		ArrayList retList = new ArrayList();
		//Interest Rate Change
		if(amortTyp==0){
			retList.add(Integer.toString(AmortTypeConstant.IRC_AT));
			retList.add(Integer.toString(AmortTypeConstant.IRC_AE));
			retList.add(Integer.toString(AmortTypeConstant.IRC_NONE));
		}
		return retList;
	}
	public static ArrayList InterestOnly(int amortTyp) {
		ArrayList retList = new ArrayList();
		//Interest Only
		if(amortTyp==0){
			retList.add(Integer.toString(AmortTypeConstant.IntOnly));
		}
		return retList;
	}
	public static ArrayList SkipInst(int amortTyp) {
		ArrayList retList = new ArrayList();
		//Interest Rate Change
		if(amortTyp==0){
			retList.add(Integer.toString(AmortTypeConstant.IRC_AT));
			retList.add(Integer.toString(AmortTypeConstant.IRC_AE));
			retList.add(Integer.toString(AmortTypeConstant.IRC_NONE));
		}
		return retList;
	}
	public static ArrayList InstallmentHoliday(int amortTyp) {
		ArrayList retList = new ArrayList();
		//Interest Rate Change
		if(amortTyp==0){
			retList.add(Integer.toString(AmortTypeConstant.IRC_AT));
			retList.add(Integer.toString(AmortTypeConstant.IRC_AE));
			retList.add(Integer.toString(AmortTypeConstant.IRC_NONE));
		}
		return retList;
	}
	public static ArrayList BalloonPayEnd(int amortTyp) {
		ArrayList retList = new ArrayList();
		//Interest Rate Change
		if(amortTyp==0){
			retList.add(Integer.toString(AmortTypeConstant.IRC_AT));
			retList.add(Integer.toString(AmortTypeConstant.IRC_AE));
			retList.add(Integer.toString(AmortTypeConstant.IRC_NONE));
		}
		return retList;
	}
	public static ArrayList StepUDEMI(int amortTyp) {
		ArrayList retList = new ArrayList();
		//Interest Rate Change
		if(amortTyp==0){
			retList.add(Integer.toString(AmortTypeConstant.IRC_AT));
			retList.add(Integer.toString(AmortTypeConstant.IRC_AE));
			retList.add(Integer.toString(AmortTypeConstant.IRC_NONE));
		}
		return retList;
	}
	public static ArrayList Fees_Basis() {
		ArrayList retList = new ArrayList();
		//Fees Basis
		retList.add(Integer.toString(AmortConstant.Component.BasisAmount));
		retList.add(Integer.toString(AmortConstant.Component.BasisPercentage));
		retList.add(Integer.toString(AmortConstant.Component.BasisCalculation));
		return retList;
	}
	public static ArrayList Fees_Type() {
		ArrayList retList = new ArrayList();
		//Fees Type
		retList.add(AmortConstant.Component.TypeRange);
		retList.add(AmortConstant.Component.AmountPercentage);
		retList.add(AmortConstant.Component.Calculate);
		retList.add(AmortConstant.Component.AmtPercentRange);
		return retList;
	}
	public static ArrayList Fees_Calc() {
		ArrayList retList = new ArrayList();
		//Fees Calculations
		retList.add(Integer.toString(AmortConstant.Component.NoCalc));
		retList.add(Integer.toString(AmortConstant.Component.Add));
		retList.add(Integer.toString(AmortConstant.Component.Subtract));
		retList.add(Integer.toString(AmortConstant.Component.Multiply));
		retList.add(Integer.toString(AmortConstant.Component.Divide));
		retList.add(Integer.toString(AmortConstant.Component.Max));
		retList.add(Integer.toString(AmortConstant.Component.Min));
		return retList;
	}
	public static ArrayList Fees_Param() {
		ArrayList retList = new ArrayList();
		//Fees Parameter
		retList.add(AmortConstant.Component.OpeningBalance);
		retList.add(AmortConstant.Component.EMI);
		retList.add(AmortConstant.Component.Interest);
		retList.add(AmortConstant.Component.Principal);
		retList.add(AmortConstant.Component.NA);
		return retList;
	}
	public static ArrayList Fees_Dependent() {
		ArrayList retList = new ArrayList();
		//Fees Dependent
		retList.add(AmortConstant.Component.DependentY);
		retList.add(AmortConstant.Component.DependentN);
		return retList;
	}
	
	public static ArrayList EMI_OP() {
		ArrayList retList = new ArrayList();
		//Rounding
		retList.add(Integer.toString(AmortTypeConstant.NoRound));
		retList.add(Integer.toString(AmortTypeConstant.EMIOnly));
		retList.add(Integer.toString(AmortConstant.Component.newRow));
		retList.add(Integer.toString(AmortConstant.Component.kpOsp));
		return retList;
	}
	public static ArrayList Input_EmiOP_Basis() {
		ArrayList retList = new ArrayList();
		//Fees Type
		retList.add(Integer.toString(AmortConstant.Component.lastInst));
		retList.add(Integer.toString(AmortConstant.Component.condNewInst));
		retList.add(Integer.toString(AmortConstant.Component.kpOsp));
		retList.add(Integer.toString(AmortConstant.Component.newRow));
		return retList;
	}
	public static ArrayList lastInstlRo() {
		ArrayList retList = new ArrayList();
		//Fees Dependent
		retList.add(AmortConstant.Component.lastInstlRoY);
		retList.add(AmortConstant.Component.lastInstlRoN);
		return retList;
	}

	public static ArrayList reduceBpi() {
		ArrayList retList = new ArrayList();
		
		retList.add(AmortConstant.Component.reduceBpiY);
		retList.add(AmortConstant.Component.reduceBpiN);
		return retList;
	}
	public static ArrayList payFirst() {
		ArrayList retList = new ArrayList();
		
		retList.add(AmortConstant.Component.payFirstY);
		retList.add(AmortConstant.Component.payFirstN);
		return retList;
	}
	public static ArrayList eom() {
		ArrayList retList = new ArrayList();
		
		retList.add(AmortConstant.Component.eomY);
		retList.add(AmortConstant.Component.eomN);
		return retList;
	}
	public static ArrayList int_amort() {
		ArrayList retList = new ArrayList();
		
		retList.add(AmortConstant.Comp.int_amortY);
		retList.add(AmortConstant.Comp.int_amortN);
		return retList;
	}
	public static ArrayList int_ReqOp() {
		ArrayList retList = new ArrayList();
		
		retList.add(AmortConstant.Comp.int_ReqOpY);
		retList.add(AmortConstant.Comp.int_ReqOpN);
		return retList;
	}
	public static ArrayList int_ToAdd() {
		ArrayList retList = new ArrayList();
		
		retList.add(AmortConstant.Comp.int_ToAddY);
		retList.add(AmortConstant.Comp.int_ToAddN);
		return retList;
	}
	public static ArrayList IntIB() {
		ArrayList retList = new ArrayList();
		retList.add(Integer.toString(AmortTypeConstant.IB_TT));
		retList.add(Integer.toString(AmortTypeConstant.IB_AA));
		retList.add(Integer.toString(AmortTypeConstant.IB_AA365));
		retList.add(Integer.toString(AmortTypeConstant.IB_AAISMA));
		return retList;
	}
	public static ArrayList Input_Adj() {
		ArrayList retList = new ArrayList();
		//Fees Type
		retList.add(Integer.toString(AmortConstant.Component.Adj_in_EMI));
		retList.add(Integer.toString(AmortConstant.Component.Adj_in_interest));
		
		return retList;
	}
	
}
