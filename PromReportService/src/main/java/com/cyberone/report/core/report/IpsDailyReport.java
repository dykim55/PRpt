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
import org.springframework.web.socket.TextMessage;

import com.cyberone.report.Constants;
import com.cyberone.report.core.dao.IpsDao;
import com.cyberone.report.core.datasource.SynthesisDataSource;
import com.cyberone.report.model.UserInfo;
import com.cyberone.report.utils.StringUtil;
import com.mongodb.DBObject;

public class IpsDailyReport extends BaseReport {
	
	private IpsDao ipsDao;

	private UserInfo userInfo;
	
	public IpsDailyReport(UserInfo userInfo) {
		this.userInfo = userInfo;
		this.ipsDao = new IpsDao(userInfo.getPromDb(), userInfo.getReportDb());
	}
	
	/* 
	 * 침입방지(IPS) 일일보고서 통계데이타 생성
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getDataSource(String sReportType, String sSearchDate, HashMap<String, Object> hMap, int ItemNo, List<String> contentsList) throws Exception {
		logger.debug("침입방지(IPS) 일일보고서 통계데이타 생성");
		
		HashMap<String, Object> reportData = new HashMap<String, Object>();
		
		reportData.put("reportType", sReportType);
		
		contentsList.add(ItemNo + ". " + (String)hMap.get("assetName") + " 장비의 일간 IPS 탐지로그 분석");
		reportData.put("RT", ItemNo + ". " + (String)hMap.get("assetName") + " 장비의 일간 IPS 탐지로그 분석");
		
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
			DBObject dbObj = ipsDao.selectAutoReportForm(Integer.parseInt(sReportType), assetCode);
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
				case "opt01" : case "opt02" : case "opt03" : case "opt04" : case "opt05" : case "opt06" : case "opt07" : 
					if (nC1 == 0) { 
						contentsList.add("  " + ItemNo + "." + ++nC1 + " 일간 탐지 추이");
						reportData.put("C1", "  " + ItemNo + "." + nC1 + " 일간 탐지 추이");
					}
					break;
				//이벤트 현황
				case "opt08" : case "opt09" : case "opt10" : case "opt11" : case "opt12" : case "opt13" : case "opt14" : case "opt15" : case "opt16" : 
					if (nC2 == 0) { 
						contentsList.add("  " + ItemNo + "." + (++nC2 + nC1) + " 일간 탐지 현황");
						reportData.put("C2", "  " + ItemNo + "." + (nC2 + nC1) + " 일간 탐지 현황");
					}
					break;
				//출발지IP 현황
				case "opt17" : case "opt18" : case "opt19" : case "opt20" : case "opt21" : case "opt22" : 
					if (nC3 == 0) { 
						contentsList.add("  " + ItemNo + "." + (++nC3 + nC2 + nC1) + " 일간 출발지 현황");
						reportData.put("C3", "  " + ItemNo + "." + (nC3 + nC2 + nC1) + " 일간 출발지 현황");
					}
					break;
				//목적지IP 현황
				case "opt23" : case "opt24" : case "opt25" : case "opt26" : case "opt27" : case "opt28" : 
					if (nC4 == 0) { 
						contentsList.add("  " + ItemNo + "." + (++nC4 + nC3 + nC2 + nC1) + " 일간 목적지 현황");
						reportData.put("C4", "  " + ItemNo + "." + (nC4 + nC3 + nC2 + nC1) + " 일간 목적지 현황");
					}
					break;
				//서비스 현황
				case "opt29" : case "opt30" : 
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
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd1")));
					All_DetectLog_Trend(reportData, -1, assetCode, sStartDay, sEndDay, nChoice);
					break;
				case "opt02" :	//외부에서 내부로의 전체 탐지로그 발생추이 
					reportData.put("opt02", push(contentsList, "    ", ItemNo, nC1, nS1++, " 외부에서 내부로의 전체 탐지로그 발생추이"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd2")));
					Direction_DetectLog_Trend(reportData, INBOUND, assetCode, sStartDay, sEndDay, nChoice);
					break;
				case "opt03" : 	//외부에서 내부로의 허용 탐지로그 발생추이
					reportData.put("opt03", push(contentsList, "    ", ItemNo, nC1, nS1++, " 외부에서 내부로의 허용 탐지로그 발생추이"));
					Compare_DetectLog_Trend(reportData, INBOUND, ALLOW, assetCode, sStartDay, sEndDay);
					break;
				case "opt04" :	//외부에서 내부로의 차단 탐지로그 발생추이
					reportData.put("opt04", push(contentsList, "    ", ItemNo, nC1, nS1++, " 외부에서 내부로의 차단 탐지로그 발생추이"));
					Compare_DetectLog_Trend(reportData, INBOUND, CUTOFF, assetCode, sStartDay, sEndDay);
					break;
				case "opt05" : 	//내부에서 외부로의 전체 탐지로그 발생추이
					reportData.put("opt05", push(contentsList, "    ", ItemNo, nC1, nS1++, " 내부에서 외부로의 전체 탐지로그 발생추이"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd5")));
					Direction_DetectLog_Trend(reportData, OUTBOUND, assetCode, sStartDay, sEndDay, nChoice);
					break;
				case "opt06" : 	//내부에서 외부로의 허용 탐지로그 발생추이
					reportData.put("opt06", push(contentsList, "    ", ItemNo, nC1, nS1++, " 내부에서 외부로의 허용 탐지로그 발생추이"));
					Compare_DetectLog_Trend(reportData, OUTBOUND, ALLOW, assetCode, sStartDay, sEndDay);
					break;
				case "opt07" : 	//내부에서 외부로의 차단 탐지로그 발생추이
					reportData.put("opt07", push(contentsList, "    ", ItemNo, nC1, nS1++, " 내부에서 외부로의 차단 탐지로그 발생추이"));
					Compare_DetectLog_Trend(reportData, OUTBOUND, CUTOFF, assetCode, sStartDay, sEndDay);
					break;
				case "opt08" : 	//외부에서 내부로의 전체 탐지로그 & 이벤트 TOP
					reportData.put("opt08", push(contentsList, "    ", ItemNo, nC2 + nC1, nS2++, " 외부에서 내부로의 전체 탐지로그에 대한 탐지룰 TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd8")));
					Direction_EventTopN(reportData, INBOUND, assetCode, sStartDay, sEndDay, nChoice);
					break;
				case "opt09" : 	//외부에서 내부로의 허용 탐지로그 & 이벤트 TOP
					reportData.put("opt09", push(contentsList, "    ", ItemNo, nC2 + nC1, nS2++, " 외부에서 내부로의 허용 탐지로그에 대한 탐지룰 TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd9")));
					Direction_Action_EventTopN(reportData, INBOUND, ALLOW, assetCode, sStartDay, sEndDay, nChoice, !StringUtil.isEmpty(hData.get("ck9")));
					break;
				case "opt10" : 	//외부에서 내부로의 차단 탐지로그 & 이벤트 TOP
					reportData.put("opt10", push(contentsList, "    ", ItemNo, nC2 + nC1, nS2++, " 외부에서 내부로의 차단 탐지로그에 대한 탐지룰 TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd10")));
					Direction_Action_EventTopN(reportData, INBOUND, CUTOFF, assetCode, sStartDay, sEndDay, nChoice, !StringUtil.isEmpty(hData.get("ck10")));
					break;
				case "opt11" : 	//내부에서 외부로의 전체 탐지로그 & 이벤트 TOP
					reportData.put("opt11", push(contentsList, "    ", ItemNo, nC2 + nC1, nS2++, " 내부에서 외부로의 전체 탐지로그에 대한 탐지룰 TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd11")));
					Direction_EventTopN(reportData, OUTBOUND, assetCode, sStartDay, sEndDay, nChoice);
					break;
				case "opt12" : 	//내부에서 외부로의 허용 탐지로그 & 이벤트 TOP
					reportData.put("opt12", push(contentsList, "    ", ItemNo, nC2 + nC1, nS2++, " 내부에서 외부로의 허용 탐지로그에 대한 탐지룰 TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd12")));
					Direction_Action_EventTopN(reportData, OUTBOUND, ALLOW, assetCode, sStartDay, sEndDay, nChoice, !StringUtil.isEmpty(hData.get("ck12")));
					break;
				case "opt13" : 	//내부에서 외부로의 차단 탐지로그 & 이벤트 TOP
					reportData.put("opt13", push(contentsList, "    ", ItemNo, nC2 + nC1, nS2++, " 내부에서 외부로의 차단 탐지로그에 대한 탐지룰 TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd13")));
					Direction_Action_EventTopN(reportData, OUTBOUND, CUTOFF, assetCode, sStartDay, sEndDay, nChoice, !StringUtil.isEmpty(hData.get("ck13")));
					break;
				case "opt14" : 	//외부에서 내부로의 전체 탐지로그 & 신규 탐지 이벤트 현황
					reportData.put("opt14", push(contentsList, "    ", ItemNo, nC2 + nC1, nS2++, " 외부에서 내부로의 신규 탐지룰 현황"));
					New_DetectLog_Condition(reportData, INBOUND, assetCode, sStartDay, sEndDay);
					break;
				case "opt15" : 	//내부에서 외부로의 전체 탐지로그 & 신규 탐지 이벤트 현황
					reportData.put("opt15", push(contentsList, "    ", ItemNo, nC2 + nC1, nS2++, " 내부에서 외부로의 신규 탐지룰 현황"));
					New_DetectLog_Condition(reportData, OUTBOUND, assetCode, sStartDay, sEndDay);
					break;
				case "opt16" : 	//외부에서 내부로의 전체 탐지로그 & 이전대비 2배증가된 이벤트 현황
					reportData.put("opt16", push(contentsList, "    ", ItemNo, nC2 + nC1, nS2++, " 외부에서 내부로의 2배 증가된 이벤트 현황"));
					Double_DetectLog_Condition(reportData, INBOUND, assetCode, sStartDay, sEndDay);
					break;
				case "opt17" : 	//외부에서 내부로의 전체 탐지로그 & SIP TOP
					reportData.put("opt17", push(contentsList, "    ", ItemNo, nC3 + nC2 + nC1, nS3++, " 외부에서 내부로의 전체 탐지로그에 대한 출발지IP TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd17")));
					Action_DetectLog_TopN(reportData, INBOUND, "", assetCode, sStartDay, sEndDay, true, nChoice, !StringUtil.isEmpty(hData.get("ck17")));
					break;
				case "opt18" : 	//외부에서 내부로의 허용 탐지로그 & SIP TOP
					reportData.put("opt18", push(contentsList, "    ", ItemNo, nC3 + nC2 + nC1, nS3++, " 외부에서 내부로의 허용 탐지로그에 대한 출발지IP TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd18")));
					Action_DetectLog_TopN(reportData, INBOUND, ALLOW, assetCode, sStartDay, sEndDay, true, nChoice, !StringUtil.isEmpty(hData.get("ck18")));
					break;
				case "opt19" : 	//외부에서 내부로의 차단 탐지로그 & SIP TOP
					reportData.put("opt19", push(contentsList, "    ", ItemNo, nC3 + nC2 + nC1, nS3++, " 외부에서 내부로의 차단 탐지로그에 대한 출발지IP TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd19")));
					Action_DetectLog_TopN(reportData, INBOUND, CUTOFF, assetCode, sStartDay, sEndDay, true, nChoice, !StringUtil.isEmpty(hData.get("ck19")));
					break;
				case "opt20" : 	//내부에서 외부로의 전체 탐지로그 & SIP TOP
					reportData.put("opt20", push(contentsList, "    ", ItemNo, nC3 + nC2 + nC1, nS3++, " 내부에서 외부로의 전체 탐지로그에 대한 출발지IP TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd20")));
					Action_DetectLog_TopN(reportData, OUTBOUND, "", assetCode, sStartDay, sEndDay, true, nChoice, !StringUtil.isEmpty(hData.get("ck20")));
					break;
				case "opt21" : 	//내부에서 외부로의 허용 탐지로그 & SIP TOP
					reportData.put("opt21", push(contentsList, "    ", ItemNo, nC3 + nC2 + nC1, nS3++, " 내부에서 외부로의 허용 탐지로그에 대한 출발지IP TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd21")));
					Action_DetectLog_TopN(reportData, OUTBOUND, ALLOW, assetCode, sStartDay, sEndDay, true, nChoice, !StringUtil.isEmpty(hData.get("ck21")));
					break;
				case "opt22" : 	//내부에서 외부로의 차단 탐지로그 & SIP TOP
					reportData.put("opt22", push(contentsList, "    ", ItemNo, nC3 + nC2 + nC1, nS3++, " 내부에서 외부로의 차단 탐지로그에 대한 출발지IP TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd22")));
					Action_DetectLog_TopN(reportData, OUTBOUND, CUTOFF, assetCode, sStartDay, sEndDay, true, nChoice, !StringUtil.isEmpty(hData.get("ck22")));
					break;
				case "opt23" : 	//외부에서 내부로의 전체 탐지로그 & DIP TOP
					reportData.put("opt23", push(contentsList, "    ", ItemNo, nC4 + nC3 + nC2 + nC1, nS4++, " 외부에서 내부로의 전체 탐지로그에 대한 목적지IP TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd23")));
					Action_DetectLog_TopN(reportData, INBOUND, "", assetCode, sStartDay, sEndDay, false, nChoice, false);
					break;
				case "opt24" : 	//외부에서 내부로의 허용 탐지로그 & DIP TOP
					reportData.put("opt24", push(contentsList, "    ", ItemNo, nC4 + nC3 + nC2 + nC1, nS4++, " 외부에서 내부로의 허용 탐지로그에 대한 목적지IP TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd24")));
					Action_DetectLog_TopN(reportData, INBOUND, ALLOW, assetCode, sStartDay, sEndDay, false, nChoice, false);
					break;
				case "opt25" : 	//외부에서 내부로의 차단 탐지로그 & DIP TOP
					reportData.put("opt25", push(contentsList, "    ", ItemNo, nC4 + nC3 + nC2 + nC1, nS4++, " 외부에서 내부로의 차단 탐지로그에 대한 목적지IP TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd25")));
					Action_DetectLog_TopN(reportData, INBOUND, CUTOFF, assetCode, sStartDay, sEndDay, false, nChoice, false);
					break;
				case "opt26" : 	//내부에서 외부로의 전체 탐지로그 & DIP TOP
					reportData.put("opt26", push(contentsList, "    ", ItemNo, nC4 + nC3 + nC2 + nC1, nS4++, " 내부에서 외부로의 전체 탐지로그에 대한 목적지IP TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd26")));
					Action_DetectLog_TopN(reportData, OUTBOUND, "", assetCode, sStartDay, sEndDay, false, nChoice, false);
					break;
				case "opt27" : 	//내부에서 외부로의 허용 탐지로그 & DIP TOP
					reportData.put("opt27", push(contentsList, "    ", ItemNo, nC4 + nC3 + nC2 + nC1, nS4++, " 내부에서 외부로의 허용 탐지로그에 대한 목적지IP TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd27")));
					Action_DetectLog_TopN(reportData, OUTBOUND, ALLOW, assetCode, sStartDay, sEndDay, false, nChoice, false);
					break;
				case "opt28" : 	//내부에서 외부로의 차단 탐지로그 & DIP TOP
					reportData.put("opt28", push(contentsList, "    ", ItemNo, nC4 + nC3 + nC2 + nC1, nS4++, " 내부에서 외부로의 차단 탐지로그에 대한 목적지IP TOP 현황"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd28")));
					Action_DetectLog_TopN(reportData, OUTBOUND, CUTOFF, assetCode, sStartDay, sEndDay, false, nChoice, false);
					break;
				case "opt29" : 	//외부에서 내부로의 전체 탐지로그 & 서비스 TOP10
					reportData.put("opt29", push(contentsList, "    ", ItemNo, nC5 + nC4 + nC3 + nC2 + nC1, nS5++, " 외부에서 내부로의 전체 탐지로그에 대한 서비스 TOP 현황"));
					ALL_ServiceTop10(reportData, INBOUND, assetCode, sStartDay, sEndDay, !StringUtil.isEmpty(hData.get("ck29")));
					break;
				case "opt30" : 	//내부에서 외부로의 전체 탐지로그 & 서비스 TOP10
					reportData.put("opt30", push(contentsList, "    ", ItemNo, nC5 + nC4 + nC3 + nC2 + nC1, nS5++, " 내부에서 외부로의 전체 탐지로그에 대한 서비스 TOP 현황"));
					ALL_ServiceTop10(reportData, OUTBOUND, assetCode, sStartDay, sEndDay, !StringUtil.isEmpty(hData.get("ck30")));
					break;
				case "opt99" :	//성능정보
					//reportData.put("opt99", push(contentsList, "    ", ItemNo, nC6 + nC5 + nC4 + nC3 + nC2 + nC1, nS6++, " 성능 정보"));
					PerformanceInfo(ipsDao, reportData, contentsList, ItemNo, nC6 + nC5 + nC4 + nC3 + nC2 + nC1, assetCode, sStartDay, sEndDay);
					break;
			}
		}
		return reportData;
	}

	private void All_DetectLog_Trend(HashMap<String, Object> reportData, int nDirection, int assetCode, String sStartDay, String sEndDay, int nChoice) throws Exception {
		
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();

		if (nChoice == 1) {	//해당일
			
			List<String> hourDay = ipsDao.getPeriodHour(sStartDay, 0);
			
			Iterable<DBObject> dbResult = ipsDao.AllDetectLogTrend("HR", -1, assetCode, sStartDay, sEndDay);
			
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
    	    		HashMap<String, Object> map = new HashMap<String, Object>();
		    		DBObject val = mapResult.get(tDay + (h < 10 ? "0" + h : String.valueOf(h)));
		    		map.put("series", "전체");
        			map.put("category", String.valueOf(h));
    	    		map.put("value", val != null ? ((Number)val.get("count")).longValue() : 0);
		    		
    	    		dataSource.add(map);
    	    		logger.debug(map.toString());
    			}
	    	}
			
			dbResult = ipsDao.AllDetectLogTrend("HR", 1, assetCode, sStartDay, sEndDay);
		
	    	mapResult = new HashMap<String, DBObject>(); 
	    	for (DBObject val : dbResult) {
	    		String sDirection = StringUtil.convertString(val.get("direction"));
	    		String sYear = ((Integer)val.get("year")).toString();
	    		String sMonth = ((Integer)val.get("month")).toString();
	    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
	    		String sDay = ((Integer)val.get("day")).toString();
	    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
	    		String sHour = ((Integer)val.get("hour")).toString();
	    		sHour = sHour.length() == 1 ? "0" + sHour : sHour;
	    		mapResult.put(sDirection + sYear + sMonth + sDay + sHour, val);
	    	}
	    	
	    	String[] saGubun = {"내부 → 외부 로그", "외부 → 내부 로그"};
    		for (int direction = 1; direction >= 0; direction--) { //아웃바운드/인바운드
    			for (String tDay : hourDay) {
	    			for (int h = 0; h < 24; h++) { //시간
	    	    		HashMap<String, Object> map = new HashMap<String, Object>();
			    		DBObject val = mapResult.get(String.valueOf(direction) + tDay + (h < 10 ? "0" + h : String.valueOf(h)));
			    		map.put("series", saGubun[direction]);
	        			map.put("category", String.valueOf(h));
	    	    		map.put("value", val != null ? ((Number)val.get("count")).longValue() : 0);
			    		
	    	    		dataSource.add(map);
	    	    		logger.debug(map.toString());
	    			}
    			}
    		}
	    	
    		
		} else { //최근3일

			List<String> dayPeriod = ipsDao.getPeriodDay(ipsDao.addDate(sStartDay,-2), sEndDay, 1);
			
			Iterable<DBObject> dbResult = ipsDao.AllDetectLogTrend("DY", -1, assetCode, ipsDao.addDate(sStartDay,-2), sEndDay);
			
			HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
	    	for (DBObject val : dbResult) {
	    		String sYear = ((Integer)val.get("year")).toString();
	    		String sMonth = ((Integer)val.get("month")).toString();
	    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
	    		String sDay = ((Integer)val.get("day")).toString();
	    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
	    		mapResult.put(sYear + sMonth + sDay, val);
	    	}
	    	
	    	String[] saCtg = {"D-2", "D-1", "D"};
	    	int nCtg = 0;
    		for (String tDay : dayPeriod) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		DBObject val = mapResult.get(tDay);
	    		map.put("series", "전체");
	    		map.put("gubun", "전체");
	    		map.put("category", saCtg[nCtg++]);
	    		map.put("value", val != null ? ((Number)val.get("count")).longValue() : 0);
	    		
	    		dataSource.add(map);
	    		logger.debug(map.toString());
			}
			
			dbResult = ipsDao.AllDetectLogTrend("DY", 1, assetCode, ipsDao.addDate(sStartDay,-2), sEndDay);

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
	    	
	    	String[] saGubun = {"내부 → 외부 로그", "외부 → 내부 로그"};
	    	String[] saGubun2 = {"내부에서 외부로의 발생", "외부에서 내부로의 발생"};
	    	for (int direction = 1; direction >= 0; direction--) { //아웃바운드/인바운드
	    		nCtg = 0;
	    		for (String tDay : dayPeriod) {
		    		HashMap<String, Object> map = new HashMap<String, Object>();
		    		DBObject val = mapResult.get(String.valueOf(direction) + tDay);
		    		map.put("series", saGubun[direction]);
		    		map.put("gubun", saGubun2[direction]);
		    		map.put("category", saCtg[nCtg++]);
		    		map.put("value", val != null ? ((Number)val.get("count")).longValue() : 0);

    	    		dataSource.add(map);
    	    		logger.debug(map.toString());
	    		}	    		
	    	}
	    	
		}
		
		if (nChoice == 1) {			//해당일
			reportData.put("OPT1_0", "시간");
			reportData.put("OPT1_1", new SynthesisDataSource(dataSource));
		} else if (nChoice == 2) {	//최근3일
			reportData.put("OPT1_0", "일");
			reportData.put("OPT1_1", new SynthesisDataSource(dataSource));
			reportData.put("OPT1_2", new SynthesisDataSource(dataSource));
		}
	}
	
	private void Direction_DetectLog_Trend(HashMap<String, Object> reportData, int nDirection, int assetCode, String sStartDay, String sEndDay, int nChoice) throws Exception {
		
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();
		
		if (nChoice == 1) {	//해당일
			
			List<String> hourDay = ipsDao.getPeriodHour(sStartDay, 0);
			
			Iterable<DBObject> dbResult = ipsDao.AllActionTrend("HR", nDirection, "", assetCode, sStartDay, sEndDay);
    		
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
    	    		HashMap<String, Object> map = new HashMap<String, Object>();
		    		DBObject val = mapResult.get(tDay + (h < 10 ? "0" + h : String.valueOf(h)));
		    		map.put("series", "전체");
        			map.put("category", String.valueOf(h));
    	    		map.put("value", val != null ? ((Number)val.get("count")).longValue() : 0);
		    		
    	    		dataSource.add(map);
    	    		logger.debug(map.toString());
    			}
	    	}
			
			dbResult = ipsDao.AllActionTrend("HR", nDirection, ALLOW, assetCode, sStartDay, sEndDay);

	    	mapResult = new HashMap<String, DBObject>(); 
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
			
	    	String[] saGubun = {"차단", "허용"};
    		for (int action = 1; action >= 0; action--) { //차단/허용
    			for (String tDay : hourDay) {
	    			for (int h = 0; h < 24; h++) { //시간
	    	    		HashMap<String, Object> map = new HashMap<String, Object>();
			    		DBObject val = mapResult.get(String.valueOf(action) + tDay + (h < 10 ? "0" + h : String.valueOf(h)));
			    		map.put("series", saGubun[action]);
	        			map.put("category", String.valueOf(h));
	    	    		map.put("value", val != null ? ((Number)val.get("count")).longValue() : 0);
			    		
	    	    		dataSource.add(map);
	    	    		logger.debug(map.toString());
	    			}
    			}
    		}

		} else {
			
			List<String> dayPeriod = ipsDao.getPeriodDay(ipsDao.addDate(sStartDay,-2), sEndDay, 1);
			
			Iterable<DBObject> dbResult = ipsDao.AllActionTrend("DY", nDirection, "", assetCode, ipsDao.addDate(sStartDay,-2), sEndDay);
	    	
			HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
	    	for (DBObject val : dbResult) {
	    		String sYear = ((Integer)val.get("year")).toString();
	    		String sMonth = ((Integer)val.get("month")).toString();
	    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
	    		String sDay = ((Integer)val.get("day")).toString();
	    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
	    		mapResult.put(sYear + sMonth + sDay, val);
	    	}
	    	
	    	String[] saCtg = {"D-2", "D-1", "D"};
	    	int nCtg = 0;
    		for (String tDay : dayPeriod) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		DBObject val = mapResult.get(tDay);
	    		map.put("series", "전체");
	    		map.put("category", saCtg[nCtg++]);
	    		map.put("value", val != null ? ((Number)val.get("count")).longValue() : 0);
	    		
	    		dataSource.add(map);
	    		logger.debug(map.toString());
			}
			
			dbResult = ipsDao.AllActionTrend("DY", nDirection, ALLOW, assetCode, ipsDao.addDate(sStartDay,-2), sEndDay);

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
	    		nCtg = 0;
	    		for (String tDay : dayPeriod) {
		    		HashMap<String, Object> map = new HashMap<String, Object>();
		    		DBObject val = mapResult.get(String.valueOf(action) + tDay);
		    		map.put("series", saGubun[action]);
		    		map.put("category", saCtg[nCtg++]);
		    		map.put("value", val != null ? ((Number)val.get("count")).longValue() : 0);

    	    		dataSource.add(map);
    	    		logger.debug(map.toString());
	    		}	    		
	    	}
	    	
		}
		
		if (nDirection == 1) {	//INBOUND
			if (nChoice == 1) {			//해당일
				reportData.put("OPT2_0", "시간");
				reportData.put("OPT2_1", new SynthesisDataSource(dataSource));
			} else if (nChoice == 2) {	//최근3일
				reportData.put("OPT2_0", "일");
				reportData.put("OPT2_1", new SynthesisDataSource(dataSource));
				reportData.put("OPT2_2", new SynthesisDataSource(dataSource));
			}
		} else {
			if (nChoice == 1) {			//해당일
				reportData.put("OPT5_0", "시간");
				reportData.put("OPT5_1", new SynthesisDataSource(dataSource));
			} else if (nChoice == 2) {	//최근3일
				reportData.put("OPT5_0", "일");
				reportData.put("OPT5_1", new SynthesisDataSource(dataSource));
				reportData.put("OPT5_2", new SynthesisDataSource(dataSource));
			}
		}
	}

	private void Compare_DetectLog_Trend(HashMap<String, Object> reportData, int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();

		Iterable<DBObject> dbResult = ipsDao.CompareDetectLogTrend("HR", nDirection, sAction, assetCode, ipsDao.addDate(sStartDay,-1), sEndDay);

    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
    	for (DBObject val : dbResult) {
    		String sYear = ((Integer)val.get("year")).toString();
    		String sMonth = ((Integer)val.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    		String sDay = ((Integer)val.get("day")).toString();
    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
    		String sHour = ((Integer)val.get("hour")).toString();
    		sHour = sHour.length() == 1 ? "0" + sHour : sHour;
    		mapResult.put(sAction + sYear + sMonth + sDay + sHour, val);
    	}
    	
    	List<String> hourDay = ipsDao.getPeriodHour(sStartDay, -1);
    	
		int nDayCount = 0;
		for (String tDay : hourDay) {
			nDayCount++;
			for (int h = 0; h < 24; h++) { //시간
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		DBObject val = mapResult.get(sAction + tDay + (h < 10 ? "0" + h : String.valueOf(h)));
    			map.put("series", (nDayCount < 2) ? "전일" : "금일");
    			map.put("category", String.valueOf(h));
	    		map.put("value", val != null ? ((Number)val.get("count")).longValue() : 0);
	    		
	    		dataSource.add(map);
	    		logger.debug(map.toString());
			}
		}

		if (nDirection == INBOUND) {
			if (sAction.equals(ALLOW)) {
				reportData.put("OPT3_0", "시간");
				reportData.put("OPT3_1", new SynthesisDataSource(dataSource));
			} else {
				reportData.put("OPT4_0", "시간");
				reportData.put("OPT4_1", new SynthesisDataSource(dataSource));
			}
		} else {
			if (sAction.equals(ALLOW)) {
				reportData.put("OPT6_0", "시간");
				reportData.put("OPT6_1", new SynthesisDataSource(dataSource));
			} else {
				reportData.put("OPT7_0", "시간");
				reportData.put("OPT7_1", new SynthesisDataSource(dataSource));
			}
		}
		
	}
	
	//외부에서 내부로의 전체 탐지로그 & 이벤트 TOP
	private void Direction_EventTopN(HashMap<String, Object> reportData, int nDirection, int assetCode, String sStartDay, String sEndDay, int nLimit) throws Exception {
		
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();

		Iterable<DBObject> dbResult = ipsDao.EventTopN("DY", nDirection, "", assetCode, sStartDay, sEndDay, 0);

		long lTotal = 0;
    	for (DBObject val : dbResult) {
    		lTotal += ((Number)val.get("count")).longValue();
    	}

    	int nNo = 1;
    	for (DBObject val : dbResult) {
    		String sMessage = (String)val.get("message");
    		String sAction = (String)val.get("action");
    		if (!sMessage.equals("-1") && nNo <= 10) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		map.put("message", (sAction.equals(ALLOW) ? "[허용]" : "[차단]") + sMessage);
	    		map.put("count", ((Number)val.get("count")).longValue());
	    		map.put("ratio", (((Number)val.get("count")).longValue()*100f)/lTotal);
	    		
	    		dataSource1.add(map);
	    		logger.debug(map.toString());
    		}
    		if (nNo >= 10) break;
    		nNo++;
    	}
    	
    	Iterable<DBObject> dbBefore = ipsDao.EventTopN("DY", nDirection, "", assetCode, ipsDao.addDate(sStartDay,-1), ipsDao.addDate(sEndDay,-1), 0);
    	HashMap<String, DBObject> mapBefore = new HashMap<String, DBObject>(); 
    	for (DBObject val : dbBefore) {
    		mapBefore.put((String)val.get("action") + "-" + (String)val.get("message"), val);
    	}
    	
    	nNo = 1;
    	for (DBObject val : dbResult) {
    		String sMessage = (String)val.get("message");
    		String sAction = (String)val.get("action");
    		
    		DBObject prevMap = mapBefore.get(sAction + "-" + sMessage);
    		
    		Iterable<DBObject> srcIpTop5 = ipsDao.EventSrcIpTopN("DY", nDirection, sAction, assetCode, sStartDay, sEndDay, sMessage, 5);
    		for (DBObject sVal : srcIpTop5) {
    			HashMap<String, Object> map = new HashMap<String, Object>();
    			map.put("no", nNo);
    			map.put("message", sMessage);
    			map.put("action", sAction.equals("1") ? "허용" : "차단");
    			map.put("count", ((Number)val.get("count")).longValue());
    			map.put("countVariation", ((Number)val.get("count")).longValue() - (prevMap != null ? ((Number)prevMap.get("count")).longValue() : 0));
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
	    	reportData.put("OPT8_1", new SynthesisDataSource(dataSource1));
	    	reportData.put("OPT8_2", new SynthesisDataSource(dataSource2));
    	} else {
	    	reportData.put("OPT11_1", new SynthesisDataSource(dataSource1));
	    	reportData.put("OPT11_2", new SynthesisDataSource(dataSource2));
    	}
		
	}		
	
	//외부에서 내부로의 허용/차단 탐지로그 & 이벤트 TOP
	private void Direction_Action_EventTopN(HashMap<String, Object> reportData, int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay, int nLimit, boolean bChk) throws Exception {
		
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> dataSource3 = new ArrayList<HashMap<String, Object>>();
		
		Iterable<DBObject> dbResult = ipsDao.EventTopN("DY", nDirection, sAction, assetCode, sStartDay, sEndDay, 0);

		long lTotal = 0;
    	for (DBObject val : dbResult) {
    		lTotal += ((Number)val.get("count")).longValue();
    	}

    	int nNo = 1;
    	List<String> saTopN = new ArrayList<String>();
    	for (DBObject val : dbResult) {
    		String sMessage = (String)val.get("message");
    		if (!sMessage.equals("-1") && nNo <= 10) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		map.put("message", sMessage);
	    		map.put("count", ((Number)val.get("count")).longValue());
	    		map.put("ratio", (((Number)val.get("count")).longValue()*100f)/lTotal);

	    		dataSource1.add(map);
	    		logger.debug(map.toString());
	    		
	    		saTopN.add((String)val.get("message"));
    		}
    		if (nNo >= 10) break;
    		nNo++;
    	}
    	
    	Iterable<DBObject> dbBefore = ipsDao.EventTopN("DY", nDirection, sAction, assetCode, ipsDao.addDate(sStartDay,-1), ipsDao.addDate(sEndDay,-1), 0);
    	HashMap<String, DBObject> mapBefore = new HashMap<String, DBObject>(); 
    	for (DBObject val : dbBefore) {
    		mapBefore.put((String)val.get("action") + "-" + (String)val.get("message"), val);
    	}
    	
    	nNo = 1;
    	for (DBObject val : dbResult) {
    		String sMessage = (String)val.get("message");
    		
    		DBObject prevMap = mapBefore.get(sAction + "-" + sMessage);
    		
    		Iterable<DBObject> srcIpTop5 = ipsDao.EventSrcIpTopN("DY", nDirection, sAction, assetCode, sStartDay, sEndDay, sMessage, 5);
    		for (DBObject sVal : srcIpTop5) {
    			HashMap<String, Object> map = new HashMap<String, Object>();
    			map.put("no", nNo);
    			map.put("message", sMessage);
    			map.put("action", sAction.equals("1") ? "허용" : "차단");
    			map.put("count", ((Number)val.get("count")).longValue());
    			map.put("countVariation", ((Number)val.get("count")).longValue() - (prevMap != null ? ((Number)prevMap.get("count")).longValue() : 0));
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
    		if (sAction.equals(ALLOW)) {
		    	reportData.put("OPT9_1", new SynthesisDataSource(dataSource1));
		    	reportData.put("OPT9_2", new SynthesisDataSource(dataSource2));
    		} else {
		    	reportData.put("OPT10_1", new SynthesisDataSource(dataSource1));
		    	reportData.put("OPT10_2", new SynthesisDataSource(dataSource2));
    		}
    	} else {
    		if (sAction.equals(ALLOW)) {
		    	reportData.put("OPT12_1", new SynthesisDataSource(dataSource1));
		    	reportData.put("OPT12_2", new SynthesisDataSource(dataSource2));
    		} else {
		    	reportData.put("OPT13_1", new SynthesisDataSource(dataSource1));
		    	reportData.put("OPT13_2", new SynthesisDataSource(dataSource2));
    		}
    	}
    	
    	if (bChk) { //Event Top 발생추이
    		
	    	dbResult = ipsDao.EventTopNTrend("HR", nDirection, sAction, assetCode, sStartDay, sEndDay, saTopN);
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
    		
	    	List<String> hourDay = ipsDao.getPeriodHour(sStartDay, 0);
	    	
	    	for (String sMessage : saTopN) {
	    		for (String tDay : hourDay) {
	    			for (int h = 0; h < 24; h++) { //시간
			    		DBObject val = mapResult.get(sMessage + "-" + tDay + (h < 10 ? "0" + h : String.valueOf(h)));
			    		HashMap<String, Object> map = new HashMap<String, Object>();
			    		map.put("series", sMessage);
			    		map.put("category", String.valueOf(h));
			    		map.put("value", val != null ? ((Number)val.get("count")).longValue() : 0);
			    		
			    		dataSource3.add(map);
			    		logger.debug(map.toString());
	    			}
		    	}
	    	}
    		
	    	if (nDirection == INBOUND) {
	    		if (sAction.equals(ALLOW)) {
	    			reportData.put("OPT9_0", "시간");
			    	reportData.put("OPT9_3", new SynthesisDataSource(dataSource3));
	    		} else {
	    			reportData.put("OPT10_0", "시간");
			    	reportData.put("OPT10_3", new SynthesisDataSource(dataSource3));
	    		}
	    	} else {
	    		if (sAction.equals(ALLOW)) {
	    			reportData.put("OPT12_0", "시간");
			    	reportData.put("OPT12_3", new SynthesisDataSource(dataSource3));
	    		} else {
	    			reportData.put("OPT13_0", "시간");
			    	reportData.put("OPT13_3", new SynthesisDataSource(dataSource3));
	    		}
	    	}
    	}

    	
	}		
	
	//외부에서 내부로의 전체 탐지로그 & 신규 탐지 이벤트 현황
	private void New_DetectLog_Condition(HashMap<String, Object> reportData, int nDirection, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();

    	//신규 탐지 이벤트리스트
    	Iterable<DBObject>  dbNewResult = ipsDao.EventTopN("DY", nDirection, assetCode, sStartDay, sEndDay, null);
    	HashMap<String, DBObject> newMap = new HashMap<String, DBObject>();
    	for (DBObject val : dbNewResult) {
    		newMap.put((String)val.get("action") + "-" + (String)val.get("message"), val);
    	}

		//전일 탐지 이벤트리스트
		Iterable<DBObject> dbResult = ipsDao.EventTopN("DY", nDirection, assetCode, ipsDao.addDate(sStartDay,-1), ipsDao.addDate(sEndDay,-1), null);
    	for (DBObject val : dbResult) {
    		newMap.remove((String)val.get("action") + "-" + (String)val.get("message"));
    	}
    	
    	List<DBObject> newList = new ArrayList<DBObject>();
    	for (Entry<String, DBObject> e : newMap.entrySet()) {
    		newList.add(e.getValue());
    	}
    	
		Comparator<DBObject> comparator = new Comparator<DBObject>() {
			@Override
			public int compare(DBObject m1, DBObject m2) {
				long o1 = ((Number)m1.get("count")).longValue();
				long o2 = ((Number)m2.get("count")).longValue();
				return o1 == o2 ? 0 : o1 > o2 ? -1 : o1 < o2 ? 1 : 0;
			}
		};

		Collections.sort(newList, comparator);
    	
		List<String> hourDay = ipsDao.getPeriodHour(sStartDay, 0);
		
		int nNo = 1;
		for (DBObject mVal : newList) {
			String sMessage = (String)mVal.get("message");
			String sAction = (String)mVal.get("action");
			
	    	dbResult = ipsDao.EventTopNTrend("HR", nDirection, sAction, assetCode, sStartDay, sEndDay, sMessage);
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
		    		map.put("series", sMessage);
		    		map.put("category", String.valueOf(h));
		    		map.put("value", pVal != null ? ((Number)pVal.get("count")).longValue() : 0);
		    		
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
			String sAction = (String)mVal.get("action");

			Iterable<DBObject> srcIpTop5 = ipsDao.EventSrcIpTopN("DY", nDirection, sAction, assetCode, sStartDay, sEndDay, sMessage, 5);
			for (DBObject sVal : srcIpTop5) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("no", nNo);
				map.put("message", sMessage);
				map.put("count", ((Number)mVal.get("count")).longValue());
				map.put("action", sAction.equals("1") ? "허용" : "차단");
				map.put("srcIp", StringUtil.convertString(sVal.get("srcIp")));
				if (nDirection == INBOUND) {
					map.put("geoCode", Constants.getCountryCode(StringUtil.convertString(sVal.get("srcIp"))));
				} else {
					map.put("geoCode", Constants.getCountryCode(StringUtil.convertString(sVal.get("destIp"))));
				}
				map.put("destIp", StringUtil.convertString(sVal.get("destIp")));
				map.put("ratio", (((Number)sVal.get("count")).longValue()*100f)/((Number)mVal.get("count")).longValue());
	    		
	    		dataSource2.add(map);
	    		logger.debug(map.toString());
			}
			if (nNo >= 30) break;
			nNo++;
    	}
    	
    	if (nDirection == INBOUND) {
    		reportData.put("OPT14_0", "시간");
    		reportData.put("OPT14_1", new SynthesisDataSource(dataSource1));
    		reportData.put("OPT14_2", new SynthesisDataSource(dataSource2));
    	} else {
    		reportData.put("OPT15_0", "시간");
    		reportData.put("OPT15_1", new SynthesisDataSource(dataSource1));
    		reportData.put("OPT15_2", new SynthesisDataSource(dataSource2));
    	}
	}		
	
	//외부에서 내부로의 전체 탐지로그 & 이전대비 2배증가된 이벤트 현황
	private void Double_DetectLog_Condition(HashMap<String, Object> reportData, int nDirection, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();

		//전일 탐지 이벤트리스트
		Iterable<DBObject> dbBefore = ipsDao.EventTopN("DY", nDirection, assetCode, ipsDao.addDate(sStartDay,-1), ipsDao.addDate(sEndDay,-1), null);
		HashMap<String, DBObject> mapBefore = new HashMap<String, DBObject>();
    	for (DBObject val : dbBefore) {
    		mapBefore.put((String)val.get("action") + "-" + (String)val.get("message"), val);
    	}

    	//신규 탐지 이벤트리스트
    	List<DBObject> dblList = new ArrayList<DBObject>();
    	List<String> saDbl = new ArrayList<String>();
    	Iterable<DBObject> dbResult = ipsDao.EventTopN("DY", nDirection, assetCode, sStartDay, sEndDay, null);
    	for (DBObject val : dbResult) {
    		String sMessage = (String)val.get("message");
    		String sAction = (String)val.get("action");
    		
    		DBObject beforeMap = (DBObject)mapBefore.get(sAction + "-" + sMessage);
    		if (beforeMap == null) continue;

    		long lCount = ((Number)val.get("count")).longValue();
    		long lBeforeCount = ((Number)beforeMap.get("count")).longValue();

    		if (lCount >= lBeforeCount*2) {
    			saDbl.add(sMessage);
    			dblList.add(val);
    		}
    	}
    	
    	List<String> hourDay = ipsDao.getPeriodHour(sStartDay, 0);
    	
		int nNo = 1;
		for (DBObject mVal : dblList) {
			String sMessage = (String)mVal.get("message");
			String sAction = (String)mVal.get("action");

	    	dbResult = ipsDao.EventTopNTrend("HR", nDirection, sAction, assetCode, sStartDay, sEndDay, sMessage);
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
		    		map.put("series", sMessage);
		    		map.put("category", String.valueOf(h));
		    		map.put("value", val != null ? ((Number)val.get("count")).longValue() : 0);
		    		
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
    		String sAction = (String)val.get("action");

			Iterable<DBObject> srcIpTop5 = ipsDao.EventSrcIpTopN("DY", nDirection, sAction, assetCode, sStartDay, sEndDay, sMessage, 5);
			for (DBObject sVal : srcIpTop5) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("no", nNo);
				map.put("message", sMessage);
				map.put("count", ((Number)val.get("count")).longValue());
				map.put("action", sAction.equals("1") ? "허용" : "차단");
				map.put("srcIp", StringUtil.convertString(sVal.get("srcIp")));
				map.put("geoCode", Constants.getCountryCode(StringUtil.convertString(sVal.get("srcIp"))));
				map.put("destIp", StringUtil.convertString(sVal.get("destIp")));
				map.put("ratio", (((Number)sVal.get("count")).longValue()*100f)/((Number)val.get("count")).longValue());
	    		
	    		dataSource2.add(map);
	    		logger.debug(map.toString());
			}
			if (nNo >= 30) break;
			nNo++;
    	}
    	
    	reportData.put("OPT16_0", "시간");
		reportData.put("OPT16_1", new SynthesisDataSource(dataSource1));
		reportData.put("OPT16_2", new SynthesisDataSource(dataSource2));
	}		
	
	//외부에서 내부로의 전체 탐지로그 & SIP TOP
	private void Action_DetectLog_TopN(HashMap<String, Object> reportData, int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay, boolean bSrcIp, int nLimit, boolean bChk) throws Exception {
		
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();
		
    	//IP 건수기준 TOP N 리스트
		Iterable<DBObject> ipTopNResult = ipsDao.IpTopN("DY", nDirection, sAction, assetCode, sStartDay, sEndDay, bSrcIp, nLimit);
    	List<String> topNIps = new ArrayList<String>();
    	for (DBObject val : ipTopNResult) {
    		if (bSrcIp) {    		
    			topNIps.add((String)val.get("srcIp"));
    		} else {
    			topNIps.add((String)val.get("destIp"));
    		}
    	}

    	//전일 현황
    	Iterable<DBObject> dbBefore = ipsDao.BeforeIpCondition("DY", nDirection, sAction, assetCode, ipsDao.addDate(sStartDay,-1), ipsDao.addDate(sEndDay,-1), bSrcIp, topNIps);
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
    			ipTopNCondition = ipsDao.IpTopNCondition("DY", nDirection, sAction, assetCode, sStartDay, sEndDay, (String)val.get("srcIp"), bSrcIp);
    		} else {
    			ipTopNCondition = ipsDao.IpTopNCondition("DY", nDirection, sAction, assetCode, sStartDay, sEndDay, (String)val.get("destIp"), bSrcIp);
    		}
    		
			for (DBObject dbObj : ipTopNCondition) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("no", nNo);
				if (bSrcIp) {
					map.put("srcIp", (String)val.get("srcIp"));
					map.put("count", ((Number)val.get("count")).longValue());
					DBObject prevMap = mapBefore.get((String)dbObj.get("srcIp"));
					map.put("countVariation", ((Number)val.get("count")).longValue() - (prevMap != null ? ((Number)prevMap.get("count")).longValue() : 0));

					map.put("message", (String)dbObj.get("message"));
					map.put("destIp", (String)dbObj.get("destIp"));
					map.put("action", StringUtil.convertString(dbObj.get("action")).equals(ALLOW) ? "허용" : "차단");
					map.put("ratio", (((Number)dbObj.get("count")).longValue()*100f)/((Number)val.get("count")).longValue());
				} else {
					map.put("destIp", (String)val.get("destIp"));
					map.put("count", ((Number)val.get("count")).longValue());
		    		DBObject prevMap = mapBefore.get((String)dbObj.get("destIp"));
		    		map.put("countVariation", ((Number)val.get("count")).longValue() - (prevMap != null ? ((Number)prevMap.get("count")).longValue() : 0));
		    		
		    		map.put("message", (String)dbObj.get("message"));
					map.put("srcIp", (String)dbObj.get("srcIp"));
					map.put("action", StringUtil.convertString(dbObj.get("action")).equals(ALLOW) ? "허용" : "차단");
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
	    	if (nDirection == INBOUND && StringUtil.isEmpty(sAction)) { //외부에서 내부로의 전체 세션로그 & SIP TOP
	    		reportData.put("OPT17_1", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == INBOUND && sAction.equals(ALLOW)) { //외부에서 내부로의 허용 세션로그 & SIP TOP
	    		reportData.put("OPT18_1", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == INBOUND && sAction.equals(CUTOFF)) { //외부에서 내부로의 차단 세션로그 & SIP TOP
	    		reportData.put("OPT19_1", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == OUTBOUND && StringUtil.isEmpty(sAction)) { //내부에서 외부로의 전체 세션로그 & SIP TOP
	    		reportData.put("OPT20_1", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == OUTBOUND && sAction.equals(ALLOW)) { //내부에서 외부로의 허용 세션로그 & SIP TOP
	    		reportData.put("OPT21_1", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == OUTBOUND && sAction.equals(CUTOFF)) { //내부에서 외부로의 차단 세션로그 & SIP TOP
	    		reportData.put("OPT22_1", new SynthesisDataSource(dataSource1));
	    	}
	    	
	    	if (topNIps.size() > 10) {
	    		topNIps = topNIps.subList(0, 10);
	    	}
	    	
	    	if (bChk) { //SRC IP TOP10 세션 로그 발생추이 
	    		ArrayList<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();
	        	Iterable<DBObject> ipTopNTrend = ipsDao.IpTopNTrend("HR", nDirection, sAction, assetCode, sStartDay, sEndDay, topNIps, true);
	        	
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
	        	
	        	List<String> hourDay = ipsDao.getPeriodHour(sStartDay, 0);
	        	
	        	for (String strIp : topNIps) {
	        		for (String tDay : hourDay) {
	    	    		for (int h = 0; h < 24; h++) { //시간
	    		    		DBObject val = mapResult.get(strIp + "-" + tDay + (h < 10 ? "0" + h : String.valueOf(h)));
	    		    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		    		map.put("series", strIp);
	    		    		map.put("category", String.valueOf(h));
	    		    		map.put("value", val != null ? ((Number)val.get("count")).longValue() : 0);
	    		    		dataSource2.add(map);
	    					logger.debug(map.toString());
	    		    	}
	        		}
	        	}
	    		
	        	if (nDirection == INBOUND && StringUtil.isEmpty(sAction)) {
	        		reportData.put("OPT17_0", "시간");
		    		reportData.put("OPT17_2", new SynthesisDataSource(dataSource2));
	        	} else if (nDirection == INBOUND && sAction.equals(ALLOW)) {
	        		reportData.put("OPT18_0", "시간");
	        		reportData.put("OPT18_2", new SynthesisDataSource(dataSource2));
	        	} else if (nDirection == INBOUND && sAction.equals(CUTOFF)) {
	        		reportData.put("OPT19_0", "시간");
	        		reportData.put("OPT19_2", new SynthesisDataSource(dataSource2));
		    	} else if (nDirection == OUTBOUND && StringUtil.isEmpty(sAction)) {
		    		reportData.put("OPT20_0", "시간");
		    		reportData.put("OPT20_2", new SynthesisDataSource(dataSource2));
		    	} else if (nDirection == OUTBOUND && sAction.equals(ALLOW)) {
		    		reportData.put("OPT21_0", "시간");
		    		reportData.put("OPT21_2", new SynthesisDataSource(dataSource2));
		    	} else if (nDirection == OUTBOUND && sAction.equals(CUTOFF)) {
		    		reportData.put("OPT22_0", "시간");
		    		reportData.put("OPT22_2", new SynthesisDataSource(dataSource2));
		    	}
	    	}
    	} else {
	    	if (nDirection == INBOUND && StringUtil.isEmpty(sAction)) { //외부에서 내부로의 전체 탐지로그 & DIP TOP
	    		reportData.put("OPT23_1", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == INBOUND && sAction.equals(ALLOW)) { //외부에서 내부로의 허용 탐지로그 & DIP TOP
	    		reportData.put("OPT24_1", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == INBOUND && sAction.equals(CUTOFF)) { //외부에서 내부로의 차단 탐지로그 & DIP TOP
	    		reportData.put("OPT25_1", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == OUTBOUND && StringUtil.isEmpty(sAction)) { //내부에서 외부로의 전체 탐지로그 & DIP TOP
	    		reportData.put("OPT26_1", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == OUTBOUND && sAction.equals(ALLOW)) { //내부에서 외부로의 허용 탐지로그 & DIP TOP
	    		reportData.put("OPT27_1", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == OUTBOUND && sAction.equals(CUTOFF)) { //내부에서 외부로의 차단 탐지로그 & DIP TOP
	    		reportData.put("OPT28_1", new SynthesisDataSource(dataSource1));
	    	}
    	}
	}
	
	private void ALL_ServiceTop10(HashMap<String, Object> reportData, int nDirection, int assetCode, String sStartDay, String sEndDay, boolean bChk) throws Exception {
		
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();

    	Iterable<DBObject> dbResult = ipsDao.ServiceDetectLog("DY", nDirection, assetCode, sStartDay, sEndDay, 0, false, 0);
    	
    	long lTotal = 0;
    	HashMap<Integer, DBObject> svcMap = new HashMap<Integer, DBObject>();
		for (DBObject val : dbResult) {
    		val.put("allow", 0);
    		val.put("cutoff", 0);
    		svcMap.put((Integer)val.get("destPort"), val);
    		lTotal += ((Number)val.get("count")).longValue();
		}

		Iterable<DBObject> actionResult = ipsDao.ServiceDetectLog("DY", nDirection, assetCode, sStartDay, sEndDay, 0, true, 0);
		for (DBObject val : actionResult) {
			int nPort = (Integer)val.get("destPort");
			DBObject svcObj = svcMap.get(nPort);
			if (StringUtil.convertString(val.get("action")).equals(ALLOW)) {
				svcObj.put("allow", ((Number)val.get("count")).longValue());
			} else {
				svcObj.put("cutoff", ((Number)val.get("count")).longValue());
			}
		}
		
    	int nNo = 1;
    	long lCount = 0, lAllow = 0, lCutoff = 0;
    	for (DBObject val : dbResult) {
    		int nPort = (Integer)val.get("destPort");
    		if (nPort != -1 && nNo <= 10) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
        		map.put("port", nPort);
        		map.put("svcName", ipsDao.getPortServiceName(nPort));
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
    		map.put("port", -1);
    		map.put("svcName", "기타");
			map.put("count", lCount);
			map.put("ratio", (lCount*100f)/lTotal);
			map.put("allow", (lAllow*100f)/lCount);
			map.put("cutoff", (lCutoff*100f)/lCount);
	
			dataSource1.add(map);
			logger.debug(map.toString());
    	}
		
    	if (nDirection == INBOUND) {
    		reportData.put("OPT29_1", new SynthesisDataSource(dataSource1));
    	} else {
    		reportData.put("OPT30_1", new SynthesisDataSource(dataSource1));
    	}
    	
    	if (bChk) { //서비스 TOP10 증감현황 및 이벤트유형
    	
    		//표 [순위 | Port | 서비스명 | 전체건수 | 증감현황 | 이벤트명 | Action | 비율(%)]
    		dbResult = ipsDao.ServiceDetectLog("DY", nDirection, assetCode, ipsDao.addDate(sStartDay,-1), ipsDao.addDate(sEndDay,-1), 0, false, 0);
        	HashMap<String, DBObject> mapBefore = new HashMap<String, DBObject>(); 
        	for (DBObject val : dbResult) {
        		mapBefore.put(String.valueOf((Integer)val.get("destPort")), val);
        	}
        	
        	List<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();
        	
        	nNo = 1;
        	for (HashMap<String, Object> hMap : dataSource1) {
        		int nPort = (Integer)hMap.get("port");
        		
        		if (nPort < 0) continue;
        		
        		Iterable<DBObject> dbTmp = ipsDao.ServiceEventTopN("DY", nDirection, assetCode, sStartDay, sEndDay, nPort);
        		for (DBObject pMap : dbTmp) {
            		HashMap<String, Object> map = new HashMap<String, Object>();
            		map.put("no", nNo);
            		map.put("port", nPort);
            		map.put("svcName", ipsDao.getPortServiceName(nPort));
            		map.put("count", ((Number)hMap.get("count")).longValue());
            		DBObject prevMap = mapBefore.get(String.valueOf(nPort));
            		map.put("countVariation", ((Number)hMap.get("count")).longValue() - (prevMap != null ? ((Number)prevMap.get("count")).longValue() : 0));
            		map.put("message", (String)pMap.get("message"));
            		map.put("action", StringUtil.convertString(pMap.get("action")).equals(ALLOW) ? "허용" : "차단");
            		map.put("ratio", (((Number)pMap.get("count")).longValue()*100f)/((Number)hMap.get("count")).longValue());
            		
            		dataSource2.add(map);
        			logger.debug(map.toString());
        		}
        		nNo++;
        	}
        	
        	if (nDirection == INBOUND) {
        		reportData.put("OPT29_2", new SynthesisDataSource(dataSource2));
        	} else {
        		reportData.put("OPT30_2", new SynthesisDataSource(dataSource2));
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




