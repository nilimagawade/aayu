 package com.ebixcash.aayu.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebixcash.aayu.customvalidator.ErrorMessages;
import com.ebixcash.aayu.defaultsetting.DefaultSettingValues;
import com.ebixcash.aayu.exception.AmortException;
import com.ebixcash.aayu.model.AmortInputBean;
import com.ebixcash.aayu.model.AmortOutputBean;
import com.ebixcash.aayu.response.AmortLoanAmountResponse;
import com.ebixcash.aayu.service.GenerateLoanAmountService;
import com.ebixcash.aayu.util.AayuCalculationUtil;
import com.ebixcash.aayu.util.AmortUtil;

@Service
public class GenerateLoanAmountServiceImpl implements GenerateLoanAmountService {

	private static final Logger LOGGER = LoggerFactory.getLogger(GenerateAmortServiceImpl.class);

	public static final boolean isDebugEnabled = LOGGER.isDebugEnabled();
	@Autowired(required = true)
	AayuCalculationUtil aayuCalculationUtil;

	@Autowired
	ErrorMessages errors;

	public ArrayList<Object> getAmortLoanamount(AmortInputBean amortInputBean) throws AmortException {
		if (isDebugEnabled)
			LOGGER.debug("In getAmortLoanamount() method");
		double real_loanamount = 0.0d;
		HashMap err = new HashMap();
		Map<String, Object> resultValues = new HashMap<>();
		StringBuffer output = new StringBuffer("");
		Map<String, Object> tempMap = new HashMap<>();
		ErrorMessages errors = new ErrorMessages();
		AmortOutputBean amortOutputBean = new AmortOutputBean();
		ArrayList<ErrorMessages> errorsList = new ArrayList<>();
		AmortLoanAmountResponse amortLoanAmountResponse = new AmortLoanAmountResponse();
		ArrayList<Object> resultList = new ArrayList<>();

		try {
			tempMap = aayuCalculationUtil.processInput(amortInputBean);
			resultValues = DefaultSettingValues.getDefaultValues(tempMap);
			String successFlag = (String) resultValues.get("successFlag");
			if ("true".equals(successFlag)) {
				AmortInputBean beanObj = aayuCalculationUtil.InitializeInputModel(tempMap);
				ArrayList tempList = aayuCalculationUtil.generateAmort(beanObj);
				real_loanamount = beanObj.getLoanAmount() + beanObj.getNoOfAdvEMI() * beanObj.getInput_emi();
				real_loanamount = AmortUtil.round(real_loanamount, beanObj.getOthers_ro_part(),
						beanObj.getOthers_ro_to(), beanObj.getOthers_unit_ro());
				if (null != err && err.size() > 0) {
					errorsList.add(aayuCalculationUtil.generateErrors(errors));
					resultList.addAll(errorsList);
				} else {
					amortOutputBean.setPrincipalEMI(real_loanamount);
					amortLoanAmountResponse.setP(real_loanamount);
					resultList.add(amortLoanAmountResponse);
				}
			} else {
				errorsList.add((ErrorMessages) resultValues.get("valErrors"));
				resultList.addAll(errorsList);
			}
		} catch (Exception exception) {
			if (isDebugEnabled)
				LOGGER.error("Method : getAmortLoanamount() : Exception thrown " + exception.toString());
			throw new AmortException.CalculationFactoryException(exception);
		}
		return resultList;
	}

}
