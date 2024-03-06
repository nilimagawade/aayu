package com.ebixcash.aayu.controller;

import java.util.ArrayList;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ebixcash.aayu.exception.AmortException;
import com.ebixcash.aayu.model.AmortInputBean;
import com.ebixcash.aayu.response.AmortIntrestRateResponse;
import com.ebixcash.aayu.service.AmortInterestRateService;

@RestController
@RequestMapping("/aayu/v1")
public class AmortInterestRateController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AmortInterestRateController.class);

	public static final boolean isDebugEnabled = LOGGER.isDebugEnabled();
	
	@Autowired
	AmortInterestRateService amortInterestRateService;
	
	@PostMapping(value = "/amortInterestRate", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<ArrayList<Object>> getAmortIntrestRate(@RequestBody AmortInputBean amortInputBean)
			throws AmortException {
		ResponseEntity<ArrayList<Object>> responseEntity = null;
		AmortIntrestRateResponse amortIntrestRateResponse = new AmortIntrestRateResponse();
		try {
			ArrayList<Object> amortIntrestRate = amortInterestRateService.getAmortIntrestRate(amortInputBean);
			responseEntity = new ResponseEntity<>(amortIntrestRate, HttpStatus.OK);
		} catch (Exception exception) {
			responseEntity = new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
			LOGGER.error("Exception Raised in method getAmortIntrestRate() : Exception thrown ", exception);

		}
		return responseEntity;

	}

}
