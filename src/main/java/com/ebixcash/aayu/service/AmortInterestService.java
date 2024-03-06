package com.ebixcash.aayu.service;

import java.util.ArrayList;

import com.ebixcash.aayu.exception.AmortException;
import com.ebixcash.aayu.model.AmortInputBean;

public interface AmortInterestService {
	public ArrayList<Object> getAmortIntrest(AmortInputBean rxml) throws AmortException;

}
