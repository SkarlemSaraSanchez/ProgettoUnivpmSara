package com.univpm.model;

import java.util.HashMap;

public class Anno {

	private String anno;
	private HashMap<Integer,Mese> mesi;
	
	
	/**
	 * Cotruttore dell'oggetto anno.
	 * @param anno indica l'anno relativo all'oggetto che si vuole creare.
	 */
	
	public Anno(String anno) {
		this.anno = anno;
		int anno_successivo = Integer.parseInt(anno)+1;
		this.mesi = new HashMap<Integer,Mese>();
		this.mesi.put(1, new Mese(this.anno+"-01-01", this.anno+"-02-01", "Gennaio"));
		this.mesi.put(2, new Mese(this.anno+"-02-01", this.anno+"-03-01", "Febbraio"));
		this.mesi.put(3, new Mese(this.anno+"-03-01", this.anno+"-04-01", "Marzo"));
		this.mesi.put(4, new Mese(this.anno+"-04-01", this.anno+"-05-01", "Aprile"));
		this.mesi.put(5, new Mese(this.anno+"-05-01", this.anno+"-06-01", "Maggio"));
		this.mesi.put(6, new Mese(this.anno+"-06-01", this.anno+"-07-01", "Giugno"));
		this.mesi.put(7, new Mese(this.anno+"-07-01", this.anno+"-08-01", "Luglio"));
		this.mesi.put(8, new Mese(this.anno+"-08-01", this.anno+"-09-01", "Agosto"));
		this.mesi.put(9, new Mese(this.anno+"-09-01", this.anno+"-10-01", "Settembre"));
		this.mesi.put(10, new Mese(this.anno+"-10-01", this.anno+"-11-01", "Ottobre"));
		this.mesi.put(11, new Mese(this.anno+"-11-01", this.anno+"-12-01", "Novembre"));
		this.mesi.put(12, new Mese(this.anno+"-12-01", anno_successivo+"-01-01", "Dicembre"));
	}

	/**
	 * Ritorna la proprieta' anno.
	 * @return anno
	 */
	public String getAnno() {
		return anno;
	}

	/**
	 * Ritorna la lista chiave valore dei mesi che compongono l'anno scelto.
	 * @return mesi 
	 */
	public HashMap<Integer, Mese> getMesi() {
		return mesi;
	}
}
