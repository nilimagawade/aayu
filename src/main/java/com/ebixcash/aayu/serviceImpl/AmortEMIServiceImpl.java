package com.ebixcash.aayu.serviceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebixcash.aayu.constant.AmortConstant;
import com.ebixcash.aayu.customvalidator.ErrorMessages;
import com.ebixcash.aayu.defaultsetting.DefaultSettingValues;
import com.ebixcash.aayu.model.AmortInputBean;
import com.ebixcash.aayu.response.AmortEmiResponse;
import com.ebixcash.aayu.service.AmortEMIService;
import com.ebixcash.aayu.util.AayuCalculationUtil;
import com.ebixcash.aayu.util.AmortUtil;

@Service
public class AmortEMIServiceImpl implements AmortEMIService {
	private static final Logger LOGGER = LoggerFactory.getLogger(AmortEMIServiceImpl.class);

	public static final boolean isDebugEnabled = LOGGER.isDebugEnabled();

	@Autowired
	AayuCalculationUtil aayuCalculationUtil;

	@Autowired
	ErrorMessages errors;

	public ArrayList<Object> amortEMI(AmortInputBean request) {
		Double fixedIntsEmi;
		HashMap err = new HashMap();
		ArrayList<ErrorMessages> errorsList = new ArrayList<>();
		ArrayList<Object> resultList = new ArrayList<>();
		double r_emi = 0.0d;
		Map<String, Object> tempMap = new HashMap<>();
		Map<String, Object> resultValues = new HashMap<>();
		ErrorMessages errors = new ErrorMessages();
		AmortEmiResponse amortEmiResponse = new AmortEmiResponse();
		double emi = 0.0d;
		ArrayList<Double> emiList = new ArrayList<Double>();
		double fixedInstEmi = 0.0d;

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
					if (!"".equals(beanObj.getCurr_emi()) && "Y".equals(beanObj.getCurr_emi())) {
						emi = fixedInstEmi;
					} else if (!"".equals(beanObj.getMaxEmi()) && "Y".equals(beanObj.getMaxEmi())) {
						emi = Collections.max(emiList);
					}

					r_emi = AmortUtil.round(beanObj.getInput_emi(), beanObj.getEMI_ro_part(), beanObj.getEMI_ro_to(),
							beanObj.getEMI_unit_ro());
					AmortConstant.isDaily = false;
					amortEmiResponse.setAmortEmi(r_emi);
					resultList.add(amortEmiResponse);
				}
			} else {
				errorsList.add((ErrorMessages) resultValues.get("valErrors"));
				resultList.addAll(errorsList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return resultList;
	}
}
