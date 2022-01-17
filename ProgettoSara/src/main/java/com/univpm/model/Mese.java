package com.univpm.model;

public class Mese {

	private String data_inizio;
	private String data_fine;
	private String nome_mese;
	
	public Mese(String data_inizio, String data_fine, String nome_mese) {
		this.data_inizio = data_inizio;
		this.data_fine = data_fine;
		this.nome_mese = nome_mese;
	}

	public String getData_inizio() {
		return data_inizio;
	}

	public String getData_fine() {
		return data_fine;
	}
	
	public String getNome_mese() {
		return this.nome_mese;
	}
}
