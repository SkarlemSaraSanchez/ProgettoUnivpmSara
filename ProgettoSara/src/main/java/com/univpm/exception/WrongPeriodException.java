package com.univpm.exception;

public class WrongPeriodException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public WrongPeriodException() {
		super();
		System.out.println(getMex());
	}
	public WrongPeriodException(String msg) {
		super(msg);
	}
	
	
	public String getMex() {
		return "ERROR: Il periodo inserito non è valido!";
	}
}
