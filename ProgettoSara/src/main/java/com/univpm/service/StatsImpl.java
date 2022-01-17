package com.univpm.service;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import com.univpm.exception.WrongPeriodException;
import com.univpm.model.Classificazione;
import com.univpm.model.Parametro;
import com.univpm.model.Periodo;
import com.univpm.util.ApiCall;
import com.univpm.util.EndPointApiKey;

@Service("stats")
public class StatsImpl implements I_Stats {

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

	
	/***************************************************************************************************************************/
	/************************************************** GESTORE FILTRO *********************************************************/
	/***************************************************************************************************************************/
	
	@Override
	public JSONObject gestoreFiltro(String countryCode, String stateCode, String nameClass, String start, String end)
			throws ParseException, WrongPeriodException {
		
		return null;
	}
	
	/***************************************************************************************************************************/
	/**************************************************** METODI UTILI *********************************************************/
	/***************************************************************************************************************************/

	//RITORNA LE MACROCATEGORIE IN FORMATO ARRAYLIST
	public ArrayList<Classificazione> getClassifications() throws ParseException {
		ArrayList<Classificazione> classificazioni = new ArrayList<Classificazione>();
		EndPointApiKey source=new EndPointApiKey("https://app.ticketmaster.com/discovery/v2/classifications.json",apikey);
		JSONObject tmp= apicall.getData(source.getApi());

		JSONObject p1=(JSONObject) tmp.get("_embedded");
		JSONArray p2=(JSONArray) p1.get("classifications");//quindi p2 è un json array
	
		for(int i=0;i<p2.size();i++) {
			JSONObject x=(JSONObject) p2.get(i);
			JSONObject y=(JSONObject) x.get("segment");
			
			if(y!=null) {
				String id= (String) y.get("id");
				String s= (String) y.get("name");
				//GESTISCO IL CASO DELLA CLASSIFICAZIONE CON IL CHAR & CHE MI DA PROBLEMI QUANDO FACCIO LA CHIAMATA A TICKETMASTER
				if(s.contains("&")) {
					System.out.println();
					s = s.replace("&", "%");
				}
				Classificazione classificazione = new Classificazione(s,id);
				classificazioni.add(classificazione);
			}
		}
		return classificazioni;
	}

	//RITORNA GLI IL NUMERO DI EVENTI CHE RITORNANO DOPO LA CHIAMATA ALL'API DI TICKETMASTER
	private long numEventi (JSONObject obj) {
		if(obj != null) {
			JSONObject page = (JSONObject) obj.get("page");
			long totalElements = (long) page.get("totalElements");
			return totalElements;
		}
		return 0;
	}

	//FA LA VALIDAZIONE DEL PARAMETRO
	private boolean validazione(String name, String valore) throws ParseException {
		source.setChiaveValore(name, valore);
		JSONObject obj = apicall.getData(source.getApi());
		long num = this.numEventi(obj);
		if (num != 0) return true;
		else return false;
	}
}
