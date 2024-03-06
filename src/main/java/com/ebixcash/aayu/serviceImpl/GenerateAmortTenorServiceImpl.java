package com.ebixcash.aayu.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebixcash.aayu.customvalidator.ErrorMessages;
import com.ebixcash.aayu.defaultsetting.DefaultSettingValues;
import com.ebixcash.aayu.exception.AmortException;
import com.ebixcash.aayu.model.AmortInputBean;
import com.ebixcash.aayu.response.AmortTenorResponse;
import com.ebixcash.aayu.service.GenerateAmortTenorService;
import com.ebixcash.aayu.util.AayuCalculationUtil;

@Service
public class GenerateAmortTenorServiceImpl implements GenerateAmortTenorService {

	@Autowired(required = true)
	AayuCalculationUtil aayuCalculationUtil;

	@Autowired
	ErrorMessages errors;

	@Override
	public ArrayList<Object> getTenore(AmortInputBean request) throws AmortException {
		HashMap err = new HashMap();
		ArrayList<Object> resultList = new ArrayList<>();
		Map<String, Object> tempMap = new HashMap<>();
		Map<String, Object> resultValues = new HashMap<>();
		ArrayList<ErrorMessages> errorsList = new ArrayList<>();
		AmortTenorResponse amortTenorResponse = new AmortTenorResponse();
		try {
			tempMap = aayuCalculationUtil.processInput(request);
			resultValues = DefaultSettingValues.getDefaultValues(tempMap);
			String successFlag = (String) resultValues.get("successFlag");
			if ("true".equals(successFlag)) {
				AmortInputBean beanObj = aayuCalculationUtil.InitializeInputModel(tempMap);
				aayuCalculationUtil.generateAmort(beanObj);

				if (null != err && err.size() > 0) {
					errorsList.add(aayuCalculationUtil.generateErrors(errors));
					resultList.addAll(errorsList);
				} else {
					amortTenorResponse.setT(beanObj.getTenor());
					amortTenorResponse.setReal_lid(beanObj.getReal_lid());
					resultList.add(amortTenorResponse);
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
