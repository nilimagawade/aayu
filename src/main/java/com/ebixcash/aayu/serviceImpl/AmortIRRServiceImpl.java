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
import com.ebixcash.aayu.exception.AmortException.CalculationFactoryException;
import com.ebixcash.aayu.irr.XIRR;
import com.ebixcash.aayu.model.AmortInputBean;
import com.ebixcash.aayu.response.AmortIRRResponse;
import com.ebixcash.aayu.service.AmortIRRService;
import com.ebixcash.aayu.util.AayuCalculationUtil;

@Service
public class AmortIRRServiceImpl implements AmortIRRService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AmortIRRServiceImpl.class);

	public static final boolean isDebugEnabled = LOGGER.isDebugEnabled();

	@Autowired(required = true)
	AayuCalculationUtil aayuCalculationUtil;

	@Override
	public ArrayList<Object> getAmortIRR(AmortInputBean request) throws CalculationFactoryException {
		// TODO Auto-generated method stub
		StringBuffer outXML = new StringBuffer("");
		Map<String, Object> tempMap = new HashMap<>();
		ArrayList<Object> resultList = new ArrayList<>();
		Map<String, Object> resultValues = new HashMap<>();
		ErrorMessages errors = new ErrorMessages();
		HashMap err = new HashMap();
		AmortIRRResponse amortIRRResponse = new AmortIRRResponse();
		ArrayList<ErrorMessages> errorsList = new ArrayList<>();

		try {
			tempMap = aayuCalculationUtil.processInput(request);
			resultValues = DefaultSettingValues.getDefaultValues(tempMap);
			String successFlag = (String) resultValues.get("successFlag");

			if ("true".equals(successFlag)) {

				AmortInputBean beanObj = aayuCalculationUtil.InitializeInputModel(tempMap);
				ArrayList tempList = aayuCalculationUtil.generateAmort(beanObj);
				if (null != err && err.size() > 0) {
					errorsList.add(aayuCalculationUtil.generateErrors(errors));
					resultList.addAll(errorsList);
				} else {
					double IRR = XIRR.getIRR(tempList, beanObj.getInterestRate(), beanObj.getLoanAmount(),
							beanObj.getDateOfDisbursement());
					double IRRX = XIRR.getXIRR(tempList, beanObj.getInterestRate(), beanObj.getLoanAmount(),
							beanObj.getDateOfDisbursement());

					amortIRRResponse.setiRR(IRR);
					amortIRRResponse.setxIRR(IRRX);
					resultList.add(amortIRRResponse);
				}
			} else {
				errorsList.add((ErrorMessages) resultValues.get("valErrors"));
				resultList.addAll(errorsList);

			}
			// if (isDebugEnabled)
			// LOGGER.debug("In getAmortIRR()method...END.");

		} catch (Exception e) {
			throw new AmortException.CalculationFactoryException(e);
		}

		return resultList;
	}

}
