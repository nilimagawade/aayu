package com.ebixcash.aayu.service;

import java.util.ArrayList;

import com.ebixcash.aayu.model.AmortInputBean;

public interface AmortNPVService {

	ArrayList<Object> getAmortNPV(AmortInputBean request);

}
