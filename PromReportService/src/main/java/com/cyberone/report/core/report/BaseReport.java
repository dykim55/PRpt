package com.cyberone.report.core.report;



public abstract class BaseReport {

	protected final boolean bLocalDebug = true;
	protected final String REPORT_DIR = "D:/Project/prom/infra/prom-webconsole/src/main/resources/WEB-INF/reports/synthesis/";
	protected final String BASE_DIR = "/jasper";
	
	//protected  final boolean bLocalDebug = false;
	//protected  final String REPORT_DIR = "WEB-INF/reports/synthesis/";
	//protected  final String BASE_DIR = "/jasper";

	protected final String promHome = System.getProperty("prom.home");
	
	protected final String ALLOW = "1";
	protected final String CUTOFF = "0";
	
	protected final int INBOUND = 1;
	protected final int OUTBOUND = 0;

}
