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
import com.cyberone.report.core.dao.IdsDao;
import com.cyberone.report.core.datasource.SynthesisDataSource;
import com.cyberone.report.model.UserInfo;
import com.cyberone.report.utils.StringUtil;
import com.mongodb.DBObject;

public class IdsPeriodReport extends BaseReport {
	
	private IdsDao idsDao;

	private Logger logger = LoggerFactory.getLogger(getClass());

	private UserInfo userInfo;
	
	public IdsPeriodReport(UserInfo userInfo) {
		this.userInfo = userInfo;
		this.idsDao = new IdsDao(userInfo.getPromDb(), userInfo.getReportDb());
	}
	
	/* 
	 * 침입탐지(IDS) 임의기간보고서 통계데이타 생성
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getDataSource(String sReportType, String sSearchDate, HashMap<String, Object> hMap, int ItemNo, List<String> contentsList) throws Exception {
		logger.debug("침입탐지(IDS) 임의기간보고서 통계데이타 생성");

		HashMap<String, Object> reportData = new HashMap<String, Object>();
		
		reportData.put("reportType", sReportType);
		
		contentsList.add(ItemNo + ". " + (String)hMap.get("assetName") + " 장비의 IDS 탐지로그 분석");
		reportData.put("RT", ItemNo + ". " + (String)hMap.get("assetName") + " 장비의 IDS 탐지로그 분석");
		
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
			DBObject dbObj = idsDao.selectAutoReportForm(Integer.parseInt(sReportType), assetCode);
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

		int nC1 = 0, nC2 = 0, nC3 = 0, nC4 = 0, nC5 = 0, nC6 = 0;
		int nS1 = 1, nS2 = 1, nS3 = 1, nS4 = 1, nS5 = 1, nS6 = 1;
		
		int nChoice = 0;
		for (Entry<String, Object> e : hData.entrySet()) {
			
			switch (e.getKey()) {
				//탐지로그 발생 추이
				case "opt01" : case "opt02" : case "opt03" :  
					if (nC1 == 0) { 
						contentsList.add("  " + ItemNo + "." + ++nC1 + " 탐지 추이");
						reportData.put("C1", "  " + ItemNo + "." + nC1 + " 탐지 추이");
					}
					break;
				//이벤트 현황
				case "opt04" : case "opt05" :   
					if (nC2 == 0) { 
						contentsList.add("  " + ItemNo + "." + (++nC2 + nC1) + " 탐지 현황");
						reportData.put("C2", "  " + ItemNo + "." + (nC2 + nC1) + " 탐지 현황");
					}
					break;
				//출발지IP 현황
				case "opt06" : case "opt07" :   
					if (nC3 == 0) { 
						contentsList.add("  " + ItemNo + "." + (++nC3 + nC2 + nC1) + " 출발지 현황");
						reportData.put("C3", "  " + ItemNo + "." + (nC3 + nC2 + nC1) + " 출발지 현황");
					}
					break;
				//목적지IP 현황
				case "opt08" : case "opt09" :   
					if (nC4 == 0) { 
						contentsList.add("  " + ItemNo + "." + (++nC4 + nC3 + nC2 + nC1) + " 목적지 현황");
						reportData.put("C4", "  " + ItemNo + "." + (nC4 + nC3 + nC2 + nC1) + " 목적지 현황");
					}
					break;
				//서비스 현황
				case "opt10" : case "opt11" :   
					if (nC5 == 0) { 
						contentsList.add("  " + ItemNo + "." + (++nC5 + nC4 + nC3 + nC2 + nC1) + " 목적지 서비스 현황");
						reportData.put("C5", "  " + ItemNo + "." + (nC5 + nC4 + nC3 + nC2 + nC1) + " 목적지 서비스 현황");
					}
					break;
				//성능정보
				case "opt99" :
					if (nC6 == 0) { 
						contentsList.add("  " + ItemNo + "." + (++nC6 + nC5 + nC4 + nC3 + nC2 + nC1) + " 성능현황");
						reportData.put("C6", "  " + ItemNo + "." + (nC6 + nC5 + nC4 + nC3 + nC2 + nC1) + " 성능현황");
					}
					break;
			}
			
			switch (e.getKey()) {
				case "opt01" :	//전체 탐지로그 발생추이
					reportData.put("opt01", push(contentsList, "    ", ItemNo, nC1, nS1++, " 전체 탐지로그 발생추이"));
					All_DetectLog_Trend(reportData, -1, assetCode, sStartDay, sEndDay);
					break;
				case "opt02" :	//외부에서 내부로의 전체 탐지로그 발생추이 
					reportData.put("opt02", push(contentsList, "    ", ItemNo, nC1, nS1++, " 외부에서 내부로의 전체 탐지로그 발생추이"));
					Direction_DetectLog_Trend(reportData, INBOUND, assetCode, sStartDay, sEndDay);
					break;
				case "opt03" : 	//내부에서 외부로의 전체 탐지로그 발생추이
					reportData.put("opt03", push(contentsList, "    ", ItemNo, nC1, nS1++, " 내부에서 외부로의 전체 탐지로그 발생추이"));
					Direction_DetectLog_Trend(reportData, OUTBOUND, assetCode, sStartDay, sEndDay);
					break;
				case "opt04" : 	//외부에서 내부로의 전체 탐지로그 & 이벤트 TOP
					reportData.put("opt04", push(contentsList, "    ", ItemNo, nC2 + nC1, nS2++, " 외부에서 내부로의 전체 탐지로그에 대한 탐지룰 TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd4")));
					Direction_EventTopN(reportData, INBOUND, assetCode, sStartDay, sEndDay, nChoice, !StringUtil.isEmpty(hData.get("ck4")));
					break;
				case "opt05" : 	//내부에서 외부로의 전체 탐지로그 & 이벤트 TOP
					reportData.put("opt05", push(contentsList, "    ", ItemNo, nC2 + nC1, nS2++, " 내부에서 외부로의 전체 탐지로그에 대한 탐지룰 TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd5")));
					Direction_EventTopN(reportData, OUTBOUND, assetCode, sStartDay, sEndDay, nChoice, !StringUtil.isEmpty(hData.get("ck5")));
					break;
				case "opt06" : 	//외부에서 내부로의 전체 탐지로그 & SIP TOP
					reportData.put("opt06", push(contentsList, "    ", ItemNo, nC3 + nC2 + nC1, nS3++, " 외부에서 내부로의 전체 탐지로그에 대한 출발지IP TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd6")));
					ALL_DetectLog_TopN(reportData, INBOUND, assetCode, sStartDay, sEndDay, true, nChoice, !StringUtil.isEmpty(hData.get("ck6")));
					break;
				case "opt07" : 	//내부에서 외부로의 전체 탐지로그 & SIP TOP
					reportData.put("opt07", push(contentsList, "    ", ItemNo, nC3 + nC2 + nC1, nS3++, " 내부에서 외부로의 전체 탐지로그에 대한 출발지IP TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd7")));
					ALL_DetectLog_TopN(reportData, OUTBOUND, assetCode, sStartDay, sEndDay, true, nChoice, !StringUtil.isEmpty(hData.get("ck7")));
					break;
				case "opt08" : 	//외부에서 내부로의 전체 탐지로그 & DIP TOP
					reportData.put("opt08", push(contentsList, "    ", ItemNo, nC4 + nC3 + nC2 + nC1, nS4++, " 외부에서 내부로의 전체 탐지로그에 대한 목적지IP TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd8")));
					ALL_DetectLog_TopN(reportData, INBOUND, assetCode, sStartDay, sEndDay, false, nChoice, false);
					break;
				case "opt09" : 	//내부에서 외부로의 전체 탐지로그 & DIP TOP
					reportData.put("opt09", push(contentsList, "    ", ItemNo, nC4 + nC3 + nC2 + nC1, nS4++, " 내부에서 외부로의 전체 탐지로그에 대한 목적지IP TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd9")));
					ALL_DetectLog_TopN(reportData, OUTBOUND, assetCode, sStartDay, sEndDay, false, nChoice, false);
					break;
				case "opt10" : 	//외부에서 내부로의 전체 탐지로그 & 서비스 TOP10
					reportData.put("opt10", push(contentsList, "    ", ItemNo, nC5 + nC4 + nC3 + nC2 + nC1, nS5++, " 외부에서 내부로의 전체 탐지로그에 대한 서비스 TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd10")));
					ALL_ServiceTop10(reportData, INBOUND, assetCode, sStartDay, sEndDay, !StringUtil.isEmpty(hData.get("ck10")));
					break;
				case "opt11" : 	//내부에서 외부로의 전체 탐지로그 & 서비스 TOP10
					reportData.put("opt11", push(contentsList, "    ", ItemNo, nC5 + nC4 + nC3 + nC2 + nC1, nS5++, " 내부에서 외부로의 전체 탐지로그에 대한 서비스 TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd11")));
					ALL_ServiceTop10(reportData, OUTBOUND, assetCode, sStartDay, sEndDay, !StringUtil.isEmpty(hData.get("ck11")));
					break;
				case "opt99" :	//성능정보
					PerformanceInfo(idsDao, reportData, contentsList, ItemNo, nC6 + nC5 + nC4 + nC3 + nC2 + nC1, assetCode, sStartDay, sEndDay);					
					break;
			}
		}
		return reportData;
	}

	private void All_DetectLog_Trend(HashMap<String, Object> reportData, int nDirection, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();

		List<String> dayPeriod = idsDao.getPeriodDay(sStartDay, sEndDay, 1);
		
		Iterable<DBObject> dbResult = idsDao.AllDetectLogTrend("DY", -1, assetCode, sStartDay, sEndDay);
		
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
		
		dbResult = idsDao.AllDetectLogTrend("DY", 1, assetCode, sStartDay, sEndDay);

    	mapResult = new HashMap<String, DBObject>(); 
    	for (DBObject val : dbResult) {
    		String sDirection = (String)val.get("direction");
    		String sYear = ((Integer)val.get("year")).toString();
    		String sMonth = ((Integer)val.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    		String sDay = ((Integer)val.get("day")).toString();
    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
    		mapResult.put(sDirection + sYear + sMonth + sDay, val);
    	}
    	
    	String[] saGubun = {"내부 → 외부 로그", "외부 → 내부 로그"};
    	for (int direction = 1; direction >= 0; direction--) { //아웃바운드/인바운드
    		for (String tDay : dayPeriod) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		DBObject val = mapResult.get(String.valueOf(direction) + tDay);
	    		map.put("series", saGubun[direction]);
	    		map.put("category", tDay.substring(6, 8));
	    		map.put("value", val != null ? ((Number)val.get("count")).longValue() : 0);

	    		dataSource.add(map);
	    		logger.debug(map.toString());
    		}	    		
    	}
    	
    	reportData.put("OPT1_0", "일");
		reportData.put("OPT1_1", new SynthesisDataSource(dataSource));
	}
	
	private void Direction_DetectLog_Trend(HashMap<String, Object> reportData, int nDirection, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();
		
    	Iterable<DBObject> dbResult = idsDao.AllActionTrend("DY", nDirection, assetCode, sStartDay, sEndDay);
    	
    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
    	for (DBObject val : dbResult) {
    		String sYear = ((Integer)val.get("year")).toString();
    		String sMonth = ((Integer)val.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    		String sDay = ((Integer)val.get("day")).toString();
    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
    		mapResult.put(sYear + sMonth + sDay, val);
    	}

    	List<String> dayPeriod = idsDao.getPeriodDay(sStartDay, sEndDay, 1);
    	
		for (String tDay : dayPeriod) {
    		HashMap<String, Object> map = new HashMap<String, Object>();
    		DBObject val = mapResult.get(tDay);
    		map.put("series", "전체");
    		map.put("category", tDay.substring(6, 8));
    		map.put("value", val != null ? ((Number)val.get("count")).longValue() : 0);
    		
    		dataSource.add(map);
    		logger.debug(map.toString());
		}
		
		if (nDirection == 1) {	//INBOUND
			reportData.put("OPT2_0", "일");
			reportData.put("OPT2_1", new SynthesisDataSource(dataSource));
		} else {
			reportData.put("OPT3_0", "일");
			reportData.put("OPT3_1", new SynthesisDataSource(dataSource));
		}
	}
	
	//외부에서 내부로의 전체 탐지로그 & 이벤트 TOP
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
	    		map.put("count", ((Number)val.get("count")).longValue());
	    		map.put("ratio", (((Number)val.get("count")).longValue()*100f)/lTotal);

	    		dataSource1.add(map);
	    		logger.debug(map.toString());
	    		
	    		saTopN.add(sMessage);
    		}
    		if (nNo >= 10) break;
    		nNo++;
    	}
    	
    	nNo = 1;
    	for (DBObject val : dbResult) {
    		String sMessage = (String)val.get("message");
    		
    		Iterable<DBObject> srcIpTop5 = idsDao.EventSrcIpTopN("DY", nDirection, assetCode, sStartDay, sEndDay, sMessage, 5);
    		for (DBObject sVal : srcIpTop5) {
    			HashMap<String, Object> map = new HashMap<String, Object>();
    			map.put("no", nNo);
    			map.put("message", sMessage);
    			map.put("count", ((Number)val.get("count")).longValue());    			
    			map.put("srcIp", StringUtil.convertString(sVal.get("srcIp")));
    			if (nDirection == INBOUND) {
    				map.put("geoCode", Constants.getCountryCode(StringUtil.convertString(sVal.get("srcIp"))));
    			} else {
    				map.put("geoCode", Constants.getCountryCode(StringUtil.convertString(sVal.get("destIp"))));
    			}
    			map.put("destIp", StringUtil.convertString(sVal.get("destIp")));
    			map.put("ratio", (((Number)sVal.get("count")).longValue()*100f)/((Number)val.get("count")).longValue());
        		
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
    		
	    	dbResult = idsDao.EventTopNTrend("DY", nDirection, assetCode, sStartDay, sEndDay, saTopN);
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

	    	List<String> dayPeriod = idsDao.getPeriodDay(sStartDay, sEndDay, 1);
	    	
	    	for (String sMessage : saTopN) {
	    		for (String tDay : dayPeriod) {
		    		DBObject val = mapResult.get(sMessage + "-" + tDay);
		    		HashMap<String, Object> map = new HashMap<String, Object>();
		    		map.put("series", sMessage);
		    		map.put("category", tDay.substring(6, 8));
		    		map.put("value", val != null ? ((Number)val.get("count")).longValue() : 0);
		    		
		    		dataSource3.add(map);
		    		logger.debug(map.toString());
		    	}
	    	}
    		
	    	if (nDirection == INBOUND) {
	    		reportData.put("OPT4_0", "일");
	    		reportData.put("OPT4_3", new SynthesisDataSource(dataSource3));
	    	} else {
	    		reportData.put("OPT5_0", "일");
		    	reportData.put("OPT5_3", new SynthesisDataSource(dataSource3));
	    	}
    	}
    	
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
					map.put("srcIp", (String)val.get("srcIp"));
					map.put("count", ((Number)val.get("count")).longValue());
					map.put("message", (String)dbObj.get("message"));
					map.put("destIp", (String)dbObj.get("destIp"));
					map.put("ratio", (((Number)dbObj.get("count")).longValue()*100f)/((Number)val.get("count")).longValue());
				} else {
					map.put("destIp", (String)val.get("destIp"));
					map.put("count", ((Number)val.get("count")).longValue());
		    		map.put("message", (String)dbObj.get("message"));
					map.put("srcIp", (String)dbObj.get("srcIp"));
					map.put("ratio", (((Number)dbObj.get("count")).longValue()*100f)/((Number)val.get("count")).longValue());
				}
				if (nDirection == INBOUND) {
					map.put("geoCode", Constants.getCountryCode((String)dbObj.get("srcIp")));
				} else {
					map.put("geoCode", Constants.getCountryCode((String)dbObj.get("destIp")));
				}
				
				dataSource1.add(map);
				logger.debug(map.toString());
			}
			nNo++;
    	}
    	
    	if (bSrcIp) {
	    	if (nDirection == INBOUND) { //외부에서 내부로의 전체 세션로그 & SIP TOP
	    		reportData.put("OPT9_1", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == OUTBOUND) { //내부에서 외부로의 전체 세션로그 & SIP TOP
	    		reportData.put("OPT10_1", new SynthesisDataSource(dataSource1));
	    	}
	    	
	    	if (topNIps.size() > 10) {
	    		topNIps = topNIps.subList(0, 10);
	    	}
	    	
	    	if (bChk) { //SRC IP TOP10 세션 로그 발생추이 
	    		ArrayList<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();
	        	Iterable<DBObject> ipTopNTrend = idsDao.IpTopNTrend("DY", nDirection, assetCode, sStartDay, sEndDay, topNIps, true);
	        	
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
	        	
	        	List<String> dayPeriod = idsDao.getPeriodDay(sStartDay, sEndDay, 1);
	        	
	        	for (String strIp : topNIps) {
	        		for (String tDay : dayPeriod) {
    		    		DBObject val = mapResult.get(strIp + "-" + tDay);
    		    		HashMap<String, Object> map = new HashMap<String, Object>();
    		    		map.put("series", strIp);
    		    		map.put("category", tDay.substring(6, 8));
    		    		map.put("value", val != null ? ((Number)val.get("count")).longValue() : 0);
    		    		dataSource2.add(map);
    					logger.debug(map.toString());
	        		}
	        	}
	    		
	        	if (nDirection == INBOUND) {
	        		reportData.put("OPT9_0", "일");
		    		reportData.put("OPT9_2", new SynthesisDataSource(dataSource2));
		    	} else if (nDirection == OUTBOUND) {
		    		reportData.put("OPT10_0", "일");
		    		reportData.put("OPT10_2", new SynthesisDataSource(dataSource2));
		    	}
	    	}
    	} else {
	    	if (nDirection == INBOUND) { //외부에서 내부로의 전체 탐지로그 & DIP TOP
	    		reportData.put("OPT11_1", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == OUTBOUND) { //내부에서 외부로의 전체 탐지로그 & DIP TOP
	    		reportData.put("OPT12_1", new SynthesisDataSource(dataSource1));
	    	}
    	}
	}
	
	private void ALL_ServiceTop10(HashMap<String, Object> reportData, int nDirection, int assetCode, String sStartDay, String sEndDay, boolean bChk) throws Exception {
		
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();

    	Iterable<DBObject> dbResult = idsDao.ServiceDetectLog("DY", nDirection, assetCode, sStartDay, sEndDay, 0);
    	
    	long lTotal = 0;
		for (DBObject val : dbResult) {
			lTotal += ((Number)val.get("count")).longValue();
		}

    	int nNo = 1;
    	long lCount = 0;
    	for (DBObject val : dbResult) {
    		int nPort = (Integer)val.get("destPort");
    		if (nPort != -1 && nNo <= 10) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
        		map.put("port", nPort);
        		map.put("svcName", idsDao.getPortServiceName(nPort));
        		map.put("count", ((Number)val.get("count")).longValue());
        		map.put("ratio", (((Number)val.get("count")).longValue()*100f)/lTotal);

	    		dataSource1.add(map);
	    		logger.debug(map.toString());
    		} else {
    			lCount += ((Number)val.get("count")).longValue();
    		}
    		nNo++;
    	}
    	
    	if (lCount > 0) {
			HashMap<String, Object> map = new HashMap<String, Object>();
    		map.put("port", -1);
    		map.put("svcName", "기타");
			map.put("count", lCount);
			map.put("ratio", (lCount*100f)/lTotal);
	
			dataSource1.add(map);
			logger.debug(map.toString());
    	}
		
    	if (nDirection == INBOUND) {
    		reportData.put("OPT13_1", new SynthesisDataSource(dataSource1));
    	} else {
    		reportData.put("OPT14_1", new SynthesisDataSource(dataSource1));
    	}
    	
    	if (bChk) { //서비스 TOP10 증감현황 및 이벤트유형
    	
    		//표 [순위 | Port | 서비스명 | 전체건수 | 이벤트명 | 비율(%)]
        	List<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();
        	
        	nNo = 1;
        	for (HashMap<String, Object> hMap : dataSource1) {
        		int nPort = (Integer)hMap.get("port");
        		
        		if (nPort < 0) continue;
        		
        		Iterable<DBObject> dbTmp = idsDao.ServiceEventTopN("DY", nDirection, assetCode, sStartDay, sEndDay, nPort);
        		for (DBObject pMap : dbTmp) {
            		HashMap<String, Object> map = new HashMap<String, Object>();
            		map.put("no", nNo);
            		map.put("port", nPort);
            		map.put("svcName", idsDao.getPortServiceName(nPort));
            		map.put("count", ((Number)hMap.get("count")).longValue());
            		map.put("message", (String)pMap.get("message"));
            		map.put("ratio", (((Number)pMap.get("count")).longValue()*100f)/((Number)hMap.get("count")).longValue());
            		
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
	
	public String push(List<String> contentsList, String sIdt, int n1, int n2, int n3, String msg) {
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
		return sTitle;
	}
	
}




