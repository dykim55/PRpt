package com.cyberone.report.core.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyberone.report.Constants;
import com.cyberone.report.core.dao.FwDao;
import com.cyberone.report.core.datasource.SynthesisDataSource;
import com.cyberone.report.utils.StringUtil;
import com.mongodb.DB;
import com.mongodb.DBObject;

public class FwMonthlyReport extends BaseReport {
	
	private FwDao fwDao;
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	public FwMonthlyReport(DB promDB, DB reportDB) {
		this.fwDao = new FwDao(promDB, reportDB);
	}
	
	/* 
	 * 방화벽 보고서 통계데이타 생성
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getDataSource(String sReportType, String sSearchDate, HashMap<String, Object> hMap) throws Exception {
		logger.debug("방화벽 보고서 통계데이타 생성");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		
		String sStartDay = "";
		String sEndDay = "";

		String sAssetCode = StringUtil.convertString(hMap.get("assetCode"));
		int assetCode = Integer.valueOf(sAssetCode.substring(0, sAssetCode.indexOf("_")));

		//조회 시작일/종료일
		int statBaseDay = fwDao.getStatMonthBaseDay(assetCode);
		if (statBaseDay > 1) {
			Date tmpSDate = sdf.parse(sSearchDate);
			Calendar startCal = fwDao.getCalendar(tmpSDate);
			startCal.add(Calendar.DAY_OF_MONTH, statBaseDay);
			startCal.add(Calendar.DAY_OF_MONTH, -1);
			
			Date tmpEDate = sdf.parse(sSearchDate);
			Calendar endCal = fwDao.getCalendar(tmpEDate);
			endCal.add(Calendar.MONTH, 1);
			endCal.add(Calendar.DAY_OF_MONTH, statBaseDay);
			endCal.add(Calendar.DAY_OF_MONTH, -1);
			endCal.add(Calendar.SECOND, -1);
			
			sStartDay = sdf2.format(startCal.getTime());
			sEndDay = sdf2.format(endCal.getTime());
		} else {
			Date tmpSDate = sdf.parse(sSearchDate);
			Calendar startCal = fwDao.getCalendar(tmpSDate);
		
			Date tmpEDate = sdf.parse(sSearchDate);
			Calendar endCal = fwDao.getCalendar(tmpEDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);

			sStartDay = sdf2.format(startCal.getTime());
			sEndDay = sdf2.format(endCal.getTime());
		}
		
		HashMap<String,Object> hFormMap = (HashMap<String,Object>)hMap.get("formData");
		
		String sEtc = "";
		HashMap<String,Object> hData = null;
		if (hFormMap == null) {
			DBObject dbObj = fwDao.selectAutoReportForm(Integer.parseInt(sReportType), assetCode);
			hData = (new ObjectMapper()).readValue(StringUtil.convertString(dbObj.get("formData")), HashMap.class);
		} else {
			hData = (HashMap<String,Object>)hFormMap.get("data");
			sEtc = StringUtil.convertString(hFormMap.get("etc"));
		}

		HashMap<String, Object> reportData = new HashMap<String, Object>();
		
		int nChoice = 0;
		for (Entry<String, Object> e : hData.entrySet()) {
			
			switch (e.getKey()) {
			
				case "opt1" :	//외부에서 내부로의 전체 세션 로그 발생추이
					logger.debug("항목: 외부에서 내부로의 전체 세션 로그 발생추이");
					All_SessionLog_Trend(reportData, INBOUND, assetCode, sStartDay, sEndDay, sEtc);
					break;
				case "opt2" : 	//외부에서 내부로의 허용 세션 로그 발생추이 (차트)
					logger.debug("항목: 외부에서 내부로의 허용 세션 로그 발생추이 (차트)");
					Action_SessionLog_Trend(reportData, INBOUND, ALLOW, assetCode, sStartDay, sEndDay);
					break;
				case "opt3" : 	//외부에서 내부로의 차단 세션 로그 발생추이 (차트)
					logger.debug("항목: 외부에서 내부로의 차단 세션 로그 발생추이 (차트)");
					Action_SessionLog_Trend(reportData, INBOUND, CUTOFF, assetCode, sStartDay, sEndDay);
					break;
				case "opt4" : 	//내부에서 외부로의 전체 세션 로그 발생추이
					logger.debug("항목: 내부에서 외부로의 전체 세션 로그 발생추이");
					All_SessionLog_Trend(reportData, OUTBOUND, assetCode, sStartDay, sEndDay, sEtc);
					break;
				case "opt5" : 	//내부에서 외부로의 허용 세션 로그 발생추이 (차트)
					logger.debug("항목: 내부에서 외부로의 허용 세션 로그 발생추이 (차트)");
					Action_SessionLog_Trend(reportData, OUTBOUND, ALLOW, assetCode, sStartDay, sEndDay);
					break;
				case "opt6" : 	//내부에서 외부로의 차단 세션 로그 발생추이 (차트)
					logger.debug("항목: 내부에서 외부로의 차단 세션 로그 발생추이 (차트)");
					Action_SessionLog_Trend(reportData, OUTBOUND, CUTOFF, assetCode, sStartDay, sEndDay);
					break;
				case "opt7" : 	//외부에서 내부로의 전체 세션로그 & SIP TOP (표)
					logger.debug("항목: 외부에서 내부로의 전체 세션로그 & SIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd7")));
					Action_SessionLog_TopN(reportData, INBOUND, "", assetCode, sStartDay, sEndDay, true, nChoice, false);
					break;
				case "opt8" : 	//외부에서 내부로의 허용 세션로그 & SIP TOP (표)
					logger.debug("항목: 외부에서 내부로의 허용 세션로그 & SIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd8")));
					Action_SessionLog_TopN(reportData, INBOUND, ALLOW, assetCode, sStartDay, sEndDay, true, nChoice, false);
					break;
				case "opt9" : 	//외부에서 내부로의 차단 세션로그 & SIP TOP (표)
					logger.debug("항목: 외부에서 내부로의 차단 세션로그 & SIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd9")));
					Action_SessionLog_TopN(reportData, INBOUND, CUTOFF, assetCode, sStartDay, sEndDay, true, nChoice, !StringUtil.isEmpty(hData.get("ck9")));
					break;
				case "opt10" : 	//내부에서 외부로의 전체 세션로그 & SIP TOP (표)
					logger.debug("항목: 내부에서 외부로의 전체 세션로그 & SIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd10")));
					Action_SessionLog_TopN(reportData, OUTBOUND, "", assetCode, sStartDay, sEndDay, true, nChoice, false);
					break;
				case "opt11" : 	//내부에서 외부로의 허용 세션로그 & SIP TOP (표)
					logger.debug("항목: 내부에서 외부로의 허용 세션로그 & SIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd11")));
					Action_SessionLog_TopN(reportData, OUTBOUND, ALLOW, assetCode, sStartDay, sEndDay, true, nChoice, false);
					break;
				case "opt12" : 	//내부에서 외부로의 차단 세션로그 & SIP TOP (표)
					logger.debug("항목: 내부에서 외부로의 차단 세션로그 & SIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd12")));
					Action_SessionLog_TopN(reportData, OUTBOUND, CUTOFF, assetCode, sStartDay, sEndDay, true, nChoice, !StringUtil.isEmpty(hData.get("ck12")));
					break;
				case "opt13" : 	//외부에서 내부로의 전체 세션로그 & DIP TOP (표)
					logger.debug("항목: 외부에서 내부로의 전체 세션로그 & DIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd13")));
					Action_SessionLog_TopN(reportData, INBOUND, "", assetCode, sStartDay, sEndDay, false, nChoice, false);
					break;
				case "opt14" : 	//외부에서 내부로의 허용 세션로그 & DIP TOP (표)
					logger.debug("항목: 외부에서 내부로의 허용 세션로그 & DIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd14")));
					Action_SessionLog_TopN(reportData, INBOUND, ALLOW, assetCode, sStartDay, sEndDay, false, nChoice, false);
					break;
				case "opt15" : 	//외부에서 내부로의 차단 세션로그 & DIP TOP (표)
					logger.debug("항목: 외부에서 내부로의 차단 세션로그 & DIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd15")));
					Action_SessionLog_TopN(reportData, INBOUND, CUTOFF, assetCode, sStartDay, sEndDay, false, nChoice, false);
					break;
				case "opt16" : 	//내부에서 외부로의 전체 세션로그 & DIP TOP (표)
					logger.debug("항목: 내부에서 외부로의 전체 세션로그 & DIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd16")));
					Action_SessionLog_TopN(reportData, OUTBOUND, "", assetCode, sStartDay, sEndDay, false, nChoice, false);
					break;
				case "opt17" : 	//내부에서 외부로의 허용 세션로그 & DIP TOP (표)
					logger.debug("항목: 내부에서 외부로의 허용 세션로그 & DIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd17")));
					Action_SessionLog_TopN(reportData, OUTBOUND, ALLOW, assetCode, sStartDay, sEndDay, false, nChoice, false);
					break;
				case "opt18" : 	//내부에서 외부로의 차단 세션로그 & DIP TOP (표)
					logger.debug("항목: 내부에서 외부로의 차단 세션로그 & DIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd18")));
					Action_SessionLog_TopN(reportData, OUTBOUND, CUTOFF, assetCode, sStartDay, sEndDay, false, nChoice, false);
					break;
				case "opt19" : 	//외부에서 내부로의 허용 세션로그 & 서비스 TOP10 (차트, 표)
					logger.debug("항목: 외부에서 내부로의 허용 세션로그 & 서비스 TOP10 (차트, 표)");
					Action_ServiceTop10(reportData, INBOUND, ALLOW, assetCode, sStartDay, sEndDay, false);
					break;
				case "opt20" : 	//외부에서 내부로의 차단 세션로그 & 서비스 TOP10 (차트, 표)
					logger.debug("항목: 외부에서 내부로의 차단 세션로그 & 서비스 TOP10 (차트, 표)");
					Action_ServiceTop10(reportData, INBOUND, CUTOFF, assetCode, sStartDay, sEndDay, !StringUtil.isEmpty(hData.get("ck20")));
					break;
				case "opt21" : 	//외부에서 내부로의 차단 세션로그 & 서비스 TOP10 상세 (차트)
					logger.debug("항목: 외부에서 내부로의 차단 세션로그 & 서비스 TOP10 상세 (차트)");
					Action_ServiceTop10_Detail_Period(reportData, INBOUND, CUTOFF, assetCode, sStartDay, sEndDay);
					break;
				case "opt22" : 	//내부에서 외부로의 허용 세션로그 & 서비스 TOP10 (차트, 표)
					logger.debug("항목: 내부에서 외부로의 허용 세션로그 & 서비스 TOP10 (차트, 표)");
					Action_ServiceTop10(reportData, OUTBOUND, ALLOW, assetCode, sStartDay, sEndDay, false);
					break;
				case "opt23" : 	//내부에서 외부로의 차단 세션로그 & 서비스 TOP10 (차트, 표)
					logger.debug("항목: 내부에서 외부로의 차단 세션로그 & 서비스 TOP10 (차트, 표)");
					Action_ServiceTop10(reportData, OUTBOUND, CUTOFF, assetCode, sStartDay, sEndDay, !StringUtil.isEmpty(hData.get("ck23")));
					break;
				case "opt24" : 	//내부에서 외부로의 차단 세션로그 & 서비스 TOP10 상세 (차트)
					logger.debug("항목: 내부에서 외부로의 차단 세션로그 & 서비스 TOP10 상세 (차트)");
					Action_ServiceTop10_Detail_Period(reportData, OUTBOUND, CUTOFF, assetCode, sStartDay, sEndDay);
					break;
			}
		}
		return reportData;
	}

	public void All_SessionLog_Trend(HashMap<String, Object> reportData, int nDirection, int assetCode, String sStartDay, String sEndDay, String sSearchPort) throws Exception {
		
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();

		int[] searchPort = null;
		if (sSearchPort != null && !sSearchPort.isEmpty()) {
			String[] saPorts = sSearchPort.split(",");
			searchPort = new int[saPorts.length];
			for (int i = 0; i < saPorts.length; i++) {
				searchPort[i] = Integer.valueOf(saPorts[i].trim());
			}
		}
		
		//허용/차단
		Iterable<DBObject> dbResult = fwDao.SessionLogTrend("MON", nDirection, ALLOW, assetCode, sStartDay, sEndDay, 0);

    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
    	for (DBObject val : dbResult) {
    		String sAction = (String)val.get("action");
    		String sYear = ((Integer)val.get("year")).toString();
    		String sMonth = ((Integer)val.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    		mapResult.put(sAction + sYear + sMonth, val);
    	}
    	
    	List<String> monthPeriod = fwDao.getPeriodMonth(sEndDay, -5, 1);
    	
    	String[] saGubun = {"차단", "허용"};
    	for (int action = 1; action >= 0; action--) {
    		for (String tMonth : monthPeriod) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		DBObject val = mapResult.get(String.valueOf(action) + tMonth);
	    		map.put("gubun", saGubun[action]);
	    		map.put("month", Integer.valueOf(tMonth));
	    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);

	    		dataSource.add(map);
	    		logger.debug(map.toString());
    		}	    		
    	}
    	
		dbResult = fwDao.SessionLogTrend("MON", nDirection, "", assetCode, sStartDay, sEndDay, 0);
		
    	mapResult = new HashMap<String, DBObject>(); 
    	for (DBObject val : dbResult) {
    		String sYear = ((Integer)val.get("year")).toString();
    		String sMonth = ((Integer)val.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    		mapResult.put(sYear + sMonth, val);
    	}
    	
		for (String tMonth : monthPeriod) {
    		HashMap<String, Object> map = new HashMap<String, Object>();
    		DBObject val = mapResult.get(tMonth);
    		map.put("gubun", "전체");
    		map.put("month", Integer.valueOf(tMonth));
    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
    		
    		dataSource.add(map);
    		logger.debug(map.toString());
		}

    	if (searchPort != null) {
    		for (int i = 0; i < searchPort.length; i++) {
	    		String strServiceName = fwDao.getPortServiceName(searchPort[i]);
	    		dbResult = fwDao.SessionLogTrend("MON", nDirection, ALLOW, assetCode, sStartDay, sEndDay, searchPort[i]);
	    	
	    		mapResult = new HashMap<String, DBObject>(); 
		    	for (DBObject val : dbResult) {
		    		String sYear = ((Integer)val.get("year")).toString();
		    		String sMonth = ((Integer)val.get("month")).toString();
		    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
		    		mapResult.put(sYear + sMonth, val);
		    	}
	    	
		    	for (String tMonth : monthPeriod) {
    	    		HashMap<String, Object> map = new HashMap<String, Object>();
    	    		DBObject val = mapResult.get(tMonth);
		    		map.put("gubun", strServiceName);
		    		map.put("month", Integer.valueOf(tMonth));
    	    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
    	    		
    	    		dataSource.add(map);
    	    		logger.debug(map.toString());
	    		}
    	    }
    	}
		
    	if (nDirection == INBOUND) {
    		reportData.put("OPT1", new SynthesisDataSource(dataSource));
    		reportData.put("OPT1_T", new SynthesisDataSource(dataSource));
    	} else if (nDirection == OUTBOUND) {
    		reportData.put("OPT4", new SynthesisDataSource(dataSource));
    		reportData.put("OPT4_T", new SynthesisDataSource(dataSource));
    	}
	}

	public void Action_SessionLog_Trend(HashMap<String, Object> reportData, int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();
		
		Iterable<DBObject> dbResult = fwDao.CompareSessionLogTrend("DY", nDirection, sAction, assetCode, fwDao.addMonth(sStartDay,-1), sEndDay);
		
    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
    	for (DBObject val : dbResult) {
    		String sYear = ((Integer)val.get("year")).toString();
    		String sMonth = ((Integer)val.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    		String sDay = ((Integer)val.get("day")).toString();
    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
    		mapResult.put(sAction + sYear + sMonth + sDay, val);
    	}

    	List<String> dayPeriod = fwDao.getPeriodDay(sStartDay, sEndDay, -1, 1);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		Date endDate = sdf.parse(sEndDay);
		Calendar endCal = fwDao.getCalendar(endDate);
		endCal.add(Calendar.MONTH, -2);

		for (String tDay : dayPeriod) {
    		HashMap<String, Object> map = new HashMap<String, Object>();
    		DBObject val = mapResult.get(sAction + tDay);

    		if (Integer.valueOf(tDay.substring(6,8)) == Integer.valueOf(sStartDay.substring(8, 10))) {
    			endCal.add(Calendar.MONTH, 1);
    		}
    		
			map.put("gubun", (endCal.get(Calendar.MONTH)+1) + "월");
			map.put("day", Integer.valueOf(tDay.substring(6,8)));
    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
			
    		dataSource.add(map);
    		logger.debug(map.toString());
		}
    	
		if (nDirection == INBOUND) {
	    	if (sAction.equals(ALLOW)) {
	    		reportData.put("OPT2", new SynthesisDataSource(dataSource));
	    	} else {
	    		reportData.put("OPT3", new SynthesisDataSource(dataSource));
	    	}
		} else if (nDirection == OUTBOUND) {
	    	if (sAction.equals(ALLOW)) {
	    		reportData.put("OPT5", new SynthesisDataSource(dataSource));
	    	} else {
	    		reportData.put("OPT6", new SynthesisDataSource(dataSource));
	    	}
		}
	}

	//외부에서 내부로의 전체 세션로그 & SIP TOP (표)
	public void Action_SessionLog_TopN(HashMap<String, Object> reportData, int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay, boolean bSrcIp, int nLimit, boolean bChk) throws Exception {
		
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();
		
    	//IP 건수기준 TOP N 리스트
		Iterable<DBObject> ipTopNResult = fwDao.IpTopN("MON", nDirection, sAction, assetCode, sStartDay, sEndDay, bSrcIp, nLimit);
    	List<String> topNIps = new ArrayList<String>();
    	
    	for (DBObject val : ipTopNResult) {
    		Iterable<DBObject> prevObj = null;
    		if (bSrcIp) {    		
    			topNIps.add((String)val.get("srcIp"));
        		prevObj = fwDao.IpSessionLogCount("MON", nDirection, sAction, assetCode, fwDao.addMonth(sStartDay,-1), fwDao.addMonth(sEndDay,-1), bSrcIp, (String)val.get("srcIp"));
    		} else {
    			topNIps.add((String)val.get("destIp"));
    			prevObj = fwDao.IpSessionLogCount("MON", nDirection, sAction, assetCode, fwDao.addMonth(sStartDay,-1), fwDao.addMonth(sEndDay,-1), bSrcIp, (String)val.get("destIp"));
    		}
    		val.put("prevCount", 0);
    		for (DBObject dbObj : prevObj) {
    			val.put("prevCount", prevObj != null ? ((Number)dbObj.get("count")).longValue() : 0);
    		}
    	}
		
    	int nNo = 1;
    	for (DBObject val : ipTopNResult) {
    		Iterable<DBObject> ipTopNCondition = null;
    		if (bSrcIp) {
    			ipTopNCondition = fwDao.IpTopNCondition("MON", nDirection, sAction, assetCode, sStartDay, sEndDay, (String)val.get("srcIp"), bSrcIp);
    		} else {
    			ipTopNCondition = fwDao.IpTopNCondition("MON", nDirection, sAction, assetCode, sStartDay, sEndDay, (String)val.get("destIp"), bSrcIp);
    		}
			for (DBObject dbObj : ipTopNCondition) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("no", nNo);
				if (bSrcIp) {
					map.put("srcIp", (String)val.get("srcIp"));
					map.put("destIp", (String)dbObj.get("destIp"));
				} else {
					map.put("destIp", (String)val.get("destIp"));
					map.put("srcIp", (String)dbObj.get("srcIp"));
				}
				
				if (nDirection == INBOUND) {
					map.put("geoCode", Constants.getCountryCode((String)dbObj.get("srcIp")));
				} else {
					map.put("geoCode", Constants.getCountryCode((String)dbObj.get("destIp")));
				}
				
				map.put("port", (Integer)dbObj.get("destPort"));
				map.put("total", ((Number)val.get("count")).longValue());
				map.put("prevCount", ((Number)val.get("prevCount")).longValue());
				map.put("action", (String)dbObj.get("action"));
				map.put("count", ((Number)dbObj.get("count")).longValue());
				map.put("rate", (((Number)dbObj.get("count")).longValue()*100f)/((Number)val.get("count")).longValue());
				
				dataSource.add(map);
				logger.debug(map.toString());
			}
			nNo++;
    	}
    	
    	if (bSrcIp) {
	    	if (nDirection == INBOUND && StringUtil.isEmpty(sAction)) { //외부에서 내부로의 전체 세션로그 & SIP TOP (표)
	    		reportData.put("OPT7", new SynthesisDataSource(dataSource));
	    	} else if (nDirection == INBOUND && sAction.equals(ALLOW)) { //외부에서 내부로의 허용 세션로그 & SIP TOP (표)
	    		reportData.put("OPT8", new SynthesisDataSource(dataSource));
	    	} else if (nDirection == INBOUND && sAction.equals(CUTOFF)) { //외부에서 내부로의 차단 세션로그 & SIP TOP (표)
	    		reportData.put("OPT9", new SynthesisDataSource(dataSource));
	    	} else if (nDirection == OUTBOUND && StringUtil.isEmpty(sAction)) { //내부에서 외부로의 전체 세션로그 & SIP TOP (표)
	    		reportData.put("OPT10", new SynthesisDataSource(dataSource));
	    	} else if (nDirection == OUTBOUND && sAction.equals(ALLOW)) { //내부에서 외부로의 허용 세션로그 & SIP TOP (표)
	    		reportData.put("OPT11", new SynthesisDataSource(dataSource));
	    	} else if (nDirection == OUTBOUND && sAction.equals(CUTOFF)) { //내부에서 외부로의 차단 세션로그 & SIP TOP (표)
	    		reportData.put("OPT12", new SynthesisDataSource(dataSource));
	    	}
	    	
	    	if (bChk) { //SRC IP TOP10 세션 로그 발생추이 
	        	dataSource = new ArrayList<HashMap<String, Object>>();
	        	Iterable<DBObject> ipTopNTrend = fwDao.IpTopNTrend("DY", nDirection, sAction, assetCode, sStartDay, sEndDay, topNIps, true);
	        	
	        	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
	        	for (DBObject val : ipTopNTrend) {
	        		String sSrcIp = (String)val.get("srcIp");
	        		String sYear = ((Integer)val.get("year")).toString();
	        		String sMonth = ((Integer)val.get("month")).toString();
	        		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
	        		String sDay = ((Integer)val.get("day")).toString();
	        		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
	        		mapResult.put(sSrcIp + "-" + sYear + sMonth + sDay, val);
	        	}
	        	
	        	List<String> dayPeriod = fwDao.getPeriodDay(sStartDay, sEndDay, 1);
	        	
	        	for (String strIp : topNIps) {
	        		for (String tDay : dayPeriod) {
	    	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    	    		DBObject val = mapResult.get(strIp + "-" + tDay);
	    	    		map.put("srcIp", strIp);
			    		map.put("day", Integer.valueOf(tDay));
	    	    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
    		    	
	    	    		dataSource.add(map);
    					logger.debug(map.toString());
	        		}
	        	}
	    		
		    	if (nDirection == INBOUND) {
		    		reportData.put("OPT9_G", new SynthesisDataSource(dataSource));
		    	} else if (nDirection == OUTBOUND) {
		    		reportData.put("OPT12_G", new SynthesisDataSource(dataSource));
		    	}
	    	}
    	} else {
	    	if (nDirection == INBOUND && StringUtil.isEmpty(sAction)) { //외부에서 내부로의 전체 세션로그 & DIP TOP (표)
	    		reportData.put("OPT13", new SynthesisDataSource(dataSource));
	    	} else if (nDirection == INBOUND && sAction.equals(ALLOW)) { //외부에서 내부로의 허용 세션로그 & DIP TOP (표)
	    		reportData.put("OPT14", new SynthesisDataSource(dataSource));
	    	} else if (nDirection == INBOUND && sAction.equals(CUTOFF)) { //외부에서 내부로의 차단 세션로그 & DIP TOP (표)
	    		reportData.put("OPT15", new SynthesisDataSource(dataSource));
	    	} else if (nDirection == OUTBOUND && StringUtil.isEmpty(sAction)) { //내부에서 외부로의 전체 세션로그 & DIP TOP (표)
	    		reportData.put("OPT16", new SynthesisDataSource(dataSource));
	    	} else if (nDirection == OUTBOUND && sAction.equals(ALLOW)) { //내부에서 외부로의 허용 세션로그 & DIP TOP (표)
	    		reportData.put("OPT17", new SynthesisDataSource(dataSource));
	    	} else if (nDirection == OUTBOUND && sAction.equals(CUTOFF)) { //내부에서 외부로의 차단 세션로그 & DIP TOP (표)
	    		reportData.put("OPT18", new SynthesisDataSource(dataSource));
	    	}
    	}

	}


	@SuppressWarnings("unchecked")
	public void Action_ServiceTop10(HashMap<String, Object> reportData, int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay, boolean bChk) throws Exception {
		
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();

    	//이전데이타 조회
    	HashMap<String, Object> prevMap = new HashMap<String, Object>();
    	Iterable<DBObject> prevResult = fwDao.ServiceSessionLog("MON", nDirection, sAction, assetCode, fwDao.addMonth(sStartDay,-1), fwDao.addMonth(sEndDay,-1));
		for (DBObject obj : prevResult) {
			prevMap.put(String.valueOf((Integer)obj.get("destPort")), obj);
		}
    	
		Iterable<DBObject> logResult = fwDao.ServiceSessionLog("MON", nDirection, sAction, assetCode, sStartDay, sEndDay);   	
		
    	int nTop = 1;
    	long lTotalCount = 0;
    	List<Integer> topNPort = new ArrayList<Integer>();
    	HashMap<String, Object> currMap = new HashMap<String, Object>(); 
    	for (DBObject val : logResult) {
			if ((Integer)val.get("destPort") != -1 && nTop <= 10) {
				topNPort.add((Integer)val.get("destPort"));
				
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("no", nTop);		
				map.put("port", (Integer)val.get("destPort"));
				map.put("svcName", fwDao.getPortServiceName((Integer)val.get("destPort")));

				HashMap<String, Object> pMap = (HashMap<String, Object>)prevMap.remove(String.valueOf((Integer)val.get("destPort")));
				
				long lCurrCount = ((Number)val.get("count")).longValue();
				long lPrevCount = pMap != null ? ((Number)pMap.get("count")).longValue() : 0; 
				
				lTotalCount += lCurrCount;
				
				map.put("count", lCurrCount);
				map.put("countVariation", lCurrCount - lPrevCount);
				
				nTop++;
				
				dataSource.add(map);
				logger.debug(map.toString());
			} else {
				lTotalCount += ((Number)val.get("count")).longValue();
				currMap.put(String.valueOf((Integer)val.get("destPort")), val);
			}
    	}
		
    	long lPrevEtcCount = 0;
    	Set<Entry<String, Object>> prevEntry = prevMap.entrySet();
    	for (Entry<String, Object> obj : prevEntry) {
    		HashMap<String,Object> map = (HashMap<String,Object>)obj.getValue();
    		lPrevEtcCount += ((Number)map.get("count")).longValue();
    	}
    	
    	long lCurrEtcCount = 0;
    	Set<Entry<String, Object>> currEntry = currMap.entrySet();
    	for (Entry<String, Object> obj : currEntry) {
    		HashMap<String,Object> map = (HashMap<String,Object>)obj.getValue();
    		lCurrEtcCount += ((Number)map.get("count")).longValue();
    	}

    	if (dataSource.size() > 0) { //기타 건수가 없을 경우 제거 ?
	    	HashMap<String, Object> map1 = new HashMap<String, Object>();
			map1.put("no", nTop);		
			map1.put("port", -1);
			map1.put("svcName", "기타");
			map1.put("count", lCurrEtcCount);
			map1.put("countVariation", lCurrEtcCount - lPrevEtcCount);
			dataSource.add(map1);
    	}
    	
		for (HashMap<String, Object> map : dataSource) {
			long count = ((Number)map.get("count")).longValue();
			map.put("ratio", (count * 100f) / lTotalCount);
		}
	
		if (nDirection == INBOUND && sAction.equals(ALLOW)) {
			reportData.put("OPT19", new SynthesisDataSource(dataSource));
			reportData.put("OPT19_T", new SynthesisDataSource(dataSource));
		} else if (nDirection == INBOUND && sAction.equals(CUTOFF)) {
			reportData.put("OPT20", new SynthesisDataSource(dataSource));
			reportData.put("OPT20_T", new SynthesisDataSource(dataSource));
		} else if (nDirection == OUTBOUND && sAction.equals(ALLOW)) {
			reportData.put("OPT22", new SynthesisDataSource(dataSource));
			reportData.put("OPT22_T", new SynthesisDataSource(dataSource));
		} else if (nDirection == OUTBOUND && sAction.equals(CUTOFF)) {
			reportData.put("OPT23", new SynthesisDataSource(dataSource));
			reportData.put("OPT23_T", new SynthesisDataSource(dataSource));
		}
		
		if (bChk) {
			dataSource = new ArrayList<HashMap<String, Object>>();
			Iterable<DBObject> portTrend = fwDao.ServiceTopNTrend("DY", nDirection, sAction, assetCode, sStartDay, sEndDay, topNPort);
        	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
        	for (DBObject val : portTrend) {
        		int nPort = (Integer)val.get("destPort");
        		String sYear = ((Integer)val.get("year")).toString();
        		String sMonth = ((Integer)val.get("month")).toString();
        		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
        		String sDay = ((Integer)val.get("day")).toString();
        		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
        		mapResult.put(nPort + "-" + sYear + sMonth + sDay, val);
        	}

        	List<String> dayPeriod = fwDao.getPeriodDay(sStartDay, sEndDay, 1);

        	for (int nPort : topNPort) {
        		for (String tDay : dayPeriod) {
		    		DBObject val = mapResult.get(nPort + "-" + tDay);
		    		HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("port", nPort);
					map.put("svcName", fwDao.getPortServiceName(nPort));
		    		map.put("day", tDay);
		    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
		    		dataSource.add(map);
					logger.debug(map.toString());
        		}
        	}
    		
	    	if (nDirection == INBOUND) {
	    		reportData.put("OPT20_G", new SynthesisDataSource(dataSource));
	    	} else if (nDirection == OUTBOUND) {
	    		reportData.put("OPT23_G", new SynthesisDataSource(dataSource));
	    	}
        	
		}
	}

	public void Action_ServiceTop10_Detail_Period(HashMap<String, Object> reportData, int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();

		Iterable<DBObject> logResult = fwDao.ServiceSessionLog("MON", nDirection, sAction, assetCode, sStartDay, sEndDay);   	
		
    	int nTop = 1;
    	List<HashMap<String, Object>> topNPort = new ArrayList<HashMap<String, Object>>();
    	for (DBObject val : logResult) {
			if ((Integer)val.get("destPort") != -1 && nTop <= 10) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("port", (Integer)val.get("destPort"));
				map.put("count", ((Number)val.get("count")).longValue());
				topNPort.add(map);
				nTop++;
			} else if (nTop > 10) {
				break;
			}
    	}

    	//서비스 TOP10 현황 (로그건수기준)
    	int nNo = 1;
    	boolean isExist = false;
    	for (HashMap<String, Object> val : topNPort) {
    		int nPort = (Integer)val.get("port");
    		long totalCount = ((Number)val.get("count")).longValue();
    		isExist = false;
    		
    		Iterable<DBObject> svcCondition = fwDao.ServiceCondition("MON", nDirection, sAction, assetCode, sStartDay, sEndDay, nPort);
    		
    		for (DBObject dbObj : svcCondition) {
    			isExist = true;
    			HashMap<String, Object> map = new HashMap<String, Object>();
    			map.put("no", nNo);
    			map.put("port", nPort);
    			map.put("total", totalCount);
    			map.put("srcIp", (String)dbObj.get("srcIp"));
    			map.put("destIp", (String)dbObj.get("destIp"));
    			map.put("count", ((Number)dbObj.get("count")).longValue());
    			map.put("ratio", (((Number)dbObj.get("count")).longValue() * 100f) / totalCount);
    			
				if (nDirection == INBOUND) {
					map.put("geoCode", Constants.getCountryCode((String)dbObj.get("srcIp")));
				} else {
					map.put("geoCode", Constants.getCountryCode((String)dbObj.get("destIp")));
				}
    			
    			dataSource.add(map);
    			logger.debug(map.toString());
    		}
    		if (isExist == false) {
    			HashMap<String, Object> map = new HashMap<String, Object>();
    			map.put("no", nNo);
    			map.put("port", nPort);
    			map.put("total", totalCount);
    			map.put("srcIp", "-");
    			map.put("destIp", "-");
    			map.put("count", 0l);
    			map.put("ratio", 0l);
    			map.put("geoCode", "-");
    			
    			dataSource.add(map);
    			logger.debug(map.toString());
    		}
    		nNo++;
    	}
    	
		if (nDirection == INBOUND && sAction.equals(CUTOFF)) {
			reportData.put("OPT21", new SynthesisDataSource(dataSource));
		} else if (nDirection == OUTBOUND && sAction.equals(CUTOFF)) {
			reportData.put("OPT24", new SynthesisDataSource(dataSource));
		}
	}
	
}
