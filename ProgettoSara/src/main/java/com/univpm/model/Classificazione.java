package com.univpm.model;

public class Classificazione {

	private String name;
	private String id;
	
	/**
	 * Costruttore di classificazione.
	 * @param name Nome della classificazione.
	 * @param id Id della classificazione.
	 */
	public Classificazione (String name, String id) {
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
