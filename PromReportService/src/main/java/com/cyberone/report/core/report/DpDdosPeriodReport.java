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
import com.cyberone.report.core.dao.DpDdosDao;
import com.cyberone.report.core.datasource.SynthesisDataSource;
import com.cyberone.report.model.UserInfo;
import com.cyberone.report.utils.StringUtil;
import com.mongodb.DBObject;

public class DpDdosPeriodReport extends BaseReport {
	
	private DpDdosDao ddosDao;

	private Logger logger = LoggerFactory.getLogger(getClass());

	private UserInfo userInfo;
	
	public DpDdosPeriodReport(UserInfo userInfo) {
		this.userInfo = userInfo;
		this.ddosDao = new DpDdosDao(userInfo.getPromDb(), userInfo.getReportDb());
	}
	
	/* 
	 * DDoS_DP 임의기간보고서 통계데이타 생성
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getDataSource(String sReportType, String sSearchDate, HashMap<String, Object> hMap, int ItemNo, List<String> contentsList) throws Exception {
		logger.debug("DDoS_DP 임의기간보고서 통계데이타 생성");
		
		HashMap<String, Object> reportData = new HashMap<String, Object>();
		
		reportData.put("reportType", sReportType);
		
		contentsList.add(ItemNo + ". " + (String)hMap.get("assetName") + " 장비의 탐지로그 분석");
		reportData.put("RT", ItemNo + ". " + (String)hMap.get("assetName") + " 장비의 탐지로그 분석");
		
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
			DBObject dbObj = ddosDao.selectAutoReportForm(Integer.parseInt(sReportType), assetCode);
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
				case "opt01" : case "opt02" : case "opt03" : case "opt04" : case "opt05" :  
					if (nC1 == 0) { 
						contentsList.add("  " + ItemNo + "." + ++nC1 + " 탐지로그 발생추이");
						reportData.put("C1", "  " + ItemNo + "." + nC1 + " 탐지로그 발생추이");
					}
					break;
				//이벤트 현황
				case "opt06" : case "opt07" : case "opt08" : case "opt09" :   
					if (nC2 == 0) { 
						contentsList.add("  " + ItemNo + "." + (++nC2 + nC1) + " 이벤트 현황");
						reportData.put("C2", "  " + ItemNo + "." + (nC2 + nC1) + " 이벤트 현황");
					}
					break;
				//출발지IP 현황
				case "opt10" : case "opt11" : case "opt12" :   
					if (nC3 == 0) { 
						contentsList.add("  " + ItemNo + "." + (++nC3 + nC2 + nC1) + " 출발지IP 현황");
						reportData.put("C3", "  " + ItemNo + "." + (nC3 + nC2 + nC1) + " 출발지IP 현황");
					}
					break;
				//목적지IP 현황
				case "opt13" : case "opt14" : case "opt15" :   
					if (nC4 == 0) { 
						contentsList.add("  " + ItemNo + "." + (++nC4 + nC3 + nC2 + nC1) + " 목적지IP 현황");
						reportData.put("C4", "  " + ItemNo + "." + (nC4 + nC3 + nC2 + nC1) + " 목적지IP 현황");
					}
					break;
				//서비스 현황
				case "opt16" : case "opt17" : case "opt18" :   
					if (nC5 == 0) { 
						contentsList.add("  " + ItemNo + "." + (++nC5 + nC4 + nC3 + nC2 + nC1) + " 서비스 현황");
						reportData.put("C5", "  " + ItemNo + "." + (nC5 + nC4 + nC3 + nC2 + nC1) + " 서비스 현황");
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
				case "opt01" :	//전체 탐지로그 발생추이 - 로그건
					reportData.put("opt01", push(contentsList, "    ", ItemNo, nC1, nS1++, " 전체 탐지로그 발생추이 - 로그건"));
					All_DetectLog_Trend(reportData, assetCode, sStartDay, sEndDay);
					break;
				case "opt02" :	//전체 탐지로그 발생추이 - 유형건 
					reportData.put("opt02", push(contentsList, "    ", ItemNo, nC1, nS1++, " 전체 탐지로그 발생추이 - 유형건"));
					All_DetectGroup_Trend(reportData, assetCode, sStartDay, sEndDay);
					break;
				case "opt03" : 	//전체 공격규모 추이 - 로그건
					reportData.put("opt03", push(contentsList, "    ", ItemNo, nC1, nS1++, " 전체 공격규모 추이 - 로그건"));
					All_DetectAttack_Trend(reportData, assetCode, sStartDay, sEndDay);
					break;
				case "opt04" :	//전체 공격규모 추이 - 유형 & Drop Packet Cnt
					reportData.put("opt04", push(contentsList, "    ", ItemNo, nC1, nS1++, " 전체 공격규모 추이 - 유형 & Drop Packet Cnt"));
					Group_DetectAttack_Trend(reportData, assetCode, sStartDay, sEndDay, "pktCnt");
					break;
				case "opt05" : 	//전체 공격규모 추이 - 유형 & BandWidth
					reportData.put("opt05", push(contentsList, "    ", ItemNo, nC1, nS1++, " 전체 공격규모 추이 - 유형 & BandWidth"));
					Group_DetectAttack_Trend(reportData, assetCode, sStartDay, sEndDay, "bandwidth");
					break;
				case "opt06" : 	//전체 탐지로그 & 공격 유형별 TOP#10
					reportData.put("opt06", push(contentsList, "    ", ItemNo, nC2 + nC1, nS2++, " 전체 탐지로그 & 공격 유형별 TOP#10"));
					Attact_GroupTopN(reportData, assetCode, sStartDay, sEndDay);
					break;
				case "opt07" : 	//전체 탐지로그 & 이벤트 TOP - 로그건
					reportData.put("opt07", push(contentsList, "    ", ItemNo, nC2 + nC1, nS2++, " 전체 탐지로그 & 이벤트 TOP - 로그건"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd7")));
					All_EventTopN(reportData, assetCode, sStartDay, sEndDay, "count", nChoice);
					break;
				case "opt08" : 	//전체 탐지로그 & 이벤트 TOP - Drop Packet Cnt
					reportData.put("opt08", push(contentsList, "    ", ItemNo, nC2 + nC1, nS2++, " 전체 탐지로그 & 이벤트 TOP - Drop Packet Cnt"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd8")));
					All_EventTopN(reportData, assetCode, sStartDay, sEndDay, "pktCnt", nChoice);
					break;
				case "opt09" : 	//전체 탐지로그 & 이벤트 TOP - Bandwidth
					reportData.put("opt09", push(contentsList, "    ", ItemNo, nC2 + nC1, nS2++, " 전체 탐지로그 & 이벤트 TOP - Bandwidth"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd9")));
					All_EventTopN(reportData, assetCode, sStartDay, sEndDay, "bandWidth", nChoice);
					break;
				case "opt10" : 	//전체 탐지로그 & SIP TOP - 로그건
					reportData.put("opt10", push(contentsList, "    ", ItemNo, nC3 + nC2 + nC1, nS3++, " 전체 탐지로그 & SIP TOP - 로그건"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd10")));
					DetectLog_IpTopN(reportData, assetCode, sStartDay, sEndDay, true, "count", nChoice, !StringUtil.isEmpty(hData.get("ck10")));
					break;
				case "opt11" : 	//전체 탐지로그 & SIP TOP - Drop Packet Cnt
					reportData.put("opt11", push(contentsList, "    ", ItemNo, nC3 + nC2 + nC1, nS3++, " 전체 탐지로그 & SIP TOP - Drop Packet Cnt"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd11")));
					DetectLog_IpTopN(reportData, assetCode, sStartDay, sEndDay, true, "pktCnt", nChoice, !StringUtil.isEmpty(hData.get("ck11")));
					break;
				case "opt12" : 	//전체 탐지로그 & SIP TOP - BandWidth
					reportData.put("opt12", push(contentsList, "    ", ItemNo, nC3 + nC2 + nC1, nS3++, " 전체 탐지로그 & SIP TOP - BandWidth"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd12")));
					DetectLog_IpTopN(reportData, assetCode, sStartDay, sEndDay, true, "bandWidth", nChoice, !StringUtil.isEmpty(hData.get("ck12")));
					break;
				case "opt13" : 	//전체 탐지로그 & DIP TOP - 로그건
					reportData.put("opt13", push(contentsList, "    ", ItemNo, nC4 + nC3 + nC2 + nC1, nS4++, " 전체 탐지로그 & DIP TOP - 로그건"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd13")));
					DetectLog_IpTopN(reportData, assetCode, sStartDay, sEndDay, false, "count", nChoice, false);
					break;
				case "opt14" : 	//전체 탐지로그 & DIP TOP - Drop Packet Cnt
					reportData.put("opt14", push(contentsList, "    ", ItemNo, nC4 + nC3 + nC2 + nC1, nS4++, " 전체 탐지로그 & DIP TOP - Drop Packet Cnt"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd14")));
					DetectLog_IpTopN(reportData, assetCode, sStartDay, sEndDay, false, "pktCnt", nChoice, false);
					break;
				case "opt15" : 	//전체 탐지로그 & DIP TOP - BandWidth
					reportData.put("opt15", push(contentsList, "    ", ItemNo, nC4 + nC3 + nC2 + nC1, nS4++, " 전체 탐지로그 & DIP TOP - BandWidth"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd15")));
					DetectLog_IpTopN(reportData, assetCode, sStartDay, sEndDay, false, "bandWidth", nChoice, false);
					break;
				case "opt16" : 	//전체 탐지로그 & 서비스 TOP10 - 로그건
					reportData.put("opt16", push(contentsList, "    ", ItemNo, nC5 + nC4 + nC3 + nC2 + nC1, nS5++, " 전체 탐지로그 & 서비스 TOP10 - 로그건"));
					DetectLog_ServiceTopN(reportData, assetCode, sStartDay, sEndDay, "count");
					break;
				case "opt17" : 	//전체 탐지로그 & 서비스 TOP10 - Drop Packet Cnt
					reportData.put("opt17", push(contentsList, "    ", ItemNo, nC5 + nC4 + nC3 + nC2 + nC1, nS5++, " 전체 탐지로그 & 서비스 TOP10 - Drop Packet Cnt"));
					DetectLog_ServiceTopN(reportData, assetCode, sStartDay, sEndDay, "pktCnt");
					break;
				case "opt18" : 	//전체 탐지로그 & 서비스 TOP10 - BandWidth
					reportData.put("opt18", push(contentsList, "    ", ItemNo, nC5 + nC4 + nC3 + nC2 + nC1, nS5++, " 전체 탐지로그 & 서비스 TOP10 - BandWidth"));
					DetectLog_ServiceTopN(reportData, assetCode, sStartDay, sEndDay, "bandWidth");
					break;
				case "opt99" :	//성능정보
					//reportData.put("opt99", push(contentsList, "    ", ItemNo, nC6 + nC5 + nC4 + nC3 + nC2 + nC1, nS6++, " 성능 정보"));
					PerformanceInfo(ddosDao, reportData, ItemNo, nC6 + nC5 + nC4 + nC3 + nC2 + nC1, assetCode, sStartDay, sEndDay);
					break;
			}
		}
		return reportData;
	}

	private void All_DetectLog_Trend(HashMap<String, Object> reportData, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();

		Iterable<DBObject> dbResult = ddosDao.AllDetectLogTrend("DY", "1", assetCode, sStartDay, sEndDay);

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
    	
    	List<String> dayPeriod = ddosDao.getPeriodDay(sStartDay, sEndDay, 1);
    	
    	String[] saGubun = {"차단", "허용"};
    	for (int action = 0; action < 2; action++) {
    		for (String tDay : dayPeriod) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		DBObject val = mapResult.get(String.valueOf(action) + tDay);
	    		map.put("series", saGubun[action]);
	    		map.put("category", tDay);
	    		map.put("value", val != null ? ((Number)val.get("count")).longValue() : 0);

	    		dataSource.add(map);
	    		logger.debug(map.toString());
    		}	    		
    	}
    	
		dbResult = ddosDao.AllDetectLogTrend("DY", "", assetCode, sStartDay, sEndDay);
		
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
    		map.put("series", "전체");
    		map.put("category", tDay);
    		map.put("value", val != null ? ((Number)val.get("count")).longValue() : 0);
    		
    		dataSource.add(map);
    		logger.debug(map.toString());
		}
		
		reportData.put("OPT1_1", new SynthesisDataSource(dataSource));
	}

	private void All_DetectGroup_Trend(HashMap<String, Object> reportData, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();

		Iterable<DBObject> dbResult = ddosDao.AllDetectAttackTrend("DY", assetCode, sStartDay, sEndDay, true, "count");

		List<String> saGroup = new ArrayList<String>();
    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
    	for (DBObject val : dbResult) {
    		String sGroup = (String)val.get("group");
    		String sYear = ((Integer)val.get("year")).toString();
    		String sMonth = ((Integer)val.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    		String sDay = ((Integer)val.get("day")).toString();
    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
    		mapResult.put(sGroup + "-" + sYear + sMonth + sDay, val);
    		if (!saGroup.contains(sGroup)) {
    			saGroup.add(sGroup);
    		}
    	}
    	
    	List<String> dayPeriod = ddosDao.getPeriodDay(ddosDao.addDate(sStartDay,-2), sEndDay, 1);
    	
    	for (String sGroup : saGroup) {
    		for (String tDay : dayPeriod) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		DBObject val = mapResult.get(sGroup + "-" + tDay);
	    		map.put("series", sGroup);
	    		map.put("category", tDay);
	    		map.put("value", val != null ? ((Number)val.get("count")).longValue() : 0);

	    		dataSource.add(map);
	    		logger.debug(map.toString());
    		}	    		
    	}
		
		reportData.put("OPT2_1", new SynthesisDataSource(dataSource));
	}
	
	private void All_DetectAttack_Trend(HashMap<String, Object> reportData, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();

		Iterable<DBObject> dbResult = ddosDao.AllDetectAttackTrend("DY", assetCode, sStartDay, sEndDay, false, "pktCnt");
		
    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
    	for (DBObject val : dbResult) {
    		String sYear = ((Integer)val.get("year")).toString();
    		String sMonth = ((Integer)val.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    		String sDay = ((Integer)val.get("day")).toString();
    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
    		mapResult.put(sYear + sMonth + sDay, val);
    	}
		
    	List<String> dayPeriod = ddosDao.getPeriodDay(sStartDay, sEndDay, 1);
    	
		for (String tDay : dayPeriod) {
    		HashMap<String, Object> map = new HashMap<String, Object>();
    		DBObject val = mapResult.get(tDay);
    		map.put("series", "Drop Packet");
    		map.put("category", tDay);
    		map.put("value", val != null ? ((Number)val.get("pktCnt")).longValue() : 0);

    		dataSource.add(map);
    		logger.debug(map.toString());
		}	    		

		dbResult = ddosDao.AllDetectAttackTrend("DY", assetCode, sStartDay, sEndDay, false, "bandwidth");
		
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
    		map.put("series", "Bandwidth");
    		map.put("category", tDay);
    		map.put("value", val != null ? ((Number)val.get("bandwidth")).longValue() : 0);

    		dataSource.add(map);
    		logger.debug(map.toString());
		}	    		
		
		reportData.put("OPT3_1", new SynthesisDataSource(dataSource));
	}
	
	private void Group_DetectAttack_Trend(HashMap<String, Object> reportData, int assetCode, String sStartDay, String sEndDay, String sSortField) throws Exception {
		
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();

		Iterable<DBObject> dbResult = ddosDao.AllDetectAttackTrend("DY", assetCode, sStartDay, sEndDay, true, sSortField);
		
		List<String> saGroup = new ArrayList<String>();
    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
    	for (DBObject val : dbResult) {
    		String sGroup = (String)val.get("group"); 
    		String sYear = ((Integer)val.get("year")).toString();
    		String sMonth = ((Integer)val.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    		String sDay = ((Integer)val.get("day")).toString();
    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
    		mapResult.put(sGroup + "-" +sYear + sMonth + sDay, val);
    		if (!saGroup.contains(sGroup)) {
    			saGroup.add(sGroup);
    		}
    	}
		
    	List<String> dayPeriod = ddosDao.getPeriodDay(sStartDay, sEndDay, 1);
    	
    	for (String sGroup : saGroup) {
    		for (String tDay : dayPeriod) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		DBObject val = mapResult.get(sGroup + "-" + tDay);
	    		map.put("series", sGroup);
	    		map.put("category", tDay);
	    		map.put("value", val != null ? ((Number)val.get(sSortField)).longValue() : 0);

	    		dataSource.add(map);
	    		logger.debug(map.toString());
    		}
    	}
		
		if (sSortField.equals("pktCnt")) {
			reportData.put("OPT4_1", new SynthesisDataSource(dataSource));
		} else {
			reportData.put("OPT5_1", new SynthesisDataSource(dataSource));
		}
	}
	
	private void Attact_GroupTopN(HashMap<String, Object> reportData, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();

		Iterable<DBObject> dbResult = ddosDao.GroupTopN("DY", assetCode, sStartDay, sEndDay, false, "");

		int nTotal = 0;
    	for (DBObject val : dbResult) {
    		String sGroup = (String)val.get("group");
    		val.put("allow", 0);
    		val.put("cutoff", 0);
    		
    		nTotal += ((Number)val.get("count")).longValue();
    		
    		Iterable<DBObject> dbTmp = ddosDao.GroupTopN("DY", assetCode, sStartDay, sEndDay, true, sGroup);
    		for (DBObject tVal : dbTmp) {
    			String sAction = (String)tVal.get("action");
    			if (sAction.equals(ALLOW)) {
    				val.put("allow", tVal != null ? ((Number)tVal.get("count")).longValue() : 0);
    			} else {
    				val.put("cutoff", tVal != null ? ((Number)tVal.get("count")).longValue() : 0);
    			}
    		}
    	}

    	int nNo = 1;
    	int nCount = 0, nAllow = 0, nCutoff = 0;
    	for (DBObject val : dbResult) {
    		String sGroup = (String)val.get("group");
    		if (!sGroup.equals("-1") && nNo <= 10) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		map.put("group", sGroup);
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
			map.put("group", "기타");
			map.put("total", nTotal);
			map.put("count", nCount);
			map.put("allow", nAllow);
			map.put("cutoff", nCutoff);
	
			dataSource1.add(map);
			logger.debug(map.toString());
    	}

    	nNo = 1;
    	for (DBObject val : dbResult) {
    		String sGroup = (String)val.get("group");
    		
    		Iterable<DBObject> messageTop5 = ddosDao.GroupMessageCondition("DY", assetCode, sStartDay, sEndDay, sGroup, 5);
    		for (DBObject sVal : messageTop5) {
    			HashMap<String, Object> map = new HashMap<String, Object>();
    			map.put("no", nNo);
    			map.put("group", sGroup);
    			map.put("total", ((Number)val.get("count")).longValue());    			
    			map.put("message", StringUtil.convertString(sVal.get("message")));
    			map.put("action", sVal.get("action"));
    			map.put("count", ((Number)sVal.get("count")).longValue());
        		
        		dataSource2.add(map);
        		logger.debug(map.toString());
    		}
    		if (nNo >= 10) break;
    		nNo++;
    	}
    	
    	reportData.put("OPT6_1", new SynthesisDataSource(dataSource1));
    	reportData.put("OPT6_2", new SynthesisDataSource(dataSource2));
		
	}		
	
	private void All_EventTopN(HashMap<String, Object> reportData, int assetCode, String sStartDay, String sEndDay, String sSortField, int nLimit) throws Exception {
		
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();

		Iterable<DBObject> dbResult = ddosDao.EventTopN("DY", assetCode, sStartDay, sEndDay, true, "", sSortField);

		int nTotal = 0;
    	for (DBObject val : dbResult) {
   			nTotal += ((Number)val.get(sSortField)).longValue();
    	}

    	int nNo = 1;
    	for (DBObject val : dbResult) {
    		String sMessage = (String)val.get("message");
    		String sAction = (String)val.get("action");
    		if (!sMessage.equals("-1") && nNo <= 10) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		map.put("message", sMessage + (sAction.equals("1") ? "(허용)" : "(차단)"));
	    		map.put("total", nTotal);
	    		map.put("count", ((Number)val.get(sSortField)).longValue());

	    		dataSource1.add(map);
	    		logger.debug(map.toString());
    		}
    		if (nNo >= 10) break;
    		nNo++;
    	}
    	
    	nNo = 1;
    	for (DBObject val : dbResult) {
    		String sMessage = (String)val.get("message");
    		String sAction = (String)val.get("action");
    		
    		Iterable<DBObject> srcIpTop5 = ddosDao.EventSrcIpTopN("DY", assetCode, sStartDay, sEndDay, sAction, sMessage, sSortField, 5);
    		for (DBObject sVal : srcIpTop5) {
    			HashMap<String, Object> map = new HashMap<String, Object>();
    			map.put("no", nNo);
    			map.put("message", sMessage);
    			map.put("action", sAction);
    			map.put("total", ((Number)val.get("count")).longValue());    			
    			map.put("srcIp", StringUtil.convertString(sVal.get("srcIp")));
   				map.put("geoCode", Constants.getCountryCode(StringUtil.convertString(sVal.get("srcIp"))));
   				map.put("destIp", StringUtil.convertString(sVal.get("destIp")));
    			map.put("count", ((Number)sVal.get("count")).longValue());
    			map.put("pktCnt", ((Number)sVal.get("pktCnt")).longValue());
    			map.put("bandWidth", ((Number)sVal.get("bandWidth")).longValue());
        		
        		dataSource2.add(map);
        		logger.debug(map.toString());
    		}
    		
    		if (nNo >= nLimit) break;
    		nNo++;
    	}
    	
    	if (sSortField.equals("count")) {
	    	reportData.put("OPT7_1", new SynthesisDataSource(dataSource1));
	    	reportData.put("OPT7_2", new SynthesisDataSource(dataSource2));
    	} else if (sSortField.equals("pktCnt")) {
	    	reportData.put("OPT8_1", new SynthesisDataSource(dataSource1));
	    	reportData.put("OPT8_2", new SynthesisDataSource(dataSource2));
    	} else if (sSortField.equals("bandWidth")) {
	    	reportData.put("OPT9_1", new SynthesisDataSource(dataSource1));
	    	reportData.put("OPT9_2", new SynthesisDataSource(dataSource2));
    	}
		
	}		
	
	private void DetectLog_IpTopN(HashMap<String, Object> reportData, int assetCode, String sStartDay, String sEndDay, boolean bSrcIp, String sSortField, int nLimit, boolean bChk) throws Exception {
		
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();
		
    	//IP 건수기준 TOP N 리스트
		Iterable<DBObject> ipTopNResult = ddosDao.IpTopN("DY", assetCode, sStartDay, sEndDay, bSrcIp, sSortField, nLimit);
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
    			ipTopNCondition = ddosDao.IpTopNCondition("DY", assetCode, sStartDay, sEndDay, (String)val.get("srcIp"), bSrcIp, sSortField);
    		} else {
    			ipTopNCondition = ddosDao.IpTopNCondition("DY", assetCode, sStartDay, sEndDay, (String)val.get("destIp"), bSrcIp, sSortField);
    		}
    		
			for (DBObject dbObj : ipTopNCondition) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("no", nNo);
				if (bSrcIp) {
					map.put("srcIp", (String)dbObj.get("srcIp"));
					map.put("message", (String)val.get("message"));
					map.put("destIp", (String)dbObj.get("destIp"));
				} else {
					map.put("destIp", (String)dbObj.get("destIp"));
					map.put("srcIp", (String)dbObj.get("srcIp"));
					map.put("message", (String)val.get("message"));
				}
				map.put("geoCode", Constants.getCountryCode((String)dbObj.get("srcIp")));
				map.put("action", (String)dbObj.get("action"));
				map.put("total", ((Number)val.get(sSortField)).longValue());
				map.put("count", ((Number)dbObj.get(sSortField)).longValue());
				
				dataSource1.add(map);
				logger.debug(map.toString());
			}
			nNo++;
    	}
    	
    	if (bSrcIp) {
	    	if (sSortField.equals("count")) {
	    		reportData.put("OPT10_1", new SynthesisDataSource(dataSource1));
	    	} else if (sSortField.equals("pktCnt")) {
	    		reportData.put("OPT11_1", new SynthesisDataSource(dataSource1));
	    	} else if (sSortField.equals("bandWidth")) {
	    		reportData.put("OPT12_1", new SynthesisDataSource(dataSource1));
	    	}
	    	
	    	if (topNIps.size() > 10) {
	    		topNIps = topNIps.subList(0, 10);
	    	}
	    	
	    	if (bChk) { //SRC IP TOP10 세션 로그 발생추이 
	    		ArrayList<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();
	        	Iterable<DBObject> ipTopNTrend = ddosDao.IpTopNTrend("DY", assetCode, sStartDay, sEndDay, topNIps, true, sSortField);
	        	
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
	        	
	        	List<String> dayPeriod = ddosDao.getPeriodDay(sStartDay, sEndDay, 1);
	        	
	        	for (String strIp : topNIps) {
	        		for (String tDay : dayPeriod) {
    		    		DBObject val = mapResult.get(strIp + "-" + tDay);
    		    		HashMap<String, Object> map = new HashMap<String, Object>();
    		    		map.put("srcIp", strIp);
    		    		map.put("day", Integer.valueOf(tDay));
    		    		map.put("count", val != null ? ((Number)val.get(sSortField)).longValue() : 0);
    		    		dataSource2.add(map);
    					logger.debug(map.toString());
	        		}
	        	}
	    		
	        	if (sSortField.equals("count")) {
		    		reportData.put("OPT10_2", new SynthesisDataSource(dataSource2));
	        	} else if (sSortField.equals("pktCnt")) {
	        		reportData.put("OPT11_2", new SynthesisDataSource(dataSource2));
	        	} else if (sSortField.equals("bandWidth")) {
	        		reportData.put("OPT12_2", new SynthesisDataSource(dataSource2));
		    	}
	    	}
    	} else {
	    	if (sSortField.equals("count")) {
	    		reportData.put("OPT13_1", new SynthesisDataSource(dataSource1));
	    	} else if (sSortField.equals("pktCnt")) {
	    		reportData.put("OPT14_1", new SynthesisDataSource(dataSource1));
	    	} else if (sSortField.equals("bandWidth")) {
	    		reportData.put("OPT15_1", new SynthesisDataSource(dataSource1));
	    	}
    	}
	}
	
	private void DetectLog_ServiceTopN(HashMap<String, Object> reportData, int assetCode, String sStartDay, String sEndDay, String sSortField) throws Exception {
		
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();
		
    	//Port 건수기준 TOP N 리스트
		Iterable<DBObject> serviceTopNResult = ddosDao.ServiceTopN("DY", assetCode, sStartDay, sEndDay, sSortField, 10);
    	List<Integer> topNService = new ArrayList<Integer>();
    	for (DBObject val : serviceTopNResult) {
    		topNService.add((Integer)val.get("destPort"));
    	}

    	int nNo = 1;
    	for (DBObject val : serviceTopNResult) {
    		Iterable<DBObject> ipTopNCondition = null;
   			ipTopNCondition = ddosDao.ServiceTopNCondition("DY", assetCode, sStartDay, sEndDay, (Integer)val.get("destPort"), sSortField);

			for (DBObject dbObj : ipTopNCondition) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("no", nNo);
				map.put("destPort", (Integer)dbObj.get("destPort"));
				map.put("message", (String)val.get("message"));
				map.put("destIp", (String)dbObj.get("destIp"));
				map.put("action", (String)dbObj.get("action"));
				map.put("total", ((Number)val.get(sSortField)).longValue());
				map.put("count", ((Number)dbObj.get(sSortField)).longValue());
				
				dataSource1.add(map);
				logger.debug(map.toString());
			}
			nNo++;
    	}
    	
    	if (sSortField.equals("count")) {
    		reportData.put("OPT16_1", new SynthesisDataSource(dataSource1));
    	} else if (sSortField.equals("pktCnt")) {
    		reportData.put("OPT17_1", new SynthesisDataSource(dataSource1));
    	} else if (sSortField.equals("bandWidth")) {
    		reportData.put("OPT18_1", new SynthesisDataSource(dataSource1));
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




