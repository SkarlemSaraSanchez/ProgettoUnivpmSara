package com.univpm.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import com.univpm.exception.WrongPeriodException;
import com.univpm.model.Anno;
import com.univpm.model.Classificazione;
import com.univpm.model.Mese;
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
	/**************************************************** STATISTICHE **********************************************************/
	/***************************************************************************************************************************/
	
	@SuppressWarnings("unchecked")
	public JSONObject getStatsAnnuali(String countryCode, String stateCode, String anno) throws ParseException, WrongPeriodException {	
		JSONObject response = new JSONObject();
		JSONObject obj;
		JSONArray arr = new JSONArray();
		Anno year = new Anno(anno);
		HashMap<Integer,Mese> mesi = year.getMesi();
		
		long tot = 0;
		long min = 0;
		int indice_min = 0;
		long max = 0;
		int indice_max = 0;
		
		response.put("Anno", anno);
		
		//VALIDAZIONE PARAMETRO COUNTRYCODE
		boolean valCountryCode = this.validazione("countryCode", countryCode);
		if(!valCountryCode) {
			obj = new JSONObject();
			obj.put("Country", countryCode);
			obj.put("Errore", "countryCode non valido");
			response = obj;
			return response;
		}
		response.put("Country", countryCode);
		
		//VALIDAZIONE PARAMETRO STATECODE
		boolean valStateCode = this.validazione("stateCode", stateCode);
		if(!valStateCode) {
			obj = new JSONObject();
			obj.put("State", stateCode);
			obj.put("Errore", "stateCode non valido");
			response = obj;
			return response;
		}
		response.put("State", stateCode);
		
		//GESTIONE STATISTISTICHE MENSILI OVVERO CALCOLO DEGLI EVENTI PER OGNI CLASSIFICAZIONE
		ArrayList<Classificazione> classificazioni = this.getClassifications(); // HO LE CLASSIFICAZIONI
		Periodo p1 = new Periodo(1,1, Integer.parseInt(anno), 31,12,Integer.parseInt(anno)); //RAPPRESENTA IL PERIODO DELL'ANNO
		JSONArray arr_obj = new JSONArray();
		for (Classificazione c : classificazioni) {
			JSONObject dato = setupModel(countryCode,stateCode,c.getName(),p1);
			long num = this.numEventi(dato);
			obj = new JSONObject();
			obj.put("Nome", c.getName());
			obj.put("Numero Eventi", num);
			arr_obj.add(obj);
		}
		response.put("Stats Classificazioni", arr_obj);
		
		//GESTIONE STATISTICHE MENSILI OVVERO CALCOLO DEGLI EVENTI PER MESE
		for (int i=1; i<=12; i++) {
			obj = new JSONObject();
			Periodo periodo = new Periodo(mesi.get(i).getData_inizio(), mesi.get(i).getData_fine());
			JSONObject dato = setupModel(countryCode,stateCode, periodo);
			long num = this.numEventi(dato);
			tot+=num; //CONTEGGIO TOTALI EVENTI
			
			//CALCOLO STATISTICHE
			if(i==1) {
				min = num;
				indice_min = i;
				max = num;
				indice_max = i;	
			}
			else {
				if(num<min) {
					min = num;
					indice_min = i;
				}
				if(num>max) {
					max = num;
					indice_max = i;
				}
			}
			
			String nome_mese = mesi.get(i).getNome_mese();
			obj.put(nome_mese, num);
			arr.add(obj);
		}
		
		//INSERIMENTO DELLE STATISTICHE NELLA RISPOSTA
		JSONArray stats_minmaxmed = new JSONArray();
		JSONObject sing = new JSONObject();
		sing.put("Media Mensile", tot/12);
		sing.put("Mese con numero minimo di eventi", mesi.get(indice_min).getNome_mese() + "(" + min + ")");
		sing.put("Mese con numero massimo di eventi" , mesi.get(indice_max).getNome_mese() + "(" + max + ")");
		stats_minmaxmed.add(sing);
		
		response.put("MIN,MAX,MEDIA", stats_minmaxmed);
		response.put("TOT EVENTI", tot);
		response.put("Stats Mensili", arr);
		return response;
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
