package com.ebixcash.aayu.service;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.ebixcash.aayu.exception.AmortException;
import com.ebixcash.aayu.model.AmortInputBean;
import com.ebixcash.aayu.response.AmortEmiResponse;
@Service
public interface AmortEMIService {

	public ArrayList<Object> amortEMI(AmortInputBean rxml) throws AmortException;
}
