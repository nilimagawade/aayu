package com.ebixcash.aayu.constant;

/**
 * AmortConstant conatins constants used in Amortization engine.
 * 
 * @author Venkata.Rao
 * @since 3.0
 */

public final class AmortConstant {
	public static final String AAYU_PROGNAME = "Amort";
	public static final String LoanAmount = "P";
	public static final String Tenor = "T";
	public static final String InterestRate = "R";
	public static final String DateOfDisbursement = "DOD";
	public static final String DateOfCycle = "DOC";
	public static final int AmortTypeValue = 0;
	public static final String AmortType = "AT";
	public static final int InterestTypeValue = 0;
	public static final String InterestType = "IT";
	public static final int RestValue = 12;
	public static final String Rest = "REST";
	public static int RepaymentFrequencyValue = 12;
	public static final int RepaymentFrequencyValue78 = 12;
	public static final String RepaymentFrequency = "F";
	public static final String RepaymentFrequency78 = "F";
	public static final int InstallmentTypeValue = 0;
	public static final String InstallmentType = "INST";
	public static final int SameAdvEMIValue = 0;
	public static final String SameAdvEMI = "kse_advEMI";
	public static final int AdvEMINoValue = 0;
	public static final String AdvEMINo = "n_advEMI";
	public static final String AdvEMIFlag = "AdvEMI_FIRST_INST";
	public static final int AdvEMIAdjValue = 1;
	public static final String AdvEMIAdj = "adj_advEMI";
	public static final int EMIRoundingValue = 0;
	public static final String EMIRounding = "EMI_ro";
	public static final String EMIRoundingUnit = "EMI_unit_ro";
	public static final int EMIRoundingUnitValue = 1;
	public static final String EMIRoundingTo = "EMI_ro_to";
	public static final String EMIRoundingPart = "EMI_ro_part";
	public static final String P_STEP = "StepBasedPercentage";
	public static final String OthersRounding = "Others_ro";
	public static final String OthersUnit = "Others_unit_ro";
	public static final int OthersRoundingUnitValue = 1;
	public static final String OthersRoundingTo = "Others_ro_to";
	public static final String OthersRoundingPart = "Others_ro_part";
	public static final int RoundingToUpper = 1;
	public static final int RoundingToLower = 2;
	public static final int RoundingInteger = 1;
	public static final int RoundingDeciaml = 2;
	public static final int DefaultDeciaml = 100000000;
	public static final int RoundingEMI = 1;
	public static final int RoundingAll = 1;
	public static final int NoRounding = 0;
	public static boolean isWeeklyflag = false;
	public static boolean isDaily = false;
	public static int calFirst_int = 0;
	public static String calFirst_intDOD = null;
	public static int monthDOD = 0;
	public static int monthDOC = 0;
	public static String calFirst_intDOC = null;
	public static long diffInTime = 0;
	public static long diffIndays = 0;
	public static long loop_count = 0;

	public static final int AmortType_Reducing_bal = 0;
	public static final int Installment_FirstPrincipal = 3;

	public static final int CompoundingFrequencyValue = 0;
	public static final String CompoundingFrequency = "CF";
	public static final String Date = "Date";
	public static final String EMI = "EMI";
	public static final String Interest = "Interest";
	public static final String PrincipalEMI = "PrincipalEMI";
	public static final String OpeningBalance = "OpeningBalance";
	public static final String ClosingBalance = "ClosingBalance";
	public static final String RoundEMI = "RoundEMI";
	public static final String Rule78 = "Rule78";
	public static final String BPIRecovery = "BPI_Recovery";
	public static final String BPMonth = "BP_Month";
	public static final String BP_Amount = "BP_Amount";
	public static final String BP_Adjust = "BP_Adjust";
	public static final int BP_AdjustValue = 0;
	public static final String InterestOnly = "Int_Only";
	public static final int InterestOnlyValue = 0;
	public static final int IntBasis_30360 = 0;
	public static final int IntBasis_Actuals = 1;
	public static final int IntBasis_Actuals_360 = 2;
	public static final int IntBasis_Actuals_365 = 3;
	public static final int IntBasis_Actuals_ISMA = 4;
	public static final int InterestTypeFlat = 2;
	public static final int Installment_EquatedPrincipal = 2;
	public static final int Installment_InterestOnly = 1;
	public static final int AdjustedEMI = 0;
	public static final int AdjustEMI = 1;
	public static final int AdjustTenor = 2;
	public static final String Adjust_Option = "Adjust_Option";
	public static final String compFreq = "compFreq";
	public static final String dateformat = "dateformat";
	public static final int setYear = 1;
	public static final int setMonth = 2;
	public static final int setDate = 3;
	public static final int setWeek = 4;
	public static final String Interest_Basis = "IB";
	public static final String Interest_Basis_Emi = "EMI_IB";
	public static final int Interest_BasisValue = 0;
	public static final int AmortType_Rule78 = 1;
	public static final int AmortType_Equatorial = 2;
	public static final String Input_EMI = "emi";
	public static final String Apply_ADV = "Y";
	public static final String tenor_in = "T_IN";
	public static final int tenor_inyear = 0; // tenor in year
	public static final int tenor_inmonths = 1;
	public static final String dtstart = "St_Date";
	public static final String dtend = "End_Date";
	public static final String bplastpay = "BP_Lastpayamt";
	public static final String Installment_No = "Installment_No";
	public static final String Amount = "Amount";
	public static final String EquatedPrincipleFrequency = "EQUATEDPRINFRQ";
	public static final String Input_EmiADJ = "EMI_ADJ";/*
														 * Parameters for the creation of new row for the last
														 * installment
														 */
	public static final String Input_EmiOP = "EMI_OP";
	public static final int Input_EmiOPValue = 0;
	public static final String Input_Onbasis = "ONBASIS";
	public static final int Input_OnbasisValue = 0;
	public static final String Input_Thresh = "THRESH";
	public static final double Input_ThreshValue = 0;
	public static final String Input_Adj = "ADJ";
	public static final String lastInstlRo = "LAST_INST_RO";
	public static final String reduce_bpi = "REDUCE_BPI";
	public static final String pay_first = "PAY_FIRST";
	public static final String eom = "EOM";
	public static final String som = "SOM";
	public static final String pipeseparated = "PIPESEPARATED";
	public static final String AmortMethod = "AM";
	public static final String int_amort = "INT_AMORT";
	public static final String int_IB = "INT_IB";
	public static final String BPI_IB = "BPI_B";
	public static final String comp = "COMP";
	public static final String val = "VAL";
	public static final String toadd = "TOBEADDED";
	public static final String lastEMIAdj = "LAST_EMI_CONST";
	public static final String Int_principal = "I_P";
	public static final String RVvalue = "RV";
	public static final String Precision = "PRECISION_VALUE";
	public static final String CompoundingMethod = "SIMPLE";
	public static final int ITVF = 0;
	public static final String IntCalMethod = "INT_CAL";
	public static final String deductTDS = "DEDUCT_TDS";
	public static final String TDSPercentage = "	";
	public static final String bpi_cap_yn = "BPI_CAP_YN";
	public static final String maxEmi = "MAX_EMI";
	public static final String currEMI = "CURR_EMI";
	public static final String EXCEPTION_OCCURED = "Service unavailable... Please try after some times!";
	public static final String FAILURE_MSG = "Failure";
	public static final String DATA_FETCH_SUCCESS = "Data fetched Successfully!";
	public static final String SUCCESS = "Success";

	public static final String request = "Request";
	public static final String request_folder = "request/";
	public static final String response_folder = "response/";
	public static final String FAILURE_MESSAGE = "Something went wrong please try again after some time";

	public final class Balloon {
		public static final String BalloonMonth = "BPmonth";
		public static final int BalloonMonthValue = 0;
		public static final String BalloonAmount = "BPAmount";
		public static final int BalloonAmountValue = 0;
		public static final String BallonAdj = "BPadjust";
		public static final int BallonAdjValue = 0;

	}

	public final class StepPer {

		public static final String StepPerFrmMonth = "frm_inst";
		public static final int StepPerFrmMonthValue = 0;
		public static final String StepPerTillMonth = "to_inst";
		public static final int StepPerTillMonthValue = 0;
		public static final String StepPerRate = "P_R";
		public static final String StepPerPER = "P_PER";
		public static final String StepPerFreq = "P_F";
		public static final String StepPerTenorUnit = "P_T_IN";

	}

	public final class AdjustRate {
		public static final String Rate = "adjustrate";
		public static final int rateValue = 0;
		public static final String AfterPayment = "afterpayment";
		public static final int AfterPaymentValue = 0;

	}

	public final class StepUpDown {
		public static final String StepUpDownType = "stepmode";
		public static final int StepUpDownTypeValue = 0;
		public static final String StepUpDownFrmMonth = "frm_month";
		public static final int StepUpDownFrmMonthValue = 0;
		public static final String StepUpDownTillMonth = "to_month";
		public static final int StepUpDownTillMonthValue = 0;
		public static final String StepUpDownBasis = "stepbasis";
		public static final int StepUpDownBasisValue = 0;
		public static final String StepUpDownBy = "stepby";
		public static final int StepUpDownByValue = 0;
		public static final String StepUpDownAdjust = "stepadjust";
		public static final int StepUpDownAdjustValue = 0;
		public static final int StepUp = 0;
		public static final int StepAmount = 0;
		public static final int StepEMI = 2; // Step EMI as input
		public static final int StepPrin = 3;
	}

	public final class PerPrinRecv {
		public static final String StepPrincFrmMonth = "stepprin_frm";
		public static final int StepPrincFrmMonthValue = 0;
		public static final String StepPrincToMonth = "stepprin_to";
		public static final int StepPrincToMonthValue = 0;
		public static final String StepPrincIn = "stepprin_in";
		public static final int StepPrincInValue = 0;
		public static final int StepPrinc_inpercentage = 0; // tenor in year
		public static final int StepPrinc_inamount = 1;
		public static final String StepPrincBy = "stepprin_by";
		public static final int StepPrincByValue = 0;
		public static final String StepPrincFreq = "stepprin_freq";
		public static final int StepPrincFreqValue = 0;
		public static final String StepPrincRate = "stepprin_rate";
		public static final double StepPrincRateValue = 0.0d;
	}

	public final class SkipEMI {
		public static final String SkipFrom = "skipfrm_month";
		public static final int SkipFromvalue = 0;
		public static final String SkipNo = "no_month";
		public static final int SkipNoValue = 0;
		public static final String SkipAdjust = "skipadjust";
		public static final int SkipAdjustValue = 0;
		public static final int AdjustedEMIIncTenor = -1;
		public static final int AdjustedEMI = 0;
		public static final int AdjustEMI = 1;
		public static final int AdjustTenor = 2;
		public static final String SkipPartialPayIn = "skippartialpay_in";
		public static final String SkipPartialPayIn_perc = "P";
		public static final String SkipPartialPayIn_amt = "A";
		public static final String SkipPartInt = "skippartint";

		public static final String SkipCapital = "skipcapital";
		public static final String SkipCapitalValue = "N"; // Interest Free
		public static final String SkipCapitalValueY = "Y"; // Interest Capitalize
		public static final String SkipCapitalValuePayable = "P"; // Interest Payable
		public static final String SkipPartialPay = "skippartpay";
		public static final String SkipPartialInterest = "Y"; // Interest Partial (Limited)
		public static final String SkipAccum = "A";
		public static final String SkipCumulative = "C";

	}

	public final class NPVROWS {
		public static final String Installment_No = "Installment_No";
		public static final String Amount = "Amount";

	}

	public final class Fees {
		public static final String Basis = "feebasis";
		public static final int BasisValue = 0;
		public static final String MinMax = "feeminmax";
		public static final int MinMaxValue = 0;
		public static final String ParamPercent = "parampercent";
		public static final int ParamPercentValue = 0;
		public static final String FeeAmount = "feeAmt";
		public static final int FeeAmountValue = 0;
		public static final String FeeParam = "feeparam";
		public static final int FeeParamValue = 0;
		public static final int BasisAmount = 0;
		public static final int BasisPercent = 1;
		public static final int AmountPercent = 2;
		public static final int Minimum = 0;

	}

	public final class Component {
		public static final String FeeName = "name";
		public static final int BasisValue = 0;
		public static final String Basis = "basis";
		public static final int BasisAmount = 0;
		public static final int BasisPercentage = 1;
		public static final int BasisCalculation = 2;
		public static final String Type = "type";
		public static final String TypeRange = "R";
		public static final String AmountPercentage = "B";
		public static final String Calculate = "C";
		public static final String AmtPercentRange = "X";
		public static final String Parameter = "param";
		public static final int ParamValue = 0;
		public static final String Value = "amountvalue";
		public static final String PercentVal = "percentvalue";
		public static final String Range = "range";
		public static final String Calculation = "calc";
		public static final String Dependent = "dependent";
		public static final String LoanAmount = "LA";
		public static final int Add = 1;
		public static final int Subtract = 2;
		public static final int Multiply = 3;
		public static final int Divide = 4;
		public static final int Min = 5;
		public static final int Max = 6;
		public static final String OpeningBalance = "Op_Bal";
		public static final String EMI = "EMI";
		public static final String Interest = "Int";
		public static final String Principal = "Prn";
		public static final String DependentY = "Y";
		public static final String DependentN = "N";
		public static final String NA = "NA";
		public static final int NoCalc = 0;
		// For New row of EMI Adjust

		public static final int onBasisNearest = 0;
		public static final int conditionNewRow = 1;
		public static final int onBasisPerBasis = 1;
		public static final int onBasisAmtBasis = 2;
		public static final int newRow = 3;
		public static final int Adj_in_EMI = 1;
		public static final int Adj_in_interest = 2;
		public static final int lastInst = 0;
		public static final int condNewInst = 1;
		public static final int kpOsp = 2;

		// For Last Row Rounding

		public static final String lastInstlRoY = "Y";
		public static final String lastInstlRoN = "N";
		// For Negative BPI

		public static final String reduceBpiY = "Y";
		public static final String reduceBpiN = "N";

		/// Pay First
		public static final String payFirstY = "Y";
		public static final String payFirstN = "N";
		// For Internal Amort

		// EOM YN
		public static final String eomY = "Y";
		public static final String eomN = "N";

		// SOM YN
		public static final String somY = "Y";
		public static final String somN = "N";

	}

	public final class Comp {
		public static final String CompName = "NAME";
		public static final String ReqOp = "REQOP";
		public static final String ctype = "CTYPE";
		public static final String Val = "VAL";
		public static final String int_amortY = "Y";
		public static final String int_amortN = "N";
		public static final String int_ReqOpY = "Y";
		public static final String int_ReqOpN = "N";
		public static final String int_ToAddY = "F";
		public static final String int_ToAddN = "C";
	}

	public final class AmortValidation {
		public static final String ERR_LENGTH_EXCEED = " should not be greater than 12";
		public static final String ERR_NUMBER_FORMAT = " should be in proper number format";
		public static final String ERR_INVALID_NUMBER = " is not a valid number";
		public static final String ERR_MANDETORY = " is required";
		public static final String ERR_ISNULL = " is null";
		public static final String ERR_ISINVALID = " is invalid";
		public static final String ERR_AMORT_NOTPOSSIBLE = "Amort is not possible for this ";
		public static final String ERR_COMPARE = " cannot be greater than ";
		public static final int MAX_LENGTH = 15;
		public static final String ERR_DATE_FORMAT = " should be in proper date format";
		public static final String ERR_DATE_INVALID = " must be less than or equal to ";
		public static final String ERR_BALLOON_INVALID = "Please specify balloon values";
		public static final String ERR_SKIP_INVALID = "Please specify skip values";
		public static final String ERR_STEPUP_INVALID = "Please specify step values";
		public static final String ERR_ADJRATE_INVALID = "Please specify adjust rate values";
		public static final String ERR_FEES_INVALID = "Please specify Fees values";
		public static final String ERR_VALUE_INVALID = " is not valid for this amort type";
		public static final String ERR_STEPUP_MONTH = "Stepping month is invalid";
		public static final String ERR_SKIP_MONTH = "Skipping month is invalid";
		public static final String ERR_BALLOON_MONTH = "Balloon month is invalid";
		public static final String ERR_INVALID_STEPMONTH = "Stepup month cannot be same as Baloon/Stepup/Skipping Month";
		public static final String ERR_INVALID_SKIPMONTH = "Skipping month cannot be same as Baloon/Stepup/Skipping Month";
		public static final String ERR_INVALID_BALLOONMONTH = "Balloon month cannot be same as Baloon/Stepup/Skipping Month";
		public static final String ERR_COMPONENT_INVALID = "Please specify Component values";
		public static final String ERR_INVALID_CALC = "Invalid Calculation Operation Specified";
		public static final int CHARMAX_LENGTH = 1;
		public static final String ERR_CHARLENGTH_EXCEED = " should not be greater than 1";
		public static final String ERR_INVALID_CVAL = "Invalid Component Value for specified calculation";
		public static final String ERR_INVALID_ROUNDUNIT = "Invalid Rounding Unit";
		public static final String ERR_RANGE_FORMAT = "Please specify range in proper format e.g. 100-200";
		public static final String ERR_ADVAMT = "Invalid amount Specified for Advance Option";
		public static final String ERR_REDUCETENOR = "Installment invalid due to reduced tenor";
		public static final String ERR_INVALID_TENOR = "Invalid Input EMI ";
		public static final String ERR_NEGATIVE_AMORT = "Negative Amort";
		public static final String ERR_ADD_INT = "Add to interest component + interest is grearter than EMI amount";
		public static final String ERR_NEGATIVE_STEPAMOUNT = "Invalid Step Amount";
		public static final String ERR_INVALID_ADJUST_OPTION = "ADJUSTED EMI VARIATION IS NOT POSSIBLE IF EMI IS PROVIDED";
		public static final String ERR_TENOR_AS_INPUT = "TENOR SHOULD NOT BE GIVEN AS INPUT WHEN PRINCIPAL,EMI AND INTEREST ARE PROVIDED!!!";
		public static final String ERR_EMI_AS_INPUT = "EMI SHOULD NOT BE GIVEN AS INPUT WHEN PRINCIPAL,TENOR AND INTEREST ARE PROVIDED!!!";
		public static final String ERR_INPUT_PARAMETERS = "TENOR,PRINCIPAL,INTEREST RATE AND EMI CANNOT BE PROVIDED TOGETHER!!!";
		public static final String ERR_INTEREST_RATE = "INTEREST RATE IS NEGATIVE";
		public static final String ERR_EMI_ROUNDING = "EMI ROUNDING PARAMETERS SHOULD BE ACCORDING TO THE GIVEN EMI";
		public static final String ERR_NEGATIVE_BPI_PARAMETERS = "NEGATIVE BPI AMORT GENERATION IS NOT POSSIBLE WITH GIVEN INPUTS!!";
		public static final String ERR_INT_AMORT_FREQUENCY = "FREQUENCY SHOULD BE MONTHLY FOR INTERNAL AMORT GENERATION!!";
		public static final String ERR_OSP_GREATERTHAN_EMI = "for last installment OSP is more than EMI !!";
		public static final String ERR_AMORT_METHOD = "FEE AMORTIZATION IS NOT POSSIBLE WITH GIVEN  INPUTS!!";
		public static final String ERR_NEGATIVE_IRR = "IRR VALUE IS NEGATIVE FOR THE GIVEN INPUTS";
		public static final String ERR_NPV = "Please specify NPV values";
		public static final String ERR_INSTALLMENT = "Please Specify Installment No.in Sequence";
		public static final String ERR_FREQUENCY = "Repayment Frequency Should not be the Zero";
		public static final String ERR_FREQUENCYVALUE = "Invalid input of Repayment Frequency";
		public static final String ERR_ADJTENOR = "Adjust Tenor is not possible in case of Adjust Rate is given with input Tenor";

		// ABFL Change
		public static final String ERR_REPAYFRQ_GRTTN_EQTPRINCFRQ = "REPAYMENT FREQUENCY SHOULD BE GREATER THAN EQUALS TO EQUATED PRINCIPLE FREQUENCY";
		// % principle changes
		public static final String ERR_PERPRINCRECV_INVALID = "Please specify % principle recovery values";
	}

	public final class AmortCaptions {
		public static final String LoanAmount = "Loan Amount";
		public static final String Tenor = "Tenor";
		public static final String TenorIn = "Tenor Unit";
		public static final String InterestRate = "Interest Rate";
		public static final String DateOfDisbursement = "Date Of Disbursement";
		public static final String DateOfCycle = "Date Of Cycle";
		public static final String AmortTypeValue = "Amort Type Value";
		public static final String AmortType = "Amort Type";
		public static final String InterestTypeValue = "Interest Type Value";
		public static final String InterestType = "Interest Type";
		public static final String RestValue = "Rest Value";
		public static final String Rest = "REST";
		public static final String RepaymentFrequencyValue = "Repayment Frequency Value";
		public static final String RepaymentFrequency = "Repayment Frequency";
		public static final String RepaymentFrequency78 = "Repayment Frequency";
		public static final String InstallmentTypeValue = "Installment Type Value";
		public static final String InstallmentType = "Installment Type";
		public static final String AdvEMINoValue = "No. of advance EMI";
		public static final String AdvEMINo = "No. of advance EMI";
		public static final String AdvEMIAdjValue = "Advance EMI Adjust value";
		public static final String AdvEMIAdj = "Advance EMI Adjust";
		public static final String EMIRoundingValue = "EMI Rounding Value";
		public static final String EMIRounding = "EMI Rounding";
		public static final String OthersRounding = "Other Rounding";
		public static final String RoundingUnitValue = "Rounding Unit Value";
		public static final String EMIRoundingUnit = "EMI Rounding Unit";
		public static final String OthersRoundingUnit = " Rounding Unit for other values";
		public static final String CompoundingFrequencyValue = "Compounding Frequency Value";
		public static final String CompoundingFrequency = "Compounding Frequency";
		public static final String Date = "Date";
		public static final String EMI = "EMI";
		public static final String Interest = "Interest";
		public static final String PrincipalEMI = "Principal EMI";
		public static final String OpeningBalance = "Opening Balance";
		public static final String ClosingBalance = "Closing Balance";
		public static final String Rule78 = "Rule 78";
		public static final String BPIRecovery = "BPI Recovery";
		public static final String BPMonth = "BP Month";
		public static final String BP_Amount = "BP Amount";
		public static final String BP_Adjust = "BP Adjust";
		public static final String BP_AdjustValue = "BP Adjust Value";
		public static final String InterestOnly = "Interest Only";
		public static final String InterestOnlyValue = "Interest Only Value";
		public static final String IntBasis_Actuals = "IntBasis Actuals";
		public static final String InterestTypeFlat = "Interest Type Flat";
		public static final String Installment_EquatedPrincipal = "Installment Equated Principal";
		public static final String Installment_InterestOnly = "Installment InterestOnly";
		public static final String AdjustedEMI = "Adjusted EMI";
		public static final String AdjustEMI = "Adjust EMI";
		public static final String AdjustTenor = "Adjust Tenor";
		public static final String compFreq = "Compound Frequency";
		public static final String Interest_Basis = "Interest Basis";
		public static final String Interest_BasisValue = "Interest Basis Value";
		public static final String AmortType_Rule78 = "Amort Type Rule78";
		public static final String AmortType_Equatorial = "Amort Type Equatorial";
		public static final String dateformat = "Date Format";
		public static final String Step_EMI = "Step Up EMI";
		public static final String StepUpDownFrmMonth = "Step Up Down From Month";
		public static final String StepUpDownTillMonth = "Step Up Down Till Month";
		public static final String StepUpDownBasis = "Step Up Down Basis";
		public static final String StepUpDownType = "Step Up Down Type";
		public static final String StepUpDownBy = "Step Up Down By";
		public static final String StepUpDownAdjust = "Step Up Down Adjust";
		public static final String Skip_EMI = "Skip EMI";
		public static final String SkipFrom = "Skip Form";
		public static final String SkipNo = "Skip No";
		public static final String SkipAdjust = "Skip Adjust";
		public static final String SkipCapital = "Skip Capital";
		public static final String BP = "Balloon Payment";
		public static final String BalloonAmount = "Balloon Amount";
		public static final String BallonAdj = "Ballon Adj";
		public static final String BalloonMonth = "Balloon Month";
		public static final String adjustrate = "Adjust Rate";
		public static final String afterpayment = "After Payment";
		public static final String Input_EMI = "Input EMI";
		public static final String EMIRoundingTo = " EMI Rounding To";
		public static final String OtherRoundingTo = " Rounding To For Other Values";
		public static final String EMIRoundingPart = " EMI Rounding Part";
		public static final String OtherRoundingPart = " Rounding Part For Other Values";
		public static final String Component = "Component";
		public static final String ComponentBasis = "Component Basis";
		public static final String ComponentValue = "Component Value";
		public static final String ComponentCalc = "Component Calculation";
		public static final String ComponentDependent = "Component Dependent";
		public static final String ComponentParam = "Component Parameter";
		public static final String Fees = "Fees";
		public static final String FeeName = "Fee Name";
		public static final String Basis = "Basis";
		public static final String Parameter = "Parameter";
		public static final String Value = "Amount Value";
		public static final String Type = "Type";
		public static final String PercentVal = "Percent Value";
		public static final String Range = "Range";
		public static final String Calculation = "Calculation";
		public static final String Dependent = "Dependent";
		public static final String CompName = "Component Name";
		public static final String ReqOp = "Reqired in Output";
		public static final String ToAdd = "To Be Added";
		public static final String Val = "Component Value";
		public static final String EmiADJ = "EMI_ADJ";/*
														 * Parameters for the creation of new row for the last
														 * installment
														 */
		public static final String EmiOP = "EMI_OP";
		public static final String NPVROWS = "NPVROWS";
		public static final String Onbasis = "ONBASIS";

		public static final String Thresh = "THRESH";
		public static final String Input_Adj = "ADJ";
		public static final String Int_principal = "Internal Loan amount";
		public static final String Installment_No = "Installment_No";
		public static final String Amount = "Amount";

		// % principle recovery changes
		public static final String stepprin = "% Principle Recovery";
		public static final String StepPrincFrmMonth = "% Principle Recovery From";
		public static final String StepPrincToMonth = "% Principle Recovery To";
		public static final String StepPrincIn = "% Principle Recovery In";
		public static final String StepPrincBy = "% Principle Recovery By";
		public static final String StepPrincFreq = "% Principle Recovery Frequency";
		public static final String StepPrincRate = "% Principle Recovery Rate";

		public static final String TDS = "TDS";
		public static final String TDSPercentage = "TDS Percentage";

	}

	public final class AmortTypeConstant {
		public static final int Rest_W = 52;
		public static final int Rest_BW = 26;
		public static final int Rest_D = 360;
		public static final int Rest_D365 = 365;
		public static final int Rest_M = 12;
		public static final int Rest_BiM = 6;
		public static final int Rest_Q = 4;
		public static final int Rest_HY = 2;
		public static final int Rest_Y = 1;
		public static final int Rest_F = 0;
		public static final int RF_W = 52;
		public static final int RF_FN = 26;
		public static final int RF_D = 360;
		public static final int RF_D365 = 365;
		public static final int RF_M = 12;
		public static final int RF_BiM = 6;
		public static final int RF_Term = 3;
		public static final int RF_Q = 4;
		public static final int RF_HY = 2;
		public static final int RF_Y = 1;
		public static final int Installment_EquatedInstallment = 0;
		public static final int Installment_InterestOnly = 1;
		public static final int Installment_EquatedPrincipal = 2;
		public static final int Installment_FirstPrincipal = 3;
		public static final int RoundEMI_No = 0;
		public static final int RoundEMI_Up = 1;
		public static final int RoundEMI_Down = 2;
		public static final int RoundEMI_Nearest = 3;
		public static final int BP_AA = 1;
		public static final int BP_TT = 0;
		public static final int BPI_IB = 3;
		public static final int BPIRecovery_UF = 1;
		public static final int BPIRecovery_AFI = 0;
		public static final int AdvEMI_Front = 0;
		public static final int AdvEMI_End = 1;
		public static final int AdvEMI_None = 2;

		public static final int CF_NA = 0;
		public static final int CF_Q = 4;
		public static final int CF_HY = 2;
		public static final int CF_Y = 1;
		public static final int CF_M = 12;

		public static final int IBP_AT = 1;
		public static final int IBP_AE = 0;
		public static final int IBP_NONE = 2;

		public static final int IRC_AT = 1;
		public static final int IRC_AE = 0;
		public static final int IRC_NONE = 2;

		public static final int IntOnly = 0;

		public static final int IB_TT = 0;
		public static final int IB_AA = 1;
		public static final int IB_AA360 = 2;
		public static final int IB_AA365 = 3;
		public static final int IB_AAISMA = 4;

		public static final int ITVari = 1;
		public static final int ITFixed = 0;
		public static final int ITVF = 2;
		public static final int AmortType_Rule78 = 1;
		public static final int AmortType_Equatorial = 2;
		public static final int AmortType_Reducing_bal = 0;
		public static final int NoRound = 0;
		public static final int EMIOnly = 1;
		public static final int RoundAll = 1;
		public static final int WeekNum = 52;
		public static final int MonthsNum = 12;
		public static final int BPI_AA365IB = 4;
	}

	public final class DateFormats {
		public static final String defaultDateFormat1 = "dd/MM/yyyy";
		public static final String defaultDateFormat2 = "yyyy-MM-dd HH:mm:ss";
		public static final String defaultDateFormat3 = "yyyy-MM-dd";
		public static final String defaultDateFormat4 = "MM/dd/yyyy";
		public static final String defaultDateFormat5 = "dd-MMM-yy";
	}

	public final class DefaultValues {
		public static final int LoanAmount = 0;
		public static final int InputEMI = 0;
		public static final int AmortTypeValue = 0;
		public static final int InterestTypeValue = 0;
		public static final int InstallmentTypeValue = 0;
		public static final int AdvEMIAdjValue = 0;
		public static final int AdvEMINoValue = 0;
		public static final int RepaymentFrequencyValue = 12;
		public static final int RestValue = 12;
		public static final int BPIRecoveryValue = 0;
		public static final int CompoundingFrequencyValue = 12;
		public static final int Interest_BasisValue = 0;
		public static final int EMIRoundingValue = 0;
		public static final int EMIRoundingUnitValue = 100;
		public static final int EMIRoundingToValue = 1;
		public static final int EMIRoundingPartValue = 2;
		public static final int OtherRoundingValue = 0;
		public static final int OtherRoundingUnitValue = 100;
		public static final int OtherRoundingToValue = 1;
		public static final int OtherRoundingPartValue = 2;
		public static final int InterestOnlyValue = 0;
		public static final int TenorUnitValue = 1;
		public static final int TenorValue = 0;
		public static final int SameAdvEMIValue = 0;
		public static final double InterestRate = 0.0d;
		public static final int Input_EmiOPValue = 3;/*
														 * Default values for the creation of new row for last
														 * installment
														 */
		public static final int Input_OnbasisValue = 2;
		public static final double Input_ThreshValue = 0.0d;
		public static final String lastInstlRo = "N";
		public static final String reduce_bpi = "N";
		public static final String pay_first = "N";
		public static final int precision_value = 3;
		public static final String int_amort = "N";
		public static final int int_IB = 0;
		public static final int Input_Adj = 1;
		public static final String pipeseparated = "N";
		public static final double RVvalue = 0.0d;
		public static final double int_principal = 0.0d;
		public static final double bplastpay = 0.0d;
		public static final double EquatedPrincipleFrequency = 0.0d;
		public static final String AdvEMI_FIRST_INST = "N";
		public static final int Adjust_Option = 0;

	}

	public final class AmortJSONConstants {
		public static final String LOANAMOUNT = "loanamount";
		public static final String TENOR = "tenor";
		public static final String TENORUNIT = "tenorunit";
		public static final String INTERESTRATE = "interestrate";
		public static final String EMI = "emi";
		public static final String DISBURSEMENTDATE = "disbdate";
		public static final String CYCLEDATE = "cycledate";
		public static final String AMORTTYPE = "amorttype";
		public static final String INTERESTBASIS = "interestbasis";
		public static final String INTERESTTYPE = "interesttype";
		public static final String INTERESTONLYINSTALLMENTS = "interestonlyinst";
		public static final String INSTALLMENTTYPE = "insttype";
		public static final String PAYMENTFREQUENCY = "pymtfrequency";
		public static final String EQUATEDPRINCIPLEFREQUENCY = "equatedprinfrq";
		public static final String COMPOUNDINGFREQUENCY = "compfreq";
		public static final String REST = "rest";
		public static final String BPIRECOVERY = "bpirecovery";
		public static final String NOOFADVANCEEMI = "noofadvemi";
		public static final String ADJUSTADVANCEEMI = "adjadvemi";
		public static final String REDUCEBPI = "reducebpi";
		public static final String PAYFIRST = "payfirst";
		public static final String EOM = "eom";
		public static final String EMIROUNDING = "emirounding";
		public static final String EMIROUNDINGUNIT = "emiroundingunit";
		public static final String EMIROUNDINGTO = "emiroundingto";
		public static final String EMIROUNDINGPART = "emiroundingpart";
		public static final String OTHERROUNDING = "otherrounding";
		public static final String OTHERROUNDINGUNIT = "otherroundingunit";
		public static final String OTHERROUNDINGTO = "otherroundingto";
		public static final String OTHERROUNDINGPART = "otherroundingpart";
		public static final String SKIPEMI = "skipemi";
		public static final String SKIPFROM = "skipform";
		public static final String SKIPNOOFMONTH = "skipnoofmonth";
		public static final String SKIPADJUST = "skipadjust";
		public static final String SKIPCAPITAL = "skipcapital";
		public static final String BALLOONPAYMENT = "balloonpymt";
		public static final String BALLOONADJUSTMENT = "balloonadjustment";
		public static final String BALLOONMONTH = "balloonmonth";
		public static final String BALLOONAMOUNT = "balloonamount";
		public static final String STEPEMI = "stepemi";
		public static final String STEPUPDOWNFRMMONTH = "stepfrommonth";
		public static final String STEPUPDOWNTILLMONTH = "steptillmonth";
		public static final String STEPUPDOWNBASIS = "stepupdownbasis";
		public static final String STEPUPDOWNTYPE = "stepupdowntype";
		public static final String STEPUPDOWNBY = "stepupdownby";
		public static final String STEPUPDOWNADJUST = "stepupdownadjust";
		public static final String ADJUSTRATE = "adjustrate";
		public static final String ADJUSTEDRATE = "adjustedrate";
		public static final String ADJUSTAFTERPAYMENT = "adjustafterpayment";
		public static final String FEES = "fees";
		public static final String FEENAME = "feename";
		public static final String FEETYPE = "feetype";
		public static final String FEEBASIS = "feebasis";
		public static final String FEEPARAM = "feeparam";
		public static final String FEEAMOUNTVALUE = "feeamountvalue";
		public static final String FEEPERCENTVALUE = "feepercentvalue";
		public static final String FEEDEPENDENT = "feedependent";
		public static final String FEECALCULATION = "feecalculation";
		public static final String FEERANGE = "feerange";
		public static final String LASTEMIADJSUTMENT = "lastemiadjustment";
		public static final String LASTEMIADJSUTMENTOPTION = "lastemiadjustoption";
		public static final String LASTEMIADJSUTMENTBASIS = "lastemiadjustbasis";
		public static final String LASTEMIADJSUTMENTTHRESHOLD = "lastemiadjustthreshold";
		public static final String KEEPLASTEMICONSTANT = "keeplastemiconstant";
		public static final String INTERNALAMORTGENERATION = "internalamort";
		public static final String INTERESTBASISFORINTERNAL = "internalbasisamort";
		public static final String COSTCOMPONENT = "costcomponent";
		public static final String AMOUNTFORCOSTCOMPONENT = "amountforcostcomponent";
		public static final String CTYPE = "ctype";
		public static final String NAMEOFFEECOMISSION = "nameoffeecommission";
		public static final String BPIRECOVERYBASIS = "bpirecoverybasis";
		public static final String STEPPRINCRECOVERY = "stepprinrecovery";
		public static final String STEPPRINCRECOVERYFRMMONTH = "stepprinrecoveryfrmmonth";
		public static final String STEPPRINCRECOVERYTOMONTH = "stepprinrecoverytomonth";
		public static final String STEPPRINCRECOVERYIN = "stepprinrecoveryin";
		public static final String STEPPRINCRECOVERYBY = "stepprinrecoveryby";
		public static final String DATEFORMAT = "dateformat";
		public static final String ADJUSTOPTION = "adjustoption";
		public static final String INTPRINCIPAL = "intprincipal";
		public static final String STARTDATE = "startdate";
		public static final String ENDDATE = "enddate";
		public static final String DEDUCT_TDS = "deductTDS";
		public static final String TDS_PER = "tdsPercentage";
		public static final String EMIBASIS = "emibasis";
	}

}
