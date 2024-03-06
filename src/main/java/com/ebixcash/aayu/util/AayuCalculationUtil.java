package com.ebixcash.aayu.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ebixcash.aayu.constant.AmortConstant;
import com.ebixcash.aayu.constant.AmortConstant.AmortValidation;
import com.ebixcash.aayu.customvalidator.ErrorMessages;
import com.ebixcash.aayu.exception.AmortException;
import com.ebixcash.aayu.model.AmortInputBean;
import com.ebixcash.aayu.model.AmortOutputBean;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class AayuCalculationUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(AayuCalculationUtil.class);

	public static final boolean isDebugEnabled = LOGGER.isDebugEnabled();

	double residue = 0.0d;
	double residue_normal = 0.0d;
	double residue_iteration = 0.0d;
	double factor = 0.0d;
	double emi = 0.0d;
	double loan_amt = 0.0d;
	double real_interest_rate = 0.0d;
	double tot_int = 0.0d;
	double tot_int78 = 0.0d;
	double tot_round_int = 0.0d;
	double tot_round_int78 = 0.0d;
	double tot_sum_fee = 0.0d;
	double real_emi = 0.0d;
	double real_tenor = 0.0d;
	double real_loanamount = 0.0d;
	double loanAmt = 0.0d;
	double temp_residual_glo = 0.0d;
	double bpi = 0.0d;
	double interest_rate = 0.0d;
	int tot_installments = 0;
	boolean secondTime = false;
	boolean sameDates = false;
	boolean flagmultiple = false;
	boolean intAmort = false;
	String err_amortcalc = "";
	ArrayList otArr = null;
	HashMap err = new HashMap();
	Vector unAmortized;
	Vector cvalVec;
	Vector arr_last_date;
	AmortOverviewBean overviewbean = new AmortOverviewBean();
	ArrayList<Double> emiList = new ArrayList<Double>();
	ArrayList<Double> fixedInstemiList = new ArrayList<Double>();
	private String bpiCalculated = "N";
	double fixedInstEmi = 0.0d;

	@Value("${AAYU_INT_CALCULATION_METHOD}")
	private String aayu_int_calculation_method;

	public HashMap processInput(AmortInputBean jsonRequest) throws AmortException {
		if (isDebugEnabled)
			LOGGER.debug("Inside Process Method : processInput =: " + jsonRequest);

		HashMap<String, Object> tempMap = new HashMap<>();

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			// Map<String, Object> jsonMap = objectMapper.readValue(jsonRequest, Map.class);
			// Convert AmortInputBean to Map
			Map<String, Object> jsonMap = objectMapper.convertValue(jsonRequest, Map.class);

			for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();

				if (key.equals("Step_EMI") || key.equals("Skip_EMI") || key.equals("BP") || key.equals("Adjust_Rate")
						|| key.equals("Fees") || key.equals("COMP") || key.equals("P_STEP") || key.equals("stepprin")
						|| key.equals("NPVROWS")) {
					HashMap<String, Object> advMap = new HashMap<>();
					advMap = createAdvHash(entry);
					// advMap.putAll((Map<String, Object>) value);
					tempMap.put(key, advMap);
				} else if (key.equals("AMORT")) {
					ArrayList<Object> arrAmort = new ArrayList<>();
					arrAmort = (ArrayList<Object>) createAdvHashAmort(entry);
					arrAmort.addAll((List<Object>) value);
					tempMap.put(key, arrAmort);
				} else if (key.equals("EMI_ADJ")) {
					Map<String, Object> emiAdjMap = (Map<String, Object>) value;
					tempMap.put(key, emiAdjMap);
				} else {
					tempMap.put(key, value);
				}
			}

		} catch (Exception exception) {
			if (isDebugEnabled)
				LOGGER.error("Method : processInput() : Exception thrown " + exception.toString());
			throw new AmortException.CalculationFactoryException(exception);
		}

		return tempMap;
	}

	public static LinkedHashMap<String, Object> createAdvHash(Map.Entry<String, Object> entry) {
		LinkedHashMap<String, Object> returnHashMap = new LinkedHashMap<>();
		try {
			String key = entry.getKey();
			Object value = entry.getValue();

			if ("Fee".equals(key)) {
				// Handle special case for "Fee" key
				if (value instanceof Map) {
					returnHashMap.put(key, value);
				}
			} else if ("AM".equals(key)) {
				// Handle special case for "AM" key
				returnHashMap.put(key, String.valueOf(value)); // Convert the value to String if needed
			} else {
				key = key.substring(1);
				if (value instanceof LinkedHashMap) {
	                LinkedHashMap<String, Object> innerMap = (LinkedHashMap<String, Object>) value;
	                if (innerMap.containsKey("R1")) {
	                    LinkedHashMap<String, Object> r1Map = (LinkedHashMap<String, Object>) innerMap.get("R1");
	                    returnHashMap.put("R1", r1Map);
	                }else {
	                	returnHashMap.put(key, innerMap);
	                }
	            } else {
	            	// Assuming value is a Map.Entry<String, Object>
					Map.Entry<String, Object> entryValue = (Map.Entry<String, Object>) value;
					Map<String, Object> innerMap = createInnerHash(entryValue);
					returnHashMap.put(key, innerMap);
	            }
			}
		} catch (Exception exception) {
			if (isDebugEnabled)
				LOGGER.error("Method : createAdvHash() : Exception thrown " + exception.toString());
		}
		return returnHashMap;
	}

	public static HashMap<String, Object> createInnerHash(Map.Entry<String, Object> entry) {
		HashMap<String, Object> returnHashMap = new HashMap<>();
		try {
			String key = entry.getKey();
			Object value = entry.getValue();

			// Assuming value is a JSON object (a Map in Java)
			if (value instanceof Map) {
				Map<String, Object> jsonObject = (Map<String, Object>) value;

				for (Map.Entry<String, Object> innerEntry : jsonObject.entrySet()) {
					String innerKey = innerEntry.getKey();
					Object innerValue = innerEntry.getValue();

					// Assuming innerValue is a leaf node in JSON (not another nested object)
					returnHashMap.put(innerKey, String.valueOf(innerValue)); // Convert the value to String if needed
				}
			}
		} catch (Exception exception) {
			if (isDebugEnabled)
				LOGGER.error("Method : createInnerHash() : Exception thrown " + exception.toString());
		}
		return returnHashMap;
	}

	public static List<Object> createAdvHashAmort(Map.Entry<String, Object> entry) {
		List<Object> arrAmort = new ArrayList<>();
		try {
			String key = entry.getKey();
			Object value = entry.getValue();

			if ("R".equals(key)) {
				List<Map<String, Object>> nodeList = (List<Map<String, Object>>) value;
				for (Map<String, Object> childNode : nodeList) {
					AmortOutputBean bean = new AmortOutputBean();

					if (childNode.containsKey("NO")) {
						bean.setInstallment(String.valueOf(childNode.get("NO")));
					}
					if (childNode.containsKey("EMI")) {
						bean.setRoundEMI(Double.parseDouble(String.valueOf(childNode.get("EMI"))));
					}
					if (childNode.containsKey("DT")) {
						bean.setCycleDate(String.valueOf(childNode.get("DT")));
					}

					arrAmort.add(bean);
				}
			}
		} catch (Exception exception) {
			if (isDebugEnabled)
				LOGGER.error("Method : createAdvHashAmort() : Exception thrown " + exception.toString());
		}
		return arrAmort;
	}

	/// IntializedbeanInput
	public AmortInputBean InitializeInputModel(Map<String, Object> hm) throws AmortException {
		if (isDebugEnabled)
			LOGGER.debug("Getting started : inside InitializeInputModel");
		AmortInputBean beanObj = new AmortInputBean();
		int bp = 0;
		int step = 0;
		double bpiAmt = 0.0;
		try {

			beanObj.setOpenAmount(Double.parseDouble(hm.get(AmortConstant.LoanAmount).toString()));
			beanObj.setLoanAmount(Double.parseDouble(hm.get(AmortConstant.LoanAmount).toString()));
			if (null != hm.get("ADDINT")) {
				beanObj.setAddIntAmount(Double.parseDouble(hm.get("ADDINT").toString()));
				beanObj.setAddInt(true);
			}
			try {
				if (!"".equals(hm.get(AmortConstant.maxEmi)) && "Y".equals(hm.get(AmortConstant.maxEmi))) {
					beanObj.setMaxEmi((String) hm.get(AmortConstant.maxEmi));
				}
			} catch (Exception exception) {
				if (isDebugEnabled)
					LOGGER.error("Method : InitializeInputModel() : Exception thrown...maxEmi tag not found "
							+ exception.toString());
			}
			try {
				if (!"".equals(hm.get(AmortConstant.currEMI)) && "Y".equals(hm.get(AmortConstant.currEMI))) {
					beanObj.setCurr_emi((String) hm.get(AmortConstant.currEMI));
				}
			} catch (Exception exception) {
				if (isDebugEnabled)
					LOGGER.error("Method : InitializeInputModel() : Exception thrown...CURR_EMI tag not found "
							+ exception.toString());
			}

			try {
				if (!"".equals(hm.get(AmortConstant.currEMI)) && "Y".equals(hm.get(AmortConstant.currEMI))) {
					beanObj.setFixedRateofInterest(Double.parseDouble(hm.get(AmortConstant.InterestRate).toString()));
				}
			} catch (Exception exception) {
				if (isDebugEnabled)
					LOGGER.error("Method : InitializeInputModel() : Exception thrown...CURR_EMI tag not found "
							+ exception.toString());
			}
			beanObj.setTenor(Double.parseDouble(hm.get(AmortConstant.Tenor).toString()));
			beanObj.setTenor78(Double.parseDouble(hm.get(AmortConstant.Tenor).toString()));
			beanObj.setInterestRate(Double.parseDouble(hm.get(AmortConstant.InterestRate).toString()));

			String doc = AmortUtil.ConvertDate(hm.get(AmortConstant.DateOfCycle).toString(),
					hm.get(AmortConstant.dateformat).toString());
			beanObj.setDateOfCycle(doc);
			String dod = AmortUtil.ConvertDate(hm.get(AmortConstant.DateOfDisbursement).toString(),
					hm.get(AmortConstant.dateformat).toString());
			beanObj.setDateOfDisbursement(dod);
			String fpd = AmortUtil.ConvertDate(hm.get(AmortConstant.DateOfCycle).toString(),
					hm.get(AmortConstant.dateformat).toString());
			beanObj.setDateOfFirstPayment(fpd);

			beanObj.setDateformat(hm.get(AmortConstant.dateformat).toString());

			beanObj.setAmortType(Integer.parseInt(hm.get(AmortConstant.AmortType).toString()));
			beanObj.setInterestType(Integer.parseInt(hm.get(AmortConstant.InterestType).toString()));
			beanObj.setInstallmentType(Integer.parseInt(hm.get(AmortConstant.InstallmentType).toString()));
			// For New Row
			// Access the nested "EMI_ADJ" map
			Map<String, Object> emiAdjMap = (Map<String, Object>) hm.get("EMI_ADJ");
			if (emiAdjMap != null) {
				beanObj.setEmiOPValue(Integer.parseInt(emiAdjMap.get(AmortConstant.Input_EmiOP).toString()));
				beanObj.setOnbasisValue(Integer.parseInt(emiAdjMap.get(AmortConstant.Input_Onbasis).toString()));
				beanObj.setThreshValue(Double.parseDouble(emiAdjMap.get(AmortConstant.Input_Thresh).toString()));
				beanObj.setAdj(Integer.parseInt(emiAdjMap.get(AmortConstant.Input_Adj).toString()));

			}

			beanObj.setBP_Lastpayamt(Double.parseDouble(hm.get(AmortConstant.bplastpay).toString()));
			beanObj.setlastInstlRo(hm.get(AmortConstant.lastInstlRo).toString());
			beanObj.setAdvEMI_FIRST_INST(hm.get(AmortConstant.AdvEMIFlag).toString());
			beanObj.setReduceBpi(hm.get(AmortConstant.reduce_bpi).toString());
			/// For Negative BPI First Installment Payment
			beanObj.setPayFirst(hm.get(AmortConstant.pay_first).toString());
			beanObj.setEOMAmort(hm.get(AmortConstant.eom) != null ? hm.get(AmortConstant.eom).toString() : null);
			beanObj.setSOMAmort(hm.get(AmortConstant.som) != null ? hm.get(AmortConstant.som).toString() : null);
			beanObj.setPrecision(Double.parseDouble(hm.get(AmortConstant.Precision).toString()));
			beanObj.setintAmort(hm.get(AmortConstant.int_amort).toString());
			beanObj.setintIB(Integer.parseInt(hm.get(AmortConstant.int_IB).toString()));
			beanObj.setPipeseparated(hm.get(AmortConstant.pipeseparated).toString());
			beanObj.setlastEMIAdj(hm.get(AmortConstant.lastEMIAdj).toString());
			beanObj.setRVvalue(Double.parseDouble(hm.get(AmortConstant.RVvalue).toString()));
			beanObj.setBPIMethod(Integer.parseInt(hm.get(AmortConstant.BPI_IB).toString()));
			beanObj.setIntprincipal(Double.parseDouble(hm.get(AmortConstant.Int_principal).toString()));
			String strDeductTDS = hm.get(AmortConstant.deductTDS).toString();
			if (strDeductTDS != null && ("Y".equalsIgnoreCase(strDeductTDS.trim())
					|| "YES".equalsIgnoreCase(strDeductTDS.trim()) || "1".equals(strDeductTDS.trim()))) {
				try {
					String strTDSPercentage = hm.get(AmortConstant.TDSPercentage).toString();
					beanObj.setTDSPercentage(Double.parseDouble(strTDSPercentage));
				} catch (Exception x) {
					err.put("ERR_TDSPERCENTAGE", "TDS Percentage should be number(without % sign)");
				}
				beanObj.setDeductTDS(true);
			} else {
				beanObj.setDeductTDS(false);
			}
			if ((hm.get(AmortConstant.AdvEMIAdj).toString()).equals(""))
				beanObj.setAdjustAdvEMIAt(AmortConstant.AdvEMIAdjValue);
			else
				beanObj.setAdjustAdvEMIAt(Integer.parseInt(hm.get(AmortConstant.AdvEMIAdj).toString()));

			if ((hm.get(AmortConstant.SameAdvEMI).toString()).equals(""))
				beanObj.setSameAdvEMI(AmortConstant.SameAdvEMIValue);
			else
				beanObj.setSameAdvEMI(Integer.parseInt(hm.get(AmortConstant.SameAdvEMI).toString()));

			if ((hm.get(AmortConstant.AdvEMINo).toString()).equals(""))
				beanObj.setNoOfAdvEMI(AmortConstant.AdvEMINoValue);
			else
				beanObj.setNoOfAdvEMI(Integer.parseInt(hm.get(AmortConstant.AdvEMINo).toString()));

			beanObj.setRepaymentFrequency(Integer.parseInt(hm.get(AmortConstant.RepaymentFrequency).toString()));
			beanObj.setRepaymentFrequency78(Integer.parseInt(hm.get(AmortConstant.RepaymentFrequency).toString()));
			beanObj.setTenor_in(Integer.parseInt(hm.get(AmortConstant.tenor_in).toString()));
			beanObj.setEqPrinFreq(Double.parseDouble(hm.get(AmortConstant.EquatedPrincipleFrequency).toString()));
			int equPrincfrq = (int) beanObj.getEqPrinFreq();
			int repfrequency = beanObj.getRepaymentFrequency();
			beanObj.setEquPriFreqpresent(true);
			if (beanObj.isEquPriFreqpresent() && beanObj.getInstallmentType() == 2)
				beanObj.setInstallmentType(1);
			if (equPrincfrq > 0) {
				// beanObj.setAdj(AmortConstant.Component.Adj_in_EMI);
				if (equPrincfrq != repfrequency)
					beanObj.setAdj(AmortConstant.Component.Adj_in_EMI);
				if (equPrincfrq > repfrequency) {
					beanObj.setRepaymentFrequency(equPrincfrq);
					beanObj.setInterestFrequency(repfrequency);
					beanObj.setTenor_in(Integer.parseInt((String) hm.get(AmortConstant.tenor_in)));
				} else {
					beanObj.setInterestFrequency(repfrequency);
				}
			}
			if (beanObj.getAmortType() == AmortConstant.AmortType_Rule78)
				beanObj.setRepaymentFrequency(12);

			beanObj.setRest(Integer.parseInt(hm.get(AmortConstant.Rest).toString()));
			beanObj.setBPI_Recovery(Integer.parseInt(hm.get(AmortConstant.BPIRecovery).toString()));
			beanObj.setCompoundFreq(Integer.parseInt(hm.get(AmortConstant.compFreq).toString()));
			beanObj.setInterest_Basis(Integer.parseInt(hm.get(AmortConstant.Interest_Basis).toString()));

			if ((hm.get(AmortConstant.Interest_Basis_Emi).toString()) == null)
				beanObj.setInterest_Basis_emi(Integer.parseInt(hm.get(AmortConstant.Interest_Basis).toString()));
			else
				beanObj.setInterest_Basis_emi(Integer.parseInt(hm.get(AmortConstant.Interest_Basis_Emi).toString()));

			beanObj.setInterest_Basis_emi2(Integer.parseInt(hm.get(AmortConstant.Interest_Basis).toString()));
			if (isDebugEnabled)
				LOGGER.debug("IB Value for Frequency:" + hm.get(AmortConstant.Interest_Basis));
			if (repfrequency == 360 && (beanObj.getInterest_Basis() == AmortConstant.IntBasis_30360
					&& beanObj.getInterest_Basis_emi() == AmortConstant.IntBasis_30360)) {
				beanObj.setInterest_Basis(3);
				beanObj.setInterest_Basis_emi(3);
				AmortConstant.isDaily = true;
			}
			beanObj.setEMI_ro(Integer.parseInt(hm.get(AmortConstant.EMIRounding).toString()));
			beanObj.setEMI_unit_ro(Integer.parseInt(hm.get(AmortConstant.EMIRoundingUnit).toString()));
			beanObj.setEMI_ro_to(Integer.parseInt(hm.get(AmortConstant.EMIRoundingTo).toString()));
			beanObj.setEMI_ro_part(Integer.parseInt(hm.get(AmortConstant.EMIRoundingPart).toString()));
			beanObj.setOthers_ro(Integer.parseInt(hm.get(AmortConstant.OthersRounding).toString()));
			beanObj.setOthers_ro_part(Integer.parseInt(hm.get(AmortConstant.OthersRoundingPart).toString()));
			beanObj.setOthers_ro_to(Integer.parseInt(hm.get(AmortConstant.OthersRoundingTo).toString()));
			beanObj.setOthers_unit_ro(Integer.parseInt(hm.get(AmortConstant.OthersUnit).toString()));

			double input_EMI = 0;
			if (!hm.get(AmortConstant.Input_EMI).equals("null")) {
				input_EMI = Double.parseDouble(hm.get(AmortConstant.Input_EMI).toString());
				beanObj.setInput_emi(input_EMI);

			}
			beanObj.setTotalInstallment(beanObj.getTenor(), beanObj.getTenor_in(), beanObj.getTenorfactor(),
					beanObj.getRepaymentFrequency());
			beanObj.setTotaleqtInstment(beanObj.getTenor(), beanObj.getTenor_in(), beanObj.getTenorfactor(),
					beanObj.getEqPrinFreq());
			beanObj.setDtstart(hm.get(AmortConstant.dtstart).toString());
			beanObj.setDtend(hm.get(AmortConstant.dtend).toString());
			beanObj.setAdjust_Option(Integer.parseInt(hm.get(AmortConstant.Adjust_Option).toString()));
			beanObj.setIntCalMethod((hm.get(AmortConstant.IntCalMethod).toString()));
			if (beanObj.getIntCalMethod() == null) {
				if (AmortConstant.CompoundingMethod.equals(aayu_int_calculation_method)) {
					beanObj.setIntCalMethod("S");
				} else {
					beanObj.setIntCalMethod("C");
				}
			}

			// Step_EMI Start
			LinkedHashMap Step_EMIHash = (LinkedHashMap) hm.get("Step_EMI");
			int StepEMIHash = Step_EMIHash != null ? Step_EMIHash.size() : 0;
			if (StepEMIHash == 0) {
				if (isDebugEnabled)
					LOGGER.info("no step up");
			} else {
				Set HostKeys = Step_EMIHash.keySet();
				Iterator It = HostKeys.iterator();
				ArrayList arr = new ArrayList();
				while (It.hasNext()) {
					String HostNow = It.next().toString();
					HashMap hmStepEmi = (HashMap) Step_EMIHash.get(HostNow);
					StepEMIInputBean StepEMI = new StepEMIInputBean();
					StepEMI.setFrm_month(
							Integer.parseInt(hmStepEmi.get(AmortConstant.StepUpDown.StepUpDownFrmMonth).toString()));
					StepEMI.setTo_month(
							Integer.parseInt(hmStepEmi.get(AmortConstant.StepUpDown.StepUpDownTillMonth).toString()));
					StepEMI.setStepbasis(
							Integer.parseInt(hmStepEmi.get(AmortConstant.StepUpDown.StepUpDownBasis).toString()));
					StepEMI.setStepmode(
							Integer.parseInt(hmStepEmi.get(AmortConstant.StepUpDown.StepUpDownType).toString()));
					StepEMI.setStepby(
							Double.parseDouble(hmStepEmi.get(AmortConstant.StepUpDown.StepUpDownBy).toString()));
					// StepEMI.setStepadjust(Integer.parseInt((String)hmStepEmi.get(AmortConstant.StepUpDown.StepUpDownAdjust)));
					// for backward compatability setting AdjustOption in global Tag
					if (hmStepEmi.get(AmortConstant.StepUpDown.StepUpDownAdjust).toString() != null)
						beanObj.setAdjust_Option(
								Integer.parseInt(hmStepEmi.get(AmortConstant.StepUpDown.StepUpDownAdjust).toString()));
					// Set advance variation flag only for Adjust EMI/Tenor option
					if (StepEMI.getStepadjust() != AmortConstant.AdjustedEMI)
						beanObj.setApply_adv(AmortConstant.Apply_ADV);

					arr.add(StepEMI);
				}
				beanObj.setArrStepEMI(arr);
				step++;
			}
			// Step_EMI End

			// % of Principle Recovery Start
			LinkedHashMap Step_PrinRecvrHash = (LinkedHashMap) hm.get("stepprin");
			int stepPrinRecvrHash = Step_PrinRecvrHash != null ? Step_PrinRecvrHash.size() : 0;
			if (stepPrinRecvrHash == 0) {
				if (isDebugEnabled)
					LOGGER.info("no % of principle step");
			} else {
				Set HostKeys = Step_PrinRecvrHash.keySet();
				Iterator It = HostKeys.iterator();
				ArrayList arr = new ArrayList();
				while (It.hasNext()) {
					String HostNow = It.next().toString();
					HashMap hmStepEmi = (HashMap) Step_PrinRecvrHash.get(HostNow);
					StepPrinRecInputBean StepPrincRecvr = new StepPrinRecInputBean();
					StepPrincRecvr.setStepprin_frm(
							Integer.parseInt(hmStepEmi.get(AmortConstant.PerPrinRecv.StepPrincFrmMonth).toString()));
					StepPrincRecvr.setStepprin_to(
							Integer.parseInt(hmStepEmi.get(AmortConstant.PerPrinRecv.StepPrincToMonth).toString()));
					StepPrincRecvr.setStepprin_in(
							Integer.parseInt(hmStepEmi.get(AmortConstant.PerPrinRecv.StepPrincIn).toString()));
					StepPrincRecvr.setStepprin_by(
							Integer.parseInt(hmStepEmi.get(AmortConstant.PerPrinRecv.StepPrincBy).toString()));
					if ((String) hmStepEmi.get(AmortConstant.PerPrinRecv.StepPrincRate) != null) {
						StepPrincRecvr.setStepprin_rate(
								Double.parseDouble(hmStepEmi.get(AmortConstant.PerPrinRecv.StepPrincRate).toString()));
					}
					if ((String) hmStepEmi.get(AmortConstant.PerPrinRecv.StepPrincFreq) != null) {
						StepPrincRecvr.setStepprin_freq(
								Integer.parseInt(hmStepEmi.get(AmortConstant.PerPrinRecv.StepPrincFreq).toString()));
					}

					arr.add(StepPrincRecvr);
				}
				beanObj.setArrPerPrinRcvr(arr);
				step++;
			}
			// % Principle of % Recovery end

			// Balloon Start
			LinkedHashMap BPHash = (LinkedHashMap) hm.get("BP");
			int bpHash = BPHash != null ? BPHash.size() : 0;
			if (bpHash == 0) {
				if (isDebugEnabled)
					LOGGER.info("no BP");
			} else {
				Set HostKeys = BPHash.keySet();
				Iterator It = HostKeys.iterator();
				ArrayList arrBP = new ArrayList();
				while (It.hasNext()) {
					String HostNow = It.next().toString();
					HashMap hmBP = (HashMap) BPHash.get(HostNow);
					BPInputBean BP = new BPInputBean();
					BP.setBP_Month(Integer.parseInt(hmBP.get(AmortConstant.Balloon.BalloonMonth).toString()));
					BP.setBP_Amount(Double.parseDouble(hmBP.get(AmortConstant.Balloon.BalloonAmount).toString()));

					if (BP.getBP_Amount() == beanObj.getLoanAmount() && BP.getBP_Month() == beanObj.getTenor()) {
						beanObj.setInstallmentType(AmortConstant.Installment_InterestOnly);
					}
					// BP.setBP_Adjust(Integer.parseInt((String)hmBP.get(AmortConstant.Balloon.BallonAdj)));
					if (step != 0) {
						if (beanObj.getAdjust_Option() != (Integer
								.parseInt(hmBP.get(AmortConstant.Balloon.BallonAdj).toString()))) {
							err.put("ERR_ADJOPTIONUNIQUE", "AdjustOptions should be unique in all Variations.");
						}
					}
					// for backward compatability setting AdjustOption in global Tag
					if ((String) hmBP.get(AmortConstant.Balloon.BallonAdj) != null)
						beanObj.setAdjust_Option(
								Integer.parseInt(hmBP.get(AmortConstant.Balloon.BallonAdj).toString()));
					// Set advance variation flag only for Adjust EMI/Tenor option
					if (BP.getBP_Adjust() != AmortConstant.AdjustedEMI)
						beanObj.setApply_adv(AmortConstant.Apply_ADV);

					arrBP.add(BP);
				}
				beanObj.setArrBP(arrBP);
				bp++;
			}
			// Balloon End

			// Skip_EMI start
			LinkedHashMap Skip_EMIHash = (LinkedHashMap) hm.get("Skip_EMI");
			int skipEMIHash = Skip_EMIHash != null ? Skip_EMIHash.size() : 0;

			if (skipEMIHash == 0) {
				if (isDebugEnabled)
					LOGGER.info("no skip");
			} else {
				Set HostKeys = Skip_EMIHash.keySet();
				Iterator It = HostKeys.iterator();
				ArrayList arrSkip = new ArrayList();
				while (It.hasNext()) {
					String HostNow = It.next().toString();
					HashMap hmStepEmi = (HashMap) Skip_EMIHash.get(HostNow);
					SkipEMIInputBean SkipEMI = new SkipEMIInputBean();
					SkipEMI.setFrm_month(Integer.parseInt(hmStepEmi.get(AmortConstant.SkipEMI.SkipFrom).toString()));
					SkipEMI.setNo_month(Integer.parseInt(hmStepEmi.get(AmortConstant.SkipEMI.SkipNo).toString()));
					// SkipEMI.setSkipadjust(Integer.parseInt((String)hmStepEmi.get(AmortConstant.SkipEMI.SkipAdjust)));
					SkipEMI.setSkip_capital(hmStepEmi.get(AmortConstant.SkipEMI.SkipCapital).toString());
					if (hmStepEmi.containsKey(AmortConstant.SkipEMI.SkipPartialPay)
							&& hmStepEmi.get(AmortConstant.SkipEMI.SkipPartialPay) != null)
						SkipEMI.setSkipPartPay(hmStepEmi.get(AmortConstant.SkipEMI.SkipPartialPay).toString());
					if (hmStepEmi.containsKey(AmortConstant.SkipEMI.SkipPartialPayIn)
							&& hmStepEmi.get(AmortConstant.SkipEMI.SkipPartialPayIn) != null)
						SkipEMI.setSkipPartPayIn(hmStepEmi.get(AmortConstant.SkipEMI.SkipPartialPayIn).toString());

					if (hmStepEmi.containsKey(AmortConstant.SkipEMI.SkipPartInt)
							&& hmStepEmi.get(AmortConstant.SkipEMI.SkipPartInt) != null)
						SkipEMI.setSkipPartInterest(
								Double.parseDouble(hmStepEmi.get(AmortConstant.SkipEMI.SkipPartInt).toString()));

					if (step != 0 || bp != 0) {
						if (beanObj.getAdjust_Option() != (Integer
								.parseInt(hmStepEmi.get(AmortConstant.SkipEMI.SkipAdjust).toString()))) {
							err.put("ERR_ADJOPTIONUNIQUE", "AdjustOptions should be unique in all Variations.");
						}
					}
					// for backward compatability setting AdjustOption in global Tag
					if ((String) hmStepEmi.get(AmortConstant.SkipEMI.SkipAdjust) != null)
						beanObj.setAdjust_Option(
								Integer.parseInt(hmStepEmi.get(AmortConstant.SkipEMI.SkipAdjust).toString()));
					// Set advance variation flag only for Adjust EMI/Tenor option
					if (SkipEMI.getSkipadjust() != AmortConstant.AdjustedEMI)
						beanObj.setApply_adv(AmortConstant.Apply_ADV);

					arrSkip.add(SkipEMI);
				}
				beanObj.setArrSkipEMI(arrSkip);

			}
			// Skip_EMI End

			// Adjust Rate
			LinkedHashMap Adjust_RateHash = (LinkedHashMap) hm.get("Adjust_Rate");
			int adjustRateHash = Adjust_RateHash != null ? Adjust_RateHash.size() : 0;
			if (adjustRateHash == 0) {
				if (isDebugEnabled)
					LOGGER.info("no adjust rate");
			} else {
				Set HostKeys = Adjust_RateHash.keySet();
				Iterator It = HostKeys.iterator();
				ArrayList arrAdjRate = new ArrayList();
				while (It.hasNext()) {
					String HostNow = It.next().toString();
					HashMap hmAdjRate = (HashMap) Adjust_RateHash.get(HostNow);
					AdjustRateInputBean AdjRate = new AdjustRateInputBean();
					AdjRate.setAdj_rate(Double.parseDouble(hmAdjRate.get(AmortConstant.AdjustRate.Rate).toString()));
					AdjRate.setAfter_month(
							Integer.parseInt(hmAdjRate.get(AmortConstant.AdjustRate.AfterPayment).toString()));
					arrAdjRate.add(AdjRate);
				}
				beanObj.setArrAdjustRate(arrAdjRate);
				// For Backward compatability if AdjustOption is given in Request then setting
				// value as per Missing value.
				if (Skip_EMIHash.size() == 0 && BPHash.size() == 0 && Step_EMIHash.size() == 0) {
					if (beanObj.getInput_emi() != 0) {
						beanObj.setAdjust_Option(AmortConstant.AdjustTenor);
					} else {
						beanObj.setAdjust_Option(AmortConstant.AdjustEMI);
					}
				}
				beanObj.setApply_adjrate(AmortConstant.Apply_ADV);
				if (beanObj.getAdjust_Option() == AmortConstant.AdjustedEMI) {
					// err.put("ERR_ADJOPTION", "AdjustedEMI is not Supported for AdjustRate.");
					if (isDebugEnabled)
						LOGGER.error("Adjusted EMI Option is not supported for AdjustRate Option.");
				}
			}
			// Adjust Rate End

			LinkedHashMap PPHash = (LinkedHashMap) hm.get("P_STEP");
			if (PPHash == null) {
				if (isDebugEnabled)
					LOGGER.info("no P_STEP");
			} else {
				Set HostKeys = PPHash.keySet();
				Iterator It = HostKeys.iterator();
				ArrayList arrPP = new ArrayList();
				while (It.hasNext()) {
					String HostNow = It.next().toString();
					HashMap hmPP = (HashMap) PPHash.get(HostNow);
					StepPerInputBean sp = new StepPerInputBean();
					sp.setFrm_inst(Integer.parseInt(hmPP.get(AmortConstant.StepPer.StepPerFrmMonth).toString()));
					sp.setTo_inst(Integer.parseInt(hmPP.get(AmortConstant.StepPer.StepPerTillMonth).toString()));
					sp.setP_F(Integer.parseInt(hmPP.get(AmortConstant.StepPer.StepPerFreq).toString()));
					sp.setP_PER(Double.parseDouble(hmPP.get(AmortConstant.StepPer.StepPerPER).toString()));
					sp.setP_R(Double.parseDouble(hmPP.get(AmortConstant.StepPer.StepPerRate).toString()));
					sp.setP_T_IN(Integer.parseInt(hmPP.get(AmortConstant.StepPer.StepPerTenorUnit).toString()));
					// Set advance variation flag only for Adjust EMI/Tenor option

					arrPP.add(sp);

				}
				beanObj.setArrStepPer(arrPP);
			}
			// Component (Fees/Subproduct/Tax)
			LinkedHashMap ComponentHash = (LinkedHashMap) hm.get("Fees");
			int componentHash = ComponentHash != null ? ComponentHash.size() : 0;
			if (componentHash == 0) {
				if (isDebugEnabled)
					LOGGER.info("no Fees defined");
			} else {
				Set HostKeys = ComponentHash.keySet();
				Iterator It = HostKeys.iterator();
				ArrayList arrComponent = new ArrayList();
				while (It.hasNext()) {
					String HostNow = It.next().toString();
					LOGGER.info("FEE:" + HostNow);
					HashMap hmComponent = (HashMap) ComponentHash.get(HostNow);
					ComponentBean cb = new ComponentBean();
					cb.setFeename((String) hmComponent.get(AmortConstant.Component.FeeName));
					cb.setBasis(Integer.parseInt(hmComponent.get(AmortConstant.Component.Basis).toString()));
					cb.setParam(hmComponent.get(AmortConstant.Component.Parameter).toString());
					cb.setValue(Double.parseDouble(hmComponent.get(AmortConstant.Component.Value).toString()));
					cb.setDependent(hmComponent.get(AmortConstant.Component.Dependent).toString());
					cb.setCalc(Integer.parseInt(hmComponent.get(AmortConstant.Component.Calculation).toString()));
					cb.setType(hmComponent.get(AmortConstant.Component.Type).toString());
					cb.setPercentvalue(
							Double.parseDouble(hmComponent.get(AmortConstant.Component.PercentVal).toString()));
					cb.setRange(hmComponent.get(AmortConstant.Component.Range).toString());
					arrComponent.add(cb);
				}
				beanObj.setArrFees(arrComponent);
			}
			// Component End
			LinkedHashMap CompHash = (LinkedHashMap) hm.get("COMP");
			int compHash = CompHash != null ? CompHash.size() : 0;
			if (compHash == 0) {
				if (isDebugEnabled)
					LOGGER.info("no BP");
			} else {
				Set HostKeys = CompHash.keySet();
				Iterator It = HostKeys.iterator();
				ArrayList arrComp = new ArrayList();
				int i = 0;
				while (It.hasNext()) {
					String HostNow = It.next().toString();

					if (i == 0) {
						beanObj.setAmortMethod(CompHash.get(AmortConstant.AmortMethod).toString());
					} else {
						HashMap hmComp = (HashMap) CompHash.get(HostNow);
						InternalAmortComponentBean ICB = new InternalAmortComponentBean();
						ICB.setName(hmComp.get(AmortConstant.Comp.CompName).toString());
						ICB.setcType(hmComp.get(AmortConstant.Comp.ctype).toString());
						ICB.setVal(Double.parseDouble(hmComp.get(AmortConstant.Comp.Val).toString()));
						arrComp.add(ICB);
					}

					// Set advance variation flag only for Adjust EMI/Tenor option
					i++;
				}
				beanObj.setArrComp(arrComp);

			}

			LinkedHashMap npv_Hash = (LinkedHashMap) hm.get("NPVROWS");
			int npvHash = npv_Hash != null ? npv_Hash.size() : 0;
			if (npvHash == 0) {
				if (isDebugEnabled)
					LOGGER.info("no skip");
			} else {
				Set HostKeys = npv_Hash.keySet();
				Iterator It = HostKeys.iterator();
				ArrayList arrSkip = new ArrayList();
				while (It.hasNext()) {
					String HostNow = It.next().toString();
					HashMap hmStepEmi = (HashMap) npv_Hash.get(HostNow);
					NPVROWSInputBean npvVal = new NPVROWSInputBean();
					npvVal.setInstallment_No(
							Integer.parseInt(hmStepEmi.get(AmortConstant.NPVROWS.Installment_No).toString()));
					npvVal.setAmount(Double.parseDouble(hmStepEmi.get(AmortConstant.NPVROWS.Amount).toString()));

					arrSkip.add(npvVal);
				}
				beanObj.setArrROWS(arrSkip);

			}

			beanObj.setInt_Only(Integer.parseInt(hm.get(AmortConstant.InterestOnly).toString()));

			/*
			 * No.of AdvanceEMIs. If No.of AdvanceEMIs are there then calculate term and
			 * emi. set the loan amount,Tenor depends on No.of AdvanceEMIs. Amort Schedule
			 * plotted on newly loan amount and Tenor.
			 */
			if (beanObj.getNoOfAdvEMI() > 0 && beanObj.getAdjustAdvEMIAt() == 0 && beanObj.getLoanAmount() > 0) {
				if (isDebugEnabled)
					LOGGER.debug("inside InitializeInputModel:Inside Advance EMI Calculation : ");
				int frequency = beanObj.getRepaymentFrequency();
				int term = 0;
				term = AmortUtil.getNPER(beanObj.getTenor(), frequency, beanObj);
				beanObj.setReal_loanamount(beanObj.getLoanAmount());
				int noAdvEmi = beanObj.getNoOfAdvEMI();
				if (isDebugEnabled)
					LOGGER.debug(
							"inside InitializeInputModel:Inside Advance EMI Calculation :Calculated Term= " + term);

				// check input EMI present or not.If not then calculate emi and use that emi to
				// setting LoanAMount else use inputted emi to setting LoanAmount.
				if (beanObj.getInput_emi() == 0.0) {
					double advEmi = 0.0;
					double preadvEmi = 0.0;
					if (!hm.containsKey("me")) {
						hm.put("me", "0");
						AmortInputBean beanXMLObj = InitializeInputModel(hm);

						ArrayList tempXMLList = generateAmort(beanXMLObj);
						advEmi = AmortUtil.round(real_emi, beanXMLObj.getEMI_ro_part(), beanXMLObj.getEMI_ro_to(),
								beanXMLObj.getEMI_unit_ro());
						// beanObj.setLoanAmountOri(beanObj.getLoanAmount());
						int iEMI = 0;
						do {
							preadvEmi = advEmi;
							beanXMLObj.setLoanAmount(
									beanXMLObj.getLoanAmountOri() - (advEmi * beanXMLObj.getNoOfAdvEMI()));
							beanXMLObj.setLoanAmount(
									AmortUtil.round(beanXMLObj.getLoanAmount(), beanXMLObj.getOthers_ro_part(),
											beanXMLObj.getOthers_ro_to(), beanXMLObj.getOthers_unit_ro()));
							overviewbean.setAdvanceEMI(beanObj.getNoOfAdvEMI() * advEmi);
							tempXMLList = generateAmort(beanXMLObj);
							advEmi = AmortUtil.round(real_emi, beanXMLObj.getEMI_ro_part(), beanXMLObj.getEMI_ro_to(),
									beanXMLObj.getEMI_unit_ro());
							iEMI = iEMI + 1;
						} while (preadvEmi != advEmi && iEMI < 2);
						if (preadvEmi < advEmi)
							advEmi = preadvEmi;
						if (null != beanObj.getArrPerPrinRcvr()) {
							ArrayList arr = (ArrayList) beanObj.getArrPerPrinRcvr();
							for (int k = 0; k < arr.size(); k++) {
								StepPrinRecInputBean sm = (StepPrinRecInputBean) arr.get(k);
								if (sm.getStepprin_frm() == 1) {
									sm.setStepprin_to(sm.getStepprin_to() - noAdvEmi);
								} else {
									sm.setStepprin_frm(sm.getStepprin_frm() - noAdvEmi);
									sm.setStepprin_to(sm.getStepprin_to() - noAdvEmi);
								}
							}
						}

						beanObj.setInput_emi(advEmi);
						beanObj.setEMIpresent(true);
						beanObj.setAdvEmiCalculated(true);
					} else {

						double perprinc_amt = 0.0;
						double last_bpvalue = 0.0;
						perprinc_amt = beanObj.getLoanAmount();
						if (null != beanObj.getArrPerPrinRcvr()) {
							ArrayList arr = (ArrayList) beanObj.getArrPerPrinRcvr();
							ArrayList arrNew = new ArrayList();
							for (int k = 0; k < arr.size(); k++) {
								StepPrinRecInputBean sm = (StepPrinRecInputBean) arr.get(k);
								if (sm.getStepprin_frm() == 1) {
									beanObj.setTenor(sm.getStepprin_to());
									if (sm.getStepprin_in() == 1) {
										perprinc_amt = beanObj.getLoanAmount() * sm.getStepprin_by() / 100;
										last_bpvalue = beanObj.getLoanAmount() - perprinc_amt;
									} else {
										last_bpvalue = beanObj.getLoanAmount() - sm.getStepprin_by();
									}
									arrNew.add(sm);
								}
							}
							beanObj.setBP_Lastpayamt(last_bpvalue);
							beanObj.setArrPerPrinRcvr(arrNew);
						}

						advEmi = AmortUtil.PMT(beanObj.getLoanAmount(),
								(double) AmortUtil.getNPER(beanObj.getTenor(), frequency, beanObj),
								AmortUtil.getRate(beanObj.getInterestRate(), beanObj.getCompoundFreq(),
										beanObj.getRepaymentFrequency()),
								beanObj.getInterestType(), beanObj.getRepaymentFrequency(), beanObj.getSameAdvEMI(),
								beanObj.getNoOfAdvEMI(), beanObj.getBP_Lastpayamt());
						// advEmi = AmortUtil.PMT(beanObj.getLoanAmount(),(double)term,
						// AmortUtil.getRate(beanObj.getInterestRate(),beanObj.getCompoundFreq(),beanObj.getRepaymentFrequency()),
						// beanObj.getInterestType(),beanObj.getRepaymentFrequency());
						advEmi = AmortUtil.round(advEmi, beanObj.getEMI_ro_part(), beanObj.getEMI_ro_to(),
								beanObj.getEMI_unit_ro());
					}
					// double advEmi = AmortUtil.PMT(beanObj.getLoanAmount(),(double)term,
					// AmortUtil.getRate(beanObj.getInterestRate(),beanObj.getCompoundFreq(),beanObj.getRepaymentFrequency()),
					// beanObj.getInterestType(),beanObj.getRepaymentFrequency(),beanObj.getSameAdvEMI(),beanObj.getNoOfAdvEMI(),beanObj.getBP_Lastpayamt());
					beanObj.setTenor(term - beanObj.getNoOfAdvEMI());
					// beanObj.setInput_emi(advEmi);
					// advEmi = AmortUtil.round(advEmi, beanObj.getEMI_ro_part(),
					// beanObj.getEMI_ro_to(), beanObj.getEMI_unit_ro());
					beanObj.setLoanAmountOri(beanObj.getLoanAmount());
					beanObj.setLoanAmount(beanObj.getLoanAmount() - (advEmi * beanObj.getNoOfAdvEMI()));
					beanObj.setLoanAmount(AmortUtil.round(beanObj.getLoanAmount(), beanObj.getOthers_ro_part(),
							beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro()));
					overviewbean.setAdvanceEMI(beanObj.getNoOfAdvEMI() * advEmi);
					if (isDebugEnabled)
						LOGGER.debug("Inside InitializeInputModel:Inside Advance EMI Calculation :Calculated EMI is ="
								+ emi + "Total AdvanceEMIs are= " + beanObj.getNoOfAdvEMI() * emi
								+ " and Loan Amount is ="
								+ (beanObj.getLoanAmount() - (emi * beanObj.getNoOfAdvEMI())));
				} else {
					beanObj.setLoanAmount(beanObj.getLoanAmount() - (beanObj.getInput_emi() * beanObj.getNoOfAdvEMI()));
					overviewbean.setAdvanceEMI(beanObj.getNoOfAdvEMI() * beanObj.getInput_emi());
					beanObj.setTenor(beanObj.getTenor() - beanObj.getNoOfAdvEMI());
					if (isDebugEnabled)
						LOGGER.debug(
								"Inside InitializeInputModel:Inside Advance EMI Calculation :Total AdvanceEMIs are= "
										+ beanObj.getNoOfAdvEMI() * beanObj.getInput_emi() + " and Loan Amount is ="
										+ (beanObj.getLoanAmount()
												- (beanObj.getInput_emi() * beanObj.getNoOfAdvEMI())));
				}

			} else if (beanObj.getNoOfAdvEMI() > 0 && beanObj.getAdjustAdvEMIAt() == 1 && beanObj.getLoanAmount() > 0) {
				int frequency = beanObj.getRepaymentFrequency();
				int term = 0;
				term = AmortUtil.getNPER(beanObj.getTenor(), frequency, beanObj);
				beanObj.setReal_loanamount(beanObj.getLoanAmount());
				if (beanObj.getInput_emi() == 0.0) {
					double actEmi = AmortUtil.PMT(beanObj.getLoanAmount(), (double) term,
							AmortUtil.getRate(beanObj.getInterestRate(), beanObj.getCompoundFreq(),
									beanObj.getRepaymentFrequency()),
							beanObj.getInterestType(), beanObj.getRepaymentFrequency());
					beanObj.setTenor((term) - beanObj.getNoOfAdvEMI());
					// beanObj.setInput_emi(actEmi);
					actEmi = AmortUtil.round(actEmi, beanObj.getEMI_ro_part(), beanObj.getEMI_ro_to(),
							beanObj.getEMI_unit_ro());
					overviewbean.setAdvanceEMI(beanObj.getNoOfAdvEMI() * actEmi);
					beanObj.setBP_Lastpayamt(beanObj.getNoOfAdvEMI() * actEmi);
					// beanObj.set
				} else {
					overviewbean.setAdvanceEMI(beanObj.getNoOfAdvEMI() * beanObj.getInput_emi());
					beanObj.setBP_Lastpayamt(beanObj.getNoOfAdvEMI() * beanObj.getInput_emi());
				}
			}

			// EMI,LOANAMOUNT,TENOR ARE GIVEN(RATE IS NOT GIVEN) then calculate Guess Rate
			// by calling cal_Interest_rate API.
			if (!beanObj.isAdvEmiCalculated() && beanObj.getInput_emi() > 0 && beanObj.getLoanAmount() > 0
					&& beanObj.getTenor() > 0 && beanObj.getRVvalue() == 0.0d) {
				if (isDebugEnabled)
					LOGGER.warn("Inside InitializeInputModel:InterestRate is Missing");
				beanObj.setInterestRatepresent(true);
				double term = AmortUtil.getNPER(beanObj.getTenor(), beanObj.getRepaymentFrequency(), beanObj);
				double rate = AmortUtil.cal_Interest_rate(beanObj);
				if (isDebugEnabled)
					LOGGER.debug("Inside InitializeInputModel:Calculated guess Rate =" + rate);
				interest_rate = rate;
				rate = rate * 100;
				beanObj.setTenor(term);
			}
			// EMI,LOANAMOUNT,RATE ARE GIVEN(TENOR IS NOT GIVEN) then calculate Guess Tenor
			// by calling claculateNPER API.
			if (!beanObj.isAdvEmiCalculated() && beanObj.getInput_emi() > 0 && beanObj.getLoanAmount() > 0
					&& beanObj.getRVvalue() == 0.0d && !beanObj.isInterestRatepresent()) {
				if (isDebugEnabled)
					LOGGER.warn("Inside InitializeInputModel:Tenor is Missing");
				beanObj.setTenorpresent(true);
				double rate = (AmortUtil.getRate(beanObj.getInterestRate(), beanObj.getCompoundFreq(),
						beanObj.getRepaymentFrequency()));
				double term = AmortUtil.claculateNPER(beanObj.getLoanAmount(), beanObj.getInput_emi(), rate,
						beanObj.getBP_Lastpayamt());
				if (isDebugEnabled)
					LOGGER.debug("Inside InitializeInputModel:Calculated guess Tenor =" + term);
				// Increase term by no. of skip months if skip adjust is skip EMI with increased
				// tenor
				int cnt = 0;
				if (null != beanObj.getArrSkipEMI()) {
					ArrayList tempList = beanObj.getArrSkipEMI();
					ListIterator listIterator = tempList.listIterator();
					while (listIterator.hasNext()) {
						listIterator.next();
						SkipEMIInputBean obj = (SkipEMIInputBean) tempList.get(cnt);
						if (beanObj.getAdjust_Option() == AmortConstant.SkipEMI.AdjustTenor) {
							term = term + obj.getNo_month();
						}
						cnt++;
					}
				}
				beanObj.setTenor(term);
				real_emi = beanObj.getInput_emi();
				if (beanObj.getInstallmentType() == AmortConstant.Installment_InterestOnly) {
					err.put("ERR_INSTINT",
							"Invalid Combination.In case of Input EMI you cannot pass Installment Type is Interest only Option");
					if (isDebugEnabled)
						LOGGER.error(
								"Invalid Combination.In case of Input EMI you cannot pass Installment Type is Interest only Option");
				}
			}
			// EMI,TENOR,RATE ARE GIVEN(LOANAMOUNT IS NOT GIVEN) then calculate Guess
			// LoanAMount by calling calLoanAmt API.
			if (beanObj.getTenor() > 0 && beanObj.getInterestRate() > 0 && beanObj.getInput_emi() > 0
					&& beanObj.getLoanAmount() == 0) {
				if (isDebugEnabled)
					LOGGER.warn("Inside InitializeInputModel:LoanAmount is Missing");
				beanObj.setLoanAmountpresent(true);
				double term = AmortUtil.getNPER(beanObj.getTenor(), beanObj.getRepaymentFrequency(), beanObj);
				double periodrate = beanObj.getInterestRate() / beanObj.getRepaymentFrequency();
				double loan_amount = AmortUtil.calLoanAmt(periodrate, (double) term, beanObj.getInput_emi(),
						beanObj.getSameAdvEMI(), beanObj.getNoOfAdvEMI());
				if (isDebugEnabled)
					LOGGER.info("Inside InitializeInputModel:Calculated guess Loan Amount=" + loan_amount);
				beanObj.setLoanAmount(loan_amount - beanObj.getInput_emi() * beanObj.getNoOfAdvEMI());
				beanObj.setTenor(term - beanObj.getNoOfAdvEMI());
			}

			// LOANAMOUNT,TENOR,RATE ARE GIVEN(EMI IS NOT GIVEN) then calculate Guess EMI by
			// calling PMT API.
			if (beanObj.getLoanAmount() > 0 && beanObj.getTenor() > 0
					&& (beanObj.getInterestRate() > 0 || beanObj.getInterestRate() == 0)
					&& beanObj.getInput_emi() == 0) {
				if (isDebugEnabled)
					LOGGER.warn("Inside InitializeInputModel:EMI is Missing");
				beanObj.setEMIpresent(true);
				double calRate = 0.0d;
				double term = AmortUtil.getNPER(beanObj.getTenor(), beanObj.getRepaymentFrequency(), beanObj);
				if (beanObj.getNoOfAdvEMI() > 0) {
					term = beanObj.getTenor();
				}
				if (beanObj.getInterestType() == AmortConstant.InterestTypeFlat)
					calRate = beanObj.getInterestRate();
				else
					calRate = (AmortUtil.getRate(beanObj.getInterestRate(), beanObj.getCompoundFreq(),
							beanObj.getRepaymentFrequency()));

				if (beanObj.getInt_Only() > 0) {
					beanObj.setTenor(term - beanObj.getInt_Only() / beanObj.getRepaymentFrequency());
				}

				double calemi = AmortUtil.PMT(beanObj.getLoanAmount(), (double) term, calRate,
						beanObj.getInterestType(), beanObj.getRepaymentFrequency(), beanObj.getSameAdvEMI(),
						beanObj.getNoOfAdvEMI(), beanObj.getBP_Lastpayamt());
				if (isDebugEnabled)
					LOGGER.info("Inside InitializeInputModel:Calculated guess EMI=" + calemi);
				beanObj.setInput_emi(calemi);
				beanObj.setTenor(term);

			}

			// check condition for AmortType is Eqitorial then set default values for
			// Installment Type and Interest Type for plotting Amort Schedule.
			if (beanObj.getAmortType() == AmortConstant.AmortType_Equatorial) {
				if (isDebugEnabled)
					LOGGER.info("Inside InitializeInputModel:Amort Type is Equitorial");
				beanObj.setInstallmentType(AmortConstant.Installment_EquatedPrincipal);
				beanObj.setInterestType(AmortConstant.InterestTypeFlat);
				if (isDebugEnabled)
					LOGGER.info(
							"Inside InitializeInputModel:Set the default options for Installment Type is EqatedPrinciple and Interest Type is Flat If Amort Type is Eqitorial");
			}

			bpi = AmortUtil.calculateBPI(beanObj, beanObj.getLoanAmount());
			if (isDebugEnabled)
				LOGGER.debug("amort calculator : set all values to input model =" + beanObj.toString());
		} catch (Exception exception) {
			if (isDebugEnabled)
				LOGGER.error("Method : InitializeInputModel() : Exception thrown " + exception.toString());
			throw new AmortException.NumberFormatException(exception);
		}
		try {
			/*
			 * boolean skip_inital= false; if(null != beanObj.getArrSkipEMI()) {
			 * 
			 * ArrayList arrskip = (ArrayList)beanObj.getArrSkipEMI();
			 * 
			 * for(int k = 0;k < arrskip.size();k++) { SkipEMIInputBean sm =
			 * (SkipEMIInputBean)arrskip.get(k); if(1 == sm.getFrm_month() &&
			 * sm.getSkip_capital().equals("Y")){
			 * 
			 * skip_inital= true; } }
			 * 
			 * }
			 */
			Object bpiCapYnValue = hm.get(AmortConstant.bpi_cap_yn);
			if (bpiCapYnValue != null && !("".equals(bpiCapYnValue)) && bpiCapYnValue.equals("Y")) {

				beanObj.setBpiCapYN((String) hm.get(AmortConstant.bpi_cap_yn));
				if (bpi > 0) {
					bpi = AmortUtil.round(bpi, beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(),
							beanObj.getOthers_unit_ro());
					bpiAmt = beanObj.getLoanAmount() + bpi;
					beanObj.setOpenAmount(bpiAmt);
					bpiCalculated = "Y";
					beanObj.setLoanAmount(bpiAmt);
				}
			}

		} catch (Exception exception) {
			if (isDebugEnabled)
				LOGGER.error("Method : InitializeInputModel() : Exception thrown...bpi_cap_yn tag not found "
						+ exception.toString());
		}
		return beanObj;
	}

	public ArrayList calperprincrecovery(ArrayList final_list, AmortInputBean beanObj) throws AmortException {
		AmortInputBean tempbeanObj = beanObj;
		ArrayList princrec_list = new ArrayList();
		ArrayList princrec_final_list = new ArrayList();
		int inst_no = 1;
		double initial_loanamt = beanObj.getLoanAmount();
		if (beanObj.getNoOfAdvEMI() > 0 && beanObj.getAdjustAdvEMIAt() == 0) {
			initial_loanamt = beanObj.getLoanAmountOri();
		}
		int initialAdj = beanObj.getAdj();
		int initial_freq = beanObj.getRepaymentFrequency();
		double initial_rate = beanObj.getInterestRate();
		double initial_tenor = beanObj.getTenor();

		double total_emi = 0.0d;
		double total_principal = 0.0d;
		double total_interest = 0.0d;
		double total_round_emi = 0.0d;
		double total_round_interest = 0.0d;
		double total_round_principal = 0.0d;
		// AmortOverviewBean overviewbean = new AmortOverviewBean();
		ArrayList arrSkip = null;
		if (null != tempbeanObj.getArrSkipEMI())
			arrSkip = (ArrayList) tempbeanObj.getArrSkipEMI();

		if (null != tempbeanObj.getArrPerPrinRcvr()) {
			ArrayList arr = (ArrayList) tempbeanObj.getArrPerPrinRcvr();
			double loan_amtvalue = tempbeanObj.getLoanAmount();
			double last_bpvalue = 0.0d;
			int noofinstall_done = 0;
			for (int k = 0; k < arr.size(); k++) {
				StepPrinRecInputBean sm = (StepPrinRecInputBean) arr.get(k);
				if (null != arrSkip) {
					for (int i = 0; i < arrSkip.size(); i++) {
						SkipEMIInputBean skipbean = (SkipEMIInputBean) arrSkip.get(i);
						if (skipbean.getFrm_month() <= sm.getStepprin_to()
								&& skipbean.getFrm_month() + skipbean.getNo_month() - 1 >= sm.getStepprin_to()
								&& k < arr.size() - 1) {
							sm.setStepprin_to(skipbean.getFrm_month() - 1);
							StepPrinRecInputBean smnext = (StepPrinRecInputBean) arr.get(k + 1);
							smnext.setStepprin_frm(skipbean.getFrm_month());
						}
					}
				}
			}

			for (int k = 0; k < arr.size(); k++) {

				tempbeanObj.setAdjterm(0);
				if (k == arr.size() - 1)
					tempbeanObj.setAdj(initialAdj);
				else
					tempbeanObj.setAdj(2);
				StepPrinRecInputBean sm = (StepPrinRecInputBean) arr.get(k);
				double perprinc_amt = 0.0d;
				double perprinc_by = sm.getStepprin_by();
				// If % principle recovery in percentage then calculate amount
				if (sm.getStepprin_in() == 1) {
					perprinc_amt = initial_loanamt * perprinc_by / 100;
					if (sm.getStepprin_frm() == 1) {
						perprinc_amt = perprinc_amt - overviewbean.getAdvanceEMI();
					}
				} else {
					perprinc_amt = perprinc_by;
				}
				// set add to interest component amount to zero if k > 0
				if (k > 0)
					tempbeanObj.setAddIntAmount(0);

				// also set frequency and rate if given

				if (sm.getStepprin_freq() != 0) {
					tempbeanObj.setRepaymentFrequency(sm.getStepprin_freq());
				}

				if (sm.getStepprin_rate() != 0) {
					tempbeanObj.setInterestRate(sm.getStepprin_rate());
				}

				// set % Loan amount
				tempbeanObj.setLoanAmount(loan_amtvalue);
				// set ballon payment
				last_bpvalue = loan_amtvalue - perprinc_amt;
				tempbeanObj.setBP_Lastpayamt(last_bpvalue);
				// calculate tenor
				if (null != arrSkip) {
					ArrayList arrSkipnew = new ArrayList();
					for (int i = 0; i < arrSkip.size(); i++) {
						SkipEMIInputBean skipbean = (SkipEMIInputBean) arrSkip.get(i);
						SkipEMIInputBean skipbeannew = skipbean;
						if (skipbean.getFrm_month() <= sm.getStepprin_to()
								&& skipbean.getFrm_month() >= sm.getStepprin_frm()) {
							skipbeannew.setFrm_month(skipbeannew.getFrm_month() - sm.getStepprin_frm() + 1);
							arrSkipnew.add(skipbeannew);
						}
					}
					tempbeanObj.setArrSkipEMI(arrSkipnew);
				}

				double term = (sm.getStepprin_to() - sm.getStepprin_frm()) + 1;
				tempbeanObj.setTenor(term);// set tenor

				// tempbeanObj.getArrSkipEMI()

				if (k > 0) {
					tempbeanObj.setBPI_Recovery(0);
					tempbeanObj.setReduceBpi("N");
				}
				/*
				 * double calRate = 0.0; if(beanObj.getInterestType() ==
				 * AmortConstant.InterestTypeFlat) calRate = beanObj.getInterestRate(); else
				 * calRate =
				 * (AmortUtil.getRate(tempbeanObj.getInterestRate(),tempbeanObj.getCompoundFreq(
				 * ),tempbeanObj.getRepaymentFrequency()));
				 * 
				 * tempbeanObj.setInput_emi(AmortUtil.PMT(tempbeanObj.getLoanAmount(),
				 * (double)term,
				 * calRate,tempbeanObj.getInterestType(),tempbeanObj.getRepaymentFrequency(),
				 * tempbeanObj.getSameAdvEMI(),tempbeanObj.getNoOfAdvEMI(),tempbeanObj.
				 * getBP_Lastpayamt()));
				 */
				princrec_list = generateAmort(tempbeanObj);
				Iterator it = princrec_list.iterator();

				for (int j = 0; j < princrec_list.size() - 1; j++) {
					beanObj.setAdvEmiCalculated(false);
					AmortOutputBean gettemprec = (AmortOutputBean) princrec_list.get(j);
					if (j == princrec_list.size() - 2) {
						gettemprec.setEmiAmount(gettemprec.getEmiAmount() - last_bpvalue);
						gettemprec.setPrincipalEMI(gettemprec.getPrincipalEMI() - last_bpvalue);
						gettemprec.setClosingBalance(gettemprec.getOpeningBalance() - gettemprec.getPrincipalEMI());

						double round_emi = (gettemprec.getRoundEMI() - last_bpvalue);
						if (beanObj.getEMI_ro() == AmortConstant.RoundingAll && round_emi != 0)
							round_emi = AmortUtil.round(round_emi, beanObj.getEMI_ro_part(), beanObj.getEMI_ro_to(),
									beanObj.getEMI_unit_ro());

						if ((beanObj.getEMI_ro_part() == AmortConstant.RoundingDeciaml)
								&& (beanObj.getEMI_ro() == AmortConstant.RoundingAll))
							round_emi = Double.parseDouble(AmortUtil.handlingNum(round_emi, beanObj.getEMI_unit_ro()));
						gettemprec.setRoundEMI(round_emi);

						// Rounding Principle Portion with others rounding options.
						double round_principal = (gettemprec.getRoundPrincipal() - last_bpvalue);
						if (beanObj.getOthers_ro() == AmortConstant.RoundingAll)
							round_principal = AmortUtil.round(round_principal, beanObj.getOthers_ro_part(),
									beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro());

						if ((beanObj.getOthers_ro_part() == AmortConstant.RoundingDeciaml)
								&& (beanObj.getOthers_ro() == AmortConstant.RoundingAll))
							round_principal = Double
									.parseDouble(AmortUtil.handlingNum(round_principal, beanObj.getOthers_unit_ro()));
						gettemprec.setRoundPrincipal(round_principal);

						double round_close = (gettemprec.getRoundOpen() - gettemprec.getRoundPrincipal());
						// Rounding Close Portion with others rounding options.
						if (beanObj.getOthers_ro() == AmortConstant.RoundingAll)
							round_close = AmortUtil.round(round_close, beanObj.getOthers_ro_part(),
									beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro());
						if ((beanObj.getOthers_ro_part() == AmortConstant.RoundingDeciaml)
								&& (beanObj.getOthers_ro() == AmortConstant.RoundingAll))
							round_close = Double
									.parseDouble(AmortUtil.handlingNum(round_close, beanObj.getOthers_unit_ro()));
						gettemprec.setRoundClose(round_close);

					}

					gettemprec.setInstallment("" + inst_no);
					total_interest += gettemprec.getInterestEMI();
					total_principal += gettemprec.getPrincipalEMI();

					total_round_emi = (BigDecimal.valueOf(total_round_emi)
							.add(BigDecimal.valueOf(gettemprec.getRoundEMI()))).doubleValue();
					total_round_interest = (BigDecimal.valueOf(total_round_interest)
							.add(BigDecimal.valueOf(gettemprec.getRoundInterest()))).doubleValue();
					total_round_principal = (BigDecimal.valueOf(total_round_principal)
							.add(BigDecimal.valueOf(gettemprec.getRoundPrincipal()))).doubleValue();
					princrec_final_list.add(gettemprec);
					inst_no++;
				}
				AmortOutputBean getlastrec = (AmortOutputBean) princrec_list.get(princrec_list.size() - 2);

				int freqfactor = tempbeanObj.getFrequencyFactor();
				int freqperiod = tempbeanObj.getFrequencyPeriod();

				String date_dis = getlastrec.getCycleDate();
				String cycle_date = AmortUtil.getCycleDate(tempbeanObj.getDateOfCycle(), date_dis,
						tempbeanObj.getDateformat(), freqfactor, freqperiod, 1, tempbeanObj.isEOMAmort());
				String prev_date = AmortUtil.getCycleDate(tempbeanObj.getDateOfCycle(), cycle_date,
						tempbeanObj.getDateformat(), freqfactor, freqperiod, -1, tempbeanObj.isEOMAmort());
				tempbeanObj.setDateOfCycle(cycle_date);
				tempbeanObj.setDateOfDisbursement(prev_date);

				loan_amtvalue = loan_amtvalue - perprinc_amt;

			}

			overviewbean.setTotalInstallments(tempbeanObj.getTenor());
			overviewbean.setTotalEMI(total_emi);
			overviewbean.setTotalInterest(total_interest);
			overviewbean.setTotalPrincipal(total_principal);
			overviewbean.setTotalRoundEMI(total_round_emi);
			overviewbean.setTotalRoundInterest(total_round_interest);
			overviewbean.setTotalRoundPrincipal(total_round_principal);
		}

		real_tenor = initial_tenor;
		real_loanamount = initial_loanamt;
		return princrec_final_list;
	}

	public ArrayList Calculate_accrued_interest(AmortInputBean beanObj) {
		double accruedInterest = 0.0d;
		double monthlyinterest = 0.0d;
		AmortOutputBean out = new AmortOutputBean();
		SimpleDateFormat dateFormat = new SimpleDateFormat(beanObj.getDateformat());

		double tot_sum_accrued = 0.0d;
		int InstCnt = 1;
		try {

			Date d_date = dateFormat.parse(beanObj.getDateOfDisbursement());

			if (AmortUtil.simpleDayCount(beanObj.getInterest_Basis(),
					AmortUtil.getlastDate(beanObj.getDateOfDisbursement(), 0, beanObj),
					AmortUtil.getlastDate(beanObj.getDateOfFirstPayment(), 0, beanObj), beanObj.getDateformat()) > 58) {
				calculate_multiple_rows(beanObj);
				flagmultiple = true;
			} else {
				accruedInterest = ((((AmortOutputBean) otArr.get(0)).getRoundInterest())
						/ AmortUtil.simpleDayCount(beanObj.getInterest_Basis(), beanObj.getDateOfDisbursement(),
								beanObj.getDateOfFirstPayment(), beanObj.getDateformat()))
						* (AmortUtil.getlastDate(beanObj.getDateOfDisbursement(), beanObj) - d_date.getDate() + 1);

				if (beanObj.getOthers_ro() == AmortConstant.RoundingAll)
					accruedInterest = AmortUtil.round(accruedInterest, beanObj.getOthers_ro_part(),
							beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro());
				if ((beanObj.getEMI_ro_part() == AmortConstant.RoundingDeciaml)
						&& (beanObj.getEMI_ro() == AmortConstant.RoundingAll))
					accruedInterest = Double
							.parseDouble(AmortUtil.handlingNum(accruedInterest, beanObj.getEMI_unit_ro()));

				out.setInstallment(Integer.toString(0));

				out.setCycleDate(beanObj.getDateOfDisbursement());
				out.setOpeningBalance(0.00);
				out.setRestOpeningBalance(0.00);
				out.setClosingBalance(0.00);
				out.setEmiAmount(0.00);
				out.setInterestEMI(0.00);
				out.setPrincipalEMI(0.00);
				out.setRoundEMI(0.00);
				out.setRoundOpen(0.00);
				out.setRoundInterest(0.00);
				out.setRoundPrincipal(0.00);
				out.setRoundClose(0.00);
				out.setRoundRestOpenBal(0.00);
				out.setAccruedInterest(accruedInterest);
				out.setMonthlyInterest(accruedInterest);
				out.setEndofMonthDate(AmortUtil.getlastDate(out.getCycleDate(), 0, beanObj));
				if (beanObj.getintAmort().equals("Y")) {

					out.setIntAccruedInterest(accruedInterest);
					out.setIntMonthlyInterest(accruedInterest);

				}
				if ((AmortUtil.getlastDate(beanObj.getDateOfDisbursement(), 0, beanObj)
						.equals(AmortUtil.getlastDate(beanObj.getDateOfFirstPayment(), 0, beanObj)))) {
					out.setAccruedInterest(0.0d);
					out.setMonthlyInterest(0.0d);
					if (beanObj.getintAmort().equals("Y")) {
						out.setIntAccruedInterest(0.0d);
						out.setIntMonthlyInterest(0.0d);

					}
				}
				otArr.add(0, out);

			}

			if (flagmultiple)
				InstCnt = arr_last_date.size();

			for (int j = InstCnt; j < otArr.size(); j++) {
				out = (AmortOutputBean) otArr.get(j);
				if (j == otArr.size() - 1) {
					accruedInterest = 0;
				}
				Date d_date1 = dateFormat.parse(((AmortOutputBean) otArr.get(j)).getCycleDate());
				if (j != otArr.size() - 1)
					accruedInterest = ((((AmortOutputBean) otArr.get(j + 1)).getRoundInterest())
							/ AmortUtil.simpleDayCount(beanObj.getInterest_Basis(),
									((AmortOutputBean) otArr.get(j)).getCycleDate(),
									(((AmortOutputBean) otArr.get(j + 1)).getCycleDate()), beanObj.getDateformat()))
							* (AmortUtil.getlastDate(out.getCycleDate(), beanObj) - d_date1.getDate() + 1);

				monthlyinterest = (((AmortOutputBean) otArr.get(j)).getRoundInterest())
						- (((AmortOutputBean) otArr.get(j - 1)).getAccruedInterest()) + accruedInterest;
				if (flagmultiple && j == arr_last_date.size()) {
					for (int acnt = 0; acnt < arr_last_date.size(); acnt++)
						tot_sum_accrued = tot_sum_accrued + ((AmortOutputBean) otArr.get(acnt)).getAccruedInterest();
					monthlyinterest = (((AmortOutputBean) otArr.get(j)).getRoundInterest()) - tot_sum_accrued
							+ accruedInterest;

				}

				if (beanObj.getOthers_ro() == AmortConstant.RoundingAll)
					accruedInterest = AmortUtil.round(accruedInterest, beanObj.getOthers_ro_part(),
							beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro());
				if ((beanObj.getEMI_ro_part() == AmortConstant.RoundingDeciaml)
						&& (beanObj.getEMI_ro() == AmortConstant.RoundingAll))
					accruedInterest = Double
							.parseDouble(AmortUtil.handlingNum(accruedInterest, beanObj.getEMI_unit_ro()));

				out.setAccruedInterest(accruedInterest);

				if (beanObj.getOthers_ro() == AmortConstant.RoundingAll)
					monthlyinterest = AmortUtil.round(monthlyinterest, beanObj.getOthers_ro_part(),
							beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro());
				if ((beanObj.getEMI_ro_part() == AmortConstant.RoundingDeciaml)
						&& (beanObj.getEMI_ro() == AmortConstant.RoundingAll))
					monthlyinterest = Double
							.parseDouble(AmortUtil.handlingNum(monthlyinterest, beanObj.getEMI_unit_ro()));

				out.setMonthlyInterest(monthlyinterest);
				out.setEndofMonthDate(AmortUtil.getlastDate(out.getCycleDate(), 0, beanObj));

				if (beanObj.getintAmort().equals("Y")) {
					out.setIntAccruedInterest(accruedInterest);
					out.setIntMonthlyInterest(monthlyinterest);

				}
			}

		} catch (Exception exception) {
			if (isDebugEnabled)
				LOGGER.error("Method : calculateAmort() : Exception thrown " + exception.toString());

		}
		return otArr;
	}

	public HashMap getComp(AmortOutputBean outCust, AmortOutputBean outInt, AmortInputBean beanObj)
			throws AmortException {
		double cval = 0.0d;
		double unAmortizedComp = 0.0d;
		double total_sum = 0.0d;
		LinkedHashMap hmComponent = new LinkedHashMap();
		LinkedHashMap hmComponentUnAmortized = new LinkedHashMap();
		try {
			if (null != beanObj.getArrComp()) {
				ArrayList arrcomp = beanObj.getArrComp();
				if (beanObj.getIntprincipal() == 0 && tot_sum_fee == 0 && beanObj.getAmortMethod().equals("D")) {
					err.put("ERR_AMORT_METHOD", AmortConstant.AmortValidation.ERR_AMORT_METHOD);
					return hmComponent;
				}
				if (beanObj.getRVvalue() == 0) {
					for (int k = 0; k < arrcomp.size(); k++) {
						InternalAmortComponentBean icb = (InternalAmortComponentBean) arrcomp.get(k);
						if (icb.getcType().equals("C")) {
							if ((beanObj.getLoanAmount() != loanAmt) || beanObj.getAmortMethod().equals("D")) {
								cval = (-1) * (outInt.getIntMonthlyInterest() - outCust.getMonthlyInterest())
										* (icb.getVal() / tot_sum_fee);
								if (beanObj.getEMI_ro() == AmortConstant.RoundingAll)
									cval = AmortUtil.round(cval, beanObj.getEMI_ro_part(), beanObj.getEMI_ro_to(),
											beanObj.getEMI_unit_ro());
								if ((beanObj.getOthers_ro_part() == AmortConstant.RoundingDeciaml)
										&& (beanObj.getOthers_ro() == AmortConstant.RoundingAll))
									cval = Double.parseDouble(AmortUtil.handlingNum(cval, beanObj.getOthers_unit_ro()));
							} else if ((beanObj.getLoanAmount() == loanAmt) || beanObj.getAmortMethod().equals("S")) {
								if (beanObj.getTenor() == 0) {
									cval = (-1) * (icb.getVal() / real_tenor);
									if (beanObj.getEMI_ro() == AmortConstant.RoundingAll)
										cval = AmortUtil.round(cval, beanObj.getEMI_ro_part(), beanObj.getEMI_ro_to(),
												beanObj.getEMI_unit_ro());
									if ((beanObj.getOthers_ro_part() == AmortConstant.RoundingDeciaml)
											&& (beanObj.getOthers_ro() == AmortConstant.RoundingAll))
										cval = Double
												.parseDouble(AmortUtil.handlingNum(cval, beanObj.getOthers_unit_ro()));

								} else {
									cval = (-1) * (icb.getVal() / beanObj.getTenor());
									if (beanObj.getEMI_ro() == AmortConstant.RoundingAll)
										cval = AmortUtil.round(cval, beanObj.getEMI_ro_part(), beanObj.getEMI_ro_to(),
												beanObj.getEMI_unit_ro());
									if ((beanObj.getOthers_ro_part() == AmortConstant.RoundingDeciaml)
											&& (beanObj.getOthers_ro() == AmortConstant.RoundingAll))
										cval = Double
												.parseDouble(AmortUtil.handlingNum(cval, beanObj.getOthers_unit_ro()));

								}

							}
						}
						if (icb.getcType().equals("F")) {

							if ((beanObj.getLoanAmount() != loanAmt) || beanObj.getAmortMethod().equals("D")) {
								cval = (outInt.getIntMonthlyInterest() - outCust.getMonthlyInterest())
										* (icb.getVal() / tot_sum_fee);
								if (beanObj.getEMI_ro() == AmortConstant.RoundingAll)
									cval = AmortUtil.round(cval, beanObj.getEMI_ro_part(), beanObj.getEMI_ro_to(),
											beanObj.getEMI_unit_ro());
								if ((beanObj.getOthers_ro_part() == AmortConstant.RoundingDeciaml)
										&& (beanObj.getOthers_ro() == AmortConstant.RoundingAll))
									cval = Double.parseDouble(AmortUtil.handlingNum(cval, beanObj.getOthers_unit_ro()));

							} else if ((beanObj.getLoanAmount() == loanAmt) || beanObj.getAmortMethod().equals("S")) {
								if (beanObj.getTenor() == 0) {

									cval = (icb.getVal() / real_tenor);
									if (beanObj.getEMI_ro() == AmortConstant.RoundingAll)
										cval = AmortUtil.round(cval, beanObj.getEMI_ro_part(), beanObj.getEMI_ro_to(),
												beanObj.getEMI_unit_ro());
									if ((beanObj.getOthers_ro_part() == AmortConstant.RoundingDeciaml)
											&& (beanObj.getOthers_ro() == AmortConstant.RoundingAll))
										cval = Double
												.parseDouble(AmortUtil.handlingNum(cval, beanObj.getOthers_unit_ro()));

								} else {
									cval = (icb.getVal() / beanObj.getTenor());
									if (beanObj.getEMI_ro() == AmortConstant.RoundingAll)
										cval = AmortUtil.round(cval, beanObj.getEMI_ro_part(), beanObj.getEMI_ro_to(),
												beanObj.getEMI_unit_ro());
									if ((beanObj.getOthers_ro_part() == AmortConstant.RoundingDeciaml)
											&& (beanObj.getOthers_ro() == AmortConstant.RoundingAll))
										cval = Double
												.parseDouble(AmortUtil.handlingNum(cval, beanObj.getOthers_unit_ro()));

								}

							}
						}

						hmComponent.put(icb.getName(), new Double(cval));
					}
					if (!flagmultiple)
						cvalVec.add(Integer.parseInt(outCust.getInstallment()), hmComponent);
					if (Integer.parseInt(outInt.getInstallment()) == otArr.size() - 2) {

						for (int k = 0; k < arrcomp.size(); k++) {
							total_sum = 0.0d;
							InternalAmortComponentBean icb = (InternalAmortComponentBean) arrcomp.get(k);

							for (int l = 0; l < otArr.size() - 2; l++) {
								total_sum = total_sum + Double
										.valueOf((((LinkedHashMap) cvalVec.get(l)).get(icb.getName()).toString()))
										.doubleValue();
							}

							cval = icb.getVal() - total_sum;
							if (icb.getcType().equals("C")) {
								cval = icb.getVal() + total_sum;
								cval = cval * (-1);
							}
							hmComponent.put(icb.getName(), new Double(cval));

						}

					}
				} else if (beanObj.getRVvalue() != 0) {
					for (int k = 0; k < arrcomp.size(); k++) {
						InternalAmortComponentBean icb = (InternalAmortComponentBean) arrcomp.get(k);
						if (icb.getcType().equals("C")) {
							if (beanObj.getTenor() == 0) {
								if (Integer.parseInt(outCust.getInstallment()) == 0) {
									unAmortizedComp = (-1) * (icb.getVal());
									cval = unAmortizedComp / (real_tenor - Integer.parseInt(outCust.getInstallment()));
									if (AmortUtil.getlastDate(beanObj.getDateOfDisbursement(), 0, beanObj).equals(
											AmortUtil.getlastDate(beanObj.getDateOfFirstPayment(), 0, beanObj))) {
										unAmortizedComp = 0.0d;
										cval = 0.0d;
									}
								} else {

									unAmortizedComp = Double
											.valueOf((((LinkedHashMap) unAmortized
													.get(Integer.parseInt(outCust.getInstallment()) - 1))
													.get(icb.getName()).toString()))
											.doubleValue()
											- Double.valueOf((((LinkedHashMap) cvalVec
													.get(Integer.parseInt(outCust.getInstallment()) - 1))
													.get(icb.getName()).toString())).doubleValue();

									if (real_tenor != Integer.parseInt(outCust.getInstallment()))
										cval = unAmortizedComp
												/ (real_tenor - Integer.parseInt(outCust.getInstallment()));
									else
										cval = 0;
								}
								if (beanObj.getOthers_ro() == AmortConstant.RoundingAll)
									cval = AmortUtil.round(cval, beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(),
											beanObj.getOthers_unit_ro());
							} else {

								if (Integer.parseInt(outCust.getInstallment()) == 0) {
									unAmortizedComp = (-1) * (icb.getVal());

									cval = unAmortizedComp
											/ (beanObj.getTenor() - Integer.parseInt(outCust.getInstallment()));
									if (AmortUtil.getlastDate(beanObj.getDateOfDisbursement(), 0, beanObj).equals(
											AmortUtil.getlastDate(beanObj.getDateOfFirstPayment(), 0, beanObj))) {
										unAmortizedComp = 0.0d;
										cval = 0.0d;
										sameDates = true;
									}

								} else {
									unAmortizedComp = Double
											.valueOf((((LinkedHashMap) unAmortized
													.get(Integer.parseInt(outCust.getInstallment()) - 1))
													.get(icb.getName()).toString()))
											.doubleValue()
											- Double.valueOf((((LinkedHashMap) cvalVec
													.get(Integer.parseInt(outCust.getInstallment()) - 1))
													.get(icb.getName()).toString())).doubleValue();
									if (sameDates && (outCust.getInstallment()).equals("1"))
										unAmortizedComp = (-1) * (icb.getVal());
									if (beanObj.getTenor() != Integer.parseInt(outCust.getInstallment()) && sameDates)
										cval = unAmortizedComp
												/ (beanObj.getTenor() - Integer.parseInt(outCust.getInstallment()) + 1);
									else if (beanObj.getTenor() != Integer.parseInt(outCust.getInstallment())
											&& !sameDates)
										cval = unAmortizedComp
												/ (beanObj.getTenor() - Integer.parseInt(outCust.getInstallment()));
									else {
										cval = 0;
										if (sameDates)
											cval = unAmortizedComp / (beanObj.getTenor()
													- Integer.parseInt(outCust.getInstallment()) + 1);
									}
								}
								if (beanObj.getOthers_ro() == AmortConstant.RoundingAll)
									cval = AmortUtil.round(cval, beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(),
											beanObj.getOthers_unit_ro());
							}
						}
						if (icb.getcType().equals("F")) {
							if (beanObj.getTenor() == 0) {

								if (Integer.parseInt(outCust.getInstallment()) == 0) {
									unAmortizedComp = (icb.getVal());

									cval = unAmortizedComp / (real_tenor - Integer.parseInt(outCust.getInstallment()));

								} else {
									unAmortizedComp = Double
											.valueOf((((LinkedHashMap) unAmortized
													.get(Integer.parseInt(outCust.getInstallment()) - 1))
													.get(icb.getName()).toString()))
											.doubleValue()
											- Double.valueOf((((LinkedHashMap) cvalVec
													.get(Integer.parseInt(outCust.getInstallment()) - 1))
													.get(icb.getName()).toString())).doubleValue();
									if (real_tenor != Integer.parseInt(outCust.getInstallment()))
										cval = unAmortizedComp
												/ (real_tenor - Integer.parseInt(outCust.getInstallment()));
									else
										cval = 0;
								}
								if (beanObj.getOthers_ro() == AmortConstant.RoundingAll)
									cval = AmortUtil.round(cval, beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(),
											beanObj.getOthers_unit_ro());
							} else {
								if (Integer.parseInt(outCust.getInstallment()) == 0) {
									unAmortizedComp = (icb.getVal());
									cval = unAmortizedComp
											/ (beanObj.getTenor() - Integer.parseInt(outCust.getInstallment()));
									if (AmortUtil.getlastDate(beanObj.getDateOfDisbursement(), 0, beanObj).equals(
											AmortUtil.getlastDate(beanObj.getDateOfFirstPayment(), 0, beanObj))) {
										unAmortizedComp = 0.0d;
										cval = 0.0d;
										sameDates = true;
									}

								} else {
									unAmortizedComp = Double
											.valueOf((((LinkedHashMap) unAmortized
													.get(Integer.parseInt(outCust.getInstallment()) - 1))
													.get(icb.getName()).toString()))
											.doubleValue()
											- Double.valueOf((((LinkedHashMap) cvalVec
													.get(Integer.parseInt(outCust.getInstallment()) - 1))
													.get(icb.getName()).toString())).doubleValue();
									if (sameDates && (outCust.getInstallment()).equals("1"))
										unAmortizedComp = (icb.getVal());
									if (beanObj.getTenor() != Integer.parseInt(outCust.getInstallment()) && sameDates)
										cval = unAmortizedComp
												/ (beanObj.getTenor() - Integer.parseInt(outCust.getInstallment()) + 1);
									else if (beanObj.getTenor() != Integer.parseInt(outCust.getInstallment())
											&& !sameDates)
										cval = unAmortizedComp
												/ (beanObj.getTenor() - Integer.parseInt(outCust.getInstallment()));
									else {
										cval = 0;
										if (sameDates)
											cval = unAmortizedComp / (beanObj.getTenor()
													- Integer.parseInt(outCust.getInstallment()) + 1);
									}
								}
								if (beanObj.getOthers_ro() == AmortConstant.RoundingAll)
									cval = AmortUtil.round(cval, beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(),
											beanObj.getOthers_unit_ro());
							}
						}

						hmComponent.put(icb.getName(), new Double(cval));
						hmComponentUnAmortized.put(icb.getName(), new Double(unAmortizedComp));

					}
					cvalVec.add(Integer.parseInt(outCust.getInstallment()), hmComponent);
					unAmortized.add(Integer.parseInt(outCust.getInstallment()), hmComponentUnAmortized);

				}
			}
		} catch (Exception exception) {
			if (isDebugEnabled)
				LOGGER.error("Method : getComponent() : Exception thrown " + exception.toString());
			throw new AmortException.CalculationFactoryException(exception);
		}

		return hmComponent;
	}

	public HashMap getComp(AmortOutputBean outCust, AmortOutputBean outInt, AmortInputBean beanObj, int cnt)
			throws AmortException {
		double cval = 0.0d;
		double unAmortizedComp = 0.0d;
		double total_sum = 0.0d;
		LinkedHashMap hmComponent = new LinkedHashMap();
		LinkedHashMap hmComponentUnAmortized = new LinkedHashMap();
		try {

			if (null != beanObj.getArrComp()) {
				ArrayList arrcomp = beanObj.getArrComp();
				if (beanObj.getIntprincipal() == 0 && tot_sum_fee == 0 && beanObj.getAmortMethod().equals("D")) {
					err.put("ERR_AMORT_METHOD", AmortConstant.AmortValidation.ERR_AMORT_METHOD);

				}
				if (beanObj.getRVvalue() == 0) {
					for (int k = 0; k < arrcomp.size(); k++) {
						InternalAmortComponentBean icb = (InternalAmortComponentBean) arrcomp.get(k);
						if (icb.getcType().equals("C")) {
							if ((beanObj.getLoanAmount() != loanAmt) || beanObj.getAmortMethod().equals("D")) {
								cval = (-1) * (outInt.getIntMonthlyInterest() - outCust.getMonthlyInterest())
										* (icb.getVal() / tot_sum_fee);

								if (beanObj.getEMI_ro() == AmortConstant.RoundingAll)
									cval = AmortUtil.round(cval, beanObj.getEMI_ro_part(), beanObj.getEMI_ro_to(),
											beanObj.getEMI_unit_ro());
								if ((beanObj.getOthers_ro_part() == AmortConstant.RoundingDeciaml)
										&& (beanObj.getOthers_ro() == AmortConstant.RoundingAll))
									cval = Double.parseDouble(AmortUtil.handlingNum(cval, beanObj.getOthers_unit_ro()));
							} else if ((beanObj.getLoanAmount() == loanAmt) || beanObj.getAmortMethod().equals("S")) {
								if (beanObj.getTenor() == 0) {

									cval = (-1) * (icb.getVal() / (real_tenor + arr_last_date.size()));
									if (beanObj.getEMI_ro() == AmortConstant.RoundingAll)
										cval = AmortUtil.round(cval, beanObj.getEMI_ro_part(), beanObj.getEMI_ro_to(),
												beanObj.getEMI_unit_ro());
									if ((beanObj.getOthers_ro_part() == AmortConstant.RoundingDeciaml)
											&& (beanObj.getOthers_ro() == AmortConstant.RoundingAll))
										cval = Double
												.parseDouble(AmortUtil.handlingNum(cval, beanObj.getOthers_unit_ro()));

								} else {
									cval = (-1) * (icb.getVal() / (beanObj.getTenor() + arr_last_date.size()));
									if (beanObj.getEMI_ro() == AmortConstant.RoundingAll)
										cval = AmortUtil.round(cval, beanObj.getEMI_ro_part(), beanObj.getEMI_ro_to(),
												beanObj.getEMI_unit_ro());
									if ((beanObj.getOthers_ro_part() == AmortConstant.RoundingDeciaml)
											&& (beanObj.getOthers_ro() == AmortConstant.RoundingAll))
										cval = Double
												.parseDouble(AmortUtil.handlingNum(cval, beanObj.getOthers_unit_ro()));

								}

							}
						}
						if (icb.getcType().equals("F")) {

							if ((beanObj.getLoanAmount() != loanAmt) || beanObj.getAmortMethod().equals("D")) {
								cval = (outInt.getIntMonthlyInterest() - outCust.getMonthlyInterest())
										* (icb.getVal() / tot_sum_fee);

								if (beanObj.getEMI_ro() == AmortConstant.RoundingAll)
									cval = AmortUtil.round(cval, beanObj.getEMI_ro_part(), beanObj.getEMI_ro_to(),
											beanObj.getEMI_unit_ro());
								if ((beanObj.getOthers_ro_part() == AmortConstant.RoundingDeciaml)
										&& (beanObj.getOthers_ro() == AmortConstant.RoundingAll))
									cval = Double.parseDouble(AmortUtil.handlingNum(cval, beanObj.getOthers_unit_ro()));

							} else if ((beanObj.getLoanAmount() == loanAmt) || beanObj.getAmortMethod().equals("S")) {
								if (beanObj.getTenor() == 0) {

									cval = (icb.getVal() / (real_tenor + arr_last_date.size()));
									if (beanObj.getEMI_ro() == AmortConstant.RoundingAll)
										cval = AmortUtil.round(cval, beanObj.getEMI_ro_part(), beanObj.getEMI_ro_to(),
												beanObj.getEMI_unit_ro());
									if ((beanObj.getOthers_ro_part() == AmortConstant.RoundingDeciaml)
											&& (beanObj.getOthers_ro() == AmortConstant.RoundingAll))
										cval = Double
												.parseDouble(AmortUtil.handlingNum(cval, beanObj.getOthers_unit_ro()));

								} else {
									cval = (icb.getVal() / (beanObj.getTenor() + arr_last_date.size()));
									if (beanObj.getEMI_ro() == AmortConstant.RoundingAll)
										cval = AmortUtil.round(cval, beanObj.getEMI_ro_part(), beanObj.getEMI_ro_to(),
												beanObj.getEMI_unit_ro());
									if ((beanObj.getOthers_ro_part() == AmortConstant.RoundingDeciaml)
											&& (beanObj.getOthers_ro() == AmortConstant.RoundingAll))
										cval = Double
												.parseDouble(AmortUtil.handlingNum(cval, beanObj.getOthers_unit_ro()));

								}

							}
						}

						hmComponent.put(icb.getName(), new Double(cval));
					}

					cvalVec.add(cnt, hmComponent);
					if (Integer.parseInt(outInt.getInstallment()) + arr_last_date.size() - 1 == otArr.size() - 2) {

						for (int k = 0; k < arrcomp.size(); k++) {
							total_sum = 0.0d;
							InternalAmortComponentBean icb = (InternalAmortComponentBean) arrcomp.get(k);

							for (int l = 0; l < otArr.size() - 2; l++) {
								total_sum = total_sum + Double
										.valueOf((((LinkedHashMap) cvalVec.get(l)).get(icb.getName()).toString()))
										.doubleValue();
							}

							cval = icb.getVal() - total_sum;
							if (icb.getcType().equals("C")) {
								cval = icb.getVal() + total_sum;
								cval = cval * (-1);
							}
							hmComponent.put(icb.getName(), new Double(cval));

						}

					}
				} else if (beanObj.getRVvalue() != 0) {
					for (int k = 0; k < arrcomp.size(); k++) {

						InternalAmortComponentBean icb = (InternalAmortComponentBean) arrcomp.get(k);
						if (icb.getcType().equals("C")) {
							if (beanObj.getTenor() == 0) {
								if (cnt == 0) {
									unAmortizedComp = (-1) * (icb.getVal());
									cval = unAmortizedComp / (real_tenor - Integer.parseInt(outCust.getInstallment()));
									if (AmortUtil.getlastDate(beanObj.getDateOfDisbursement(), 0, beanObj).equals(
											AmortUtil.getlastDate(beanObj.getDateOfFirstPayment(), 0, beanObj))) {
										unAmortizedComp = 0.0d;
										cval = 0.0d;
									}
								} else {

									unAmortizedComp = Double
											.valueOf((((LinkedHashMap) unAmortized
													.get(Integer.parseInt(outCust.getInstallment()) - 1))
													.get(icb.getName()).toString()))
											.doubleValue()
											- Double.valueOf((((LinkedHashMap) cvalVec
													.get(Integer.parseInt(outCust.getInstallment()) - 1))
													.get(icb.getName()).toString())).doubleValue();

									if (real_tenor != Integer.parseInt(outCust.getInstallment()))
										cval = unAmortizedComp
												/ (real_tenor - Integer.parseInt(outCust.getInstallment()));
									else
										cval = 0;
								}
								if (beanObj.getOthers_ro() == AmortConstant.RoundingAll)
									cval = AmortUtil.round(cval, beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(),
											beanObj.getOthers_unit_ro());
							} else {

								if (cnt == 0) {
									unAmortizedComp = (-1) * (icb.getVal());

									cval = unAmortizedComp / (beanObj.getTenor() + arr_last_date.size() - cnt);
									if (AmortUtil.getlastDate(beanObj.getDateOfDisbursement(), 0, beanObj).equals(
											AmortUtil.getlastDate(beanObj.getDateOfFirstPayment(), 0, beanObj))) {
										unAmortizedComp = 0.0d;
										cval = 0.0d;
										sameDates = true;
									}

								} else {
									unAmortizedComp = Double
											.valueOf((((LinkedHashMap) unAmortized.get(cnt - 1)).get(icb.getName())
													.toString()))
											.doubleValue()
											- Double.valueOf((((LinkedHashMap) cvalVec.get(cnt - 1)).get(icb.getName())
													.toString())).doubleValue();
									if (sameDates && cnt == 1)
										unAmortizedComp = (-1) * (icb.getVal());
									if (beanObj.getTenor() + arr_last_date.size() != cnt && sameDates)
										cval = unAmortizedComp / (beanObj.getTenor() + arr_last_date.size() - cnt + 1);
									else if (beanObj.getTenor() + arr_last_date.size() != cnt && !sameDates)
										cval = unAmortizedComp / (beanObj.getTenor() + arr_last_date.size() - cnt);
									else {
										cval = 0;
										if (sameDates)
											cval = unAmortizedComp
													/ (beanObj.getTenor() + arr_last_date.size() - cnt + 1);
									}
								}
								if (beanObj.getOthers_ro() == AmortConstant.RoundingAll)
									cval = AmortUtil.round(cval, beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(),
											beanObj.getOthers_unit_ro());
							}
						}
						if (icb.getcType().equals("F")) {
							if (beanObj.getTenor() == 0) {

								if (Integer.parseInt(outCust.getInstallment()) == 0) {
									unAmortizedComp = (icb.getVal());

									cval = unAmortizedComp / (real_tenor - Integer.parseInt(outCust.getInstallment()));

								} else {
									unAmortizedComp = Double
											.valueOf((((LinkedHashMap) unAmortized
													.get(Integer.parseInt(outCust.getInstallment()) - 1))
													.get(icb.getName()).toString()))
											.doubleValue()
											- Double.valueOf((((LinkedHashMap) cvalVec
													.get(Integer.parseInt(outCust.getInstallment()) - 1))
													.get(icb.getName()).toString())).doubleValue();
									if (real_tenor != Integer.parseInt(outCust.getInstallment()))
										cval = unAmortizedComp
												/ (real_tenor - Integer.parseInt(outCust.getInstallment()));
									else
										cval = 0;
								}
								if (beanObj.getOthers_ro() == AmortConstant.RoundingAll)
									cval = AmortUtil.round(cval, beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(),
											beanObj.getOthers_unit_ro());
							} else {
								if (cnt == 0) {
									unAmortizedComp = (icb.getVal());
									cval = unAmortizedComp / (beanObj.getTenor() + arr_last_date.size() - cnt);
									if (AmortUtil.getlastDate(beanObj.getDateOfDisbursement(), 0, beanObj).equals(
											AmortUtil.getlastDate(beanObj.getDateOfFirstPayment(), 0, beanObj))) {
										unAmortizedComp = 0.0d;
										cval = 0.0d;
										sameDates = true;
									}

								} else {
									unAmortizedComp = Double
											.valueOf((((LinkedHashMap) unAmortized.get(cnt - 1)).get(icb.getName())
													.toString()))
											.doubleValue()
											- Double.valueOf((((LinkedHashMap) cvalVec.get(cnt - 1)).get(icb.getName())
													.toString())).doubleValue();
									if (sameDates && cnt == 1)
										unAmortizedComp = (icb.getVal());
									if (beanObj.getTenor() + arr_last_date.size() != cnt && sameDates)
										cval = unAmortizedComp / (beanObj.getTenor() + arr_last_date.size() - cnt + 1);
									else if (beanObj.getTenor() + arr_last_date.size() != cnt && !sameDates)
										cval = unAmortizedComp / (beanObj.getTenor() + arr_last_date.size() - cnt);
									else {
										cval = 0;
										if (sameDates)
											cval = unAmortizedComp
													/ (beanObj.getTenor() + arr_last_date.size() - cnt + 1);
									}
								}
								if (beanObj.getOthers_ro() == AmortConstant.RoundingAll)
									cval = AmortUtil.round(cval, beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(),
											beanObj.getOthers_unit_ro());
							}

						}

						hmComponent.put(icb.getName(), new Double(cval));
						hmComponentUnAmortized.put(icb.getName(), new Double(unAmortizedComp));

					}
					cvalVec.add(cnt, hmComponent);
					unAmortized.add(cnt, hmComponentUnAmortized);
				}
			}
		} catch (Exception exception) {
			if (isDebugEnabled)
				LOGGER.error("Method : getComponent() : Exception thrown " + exception.toString());
			throw new AmortException.CalculationFactoryException(exception);
		}

		return hmComponent;
	}

	/**
	 * generateErrors API starts. This API calls from all APIs. All the Error are
	 * add into HashMap where ever generated. In each API check that error hash map
	 * null or not.If not then call comes to this API. In this API what ever Error
	 * is present then this will return that Error message.
	 */

	public ErrorMessages generateErrors(ErrorMessages errors) {
		if (isDebugEnabled)
			LOGGER.debug("inside generateErrors Method.");
		if (null != err.get("ERR_ADV"))
			errors.addError((String) err.get("ERR_ADV"), "", "ERR_ADV");
		if (null != err.get("ERR_Round"))
			errors.addError((String) err.get("ERR_Round"), "", "ERR_Round");
		if (null != err.get("ERR_ADJRATE"))
			errors.addError((String) err.get("ERR_ADJRATE"), "", "ERR_ADJRATE");
		if (null != err.get("ERR_NEGATIVE_AMORT"))
			errors.addError((String) err.get("ERR_NEGATIVE_AMORT"), "", "ERR_NEGATIVE_AMORT");
		if (null != err.get("ERR_ADD_INT"))
			errors.addError((String) err.get("ERR_ADD_INT"), "", "ERR_ADD_INT");
		if (null != err.get("ERR_INPUT"))
			errors.addError((String) err.get("ERR_INPUT"), "", "ERR_INPUT");
		if (null != err.get("ERR_ADJUSTED_EMI"))
			errors.addError((String) err.get("ERR_ADJUSTED_EMI"), "", "ERR_ADJUSTED_EMI");
		if (null != err.get("NEGATIVE_INTEREST_RATE"))
			errors.addError((String) err.get("NEGATIVE_INTEREST_RATE"), "", "NEGATIVE_INTEREST_RATE");
		if (null != err.get("INVALID_ROUNDING_PARAMETERS"))
			errors.addError((String) err.get("INVALID_ROUNDING_PARAMETERS"), "", "INVALID_ROUNDING_PARAMETERS");
		if (null != err.get("ERR_INPUT_BPI_PARAMETERS"))
			errors.addError((String) err.get("ERR_INPUT_BPI_PARAMETERS"), "", "ERR_INPUT_BPI_PARAMETERS");
		if (null != err.get("ERR_INT_AMORT_FREQUENCY"))
			errors.addError((String) err.get("ERR_INT_AMORT_FREQUENCY"), "", "ERR_INT_AMORT_FREQUENCY");
		if (null != err.get("ERR_OSP_GREATERTHAN_EMI"))
			errors.addError((String) err.get("ERR_OSP_GREATERTHAN_EMI"), "", "ERR_OSP_GREATERTHAN_EMI");
		if (null != err.get("ERR_AMORT_METHOD"))
			errors.addError((String) err.get("ERR_AMORT_METHOD"), "", "ERR_AMORT_METHOD");
		if (null != err.get("ERR_NEGATIVE_IRR"))
			errors.addError((String) err.get("ERR_NEGATIVE_IRR"), "", "ERR_NEGATIVE_IRR");
		if (null != err.get("ERR_NEGATIVE_STEPAMOUNT"))
			errors.addError((String) err.get("ERR_NEGATIVE_STEPAMOUNT"), "", "ERR_NEGATIVE_STEPAMOUNT");
		if (null != err.get("ERR_ADJTENOR"))
			errors.addError((String) err.get("ERR_ADJTENOR"), "", "ERR_ADJTENOR");
		if (null != err.get("ERR_ADJOPTION"))
			errors.addError((String) err.get("ERR_ADJOPTION"), "", "ERR_ADJOPTION");
		if (null != err.get("ERR_ADJOPTIONUNIQUE"))
			errors.addError((String) err.get("ERR_ADJOPTIONUNIQUE"), "", "ERR_ADJOPTIONUNIQUE");
		if (null != err.get("ERR_INSTINT"))
			errors.addError((String) err.get("ERR_INSTINT"), "", "ERR_INSTINT");

		return errors;

	}

	private String convertToDecimal(BigDecimal value) {
		DecimalFormat df = new DecimalFormat("#.######");
		return df.format(value);
	}

	public String convertToDecimal(double value) {
		DecimalFormat df = new DecimalFormat("#.######");
		return df.format(value);
	}

	public ArrayList calculateAmortStraightLine(AmortInputBean beanObj) {
		// If term is not in year
		int frequency = beanObj.getRepaymentFrequency();
		double tenor = 0.0d;
		int term = 0;
		String cycle_date = "";
		String prev_date = "";
		String date_dis = "";
		int freqfactor = beanObj.getFrequencyFactor();
		int freqperiod = beanObj.getFrequencyPeriod();
		double accrued_interest_eom = 0.0d;
		double depreciation_fee = 0.0d;
		double financial_fee = 0.0d;
		otArr = new ArrayList();
		term = AmortUtil.getNPER(tenor, frequency, beanObj);
		depreciation_fee = (beanObj.getLoanAmount() - beanObj.getLoanAmount() * (beanObj.getRVvalue() / 100)) / term;
		financial_fee = beanObj.getInput_emi() - depreciation_fee;
		if (AmortUtil.simpleDayCount(beanObj.getInterest_Basis(),
				AmortUtil.getlastDate(beanObj.getDateOfDisbursement(), 0, beanObj),
				AmortUtil.getlastDate(beanObj.getDateOfFirstPayment(), 0, beanObj), beanObj.getDateformat()) > 58) {
			calculate_multiple_rows(beanObj);
			flagmultiple = true;
		}
		double counter = 0.0d;
		if (flagmultiple)
			counter = term + arr_last_date.size() - 1;
		else if (!flagmultiple)
			counter = term;

		for (int i = arr_last_date.size(); i <= counter; i++) { // for loop for number of installments

			AmortOutputBean out = new AmortOutputBean();

			if (i == arr_last_date.size() && !flagmultiple) {
				cycle_date = beanObj.getDateOfDisbursement();
				prev_date = AmortUtil.getCycleDate(beanObj.getDateOfFirstPayment(), beanObj.getDateOfFirstPayment(),
						beanObj.getDateformat(), freqfactor, freqperiod, -1, beanObj.isEOMAmort());
				out.setEndofMonthDate(AmortUtil.getlastDate(cycle_date, 0, beanObj));
				out.setEmiAmount(beanObj.getInput_emi());

			} else {

				date_dis = beanObj.getCurrentCycleDate();
				cycle_date = AmortUtil.getCycleDate(beanObj.getDateOfFirstPayment(), prev_date, beanObj.getDateformat(),
						freqfactor, freqperiod, 1, beanObj.isEOMAmort());
				if (flagmultiple && i == arr_last_date.size())
					cycle_date = beanObj.getDateOfFirstPayment();
				prev_date = cycle_date;
				out.setEndofMonthDate(AmortUtil.getlastDate(cycle_date, 0, beanObj));
				if (beanObj.getOthers_ro() == AmortConstant.RoundingAll)
					depreciation_fee = AmortUtil.round(depreciation_fee, beanObj.getOthers_ro_part(),
							beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro());
				out.setDepreciationFee(depreciation_fee);
				if (beanObj.getOthers_ro() == AmortConstant.RoundingAll)
					financial_fee = AmortUtil.round(financial_fee, beanObj.getOthers_ro_part(),
							beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro());
				out.setFinancialFee(financial_fee);
			}

			beanObj.setCurrentCycleDate(cycle_date);
			out.setInstallment(Integer.toString(i));
			if (flagmultiple)
				out.setInstallment(Integer.toString(i - arr_last_date.size() + 1));
			out.setCycleDate(cycle_date);
			out.setOpeningBalance(0.0d);
			out.setRestOpeningBalance(0.0d);
			out.setClosingBalance(0.0d);
			out.setEmiAmount(beanObj.getInput_emi());
			out.setInterestEMI(0.0d);
			out.setPrincipalEMI(0.0d);
			out.setRoundEMI(0.0d);
			out.setRoundOpen(0.0d);
			out.setRoundInterest(0.0d);
			out.setRoundPrincipal(0.0d);
			out.setRoundClose(0.0d);
			out.setRoundRestOpenBal(0.0d);
			otArr.add(out);
		}

		for (int j = arr_last_date.size(); j <= counter; j++) {
			AmortOutputBean out1 = (AmortOutputBean) otArr.get(j);
			if (!flagmultiple) {
				if (j <= term - 1) {
					accrued_interest_eom = ((((AmortOutputBean) otArr.get(arr_last_date.size())).getEmiAmount())
							/ AmortUtil.simpleDayCount(beanObj.getInterest_Basis(),
									((AmortOutputBean) otArr.get(j)).getCycleDate(),
									(((AmortOutputBean) otArr.get(j + 1)).getCycleDate()), beanObj.getDateformat()))
							* (AmortUtil.simpleDayCount(beanObj.getInterest_Basis(),
									((AmortOutputBean) otArr.get(j)).getCycleDate(),
									(((AmortOutputBean) otArr.get(j)).getEndofMonthDate()), beanObj.getDateformat())
									+ 1);
					if (AmortUtil.getlastDate(beanObj.getDateOfDisbursement(), 0, beanObj)
							.equals(AmortUtil.getlastDate(beanObj.getDateOfFirstPayment(), 0, beanObj))
							&& j == arr_last_date.size())
						accrued_interest_eom = 0.0d;
				}
			}
			if (flagmultiple) {
				if (j <= counter - 2) {
					accrued_interest_eom = ((((AmortOutputBean) otArr.get(arr_last_date.size())).getEmiAmount())
							/ AmortUtil.simpleDayCount(beanObj.getInterest_Basis(),
									((AmortOutputBean) otArr.get(j)).getCycleDate(),
									(((AmortOutputBean) otArr.get(j + 1)).getCycleDate()), beanObj.getDateformat()))
							* (AmortUtil.simpleDayCount(beanObj.getInterest_Basis(),
									((AmortOutputBean) otArr.get(j)).getCycleDate(),
									(((AmortOutputBean) otArr.get(j)).getEndofMonthDate()), beanObj.getDateformat())
									+ 1);
					if (AmortUtil.getlastDate(beanObj.getDateOfDisbursement(), 0, beanObj)
							.equals(AmortUtil.getlastDate(beanObj.getDateOfFirstPayment(), 0, beanObj))
							&& j == arr_last_date.size())
						accrued_interest_eom = 0.0d;
				}
			}
			if (beanObj.getOthers_ro() == AmortConstant.RoundingAll)
				accrued_interest_eom = AmortUtil.round(accrued_interest_eom, beanObj.getOthers_ro_part(),
						beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro());
			out1.setIntAccruedInterest(accrued_interest_eom);
			if (j == term + arr_last_date.size()) {
				out1.setIntAccruedInterest(0.0d);
			}

		}

		return otArr;
	}

	/**
	 * This API called from generateAmort() API. Find the Exact value using high-low
	 * method. Check what is missing out of four parameters
	 * principle,EMI,Tenor,Interest in the input.This is already check in
	 * InitializeInputModel() API. What ever is missing in out of above four
	 * parameters set the value true in bean object and calculate guess value for
	 * missing parameter and set into bean object. generate the amort using guess
	 * value by calling calculateAmort api.If residue is there then incrase by 1 to
	 * guessvalue and gives the call to calculate amort api. If residue is not zero
	 * then calculate the factor and add that factor to gussvalue and gives the call
	 * to calculateamort.
	 */

	public ArrayList calAmortHighLow(ArrayList final_list, AmortInputBean beanObj) throws AmortException {
		if (isDebugEnabled)
			LOGGER.debug("Inside generateAmort : ");
		double emi = beanObj.getInput_emi();
		double guessLoaAmount = beanObj.getLoanAmount();
		double finalemi = emi;
		double prn = 0;
		double tot_round_int78 = 0;
		double tenor = 0;
		if (!beanObj.isTenorpresent()) {
			// In this API Amort is scheduled using guess value.
			final_list = calculateAmort(beanObj, beanObj.getInput_emi(), 0);
			// If residue is not zero then High-Low will starts to generate Amort.

			if (residue != 0 && !beanObj.isAdvEmiCalculated()) {
				residue_normal = residue;
				if (isDebugEnabled)
					LOGGER.debug("Inside generateAmort : Normal Case  residue = " + residue);
				// If residue is not zero then add 0ne to guessvalue and gives the call to
				// calculateamort.
				if (beanObj.isEMIpresent())
					finalemi = emi + 1;
				if (beanObj.isLoanAmountpresent()) {
					double lamtIterationOne = guessLoaAmount + 1;
					beanObj.setLoanAmount(lamtIterationOne);
				}
				/*
				 * else if(beanObj.getNoOfAdvEMI() > 0 && beanObj.getAdjustAdvEMIAt() == 0 ) {
				 * beanObj.setLoanAmount(beanObj.getLoanAmountOri() - (beanObj.getNoOfAdvEMI() *
				 * finalemi)); }
				 */
				if (beanObj.isInterestRatepresent())
					interest_rate = (interest_rate + 0.00001);

				if (isDebugEnabled)
					LOGGER.debug("Inside generateAmort :Iteration 1  tenor = " + beanObj.getTenor() + "EMI="
							+ beanObj.getInput_emi() + "loan_amt = " + beanObj.getLoanAmount() + "interest_rate = "
							+ beanObj.getInterestRate());
				final_list = calculateAmort(beanObj, finalemi, 1);
				if (isDebugEnabled)
					LOGGER.debug("Inside generateAmort : Iteration 1  residue = " + residue);
				residue_iteration = residue;
				// Check again residue zero or not.
				if (residue_normal != residue_iteration && residue_iteration != 0) {
					// If residue is not zero then calculate factor.
					factor = residue_normal / (residue_normal - residue_iteration);
					// Add the factor to the guess value and give the call to calculateamort.
					if (beanObj.isEMIpresent())
						finalemi = emi + factor;
					if (beanObj.isLoanAmountpresent()) {
						double finalLoanAmt = guessLoaAmount + factor;
						beanObj.setLoanAmount(AmortUtil.round(finalLoanAmt, beanObj.getOthers_ro_part(),
								beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro()));
					}
					/*
					 * else if(beanObj.getNoOfAdvEMI() > 0 && beanObj.getAdjustAdvEMIAt() == 0 ) {
					 * beanObj.setLoanAmount(beanObj.getLoanAmount() - (beanObj.getNoOfAdvEMI() *
					 * finalemi)); }
					 */
					if (beanObj.isInterestRatepresent()) {
						factor = residue_normal * 0.00001 / (residue_normal - residue_iteration);
						interest_rate = (interest_rate + factor);
						beanObj.setInterestRate(interest_rate * 100);
						real_interest_rate = interest_rate;
					}
					if (isDebugEnabled)
						LOGGER.debug("Inside generateAmort :Iteration 2   factor = " + factor);
					if (isDebugEnabled)
						LOGGER.debug("Inside generateAmort :Iteration 2   Final tenor = " + beanObj.getTenor()
								+ "Final emi=" + beanObj.getInput_emi() + "Final LoanAMount=" + beanObj.getLoanAmount()
								+ "Final InterestRate= " + beanObj.getInterestRate());
					final_list = calculateAmort(beanObj, finalemi, 2);
				}
			}
		}
		// By calling this api we will get exact schedule.
		final_list = calculateAmort(beanObj, finalemi, 3);

		return final_list;
	}

	public ArrayList calRule78Flat(ArrayList rule_list, AmortInputBean beanObj) throws AmortException {
		if (isDebugEnabled)
			LOGGER.debug("Inside generateAmort : ");
		double emi = beanObj.getInput_emi();
		double finalemi = emi;
		rule_list = calculateAmort(beanObj, finalemi, 1);
		return rule_list;
	}

	/**
	 * Recalculates the Amort schedule based on rule 78
	 * 
	 * @param ArrayList of AmortOutputBean Object.
	 *
	 * @return ArrayList of AmortOutputBean Object
	 */

	public ArrayList calculateRule78new(ArrayList arrAmort, AmortInputBean beanObj) throws AmortException {
		double factor_78 = 0.0d;
		double factor_78new = 0.0d;
		double interest_78 = 0.0d;
		double principal_78 = 0.0d;
		double round_interest_78 = 0.0d;
		double round_principal_78 = 0.0d;
		double temp = 0.0d;
		double total_interest = 0.0d;
		double total_principal = 0.0d;
		double total_round_interest = 0.0d;
		double total_round_principal = 0.0d;
		Double total = 0.0d;
		double total_int = 0.0d;
		double total_emi = 0.0d;
		double total_pri = 0.0d;
		double round_open = 0.0d;
		ArrayList arrAmort1 = new ArrayList();
		int cnt = 0;
		int k1 = 1;
		if (isDebugEnabled)
			LOGGER.info("inside calculateRule78");
		if (isDebugEnabled)
			LOGGER.info("tot_installments = " + tot_installments + "tot int =  " + tot_int);
		try {
			for (int k = 0; k < beanObj.getTenor78(); k++) {
				AmortOutputBean Out = (AmortOutputBean) arrAmort.get(k);
				AmortOutputBean prvOut = new AmortOutputBean();

				if (k > 0) {
					prvOut = (AmortOutputBean) arrAmort.get(k - 1);
					Out.setOpeningBalance(prvOut.getClosingBalance());
					Out.setRoundOpen(prvOut.getRoundClose());
				}

				temp = Double.parseDouble(Integer.toString(k));
				factor_78 = (Double.parseDouble(Integer.toString(arrAmort.size())) - temp)
						/ Double.parseDouble(Integer.toString(tot_installments));

				interest_78 = (factor_78 * tot_int);

				principal_78 = Out.getEmiAmount() - interest_78;

				round_interest_78 = (factor_78 * tot_round_int78);
				if (beanObj.getOthers_ro() == AmortConstant.RoundingAll)
					round_interest_78 = AmortUtil.round(round_interest_78, beanObj.getOthers_ro_part(),
							beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro());
				round_principal_78 = Out.getRoundEMI() - round_interest_78;
				if (beanObj.getOthers_ro() == AmortConstant.RoundingAll)
					round_principal_78 = AmortUtil.round(round_principal_78, beanObj.getOthers_ro_part(),
							beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro());
				total_interest += interest_78;
				total_principal += principal_78;
				total_round_interest += round_interest_78;
				total_round_principal += round_principal_78;

				Out.setPrincipalEMI(principal_78);
				Out.setInterestEMI(interest_78);
				Out.setRoundPrincipal(round_principal_78);
				Out.setRoundInterest(round_interest_78);

				Out.setClosingBalance(Out.getOpeningBalance() - Out.getPrincipalEMI());
				Out.setRoundClose(Out.getRoundOpen() - Out.getRoundPrincipal());
				if ((beanObj.getOthers_ro_part() == AmortConstant.RoundingDeciaml)
						&& (beanObj.getOthers_ro() == AmortConstant.RoundingAll))
					Out.setRoundClose(Double.parseDouble(AmortUtil
							.handlingNum((Out.getRoundOpen() - Out.getRoundPrincipal()), beanObj.getOthers_unit_ro())));

				arrAmort.set(k, Out);
			}
			for (int j = 1; j <= beanObj.getTenor78(); j++) {

				AmortOutputBean ruleOut = (AmortOutputBean) arrAmort.get(j - 1);
				AmortOutputBean ruleOutNew = (AmortOutputBean) arrAmort.get(j - 1);
				AmortOutputBean prvOutNew = new AmortOutputBean();
				if (round_open == 0)
					round_open = ruleOut.getOpeningBalance();
				// addition logic should be written here...

				if (j % (beanObj.getRepaymentFrequency() / beanObj.getRepaymentFrequency78()) == 0) {

					ruleOutNew.setInstallment(Integer.toString(k1));
					k1++;
					// System.out.println(ruleOutNew.getInstallment());
					total_int += ruleOut.getInterestEMI();
					total_emi += ruleOutNew.getEmiAmount();
					total_pri += ruleOutNew.getPrincipalEMI();
					overviewbean.setInterestEMI(total_int);
					overviewbean.setIntPrincipalEMI(total_pri);
					overviewbean.setTotalRoundEMI(total_emi);
					ruleOutNew.setInterestEMI(total_int);
					ruleOutNew.setEmiAmount(total_emi);
					ruleOutNew.setIntPrincipalEMI(total_pri);
					ruleOutNew.setAdj_installment(total_emi);
					ruleOutNew.setRoundEMI(AmortUtil.round(total_emi, beanObj.getOthers_ro_part(),
							beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro()));
					ruleOutNew.setAdj_installment(AmortUtil.round(total_emi, beanObj.getOthers_ro_part(),
							beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro()));
					ruleOutNew.setRoundInterest(AmortUtil.round(total_int, beanObj.getOthers_ro_part(),
							beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro()));
					ruleOutNew.setRoundPrincipal(AmortUtil.round(total_pri, beanObj.getOthers_ro_part(),
							beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro()));
					// prvOutNew = (AmortOutputBean)arrAmort.get(cnt);
					ruleOutNew.setOpeningBalance(AmortUtil.round(round_open, beanObj.getOthers_ro_part(),
							beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro()));
					ruleOutNew.setRoundOpen(AmortUtil.round(round_open, beanObj.getOthers_ro_part(),
							beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro()));
					round_open = 0;
					ruleOutNew.setClosingBalance(ruleOutNew.getOpeningBalance() - ruleOutNew.getPrincipalEMI());
					ruleOutNew.setRoundClose(ruleOutNew.getRoundOpen() - ruleOutNew.getRoundPrincipal());
					ruleOutNew.setRoundClose(AmortUtil.round(ruleOutNew.getRoundClose(), beanObj.getOthers_ro_part(),
							beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro()));
					arrAmort1.add(cnt, ruleOutNew);
					cnt++;
					total_int = 0.0d;
					total_emi = 0.0d;
					total_pri = 0.0d;
				} else {
					total_int += ruleOut.getInterestEMI();
					total_emi += ruleOutNew.getEmiAmount();
					total_pri += ruleOutNew.getPrincipalEMI();
				}
			}
			overviewbean.setTotalInterest(total_interest);
			overviewbean.setTotalPrincipal(total_principal);
			overviewbean.setTotalRoundInterest(total_round_interest);
			overviewbean.setTotalRoundPrincipal(total_round_principal);
			real_emi = ((AmortOutputBean) arrAmort1.get(0)).getRoundEMI();

		} catch (Exception exception) {
			if (isDebugEnabled)
				LOGGER.error("Method : calculateRule78() : Exception thrown " + exception.toString());
			throw new AmortException.CalculationFactoryException(exception);
		}
		return arrAmort1;
	}

	/**
	 * LatEMIAdjustment starts. This API call from generateAmort API. Once Total
	 * Amort scheduled then call comes to this API for Adjusting last installment.
	 * Last Installment getting Adjusting based on EMIADJ options in I/P either it
	 * creates new Installment or Adjusing same Instalment.
	 * 
	 * @param final_list
	 * @param beanObj
	 * @return arraylist of output bean object.
	 * @throws AmortException
	 */
	public ArrayList latEMI_Adjustment(ArrayList final_list, AmortInputBean beanObj) throws AmortException {
		if (isDebugEnabled)
			LOGGER.debug("Inside latEMI_Adjustment:");
		AmortOutputBean out = new AmortOutputBean();
		double round_emi = 0.0d;
		double round_principal = 0.0d;
		double round_close = 0.0d;
		double final_emi = 0.0d;
		double principal_emi = 0.0;
		double close_balance = 0.0;
		double threshValue = 0.0;
		double round_interest = 0.0d;
		double interest = 0.0d;
		double round_open = 0.0d;
		double open_balance = 0.0d;
		String cycle_date = "";
		String date_dis = beanObj.getCurrentCycleDate();
		cycle_date = AmortUtil.getCycleDate(beanObj.getDateOfCycle(), date_dis, beanObj.getDateformat(),
				beanObj.getFrequencyFactor(), beanObj.getFrequencyPeriod(), 1, beanObj.isEOMAmort());
		String prev_date = AmortUtil.getCycleDate(beanObj.getDateOfCycle(), cycle_date, beanObj.getDateformat(),
				beanObj.getFrequencyFactor(), beanObj.getFrequencyPeriod(), -1, beanObj.isEOMAmort());
		real_loanamount = ((AmortOutputBean) otArr.get(0)).getRoundOpen();
		real_emi = ((AmortOutputBean) otArr.get(0)).getRoundEMI();
		// Calculate Thresh Value using Nearest option
		if (beanObj.getOnbasisValue() == AmortConstant.Component.onBasisNearest) {
			beanObj.setThreshValue(50);
			threshValue = ((AmortOutputBean) otArr.get(final_list.size() - 1)).getRoundEMI()
					* (beanObj.getThreshValue() / 100);
			if (isDebugEnabled)
				LOGGER.debug("Inside latEMI_Adjustment:Calculated ThreshValue Using NearestOption  =" + threshValue);
		}
		// Calculate Thresh Value using Percent Basis
		else if (beanObj.getOnbasisValue() == AmortConstant.Component.onBasisPerBasis) {
			threshValue = ((AmortOutputBean) otArr.get(final_list.size() - 1)).getRoundEMI()
					* (beanObj.getThreshValue() / 100);
			if (isDebugEnabled)
				LOGGER.debug("Inside latEMI_Adjustment:Calculated ThreshValue Using PercentBasis  =" + threshValue);
		}
		// Calculate Thresh Value using Amount Basis
		else if (beanObj.getOnbasisValue() == AmortConstant.Component.onBasisAmtBasis) {
			threshValue = beanObj.getThreshValue();
			if (isDebugEnabled)
				LOGGER.debug("Inside latEMI_Adjustment:Calculated ThreshValue Using AmountBasis  =" + threshValue);
		}
		// Check EMIOP value.If the value is 3 then it creates the new row always else
		// it creates either new installment or Adjust same installment conditionally
		// based on thresh value.
		if ((threshValue < (((AmortOutputBean) otArr.get(final_list.size() - 1)).getClosingBalance())
				&& beanObj.getEmiOPValue() == AmortConstant.Component.conditionNewRow
				&& (beanObj.getInstallmentType() != AmortConstant.Installment_InterestOnly)
				&& (beanObj.getInstallmentType() != 3)) || beanObj.getEmiOPValue() == AmortConstant.Component.newRow) {
			round_principal = ((AmortOutputBean) otArr.get(final_list.size() - 1)).getRoundClose();
			round_open = round_principal;
			interest = AmortUtil.getInterest(round_open, beanObj.getInterestRate(), beanObj.getCompoundFreq(),
					beanObj.getInterest_Basis(), prev_date, cycle_date, beanObj.getDateformat(), false,
					beanObj.getIntCalMethod());
			round_interest = interest;
			round_interest = AmortUtil.round(round_interest, beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(),
					beanObj.getOthers_unit_ro());
			round_emi = (BigDecimal.valueOf(round_principal).add(BigDecimal.valueOf(round_interest))).doubleValue();
			round_close = (BigDecimal.valueOf(round_open).subtract(BigDecimal.valueOf(round_principal))).doubleValue();
			round_emi = AmortUtil.round(round_emi, beanObj.getEMI_ro_part(), beanObj.getEMI_ro_to(),
					beanObj.getEMI_unit_ro());
			round_interest = (BigDecimal.valueOf(round_emi).subtract(BigDecimal.valueOf(round_principal)))
					.doubleValue();

			if (threshValue < (((AmortOutputBean) otArr.get(final_list.size() - 1)).getClosingBalance())
					&& beanObj.getEmiOPValue() == AmortConstant.Component.conditionNewRow
					&& beanObj.getAdj() == AmortConstant.Component.Adj_in_interest) {
				round_emi = AmortUtil.round(beanObj.getInput_emi(), beanObj.getEMI_ro_part(), beanObj.getEMI_ro_to(),
						beanObj.getEMI_unit_ro());
				interest = round_emi - round_principal;
				round_interest = interest;
				round_interest = AmortUtil.round(round_interest, beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(),
						beanObj.getOthers_unit_ro());
			}

			if (isDebugEnabled)
				LOGGER.debug("Inside latEMI_Adjustment:New Row is created");

			out.setCycleDate(cycle_date);
			beanObj.setReal_lid(cycle_date);
			out.setInstallment(Integer.toString(final_list.size() + 1));
			out.setOpeningBalance(round_open);
			out.setRestOpeningBalance(round_open);
			out.setClosingBalance(round_close);
			out.setEmiAmount(round_emi);
			out.setInterestEMI(interest);
			out.setPrincipalEMI(round_principal);
			out.setRoundEMI(round_emi);
			out.setRoundOpen(round_open);
			out.setRoundInterest(round_interest);
			out.setRoundPrincipal(round_principal);
			out.setRoundClose(round_close);
			out.setRoundRestOpenBal(round_open);
			out.setAdj_installment(round_emi);
			overviewbean.setTotalRoundEMI(overviewbean.getTotalRoundEMI() + round_emi);
			overviewbean.setTotalRoundInterest(overviewbean.getTotalRoundInterest() + round_interest);
			overviewbean.setTotalRoundPrincipal(overviewbean.getTotalRoundPrincipal() + round_principal);
			otArr.add(out);
			real_tenor = otArr.size();
			if (isDebugEnabled)
				LOGGER.debug("Inside latEMI_Adjustment:New Row Opening Balance=" + round_open + "and EMI=" + round_emi
						+ "and interest=" + round_interest + "Principle=" + round_principal);

		}
		// Adjust in same installment.
		else {
			if (isDebugEnabled)
				LOGGER.debug("Inside latEMI_Adjustment:Adjust in Same Installment");
			out = ((AmortOutputBean) otArr.get(final_list.size() - 1));
			round_close = out.getRoundClose();
			round_open = out.getRoundOpen();
			round_emi = out.getRoundEMI();
			round_principal = out.getRoundPrincipal();
			double adjinstallment = out.getAdj_installment();
			open_balance = out.getOpeningBalance();
			final_emi = out.getEmiAmount();
			principal_emi = out.getPrincipalEMI();
			close_balance = out.getClosingBalance();
			interest = out.getInterestEMI();
			round_interest = out.getRoundInterest();
			overviewbean.setTotalRoundPrincipal(overviewbean.getTotalRoundPrincipal() + close_balance);
			overviewbean.setTotalRoundPrincipal(AmortUtil.round(overviewbean.getTotalRoundPrincipal(),
					beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro()));
			// Last Installment EMI is not same as per rest of EMIs.
			if (beanObj.getAdj() == AmortConstant.Component.Adj_in_EMI) {
				final_emi = (BigDecimal.valueOf(final_emi).add(BigDecimal.valueOf(close_balance))).doubleValue();
				principal_emi = (BigDecimal.valueOf(principal_emi).add(BigDecimal.valueOf(close_balance)))
						.doubleValue();
				close_balance = (BigDecimal.valueOf(open_balance).subtract(BigDecimal.valueOf(principal_emi)))
						.doubleValue();
				round_emi = (BigDecimal.valueOf(round_emi).add(BigDecimal.valueOf(round_close))).doubleValue();
				round_principal = (BigDecimal.valueOf(round_principal).add(BigDecimal.valueOf(round_close)))
						.doubleValue();
				round_close = (BigDecimal.valueOf(round_open).subtract(BigDecimal.valueOf(round_principal)))
						.doubleValue();
				adjinstallment = (BigDecimal.valueOf(adjinstallment).add(BigDecimal.valueOf(round_close)))
						.doubleValue();
			} else {
				if (isDebugEnabled)
					LOGGER.debug("Inside latEMI_Adjustment:Keep Last EMI is same.");
				principal_emi = (BigDecimal.valueOf(principal_emi).add(BigDecimal.valueOf(close_balance)))
						.doubleValue();
				close_balance = (BigDecimal.valueOf(open_balance).subtract(BigDecimal.valueOf(principal_emi)))
						.doubleValue();
				round_principal = (BigDecimal.valueOf(round_principal).add(BigDecimal.valueOf(round_close)))
						.doubleValue();
				interest = (BigDecimal.valueOf(round_emi).subtract(BigDecimal.valueOf(round_principal))).doubleValue();
				round_interest = interest;
				round_close = (BigDecimal.valueOf(round_open).subtract(BigDecimal.valueOf(round_principal)))
						.doubleValue();
			}
			round_emi = AmortUtil.round(round_emi, beanObj.getEMI_ro_part(), beanObj.getEMI_ro_to(),
					beanObj.getEMI_unit_ro());
			round_interest = (BigDecimal.valueOf(round_emi).subtract(BigDecimal.valueOf(round_principal)))
					.doubleValue();
			out.setClosingBalance(close_balance);
			out.setEmiAmount(final_emi);
			out.setPrincipalEMI(principal_emi);
			out.setRoundEMI(round_emi);
			out.setRoundPrincipal(round_principal);
			out.setRoundClose(round_close);
			out.setAdj_installment(adjinstallment);
			out.setInterestEMI(interest);
			out.setRoundInterest(round_interest);
			cycle_date = out.getCycleDate();
			beanObj.setReal_lid(cycle_date);
			otArr.set(final_list.size() - 1, out);
			if (isDebugEnabled)
				LOGGER.debug("Last Installment is Adjusted");
			if (isDebugEnabled)
				LOGGER.debug("Inside latEMI_Adjustment:After Adjusting in same Installment values are  Opening Balance="
						+ round_open + "and EMI=" + round_emi + "and interest=" + round_interest + "Principle="
						+ round_principal);
		}

		return otArr;
	}

	/**
	 * Calculate BPI. This API is called twice from generateAmort API.In first call
	 * this API first calculate the BPI. If BPI is not zero then calculate first
	 * cycle date and set in bean object. In second call BPI value is set to first
	 * Installment as per Input parameters.
	 */
	public ArrayList calBPI(ArrayList arrfinal, AmortInputBean beanObj) throws AmortException {
		if (isDebugEnabled)
			LOGGER.info("Inside calBPI:");
		String disbursedate = beanObj.getDateOfDisbursement();
		String dateformat = beanObj.getDateformat();
		int intbasis = beanObj.getBPIMethod();
		double amount = beanObj.getLoanAmount();

//added line start
		String strBPIDate = "";
		String payment_date = beanObj.getDateOfFirstPayment();
		int freqfactor = beanObj.getFrequencyFactor();
		int freqperiod = beanObj.getFrequencyPeriod();
//added line End

		if (beanObj.getOriginalRate() != 0) {
			beanObj.setInterestRate(beanObj.getOriginalRate());
		}
		double interestrate = beanObj.getInterestRate();
		AmortOutputBean tempout = new AmortOutputBean();
		AmortOutputBean am = null;
		AmortOutputBean am0 = null;
		ArrayList temp_list = new ArrayList();
		am0 = (AmortOutputBean) arrfinal.get(0);
		if (beanObj.getPayFirst().equals(AmortConstant.Component.payFirstY)) {
			am = (AmortOutputBean) arrfinal.get(0);
		} else {
			am = (AmortOutputBean) arrfinal.get(arrfinal.size() - 1);
		}
		String paymentdate = am0.getCycleDate();
		amount = am0.getRoundOpen();
		bpi = AmortUtil.calculateBPI(beanObj, amount);
		if (isDebugEnabled)
			LOGGER.info("Inside calBPI: bpi =" + bpi);
		if (beanObj.getAdjRate() != 0.0)
			interestrate = beanObj.getAdjRate();
		if (bpi > 0.0 && beanObj.getReduceBpi().equals(AmortConstant.Component.reduceBpiY)) {

			// First Interest will be zero incase of Initial Skip with No Capitalize,so
			// negative BPI is not to consider.

			if (beanObj.getPayFirst().equals(AmortConstant.Component.payFirstY)) {
				double intEMI = am.getInterestEMI();
				intEMI = AmortUtil.round(intEMI, beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(),
						beanObj.getOthers_unit_ro());
				double n_bpi = bpi + am.getInterestEMI();
				if (isDebugEnabled)
					LOGGER.info("Inside calBPI:n_bpi =" + n_bpi);
				n_bpi = AmortUtil.round(n_bpi, beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(),
						beanObj.getOthers_unit_ro());
				if (isDebugEnabled)
					LOGGER.info(
							"Inside calBPI:In case of Negative bpi calculated bpi for setting to first Installment.n_bpi ="
									+ n_bpi);
				if (beanObj.getBPI_Recovery() == AmortConstant.AmortTypeConstant.BPIRecovery_UF) {
					am.setInterestEMI(am.getInterestEMI());
					am.setIntInterestEMI(am.getInterestEMI());
					am.setRoundInterest(am.getRoundInterest());
				} else {
					// if(null == beanObj.getArrSkipEMI() )
					// {
					am.setInterestEMI(n_bpi);
					am.setIntInterestEMI(n_bpi);
					am.setRoundInterest(n_bpi);
					// }
				}
				if (null != beanObj.getArrStepEMI()) {
					ArrayList arr = (ArrayList) beanObj.getArrStepEMI();
					/*
					 * StepEMIInputBean sm = (StepEMIInputBean)arr.get(0); if(sm.getStepbasis() == 2
					 * ) { //am.setPrincipalEMI(am.getEmiAmount() - am.getInterestEMI());
					 * am.setPrincipalEMI(am.getEmiAmount() - am.getRoundInterest());
					 * 
					 * //am.setRoundPrincipal(AmortUtil.round(am.getEmiAmount() -
					 * am.getInterestEMI(), beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(),
					 * beanObj.getOthers_unit_ro()));
					 * am.setRoundPrincipal(AmortUtil.round(am.getPrincipalEMI(),
					 * beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(),
					 * beanObj.getOthers_unit_ro())); }
					 */
					if ((beanObj.getAdjust_Option() == AmortConstant.AdjustEMI
							|| beanObj.getAdjust_Option() == AmortConstant.AdjustTenor)) {
						// BPI date for Payable case
						strBPIDate = AmortUtil.getCycleDate(beanObj.getDateOfCycle(), payment_date,
								beanObj.getDateformat(), freqfactor, freqperiod, -1, beanObj.isEOMAmort());
					} else
						strBPIDate = AmortUtil.getCycleDate(beanObj.getDateOfCycle(), beanObj.getDateOfCycle(),
								beanObj.getDateformat(), beanObj.getFrequencyFactor(), beanObj.getFrequencyPeriod(), -1,
								beanObj.isEOMAmort());

				}
				if (null != beanObj.getArrSkipEMI()) {
					ArrayList arr = (ArrayList) beanObj.getArrSkipEMI();
					SkipEMIInputBean sm = (SkipEMIInputBean) arr.get(0);
					if (sm.getFrm_month() == 1 && bpi > 0) {
						if (beanObj.getBPI_Recovery() == 0 && sm.getSkip_capital().equals("Y")
								&& (sm.getFrm_month() == 1 && bpi > 0)) {
							am.setEmiAmount(0);
							am.setRoundEMI(0);
							am.setInterestEMI(intEMI);
							am.setIntInterestEMI(intEMI);
							am.setRoundInterest(intEMI);

						} else if (beanObj.getBPI_Recovery() != AmortConstant.AmortTypeConstant.BPIRecovery_UF
								&& !sm.getSkip_capital().equals(AmortConstant.SkipEMI.SkipCapitalValuePayable)) {

							am.setEmiAmount(bpi);
							am.setRoundEMI(AmortUtil.round(bpi, beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(),
									beanObj.getOthers_unit_ro()));
						}
						// Interest Option : Free
						if (beanObj.getBPI_Recovery() == AmortConstant.AmortTypeConstant.BPIRecovery_UF
								&& !sm.getSkip_capital().equals(AmortConstant.SkipEMI.SkipCapitalValuePayable)
								&& !sm.getSkip_capital().equals(AmortConstant.SkipEMI.SkipCapitalValueY)) {

							am.setEmiAmount(0);
							am.setRoundEMI(0);
							strBPIDate = AmortUtil.getCycleDate(beanObj.getDateOfCycle(), payment_date,
									beanObj.getDateformat(), freqfactor, freqperiod, -1, beanObj.isEOMAmort());
						}
						// Interest Option : Capital
						if (beanObj.getBPI_Recovery() == AmortConstant.AmortTypeConstant.BPIRecovery_UF
								&& !sm.getSkip_capital().equals(AmortConstant.SkipEMI.SkipCapitalValuePayable)
								&& sm.getSkip_capital().equals(AmortConstant.SkipEMI.SkipCapitalValueY)) {

							am.setEmiAmount(0);
							am.setRoundEMI(0);
							strBPIDate = AmortUtil.getCycleDate(beanObj.getDateOfCycle(), payment_date,
									beanObj.getDateformat(), freqfactor, freqperiod, -1, beanObj.isEOMAmort());
						}
					} // For BPI date when non-InitialSkip case
					else if (beanObj.getBPI_Recovery() == AmortConstant.AmortTypeConstant.BPIRecovery_UF
							&& !sm.getSkip_capital().equals(AmortConstant.SkipEMI.SkipCapitalValuePayable)) {
						strBPIDate = AmortUtil.getCycleDate(beanObj.getDateOfCycle(), payment_date,
								beanObj.getDateformat(), freqfactor, freqperiod, -1, beanObj.isEOMAmort());
						am.setEmiAmount(am.getPrincipalEMI() + am.getInterestEMI());
						am.setRoundEMI(AmortUtil.round(am.getRoundPrincipal() + am.getRoundInterest(),
								beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro()));
					} else {
						strBPIDate = AmortUtil.getCycleDate(beanObj.getDateOfCycle(), beanObj.getDateOfCycle(),
								beanObj.getDateformat(), beanObj.getFrequencyFactor(), beanObj.getFrequencyPeriod(), -1,
								beanObj.isEOMAmort());
						am.setEmiAmount(am.getPrincipalEMI() + am.getInterestEMI());
						am.setRoundEMI(AmortUtil.round(am.getRoundPrincipal() + am.getRoundInterest(),
								beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro()));
					}
				} else {// BPI date for Normal variation
					strBPIDate = AmortUtil.getCycleDate(beanObj.getDateOfCycle(), beanObj.getDateOfCycle(),
							beanObj.getDateformat(), beanObj.getFrequencyFactor(), beanObj.getFrequencyPeriod(), -1,
							beanObj.isEOMAmort());
					am.setEmiAmount(am.getPrincipalEMI() + am.getInterestEMI());
					am.setRoundEMI(AmortUtil.round(am.getRoundPrincipal() + am.getRoundInterest(),
							beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro()));
				}

				// added lines End

				if (beanObj.getBPI_Recovery() == AmortConstant.AmortTypeConstant.BPIRecovery_UF) {
					bpi = AmortUtil.round(bpi, beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(),
							beanObj.getOthers_unit_ro());
					overviewbean.setBPIAmount(bpi);
// commented		String strBPIDate = AmortUtil.getCycleDate(beanObj.getDateOfCycle(),beanObj.getDateOfCycle(),beanObj.getDateformat(),beanObj.getFrequencyFactor(),beanObj.getFrequencyPeriod(),-1,beanObj.isEOMAmort());
					tempout.setInstallment(Integer.toString(0));
					tempout.setCycleDate(strBPIDate);
					tempout.setOpeningBalance(0);
					tempout.setRestOpeningBalance(0);
					tempout.setRoundOpen(0);
					tempout.setRoundRestOpenBal(0);
					tempout.setRoundEMI(bpi);
					tempout.setRoundInterest(bpi);
					tempout.setRoundPrincipal(0);
					tempout.setEmiAmount(bpi);
					tempout.setPrincipalEMI(0);
					tempout.setRoundClose(0);
					tempout.setInterestEMI(bpi);
					tempout.setClosingBalance(0);
					temp_list.add(0, tempout);
					temp_list.addAll(1, arrfinal);
					return temp_list;
				}

			} else {

				double l_bpi = bpi + am.getInterestEMI();
				l_bpi = AmortUtil.round(l_bpi, beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(),
						beanObj.getOthers_unit_ro());
				if (isDebugEnabled)
					LOGGER.info(
							"Inside calBPI:In case of Negative bpi calculated bpi for setting to first Installment.n_bpi ="
									+ l_bpi);
				overviewbean.setBPIAmount(bpi);
				am.setInterestEMI(l_bpi);
				am.setIntInterestEMI(l_bpi);
				am.setRoundInterest(l_bpi);

				am.setEmiAmount(am.getPrincipalEMI() + am.getInterestEMI());
				am.setRoundEMI(AmortUtil.round(am.getRoundPrincipal() + am.getRoundInterest(),
						beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro()));
			}
		}
		return arrfinal;
	}

	public ArrayList adjust_Last_bullet_intprin(ArrayList final_list, AmortInputBean beanObj) {
		double amt = 0;
		ArrayList arrlist = new ArrayList();

		Iterator itr = final_list.iterator();

		int size = final_list.size();
		for (int i = 0; i < size; i++) {

			Object objBullet = final_list.get(i);
			AmortOutputBean Out = (AmortOutputBean) objBullet;
			double temp_amt = Out.getRoundInterest();
			amt = amt + temp_amt;

			if (i == size - 1) {
				double prin_amt = Out.getPrincipalEMI();
				Out.setEmiAmount(prin_amt + amt);
				Out.setAdj_installment(prin_amt + amt);
				Out.setRoundEMI(prin_amt + amt);
				Out.setRoundInterest(amt);
				Out.setInstallment("1");
				Out.setInterestEMI(amt);

			}
		}

		int cnt = 0;
		if (null != beanObj.getArrFees() && beanObj.getArrFees().size() > 0) {

			cnt++;
		}

		/*
		 * if (isDebugEnabled) LOGGER.debug("Array List iteration Start"); ListIterator
		 * listIterator = null; while(listIterator.hasNext()){ listIterator.next();
		 * Object obj = final_list.get(cnt); cnt++;
		 * 
		 * if (isDebugEnabled) LOGGER.debug("Output Object 1 "+ obj); AmortOutputBean
		 * Out = (AmortOutputBean)obj; amt=Out.getRoundInterest();}
		 */
		arrlist.add(final_list.get(final_list.size() - 1));
		return arrlist;
	}

	/**
	 * If No.of AdvanceEMIs are present in input request and AddFirstInstall falg is
	 * Y then new row append to 0 th Installment.
	 * 
	 * @param beanObj
	 * @return Arraylist
	 */
	public ArrayList addFirstRowAdvEMI(AmortInputBean beanObj) {
		AmortOutputBean advout = new AmortOutputBean();
		ArrayList tempArray = new ArrayList();
		double advemi = 0.0d;
		double advloanAmt = beanObj.getReal_loanamount();
		AmortOutputBean advget = (AmortOutputBean) otArr.get(0);
		if (beanObj.getEMI_ro() == AmortConstant.RoundingAll)
			advemi = AmortUtil.round(overviewbean.getAdvanceEMI(), beanObj.getEMI_ro_part(), beanObj.getEMI_ro_to(),
					beanObj.getEMI_unit_ro());
		advout.setInstallment(Integer.toString(0));
		advout.setCycleDate(beanObj.getDateOfDisbursement());
		advout.setOpeningBalance(advloanAmt);
		advout.setRestOpeningBalance(advloanAmt);
		advout.setRoundOpen(advloanAmt);
		advout.setRoundRestOpenBal(advloanAmt);
		advout.setRoundEMI(advemi);
		advout.setRoundInterest(0.0);
		advout.setRoundPrincipal(advemi);
		advout.setEmiAmount(overviewbean.getAdvanceEMI());
		advout.setPrincipalEMI(overviewbean.getAdvanceEMI());
		advout.setRoundClose(advget.getRoundOpen());
		advout.setInterestEMI(0.0);
		advout.setClosingBalance(advget.getRoundOpen());
		overviewbean.setTotalRoundEMI(advemi + overviewbean.getTotalRoundEMI());
		overviewbean.setTotalRoundPrincipal(advemi + overviewbean.getTotalRoundPrincipal());
		tempArray.add(0, advout);
		for (int i = 0; i < otArr.size(); i++) {
			AmortOutputBean advout1 = (AmortOutputBean) otArr.get(i);
			tempArray.add(i + 1, advout1);
		}

		return tempArray;
	}

	/**
	 * This method will deduct TDS(Tax deducted at source) from final EMI. Amount
	 * being deducted as TDS will be available as TDSAmount
	 * 
	 * @param final_list
	 * @param beanObj
	 * @return
	 * @throws AmortException
	 */
	public ArrayList deductTDS(ArrayList final_list, AmortInputBean beanObj) throws AmortException {
		if (beanObj != null && final_list != null && final_list.size() > 0) {
			if (beanObj.isDeductTDS() && beanObj.getTDSPercentage() > 0) {
				ListIterator listIterator = final_list.listIterator();
				while (listIterator.hasNext()) {
					Object obj = listIterator.next();
					if (obj.getClass().getName().equals("com.rsystems.aayu.util.AmortOverviewBean")) {
						continue;
					}
					AmortOutputBean amortOut = (AmortOutputBean) obj;
					if (amortOut != null && amortOut.getRoundInterest() > 0) {
						Double TDS = (amortOut.getRoundInterest() * beanObj.getTDSPercentage()) / 100;
						// TDS = AmortUtil.round(TDS, beanObj.getEMI_ro_part(), beanObj.getEMI_ro_to(),
						// beanObj.getEMI_unit_ro());
						TDS = AmortUtil.round(TDS, beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(),
								beanObj.getOthers_unit_ro());
						double substractedInterest = amortOut.getRoundInterest() - TDS;
						substractedInterest = AmortUtil.round(substractedInterest, beanObj.getOthers_ro_part(),
								beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro());

						amortOut.setRoundInterest(substractedInterest);
						amortOut.setInterestEMI(amortOut.getInterestEMI() - TDS);
						amortOut.setEmiAmount(amortOut.getEmiAmount() - TDS);
						amortOut.setRoundEMI(amortOut.getRoundEMI() - TDS);
						amortOut.setTDS(TDS);
					}
				}
			}
		}
		return final_list;
	}

	public void calculate_multiple_rows(AmortInputBean beanObj) {

		double accrued_interest = 0.0d;
		int i = 0;
		try {
			String temp_date = "";
			String temp_last_date = "";
			Vector arr_start_date = new Vector();
			while (!AmortUtil.getlastDate((beanObj.getDateOfDisbursement()), 0, beanObj).equals(temp_last_date)) {
				if (i == 0)
					temp_date = beanObj.getDateOfCycle();
				else
					temp_date = AmortUtil.getCycleDate(beanObj.getDateOfDisbursement(), temp_date,
							beanObj.getDateformat(), beanObj.getFrequencyFactor(), beanObj.getFrequencyPeriod(), -1,
							beanObj.isEOMAmort());
				temp_last_date = AmortUtil.getlastDate((temp_date), 0, beanObj);
				arr_last_date.add(temp_last_date);
				arr_start_date.add(temp_date);
				i++;
			}
			arr_last_date.remove(0);
			arr_start_date.remove(0);
			int cnt = 0;
			double first_interest = 0.0d;
			if (beanObj.getRVvalue() == 0)
				first_interest = (((AmortOutputBean) otArr.get(0)).getRoundInterest());
			if (beanObj.getRVvalue() != 0)
				first_interest = beanObj.getInput_emi();
			for (int j = arr_last_date.size() - 1; j >= 0; j--) {
				AmortOutputBean out = new AmortOutputBean();
				accrued_interest = ((first_interest) / AmortUtil.simpleDayCount(beanObj.getInterest_Basis(),
						beanObj.getDateOfDisbursement(), beanObj.getDateOfFirstPayment(), beanObj.getDateformat()))
						* (AmortUtil.simpleDayCount(beanObj.getInterest_Basis(), (String) arr_start_date.get(j),
								(String) arr_last_date.get(j), beanObj.getDateformat()) + 1);
				if (j == arr_last_date.size() - 1)
					accrued_interest = ((first_interest) / AmortUtil.simpleDayCount(beanObj.getInterest_Basis(),
							beanObj.getDateOfDisbursement(), beanObj.getDateOfFirstPayment(), beanObj.getDateformat()))
							* (AmortUtil.simpleDayCount(beanObj.getInterest_Basis(), beanObj.getDateOfDisbursement(),
									(String) arr_last_date.get(j), beanObj.getDateformat()) + 1);
				if (beanObj.getOthers_ro() == AmortConstant.RoundingAll)
					accrued_interest = AmortUtil.round(accrued_interest, beanObj.getOthers_ro_part(),
							beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro());
				if ((beanObj.getEMI_ro_part() == AmortConstant.RoundingDeciaml)
						&& (beanObj.getEMI_ro() == AmortConstant.RoundingAll))
					accrued_interest = Double
							.parseDouble(AmortUtil.handlingNum(accrued_interest, beanObj.getEMI_unit_ro()));
				out.setCycleDate((String) arr_start_date.get(j));
				if (j == arr_last_date.size() - 1)
					out.setCycleDate(beanObj.getDateOfDisbursement());
				out.setInstallment(Integer.toString((-1) * j));// setMonthlyInterest(accrued_interest);
				out.setOpeningBalance(0.00);
				out.setRestOpeningBalance(0.00);
				out.setClosingBalance(0.00);
				out.setEmiAmount(0.00);
				out.setInterestEMI(0.00);
				out.setPrincipalEMI(0.00);
				out.setRoundEMI(0.00);
				out.setRoundOpen(0.00);
				out.setRoundInterest(0.00);
				out.setRoundPrincipal(0.00);
				out.setRoundClose(0.00);
				out.setRoundRestOpenBal(0.00);
				out.setAccruedInterest(accrued_interest);
				out.setMonthlyInterest(accrued_interest);
				out.setEndofMonthDate(AmortUtil.getlastDate(out.getCycleDate(), 0, beanObj));
				if (beanObj.getintAmort().equals("Y")) {
					out.setIntAccruedInterest(accrued_interest);
					out.setIntMonthlyInterest(accrued_interest);

				}
				otArr.add(cnt, out);

				cnt++;
			}

		} catch (Exception exception) {
			if (isDebugEnabled)
				LOGGER.error("Method : calculateAmort() : Exception thrown " + exception.toString());

		}
		return;
	}

	public ArrayList calculateAmort(AmortInputBean beanObj, double n_emi, int iteration) throws AmortException {

		try {
			return calculateAmort(beanObj, n_emi, iteration, false);
		} catch (Exception exception) {
			if (isDebugEnabled)
				LOGGER.error("Method : calculateAmort() : Exception thrown " + exception.toString());
			throw new AmortException.CalculationFactoryException(exception);
		}
	}

	public ArrayList calculateAmort(AmortInputBean beanObj, double n_emi, int iteration, boolean advoption)
			throws AmortException {

		if (isDebugEnabled)
			LOGGER.info("Inside calculateAmort:");
		double open_balance = 0.0d;
		double interest = 0.0d;
		double final_emi = 0.0d;
		double principal_emi = 0.0d;
		double close_balance = 0.0d;
		double rest_open_balance = 0.0d;
		double round_interest = 0.0d;
		double round_principal = 0.0d;
		double round_emi = 0.0d;
		double round_open = 0.0d;
		double round_rest_open = 0.0d;
		double round_close = 0.0d;
		double total_emi = 0.0d;
		double equi_int = 0.0d;
		double total_principal = 0.0d;
		double total_interest = 0.0d;
		double total_round_emi = 0.0d;
		double total_round_interest = 0.0d;
		double total_round_principal = 0.0d;
		double calRate = 0.0d;
		double accumulatedInt = 0.0;
		double threshValue = 0.0;
		double loan_amount = beanObj.getLoanAmount();
		if (beanObj.getOriginalRate() != 0) {
			beanObj.setInterestRate(beanObj.getOriginalRate());
		}
		double rate = beanObj.getInterestRate();
		double Total_Interest = beanObj.getTotalInterest();
		int interest_type = beanObj.getInterestType();
		int install_type = beanObj.getInstallmentType();
		int compoundFreq = beanObj.getCompoundFreq();
		int int_basis = beanObj.getInterest_Basis();
		int int_basis_emi = beanObj.getInterest_Basis_emi();
		int int_basis_emi_1 = beanObj.getInterest_Basis_emi2();
		int freqfactor = beanObj.getFrequencyFactor();
		int freqperiod = beanObj.getFrequencyPeriod();
		int frequency = beanObj.getRepaymentFrequency();
		int term = (int) beanObj.getTenor();
		int tenor78 = (int) beanObj.getTenor78();
		int n = 0;
		boolean result = false;
		String cycle_date = "";
		String date_dis = "";
		String prev_date = "";
		tot_installments = 0;
		tot_int = 0.0d;
		tot_round_int = 0.0d;
		otArr = new ArrayList();
		ArrayList arrfinal = null;
		boolean tenorNotPresent = false;
		boolean tenorPresent = true;
		double skipPartIntOpen = 0;
		double skipPartIntClose = 0;
		double skipPartInt = 0;
		double skipPartIntonInt = 0;
		double skipPartIntOpentmp = 0;
		double skipIntBeforePartial = 0;
		String bpicap_yn = "";
		double roundemi = 0.0d;

		try {
			emi = n_emi;
			if (emi < 0) {
				err.put("ERR_NEGATIVE_AMORT", AmortConstant.AmortValidation.ERR_NEGATIVE_AMORT);
			}
			if (beanObj.isInterestRatepresent())
				rate = interest_rate;
			if (isDebugEnabled)
				LOGGER.info("Inside calculateAmort:Total calculated Term =" + term);
			if (isDebugEnabled)
				LOGGER.info("Inside calculateAmort:EMI=" + emi);
			// check condition Adjterm is zero or not.If it is not zero then assign that
			// value to one variable.Amort will start from that value only.
			if (beanObj.getAdjterm() != 0) {
				n = beanObj.getAdjterm();
				term = term + beanObj.getAdjterm();
				if (isDebugEnabled)
					LOGGER.info("Inside calculateAmort:No.of Installments to be Freeze =" + beanObj.getAdjterm());
			}

			String toChk_date = AmortUtil.getCycleDate(beanObj.getDateOfCycle(), beanObj.getDateOfCycle(),
					beanObj.getDateformat(), beanObj.getRestFactor(), AmortConstant.setMonth, 1, beanObj.isEOMAmort());

			if (beanObj.isTenorpresent()) {
				tenorNotPresent = true;
				tenorPresent = false;
			}
			if (iteration != 3) {
				int_basis = int_basis_emi;
				beanObj.setInterest_Basis(int_basis_emi);
			} else {
				int_basis = int_basis_emi_1;
				beanObj.setInterest_Basis(int_basis_emi_1);
			}
			// Start generating Amortschedule(loop starts) Using term.
			// for(int i = n;i < term;i++){
			int originalInterestBasis = beanObj.getInterest_Basis();
			int i = n;
			AmortConstant.loop_count = iteration + 1;
			if (compoundFreq > 0) {
				AmortConstant.RepaymentFrequencyValue = compoundFreq;
			}
			do {

				int flg = 0;
				boolean skipIntDonotRound = false;
				AmortOutputBean out = new AmortOutputBean();
				// check the Adjterm zero or not.If not zero then add the objects to arraylist n
				// times.
				if (i == n && beanObj.getAdjterm() != 0) {
					for (int j = 0; j < i; j++) {
						otArr.add(out);
					}
				}
				// check contion for first Installment.
				if (i == n) {
					open_balance = loan_amount;
					rest_open_balance = open_balance;
					round_open = loan_amount;
					round_rest_open = round_open;
					cycle_date = beanObj.getDateOfCycle();
					prev_date = beanObj.getDateOfDisbursement();
					if (n != 0 || bpi > 0)
						prev_date = AmortUtil.getCycleDate(beanObj.getDateOfCycle(), cycle_date,
								beanObj.getDateformat(), freqfactor, freqperiod, -1, beanObj.isEOMAmort());
				} else {
					open_balance = close_balance;
					rest_open_balance = open_balance;
					round_open = round_close;
					round_rest_open = round_open;
					date_dis = beanObj.getCurrentCycleDate();
					cycle_date = AmortUtil.getCycleDate(beanObj.getDateOfCycle(), date_dis, beanObj.getDateformat(),
							freqfactor, freqperiod, 1, beanObj.isEOMAmort());
					// prev_date =
					// AmortUtil.getCycleDate(beanObj.getDateOfCycle(),cycle_date,beanObj.getDateformat(),freqfactor,freqperiod,-1,beanObj.isEOMAmort());
					prev_date = beanObj.getCurrentCycleDate();
				}
				// Set the cycle date in bean object.
				beanObj.setCurrentCycleDate(cycle_date);

				beanObj.setInterest_Basis(originalInterestBasis);

				if (bpi < 0 && (beanObj.getInterest_Basis() == AmortConstant.IntBasis_30360
						&& beanObj.getInterest_Basis_emi() == AmortConstant.IntBasis_30360
						&& beanObj.getRepaymentFrequency() != 1 && beanObj.getRepaymentFrequency() != 2
						&& beanObj.getRepaymentFrequency() != 4 && beanObj.getRepaymentFrequency() != 6
						&& beanObj.getRepaymentFrequency() != 3) && beanObj.isEOMAmort() == false && i == n) {
					if (i == 0)
						beanObj.setInterest_Basis(3);
				}
				if (beanObj.getRepaymentFrequency() == 360
						&& (beanObj.getInterest_Basis() == AmortConstant.IntBasis_30360
								&& beanObj.getInterest_Basis_emi() == AmortConstant.IntBasis_30360)) {
					beanObj.setInterest_Basis(3);
				}

				// added for bullet :daily(Interest+Principle)
				if (beanObj.getRepaymentFrequency() == 360
						&& (beanObj.getInterest_Basis() == AmortConstant.IntBasis_Actuals
								&& beanObj.getInterest_Basis_emi() == AmortConstant.IntBasis_Actuals)) {
					beanObj.setInterest_Basis(1);
				}

				// added for bullet :daily(Interest+Principle)
				if (beanObj.getRepaymentFrequency() == 360
						&& (beanObj.getInterest_Basis() == AmortConstant.IntBasis_Actuals
								&& beanObj.getInterest_Basis_emi() == AmortConstant.IntBasis_30360)) {
					beanObj.setInterest_Basis(3);
				}

				// added for bullet :daily(Interest+Principle)
				if (beanObj.getRepaymentFrequency() == 360
						&& (beanObj.getInterest_Basis() == AmortConstant.IntBasis_30360
								&& beanObj.getInterest_Basis_emi() == AmortConstant.IntBasis_Actuals)) {
					beanObj.setInterest_Basis(3);
				}

				int_basis = beanObj.getInterest_Basis();

				/*
				 * Adjust Rate If Adjust Rate is defined for any installment then EMI,Tenor is
				 * recalculated by new Adjust Rate depends on Adjust option. Interest is
				 * calculated rest of Installments using new Interest Rate.
				 */

				if (null != beanObj.getArrAdjustRate()) {
					ArrayList arrAdj_Rate = (ArrayList) beanObj.getArrAdjustRate();
					for (int k = 0; k < arrAdj_Rate.size(); k++) {
						AdjustRateInputBean Adj_Bean = (AdjustRateInputBean) arrAdj_Rate.get(k);
						if (Adj_Bean.getAfter_month() == i
								&& (beanObj.getAdjust_Option() == AmortConstant.AdjustedEMI || iteration == 3)) {
							beanObj.setAdjRate(rate);
							rate = Adj_Bean.getAdj_rate() / 100;
							calRate = (AmortUtil.getRate(rate, compoundFreq, frequency));
							// If the Adjust Option is AdjustEMI then EMI is recalculated using new Rate.

							// uncommented for Bug 231088 - Capri aayu xml <adjust_rate> tag purpose
							if (beanObj.getAdjust_Option() == AmortConstant.AdjustEMI && (rate != interest_rate))
								emi = AmortUtil.PMT(round_open, (term - i), calRate, beanObj.getInterestType(),
										beanObj.getRepaymentFrequency(), beanObj.getSameAdvEMI(), 0,
										beanObj.getBP_Lastpayamt());

							if (beanObj.getOriginalRate() == 0) {
								beanObj.setOriginalRate(beanObj.getInterestRate());
							}
							beanObj.setInterestRate(Adj_Bean.getAdj_rate());
							if (isDebugEnabled)
								LOGGER.debug("Inside calculateAmort:AdjustRate is=" + Adj_Bean.getAdj_rate()
										+ "and emi is=" + emi + "for the Installment" + i);
							// If the Adjust Option is AdjustTenor then Tenor is recalculated using new
							// Rate.
							if (beanObj.getAdjust_Option() == AmortConstant.AdjustTenor
									|| beanObj.getAdjust_Option() == AmortConstant.AdjustedEMI) {
								term = AmortUtil.claculateNPER(round_open, beanObj.getInput_emi(), calRate,
										beanObj.getBP_Lastpayamt());
								term = term + i;
								if (isDebugEnabled)
									LOGGER.debug(
											"Inside calculateAmort:In AdjustRate If the Adjust option is AdjustTenor then tenor is recalculated with changed Rate.Tenor="
													+ term);
							}
						}
					}
				}

				// If Interest Type is flat then for total tenor Interest will be calculated
				// based on Loan Amount only.
				if (interest_type == AmortConstant.InterestTypeFlat) {
					if (isDebugEnabled)
						LOGGER.info("Inside calculateAmort:Interest Tye is Flat");
					interest = AmortUtil.getInterest(loan_amount, rate, compoundFreq, int_basis, prev_date, cycle_date,
							beanObj.getDateformat(), false, beanObj.getIntCalMethod());
					if (bpi != 0 && beanObj.getReduceBpi().equals(AmortConstant.Component.reduceBpiY)
							&& beanObj.getPayFirst().equals(AmortConstant.Component.payFirstN) && i == 0) {
						interest = loan_amount * AmortUtil.getDayfactor(beanObj.getBPIMethod(), prev_date, cycle_date,
								beanObj.getDateformat(), false) * rate;
					}
					if (i == 0 && beanObj.getAddIntAmount() > 0)
						interest = interest + beanObj.getAddIntAmount();
					if (interest > n_emi)
						err.put("ERR_ADD_INT", AmortConstant.AmortValidation.ERR_ADD_INT);
					round_interest = interest;
					if (isDebugEnabled)
						LOGGER.info("Inside calculateAmort:Calcularted Interest= " + round_interest);
				} else { // If Interest Type is variable then for total tenor Interest will be calculated
							// based on opening balance only.check frequency if it is weekly or fortnightly
							// then calulate Interest.
					if (frequency != AmortConstant.AmortTypeConstant.RF_W
							&& frequency != AmortConstant.AmortTypeConstant.RF_FN) {
						if (i == n && iteration == 3) {
							// Bug 252917 - Incorrect Interest is getting calculated for IB-30/360
							SimpleDateFormat df = new SimpleDateFormat(beanObj.getDateformat());
							AmortConstant.calFirst_int = 3;
							AmortConstant.calFirst_intDOD = beanObj.getDateOfDisbursement();

							AmortConstant.calFirst_intDOC = beanObj.getDateOfCycle();

							Date d_date1 = df.parse(AmortConstant.calFirst_intDOD);
							Date d_date2 = df.parse(AmortConstant.calFirst_intDOC);
							Calendar c_date1 = Calendar.getInstance();
							c_date1.setTime(d_date1);

							Calendar c_date2 = Calendar.getInstance();
							c_date2.setTime(d_date2);
							AmortConstant.diffInTime = d_date2.getTime() - d_date1.getTime();
							AmortConstant.diffIndays = (int) (AmortConstant.diffInTime / (24 * 60 * 60 * 1000));
							AmortConstant.monthDOD = c_date1.get(Calendar.MONTH);
							AmortConstant.monthDOC = c_date2.get(Calendar.MONTH);

						} else {
							AmortConstant.monthDOD = -1;
							AmortConstant.monthDOC = -1;

						}

						interest = AmortUtil.getInterest(rest_open_balance, rate, compoundFreq, int_basis, prev_date,
								cycle_date, beanObj.getDateformat(), false, beanObj.getIntCalMethod());
						// round_interest =
						// AmortUtil.getInterest(round_rest_open,rate,compoundFreq,int_basis,prev_date,cycle_date,beanObj.getDateformat(),false);
						round_interest = AmortUtil.round(
								AmortUtil.getInterest(round_rest_open, rate, compoundFreq, int_basis, prev_date,
										cycle_date, beanObj.getDateformat(), false, beanObj.getIntCalMethod()),
								beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro());
						// If bpi is negative then calculate interest for first installment using
						// opening balance and dayfactor.
						if (bpi > 0 && beanObj.getReduceBpi().equals(AmortConstant.Component.reduceBpiY)
								&& beanObj.getPayFirst().equals(AmortConstant.Component.payFirstN)
								&& beanObj.getBPI_Recovery() != AmortConstant.AmortTypeConstant.BPIRecovery_UF && i == 0
								&& iteration == 3) {
							interest = rest_open_balance * AmortUtil.getDayfactor(beanObj.getBPIMethod(),
									beanObj.getDateOfDisbursement(), cycle_date, beanObj.getDateformat(), false) * rate;
						}
						if (bpi > 0 && beanObj.getReduceBpi().equals(AmortConstant.Component.reduceBpiY)
								&& beanObj.getPayFirst().equals(AmortConstant.Component.payFirstN)
								&& beanObj.getBPI_Recovery() != AmortConstant.AmortTypeConstant.BPIRecovery_UF && i == 0
								&& iteration == 3) {
							round_interest = round_rest_open * AmortUtil.getDayfactor(beanObj.getBPIMethod(),
									beanObj.getDateOfDisbursement(), cycle_date, beanObj.getDateformat(), false) * rate;
						}
						if (bpi < 0 && i == 0) {
							interest = AmortUtil.getInterest(rest_open_balance, rate, compoundFreq, int_basis,
									beanObj.getDateOfDisbursement(), cycle_date, beanObj.getDateformat(), false,
									beanObj.getIntCalMethod());
							round_interest = AmortUtil.round(
									AmortUtil.getInterest(round_rest_open, rate, compoundFreq, int_basis,
											beanObj.getDateOfDisbursement(), cycle_date, beanObj.getDateformat(), false,
											beanObj.getIntCalMethod()),
									beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(),
									beanObj.getOthers_unit_ro());
						}
						if (i == 0 && beanObj.getAddIntAmount() > 0) {
							interest = interest + beanObj.getAddIntAmount();
							round_interest = round_interest + beanObj.getAddIntAmount();
							if (interest > n_emi)
								err.put("ERR_ADD_INT", AmortConstant.AmortValidation.ERR_ADD_INT);
						}
						if (isDebugEnabled)
							LOGGER.info(
									"Inside calculateAmort:Calculated Interest if Repayment Frequency is not weekly and fortnightly for Installment ="
											+ i + " is " + interest);
					} else {

						result = AmortUtil.datesInBetween(prev_date, cycle_date, toChk_date, beanObj.getDateformat());
						if (result) {
							interest = AmortUtil.cal_WeeklyInterest(prev_date, cycle_date, toChk_date,
									rest_open_balance, open_balance, rate, beanObj);
							round_interest = AmortUtil.cal_WeeklyInterest(prev_date, cycle_date, toChk_date,
									round_rest_open, round_open, rate, beanObj);
						} else {
							interest = AmortUtil.cal_WeeklyInterest(prev_date, cycle_date, "", rest_open_balance,
									open_balance, rate, beanObj);
							round_interest = AmortUtil.cal_WeeklyInterest(prev_date, cycle_date, "", round_rest_open,
									round_open, rate, beanObj);
						}
						if (i == 0 && beanObj.getAddIntAmount() > 0) {
							interest = interest + beanObj.getAddIntAmount();
							round_interest = round_interest + beanObj.getAddIntAmount();
							if (interest > n_emi)
								err.put("ERR_ADD_INT", AmortConstant.AmortValidation.ERR_ADD_INT);
						}
						toChk_date = AmortUtil.getCycleDate(toChk_date, toChk_date, beanObj.getDateformat(),
								beanObj.getRestFactor(), AmortConstant.setMonth, 1, beanObj.isEOMAmort());

						if (isDebugEnabled)
							LOGGER.info(
									"Inside calculateAmort:Calculated Interest if Repayment Frequency either weekly or fortnightly for Installment ="
											+ i + " is " + interest);
					}
				}

				/*
				 * Installment Type is EquatedPrincipal. For equated principal EMI is calculated
				 * for each installment principal is same for all installments
				 */
				if (install_type == AmortConstant.Installment_EquatedPrincipal) {
					if (isDebugEnabled)
						LOGGER.info("Inside calculateAmort:Installment Type is EqatedPrincipal");
					principal_emi = beanObj.getOpenAmount() / beanObj.getTotalInstallment();
					final_emi = interest + principal_emi;
					round_principal = (BigDecimal.valueOf(beanObj.getOpenAmount())
							.divide(BigDecimal.valueOf(beanObj.getTotalInstallment()), new MathContext(20)))
							.doubleValue();
					round_emi = round_interest + round_principal;
					if (isDebugEnabled)
						LOGGER.info("Inside calculateAmort:Calculated Principle=" + principal_emi + "and emi= "
								+ final_emi);
				}
				/*
				 * Installment Type is InterestOnly. EMI is set to interest and principal to 0
				 * For last installment the remaining opening balance is recovered from
				 * principal
				 */
				else if (install_type == AmortConstant.Installment_InterestOnly || install_type == 3) {
					if (isDebugEnabled)
						LOGGER.info("Inside calculateAmort:Installment Type is Interestonly");
					int equPrincfrq = (int) beanObj.getEqPrinFreq();
					int repfrequency = beanObj.getInterestFrequency();
					if (equPrincfrq > repfrequency) {
						equi_int += interest;
						principal_emi = principal_emi = loan_amount / beanObj.getTotaleqtInstment();
						round_principal = AmortUtil.round(principal_emi, beanObj.getOthers_ro_part(),
								beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro());
						interest = 0;
						round_interest = 0;
						final_emi = principal_emi;
						round_emi = round_principal;
						if (beanObj.getEqPrinFreq() > 0 && isFrqEqPrinciple(beanObj, i)) {
							interest = equi_int;
							round_interest = AmortUtil.round(interest, beanObj.getOthers_ro_part(),
									beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro());
							final_emi = principal_emi + interest;
							round_emi = AmortUtil.addDouble(round_interest, round_principal);
							equi_int = 0;
						}

					} else {
						final_emi = interest;
						round_emi = round_interest;
						principal_emi = (i < term) ? 0.0d : open_balance;
						round_principal = (i < term) ? 0.0d : round_open;
						if (isDebugEnabled)
							LOGGER.info("Inside calculateAmort:Calculated Principle=" + principal_emi + "and emi= "
									+ final_emi);
						if (beanObj.getEqPrinFreq() > 0 && isFrqEqPrinciple(beanObj, i)) {

							principal_emi = loan_amount / beanObj.getTotaleqtInstment();
							final_emi = interest + principal_emi;
							round_principal = principal_emi;
							round_emi = AmortUtil.addDouble(round_interest, round_principal);
							if (isDebugEnabled)
								LOGGER.info("Inside calculateAmort:Calculated Principle=" + principal_emi + "and emi= "
										+ final_emi);
						}

					}

				}

				else {
					// If the Installment type is EqatedInstallments then throught tenor emi will be
					// same.
					if (isDebugEnabled)
						LOGGER.info("Inside calculateAmort:Installment Type is EqatedInstallments");
					final_emi = emi;
					round_emi = emi;
					principal_emi = (BigDecimal.valueOf(final_emi).subtract(BigDecimal.valueOf(interest)))
							.doubleValue();
					round_principal = (BigDecimal.valueOf(round_emi).subtract(BigDecimal.valueOf(round_interest)))
							.doubleValue();
					if (round_principal < 0 && iteration == 3) {
						err.put("ERR_NEGATIVE_AMORT", AmortConstant.AmortValidation.ERR_NEGATIVE_AMORT);

					}
					if (round_open < 0 && round_interest < 0 && round_close < 0 && iteration == 3) {
						err.put("ERR_NEGATIVE_AMORT", AmortConstant.AmortValidation.ERR_NEGATIVE_AMORT);

					}
					if (isDebugEnabled)
						LOGGER.info("Inside calculateAmort:Calculated Principle=" + principal_emi + "and emi= "
								+ final_emi);
					/* Negative Amort Check */
					// if(interest >= final_emi && beanObj.getAdjust_Option() != 0 &&
					// beanObj.isCapitalize() == false){
					if ((interest >= final_emi || interest < 0 || round_principal < 0 || round_open < 0)
							&& iteration == 3 && beanObj.getAdjust_Option() != 0 && beanObj.isCapitalize() == false) {
						err.put("ERR_NEGATIVE_AMORT", AmortConstant.AmortValidation.ERR_NEGATIVE_AMORT);
						if (isDebugEnabled)
							LOGGER.error("Inside calculateAmort:Error occurred at Installment No.=" + i
									+ " because Interest=" + interest + " greater than EMI =" + final_emi);
						// break;
					}
				}
				/*
				 * Do not remove this loop count...Added for yeary issue: Bug 268586 - EOM step
				 * variation || Yearly case || Dynamic issue
				 */
				AmortConstant.loop_count = 5;

				// Get Max Emi
				if ((round_emi > 0) && iteration == 3) {
					roundemi = round_emi;
				}

				// Get fixed interest rate based Emi
				if ((round_emi > 0) && iteration == 3 && beanObj.getFixedRateofInterest() == rate
						&& round_emi >= fixedInstEmi) {
					fixedInstEmi = round_emi;
				}
				/*
				 * Step EMI If Step Up/Down is defined for any installment then EMI of that
				 * installment is recalculated according to Step Up/Down input Principal is also
				 * recalculated as EMI is changed Only if step adjust option is adjusted EMI
				 * then EMI is not changed.variation applied for calculated EMI only. Once
				 * variation Tenor completed except AdjustedEMI case rest of the options in case
				 * of AdjustEMI EMI will be recalculated in case of AdjustTenor then Tenor will
				 * be recalculted. Once EMI or Tenor are recalculated then internally call goes
				 * to generateAmort API for generating rest of AMort using High-Low method.
				 */

				// check Step is present in I/P request.
				if (null != beanObj.getArrStepEMI()) {
					ArrayList arr = (ArrayList) beanObj.getArrStepEMI();
					for (int k = 0; k < arr.size(); k++)

					{
						StepEMIInputBean sm = (StepEMIInputBean) arr.get(k);
						// check condition for apllying variation for installment.This will applying
						// only in last Iteration.Internally this will call calVariationAmort API for
						// applying variation.
						if (i + 1 >= sm.getFrm_month() && i + 1 <= sm.getTo_month()
								&& (beanObj.getAdjust_Option() == AmortConstant.AdjustedEMI
										|| (beanObj.getAdjust_Option() == AmortConstant.AdjustEMI && iteration == 3)
										|| beanObj.getAdjust_Option() == AmortConstant.AdjustTenor && iteration == 3)) {
							// call the below API for Applying variation and get the values after applying
							// variation to that Instalment.
							ArrayList arrayinitial = calVariationAmort(beanObj, term, i, open_balance, 0);
							AmortOutputBean Out = (AmortOutputBean) arrayinitial.get(0);
							final_emi = Out.getEmiAmount();
							round_emi = Out.getEmiAmount();
							if (sm.getStepbasis() == 3) {
								round_emi = round_interest + sm.getStepby();
								round_principal = sm.getStepby();
								flg = 1;
							}
							if ((sm.getStepbasis() == 2 && i == 0
									&& beanObj.getReduceBpi().equals(AmortConstant.Component.reduceBpiY)
									&& beanObj.getPayFirst().equals(AmortConstant.Component.payFirstY)
									&& beanObj.isAddInt() == false)) {
								interest = beanObj.getLoanAmount()
										* AmortUtil.getDayfactor(beanObj.getInterest_Basis(), prev_date,
												beanObj.getCurrentCycleDate(), beanObj.getDateformat(), false)
										* beanObj.getInterestRate();
								round_interest = AmortUtil.round(interest, beanObj.getOthers_ro_part(),
										beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro());
							}
							principal_emi = (BigDecimal.valueOf(final_emi).subtract(BigDecimal.valueOf(interest)))
									.doubleValue();
							round_principal = (BigDecimal.valueOf(round_emi)
									.subtract(BigDecimal.valueOf(round_interest))).doubleValue();
							if (round_interest > round_emi && iteration == 3) {
								err.put("ERR_NEGATIVE_AMORT", AmortConstant.AmortValidation.ERR_NEGATIVE_AMORT);
								if (isDebugEnabled)
									LOGGER.error("Inside calculateAmort:Error occurred at Installment No.=" + i
											+ " because Interest=" + interest + " greater than EMI =" + final_emi);
								// break;
							}
							if (round_principal < 0) {
								err.put("ERR_NEGATIVE_AMORT", AmortConstant.AmortValidation.ERR_NEGATIVE_AMORT);

							}
							beanObj.setStepAdjust(true);
							if (isDebugEnabled)
								LOGGER.debug("Inside calculateAmort:After applying Step variation EMI  =" + round_emi
										+ " and Principle Amount= " + round_principal + "for the Installment=" + i);

						}
						// check below condition weather installment no. is equal to after completion of
						// Step Tenor only in case of AdjustEMI or AdjustTenor.
						if (i == sm.getTo_month()
								&& (beanObj.getAdjust_Option() == AmortConstant.AdjustEMI
										|| beanObj.getAdjust_Option() == AmortConstant.AdjustTenor)
								&& iteration == 3 && beanObj.isStepAdjust()) {
							beanObj.setLoanAmount(round_open);
							beanObj.setDateOfCycle(cycle_date);
							arrfinal = calAmortAfterVariation(arrfinal, round_open, term, rate, beanObj, i);
						}

					}
				}

				/*
				 * Balloon Payment If balloon payment is defined for any installment then EMI is
				 * recalculated by adding BP amount to EMI Principal is also recalculated as EMI
				 * is changed Only if BP adjust option is adjusted EMI then EMI is not
				 * changed.variation applied for calculated EMI only. Once variation Tenor
				 * completed except AdjustedEMI case rest of the options in case of AdjustEMI
				 * EMI will be recalculated in case of AdjustTenor then Tenor will be
				 * recalculted. Once EMI or Tenor are recalculated then internally call goes to
				 * generateAmort API for generating rest of Amort using High-Low method.
				 */
				// check BP is present in I/P request.
				if (null != beanObj.getArrBP()) {
					ArrayList arrBP = beanObj.getArrBP();
					for (int j = 0; j < arrBP.size(); j++) {
						BPInputBean bp = (BPInputBean) arrBP.get(j);
						// check condition for apllying variation for installment.This will applying
						// only in last Iteration.Internally this will call calVariationAmort API for
						// applying variation.
						if (beanObj.getAdjust_Option() == AmortConstant.AdjustedEMI
								|| (beanObj.getAdjust_Option() == AmortConstant.AdjustEMI && iteration == 3)
								|| (beanObj.getAdjust_Option() == AmortConstant.AdjustTenor && iteration == 3)) {
							// check condition Installment having is BP month or not.
							if (i + 1 == bp.getBP_Month()) {
								beanObj.setBPAdjust(true);
								// call the below API for Applying variation and get the values after applying
								// variation to that Instalment.
								ArrayList arrayinitial = calVariationAmort(beanObj, term, i, open_balance, 0);
								AmortOutputBean Out = (AmortOutputBean) arrayinitial.get(0);
								final_emi = Out.getEmiAmount();
								round_emi = Out.getEmiAmount();
								principal_emi = (BigDecimal.valueOf(final_emi).subtract(BigDecimal.valueOf(interest)))
										.doubleValue();
								round_principal = (BigDecimal.valueOf(round_emi)
										.subtract(BigDecimal.valueOf(round_interest))).doubleValue();
								if (isDebugEnabled)
									LOGGER.debug("Inside calculateAmort:After applying Balloon variation EMI  ="
											+ round_emi + " and Principle Amount= " + round_principal);
								if (round_principal < 0) {
									err.put("ERR_NEGATIVE_AMORT", AmortConstant.AmortValidation.ERR_NEGATIVE_AMORT);

								}
							}
							// check below condition weather installment no. is equal to after completion of
							// BP Tenor only in case of AdjustEMI or AdjustTenor.
							if (i == bp.getBP_Month()
									&& (beanObj.getAdjust_Option() == AmortConstant.AdjustEMI
											|| beanObj.getAdjust_Option() == AmortConstant.AdjustTenor)
									&& iteration == 3 && beanObj.isBPAdjust()) {
								beanObj.setLoanAmount(round_open);
								beanObj.setDateOfCycle(cycle_date);
								arrfinal = calAmortAfterVariation(arrfinal, open_balance, term, rate, beanObj, i);

							}

						}
					}
				}

				/*
				 * Last Balloon payment starts. If Tenor is present in the Request this will
				 * automatically Treats as AdjustedEMI option amount adjusted throughout tenor.
				 * If EMI is present in the Request this will automatically treats as
				 * AdjustTenor.In this case tenor is adjusted.
				 */
				if (beanObj.getBP_Lastpayamt() > 0) {

					if (i + 1 == (term) && (beanObj.getAdjust_Option() == AmortConstant.AdjustEMI
							|| beanObj.getAdjust_Option() == AmortConstant.AdjustedEMI)) {
						final_emi = AmortUtil.addDouble(final_emi, beanObj.getBP_Lastpayamt());
						round_emi = AmortUtil.addDouble(round_emi, beanObj.getBP_Lastpayamt());
						principal_emi = (BigDecimal.valueOf(final_emi).subtract(BigDecimal.valueOf(interest)))
								.doubleValue();
						round_principal = (BigDecimal.valueOf(round_emi).subtract(BigDecimal.valueOf(round_interest)))
								.doubleValue();
						if (round_principal < 0) {
							err.put("ERR_NEGATIVE_AMORT", AmortConstant.AmortValidation.ERR_NEGATIVE_AMORT);

						}
						if (isDebugEnabled)
							LOGGER.info("Inside calculateAmort:In case of LastBalloon Payment.EMI=" + round_emi);
					}
					if (i + 1 == (term) && (beanObj.getAdjust_Option() == AmortConstant.AdjustTenor) && iteration == 3
							&& (round_open - beanObj.getBP_Lastpayamt()) > (round_emi * 2)) {
						beanObj.setLoanAmount(round_open);
						beanObj.setDateOfCycle(cycle_date);
						arrfinal = calAmortAfterVariation(arrfinal, open_balance, term, rate, beanObj, i);
					}
				}

				/*
				 * Interest Only Month starts Sets EMI to interest for all the installments
				 * defined to be interest only Sets principal amount to zero and recalculates
				 * closing balance.
				 */
				if (beanObj.getInt_Only() > 0) {
					if (isDebugEnabled)
						LOGGER.info("Inside calculateAmort:Interest only option starts.");
					// Check contion for Interest only option Installment No.
					if (i + 1 <= beanObj.getInt_Only()) {
						final_emi = interest;
						principal_emi = 0.0d;
						close_balance = open_balance - principal_emi;
						round_emi = round_interest;
						round_principal = 0.0d;
						round_close = round_open - round_principal;
						if (isDebugEnabled)
							LOGGER.info("Inside calculateAmort:Interest only option starts.Interest=" + interest
									+ "Interest is assign to EMI.EMI=" + final_emi);
					} else { // check contion for Installment Type is Equated principle.
						if (install_type == AmortConstant.Installment_EquatedPrincipal) {
							if (isDebugEnabled)
								LOGGER.info(
										"Inside calculateAmort:Interest only option starts.Installment Type is Eqated principle");
							principal_emi = loan_amount / (term - beanObj.getInt_Only());
							final_emi = interest + principal_emi;
							round_principal = principal_emi;
							if (round_principal < 0) {
								err.put("ERR_NEGATIVE_AMORT", AmortConstant.AmortValidation.ERR_NEGATIVE_AMORT);

							}
							round_emi = AmortUtil.addDouble(round_interest, round_principal);
							if (isDebugEnabled)
								LOGGER.info(
										"Inside calculateAmort:Interest only option starts.Installment Type is Eqated principle.Calculated Principle Amount="
												+ principal_emi);
						}
					}
				}
				// Interest Only Month ends

				// Calculate the closing balance for every Installment.
				close_balance = (BigDecimal.valueOf(open_balance).subtract(BigDecimal.valueOf(principal_emi)))
						.doubleValue();
				round_close = (BigDecimal.valueOf(round_open).subtract(BigDecimal.valueOf(round_principal)))
						.doubleValue();

				/*
				 * Skip EMI Skips EMI by setting principal and EMI to 0 If capitalize interest
				 * is enabled then interest is added to closing balance. Once variation Tenor
				 * completed except AdjustedEMI case rest of the options in case of AdjustEMI
				 * EMI will be recalculated in case of AdjustTenor then Tenor will be
				 * recalculted. Once EMI or Tenor is recalculated then internally call goes to
				 * generateAmort API for generating rest of AMort using High-Low method.
				 */
				// check Skip is present in I/P request.
				if (null != beanObj.getArrSkipEMI()) {

					ArrayList arrskip = (ArrayList) beanObj.getArrSkipEMI();
					for (int k = 0; k < arrskip.size(); k++) {
						SkipEMIInputBean sm = (SkipEMIInputBean) arrskip.get(k);
						// check condition for apllying variation for installment.This will applying
						// only in last Iteration.Internally this will call calVariationAmort API for
						// applying variation.
						if (beanObj.getAdjust_Option() == AmortConstant.SkipEMI.AdjustedEMI
								|| (beanObj.getAdjust_Option() == AmortConstant.SkipEMI.AdjustEMI && iteration == 3)
								|| (beanObj.getAdjust_Option() == AmortConstant.AdjustTenor && iteration == 3)) {

							// check condition Installment having is Skip month or not.
							if (i + 1 >= sm.getFrm_month() && i + 1 <= (sm.getFrm_month() + sm.getNo_month() - 1)) {
								// call the below API for Applying variation and get the values after applying
								// variation to that Instalment.
								if (iteration == 3) {
									open_balance = round_open;
								} else {
									round_open = open_balance;
								}
								ArrayList arrayinitial = calVariationAmort(beanObj, term, i, round_open,
										skipPartIntOpentmp);
								AmortOutputBean Out = (AmortOutputBean) arrayinitial.get(0);
								final_emi = Out.getEmiAmount();
								round_emi = Out.getEmiAmount();
								principal_emi = Out.getPrincipalEMI();
								close_balance = Out.getClosingBalance();
								// Zanazco Bug 249304
								// -BPI_Treatment-Adjust_With_Interest\Index_Rate_Changed\Next interest amount
								// is incorrect & Tenor increased with change in EMI for skip capitalize
								if (sm.getFrm_month() == 1 && beanObj.getAddIntAmount() > 0
										&& sm.getSkip_capital().equals(AmortConstant.SkipEMI.SkipCapitalValueY)) {
									interest = round_interest;
								} else {
									interest = Out.getInterestEMI();
								}

								// Zanazco Bug 249304 End

								round_principal = 0.0;
								if (!sm.getSkip_capital().equals(AmortConstant.SkipEMI.SkipCapitalValueY))
									round_interest = Out.getRoundInterest();
								if (!sm.getSkipPartPay().equals(AmortConstant.SkipEMI.SkipPartialInterest)) {

									// If Skip Capital is Accumulated then take sum of interest during skip period
									// and added to Interest portion after completion of Skip period.
									if (sm.getSkip_capital().equals(AmortConstant.SkipEMI.SkipAccum)) {
										accumulatedInt = accumulatedInt + beanObj.getAccumulInt();
									}
									// If Skip Capital is Cumulative Interst then calculate interrest on Interst
									// during Skip Period and added to Interst portion after completion of Skip
									// period.
									if (sm.getSkip_capital().equals(AmortConstant.SkipEMI.SkipCumulative)) {
										double cuminterest = AmortUtil.getInterest(beanObj.getAccumulInt(), rate,
												compoundFreq, int_basis, prev_date, cycle_date, beanObj.getDateformat(),
												false, beanObj.getIntCalMethod());
										accumulatedInt = accumulatedInt + cuminterest + beanObj.getAccumulInt();
									}
									/*
									 * if( sm.getSkip_capital().equals(AmortConstant.SkipEMI.SkipCapitalValueY) &&
									 * sm.getFrm_month() == 1 && bpi > 0){ // added new condition round_close =
									 * round_open + interest + bpi; //round_interest = Out.getRoundInterest();
									 * round_interest = interest; }else
									 */if (sm.getSkip_capital().equals(AmortConstant.SkipEMI.SkipCapitalValueY)) {
										round_close = round_open + interest;
										if (beanObj.getAddIntAmount() > 0)
											round_interest = interest;
										else
											round_interest = Out.getRoundInterest();
									} else {
										round_close = round_open + round_interest;

									}

								} else {
									skipIntDonotRound = true;
									round_close = Out.getRoundClose();

									// Bug 255265 - LMS / Full Prepayment / Partial Interest Skip - Cumulative /
									// Issue After Full prepayment
									// rounding applying to all interest

									skipPartIntOpen = Out.getSkipPartialIntOpen();
									skipPartIntOpen = AmortUtil.round(skipPartIntOpen, beanObj.getOthers_ro_part(),
											beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro());

									skipPartIntClose = Out.getSkipPartialIntClose();
									skipPartIntClose = AmortUtil.round(skipPartIntClose, beanObj.getOthers_ro_part(),
											beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro());

									skipPartInt = Out.getSkipPartialInt();
									skipPartInt = AmortUtil.round(skipPartInt, beanObj.getOthers_ro_part(),
											beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro());

									skipPartIntonInt = Out.getSkipPartialIntonInt();
									skipPartIntonInt = AmortUtil.round(skipPartIntonInt, beanObj.getOthers_ro_part(),
											beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro());

									skipPartIntOpentmp = Out.getSkipPartialIntClose();
									skipPartIntOpentmp = AmortUtil.round(skipPartIntOpentmp,
											beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(),
											beanObj.getOthers_unit_ro());

									round_interest = Out.getRoundInterest();

									skipIntBeforePartial = Out.getSkipIntBeforePartial();
									skipIntBeforePartial = AmortUtil.round(skipIntBeforePartial,
											beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(),
											beanObj.getOthers_unit_ro());

									beanObj.setCapitalize(true);
								}
								beanObj.setSkipAdjust(true);
								if (isDebugEnabled)
									LOGGER.debug("Inside calculateAmort:After applying Skip variation EMI  ="
											+ round_emi + " and Principle Amount= " + round_principal + "and interest="
											+ interest + "and closing balance =" + close_balance);
							}
							if (i + 1 == (sm.getFrm_month() + sm.getNo_month() - 1)) {
								if (!sm.getSkipPartPay().equals(AmortConstant.SkipEMI.SkipPartialInterest)) {
									beanObj.setTotAccumulInt(accumulatedInt);
								} else {
									beanObj.setTotAccumulInt(beanObj.getAccumulInt());
								}
								if (sm.getSkip_capital().equals(AmortConstant.SkipEMI.SkipAccum)
										|| sm.getSkip_capital().equals(AmortConstant.SkipEMI.SkipCumulative)) {
									beanObj.setCapitalize(true);
									round_close = round_open + beanObj.getTotAccumulInt();
									close_balance = open_balance + beanObj.getTotAccumulInt();
								}
							}

							/* PS-327 */
							if (beanObj.getAddIntAmount() > 0 && i == 0) {
								round_interest = round_interest + beanObj.getAddIntAmount();
							}

							// check below condition weather installment no. is equal to after completion of
							// Skip Tenor only in case of AdjustEMI or AdjustTenor.
							if (i == (sm.getFrm_month() + sm.getNo_month() - 1)
									&& (beanObj.getAdjust_Option() == AmortConstant.SkipEMI.AdjustEMI
											|| beanObj.getAdjust_Option() == AmortConstant.AdjustTenor)
									&& iteration == 3 && beanObj.isSkipAdjust()) {
								beanObj.setCapitalize(false);

								beanObj.setLoanAmount(round_open);
								beanObj.setDateOfCycle(cycle_date);
								arrfinal = calAmortAfterVariation(arrfinal, open_balance, term, rate, beanObj, i);

							}
							if (i == (sm.getFrm_month() + sm.getNo_month() - 1)
									&& (beanObj.getAdjust_Option() == AmortConstant.SkipEMI.AdjustedEMI)
									&& iteration == 3 && beanObj.isSkipAdjust()) {
								beanObj.setCapitalize(false);

							}

						}
					}
				}
				// End Skip.
				/*
				 * In input if any variation is present with AdjustEMI or AdjustTenor present
				 * then we have regenerate amort applying variation. Once regenerates amort
				 * added the all installments to Arraylist.if below arraylist is not null then
				 * it will calculate totalEMI and Totalprinciple and Total Interest. After that
				 * this will comeout from this API.
				 */
				if (arrfinal != null) {
					total_emi += overviewbean.getTotalEMI();
					total_interest += overviewbean.getTotalInterest();
					total_principal += overviewbean.getTotalPrincipal();
					total_round_emi += overviewbean.getTotalRoundEMI();
					total_round_interest += overviewbean.getTotalRoundInterest();
					total_round_principal += overviewbean.getTotalRoundPrincipal();
					overviewbean.setTotalInstallments(term);
					overviewbean.setTotalEMI(total_emi);
					overviewbean.setTotalInterest(total_interest);
					overviewbean.setTotalPrincipal(total_principal);
					overviewbean.setTotalRoundEMI(total_round_emi);
					overviewbean.setTotalRoundInterest(total_round_interest);
					overviewbean.setTotalRoundPrincipal(total_round_principal);
					beanObj.setCapitalize(false);
					return arrfinal;
				}
				// In presence of Skip Accumulate and Skip Cumualated options add Interst
				// portion with Skip period Interst.
				/*
				 * if(i == n && beanObj.getTotAccumulInt() != 0 || beanObj.getTotAccumulInt() !=
				 * 0 && beanObj.getAdjust_Option() == AmortConstant.SkipEMI.AdjustedEMI ){
				 * round_interest =
				 * (BigDecimal.valueOf(round_interest).add(BigDecimal.valueOf(beanObj.
				 * getTotAccumulInt()))).doubleValue();
				 * 
				 * if(round_interest > round_emi){ err.put("ERR_NEGATIVE_AMORT",
				 * AmortConstant.AmortValidation.ERR_NEGATIVE_AMORT); if(isDebugEnabled)LOGGER.
				 * error("Inside calculateAmort:Error occurred at Installment No.=" + i +
				 * " because Interest=" + interest + " greater than EMI =" + final_emi); break;
				 * } }
				 */
				// Rounding Opening Balance Portion with others rounding options.
				if (beanObj.getOthers_ro() == AmortConstant.RoundingAll)
					round_open = AmortUtil.round(round_open, beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(),
							beanObj.getOthers_unit_ro());
				if ((beanObj.getOthers_ro_part() == AmortConstant.RoundingDeciaml)
						&& (beanObj.getOthers_ro() == AmortConstant.RoundingAll))
					round_open = Double.parseDouble(AmortUtil.handlingNum(round_open, beanObj.getOthers_unit_ro()));

				// Rounding EMI Portion.If the Installment is Interest only option then Rounding
				// with Others rounding option else rounding with EMI rounding options.
				if (i + 1 <= beanObj.getInt_Only() || flg == 1 || install_type == AmortConstant.Installment_InterestOnly
						|| skipIntDonotRound || install_type == 3) {
					if (beanObj.getEMI_ro() == AmortConstant.RoundingAll) {
						round_emi = AmortUtil.round(round_emi, beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(),
								beanObj.getOthers_unit_ro());
						roundemi = AmortUtil.round(roundemi, beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(),
								beanObj.getOthers_unit_ro());

					}
				} else {
					if (beanObj.getEMI_ro() == AmortConstant.RoundingAll && round_emi != 0) {
						round_emi = AmortUtil.round(round_emi, beanObj.getEMI_ro_part(), beanObj.getEMI_ro_to(),
								beanObj.getEMI_unit_ro());
						roundemi = AmortUtil.round(roundemi, beanObj.getEMI_ro_part(), beanObj.getEMI_ro_to(),
								beanObj.getEMI_unit_ro());
					}

					if ((beanObj.getEMI_ro_part() == AmortConstant.RoundingDeciaml)
							&& (beanObj.getEMI_ro() == AmortConstant.RoundingAll)) {
						round_emi = Double.parseDouble(AmortUtil.handlingNum(round_emi, beanObj.getEMI_unit_ro()));
						roundemi = Double.parseDouble(AmortUtil.handlingNum(roundemi, beanObj.getEMI_unit_ro()));

					}
				}
				// Rounding Interest Portion with Others rounding options.
				if (beanObj.getOthers_ro() == AmortConstant.RoundingAll)
					round_interest = AmortUtil.round(round_interest, beanObj.getOthers_ro_part(),
							beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro());
				if ((beanObj.getOthers_ro_part() == AmortConstant.RoundingDeciaml)
						&& (beanObj.getOthers_ro() == AmortConstant.RoundingAll))
					round_interest = Double
							.parseDouble(AmortUtil.handlingNum(round_interest, beanObj.getOthers_unit_ro()));
				// Rounding Principle Portion with others rounding options.
				if (beanObj.isCapitalize() == false) {
					if (beanObj.getInstallmentType() == AmortConstant.Installment_EquatedPrincipal) {
						round_principal = AmortUtil.round(round_principal, beanObj.getOthers_ro_part(),
								beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro());
						round_emi = (BigDecimal.valueOf(round_interest).add(BigDecimal.valueOf(round_principal)))
								.doubleValue();
					} else {
						round_principal = (BigDecimal.valueOf(round_emi).subtract(BigDecimal.valueOf(round_interest)))
								.doubleValue();
					}
				}
				if (beanObj.getOthers_ro() == AmortConstant.RoundingAll)
					round_principal = AmortUtil.round(round_principal, beanObj.getOthers_ro_part(),
							beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro());

				if ((beanObj.getOthers_ro_part() == AmortConstant.RoundingDeciaml)
						&& (beanObj.getOthers_ro() == AmortConstant.RoundingAll))
					round_principal = Double
							.parseDouble(AmortUtil.handlingNum(round_principal, beanObj.getOthers_unit_ro()));

				// Rounding Close Portion with others rounding options.
				if (beanObj.isCapitalize() == false)
					round_close = (BigDecimal.valueOf(round_open).subtract(BigDecimal.valueOf(round_principal)))
							.doubleValue();
				if (beanObj.getOthers_ro() == AmortConstant.RoundingAll)
					round_close = AmortUtil.round(round_close, beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(),
							beanObj.getOthers_unit_ro());
				if ((beanObj.getOthers_ro_part() == AmortConstant.RoundingDeciaml)
						&& (beanObj.getOthers_ro() == AmortConstant.RoundingAll))
					round_close = Double.parseDouble(AmortUtil.handlingNum(round_close, beanObj.getOthers_unit_ro()));

				if (isDebugEnabled)
					LOGGER.info("Inside calculateAmort:After Rounding the values are round_open=" + round_open
							+ "round_emi =" + round_emi + "round_interest=" + round_interest + "round_principal="
							+ round_principal + "round_close=" + round_close + " for the Installment No." + i);

				// Set all the calculated values to output bean for every installment.
				if (beanObj.isSOMAmort() == true) {
					String dateformat = beanObj.getDateformat();
					SimpleDateFormat df = new SimpleDateFormat(dateformat);
					Date cyl_date = df.parse(cycle_date);
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(cyl_date);
					if (freqperiod == AmortConstant.setMonth) {
						calendar.add(Calendar.MONTH, -(freqfactor * 1));
					} else if (freqperiod == AmortConstant.setWeek) {
						calendar.add(Calendar.WEEK_OF_MONTH, -(freqfactor * 1));
					} else if (freqperiod == AmortConstant.setDate) {
						calendar.add(Calendar.DATE, 1);
					}
					cycle_date = df.format(calendar.getTime());
				} else {
					out.setCycleDate(cycle_date);
				}
				out.setInstallment(Integer.toString(i + 1));
				out.setCycleDate(cycle_date);
				out.setOpeningBalance(open_balance);
				out.setRestOpeningBalance(rest_open_balance);
				out.setClosingBalance(close_balance);
				out.setEmiAmount(final_emi);
				out.setInterestEMI(interest);
				out.setPrincipalEMI(principal_emi);
				out.setRoundEMI(round_emi);
				out.setRoundOpen(round_open);
				out.setRoundInterest(round_interest);
				out.setRoundPrincipal(round_principal);
				out.setRoundClose(round_close);
				out.setRoundRestOpenBal(round_rest_open);
				out.setSkipPartialInt(skipPartInt);
				out.setSkipPartialIntClose(skipPartIntClose);
				out.setSkipPartialIntOpen(skipPartIntOpen);
				out.setSkipPartialIntonInt(skipPartIntonInt);
				out.setSkipIntBeforePartial(skipIntBeforePartial);
				skipPartInt = 0;
				skipPartIntClose = 0;
				skipPartIntOpen = 0;
				skipPartIntonInt = 0;
				skipIntBeforePartial = 0;
				if (roundemi > 0)
					emiList.add(roundemi);

				if (beanObj.getintAmort().equals("Y")) {

					out.setIntOpeningBalance(open_balance);
					out.setIntClosingBalance(close_balance);
					out.setIntInterestEMI(round_interest);
					out.setIntPrincipalEMI(round_principal);
				}

				// Calculate Component(Fees/Subproduct/Tax))

				if (!beanObj.getApply_adv().equals("Y") && null != beanObj.getArrFees()) {
					HashMap hmComponent = new HashMap();
					hmComponent = getComponent(out, beanObj);
					out.setComponent(hmComponent);
					out.setAdj_installment(round_emi + ((Double) hmComponent.get("Total")).doubleValue());
				} else
					out.setAdj_installment(out.getRoundEMI());
				// Component end

				// rule 78- calculate total interest and total installments for this iteration
				if (beanObj.getAmortType() == AmortConstant.AmortType_Rule78) {
					tot_int = (loan_amount * rate * (term) / frequency);
				} else {
					tot_int = tot_int + interest;
				}
				tot_installments = tot_installments + (i + 1);
				tot_round_int = tot_round_int + round_interest;
				if (beanObj.getAmortType() == AmortConstant.AmortType_Rule78) {
					tot_round_int78 = (loan_amount * rate * (term) / frequency);
					double tot_amt = tot_round_int78 + loan_amount;
				}
				// rule 78 end

				total_emi += out.getEmiAmount();
				total_interest += out.getInterestEMI();
				total_principal += out.getPrincipalEMI();

				total_round_emi = (BigDecimal.valueOf(total_round_emi).add(BigDecimal.valueOf(out.getRoundEMI())))
						.doubleValue();
				total_round_interest = (BigDecimal.valueOf(total_round_interest)
						.add(BigDecimal.valueOf(out.getRoundInterest()))).doubleValue();
				total_round_principal = (BigDecimal.valueOf(total_round_principal)
						.add(BigDecimal.valueOf(out.getRoundPrincipal()))).doubleValue();

				otArr.add(out);
				residue = close_balance;
				if (isDebugEnabled)
					LOGGER.debug("Inside calculateAmort:Residue = " + residue + " for the Itereation= " + iteration);
				if (isDebugEnabled)
					LOGGER.debug("Inside calculateAmort:SemiFinal Result : EMI= " + final_emi + " interesr= " + interest
							+ " principal emi= " + principal_emi + " opening balance= " + open_balance
							+ " closing balance= " + close_balance + " Cycle Date= " + cycle_date);
				i++;
				if (beanObj.isTenorpresent()) {
					term = i;
				}
				if (beanObj.getOnbasisValue() == AmortConstant.Component.onBasisNearest && tenorNotPresent) {
					beanObj.setThreshValue(50);
					threshValue = ((AmortOutputBean) otArr.get(term - 1)).getRoundEMI()
							* (beanObj.getThreshValue() / 100);
					if (isDebugEnabled)
						LOGGER.debug(
								"Inside latEMI_Adjustment:Calculated ThreshValue Using NearestOption  =" + threshValue);
				}
				// Calculate Thresh Value using Percent Basis
				else if (beanObj.getOnbasisValue() == AmortConstant.Component.onBasisPerBasis && tenorNotPresent) {
					threshValue = ((AmortOutputBean) otArr.get(term - 1)).getRoundEMI()
							* (beanObj.getThreshValue() / 100);
					if (isDebugEnabled)
						LOGGER.debug(
								"Inside latEMI_Adjustment:Calculated ThreshValue Using PercentBasis  =" + threshValue);
				}
				// Calculate Thresh Value using Amount Basis
				else if (beanObj.getOnbasisValue() == AmortConstant.Component.onBasisAmtBasis && tenorNotPresent) {
					threshValue = beanObj.getThreshValue();
					if (isDebugEnabled)
						LOGGER.debug(
								"Inside latEMI_Adjustment:Calculated ThreshValue Using AmountBasis  =" + threshValue);
				}
				if (tenorNotPresent && (close_balance + beanObj.getBP_Lastpayamt()) > 0 && interest >= emi) {
					err.put("ERR_NEGATIVE_AMORT", AmortConstant.AmortValidation.ERR_NEGATIVE_AMORT);
					if (isDebugEnabled)
						LOGGER.error("Inside calculateAmort:Error occurred at Installment No.=" + i
								+ " because Interest=" + interest + " greater than EMI =" + final_emi);
					break;
				}

			} while ((tenorNotPresent && (close_balance + beanObj.getBP_Lastpayamt()) > threshValue)
					|| (tenorPresent && i < term));
			// end of for loop for number of installments

			real_tenor = term;
			// beanObj.setReal_lid(cycle_date);
			overviewbean.setTotalInstallments(term);
			overviewbean.setTotalEMI(total_emi);
			overviewbean.setTotalInterest(total_interest);
			overviewbean.setTotalPrincipal(total_principal);
			overviewbean.setTotalRoundEMI(total_round_emi);
			overviewbean.setTotalRoundInterest(total_round_interest);
			overviewbean.setTotalRoundPrincipal(total_round_principal);
			beanObj.setCapitalize(false);

		} catch (Exception exception) {
			if (isDebugEnabled)
				LOGGER.error("Method : calculateAmort() : Exception thrown " + exception.toString());
			throw new AmortException.CalculationFactoryException(exception);
		}

		return otArr;
	}

//	ABFL Equated Principle Frequency Change start
	public boolean isFrqEqPrinciple(AmortInputBean beanObj, int i) {
		int equPrincfrq = (int) beanObj.getEqPrinFreq();
		int repfrequency = beanObj.getInterestFrequency();
		int modFactor = 1;
		if (equPrincfrq < repfrequency) {
			modFactor = repfrequency / equPrincfrq;
		} else {
			modFactor = equPrincfrq / repfrequency;
		}
		if ((i + 1) % modFactor == 0)
			return true;
		else
			return false;

	}
	// ABFL Equated Principle Frequency Change End

	/**
	 * Applying variation for Installment. This API calls from calculateAmort if any
	 * installment having variation. In this API that variation Amort is applied to
	 * that Installment and goes back to calling API. Finally it gives arraylist of
	 * output bean object.
	 * 
	 * @param input bean object.
	 * @return arraylist of output bean object.
	 * @see calVariationAmort(AmortInputBean,double,int,double)
	 * @see AmortInputBean()
	 */

	public ArrayList calVariationAmort(AmortInputBean beanObj, int term, int i, double open_balance,
			double skipPartIntOpen) throws AmortException {
		if (isDebugEnabled)
			LOGGER.info("Inside calVariationAmort:");
		ArrayList arrInitial = null;
		double principal_emi = 0.0d;
		double round_emi = 0.0d;
		double final_emi = 0.0d;
		double round_interest = 0.0d;
		double round_close = 0.0d;
		double close_balance = 0.0d;
		double rate = beanObj.getInterestRate();
		double principle_emi = 0.0d;
		String date_dis = "";
		String cycle_date = "";
		String prev_date = "";
		double round_principal = 0.0d;
		double skipPartIntClose = 0;
		double skipPartInt = 0;
		double skipPartIntonInt = 0;
		double skipIntBeforePartial = 0;
		arrInitial = new ArrayList();
		AmortOutputBean out = new AmortOutputBean();
		date_dis = beanObj.getCurrentCycleDate();
		cycle_date = date_dis;
		// cycle_date =
		// AmortUtil.getCycleDate(beanObj.getDateOfCycle(),date_dis,beanObj.getDateformat(),beanObj.getFrequencyFactor(),beanObj.getFrequencyPeriod(),0);
		prev_date = AmortUtil.getCycleDate(beanObj.getDateOfCycle(), cycle_date, beanObj.getDateformat(),
				beanObj.getFrequencyFactor(), beanObj.getFrequencyPeriod(), -1, beanObj.isEOMAmort());
		if (i == 0 && (beanObj.getBPI_Recovery() == 0 || bpi == 0)) {
			cycle_date = beanObj.getDateOfCycle();
			// prev_date = beanObj.getDateOfDisbursement();
			prev_date = AmortUtil.getCycleDate(beanObj.getDateOfCycle(), beanObj.getDateOfCycle(),
					beanObj.getDateformat(), beanObj.getFrequencyFactor(), beanObj.getFrequencyPeriod(), -1,
					beanObj.isEOMAmort());
		}

		/*
		 * Step EMI If Step Up/Down is defined for any installment then EMI of that
		 * installment is recalculated according to Step Up/Down input Only if step
		 * adjust option is adjusted EMI
		 */

		double interest = AmortUtil.getInterest(open_balance, rate, beanObj.getCompoundFreq(),
				beanObj.getInterest_Basis(), prev_date, cycle_date, beanObj.getDateformat(), false,
				beanObj.getIntCalMethod());
		if (bpi < 0 && i == 0) {
			interest = AmortUtil.getInterest(open_balance, rate, beanObj.getCompoundFreq(), beanObj.getInterest_Basis(),
					beanObj.getDateOfDisbursement(), cycle_date, beanObj.getDateformat(), false,
					beanObj.getIntCalMethod());
		}
		if (null != beanObj.getArrStepEMI()) {
			if (isDebugEnabled)
				LOGGER.info("Inside calVariationAmort: Inside Step Variation.");
			ArrayList arr = (ArrayList) beanObj.getArrStepEMI();
			for (int k = 0; k < arr.size(); k++) {
				StepEMIInputBean sm = (StepEMIInputBean) arr.get(k);

				if (i + 1 >= sm.getFrm_month() && i + 1 <= sm.getTo_month()) {
					// If the Stepbasis is StepAmout then Inputted StepAmount added to base emi.
					if (sm.getStepbasis() == AmortConstant.StepUpDown.StepAmount) {
						final_emi = emi + (sm.getStepmodeval() * sm.getStepby());
						round_emi = emi + (sm.getStepmodeval() * sm.getStepby());
						if (isDebugEnabled)
							LOGGER.debug(
									"Inside calVariationAmort:Step Basis is Step Amount:After Applying StepAmount EMI= "
											+ round_emi);
					}
					// If StepBasis is StepAbsolute then Inputted value is the emi.
					else if (sm.getStepbasis() == AmortConstant.StepUpDown.StepEMI) {
						final_emi = sm.getStepby();
						round_emi = sm.getStepby();
						if (isDebugEnabled)
							LOGGER.debug(
									"Inside calVariationAmort:Step Basis is Absolute:After Applying StepAmount EMI= "
											+ round_emi);
					}
					// If Stepbasis is StepPrinciple
					else if (sm.getStepbasis() == AmortConstant.StepUpDown.StepPrin) {
						round_emi = interest + sm.getStepby();
						principal_emi = sm.getStepby();

						if (isDebugEnabled)
							LOGGER.debug(
									"Inside calVariationAmort:Step Basis is Stepprinciple:After Applying Stepprinciple EMI= "
											+ round_emi + "Interest =" + round_interest);
					}
					// if Stepbasis is Step Percentage then calculate value using inputted
					// percentage then that value added to base emi.
					else {
						round_emi = emi;
						if (beanObj.getEMI_ro() == AmortConstant.RoundingAll)
							round_emi = AmortUtil.round(round_emi, beanObj.getEMI_ro_part(), beanObj.getEMI_ro_to(),
									beanObj.getEMI_unit_ro());
						final_emi = round_emi * (1 + (sm.getStepmodeval() * (sm.getStepby() / 100)));
						round_emi = round_emi * (1 + (sm.getStepmodeval() * (sm.getStepby() / 100)));
						if (isDebugEnabled)
							LOGGER.debug(
									"Inside calVariationAmort:Step Basis is StepPercent:After Applying StepPercent EMI= "
											+ round_emi);
					}

					if (round_emi < 0) {
						err.put("ERR_NEGATIVE_STEPAMOUNT", AmortConstant.AmortValidation.ERR_NEGATIVE_STEPAMOUNT);
						if (isDebugEnabled)
							LOGGER.error("Inside calVariationAmort:Error is occurred due to EMI is lessthan Zero.EMI="
									+ round_emi);
						break;
					}

				}

			}
		}
		// Step EMI End

		/*
		 * Balloon Payment. If Bp is defined for any Installment then that will apply to
		 * base emi so emi will be changed.
		 */
		if (null != beanObj.getArrBP()) {
			if (isDebugEnabled)
				LOGGER.info("Inside calVariationAmort: Inside Balloon Variation.");
			ArrayList arrBP = beanObj.getArrBP();
			for (int j = 0; j < arrBP.size(); j++) {
				BPInputBean bp = (BPInputBean) arrBP.get(j);
				// Chech BPI month and applying Amount to that Installment.
				if (i + 1 == bp.getBP_Month()) {
					final_emi = AmortUtil.addDouble(emi, bp.getBP_Amount());
					round_emi = AmortUtil.addDouble(emi, bp.getBP_Amount());
					if (isDebugEnabled)
						LOGGER.info("Inside calVariationAmort: Inside Balloon Variation.Balloon Amount= "
								+ bp.getBP_Amount() + "After applying Balloon EMI=" + round_emi);
				}

			}

		}
		/*
		 * Skip EMI. If Skip EMI is defined for any installment then this will skip emi
		 * and interest amd principle to that installment.
		 */
		if (null != beanObj.getArrSkipEMI()) {
			if (isDebugEnabled)
				LOGGER.info("Inside calVariationAmort: Inside Skip Variation.");
			ArrayList arrskip = (ArrayList) beanObj.getArrSkipEMI();

			for (int k = 0; k < arrskip.size(); k++) {
				SkipEMIInputBean sm = (SkipEMIInputBean) arrskip.get(k);

				if (i + 1 >= sm.getFrm_month() && i + 1 <= (sm.getFrm_month() + sm.getNo_month() - 1)) {
					final_emi = 0;
					principal_emi = 0.0d;
					round_emi = 0.0d;
					round_principal = 0.0d;
					// interest = AmortUtil.round(interest, beanObj.getOthers_ro_part(),
					// beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro());
					double tempInt = interest;
					skipIntBeforePartial = interest;

					if (sm.getSkipPartPay().equals(AmortConstant.SkipEMI.SkipPartialInterest)) {

						if (sm.getSkipPartPayIn().equals(AmortConstant.SkipEMI.SkipPartialPayIn_perc)) {
							interest = interest * sm.getSkipPartInterest() / 100;
						} else {

							// PS-321
							if (interest < sm.getSkipPartInterest()) {

								interest = interest;

							} else {
								interest = sm.getSkipPartInterest();
							}
						}
						interest = AmortUtil.round(interest, beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(),
								beanObj.getOthers_unit_ro());
						round_interest = interest;
						round_emi = round_interest;
						close_balance = open_balance;
						skipPartInt = tempInt - interest;
						skipPartIntonInt = AmortUtil.getInterest(skipPartIntOpen, rate, beanObj.getCompoundFreq(),
								beanObj.getInterest_Basis(), prev_date, cycle_date, beanObj.getDateformat(), false,
								beanObj.getIntCalMethod());
						if (bpi < 0 && i == 0) {
							skipPartIntonInt = AmortUtil.getInterest(skipPartIntOpen, rate, beanObj.getCompoundFreq(),
									beanObj.getInterest_Basis(), beanObj.getDateOfDisbursement(), cycle_date,
									beanObj.getDateformat(), false, beanObj.getIntCalMethod());
						}
						skipPartIntonInt = AmortUtil.round(skipPartIntonInt, beanObj.getOthers_ro_part(),
								beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro());

						if (sm.getSkip_capital().equals(AmortConstant.SkipEMI.SkipCapitalValueY)) {
							skipPartIntonInt = 0;
							close_balance = open_balance + skipPartInt;
							round_close = close_balance;
						} else if (sm.getSkip_capital().equals(AmortConstant.SkipEMI.SkipCumulative)) {
							skipPartIntClose = skipPartIntOpen + skipPartInt + skipPartIntonInt;
							beanObj.setAccumulInt(skipPartIntClose);
							close_balance = open_balance;
							round_close = open_balance;
						} else if (sm.getSkip_capital().equals(AmortConstant.SkipEMI.SkipAccum)) {
							skipPartIntClose = skipPartIntOpen + skipPartInt;
							skipPartIntonInt = 0;
							beanObj.setAccumulInt(skipPartIntClose);
							close_balance = open_balance;
							round_close = open_balance;
						} else if (sm.getSkip_capital().equals(AmortConstant.SkipEMI.SkipCapitalValue)) {
							skipPartIntonInt = 0;
							close_balance = open_balance;
							round_close = open_balance;
							// skipPartInt = 0;
						}
					} else {
						// Check condition for Skip capital.If present then only interest portion is
						// capitalized.
						if (sm.getSkip_capital().equals(AmortConstant.SkipEMI.SkipCapitalValueY)) {
							if (isDebugEnabled)
								LOGGER.info("Inside calVariationAmort: Inside Skip Variation.Skip Capitalize is true");
							/*
							 * if(bpi < 0 && i == 0 && beanObj.getBPI_Recovery()!=0){
							 * 
							 * interest = open_balance *
							 * AmortUtil.getDayfactor(beanObj.getBPIMethod(),beanObj.getDateOfDisbursement()
							 * ,beanObj.getDateOfCycle(),beanObj.getDateformat(),false) * rate; }
							 */

							/*
							 * adding bpi into interest in case of skip, cap, initial, bpi>0, bpi- add to
							 * first installment
							 */
							if (sm.getSkip_capital().equals("Y") && sm.getFrm_month() == 1
									&& beanObj.getBPI_Recovery() == 0 && bpi > 0 && i == 0) {
								interest = interest + bpi;
							}

							round_interest = interest;
							beanObj.setCapitalize(true);
							if (beanObj.getOthers_ro() == AmortConstant.RoundingAll)
								round_interest = AmortUtil.round(round_interest, beanObj.getOthers_ro_part(),
										beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro());
							close_balance = open_balance + interest;

						} else if (sm.getSkip_capital().equals(AmortConstant.SkipEMI.SkipCapitalValuePayable)) {
							// round_interest = AmortUtil.round(interest, beanObj.getOthers_ro_part(),
							// beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro());
							round_interest = interest;
							round_emi = round_interest;
							close_balance = open_balance;
						} else {
							if (sm.getSkip_capital().equals(AmortConstant.SkipEMI.SkipCumulative)
									|| sm.getSkip_capital().equals(AmortConstant.SkipEMI.SkipAccum)) {
								beanObj.setAccumulInt(interest);
							}
							close_balance = open_balance;
							round_close = open_balance;
							round_interest = 0;
							interest = 0.0;
						}
					}
					if (isDebugEnabled)
						LOGGER.info(
								"Inside calVariationAmort: Inside Skip Variation.After applying Skip variation:EMI= "
										+ round_emi + "Interest=" + round_interest + "Principle=" + round_principal
										+ "ClosingBalance=" + close_balance);
				}
			}
		}
		// setting all the value to output bean.
		out.setEmiAmount(round_emi);
		out.setClosingBalance(close_balance);
		out.setPrincipalEMI(principal_emi);
		out.setRoundClose(round_close);
		out.setRoundInterest(round_interest);
		out.setInterestEMI(interest);
		out.setSkipPartialInt(skipPartInt);
		out.setSkipPartialIntClose(skipPartIntClose);
		out.setSkipPartialIntOpen(skipPartIntOpen);
		out.setSkipPartialIntonInt(skipPartIntonInt);
		out.setSkipIntBeforePartial(skipIntBeforePartial);
		arrInitial.add(out);
		return arrInitial;
	}

	/**
	 * calAmortAfterVariation starts. This API calls from CalculateAmort API. In the
	 * I/P request if any variation is present with AdjustEMI or AdjustTenor options
	 * then after applying variation this API will call. In this API depends on
	 * Adjustment option in case of AdjustEMI emi will be recalculated and using
	 * that emi this will give the call to generateAmort API. By using high-low
	 * method rest of Amort will be generated.
	 * 
	 * @param arrfinal
	 * @param open_balance
	 * @param term
	 * @param rate
	 * @param beanObj
	 * @param i
	 * @return arrfinal
	 * @throws AmortException
	 */

	public ArrayList calAmortAfterVariation(ArrayList arrfinal, double open_balance, int term, double rate,
			AmortInputBean beanObj, int i) throws AmortException {

		// check contion StepAdjust.If the Step adjust is AdjustEMI then EMI will be
		// recalcultes and give the call to generateAmort API to generate rest of AMort
		// else Tenor is recalcultes.
		if (beanObj.getAdjust_Option() == AmortConstant.AdjustEMI) {
			beanObj.setTenor(term - (i));
			rate = (AmortUtil.getRate(rate, beanObj.getCompoundFreq(), beanObj.getRepaymentFrequency()));
			beanObj.setInput_emi(emi);
			beanObj.setEMIpresent(true);
			beanObj.setTenorpresent(false);
			beanObj.setLoanAmountpresent(false);
			beanObj.setInterestRatepresent(false);
			if (isDebugEnabled)
				LOGGER.info(
						"Inside calculateAmort:After completion of variation applied in case of AdjustEMI option EMI will be recalculred.EMI="
								+ emi);
		} else {
			rate = (AmortUtil.getRate(rate, beanObj.getCompoundFreq(), beanObj.getRepaymentFrequency()));
			term = AmortUtil.claculateNPER(beanObj.getLoanAmount(), beanObj.getInput_emi(), rate,
					beanObj.getBP_Lastpayamt());
			beanObj.setInput_emi(emi);
			beanObj.setTenor(term);
			beanObj.setEMIpresent(false);
			beanObj.setTenorpresent(true);
			beanObj.setLoanAmountpresent(false);
			beanObj.setInterestRatepresent(false);
			if (isDebugEnabled)
				LOGGER.info(
						"Inside calculateAmort:After completion of variation applied in case of AdjustTenor option Tenor will be recalculred.Tenor="
								+ term);
		}
		// If variation (AdjustEMI or AdjustTenor) is present then assign the
		// installment No.before starting variation term.
		beanObj.setAdjterm(i);
		beanObj.setStepAdjust(false);
		beanObj.setSkipAdjust(false);
		beanObj.setBPAdjust(false);
		ArrayList arrayList = new ArrayList();
		for (int j = 0; j < i; j++) {
			arrayList.add(j, otArr.get(j));
		}
		// Call the generateAPI for generating Amort with High-Low method After applying
		// variation.
		beanObj.setAdvEmiCalculated(false);
		arrfinal = calAmortHighLow(arrayList, beanObj);
		// Add the rest of Installmets to Arraylist after applying vation.
		for (int j = 0; j < i; j++) {
			arrfinal.set(j, arrayList.get(j));
		}
		return arrfinal;
	}

	public HashMap getComponent(AmortOutputBean out, AmortInputBean beanObj) throws AmortException {
		double cval = 0.0d;
		double total_cval = 0.0d;
		double[] val1 = null;

		double pval = 0.0d;
		boolean flag = false;
		LinkedHashMap hmComponent = new LinkedHashMap();
		try {
			if (null != beanObj.getArrFees()) {
				ArrayList arrcomponent = beanObj.getArrFees();
				for (int k = 0; k < arrcomponent.size(); k++) {
					ComponentBean cb = (ComponentBean) arrcomponent.get(k);
					if (out.getEmiAmount() == 0) {
						cval = 0.0d;
					} else {
						if (cb.getType().equals(AmortConstant.Component.TypeRange)
								|| cb.getType().equals(AmortConstant.Component.AmtPercentRange)) {
							String[] arrRange = cb.getRange().split("-");

							if (cb.getDependent().equals("N")) {
								pval = cb.getParam().equals(AmortConstant.Component.LoanAmount)
										? beanObj.getLoanAmount()
										: AmortUtil.getParamval(cb.getParam(), out);
							} else {
								pval = (hmComponent.containsKey(cb.getParam()))
										? ((Double) hmComponent.get(cb.getParam())).doubleValue()
										: 0.0;
							}
							if (pval >= Double.parseDouble(arrRange[0]) && pval <= Double.parseDouble(arrRange[1])) {
								flag = true;
							} else {
								flag = false;
								cval = 0;
							}
						} else if (cb.getType().equals(AmortConstant.Component.AmountPercentage)
								|| cb.getType().equals(AmortConstant.Component.Calculate)) {
							flag = true;
						}
						if (flag) {
							// for Amount basis flat value from input is taken as component value.
							if (cb.getBasis() == AmortConstant.Component.BasisAmount) {
								cval = cb.getValue();
							} else if (cb.getBasis() == AmortConstant.Component.BasisPercentage) {
								// If component is not dependent on any variable calculated value then
								// value of parameter specified is used for calculation.
								if (cb.getDependent().equals("N")) {
									// getParamval() gets the value according to Parameter specified.
									// Parameter can be Opening Balance,Interest,EMI,Principal

									if (cb.getParam() == AmortConstant.Component.LoanAmount)
										cval = (cb.getPercentvalue() / 100) * beanObj.getLoanAmount();
									else
										cval = (cb.getPercentvalue() / 100) * AmortUtil.getParamval(cb.getParam(), out);
								}

								// Else the value initially calculated and stored in HashMap
								// is used for calculations.
								else {
									cval = (hmComponent.containsKey(cb.getParam())) ? ((cb.getPercentvalue() / 100)
											* ((Double) hmComponent.get(cb.getParam())).doubleValue()) : 0;
								}

							} else if (cb.getBasis() == AmortConstant.Component.BasisCalculation) {
								// Multiple parameters separated by ,
								if (cb.getParam().indexOf(",") != -1) {
									String[] arrParam = cb.getParam().split(",");
									val1 = new double[arrParam.length];
									for (int j = 0; j < arrParam.length; j++) {
										if (hmComponent.containsKey(arrParam[j])) {
											val1[j] = ((Double) hmComponent.get(arrParam[j])).doubleValue();
										} else {
											val1[j] = arrParam[j].equals(AmortConstant.Component.LoanAmount)
													? beanObj.getLoanAmount()
													: AmortUtil.getParamval(arrParam[j], out);
										}
									}
									cval = AmortUtil.Calculate(cb.getCalc(), val1);
									cval = AmortUtil.Calculate(cb.getCalc(), cval, cb.getValue());
								} else {
									if (cb.getDependent().equals("Y"))
										cval = (hmComponent.containsKey(cb.getParam())) ? AmortUtil.Calculate(
												cb.getCalc(), ((Double) hmComponent.get(cb.getParam())).doubleValue(),
												cb.getValue()) : 0;

									else {
										// if(cb.getParam() == AmortConstant.Component.LoanAmount)
										if (cb.getParam().equals(AmortConstant.Component.LoanAmount))
											cval = AmortUtil.Calculate(cb.getCalc(), beanObj.getLoanAmount(),
													cb.getValue());
										else {
											cval = AmortUtil.Calculate(cb.getCalc(),
													AmortUtil.getParamval(cb.getParam(), out), cb.getValue());
										}
									}
								}
							}
						}

						if (cb.getCalc() == AmortConstant.Component.Min || cb.getCalc() == AmortConstant.Component.Max)
							cval = AmortUtil.Calculate(cb.getCalc(), cval, cb.getValue());
					}

					total_cval = total_cval + cval;
					hmComponent.put(cb.getFeename(), new Double(cval));
					hmComponent.put("Total", new Double(total_cval));
				}
			}
		} catch (Exception exception) {
			if (isDebugEnabled)
				LOGGER.error("Method : getComponent() : Exception thrown " + exception.toString());
			throw new AmortException.CalculationFactoryException(exception);
		}

		return hmComponent;
	}

	/**
	 * This API will call as per Method call to Parse I/P xml.
	 * 
	 * @param rxml
	 * @return
	 * @throws AmortException
	 */

	/*
	 * public HashMap processInput(String rxml) throws AmortException { if
	 * (isDebugEnabled)
	 * LOGGER.debug("inside Process Method of AmortInputBean : processInput =: " +
	 * rxml); HashMap tempMap = new HashMap(); try { document =
	 * XMLParser.parse(rxml.getBytes()); NodeList suitNodes =
	 * document.getElementsByTagName("root"); final Node childNode =
	 * suitNodes.item(0); NodeList childNodes = childNode.getChildNodes(); if
	 * (isDebugEnabled) LOGGER.debug("String XML Tag Length:" +
	 * childNodes.getLength()); for (int i = 0; i < childNodes.getLength(); i++) {
	 * final Node childRootNode = childNodes.item(i); if
	 * (childRootNode.getNodeName().equals("Step_EMI") ||
	 * childRootNode.getNodeName().equals("Skip_EMI") ||
	 * childRootNode.getNodeName().equals("BP") ||
	 * childRootNode.getNodeName().equals("Adjust_Rate") ||
	 * childRootNode.getNodeName().equals("Fees") ||
	 * childRootNode.getNodeName().equals("COMP") ||
	 * childRootNode.getNodeName().equals("P_STEP") ||
	 * childRootNode.getNodeName().equals("stepprin") ||
	 * childRootNode.getNodeName().equals("NPVROWS")) { HashMap advMap = new
	 * HashMap(); advMap = createAdvHash(childRootNode);
	 * tempMap.put(childRootNode.getNodeName(), advMap); } else if
	 * (childRootNode.getNodeName().equals("AMORT")) { ArrayList arrAmort = new
	 * ArrayList(); arrAmort = createAdvHashAmort(childRootNode);
	 * tempMap.put(childRootNode.getNodeName(), arrAmort); } else if
	 * (childRootNode.getNodeName().equals("EMI_ADJ")) { document =
	 * XMLParser.parse(rxml.getBytes()); NodeList suitNodes1 =
	 * document.getElementsByTagName("EMI_ADJ"); final Node childNode1 =
	 * suitNodes1.item(0); NodeList childNodes1 = childNode1.getChildNodes(); if
	 * (isDebugEnabled) LOGGER.debug("String XML Tag Length:" +
	 * childNodes1.getLength()); for (int j = 0; j < childNodes1.getLength(); j++) {
	 * final Node childRootNode1 = childNodes1.item(j);
	 * 
	 * tempMap.put(childRootNode1.getNodeName(),
	 * XMLUtils.getTagValue(childRootNode1)); } }
	 * 
	 * else { tempMap.put(childRootNode.getNodeName(),
	 * XMLUtils.getTagValue(childRootNode)); } }
	 * 
	 * } catch (Exception exception) { if (isDebugEnabled)
	 * LOGGER.error("Method : getAmortObjecttoJSON() : Exception thrown " +
	 * exception.toString()); throw new
	 * AmortException.CalculationFactoryException(exception); } return tempMap; }
	 */

	/*
	 * public ArrayList createAdvHashAmort(Map.Entry<String, Object> entry) {
	 * ArrayList arrAmort = new ArrayList(); try { NodeList childNodes =
	 * node.getChildNodes(); if (childNodes.getLength() > 0) { for (int i = 0; i <
	 * childNodes.getLength(); i++) { String key = ""; final Node childNode =
	 * childNodes.item(i); if (null != childNode) { if
	 * (childNode.getNodeName().equals("R")) { AmortOutputBean bean = new
	 * AmortOutputBean(); NodeList childNodes1 = childNode.getChildNodes(); for (int
	 * j = 0; j < childNodes1.getLength(); j++) { final Node childNode1 =
	 * childNodes1.item(j); if (childNode1.getNodeName().equals("NO")) {
	 * bean.setInstallment(XMLUtils.getTagValue(childNode1)); } if
	 * (childNode1.getNodeName().equals("EMI")) {
	 * bean.setRoundEMI(Double.parseDouble(XMLUtils.getTagValue(childNode1))); } if
	 * (childNode1.getNodeName().equals("DT")) {
	 * bean.setCycleDate(XMLUtils.getTagValue(childNode1)); } } arrAmort.add(bean);
	 * 
	 * } } } } } catch (Exception exception) { if (isDebugEnabled)
	 * LOGGER.error("Method : createAdvHashAmort() : Exception thrown " +
	 * exception.toString()); } return arrAmort; }
	 */

	public ArrayList generateAmort(AmortInputBean beanObj) throws AmortException {
		if (isDebugEnabled)
			LOGGER.debug("Inside generateAmort : ");
		ArrayList final_list = new ArrayList();
		unAmortized = new Vector();
		cvalVec = new Vector();
		arr_last_date = new Vector();

		try {
			// Check condition in case of InternalAmort presence Repayment Frequency Monthly
			// or not.If not throwing validation message.
			if (beanObj.getRepaymentFrequency() != 12 && beanObj.getintAmort().equals("Y")) {
				err.put("ERR_INT_AMORT_FREQUENCY", AmortConstant.AmortValidation.ERR_INT_AMORT_FREQUENCY);
				if (isDebugEnabled)
					LOGGER.error("Repayment frequency should be Monthly.");
				return final_list;
			}

			// Check the condition EMI Unit Rounding is greater than Others Unit Rounding.If
			// true then throwing validation.
			if (beanObj.getEMI_unit_ro() > beanObj.getOthers_unit_ro()) {
				err.put("INVALID_ROUNDING_PARAMETERS", AmortValidation.ERR_EMI_ROUNDING);
				if (isDebugEnabled)
					LOGGER.error("EMI Unit Rounding should not be greater than Others Unit Rounding.");
				return final_list;
			}

			if (beanObj.getRVvalue() != 0) {

				final_list = calculateAmortStraightLine(beanObj);
			}

			/**
			 * Find the Exact value using high-low method. Check what is missing out of four
			 * parameters principle,EMI,Tenor,Interest in the input.This is already check in
			 * InitializeInputModel() API. By calling calAmortHighLow() API,Amort is
			 * scheduled by using High-Low logic.
			 * 
			 * @param input                bean object
			 * @param final_list-ArrayList object which is having Amort schedule.
			 * @return arraylist of output bean object.
			 * @see calAmortHighLow(ArrayList final_list,AmortInputBean beanObj)
			 */

			if ((beanObj.isTenorpresent() || beanObj.isLoanAmountpresent() || beanObj.isInterestRatepresent()
					|| beanObj.isEMIpresent()) && (beanObj.getAmortType() != AmortConstant.AmortType_Rule78)) {
				final_list = calAmortHighLow(final_list, beanObj);
			} else {
				final_list = calRule78Flat(final_list, beanObj);
			}
			// If Amort Type is Rule of 78 then Calculate Amort by calling below API.
			if (beanObj.getAmortType() == AmortConstant.AmortType_Rule78) {
				final_list = calculateRule78new(final_list, beanObj);

			}

			// For calculating InternalAMort.
			if (beanObj.getintAmort().equals("Y") && beanObj.getRVvalue() == 0 && beanObj.getArrPerPrinRcvr() == null) {
				final_list = Calculate_accrued_interest(beanObj);
			}

			/**
			 * By Calling this API Last EMI handling should be done based on input
			 * parameters. Internally in this API handling either creates new installment or
			 * Adjusting in same installment based on residue value and input parameters.
			 * Once Adjusing Last Installment this will return ArrayList object which is
			 * contains Total amort.
			 * 
			 * @param input                bean object
			 * @param final_list-ArrayList object which is having Amort schedule.
			 * @return arraylist of output bean object.
			 * @see latEMI_Adjustment(ArrayList final_list,AmortInputBean beanObj)
			 */
			if (beanObj.getAmortType() != AmortConstant.AmortType_Rule78)
				final_list = latEMI_Adjustment(final_list, beanObj);

			// added for bullet: pricipal+interest
			if (beanObj.getInstallmentType() == 3) {
				final_list = adjust_Last_bullet_intprin(final_list, beanObj);
			}

			/**
			 * By Calling this API BPI Amount is set to first installment based on input
			 * parameters.
			 * 
			 * @param input                bean object
			 * @param final_list-ArrayList object which is having Amort schedule.
			 * @return arraylist of output bean object.
			 * @see calBPI(ArrayList final_list,AmortInputBean beanObj)
			 */
			if (beanObj.getAmortType() != AmortConstant.AmortType_Rule78 && "N".equalsIgnoreCase(bpiCalculated))
				final_list = calBPI(final_list, beanObj);

			if (beanObj.getNoOfAdvEMI() != 0 && beanObj.getAdvEMI_FIRST_INST().equals("Y")) {
				final_list = addFirstRowAdvEMI(beanObj);
			}

			// Deduct TDS if configured in input bean(beanObj)
			deductTDS(final_list, beanObj);

			AmortConstant.isWeeklyflag = false;
			final_list.add(overviewbean);
			return final_list;
		} catch (Exception exception) {
			if (isDebugEnabled)
				LOGGER.error("Method : generateAmort() : Exception thrown " + exception.toString());
			throw new AmortException.CalculationFactoryException(exception);
		}
	}
	
	public HashMap processInput(String jsonRequest) throws AmortException {
		if (isDebugEnabled)
			LOGGER.debug("Inside Process Method : processInput =: " + jsonRequest);

		HashMap<String, Object> tempMap = new HashMap<>();

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			// Map<String, Object> jsonMap = objectMapper.readValue(jsonRequest, Map.class);
			// Convert AmortInputBean to Map
			Map<String, Object> jsonMap = objectMapper.convertValue(jsonRequest, Map.class);

			for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();

				if (key.equals("Step_EMI") || key.equals("Skip_EMI") || key.equals("BP") || key.equals("Adjust_Rate")
						|| key.equals("Fees") || key.equals("COMP") || key.equals("P_STEP") || key.equals("stepprin")
						|| key.equals("NPVROWS")) {
					HashMap<String, Object> advMap = new HashMap<>();
					advMap = createAdvHash(entry);
					// advMap.putAll((Map<String, Object>) value);
					tempMap.put(key, advMap);
				} else if (key.equals("AMORT")) {
					ArrayList<Object> arrAmort = new ArrayList<>();
					arrAmort = (ArrayList<Object>) createAdvHashAmort(entry);
					arrAmort.addAll((List<Object>) value);
					tempMap.put(key, arrAmort);
				} else if (key.equals("EMI_ADJ")) {
					Map<String, Object> emiAdjMap = (Map<String, Object>) value;
					tempMap.put(key, emiAdjMap);
				} else {
					tempMap.put(key, value);
				}
			}

		} catch (Exception exception) {
			if (isDebugEnabled)
				LOGGER.error("Method : processInput() : Exception thrown " + exception.toString());
			throw new AmortException.CalculationFactoryException(exception);
		}

		return tempMap;
	}


}
