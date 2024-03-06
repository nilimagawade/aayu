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
import com.ebixcash.aayu.service.GenerateAmortService;
import com.ebixcash.aayu.util.AayuCalculationUtil;
import com.ebixcash.aayu.util.AmortOverviewBean;
import com.ebixcash.aayu.util.InternalAmortComponentBean;

@Service
public class GenerateAmortServiceImpl implements GenerateAmortService {

	private static final Logger LOGGER = LoggerFactory.getLogger(GenerateAmortServiceImpl.class);

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

	public ArrayList<Object> getAmortObjecttoJsonStr(AmortInputBean amortInputBean) throws AmortException {
		if (isDebugEnabled)
			LOGGER.debug("In getAmortObjecttoXMLStr() method");
		Map<String, Object> resultValues = new HashMap<>();
		ArrayList<Object> resultList = new ArrayList<>();
		StringBuffer outXML = new StringBuffer("");
		Map<String, Object> tempMap = new HashMap<>();
		double lnAmt = 0.0d;
		double valueCb = 0.0d;
		ArrayList tempListint = null;
		ArrayList tempListIntCust = null;
		ArrayList tempList = new ArrayList();

		double actual_rate_value = 0.0d;
		ArrayList<AmortOutputBean> amortOutputBeans = new ArrayList<>();
		ArrayList<Map<String, Object>> amortOutputresponses = new ArrayList<>();
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
						// feelabel="";
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
							//if (isDebugEnabled)
								//LOGGER.debug("Output Object 1 " + obj);
								System.out.println("Output Object 1 " + obj);
							amortOutputBean = (AmortOutputBean) obj;
							//if (isDebugEnabled)
								//LOGGER.debug("Output Object 2 " + amortOutputBean);
								System.out.println("Output Object 2 " + amortOutputBean);
							cnt++;

							if (null != amortOutputBean.getComponent()) {
								LinkedHashMap hm = (LinkedHashMap) amortOutputBean.getComponent();
								Set HostKeys = hm.keySet();
								Iterator It = HostKeys.iterator();
								while (It.hasNext()) {
									String HostNow = (String) (It.next());
									double t1 = ((Double) hm.get(HostNow)).doubleValue();
									if (!HostNow.equals("Total")) {
										feedata.put(HostNow, String.valueOf(t1));
									}
								}
								amortOutputBean.setComponent(feedata);
							}
							// outXML.append("<row><NO>"+Out.getInstallment()+"</NO><CYLDT>"+Out.getCycleDate()+"</CYLDT><RESTBAL>"+convertToDecimal(Out.getRoundRestOpenBal())+"</RESTBAL><OPBAL>"+convertToDecimal(Out.getRoundOpen())+"</OPBAL><EMI>"+convertToDecimal(Out.getRoundEMI())+"</EMI><INTEMI>"+convertToDecimal(Out.getRoundInterest())+"</INTEMI><PRIEMI>"+convertToDecimal(Out.getRoundPrincipal())+"</PRIEMI><CLSBAL>"+convertToDecimal(Out.getRoundClose())+"</CLSBAL>"+feedata+"<ADJEMI>"+convertToDecimal(Out.getAdj_installment())+"</ADJEMI>"+"</row>");
							/*
							 * outXML.append("<row><NO>" + Out.getInstallment() + "</NO><CYLDT>" +
							 * Out.getCycleDate() + "</CYLDT><RESTBAL>" +
							 * convertToDecimal(Out.getRoundRestOpenBal()) + "</RESTBAL><OPBAL>" +
							 * convertToDecimal(Out.getRoundOpen()) + "</OPBAL><EMI>" +
							 * convertToDecimal(Out.getRoundEMI()) + "</EMI><INTEMI>" +
							 * convertToDecimal(Out.getRoundInterest()) + "</INTEMI><PRIEMI>" +
							 * convertToDecimal(Out.getRoundPrincipal()) + "</PRIEMI><CLSBAL>" +
							 * convertToDecimal(Out.getRoundClose()) + "</CLSBAL>" + feedata + "<ADJEMI>" +
							 * convertToDecimal(Out.getAdj_installment()) + "</ADJEMI>" + "<INTBEFPARTSKIP>"
							 * + convertToDecimal(Out.getSkipIntBeforePartial()) + "</INTBEFPARTSKIP>" +
							 * "<SKPINTOPEN>" + convertToDecimal(Out.getSkipPartialIntOpen()) +
							 * "</SKPINTOPEN>" + "<SKPINT>" + convertToDecimal(Out.getSkipPartialInt()) +
							 * "</SKPINT>" + "<SKPINTONINT>" +
							 * convertToDecimal(Out.getSkipPartialIntonInt()) + "</SKPINTONINT>" +
							 * "<SKPINTCLS>" + convertToDecimal(Out.getSkipPartialIntClose()) +
							 * "</SKPINTCLS>" + "</row>");
							 */
							// outResult.put(String.valueOf(cnt), Out);
							amortOutputBeans.add(amortOutputBean);
						}
					}

					if (beanObj.getintAmort().equals("Y")) {

						ListIterator listIterator = tempListIntCust.listIterator();
						int cnt = 0;
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
							//if (isDebugEnabled)
								//LOGGER.debug("Output Object 3 " + obj);
							System.out.println("Output Object 3 " + obj);
							amortOutputBean = (AmortOutputBean) obj;
							AmortOutputBean OutInt = (AmortOutputBean) objInt;
							AmortOutputBean OutIntCust = (AmortOutputBean) objIntCust;
							//if (isDebugEnabled)
								//LOGGER.debug("Output Object 4 " + amortOutputBean);
							System.out.println("Output Object 4 " + amortOutputBean);
							cnt++;
							if (null != amortOutputBean.getComponent()) {
								LinkedHashMap hm = (LinkedHashMap) amortOutputBean.getComponent();
								Set HostKeys = hm.keySet();
								Iterator It = HostKeys.iterator();
								while (It.hasNext()) {
									String HostNow = (String) (It.next());
									double t1 = ((Double) hm.get(HostNow)).doubleValue();
									if (!HostNow.equals("Total")) {
										feedata.put(HostNow, String.valueOf(t1));
									}
								}
								amortOutputBean.setComponent(feedata);
								amortOutputBeans.add(amortOutputBean);
							}
							if (null != OutIntCust.getComp()) {
								LinkedHashMap hm = (LinkedHashMap) OutIntCust.getComp();
								Set HostKeys = hm.keySet();
								Iterator It = HostKeys.iterator();
								while (It.hasNext()) {
									String HostNow = (String) (It.next());
									double t2 = ((Double) hm.get(HostNow)).doubleValue();
									if (!HostNow.equals("Total")) {
										compdata.put(HostNow, String.valueOf(t2));
									}
								}
								OutIntCust.setComponent(compdata);
								amortOutputBeans.add(OutIntCust);
							}
							if (beanObj.getRVvalue() == 0) {
								if (amortOutputBean.getAccruedInterest() == 0.0d
										&& OutIntCust.getAccruedInterest() == 0.0d
										&& amortOutputBean.getInstallment().equals("0"))
									outXML.append("");
								else {
									amortOutputBean.setInstallment(amortOutputBean.getInstallment());
									amortOutputBean.setCycleDate(amortOutputBean.getCycleDate());
									amortOutputBean.setRestOpeningBalance(amortOutputBean.getRestOpeningBalance());
									amortOutputBean.setRoundOpen(amortOutputBean.getRoundOpen());
									amortOutputBean.setRoundEMI(amortOutputBean.getRoundEMI());
									amortOutputBean.setRoundInterest(amortOutputBean.getRoundInterest());
									amortOutputBean.setRoundPrincipal(amortOutputBean.getRoundPrincipal());
									amortOutputBean.setClosingBalance(amortOutputBean.getClosingBalance());
									amortOutputBean.setAdj_installment(amortOutputBean.getAdj_installment());
									amortOutputBean.setEndofMonthDate(amortOutputBean.getEndofMonthDate());
									amortOutputBean.setAccruedInterest(amortOutputBean.getAccruedInterest());
									amortOutputBean.setIntOpeningBalance(amortOutputBean.getIntOpeningBalance());
									amortOutputBean.setInterestEMI(amortOutputBean.getInterestEMI());
									amortOutputBean.setIntPrincipalEMI(amortOutputBean.getIntPrincipalEMI());
									amortOutputBean.setClosingBalance(amortOutputBean.getClosingBalance());
									amortOutputBean.setIntAccruedInterest(amortOutputBean.getIntAccruedInterest());
								}

							} else if (beanObj.getRVvalue() != 0) {
								if (OutInt.getIntAccruedInterest() == 0.0d
										&& amortOutputBean.getInstallment().equals("0"))
									outXML.append("");
								else {
									
									amortOutputBean.setInstallment(amortOutputBean.getInstallment());
									amortOutputBean.setCycleDate(amortOutputBean.getCycleDate());
									amortOutputBean.setEmiAmount(amortOutputBean.getEmiAmount());
									amortOutputBean.setEndofMonthDate(amortOutputBean.getEndofMonthDate());
									amortOutputBean.setComp(amortOutputBean.getComp());
									amortOutputBean.setAccruedInterest(amortOutputBean.getAccruedInterest());
									amortOutputBean.setFinancialFee(amortOutputBean.getFinancialFee());
									amortOutputBean.setDepreciationFee(amortOutputBean.getDepreciationFee());
								}
							}
							// outResult.put(String.valueOf(cnt), OutIntCust);
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
		
		for (AmortOutputBean amortOutputBean : amortOutputBeans) {
		    AmortOutputResponse amortOutputResponse = new AmortOutputResponse();
		    
		    // Check if the value from amortOutputBean is not null before setting it in amortOutputResponse
		    if (amortOutputBean.getCycleDate() != null)
		        amortOutputResponse.setCycleDate(amortOutputBean.getCycleDate());
		    if (amortOutputBean.getRoundEMI() != 0.0)
		        amortOutputResponse.setRoundemi(amortOutputBean.getRoundEMI());
		    if (amortOutputBean.getRoundPrincipal() != 0.0)
		        amortOutputResponse.setRoundprincipal(amortOutputBean.getRoundPrincipal());
		    if (amortOutputBean.getRoundInterest() != 0.0)
		        amortOutputResponse.setRoundinterest(amortOutputBean.getRoundInterest());
		    if (amortOutputBean.getRoundOpen() != 0.0)
		        amortOutputResponse.setRoundopen(amortOutputBean.getRoundOpen());
		    if (amortOutputBean.getRoundClose() != 0.0)
		        amortOutputResponse.setRoundclose(amortOutputBean.getRoundClose());
		    if (amortOutputBean.getRoundRestOpenBal() != 0.0)
		        amortOutputResponse.setRoundrestopenbal(amortOutputBean.getRoundRestOpenBal());
		    if (amortOutputBean.getSkipPartialInt() != 0.0)
		        amortOutputResponse.setSkipPartialInt(amortOutputBean.getSkipPartialInt());
		    if (amortOutputBean.getSkipPartialIntonInt() != 0.0)
		        amortOutputResponse.setSkipPartialIntonInt(amortOutputBean.getSkipPartialIntonInt());
		    if (amortOutputBean.getSkipPartialIntOpen() != 0.0)
		        amortOutputResponse.setSkipPartialIntOpen(amortOutputBean.getSkipPartialIntOpen());
		    if (amortOutputBean.getSkipPartialIntClose() != 0.0)
		        amortOutputResponse.setSkipPartialIntClose(amortOutputBean.getSkipPartialIntClose());
		    if (amortOutputBean.getSkipIntBeforePartial() != 0.0)
		        amortOutputResponse.setSkipIntBeforePartial(amortOutputBean.getSkipIntBeforePartial());
		    if (amortOutputBean.getInstallment() != null)
		        amortOutputResponse.setInstallment(amortOutputBean.getInstallment());
		    if (amortOutputBean.getComponent() != null)
		        amortOutputResponse.setComponent(amortOutputBean.getComponent());
		    if (amortOutputBean.getComp() != null)
		        amortOutputResponse.setComp(amortOutputBean.getComp());
		    if (amortOutputBean.getAdj_installment() != 0.0)
		        amortOutputResponse.setAdj_installment(amortOutputBean.getAdj_installment());
		    if (amortOutputBean.getAccruedInterest() != 0.0)
		        amortOutputResponse.setAccruedinterest(amortOutputBean.getAccruedInterest());
		    if (amortOutputBean.getEndofMonthDate() != null)
		        amortOutputResponse.setEndofMonthDate(amortOutputBean.getEndofMonthDate());
		    if (amortOutputBean.getIntAccruedInterest() != 0.0)
		        amortOutputResponse.setIntaccruedinterest(amortOutputBean.getIntAccruedInterest());
		    if (amortOutputBean.getIntOpeningBalance() != 0.0)
		        amortOutputResponse.setIntopeningBalance(amortOutputBean.getIntOpeningBalance());
		    if (amortOutputBean.getIntInterestEMI() != 0.0)
		        amortOutputResponse.setIntInterestEMI(amortOutputBean.getIntInterestEMI());
		    if (amortOutputBean.getIntPrincipalEMI() != 0.0)
		        amortOutputResponse.setIntprincipalEMI(amortOutputBean.getIntPrincipalEMI());
		    if (amortOutputBean.getIntClosingBalance() != 0.0)
		        amortOutputResponse.setIntclosingBalance(amortOutputBean.getIntClosingBalance());
		    if (amortOutputBean.getFinancialFee() != 0.0)
		        amortOutputResponse.setFinancialFee(amortOutputBean.getFinancialFee());
		    if (amortOutputBean.getDepreciationFee() != 0.0)
		        amortOutputResponse.setDepreciationFee(amortOutputBean.getDepreciationFee());
		    
		    Map<String, Object> AmortDTOResponse = amortOutputResponse.toResponse();
		    amortOutputresponses.add(AmortDTOResponse);
		}

		resultList.addAll(amortOutputresponses);
		
		return resultList;

		// return amortOutputBeans;
	}

}
