package com.ebixcash.aayu.controller;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ebixcash.aayu.constant.AmortConstant;
import com.ebixcash.aayu.exception.AmortException;
import com.ebixcash.aayu.model.AmortInputBean;
import com.ebixcash.aayu.response.AmortFailureResponse;
import com.ebixcash.aayu.service.GenerateLoanAmountService;

@RestController
@RequestMapping(value = "/aayu/v1")
public class GenerateLoanAmountController {
	private static final Logger LOGGER = LoggerFactory.getLogger(GenerateLoanAmountController.class);

	@Autowired
	private GenerateLoanAmountService loanAmountService;

	@PostMapping(value = "/amortLoanAmount")
	public ResponseEntity<ArrayList<Object>> getAmortLoanamount(@RequestBody AmortInputBean rxml)
			throws AmortException {
		ResponseEntity<ArrayList<Object>> responseEntity = null;
		boolean isError = false;
		AmortFailureResponse amortFailureResponse = new AmortFailureResponse();
		ArrayList<Object> resultList = new ArrayList<>();

		try {
			ArrayList<Object> amortLoanamount = loanAmountService.getAmortLoanamount(rxml);
			responseEntity = new ResponseEntity<>(amortLoanamount, HttpStatus.OK);
		} catch (Exception e) {
			isError = true;
			LOGGER.error("Exception Raised in method getAmortLoanamount() : Exception thrown ", e);
		} finally {
			if (isError) {
				amortFailureResponse.setMessage(AmortConstant.FAILURE_MESSAGE);
				resultList.add(amortFailureResponse);
				responseEntity = new ResponseEntity<>(resultList, HttpStatus.OK);
			}
		}

		return responseEntity;

	}

}
