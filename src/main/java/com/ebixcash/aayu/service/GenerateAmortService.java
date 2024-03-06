package com.ebixcash.aayu.service;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.ebixcash.aayu.exception.AmortException;
import com.ebixcash.aayu.model.AmortInputBean;

@Service
public interface GenerateAmortService {
	public ArrayList<Object> getAmortObjecttoJsonStr(AmortInputBean input) throws AmortException;
	
}
