package com.cyberone.report;

import java.net.URL;

import com.maxmind.geoip.LookupService;

public class Constants {

	public static final String	REPORT_DAILY = "1";		//일일보고서
	public static final String	REPORT_WEEKLY = "2";	//주간보고서
	public static final String	REPORT_MONTHLY = "3";	//월간보고서
	public static final String	REPORT_PERIOD = "4";	//기간보고서
	
	/** 침해위협연동구분 */
	public static final int	DTCTGEAR_CODEPRO = 1;
	public static final int	DTCTGEAR_WSD = 2;
	public static final int	DTCTGEAR_SNORT = 3;
	
    private static LookupService cl = null;
    
	public static String getReportTypeName(String type){
		String result;
		switch(Integer.parseInt(type)){
			case 1 : result = "일일보고서";
				break;
			case 2 : result = "주간보고서";
				break;
			case 3 : result = "월간보고서";
				break;
			case 4 : result = "임의기간보고서";
				break;
			default : result = "-";
		}
		return result;
	}
	
	public static String getCountryCode(String sIp) {
		try {
			if (cl == null) {
				URL url = Constants.class.getClassLoader().getResource("GeoIP.dat");
				cl = new LookupService(url.getFile(), LookupService.GEOIP_MEMORY_CACHE);
			}
		} catch (Exception e) {
			return "";
		}
		return cl.getCountry(sIp).getCode().equals("--") ? "-" : cl.getCountry(sIp).getCode();
	}
	
}