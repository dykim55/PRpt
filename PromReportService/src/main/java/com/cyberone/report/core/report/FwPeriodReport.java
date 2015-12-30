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
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;

import com.cyberone.report.Constants;
import com.cyberone.report.core.dao.FwDao;
import com.cyberone.report.core.datasource.SynthesisDataSource;
import com.cyberone.report.model.UserInfo;
import com.cyberone.report.utils.StringUtil;
import com.mongodb.DBObject;

public class FwPeriodReport extends BaseReport {
	
	private FwDao fwDao;

	private UserInfo userInfo;
	
	public FwPeriodReport(UserInfo userInfo) {
		this.userInfo = userInfo;
		this.fwDao = new FwDao(userInfo.getPromDb(), userInfo.getReportDb());
	}
	
	/* 
	 * 방화벽 보고서 통계데이타 생성
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getDataSource(String sReportType, String sSearchDate, HashMap<String, Object> hMap, int ItemNo, List<String> contentsList) throws Exception {
		logger.debug("방화벽 임의기간보고서 통계데이타 생성");
		
		HashMap<String, Object> reportData = new HashMap<String, Object>();
		
		reportData.put("reportType", sReportType);
		
		contentsList.add(ItemNo + ". " + (String)hMap.get("assetName") + " 장비의 세션로그 분석");
		reportData.put("RT", ItemNo + ". " + (String)hMap.get("assetName") + " 장비의 세션로그 분석"); //Report Title
		
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
			DBObject dbObj = fwDao.selectAutoReportForm(Integer.parseInt(sReportType), assetCode);
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
				//세션로그 발생 추이
				case "opt01" : case "opt02" : case "opt03" :
					if (nC1 == 0) { 
						contentsList.add("  " + ItemNo + "." + ++nC1 + " 세션로그 발생추이");
						reportData.put("C1", "  " + ItemNo + "." + nC1 + " 세션로그 발생추이");
					}
					break;
				//출발지IP 현황
				case "opt04" : case "opt05" : case "opt06" : case "opt07" : case "opt08" : case "opt09" :
					if (nC2 == 0) { 
						contentsList.add("  " + ItemNo + "." + (++nC2 + nC1) + " 출발지IP 현황");
						reportData.put("C2", "  " + ItemNo + "." + (nC2 + nC1) + " 출발지IP 현황");
					}
					break;
				//목적지IP 현황
				case "opt10" : case "opt11" : case "opt12" : case "opt13" : case "opt14" : case "opt15" :
					if (nC3 == 0) { 
						contentsList.add("  " + ItemNo + "." + (++nC3 + nC2 + nC1) + " 목적지IP 현황");
						reportData.put("C3", "  " + ItemNo + "." + (nC3 + nC2 + nC1) + " 목적지IP 현황");
					}
					break;
				//서비스 현황
				case "opt16" : case "opt17" : case "opt18" : case "opt19" : case "opt20" : case "opt21" :
					if (nC4 == 0) { 
						contentsList.add("  " + ItemNo + "." + (++nC4 + nC3 + nC2 + nC1) + " 서비스 현황");
						reportData.put("C4", "  " + ItemNo + "." + (nC4 + nC3 + nC2 + nC1) + " 서비스 현황");
					}
					break;
				//성능정보
				case "opt99" :
					if (nC5 == 0) { 
						contentsList.add("  " + ItemNo + "." + (++nC5 + nC4 + nC3 + nC2 + nC1) + " 성능현황");
						reportData.put("C5", "  " + ItemNo + "." + (nC5 + nC4 + nC3 + nC2 + nC1) + " 성능현황");
					}
					break;
			}
			
			switch (e.getKey()) {
				case "opt01" :	//전체 세션로그 발생추이
					reportData.put("opt01", push(contentsList, "    ", ItemNo, nC1, nS1++, " 전체 세션로그 발생추이"));
					All_SessionLog_Trend(reportData, -1, assetCode, sStartDay, sEndDay, sEtc);
					break;
				case "opt02" :	//외부에서 내부로의 전체 세션 로그 발생추이
					reportData.put("opt02", push(contentsList, "    ", ItemNo, nC1, nS1++, " 외부에서 내부로의 전체 세션로그 발생추이"));
					All_SessionLog_Trend(reportData, INBOUND, assetCode, sStartDay, sEndDay, sEtc);
					break;
				case "opt03" : 	//내부에서 외부로의 전체 세션 로그 발생추이
					reportData.put("opt03", push(contentsList, "    ", ItemNo, nC1, nS1++, " 내부에서 외부로의 전체 세션로그 발생추이"));
					All_SessionLog_Trend(reportData, OUTBOUND, assetCode, sStartDay, sEndDay, sEtc);
					break;
				case "opt04" : 	//외부에서 내부로의 전체 세션로그 & SIP TOP
					reportData.put("opt04", push(contentsList, "    ", ItemNo, nC2 + nC1, nS2++, " 외부에서 내부로의 전체 세션로그에 대한 출발지IP TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd4")));
					Action_SessionLog_TopN(reportData, INBOUND, "", assetCode, sStartDay, sEndDay, true, nChoice, false);
					break;
				case "opt05" : 	//외부에서 내부로의 허용 세션로그 & SIP TOP
					reportData.put("opt05", push(contentsList, "    ", ItemNo, nC2 + nC1, nS2++, " 외부에서 내부로의 허용 세션로그에 대한 출발지IP TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd5")));
					Action_SessionLog_TopN(reportData, INBOUND, ALLOW, assetCode, sStartDay, sEndDay, true, nChoice, false);
					break;
				case "opt06" : 	//외부에서 내부로의 차단 세션로그 & SIP TOP
					reportData.put("opt06", push(contentsList, "    ", ItemNo, nC2 + nC1, nS2++, " 외부에서 내부로의 차단 세션로그에 대한 출발지IP TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd6")));
					Action_SessionLog_TopN(reportData, INBOUND, CUTOFF, assetCode, sStartDay, sEndDay, true, nChoice, !StringUtil.isEmpty(hData.get("ck6")));
					break;
				case "opt07" : 	//내부에서 외부로의 전체 세션로그 & SIP TOP
					reportData.put("opt07", push(contentsList, "    ", ItemNo, nC2 + nC1, nS2++, " 내부에서 외부로의 전체 세션로그에 대한 출발지IP TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd7")));
					Action_SessionLog_TopN(reportData, OUTBOUND, "", assetCode, sStartDay, sEndDay, true, nChoice, false);
					break;
				case "opt08" : 	//내부에서 외부로의 허용 세션로그 & SIP TOP
					reportData.put("opt08", push(contentsList, "    ", ItemNo, nC2 + nC1, nS2++, " 내부에서 외부로의 허용 세션로그에 대한 출발지IP TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd8")));
					Action_SessionLog_TopN(reportData, OUTBOUND, ALLOW, assetCode, sStartDay, sEndDay, true, nChoice, false);
					break;
				case "opt09" : 	//내부에서 외부로의 차단 세션로그 & SIP TOP
					reportData.put("opt09", push(contentsList, "    ", ItemNo, nC2 + nC1, nS2++, " 내부에서 외부로의 차단 세션로그에 대한 출발지IP TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd9")));
					Action_SessionLog_TopN(reportData, OUTBOUND, CUTOFF, assetCode, sStartDay, sEndDay, true, nChoice, !StringUtil.isEmpty(hData.get("ck9")));
					break;
				case "opt10" : 	//외부에서 내부로의 전체 세션로그 & DIP TOP
					reportData.put("opt10", push(contentsList, "    ", ItemNo, nC3 + nC2 + nC1, nS3++, " 외부에서 내부로의 전체 세션로그에 대한 목적지IP TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd10")));
					Action_SessionLog_TopN(reportData, INBOUND, "", assetCode, sStartDay, sEndDay, false, nChoice, false);
					break;
				case "opt11" : 	//외부에서 내부로의 허용 세션로그 & DIP TOP
					reportData.put("opt11", push(contentsList, "    ", ItemNo, nC3 + nC2 + nC1, nS3++, " 외부에서 내부로의 허용 세션로그에 대한 목적지IP TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd11")));
					Action_SessionLog_TopN(reportData, INBOUND, ALLOW, assetCode, sStartDay, sEndDay, false, nChoice, false);
					break;
				case "opt12" : 	//외부에서 내부로의 차단 세션로그 & DIP TOP
					reportData.put("opt12", push(contentsList, "    ", ItemNo, nC3 + nC2 + nC1, nS3++, " 외부에서 내부로의 차단 세션로그에 대한 목적지IP TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd12")));
					Action_SessionLog_TopN(reportData, INBOUND, CUTOFF, assetCode, sStartDay, sEndDay, false, nChoice, false);
					break;
				case "opt13" : 	//내부에서 외부로의 전체 세션로그 & DIP TOP
					reportData.put("opt13", push(contentsList, "    ", ItemNo, nC3 + nC2 + nC1, nS3++, " 내부에서 외부로의 전체 세션로그에 대한 목적지IP TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd13")));
					Action_SessionLog_TopN(reportData, OUTBOUND, "", assetCode, sStartDay, sEndDay, false, nChoice, false);
					break;
				case "opt14" : 	//내부에서 외부로의 허용 세션로그 & DIP TOP
					reportData.put("opt14", push(contentsList, "    ", ItemNo, nC3 + nC2 + nC1, nS3++, " 내부에서 외부로의 허용 세션로그에 대한 목적지IP TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd14")));
					Action_SessionLog_TopN(reportData, OUTBOUND, ALLOW, assetCode, sStartDay, sEndDay, false, nChoice, false);
					break;
				case "opt15" : 	//내부에서 외부로의 차단 세션로그 & DIP TOP
					reportData.put("opt15", push(contentsList, "    ", ItemNo, nC3 + nC2 + nC1, nS3++, " 내부에서 외부로의 차단 세션로그에 대한 목적지IP TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd15")));
					Action_SessionLog_TopN(reportData, OUTBOUND, CUTOFF, assetCode, sStartDay, sEndDay, false, nChoice, false);
					break;
				case "opt16" : 	//외부에서 내부로의 허용 세션로그 & 서비스 TOP10
					reportData.put("opt16", push(contentsList, "    ", ItemNo, nC4 + nC3 + nC2 + nC1, nS4++, " 외부에서 내부로의 허용 세션로그에 대한 서비스 TOP 현황"));
					Action_ServiceTop10(reportData, INBOUND, ALLOW, assetCode, sStartDay, sEndDay, false);
					break;
				case "opt17" : 	//외부에서 내부로의 차단 세션로그 & 서비스 TOP10
					reportData.put("opt17", push(contentsList, "    ", ItemNo, nC4 + nC3 + nC2 + nC1, nS4++, " 외부에서 내부로의 차단 세션로그에 대한 서비스 TOP 현황"));
					Action_ServiceTop10(reportData, INBOUND, CUTOFF, assetCode, sStartDay, sEndDay, !StringUtil.isEmpty(hData.get("ck17")));
					break;
				case "opt18" : 	//외부에서 내부로의 차단 세션로그 & 서비스 TOP10 상세
					reportData.put("opt18", push(contentsList, "    ", ItemNo, nC4 + nC3 + nC2 + nC1, nS4++, " 외부에서 내부로의 차단 세션로그에 대한 서비스 상세현황"));
					Action_ServiceTop10_Detail(reportData, INBOUND, CUTOFF, assetCode, sStartDay, sEndDay);
					break;
				case "opt19" : 	//내부에서 외부로의 허용 세션로그 & 서비스 TOP10
					reportData.put("opt19", push(contentsList, "    ", ItemNo, nC4 + nC3 + nC2 + nC1, nS4++, " 내부에서 외부로의 허용 세션로그에 대한 서비스 TOP 현황"));
					Action_ServiceTop10(reportData, OUTBOUND, ALLOW, assetCode, sStartDay, sEndDay, false);
					break;
				case "opt20" : 	//내부에서 외부로의 차단 세션로그 & 서비스 TOP10
					reportData.put("opt20", push(contentsList, "    ", ItemNo, nC4 + nC3 + nC2 + nC1, nS4++, " 내부에서 외부로의 차단 세션로그에 대한 서비스 TOP 현황"));
					Action_ServiceTop10(reportData, OUTBOUND, CUTOFF, assetCode, sStartDay, sEndDay, !StringUtil.isEmpty(hData.get("ck20")));
					break;
				case "opt21" : 	//내부에서 외부로의 차단 세션로그 & 서비스 TOP10 상세
					reportData.put("opt21", push(contentsList, "    ", ItemNo, nC4 + nC3 + nC2 + nC1, nS4++, " 내부에서 외부로의 차단 세션로그에 대한 서비스 상세현황"));
					Action_ServiceTop10_Detail(reportData, OUTBOUND, CUTOFF, assetCode, sStartDay, sEndDay);
					break;
				case "opt99" :	//성능정보
					//reportData.put("opt99", push(contentsList, "    ", ItemNo, nC5 + nC4 + nC3 + nC2 + nC1, nS5++, " 성능 정보"));
					PerformanceInfo(fwDao, reportData, contentsList, ItemNo, nC5 + nC4 + nC3 + nC2 + nC1, assetCode, sStartDay, sEndDay);
					break;
			}				
		}
		return reportData;
	}

	private void All_SessionLog_Trend(HashMap<String, Object> reportData, int nDirection, int assetCode, String sStartDay, String sEndDay, String sSearchPort) throws Exception {
		
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();

		int[] searchPort = null;
		if (sSearchPort != null && !sSearchPort.isEmpty()) {
			String[] saPorts = sSearchPort.split(",");
			searchPort = new int[saPorts.length];
			for (int i = 0; i < saPorts.length; i++) {
				searchPort[i] = Integer.valueOf(saPorts[i].trim());
			}
		}
		
		List<String> dayPeriod = fwDao.getPeriodDay(sStartDay, sEndDay, 1);
		
		Iterable<DBObject> dbResult = fwDao.SessionLogTrend("DY", nDirection, "", assetCode, sStartDay, sEndDay, 0);
		
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
		
		//허용/차단
		dbResult = fwDao.SessionLogTrend("DY", nDirection, ALLOW, assetCode, sStartDay, sEndDay, 0);

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
    	
    	if (nDirection != OUTBOUND && searchPort != null) {
    		for (int i = 0; i < searchPort.length; i++) {
	    		String strServiceName = fwDao.getPortServiceName(searchPort[i]);
	    		dbResult = fwDao.SessionLogTrend("DY", nDirection, ALLOW, assetCode, sStartDay, sEndDay, searchPort[i]);
	    	
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
		    		map.put("series", strServiceName);
		    		map.put("category", tDay.substring(6, 8));
    	    		map.put("value", val != null ? ((Number)val.get("count")).longValue() : 0);
    	    		
    	    		dataSource1.add(map);
    	    		logger.debug(map.toString());
	    		}
    	    }
    	}
		
    	if (nDirection == -1) {
    		reportData.put("OPT1_1", new SynthesisDataSource(dataSource1));
    	} else if (nDirection == INBOUND) {
    		reportData.put("OPT2_1", new SynthesisDataSource(dataSource1));
    	} else if (nDirection == OUTBOUND) {
    		reportData.put("OPT3_1", new SynthesisDataSource(dataSource1));
    	}
	}
	
	//외부에서 내부로의 전체 세션로그 & SIP TOP
	private void Action_SessionLog_TopN(
			HashMap<String, Object> reportData, 
			int nDirection, 
			String sAction, 
			int assetCode, 
			String sStartDay, 
			String sEndDay, 
			boolean bSrcIp, 
			int nLimit,
			boolean bChk) throws Exception {
		
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();
		
    	//IP 건수기준 TOP N 리스트
		Iterable<DBObject> ipTopNResult = fwDao.IpTopN("DY", nDirection, sAction, assetCode, sStartDay, sEndDay, bSrcIp, nLimit);
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
    		String sIp = bSrcIp ? StringUtil.convertString(val.get("srcIp")) : StringUtil.convertString(val.get("destIp"));
    		
    		Iterable<DBObject> ipTopNCondition = fwDao.IpTopNCondition("DY", nDirection, sAction, assetCode, sStartDay, sEndDay, sIp, bSrcIp);

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
				map.put("count", ((Number)val.get("count")).longValue());
				map.put("action", StringUtil.convertString(dbObj.get("action")).equals("1") ? "허용" : "차단");
				map.put("ratio", (((Number)dbObj.get("count")).longValue()*100f)/((Number)val.get("count")).longValue());
				dataSource.add(map);
				logger.debug(map.toString());
			}
			nNo++;
    	}
    	
    	if (bSrcIp) {
	    	if (nDirection == INBOUND && StringUtil.isEmpty(sAction)) { //외부에서 내부로의 전체 세션로그 & SIP TOP
	    		reportData.put("OPT4_1", new SynthesisDataSource(dataSource));
	    	} else if (nDirection == INBOUND && sAction.equals(ALLOW)) { //외부에서 내부로의 허용 세션로그 & SIP TOP
	    		reportData.put("OPT5_1", new SynthesisDataSource(dataSource));
	    	} else if (nDirection == INBOUND && sAction.equals(CUTOFF)) { //외부에서 내부로의 차단 세션로그 & SIP TOP
	    		reportData.put("OPT6_1", new SynthesisDataSource(dataSource));
	    	} else if (nDirection == OUTBOUND && StringUtil.isEmpty(sAction)) { //내부에서 외부로의 전체 세션로그 & SIP TOP
	    		reportData.put("OPT7_1", new SynthesisDataSource(dataSource));
	    	} else if (nDirection == OUTBOUND && sAction.equals(ALLOW)) { //내부에서 외부로의 허용 세션로그 & SIP TOP
	    		reportData.put("OPT8_1", new SynthesisDataSource(dataSource));
	    	} else if (nDirection == OUTBOUND && sAction.equals(CUTOFF)) { //내부에서 외부로의 차단 세션로그 & SIP TOP
	    		reportData.put("OPT9_1", new SynthesisDataSource(dataSource));
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
	    	    		map.put("series", strIp);
			    		map.put("category", tDay.substring(6, 8));
	    	    		map.put("value", val != null ? ((Number)val.get("count")).longValue() : 0);
    		    	
	    	    		dataSource.add(map);
    					logger.debug(map.toString());
	        		}
	        	}
	    		
		    	if (nDirection == INBOUND) {
		    		reportData.put("OPT6_2", new SynthesisDataSource(dataSource));
		    	} else if (nDirection == OUTBOUND) {
		    		reportData.put("OPT9_2", new SynthesisDataSource(dataSource));
		    	}
	    	}
    	} else {
	    	if (nDirection == INBOUND && StringUtil.isEmpty(sAction)) { //외부에서 내부로의 전체 세션로그 & DIP TOP
	    		reportData.put("OPT10_1", new SynthesisDataSource(dataSource));
	    	} else if (nDirection == INBOUND && sAction.equals(ALLOW)) { //외부에서 내부로의 허용 세션로그 & DIP TOP
	    		reportData.put("OPT11_1", new SynthesisDataSource(dataSource));
	    	} else if (nDirection == INBOUND && sAction.equals(CUTOFF)) { //외부에서 내부로의 차단 세션로그 & DIP TOP
	    		reportData.put("OPT12_1", new SynthesisDataSource(dataSource));
	    	} else if (nDirection == OUTBOUND && StringUtil.isEmpty(sAction)) { //내부에서 외부로의 전체 세션로그 & DIP TOP
	    		reportData.put("OPT13_1", new SynthesisDataSource(dataSource));
	    	} else if (nDirection == OUTBOUND && sAction.equals(ALLOW)) { //내부에서 외부로의 허용 세션로그 & DIP TOP
	    		reportData.put("OPT14_1", new SynthesisDataSource(dataSource));
	    	} else if (nDirection == OUTBOUND && sAction.equals(CUTOFF)) { //내부에서 외부로의 차단 세션로그 & DIP TOP
	    		reportData.put("OPT15_1", new SynthesisDataSource(dataSource));
	    	}
    	}
    	
	}

	@SuppressWarnings("unchecked")
	private void Action_ServiceTop10(
			HashMap<String, Object> reportData, 
			int nDirection, 
			String sAction, 
			int assetCode, 
			String sStartDay, 
			String sEndDay,
			boolean bChk) throws Exception {
		
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();

		Iterable<DBObject> logResult = fwDao.ServiceSessionLog("DY", nDirection, sAction, assetCode, sStartDay, sEndDay);   	
		
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

				long lCurrCount = ((Number)val.get("count")).longValue();
				lTotalCount += lCurrCount;
				map.put("count", lCurrCount);
				
				nTop++;
				
				dataSource1.add(map);
				logger.debug(map.toString());
			} else {
				lTotalCount += ((Number)val.get("count")).longValue();
				currMap.put(String.valueOf((Integer)val.get("destPort")), val);
			}
    	}
		
    	long lCurrEtcCount = 0;
    	Set<Entry<String, Object>> currEntry = currMap.entrySet();
    	for (Entry<String, Object> obj : currEntry) {
    		HashMap<String,Object> map = (HashMap<String,Object>)obj.getValue();
    		lCurrEtcCount += ((Number)map.get("count")).longValue();
    	}

    	if (dataSource1.size() > 0 && lCurrEtcCount > 0) {
	    	HashMap<String, Object> map1 = new HashMap<String, Object>();
			map1.put("no", nTop);		
			map1.put("port", -1);
			map1.put("svcName", "기타");
			map1.put("count", lCurrEtcCount);
			dataSource1.add(map1);
    	}
    	
		for (HashMap<String, Object> map : dataSource1) {
			long count = ((Number)map.get("count")).longValue();
			map.put("ratio", (count * 100f) / lTotalCount);
		}
	
		List<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();
		for (HashMap<String, Object> pMap : dataSource1) {
			if ((Integer)pMap.get("no") < 11) {
				dataSource2.add((HashMap<String, Object>)pMap.clone());
			}
		}
		
		if (nDirection == INBOUND && sAction.equals(ALLOW)) {
			reportData.put("OPT16_1", new SynthesisDataSource(dataSource1));
			reportData.put("OPT16_2", new SynthesisDataSource(dataSource2));
		} else if (nDirection == INBOUND && sAction.equals(CUTOFF)) {
			reportData.put("OPT17_1", new SynthesisDataSource(dataSource1));
			reportData.put("OPT17_2", new SynthesisDataSource(dataSource2));
		} else if (nDirection == OUTBOUND && sAction.equals(ALLOW)) {
			reportData.put("OPT19_1", new SynthesisDataSource(dataSource1));
			reportData.put("OPT19_2", new SynthesisDataSource(dataSource2));
		} else if (nDirection == OUTBOUND && sAction.equals(CUTOFF)) {
			reportData.put("OPT20_1", new SynthesisDataSource(dataSource1));
			reportData.put("OPT20_2", new SynthesisDataSource(dataSource2));
		}
		
		if (bChk) {
			List<HashMap<String, Object>> dataSource3 = new ArrayList<HashMap<String, Object>>();
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
					map.put("series", fwDao.getPortServiceName(nPort));
		    		map.put("category", tDay.substring(6, 8));
		    		map.put("value", val != null ? ((Number)val.get("count")).longValue() : 0);
		    		dataSource3.add(map);
					logger.debug(map.toString());
        		}
        	}
    		
	    	if (nDirection == INBOUND) {
	    		reportData.put("OPT17_3", new SynthesisDataSource(dataSource3));
	    	} else if (nDirection == OUTBOUND) {
	    		reportData.put("OPT20_3", new SynthesisDataSource(dataSource3));
	    	}
        	
		}
	}
	
	private void Action_ServiceTop10_Detail(
			HashMap<String, Object> reportData, 
			int nDirection, 
			String sAction, 
			int assetCode, 
			String sStartDay, 
			String sEndDay) throws Exception {
		
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();

		Iterable<DBObject> logResult = fwDao.ServiceSessionLog("DY", nDirection, sAction, assetCode, sStartDay, sEndDay);   	
		
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
    		
    		Iterable<DBObject> svcCondition = fwDao.ServiceCondition("DY", nDirection, sAction, assetCode, sStartDay, sEndDay, nPort);
    		
    		for (DBObject dbObj : svcCondition) {
    			isExist = true;
    			HashMap<String, Object> map = new HashMap<String, Object>();
    			map.put("no", nNo);
    			map.put("port", nPort);
    			map.put("svcName", fwDao.getPortServiceName(nPort));
    			map.put("count", totalCount);
    			map.put("srcIp", (String)dbObj.get("srcIp"));
    			map.put("destIp", (String)dbObj.get("destIp"));
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
    			map.put("svcName", fwDao.getPortServiceName(nPort));
    			map.put("count", totalCount);
    			map.put("srcIp", "-");
    			map.put("destIp", "-");
    			map.put("ratio", 0l);
    			map.put("geoCode", "-");
    			
    			dataSource.add(map);
    			logger.debug(map.toString());
    		}
    		nNo++;
    	}
    	
		if (nDirection == INBOUND && sAction.equals(CUTOFF)) {
			reportData.put("OPT18_1", new SynthesisDataSource(dataSource));
		} else if (nDirection == OUTBOUND && sAction.equals(CUTOFF)) {
			reportData.put("OPT21_1", new SynthesisDataSource(dataSource));
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
