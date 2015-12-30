package com.cyberone.report.core.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.socket.TextMessage;

import com.cyberone.report.Constants;
import com.cyberone.report.core.dao.WafDao;
import com.cyberone.report.core.datasource.SynthesisDataSource;
import com.cyberone.report.model.UserInfo;
import com.cyberone.report.utils.StringUtil;
import com.mongodb.DBObject;

public class WafPeriodReport extends BaseReport {
	
	private WafDao wafDao;

	private UserInfo userInfo;
	
	public WafPeriodReport(UserInfo userInfo) {
		this.userInfo = userInfo;
		this.wafDao = new WafDao(userInfo.getPromDb(), userInfo.getReportDb());
	}
	
	/* 
	 * 웹방화벽 임의기간보고서 통계데이타 생성
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getDataSource(String sReportType, String sSearchDate, HashMap<String, Object> hMap, int ItemNo, List<String> contentsList) throws Exception {
		logger.debug("웹방화벽 임의기간보고서 통계데이타 생성");
		
		HashMap<String, Object> reportData = new HashMap<String, Object>();
		
		reportData.put("reportType", sReportType);
		
		contentsList.add(ItemNo + ". " + (String)hMap.get("assetName") + " 장비의 웹방화벽 탐지로그 분석");
		reportData.put("RT", ItemNo + ". " + (String)hMap.get("assetName") + " 장비의 웹방화벽 탐지로그 분석"); //Report Title
		
		String sStartDay = "";
		String sEndDay = "";
		
		String sAssetCode = StringUtil.convertString(hMap.get("assetCode"));
		int assetCode = Integer.valueOf(sAssetCode.substring(0, sAssetCode.indexOf("_")));

		//조회 시작일/종료일
		String[] saPeriod = sSearchDate.split("\\|");
		sStartDay = saPeriod[0];
		sEndDay = saPeriod[1];
		
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
		List<String> saSubNo = new ArrayList<String>(Arrays.asList("가","나","다"));

		int nChoice = 0;
		for (Entry<String, Object> e : hData.entrySet()) {
			
			switch (e.getKey()) {
				//탐지로그 발생 추이
				case "opt01" : case "opt02" :
					if (nC1 == 0) { 
						reportData.put("C1", push(contentsList, "  ", ItemNo, ++nC1, " 탐지 추이"));
					}
					break;
				//이벤트 현황
				case "opt03" :
					if (nC2 == 0) {
						reportData.put("C2", push(contentsList, "  ", ItemNo, (++nC2 + nC1), " 탐지 현황"));
					}
					break;
				//출발지IP 현황
				case "opt04" :
					if (nC3 == 0) {
						reportData.put("C3", push(contentsList, "  ", ItemNo, (++nC3 + nC2 + nC1), " 출발지 현황"));
					}
					break;
				//도메인별 상세통계
				case "opt05" : case "opt06" : case "opt07" :
					if (nC4 == 0) {
						reportData.put("C4", push(contentsList, "  ", ItemNo, (++nC4 + nC3 + nC2 + nC1), " 목적지 현황"));
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
					All_DetectLog_Trend(reportData, assetCode, sStartDay, sEndDay);
					break;
				case "opt02" : 	//전체 탐지로그 & 도메인 TOP10 발생추이
					All_Domain_TopN_Trend(reportData, assetCode, sStartDay, sEndDay);
					break;
				case "opt03" : 	//전체 탐지로그 & Event TOP
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd3")));
					ALL_Event_TopN(reportData, assetCode, sStartDay, sEndDay, nChoice);
					break;
				case "opt04" :	//전체 탐지로그 & SIP TOP
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd4")));
					ALL_SrcIp_TopN(reportData, assetCode, sStartDay, sEndDay, nChoice, !StringUtil.isEmpty(hData.get("ck4")));
					break;
				case "opt05" : 	//도메인 별 탐지로그 & 탐지로그 발생추이
					reportData.put("opt05", saSubNo.remove(0));
					Domain_TopN_Trend(reportData, assetCode, sStartDay, sEndDay);
					break;
				case "opt06" : 	//도메인 별 탐지로그 & EVT TOP10 발생추이
					reportData.put("opt06", saSubNo.remove(0));
					Domain_EventTopN_Trend(reportData, assetCode, sStartDay, sEndDay);
					break;
				case "opt07" : 	//도메인 별 탐지로그 & EVT TOP10 통계
					reportData.put("opt07", saSubNo.remove(0));
					Domain_EventTopN_Condition(reportData, assetCode, sStartDay, sEndDay);
					break;
				case "opt99" :	//성능정보
					PerformanceInfo(wafDao, reportData, contentsList, ItemNo, nC5 + nC4 + nC3 + nC2 + nC1, assetCode, sStartDay, sEndDay);
					break;
			}
		}
		return reportData;
	}

	private void All_DetectLog_Trend(HashMap<String, Object> reportData, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();

		List<String> dayPeriod = wafDao.getPeriodDay(sStartDay, sEndDay, 1);
		
		Iterable<DBObject> dbResult = wafDao.DetectLogTrend("DY", "", assetCode, sStartDay, sEndDay);
		
		HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
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
    		map.put("series", "전체");
    		map.put("category", tDay.substring(6, 8));
    		map.put("value", val != null ? ((Number)val.get("count")).longValue() : 0);
    		
    		dataSource1.add(map);
    		logger.debug(map.toString());
		}
		
		dbResult = wafDao.DetectLogTrend("DY", ALLOW, assetCode, sStartDay, sEndDay);

    	mapResult = new HashMap<String, DBObject>(); 
    	for (DBObject val : dbResult) {
    		String sAction = (String)val.get("action");
    		String sYear = ((Integer)val.get("year")).toString();
    		String sMonth = ((Integer)val.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    		String sDay = ((Integer)val.get("day")).toString();
    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
    		mapResult.put(sAction + sYear + sMonth + sDay, val);
    	}
    	
    	String[] saGubun = {"차단", "허용"};
    	for (int action = 1; action >= 0; action--) {
    		for (String tDay : dayPeriod) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		DBObject val = mapResult.get(String.valueOf(action) + tDay);
	    		map.put("series", saGubun[action]);
	    		map.put("category", tDay.substring(6, 8));
	    		map.put("value", val != null ? ((Number)val.get("count")).longValue() : 0);

	    		dataSource1.add(map);
	    		logger.debug(map.toString());
    		}	    		
    	}
    	
    	reportData.put("OPT1_UNIT", "일");
		reportData.put("OPT1_1", new SynthesisDataSource(dataSource1));
		
	}

	private void All_Domain_TopN_Trend(HashMap<String, Object> reportData, int assetCode, String sStartDay, String sEndDay) throws Exception {

		List<HashMap<String, Object>>  dataSource = new ArrayList<HashMap<String, Object>>();
		
		Iterable<DBObject> dbTopN = wafDao.DomainTopN("DY", assetCode,sStartDay, sEndDay, 10);
    	List<String> saTopN = new ArrayList<String>();
    	for (DBObject val : dbTopN) {
    		saTopN.add((String)val.get("host"));
    	}
		
    	Iterable<DBObject> dbResult = wafDao.DomainTopNTrend("DY", "", assetCode, sStartDay, sEndDay, saTopN);
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
    	
    	List<String> dayPeriod = wafDao.getPeriodDay(sStartDay, sEndDay, 1);
    	
    	for (String sHost : saTopN) {
    		for (String tDay : dayPeriod) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		DBObject val = mapResult.get(String.valueOf(sHost) + "-" + tDay);
	    		map.put("series", sHost);
	    		map.put("category", tDay.substring(6, 8));
	    		map.put("value", val != null ? ((Number)val.get("count")).longValue() : 0);

	    		dataSource.add(map);
	    		logger.debug(map.toString());
    		}	    		
    	}	    	
		
    	reportData.put("OPT2_UNIT", "일");
		reportData.put("OPT2_1", new SynthesisDataSource(dataSource));
	}

	//도메인 별 탐지로그 & EVT TOP10 통계
	private void Domain_EventTopN_Condition(HashMap<String, Object> reportData, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		List<HashMap<String, Object>> hostSource = new ArrayList<HashMap<String, Object>>();
		
		HashMap<String, Object> chartMap = new HashMap<String, Object>();
		HashMap<String, Object> gridMap = new HashMap<String, Object>();
		
		Iterable<DBObject> dbTopN = wafDao.DomainTopN("DY", assetCode, sStartDay, sEndDay, 10);

		int nNo2 = 1;
    	for (DBObject mVal : dbTopN) {
    		String sHost = (String)mVal.get("host");
    		logger.debug("도메인 : " + sHost);

    		HashMap<String, Object> mHost = new HashMap<String, Object>();
    		mHost.put("no", nNo2++);
    		mHost.put("host", sHost);
    		mHost.put("count", ((Number)mVal.get("count")).longValue());
    		hostSource.add(mHost);
    		
    		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();
    		List<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();
    		
    		Iterable<DBObject> dbResult = wafDao.DomainEventTopN("DY", assetCode, sStartDay, sEndDay, sHost, 0);
    		int nNo = 1;
    		long lEtcCount = 0;
    		long lEventTotal = 0;
    		for (DBObject val : dbResult) {
    			String sMessage = (String)val.get("message");
    			
    			lEventTotal = lEventTotal + ((Number)val.get("count")).longValue();
    			
        		HashMap<String, Object> m_Map = new HashMap<String, Object>();
        		if (!sMessage.equals("-1") && nNo <= 10) {
	        		m_Map.put("message", sMessage);
	        		m_Map.put("count", ((Number)val.get("count")).longValue());

	        		dataSource1.add(m_Map);
	        		logger.debug(m_Map.toString());
	        		
	        		lEtcCount += ((Number)val.get("count")).longValue();
        		} else {
        			continue;
        		}
        		
        		Iterable<DBObject> srcIpTop5 = wafDao.DomainEventSrcIpCondition("DY", assetCode, sStartDay, sEndDay, sHost, sMessage, 5);
        		for (DBObject sVal : srcIpTop5) {
            		m_Map = new HashMap<String, Object>();
            		m_Map.put("no", nNo);
            		m_Map.put("message", sMessage);
            		m_Map.put("count", ((Number)val.get("count")).longValue());
            		m_Map.put("srcIp", StringUtil.convertString(sVal.get("srcIp")));
            		m_Map.put("action", StringUtil.convertString(sVal.get("action")).equals("1") ? "허용" : "차단" );
            		m_Map.put("ratio", ((Number)sVal.get("count")).longValue()*100f/((Number)val.get("count")).longValue());
            		
	        		dataSource2.add(m_Map);
	        		logger.debug(m_Map.toString());
        		}
        		nNo++;
    		}
    		
    		lEtcCount = lEventTotal - lEtcCount;
    		
    		if (lEtcCount > 0) {
	    		HashMap<String, Object> m_Map = new HashMap<String, Object>();
	    		m_Map.put("message", "기타");
        		m_Map.put("count", lEtcCount);
	    		dataSource1.add(m_Map);
    		}

    		for (HashMap<String, Object> val : dataSource1) {
    			val.put("ratio", ((Number)val.get("count")).longValue()*100f/lEventTotal);
    		}
    		
    		chartMap.put(sHost, new SynthesisDataSource(dataSource1));
    		gridMap.put(sHost, new SynthesisDataSource(dataSource2));
    	}

    	reportData.put("OPT7_0", new SynthesisDataSource(hostSource));
    	reportData.put("OPT7_1", chartMap);
    	reportData.put("OPT7_2", gridMap);
	}
	
	//도메인 별 탐지로그 & EVT TOP10 발생추이
	private void Domain_EventTopN_Trend(HashMap<String, Object> reportData, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> hostSource = new ArrayList<HashMap<String, Object>>();
		
		HashMap<String, Object> chartMap = new HashMap<String, Object>();
		
		Iterable<DBObject> dbTopN = wafDao.DomainTopN("DY", assetCode, sStartDay, sEndDay, 10);
		
		int nNo = 1;
    	for (DBObject mVal : dbTopN) {
    		String sHost = (String)mVal.get("host");
    		logger.debug("도메인 : " + sHost);
    	
    		HashMap<String, Object> mHost = new HashMap<String, Object>();
    		mHost.put("no", nNo++);
    		mHost.put("host", sHost);
    		mHost.put("count", ((Number)mVal.get("count")).longValue());
    		hostSource.add(mHost);
    		
    		Iterable<DBObject> dbResult = wafDao.DomainEventTopN("DY", assetCode, sStartDay, sEndDay, sHost, 10);
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
		    		HashMap<String, Object> map = new HashMap<String, Object>();
		    		DBObject val = mapResult.get(String.valueOf(sMessage) + "-" + tDay);
		    		map.put("series", sMessage);
		    		map.put("category", tDay.substring(6, 8));
		    		map.put("value", val != null ? ((Number)val.get("count")).longValue() : 0);

    	    		dataSource.add(map);
    	    		logger.debug(map.toString());
	    		}	    		
	    	}	    	
    	
	    	chartMap.put(sHost, new SynthesisDataSource(dataSource));
    	}
			
    	reportData.put("OPT6_UNIT", "일");
    	reportData.put("OPT6_0", new SynthesisDataSource(hostSource));
		reportData.put("OPT6_1", chartMap);
	}

	private void Domain_TopN_Trend(HashMap<String, Object> reportData, int assetCode, String sStartDay, String sEndDay) throws Exception {

		List<HashMap<String, Object>>  dataSource = new ArrayList<HashMap<String, Object>>();
		
		Iterable<DBObject> dbTopN = wafDao.DomainTopN("DY", assetCode, sStartDay, sEndDay, 10);
		
		int nNo = 1;
    	List<String> saTopN = new ArrayList<String>();
    	for (DBObject val : dbTopN) {
    		saTopN.add((String)val.get("host"));
    		
    		HashMap<String, Object> mHost = new HashMap<String, Object>();
    		mHost.put("no", nNo++);
    		mHost.put("host", (String)val.get("host"));
    		mHost.put("count", ((Number)val.get("count")).longValue());
    		dataSource.add(mHost);
    		logger.debug(mHost.toString());
    	}
    	reportData.put("OPT5_0", new SynthesisDataSource(dataSource));
    	
		HashMap<String, Object> chartMap = new HashMap<String, Object>();
    	
		for (DBObject mVal : dbTopN) {
    		String sHost = (String)mVal.get("host");
    		logger.debug("도메인 : " + sHost);
    		
    		dataSource = new ArrayList<HashMap<String, Object>>();
    		
    		List<String> dayPeriod = wafDao.getPeriodDay(sStartDay, sEndDay, 1);
    		
    		Iterable<DBObject> dbResult = wafDao.DomainTopNTrend("DY", "", assetCode, sStartDay, sEndDay, sHost);
    		
    		HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
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
	    		map.put("series", "전체");
	    		map.put("category", tDay.substring(6, 8));
	    		map.put("value", val != null ? ((Number)val.get("count")).longValue() : 0);

	    		dataSource.add(map);
	    		logger.debug(map.toString());
    		}	    		
    		
    		dbResult = wafDao.DomainTopNTrend("DY", ALLOW, assetCode, sStartDay, sEndDay, sHost);

	    	mapResult = new HashMap<String, DBObject>(); 
	    	for (DBObject val : dbResult) {
	    		String sAction = (String)val.get("action");
	    		String sYear = ((Integer)val.get("year")).toString();
	    		String sMonth = ((Integer)val.get("month")).toString();
	    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
	    		String sDay = ((Integer)val.get("day")).toString();
	    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
	    		mapResult.put(sAction + sYear + sMonth + sDay, val);
	    	}
			
	    	String[] saGubun = {"차단", "탐지"};
    		for (int action = 1; action >= 0; action--) { //허용/차단
	    		for (String tDay : dayPeriod) {
    	    		HashMap<String, Object> map = new HashMap<String, Object>();
		    		DBObject val = mapResult.get(String.valueOf(action) + tDay);
		    		map.put("series", saGubun[action]);
		    		map.put("category", tDay.substring(6, 8));
		    		map.put("value", val != null ? ((Number)val.get("count")).longValue() : 0);

    	    		dataSource.add(map);
    	    		logger.debug(map.toString());
	    		}	    		
    		}
    		
	    	chartMap.put(sHost, new SynthesisDataSource(dataSource));
		}
		
		reportData.put("OPT5_UNIT", "일");
		reportData.put("OPT5_1", chartMap);
	}
	
	//전체 탐지로그 & Event TOP
	private void ALL_Event_TopN(HashMap<String, Object> reportData, int assetCode, String sStartDay, String sEndDay, int nLimit) throws Exception {
		
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();
		
		Iterable<DBObject> dbResult = wafDao.EventTopN("DY", assetCode, sStartDay, sEndDay, 0);

		long lTotal = 0;
    	for (DBObject val : dbResult) {
    		String sMessage = (String)val.get("message");
    		val.put("allow", 0);
    		val.put("cutoff", 0);
    		
    		lTotal += ((Number)val.get("count")).longValue();
    		
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
    	long lCount = 0, lAllow = 0, lCutoff = 0;
    	for (DBObject val : dbResult) {
    		String sMessage = (String)val.get("message");
    		if (!sMessage.equals("-1") && nNo <= 10) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		map.put("message", sMessage);
	    		map.put("count", ((Number)val.get("count")).longValue());
	    		map.put("ratio", (((Number)val.get("count")).longValue()*100f)/lTotal);
	    		map.put("allow", (((Number)val.get("allow")).longValue()*100f)/((Number)val.get("count")).longValue());
	    		map.put("cutoff", (((Number)val.get("cutoff")).longValue()*100f)/((Number)val.get("count")).longValue());

	    		dataSource1.add(map);
	    		logger.debug(map.toString());
    		} else {
    			lCount += ((Number)val.get("count")).longValue();
    			lAllow += ((Number)val.get("allow")).longValue();
    			lCutoff += ((Number)val.get("cutoff")).longValue();
    		}
    		nNo++;
    	}

    	if (lCount > 0) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("message", "기타");
			map.put("count", lCount);
			map.put("ratio", (lCount*100f)/lTotal);
			map.put("allow", (lAllow*100f)/lCount);
			map.put("cutoff", (lCutoff*100f)/lCount);
	
			dataSource1.add(map);
			logger.debug(map.toString());
    	}
    	
		reportData.put("OPT3_1", new SynthesisDataSource(dataSource1));
    	
		//표 [순위 | 이벤트명 | 탐지건수 | 도메인 | Action | 비율(%)]
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
        		map.put("count", ((Number)hMap.get("count")).longValue());
        		map.put("host", (String)pMap.get("host"));
        		map.put("action", ((String)pMap.get("action")).equals("1") ? "허용" : "차단");
        		map.put("ratio", ((Number)pMap.get("count")).longValue()*100f/((Number)hMap.get("count")).longValue());
        		
        		dataSource2.add(map);
    			logger.debug(map.toString());
    		}
    		nNo++;
    	}
		
    	reportData.put("OPT3_2", new SynthesisDataSource(dataSource2));
	}

	//전체 탐지로그 & SIP TOP
	private void ALL_SrcIp_TopN(HashMap<String, Object> reportData, int assetCode, String sStartDay, String sEndDay, int nLimit, boolean bChk) throws Exception {

		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();
		
		//표 [순위 | SRC IP | 국가정보 | 전체건수 | 이벤트명 | 도메인 | Action | 비율(%)]
		Iterable<DBObject> dbTopN = wafDao.SrcIpTopN("DY", assetCode, sStartDay, sEndDay, nLimit, null);
		
    	List<String> saTopN = new ArrayList<String>();
    	for (DBObject val : dbTopN) {
    		saTopN.add((String)val.get("srcIp"));
    	}

    	int nNo = 1;
		for (DBObject val : dbTopN) {
			String sSrcIp = (String)val.get("srcIp");
			
			Iterable<DBObject> dbTop5 = wafDao.EventHostCondition("DY", assetCode, sStartDay, sEndDay, sSrcIp);
			
			for (DBObject hVal : dbTop5) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("no", nNo);
				map.put("srcIp", sSrcIp);
				map.put("geoCode", Constants.getCountryCode(sSrcIp));
				map.put("count", ((Number)val.get("count")).longValue());
				map.put("message", StringUtil.convertString(hVal.get("message")));
				map.put("host", StringUtil.convertString(hVal.get("host")));
				map.put("action", ((String)hVal.get("action")).equals("1") ? "허용" : "차단");
				map.put("ratio", ((Number)hVal.get("count")).longValue()*100f/((Number)val.get("count")).longValue());
				
        		dataSource.add(map);
    			logger.debug(map.toString());
			}
			nNo++;
		}
    	
		reportData.put("OPT4_1", new SynthesisDataSource(dataSource));
		
		if (bChk) {
			
			dataSource = new ArrayList<HashMap<String, Object>>();
			
	    	if (saTopN.size() > 10) {
	    		saTopN = saTopN.subList(0, 10);
	    	}
			
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
		    		HashMap<String, Object> map = new HashMap<String, Object>();
		    		DBObject val = mapResult.get(String.valueOf(sSrcIp) + "-" + tDay);
		    		map.put("series", sSrcIp);
		    		map.put("category", tDay.substring(6, 8));
		    		map.put("value", val != null ? ((Number)val.get("count")).longValue() : 0);

		    		dataSource.add(map);
		    		logger.debug(map.toString());
	    		}	    		
	    	}	    	

	    	reportData.put("OPT4_UNIT", "일");
			reportData.put("OPT4_2", new SynthesisDataSource(dataSource));
		}
		
	}
	
	public String push(List<String> contentsList, String sIdt, int n1, int n2, String msg) {
		String sTitle = sIdt + n1 + "." + n2 + msg;    
		contentsList.add(sTitle);
		HashMap<String, Object> pMap = new HashMap<String, Object>();
    	pMap.put("message", sTitle);
		try {
			ObjectMapper mapper = new ObjectMapper();
			userInfo.getWsSession().sendMessage(new TextMessage(mapper.writeValueAsString(pMap)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sTitle;
	}
	
}

