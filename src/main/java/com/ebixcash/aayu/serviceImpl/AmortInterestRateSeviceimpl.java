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
import com.ebixcash.aayu.model.AmortInputBean;
import com.ebixcash.aayu.response.AmortIntrestRateResponse;
import com.ebixcash.aayu.service.AmortInterestRateService;
import com.ebixcash.aayu.util.AayuCalculationUtil;
import com.ebixcash.aayu.util.AmortUtil;


@Service
public class AmortInterestRateSeviceimpl implements AmortInterestRateService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AmortInterestRateSeviceimpl.class);

	public static final boolean isDebugEnabled = LOGGER.isDebugEnabled();
	
	@Autowired(required = true)
	AayuCalculationUtil aayuCalculationUtil;

	@Autowired
	ErrorMessages errors;

	@Override
	public ArrayList<Object> getAmortIntrestRate(AmortInputBean amortInputBean){
		
		ArrayList<Object> resultList = new ArrayList<>();
		ArrayList<ErrorMessages> errorsList = new ArrayList<>();
		HashMap err = new HashMap();
		Map<String, Object> tempMap = new HashMap<>();
		Map<String, Object> resultValues = new HashMap<>();
		AmortIntrestRateResponse amortIntrestRateResponse = new AmortIntrestRateResponse();
		double real_interest_rate  = 0.0d;
		
		
		
			if (isDebugEnabled) LOGGER.debug("In getAmortInterest() method");
			StringBuffer outXML = new StringBuffer("");
			//HashMap tempMap = new HashMap();
			ErrorMessages errors = new ErrorMessages();
			try{
				tempMap = aayuCalculationUtil.processInput(amortInputBean);
				resultValues = DefaultSettingValues.getDefaultValues(tempMap);
				String successFlag = (String) resultValues.get("successFlag");
				if ("true".equals(successFlag))
				{
					
					AmortInputBean beanXMLObj = aayuCalculationUtil.InitializeInputModel(tempMap);
					ArrayList tempXMLList = aayuCalculationUtil.generateAmort(beanXMLObj);
					if(AmortUtil.compare2Dates(beanXMLObj.getDtstart(), beanXMLObj.getDtend(), beanXMLObj.getDateformat()) == 1
							|| AmortUtil.compare2Dates(beanXMLObj.getDtstart(), beanXMLObj.getDtend(), beanXMLObj.getDateformat()) == 0
					)
					{
						err.put("ERR_Date", "End Date should be greater than Start Date");
					}
					if(null != err && err.size() > 0)
					{
						errorsList.add(aayuCalculationUtil.generateErrors(errors));
						resultList.addAll(errorsList);
					}
					else
					{
						real_interest_rate = real_interest_rate * 100;
						real_interest_rate = AmortUtil.round(real_interest_rate, beanXMLObj.getOthers_ro_part(), beanXMLObj.getOthers_ro_to(), beanXMLObj.getOthers_unit_ro());
						outXML.append("<?xml version=\"1.0\"?>\n<root>");
						outXML.append("<interestrate>" + real_interest_rate + "</interestrate></root>");
						amortIntrestRateResponse.setInterestrate(real_interest_rate);
						resultList.add(amortIntrestRateResponse);
					}
				}
				else
				{
					errorsList.add((ErrorMessages) resultValues.get("valErrors"));
					resultList.addAll(errorsList);
				}

				if (isDebugEnabled) LOGGER.debug(" In getAmortBPI Output XML :"+outXML.toString());
				if (isDebugEnabled) LOGGER.debug("In getAmortBPI()method...END.");
			}
			catch(Exception exception){
				if (isDebugEnabled) LOGGER.error("Method : getAmortBPI() : Exception thrown " + exception.toString());
				//throw new AmortException.CalculationFactoryException(exception);
			}
			return resultList;
	
}
}