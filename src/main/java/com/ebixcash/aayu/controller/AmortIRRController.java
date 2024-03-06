package com.ebixcash.aayu.controller;

import java.util.ArrayList;


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
import com.ebixcash.aayu.service.AmortIRRService;

@RestController
@RequestMapping(value = "aayu/v1")
public class AmortIRRController {

	@Autowired
	AmortIRRService amortIRRService;

	@PostMapping(value = "/amortIRR", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<ArrayList<Object>> getAmortIRR(@RequestBody AmortInputBean request) throws AmortException {
		ResponseEntity<ArrayList<Object>> responseEntity = null;
		try {

			ArrayList<Object> amortIrr = amortIRRService.getAmortIRR(request);
			responseEntity = new ResponseEntity<>(amortIrr, HttpStatus.OK);

		} catch (Exception e) {

			e.printStackTrace();

		}

		return responseEntity;
	}

}
