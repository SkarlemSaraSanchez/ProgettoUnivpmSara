package com.univpm.service;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import com.univpm.model.Parametro;
import com.univpm.model.Periodo;
import com.univpm.util.ApiCall;
import com.univpm.util.EndPointApiKey;

@Service("stats")
public class StatsImpl {

	Parametro apikey=new Parametro ("apikey","GV7AaEfUWGgyb0QXwOhDquUh5gmpbRMV"); //PARAMETRO CHE RAPPRESENTA L'API KEY
	EndPointApiKey source = new EndPointApiKey("https://app.ticketmaster.com/discovery/v2/events.json",apikey); //PER AVERE L'URL PER FARE LA CHIAMATA ALL'API DI TICKET MASTER
	ApiCall apicall = new ApiCall(); //OGGETTO PER AVERE I DATI DA TICKET MASTER
	
	/***************************************************************************************************************************/
	/****************************************** PRENDE I DATI DA TICKETMASTER **************************************************/
	/***************************************************************************************************************************/
	
	//CHIAMATA A TICKETMASTER CON IL COUNTRY E LO STATE
	public JSONObject setupModel(String countryCode, String stateCode) throws ParseException {
		source.setChiaveValore("countryCode", countryCode);
		source.setChiaveValore("stateCode",stateCode);
		return apicall.getData(source.getApi());
	}
	
	//CHIAMATA TICKETMASTER CON IL COUNTRY, STATE E CLASSIFICAZIONE
	public JSONObject setupModel(String countryCode, String stateCode,String nameClass) throws ParseException {
		source.setChiaveValore("countryCode", countryCode);
		source.setChiaveValore("stateCode",stateCode);
		source.setChiaveValore("segmentName", nameClass);
		return apicall.getData(source.getApi());
	}

	//CHIAMATA TICKETMASTER CON COUNTRY, STATE, CLASSIFICAZIONE E PERIODO
	public JSONObject setupModel(String countryCode, String stateCode, String nameClass, Periodo periodo) throws ParseException {
		source.setChiaveValore("countryCode", countryCode);
		source.setChiaveValore("stateCode", stateCode);
		source.setChiaveValore("segmentName", nameClass);
		source.setChiaveValore("startDateTime", periodo.getStartDate());
		source.setChiaveValore("endDateTime", periodo.getEndDate());
		return apicall.getData(source.getApi());
	}

	//CHIAMATA TICKETMASTER CON COUNTRY, STATE E PERIODO
	public JSONObject setupModel(String countryCode, String stateCode, Periodo periodo) throws ParseException {
		source.setChiaveValore("countryCode", countryCode);
		source.setChiaveValore("stateCode", stateCode);
		source.setChiaveValore("startDateTime", periodo.getStartDate());
		source.setChiaveValore("endDateTime", periodo.getEndDate());
		return apicall.getData(source.getApi());
	}
}
