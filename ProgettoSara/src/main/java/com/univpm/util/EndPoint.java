package com.univpm.util;

import java.util.ArrayList;

import com.univpm.model.Parametro;


public class EndPoint {

	ArrayList<Parametro> listaParametri= new ArrayList<Parametro>();
	ArrayList<Parametro> ordineParametri_queryString = new ArrayList<Parametro>();
	
	String domain;
	String i_path;
	String QueryString = ""; 
	String addQueryString = "";
	int indiceDiChiamta;
	
	public EndPoint(String url) {
		this.domain = url;
	}
	
	public EndPoint() {
	}
 
	public String getDomain() {
		return domain;
	}
 
	public void setPath(String path) {
		this.i_path=path + ".json"; //CHIEDERE A GE
	}

	public String getPath() {
		return  this.i_path;
	}
	
	int indexParametro=0;//setChiaveValore se aggiunge un nuovo parametro incrementa questo valore
	public void setChiaveValore (String chiave,String Valore) {

		boolean continua=avvisaIncasoDiParametriIncoerenti(chiave, Valore);
		if(continua) {	
			boolean trovato=false;	 
			if(listaParametri.size()==0) {
				Parametro p=new Parametro(chiave,Valore);
				p.setIndex(indexParametro);
				listaParametri.add(p);
				indexParametro++; 
				System.out.println("esco");
			}
			else {
				if(listaParametri.size()>0) {
					for(int i =0;i<listaParametri.size();i++) {
			
						String tmp=listaParametri.get(i).getChiave();
						if(listaParametri.get(i).getChiave().equals(chiave)) {
				
							if(!Valore.equals("") && !Valore.equals("-")) {
								listaParametri.get(i).setChiave(chiave);
								listaParametri.get(i).setValueQueryParams(Valore);
								listaParametri.get(i).setIndex(indexParametro);
								indexParametro++;
								trovato=true;
								break;
							}
						}
						else {
							trovato=false;
						}	
					}
					if(!trovato){
						Parametro p = new Parametro(chiave,Valore);
						int index=indexParametro;
						p.setIndex(index);
						listaParametri.add(p);
						indexParametro++;
					}
				}
			}
		}
	}

	public boolean costruisciApi () {
		int count = listaParametri.size();
		boolean chiamataConParams = false;
		
		for(int i=0; i<listaParametri.size(); i++) {//itero la lista dei parametri di un endpoint
			if(!listaParametri.get(i).getValueQueryParams().equals("")) {
				chiamataConParams=true;
			}
		}
				
		if(chiamataConParams) {//ha trovato un valore
			boolean primovalore = false;
			
			int indiceDelPrimoParametro = 0;
			for(int i=0; i<count; i++) {
				if(!listaParametri.get(i).getValueQueryParams().equals("") && !listaParametri.get(i).getChiave().equals("") ) {
					int compare=listaParametri.get(i).getIndexCall();
					if(compare<indiceDelPrimoParametro) {
						indiceDelPrimoParametro=compare;
					}
				}
			}
			for(int j=0;j<count;j++) {
				if(listaParametri.get(j).getIndexCall()==indiceDelPrimoParametro && !listaParametri.get(j).getValueQueryParams().equals("") ) {
					QueryString+= "?" + listaParametri.get(j).getChiave()+"="+listaParametri.get(j).getValueQueryParams();	
				}
				else {
					if(!listaParametri.get(j).getValueQueryParams().equals("") ) {
					addQueryString+= "&" + listaParametri.get(j).getChiave()+"="+listaParametri.get(j).getValueQueryParams();
					}
				}
			}
		}
		return chiamataConParams;
	}
	
	public String getApi() {
		costruisciApi() ;//mi aggiorna con nuovi parametri
	 
		pulisciqueryString();
		indexParametro=0;//riporto a zero l indice
		return "";
	}	
 
	//pulisce il l'arraylist dei parametri ma il suo size non cambia
	public void pulisciqueryString() {
		
		QueryString="";
		addQueryString="";
		System.out.println("pulisci");
		for(int i= 0;i<listaParametri.size();i++) {
			listaParametri.get(i).setValueQueryParams("");
			listaParametri.get(i).setIndex(0);
		}
		this.i_path="";
	}
 
	public void setValueForParams(String update) {
		for(int i= 0;i<listaParametri.size();i++) {
			listaParametri.get(i).setValueQueryParams("");;
		}
	}
 
	public ArrayList <Parametro> getParametri(){
		return listaParametri;
	}
	
	public Parametro getApiKey() {
		Parametro x=new Parametro("","");
		return x;
	}
	
	//PER LA CONSOLE
	public boolean avvisaIncasoDiParametriIncoerenti(String k,String v) {
		int codiceerrore=0;
		boolean responce=true;
		String msg = "intercettato un  parametro ma hai effettuato una chiamata";		
		if(k.equals("")) {
			//incremento di 1
			codiceerrore++;
			responce=false; 
		}
		if( v.equals("") || v.equals("-")) { //MODIFICA SARA
			//incfremento di 2
			codiceerrore++;
			codiceerrore++;	
			responce=false; 
		}
		switch(codiceerrore) {
		 	case 1:
		 		msg = msg  + " senza chiave per il valore : "+ v;   
		 		break;
		 	case 2:
		 		msg = msg  + " senza valore per la chiave : "+ k;
		 		break;
		 	case 3:
		 		msg=msg + "  senza valorizzare chiave e valore ";
		}
		if(!responce) {
		      msg=msg + " \n il parametro quindi non è stato aggiunto alla queryString ";
		      System.out.println(msg);
		}
		return responce;	 
	 }
	
}
