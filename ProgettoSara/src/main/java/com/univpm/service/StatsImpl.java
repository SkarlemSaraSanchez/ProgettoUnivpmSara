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
	public JSONObject gestoreFiltro(String stateCode, String nameClass, String start, String end)
			throws ParseException, WrongPeriodException {
		JSONObject response = new JSONObject();
		String countryCode = "CA";
		
		//COSTRUISCO L'OGGETTO PERIODO
		Periodo periodo = null;
		if(start != null && end != null) {
			String[] arr_start = start.split("-");
			String[] arr_end = end.split("-");
			periodo = new Periodo(Integer.parseInt(arr_start[0]), Integer.parseInt(arr_start[1]), Integer.parseInt(arr_start[2]), Integer.parseInt(arr_end[0]), Integer.parseInt(arr_end[1]), Integer.parseInt(arr_end[2]));
		}
		
		if(nameClass != null) {
			//FILTRO CON NAMECLASS E PERIODO
			if(periodo != null) {
				response = this.getStats(countryCode,stateCode,nameClass,periodo);
			}
			else {
				response = this.getStats(countryCode,stateCode,nameClass);
			}
		}
		else {
			if(periodo != null) {
				response = this.getStats(countryCode,stateCode,periodo);
			}
			else {
				response = this.getStats(countryCode,stateCode);
			}
		}
		return response;
	}

	/***************************************************************************************************************************/
	/**************************************************** STATISTICHE **********************************************************/
	/***************************************************************************************************************************/
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getStatsAnnuali(String stateCode, String anno) throws ParseException, WrongPeriodException {	
		JSONObject response = new JSONObject();
		JSONObject obj;
		JSONArray arr = new JSONArray();
		Anno year = new Anno(anno);
		HashMap<Integer,Mese> mesi = year.getMesi();
		String countryCode = "CA";
		
		long tot = 0;
		long min = 0;
		int indice_min = 0;
		long max = 0;
		int indice_max = 0;
		
		//MANCA VALIDAZIONE ANNO
		response.put("Anno", anno);
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

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getStats(String countryCode, String stateCode) throws ParseException {
		JSONObject response = new JSONObject();
		JSONObject obj = new JSONObject();
		JSONArray arr= new JSONArray();
		String[] states = stateCode.split("-");
		
		//PER LE STATISTICHE
		long tot = 0;
		long min = 0;
		int indice_min = 0;
		long max = 0;
		int indice_max = 0;
		
		//VALIDAZIONE PARAMETRO COUNTRYCODE
		boolean valCountryCode = this.validazione("countryCode", countryCode);
		if(!valCountryCode) {
			obj.put("Country", countryCode);
			obj.put("Errore", "countryCode non valido");
			obj.put("Messaggio", "Pattern di chiamata compromesso");
			response = obj;
			return response;
		}
		obj.put("Country", countryCode);
		
		//SINGOLO STATE
		if(states.length == 1) {
			//VALIDAZIONE PARAMETRO STATECODE
			boolean valStateCode = this.validazione("stateCode", stateCode);
			if (!valStateCode) {
				obj.put("State", stateCode);
				obj.put("Errore", "stateCode non valido");
				obj.put("Messaggio", "Pattern di chiamata compromesso");
				response = obj;
				return response;
			}
			obj.put("Stato", stateCode);
			JSONObject dato = setupModel(countryCode,stateCode);
			long num = this.numEventi(dato);
			obj.put("Numero Eventi", num);
			obj.put("Nota", "Statistiche non disponibili");
			response = obj;
		}
		
		//MULTI STATE
		if(states.length > 1) {
			for (int i=0; i<states.length; i++) {
				JSONObject singolo = new JSONObject();
				//VALIDAZIONE STATE
				boolean valStateCode = this.validazione("stateCode", states[i]);
				if (!valStateCode) {
					JSONObject errore = this.getErrori("State", states[i]);
					singolo = errore;
				}
				else {
					JSONObject dato = setupModel(countryCode,states[i]);
					long num = this.numEventi(dato);				
					tot += num;
					
					//CALCOLO STATISTICHE
					if(i==0) {
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
						if(num>max){
							max = num;
							indice_max = i;
						}
					}
					
					singolo.put("Numeri Eventi", num);
					singolo.put("State", states[i]);
				}
				arr.add(singolo);
			}
			//GESTIONE STATISTICHE
			JSONArray arr_stats = new JSONArray();
			JSONObject stats = new JSONObject();
			stats.put("Stato Min Eventi", states[indice_min] + "(" + min + ")"); 
			stats.put("Stato Max Eventi", states[indice_max] + "(" + max + ")");
			stats.put("Media Eventi Stato", tot/states.length); //POTREI CASTARLE A DOUBLE
			arr_stats.add(stats);
			
			obj.put("Stati", arr);
			obj.put("Stats", arr_stats);
			
			response = obj;
		}
		
		return response;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getStats(String countryCode, String stateCode,String nameClass) throws ParseException {
		//AGGIUNGERE PERIODO PER AVERE UNO STESSO METODO
		JSONObject obj = new JSONObject();
		JSONArray objs = new JSONArray();
		JSONObject response = new JSONObject();
		long num = 0;
		String[] states = stateCode.split("-");
		String[] classific = nameClass.split("-");
		
		//PER LE STATISTICHE
		long tot = 0;
		long min = 0;
		int indice_min = 0;
		long max = 0;
		int indice_max = 0;
		
		//VALIDAZIONE PARAMETRO COUNTRYCODE
		boolean valCountryCode = this.validazione("countryCode", countryCode);
		if(!valCountryCode) {
			obj.put("Country", countryCode);
			obj.put("Errore", "countryCode non valido");
			obj.put("Messaggio", "Pattern di chiamata compromesso");
			response = obj;
			return response;
		}
		
		//AGGIUNGO IL DATO COUNTRY
		obj.put("Country", countryCode);
		
		//SINGOLO STATO, SINGOLA CLASSIFICAZIONE
		if (states.length == 1 && classific.length == 1) {
			
			//VALIDAZIONE DEI PARAMETRI CHE L'UTENTE IMMETTE
			boolean valStateCode = this.validazione("stateCode", stateCode);
			boolean valClass = this.validazione("segmentName", nameClass);
			//SE UNO DEI PARAMETRI NON E' VALIDO MANDO UN ECCEZIONE
			if (!valStateCode) {
				obj.put("State", stateCode);
				obj.put("Errore", "Stato non valido");
				response = obj;
				return response;
			}
			if (!valClass) {
				obj.put("Classification", nameClass);
				obj.put("Errore", "nameClass non valido");
				response = obj;
				return response;
			}
			
			//ALTRIMENTI CONTINUO
			num = this.numEventi(setupModel(countryCode,stateCode,nameClass));
			obj.put("Country", countryCode);
			obj.put("State", stateCode);
			obj.put("Classification", nameClass);
			obj.put("NUM", num);
			obj.put("Nota", "Statistiche non disponibili");
			response = obj;
		}
		
		//SINGOLO STATO, MULTI CLASSIFICAZIONI
		if(states.length == 1 && classific.length > 1) {
			
			//VALIDAZIONE DEI PARAMETRI CHE L'UTENTE IMMETTE
			boolean valStateCode = this.validazione("stateCode", stateCode);
			if(!valStateCode) {
				obj.put("State", stateCode);
				obj.put("Errore", "Stato non valido");
				response = obj;
				return response;
			}
			obj.put("State", stateCode);
			for (int i=0; i<classific.length; i++) {
				boolean valCla = this.validazione("segmentName", classific[i]);
				JSONObject singola = new JSONObject();
				if(!valCla) {
					singola.put("Classificazione", classific[i]);
					singola.put("Errore", "Classificazione non valida");
				}
				else {
					num = this.numEventi(setupModel(countryCode,stateCode,classific[i]));
					tot += num;
					
					//CALCOLO STATISTICHE
					if(i==0) {
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
						if(num>max){
							max = num;
							indice_max = i;
						}
					}
					
					singola.put("Classificazione", classific[i]);
					singola.put("NUM", num);
				}
				objs.add(singola);
			}
			
			JSONArray arr_stats = new JSONArray();
			JSONObject stats = new JSONObject();
			stats.put("Genere Min Eventi", classific[indice_min] + "(" + min + ")");
			stats.put("Genere Max Eventi", classific[indice_max] + "(" + max + ")");
			stats.put("Media Eventi Cat", tot/classific.length);
			arr_stats.add(stats);
			
			obj.put("Stats", arr_stats);
			obj.put("Classificazioni", objs);
			response = obj;
		}
		
		//MULTI STATO, SINGOLA CLASSIFICAZIONE
		if(states.length > 1 && classific.length == 1) {
			//VALIDAZIONE CLASSIFICAZIONE
			boolean valCla = this.validazione("segmentName", nameClass);
			if(!valCla) {
				obj.put("Classificazione", nameClass);
				obj.put("Errore", "Classificazione non valida");
				response = obj;
				return response;
			}
			obj.put("Classificazione", nameClass);
			for (int i=0; i<states.length; i++) {
				boolean valSta = this.validazione("stateCode", states[i]);
				JSONObject singola = new JSONObject();
				if(!valSta) {
					singola.put("Stato", states[i]);
					singola.put("Errore", "Stato non valido");
				}
				else {
					num = numEventi(setupModel(countryCode,states[i],nameClass));
					tot += num;
					
					//CALCOLO STATISTICHE
					if(i==0) {
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
						if(num>max){
							max = num;
							indice_max = i;
						}
					}
					
					
					singola.put("Stato", states[i]);
					singola.put("NUM", num);
				}
				objs.add(singola);
			}
			JSONArray arr_stats = new JSONArray();
			JSONObject stats = new JSONObject();
			stats.put("Stato Min Eventi", states[indice_min] + "(" + min + ")"); 
			stats.put("Stato Max Eventi", states[indice_max] + "(" + max + ")");
			stats.put("Media Eventi Stato", tot/states.length); //POTREI CASTARLE A DOUBLE
			arr_stats.add(stats);
			
			obj.put("Stats", arr_stats);
			obj.put("States", objs);
			response = obj;
		}
		
		//MULTI STATO, MULTI CLASSIFICAZIONE
		if(states.length > 1 && classific.length > 1) {
			//FACCIO L'ANALISI PER OGNI STATO
			//RICHIAMO QUESTO STESSO METODO
			for (String s : states) {
				JSONObject singolo = this.getStats(countryCode, s, nameClass);
				singolo.remove("Periodo"); //RIMUOVO LA PROPRIETA' PERIODO PER NON FARLA RIPETERE
				objs.add(singolo);
			}
			obj.put("Stats", objs);
			response = obj;
		}
		return response;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getStats(String countryCode, String stateCode, Periodo periodo) throws ParseException {
		
		JSONObject response = new JSONObject();
		JSONObject obj = new JSONObject();
		JSONArray arr= new JSONArray();
		String[] states = stateCode.split("-");
		
		//PER LE STATISTICHE
		long tot = 0;
		long min = 0;
		int indice_min = 0;
		long max = 0;
		int indice_max = 0;
		
		//VALIDAZIONE PARAMETRO COUNTRYCODE
		boolean valCountryCode = this.validazione("countryCode", countryCode);
		if(!valCountryCode) {
			obj.put("Country", countryCode);
			obj.put("Errore", "countryCode non valido");
			obj.put("Messaggio", "Pattern di chiamata compromesso");
			response = obj;
			return response;
		}
		obj.put("Country", countryCode);
		obj.put("Periodo", periodo);
		
		//SINGOLO STATE
		if(states.length == 1) {
			//VALIDAZIONE PARAMETRO STATECODE
			boolean valStateCode = this.validazione("stateCode", stateCode);
			if (!valStateCode) {
				obj.put("State", stateCode);
				obj.put("Errore", "stateCode non valido");
				obj.put("Messaggio", "Pattern di chiamata compromesso");
				response = obj;
				return response;
			}
			obj.put("Stato", stateCode);
			JSONObject dato = setupModel(countryCode,stateCode,periodo);
			long num = this.numEventi(dato);
			obj.put("Numero Eventi", num);
			response = obj;
		}
		
		//MULTI STATE
		if(states.length > 1) {
			for (int i=0; i<states.length; i++) {
				JSONObject singolo = new JSONObject();
				//VALIDAZIONE STATE
				boolean valStateCode = this.validazione("stateCode", states[i]);
				if (!valStateCode) {
					singolo.put("State", states[i]);
					singolo.put("Errore", "stateCode non valido");
					singolo.put("Messaggio", "Pattern di chiamata compromesso");
				}
				else {
					JSONObject dato = setupModel(countryCode,states[i],periodo);
					long num = this.numEventi(dato);				
					tot += num;
					
					//CALCOLO STATISTICHE
					if(i==0) {
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
						if(num>max){
							max = num;
							indice_max = i;
						}
					}
					
					singolo.put("Numeri Eventi", num);
					singolo.put("State", states[i]);
				}
				arr.add(singolo);
			}
			//GESTIONE STATISTICHE
			JSONArray arr_stats = new JSONArray();
			JSONObject stats = new JSONObject();
			stats.put("Stato Min Eventi", states[indice_min] + "(" + min + ")"); 
			stats.put("Stato Max Eventi", states[indice_max] + "(" + max + ")");
			stats.put("Media Eventi Stato", tot/states.length); //POTREI CASTARLE A DOUBLE
			arr_stats.add(stats);
			
			obj.put("Stati", arr);
			obj.put("Stats", arr_stats);
			
			response = obj;
		}
		
		return response;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getStats(String countryCode, String stateCode, String nameClass, Periodo periodo) throws ParseException {
		
		JSONObject obj = new JSONObject();
		JSONArray objs = new JSONArray();
		JSONObject response = new JSONObject();
		long num = 0;
		
		//VALORI DEGLI STATI E DELLE CLASSIFICAZIONI
		String[] states = stateCode.split("-");
		String[] classific = nameClass.split("-");
		
		//PER LE STATISTICHE
		long tot = 0;
		long min = 0;
		int indice_min = 0;
		long max = 0;
		int indice_max = 0;
		
		//VALIDAZIONE PARAMETRO COUNTRYCODE
		boolean valCountryCode = this.validazione("countryCode", countryCode);
		if(!valCountryCode) {
			return this.getErrori("countryCode", countryCode);
		}
		
		//AGGIUNGO IL DATO COUNTRY
		obj.put("Country", countryCode);
		
		//SINGOLO STATO, SINGOLA CLASSIFICAZIONE --- NON HO LE STATS
		if (states.length == 1 && classific.length == 1) {
			
			//VALIDAZIONE DEI PARAMETRI CHE L'UTENTE IMMETTE
			boolean valStateCode = this.validazione("stateCode", stateCode);
			boolean valClass = this.validazione("segmentName", nameClass);
			//SE UNO DEI PARAMETRI NON E' VALIDO MANDO UN ECCEZIONE
			if (!valStateCode) {
				return this.getErrori("stateCode", stateCode);
			}
			if (!valClass) {
				return this.getErrori("nameClass", nameClass);
			}
			
			//ALTRIMENTI CONTINUO
			num = this.numEventi(setupModel(countryCode,stateCode,nameClass,periodo));
			obj.put("Country", countryCode);
			obj.put("State", stateCode);
			obj.put("Classification", nameClass);
			obj.put("NUM", num);
			response = obj;
			
			//AGGIUNGO IL DATO PERIODO
			response.put("Periodo", periodo);
		}
		
		//SINGOLO STATO, MULTI CLASSIFICAZIONI --- HO LE STATS
		if(states.length == 1 && classific.length > 1) {
			
			//VALIDAZIONE DEI PARAMETRI CHE L'UTENTE IMMETTE
			boolean valStateCode = this.validazione("stateCode", stateCode);
			if(!valStateCode) {
				return this.getErrori("stateCode", stateCode);
			}
			obj.put("State", stateCode);
			for (int i=0; i<classific.length; i++) {
				boolean valCla = this.validazione("segmentName", classific[i]);
				JSONObject singola = new JSONObject();
				if(!valCla) {
					singola.put("Classificazione", classific[i]);
					singola.put("Errore", "Classificazione non valida");
				}
				else {
					num = this.numEventi(setupModel(countryCode,stateCode,classific[i],periodo));
					tot += num;
					
					//CALCOLO STATISTICHE
					if(i==0) {
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
						if(num>max){
							max = num;
							indice_max = i;
						}
					}
					
					singola.put("Nome", classific[i]);
					singola.put("Numero Eventi", num);
				}
				objs.add(singola);
			}
			
			JSONArray arr_stats = new JSONArray();
			JSONObject stats = new JSONObject();
			stats.put("Genere Min Eventi", classific[indice_min] + "(" + min + ")");
			stats.put("Genere Max Eventi", classific[indice_max] + "(" + max + ")");
			stats.put("Media Eventi Cat", tot/classific.length);
			arr_stats.add(stats);
			
			obj.put("Classificazioni", objs);
			obj.put("Tot Eventi", tot);
			obj.put("Stats", arr_stats);
			response = obj;
			
			//AGGIUNGO IL DATO PERIODO
			response.put("Periodo", periodo);
		}
		
		//MULTI STATO, SINGOLA CLASSIFICAZIONE --- HO LE STATS
		if(states.length > 1 && classific.length == 1) {
			//VALIDAZIONE CLASSIFICAZIONE
			boolean valCla = this.validazione("segmentName", nameClass);
			if(!valCla) {
				obj.put("Classificazione", nameClass);
				obj.put("Errore", "Classificazione non valida");
				response = obj;
				return response;
			}
			//SE LA VALIDAZIONE E' ANDATA A BUON FINE
			obj.put("Classificazione", nameClass);
			
			for (int i=0; i<states.length; i++) {
				boolean valSta = this.validazione("stateCode", states[i]);
				JSONObject singola = new JSONObject();
				if(!valSta) {
					singola.put("Stato", states[i]);
					singola.put("Errore", "Stato non valido");
				}
				else {
					num = this.numEventi(setupModel(countryCode,states[i],nameClass,periodo));
					tot += num;
					
					//CALCOLO STATISTICHE
					if(i==0) {
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
						if(num>max){
							max = num;
							indice_max = i;
						}
					}

					singola.put("Stato", states[i]);
					singola.put("Numero Eventi", num);
				}
				objs.add(singola);
			}
			
			JSONArray arr_stats = new JSONArray();
			JSONObject stats = new JSONObject();
			stats.put("Stato Min Eventi", states[indice_min] + "(" + min + ")"); 
			stats.put("Stato Max Eventi", states[indice_max] + "(" + max + ")");
			stats.put("Media Eventi Stato", tot/states.length); //POTREI CASTARLE A DOUBLE
			arr_stats.add(stats);
			
			obj.put("States", objs); //AGGIUNGO I NUMERO DEGLI EVENTI
			obj.put("Stats", arr_stats); //AGGIUNGO LE STATS ALL'OGGETTO DI RISPOSTA
			response = obj;
			
			//AGGIUNGO IL DATO PERIODO
			response.put("Periodo", periodo);
		}
		
		//MULTI STATO, MULTI CLASSIFICAZIONE
		if(states.length > 1 && classific.length > 1) {
			//FACCIO L'ANALISI PER OGNI STATO
			//RICHIAMO QUESTO STESSO METODO
			for (String s : states) {
				JSONObject singolo = this.getStats(countryCode, s, nameClass, periodo);
				singolo.remove("Periodo"); //RIMUOVO LA PROPRIETA' PERIODO PER NON FARLA RIPETERE
				//singolo.remove("Stats"); //POTREI RIMUOVERE LE STATS PER FARNE ALTRE
				objs.add(singolo);
			}
			obj.put("States", objs);
			response = obj;
			
			//AGGIUNGO IL DATO PERIODO
			response.put("Periodo", periodo);
		}
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

	@SuppressWarnings("unchecked")
	private JSONObject getErrori(String parametro, String valore) {
		JSONObject errore = new JSONObject();
		errore.put(parametro, valore);
		errore.put("Errore", "Statistiche non disponibili");
		errore.put("Messaggio", "Pattern di chiamata compromesso");
		return errore;
	}
}
