package com.ebixcash.aayu.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebixcash.aayu.customvalidator.ErrorMessages;
import com.ebixcash.aayu.defaultsetting.DefaultSettingValues;
import com.ebixcash.aayu.model.AmortInputBean;
import com.ebixcash.aayu.response.TotalIntrestRespose;
import com.ebixcash.aayu.service.TotalInterestService;
import com.ebixcash.aayu.util.AayuCalculationUtil;
import com.ebixcash.aayu.util.AmortOverviewBean;

@Service
public class TotalInterestServiceImpl implements TotalInterestService {

	@Autowired(required = true)
	AayuCalculationUtil aayuCalculationUtil;

	@Autowired
	ErrorMessages errors;

	ArrayList otArr = null;
	AmortOverviewBean overviewbean = new AmortOverviewBean();

	@Override
	public ArrayList<Object> getTotalIntrest(AmortInputBean request) {
		double tot_int = 0.0d;
		HashMap err = new HashMap();
		ArrayList<Object> resultList = new ArrayList<>();
		Map<String, Object> tempMap = new HashMap<>();
		TotalIntrestRespose intrestRespose = new TotalIntrestRespose();
		Map<String, Object> resultValues = new HashMap<>();
		ArrayList<ErrorMessages> errorsList = new ArrayList<>();
		ArrayList tempXMLList = new ArrayList();
		try {
			tempMap = aayuCalculationUtil.processInput(request);
			resultValues = DefaultSettingValues.getDefaultValues(tempMap);
			String successFlag = (String) resultValues.get("successFlag");
			if ("true".equals(successFlag)) {
				AmortInputBean beanObj = aayuCalculationUtil.InitializeInputModel(tempMap);

				if (null != beanObj.getArrPerPrinRcvr()) {
					String dds = beanObj.getDateOfDisbursement();
					String dcy = beanObj.getDateOfCycle();
					double loan_amt = beanObj.getLoanAmount();
					double tenr = beanObj.getTenor();

					tempXMLList = aayuCalculationUtil.calperprincrecovery(tempXMLList, beanObj);
					beanObj.setLoanAmount(loan_amt);
					beanObj.setTenor(tenr);
					beanObj.setDateOfDisbursement(dds);
					beanObj.setDateOfCycle(dcy);

					if (beanObj.getintAmort().equals("Y") && beanObj.getRVvalue() == 0
							&& beanObj.getArrPerPrinRcvr() != null) {

						otArr = tempXMLList;

						tempXMLList = aayuCalculationUtil.Calculate_accrued_interest(beanObj);
						tempXMLList.add(overviewbean);
						// tempList.add(AmortOverviewBean);
					}

				} else {
					tempXMLList = aayuCalculationUtil.generateAmort(beanObj);
				}

				if (null != err && err.size() > 0) {
					errorsList.add(aayuCalculationUtil.generateErrors(errors));
					resultList.addAll(errorsList);
				} else {
					tot_int = ((AmortOverviewBean) tempXMLList.get(tempXMLList.size() - 1)).getTotalRoundInterest();
					intrestRespose.setTotalIntrest(tot_int);
					resultList.add(intrestRespose);
				}

			} else {
				errorsList.add((ErrorMessages) resultValues.get("valErrors"));
				resultList.addAll(errorsList);
			}

		} catch (Exception e) {
		}
		return resultList;
	}

}
