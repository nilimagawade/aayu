package com.ebixcash.aayu.controller;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ebixcash.aayu.constant.AmortConstant;
import com.ebixcash.aayu.model.AmortInputBean;
import com.ebixcash.aayu.response.AmortFailureResponse;
import com.ebixcash.aayu.service.GenerateAmortTenorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/aayu/v1/")
public class GenerateAmortTenorController {

	private static final Logger LOGGER = LoggerFactory.getLogger(GenerateAmortTenorController.class);
	@Autowired
	GenerateAmortTenorService amortService;

	@PostMapping(value = "amortTenor", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<ArrayList<Object>> getTenor(@RequestBody @Valid AmortInputBean request) {
		boolean isError = false;
		ResponseEntity<ArrayList<Object>> responseEntity = null;
		AmortFailureResponse amortFailureResponse = new AmortFailureResponse();
		ArrayList<Object> resultList = new ArrayList<>();
		try {
			ArrayList<Object> tenore = amortService.getTenore(request);
			responseEntity = new ResponseEntity<>(tenore, HttpStatus.OK);
		} catch (Exception e) {
			isError = true;
			LOGGER.error("Exception Raised in method getTenor() : Exception thrown ", e);
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
