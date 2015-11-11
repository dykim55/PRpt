package com.cyberone.report.main.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cyberone.report.main.dao.MainDao;

@Service
public class MainService {
	
	@Autowired
	private MainDao mainDao;
	
}
