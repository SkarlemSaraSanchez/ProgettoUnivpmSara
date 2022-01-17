package com.univpm.service;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.univpm.exception.WrongPeriodException;

public interface I_Stats {

	//GESTISCE IL FILTRO
	public JSONObject gestoreFiltro(String countryCode, String stateCode, String nameClass, String start, String end) 
			throws ParseException, WrongPeriodException;
}
