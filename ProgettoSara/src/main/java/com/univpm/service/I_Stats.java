package com.univpm.service;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.univpm.exception.WrongPeriodException;
import com.univpm.model.Periodo;

public interface I_Stats {

	//GESTISCE IL FILTRO
	public JSONObject gestoreFiltro(String countryCode, String stateCode, String nameClass, String start, String end) 
			throws ParseException, WrongPeriodException;

	public JSONObject getStats(String countryCode, String stateCode) throws ParseException;

	public JSONObject getStats(String countryCode, String stateCode, String nameClass) throws ParseException;

	public JSONObject getStats(String countryCode, String stateCode, Periodo periodo) throws ParseException;

	public JSONObject getStats(String countryCode, String stateCode, String nameClass, Periodo periodo) throws ParseException;
}
