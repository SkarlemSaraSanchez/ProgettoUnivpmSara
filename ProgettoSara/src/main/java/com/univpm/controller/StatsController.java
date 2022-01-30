package com.univpm.controller;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.univpm.exception.WrongPeriodException;
import com.univpm.service.I_Stats;

/**
 * Classe che rappresenta il controller dell'applicazione Spring.
 * @author Sara
 *
 */
@RestController
@RequestMapping("/stats")
public class StatsController {

	@Autowired
	@Qualifier("stats")
	private I_Stats stats;
	private JSONObject response;
	
	@SuppressWarnings("unchecked")
	@GetMapping("/canada/state/{stateCode}/year/{anno}")
	public JSONObject showStatsAnnuali(@PathVariable String stateCode, @PathVariable String anno) throws WrongPeriodException {
		
		try {
			response = stats.getStatsAnnuali(stateCode, anno);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			response.put("errore", "problema nel parsing");
		}
		return response;
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping("/canada/{stateCode}")
	public JSONObject showStats(@PathVariable String stateCode) throws WrongPeriodException { 
		try {
			response = stats.gestoreFiltro(stateCode, null, null, null);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			response.put("errore", "problema nel parsing");
		}
		return response;
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping("/canada/{stateCode}/{nameClass}")
	public JSONObject showStatsStateClass(@PathVariable String stateCode, @PathVariable String nameClass) throws WrongPeriodException {
		
		try {
			response = stats.gestoreFiltro(stateCode, nameClass, null, null);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			response.put("errore", "problema nel parsing");
		}
		return response;			
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping("/canada/{stateCode}/{nameClass}/{start}/{end}")
	public JSONObject showStatsStateClassPeriod(@PathVariable String stateCode, @PathVariable String nameClass,
			@PathVariable String start, @PathVariable String end) throws WrongPeriodException {
		try {
			response = stats.gestoreFiltro(stateCode, nameClass, start, end);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			response.put("errore", "problema nel parsing");
		}
		return response;	
	}
	
}
