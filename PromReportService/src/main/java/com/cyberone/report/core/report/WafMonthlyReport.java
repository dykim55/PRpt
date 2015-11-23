package com.cyberone.report.core.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;

import com.cyberone.report.Constants;
import com.cyberone.report.core.dao.WafDao;
import com.cyberone.report.core.datasource.SynthesisDataSource;
import com.cyberone.report.model.UserInfo;
import com.cyberone.report.utils.StringUtil;
import com.mongodb.DBObject;

public class WafMonthlyReport extends BaseReport {
	
	private WafDao wafDao;

	private Logger logger = LoggerFactory.getLogger(getClass());

	private UserInfo userInfo;
	
	public WafMonthlyReport(UserInfo userInfo) {
		this.userInfo = userInfo;
		this.wafDao = new WafDao(userInfo.getPromDb(), userInfo.getReportDb());
	}
	
	/* 
	 * 웹방화벽 월간보고서 통계데이타 생성
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getDataSource(String sReportType, String sSearchDate, HashMap<String, Object> hMap) throws Exception {
		logger.debug("웹방화벽 월간보고서 통계데이타 생성");
	
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		
		String sStartDay = "";
		String sEndDay = "";
		
		String sAssetCode = StringUtil.convertString(hMap.get("assetCode"));
		int assetCode = Integer.valueOf(sAssetCode.substring(0, sAssetCode.indexOf("_")));

		//조회 시작일/종료일
		int statBaseDay = wafDao.getStatMonthBaseDay(assetCode);
		if (statBaseDay > 1) {
			Date tmpSDate = sdf.parse(sSearchDate);
			Calendar startCal = wafDao.getCalendar(tmpSDate);
			startCal.add(Calendar.DAY_OF_MONTH, statBaseDay);
			startCal.add(Calendar.DAY_OF_MONTH, -1);
			
			Date tmpEDate = sdf.parse(sSearchDate);
			Calendar endCal = wafDao.getCalendar(tmpEDate);
			endCal.add(Calendar.MONTH, 1);
			endCal.add(Calendar.DAY_OF_MONTH, statBaseDay);
			endCal.add(Calendar.DAY_OF_MONTH, -1);
			endCal.add(Calendar.SECOND, -1);
			
			sStartDay = sdf2.format(startCal.getTime());
			sEndDay = sdf2.format(endCal.getTime());
		} else {
			Date tmpSDate = sdf.parse(sSearchDate);
			Calendar startCal = wafDao.getCalendar(tmpSDate);
		
			Date tmpEDate = sdf.parse(sSearchDate);
			Calendar endCal = wafDao.getCalendar(tmpEDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);

			sStartDay = sdf2.format(startCal.getTime());
			sEndDay = sdf2.format(endCal.getTime());
		}
		
		HashMap<String,Object> hFormMap = (HashMap<String,Object>)hMap.get("formData");
		
		String sEtc = "";
		HashMap<String,Object> hData = null;
		if (hFormMap == null) {
			DBObject dbObj = wafDao.selectAutoReportForm(Integer.parseInt(sReportType), assetCode);
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
					All_DetectLog_Trend(reportData, assetCode, sStartDay, sEndDay);
					break;
				case "opt2" : 	//전체 탐지로그 & 도메인 TOP10 발생추이 (차트)
					push("항목: 전체 탐지로그 & 도메인 TOP10 발생추이 (차트)");
					All_Domain_TopN_Trend(reportData, assetCode, sStartDay, sEndDay);
					break;
				case "opt3" : 	//전체 탐지로그 & Event TOP (차트, 표)
					push("항목: 전체 탐지로그 & Event TOP (차트, 표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd3")));
					ALL_Event_TopN(reportData, assetCode, sStartDay, sEndDay, nChoice);
					break;
				case "opt4" :	//전체 탐지로그 & SIP TOP (표)
					push("항목: 전체 탐지로그 & SIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd4")));
					ALL_SrcIp_TopN(reportData, assetCode, sStartDay, sEndDay, nChoice, !StringUtil.isEmpty(hData.get("ck4")), Integer.valueOf(StringUtil.convertString(hData.get("sd4"))));
					break;
				case "opt5" : 	//도메인 별 탐지로그 & 탐지로그 발생추이
					push("항목: 도메인 별 탐지로그 & 탐지로그 발생추이");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd6")));
					Domain_TopN_Trend(reportData, assetCode, sStartDay, sEndDay, nChoice);
					break;
				case "opt6" : 	//도메인 별 탐지로그 & EVT TOP10 발생추이
					push("항목: 도메인 별 탐지로그 & EVT TOP10 발생추이");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd7")));
					Domain_EventTopN_Trend(reportData, assetCode, sStartDay, sEndDay, nChoice);
					break;
				case "opt7" : 	//도메인 별 탐지로그 & EVT TOP10 통계 (차트, 표)
					push("항목: 도메인 별 탐지로그 & EVT TOP10 통계 (차트, 표)");
					Domain_EventTopN_Condition(reportData, assetCode, sStartDay, sEndDay);
					break;
			}
		}
		return reportData;
	}

	private void All_DetectLog_Trend(HashMap<String, Object> reportData, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();

		Iterable<DBObject> dbResult = wafDao.DetectLogTrend("MON", ALLOW, assetCode, wafDao.addMonth(sStartDay, -5), sEndDay);

    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
    	for (DBObject val : dbResult) {
    		String sAction = (String)val.get("action");
    		String sYear = ((Integer)val.get("year")).toString();
    		String sMonth = ((Integer)val.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    		mapResult.put(sAction + sYear + sMonth, val);
    	}
    	
    	List<String> monthPeriod = wafDao.getPeriodMonth(sEndDay, -5, 1);
    	
    	String[] saGubun = {"차단", "허용"};
    	for (int action = 1; action >= 0; action--) {
    		for (String tMonth : monthPeriod) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		DBObject val = mapResult.get(String.valueOf(action) + tMonth);
	    		map.put("gubun", saGubun[action]);
	    		map.put("month", tMonth);
	    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);

	    		dataSource.add(map);
	    		logger.debug(map.toString());
    		}	    		
    	}
    	
		dbResult = wafDao.DetectLogTrend("MON", "", assetCode, wafDao.addMonth(sStartDay,-5), sEndDay);
		
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
    		map.put("month", tMonth);
    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
    		
    		dataSource.add(map);
    		logger.debug(map.toString());
		}
    	
		reportData.put("OPT1_1", new SynthesisDataSource(dataSource));
		reportData.put("OPT1_2", new SynthesisDataSource(dataSource));
	}

	private void All_Domain_TopN_Trend(HashMap<String, Object> reportData, int assetCode, String sStartDay, String sEndDay) throws Exception {

		List<HashMap<String, Object>>  dataSource = new ArrayList<HashMap<String, Object>>();
		
		Iterable<DBObject> dbTopN = wafDao.DomainTopN("MON", assetCode, sStartDay, sEndDay, 10);
    	List<String> saTopN = new ArrayList<String>();
    	for (DBObject val : dbTopN) {
    		saTopN.add((String)val.get("host"));
    	}
		
    	Iterable<DBObject> dbResult = wafDao.DomainTopNTrend("MON", "", assetCode, wafDao.addMonth(sStartDay, -5), sEndDay, saTopN);
    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
    	for (DBObject val : dbResult) {
    		String sHost = (String)val.get("host");
    		String sYear = ((Integer)val.get("year")).toString();
    		String sMonth = ((Integer)val.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    		mapResult.put(sHost + "-" + sYear + sMonth, val);
    	}
    	
    	List<String> monthPeriod = wafDao.getPeriodMonth(sEndDay, -5, 1);
    	
    	for (String sHost : saTopN) {
    		for (String tMonth : monthPeriod) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		DBObject val = mapResult.get(String.valueOf(sHost) + "-" + tMonth);
	    		map.put("gubun", sHost);
	    		map.put("month", tMonth);
	    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);

	    		dataSource.add(map);
	    		logger.debug(map.toString());
    		}	    		
    	}	    	
		
		reportData.put("OPT2", new SynthesisDataSource(dataSource));
	}

	//도메인 별 탐지로그 & EVT TOP10 통계
	private void Domain_EventTopN_Condition(HashMap<String, Object> reportData, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();

		HashMap<String, Object> chartMap = new HashMap<String, Object>();
		HashMap<String, Object> gridMap = new HashMap<String, Object>();
		
		Iterable<DBObject> dbTopN = wafDao.DomainTopN("MON", assetCode, sStartDay, sEndDay, 10);

		int nNo;
		long mEtcCount;
    	for (DBObject mVal : dbTopN) {
    		String sHost = (String)mVal.get("host");
    		logger.debug("도메인 : " + sHost);
    		
    		dataSource1 = new ArrayList<HashMap<String, Object>>();
    		dataSource2 = new ArrayList<HashMap<String, Object>>();
    		
    		Iterable<DBObject> dbBefore = wafDao.DomainEventTopN("MON", assetCode, wafDao.addMonth(sStartDay, -1), wafDao.addMonth(sEndDay, -1), sHost, 0);
    		
    		HashMap<String, DBObject> mapBefore = new HashMap<String, DBObject>();
        	for (DBObject val : dbBefore) {
        		mapBefore.put((String)val.get("message"), val);
        	}
    		
    		Iterable<DBObject> dbResult = wafDao.DomainEventTopN("MON", assetCode, sStartDay, sEndDay, sHost, 0);
    		nNo = 1;
    		mEtcCount = 0;
    		for (DBObject val : dbResult) {
    			String sMessage = (String)val.get("message");
    			
    			DBObject prevMap = mapBefore.get(sMessage);;
    			
        		HashMap<String, Object> m_Map = new HashMap<String, Object>();
        		if (!sMessage.equals("-1") && nNo <= 10) {
	        		m_Map.put("message", sMessage);
	        		m_Map.put("count", ((Number)val.get("count")).longValue());
	        		m_Map.put("total", ((Number)mVal.get("count")).longValue());
	        		m_Map.put("prevCount", prevMap != null ? ((Number)prevMap.get("count")).longValue() : 0);

	        		dataSource1.add(m_Map);
	        		logger.debug(m_Map.toString());
	        		
	        		mEtcCount += ((Number)val.get("count")).longValue();
        		} else {
        			continue;
        		}
        		
        		Iterable<DBObject> srcIpTop5 = wafDao.DomainEventSrcIpCondition("MON", assetCode, sStartDay, sEndDay, sHost, sMessage, 5);
        		for (DBObject sVal : srcIpTop5) {
            		m_Map = new HashMap<String, Object>();
            		m_Map.put("no", nNo);
            		m_Map.put("message", sMessage);
            		m_Map.put("total", ((Number)val.get("count")).longValue());
            		m_Map.put("prevCount", prevMap != null ? ((Number)prevMap.get("count")).longValue() : 0);
            		m_Map.put("srcIp", StringUtil.convertString(sVal.get("srcIp")));
            		m_Map.put("action", StringUtil.convertString(sVal.get("action")));
            		m_Map.put("count", ((Number)sVal.get("count")).longValue());
            		
	        		dataSource2.add(m_Map);
	        		logger.debug(m_Map.toString());
        		}
    		}
    		
    		mEtcCount = ((Number)mVal.get("count")).longValue() - mEtcCount;
    		
    		if (mEtcCount > 0) {
	    		HashMap<String, Object> m_Map = new HashMap<String, Object>();
	    		m_Map.put("message", "기타");
        		m_Map.put("count", mEtcCount);
        		m_Map.put("total", ((Number)mVal.get("count")).longValue());
        		m_Map.put("prevCount", 0);
	    		dataSource1.add(m_Map);
    		}
    		
    		chartMap.put(sHost, new SynthesisDataSource(dataSource1));
    		gridMap.put(sHost, new SynthesisDataSource(dataSource2));
    	}
    	
    	reportData.put("OPT7_1", chartMap);
    	reportData.put("OPT7_2", gridMap);
	}
	
	//도메인 별 탐지로그 & EVT TOP10 발생추이
	private void Domain_EventTopN_Trend(HashMap<String, Object> reportData, int assetCode, String sStartDay, String sEndDay, int nChoice) throws Exception {
		
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();

		HashMap<String, Object> chartMap = new HashMap<String, Object>();
		
		Iterable<DBObject> dbTopN = wafDao.DomainTopN("MON", assetCode, sStartDay, sEndDay, 10);
		
		if (nChoice == 1) { //도메인 TOP10 일별 발생추이
		
	    	for (DBObject mVal : dbTopN) {
	    		String sHost = (String)mVal.get("host");
	    		logger.debug("도메인 : " + sHost);
	    		
	    		Iterable<DBObject> dbResult = wafDao.DomainEventTopN("MON", assetCode, sStartDay, sEndDay, sHost, 10);
	        	List<String> saTopN = new ArrayList<String>();
	        	for (DBObject val : dbResult) {
	        		saTopN.add((String)val.get("message"));
	        	}

	        	dbResult = wafDao.DomainEventTopNTrend("DY", assetCode, sStartDay, sEndDay, sHost, saTopN);
    	    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
    	    	for (DBObject val : dbResult) {
    	    		String sMessage = (String)val.get("message");
    	    		String sYear = ((Integer)val.get("year")).toString();
    	    		String sMonth = ((Integer)val.get("month")).toString();
    	    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    	    		String sDay = ((Integer)val.get("day")).toString();
    	    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
    	    		mapResult.put(sMessage + "-" + sYear + sMonth + sDay, val);
    	    	}
        		
    	    	List<String> dayPeriod = wafDao.getPeriodDay(sStartDay, sEndDay, 1);
    	    	
    	    	dataSource = new ArrayList<HashMap<String, Object>>();
    	    	
    	    	for (String sMessage : saTopN) {
    	    		for (String tDay : dayPeriod) {
			    		DBObject val = mapResult.get(sMessage + "-" + tDay);
			    		HashMap<String, Object> map = new HashMap<String, Object>();
			    		map.put("gubun", sMessage);
			    		map.put("day", tDay);
			    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
			    		
			    		dataSource.add(map);
			    		logger.debug(map.toString());
    		    	}
    	    	}
	    	
    	    	chartMap.put(sHost, new SynthesisDataSource(dataSource));
	    	}			
			
		} else {
			
	    	for (DBObject mVal : dbTopN) {
	    		String sHost = (String)mVal.get("host");
	    		logger.debug("도메인 : " + sHost);
	    		
	    		Iterable<DBObject> dbResult = wafDao.DomainEventTopN("MON", assetCode, sStartDay, sEndDay, sHost, 10);
	        	List<String> saTopN = new ArrayList<String>();
	        	for (DBObject val : dbResult) {
	        		saTopN.add((String)val.get("message"));
	        	}
	    		
	        	dbResult = wafDao.DomainEventTopNTrend("MON", assetCode, wafDao.addMonth(sStartDay,-5), sEndDay, sHost, saTopN);
    	    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
    	    	for (DBObject val : dbResult) {
    	    		String sMessage = (String)val.get("message");
    	    		String sYear = ((Integer)val.get("year")).toString();
    	    		String sMonth = ((Integer)val.get("month")).toString();
    	    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    	    		mapResult.put(sMessage + "-" + sYear + sMonth, val);
    	    	}
        		
    	    	List<String> monthPeriod = wafDao.getPeriodMonth(sEndDay, -5, 1);
    	    	
    	    	dataSource = new ArrayList<HashMap<String, Object>>();
    	    	
    	    	for (String sMessage : saTopN) {
    	    		for (String tMonth : monthPeriod) {
    		    		HashMap<String, Object> map = new HashMap<String, Object>();
    		    		DBObject val = mapResult.get(String.valueOf(sMessage) + "-" + tMonth);
    		    		map.put("gubun", sMessage);
    		    		map.put("month", tMonth);
    		    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);

        	    		dataSource.add(map);
        	    		logger.debug(map.toString());
    	    		}	    		
    	    	}	    	
	    	
    	    	chartMap.put(sHost, new SynthesisDataSource(dataSource));
	    	}
			
		}
		
		reportData.put("OPT6", chartMap);
	}

	private void Domain_TopN_Trend(HashMap<String, Object> reportData, int assetCode, String sStartDay, String sEndDay, int nChoice) throws Exception {

		List<HashMap<String, Object>>  dataSource = new ArrayList<HashMap<String, Object>>();
		
		Iterable<DBObject> dbTopN = wafDao.DomainTopN("MON", assetCode, sStartDay, sEndDay, 10);
    	List<String> saTopN = new ArrayList<String>();
    	for (DBObject val : dbTopN) {
    		saTopN.add((String)val.get("host"));
    	}
		
		HashMap<String, Object> chartMap = new HashMap<String, Object>();
		HashMap<String, Object> gridMap = new HashMap<String, Object>();
    	
    	if (nChoice == 1) { //도메인 TOP10 일별 발생추이
    		
	    	for (DBObject mVal : dbTopN) {
	    		String sHost = (String)mVal.get("host");
	    		logger.debug("도메인 : " + sHost);
	    		
	    		Iterable<DBObject> dbResult = wafDao.DomainTopNTrend("DY", ALLOW, assetCode, sStartDay, sEndDay, sHost);

		    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
		    	for (DBObject val : dbResult) {
		    		String sAction = (String)val.get("action");
		    		String sYear = ((Integer)val.get("year")).toString();
		    		String sMonth = ((Integer)val.get("month")).toString();
		    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
		    		String sDay = ((Integer)val.get("day")).toString();
		    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
		    		mapResult.put(sAction + sYear + sMonth + sDay, val);
		    	}
		    	
		    	List<String> dayPeriod = wafDao.getPeriodDay(sStartDay, sEndDay, 1);
		    	
		    	String[] saGubun = {"차단", "탐지"};
	    		for (int action = 0; action < 2; action++) { //허용/차단
	    			for (String tDay : dayPeriod) {
	    	    		HashMap<String, Object> map = new HashMap<String, Object>();
			    		DBObject val = mapResult.get(String.valueOf(action) + tDay);
			    		map.put("gubun", saGubun[action]);
	        			map.put("day", tDay);
	    	    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
			    		
	    	    		dataSource.add(map);
	    	    		logger.debug(map.toString());
	    			}
	    		}
		    	
	    		dbResult = wafDao.DomainTopNTrend("DY", "", assetCode, sStartDay, sEndDay, sHost);
				
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
		    		map.put("day", tDay);
    	    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
		    		
    	    		dataSource.add(map);
    	    		logger.debug(map.toString());
		    	}
	    		
		    	chartMap.put(sHost, new SynthesisDataSource(dataSource));
	    	}
    	
	    	reportData.put("OPT5", chartMap);
	    	
		} else {
		
			for (DBObject mVal : dbTopN) {
	    		String sHost = (String)mVal.get("host");
	    		logger.debug("도메인 : " + sHost);
	    		
	    		Iterable<DBObject> dbResult = wafDao.DomainTopNTrend("MON", ALLOW, assetCode, wafDao.addMonth(sStartDay,-5), sEndDay, sHost);

		    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
		    	for (DBObject val : dbResult) {
		    		String sAction = (String)val.get("action");
		    		String sYear = ((Integer)val.get("year")).toString();
		    		String sMonth = ((Integer)val.get("month")).toString();
		    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
		    		mapResult.put(sAction + sYear + sMonth, val);
		    	}
				
		    	List<String> monthPeriod = wafDao.getPeriodMonth(sEndDay, -5, 1);
		    	
		    	String[] saGubun = {"차단", "탐지"};
	    		for (int action = 0; action < 2; action++) { //허용/차단
		    		for (String tMonth : monthPeriod) {
	    	    		HashMap<String, Object> map = new HashMap<String, Object>();
			    		DBObject val = mapResult.get(String.valueOf(action) + tMonth);
			    		map.put("gubun", saGubun[action]);
			    		map.put("month", tMonth);
			    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
	
	    	    		dataSource.add(map);
	    	    		logger.debug(map.toString());
		    		}	    		
	    		}
	    		
	    		dbResult = wafDao.DomainTopNTrend("MON", "", assetCode, wafDao.addMonth(sStartDay,-5), sEndDay, sHost);
	    		
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
		    		map.put("month", tMonth);
		    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);

    	    		dataSource.add(map);
    	    		logger.debug(map.toString());
	    		}	    		
	    		
		    	chartMap.put(sHost, new SynthesisDataSource(dataSource));
		    	gridMap.put(sHost, new SynthesisDataSource(dataSource));
			}
			
			reportData.put("OPT5_1", chartMap);
			reportData.put("OPT5_2", gridMap);
		}
	}
	
	private void SrcIp_Top10_Trend(HashMap<String, Object> reportData, int assetCode, String sStartDay, String sEndDay, int nChoice) throws Exception {

		List<HashMap<String, Object>>  dataSource = new ArrayList<HashMap<String, Object>>();

		Iterable<DBObject> dbTopN = wafDao.SrcIpTopN("MON", assetCode, sStartDay, sEndDay, 10, null);
		
    	List<String> saTopN = new ArrayList<String>();
    	for (DBObject val : dbTopN) {
    		saTopN.add((String)val.get("srcIp"));
    	}
		
		if (nChoice == 1) { //TOP10 일별 발생추이
			
	    	Iterable<DBObject> dbResult = wafDao.SrcIpTopNTrend("DY", assetCode, sStartDay, sEndDay, saTopN);
	    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
	    	for (DBObject val : dbResult) {
	    		String sSrcIp = (String)val.get("srcIp");
	    		String sYear = ((Integer)val.get("year")).toString();
	    		String sMonth = ((Integer)val.get("month")).toString();
	    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
	    		String sDay = ((Integer)val.get("day")).toString();
	    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
	    		mapResult.put(sSrcIp + "-" + sYear + sMonth + sDay, val);
	    	}
	    	
	    	List<String> dayPeriod = wafDao.getPeriodDay(sStartDay, sEndDay, 1);
	    	
	    	for (String sSrcIp : saTopN) {
	    		for (String tDay : dayPeriod) {
		    		DBObject val = mapResult.get(sSrcIp + "-" + tDay);
		    		HashMap<String, Object> map = new HashMap<String, Object>();
		    		map.put("gubun", sSrcIp);
		    		map.put("day", tDay);
		    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
		    		
		    		dataSource.add(map);
		    		logger.debug(map.toString());
		    	}
	    	}
			
		} else if (nChoice == 2) { //TOP10 월별 발생추이
		
	    	Iterable<DBObject> dbResult = wafDao.SrcIpTopNTrend("MON", assetCode, wafDao.addMonth(sStartDay,-5), sEndDay, saTopN);
	    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
	    	for (DBObject val : dbResult) {
	    		String sSrcIp = (String)val.get("srcIp");
	    		String sYear = ((Integer)val.get("year")).toString();
	    		String sMonth = ((Integer)val.get("month")).toString();
	    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
	    		mapResult.put(sSrcIp + "-" + sYear + sMonth, val);
	    	}
	    	
	    	List<String> monthPeriod = wafDao.getPeriodMonth(sEndDay, -5, 1);
	    	
	    	for (String sSrcIp : saTopN) {
	    		for (String tMonth : monthPeriod) {
		    		HashMap<String, Object> map = new HashMap<String, Object>();
		    		DBObject val = mapResult.get(String.valueOf(sSrcIp) + "-" + tMonth);
		    		map.put("gubun", sSrcIp);
		    		map.put("month", tMonth);
		    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);

    	    		dataSource.add(map);
    	    		logger.debug(map.toString());
	    		}	    		
	    	}	    	
		}
		
		reportData.put("OPT5", new SynthesisDataSource(dataSource));
	}
	
	//전체 탐지로그 & Event TOP
	private void ALL_Event_TopN(HashMap<String, Object> reportData, int assetCode, String sStartDay, String sEndDay, int nLimit) throws Exception {
		
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();
		
		Iterable<DBObject> dbResult = wafDao.EventTopN("MON", assetCode, sStartDay, sEndDay, 0);

		int nTotal = 0;
    	for (DBObject val : dbResult) {
    		String sMessage = (String)val.get("message");
    		val.put("allow", 0);
    		val.put("cutoff", 0);
    		
    		nTotal += ((Number)val.get("count")).longValue();
    		
    		Iterable<DBObject> dbTmp = wafDao.EventActionCount("MON", assetCode, sStartDay, sEndDay, sMessage);
    		for (DBObject val2 : dbTmp) {
    			String sAction = (String)val2.get("action");
    			if (sAction.equals(ALLOW)) {
    				val.put("allow", val2 != null ? ((Number)val2.get("count")).longValue() : 0);
    			} else {
    				val.put("cutoff", val2 != null ? ((Number)val2.get("count")).longValue() : 0);
    			}
    		}
    	}

    	int nNo = 1;
    	int nCount = 0, nAllow = 0, nCutoff = 0;
    	for (DBObject val : dbResult) {
    		String sMessage = (String)val.get("message");
    		if (!sMessage.equals("-1") && nNo <= 10) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		map.put("message", sMessage);
	    		map.put("total", nTotal);
	    		map.put("count", ((Number)val.get("count")).longValue());
	    		map.put("allow", ((Number)val.get("allow")).longValue());
	    		map.put("cutoff", ((Number)val.get("cutoff")).longValue());

	    		dataSource1.add(map);
	    		logger.debug(map.toString());
    		} else {
    			nCount += ((Number)val.get("count")).longValue();
    			nAllow += ((Number)val.get("allow")).longValue();
    			nCutoff += ((Number)val.get("cutoff")).longValue();
    		}
    		nNo++;
    	}
    	
    	if (nCount > 0) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("message", "기타");
			map.put("total", nTotal);
			map.put("count", nCount);
			map.put("allow", nAllow);
			map.put("cutoff", nCutoff);
	
			dataSource1.add(map);
			logger.debug(map.toString());
    	}
		
		reportData.put("OPT2_1", new SynthesisDataSource(dataSource1));
    	
    	
		//표 [순위 | 이벤트명 | 탐지건수 | 증감현황 | 도메인 | Action | 비율(%)]
		dbResult = wafDao.EventTopN("MON", assetCode, wafDao.addMonth(sStartDay,-1), wafDao.addMonth(sEndDay,-1), 0);
    	HashMap<String, DBObject> mapBefore = new HashMap<String, DBObject>(); 
    	for (DBObject val : dbResult) {
    		mapBefore.put((String)val.get("message"), val);
    	}
    	
    	List<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();
    	
    	nNo = 1;
    	for (HashMap<String, Object> hMap : dataSource1) {
    		String sMessage = (String)hMap.get("message");
    		
    		if (sMessage.equals("기타")) continue;
    		
    		Iterable<DBObject> dbTmp = wafDao.EventDomainTopN("MON", assetCode, sStartDay, sEndDay, sMessage);
    		for (DBObject pMap : dbTmp) {
    	
        		HashMap<String, Object> map = new HashMap<String, Object>();
        		map.put("no", nNo);
        		map.put("message", sMessage);
        		map.put("total", ((Number)hMap.get("count")).longValue());
        		
        		DBObject prevMap = mapBefore.get(sMessage);
        		map.put("prevCount", prevMap != null ? ((Number)prevMap.get("count")).longValue() : 0);
        		map.put("host", (String)pMap.get("host"));
        		map.put("action", (String)pMap.get("action"));
        		map.put("count", ((Number)pMap.get("count")).longValue());
        		
        		dataSource2.add(map);
    			logger.debug(map.toString());
    		}
    		nNo++;
    	}
		
    	reportData.put("OPT2_2", new SynthesisDataSource(dataSource2));
	}

	//전체 탐지로그 & SIP TOP
	private void ALL_SrcIp_TopN(HashMap<String, Object> reportData, int assetCode, String sStartDay, String sEndDay, int nLimit, boolean bChk, int nOpt) throws Exception {

		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();
		
		//표 [순위 | SRC IP | 국가정보 | 전체건수(증감현황) | 이벤트명 | 도메인 | Action | 비율(%)]
		Iterable<DBObject> dbTopN = wafDao.SrcIpTopN("MON", assetCode, sStartDay, sEndDay, nLimit, null);
		
    	List<String> saTopN = new ArrayList<String>();
    	for (DBObject val : dbTopN) {
    		saTopN.add((String)val.get("srcIp"));
    	}

    	//전월 SrcIp TopN
    	Iterable<DBObject> dbBefore = wafDao.SrcIpTopN("MON", assetCode, wafDao.addMonth(sStartDay, -1), wafDao.addMonth(sEndDay, -1), 0, saTopN);
		
    	HashMap<String, DBObject> mapBefore = new HashMap<String, DBObject>();
    	for (DBObject val : dbBefore) {
    		mapBefore.put((String)val.get("srcIp"), val);
    	}
    	
    	int nNo = 1;
		for (DBObject val : dbTopN) {
			String sSrcIp = (String)val.get("srcIp");
			
			Iterable<DBObject> dbTop5 = wafDao.EventHostCondition("MON", assetCode, sStartDay, sEndDay, sSrcIp);
			
			HashMap<String, Object> map = new HashMap<String, Object>();
			for (DBObject hVal : dbTop5) {
				map.put("no", nNo);
				map.put("srcIp", sSrcIp);
				map.put("geoCode", Constants.getCountryCode(sSrcIp));
				map.put("total", ((Number)val.get("count")).longValue());
				
        		DBObject prevMap = mapBefore.get(sSrcIp);
        		map.put("prevCount", prevMap != null ? ((Number)prevMap.get("count")).longValue() : 0);
				
				map.put("message", StringUtil.convertString(hVal.get("message")));
				map.put("host", StringUtil.convertString(hVal.get("host")));
				map.put("action", StringUtil.convertString(hVal.get("action")));
				map.put("count", ((Number)hVal.get("count")).longValue());
				
        		dataSource.add(map);
    			logger.debug(map.toString());
			}
			nNo++;
		}
    	
		reportData.put("OPT4_1", new SynthesisDataSource(dataSource));
		
		if (bChk) {
			
			if (nOpt == 1) { //최근 1개월
				
		    	Iterable<DBObject> dbResult = wafDao.SrcIpTopNTrend("DY", assetCode, sStartDay, sEndDay, saTopN);
		    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
		    	for (DBObject val : dbResult) {
		    		String sSrcIp = (String)val.get("srcIp");
		    		String sYear = ((Integer)val.get("year")).toString();
		    		String sMonth = ((Integer)val.get("month")).toString();
		    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
		    		String sDay = ((Integer)val.get("day")).toString();
		    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
		    		mapResult.put(sSrcIp + "-" + sYear + sMonth + sDay, val);
		    	}
		    	
		    	List<String> dayPeriod = wafDao.getPeriodDay(sStartDay, sEndDay, 1);
		    	
		    	for (String sSrcIp : saTopN) {
		    		for (String tDay : dayPeriod) {
			    		DBObject val = mapResult.get(sSrcIp + "-" + tDay);
			    		HashMap<String, Object> map = new HashMap<String, Object>();
			    		map.put("gubun", sSrcIp);
			    		map.put("day", tDay);
			    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
			    		
			    		dataSource.add(map);
			    		logger.debug(map.toString());
			    	}
		    	}
				
			} else {
				
		    	Iterable<DBObject> dbResult = wafDao.SrcIpTopNTrend("MON", assetCode, wafDao.addMonth(sStartDay,-5), sEndDay, saTopN);
		    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
		    	for (DBObject val : dbResult) {
		    		String sSrcIp = (String)val.get("srcIp");
		    		String sYear = ((Integer)val.get("year")).toString();
		    		String sMonth = ((Integer)val.get("month")).toString();
		    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
		    		mapResult.put(sSrcIp + "-" + sYear + sMonth, val);
		    	}
		    	
		    	List<String> monthPeriod = wafDao.getPeriodMonth(sEndDay, -5, 1);
		    	
		    	for (String sSrcIp : saTopN) {
		    		for (String tMonth : monthPeriod) {
			    		HashMap<String, Object> map = new HashMap<String, Object>();
			    		DBObject val = mapResult.get(String.valueOf(sSrcIp) + "-" + tMonth);
			    		map.put("gubun", sSrcIp);
			    		map.put("month", tMonth);
			    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);

	    	    		dataSource.add(map);
	    	    		logger.debug(map.toString());
		    		}	    		
		    	}	    	
			}
			
			reportData.put("OPT4_2", new SynthesisDataSource(dataSource));
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

