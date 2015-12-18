package com.cyberone.report.core.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

public class WafDailyReport extends BaseReport {
	
	private WafDao wafDao;

	private Logger logger = LoggerFactory.getLogger(getClass());

	private UserInfo userInfo;
	
	public WafDailyReport(UserInfo userInfo) {
		this.userInfo = userInfo;
		this.wafDao = new WafDao(userInfo.getPromDb(), userInfo.getReportDb());
	}
	
	/* 
	 * 웹방화벽 일일보고서 통계데이타 생성
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getDataSource(String sReportType, String sSearchDate, HashMap<String, Object> hMap, int ItemNo, List<String> contentsList) throws Exception {
		logger.debug("웹방화벽 일일보고서 통계데이타 생성");

		HashMap<String, Object> reportData = new HashMap<String, Object>();
		
		contentsList.add(ItemNo + ". " + (String)hMap.get("assetName") + " 장비의 일간 웹방화벽 탐지로그 분석");
		reportData.put("RT", ItemNo + ". " + (String)hMap.get("assetName") + " 장비의 일간 웹방화벽 탐지로그 분석"); //Report Title
		
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
			DBObject dbObj = wafDao.selectAutoReportForm(Integer.parseInt(sReportType), assetCode);
			hData = (new ObjectMapper()).readValue(StringUtil.convertString(dbObj.get("formData")), HashMap.class);
			
			List<Map.Entry<String,Object>> list = new LinkedList<>(hData.entrySet());
			Collections.sort(list, new Comparator<Map.Entry<String, Object>>() {
		        @Override
		        public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
		            return (o1.getKey()).compareTo(o2.getKey());
		        }
		    });
			hData = new LinkedHashMap<>();
		    for (Map.Entry<String, Object> entry : list) {
		    	hData.put(entry.getKey(), entry.getValue());
		    }
		} else {
			hData = (HashMap<String,Object>)hFormMap.get("data");
			sEtc = StringUtil.convertString(hFormMap.get("etc"));
		}

		int nC1 = 0, nC2 = 0, nC3 = 0, nC4 = 0, nC5 = 0;
		int nS1 = 1, nS2 = 1, nS3 = 1, nS4 = 1, nS5 = 1;

		int nChoice = 0;
		for (Entry<String, Object> e : hData.entrySet()) {
			
			switch (e.getKey()) {
				//탐지로그 발생 추이
				case "opt01" : case "opt02" :
					if (nC1 == 0) { 
						contentsList.add("  " + ItemNo + "." + ++nC1 + " 탐지로그 발생추이");
						reportData.put("C1", "  " + ItemNo + "." + nC1 + " 탐지로그 발생추이");
					}
					break;
				//이벤트 현황
				case "opt03" :
					if (nC2 == 0) { 
						contentsList.add("  " + ItemNo + "." + (++nC2 + nC1) + " 이벤트 현황");
						reportData.put("C2", "  " + ItemNo + "." + (nC2 + nC1) + " 이벤트 현황");
					}
					break;
				//출발지IP 현황
				case "opt04" :
					if (nC3 == 0) { 
						contentsList.add("  " + ItemNo + "." + (++nC3 + nC2 + nC1) + " 출발지IP 현황");
						reportData.put("C3", "  " + ItemNo + "." + (nC3 + nC2 + nC1) + " 출발지IP 현황");
					}
					break;
				//도메인별 상세통계
				case "opt05" : case "opt06" : case "opt07" :
					if (nC4 == 0) { 
						contentsList.add("  " + ItemNo + "." + (++nC4 + nC3 + nC2 + nC1) + " 도메인별 상세통계");
						reportData.put("C4", "  " + ItemNo + "." + (nC4 + nC3 + nC2 + nC1) + " 도메인별 상세통계");
					}
					break;
				//성능정보
				case "opt99" :
					if (nC5 == 0) { 
						contentsList.add("  " + ItemNo + "." + (++nC5 + nC4 + nC3 + nC2 + nC1) + " 성능정보");
						reportData.put("C5", "  " + ItemNo + "." + (nC5 + nC4 + nC3 + nC2 + nC1) + " 성능정보");
					}
					break;
			}
			
			switch (e.getKey()) {
				case "opt01" :	//전체 탐지로그 발생추이
					push(contentsList, "    ", ItemNo, nC1, nS1++, " 전체 탐지로그 발생추이");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd1")));
					All_DetectLog_Trend(reportData, assetCode, sStartDay, sEndDay, nChoice);
					break;
				case "opt02" : 	//전체 탐지로그 & 도메인 TOP10 발생추이 (차트)
					push(contentsList, "    ", ItemNo, nC1, nS1++, " 전체 탐지로그 & 도메인 TOP10 발생추이");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd2")));
					All_Domain_TopN_Trend(reportData, assetCode, sStartDay, sEndDay, nChoice);
					break;
				case "opt03" : 	//전체 탐지로그 & Event TOP (차트, 표)
					push(contentsList, "    ", ItemNo, nC2 + nC1, nS2++, " 전체 탐지로그 & 이벤트 TOP");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd3")));
					ALL_Event_TopN(reportData, assetCode, sStartDay, sEndDay, nChoice);
					break;
				case "opt04" :	//전체 탐지로그 & SIP TOP (표)
					push(contentsList, "    ", ItemNo, nC3 + nC2 + nC1, nS3++, " 전체 탐지로그 & 출발지IP TOP");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd4")));
					ALL_SrcIp_TopN(reportData, assetCode, sStartDay, sEndDay, nChoice, !StringUtil.isEmpty(hData.get("ck4")), StringUtil.convertString(hData.get("sd4")));
					break;
				case "opt05" : 	//도메인 별 탐지로그 & 탐지로그 발생추이
					push(contentsList, "    ", ItemNo, nC4 + nC3 + nC2 + nC1, nS4++, " 도메인별 탐지로그 발생추이");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd5")));
					Domain_TopN_Trend(reportData, assetCode, sStartDay, sEndDay, nChoice);
					break;
				case "opt06" : 	//도메인 별 탐지로그 & EVT TOP10 발생추이
					push(contentsList, "    ", ItemNo, nC4 + nC3 + nC2 + nC1, nS4++, " 도메인별 이벤트 TOP 발생추이");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd6")));
					Domain_EventTopN_Trend(reportData, assetCode, sStartDay, sEndDay, nChoice);
					break;
				case "opt07" : 	//도메인 별 탐지로그 & EVT TOP10 통계 (차트, 표)
					push(contentsList, "    ", ItemNo, nC4 + nC3 + nC2 + nC1, nS4++, " 도메인별 이벤트 TOP 통계");
					Domain_EventTopN_Condition(reportData, assetCode, sStartDay, sEndDay);
					break;
				case "opt99" :	//성능정보
					//push(contentsList, "    ", ItemNo, nC5 + nC4 + nC3 + nC2 + nC1, nS5++, " 성능 정보");
					PerformanceInfo(wafDao, reportData, assetCode, sStartDay, sEndDay, "HR");
					break;
			}
		}
		
		return reportData;
	}

	private void All_DetectLog_Trend(HashMap<String, Object> reportData, int assetCode, String sStartDay, String sEndDay, int nChoice) throws Exception {
		
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();

		if (nChoice == 1) {	//해당일
			Iterable<DBObject> dbResult = wafDao.DetectLogTrend("HR", ALLOW, assetCode, sStartDay, sEndDay);
		
	    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
	    	for (DBObject val : dbResult) {
	    		String sAction = (String)val.get("action");
	    		String sYear = ((Integer)val.get("year")).toString();
	    		String sMonth = ((Integer)val.get("month")).toString();
	    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
	    		String sDay = ((Integer)val.get("day")).toString();
	    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
	    		String sHour = ((Integer)val.get("hour")).toString();
	    		sHour = sHour.length() == 1 ? "0" + sHour : sHour;
	    		mapResult.put(sAction + sYear + sMonth + sDay + sHour, val);
	    	}
	    	
	    	List<String> hourDay = wafDao.getPeriodHour(sStartDay, 0);
	    	
	    	String[] saGubun = {"차단", "허용"};
    		for (int action = 0; action < 2; action++) { //허용/차단
    			for (String tDay : hourDay) {
	    			for (int h = 0; h < 24; h++) { //시간
	    	    		HashMap<String, Object> map = new HashMap<String, Object>();
			    		DBObject val = mapResult.get(String.valueOf(action) + tDay + (h < 10 ? "0" + h : String.valueOf(h)));
			    		map.put("gubun", saGubun[action]);
	        			map.put("hour", String.valueOf(h));
	    	    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
			    		
	    	    		dataSource.add(map);
	    	    		logger.debug(map.toString());
	    			}
    			}
    		}
	    	
			dbResult = wafDao.DetectLogTrend("HR", "", assetCode, sStartDay, sEndDay);
			
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

			Iterable<DBObject> dbResult = wafDao.DetectLogTrend("DY", ALLOW, assetCode, wafDao.addDate(sStartDay,-2), sEndDay);

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
	    	
	    	List<String> dayPeriod = wafDao.getPeriodDay(wafDao.addDate(sStartDay,-2), sEndDay, 1);
	    	
	    	String[] saGubun = {"차단", "허용"};
	    	for (int action = 1; action >= 0; action--) {
	    		for (String tDay : dayPeriod) {
		    		HashMap<String, Object> map = new HashMap<String, Object>();
		    		DBObject val = mapResult.get(String.valueOf(action) + tDay);
		    		map.put("gubun", saGubun[action]);
		    		map.put("day", Integer.valueOf(tDay));
		    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);

    	    		dataSource.add(map);
    	    		logger.debug(map.toString());
	    		}	    		
	    	}
	    	
			dbResult = wafDao.DetectLogTrend("DY", "", assetCode, wafDao.addDate(sStartDay,-2), sEndDay);
			
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
			reportData.put("OPT1", new SynthesisDataSource(dataSource));
			reportData.put("OPT1_T", new SynthesisDataSource(dataSource));
		}
		
	}

	private void All_Domain_TopN_Trend(HashMap<String, Object> reportData, int assetCode, String sStartDay, String sEndDay, int nChoice) throws Exception {

		List<HashMap<String, Object>>  dataSource = new ArrayList<HashMap<String, Object>>();
		
		Iterable<DBObject> dbTopN = wafDao.DomainTopN("DY", assetCode,sStartDay, sEndDay, 10);
    	List<String> saTopN = new ArrayList<String>();
    	for (DBObject val : dbTopN) {
    		saTopN.add((String)val.get("host"));
    	}
		
		if (nChoice == 1) { //도메인 TOP10 시간별 발생추이
			
	    	Iterable<DBObject> dbResult = wafDao.DomainTopNTrend("HR", "", assetCode, sStartDay, sEndDay, saTopN);
	    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
	    	for (DBObject val : dbResult) {
	    		String sHost = (String)val.get("host");
	    		String sYear = ((Integer)val.get("year")).toString();
	    		String sMonth = ((Integer)val.get("month")).toString();
	    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
	    		String sDay = ((Integer)val.get("day")).toString();
	    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
	    		String sHour = ((Integer)val.get("hour")).toString();
	    		sHour = sHour.length() == 1 ? "0" + sHour : sHour;
	    		mapResult.put(sHost + "-" + sYear + sMonth + sDay + sHour, val);
	    	}
	    	
	    	List<String> hourDay = wafDao.getPeriodHour(sStartDay, 0);
	    	
	    	for (String sHost : saTopN) {
	    		for (String tDay : hourDay) {
	    			for (int h = 0; h < 24; h++) { //시간
			    		DBObject val = mapResult.get(sHost + "-" + tDay + (h < 10 ? "0" + h : String.valueOf(h)));
			    		HashMap<String, Object> map = new HashMap<String, Object>();
			    		map.put("gubun", sHost);
			    		map.put("hour", String.valueOf(h));
			    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
			    		
			    		dataSource.add(map);
			    		logger.debug(map.toString());
	    			}
		    	}
	    	}
			
		} else if (nChoice == 2) { //도메인 TOP10 일별 발생추이
		
	    	Iterable<DBObject> dbResult = wafDao.DomainTopNTrend("DY", "", assetCode, wafDao.addDate(sStartDay,-2), sEndDay, saTopN);
	    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
	    	for (DBObject val : dbResult) {
	    		String sHost = (String)val.get("host");
	    		String sYear = ((Integer)val.get("year")).toString();
	    		String sMonth = ((Integer)val.get("month")).toString();
	    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
	    		String sDay = ((Integer)val.get("day")).toString();
	    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
	    		mapResult.put(sHost + "-" + sYear + sMonth + sDay, val);
	    	}
	    	
	    	List<String> dayPeriod = wafDao.getPeriodDay(wafDao.addDate(sStartDay,-2), sEndDay, 1);
	    	
	    	for (String sHost : saTopN) {
	    		for (String tDay : dayPeriod) {
		    		HashMap<String, Object> map = new HashMap<String, Object>();
		    		DBObject val = mapResult.get(String.valueOf(sHost) + "-" + tDay);
		    		map.put("gubun", sHost);
		    		map.put("day", Integer.valueOf(tDay));
		    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);

    	    		dataSource.add(map);
    	    		logger.debug(map.toString());
	    		}	    		
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
		
		Iterable<DBObject> dbTopN = wafDao.DomainTopN("DY", assetCode, sStartDay, sEndDay, 10);

		int nNo;
		long mEtcCount;
    	for (DBObject mVal : dbTopN) {
    		String sHost = (String)mVal.get("host");
    		logger.debug("도메인 : " + sHost);
    		
    		dataSource1 = new ArrayList<HashMap<String, Object>>();
    		dataSource2 = new ArrayList<HashMap<String, Object>>();
    		
    		Iterable<DBObject> dbBefore = wafDao.DomainEventTopN("DY", assetCode, wafDao.addDate(sStartDay, -1), wafDao.addDate(sEndDay, -1), sHost, 0);
    		
    		HashMap<String, DBObject> mapBefore = new HashMap<String, DBObject>();
        	for (DBObject val : dbBefore) {
        		mapBefore.put((String)val.get("message"), val);
        	}
    		
    		Iterable<DBObject> dbResult = wafDao.DomainEventTopN("DY", assetCode, sStartDay, sEndDay, sHost, 0);
    		nNo = 1;
    		mEtcCount = 0;
    		for (DBObject val : dbResult) {
    			String sMessage = (String)val.get("message");
    			
    			DBObject prevMap = mapBefore.get(sMessage);
    			
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
        		
        		Iterable<DBObject> srcIpTop5 = wafDao.DomainEventSrcIpCondition("DY", assetCode, sStartDay, sEndDay, sHost, sMessage, 5);
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
		
		Iterable<DBObject> dbTopN = wafDao.DomainTopN("DY", assetCode, sStartDay, sEndDay, 10);
		
		if (nChoice == 1) { //도메인 TOP10 시간별 발생추이
		
	    	for (DBObject mVal : dbTopN) {
	    		String sHost = (String)mVal.get("host");
	    		logger.debug("도메인 : " + sHost);
	    		
	    		Iterable<DBObject> dbResult = wafDao.DomainEventTopN("DY", assetCode, sStartDay, sEndDay, sHost, 10);
	        	List<String> saTopN = new ArrayList<String>();
	        	for (DBObject val : dbResult) {
	        		saTopN.add((String)val.get("message"));
	        	}

	        	dbResult = wafDao.DomainEventTopNTrend("HR", assetCode, sStartDay, sEndDay, sHost, saTopN);
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
        		
    	    	List<String> hourDay = wafDao.getPeriodHour(sStartDay, 0);
    	    	
    	    	dataSource = new ArrayList<HashMap<String, Object>>();
    	    	
    	    	for (String sMessage : saTopN) {
    	    		for (String tDay : hourDay) {
    	    			for (int h = 0; h < 24; h++) { //시간
    			    		DBObject val = mapResult.get(sMessage + "-" + tDay + (h < 10 ? "0" + h : String.valueOf(h)));
    			    		HashMap<String, Object> map = new HashMap<String, Object>();
    			    		map.put("gubun", sMessage);
    			    		map.put("hour", String.valueOf(h));
    			    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
    			    		
    			    		dataSource.add(map);
    			    		logger.debug(map.toString());
    	    			}
    		    	}
    	    	}
	    	
    	    	chartMap.put(sHost, new SynthesisDataSource(dataSource));
	    	}			
			
		} else {
			
	    	for (DBObject mVal : dbTopN) {
	    		String sHost = (String)mVal.get("host");
	    		logger.debug("도메인 : " + sHost);
	    		
	    		Iterable<DBObject> dbResult = wafDao.DomainEventTopN("DY", assetCode, sStartDay, sEndDay, sHost, 10);
	        	List<String> saTopN = new ArrayList<String>();
	        	for (DBObject val : dbResult) {
	        		saTopN.add((String)val.get("message"));
	        	}
	    		
	        	dbResult = wafDao.DomainEventTopNTrend("DY", assetCode, wafDao.addDate(sStartDay,-2), sEndDay, sHost, saTopN);
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
        		
    	    	List<String> dayPeriod = wafDao.getPeriodDay(wafDao.addDate(sStartDay,-2), sEndDay, 1);
    	    	
    	    	dataSource = new ArrayList<HashMap<String, Object>>();
    	    	
    	    	for (String sMessage : saTopN) {
    	    		for (String tDay : dayPeriod) {
    		    		HashMap<String, Object> map = new HashMap<String, Object>();
    		    		DBObject val = mapResult.get(String.valueOf(sMessage) + "-" + tDay);
    		    		map.put("gubun", sMessage);
    		    		map.put("day", Integer.valueOf(tDay));
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
		
		Iterable<DBObject> dbTopN = wafDao.DomainTopN("DY", assetCode, sStartDay, sEndDay, 10);
    	List<String> saTopN = new ArrayList<String>();
    	for (DBObject val : dbTopN) {
    		saTopN.add((String)val.get("host"));
    	}
		
		HashMap<String, Object> chartMap = new HashMap<String, Object>();
		HashMap<String, Object> gridMap = new HashMap<String, Object>();
    	
    	if (nChoice == 1) { //도메인 TOP10 시간별 발생추이
    		
	    	for (DBObject mVal : dbTopN) {
	    		String sHost = (String)mVal.get("host");
	    		logger.debug("도메인 : " + sHost);
	    		
	    		Iterable<DBObject> dbResult = wafDao.DomainTopNTrend("HR", ALLOW, assetCode, sStartDay, sEndDay, sHost);

		    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
		    	for (DBObject val : dbResult) {
		    		String sAction = (String)val.get("action");
		    		String sYear = ((Integer)val.get("year")).toString();
		    		String sMonth = ((Integer)val.get("month")).toString();
		    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
		    		String sDay = ((Integer)val.get("day")).toString();
		    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
		    		String sHour = ((Integer)val.get("hour")).toString();
		    		sHour = sHour.length() == 1 ? "0" + sHour : sHour;
		    		mapResult.put(sAction + sYear + sMonth + sDay + sHour, val);
		    	}
		    	
		    	List<String> hourDay = wafDao.getPeriodHour(sStartDay, 0);
		    	
		    	String[] saGubun = {"차단", "탐지"};
	    		for (int action = 0; action < 2; action++) { //허용/차단
	    			for (String tDay : hourDay) {
		    			for (int h = 0; h < 24; h++) { //시간
		    	    		HashMap<String, Object> map = new HashMap<String, Object>();
				    		DBObject val = mapResult.get(String.valueOf(action) + tDay + (h < 10 ? "0" + h : String.valueOf(h)));
				    		map.put("gubun", saGubun[action]);
		        			map.put("hour", String.valueOf(h));
		    	    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
				    		
		    	    		dataSource.add(map);
		    	    		logger.debug(map.toString());
		    			}
	    			}
	    		}
		    	
	    		dbResult = wafDao.DomainTopNTrend("HR", "", assetCode, sStartDay, sEndDay, sHost);
				
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
	    		
		    	chartMap.put(sHost, new SynthesisDataSource(dataSource));
	    	}
    	
	    	reportData.put("OPT5", chartMap);
	    	
		} else {
		
			for (DBObject mVal : dbTopN) {
	    		String sHost = (String)mVal.get("host");
	    		logger.debug("도메인 : " + sHost);
	    		
	    		Iterable<DBObject> dbResult = wafDao.DomainTopNTrend("DY", ALLOW, assetCode, wafDao.addDate(sStartDay,-2), sEndDay, sHost);

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
				
		    	List<String> dayPeriod = wafDao.getPeriodDay(wafDao.addDate(sStartDay,-2), sEndDay, 1);
		    	
		    	String[] saGubun = {"차단", "탐지"};
	    		for (int action = 0; action < 2; action++) { //허용/차단
		    		for (String tDay : dayPeriod) {
	    	    		HashMap<String, Object> map = new HashMap<String, Object>();
			    		DBObject val = mapResult.get(String.valueOf(action) + tDay);
			    		map.put("gubun", saGubun[action]);
			    		map.put("day", Integer.valueOf(tDay));
			    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
	
	    	    		dataSource.add(map);
	    	    		logger.debug(map.toString());
		    		}	    		
	    		}
	    		
	    		dbResult = wafDao.DomainTopNTrend("DY", "", assetCode, wafDao.addDate(sStartDay,-2), sEndDay, sHost);
	    		
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
	    		
		    	chartMap.put(sHost, new SynthesisDataSource(dataSource));
		    	gridMap.put(sHost, new SynthesisDataSource(dataSource));
			}
			
			reportData.put("OPT6_1", chartMap);
			reportData.put("OPT6_2", gridMap);
		}
	}
	
	//전체 탐지로그 & Event TOP
	private void ALL_Event_TopN(HashMap<String, Object> reportData, int assetCode, String sStartDay, String sEndDay, int nLimit) throws Exception {
		
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();
		
		Iterable<DBObject> dbResult = wafDao.EventTopN("DY", assetCode, sStartDay, sEndDay, 0);

		int nTotal = 0;
    	for (DBObject val : dbResult) {
    		String sMessage = (String)val.get("message");
    		val.put("allow", 0);
    		val.put("cutoff", 0);
    		
    		nTotal += ((Number)val.get("count")).longValue();
    		
    		Iterable<DBObject> dbTmp = wafDao.EventActionCount("DY", assetCode, sStartDay, sEndDay, sMessage);
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
			map.put("count", nCount);
			map.put("allow", nAllow);
			map.put("cutoff", nCutoff);
	
			dataSource1.add(map);
			logger.debug(map.toString());
    	}
		
		reportData.put("OPT2_1", new SynthesisDataSource(dataSource1));
    	
    	
		//표 [순위 | 이벤트명 | 탐지건수 | 증감현황 | 도메인 | Action | 비율(%)]
		dbResult = wafDao.EventTopN("DY", assetCode, wafDao.addDate(sStartDay,-1), wafDao.addDate(sEndDay,-1), 0);
    	HashMap<String, DBObject> mapBefore = new HashMap<String, DBObject>(); 
    	for (DBObject val : dbResult) {
    		mapBefore.put((String)val.get("message"), val);
    	}
    	
    	List<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();
    	
    	nNo = 1;
    	for (HashMap<String, Object> hMap : dataSource1) {
    		String sMessage = (String)hMap.get("message");
    		
    		if (sMessage.equals("기타")) continue;
    		
    		Iterable<DBObject> dbTmp = wafDao.EventDomainTopN("DY", assetCode, sStartDay, sEndDay, sMessage);
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
	private void ALL_SrcIp_TopN(HashMap<String, Object> reportData, int assetCode, String sStartDay, String sEndDay, int nLimit, boolean bChk, String sOpt) throws Exception {

		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();
		
		//표 [순위 | SRC IP | 국가정보 | 전체건수(증감현황) | 이벤트명 | 도메인 | Action | 비율(%)]
		Iterable<DBObject> dbTopN = wafDao.SrcIpTopN("DY", assetCode, sStartDay, sEndDay, nLimit, null);
		
    	List<String> saTopN = new ArrayList<String>();
    	for (DBObject val : dbTopN) {
    		saTopN.add((String)val.get("srcIp"));
    	}

    	//전일 SrcIp TopN
    	Iterable<DBObject> dbBefore = wafDao.SrcIpTopN("DY", assetCode, wafDao.addDate(sStartDay, -1), wafDao.addDate(sEndDay, -1), 0, saTopN);
		
    	HashMap<String, DBObject> mapBefore = new HashMap<String, DBObject>();
    	for (DBObject val : dbBefore) {
    		mapBefore.put((String)val.get("srcIp"), val);
    	}
    	
    	int nNo = 1;
		for (DBObject val : dbTopN) {
			String sSrcIp = (String)val.get("srcIp");
			
			Iterable<DBObject> dbTop5 = wafDao.EventHostCondition("DY", assetCode, sStartDay, sEndDay, sSrcIp);
			
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
			
			if (Integer.valueOf(sOpt) == 1) { //해당일
				
		    	Iterable<DBObject> dbResult = wafDao.SrcIpTopNTrend("HR", assetCode, sStartDay, sEndDay, saTopN);
		    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
		    	for (DBObject val : dbResult) {
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
		    	
		    	List<String> hourDay = wafDao.getPeriodHour(sStartDay, 0);
		    	
		    	for (String sSrcIp : saTopN) {
		    		for (String tDay : hourDay) {
		    			for (int h = 0; h < 24; h++) { //시간
				    		DBObject val = mapResult.get(sSrcIp + "-" + tDay + (h < 10 ? "0" + h : String.valueOf(h)));
				    		HashMap<String, Object> map = new HashMap<String, Object>();
				    		map.put("gubun", sSrcIp);
				    		map.put("hour", String.valueOf(h));
				    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
				    		
				    		dataSource.add(map);
				    		logger.debug(map.toString());
		    			}
			    	}
		    	}
				
			} else {
				
		    	Iterable<DBObject> dbResult = wafDao.SrcIpTopNTrend("DY", assetCode, wafDao.addDate(sStartDay,-2), sEndDay, saTopN);
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
		    	
		    	List<String> dayPeriod = wafDao.getPeriodDay(wafDao.addDate(sStartDay,-2), sEndDay, 1);
		    	
		    	for (String sSrcIp : saTopN) {
		    		for (String tDay : dayPeriod) {
			    		HashMap<String, Object> map = new HashMap<String, Object>();
			    		DBObject val = mapResult.get(String.valueOf(sSrcIp) + "-" + tDay);
			    		map.put("gubun", sSrcIp);
			    		map.put("day", Integer.valueOf(tDay));
			    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);

	    	    		dataSource.add(map);
	    	    		logger.debug(map.toString());
		    		}	    		
		    	}	    	
				
			}
			reportData.put("OPT4_2", new SynthesisDataSource(dataSource));
		}
		
	}
	
	public void push(List<String> contentsList, String sIdt, int n1, int n2, int n3, String msg) {
		String sTitle = sIdt + n1 + "." + n2 + "." + n3 + msg;    
		contentsList.add(sTitle);
		HashMap<String, Object> pMap = new HashMap<String, Object>();
    	pMap.put("message", sTitle);
		try {
			ObjectMapper mapper = new ObjectMapper();
			userInfo.getWsSession().sendMessage(new TextMessage(mapper.writeValueAsString(pMap)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}

