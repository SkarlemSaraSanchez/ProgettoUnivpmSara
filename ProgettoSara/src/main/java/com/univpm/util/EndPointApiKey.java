package com.univpm.util;

import com.univpm.model.Parametro;

public class EndPointApiKey extends EndPoint {

	private Parametro apiKey=new Parametro ("",""); 
	String addApiKey;
	
	public EndPointApiKey(String url,Parametro apiKey) {
		super(url);
		
		int i=listaParametri.size(); // i vale 0
		apiKey.setIndex(i);
		this.apiKey=apiKey;
	}
	
	public EndPointApiKey() {
		this.apiKey = new Parametro("apikey", "068qHZJdPGcLqwUJG5lkC24XfTKmPesP");
	}
	
	public void addKEy() {
	if(listaParametri.size()==0) {
		 addApiKey="?" + this.apiKey.getChiave()+"="+this.apiKey.getValueQueryParams();
	 }
	 else {
		 addApiKey="&"+this.apiKey.getChiave()+"="+this.apiKey.getValueQueryParams(); 
		 }
	}
	
	public String getApi() {
		String api=domain+i_path;
		costruisciApi();
		addKEy();
		if(QueryString.length()>1){
			api+=QueryString;
			if(addQueryString.length()>1) {
				api+=addQueryString;
			}
		}
		api+=addApiKey;
		System.out.println("endpoint con autenticazione------------>"+api);
		pulisciqueryString();
		indexParametro = 0;
		return api;
	}
	
	public Parametro getApiKey() {
		return this.apiKey;
	}
		
	public void setApiKey(String k,String v) {
		this.apiKey.setChiave(k);
		this.apiKey.setValueQueryParams(v);
	}
}
