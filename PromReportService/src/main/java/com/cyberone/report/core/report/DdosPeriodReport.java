package com.cyberone.report.core.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;

import com.cyberone.report.Constants;
import com.cyberone.report.core.dao.DdosDao;
import com.cyberone.report.core.datasource.SynthesisDataSource;
import com.cyberone.report.model.UserInfo;
import com.cyberone.report.utils.StringUtil;
import com.mongodb.DBObject;

public class DdosPeriodReport extends BaseReport {
	
	private DdosDao ddosDao;

	private Logger logger = LoggerFactory.getLogger(getClass());

	private UserInfo userInfo;
	
	public DdosPeriodReport(UserInfo userInfo) {
		this.userInfo = userInfo;
		this.ddosDao = new DdosDao(userInfo.getPromDb(), userInfo.getReportDb());
	}
	
	/* 
	 * DDoS 임의기간보고서 통계데이타 생성
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getDataSource(String sReportType, String sSearchDate, HashMap<String, Object> hMap) throws Exception {
		logger.debug("DDoS 임의기간보고서 통계데이타 생성");
		
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
		} else {
			hData = (HashMap<String,Object>)hFormMap.get("data");
			sEtc = StringUtil.convertString(hFormMap.get("etc"));
		}

		HashMap<String, Object> reportData = new HashMap<String, Object>();
		
		int nChoice = 0;
		for (Entry<String, Object> e : hData.entrySet()) {
			
			switch (e.getKey()) {
				case "opt01" :	//전체 탐지로그 발생추이
					push("항목: 전체 탐지로그 발생추이");
					All_DetectLog_Trend(reportData, -1, assetCode, sStartDay, sEndDay);
					break;
				case "opt02" :	//외부에서 내부로의 전체 탐지로그 발생추이 
					push("항목: 외부에서 내부로의 전체 탐지로그 발생추이");
					Direction_DetectLog_Trend(reportData, INBOUND, assetCode, sStartDay, sEndDay);
					break;
				case "opt03" : 	//외부에서 내부로의 허용 탐지로그 발생추이 (차트)
					push("항목: 외부에서 내부로의 허용 탐지로그 발생추이 (차트)");
					Action_DetectLog_Trend(reportData, INBOUND, ALLOW, assetCode, sStartDay, sEndDay);
					break;
				case "opt04" :	//외부에서 내부로의 차단 탐지로그 발생추이 (차트)
					push("항목: 외부에서 내부로의 차단 탐지로그 발생추이 (차트)");
					Action_DetectLog_Trend(reportData, INBOUND, CUTOFF, assetCode, sStartDay, sEndDay);
					break;
				case "opt05" : 	//내부에서 외부로의 전체 탐지로그 발생추이
					push("항목: 내부에서 외부로의 전체 탐지로그 발생추이");
					Direction_DetectLog_Trend(reportData, OUTBOUND, assetCode, sStartDay, sEndDay);
					break;
				case "opt06" : 	//내부에서 외부로의 허용 탐지로그 발생추이 (차트)
					push("항목: 내부에서 외부로의 허용 탐지로그 발생추이 (차트)");
					Action_DetectLog_Trend(reportData, OUTBOUND, ALLOW, assetCode, sStartDay, sEndDay);
					break;
				case "opt07" : 	//내부에서 외부로의 차단 탐지로그 발생추이 (차트)
					push("항목: 내부에서 외부로의 차단 탐지로그 발생추이 (차트)");
					Action_DetectLog_Trend(reportData, OUTBOUND, CUTOFF, assetCode, sStartDay, sEndDay);
					break;
				case "opt08" : 	//외부에서 내부로의 전체 탐지로그 & 이벤트 TOP (차트, 표)
					push("항목: 외부에서 내부로의 전체 탐지로그 & 이벤트 TOP (차트, 표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd8")));
					Direction_EventTopN(reportData, INBOUND, assetCode, sStartDay, sEndDay, nChoice);
					break;
				case "opt09" : 	//외부에서 내부로의 허용 탐지로그 & 이벤트 TOP (차트, 표)
					push("항목: 외부에서 내부로의 허용 탐지로그 & 이벤트 TOP (차트, 표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd9")));
					Direction_Action_EventTopN(reportData, INBOUND, ALLOW, assetCode, sStartDay, sEndDay, nChoice, !StringUtil.isEmpty(hData.get("ck9")));
					break;
				case "opt10" : 	//외부에서 내부로의 차단 탐지로그 & 이벤트 TOP (차트, 표)
					push("항목: 외부에서 내부로의 차단 탐지로그 & 이벤트 TOP (차트, 표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd10")));
					Direction_Action_EventTopN(reportData, INBOUND, CUTOFF, assetCode, sStartDay, sEndDay, nChoice, !StringUtil.isEmpty(hData.get("ck10")));
					break;
				case "opt11" : 	//내부에서 외부로의 전체 탐지로그 & 이벤트 TOP (차트, 표)
					push("항목: 내부에서 외부로의 전체 탐지로그 & 이벤트 TOP (차트, 표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd11")));
					Direction_EventTopN(reportData, OUTBOUND, assetCode, sStartDay, sEndDay, nChoice);
					break;
				case "opt12" : 	//내부에서 외부로의 허용 탐지로그 & 이벤트 TOP (차트, 표)
					push("항목: 내부에서 외부로의 허용 탐지로그 & 이벤트 TOP (차트, 표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd12")));
					Direction_Action_EventTopN(reportData, OUTBOUND, ALLOW, assetCode, sStartDay, sEndDay, nChoice, !StringUtil.isEmpty(hData.get("ck12")));
					break;
				case "opt13" : 	//내부에서 외부로의 차단 탐지로그 & 이벤트 TOP (차트, 표)
					push("항목: 내부에서 외부로의 차단 탐지로그 & 이벤트 TOP (차트, 표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd13")));
					Direction_Action_EventTopN(reportData, OUTBOUND, CUTOFF, assetCode, sStartDay, sEndDay, nChoice, !StringUtil.isEmpty(hData.get("ck13")));
					break;
				case "opt14" : 	//외부에서 내부로의 전체 탐지로그 & SIP TOP (표)
					push("항목: 외부에서 내부로의 전체 탐지로그 & SIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd14")));
					Action_DetectLog_TopN(reportData, INBOUND, "", assetCode, sStartDay, sEndDay, true, nChoice, !StringUtil.isEmpty(hData.get("ck14")));
					break;
				case "opt15" : 	//외부에서 내부로의 허용 탐지로그 & SIP TOP (표)
					push("항목: 외부에서 내부로의 허용 탐지로그 & SIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd15")));
					Action_DetectLog_TopN(reportData, INBOUND, ALLOW, assetCode, sStartDay, sEndDay, true, nChoice, !StringUtil.isEmpty(hData.get("ck15")));
					break;
				case "opt16" : 	//외부에서 내부로의 차단 탐지로그 & SIP TOP (표)
					push("항목: 외부에서 내부로의 차단 탐지로그 & SIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd16")));
					Action_DetectLog_TopN(reportData, INBOUND, CUTOFF, assetCode, sStartDay, sEndDay, true, nChoice, !StringUtil.isEmpty(hData.get("ck16")));
					break;
				case "opt17" : 	//내부에서 외부로의 전체 탐지로그 & SIP TOP (표)
					push("항목: 내부에서 외부로의 전체 탐지로그 & SIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd17")));
					Action_DetectLog_TopN(reportData, OUTBOUND, "", assetCode, sStartDay, sEndDay, true, nChoice, !StringUtil.isEmpty(hData.get("ck17")));
					break;
				case "opt18" : 	//내부에서 외부로의 허용 탐지로그 & SIP TOP (표)
					push("항목: 내부에서 외부로의 허용 탐지로그 & SIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd18")));
					Action_DetectLog_TopN(reportData, OUTBOUND, ALLOW, assetCode, sStartDay, sEndDay, true, nChoice, !StringUtil.isEmpty(hData.get("ck18")));
					break;
				case "opt19" : 	//내부에서 외부로의 차단 탐지로그 & SIP TOP (표)
					push("항목: 내부에서 외부로의 차단 탐지로그 & SIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd19")));
					Action_DetectLog_TopN(reportData, OUTBOUND, CUTOFF, assetCode, sStartDay, sEndDay, true, nChoice, !StringUtil.isEmpty(hData.get("ck19")));
					break;
				case "opt20" : 	//외부에서 내부로의 전체 탐지로그 & DIP TOP (표)
					push("항목: 외부에서 내부로의 전체 탐지로그 & DIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd20")));
					Action_DetectLog_TopN(reportData, INBOUND, "", assetCode, sStartDay, sEndDay, false, nChoice, false);
					break;
				case "opt21" : 	//외부에서 내부로의 허용 탐지로그 & DIP TOP (표)
					push("항목: 외부에서 내부로의 허용 탐지로그 & DIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd21")));
					Action_DetectLog_TopN(reportData, INBOUND, ALLOW, assetCode, sStartDay, sEndDay, false, nChoice, false);
					break;
				case "opt22" : 	//외부에서 내부로의 차단 탐지로그 & DIP TOP (표)
					push("항목: 외부에서 내부로의 차단 탐지로그 & DIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd22")));
					Action_DetectLog_TopN(reportData, INBOUND, CUTOFF, assetCode, sStartDay, sEndDay, false, nChoice, false);
					break;
				case "opt23" : 	//내부에서 외부로의 전체 탐지로그 & DIP TOP (표)
					push("항목: 내부에서 외부로의 전체 탐지로그 & DIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd23")));
					Action_DetectLog_TopN(reportData, OUTBOUND, "", assetCode, sStartDay, sEndDay, false, nChoice, false);
					break;
				case "opt24" : 	//내부에서 외부로의 허용 탐지로그 & DIP TOP (표)
					push("항목: 내부에서 외부로의 허용 탐지로그 & DIP TOP (표))");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd24")));
					Action_DetectLog_TopN(reportData, OUTBOUND, ALLOW, assetCode, sStartDay, sEndDay, false, nChoice, false);
					break;
				case "opt25" : 	//내부에서 외부로의 차단 탐지로그 & DIP TOP (표)
					push("항목: 내부에서 외부로의 차단 탐지로그 & DIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd25")));
					Action_DetectLog_TopN(reportData, OUTBOUND, CUTOFF, assetCode, sStartDay, sEndDay, false, nChoice, false);
					break;
				case "opt26" : 	//외부에서 내부로의 전체 탐지로그 & 서비스 TOP10 (차트)
					push("항목: 외부에서 내부로의 전체 탐지로그 & 서비스 TOP10 (차트)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd26")));
					ALL_ServiceTop10(reportData, INBOUND, assetCode, sStartDay, sEndDay, !StringUtil.isEmpty(hData.get("ck26")));
					break;
				case "opt27" : 	//내부에서 외부로의 전체 탐지로그 & 서비스 TOP10 (차트)
					push("항목: 내부에서 외부로의 전체 탐지로그 & 서비스 TOP10 (차트)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd27")));
					ALL_ServiceTop10(reportData, OUTBOUND, assetCode, sStartDay, sEndDay, !StringUtil.isEmpty(hData.get("ck27")));
					break;
					
			}
		}
		return reportData;
	}

	private void All_DetectLog_Trend(HashMap<String, Object> reportData, int nDirection, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();

		Iterable<DBObject> dbResult = ddosDao.AllDetectLogTrend("DY", 1, assetCode, sStartDay, sEndDay);

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
    	
    	List<String> dayPeriod = ddosDao.getPeriodDay(sStartDay, sEndDay, 1);
    	
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
    	
		dbResult = ddosDao.AllDetectLogTrend("DY", -1, assetCode, sStartDay, sEndDay);
		
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

		reportData.put("OPT1_1", new SynthesisDataSource(dataSource));
	}
	
	private void Direction_DetectLog_Trend(HashMap<String, Object> reportData, int nDirection, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();
			
		Iterable<DBObject> dbResult = ddosDao.AllActionTrend("DY", nDirection, ALLOW, assetCode, sStartDay, sEndDay);

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
    	for (int action = 0; action < 2; action++) { //차단/허용
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
    	
    	dbResult = ddosDao.AllActionTrend("DY", nDirection, "", assetCode, sStartDay, sEndDay);
    	
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
		
		if (nDirection == INBOUND) {
			reportData.put("OPT2_1", new SynthesisDataSource(dataSource));
		} else {
			reportData.put("OPT5_1", new SynthesisDataSource(dataSource));
		}
	}

	private void Action_DetectLog_Trend(HashMap<String, Object> reportData, int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();

		Iterable<DBObject> dbResult = ddosDao.CompareDetectLogTrend("DY", nDirection, sAction, assetCode, sStartDay, sEndDay);

    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
    	for (DBObject val : dbResult) {
    		String sYear = ((Integer)val.get("year")).toString();
    		String sMonth = ((Integer)val.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    		String sDay = ((Integer)val.get("day")).toString();
    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
    		mapResult.put(sAction + sYear + sMonth + sDay, val);
    	}
    	
    	List<String> dayPeriod = ddosDao.getPeriodDay(sStartDay, sEndDay, 1);

    	String[] saGubun = {"차단", "허용"};
		for (String tDay : dayPeriod) {
    		HashMap<String, Object> map = new HashMap<String, Object>();
    		DBObject val = mapResult.get(sAction + tDay);

			map.put("gubun", saGubun[Integer.valueOf(sAction)]);
			map.put("week", "");
			map.put("day", Integer.valueOf(tDay.substring(6,8)));
    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
			
    		dataSource.add(map);
    		logger.debug(map.toString());
		}
    	
		if (nDirection == INBOUND) {
			if (sAction.equals(ALLOW)) {
				reportData.put("OPT3", new SynthesisDataSource(dataSource));
			} else {
				reportData.put("OPT4", new SynthesisDataSource(dataSource));
			}
		} else {
			if (sAction.equals(ALLOW)) {
				reportData.put("OPT6", new SynthesisDataSource(dataSource));
			} else {
				reportData.put("OPT7", new SynthesisDataSource(dataSource));
			}
		}
		
	}
	
	//외부에서 내부로의 전체 탐지로그 & 이벤트 TOP (차트, 표)
	private void Direction_EventTopN(HashMap<String, Object> reportData, int nDirection, int assetCode, String sStartDay, String sEndDay, int nLimit) throws Exception {
		
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();

		Iterable<DBObject> dbResult = ddosDao.EventTopN("DY", nDirection, "", assetCode, sStartDay, sEndDay, 0);

		int nTotal = 0;
    	for (DBObject val : dbResult) {
    		nTotal += ((Number)val.get("count")).longValue();
    	}

    	int nNo = 1;
    	for (DBObject val : dbResult) {
    		String sMessage = (String)val.get("message");
    		String sAction = (String)val.get("action");
    		if (!sMessage.equals("-1") && nNo <= 10) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		map.put("message", sMessage + (sAction.equals(ALLOW) ? "(허용)" : "(차단)"));
	    		map.put("total", nTotal);
	    		map.put("count", ((Number)val.get("count")).longValue());

	    		dataSource.add(map);
	    		logger.debug(map.toString());
    		}
    		if (nNo >= 10) break;
    		nNo++;
    	}
    	
    	nNo = 1;
    	for (DBObject val : dbResult) {
    		String sMessage = (String)val.get("message");
    		String sAction = (String)val.get("action");
    		
    		Iterable<DBObject> srcIpTop5 = ddosDao.EventSrcIpTopN("DY", nDirection, sAction, assetCode, sStartDay, sEndDay, sMessage, 5);
    		for (DBObject sVal : srcIpTop5) {
    			HashMap<String, Object> map = new HashMap<String, Object>();
    			map.put("no", nNo);
    			map.put("message", sMessage);
    			map.put("action", sAction);
    			map.put("total", ((Number)val.get("count")).longValue());
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
	    	reportData.put("OPT8_1", new SynthesisDataSource(dataSource));
	    	reportData.put("OPT8_2", new SynthesisDataSource(dataSource2));
    	} else {
	    	reportData.put("OPT11_1", new SynthesisDataSource(dataSource));
	    	reportData.put("OPT11_2", new SynthesisDataSource(dataSource2));
    	}
		
	}		
	
	//외부에서 내부로의 허용/차단 탐지로그 & 이벤트 TOP (차트, 표)
	private void Direction_Action_EventTopN(HashMap<String, Object> reportData, int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay, int nLimit, boolean bChk) throws Exception {
		
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> dataSource3 = new ArrayList<HashMap<String, Object>>();
		
		Iterable<DBObject> dbResult = ddosDao.EventTopN("DY", nDirection, sAction, assetCode, sStartDay, sEndDay, 0);

		int nTotal = 0;
    	for (DBObject val : dbResult) {
    		nTotal += ((Number)val.get("count")).longValue();
    	}

    	int nNo = 1;
    	List<String> saTopN = new ArrayList<String>();
    	for (DBObject val : dbResult) {
    		String sMessage = (String)val.get("message");
    		if (!sMessage.equals("-1") && nNo <= 10) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		map.put("message", sMessage);
	    		map.put("total", nTotal);
	    		map.put("count", ((Number)val.get("count")).longValue());

	    		dataSource.add(map);
	    		logger.debug(map.toString());
	    		
	    		saTopN.add((String)val.get("message"));
    		}
    		if (nNo >= 10) break;
    		nNo++;
    	}
    	
    	nNo = 1;
    	for (DBObject val : dbResult) {
    		String sMessage = (String)val.get("message");
    		
    		Iterable<DBObject> srcIpTop5 = ddosDao.EventSrcIpTopN("DY", nDirection, sAction, assetCode, sStartDay, sEndDay, sMessage, 5);
    		for (DBObject sVal : srcIpTop5) {
    			HashMap<String, Object> map = new HashMap<String, Object>();
    			map.put("no", nNo);
    			map.put("message", sMessage);
    			map.put("action", sAction);
    			map.put("total", ((Number)val.get("count")).longValue());
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
    		if (sAction.equals(ALLOW)) {
		    	reportData.put("OPT9_1", new SynthesisDataSource(dataSource));
		    	reportData.put("OPT9_2", new SynthesisDataSource(dataSource2));
    		} else {
		    	reportData.put("OPT10_1", new SynthesisDataSource(dataSource));
		    	reportData.put("OPT10_2", new SynthesisDataSource(dataSource2));
    		}
    	} else {
    		if (sAction.equals(ALLOW)) {
		    	reportData.put("OPT12_1", new SynthesisDataSource(dataSource));
		    	reportData.put("OPT12_2", new SynthesisDataSource(dataSource2));
    		} else {
		    	reportData.put("OPT13_1", new SynthesisDataSource(dataSource));
		    	reportData.put("OPT13_2", new SynthesisDataSource(dataSource2));
    		}
    	}
    	
    	if (bChk) { //Event Top 발생추이
    		
	    	dbResult = ddosDao.EventTopNTrend("DY", nDirection, sAction, assetCode, sStartDay, sEndDay, saTopN);
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
    		
	    	List<String> dayPeriod = ddosDao.getPeriodDay(sStartDay, sEndDay, 1);
	    	
	    	for (String sMessage : saTopN) {
	    		for (String tDay : dayPeriod) {
		    		DBObject val = mapResult.get(sMessage + "-" + tDay);
		    		HashMap<String, Object> map = new HashMap<String, Object>();
		    		map.put("gubun", sMessage);
		    		map.put("day", Integer.valueOf(tDay));
		    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
		    		
		    		dataSource3.add(map);
		    		logger.debug(map.toString());
		    	}
	    	}
    		
	    	if (nDirection == INBOUND) {
	    		if (sAction.equals(ALLOW)) {
			    	reportData.put("OPT9_3", new SynthesisDataSource(dataSource));
	    		} else {
			    	reportData.put("OPT10_3", new SynthesisDataSource(dataSource));
	    		}
	    	} else {
	    		if (sAction.equals(ALLOW)) {
			    	reportData.put("OPT12_3", new SynthesisDataSource(dataSource));
	    		} else {
			    	reportData.put("OPT13_3", new SynthesisDataSource(dataSource));
	    		}
	    	}
    	}
	}		
	
	//외부에서 내부로의 전체 탐지로그 & SIP TOP
	private void Action_DetectLog_TopN(HashMap<String, Object> reportData, int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay, boolean bSrcIp, int nLimit, boolean bChk) throws Exception {
		
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();
		
    	//IP 건수기준 TOP N 리스트
		Iterable<DBObject> ipTopNResult = ddosDao.IpTopN("DY", nDirection, sAction, assetCode, sStartDay, sEndDay, bSrcIp, nLimit);
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
    			ipTopNCondition = ddosDao.IpTopNCondition("DY", nDirection, sAction, assetCode, sStartDay, sEndDay, (String)val.get("srcIp"), bSrcIp);
    		} else {
    			ipTopNCondition = ddosDao.IpTopNCondition("DY", nDirection, sAction, assetCode, sStartDay, sEndDay, (String)val.get("destIp"), bSrcIp);
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
				
				if (nDirection == INBOUND) {
					map.put("geoCode", Constants.getCountryCode((String)dbObj.get("srcIp")));
				} else {
					map.put("geoCode", Constants.getCountryCode((String)dbObj.get("destIp")));
				}
				map.put("action", (String)dbObj.get("action"));
				map.put("total", ((Number)val.get("count")).longValue());
				map.put("count", ((Number)dbObj.get("count")).longValue());
				
				dataSource1.add(map);
				logger.debug(map.toString());
			}
			nNo++;
    	}
    	
    	if (bSrcIp) {
	    	if (nDirection == INBOUND && StringUtil.isEmpty(sAction)) { //외부에서 내부로의 전체 세션로그 & SIP TOP (표)
	    		reportData.put("OPT14_1", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == INBOUND && sAction.equals(ALLOW)) { //외부에서 내부로의 허용 세션로그 & SIP TOP (표)
	    		reportData.put("OPT15_1", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == INBOUND && sAction.equals(CUTOFF)) { //외부에서 내부로의 차단 세션로그 & SIP TOP (표)
	    		reportData.put("OPT16_1", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == OUTBOUND && StringUtil.isEmpty(sAction)) { //내부에서 외부로의 전체 세션로그 & SIP TOP (표)
	    		reportData.put("OPT17_1", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == OUTBOUND && sAction.equals(ALLOW)) { //내부에서 외부로의 허용 세션로그 & SIP TOP (표)
	    		reportData.put("OPT18_1", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == OUTBOUND && sAction.equals(CUTOFF)) { //내부에서 외부로의 차단 세션로그 & SIP TOP (표)
	    		reportData.put("OPT19_1", new SynthesisDataSource(dataSource1));
	    	}
	    	
	    	if (topNIps.size() > 10) {
	    		topNIps = topNIps.subList(0, 10);
	    	}
	    	
	    	if (bChk) { //SRC IP TOP10 세션 로그 발생추이 
	    		ArrayList<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();
	        	Iterable<DBObject> ipTopNTrend = ddosDao.IpTopNTrend("DY", nDirection, sAction, assetCode, sStartDay, sEndDay, topNIps, true);
	        	
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
    		    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
    		    		dataSource2.add(map);
    					logger.debug(map.toString());
	        		}
	        	}
	    		
	        	if (nDirection == INBOUND && StringUtil.isEmpty(sAction)) {
		    		reportData.put("OPT14_2", new SynthesisDataSource(dataSource2));
	        	} else if (nDirection == INBOUND && sAction.equals(ALLOW)) {
	        		reportData.put("OPT15_2", new SynthesisDataSource(dataSource2));
	        	} else if (nDirection == INBOUND && sAction.equals(CUTOFF)) {
	        		reportData.put("OPT16_2", new SynthesisDataSource(dataSource2));
		    	} else if (nDirection == OUTBOUND && StringUtil.isEmpty(sAction)) {
		    		reportData.put("OPT17_2", new SynthesisDataSource(dataSource2));
		    	} else if (nDirection == OUTBOUND && sAction.equals(ALLOW)) {
		    		reportData.put("OPT18_2", new SynthesisDataSource(dataSource2));
		    	} else if (nDirection == OUTBOUND && sAction.equals(CUTOFF)) {
		    		reportData.put("OPT19_2", new SynthesisDataSource(dataSource2));
		    	}
	    	}
    	} else {
	    	if (nDirection == INBOUND && StringUtil.isEmpty(sAction)) { //외부에서 내부로의 전체 탐지로그 & DIP TOP (표)
	    		reportData.put("OPT20", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == INBOUND && sAction.equals(ALLOW)) { //외부에서 내부로의 허용 탐지로그 & DIP TOP (표)
	    		reportData.put("OPT21", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == INBOUND && sAction.equals(CUTOFF)) { //외부에서 내부로의 차단 탐지로그 & DIP TOP (표)
	    		reportData.put("OPT22", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == OUTBOUND && StringUtil.isEmpty(sAction)) { //내부에서 외부로의 전체 탐지로그 & DIP TOP (표)
	    		reportData.put("OPT23", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == OUTBOUND && sAction.equals(ALLOW)) { //내부에서 외부로의 허용 탐지로그 & DIP TOP (표)
	    		reportData.put("OPT24", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == OUTBOUND && sAction.equals(CUTOFF)) { //내부에서 외부로의 차단 탐지로그 & DIP TOP (표)
	    		reportData.put("OPT25", new SynthesisDataSource(dataSource1));
	    	}
    	}
	}
	
	private void ALL_ServiceTop10(HashMap<String, Object> reportData, int nDirection, int assetCode, String sStartDay, String sEndDay, boolean bChk) throws Exception {
		
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();

    	Iterable<DBObject> dbResult = ddosDao.ServiceDetectLog("DY", nDirection, assetCode, sStartDay, sEndDay, 0, false, 0);
    	
    	int nTotal = 0;
		for (DBObject val : dbResult) {
    		int nPort = (Integer)val.get("destPort");
    		val.put("allow", 0);
    		val.put("cutoff", 0);
    		
    		nTotal += ((Number)val.get("count")).longValue();
    		
    		Iterable<DBObject> dbTmp = ddosDao.ServiceDetectLog("DY", nDirection, assetCode, sStartDay, sEndDay, 0, true, nPort);
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
    		int nPort = (Integer)val.get("destPort");
    		if (nPort != -1 && nNo <= 10) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		map.put("destPort", String.valueOf(nPort));
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
			map.put("destPort", "기타");
			map.put("count", nCount);
			map.put("allow", nAllow);
			map.put("cutoff", nCutoff);
	
			dataSource1.add(map);
			logger.debug(map.toString());
    	}
		
    	if (nDirection == INBOUND) {
    		reportData.put("OPT26_1", new SynthesisDataSource(dataSource1));
    	} else {
    		reportData.put("OPT27_1", new SynthesisDataSource(dataSource1));
    	}
    	
    	if (bChk) { //서비스 TOP10 증감현황 및 이벤트유형
    	
    		//표 [순위 | Port | 서비스명 | 전체건수 | 이벤트명 | Action | 비율(%)]
        	List<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();
        	
        	nNo = 1;
        	for (HashMap<String, Object> hMap : dataSource1) {
        		String sPort = (String)hMap.get("destPort");
        		
        		if (sPort.equals("기타")) continue;
        		
        		Iterable<DBObject> dbTmp = ddosDao.ServiceEventTopN("DY", assetCode, sStartDay, sEndDay, Integer.valueOf(sPort));
        		for (DBObject pMap : dbTmp) {
            		HashMap<String, Object> map = new HashMap<String, Object>();
            		map.put("no", nNo);
            		map.put("destPort", sPort);
            		map.put("total", ((Number)hMap.get("count")).longValue());
            		map.put("message", (String)pMap.get("message"));
            		map.put("action", (String)pMap.get("action"));
            		map.put("count", ((Number)pMap.get("count")).longValue());
            		
            		dataSource2.add(map);
        			logger.debug(map.toString());
        		}
        		nNo++;
        	}
        	
        	if (nDirection == INBOUND) {
        		reportData.put("OPT26_2", new SynthesisDataSource(dataSource2));
        	} else {
        		reportData.put("OPT27_2", new SynthesisDataSource(dataSource2));
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




