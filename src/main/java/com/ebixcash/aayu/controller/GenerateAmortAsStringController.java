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
import com.ebixcash.aayu.service.GenerateAmortAsStringService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/aayu/v1/")
public class GenerateAmortAsStringController {
	@Autowired(required=true)
	private GenerateAmortAsStringService generateAmortStringService;

	@PostMapping(value = "/generateAmortString", consumes = { 
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<ArrayList<Object>> generateAmortAsString(@RequestBody @Valid AmortInputBean request) throws AmortException {
		ResponseEntity<ArrayList<Object>> responseEntity = null; 
		ArrayList<Object> amortObjecttoStr = generateAmortStringService.getAmortObjecttoStr(request);
		responseEntity = new ResponseEntity<>(amortObjecttoStr, HttpStatus.OK);
	    return responseEntity;
	}

}
