package com.univpm.model;

public class Parametro {

	private String chiave;
	 private String valoreStringa;
	 private boolean valorebooelan;
	 private int indexCall;
	 
	 public Parametro(String a){
		this.chiave=a;
	 }
	 
	 public Parametro(String x, long i) {
		this.chiave = x;
		this.valoreStringa = "" + i;
	 }
	 
	 public Parametro(String k,boolean b){
		 this.chiave=k;
		 this.valorebooelan=b; 
	 }

	 public Parametro(String k, String v) {
		 this.chiave=k;
		 this.valoreStringa=v;
	}

	public void setChiave(String k) {
		 this.chiave=k;
	 }
	 
	 public void setValueQueryParams(String v) {
		 this.valoreStringa=v;
	 }
	 	
	 public String getChiave() {
		 return this.chiave;
	 }
		
	 public String getValueQueryParams() {
		return this.valoreStringa;
	 }
	 
	 public boolean getboolean() {
		return this.valorebooelan;
	 }
	
	 public void setIndex(int i) {
		this.indexCall=i;
	 }
	
	 public int getIndexCall() {
		return this.indexCall;
	 }
}
