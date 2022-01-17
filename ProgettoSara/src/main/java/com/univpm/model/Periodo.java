package com.univpm.model;

import java.util.Calendar;

import com.univpm.exception.WrongPeriodException;

public class Periodo {

	//saranno i valori che andro' a comporre nel costruttore;
	private String startDate;
	private String endDate;
			
	//valori che mi servono per costruire la data
	private int startDay;
	private int startMonth;
	private int startYear;
	private int endDay;
	private int endMonth;
	private int endYear;
			
	//potrebbe servirci per prendere solo i dati dell'anno corrente o degli anni successivi
	private int year = Calendar.getInstance().get(Calendar.YEAR);
			
	public Periodo(int startDay, int startMonth, int startYear, int endDay, int endMonth, int endYear) throws WrongPeriodException {
		this.setStartDay(startDay);
		this.setStartMonth(startMonth);
		this.setStartYear(startYear);
		this.setEndDay(endDay);
		this.setEndMonth(endMonth);
		this.setEndYear(endYear);
				
		if(this.endYear < this.startYear) throw new WrongPeriodException("Anno non valido");
			//creo la stringa che mi serve a seconda dei valori che ho
		if(this.startMonth<=9 || this.startDay<=9) {
			if(this.startMonth<=9 && this.startDay<=9) this.startDate = this.startYear + "-0"+ this.startMonth + "-0" + this.startDay;
			else if(this.startMonth<=9) this.startDate = this.startYear + "-0"+ this.startMonth + "-" + this.startDay;
			else if(this.startDay<=9) this.startDate = this.startYear + "-"+ this.startMonth + "-0" + this.startDay;
		}
		else this.startDate = this.startYear + "-"+ this.startMonth + "-" + this.startDay;
				
		if(this.endMonth<=9 || this.endDay<=9) {
			if(this.endMonth<=9 && this.endDay<=9) this.endDate = this.endYear + "-0" + this.endMonth + "-0" + this.endDay;
			else if(this.endMonth<=9) this.endDate = this.endYear + "-0" + this.endMonth + "-" + this.endDay;
			else if(this.endDay<=9) this.endDate = this.endYear + "-" + this.endMonth + "-0" + this.endDay;
		}
		else this.endDate = this.endYear + "-" + this.endMonth + "-" + this.endDay;
	}
			
	public void setStartDay(int startDay) throws WrongPeriodException {
		if(startDay<=0 || startDay >31) throw new WrongPeriodException("Giorno non valido!");
		else this.startDay = startDay;
	}
			
	public void setStartMonth(int startMonth) throws WrongPeriodException {
		if(startMonth<=0 || startMonth>12) throw new WrongPeriodException("Mese non valido!");
		else this.startMonth = startMonth;
	}
			
	public void setStartYear(int startYear) throws WrongPeriodException {
		if(startYear<this.year) throw new WrongPeriodException("Anno non valido!");
		else this.startYear = startYear;
	}
			
	public void setEndDay(int endDay) throws WrongPeriodException {
		if(endDay<=0 || endDay >31) throw new WrongPeriodException("Giorno non valido!");
		else this.endDay = endDay;
	}
			
	public void setEndMonth(int endMonth) throws WrongPeriodException {
		if(endMonth<=0 || endMonth>12) throw new WrongPeriodException("Mese non valido!");
		else this.endMonth = endMonth;
	}
			
	public void setEndYear(int endYear) throws WrongPeriodException {
		if(endYear<this.year) throw new WrongPeriodException("Anno non valido!");
		else this.endYear = endYear;
	}

	public String getStartDate() {
		return this.startDate + "T00:00:00Z";
	}

	public String getEndDate() {
		return this.endDate + "T00:00:00Z";
	}
}
