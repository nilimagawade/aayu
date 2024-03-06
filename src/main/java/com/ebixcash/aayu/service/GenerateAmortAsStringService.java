package com.ebixcash.aayu.service;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.ebixcash.aayu.exception.AmortException;
import com.ebixcash.aayu.model.AmortInputBean;

@Service
public interface GenerateAmortAsStringService {
	
	public ArrayList<Object> getAmortObjecttoStr(AmortInputBean input) throws AmortException;

}
