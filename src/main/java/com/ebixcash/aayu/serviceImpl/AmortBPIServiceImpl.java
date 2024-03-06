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
import com.ebixcash.aayu.response.AmortBPIResponse;
import com.ebixcash.aayu.service.AmortBPIService;
import com.ebixcash.aayu.util.AayuCalculationUtil;
import com.ebixcash.aayu.util.AmortUtil;

@Service
public class AmortBPIServiceImpl implements AmortBPIService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AmortBPIServiceImpl.class);

	public static final boolean isDebugEnabled = LOGGER.isDebugEnabled();

	@Autowired(required = true)
	AayuCalculationUtil aayuCalculationUtil;

	@Autowired
	ErrorMessages errors;

	@Override
	public ArrayList<Object> amortBPI(AmortInputBean request) throws AmortException {
		Map<String, Object> tempMap = new HashMap<>();
		ArrayList<Object> resultList = new ArrayList<>();
		Map<String, Object> resultValues = new HashMap<>();
		ErrorMessages errors = new ErrorMessages();
		HashMap err = new HashMap();
		AmortBPIResponse amortBPIResponse = new AmortBPIResponse();
		ArrayList<ErrorMessages> errorsList = new ArrayList<>();
		String bpiCalculated = "N";
		double bpi = 0.0d;

		try {
			tempMap = aayuCalculationUtil.processInput(request);
			resultValues = DefaultSettingValues.getDefaultValues(tempMap);
			String successFlag = (String) resultValues.get("successFlag");
			//successFlag = "true";
			if ("true".equals(successFlag)) {
				AmortInputBean beanObj = aayuCalculationUtil.InitializeInputModel(tempMap);
				ArrayList tempXMLList = aayuCalculationUtil.generateAmort(beanObj);
				if ("N".equalsIgnoreCase(bpiCalculated)) {
					bpi = AmortUtil.calculateBPI(beanObj, beanObj.getLoanAmount());
				}
				bpi = AmortUtil.round(bpi, beanObj.getOthers_ro_part(), beanObj.getOthers_ro_to(),
						beanObj.getOthers_unit_ro());
				if (bpi < 0) {
					bpi = -1 * bpi;
				}

				if (null != err && err.size() > 0) {
					errorsList.add(aayuCalculationUtil.generateErrors(errors));
					resultList.addAll(errorsList);
				} else {
					amortBPIResponse.setBpi(bpi);
					resultList.add(amortBPIResponse);

				}
			} else {
				errorsList.add((ErrorMessages) resultValues.get("valErrors"));
				resultList.addAll(errorsList);

			}

		} catch (AmortException e) {
			throw new AmortException.CalculationFactoryException(e);
		}
		return resultList;
	}

}
