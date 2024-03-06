package com.ebixcash.aayu.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ebixcash.aayu.constant.AmortConstant;
import com.ebixcash.aayu.customvalidator.ErrorMessages;
import com.ebixcash.aayu.defaultsetting.DefaultSettingValues;
import com.ebixcash.aayu.irr.XIRR;
import com.ebixcash.aayu.model.AmortInputBean;
import com.ebixcash.aayu.model.AmortOutputBean;
import com.ebixcash.aayu.response.AmortInstallmentResponse;
import com.ebixcash.aayu.response.AmortIntrestResponse;
import com.ebixcash.aayu.service.AmortInstallmentService;
import com.ebixcash.aayu.util.AayuCalculationUtil;
import com.ebixcash.aayu.util.AmortOverviewBean;
import com.ebixcash.aayu.util.InternalAmortComponentBean;

@Service
public class AmortInstallmentServiceImpl implements AmortInstallmentService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AmortInstallmentServiceImpl.class);

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

	@Autowired
	AayuCalculationUtil aayuCalculationUtil;

	@Override
	public ArrayList<Object> getAmortInstallment(AmortInputBean request) {
		double lnAmt = 0.0d;
		double valueCb = 0.0d;
		ArrayList tempListint = null;
		ArrayList tempListIntCust = null;
		String compdata = "";
		String feedata = "";
		ArrayList<AmortOutputBean> amortOutputBeans = new ArrayList<>();
		ErrorMessages errors = new ErrorMessages();
		ArrayList tempList = new ArrayList();
		double actual_rate_value = 0.0d;
		Map<String, Object> tempMap = new HashMap<>();
		Map<String, Object> resultValues = new HashMap<>();
		HashMap err = new HashMap();
		ArrayList<Object> resultList = new ArrayList<>();
		ArrayList<ErrorMessages> errorsList = new ArrayList<>();

		ArrayList<AmortInstallmentResponse> amortInstallmentResponses = new ArrayList<>();
		AmortOutputBean amortOutputBeanJson = new AmortOutputBean();
		AmortInstallmentResponse amortInstallmentResponse = new AmortInstallmentResponse();
		AmortIntrestResponse amortIntrestResponse = new AmortIntrestResponse();

		AmortInstallmentResponse.AmortInstallmentResponseFirstSce amortInstallmentResponseFirstInnerCls = amortInstallmentResponse.new AmortInstallmentResponseFirstSce();
		AmortInstallmentResponse.AmortInstallmentResponseSecondSce amortInstallmentResponseSecondInnerCls = amortInstallmentResponse.new AmortInstallmentResponseSecondSce();

		
		try {
			tempMap = aayuCalculationUtil.processInput(request);
			resultValues = DefaultSettingValues.getDefaultValues(tempMap);
			StringBuffer output = new StringBuffer("");
			String successFlag = (String) resultValues.get("successFlag");
			if ("true".equals(successFlag)) {
				AmortInputBean beanObj = aayuCalculationUtil.InitializeInputModel(tempMap);
				actual_rate_value = beanObj.getInterestRate();

				if (null != beanObj.getArrPerPrinRcvr()) {
					String dds = beanObj.getDateOfDisbursement();
					String dcy = beanObj.getDateOfCycle();
					double loan_amt = beanObj.getLoanAmount();
					double tenr = beanObj.getTenor();

					tempList = aayuCalculationUtil.calperprincrecovery(tempList, beanObj);
					beanObj.setLoanAmount(loan_amt);
					beanObj.setTenor(tenr);
					beanObj.setDateOfDisbursement(dds);
					beanObj.setDateOfCycle(dcy);

					if (beanObj.getintAmort().equals("Y") && beanObj.getRVvalue() == 0
							&& beanObj.getArrPerPrinRcvr() != null) {

						otArr = tempList;

						tempList = aayuCalculationUtil.Calculate_accrued_interest(beanObj);
						tempList.add(overviewbean);
						// tempList.add(AmortOverviewBean);
					}

				} else {
					tempList = aayuCalculationUtil.generateAmort(beanObj);

				}
				if (null != beanObj.getArrComp()) {
					ArrayList arrComp = beanObj.getArrComp();

					for (int j = 0; j < arrComp.size(); j++) {
						InternalAmortComponentBean icb = (InternalAmortComponentBean) arrComp.get(j);
						// icb.getToadd()
						if (icb.getcType().equals("C")) {
							valueCb = (-1) * icb.getVal();

						} else if (icb.getcType().equals("F")) {
							valueCb = icb.getVal();
						}
						tot_sum_fee = tot_sum_fee + valueCb;
					}

				}

				if (beanObj.getintAmort().equals("Y")) {// real_loanamount
					if (beanObj.getLoanAmount() == 0) {
						lnAmt = real_loanamount;

					} else {
						if (beanObj.getIntprincipal() == 0)
							lnAmt = beanObj.getLoanAmount();
						else if (beanObj.getIntprincipal() != 0)
							lnAmt = beanObj.getIntprincipal();
					}
					if (tot_sum_fee > 0 || tot_sum_fee == 0) {

						loanAmt = (lnAmt - (tot_sum_fee));

					}
					if (tot_sum_fee < 0 || tot_sum_fee == 0) {
						loanAmt = (lnAmt - (tot_sum_fee));
					}
					ArrayList tempList1 = new ArrayList();

					if (bpi < 0 || bpi > 0) {
						tempList1 = (ArrayList) tempList.clone();

						for (int cnt = 1; cnt < arr_last_date.size(); cnt++) {

							tempList1.remove(tempList.get(cnt));
						}

					}
					double IRR1 = XIRR.getIRR(tempList, beanObj.getInterestRate(), beanObj.getLoanAmount(),
							beanObj.getDateOfDisbursement());
					if (IRR1 < 0) {
						err.put("ERR_NEGATIVE_IRR", AmortConstant.AmortValidation.ERR_NEGATIVE_IRR);

					}
					beanObj.setInterestRate(IRR1 * beanObj.getRepaymentFrequency());

					if ((bpi > 0 || bpi < 0)) {
						beanObj.setInterestRate(actual_rate_value * 100);

					}

					beanObj.setInterest_Basis(beanObj.getintIB());

					if (beanObj.getIntprincipal() > 0 && loanAmt == beanObj.getLoanAmount()
							&& beanObj.getAmortMethod().equals("D")) {
						beanObj.setAmortMethod("S");

					}
					if (beanObj.getLoanAmount() != loanAmt) {
						if (beanObj.getIntprincipal() > 0 && loanAmt == beanObj.getLoanAmount()
								&& beanObj.getAmortMethod().equals("D")) {
							// err.put("ERR_AMORT_METHOD", AmortConstant.AmortValidation.ERR_AMORT_METHOD);

						}

						beanObj.setLoanAmount(beanObj.getLoanAmount());
						secondTime = true;
						tempListIntCust = aayuCalculationUtil.generateAmort(beanObj);
						double IRR2 = 0.0d;
						IRR2 = XIRR.getIRR(tempList, beanObj.getInterestRate(), loanAmt,
								beanObj.getDateOfDisbursement());
						if (IRR2 < 0) {
							err.put("ERR_NEGATIVE_IRR", AmortConstant.AmortValidation.ERR_NEGATIVE_IRR);

						}

						if (bpi < 0 || bpi > 0)
							IRR2 = XIRR.getIRR(tempList1, beanObj.getInterestRate(), loanAmt,
									beanObj.getDateOfDisbursement());

						temp_residual_glo = ((AmortOutputBean) otArr.get(1)).getRoundEMI();
						if (flagmultiple)
							temp_residual_glo = ((AmortOutputBean) otArr.get(arr_last_date.size())).getRoundEMI();

						beanObj.setInterestRate(IRR2 * beanObj.getRepaymentFrequency());
						beanObj.setLoanAmount(loanAmt);
						beanObj.setInterest_Basis(beanObj.getintIB());
						intAmort = true;
						tempListint = aayuCalculationUtil.generateAmort(beanObj);
					} else if (beanObj.getLoanAmount() == loanAmt) {
						tempListIntCust = tempList;
						tempListint = tempList;
					}
					if (otArr != null && otArr.size() >= 0) {
						for (int cnt = 0; cnt < otArr.size(); cnt++) {
							if (!(otArr.get(cnt) instanceof AmortOverviewBean)
									&& !(tempListIntCust.get(cnt) instanceof AmortOverviewBean)) {
								AmortOutputBean OutInt = (AmortOutputBean) tempListint.get(cnt);
								AmortOutputBean OutIntCust = (AmortOutputBean) tempListIntCust.get(cnt);
								if (null != beanObj.getArrComp()) {
									HashMap hmComponent = new HashMap();

									if (flagmultiple)
										hmComponent = aayuCalculationUtil.getComp(OutIntCust, OutInt, beanObj, cnt);
									else
										hmComponent = aayuCalculationUtil.getComp(OutIntCust, OutInt, beanObj);
									OutIntCust.setComp(hmComponent);

								}
							}
						}
					}

				}
				if (null != err && err.size() > 0) {
					errorsList.add(aayuCalculationUtil.generateErrors(errors));
					resultList.addAll(errorsList);
				} else {
					if (!beanObj.getintAmort().equals("Y")) {
						ListIterator listIterator = tempList.listIterator();
						int cnt = 0;
						if (isDebugEnabled)
							LOGGER.debug("Array List iteration Start");
						while (listIterator.hasNext()) {
							listIterator.next();
							Object obj = tempList.get(cnt);
							if (obj.getClass().getName().equals("com.ebixcash.aayu.util.AmortOverviewBean")) {
								continue;
							}
							if (isDebugEnabled)
								LOGGER.debug("Output Object 1 " + obj);

							amortInstallmentResponse = new AmortInstallmentResponse();
							amortOutputBeanJson = (AmortOutputBean) obj;

							amortInstallmentResponse.setInstallment(amortOutputBeanJson.getInstallment());
							amortInstallmentResponse.setCycleDate(amortOutputBeanJson.getCycleDate());
							amortInstallmentResponse.setRoundrestopenbal(amortOutputBeanJson.getRoundRestOpenBal());
							amortInstallmentResponse.setRoundopen(amortOutputBeanJson.getRoundOpen());
							amortInstallmentResponse.setRoundemi(amortOutputBeanJson.getRoundEMI());
							amortInstallmentResponse.setRoundinterest(amortOutputBeanJson.getRoundInterest());
							amortInstallmentResponse.setRoundprincipal(amortOutputBeanJson.getRoundPrincipal());
							amortInstallmentResponse.setRoundclose(amortOutputBeanJson.getRoundClose());
							amortInstallmentResponse.setAdj_installment(amortOutputBeanJson.getAdj_installment());
							amortInstallmentResponse
									.setSkipIntBeforePartial(amortOutputBeanJson.getSkipIntBeforePartial());
							amortInstallmentResponse.setSkipPartialIntOpen(amortOutputBeanJson.getSkipPartialIntOpen());
							amortInstallmentResponse.setSkipPartialInt(amortOutputBeanJson.getSkipPartialInt());
							amortInstallmentResponse
									.setSkipPartialIntonInt(amortOutputBeanJson.getSkipPartialIntonInt());
							amortInstallmentResponse
									.setSkipPartialIntClose(amortOutputBeanJson.getSkipPartialIntClose());

							// amortInstallmentResponses.add(amortInstallmentResponse);
							resultList.add(amortInstallmentResponse);

							// infoObj.put("NO", amortOutputBeanJson.getInstallment());

							if (isDebugEnabled)
								LOGGER.debug("Output Object 2 " + amortOutputBeanJson);
							cnt++;

							if (null != amortOutputBeanJson.getComponent()) {
								feedata = "";
								LinkedHashMap hm = (LinkedHashMap) amortOutputBeanJson.getComponent();
								Set HostKeys = hm.keySet();
								Iterator It = HostKeys.iterator();
								while (It.hasNext()) {
									String HostNow = (String) (It.next());
									double t1 = ((Double) hm.get(HostNow)).doubleValue();
									if (!HostNow.equals("Total")) {

										feedata = feedata + "<" + HostNow + ">" + t1 + "</" + HostNow + ">";

									}

								}
							}
						}

					}
					if (beanObj.getintAmort().equals("Y")) {

						ListIterator listIterator = tempListIntCust.listIterator();
						int cnt = 0;
						// output.append("<?xml version=\"1.0\"?>\n<root>");

						if (isDebugEnabled)
							LOGGER.debug("Array List iteration Start");
						while (listIterator.hasNext()) {
							listIterator.next();
							Object objInt = tempListint.get(cnt);
							Object objIntCust = tempListIntCust.get(cnt);
							Object obj = tempList.get(cnt);

							if (objIntCust.getClass().getName().equals("com.ebixcash.aayu.util.AmortOverviewBean")
									|| obj.getClass().getName().equals("com.ebixcash.aayu.util.AmortOverviewBean")
									|| objInt.getClass().getName().equals("com.ebixcash.aayu.util.AmortOverviewBean")) {
								continue;
							}
							if (isDebugEnabled)
								LOGGER.debug("Output Object 1 " + obj);
							AmortOutputBean Out = (AmortOutputBean) obj;
							AmortOutputBean OutInt = (AmortOutputBean) objInt;
							AmortOutputBean OutIntCust = (AmortOutputBean) objIntCust;
							if (isDebugEnabled)
								LOGGER.debug("Output Object 2 " + Out);
							cnt++;
							if (null != Out.getComponent()) {
								feedata = "";
								LinkedHashMap hm = (LinkedHashMap) Out.getComponent();
								Set HostKeys = hm.keySet();
								Iterator It = HostKeys.iterator();
								while (It.hasNext()) {
									String HostNow = (String) (It.next());
									double t1 = ((Double) hm.get(HostNow)).doubleValue();
									if (!HostNow.equals("Total")) {

										feedata = feedata + "<" + HostNow + ">" + t1 + "</" + HostNow + ">";

									}

								}
							}
							if (null != OutIntCust.getComp()) {
								compdata = "";
								LinkedHashMap hm = (LinkedHashMap) OutIntCust.getComp();
								Set HostKeys = hm.keySet();
								Iterator It = HostKeys.iterator();
								while (It.hasNext()) {
									String HostNow = (String) (It.next());
									double t2 = ((Double) hm.get(HostNow)).doubleValue();
									if (!HostNow.equals("Total")) {

										compdata = compdata + "<" + HostNow + ">" + t2 + "</" + HostNow + ">";
									}
								}
							}

							if (beanObj.getRVvalue() == 0) {
								if (Out.getAccruedInterest() == 0.0d && OutIntCust.getAccruedInterest() == 0.0d
										&& Out.getInstallment().equals("0"))
									output.append("");
								else
									amortInstallmentResponseFirstInnerCls = amortInstallmentResponse.new AmortInstallmentResponseFirstSce();
								amortOutputBeanJson = (AmortOutputBean) obj;

								amortInstallmentResponseFirstInnerCls.setInstallment(amortOutputBeanJson.getInstallment());
								amortInstallmentResponseFirstInnerCls.setCycleDate(amortOutputBeanJson.getCycleDate());
								amortInstallmentResponseFirstInnerCls
										.setRoundrestopenbal(amortOutputBeanJson.getRoundRestOpenBal());
								amortInstallmentResponseFirstInnerCls.setRoundopen(amortOutputBeanJson.getRoundOpen());
								amortInstallmentResponseFirstInnerCls.setRoundemi(amortOutputBeanJson.getRoundEMI());
								amortInstallmentResponseFirstInnerCls
										.setRoundinterest(amortOutputBeanJson.getRoundInterest());
								amortInstallmentResponseFirstInnerCls
										.setRoundprincipal(amortOutputBeanJson.getRoundPrincipal());
								amortInstallmentResponseFirstInnerCls.setRoundclose(amortOutputBeanJson.getRoundClose());
								amortInstallmentResponseFirstInnerCls
										.setAdj_installment(amortOutputBeanJson.getAdj_installment());
								amortInstallmentResponseFirstInnerCls
										.setAccruedinterest(amortOutputBeanJson.getAccruedInterest());
								amortInstallmentResponseFirstInnerCls
										.setMonthlyinterest(amortOutputBeanJson.getMonthlyInterest());
								amortInstallmentResponseFirstInnerCls
										.setIntopeningBalance(amortOutputBeanJson.getIntOpeningBalance());
								amortInstallmentResponseFirstInnerCls
										.setIntInterestEMI(amortOutputBeanJson.getIntInterestEMI());
								amortInstallmentResponseFirstInnerCls
										.setIntprincipalEMI(amortOutputBeanJson.getIntPrincipalEMI());
								amortInstallmentResponseFirstInnerCls
										.setIntclosingBalance(amortOutputBeanJson.getIntClosingBalance());
								amortInstallmentResponseFirstInnerCls
										.setIntaccruedinterest(amortOutputBeanJson.getIntAccruedInterest());
								amortInstallmentResponseFirstInnerCls
										.setIntmonthlyinterest(amortOutputBeanJson.getIntMonthlyInterest());
								
								resultList.add(amortInstallmentResponseFirstInnerCls);

//									output.append("<row><NO>" + Out.getInstallment() + "</NO><CYLDT>"
//											+ Out.getCycleDate() + "</CYLDT><RESTBAL>" + Out.getRoundRestOpenBal()
//											+ "</RESTBAL><OPBAL>" + Out.getRoundOpen() + "</OPBAL><EMI>"
//											+ Out.getRoundEMI() + "</EMI><INTEMI>" + Out.getRoundInterest()
//											+ "</INTEMI><PRIEMI>" + Out.getRoundPrincipal() + "</PRIEMI><CLSBAL>"
//											+ Out.getRoundClose() + "</CLSBAL>" + feedata + "<ADJEMI>"
//											+ Out.getAdj_installment() + "</ADJEMI><ACCI>" + Out.getAccruedInterest()
//											+ "</ACCI><MNTH_INT>" + Out.getMonthlyInterest() + "</MNTH_INT><I_OPBAL>"
//											+ OutIntCust.getIntOpeningBalance() + "</I_OPBAL><I_INTEMI>"
//											+ OutIntCust.getIntInterestEMI() + "</c><I_PRIEMI>"
//											+ OutIntCust.getIntPrincipalEMI() + "</I_PRIEMI><I_CLSBAL>"
//											+ OutIntCust.getIntClosingBalance() + "</I_CLSBAL>" + compdata + "<I_ACCI>"
//											+ OutIntCust.getIntAccruedInterest() + "</I_ACCI><I_MNTH_INT>"
//											+ OutIntCust.getIntMonthlyInterest() + "</I_MNTH_INT>" + "</row>");
							} else if (beanObj.getRVvalue() != 0) {
								if (OutInt.getIntAccruedInterest() == 0.0d && Out.getInstallment().equals("0"))
									output.append("");
								else
									amortInstallmentResponseSecondInnerCls = amortInstallmentResponse.new AmortInstallmentResponseSecondSce();
								amortOutputBeanJson = (AmortOutputBean) obj;

								amortInstallmentResponseSecondInnerCls.setInstallment(amortOutputBeanJson.getInstallment());
								amortInstallmentResponseSecondInnerCls.setCycleDate(amortOutputBeanJson.getCycleDate());
								amortInstallmentResponseSecondInnerCls.setRoundemi(amortOutputBeanJson.getRoundEMI());
								amortInstallmentResponseSecondInnerCls.setIntaccruedinterest(amortOutputBeanJson.getIntAccruedInterest());
								amortInstallmentResponseSecondInnerCls.setFinancialFee(amortOutputBeanJson.getFinancialFee());
								amortInstallmentResponseSecondInnerCls.setDepreciationFee(amortOutputBeanJson.getDepreciationFee());
								
								// amortInstallmentResponses.add(amortInstallmentResponse);
								resultList.add(amortInstallmentResponseSecondInnerCls);
//									output.append("<row><NO>" + Out.getInstallment() + "</NO><CYLDT>"
//											+ Out.getCycleDate() + "</CYLDT><EMI>" + Out.getRoundEMI() + "</EMI>"
//											+ compdata + "<I_ACCI>" + OutInt.getIntAccruedInterest()
//											+ "</I_ACCI><I_FIN>" + Out.getFinancialFee() + "</I_FIN><I_DEP>"
//											+ Out.getDepreciationFee() + "</I_DEP>" + "</row>");
							}

						}

					}
					output.append("</root>");
					if (isDebugEnabled)
						LOGGER.debug("Output JSON :" + output.toString());
					resultValues.put("Amort", output.toString());

				}
			} else {
				errorsList.add((ErrorMessages) resultValues.get("valErrors"));
				resultList.addAll(errorsList);
			}
			if (isDebugEnabled)
				LOGGER.debug("In getAmortObjecttoJSON()method...END.");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return resultList;

	}

}
