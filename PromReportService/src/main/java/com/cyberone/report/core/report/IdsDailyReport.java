package com.cyberone.report.core.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;

import com.cyberone.report.Constants;
import com.cyberone.report.core.dao.IdsDao;
import com.cyberone.report.core.datasource.SynthesisDataSource;
import com.cyberone.report.model.UserInfo;
import com.cyberone.report.utils.StringUtil;
import com.mongodb.DBObject;

public class IdsDailyReport extends BaseReport {
	
	private IdsDao idsDao;

	private Logger logger = LoggerFactory.getLogger(getClass());

	private UserInfo userInfo;
	
	public IdsDailyReport(UserInfo userInfo) {
		this.userInfo = userInfo;
		this.idsDao = new IdsDao(userInfo.getPromDb(), userInfo.getReportDb());
	}
	
	/* 
	 * 침입탐지(IDS) 일일보고서 통계데이타 생성
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getDataSource(String sReportType, String sSearchDate, HashMap<String, Object> hMap) throws Exception {
		logger.debug("침입탐지(IDS) 일일보고서 통계데이타 생성");
		
		String sStartDay = "";
		String sEndDay = "";
		
		String sAssetCode = StringUtil.convertString(hMap.get("assetCode"));
		int assetCode = Integer.valueOf(sAssetCode.substring(0, sAssetCode.indexOf("_")));

		//조회 시작일/종료일
		sStartDay = sSearchDate;
		sEndDay = sSearchDate;
		
		HashMap<String,Object> hFormMap = (HashMap<String,Object>)hMap.get("formData");
		
		String sEtc = "";
		HashMap<String,Object> hData = null;
		if (hFormMap == null) {
			DBObject dbObj = idsDao.selectAutoReportForm(Integer.parseInt(sReportType), assetCode);
			hData = (new ObjectMapper()).readValue(StringUtil.convertString(dbObj.get("formData")), HashMap.class);
		} else {
			hData = (HashMap<String,Object>)hFormMap.get("data");
			sEtc = StringUtil.convertString(hFormMap.get("etc"));
		}

		HashMap<String, Object> reportData = new HashMap<String, Object>();
		
		int nChoice = 0;
		for (Entry<String, Object> e : hData.entrySet()) {
			
			switch (e.getKey()) {
				case "opt1" :	//전체 탐지로그 발생추이
					push("항목: 전체 탐지로그 발생추이");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd1")));
					All_DetectLog_Trend(reportData, -1, assetCode, sStartDay, sEndDay, nChoice);
					break;
				case "opt2" :	//외부에서 내부로의 전체 탐지로그 발생추이 
					push("항목: 외부에서 내부로의 전체 탐지로그 발생추이");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd2")));
					Direction_DetectLog_Trend(reportData, INBOUND, assetCode, sStartDay, sEndDay, nChoice);
					break;
				case "opt3" : 	//내부에서 외부로의 전체 탐지로그 발생추이
					push("항목: 내부에서 외부로의 전체 탐지로그 발생추이");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd5")));
					Direction_DetectLog_Trend(reportData, OUTBOUND, assetCode, sStartDay, sEndDay, nChoice);
					break;
				case "opt4" : 	//외부에서 내부로의 전체 탐지로그 & 이벤트 TOP (차트, 표)
					push("항목: 외부에서 내부로의 전체 탐지로그 & 이벤트 TOP (차트, 표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd4")));
					Direction_EventTopN(reportData, INBOUND, assetCode, sStartDay, sEndDay, nChoice, !StringUtil.isEmpty(hData.get("ck4")));
					break;
				case "opt5" : 	//내부에서 외부로의 전체 탐지로그 & 이벤트 TOP (차트, 표)
					push("항목: 내부에서 외부로의 전체 탐지로그 & 이벤트 TOP (차트, 표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd5")));
					Direction_EventTopN(reportData, OUTBOUND, assetCode, sStartDay, sEndDay, nChoice, !StringUtil.isEmpty(hData.get("ck5")));
					break;
				case "opt6" : 	//외부에서 내부로의 전체 탐지로그 & 신규 탐지 이벤트 현황 (차트, 표)
					push("항목: 외부에서 내부로의 전체 탐지로그 & 신규 탐지 이벤트 현황 (차트, 표)");
					New_DetectLog_Condition(reportData, INBOUND, assetCode, sStartDay, sEndDay);
					break;
				case "opt7" : 	//내부에서 외부로의 전체 탐지로그 & 신규 탐지 이벤트 현황 (차트, 표)
					push("항목: 내부에서 외부로의 전체 탐지로그 & 신규 탐지 이벤트 현황 (차트, 표)");
					New_DetectLog_Condition(reportData, OUTBOUND, assetCode, sStartDay, sEndDay);
					break;
				case "opt8" : 	//외부에서 내부로의 전체 탐지로그 & 이전대비 2배증가된 이벤트 현황 (차트, 표)
					push("항목: 외부에서 내부로의 전체 탐지로그 & 이전대비 2배증가된 이벤트 현황 (차트, 표)");
					Double_DetectLog_Condition(reportData, INBOUND, assetCode, sStartDay, sEndDay);
					break;

				
				case "opt9" : 	//외부에서 내부로의 전체 탐지로그 & SIP TOP (표)
					push("항목: 외부에서 내부로의 전체 탐지로그 & SIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd9")));
					ALL_DetectLog_TopN(reportData, INBOUND, assetCode, sStartDay, sEndDay, true, nChoice, !StringUtil.isEmpty(hData.get("ck9")));
					break;
				case "opt10" : 	//내부에서 외부로의 전체 탐지로그 & SIP TOP (표)
					push("항목: 내부에서 외부로의 전체 탐지로그 & SIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd10")));
					ALL_DetectLog_TopN(reportData, OUTBOUND, assetCode, sStartDay, sEndDay, true, nChoice, !StringUtil.isEmpty(hData.get("ck10")));
					break;
				case "opt11" : 	//외부에서 내부로의 전체 탐지로그 & DIP TOP (표)
					push("항목: 외부에서 내부로의 전체 탐지로그 & DIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd11")));
					ALL_DetectLog_TopN(reportData, INBOUND, assetCode, sStartDay, sEndDay, false, nChoice, false);
					break;
				case "opt12" : 	//내부에서 외부로의 전체 탐지로그 & DIP TOP (표)
					push("항목: 내부에서 외부로의 전체 탐지로그 & DIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd12")));
					ALL_DetectLog_TopN(reportData, OUTBOUND, assetCode, sStartDay, sEndDay, false, nChoice, false);
					break;
				case "opt13" : 	//외부에서 내부로의 전체 탐지로그 & 서비스 TOP10 (차트)
					push("항목: 외부에서 내부로의 전체 탐지로그 & 서비스 TOP10 (차트)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd13")));
					ALL_ServiceTop10(reportData, INBOUND, assetCode, sStartDay, sEndDay, !StringUtil.isEmpty(hData.get("ck13")));
					break;
				case "opt14" : 	//내부에서 외부로의 전체 탐지로그 & 서비스 TOP10 (차트)
					push("항목: 내부에서 외부로의 전체 탐지로그 & 서비스 TOP10 (차트)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd14")));
					ALL_ServiceTop10(reportData, OUTBOUND, assetCode, sStartDay, sEndDay, !StringUtil.isEmpty(hData.get("ck14")));
					break;
					
			}
		}
		return reportData;
	}

	private void All_DetectLog_Trend(HashMap<String, Object> reportData, int nDirection, int assetCode, String sStartDay, String sEndDay, int nChoice) throws Exception {
		
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();

		if (nChoice == 1) {	//해당일
			Iterable<DBObject> dbResult = idsDao.AllDetectLogTrend("HR", 1, assetCode, sStartDay, sEndDay);
		
	    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
	    	for (DBObject val : dbResult) {
	    		String sDirection = (String)val.get("direction");
	    		String sYear = ((Integer)val.get("year")).toString();
	    		String sMonth = ((Integer)val.get("month")).toString();
	    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
	    		String sDay = ((Integer)val.get("day")).toString();
	    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
	    		String sHour = ((Integer)val.get("hour")).toString();
	    		sHour = sHour.length() == 1 ? "0" + sHour : sHour;
	    		mapResult.put(sDirection + sYear + sMonth + sDay + sHour, val);
	    	}
	    	
	    	List<String> hourDay = idsDao.getPeriodHour(sStartDay, 0);
	    	
	    	String[] saGubun = {"아웃바운드", "인바운드"};
    		for (int direction = 0; direction < 2; direction++) { //아웃바운드/인바운드
    			for (String tDay : hourDay) {
	    			for (int h = 0; h < 24; h++) { //시간
	    	    		HashMap<String, Object> map = new HashMap<String, Object>();
			    		DBObject val = mapResult.get(String.valueOf(direction) + tDay + (h < 10 ? "0" + h : String.valueOf(h)));
			    		map.put("gubun", saGubun[direction]);
	        			map.put("hour", String.valueOf(h));
	    	    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
			    		
	    	    		dataSource.add(map);
	    	    		logger.debug(map.toString());
	    			}
    			}
    		}
	    	
			dbResult = idsDao.AllDetectLogTrend("HR", -1, assetCode, sStartDay, sEndDay);
			
	    	mapResult = new HashMap<String, DBObject>(); 
	    	for (DBObject val : dbResult) {
	    		String sYear = ((Integer)val.get("year")).toString();
	    		String sMonth = ((Integer)val.get("month")).toString();
	    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
	    		String sDay = ((Integer)val.get("day")).toString();
	    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
	    		String sHour = ((Integer)val.get("hour")).toString();
	    		sHour = sHour.length() == 1 ? "0" + sHour : sHour;
	    		mapResult.put(sYear + sMonth + sDay + sHour, val);
	    	}
	    	
	    	for (String tDay : hourDay) {
    			for (int h = 0; h < 24; h++) { //시간
    	    		HashMap<String, Object> map = new HashMap<String, Object>();
		    		DBObject val = mapResult.get(tDay + (h < 10 ? "0" + h : String.valueOf(h)));
		    		map.put("gubun", "전체");
        			map.put("hour", String.valueOf(h));
    	    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
		    		
    	    		dataSource.add(map);
    	    		logger.debug(map.toString());
    			}
	    	}
    		
		} else { //최근3일

			Iterable<DBObject> dbResult = idsDao.AllDetectLogTrend("DY", 1, assetCode, idsDao.addDate(sStartDay,-2), sEndDay);

	    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
	    	for (DBObject val : dbResult) {
	    		String sDirection = (String)val.get("direction");
	    		String sYear = ((Integer)val.get("year")).toString();
	    		String sMonth = ((Integer)val.get("month")).toString();
	    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
	    		String sDay = ((Integer)val.get("day")).toString();
	    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
	    		mapResult.put(sDirection + sYear + sMonth + sDay, val);
	    	}
	    	
	    	List<String> dayPeriod = idsDao.getPeriodDay(idsDao.addDate(sStartDay,-2), sEndDay, 1);
	    	
	    	String[] saGubun = {"아웃바운드", "인바운드"};
	    	for (int direction = 0; direction < 2; direction++) { //아웃바운드/인바운드
	    		for (String tDay : dayPeriod) {
		    		HashMap<String, Object> map = new HashMap<String, Object>();
		    		DBObject val = mapResult.get(String.valueOf(direction) + tDay);
		    		map.put("gubun", saGubun[direction]);
		    		map.put("day", Integer.valueOf(tDay));
		    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);

    	    		dataSource.add(map);
    	    		logger.debug(map.toString());
	    		}	    		
	    	}
	    	
			dbResult = idsDao.AllDetectLogTrend("DY", -1, assetCode, idsDao.addDate(sStartDay,-2), sEndDay);
			
	    	mapResult = new HashMap<String, DBObject>(); 
	    	for (DBObject val : dbResult) {
	    		String sYear = ((Integer)val.get("year")).toString();
	    		String sMonth = ((Integer)val.get("month")).toString();
	    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
	    		String sDay = ((Integer)val.get("day")).toString();
	    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
	    		mapResult.put(sYear + sMonth + sDay, val);
	    	}
	    	
    		for (String tDay : dayPeriod) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		DBObject val = mapResult.get(tDay);
	    		map.put("gubun", "전체");
	    		map.put("day", Integer.valueOf(tDay));
	    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
	    		
	    		dataSource.add(map);
	    		logger.debug(map.toString());
			}

		}
		
		if (nChoice == 1) {			//해당일
			reportData.put("OPT1", new SynthesisDataSource(dataSource));
		} else if (nChoice == 2) {	//최근3일
			reportData.put("OPT1_1", new SynthesisDataSource(dataSource));
			reportData.put("OPT1_2", new SynthesisDataSource(dataSource));
		}
	}
	
	private void Direction_DetectLog_Trend(HashMap<String, Object> reportData, int nDirection, int assetCode, String sStartDay, String sEndDay, int nChoice) throws Exception {
		
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();
		
		if (nChoice == 1) {	//해당일
			
    		Iterable<DBObject> dbResult = idsDao.AllActionTrend("HR", nDirection, assetCode, sStartDay, sEndDay);
    		
    		HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
	    	for (DBObject val : dbResult) {
	    		String sYear = ((Integer)val.get("year")).toString();
	    		String sMonth = ((Integer)val.get("month")).toString();
	    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
	    		String sDay = ((Integer)val.get("day")).toString();
	    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
	    		String sHour = ((Integer)val.get("hour")).toString();
	    		sHour = sHour.length() == 1 ? "0" + sHour : sHour;
	    		mapResult.put(sYear + sMonth + sDay + sHour, val);
	    	}
	    	
	    	List<String> hourDay = idsDao.getPeriodHour(sStartDay, 0);
	    	
	    	for (String tDay : hourDay) {
    			for (int h = 0; h < 24; h++) { //시간
    	    		HashMap<String, Object> map = new HashMap<String, Object>();
		    		DBObject val = mapResult.get(tDay + (h < 10 ? "0" + h : String.valueOf(h)));
		    		map.put("gubun", "전체");
        			map.put("hour", String.valueOf(h));
    	    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
		    		
    	    		dataSource.add(map);
    	    		logger.debug(map.toString());
    			}
	    	}
    		
		} else {
			
	    	Iterable<DBObject> dbResult = idsDao.AllActionTrend("DY", nDirection, assetCode, idsDao.addDate(sStartDay,-2), sEndDay);
	    	
	    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
	    	for (DBObject val : dbResult) {
	    		String sYear = ((Integer)val.get("year")).toString();
	    		String sMonth = ((Integer)val.get("month")).toString();
	    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
	    		String sDay = ((Integer)val.get("day")).toString();
	    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
	    		mapResult.put(sYear + sMonth + sDay, val);
	    	}

	    	List<String> dayPeriod = idsDao.getPeriodDay(idsDao.addDate(sStartDay,-2), sEndDay, 1);
	    	
    		for (String tDay : dayPeriod) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		DBObject val = mapResult.get(tDay);
	    		map.put("gubun", "전체");
	    		map.put("day", Integer.valueOf(tDay));
	    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
	    		
	    		dataSource.add(map);
	    		logger.debug(map.toString());
			}
		}
		
		if (nDirection == 1) {	//INBOUND
			if (nChoice == 1) {			//해당일
				reportData.put("OPT2", new SynthesisDataSource(dataSource));
			} else if (nChoice == 2) {	//최근3일
				reportData.put("OPT2_1", new SynthesisDataSource(dataSource));
				reportData.put("OPT2_2", new SynthesisDataSource(dataSource));
			}
		} else {
			if (nChoice == 1) {			//해당일
				reportData.put("OPT3", new SynthesisDataSource(dataSource));
			} else if (nChoice == 2) {	//최근3일
				reportData.put("OPT3_1", new SynthesisDataSource(dataSource));
				reportData.put("OPT3_2", new SynthesisDataSource(dataSource));
			}
		}
	}
	
	//외부에서 내부로의 전체 탐지로그 & 이벤트 TOP (차트, 표)
	private void Direction_EventTopN(HashMap<String, Object> reportData, int nDirection, int assetCode, String sStartDay, String sEndDay, int nLimit, boolean bChk) throws Exception {
		
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> dataSource3 = new ArrayList<HashMap<String, Object>>();

		Iterable<DBObject> dbResult = idsDao.EventTopN("DY", nDirection, assetCode, sStartDay, sEndDay, 0);

		long lTotal = 0L;
    	for (DBObject val : dbResult) {
    		lTotal += ((Number)val.get("count")).longValue();
    	}

    	List<String> saTopN = new ArrayList<String>();
    	int nNo = 1;
    	for (DBObject val : dbResult) {
    		String sMessage = (String)val.get("message");
    		if (!sMessage.equals("-1") && nNo <= 10) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		map.put("message", sMessage);
	    		map.put("total", lTotal);
	    		map.put("count", ((Number)val.get("count")).longValue());

	    		dataSource1.add(map);
	    		logger.debug(map.toString());
	    		
	    		saTopN.add(sMessage);
    		}
    		if (nNo >= 10) break;
    		nNo++;
    	}
    	
    	Iterable<DBObject> dbBefore = idsDao.EventTopN("DY", nDirection, assetCode, idsDao.addDate(sStartDay,-1), idsDao.addDate(sEndDay,-1), 0);
    	HashMap<String, DBObject> mapBefore = new HashMap<String, DBObject>(); 
    	for (DBObject val : dbBefore) {
    		mapBefore.put((String)val.get("message"), val);
    	}
    	
    	nNo = 1;
    	for (DBObject val : dbResult) {
    		String sMessage = (String)val.get("message");
    		
    		DBObject prevMap = mapBefore.get(sMessage);
    		
    		Iterable<DBObject> srcIpTop5 = idsDao.EventSrcIpTopN("DY", nDirection, assetCode, sStartDay, sEndDay, sMessage, 5);
    		for (DBObject sVal : srcIpTop5) {
    			HashMap<String, Object> map = new HashMap<String, Object>();
    			map.put("no", nNo);
    			map.put("message", sMessage);
    			map.put("total", ((Number)val.get("count")).longValue());    			
    			map.put("prevCount", prevMap != null ? ((Number)prevMap.get("count")).longValue() : 0);
    			map.put("srcIp", StringUtil.convertString(sVal.get("srcIp")));
    			if (nDirection == INBOUND) {
    				map.put("geoCode", Constants.getCountryCode(StringUtil.convertString(sVal.get("srcIp"))));
    			} else {
    				map.put("geoCode", Constants.getCountryCode(StringUtil.convertString(sVal.get("destIp"))));
    			}
    			map.put("destIp", StringUtil.convertString(sVal.get("destIp")));
    			map.put("count", ((Number)sVal.get("count")).longValue());
        		
        		dataSource2.add(map);
        		logger.debug(map.toString());
    		}
    		if (nNo >= nLimit) break;
    		nNo++;
    	}
    	
    	if (nDirection == INBOUND) {
	    	reportData.put("OPT4_1", new SynthesisDataSource(dataSource1));
	    	reportData.put("OPT4_2", new SynthesisDataSource(dataSource2));
    	} else {
	    	reportData.put("OPT5_1", new SynthesisDataSource(dataSource1));
	    	reportData.put("OPT5_2", new SynthesisDataSource(dataSource2));
    	}
		
    	if (bChk) { //Event Top 발생추이
    		
	    	dbResult = idsDao.EventTopNTrend("HR", nDirection, assetCode, sStartDay, sEndDay, saTopN);
	    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
	    	for (DBObject val : dbResult) {
	    		String sMessage = (String)val.get("message");
	    		String sYear = ((Integer)val.get("year")).toString();
	    		String sMonth = ((Integer)val.get("month")).toString();
	    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
	    		String sDay = ((Integer)val.get("day")).toString();
	    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
	    		String sHour = ((Integer)val.get("hour")).toString();
	    		sHour = sHour.length() == 1 ? "0" + sHour : sHour;
	    		mapResult.put(sMessage + "-" + sYear + sMonth + sDay + sHour, val);
	    	}
    		
	    	List<String> hourDay = idsDao.getPeriodHour(sStartDay, 0);
	    	
	    	for (String sMessage : saTopN) {
	    		for (String tDay : hourDay) {
	    			for (int h = 0; h < 24; h++) { //시간
			    		DBObject val = mapResult.get(sMessage + "-" + tDay + (h < 10 ? "0" + h : String.valueOf(h)));
			    		HashMap<String, Object> map = new HashMap<String, Object>();
			    		map.put("gubun", sMessage);
			    		map.put("hour", String.valueOf(h));
			    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
			    		
			    		dataSource3.add(map);
			    		logger.debug(map.toString());
	    			}
		    	}
	    	}
    		
	    	if (nDirection == INBOUND) {
	    		reportData.put("OPT4_3", new SynthesisDataSource(dataSource3));
	    	} else {
		    	reportData.put("OPT5_3", new SynthesisDataSource(dataSource3));
	    	}
    	}
    	
	}		
	
	//외부에서 내부로의 전체 탐지로그 & 신규 탐지 이벤트 현황
	private void New_DetectLog_Condition(HashMap<String, Object> reportData, int nDirection, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();

    	//신규 탐지 이벤트리스트
    	Iterable<DBObject>  dbNewResult = idsDao.EventTopN("DY", nDirection, assetCode, sStartDay, sEndDay, null);
    	HashMap<String, DBObject> newMap = new HashMap<String, DBObject>();
    	for (DBObject val : dbNewResult) {
    		newMap.put((String)val.get("message"), val);
    	}

		//전일 탐지 이벤트리스트
		Iterable<DBObject> dbResult = idsDao.EventTopN("DY", nDirection, assetCode, idsDao.addDate(sStartDay,-1), idsDao.addDate(sEndDay,-1), null);
    	for (DBObject val : dbResult) {
    		newMap.remove((String)val.get("message"));
    	}
    	
    	List<DBObject> newList = new ArrayList<DBObject>();
    	for (Entry<String, DBObject> e : newMap.entrySet()) {
    		newList.add(e.getValue());
    	}
    	
		Comparator<DBObject> comparator = new Comparator<DBObject>() {
			@Override
			public int compare(DBObject m1, DBObject m2) {
				String o1 = (String)m1.get("count");
				String o2 = (String)m2.get("count");
				if (o1 == null || o2 == null) {
					return 0;
				}
				return o1.compareTo(o2);
			}
		};

		Collections.sort(newList, comparator);
    	
		List<String> hourDay = idsDao.getPeriodHour(sStartDay, 0);
		
		int nNo = 1;
		for (DBObject mVal : newList) {
			String sMessage = (String)mVal.get("message");
			
	    	dbResult = idsDao.EventTopNTrend("HR", nDirection, assetCode, sStartDay, sEndDay, sMessage);
	    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
	    	for (DBObject sVal : dbResult) {
	    		String sYear = ((Integer)sVal.get("year")).toString();
	    		String sMonth = ((Integer)sVal.get("month")).toString();
	    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
	    		String sDay = ((Integer)sVal.get("day")).toString();
	    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
	    		String sHour = ((Integer)sVal.get("hour")).toString();
	    		sHour = sHour.length() == 1 ? "0" + sHour : sHour;
	    		mapResult.put(sYear + sMonth + sDay + sHour, sVal);
	    	}
			
    		for (String tDay : hourDay) {
    			for (int h = 0; h < 24; h++) { //시간
		    		DBObject pVal = mapResult.get(tDay + (h < 10 ? "0" + h : String.valueOf(h)));
		    		HashMap<String, Object> map = new HashMap<String, Object>();
		    		map.put("gubun", sMessage);
		    		map.put("hour", String.valueOf(h));
		    		map.put("count", pVal != null ? ((Number)pVal.get("count")).longValue() : 0);
		    		
		    		dataSource1.add(map);
		    		logger.debug(map.toString());
    			}
	    	}
    		if (nNo >= 10) break;
    		nNo++;
		}
    	
    	nNo = 1;
    	for (DBObject mVal : newList) {
			String sMessage = (String)mVal.get("message");

			Iterable<DBObject> srcIpTop5 = idsDao.EventSrcIpTopN("DY", nDirection, assetCode, sStartDay, sEndDay, sMessage, 5);
			for (DBObject sVal : srcIpTop5) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("no", nNo);
				map.put("message", sMessage);
				map.put("total", ((Number)mVal.get("count")).longValue());
				map.put("srcIp", StringUtil.convertString(sVal.get("srcIp")));
				
				if (nDirection == INBOUND) {
					map.put("geoCode", Constants.getCountryCode(StringUtil.convertString(sVal.get("srcIp"))));
				} else {
					map.put("geoCode", Constants.getCountryCode(StringUtil.convertString(sVal.get("destIp"))));
				}
				
				map.put("destIp", StringUtil.convertString(sVal.get("destIp")));
				map.put("count", ((Number)sVal.get("count")).longValue());
	    		
	    		dataSource2.add(map);
	    		logger.debug(map.toString());
			}
			if (nNo >= 30) break;
			nNo++;
    	}
    	
    	if (nDirection == INBOUND) {
    		reportData.put("OPT6_1", new SynthesisDataSource(dataSource1));
    		reportData.put("OPT6_2", new SynthesisDataSource(dataSource2));
    	} else {
    		reportData.put("OPT7_1", new SynthesisDataSource(dataSource1));
    		reportData.put("OPT7_2", new SynthesisDataSource(dataSource2));
    	}
	}		
	
	//외부에서 내부로의 전체 탐지로그 & 이전대비 2배증가된 이벤트 현황
	private void Double_DetectLog_Condition(HashMap<String, Object> reportData, int nDirection, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();

		//전일 탐지 이벤트리스트
		Iterable<DBObject> dbBefore = idsDao.EventTopN("DY", nDirection, assetCode, idsDao.addDate(sStartDay,-1), idsDao.addDate(sEndDay,-1), null);
		HashMap<String, DBObject> mapBefore = new HashMap<String, DBObject>();
    	for (DBObject val : dbBefore) {
    		mapBefore.put((String)val.get("message"), val);
    	}

    	//신규 탐지 이벤트리스트
    	List<DBObject> dblList = new ArrayList<DBObject>();
    	List<String> saDbl = new ArrayList<String>();
    	Iterable<DBObject> dbResult = idsDao.EventTopN("DY", nDirection, assetCode, sStartDay, sEndDay, null);
    	for (DBObject val : dbResult) {
    		String sMessage = (String)val.get("message");
    		
    		DBObject beforeMap = (DBObject)mapBefore.get(sMessage);
    		if (beforeMap == null) continue;

    		long lCount = ((Number)val.get("count")).longValue();
    		long lBeforeCount = ((Number)beforeMap.get("count")).longValue();

    		if (lCount >= lBeforeCount*2) {
    			saDbl.add(sMessage);
    			dblList.add(val);
    		}
    	}
    	
    	List<String> hourDay = idsDao.getPeriodHour(sStartDay, 0);
    	
		int nNo = 1;
		for (DBObject mVal : dblList) {
			String sMessage = (String)mVal.get("message");

	    	dbResult = idsDao.EventTopNTrend("HR", nDirection, assetCode, sStartDay, sEndDay, sMessage);
	    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
	    	for (DBObject val : dbResult) {
	    		String sYear = ((Integer)val.get("year")).toString();
	    		String sMonth = ((Integer)val.get("month")).toString();
	    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
	    		String sDay = ((Integer)val.get("day")).toString();
	    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
	    		String sHour = ((Integer)val.get("hour")).toString();
	    		sHour = sHour.length() == 1 ? "0" + sHour : sHour;
	    		mapResult.put(sYear + sMonth + sDay + sHour, val);
	    	}
			
    		for (String tDay : hourDay) {
    			for (int h = 0; h < 24; h++) { //시간
		    		DBObject val = mapResult.get(tDay + (h < 10 ? "0" + h : String.valueOf(h)));
		    		HashMap<String, Object> map = new HashMap<String, Object>();
		    		map.put("gubun", sMessage);
		    		map.put("hour", String.valueOf(h));
		    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
		    		
		    		dataSource1.add(map);
		    		logger.debug(map.toString());
    			}
	    	}
    		if (nNo >= 10) break;
    		nNo++;
		}
    	
    	nNo = 1;
    	for (DBObject val : dblList) {
    		String sMessage = (String)val.get("message");

			Iterable<DBObject> srcIpTop5 = idsDao.EventSrcIpTopN("DY", nDirection, assetCode, sStartDay, sEndDay, sMessage, 5);
			for (DBObject sVal : srcIpTop5) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("no", nNo);
				map.put("message", sMessage);
				map.put("total", ((Number)val.get("count")).longValue());
				map.put("srcIp", StringUtil.convertString(sVal.get("srcIp")));
				map.put("geoCode", Constants.getCountryCode(StringUtil.convertString(sVal.get("srcIp"))));
				map.put("destIp", StringUtil.convertString(sVal.get("destIp")));
				map.put("count", ((Number)sVal.get("count")).longValue());
	    		
	    		dataSource2.add(map);
	    		logger.debug(map.toString());
			}
			if (nNo >= 30) break;
			nNo++;
    	}
    	
		reportData.put("OPT8_1", new SynthesisDataSource(dataSource1));
		reportData.put("OPT8_2", new SynthesisDataSource(dataSource2));
	}		
	
	//외부에서 내부로의 전체 탐지로그 & SIP TOP
	private void ALL_DetectLog_TopN(HashMap<String, Object> reportData, int nDirection, int assetCode, String sStartDay, String sEndDay, boolean bSrcIp, int nLimit, boolean bChk) throws Exception {
		
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();
		
    	//IP 건수기준 TOP N 리스트
		Iterable<DBObject> ipTopNResult = idsDao.IpTopN("DY", nDirection, assetCode, sStartDay, sEndDay, bSrcIp, nLimit);
    	List<String> topNIps = new ArrayList<String>();
    	for (DBObject val : ipTopNResult) {
    		if (bSrcIp) {    		
    			topNIps.add((String)val.get("srcIp"));
    		} else {
    			topNIps.add((String)val.get("destIp"));
    		}
    	}

    	//전일 현황
    	Iterable<DBObject> dbBefore = idsDao.BeforeIpCondition("DY", nDirection, assetCode, idsDao.addDate(sStartDay,-1), idsDao.addDate(sEndDay,-1), bSrcIp, topNIps);
    	HashMap<String, DBObject> mapBefore = new HashMap<String, DBObject>();
    	for (DBObject val : dbBefore) {
    		if (bSrcIp) {
    			mapBefore.put((String)val.get("srcIp"), val);
    		} else {
    			mapBefore.put((String)val.get("destIp"), val);
    		}
    	}
    	
    	int nNo = 1;
    	for (DBObject val : ipTopNResult) {
    		Iterable<DBObject> ipTopNCondition = null;
    		if (bSrcIp) {
    			ipTopNCondition = idsDao.IpTopNCondition("DY", nDirection, assetCode, sStartDay, sEndDay, (String)val.get("srcIp"), bSrcIp);
    		} else {
    			ipTopNCondition = idsDao.IpTopNCondition("DY", nDirection, assetCode, sStartDay, sEndDay, (String)val.get("destIp"), bSrcIp);
    		}
    		
			for (DBObject dbObj : ipTopNCondition) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("no", nNo);
				if (bSrcIp) {
					map.put("srcIp", (String)dbObj.get("srcIp"));
					
		    		DBObject prevMap = mapBefore.get((String)dbObj.get("srcIp"));
		    		map.put("prevCount", prevMap != null ? ((Number)prevMap.get("count")).longValue() : 0);
					
					map.put("message", (String)val.get("message"));
					map.put("destIp", (String)dbObj.get("destIp"));
				} else {
					map.put("destIp", (String)dbObj.get("destIp"));
					
		    		DBObject prevMap = mapBefore.get((String)dbObj.get("destIp"));
		    		map.put("prevCount", prevMap != null ? ((Number)prevMap.get("count")).longValue() : 0);
					
					map.put("srcIp", (String)dbObj.get("srcIp"));
					map.put("message", (String)val.get("message"));
				}
				
				if (nDirection == INBOUND) {
					map.put("geoCode", Constants.getCountryCode((String)dbObj.get("srcIp")));
				} else {
					map.put("geoCode", Constants.getCountryCode((String)dbObj.get("destIp")));
				}
				map.put("total", ((Number)val.get("count")).longValue());
				map.put("count", ((Number)dbObj.get("count")).longValue());
				
				dataSource1.add(map);
				logger.debug(map.toString());
			}
			nNo++;
    	}
    	
    	if (bSrcIp) {
	    	if (nDirection == INBOUND) { //외부에서 내부로의 전체 세션로그 & SIP TOP (표)
	    		reportData.put("OPT9_1", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == OUTBOUND) { //내부에서 외부로의 전체 세션로그 & SIP TOP (표)
	    		reportData.put("OPT10_1", new SynthesisDataSource(dataSource1));
	    	}
	    	
	    	if (topNIps.size() > 10) {
	    		topNIps = topNIps.subList(0, 10);
	    	}
	    	
	    	if (bChk) { //SRC IP TOP10 세션 로그 발생추이 
	    		ArrayList<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();
	        	Iterable<DBObject> ipTopNTrend = idsDao.IpTopNTrend("HR", nDirection, assetCode, sStartDay, sEndDay, topNIps, true);
	        	
	        	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
	        	for (DBObject val : ipTopNTrend) {
	        		String sSrcIp = (String)val.get("srcIp");
	        		String sYear = ((Integer)val.get("year")).toString();
	        		String sMonth = ((Integer)val.get("month")).toString();
	        		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
	        		String sDay = ((Integer)val.get("day")).toString();
	        		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
	        		String sHour = ((Integer)val.get("hour")).toString();
	        		sHour = sHour.length() == 1 ? "0" + sHour : sHour;
	        		mapResult.put(sSrcIp + "-" + sYear + sMonth + sDay + sHour, val);
	        	}
	        	
	        	List<String> hourDay = idsDao.getPeriodHour(sStartDay, 0);
	        	
	        	for (String strIp : topNIps) {
	        		for (String tDay : hourDay) {
	    	    		for (int h = 0; h < 24; h++) { //시간
	    		    		DBObject val = mapResult.get(strIp + "-" + tDay + (h < 10 ? "0" + h : String.valueOf(h)));
	    		    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		    		map.put("srcIp", strIp);
	    		    		map.put("hour", String.valueOf(h));
	    		    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
	    		    		dataSource2.add(map);
	    					logger.debug(map.toString());
	    		    	}
	        		}
	        	}
	    		
	        	if (nDirection == INBOUND) {
		    		reportData.put("OPT9_2", new SynthesisDataSource(dataSource2));
		    	} else if (nDirection == OUTBOUND) {
		    		reportData.put("OPT10_2", new SynthesisDataSource(dataSource2));
		    	}
	    	}
    	} else {
	    	if (nDirection == INBOUND) { //외부에서 내부로의 전체 탐지로그 & DIP TOP (표)
	    		reportData.put("OPT11", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == OUTBOUND) { //내부에서 외부로의 전체 탐지로그 & DIP TOP (표)
	    		reportData.put("OPT12", new SynthesisDataSource(dataSource1));
	    	}
    	}
	}
	
	private void ALL_ServiceTop10(HashMap<String, Object> reportData, int nDirection, int assetCode, String sStartDay, String sEndDay, boolean bChk) throws Exception {
		
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();

    	Iterable<DBObject> dbResult = idsDao.ServiceDetectLog("DY", nDirection, assetCode, sStartDay, sEndDay, 0);
    	
    	int nTotal = 0;
		for (DBObject val : dbResult) {
    		nTotal += ((Number)val.get("count")).longValue();
		}

    	int nNo = 1;
    	int nCount = 0;
    	for (DBObject val : dbResult) {
    		int nPort = (Integer)val.get("destPort");
    		if (nPort != -1 && nNo <= 10) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		map.put("destPort", String.valueOf(nPort));
	    		map.put("total", nTotal);
	    		map.put("count", ((Number)val.get("count")).longValue());

	    		dataSource1.add(map);
	    		logger.debug(map.toString());
    		} else {
    			nCount += ((Number)val.get("count")).longValue();
    		}
    		nNo++;
    	}
    	
    	if (nCount > 0) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("destPort", "기타");
			map.put("count", nCount);
	
			dataSource1.add(map);
			logger.debug(map.toString());
    	}
		
    	if (nDirection == INBOUND) {
    		reportData.put("OPT13_1", new SynthesisDataSource(dataSource1));
    	} else {
    		reportData.put("OPT14_1", new SynthesisDataSource(dataSource1));
    	}
    	
    	if (bChk) { //서비스 TOP10 증감현황 및 이벤트유형
    	
    		//표 [순위 | Port | 서비스명 | 전체건수 | 증감현황 | 이벤트명 | 비율(%)]
    		dbResult = idsDao.ServiceDetectLog("DY", nDirection, assetCode, idsDao.addDate(sStartDay,-1), idsDao.addDate(sEndDay,-1), 0);
        	HashMap<String, DBObject> mapBefore = new HashMap<String, DBObject>(); 
        	for (DBObject val : dbResult) {
        		mapBefore.put(String.valueOf((Integer)val.get("destPort")), val);
        	}
        	
        	List<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();
        	
        	nNo = 1;
        	for (HashMap<String, Object> hMap : dataSource1) {
        		String sPort = (String)hMap.get("destPort");
        		
        		if (sPort.equals("기타")) continue;
        		
        		Iterable<DBObject> dbTmp = idsDao.ServiceEventTopN("DY", assetCode, sStartDay, sEndDay, Integer.valueOf(sPort));
        		for (DBObject pMap : dbTmp) {
            		HashMap<String, Object> map = new HashMap<String, Object>();
            		map.put("no", nNo);
            		map.put("destPort", sPort);
            		map.put("total", ((Number)hMap.get("count")).longValue());
            		
            		DBObject prevMap = mapBefore.get(sPort);
            		map.put("prevCount", prevMap != null ? ((Number)prevMap.get("count")).longValue() : 0);
            		map.put("message", (String)pMap.get("message"));
            		map.put("count", ((Number)pMap.get("count")).longValue());
            		
            		dataSource2.add(map);
        			logger.debug(map.toString());
        		}
        		nNo++;
        	}
        	
        	if (nDirection == INBOUND) {
        		reportData.put("OPT13_2", new SynthesisDataSource(dataSource2));
        	} else {
        		reportData.put("OPT14_2", new SynthesisDataSource(dataSource2));
        	}
    	}
	}
	
	public void push(String msg) {
		HashMap<String, Object> pMap = new HashMap<String, Object>();
    	pMap.put("message", msg);
		try {
			ObjectMapper mapper = new ObjectMapper();
			userInfo.getWsSession().sendMessage(new TextMessage(mapper.writeValueAsString(pMap)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}




