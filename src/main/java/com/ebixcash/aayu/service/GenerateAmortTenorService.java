package com.ebixcash.aayu.service;

import java.util.ArrayList;

import com.ebixcash.aayu.exception.AmortException;
import com.ebixcash.aayu.model.AmortInputBean;
import com.ebixcash.aayu.response.AmortTenorResponse;

public interface GenerateAmortTenorService {

	ArrayList<Object> getTenore(AmortInputBean request) throws AmortException;

}
