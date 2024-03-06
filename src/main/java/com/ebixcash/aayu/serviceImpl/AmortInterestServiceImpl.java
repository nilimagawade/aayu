package com.ebixcash.aayu.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebixcash.aayu.constant.AmortConstant;
import com.ebixcash.aayu.customvalidator.ErrorMessages;
import com.ebixcash.aayu.defaultsetting.DefaultSettingValues;
import com.ebixcash.aayu.exception.AmortException;
import com.ebixcash.aayu.model.AmortInputBean;
import com.ebixcash.aayu.response.AmortIntrestResponse;
import com.ebixcash.aayu.service.AmortInterestService;
import com.ebixcash.aayu.util.AayuCalculationUtil;
import com.ebixcash.aayu.util.AmortUtil;

@Service
public class AmortInterestServiceImpl implements AmortInterestService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AmortInterestServiceImpl.class);

	public static final boolean isDebugEnabled = LOGGER.isDebugEnabled();

	@Autowired(required = true)
	AayuCalculationUtil aayuCalculationUtil;

	@Autowired
	ErrorMessages errors;

	@Override
	public ArrayList<Object> getAmortIntrest(AmortInputBean amortInputBean) throws AmortException {

		double bpi = 0.0d;
		HashMap err = new HashMap();
		Map<String, Object> tempMap = new HashMap<>();
		Map<String, Object> resultValues = new HashMap<>();
		ErrorMessages errors = new ErrorMessages();
		AmortIntrestResponse amortIntrestResponse = new AmortIntrestResponse();
		ArrayList<Object> resultList = new ArrayList<>();
		ArrayList<ErrorMessages> errorsList = new ArrayList<>();
		try {
			tempMap = aayuCalculationUtil.processInput(amortInputBean);
			resultValues = DefaultSettingValues.getDefaultValues(tempMap);
			String successFlag = (String) resultValues.get("successFlag");
			if ("true".equals(successFlag)) {
				AmortInputBean beanObj = aayuCalculationUtil.InitializeInputModel(tempMap);
				if (null != err && err.size() > 0) {
					errorsList.add(aayuCalculationUtil.generateErrors(errors));
					resultList.addAll(errorsList);
				} else {
					if (bpi < 0
							&& (beanObj.getInterest_Basis() == AmortConstant.IntBasis_30360
									&& beanObj.getInterest_Basis_emi() == AmortConstant.IntBasis_30360)
							&& beanObj.isEOMAmort() == false) {
						beanObj.setInterest_Basis(3);
					}
					if (AmortUtil.compare2Dates(beanObj.getDtstart(), beanObj.getDtend(),
							beanObj.getDateformat()) == 1) {
						err.put("ERR_Date", "End Date should be greater than Start Date");
					}
					double interest = AmortUtil.calculateInterest(beanObj);

					interest = AmortUtil.round(interest, beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(),
							beanObj.getOthers_unit_ro());
					amortIntrestResponse.setAmortIntrest(interest);
					resultList.add(amortIntrestResponse);
				}
			} else {
				errorsList.add((ErrorMessages) resultValues.get("valErrors"));
				resultList.addAll(errorsList);
			}

		} catch (Exception exception) {
			LOGGER.error("Exception Raised in method getAmortIntrest() : Exception thrown ", exception);
		}
		return resultList;
	}

}
