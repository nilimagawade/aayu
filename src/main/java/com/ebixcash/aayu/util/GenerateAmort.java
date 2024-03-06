package com.ebixcash.aayu.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.ebixcash.aayu.constant.AmortConstant;
import com.ebixcash.aayu.constant.AmortConstant.AmortValidation;
import com.ebixcash.aayu.exception.AmortException;
import com.ebixcash.aayu.model.AmortInputBean;
import com.ebixcash.aayu.model.AmortOutputBean;


public class GenerateAmort {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GenerateAmort.class);

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

	public ArrayList calRule78Flat(ArrayList rule_list, AmortInputBean beanObj) throws AmortException {
		if (isDebugEnabled)
			LOGGER.debug("Inside generateAmort : ");
		double emi = beanObj.getInput_emi();
		double finalemi = emi;
		rule_list = calculateAmort(beanObj, finalemi, 1);
		return rule_list;
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

}
