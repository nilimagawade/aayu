package com.ebixcash.aayu.customvalidator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.lang.Number;
import java.lang.Double;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.ebixcash.aayu.constant.AmortConstant.AmortCaptions;
import com.ebixcash.aayu.constant.AmortConstant.AmortTypeConstant;
import com.ebixcash.aayu.constant.AmortConstant.AmortValidation;
import com.ebixcash.aayu.util.HashMapUtils;
import com.ebixcash.aayu.util.*;

import com.ebixcash.aayu.constant.*;
import com.ebixcash.aayu.constant.AmortConstant.DateFormats;

import com.ebixcash.aayu.exception.*;
import com.ebixcash.aayu.exception.AmortException.ValidationException;
import com.ebixcash.aayu.model.AmortInputBean;

public class Validations {
	private static final Logger logger = LoggerFactory.getLogger(Validations.class);

	public static final boolean isDebugEnabled = logger.isDebugEnabled();
	
	public static HashMap<String, Object> getValidate(Map<String, Object> amortMap) throws AmortException {
		boolean amortFlag = false;
		HashMap<String, Object> resultValues = null;
		String errorMsg = null;
		ErrorMessages errors = new ErrorMessages();
		String progName = AmortConstant.AAYU_PROGNAME;
		HashMap<String, Object> defaultValmap = setDefaultValues(amortMap);
		if (logger.isDebugEnabled())
			logger.debug(progName + ":Invoking Validator");
		try {
			resultValues = new HashMap();
			amortMap = defaultValmap;
			// Validation for Amort Type with possible combinations
			int FreqFactor = 0;

			// Amort Type
			String AmortType = amortMap.get(AmortConstant.AmortType).toString();
			ArrayList AmortTypeLi = ValidationList.AmortType();
			if (AmortTypeLi.contains(AmortType)) {
				Integer AmortTypeVal = GenericTypeValidator.formatInt(AmortType, Locale.ENGLISH);
				if (null == AmortTypeVal) {
					errorMsg = AmortCaptions.AmortType + AmortValidation.ERR_NUMBER_FORMAT;
					errors.addError(errorMsg, AmortConstant.AmortType, "ERR_NUMBER_FORMAT");
					amortFlag = false;
				}
			} else {
				errorMsg = AmortCaptions.AmortType + " is not valid.";
				errors.addError(errorMsg, AmortConstant.AmortType);
			}

			// Interest Basis
			String InterestBasis = amortMap.get(AmortConstant.Interest_Basis).toString();
			ArrayList IntBasisLi = ValidationList.InterestBasis(Integer.parseInt(AmortType));
			if (IntBasisLi.contains(InterestBasis)) {
				Integer InterestBasisVal = GenericTypeValidator.formatInt(InterestBasis, Locale.ENGLISH);
				if (null == InterestBasisVal) {
					errorMsg = AmortCaptions.Interest_Basis + AmortValidation.ERR_NUMBER_FORMAT;
					errors.addError(errorMsg, AmortConstant.Interest_Basis, "ERR_NUMBER_FORMAT");

					amortFlag = false;
				}
			} else {
				errorMsg = AmortCaptions.Interest_Basis + AmortValidation.ERR_VALUE_INVALID;
				errors.addError(errorMsg, AmortConstant.Interest_Basis, "ERR_VALUE_INVALID");
			}
			// BPI Interest Basis

			String BPIInterestBasis = amortMap.get(AmortConstant.BPI_IB).toString();
			ArrayList BPIIntBasisLi = ValidationList.InterestBasis(Integer.parseInt(AmortType));
			if (BPIIntBasisLi.contains(BPIInterestBasis)) {
				Integer BPIInterestBasisVal = GenericTypeValidator.formatInt(InterestBasis, Locale.ENGLISH);
				if (null == BPIInterestBasisVal) {
					errorMsg = AmortCaptions.Interest_Basis + AmortValidation.ERR_NUMBER_FORMAT;
					errors.addError(errorMsg, AmortConstant.Interest_Basis, "ERR_NUMBER_FORMAT");
					amortFlag = false;
				}
			} else {
				errorMsg = AmortCaptions.Interest_Basis + AmortValidation.ERR_VALUE_INVALID;
				errors.addError(errorMsg, AmortConstant.Interest_Basis, "ERR_VALUE_INVALID");
			}

			// Interest Type
			String InterestType = amortMap.get(AmortConstant.InterestType).toString();
			ArrayList IntTypeLi = ValidationList.InterestType(Integer.parseInt(AmortType));
			if (IntTypeLi.contains(InterestType)) {
				Integer InterestTypeVal = GenericTypeValidator.formatInt(InterestType, Locale.ENGLISH);
				if (null == InterestTypeVal) {
					errorMsg = AmortCaptions.InterestType + AmortValidation.ERR_NUMBER_FORMAT;
					errors.addError(errorMsg, AmortConstant.InterestType, "ERR_NUMBER_FORMAT");
				}
			} else {
				errorMsg = AmortCaptions.InterestType + AmortValidation.ERR_VALUE_INVALID;
				errors.addError(errorMsg, AmortConstant.InterestType, "ERR_VALUE_INVALID");
			}

			// Rest
			String Rest = amortMap.get(AmortConstant.Rest).toString();
			ArrayList RestLi = ValidationList.Rest(Integer.parseInt(AmortType));
			if (RestLi.contains(Rest)) {
				Integer RestVal = GenericTypeValidator.formatInt(Rest, Locale.ENGLISH);
				if (null == RestVal) {
					errorMsg = AmortCaptions.Rest + AmortValidation.ERR_NUMBER_FORMAT;
					errors.addError(errorMsg, AmortConstant.Rest, "ERR_NUMBER_FORMAT");
				}
			} else {
				errorMsg = AmortCaptions.Rest + AmortValidation.ERR_VALUE_INVALID;
				errors.addError(errorMsg, AmortConstant.Rest, "ERR_VALUE_INVALID");
			}

			// Repayment Frequency
			String RepaymentFrequency = amortMap.get(AmortConstant.RepaymentFrequency).toString();
			ArrayList RepaymentFreqLi = ValidationList.RepaymentFreq(Integer.parseInt(AmortType));
			if (RepaymentFreqLi.contains(RepaymentFrequency)) {
				Integer RepaymentFrequencyVal = GenericTypeValidator.formatInt(RepaymentFrequency, Locale.ENGLISH);
				if (null == RepaymentFrequencyVal) {
					errorMsg = AmortCaptions.RepaymentFrequency + AmortValidation.ERR_NUMBER_FORMAT;
					errors.addError(errorMsg, AmortConstant.RepaymentFrequency, "ERR_NUMBER_FORMAT");
					amortFlag = false;
				} else {
					if (amortFlag)
						amortFlag = true;
					FreqFactor = ValidationList.FreqFactor(RepaymentFrequencyVal.intValue());
				}
			} else {
				errorMsg = AmortCaptions.RepaymentFrequency + AmortValidation.ERR_VALUE_INVALID;
				errors.addError(errorMsg, AmortConstant.RepaymentFrequency, "ERR_VALUE_INVALID");
				amortFlag = false;
			}

			// Installment Type
			String InstallmentType = amortMap.get(AmortConstant.InstallmentType).toString();
			ArrayList InstTypLi = ValidationList.InstallmentType(Integer.parseInt(AmortType));
			if (InstTypLi.contains(InstallmentType)) {
				Integer InstallmentTypeVal = GenericTypeValidator.formatInt(InstallmentType, Locale.ENGLISH);
				if (null == InstallmentTypeVal) {
					errorMsg = AmortCaptions.InstallmentType + AmortValidation.ERR_NUMBER_FORMAT;
					errors.addError(errorMsg, AmortConstant.InstallmentType, "ERR_NUMBER_FORMAT");
				}
			} else {
				errorMsg = AmortCaptions.InstallmentType + AmortValidation.ERR_VALUE_INVALID;
				errors.addError(errorMsg, AmortConstant.InstallmentType, "ERR_VALUE_INVALID");
			}

			// EMI Rounding
			String EMIRounding = amortMap.get(AmortConstant.EMIRounding).toString();
			ArrayList RoundEMILi = ValidationList.RoundEMI(Integer.parseInt(AmortType));
			if (RoundEMILi.contains(EMIRounding)) {
				Integer EMIRoundingVal = GenericTypeValidator.formatInt(EMIRounding, Locale.ENGLISH);
				if (null == EMIRoundingVal) {
					errorMsg = AmortCaptions.EMIRounding + AmortValidation.ERR_NUMBER_FORMAT;
					errors.addError(errorMsg, AmortConstant.EMIRounding, "ERR_NUMBER_FORMAT");
				}
			} else {
				errorMsg = AmortCaptions.EMIRounding + AmortValidation.ERR_VALUE_INVALID;
				errors.addError(errorMsg, AmortConstant.EMIRounding, "ERR_VALUE_INVALID");
			}

			// Other Rounding
			String OtherRounding = amortMap.get(AmortConstant.OthersRounding).toString();
			ArrayList RoundOtherLi = ValidationList.RoundOthers(Integer.parseInt(AmortType));
			if (RoundOtherLi.contains(OtherRounding)) {
				Integer OtherRoundingVal = GenericTypeValidator.formatInt(OtherRounding, Locale.ENGLISH);
				if (null == OtherRoundingVal) {
					errorMsg = AmortCaptions.OthersRounding + AmortValidation.ERR_NUMBER_FORMAT;
					errors.addError(errorMsg, AmortConstant.OthersRounding, "ERR_NUMBER_FORMAT");
				}
			} else {
				errorMsg = AmortCaptions.OthersRounding + AmortValidation.ERR_VALUE_INVALID;
				errors.addError(errorMsg, AmortConstant.OthersRounding, "ERR_VALUE_INVALID");
			}

			// EMI Rounding Unit
			String RoundingUnit = amortMap.get(AmortConstant.EMIRoundingUnit).toString();
			if (RoundingUnit != null) {
				if (!RoundingUnit.equals("")) {
					Integer RoundingUnitVal = GenericTypeValidator.formatInt(RoundingUnit, Locale.ENGLISH);
					if (null == RoundingUnitVal) {
						errorMsg = AmortCaptions.EMIRoundingUnit + AmortValidation.ERR_NUMBER_FORMAT;
						errors.addError(errorMsg, AmortConstant.EMIRoundingUnit, "ERR_NUMBER_FORMAT");
					}
				}
			} else {
				errorMsg = AmortCaptions.EMIRoundingUnit + AmortValidation.ERR_ISNULL;
				errors.addError(errorMsg, AmortConstant.EMIRoundingUnit);
			}

			// TDS
			String strDeductTDS = amortMap.get(AmortConstant.deductTDS).toString();
			if (strDeductTDS != null
					&& !("Y".equalsIgnoreCase(strDeductTDS.trim()) || "YES".equalsIgnoreCase(strDeductTDS.trim())
							|| "1".equals(strDeductTDS.trim()) || "false".equalsIgnoreCase(strDeductTDS.trim())
							|| "true".equalsIgnoreCase(strDeductTDS.trim()) || "N".equalsIgnoreCase(strDeductTDS.trim())
							|| "NO".equalsIgnoreCase(strDeductTDS.trim()) || "0".equals(strDeductTDS.trim()))) {
				errorMsg = AmortCaptions.TDS + AmortValidation.ERR_ISINVALID
						+ " should be one of N, Y, NO, YES, 0, 1 (case insensitive)";
				errors.addError(errorMsg, AmortConstant.deductTDS, "ERR_ISINVALID");
			}
			// TDS Percentage
			if (strDeductTDS != null && ("Y".equalsIgnoreCase(strDeductTDS.trim())
					|| "YES".equalsIgnoreCase(strDeductTDS.trim()) || "1".equals(strDeductTDS.trim()))) {
				Double TDSPercentage = GenericTypeValidator
						.formatDouble((String) amortMap.get(AmortConstant.TDSPercentage), Locale.ENGLISH);
				if (null == TDSPercentage) {
					errorMsg = AmortCaptions.TDSPercentage + AmortValidation.ERR_NUMBER_FORMAT;
					errors.addError(errorMsg, AmortConstant.TDSPercentage, "ERR_ISINVALID");
				}
			}

			// EMI Rounding To
			String RoundingTo = amortMap.get(AmortConstant.EMIRoundingTo).toString();

			if (RoundingTo != null) {
				if (!RoundingTo.equals("")) {
					Integer RoundingToVal = GenericTypeValidator.formatInt(RoundingTo, Locale.ENGLISH);
					if (null == RoundingToVal) {
						errorMsg = AmortCaptions.EMIRoundingTo + AmortValidation.ERR_NUMBER_FORMAT;
						errors.addError(errorMsg, AmortConstant.EMIRoundingTo, "ERR_NUMBER_FORMAT");
					}
				}
			} else {
				errorMsg = AmortCaptions.EMIRoundingTo + AmortValidation.ERR_ISNULL;
				errors.addError(errorMsg, AmortConstant.EMIRoundingTo);
			}

			// EMI Rounding Part
			String RoundingPart = amortMap.get(AmortConstant.EMIRoundingPart).toString();
			if (RoundingPart != null) {
				if (!RoundingPart.equals("")) {
					Integer RoundingPartVal = GenericTypeValidator.formatInt(RoundingPart, Locale.ENGLISH);
					if (null == RoundingPartVal) {
						errorMsg = AmortCaptions.EMIRoundingPart + AmortValidation.ERR_NUMBER_FORMAT;
						errors.addError(errorMsg, AmortConstant.EMIRoundingPart, "ERR_NUMBER_FORMAT");
					}
				}
			} else {
				errorMsg = AmortCaptions.EMIRoundingPart + AmortValidation.ERR_ISNULL;
				errors.addError(errorMsg, AmortConstant.EMIRoundingPart);
			}

			// Other Rounding Unit
			String OthersRoundingUnit = amortMap.get(AmortConstant.OthersUnit).toString();
			if (OthersRoundingUnit != null) {
				if (!OthersRoundingUnit.equals("")) {
					Integer OthersRoundingUnitVal = GenericTypeValidator.formatInt(OthersRoundingUnit, Locale.ENGLISH);
					if (null == OthersRoundingUnitVal) {
						errorMsg = AmortCaptions.OthersRoundingUnit + AmortValidation.ERR_NUMBER_FORMAT;
						errors.addError(errorMsg, AmortConstant.OthersUnit, "ERR_NUMBER_FORMAT");
					}
				}
			} else {
				errorMsg = AmortCaptions.OthersRoundingUnit + AmortValidation.ERR_ISNULL;
				errors.addError(errorMsg, AmortConstant.OthersUnit);
			}

			// Other Rounding To
			String OthersRoundingTo = amortMap.get(AmortConstant.OthersRoundingTo).toString();
			if (OthersRoundingTo != null) {
				if (!OthersRoundingTo.equals("")) {
					Integer OthersRoundingToVal = GenericTypeValidator.formatInt(OthersRoundingTo, Locale.ENGLISH);
					if (null == OthersRoundingToVal) {
						errorMsg = AmortCaptions.OtherRoundingTo + AmortValidation.ERR_NUMBER_FORMAT;
						errors.addError(errorMsg, AmortConstant.OthersRoundingTo, "ERR_NUMBER_FORMAT");
					}
				}
			} else {
				errorMsg = AmortCaptions.OtherRoundingTo + AmortValidation.ERR_ISNULL;
				errors.addError(errorMsg, AmortConstant.OthersRoundingTo);
			}

			// Other Rounding Part
			String OthersRoundingPart = amortMap.get(AmortConstant.OthersRoundingPart).toString();
			if (OthersRoundingPart != null) {
				if (!OthersRoundingPart.equals("")) {
					Integer RoundingPartVal = GenericTypeValidator.formatInt(OthersRoundingPart, Locale.ENGLISH);
					if (null == RoundingPartVal) {
						errorMsg = AmortCaptions.OtherRoundingPart + AmortValidation.ERR_NUMBER_FORMAT;
						errors.addError(errorMsg, AmortConstant.OthersRoundingPart, "ERR_NUMBER_FORMAT");
					}
				}
			} else {
				errorMsg = AmortCaptions.OtherRoundingPart + AmortValidation.ERR_ISNULL;
				errors.addError(errorMsg, AmortConstant.OthersRoundingPart);
			}

			// Advance EMI No
			String AdvEMINo = amortMap.get(AmortConstant.AdvEMINo).toString();
			if (AdvEMINo != null) {
				if (!AdvEMINo.equals("")) {
					Integer AdvEMINoVal = GenericTypeValidator.formatInt(AdvEMINo, Locale.ENGLISH);
					if (null != AdvEMINoVal && AdvEMINoVal.intValue() > 0) {
						String AdvEMIAdj = amortMap.get(AmortConstant.AdvEMIAdj).toString();
						if (AdvEMIAdj != null && !AdvEMIAdj.equals("")) {
							ArrayList AdvEMILi = ValidationList.AdvEMI(Integer.parseInt(AmortType));
							if (AdvEMILi.contains(AdvEMIAdj)) {
								Integer AdvEMIAdjVal = GenericTypeValidator.formatInt(AdvEMIAdj, Locale.ENGLISH);
								if (null == AdvEMIAdjVal) {
									errorMsg = AmortCaptions.AdvEMIAdj + AmortValidation.ERR_NUMBER_FORMAT;
									errors.addError(errorMsg, AmortConstant.AdvEMIAdj, "ERR_NUMBER_FORMAT");
								}
							} else {
								errorMsg = AmortCaptions.AdvEMIAdj + AmortValidation.ERR_VALUE_INVALID;
								errors.addError(errorMsg, AmortConstant.AdvEMIAdj, "ERR_VALUE_INVALID");
							}
						} else {
							errorMsg = AmortCaptions.AdvEMIAdj + AmortValidation.ERR_MANDETORY;
							errors.addError(errorMsg, AmortConstant.AdvEMIAdj, "ERR_MANDETORY");
						}
					} else {
						if (AdvEMINoVal != null && 0 > (AdvEMINoVal.intValue())) {
							errorMsg = AmortCaptions.AdvEMINo + AmortValidation.ERR_NUMBER_FORMAT;
							errors.addError(errorMsg, AmortConstant.AdvEMINo, "ERR_NUMBER_FORMAT");
						} else if (AdvEMINoVal != null && AdvEMINo.length() > AmortValidation.MAX_LENGTH) {
							errorMsg = AmortCaptions.AdvEMINo + AmortValidation.ERR_LENGTH_EXCEED;
							errors.addError(errorMsg, AmortConstant.AdvEMINo, "ERR_LENGTH_EXCEED");
						}
					}
				}
			} else {
				errorMsg = AmortCaptions.AdvEMINo + AmortValidation.ERR_ISNULL;
				errors.addError(errorMsg, AmortConstant.AdvEMINo);
			}

			// Compound Frequency
			String compFreq = amortMap.get(AmortConstant.compFreq).toString();
			if (compFreq != null && compFreq.equals("-1") && (!compFreq.equals(""))) { // &&
																						// Integer.parseInt(AmortType)==0){
				ArrayList CompoundFreqLi = ValidationList.CompoundFreq(Integer.parseInt(AmortType));
				if (CompoundFreqLi.contains(compFreq)) {
					Integer compFreqVal = GenericTypeValidator.formatInt(compFreq, Locale.ENGLISH);
					if (null == compFreqVal) {
						errorMsg = AmortCaptions.compFreq + AmortValidation.ERR_NUMBER_FORMAT;
						errors.addError(errorMsg, AmortConstant.compFreq, "ERR_NUMBER_FORMAT");
					}
				} else {
					errorMsg = AmortCaptions.compFreq + AmortValidation.ERR_VALUE_INVALID + Integer.parseInt(compFreq);
					errors.addError(errorMsg, AmortConstant.compFreq, "ERR_VALUE_INVALID");
				}
			}

			// Interest Only
			String InterestOnly = amortMap.get(AmortConstant.InterestOnly).toString();
			if (InterestOnly != null && (!InterestOnly.equals("")) && Integer.parseInt(AmortType) == 0) {
				Integer InterestOnlyVal = GenericTypeValidator.formatInt(InterestOnly, Locale.ENGLISH);
				if (null == InterestOnlyVal) {
					errorMsg = AmortCaptions.InterestOnly + AmortValidation.ERR_NUMBER_FORMAT;
					errors.addError(errorMsg, AmortConstant.InterestOnly, "ERR_NUMBER_FORMAT");
				}

			}

			// BPI Recovery
			String BPIRecovery = amortMap.get(AmortConstant.BPIRecovery).toString();
			if (BPIRecovery != null && !BPIRecovery.equals("") && !BPIRecovery.equals("-1")) {
				ArrayList BPIRecoveryLi = ValidationList.BPIRecovery(Integer.parseInt(AmortType));
				if (BPIRecoveryLi.contains(BPIRecovery)) {
					Integer BPIRecoveryVal = GenericTypeValidator.formatInt(BPIRecovery, Locale.ENGLISH);
					if (null == BPIRecoveryVal) {
						errorMsg = AmortCaptions.BPIRecovery + AmortValidation.ERR_MANDETORY;
						errors.addError(errorMsg, AmortConstant.BPIRecovery, "ERR_MANDETORY");
					}
				} else {
					errorMsg = AmortCaptions.BPIRecovery + AmortValidation.ERR_VALUE_INVALID;
					errors.addError(errorMsg, AmortConstant.BPIRecovery, "ERR_VALUE_INVALID");
				}
			}

			// Loan Amount
			String loanAmt = amortMap.get(AmortConstant.LoanAmount).toString();
			if (loanAmt != null && !loanAmt.equals("")) {
				Double loanVal = GenericTypeValidator.formatDouble(loanAmt, Locale.ENGLISH);
				if (null == loanVal) {
					errorMsg = AmortCaptions.LoanAmount + AmortValidation.ERR_NUMBER_FORMAT;
					errors.addError(errorMsg, AmortConstant.LoanAmount, "ERR_NUMBER_FORMAT");
				} else {
					if (0 > (loanVal.intValue())) {
						errorMsg = AmortCaptions.LoanAmount + AmortValidation.ERR_INVALID_NUMBER;
						errors.addError(errorMsg, AmortConstant.LoanAmount, "ERR_INVALID_NUMBER");
					} else if (loanAmt.length() > AmortValidation.MAX_LENGTH) {
						errorMsg = AmortCaptions.LoanAmount + AmortValidation.ERR_LENGTH_EXCEED;
						errors.addError(errorMsg, AmortConstant.LoanAmount, "ERR_LENGTH_EXCEED");
					}
				}
			} else {
				errorMsg = AmortCaptions.LoanAmount + AmortValidation.ERR_MANDETORY;
				errors.addError(errorMsg, AmortConstant.LoanAmount, "ERR_MANDETORY");
			}
			// Tenor in :Tenor defined in month/year/week
			String Tenor_In = amortMap.get(AmortConstant.tenor_in).toString();
			Integer Tenor_InVal = null;
			if (Tenor_In != null && !Tenor_In.equals("") && !Tenor_In.equals("-1")) {
				Tenor_InVal = GenericTypeValidator.formatInt(Tenor_In, Locale.ENGLISH);
				if (null == Tenor_InVal) {
					errorMsg = AmortCaptions.TenorIn + AmortValidation.ERR_NUMBER_FORMAT;
					errors.addError(errorMsg, AmortConstant.tenor_in, "ERR_NUMBER_FORMAT");
				}
			} else {
				errorMsg = AmortCaptions.TenorIn + AmortValidation.ERR_MANDETORY;
				errors.addError(errorMsg, AmortConstant.tenor_in, "ERR_MANDETORY");
			}

			// Tenor
			String Tenor = amortMap.get(AmortConstant.Tenor).toString();
			if (Tenor != null && !Tenor.equals("") && !Tenor.equals("-1")) {
				Double TenorVal = GenericTypeValidator.formatDouble(Tenor, Locale.ENGLISH);
				if (null == TenorVal) {
					errorMsg = AmortCaptions.Tenor + AmortValidation.ERR_NUMBER_FORMAT;
					errors.addError(errorMsg, AmortConstant.Tenor, "ERR_NUMBER_FORMAT");
					amortFlag = false;
				} else {
					if (0 > (TenorVal.intValue())) {
						errorMsg = AmortCaptions.Tenor + AmortValidation.ERR_INVALID_NUMBER;
						errors.addError(errorMsg, AmortConstant.Tenor, "ERR_INVALID_NUMBER");
						amortFlag = false;
					} else if (Tenor.length() > AmortValidation.MAX_LENGTH) {
						errorMsg = AmortCaptions.Tenor + AmortValidation.ERR_LENGTH_EXCEED;
						errors.addError(errorMsg, AmortConstant.Tenor, "ERR_LENGTH_EXCEED");
						amortFlag = false;
					} else if (Tenor_InVal != null && Tenor_InVal.intValue() != AmortConstant.tenor_inyear
							&& FreqFactor != 0) {
						if ((TenorVal.intValue() % FreqFactor) != 0) {
							if (Integer.parseInt(RepaymentFrequency) != 26
									|| Integer.parseInt(RepaymentFrequency) != 52) {
								if (Integer.parseInt(RepaymentFrequency) != 2) {
									// errorMsg = "Tenor is not valid accrding to Repayment Frequency";
									// errors.addError(errorMsg, AmortConstant.Tenor);
								}
							}
						} else {
							if (amortFlag)
								amortFlag = true;
						}
					}
				}
			} else {
				errorMsg = AmortCaptions.Tenor + AmortValidation.ERR_MANDETORY;
				errors.addError(errorMsg, AmortConstant.Tenor, "ERR_MANDETORY");
				amortFlag = false;
			}
			// Interest Rate
			String InterestRate = amortMap.get(AmortConstant.InterestRate).toString();
			if (InterestRate != null && !InterestRate.equals("") && !InterestRate.equals("-1")) {
				Double InterestRateVal = GenericTypeValidator.formatDouble(InterestRate, Locale.ENGLISH);
				if (null == InterestRateVal) {
					errorMsg = AmortCaptions.InterestRate + AmortValidation.ERR_NUMBER_FORMAT;
					errors.addError(errorMsg, AmortConstant.InterestRate, "ERR_NUMBER_FORMAT");
				} else {
					if (0 > (InterestRateVal.intValue())) {
						errorMsg = AmortCaptions.InterestRate + AmortValidation.ERR_INVALID_NUMBER;
						errors.addError(errorMsg, AmortConstant.InterestRate, "ERR_INVALID_NUMBER");
					} else if (InterestRate.length() > AmortValidation.MAX_LENGTH) {
						errorMsg = AmortCaptions.InterestRate + AmortValidation.ERR_LENGTH_EXCEED;
						errors.addError(errorMsg, AmortConstant.InterestRate, "ERR_LENGTH_EXCEED");
					}
				}
			}

			// Date Format
			boolean dFlag = false;
			String dateformat = amortMap.get(AmortConstant.dateformat).toString();
			if (dateformat == null || dateformat.equals("")) {
				dFlag = true;
				errorMsg = AmortCaptions.dateformat + AmortValidation.ERR_MANDETORY;
				errors.addError(errorMsg, AmortCaptions.dateformat, "ERR_MANDETORY");
			}
			// Date Of Disbursement
			String DateOfDisbursement = amortMap.get(AmortConstant.DateOfDisbursement).toString();
			String dateFormat = DateFormats.defaultDateFormat1;
			if (amortMap.containsKey(AmortConstant.dateformat)) {
				dateFormat = (String) amortMap.get(AmortConstant.dateformat);
			}
			Date DateOfDisbursementVal = null;
			if (DateOfDisbursement != null && !DateOfDisbursement.equals("") && !DateOfDisbursement.equals("-1")) {
				DateOfDisbursementVal = GenericTypeValidator.formatDate(DateOfDisbursement, dateFormat, true);
				if (null == DateOfDisbursementVal) {
					errorMsg = AmortCaptions.DateOfDisbursement + AmortValidation.ERR_DATE_FORMAT;
					errors.addError(errorMsg, AmortConstant.DateOfDisbursement, "ERR_DATE_FORMAT");
				}
			} else {
				errorMsg = AmortCaptions.DateOfDisbursement + AmortValidation.ERR_MANDETORY;
				errors.addError(errorMsg, AmortConstant.DateOfDisbursement, "ERR_MANDETORY");
			}
			// Date Of Cycle
			String DateOfCycle = amortMap.get(AmortConstant.DateOfCycle).toString();
			Date DateOfCycleVal = null;
			if (DateOfCycle != null && !DateOfCycle.equals("") && !DateOfCycle.equals("-1")) {
				DateOfCycleVal = GenericTypeValidator.formatDate(DateOfCycle, dateFormat, true);
				if (null == DateOfCycleVal) {
					errorMsg = AmortCaptions.DateOfCycle + AmortValidation.ERR_DATE_FORMAT;
					errors.addError(errorMsg, AmortConstant.DateOfCycle, "ERR_DATE_FORMAT");
				}
			} else {
				errorMsg = AmortCaptions.DateOfCycle + AmortValidation.ERR_MANDETORY;
				errors.addError(errorMsg, AmortConstant.DateOfCycle, "ERR_MANDETORY");
			}

			if (null != DateOfCycle && null != DateOfDisbursement && null != DateOfDisbursementVal
					&& null != DateOfCycleVal) {
				if (!DateOfCycle.equals("-1") && !DateOfDisbursement.equals("-1") && !DateOfDisbursement.equals("")
						&& !DateOfCycle.equals("")) {
					if (dFlag)
						dateformat = DateFormats.defaultDateFormat1;
					if (compare2Dates(DateOfDisbursement, DateOfCycle, dateformat) > 0) {
						errorMsg = AmortCaptions.DateOfDisbursement + AmortValidation.ERR_DATE_INVALID
								+ AmortCaptions.DateOfCycle;
						errors.addError(errorMsg, AmortConstant.DateOfDisbursement, "ERR_DATE_INVALID");
					}
				}
			}

			// Input EMI
			String Input_EMI = amortMap.get(AmortConstant.Input_EMI).toString();
			if (Input_EMI == null || Input_EMI.equals("") || Input_EMI.equals("-1")) {
				errorMsg = AmortCaptions.Input_EMI + AmortValidation.ERR_MANDETORY;
				errors.addError(errorMsg, AmortConstant.Input_EMI, "ERR_MANDETORY");
			}

			HashMap Step_EMIHash = (HashMap) amortMap.get("Step_EMI");
			if (Step_EMIHash == null || Step_EMIHash.size() == 0) {
				if (isDebugEnabled)
					logger.info("no step up");
			} else {
				if (!AmortType.equals("")) {
					if (Integer.parseInt(AmortType) == 0) {
						String StepUpDownFrmMonth = null;
						String StepUpDownTillMonth = null;
						String StepUpDownBasis = null;
						String StepUpDownType = null;
						String StepUpDownBy = null;
						String StepUpDownAdjust = null;
						int i = 0;
						int j = 0;
						Set HostKeys = Step_EMIHash.keySet();
						Iterator It = HostKeys.iterator();
						while (It.hasNext()) {
							if (isDebugEnabled)
								logger.info("skip emi total=" + Step_EMIHash.size());
							String HostNow = (String) (It.next());
							HashMap hmStepEmi = (HashMap) Step_EMIHash.get(HostNow);

							StepUpDownFrmMonth = hmStepEmi.get(AmortConstant.StepUpDown.StepUpDownFrmMonth).toString();
							StepUpDownTillMonth = hmStepEmi.get(AmortConstant.StepUpDown.StepUpDownTillMonth)
									.toString();
							StepUpDownBasis = hmStepEmi.get(AmortConstant.StepUpDown.StepUpDownBasis).toString();
							StepUpDownType = hmStepEmi.get(AmortConstant.StepUpDown.StepUpDownType).toString();
							StepUpDownBy = hmStepEmi.get(AmortConstant.StepUpDown.StepUpDownBy).toString();
							StepUpDownAdjust = hmStepEmi.get(AmortConstant.StepUpDown.StepUpDownAdjust).toString();
							if (StepUpDownAdjust != null) {
								if (i == 0)
									j = Integer.parseInt(StepUpDownAdjust);
								if (j != Integer.parseInt(StepUpDownAdjust)) {
									errors.addError("ERR_ADJOPTIONUNIQUE",
											"AdjustOptions should be unique in all Variations.");
								}
							}
							if (StepUpDownFrmMonth.equals("") && StepUpDownTillMonth.equals("")
									&& StepUpDownBasis.equals("") && StepUpDownType.equals("")
									&& StepUpDownBy.equals("")) {
								if (isDebugEnabled)
									logger.info("no step up specified");
								errorMsg = AmortValidation.ERR_STEPUP_INVALID;
								errors.addError(errorMsg, AmortCaptions.Step_EMI, "ERR_STEPUP_INVALID");
							} else {
								if (!StepUpDownFrmMonth.equals("")) {
									Integer SUD_FM_Val = GenericTypeValidator.formatInt(StepUpDownFrmMonth,
											Locale.ENGLISH);
									if (null == SUD_FM_Val) {
										errorMsg = AmortCaptions.StepUpDownFrmMonth + AmortValidation.ERR_NUMBER_FORMAT;
										errors.addError(errorMsg, AmortConstant.StepUpDown.StepUpDownFrmMonth,
												"ERR_NUMBER_FORMAT");
									} else {
										if (0 >= (SUD_FM_Val.intValue())) {
											errorMsg = AmortCaptions.StepUpDownFrmMonth
													+ AmortValidation.ERR_INVALID_NUMBER;
											errors.addError(errorMsg, AmortConstant.StepUpDown.StepUpDownFrmMonth,
													"ERR_INVALID_NUMBER");
										} else if (StepUpDownFrmMonth.length() > AmortValidation.MAX_LENGTH) {
											errorMsg = AmortCaptions.StepUpDownFrmMonth
													+ AmortValidation.ERR_LENGTH_EXCEED;
											errors.addError(errorMsg, AmortConstant.StepUpDown.StepUpDownFrmMonth,
													"ERR_LENGTH_EXCEED");
										}
									}
								} else {
									errorMsg = AmortCaptions.StepUpDownFrmMonth + AmortValidation.ERR_MANDETORY;
									errors.addError(errorMsg, AmortConstant.StepUpDown.StepUpDownFrmMonth,
											"ERR_MANDETORY");
								}

								if (!StepUpDownTillMonth.equals("")) {
									Integer SUD_TM_Val = GenericTypeValidator.formatInt(StepUpDownTillMonth,
											Locale.ENGLISH);
									if (null == SUD_TM_Val) {
										errorMsg = AmortCaptions.StepUpDownTillMonth
												+ AmortValidation.ERR_NUMBER_FORMAT;
										errors.addError(errorMsg, AmortConstant.StepUpDown.StepUpDownTillMonth,
												"ERR_NUMBER_FORMAT");
									} else {
										if (0 >= (SUD_TM_Val.intValue())) {
											errorMsg = AmortCaptions.StepUpDownTillMonth
													+ AmortValidation.ERR_INVALID_NUMBER;
											errors.addError(errorMsg, AmortConstant.StepUpDown.StepUpDownTillMonth,
													"ERR_INVALID_NUMBER");
										} else if (StepUpDownTillMonth.length() > AmortValidation.MAX_LENGTH) {
											errorMsg = AmortCaptions.StepUpDownTillMonth
													+ AmortValidation.ERR_LENGTH_EXCEED;
											errors.addError(errorMsg, AmortConstant.StepUpDown.StepUpDownTillMonth,
													"ERR_LENGTH_EXCEED");
										}
									}
								} else {
									errorMsg = AmortCaptions.StepUpDownTillMonth + AmortValidation.ERR_MANDETORY;
									errors.addError(errorMsg, AmortConstant.StepUpDown.StepUpDownTillMonth,
											"ERR_MANDETORY");
								}
								if (!StepUpDownBasis.equals("") && !StepUpDownBasis.equals("-1")) {
									Integer SUD_B_Val = GenericTypeValidator.formatInt(StepUpDownBasis, Locale.ENGLISH);
									if (null == SUD_B_Val) {
										errorMsg = AmortCaptions.StepUpDownBasis + AmortValidation.ERR_NUMBER_FORMAT;
										errors.addError(errorMsg, AmortConstant.StepUpDown.StepUpDownBasis,
												"ERR_NUMBER_FORMAT");
									}
								} else {
									errorMsg = AmortCaptions.StepUpDownBasis + AmortValidation.ERR_MANDETORY;
									errors.addError(errorMsg, AmortConstant.StepUpDown.StepUpDownBasis,
											"ERR_MANDETORY");
								}

								if (!StepUpDownType.equals("")) {
									Integer SUD_TYP_Val = GenericTypeValidator.formatInt(StepUpDownType,
											Locale.ENGLISH);
									if (null == SUD_TYP_Val) {
										errorMsg = AmortCaptions.StepUpDownType + AmortValidation.ERR_NUMBER_FORMAT;
										errors.addError(errorMsg, AmortConstant.StepUpDown.StepUpDownType,
												"ERR_NUMBER_FORMAT");
									}
								} else {
									errorMsg = AmortCaptions.StepUpDownType + AmortValidation.ERR_MANDETORY;
									errors.addError(errorMsg, AmortConstant.StepUpDown.StepUpDownType, "ERR_MANDETORY");
								}
								if (!StepUpDownBy.equals("") && !StepUpDownBy.equals("-1")) {
									Double SUD_By_val = GenericTypeValidator.formatDouble(StepUpDownBy, Locale.ENGLISH);
									if (null == SUD_By_val) {
										errorMsg = AmortCaptions.StepUpDownBy + AmortValidation.ERR_NUMBER_FORMAT;
										errors.addError(errorMsg, AmortConstant.StepUpDown.StepUpDownBy,
												"ERR_NUMBER_FORMAT");
									} else {
										if (Integer.parseInt(StepUpDownBasis) == AmortConstant.StepUpDown.StepPrin) {
											if (0 > SUD_By_val.intValue()) {
												errorMsg = AmortCaptions.StepUpDownBy
														+ AmortValidation.ERR_INVALID_NUMBER;
												errors.addError(errorMsg, AmortConstant.StepUpDown.StepUpDownBy,
														"ERR_INVALID_NUMBER");
											}
										} else if (0 >= SUD_By_val.intValue()) {
											errorMsg = AmortCaptions.StepUpDownBy + AmortValidation.ERR_INVALID_NUMBER;
											errors.addError(errorMsg, AmortConstant.StepUpDown.StepUpDownBy,
													"ERR_INVALID_NUMBER");
										} else if (StepUpDownBy.length() > AmortValidation.MAX_LENGTH) {
											errorMsg = AmortCaptions.StepUpDownBy + AmortValidation.ERR_LENGTH_EXCEED;
											errors.addError(errorMsg, AmortConstant.StepUpDown.StepUpDownBy,
													"ERR_LENGTH_EXCEED");
										}
									}
								} else {
									errorMsg = AmortCaptions.StepUpDownBy + AmortValidation.ERR_MANDETORY;
									errors.addError(errorMsg, AmortConstant.StepUpDown.StepUpDownBy, "ERR_MANDETORY");
								}
								/*
								 * if(!StepUpDownAdjust.equals("") && !StepUpDownAdjust.equals("-1")){ Integer
								 * SUD_Adj_Val = GenericTypeValidator.formatInt(StepUpDownAdjust,
								 * Locale.ENGLISH); if (null == SUD_Adj_Val) { errorMsg =
								 * AmortCaptions.StepUpDownAdjust+AmortValidation.ERR_NUMBER_FORMAT;
								 * errors.addError(errorMsg,
								 * AmortConstant.StepUpDown.StepUpDownAdjust,"ERR_NUMBER_FORMAT"); }
								 * 
								 * } else{ errorMsg =
								 * AmortCaptions.StepUpDownAdjust+AmortValidation.ERR_MANDETORY;
								 * errors.addError(errorMsg,
								 * AmortConstant.StepUpDown.StepUpDownAdjust,"ERR_MANDETORY"); }
								 */
								if (!StepUpDownFrmMonth.equals("") && !StepUpDownTillMonth.equals("")
										&& !StepUpDownFrmMonth.equals("-1") && StepUpDownTillMonth.equals("-1")) {
									if (Integer.parseInt(StepUpDownFrmMonth) > Integer.parseInt(StepUpDownTillMonth)) {
										errorMsg = AmortCaptions.StepUpDownAdjust + AmortValidation.ERR_COMPARE
												+ AmortCaptions.StepUpDownTillMonth;
										errors.addError(errorMsg, AmortConstant.StepUpDown.StepUpDownAdjust);
									}
								}
							}
							i++;
						}
					} else {
						errorMsg = AmortValidation.ERR_AMORT_NOTPOSSIBLE + AmortCaptions.AmortType + " & "
								+ AmortCaptions.Step_EMI;
						errors.addError(errorMsg, AmortCaptions.Step_EMI, "ERR_AMORT_NOTPOSSIBLE");
					}
				}
			}

			HashMap Skip_EMIHash = (HashMap) amortMap.get("Skip_EMI");
			if (Skip_EMIHash == null || Skip_EMIHash.size() == 0) {
				if (isDebugEnabled)
					logger.info("no skip");
			} else {
				if (!AmortType.equals("")) {
					if (Integer.parseInt(AmortType) == 0) {
						Set HostKeys = Skip_EMIHash.keySet();
						Iterator It = HostKeys.iterator();
						ArrayList arrSkip = new ArrayList();
						int i = 0;
						int j = 0;
						while (It.hasNext()) {
							String HostNow = (String) (It.next());
							HashMap hmStepEmi = (HashMap) Skip_EMIHash.get(HostNow);

							String SkipFrom = null;
							String SkipNo = null;
							String SkipAdjust = null;
							String SkipCapital = null;
							if (isDebugEnabled)
								logger.info("skip emi total=" + Skip_EMIHash.size());

							SkipFrom = hmStepEmi.get(AmortConstant.SkipEMI.SkipFrom).toString();
							SkipNo = hmStepEmi.get(AmortConstant.SkipEMI.SkipNo).toString();
							SkipAdjust = hmStepEmi.get(AmortConstant.SkipEMI.SkipAdjust).toString();
							SkipCapital = hmStepEmi.get(AmortConstant.SkipEMI.SkipCapital).toString();
							if (SkipAdjust != null) {
								if (i == 0)
									j = Integer.parseInt(SkipAdjust);
								if (j != Integer.parseInt(SkipAdjust)) {
									errors.addError("ERR_ADJOPTIONUNIQUE",
											"AdjustOptions should be unique in all Variations.");
								}
							}
							if (SkipFrom.equals("") && SkipNo.equals("") /* && !SkipCapital.equals("") */) {
								if (isDebugEnabled)
									logger.info("no skip specified");
								errorMsg = AmortValidation.ERR_SKIP_INVALID;
								errors.addError(errorMsg, AmortCaptions.Skip_EMI, "ERR_SKIP_INVALID");
							} else {
								if (!SkipFrom.equals("")) {
									Integer SkipFrom_val = GenericTypeValidator.formatInt(SkipFrom, Locale.ENGLISH);
									if (null == SkipFrom_val) {
										errorMsg = AmortCaptions.SkipFrom + AmortValidation.ERR_NUMBER_FORMAT;
										errors.addError(errorMsg, AmortConstant.SkipEMI.SkipFrom, "ERR_NUMBER_FORMAT");
									} else {
										if (0 >= (SkipFrom_val.intValue())) {
											errorMsg = AmortCaptions.SkipFrom + AmortValidation.ERR_INVALID_NUMBER;
											errors.addError(errorMsg, AmortConstant.SkipEMI.SkipFrom,
													"ERR_INVALID_NUMBER");
										} else if (SkipFrom.length() > AmortValidation.MAX_LENGTH) {
											errorMsg = AmortCaptions.SkipFrom + AmortValidation.ERR_LENGTH_EXCEED;
											errors.addError(errorMsg, AmortConstant.SkipEMI.SkipFrom,
													"ERR_LENGTH_EXCEED");
										}
									}
								} else {
									errorMsg = AmortCaptions.SkipFrom + AmortValidation.ERR_MANDETORY;
									errors.addError(errorMsg, AmortConstant.SkipEMI.SkipFrom, "ERR_MANDETORY");
								}
								if (!SkipNo.equals("")) {
									Integer SkipNo_Val = GenericTypeValidator.formatInt(SkipNo, Locale.ENGLISH);
									if (null == SkipNo_Val) {
										errorMsg = AmortCaptions.SkipNo + AmortValidation.ERR_NUMBER_FORMAT;
										errors.addError(errorMsg, AmortConstant.SkipEMI.SkipNo, "ERR_NUMBER_FORMAT");
									} else {
										if (0 >= (SkipNo_Val.intValue())) {
											errorMsg = AmortCaptions.SkipNo + AmortValidation.ERR_INVALID_NUMBER;
											errors.addError(errorMsg, AmortConstant.SkipEMI.SkipNo,
													"ERR_INVALID_NUMBER");
										} else if (SkipNo.length() > AmortValidation.MAX_LENGTH) {
											errorMsg = AmortCaptions.SkipNo + AmortValidation.ERR_LENGTH_EXCEED;
											errors.addError(errorMsg, AmortConstant.SkipEMI.SkipNo,
													"ERR_LENGTH_EXCEED");
										}
									}
								} else {
									errorMsg = AmortCaptions.SkipNo + AmortValidation.ERR_MANDETORY;
									errors.addError(errorMsg, AmortConstant.SkipEMI.SkipNo, "ERR_MANDETORY");
								}
								/*
								 * if(!SkipAdjust.equals("")){ Integer SkipAdjust_Val =
								 * GenericTypeValidator.formatInt(SkipAdjust, Locale.ENGLISH); if (null ==
								 * SkipAdjust_Val) { errorMsg =
								 * AmortCaptions.SkipAdjust+AmortValidation.ERR_NUMBER_FORMAT;
								 * errors.addError(errorMsg,
								 * AmortConstant.SkipEMI.SkipAdjust,"ERR_NUMBER_FORMAT"); } } else { errorMsg =
								 * AmortCaptions.SkipAdjust+AmortValidation.ERR_MANDETORY;
								 * errors.addError(errorMsg, AmortConstant.SkipEMI.SkipAdjust,"ERR_MANDETORY");
								 * }
								 */

							}
							i++;
						}
					} else {
						errorMsg = AmortValidation.ERR_AMORT_NOTPOSSIBLE + AmortCaptions.AmortType + " & "
								+ AmortCaptions.Skip_EMI;
						errors.addError(errorMsg, AmortCaptions.Skip_EMI, "ERR_AMORT_NOTPOSSIBLE");
					}
				}
			}
			// Skip EMI End

			// validation for % of principle Recovery
			HashMap PrincRecv_EMIHash = (HashMap) amortMap.get("stepprin");
			if (PrincRecv_EMIHash == null || PrincRecv_EMIHash.size() == 0) {
				if (isDebugEnabled)
					logger.info("no @ principle Recovery");
			} else {
				if (!AmortType.equals("")) {
					if (Integer.parseInt(AmortType) == 0) {
						String StepPrincFrmMonth = null;
						String StepPrincToMonth = null;
						String StepPrincIn = null;
						String StepPrincBy = null;
						String StepPrincFreq = null;
						String StepPrincRate = null;
						Set HostKeys = PrincRecv_EMIHash.keySet();
						Iterator It = HostKeys.iterator();
						while (It.hasNext()) {
							if (isDebugEnabled)
								logger.info("skip emi total=" + PrincRecv_EMIHash.size());
							String HostNow = (String) (It.next());
							HashMap hmPricRecvEmi = (HashMap) PrincRecv_EMIHash.get(HostNow);
							StepPrincFrmMonth = hmPricRecvEmi.get(AmortConstant.PerPrinRecv.StepPrincFrmMonth)
									.toString();
							StepPrincToMonth = hmPricRecvEmi.get(AmortConstant.PerPrinRecv.StepPrincToMonth).toString();
							StepPrincIn = hmPricRecvEmi.get(AmortConstant.PerPrinRecv.StepPrincIn).toString();
							StepPrincBy = hmPricRecvEmi.get(AmortConstant.PerPrinRecv.StepPrincBy).toString();
							StepPrincFreq = hmPricRecvEmi.get(AmortConstant.PerPrinRecv.StepPrincFreq).toString();
							StepPrincRate = hmPricRecvEmi.get(AmortConstant.PerPrinRecv.StepPrincRate).toString();

							if (StepPrincFrmMonth.equals("") && StepPrincToMonth.equals("") && StepPrincIn.equals("")
									&& StepPrincBy.equals("")) {
								if (isDebugEnabled)
									logger.info("no % princple recovery steps specified");
								errorMsg = AmortValidation.ERR_PERPRINCRECV_INVALID;
								errors.addError(errorMsg, AmortCaptions.stepprin, "ERR_PERPRINCRECV_INVALID");
							} else {

								// StepPrincFrmMonth
								if (!StepPrincFrmMonth.equals("")) {
									Integer SUD_FM_Val = GenericTypeValidator.formatInt(StepPrincFrmMonth,
											Locale.ENGLISH);
									if (null == SUD_FM_Val) {
										errorMsg = AmortCaptions.StepPrincFrmMonth + AmortValidation.ERR_NUMBER_FORMAT;
										errors.addError(errorMsg, AmortConstant.PerPrinRecv.StepPrincFrmMonth,
												"ERR_NUMBER_FORMAT");
									} else {
										if (0 >= (SUD_FM_Val.intValue())) {
											errorMsg = AmortCaptions.StepPrincFrmMonth
													+ AmortValidation.ERR_INVALID_NUMBER;
											errors.addError(errorMsg, AmortConstant.PerPrinRecv.StepPrincFrmMonth,
													"ERR_INVALID_NUMBER");
										} else if (StepPrincFrmMonth.length() > AmortValidation.MAX_LENGTH) {
											errorMsg = AmortCaptions.StepPrincFrmMonth
													+ AmortValidation.ERR_LENGTH_EXCEED;
											errors.addError(errorMsg, AmortConstant.PerPrinRecv.StepPrincFrmMonth,
													"ERR_LENGTH_EXCEED");
										}
									}
								} else {
									errorMsg = AmortCaptions.StepPrincFrmMonth + AmortValidation.ERR_MANDETORY;
									errors.addError(errorMsg, AmortConstant.PerPrinRecv.StepPrincFrmMonth,
											"ERR_MANDETORY");
								}

								// StepPrincToMonth

								if (!StepPrincToMonth.equals("")) {
									Integer SUD_FM_Val = GenericTypeValidator.formatInt(StepPrincToMonth,
											Locale.ENGLISH);
									if (null == SUD_FM_Val) {
										errorMsg = AmortCaptions.StepPrincToMonth + AmortValidation.ERR_NUMBER_FORMAT;
										errors.addError(errorMsg, AmortConstant.PerPrinRecv.StepPrincToMonth,
												"ERR_NUMBER_FORMAT");
									} else {
										if (0 >= (SUD_FM_Val.intValue())) {
											errorMsg = AmortCaptions.StepPrincToMonth
													+ AmortValidation.ERR_INVALID_NUMBER;
											errors.addError(errorMsg, AmortConstant.PerPrinRecv.StepPrincToMonth,
													"ERR_INVALID_NUMBER");
										} else if (StepPrincToMonth.length() > AmortValidation.MAX_LENGTH) {
											errorMsg = AmortCaptions.StepPrincToMonth
													+ AmortValidation.ERR_LENGTH_EXCEED;
											errors.addError(errorMsg, AmortConstant.PerPrinRecv.StepPrincToMonth,
													"ERR_LENGTH_EXCEED");
										}
									}
								} else {
									errorMsg = AmortCaptions.StepPrincToMonth + AmortValidation.ERR_MANDETORY;
									errors.addError(errorMsg, AmortConstant.PerPrinRecv.StepPrincToMonth,
											"ERR_MANDETORY");
								}

								// StepPrincIn
								if (!StepPrincIn.equals("")) {
									Integer SUD_B_Val = GenericTypeValidator.formatInt(StepPrincIn, Locale.ENGLISH);
									if (null == SUD_B_Val) {
										errorMsg = AmortCaptions.StepPrincIn + AmortValidation.ERR_NUMBER_FORMAT;
										errors.addError(errorMsg, AmortConstant.PerPrinRecv.StepPrincIn,
												"ERR_NUMBER_FORMAT");
									}
								} else {
									errorMsg = AmortCaptions.StepPrincIn + AmortValidation.ERR_MANDETORY;
									errors.addError(errorMsg, AmortConstant.PerPrinRecv.StepPrincIn, "ERR_MANDETORY");
								}

								// StepPrincFreq
								if (!(StepPrincFreq == null)) {
									Integer SUD_FR_Val = GenericTypeValidator.formatInt(StepPrincFreq, Locale.ENGLISH);
									if (null == SUD_FR_Val) {
										errorMsg = AmortCaptions.StepPrincFreq + AmortValidation.ERR_NUMBER_FORMAT;
										errors.addError(errorMsg, AmortConstant.PerPrinRecv.StepPrincFreq,
												"ERR_NUMBER_FORMAT");
									}
								}

								// StepPrincRate
								if (!(StepPrincRate == null)) {
									Double SUD_RT_Val = GenericTypeValidator.formatDouble(StepPrincRate,
											Locale.ENGLISH);
									if (null == SUD_RT_Val) {
										errorMsg = AmortCaptions.StepPrincRate + AmortValidation.ERR_NUMBER_FORMAT;
										errors.addError(errorMsg, AmortConstant.PerPrinRecv.StepPrincRate,
												"ERR_NUMBER_FORMAT");
									}
								}

								// StepPrincBy
								if (!StepPrincBy.equals("")) {
									Double SUD_FM_Val = GenericTypeValidator.formatDouble(StepPrincBy, Locale.ENGLISH);
									if (null == SUD_FM_Val) {
										errorMsg = AmortCaptions.StepPrincBy + AmortValidation.ERR_NUMBER_FORMAT;
										errors.addError(errorMsg, AmortConstant.PerPrinRecv.StepPrincBy,
												"ERR_NUMBER_FORMAT");
									} else {
										if (0 >= (SUD_FM_Val.intValue())) {
											errorMsg = AmortCaptions.StepPrincBy + AmortValidation.ERR_INVALID_NUMBER;
											errors.addError(errorMsg, AmortConstant.PerPrinRecv.StepPrincBy,
													"ERR_INVALID_NUMBER");
										} else if (StepPrincBy.length() > AmortValidation.MAX_LENGTH) {
											errorMsg = AmortCaptions.StepPrincBy + AmortValidation.ERR_LENGTH_EXCEED;
											errors.addError(errorMsg, AmortConstant.PerPrinRecv.StepPrincBy,
													"ERR_LENGTH_EXCEED");
										}
									}
								} else {
									errorMsg = AmortCaptions.StepPrincBy + AmortValidation.ERR_MANDETORY;
									errors.addError(errorMsg, AmortConstant.PerPrinRecv.StepPrincBy, "ERR_MANDETORY");
								}
								if (!StepPrincFrmMonth.equals("") && !StepPrincToMonth.equals("")
										&& !StepPrincFrmMonth.equals("-1") && !StepPrincToMonth.equals("-1")) {

									if (Integer.parseInt(StepPrincFrmMonth) > Integer.parseInt(StepPrincToMonth)) {
										errorMsg = AmortCaptions.StepPrincFrmMonth + AmortValidation.ERR_COMPARE
												+ AmortCaptions.StepPrincFrmMonth;
										errors.addError(errorMsg, AmortConstant.PerPrinRecv.StepPrincFrmMonth);
									}
								}
							}
						}

					}
				} else {
					errorMsg = AmortValidation.ERR_AMORT_NOTPOSSIBLE + AmortCaptions.AmortType + " & "
							+ AmortCaptions.stepprin;
					errors.addError(errorMsg, AmortCaptions.stepprin, "ERR_AMORT_NOTPOSSIBLE");
				}

			}
//end of % of principle of recovery 

			// BP EMI
			HashMap BPHash = (HashMap) amortMap.get("BP");
			if (BPHash == null || BPHash.size() == 0) {
				if (isDebugEnabled)
					logger.info("no BP");
			} else {
				if (!AmortType.equals("")) {
					if (Integer.parseInt(AmortType) == 0) {
						Set HostKeys = BPHash.keySet();
						Iterator It = HostKeys.iterator();
						int i = 0;
						int j = 0;
						while (It.hasNext()) {
							String HostNow = It.next().toString();
							HashMap hmBP = (HashMap) BPHash.get(HostNow);
							if (isDebugEnabled)
								logger.info("BP emi total=" + BPHash.size());
							String BalloonMonth = null;
							String BalloonAmount = null;
							String BallonAdj = null;
							BalloonMonth = hmBP.get(AmortConstant.Balloon.BalloonMonth).toString();
							BalloonAmount = hmBP.get(AmortConstant.Balloon.BalloonAmount).toString();
							BallonAdj = hmBP.get(AmortConstant.Balloon.BallonAdj).toString();
							if (BallonAdj != null) {
								if (i == 0)
									j = Integer.parseInt(BallonAdj);
								if (j != Integer.parseInt(BallonAdj)) {
									errors.addError("ERR_ADJOPTIONUNIQUE",
											"AdjustOptions should be unique in all Variations.");
								}
							}
							if (BalloonMonth.equals("") && BalloonAmount.equals("")) {
								if (isDebugEnabled)
									logger.info("no balloon specified");
								errorMsg = AmortValidation.ERR_BALLOON_INVALID;
								errors.addError(errorMsg, AmortCaptions.BP, "ERR_BALLOON_INVALID");
							} else {
								if (!BalloonMonth.equals("")) {
									Integer BM_Val = GenericTypeValidator.formatInt(BalloonMonth, Locale.ENGLISH);
									if (null == BM_Val) {
										errorMsg = AmortCaptions.BPMonth + AmortValidation.ERR_NUMBER_FORMAT;
										errors.addError(errorMsg, AmortConstant.Balloon.BalloonMonth,
												"ERR_NUMBER_FORMAT");
									} else {
										if (0 >= (BM_Val.intValue())) {
											errorMsg = AmortCaptions.BPMonth + AmortValidation.ERR_INVALID_NUMBER;
											errors.addError(errorMsg, AmortConstant.Balloon.BalloonMonth,
													"ERR_INVALID_NUMBER");
										} else if (BalloonMonth.length() > AmortValidation.MAX_LENGTH) {
											errorMsg = AmortCaptions.BPMonth + AmortValidation.ERR_LENGTH_EXCEED;
											errors.addError(errorMsg, AmortConstant.Balloon.BalloonMonth,
													"ERR_LENGTH_EXCEED");
										}
									}
								} else {
									errorMsg = AmortCaptions.BPMonth + AmortValidation.ERR_MANDETORY;
									errors.addError(errorMsg, AmortConstant.Balloon.BalloonMonth, "ERR_MANDETORY");
								}
								if (!BalloonAmount.equals("")) {
									Double BA_Val = GenericTypeValidator.formatDouble(BalloonAmount, Locale.ENGLISH);
									if (null == BA_Val) {
										errorMsg = AmortCaptions.BP_Amount + AmortValidation.ERR_NUMBER_FORMAT;
										errors.addError(errorMsg, AmortConstant.Balloon.BalloonAmount,
												"ERR_NUMBER_FORMAT");
									} else {
										if (0 >= (BA_Val.intValue())) {
											errorMsg = AmortCaptions.BP_Amount + AmortValidation.ERR_INVALID_NUMBER;
											errors.addError(errorMsg, AmortConstant.Balloon.BalloonAmount,
													"ERR_INVALID_NUMBER");
										} else if (BalloonAmount.length() > AmortValidation.MAX_LENGTH) {
											errorMsg = AmortCaptions.BP_Amount + AmortValidation.ERR_LENGTH_EXCEED;
											errors.addError(errorMsg, AmortConstant.Balloon.BalloonAmount,
													"ERR_LENGTH_EXCEED");
										}
									}
								} else {
									errorMsg = AmortCaptions.BP_Amount + AmortValidation.ERR_MANDETORY;
									errors.addError(errorMsg, AmortConstant.Balloon.BalloonAmount, "ERR_MANDETORY");
								}
								/*
								 * if(!BallonAdj.equals("") && !BallonAdj.equals("-1") ){ Integer BallonAdj_Val
								 * = GenericTypeValidator.formatInt(BallonAdj, Locale.ENGLISH); if (null ==
								 * BallonAdj_Val) { errorMsg =
								 * AmortCaptions.BP_Adjust+AmortValidation.ERR_NUMBER_FORMAT;
								 * errors.addError(errorMsg, AmortConstant.Balloon.BallonAdj,"ERR_MANDETORY"); }
								 * } else{ errorMsg = AmortCaptions.BP_Adjust+AmortValidation.ERR_MANDETORY;
								 * errors.addError(errorMsg, AmortConstant.Balloon.BallonAdj,"ERR_MANDETORY"); }
								 */
							}
							i++;
						}
					} else {
						errorMsg = AmortValidation.ERR_AMORT_NOTPOSSIBLE + AmortCaptions.AmortType + " & "
								+ AmortCaptions.BP;
						errors.addError(errorMsg, AmortCaptions.BP, "ERR_AMORT_NOTPOSSIBLE");
					}
				}
			}
			// BP EMI End
			// NPV Start
			HashMap NPVHash = (HashMap) amortMap.get("NPVROWS");
			boolean isError = false;
			if (NPVHash == null || NPVHash.size() == 0) {
				if (isDebugEnabled)
					logger.info("no NPV");
			} else {
				if (!AmortType.equals("")) {
					if (Integer.parseInt(AmortType) == 0) {
						Set HostKeys = NPVHash.keySet();
						Iterator It = HostKeys.iterator();

						while (It.hasNext()) {
							String HostNow = It.next().toString();
							HashMap hmBP = (HashMap) NPVHash.get(HostNow);
							if (isDebugEnabled)
								logger.info("NPV total=" + NPVHash.size());
							String Amount = null;
							String Installment_No = null;
							Amount = hmBP.get(AmortConstant.NPVROWS.Amount).toString();
							Installment_No = hmBP.get(AmortConstant.NPVROWS.Installment_No).toString();
							if (Amount == null || Installment_No == null) {
								if (isDebugEnabled)
									logger.info("no Amount specified");
								errorMsg = AmortValidation.ERR_NPV;
								errors.addError(errorMsg, "NPV", "ERR_NPV");
								if (errors != null && errors.getSize() > 0) {
									for (int i = 0; i < errors.getSize(); i++) {
										errors.getError(i).setErrorNo(Integer.toString(i));

									}
									if (logger.isInfoEnabled())
										logger.info(progName + "Amort :Errors found during validation. Errors XML="
												+ errors.toJsonString());
									resultValues = HashMapUtils.getReturnHashMap("false", "valErrors", errors);
								}
								isError = true;
							} else {

								if (!Amount.equals("")) {
									Double BA_Val = GenericTypeValidator.formatDouble(Amount, Locale.ENGLISH);
									if (null == BA_Val) {
										errorMsg = AmortCaptions.Amount + AmortValidation.ERR_NUMBER_FORMAT;
										errors.addError(errorMsg, AmortConstant.NPVROWS.Amount, "ERR_NUMBER_FORMAT");
									} else {
										if (0 >= (BA_Val.intValue())) {
											errorMsg = AmortCaptions.Amount + AmortValidation.ERR_INVALID_NUMBER;
											errors.addError(errorMsg, AmortConstant.NPVROWS.Amount,
													"ERR_INVALID_NUMBER");
										} else if (Amount.length() > AmortValidation.MAX_LENGTH) {
											errorMsg = AmortCaptions.Amount + AmortValidation.ERR_LENGTH_EXCEED;
											errors.addError(errorMsg, AmortConstant.NPVROWS.Amount,
													"ERR_LENGTH_EXCEED");
										}
									}
								} else {
									errorMsg = AmortCaptions.Amount + AmortValidation.ERR_MANDETORY;
									errors.addError(errorMsg, AmortConstant.NPVROWS.Amount, "ERR_MANDETORY");
								}
								if (!Installment_No.equals("")) {
									Double BA_Val = GenericTypeValidator.formatDouble(Amount, Locale.ENGLISH);
									if (null == BA_Val) {
										errorMsg = AmortCaptions.Amount + AmortValidation.ERR_NUMBER_FORMAT;
										errors.addError(errorMsg, AmortConstant.NPVROWS.Amount, "ERR_NUMBER_FORMAT");
									} else {
										if (0 >= (BA_Val.intValue())) {
											errorMsg = AmortCaptions.Installment_No
													+ AmortValidation.ERR_INVALID_NUMBER;
											errors.addError(errorMsg, AmortConstant.NPVROWS.Amount,
													"ERR_INVALID_NUMBER");
										} else if (Amount.length() > AmortValidation.MAX_LENGTH) {
											errorMsg = AmortCaptions.Installment_No + AmortValidation.ERR_LENGTH_EXCEED;
											errors.addError(errorMsg, AmortConstant.NPVROWS.Installment_No,
													"ERR_LENGTH_EXCEED");
										}
									}
								} else {
									errorMsg = AmortCaptions.Installment_No + AmortValidation.ERR_MANDETORY;
									errors.addError(errorMsg, AmortConstant.NPVROWS.Installment_No, "ERR_MANDETORY");
								}

							}
						}
					} else {
						errorMsg = AmortValidation.ERR_AMORT_NOTPOSSIBLE + AmortCaptions.AmortType + " & " + "NPV";
						errors.addError(errorMsg, "NPV", "ERR_AMORT_NOTPOSSIBLE");
					}
				}
			}
			// NPV End
			// Adjust_Rate
			HashMap Adjust_RateHash = (HashMap) amortMap.get("Adjust_Rate");
			if (Adjust_RateHash.size() == 0) {
				if (isDebugEnabled)
					logger.info("no Adjust Rate");
			} else {
				Set HostKeys = Adjust_RateHash.keySet();
				Iterator It = HostKeys.iterator();
				while (It.hasNext()) {
					String HostNow = It.next().toString();
					HashMap hmAdjust_Rate = (HashMap) Adjust_RateHash.get(HostNow);
					if (isDebugEnabled)
						logger.info("Adjust Rate emi total=" + BPHash.size());
					String adjustrate = hmAdjust_Rate.get(AmortConstant.AdjustRate.Rate).toString();
					String afterPayment = hmAdjust_Rate.get(AmortConstant.AdjustRate.AfterPayment).toString();

					if (adjustrate.equals("") && afterPayment.equals("")) {
						if (isDebugEnabled)
							logger.info("no Adjust Rate specified");
						errorMsg = AmortValidation.ERR_ADJRATE_INVALID;
						errors.addError(errorMsg, AmortCaptions.adjustrate);
					} else {
						if (!adjustrate.equals("")) {
							Double rate_Val = GenericTypeValidator.formatDouble(adjustrate, Locale.ENGLISH);

							if (null == rate_Val) {
								errorMsg = AmortCaptions.adjustrate + AmortValidation.ERR_NUMBER_FORMAT;
								errors.addError(errorMsg, AmortConstant.AdjustRate.Rate, "ERR_NUMBER_FORMAT");
							} else {
								if (0 >= (rate_Val.intValue()) && Math.signum(rate_Val) == -1) {
									errorMsg = AmortCaptions.adjustrate + AmortValidation.ERR_NUMBER_FORMAT;
									errors.addError(errorMsg, AmortConstant.AdjustRate.Rate, "ERR_NUMBER_FORMAT");
								} else if (adjustrate.length() > AmortValidation.MAX_LENGTH) {
									errorMsg = AmortCaptions.adjustrate + AmortValidation.ERR_LENGTH_EXCEED;
									errors.addError(errorMsg, AmortConstant.AdjustRate.Rate, "ERR_LENGTH_EXCEED");
								}
							}
						} else {
							errorMsg = AmortCaptions.adjustrate + AmortValidation.ERR_MANDETORY;
							errors.addError(errorMsg, AmortConstant.AdjustRate.Rate, "ERR_MANDETORY");
						}
						if (!afterPayment.equals("") && !afterPayment.equals("-1")) {
							Double AP_Val = GenericTypeValidator.formatDouble(afterPayment, Locale.ENGLISH);
							if (null == AP_Val) {
								errorMsg = AmortCaptions.afterpayment + AmortValidation.ERR_NUMBER_FORMAT;
								errors.addError(errorMsg, AmortConstant.AdjustRate.AfterPayment, "ERR_NUMBER_FORMAT");
							}
						} else {
							errorMsg = AmortCaptions.afterpayment + AmortValidation.ERR_MANDETORY;
							errors.addError(errorMsg, AmortConstant.AdjustRate.AfterPayment, "ERR_MANDETORY");
						}
					}
				}
			}
			// Fees
			HashMap Fees_CompoHash = (HashMap) amortMap.get("Fees");
			if (Fees_CompoHash.size() == 0) {
				if (isDebugEnabled)
					logger.info("no Fees");
			} else {
				Set HostKeys = Fees_CompoHash.keySet();
				Iterator It = HostKeys.iterator();
				while (It.hasNext()) {
					String HostNow = It.next().toString();
					HashMap hmFees_Compo = (HashMap) Fees_CompoHash.get(HostNow);
					if (isDebugEnabled)
						logger.info("Adjust Rate emi total=" + BPHash.size());

					String feeName = hmFees_Compo.get(AmortConstant.Component.FeeName).toString();
					String fBasis = hmFees_Compo.get(AmortConstant.Component.Basis).toString();
					String fParam = hmFees_Compo.get(AmortConstant.Component.Parameter).toString();
					String famtVal = hmFees_Compo.get(AmortConstant.Component.Value).toString();
					String fDependent = hmFees_Compo.get(AmortConstant.Component.Dependent).toString();
					String fCalc = hmFees_Compo.get(AmortConstant.Component.Calculation).toString();
					String fType = hmFees_Compo.get(AmortConstant.Component.Type).toString();
					String fPercentVal = hmFees_Compo.get(AmortConstant.Component.PercentVal).toString();
					String fRange = hmFees_Compo.get(AmortConstant.Component.Range).toString();

					if (feeName.equals("") && fBasis.equals("") && fParam.equals("") && famtVal.equals("")
							&& fDependent.equals("") && fCalc.equals("") && fType.equals("")
							&& fPercentVal.equals("")) {
						if (isDebugEnabled)
							logger.info("no Fees specified");
						errorMsg = AmortValidation.ERR_FEES_INVALID;
						errors.addError(errorMsg, AmortCaptions.Fees, "ERR_FEES_INVALID");
					} else {
						// 1.Fee name
						if (feeName.equals("") || feeName.equals("-1")) {
							errorMsg = AmortCaptions.FeeName + AmortValidation.ERR_MANDETORY;
							errors.addError(errorMsg, AmortConstant.Component.FeeName, "ERR_MANDETORY");
						}

						// 2.Basis
						if (null != fBasis && !fBasis.equals("") && !fBasis.equals("-1")) {
							ArrayList BasisLi = ValidationList.Fees_Basis();
							if (BasisLi.contains(fBasis)) {
								Integer fBasis_Val = GenericTypeValidator.formatInt(fBasis, Locale.ENGLISH);
								if (null == fBasis_Val) {
									errorMsg = AmortCaptions.Basis + AmortValidation.ERR_NUMBER_FORMAT;
									errors.addError(errorMsg, AmortConstant.Component.Basis, "ERR_NUMBER_FORMAT");
								}
							} else {
								errorMsg = AmortCaptions.Basis + AmortValidation.ERR_ISINVALID;
								errors.addError(errorMsg, AmortConstant.Component.Basis, "ERR_ISINVALID");
							}
						} else {
							errorMsg = AmortCaptions.Basis + AmortValidation.ERR_MANDETORY;
							errors.addError(errorMsg, AmortConstant.Component.Basis, "ERR_MANDETORY");
						}
						// 3.Type
						if (null != fType && !fType.equals("") && !fType.equals("-1")) {
							ArrayList TypeLi = ValidationList.Fees_Type();
							if (!TypeLi.contains(fType)) {
								errorMsg = AmortCaptions.Type + AmortValidation.ERR_ISINVALID;
								errors.addError(errorMsg, AmortConstant.Component.Type, "ERR_ISINVALID");
							}
						} else {
							errorMsg = AmortCaptions.Type + AmortValidation.ERR_MANDETORY;
							errors.addError(errorMsg, AmortConstant.Component.Type, "ERR_MANDETORY");
						}
						// 4.Calculation
						if (null != fCalc && !fCalc.equals("") && !fCalc.equals("-1")) {
							ArrayList fCalcLi = ValidationList.Fees_Calc();
							if (fCalcLi.contains(fCalc)) {
								Integer fCalc_Val = GenericTypeValidator.formatInt(fCalc, Locale.ENGLISH);
								if (null == fCalc_Val) {
									errorMsg = AmortCaptions.Calculation + AmortValidation.ERR_NUMBER_FORMAT;
									errors.addError(errorMsg, AmortConstant.Component.Calculation, "ERR_NUMBER_FORMAT");
								}
							} else {
								errorMsg = AmortCaptions.Calculation + AmortValidation.ERR_ISINVALID;
								errors.addError(errorMsg, AmortConstant.Component.Calculation, "ERR_ISINVALID");
							}
						} else {
							errorMsg = AmortCaptions.Calculation + AmortValidation.ERR_MANDETORY;
							errors.addError(errorMsg, AmortConstant.Component.Calculation, "ERR_MANDETORY");
						}
						// 5.Parameter
						if (null != fParam && !fParam.equals("") && !fParam.equals("-1")) {
							ArrayList fParamLi = ValidationList.Fees_Param();
							if (!fParamLi.contains(fParam) && !fParam.equals(feeName)) {
								// errorMsg = AmortCaptions.Parameter+AmortValidation.ERR_ISINVALID;
								// errors.addError(errorMsg, AmortConstant.Component.Parameter);
							}
						} else {
							errorMsg = AmortCaptions.Parameter + AmortValidation.ERR_MANDETORY;
							errors.addError(errorMsg, AmortConstant.Component.Parameter, "ERR_MANDETORY");
						}
						// 6.Dependent
						if (null != fDependent && !fDependent.equals("") && !fDependent.equals("-1")) {
							ArrayList fDependentLi = ValidationList.Fees_Dependent();
							if (!fDependentLi.contains(fDependent)) {
								errorMsg = AmortCaptions.Dependent + AmortValidation.ERR_ISINVALID;
								errors.addError(errorMsg, AmortConstant.Component.Dependent, "ERR_ISINVALID");
							}
						} else {
							errorMsg = AmortCaptions.Dependent + AmortValidation.ERR_MANDETORY;
							errors.addError(errorMsg, AmortConstant.Component.Dependent, "ERR_MANDETORY");
						}
						// 7.Amount Value
						if (Integer.parseInt(fBasis) == AmortConstant.Component.BasisAmount) {
							if (!famtVal.equals("")) {
								Double famtVal_Val = GenericTypeValidator.formatDouble(famtVal, Locale.ENGLISH);
								if (null == famtVal_Val) {

									errorMsg = AmortCaptions.Value + AmortValidation.ERR_NUMBER_FORMAT;
									errors.addError(errorMsg, AmortConstant.Component.Value, "ERR_NUMBER_FORMAT");
								} else {
									if (0 >= (famtVal_Val.intValue())) {
										errorMsg = AmortCaptions.Value + AmortValidation.ERR_NUMBER_FORMAT;
										errors.addError(errorMsg, AmortConstant.Component.Value, "ERR_NUMBER_FORMAT");
									} else if (famtVal.length() > AmortValidation.MAX_LENGTH) {
										errorMsg = AmortCaptions.Value + AmortValidation.ERR_LENGTH_EXCEED;
										errors.addError(errorMsg, AmortConstant.Component.Value, "ERR_LENGTH_EXCEED");
									}
								}
							} else {
								errorMsg = AmortCaptions.Value + AmortValidation.ERR_MANDETORY;
								errors.addError(errorMsg, AmortConstant.Component.Value, "ERR_MANDETORY");
							}
						}

						// 8.Range Value
						if (!fRange.equals("")) {
							if (!fRange.equals("0") && !fRange.equals("0-0")) {
								int septr = fRange.indexOf("-");
								if (septr != -1) {
									String[] splitStr = fRange.split("-");
									if (null != splitStr && splitStr.length == 2) {
										Double fRange_Val1 = GenericTypeValidator.formatDouble(splitStr[0],
												Locale.ENGLISH);
										Double fRange_Val2 = GenericTypeValidator.formatDouble(splitStr[1],
												Locale.ENGLISH);
										if (null == fRange_Val1 || null == fRange_Val2) {
											errorMsg = AmortCaptions.Range + AmortValidation.ERR_NUMBER_FORMAT;
											errors.addError(errorMsg, AmortConstant.Component.Range,
													"ERR_NUMBER_FORMAT");
										} else {
											if ((fRange_Val1.intValue() > (fRange_Val2.intValue()))) {
												errorMsg = "Range from" + AmortValidation.ERR_COMPARE + "Range Upto";
												errors.addError(errorMsg, AmortConstant.Component.Range, "ERR_COMPARE");
											} else if (fRange.length() > AmortValidation.MAX_LENGTH) {
												errorMsg = AmortCaptions.Range + AmortValidation.ERR_LENGTH_EXCEED;
												errors.addError(errorMsg, AmortConstant.Component.Range,
														"ERR_LENGTH_EXCEED");
											}
										}
									}

								} else {
									errorMsg = AmortValidation.ERR_RANGE_FORMAT;
									errors.addError(errorMsg, AmortConstant.Component.Range, "ERR_RANGE_FORMAT");
								}
							}
						} else {
							errorMsg = AmortCaptions.Range + AmortValidation.ERR_MANDETORY;
							errors.addError(errorMsg, AmortConstant.Component.Range, "ERR_MANDETORY");
						}

						// 9.Percent Value
						if (Integer.parseInt(fBasis) == AmortConstant.Component.BasisPercentage) {
							if (!fPercentVal.equals("")) {
								Double fPercent_Val = GenericTypeValidator.formatDouble(fPercentVal, Locale.ENGLISH);
								if (null == fPercent_Val) {

									errorMsg = AmortCaptions.PercentVal + AmortValidation.ERR_NUMBER_FORMAT;
									errors.addError(errorMsg, AmortConstant.Component.PercentVal, "ERR_NUMBER_FORMAT");
								} else {
									if (0 > (fPercent_Val.intValue())) {
										errorMsg = AmortCaptions.PercentVal + AmortValidation.ERR_NUMBER_FORMAT;
										errors.addError(errorMsg, AmortConstant.Component.PercentVal,
												"ERR_NUMBER_FORMAT");
									} else if (fPercentVal.length() > AmortValidation.MAX_LENGTH) {
										errorMsg = AmortCaptions.PercentVal + AmortValidation.ERR_LENGTH_EXCEED;
										errors.addError(errorMsg, AmortConstant.Component.PercentVal,
												"ERR_LENGTH_EXCEED");
									}
								}
							} else {
								errorMsg = AmortCaptions.PercentVal + AmortValidation.ERR_MANDETORY;
								errors.addError(errorMsg, AmortConstant.Component.PercentVal, "ERR_MANDETORY");
							}
						}
					}
				}
			}

			double input_EMI = 0;
			if (!amortMap.get(AmortConstant.Input_EMI).equals("null")) {
				input_EMI = Double.parseDouble(amortMap.get(AmortConstant.Input_EMI).toString());
			}
			double input_rate = Double.parseDouble(amortMap.get(AmortConstant.InterestRate).toString());
			double input_tenor = Double.parseDouble(amortMap.get(AmortConstant.Tenor).toString());
			double input_loan = Double.parseDouble(amortMap.get(AmortConstant.LoanAmount).toString());

			if (input_EMI > 0 && input_loan > 0 && input_rate > 0 && input_tenor > 0) {

				errorMsg = AmortValidation.ERR_INPUT_PARAMETERS;
				errors.addError(errorMsg, "", "ERR_INPUT_PARAMETERS");

			}

			// For EMI Adjustment in new row
			Map<String, Object> emiAdjMap = (Map<String, Object>) amortMap.get("EMI_ADJ");
			// emi_op
			if (emiAdjMap != null) {
				String Emi_Op = emiAdjMap.get(AmortConstant.Input_EmiOP).toString();
				// emi_op_basis
				String Input_EmiOP_Basis = emiAdjMap.get(AmortConstant.Input_Onbasis).toString();

				if (Emi_Op != null && !Emi_Op.equals("-1") && (!Emi_Op.equals(""))) {
					ArrayList Emi_Op_basisLi = ValidationList.Input_EmiOP_Basis();
					if (Emi_Op_basisLi.contains(Input_EmiOP_Basis)) {
						Integer Emi_Op_BasisVal = GenericTypeValidator.formatInt(Input_EmiOP_Basis, Locale.ENGLISH);
						if (null == Emi_Op_BasisVal) {
							errorMsg = AmortCaptions.Onbasis + AmortValidation.ERR_NUMBER_FORMAT;
							errors.addError(errorMsg, AmortConstant.Input_EmiOP, "ERR_NUMBER_FORMAT");
						}
					} else {
						errorMsg = AmortCaptions.Onbasis + AmortValidation.ERR_VALUE_INVALID + Integer.parseInt(Emi_Op);
						errors.addError(errorMsg, AmortConstant.Input_Onbasis, "ERR_VALUE_INVALID");
					}

					/// emi_thresh
					String Emi_Thresh = emiAdjMap.get(AmortConstant.Input_Thresh).toString();

					if (Emi_Thresh != null && !Emi_Thresh.equals("") && !Emi_Thresh.equals("-1")) {
						Double Emi_ThreshVal = GenericTypeValidator.formatDouble(Emi_Thresh, Locale.ENGLISH);
						if (null == Emi_ThreshVal) {
							errorMsg = AmortCaptions.Thresh + AmortValidation.ERR_NUMBER_FORMAT;
							errors.addError(errorMsg, AmortConstant.Input_Thresh, "ERR_NUMBER_FORMAT");
						}

						else {
							if (0 > (Emi_ThreshVal.intValue())) {
								errorMsg = AmortCaptions.Thresh + AmortValidation.ERR_INVALID_NUMBER;
								errors.addError(errorMsg, AmortConstant.Input_Thresh, "ERR_INVALID_NUMBER");
							}

						}
					}

					String Input_Adj = emiAdjMap.get(AmortConstant.Input_Adj).toString();

					if (Input_Adj != null && !Input_Adj.equals("-1") && (!Input_Adj.equals(""))) { // &&
																									// Integer.parseInt(AmortType)==0){
						ArrayList Input_AdjLi = ValidationList.Input_EmiOP_Basis();
						if (Input_AdjLi.contains(Input_Adj)) {
							Integer Input_AdjVal = GenericTypeValidator.formatInt(Input_Adj, Locale.ENGLISH);
							if (null == Input_Adj) {
								errorMsg = AmortCaptions.Input_Adj + AmortValidation.ERR_NUMBER_FORMAT;
								errors.addError(errorMsg, AmortConstant.Input_Adj, "ERR_NUMBER_FORMAT");
							}
						} else {
							errorMsg = AmortCaptions.Onbasis + AmortValidation.ERR_VALUE_INVALID
									+ Integer.parseInt(Emi_Op);
							errors.addError(errorMsg, AmortConstant.Input_Onbasis, "ERR_VALUE_INVALID");
						}
					}

				}
			}

			String IntIB = amortMap.get(AmortConstant.int_IB).toString();
			ArrayList IntIBLi = ValidationList.IntIB();
			if (IntIBLi.contains(IntIB)) {
				Integer IntIBVal = GenericTypeValidator.formatInt(IntIB, Locale.ENGLISH);
				if (null == IntIBVal) {
					errorMsg = AmortConstant.int_IB + AmortValidation.ERR_NUMBER_FORMAT;
					errors.addError(errorMsg, AmortConstant.int_IB, "ERR_NUMBER_FORMAT");
					amortFlag = false;
				}
			} else {
				errorMsg = AmortCaptions.Interest_Basis + AmortValidation.ERR_VALUE_INVALID;
				errors.addError(errorMsg, AmortConstant.Interest_Basis, "ERR_VALUE_INVALID");
			}

			//// FOR INTERNAL LOAN AMOUNT
			String IntloanAmt = amortMap.get(AmortConstant.Int_principal).toString();
			if (IntloanAmt != null && !IntloanAmt.equals("")) {
				Double IntloanVal = GenericTypeValidator.formatDouble(IntloanAmt, Locale.ENGLISH);
				if (null == IntloanVal) {
					errorMsg = AmortCaptions.LoanAmount + AmortValidation.ERR_NUMBER_FORMAT;
					errors.addError(errorMsg, AmortConstant.Int_principal, "ERR_NUMBER_FORMAT");
				} else {
					if (0 > (IntloanVal.intValue())) {
						errorMsg = AmortCaptions.LoanAmount + AmortValidation.ERR_INVALID_NUMBER;
						errors.addError(errorMsg, AmortConstant.Int_principal, "ERR_INVALID_NUMBER");
					} else if (IntloanAmt.length() > AmortValidation.MAX_LENGTH) {
						errorMsg = AmortCaptions.LoanAmount + AmortValidation.ERR_LENGTH_EXCEED;
						errors.addError(errorMsg, AmortConstant.Int_principal, "ERR_LENGTH_EXCEED");
					}
				}
			}

			if (NPVHash.size() == 0 || isError == true) {

				if (errors != null && errors.getSize() > 0) {
					for (int i = 0; i < errors.getSize(); i++) {
						errors.getError(i).setErrorNo(Integer.toString(i));

					}
					if (logger.isInfoEnabled())
						logger.info(progName + "Amort :Errors found during validation. Errors XML="
								+ errors.toJsonString());
					resultValues = HashMapUtils.getReturnHashMap("false", "valErrors", errors);
				} else
					resultValues = HashMapUtils.getReturnHashMap("true");
			} else
				resultValues = HashMapUtils.getReturnHashMap("true");

		} // END

		catch (Exception exception) {
			if (isDebugEnabled)
				logger.debug("Method : getValidate() : Exception thrown " + exception.toString());
			resultValues = HashMapUtils.getReturnHashMap("false");
			exception.printStackTrace();
			throw new AmortException.ValidationException(exception);
		} finally {
			String AmortType = amortMap.get(AmortConstant.AmortType).toString();
			if ("true".equals(resultValues.get("successFlag").toString()) && !AmortType.equals("")) {
				if (amortFlag && Integer.parseInt(AmortType) == 0) {
					HashMap<String, Object> vMap = skip_validation(amortMap, resultValues);
					String successFlag = vMap.get("successFlag").toString();
					if ("true".equals(successFlag))
						return resultValues;
					else
						return vMap;
				} else
					return resultValues;
			} else
				return resultValues;

		}
	}

	private static HashMap skip_validation(Map<String, Object> amortMap, HashMap<String, Object> resultValues)
			throws ValidationException {
		int BalloonMonth = 0;
		int SkipFrom = 0;
		int SUDFMonth = 0;
		int SUDTMonth = 0;
		int SPRFMonth = 0;
		int SPRTMonth = 0;
		int SkipNo = 0;
		int l = 0;
		int c = 0;
		int i = 0;
		HashMap insMap = new HashMap();
		String errorMsg = null;
		ErrorMessages errors = null;
		HashMap vMap = new HashMap();

		HashMap BPHash = (HashMap) amortMap.get("BP");
		int blength = 0;
		boolean bFlag = mValidations(BPHash);
		if (bFlag)
			blength = BPHash.size();

		HashMap Skip_EMIHash = (HashMap) amortMap.get("Skip_EMI");
		int sklength = 0;
		boolean skFlag = mValidations(Skip_EMIHash);
		if (skFlag)
			sklength = Skip_EMIHash.size();

		HashMap Step_EMIHash = (HashMap) amortMap.get("Step_EMI");
		int stlength = 0;
		boolean stFlag = mValidations(Step_EMIHash);
		if (stFlag)
			stlength = Step_EMIHash.size();

		HashMap PrincRecv_EMIHash = (HashMap) amortMap.get("stepprin");
		int sprlength = 0;
		boolean sprFlag = mValidations(PrincRecv_EMIHash);
		if (sprFlag)
			sprlength = PrincRecv_EMIHash.size();

		try {
			if ((ErrorMessages) resultValues.get("valErrors") != null) {
				errors = (ErrorMessages) resultValues.get("valErrors");
			} else
				errors = new ErrorMessages();
			// HashMap vMap = new HashMap();
			int Tenor = Integer.parseInt(amortMap.get(AmortConstant.Tenor).toString());
			int RepaymentFrequency = Integer.parseInt(amortMap.get(AmortConstant.RepaymentFrequency).toString());
			int installments = (RepaymentFrequency * Tenor);

			if (blength == 0) {
				if (isDebugEnabled)
					logger.info("no BP");
			} else {
				Set HostKeys = BPHash.keySet();
				Iterator It = HostKeys.iterator();

				while (It.hasNext()) {
					i++;
					String HostNow = It.next().toString();
					HashMap hmBP = (HashMap) BPHash.get(HostNow);
					BalloonMonth = Integer.parseInt(hmBP.get(AmortConstant.Balloon.BalloonMonth).toString());
					if (installments >= BalloonMonth) {
						// insMap.put("Ballon_"+i, (String)
						// hmBP.get(AmortConstant.Balloon.BalloonMonth));
						String inStr = Integer.toString(BalloonMonth);
						if (insMap.containsValue(inStr)) {
							if (!insMap.containsValue(inStr)) {
								insMap.put("Ballon_" + i, inStr);
							}
							errorMsg = AmortValidation.ERR_BALLOON_MONTH;
							errors.addError(errorMsg, AmortConstant.Balloon.BalloonMonth, "ERR_BALLOON_MONTH");
						} else {
							insMap.put("Ballon_" + i, inStr);
						}
						vMap = HashMapUtils.getReturnHashMap("true");
					} else {
						errorMsg = AmortValidation.ERR_BALLOON_MONTH;
						errors.addError(errorMsg, AmortConstant.Balloon.BalloonMonth, "ERR_BALLOON_MONTH");
					}
				}
			}

			if (sklength == 0) {
				if (isDebugEnabled)
					logger.info("no skip");
			} else {
				Set HostKeys = Skip_EMIHash.keySet();
				Iterator It = HostKeys.iterator();
				while (It.hasNext()) {
					String HostNow = It.next().toString();
					HashMap hmSkipEmi = (HashMap) Skip_EMIHash.get(HostNow);
					SkipFrom = Integer.parseInt(hmSkipEmi.get(AmortConstant.SkipEMI.SkipFrom).toString());
					SkipNo = Integer.parseInt(hmSkipEmi.get(AmortConstant.SkipEMI.SkipNo).toString());

					if (installments >= (SkipFrom + SkipNo)) {
						vMap = HashMapUtils.getReturnHashMap("true");
						for (int j = 0; j <= SkipNo; j++) {
							String inStr = Integer.toString(SkipFrom + j);
							if (insMap.containsValue(inStr)) {
								if (!insMap.containsValue(inStr)) {
									insMap.put("Skip_EMI_" + l, inStr);
									l++;
								}
								errorMsg = AmortValidation.ERR_INVALID_SKIPMONTH;
								errors.addError(errorMsg, AmortConstant.SkipEMI.SkipFrom, "ERR_INVALID_SKIPMONTH");
							} else {
								if (!insMap.containsValue(inStr)) {
									insMap.put("Skip_EMI_" + l, inStr);
									l++;
								}
							}
						}
					} else {
						errorMsg = AmortValidation.ERR_SKIP_MONTH;
						errors.addError(errorMsg, AmortConstant.SkipEMI.SkipFrom, "ERR_SKIP_MONTH");
					}
				}
			}

			if (Step_EMIHash.size() == 0) {
				if (isDebugEnabled)
					logger.info("no step up");
			} else {
				Set HostKeys = Step_EMIHash.keySet();
				Iterator It = HostKeys.iterator();
				while (It.hasNext()) {
					String HostNow = It.next().toString();
					HashMap hmStepEmi = (HashMap) Step_EMIHash.get(HostNow);
					SUDFMonth = Integer.parseInt(hmStepEmi.get(AmortConstant.StepUpDown.StepUpDownFrmMonth).toString());
					SUDTMonth = Integer
							.parseInt(hmStepEmi.get(AmortConstant.StepUpDown.StepUpDownTillMonth).toString());

					if (installments >= SUDTMonth) {
						vMap = HashMapUtils.getReturnHashMap("true");
						int len = SUDTMonth - SUDFMonth;
						for (int j = 0; j <= len; j++) {
							String inStr = Integer.toString(SUDFMonth + j);
							if (insMap.containsValue(inStr)) {
								if (!insMap.containsValue(inStr)) {
									insMap.put("Stepup_" + c, inStr);
									c++;
								}
								errorMsg = AmortValidation.ERR_INVALID_STEPMONTH;
								errors.addError(errorMsg, AmortConstant.StepUpDown.StepUpDownFrmMonth,
										"ERR_INVALID_STEPMONTH");
							} else {
								if (!insMap.containsValue(inStr)) {
									insMap.put("Stepup_" + c, inStr);
									c++;
								}
							}
						}
					} else {
						errorMsg = AmortValidation.ERR_STEPUP_MONTH;
						errors.addError(errorMsg, AmortConstant.StepUpDown.StepUpDownTillMonth, "ERR_STEPUP_MONTH");
					}
				}
			}

			if (PrincRecv_EMIHash.size() == 0) {
				if (isDebugEnabled)
					logger.info("no step up % of principle recovery");
			} else {
				Set HostKeys = PrincRecv_EMIHash.keySet();
				Iterator It = HostKeys.iterator();
				while (It.hasNext()) {
					String HostNow = It.next().toString();
					HashMap hmStepPerRecEmi = (HashMap) PrincRecv_EMIHash.get(HostNow);
					SPRFMonth = Integer.parseInt(hmStepPerRecEmi.get(AmortConstant.StepPer.StepPerFrmMonth).toString());
					SPRTMonth = Integer
							.parseInt(hmStepPerRecEmi.get(AmortConstant.StepPer.StepPerTillMonth).toString());

					if (installments >= SPRTMonth) {
						vMap = HashMapUtils.getReturnHashMap("true");
						int len = SPRTMonth - SPRFMonth;
						for (int j = 0; j <= len; j++) {
							String inStr = Integer.toString(SPRFMonth + j);
							if (insMap.containsValue(inStr)) {
								if (!insMap.containsValue(inStr)) {
									insMap.put("stepprin_" + c, inStr);
									c++;
								}
								errorMsg = AmortValidation.ERR_INVALID_STEPMONTH;
								errors.addError(errorMsg, AmortConstant.StepPer.StepPerFrmMonth,
										"ERR_INVALID_STEPMONTH");
							} else {
								if (!insMap.containsValue(inStr)) {
									insMap.put("stepprin_" + c, inStr);
									c++;
								}
							}
						}
					} else {
						errorMsg = AmortValidation.ERR_STEPUP_MONTH;
						errors.addError(errorMsg, AmortConstant.StepPer.StepPerTillMonth, "ERR_STEPUP_MONTH");
					}
				}
			}

			if (installments > 0) {
				if (errors != null && errors.getSize() > 0) {
					if (logger.isInfoEnabled())
						logger.info(":Errors found during validation. Errors XML=" + errors.toJsonString());
					vMap = HashMapUtils.getReturnHashMap("false", "valErrors", errors);
				} else {
					vMap = HashMapUtils.getReturnHashMap("true");
				}
			}

		} catch (Exception exception) {
			if (isDebugEnabled)
				logger.debug("Method : skip_validations() : Exception thrown " + exception.toString());
			vMap = HashMapUtils.getReturnHashMap("true");
			throw new AmortException.ValidationException(exception);
		}
		return vMap;
	}

	private static boolean mValidations(HashMap aMap) {
		boolean mFlag = false;
		if (aMap.containsValue(null) || aMap.containsValue("")) {
			mFlag = false;
		} else
			mFlag = true;
		return mFlag;
	}

	public static int compare2Dates(String dod, String fpd, String dateformat) throws ValidationException {
		int i = 0;
		SimpleDateFormat fm = null;
		String dod1 = AmortUtil.ConvertDate(dod, dateformat);
		String fpd1 = AmortUtil.ConvertDate(fpd, dateformat);

		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		Date dt1 = new Date();
		Date dt2 = new Date();
		try {
			fm = new SimpleDateFormat(dateformat);
			dt1 = fm.parse(dod1);
			dt2 = fm.parse(fpd1);
		} catch (Exception exception) {
			if (isDebugEnabled)
				logger.debug("Method : compare2Dates() : Exception thrown " + exception.toString());
			throw new AmortException.ValidationException(":Failed to perform validation", exception);
			// return 2;
		}
		c1.setTime(dt1);
		c2.setTime(dt2);

		if (c1.before(c2)) {
			i = -1;
		} else if (c1.after(c2)) {
			i = 1;

		} else if (c1.equals(c2)) {
			i = 0;

		}
		return i;
	}

	public static HashMap setDefaultValues(Map<String, Object> defaultValuesMap) throws AmortException {
		String progName = AmortConstant.AAYU_PROGNAME;
		if (logger.isDebugEnabled())
			logger.debug(progName + ":In getValidate method");
		try {
			String LA = defaultValuesMap.get(AmortConstant.LoanAmount).toString();
			String IE = defaultValuesMap.get(AmortConstant.Input_EMI).toString();
			if ((blankCheck(LA)) && (!blankCheck(IE))) {
				defaultValuesMap.put(AmortConstant.LoanAmount, String.valueOf(AmortConstant.DefaultValues.LoanAmount));
				LA = String.valueOf(AmortConstant.DefaultValues.LoanAmount);
			}
			if ((blankCheck(LA))) {
				defaultValuesMap.put(AmortConstant.LoanAmount, String.valueOf(AmortConstant.DefaultValues.LoanAmount));
				LA = String.valueOf(AmortConstant.DefaultValues.LoanAmount);
			}
			if (blankCheck(IE)) {
				defaultValuesMap.put(AmortConstant.Input_EMI,
						String.valueOf(defaultValuesMap.get(AmortConstant.Input_EMI)));
				IE = String.valueOf(AmortConstant.DefaultValues.InputEMI);
			}

			String AT = defaultValuesMap.get(AmortConstant.AmortType).toString();
			if (blankCheck(AT)) {
				defaultValuesMap.put(AmortConstant.AmortType,
						String.valueOf(defaultValuesMap.get(AmortConstant.AmortType)));
				AT = String.valueOf(AmortConstant.DefaultValues.AmortTypeValue);
			}

			String R = defaultValuesMap.get(AmortConstant.InterestRate).toString();
			if (blankCheck(R)) {
				defaultValuesMap.put(AmortConstant.InterestRate,
						String.valueOf(defaultValuesMap.get(AmortConstant.InterestRate)));
				R = String.valueOf(AmortConstant.DefaultValues.InterestRate);
			}
			String tenoruni = defaultValuesMap.get(AmortConstant.tenor_in).toString();
			if (blankCheck(tenoruni)) {
				defaultValuesMap.put(AmortConstant.tenor_in,
						String.valueOf(defaultValuesMap.get(AmortConstant.tenor_in)));
				tenoruni = String.valueOf(AmortConstant.DefaultValues.TenorUnitValue);
			}

			String tenor = defaultValuesMap.get(AmortConstant.Tenor).toString();
			if (blankCheck(tenor)) {
				defaultValuesMap.put(AmortConstant.Tenor, String.valueOf(AmortConstant.DefaultValues.TenorValue));
				tenor = String.valueOf(AmortConstant.DefaultValues.TenorValue);
			}

			String IT = defaultValuesMap.get(AmortConstant.InterestType).toString();
			if (blankCheck(IT)) {
				defaultValuesMap.put(AmortConstant.InterestType,
						String.valueOf(defaultValuesMap.get(AmortConstant.InterestType)));
				IT = String.valueOf(AmortConstant.DefaultValues.InterestTypeValue);
			}

			String InstT = defaultValuesMap.get(AmortConstant.InstallmentType).toString();
			if (blankCheck(InstT)) {
				defaultValuesMap.put(AmortConstant.InstallmentType,
						String.valueOf(defaultValuesMap.get(AmortConstant.InstallmentType)));
				InstT = String.valueOf(AmortConstant.DefaultValues.InstallmentTypeValue);
			}

			String AdvEMIAdj = defaultValuesMap.get(AmortConstant.AdvEMIAdj).toString();
			if (blankCheck(AdvEMIAdj)) {
				defaultValuesMap.put(AmortConstant.AdvEMIAdj,
						String.valueOf(AmortConstant.DefaultValues.AdvEMIAdjValue));
			}

			String SameAdvEMI = defaultValuesMap.get(AmortConstant.SameAdvEMI).toString();
			if (blankCheck(SameAdvEMI)) {
				defaultValuesMap.put(AmortConstant.SameAdvEMI,
						String.valueOf(AmortConstant.DefaultValues.SameAdvEMIValue));
			}

			String AdvEMINo = defaultValuesMap.get(AmortConstant.AdvEMINo).toString();
			if (blankCheck(AdvEMINo)) {
				defaultValuesMap.put(AmortConstant.AdvEMINo,
						String.valueOf(defaultValuesMap.get(AmortConstant.AdvEMINo)));
			}

			String RF = defaultValuesMap.get(AmortConstant.RepaymentFrequency).toString();
			if (blankCheck(RF)) {
				defaultValuesMap.put(AmortConstant.RepaymentFrequency,
						String.valueOf(defaultValuesMap.get(AmortConstant.RepaymentFrequency)));
				RF = String.valueOf(defaultValuesMap.get(AmortConstant.RepaymentFrequency));
			}

			// ABFL Equeated Principle Frequency Change start
			String EPF = defaultValuesMap.get(AmortConstant.EquatedPrincipleFrequency).toString();
			if (blankCheck(EPF)) {
				defaultValuesMap.put(AmortConstant.EquatedPrincipleFrequency,
						String.valueOf(defaultValuesMap.get(AmortConstant.EquatedPrincipleFrequency)));
				EPF = String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.EquatedPrincipleFrequency)));
			} // ABFL Equeated Principle Frequency Change Change End

			String rest = defaultValuesMap.get(AmortConstant.Rest).toString();
			if (blankCheck(rest)) {
				defaultValuesMap.put(AmortConstant.Rest,
						String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.Rest))));
			}

			String BPR = defaultValuesMap.get(AmortConstant.BPIRecovery).toString();
			if (blankCheck(BPR)) {
				defaultValuesMap.put(AmortConstant.BPIRecovery,
						String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.BPIRecovery))));
			}

			String CF = defaultValuesMap.get(AmortConstant.compFreq).toString();
			if (blankCheck(CF)) {
				defaultValuesMap.put(AmortConstant.compFreq,
						String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.compFreq))));
			}

			String IB = defaultValuesMap.get(AmortConstant.Interest_Basis).toString();
			if (blankCheck(IB)) {
				defaultValuesMap.put(AmortConstant.Interest_Basis,
						String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.Interest_Basis))));
			}
			if (RF.equals(Integer.toString(AmortTypeConstant.RF_W))
					|| RF.equals(Integer.toString(AmortTypeConstant.RF_FN))) {
				defaultValuesMap.put(AmortConstant.Interest_Basis,
						String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.Interest_Basis))));
				AmortConstant.isWeeklyflag = true;
				if (isDebugEnabled)
					logger.debug("Repayment Frequency is Weekly or Bi-Weekly so default 30 By 360 done");
			}
			String EMIRounding = defaultValuesMap.get(AmortConstant.EMIRounding).toString();
			if (blankCheck(EMIRounding)) {
				defaultValuesMap.put(AmortConstant.EMIRounding,
						String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.EMIRounding))));
				defaultValuesMap.put(AmortConstant.EMIRoundingUnit,
						String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.EMIRoundingUnit))));
				defaultValuesMap.put(AmortConstant.EMIRoundingTo,
						String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.EMIRoundingTo))));
				defaultValuesMap.put(AmortConstant.EMIRoundingPart,
						String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.EMIRoundingPart))));
			}

			String OtherRounding = defaultValuesMap.get(AmortConstant.OthersRounding).toString();
			if (blankCheck(OtherRounding)) {
				defaultValuesMap.put(AmortConstant.OthersRounding,
						String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.OthersRounding))));
				defaultValuesMap.put(AmortConstant.OthersUnit,
						String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.OthersUnit))));
				defaultValuesMap.put(AmortConstant.OthersRoundingTo,
						String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.OthersRoundingTo))));
				defaultValuesMap.put(AmortConstant.OthersRoundingPart,
						String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.OthersRoundingPart))));
			}

			String adjustoption = defaultValuesMap.get(AmortConstant.Adjust_Option).toString();
			if (blankCheck(adjustoption)) {
				defaultValuesMap.put(AmortConstant.Adjust_Option,
						String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.Adjust_Option))));
			}

			String dateformat = defaultValuesMap.get(AmortConstant.dateformat).toString();
			if (blankCheck(dateformat)) {
				defaultValuesMap.put(AmortConstant.dateformat,
						String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.dateformat))));
			}
			String bplastpay = defaultValuesMap.get(AmortConstant.bplastpay).toString();
			if (blankCheck(bplastpay)) {
				defaultValuesMap.put(AmortConstant.bplastpay, String.valueOf(AmortConstant.DefaultValues.bplastpay));
			}

			String IntOnly = defaultValuesMap.get(AmortConstant.InterestOnly).toString();
			if (blankCheck(IntOnly)) {
				defaultValuesMap.put(AmortConstant.InterestOnly,
						String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.InterestOnly))));
			}
			Map<String, Object> emiAdjMap = (Map<String, Object>) defaultValuesMap.get("EMI_ADJ");

			if (emiAdjMap != null) {
				String Input_EmiOP = emiAdjMap.get(AmortConstant.Input_EmiOP).toString();
				if (blankCheck(Input_EmiOP)) {
					defaultValuesMap.put(AmortConstant.Input_EmiOP,
							String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.Input_EmiOP))));
				}

				String Input_Thresh = emiAdjMap.get(AmortConstant.Input_Thresh).toString();
				if (blankCheck(Input_Thresh)) {
					defaultValuesMap.put(AmortConstant.Input_Thresh,
							String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.Input_Thresh))));
				}
				String Input_Onbasis = emiAdjMap.get(AmortConstant.Input_Onbasis).toString();
				if (blankCheck(Input_Onbasis)) {
					defaultValuesMap.put(AmortConstant.Input_Onbasis,
							String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.Input_Onbasis))));
				}
			}

			String lastInstlRo = defaultValuesMap.get(AmortConstant.lastInstlRo).toString();
			if (blankCheck(lastInstlRo)) {
				defaultValuesMap.put(AmortConstant.lastInstlRo,
						String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.lastInstlRo))));
				lastInstlRo = String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.lastInstlRo)));
			}
			String reduce_bpi = defaultValuesMap.get(AmortConstant.reduce_bpi).toString();
			if (blankCheck(reduce_bpi)) {
				defaultValuesMap.put(AmortConstant.reduce_bpi,
						String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.reduce_bpi))));
				reduce_bpi = String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.reduce_bpi)));
			}

			String reduce_bpi_pay_first = defaultValuesMap.get(AmortConstant.pay_first).toString();
			if (blankCheck(reduce_bpi_pay_first)) {
				defaultValuesMap.put(AmortConstant.pay_first,
						String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.pay_first))));
				reduce_bpi_pay_first = String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.pay_first)));
			}

			String precision_value = defaultValuesMap.get(AmortConstant.Precision).toString();
			if (blankCheck(precision_value)) {
				defaultValuesMap.put(AmortConstant.Precision,
						String.valueOf(AmortConstant.DefaultValues.precision_value));
				precision_value = String.valueOf(AmortConstant.DefaultValues.precision_value);
			}
			String int_Amort = defaultValuesMap.get(AmortConstant.int_amort).toString();
			if (blankCheck(int_Amort)) {
				defaultValuesMap.put(AmortConstant.int_amort,
						String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.int_amort))));
				int_Amort = String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.int_amort)));
			}
			String int_IB = defaultValuesMap.get(AmortConstant.int_IB).toString();

			if (blankCheck(int_IB)) {
				defaultValuesMap.put(AmortConstant.int_IB,
						String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.int_IB))));
				int_IB = String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.int_IB)));
			}

			String int_Principal = defaultValuesMap.get(AmortConstant.Int_principal).toString();

			if (blankCheck(int_Principal)) {
				defaultValuesMap.put(AmortConstant.Int_principal,
						String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.Int_principal))));

			}
			if (emiAdjMap != null) {
				String Input_Adj = emiAdjMap.get(AmortConstant.Input_Adj).toString();
				if (blankCheck(Input_Adj)) {
					defaultValuesMap.put(AmortConstant.Input_Adj,
							String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.Input_Adj))));
					Input_Adj = String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.Input_Adj)));
				}
			}

			String pipeseparated = defaultValuesMap.get(AmortConstant.pipeseparated).toString();
			if (blankCheck(pipeseparated)) {
				defaultValuesMap.put(AmortConstant.pipeseparated,
						String.valueOf(AmortConstant.DefaultValues.pipeseparated));
				pipeseparated = String.valueOf(AmortConstant.DefaultValues.pipeseparated);
			}
			String rvValue = defaultValuesMap.get(AmortConstant.RVvalue).toString();
			if (blankCheck(rvValue)) {
				defaultValuesMap.put(AmortConstant.RVvalue, String.valueOf(AmortConstant.DefaultValues.RVvalue));
				pipeseparated = String.valueOf(AmortConstant.DefaultValues.RVvalue);
			}
			String BPI_B = defaultValuesMap.get(AmortConstant.BPI_IB).toString();
			if (blankCheck(BPI_B)) {
				defaultValuesMap.put(AmortConstant.BPI_IB, (String) defaultValuesMap.get(AmortConstant.Interest_Basis));
			}

			String AdvEMIFlag = defaultValuesMap.get(AmortConstant.AdvEMIFlag).toString();
			if (blankCheck(AdvEMIFlag)) {
				defaultValuesMap.put(AmortConstant.AdvEMIFlag,
						String.valueOf(defaultValuesMap.get(AmortConstant.AdvEMIFlag)));
				lastInstlRo = String.valueOf(String.valueOf(defaultValuesMap.get(AmortConstant.AdvEMIFlag)));
			}

			if (null == defaultValuesMap.get("BP")) {
				defaultValuesMap.put("BP", new LinkedHashMap());
			}

			if (null == defaultValuesMap.get("Step_EMI")) {
				defaultValuesMap.put("Step_EMI", new LinkedHashMap());
			}

			if (null == defaultValuesMap.get("Skip_EMI")) {
				defaultValuesMap.put("Skip_EMI", new LinkedHashMap());
			}

			if (null == defaultValuesMap.get("Adjust_Rate")) {
				defaultValuesMap.put("Adjust_Rate", new LinkedHashMap());
			}

			if (null == defaultValuesMap.get("Fees")) {
				defaultValuesMap.put("Fees", new LinkedHashMap());
			}

			if (null == defaultValuesMap.get("COMP")) {
				defaultValuesMap.put("COMP", new LinkedHashMap());
			}
			if (null == defaultValuesMap.get("NPVROWS")) {
				defaultValuesMap.put("NPVROWS", new LinkedHashMap());
			}

			if (null == defaultValuesMap.get("stepprin")) {
				defaultValuesMap.put("stepprin", new LinkedHashMap());
			}
		} catch (NullPointerException exception) {
			if (logger.isDebugEnabled())
				logger.debug(progName + ":Exception occured while setting default values");
			defaultValuesMap = HashMapUtils.getReturnHashMap("false");
			throw new AmortException.ValidationException(exception);
		}
		return (HashMap) defaultValuesMap;
	}

	private static boolean blankCheck(String fieldVal) {
		if (fieldVal == null || fieldVal.equals(""))
			return true;
		else
			return false;
	}

}
