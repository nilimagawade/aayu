package com.ebixcash.aayu.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebixcash.aayu.constant.AmortConstant;
import com.ebixcash.aayu.controller.AmortNPVController;
import com.ebixcash.aayu.customvalidator.ErrorMessages;
import com.ebixcash.aayu.defaultsetting.DefaultSettingValues;
import com.ebixcash.aayu.model.AmortInputBean;
import com.ebixcash.aayu.response.AmortNPVResponse;
import com.ebixcash.aayu.response.TotalIntrestRespose;
import com.ebixcash.aayu.service.AmortNPVService;
import com.ebixcash.aayu.util.AayuCalculationUtil;
import com.ebixcash.aayu.util.AmortUtil;

@Service
public class AmortNPVServiceImpl implements AmortNPVService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AmortNPVServiceImpl.class);

	@Autowired
	AayuCalculationUtil aayuCalculationUtil;

	@Autowired
	ErrorMessages errors;

	@Override
	public ArrayList<Object> getAmortNPV(AmortInputBean request) {
		double npvValue = 0.0d;
		ArrayList<Object> resultList = new ArrayList<>();
		HashMap err = new HashMap();
		Map<String, Object> tempMap = new HashMap<>();
		TotalIntrestRespose intrestRespose = new TotalIntrestRespose();
		Map<String, Object> resultValues = new HashMap<>();
		AmortNPVResponse amortNPVResponse = new AmortNPVResponse();
		ArrayList<ErrorMessages> errorsList = new ArrayList<>();
		try {
			tempMap = aayuCalculationUtil.processInput(request);
			resultValues = DefaultSettingValues.getDefaultValues(tempMap);
			String successFlag = (String) resultValues.get("successFlag");
			if ("true".equals(successFlag)) {
				AmortInputBean beanObj = aayuCalculationUtil.InitializeInputModel(tempMap);
				int freq = beanObj.getRepaymentFrequency();
				if (freq == 12 || freq == 6 || freq == 4 || freq == 2 || freq == 1) {
					npvValue = AmortUtil.getAmortCal_NPV(beanObj);
				} else {
					err.put("ERR_FREQUENCYVALUE", AmortConstant.AmortValidation.ERR_FREQUENCYVALUE);
				}

				if (null != err && err.size() > 0) {
					errorsList.add(aayuCalculationUtil.generateErrors(errors));
					resultList.addAll(errorsList);
				} else {
					amortNPVResponse.setNpvValue(npvValue);
					resultList.add(amortNPVResponse);
				}

			} else {
				errorsList.add((ErrorMessages) resultValues.get("valErrors"));
				resultList.addAll(errorsList);
			}
		} catch (Exception e) {
			LOGGER.error("Exception Raised in method getAmortNPV() : Exception thrown ", e);
		}
		return resultList;
	}

}
