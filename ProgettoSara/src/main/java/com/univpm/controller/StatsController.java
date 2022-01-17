package com.univpm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;

import com.univpm.service.I_Stats;

@RestController
public class StatsController {

	@Autowired
	@Qualifier("stats")
	private I_Stats response;
	
}
