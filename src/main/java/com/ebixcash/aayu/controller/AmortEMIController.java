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

import com.ebixcash.aayu.constant.AmortConstant;
import com.ebixcash.aayu.exception.AmortException;
import com.ebixcash.aayu.model.AmortInputBean;
import com.ebixcash.aayu.response.AmortFailureResponse;
import com.ebixcash.aayu.service.AmortEMIService;

@RestController
@RequestMapping("/aayu/v1")
public class AmortEMIController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AmortEMIController.class);

	@Autowired
	AmortEMIService amortEMIService;

	@PostMapping(value = "/amortEmi", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<ArrayList<Object>> getAmortEMI(@RequestBody AmortInputBean request) throws AmortException {
		boolean isError = false;
		ResponseEntity<ArrayList<Object>> responseEntity = null;
		AmortFailureResponse amortFailureResponse = new AmortFailureResponse();
		ArrayList<Object> resultList = new ArrayList<>();
		try {
			ArrayList<Object> amortEMI = amortEMIService.amortEMI(request);
			responseEntity = new ResponseEntity<>(amortEMI, HttpStatus.OK);

		} catch (Exception e) {
			isError = true;
			LOGGER.error("Exception Raised in method getAmortIntrest() : Exception thrown ", e);
		} finally {
			if (isError) {
				amortFailureResponse.setMessage(AmortConstant.FAILURE_MESSAGE);
				resultList.add(amortFailureResponse);
				responseEntity = new ResponseEntity<>(resultList, HttpStatus.EXPECTATION_FAILED);
			}
		}

		return responseEntity;
	}
}
