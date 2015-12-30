package com.cyberone.report.core.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyberone.report.Constants;
import com.cyberone.report.core.dao.BaseDao;
import com.cyberone.report.core.datasource.SynthesisDataSource;
import com.cyberone.report.utils.StringUtil;
import com.mongodb.DBObject;



public class BaseReport {

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

	protected BaseDao baseDao;

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	protected void PerformanceInfo(BaseDao baseDao, HashMap<String, Object> reportData, List<String> contentsList, int ItemNo, int nC, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		List<HashMap<String, Object>> dataSource1 = null;
		List<HashMap<String, Object>> dataSource2 = null;
		
		Iterable<DBObject> dbResult = null;
		if (StringUtil.convertString(reportData.get("reportType")).equals(Constants.REPORT_MONTHLY)) {
			dbResult = baseDao.getPerformanceInfo("MON", assetCode, sStartDay, sEndDay);
		} else {
			dbResult = baseDao.getPerformanceInfo("DY", assetCode, sStartDay, sEndDay);
		}
		
		reportData.put("OPT99_CTG_UNIT", "일");
		
		if (StringUtil.convertString(reportData.get("reportType")).equals(Constants.REPORT_DAILY)) {
			int nS = 0;
			for (DBObject mVal : dbResult) {
				dataSource1 = new ArrayList<HashMap<String, Object>>();
				String sName = (String)mVal.get("name");
				
				if (((Number)mVal.get("max")).longValue() == 0 && ((Number)mVal.get("min")).longValue() == 0 && ((Number)mVal.get("avg")).longValue() == 0) {
					continue;
				}
				
				String sTitle = "    " + ItemNo + "." + nC + "." + ++nS + " " + sName + " 추이";
				reportData.put(sName, sTitle);
				contentsList.add(sTitle);
				logger.debug(sName + " 추이");
				
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		map.put("gubun", sName);
	    		map.put("max", ((Number)mVal.get("max")).longValue());
	    		map.put("avg", ((Number)mVal.get("avg")).longValue());
	    		map.put("min", ((Number)mVal.get("min")).longValue());
	    		
	    		dataSource1.add(map);
	    		logger.debug(map.toString());
	    		
	    		Iterable<DBObject> dbTrend = baseDao.getPerformanceTrend("HR", assetCode, baseDao.addDate(sStartDay, -1), sEndDay, sName);
	    	
            	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
            	for (DBObject dVal : dbTrend) {
            		String sYear = ((Integer)dVal.get("year")).toString();
            		String sMonth = ((Integer)dVal.get("month")).toString();
            		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
            		String sDay = ((Integer)dVal.get("day")).toString();
            		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
            		String sHour = ((Integer)dVal.get("hour")).toString();
            		sHour = sHour.length() == 1 ? "0" + sHour : sHour;
            		mapResult.put(sYear + sMonth + sDay + sHour, dVal);
            	}
    			
            	List<String> hourDay = baseDao.getPeriodHour(sStartDay, -1);
            	
            	dataSource2 = new ArrayList<HashMap<String, Object>>();
    			int nDayCount = 0;
        		for (String tDay : hourDay) {
        			nDayCount++;
        			for (int h = 0; h < 24; h++) { //시간
    		    		DBObject val = mapResult.get(tDay + (h < 10 ? "0" + h : String.valueOf(h)));
    	        		HashMap<String, Object> tmpMap = new HashMap<String, Object>();
    	        		tmpMap.put("series", (nDayCount < 2) ? "전일 평균" : "금일 평균");
    	        		tmpMap.put("category", String.valueOf(h));
    	        		tmpMap.put("value", val != null ? ((Number)val.get("avg")).longValue() : 0);
    	        		dataSource2.add(tmpMap);
    	        		logger.debug(tmpMap.toString());
        			}
    	    	}
    			
        		nDayCount = 0;
        		for (String tDay : hourDay) {
        			nDayCount++;
        			for (int h = 0; h < 24; h++) { //시간
        				DBObject val = mapResult.get(tDay + (h < 10 ? "0" + h : String.valueOf(h)));
    	        		HashMap<String, Object> tmpMap = new HashMap<String, Object>();
    	        		tmpMap.put("series", (nDayCount < 2) ? "전일 최고" : "금일 최고");
    	        		tmpMap.put("category", String.valueOf(h));
    	        		tmpMap.put("value", val != null ? ((Number)val.get("max")).longValue() : 0);
    	        		dataSource2.add(tmpMap);
    	        		logger.debug(tmpMap.toString());
        			}
            	}
        		reportData.put("OPT99_CTG_UNIT", "시간");
        		reportData.put("OPT99_T_" + sName, new SynthesisDataSource(dataSource1));
        		reportData.put("OPT99_C_" + sName, new SynthesisDataSource(dataSource2));
			}
		} else if (StringUtil.convertString(reportData.get("reportType")).equals(Constants.REPORT_WEEKLY)) {
			int nS = 0;
			for (DBObject mVal : dbResult) {
				dataSource1 = new ArrayList<HashMap<String, Object>>();
				String sName = (String)mVal.get("name");
				
				if (((Number)mVal.get("max")).longValue() == 0 && ((Number)mVal.get("min")).longValue() == 0 && ((Number)mVal.get("avg")).longValue() == 0) {
					continue;
				}
				
				String sTitle = "    " + ItemNo + "." + nC + "." + ++nS + " " + sName + " 추이";
				reportData.put(sName, sTitle);
				contentsList.add(sTitle);
				logger.debug(sName + " 추이");
				
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		map.put("gubun", sName);
	    		map.put("max", ((Number)mVal.get("max")).longValue());
	    		map.put("avg", ((Number)mVal.get("avg")).longValue());
	    		map.put("min", ((Number)mVal.get("min")).longValue());
	    		
	    		dataSource1.add(map);
	    		logger.debug(map.toString());
	    		
	    		Iterable<DBObject> dbTrend = baseDao.getPerformanceTrend("DY", assetCode, baseDao.addDate(sStartDay, -7), sEndDay, sName);
	    	
            	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
            	for (DBObject dVal : dbTrend) {
            		String sYear = ((Integer)dVal.get("year")).toString();
            		String sMonth = ((Integer)dVal.get("month")).toString();
            		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
            		String sDay = ((Integer)dVal.get("day")).toString();
            		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
            		mapResult.put(sYear + sMonth + sDay, dVal);
            	}
    			
            	List<String> dayPeriod = baseDao.getPeriodDay(baseDao.addDate(sStartDay,-7), sEndDay, 1);
            	
            	dataSource2 = new ArrayList<HashMap<String, Object>>();
            	
            	String[] saCtg = {"D-6", "D-5", "D-4", "D-3", "D-2", "D-1", "D"};
            	int nCtg = 0;
    			int nDayCount = 0;
        		for (String tDay : dayPeriod) {
        			nDayCount++;
		    		DBObject val = mapResult.get(tDay);
	        		HashMap<String, Object> tmpMap = new HashMap<String, Object>();
	        		tmpMap.put("series", (nDayCount <= 7) ? "전주 평균" : "금주 평균");
	        		tmpMap.put("category", saCtg[nCtg++%7]);
	        		tmpMap.put("value", val != null ? ((Number)val.get("avg")).longValue() : 0);
	        		dataSource2.add(tmpMap);
	        		logger.debug(tmpMap.toString());
    	    	}
    			
        		nCtg = 0;
        		nDayCount = 0;
        		for (String tDay : dayPeriod) {
        			nDayCount++;
    				DBObject val = mapResult.get(tDay);
	        		HashMap<String, Object> tmpMap = new HashMap<String, Object>();
	        		tmpMap.put("series", (nDayCount <= 7) ? "전주 최고" : "금주 최고");
	        		tmpMap.put("category", saCtg[nCtg++%7]);
	        		tmpMap.put("value", val != null ? ((Number)val.get("max")).longValue() : 0);
	        		dataSource2.add(tmpMap);
	        		logger.debug(tmpMap.toString());
            	}
        		reportData.put("OPT99_T_" + sName, new SynthesisDataSource(dataSource1));
        		reportData.put("OPT99_C_" + sName, new SynthesisDataSource(dataSource2));
			}
		} else if (StringUtil.convertString(reportData.get("reportType")).equals(Constants.REPORT_MONTHLY)) {
			int nS = 0;
			for (DBObject mVal : dbResult) {
				dataSource1 = new ArrayList<HashMap<String, Object>>();
				String sName = (String)mVal.get("name");
				
				if (((Number)mVal.get("max")).longValue() == 0 && ((Number)mVal.get("min")).longValue() == 0 && ((Number)mVal.get("avg")).longValue() == 0) {
					continue;
				}
				
				String sTitle = "    " + ItemNo + "." + nC + "." + ++nS + " " + sName + " 추이";
				reportData.put(sName, sTitle);
				contentsList.add(sTitle);
				logger.debug(sName + " 추이");
				
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		map.put("gubun", sName);
	    		map.put("max", ((Number)mVal.get("max")).longValue());
	    		map.put("avg", ((Number)mVal.get("avg")).longValue());
	    		map.put("min", ((Number)mVal.get("min")).longValue());
	    		
	    		dataSource1.add(map);
	    		logger.debug(map.toString());
	    		
	    		Iterable<DBObject> dbTrend = baseDao.getPerformanceTrend("DY", assetCode, baseDao.addMonth(sStartDay, -1), sEndDay, sName);
	    	
            	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
            	for (DBObject dVal : dbTrend) {
            		String sYear = ((Integer)dVal.get("year")).toString();
            		String sMonth = ((Integer)dVal.get("month")).toString();
            		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
            		String sDay = ((Integer)dVal.get("day")).toString();
            		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
            		mapResult.put(sYear + sMonth + sDay, dVal);
            	}
    			
            	List<String> dayPeriod = baseDao.getPeriodDay(sStartDay, sEndDay, -1, 1);
            	
        		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        		Date endDate = sdf.parse(sEndDay);
        		Calendar endCal = baseDao.getCalendar(endDate);
        		endCal.add(Calendar.MONTH, -2);
            	
            	dataSource2 = new ArrayList<HashMap<String, Object>>();
        		for (String tDay : dayPeriod) {
            		if (Integer.valueOf(tDay.substring(6,8)) == Integer.valueOf(sStartDay.substring(8, 10))) {
            			endCal.add(Calendar.MONTH, 1);
            		}
		    		DBObject val = mapResult.get(tDay);
	        		HashMap<String, Object> tmpMap = new HashMap<String, Object>();
	        		tmpMap.put("series", (endCal.get(Calendar.MONTH)+1) + "월 평균");
	        		tmpMap.put("category", tDay.substring(6, 8));
	        		tmpMap.put("value", val != null ? ((Number)val.get("avg")).longValue() : 0);
	        		dataSource2.add(tmpMap);
	        		logger.debug(tmpMap.toString());
    	    	}
    			
    			endDate = sdf.parse(sEndDay);
    			endCal = baseDao.getCalendar(endDate);
    			endCal.add(Calendar.MONTH, -2);

        		for (String tDay : dayPeriod) {
            		if (Integer.valueOf(tDay.substring(6,8)) == Integer.valueOf(sStartDay.substring(8, 10))) {
            			endCal.add(Calendar.MONTH, 1);
            		}
    				DBObject val = mapResult.get(tDay);
	        		HashMap<String, Object> tmpMap = new HashMap<String, Object>();
	        		tmpMap.put("series", (endCal.get(Calendar.MONTH)+1) + "월 최고");
	        		tmpMap.put("category", tDay.substring(6, 8));
	        		tmpMap.put("value", val != null ? ((Number)val.get("max")).longValue() : 0);
	        		dataSource2.add(tmpMap);
	        		logger.debug(tmpMap.toString());
            	}
        		reportData.put("OPT99_T_" + sName, new SynthesisDataSource(dataSource1));
        		reportData.put("OPT99_C_" + sName, new SynthesisDataSource(dataSource2));
			}
		} else if (StringUtil.convertString(reportData.get("reportType")).equals(Constants.REPORT_PERIOD)) {
			int nS = 0;
			for (DBObject mVal : dbResult) {
				String sName = (String)mVal.get("name");
				
				if (((Number)mVal.get("max")).longValue() == 0 && ((Number)mVal.get("min")).longValue() == 0 && ((Number)mVal.get("avg")).longValue() == 0) {
					continue;
				}
				
				String sTitle = "    " + ItemNo + "." + nC + "." + ++nS + " " + sName + " 추이";
				reportData.put(sName, sTitle);
				contentsList.add(sTitle);
				logger.debug(sName + " 추이");
				
	    		Iterable<DBObject> dbTrend = baseDao.getPerformanceTrend("DY", assetCode, sStartDay, sEndDay, sName);
	    	
            	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
            	for (DBObject dVal : dbTrend) {
            		String sYear = ((Integer)dVal.get("year")).toString();
            		String sMonth = ((Integer)dVal.get("month")).toString();
            		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
            		String sDay = ((Integer)dVal.get("day")).toString();
            		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
            		mapResult.put(sYear + sMonth + sDay, dVal);
            	}
    			
            	List<String> dayPeriod = baseDao.getPeriodDay(sStartDay, sEndDay, 1);
            	
            	dataSource2 = new ArrayList<HashMap<String, Object>>();
        		for (String tDay : dayPeriod) {
		    		DBObject val = mapResult.get(tDay);
	        		HashMap<String, Object> tmpMap = new HashMap<String, Object>();
	        		tmpMap.put("series", "평균");
	        		tmpMap.put("category", tDay.substring(6, 8));
	        		tmpMap.put("value", val != null ? ((Number)val.get("avg")).longValue() : 0);
	        		dataSource2.add(tmpMap);
	        		logger.debug(tmpMap.toString());
    	    	}
    			
        		for (String tDay : dayPeriod) {
    				DBObject val = mapResult.get(tDay);
	        		HashMap<String, Object> tmpMap = new HashMap<String, Object>();
	        		tmpMap.put("series", "최고");
	        		tmpMap.put("category", tDay.substring(6, 8));
	        		tmpMap.put("value", val != null ? ((Number)val.get("max")).longValue() : 0);
	        		dataSource2.add(tmpMap);
	        		logger.debug(tmpMap.toString());
            	}
        		reportData.put("OPT99_C_" + sName, new SynthesisDataSource(dataSource2));
			}
		}
		
	}		
	
	public String removeLastCrLf(String str) {
		if (str == null || str.length() == 0) return str;
		if (str.charAt(str.length()-1) == '\n') {
			return removeLastCrLf(str.substring(0, str.length()-1));
		}
		if (str.charAt(str.length()-1) == '\r') {
			return removeLastCrLf(str.substring(0, str.length()-1));
		}
		return str;
	}
	
	public String getRptTypeName(String rpt) {
		if (rpt.equals("5")) {
			return "상황보고서";
		} else if (rpt.equals("6")) {
			return "장애보고서";
		} else if (rpt.equals("7")) {
			return "작업수행계획서";
		} else if (rpt.equals("8")) {
			return "작업완료보고서";
		}
		return "-";
	}

	public String getDryTypeName(String sct) {
		if (sct.equals("1")) {
			return "방화벽 정책변경";
		} else if (sct.equals("6")) {
			return "탐지룰 설정변경";
		} else if (sct.equals("7")) {
			return "패턴 업데이트";
		} else if (sct.equals("8")) {
			return "패치";
		}
		return "-";
	}

	public String getReqTypeName(String rpt) {
		if (rpt.equals("2")) {
			return "일반요청";
		} else if (rpt.equals("3")) {
			return "침해사고 분석 요청";
		} else if (rpt.equals("4")) {
			return "기술지원 요청";
		}
		return "-";
	}

	public String getNewsTypeName(String sct) {
		if (sct.equals("R")) {
			return "일일뉴스클리핑";
		} else if (sct.equals("T")) {
			return "주간보안동향";
		} else if (sct.equals("P")) {
			return "상황전파문";
		} else if (sct.equals("B")) {
			return "악성코드 유포&경유지";
		}
		return "-";
	}
	
}
