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
import com.ebixcash.aayu.exception.AmortException;
import com.ebixcash.aayu.irr.XIRR;
import com.ebixcash.aayu.model.AmortInputBean;
import com.ebixcash.aayu.model.AmortOutputBean;
import com.ebixcash.aayu.response.AmortOutputResponse;
import com.ebixcash.aayu.response.AmortStringOutputResponse;
import com.ebixcash.aayu.service.GenerateAmortAsStringService;
import com.ebixcash.aayu.util.AayuCalculationUtil;
import com.ebixcash.aayu.util.AmortOverviewBean;
import com.ebixcash.aayu.util.ComponentBean;
import com.ebixcash.aayu.util.InternalAmortComponentBean;

@Service
public class GenerateAmortAsStringServiceImpl implements GenerateAmortAsStringService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GenerateAmortAsStringServiceImpl.class);

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
	AmortOutputBean amortOutputBean;
	
	@Autowired
	AmortOutputResponse amortOutputResponse;

	@Autowired
	AayuCalculationUtil aayuCalculationUtil;

	@Autowired
	ErrorMessages errors;;

	@Override
	public ArrayList<Object> getAmortObjecttoStr(AmortInputBean amortInputBean) throws AmortException {
		if (isDebugEnabled)
			LOGGER.debug("In getAmortObjecttoStr() method");
		Map<String, Object> resultValues = new HashMap<>();
		ArrayList<Object> resultList = new ArrayList<>();
		StringBuffer outXML = new StringBuffer("");
		Map<String, Object> tempMap = new HashMap<>();
		double lnAmt = 0.0d;
		double valueCb = 0.0d;
		ArrayList tempListint = null;
		ArrayList tempListIntCust = null;
		ArrayList tempList = new ArrayList();

		String feeLabel = "";
		String compLabel = "";
		double actual_rate_value = 0.0d;
		
		ArrayList<AmortOutputBean> amortOutputBeans = new ArrayList<>();
		ArrayList<AmortStringOutputResponse> amortOutputresponses = new ArrayList<>();
		ArrayList<ErrorMessages> errorsList = new ArrayList<>();

		try {
			tempMap = aayuCalculationUtil.processInput(amortInputBean);
			// resultValues = Validations.getValidate(tempMap);
			resultValues = DefaultSettingValues.getDefaultValues(tempMap);
			String successFlag = (String) resultValues.get("successFlag");
			// String successFlag = "true";
			if ("true".equals(successFlag)) {
				HashMap<String, String> feedata = new HashMap<>();
				HashMap<String, String> compdata = new HashMap<>();
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

				// ArrayList tempXMLList = generateAmort(beanXMLObj);
				if (null != beanObj.getArrComp()) {
					ArrayList arrComp = beanObj.getArrComp();

					for (int j = 0; j < arrComp.size(); j++) {
						InternalAmortComponentBean icb = (InternalAmortComponentBean) arrComp.get(j);
						if (icb.getcType().equals("C")) {
							valueCb = (-1) * icb.getVal();

						} else if (icb.getcType().equals("F")) {
							valueCb = icb.getVal();
						}
						tot_sum_fee = tot_sum_fee + valueCb;
					}

				}

				if (beanObj.getintAmort().equals("Y")) {
					// real_loanamount
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
				if (null != err && err.size() > 0) {
					errorsList.add(aayuCalculationUtil.generateErrors(errors));
					resultList.addAll(errorsList);
					return resultList;
					// outXML.append(errors.toJsonString());
				} else {
					
					if (!beanObj.getintAmort().equals("Y")) {
						ListIterator listIterator = tempList.listIterator();
						int cnt = 0;
						//outXML.append("<?xml version=\"1.0\"?>\n<root>\n<str>");
						if (null != beanObj.getArrFees() && beanObj.getArrFees().size() > 0) {
							for (int f = 0; f < beanObj.getArrFees().size(); f++)
								feeLabel = feeLabel + ((ComponentBean) beanObj.getArrFees().get(f)).getFeename() + "|";
						}
						
						//List<AmortOutputResponse> outputBeans = new ArrayList<>();

						if (isDebugEnabled)
							LOGGER.debug("Array List iteration Start");
						while (listIterator.hasNext()) {
							AmortStringOutputResponse amortStringOutputResponse = new AmortStringOutputResponse();
							
							amortStringOutputResponse.setHeader("SRNO|CYLDT|RESTBAL|OPBAL|EMI|INTEMI|PRIEMI|CLSBAL|" 
									+ feeLabel  + "ADJEMI|TDS|INTBEFPARTSKIP|SKPINTOPEN|SKPINT|SKPINTONINT|SKPINTCLS\n");
							
							listIterator.next();
							Object obj = tempList.get(cnt);
							if (obj.getClass().getName().equals("com.ebixcash.aayu.util.AmortOverviewBean")) {
								continue;
							}
							if (isDebugEnabled)
								LOGGER.debug("Output Object 1 " + obj);
							AmortOutputBean Out = (AmortOutputBean) obj;
							if (isDebugEnabled)
								LOGGER.debug("Output Object 2 " + Out);
							cnt++;
							if (null != Out.getComponent()) {
								//feedata = "";
								LinkedHashMap hm = (LinkedHashMap) Out.getComponent();
								Set HostKeys = hm.keySet();
								Iterator It = HostKeys.iterator();
								while (It.hasNext()) {
									String HostNow = (String) (It.next());
									double t1 = ((Double) hm.get(HostNow)).doubleValue();
									if (!HostNow.equals("Total")) {
										feedata.put(HostNow,  t1 + "|");
									}
								}
							}

							
							/*
							 * outXML.append(Out.getInstallment() + "|" + Out.getCycleDate() + "|" +
							 * aayuCalculationUtil.convertToDecimal(Out.getRoundRestOpenBal()) + "|" +
							 * aayuCalculationUtil.convertToDecimal(Out.getRoundOpen()) + "|" +
							 * aayuCalculationUtil.convertToDecimal(Out.getRoundEMI()) + "|" +
							 * aayuCalculationUtil.convertToDecimal(Out.getRoundInterest()) + "|" +
							 * aayuCalculationUtil.convertToDecimal(Out.getRoundPrincipal()) + "|" +
							 * aayuCalculationUtil.convertToDecimal(Out.getRoundClose()) + "|" + feedata +
							 * aayuCalculationUtil.convertToDecimal(Out.getAdj_installment()) + "|" +
							 * aayuCalculationUtil.convertToDecimal(Out.getTDS()) + "|" +
							 * aayuCalculationUtil.convertToDecimal(Out.getSkipIntBeforePartial()) + "|" +
							 * aayuCalculationUtil.convertToDecimal(Out.getSkipPartialIntOpen()) + "|" +
							 * aayuCalculationUtil.convertToDecimal(Out.getSkipPartialInt()) + "|" +
							 * aayuCalculationUtil.convertToDecimal(Out.getSkipPartialIntonInt()) + "|" +
							 * aayuCalculationUtil.convertToDecimal(Out.getSkipPartialIntClose()) + "\n");
							 */
							 
							
							amortStringOutputResponse.setResponseData(Out.getInstallment() + "|" + Out.getCycleDate() + "|" +
									  aayuCalculationUtil.convertToDecimal(Out.getRoundRestOpenBal()) + "|" +
									  aayuCalculationUtil.convertToDecimal(Out.getRoundOpen()) + "|" +
									  aayuCalculationUtil.convertToDecimal(Out.getRoundEMI()) + "|" +
									  aayuCalculationUtil.convertToDecimal(Out.getRoundInterest()) + "|" +
									  aayuCalculationUtil.convertToDecimal(Out.getRoundPrincipal()) + "|" +
									  aayuCalculationUtil.convertToDecimal(Out.getRoundClose()) + "|" + feedata +
									  aayuCalculationUtil.convertToDecimal(Out.getAdj_installment()) + "|" +
									  aayuCalculationUtil.convertToDecimal(Out.getTDS()) + "|" +
									  aayuCalculationUtil.convertToDecimal(Out.getSkipIntBeforePartial()) + "|" +
									  aayuCalculationUtil.convertToDecimal(Out.getSkipPartialIntOpen()) + "|" +
									  aayuCalculationUtil.convertToDecimal(Out.getSkipPartialInt()) + "|" +
									  aayuCalculationUtil.convertToDecimal(Out.getSkipPartialIntonInt()) + "|" +
									  aayuCalculationUtil.convertToDecimal(Out.getSkipPartialIntClose()) + "\n");
							
								amortOutputresponses.add(amortStringOutputResponse);

						}
					}

					if (beanObj.getintAmort().equals("Y")) {
						
						ListIterator listIterator = tempListIntCust.listIterator();
						int cnt = 0;
						//outXML.append("<?xml version=\"1.0\"?>\n<root>\n<str>");
						
						if (null != beanObj.getArrFees() && beanObj.getArrFees().size() > 0) {
							for (int f = 0; f < beanObj.getArrFees().size(); f++)
								feeLabel = feeLabel + ((ComponentBean) beanObj.getArrFees().get(f)).getFeename() + "|";
						}
						if (null != beanObj.getArrComp() && beanObj.getArrComp().size() > 0) {
							for (int f = 0; f < beanObj.getArrComp().size(); f++)
								if (((InternalAmortComponentBean) beanObj.getArrComp().get(f)).getReqop().equals("Y"))
									compLabel = compLabel
											+ ((InternalAmortComponentBean) beanObj.getArrComp().get(f)).getName()
											+ "|";
						}

						if (isDebugEnabled)
							LOGGER.debug("Array List iteration Start");
						while (listIterator.hasNext()) {
							AmortStringOutputResponse amortStringOutputResponse = new AmortStringOutputResponse();
							if (beanObj.getRVvalue() == 0) {
								amortStringOutputResponse.setHeader("SRNO|CYLDT|RESTBAL|OPBAL|EMI|INTEMI|PRIEMI|CLSBAL|" + feeLabel
										  + "ADJEMI|TDS|ACCI|EOMDT|I_OPBAL|I_INTEMI|I_PRIEMI|I_CLSBAL|" + compLabel +
										  "I_ACCI" + "\n");
							} else if (beanObj.getRVvalue() != 0) {
								amortStringOutputResponse.setHeader("SRNO|CYLDT|EMI|EOMDT|" + compLabel + "I_ACCI|I_FIN|I_DEP" +
										  "\n");
							}
							
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
								//feedata = "";
								LinkedHashMap hm = (LinkedHashMap) Out.getComponent();
								Set HostKeys = hm.keySet();
								Iterator It = HostKeys.iterator();
								while (It.hasNext()) {
									String HostNow = (String) (It.next());
									double t1 = ((Double) hm.get(HostNow)).doubleValue();
									if (!HostNow.equals("Total")) {
										feedata.put(HostNow,  t1 + "|");
									}
								}
							}
							if (null != OutIntCust.getComp()) {
								//compdata = "";
								LinkedHashMap hm = (LinkedHashMap) OutIntCust.getComp();
								Set HostKeys = hm.keySet();
								Iterator It = HostKeys.iterator();
								while (It.hasNext()) {
									String HostNow = (String) (It.next());
									double t2 = ((Double) hm.get(HostNow)).doubleValue();
									if (!HostNow.equals("Total")) {
										//compdata = compdata + t2 + "|";
										compdata.put(HostNow, String.valueOf(t2) + "|");

									}
								}
							}
							if (beanObj.getRVvalue() == 0) {
								if (Out.getAccruedInterest() == 0.0d && OutIntCust.getAccruedInterest() == 0.0d
										&& Out.getInstallment().equals("0"))
									 outXML.append("");
								else
									amortStringOutputResponse.setResponseData(Out.getInstallment() + "|" + Out.getCycleDate() + "|"
											+ Out.getRoundRestOpenBal() + "|" + Out.getRoundOpen() + "|"
											+ Out.getRoundEMI() + "|" + Out.getRoundInterest() + "|"
											+ Out.getRoundPrincipal() + "|" + Out.getRoundClose() + "|" + feedata
											+ Out.getAdj_installment() + "|" + aayuCalculationUtil.convertToDecimal(Out.getTDS()) + "|"
											+ Out.getAccruedInterest() + "|" + Out.getEndofMonthDate() + "|"
											+ OutIntCust.getIntOpeningBalance() + "|" + OutIntCust.getIntInterestEMI()
											+ "|" + OutIntCust.getIntPrincipalEMI() + "|"
											+ OutIntCust.getIntClosingBalance() + "|" + compdata
											+ OutIntCust.getIntAccruedInterest() + "\n");
								/*
								 * outXML.append(Out.getInstallment() + "|" + Out.getCycleDate() + "|" +
								 * Out.getRoundRestOpenBal() + "|" + Out.getRoundOpen() + "|" +
								 * Out.getRoundEMI() + "|" + Out.getRoundInterest() + "|" +
								 * Out.getRoundPrincipal() + "|" + Out.getRoundClose() + "|" + feedata +
								 * Out.getAdj_installment() + "|" +
								 * aayuCalculationUtil.convertToDecimal(Out.getTDS()) + "|" +
								 * Out.getAccruedInterest() + "|" + Out.getEndofMonthDate() + "|" +
								 * OutIntCust.getIntOpeningBalance() + "|" + OutIntCust.getIntInterestEMI() +
								 * "|" + OutIntCust.getIntPrincipalEMI() + "|" +
								 * OutIntCust.getIntClosingBalance() + "|" + compdata +
								 * OutIntCust.getIntAccruedInterest() + "\n");
								 */
							} else if (beanObj.getRVvalue() != 0) {
								if (OutInt.getIntAccruedInterest() == 0.0d && Out.getInstallment().equals("0"))
									outXML.append("");
								else
									amortStringOutputResponse.setResponseData(Out.getInstallment() + "|" + Out.getCycleDate() + "|"
											+ Out.getEmiAmount() + "|" + Out.getEndofMonthDate() + "|" + compdata
											+ OutInt.getIntAccruedInterest() + "|" + Out.getFinancialFee() + "|"
											+ Out.getDepreciationFee() + "\n");
								/*
								 * outXML.append(Out.getInstallment() + "|" + Out.getCycleDate() + "|" +
								 * Out.getEmiAmount() + "|" + Out.getEndofMonthDate() + "|" + compdata +
								 * OutInt.getIntAccruedInterest() + "|" + Out.getFinancialFee() + "|" +
								 * Out.getDepreciationFee() + "\n");
								 */
							}
							amortOutputresponses.add(amortStringOutputResponse);
						}

					}
					
					// outXML.append("</root>");
					if (isDebugEnabled)
						LOGGER.debug("Output Json :" + outXML.toString());
				}
			} else {
				if (LOGGER.isInfoEnabled())
					LOGGER.info("getAmortObjecttoXMLStr :Errors found during validation. Errors XML="
							+ ((ErrorMessages) resultValues.get("valErrors")).toJsonString());

				// outXML.append(((ErrorMessages)
				// resultValues.get("valErrors")).toJsonString());
				errorsList.add((ErrorMessages) resultValues.get("valErrors"));
				resultList.addAll(errorsList);
				return resultList;
			}
			if (isDebugEnabled)
				LOGGER.debug("In getAmortObjecttoXML()method...END.");
			AmortConstant.isWeeklyflag = false;
			AmortConstant.isDaily = false;
		} catch (Exception exception) {
			if (isDebugEnabled)
				LOGGER.error("Method : getAmortObjecttoXML() : Exception thrown " + exception.toString());
			throw new AmortException.CalculationFactoryException(exception);
		}

		resultList.addAll(amortOutputresponses);
		
		return resultList;
	}

}
