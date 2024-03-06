package com.ebixcash.aayu.service;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.ebixcash.aayu.exception.AmortException.CalculationFactoryException;
import com.ebixcash.aayu.model.AmortInputBean;

@Service
public interface AmortIRRService {
	
	public ArrayList<Object> getAmortIRR(AmortInputBean request) throws CalculationFactoryException;

}
