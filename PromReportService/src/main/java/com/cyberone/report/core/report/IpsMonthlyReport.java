package com.cyberone.report.core.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;

import com.cyberone.report.Constants;
import com.cyberone.report.core.dao.IpsDao;
import com.cyberone.report.core.datasource.SynthesisDataSource;
import com.cyberone.report.model.UserInfo;
import com.cyberone.report.utils.StringUtil;
import com.mongodb.DBObject;

public class IpsMonthlyReport extends BaseReport {
	
	private IpsDao ipsDao;

	private Logger logger = LoggerFactory.getLogger(getClass());

	private UserInfo userInfo;
	
	public IpsMonthlyReport(UserInfo userInfo) {
		this.userInfo = userInfo;
		this.ipsDao = new IpsDao(userInfo.getPromDb(), userInfo.getReportDb());
	}
	
	/* 
	 * 침입방지(IPS) 월간보고서 통계데이타 생성
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getDataSource(String sReportType, String sSearchDate, HashMap<String, Object> hMap) throws Exception {
		logger.debug("침입방지(IPS) 월간보고서 통계데이타 생성");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		
		String sStartDay = "";
		String sEndDay = "";
		
		String sAssetCode = StringUtil.convertString(hMap.get("assetCode"));
		int assetCode = Integer.valueOf(sAssetCode.substring(0, sAssetCode.indexOf("_")));

		//조회 시작일/종료일
		int statBaseDay = ipsDao.getStatMonthBaseDay(assetCode);
		if (statBaseDay > 1) {
			Date tmpSDate = sdf.parse(sSearchDate);
			Calendar startCal = ipsDao.getCalendar(tmpSDate);
			startCal.add(Calendar.DAY_OF_MONTH, statBaseDay);
			startCal.add(Calendar.DAY_OF_MONTH, -1);
			
			Date tmpEDate = sdf.parse(sSearchDate);
			Calendar endCal = ipsDao.getCalendar(tmpEDate);
			endCal.add(Calendar.MONTH, 1);
			endCal.add(Calendar.DAY_OF_MONTH, statBaseDay);
			endCal.add(Calendar.DAY_OF_MONTH, -1);
			endCal.add(Calendar.SECOND, -1);
			
			sStartDay = sdf2.format(startCal.getTime());
			sEndDay = sdf2.format(endCal.getTime());
		} else {
			Date tmpSDate = sdf.parse(sSearchDate);
			Calendar startCal = ipsDao.getCalendar(tmpSDate);
		
			Date tmpEDate = sdf.parse(sSearchDate);
			Calendar endCal = ipsDao.getCalendar(tmpEDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);

			sStartDay = sdf2.format(startCal.getTime());
			sEndDay = sdf2.format(endCal.getTime());
		}
		
		HashMap<String,Object> hFormMap = (HashMap<String,Object>)hMap.get("formData");
		
		String sEtc = "";
		HashMap<String,Object> hData = null;
		if (hFormMap == null) {
			DBObject dbObj = ipsDao.selectAutoReportForm(Integer.parseInt(sReportType), assetCode);
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
					Compare_DetectLog_Trend(reportData, INBOUND, ALLOW, assetCode, sStartDay, sEndDay);
					break;
				case "opt04" :	//외부에서 내부로의 차단 탐지로그 발생추이 (차트)
					push("항목: 외부에서 내부로의 차단 탐지로그 발생추이 (차트)");
					Compare_DetectLog_Trend(reportData, INBOUND, CUTOFF, assetCode, sStartDay, sEndDay);
					break;
				case "opt05" : 	//내부에서 외부로의 전체 탐지로그 발생추이
					push("항목: 내부에서 외부로의 전체 탐지로그 발생추이");
					Direction_DetectLog_Trend(reportData, OUTBOUND, assetCode, sStartDay, sEndDay);
					break;
				case "opt06" : 	//내부에서 외부로의 허용 탐지로그 발생추이 (차트)
					push("항목: 내부에서 외부로의 허용 탐지로그 발생추이 (차트)");
					Compare_DetectLog_Trend(reportData, OUTBOUND, ALLOW, assetCode, sStartDay, sEndDay);
					break;
				case "opt07" : 	//내부에서 외부로의 차단 탐지로그 발생추이 (차트)
					push("항목: 내부에서 외부로의 차단 탐지로그 발생추이 (차트)");
					Compare_DetectLog_Trend(reportData, OUTBOUND, CUTOFF, assetCode, sStartDay, sEndDay);
					break;
				case "opt08" : 	//외부에서 내부로의 전체 탐지로그 & 이벤트 TOP (차트, 표)
					push("항목: 외부에서 내부로의 전체 탐지로그 & 이벤트 TOP (차트, 표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd8")));
					Direction_EventTopN(reportData, INBOUND, assetCode, sStartDay, sEndDay, nChoice);
					break;
				case "opt09" : 	//외부에서 내부로의 허용 탐지로그 & 이벤트 TOP (차트, 표)
					push("항목: 외부에서 내부로의 허용 탐지로그 & 이벤트 TOP (차트, 표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd9")));
					Direction_Action_EventTopN(reportData, INBOUND, ALLOW, assetCode, sStartDay, sEndDay, nChoice, !StringUtil.isEmpty(hData.get("ck9")), StringUtil.convertString(hData.get("sd9")));
					break;
				case "opt10" : 	//외부에서 내부로의 차단 탐지로그 & 이벤트 TOP (차트, 표)
					push("항목: 외부에서 내부로의 차단 탐지로그 & 이벤트 TOP (차트, 표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd10")));
					Direction_Action_EventTopN(reportData, INBOUND, CUTOFF, assetCode, sStartDay, sEndDay, nChoice, !StringUtil.isEmpty(hData.get("ck10")), StringUtil.convertString(hData.get("sd10")));
					break;
				case "opt11" : 	//내부에서 외부로의 전체 탐지로그 & 이벤트 TOP (차트, 표)
					push("항목: 내부에서 외부로의 전체 탐지로그 & 이벤트 TOP (차트, 표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd11")));
					Direction_EventTopN(reportData, OUTBOUND, assetCode, sStartDay, sEndDay, nChoice);
					break;
				case "opt12" : 	//내부에서 외부로의 허용 탐지로그 & 이벤트 TOP (차트, 표)
					push("항목: 내부에서 외부로의 허용 탐지로그 & 이벤트 TOP (차트, 표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd12")));
					Direction_Action_EventTopN(reportData, OUTBOUND, ALLOW, assetCode, sStartDay, sEndDay, nChoice, !StringUtil.isEmpty(hData.get("ck12")), StringUtil.convertString(hData.get("sd11")));
					break;
				case "opt13" : 	//내부에서 외부로의 차단 탐지로그 & 이벤트 TOP (차트, 표)
					push("항목: 내부에서 외부로의 차단 탐지로그 & 이벤트 TOP (차트, 표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd13")));
					Direction_Action_EventTopN(reportData, OUTBOUND, CUTOFF, assetCode, sStartDay, sEndDay, nChoice, !StringUtil.isEmpty(hData.get("ck13")), StringUtil.convertString(hData.get("sd12")));
					break;
				case "opt14" : 	//외부에서 내부로의 전체 탐지로그 & 신규 탐지 이벤트 현황 (차트, 표)
					push("항목: 외부에서 내부로의 전체 탐지로그 & 신규 탐지 이벤트 현황 (차트, 표)");
					New_DetectLog_Condition(reportData, INBOUND, assetCode, sStartDay, sEndDay);
					break;
				case "opt15" : 	//내부에서 외부로의 전체 탐지로그 & 신규 탐지 이벤트 현황 (차트, 표)
					push("항목: 내부에서 외부로의 전체 탐지로그 & 신규 탐지 이벤트 현황 (차트, 표)");
					New_DetectLog_Condition(reportData, OUTBOUND, assetCode, sStartDay, sEndDay);
					break;
				case "opt16" : 	//외부에서 내부로의 전체 탐지로그 & 이전대비 2배증가된 이벤트 현황 (차트, 표)
					push("항목: 외부에서 내부로의 전체 탐지로그 & 이전대비 2배증가된 이벤트 현황 (차트, 표)");
					Double_DetectLog_Condition(reportData, INBOUND, assetCode, sStartDay, sEndDay);
					break;
				case "opt17" : 	//외부에서 내부로의 전체 탐지로그 & SIP TOP (표)
					push("항목: 외부에서 내부로의 전체 탐지로그 & SIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd17")));
					Action_DetectLog_TopN(reportData, INBOUND, "", assetCode, sStartDay, sEndDay, true, nChoice, !StringUtil.isEmpty(hData.get("ck17")), StringUtil.convertString(hData.get("sd17")));
					break;
				case "opt18" : 	//외부에서 내부로의 허용 탐지로그 & SIP TOP (표)
					push("항목: 외부에서 내부로의 허용 탐지로그 & SIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd18")));
					Action_DetectLog_TopN(reportData, INBOUND, ALLOW, assetCode, sStartDay, sEndDay, true, nChoice, !StringUtil.isEmpty(hData.get("ck18")), StringUtil.convertString(hData.get("sd18")));
					break;
				case "opt19" : 	//외부에서 내부로의 차단 탐지로그 & SIP TOP (표)
					push("항목: 외부에서 내부로의 차단 탐지로그 & SIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd19")));
					Action_DetectLog_TopN(reportData, INBOUND, CUTOFF, assetCode, sStartDay, sEndDay, true, nChoice, !StringUtil.isEmpty(hData.get("ck19")), StringUtil.convertString(hData.get("sd19")));
					break;
				case "opt20" : 	//내부에서 외부로의 전체 탐지로그 & SIP TOP (표)
					push("항목: 내부에서 외부로의 전체 탐지로그 & SIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd20")));
					Action_DetectLog_TopN(reportData, OUTBOUND, "", assetCode, sStartDay, sEndDay, true, nChoice, !StringUtil.isEmpty(hData.get("ck20")), StringUtil.convertString(hData.get("sd20")));
					break;
				case "opt21" : 	//내부에서 외부로의 허용 탐지로그 & SIP TOP (표)
					push("항목: 내부에서 외부로의 허용 탐지로그 & SIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd21")));
					Action_DetectLog_TopN(reportData, OUTBOUND, ALLOW, assetCode, sStartDay, sEndDay, true, nChoice, !StringUtil.isEmpty(hData.get("ck21")), StringUtil.convertString(hData.get("sd21")));
					break;
				case "opt22" : 	//내부에서 외부로의 차단 탐지로그 & SIP TOP (표)
					push("항목: 내부에서 외부로의 차단 탐지로그 & SIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd22")));
					Action_DetectLog_TopN(reportData, OUTBOUND, CUTOFF, assetCode, sStartDay, sEndDay, true, nChoice, !StringUtil.isEmpty(hData.get("ck22")), StringUtil.convertString(hData.get("sd22")));
					break;
				case "opt23" : 	//외부에서 내부로의 전체 탐지로그 & DIP TOP (표)
					push("항목: 외부에서 내부로의 전체 탐지로그 & DIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd23")));
					Action_DetectLog_TopN(reportData, INBOUND, "", assetCode, sStartDay, sEndDay, false, nChoice, false, "");
					break;
				case "opt24" : 	//외부에서 내부로의 허용 탐지로그 & DIP TOP (표)
					push("항목: 외부에서 내부로의 허용 탐지로그 & DIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd24")));
					Action_DetectLog_TopN(reportData, INBOUND, ALLOW, assetCode, sStartDay, sEndDay, false, nChoice, false, "");
					break;
				case "opt25" : 	//외부에서 내부로의 차단 탐지로그 & DIP TOP (표)
					push("항목: 외부에서 내부로의 차단 탐지로그 & DIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd25")));
					Action_DetectLog_TopN(reportData, INBOUND, CUTOFF, assetCode, sStartDay, sEndDay, false, nChoice, false, "");
					break;
				case "opt26" : 	//내부에서 외부로의 전체 탐지로그 & DIP TOP (표)
					push("항목: 내부에서 외부로의 전체 탐지로그 & DIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd26")));
					Action_DetectLog_TopN(reportData, OUTBOUND, "", assetCode, sStartDay, sEndDay, false, nChoice, false, "");
					break;
				case "opt27" : 	//내부에서 외부로의 허용 탐지로그 & DIP TOP (표)
					push("항목: 내부에서 외부로의 허용 탐지로그 & DIP TOP (표))");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd27")));
					Action_DetectLog_TopN(reportData, OUTBOUND, ALLOW, assetCode, sStartDay, sEndDay, false, nChoice, false, "");
					break;
				case "opt28" : 	//내부에서 외부로의 차단 탐지로그 & DIP TOP (표)
					push("항목: 내부에서 외부로의 차단 탐지로그 & DIP TOP (표)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd28")));
					Action_DetectLog_TopN(reportData, OUTBOUND, CUTOFF, assetCode, sStartDay, sEndDay, false, nChoice, false, "");
					break;
				case "opt29" : 	//외부에서 내부로의 전체 탐지로그 & 서비스 TOP10 (차트)
					push("항목: 외부에서 내부로의 전체 탐지로그 & 서비스 TOP10 (차트)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd29")));
					ALL_ServiceTop10(reportData, INBOUND, assetCode, sStartDay, sEndDay, !StringUtil.isEmpty(hData.get("ck29")));
					break;
				case "opt30" : 	//내부에서 외부로의 전체 탐지로그 & 서비스 TOP10 (차트)
					push("항목: 내부에서 외부로의 전체 탐지로그 & 서비스 TOP10 (차트)");
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd30")));
					ALL_ServiceTop10(reportData, OUTBOUND, assetCode, sStartDay, sEndDay, !StringUtil.isEmpty(hData.get("ck30")));
					break;
					
			}
		}
		return reportData;
	}

	private void All_DetectLog_Trend(HashMap<String, Object> reportData, int nDirection, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();

		Iterable<DBObject> dbResult = ipsDao.AllDetectLogTrend("MON", 1, assetCode, ipsDao.addMonth(sStartDay, -5), sEndDay);

    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
    	for (DBObject val : dbResult) {
    		String sDirection = (String)val.get("direction");
    		String sYear = ((Integer)val.get("year")).toString();
    		String sMonth = ((Integer)val.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    		mapResult.put(sDirection + sYear + sMonth, val);
    	}
    	
    	List<String> monthPeriod = ipsDao.getPeriodMonth(sEndDay, -5, 1);
    	
    	String[] saGubun = {"아웃바운드", "인바운드"};
    	for (int direction = 0; direction < 2; direction++) { //아웃바운드/인바운드
    		for (String tMonth : monthPeriod) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		DBObject val = mapResult.get(String.valueOf(direction) + tMonth);
	    		map.put("gubun", saGubun[direction]);
	    		map.put("month", Integer.valueOf(tMonth));
	    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);

	    		dataSource.add(map);
	    		logger.debug(map.toString());
    		}	    		
    	}
    	
		dbResult = ipsDao.AllDetectLogTrend("MON", -1, assetCode, ipsDao.addMonth(sStartDay, -5), sEndDay);
		
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
    		map.put("day", Integer.valueOf(tMonth));
    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
    		
    		dataSource.add(map);
    		logger.debug(map.toString());
		}

		reportData.put("OPT1_1", new SynthesisDataSource(dataSource));
		reportData.put("OPT1_2", new SynthesisDataSource(dataSource));
	}
	
	private void Direction_DetectLog_Trend(HashMap<String, Object> reportData, int nDirection, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();
			
		Iterable<DBObject> dbResult = ipsDao.AllActionTrend("MON", nDirection, ALLOW, assetCode, ipsDao.addMonth(sStartDay, -5), sEndDay);

    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
    	for (DBObject val : dbResult) {
    		String sAction = (String)val.get("action");
    		String sYear = ((Integer)val.get("year")).toString();
    		String sMonth = ((Integer)val.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    		mapResult.put(sAction + sYear + sMonth, val);
    	}
    	
    	List<String> monthPeriod = ipsDao.getPeriodMonth(sEndDay, -5, 1);
    	
    	String[] saGubun = {"차단", "허용"};
    	for (int action = 0; action < 2; action++) { //차단/허용
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
    	
    	dbResult = ipsDao.AllActionTrend("MON", nDirection, "", assetCode, ipsDao.addMonth(sStartDay, -5), sEndDay);
    	
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
		
		if (nDirection == INBOUND) {
			reportData.put("OPT2_1", new SynthesisDataSource(dataSource));
			reportData.put("OPT2_2", new SynthesisDataSource(dataSource));
		} else {
			reportData.put("OPT5_1", new SynthesisDataSource(dataSource));
			reportData.put("OPT5_2", new SynthesisDataSource(dataSource));
		}
	}

	private void Compare_DetectLog_Trend(HashMap<String, Object> reportData, int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();

		Iterable<DBObject> dbResult = ipsDao.CompareDetectLogTrend("DY", nDirection, sAction, assetCode, ipsDao.addMonth(sStartDay, -1), sEndDay);

    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
    	for (DBObject val : dbResult) {
    		String sYear = ((Integer)val.get("year")).toString();
    		String sMonth = ((Integer)val.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    		String sDay = ((Integer)val.get("day")).toString();
    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
    		mapResult.put(sAction + sYear + sMonth + sDay, val);
    	}
    	
    	List<String> dayPeriod = ipsDao.getPeriodDay(sStartDay, sEndDay, -1, 1);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		Date endDate = sdf.parse(sEndDay);
		Calendar endCal = ipsDao.getCalendar(endDate);
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

		Iterable<DBObject> dbResult = ipsDao.EventTopN("MON", nDirection, "", assetCode, sStartDay, sEndDay, 0);

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
    	
    	Iterable<DBObject> dbBefore = ipsDao.EventTopN("MON", nDirection, "", assetCode, ipsDao.addMonth(sStartDay,-1), ipsDao.addMonth(sEndDay,-1), 0);
    	HashMap<String, DBObject> mapBefore = new HashMap<String, DBObject>(); 
    	for (DBObject val : dbBefore) {
    		mapBefore.put((String)val.get("action") + (String)val.get("message"), val);
    	}
    	
    	nNo = 1;
    	for (DBObject val : dbResult) {
    		String sMessage = (String)val.get("message");
    		String sAction = (String)val.get("action");
    		
    		DBObject prevMap = mapBefore.get(sAction + sMessage);
    		
    		Iterable<DBObject> srcIpTop5 = ipsDao.EventSrcIpTopN("MON", nDirection, sAction, assetCode, sStartDay, sEndDay, sMessage, 5);
    		for (DBObject sVal : srcIpTop5) {
    			HashMap<String, Object> map = new HashMap<String, Object>();
    			map.put("no", nNo);
    			map.put("message", sMessage);
    			map.put("action", sAction);
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
	    	reportData.put("OPT8_1", new SynthesisDataSource(dataSource));
	    	reportData.put("OPT8_2", new SynthesisDataSource(dataSource2));
    	} else {
	    	reportData.put("OPT11_1", new SynthesisDataSource(dataSource));
	    	reportData.put("OPT11_2", new SynthesisDataSource(dataSource2));
    	}
		
	}		
	
	//외부에서 내부로의 허용/차단 탐지로그 & 이벤트 TOP (차트, 표)
	private void Direction_Action_EventTopN(HashMap<String, Object> reportData, int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay, int nLimit, boolean bChk, String sOpt) throws Exception {
		
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> dataSource3 = new ArrayList<HashMap<String, Object>>();
		
		Iterable<DBObject> dbResult = ipsDao.EventTopN("MON", nDirection, sAction, assetCode, sStartDay, sEndDay, 0);

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
    	
    	Iterable<DBObject> dbBefore = ipsDao.EventTopN("MON", nDirection, sAction, assetCode, ipsDao.addMonth(sStartDay,-1), ipsDao.addMonth(sEndDay,-1), 0);
    	HashMap<String, DBObject> mapBefore = new HashMap<String, DBObject>(); 
    	for (DBObject val : dbBefore) {
    		mapBefore.put((String)val.get("action") + (String)val.get("message"), val);
    	}
    	
    	nNo = 1;
    	for (DBObject val : dbResult) {
    		String sMessage = (String)val.get("message");
    		
    		DBObject prevMap = mapBefore.get(sAction + sMessage);
    		
    		Iterable<DBObject> srcIpTop5 = ipsDao.EventSrcIpTopN("MON", nDirection, sAction, assetCode, sStartDay, sEndDay, sMessage, 5);
    		for (DBObject sVal : srcIpTop5) {
    			HashMap<String, Object> map = new HashMap<String, Object>();
    			map.put("no", nNo);
    			map.put("message", sMessage);
    			map.put("action", sAction);
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
    		
    		if (Integer.valueOf(sOpt) == 1) { //최근 1개월
    			
    	    	dbResult = ipsDao.EventTopNTrend("DY", nDirection, sAction, assetCode, sStartDay, sEndDay, saTopN);
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
        		
    	    	List<String> dayPeriod = ipsDao.getPeriodDay(sStartDay, sEndDay, 1);
    	    	
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
    			
    		} else {

    	    	dbResult = ipsDao.EventTopNTrend("MON", nDirection, sAction, assetCode, ipsDao.addMonth(sStartDay, -5), sEndDay, saTopN);
    	    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
    	    	for (DBObject val : dbResult) {
    	    		String sMessage = (String)val.get("message");
    	    		String sYear = ((Integer)val.get("year")).toString();
    	    		String sMonth = ((Integer)val.get("month")).toString();
    	    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    	    		mapResult.put(sMessage + "-" + sYear + sMonth, val);
    	    	}
        		
    	    	List<String> monthPeriod = ipsDao.getPeriodMonth(sEndDay, -5, 1);
    	    	
    	    	for (String sMessage : saTopN) {
    	    		for (String tMonth : monthPeriod) {
    		    		DBObject val = mapResult.get(sMessage + "-" + tMonth);
    		    		HashMap<String, Object> map = new HashMap<String, Object>();
    		    		map.put("gubun", sMessage);
    		    		map.put("month", Integer.valueOf(tMonth));
    		    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
    		    		
    		    		dataSource3.add(map);
    		    		logger.debug(map.toString());
    		    	}
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
	
	//외부에서 내부로의 전체 탐지로그 & 신규 탐지 이벤트 현황
	private void New_DetectLog_Condition(HashMap<String, Object> reportData, int nDirection, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();

    	//신규 탐지 이벤트리스트
    	Iterable<DBObject>  dbNewResult = ipsDao.EventTopN("MON", nDirection, assetCode, sStartDay, sEndDay, null);
    	HashMap<String, DBObject> newMap = new HashMap<String, DBObject>();
    	for (DBObject val : dbNewResult) {
    		newMap.put((String)val.get("action") + "-" + (String)val.get("message"), val);
    	}

		//전일 탐지 이벤트리스트
		Iterable<DBObject> dbResult = ipsDao.EventTopN("MON", nDirection,assetCode, ipsDao.addMonth(sStartDay,-1), ipsDao.addMonth(sEndDay,-1), null);
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
				String o1 = (String)m1.get("count");
				String o2 = (String)m2.get("count");
				if (o1 == null || o2 == null) {
					return 0;
				}
				return o1.compareTo(o2);
			}
		};

		Collections.sort(newList, comparator);
    	
		List<String> dayPeriod = ipsDao.getPeriodDay(sStartDay, sEndDay, 1);
		
		int nNo = 1;
		for (DBObject mVal : newList) {
			String sMessage = (String)mVal.get("message");
			String sAction = (String)mVal.get("action");

	    	dbResult = ipsDao.EventTopNTrend("DY", nDirection, sAction, assetCode, sStartDay, sEndDay, sMessage);
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
	    		DBObject pVal = mapResult.get(tDay);
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		map.put("gubun", sMessage);
	    		map.put("day", Integer.valueOf(tDay));
	    		map.put("count", pVal != null ? ((Number)pVal.get("count")).longValue() : 0);
	    		
	    		dataSource1.add(map);
	    		logger.debug(map.toString());
	    	}
    		if (nNo >= 10) break;
    		nNo++;
		}
		
    	nNo = 1;
    	for (DBObject mVal : newList) {
    		String sMessage = (String)mVal.get("message");
    		String sAction = (String)mVal.get("action");

			Iterable<DBObject> srcIpTop5 = ipsDao.EventSrcIpTopN("MON", nDirection, sAction, assetCode, sStartDay, sEndDay, sMessage, 5);
			for (DBObject sVal : srcIpTop5) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("no", nNo);
				map.put("message", sMessage);
				map.put("action", sAction);
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
    		reportData.put("OPT14_1", new SynthesisDataSource(dataSource1));
    		reportData.put("OPT14_2", new SynthesisDataSource(dataSource2));
    	} else {
    		reportData.put("OPT15_1", new SynthesisDataSource(dataSource1));
    		reportData.put("OPT15_2", new SynthesisDataSource(dataSource2));
    	}
	}		
	
	//외부에서 내부로의 전체 탐지로그 & 이전대비 2배증가된 이벤트 현황
	private void Double_DetectLog_Condition(HashMap<String, Object> reportData, int nDirection, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();

		//전주 탐지 이벤트리스트
		Iterable<DBObject> dbBefore = ipsDao.EventTopN("MON", nDirection, assetCode, ipsDao.addMonth(sStartDay,-1), ipsDao.addMonth(sEndDay,-1), null);
		HashMap<String, DBObject> mapBefore = new HashMap<String, DBObject>();
    	for (DBObject val : dbBefore) {
    		mapBefore.put((String)val.get("action") + "-" + (String)val.get("message"), val);
    	}

    	//신규 탐지 이벤트리스트
    	List<DBObject> dblList = new ArrayList<DBObject>();
    	List<String> saDbl = new ArrayList<String>();
    	Iterable<DBObject> dbResult = ipsDao.EventTopN("MON", nDirection, assetCode, sStartDay, sEndDay, null);
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
    	
    	List<String> dayPeriod = ipsDao.getPeriodDay(sStartDay, sEndDay, 1);
		
		int nNo = 1;
		for (DBObject mVal : dblList) {
			String sMessage = (String)mVal.get("message");
			String sAction = (String)mVal.get("action");

	    	dbResult = ipsDao.EventTopNTrend("DY", nDirection, sAction, assetCode, sStartDay, sEndDay, sMessage);
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
	    		DBObject val = mapResult.get(tDay);
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		map.put("gubun", sMessage);
	    		map.put("day", Integer.valueOf(tDay));
	    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
	    		
	    		dataSource1.add(map);
	    		logger.debug(map.toString());
	    	}
    		if (nNo >= 10) break;
    		nNo++;
		}
    	
    	nNo = 1;
    	for (DBObject val : dblList) {
    		String sMessage = (String)val.get("message");
    		String sAction = (String)val.get("action");

			Iterable<DBObject> srcIpTop5 = ipsDao.EventSrcIpTopN("MON", nDirection, sAction, assetCode, sStartDay, sEndDay, sMessage, 5);
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
	    		
	    		dataSource2.add(map);
	    		logger.debug(map.toString());
			}
			if (nNo >= 30) break;
			nNo++;
    	}
    	
		reportData.put("OPT16_1", new SynthesisDataSource(dataSource1));
		reportData.put("OPT16_2", new SynthesisDataSource(dataSource2));
	}		
	
	//외부에서 내부로의 전체 탐지로그 & SIP TOP
	private void Action_DetectLog_TopN(HashMap<String, Object> reportData, int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay, boolean bSrcIp, int nLimit, boolean bChk, String sOpt) throws Exception {
		
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();
		
    	//IP 건수기준 TOP N 리스트
		Iterable<DBObject> ipTopNResult = ipsDao.IpTopN("MON", nDirection, sAction, assetCode, sStartDay, sEndDay, bSrcIp, nLimit);
    	List<String> topNIps = new ArrayList<String>();
    	for (DBObject val : ipTopNResult) {
    		if (bSrcIp) {    		
    			topNIps.add((String)val.get("srcIp"));
    		} else {
    			topNIps.add((String)val.get("destIp"));
    		}
    	}

    	//전월 현황
    	Iterable<DBObject> dbBefore = ipsDao.BeforeIpCondition("MON", nDirection, sAction, assetCode, ipsDao.addMonth(sStartDay,-1), ipsDao.addMonth(sEndDay,-1), bSrcIp, topNIps);
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
    			ipTopNCondition = ipsDao.IpTopNCondition("MON", nDirection, sAction, assetCode, sStartDay, sEndDay, (String)val.get("srcIp"), bSrcIp);
    		} else {
    			ipTopNCondition = ipsDao.IpTopNCondition("MON", nDirection, sAction, assetCode, sStartDay, sEndDay, (String)val.get("destIp"), bSrcIp);
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
	    		reportData.put("OPT17_1", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == INBOUND && sAction.equals(ALLOW)) { //외부에서 내부로의 허용 세션로그 & SIP TOP (표)
	    		reportData.put("OPT18_1", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == INBOUND && sAction.equals(CUTOFF)) { //외부에서 내부로의 차단 세션로그 & SIP TOP (표)
	    		reportData.put("OPT19_1", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == OUTBOUND && StringUtil.isEmpty(sAction)) { //내부에서 외부로의 전체 세션로그 & SIP TOP (표)
	    		reportData.put("OPT20_1", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == OUTBOUND && sAction.equals(ALLOW)) { //내부에서 외부로의 허용 세션로그 & SIP TOP (표)
	    		reportData.put("OPT21_1", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == OUTBOUND && sAction.equals(CUTOFF)) { //내부에서 외부로의 차단 세션로그 & SIP TOP (표)
	    		reportData.put("OPT22_1", new SynthesisDataSource(dataSource1));
	    	}
	    	
	    	if (topNIps.size() > 10) {
	    		topNIps = topNIps.subList(0, 10);
	    	}
	    	
	    	if (bChk) { //SRC IP TOP10 세션 로그 발생추이 
	    		ArrayList<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();
	    		
	    		if (Integer.valueOf(sOpt) == 1) { //최근 1개월

		        	Iterable<DBObject> ipTopNTrend = ipsDao.IpTopNTrend("DY", nDirection, sAction, assetCode, sStartDay, sEndDay, topNIps, true);
		        	
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
		        	
		        	List<String> dayPeriod = ipsDao.getPeriodDay(sStartDay, sEndDay, 1);
		        	
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
	    			
	    		} else {

		        	Iterable<DBObject> ipTopNTrend = ipsDao.IpTopNTrend("MON", nDirection, sAction, assetCode, ipsDao.addMonth(sStartDay, -2), sEndDay, topNIps, true);
		        	
		        	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
		        	for (DBObject val : ipTopNTrend) {
		        		String sSrcIp = (String)val.get("srcIp");
		        		String sYear = ((Integer)val.get("year")).toString();
		        		String sMonth = ((Integer)val.get("month")).toString();
		        		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
		        		mapResult.put(sSrcIp + "-" + sYear + sMonth, val);
		        	}
		        	
		        	List<String> monthPeriod = ipsDao.getPeriodMonth(sEndDay, -2, 1);
		        	
		        	for (String strIp : topNIps) {
		        		for (String tMonth : monthPeriod) {
	    		    		DBObject val = mapResult.get(strIp + "-" + tMonth);
	    		    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		    		map.put("srcIp", strIp);
	    		    		map.put("month", Integer.valueOf(tMonth));
	    		    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
	    		    		dataSource2.add(map);
	    					logger.debug(map.toString());
		        		}
		        	}
	    		}
	    		
	        	if (nDirection == INBOUND && StringUtil.isEmpty(sAction)) {
		    		reportData.put("OPT17_2", new SynthesisDataSource(dataSource2));
	        	} else if (nDirection == INBOUND && sAction.equals(ALLOW)) {
	        		reportData.put("OPT18_2", new SynthesisDataSource(dataSource2));
	        	} else if (nDirection == INBOUND && sAction.equals(CUTOFF)) {
	        		reportData.put("OPT19_2", new SynthesisDataSource(dataSource2));
		    	} else if (nDirection == OUTBOUND && StringUtil.isEmpty(sAction)) {
		    		reportData.put("OPT20_2", new SynthesisDataSource(dataSource2));
		    	} else if (nDirection == OUTBOUND && sAction.equals(ALLOW)) {
		    		reportData.put("OPT21_2", new SynthesisDataSource(dataSource2));
		    	} else if (nDirection == OUTBOUND && sAction.equals(CUTOFF)) {
		    		reportData.put("OPT22_2", new SynthesisDataSource(dataSource2));
		    	}
	    	}
    	} else {
	    	if (nDirection == INBOUND && StringUtil.isEmpty(sAction)) { //외부에서 내부로의 전체 탐지로그 & DIP TOP (표)
	    		reportData.put("OPT23", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == INBOUND && sAction.equals(ALLOW)) { //외부에서 내부로의 허용 탐지로그 & DIP TOP (표)
	    		reportData.put("OPT24", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == INBOUND && sAction.equals(CUTOFF)) { //외부에서 내부로의 차단 탐지로그 & DIP TOP (표)
	    		reportData.put("OPT25", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == OUTBOUND && StringUtil.isEmpty(sAction)) { //내부에서 외부로의 전체 탐지로그 & DIP TOP (표)
	    		reportData.put("OPT26", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == OUTBOUND && sAction.equals(ALLOW)) { //내부에서 외부로의 허용 탐지로그 & DIP TOP (표)
	    		reportData.put("OPT27", new SynthesisDataSource(dataSource1));
	    	} else if (nDirection == OUTBOUND && sAction.equals(CUTOFF)) { //내부에서 외부로의 차단 탐지로그 & DIP TOP (표)
	    		reportData.put("OPT28", new SynthesisDataSource(dataSource1));
	    	}
    	}
	}
	
	private void ALL_ServiceTop10(HashMap<String, Object> reportData, int nDirection, int assetCode, String sStartDay, String sEndDay, boolean bChk) throws Exception {
		
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();

    	Iterable<DBObject> dbResult = ipsDao.ServiceDetectLog("MON", nDirection, assetCode, sStartDay, sEndDay, 0, false, 0);
    	
    	int nTotal = 0;
    	HashMap<Integer, DBObject> svcMap = new HashMap<Integer, DBObject>();
		for (DBObject val : dbResult) {
    		val.put("allow", 0);
    		val.put("cutoff", 0);
    		svcMap.put((Integer)val.get("destPort"), val);
    		nTotal += ((Number)val.get("count")).longValue();
		}
		
		Iterable<DBObject> actionResult = ipsDao.ServiceDetectLog("MON", nDirection, assetCode, sStartDay, sEndDay, 0, true, 0);
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
    		reportData.put("OPT29_1", new SynthesisDataSource(dataSource1));
    	} else {
    		reportData.put("OPT30_1", new SynthesisDataSource(dataSource1));
    	}
    	
    	if (bChk) { //서비스 TOP10 증감현황 및 이벤트유형
    	
    		//표 [순위 | Port | 서비스명 | 전체건수 | 증감현황 | 이벤트명 | Action | 비율(%)]
    		dbResult = ipsDao.ServiceDetectLog("MON", nDirection, assetCode, ipsDao.addMonth(sStartDay,-1), ipsDao.addMonth(sEndDay,-1), 0, false, 0);
        	HashMap<String, DBObject> mapBefore = new HashMap<String, DBObject>(); 
        	for (DBObject val : dbResult) {
        		mapBefore.put(String.valueOf((Integer)val.get("destPort")), val);
        	}
        	
        	List<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();
        	
        	nNo = 1;
        	for (HashMap<String, Object> hMap : dataSource1) {
        		String sPort = (String)hMap.get("destPort");
        		
        		if (sPort.equals("기타")) continue;
        		
        		Iterable<DBObject> dbTmp = ipsDao.ServiceEventTopN("MON", assetCode, sStartDay, sEndDay, Integer.valueOf(sPort));
        		for (DBObject pMap : dbTmp) {
            		HashMap<String, Object> map = new HashMap<String, Object>();
            		map.put("no", nNo);
            		map.put("destPort", sPort);
            		map.put("total", ((Number)hMap.get("count")).longValue());
            		
            		DBObject prevMap = mapBefore.get(sPort);
            		map.put("prevCount", prevMap != null ? ((Number)prevMap.get("count")).longValue() : 0);
            		map.put("message", (String)pMap.get("message"));
            		map.put("action", (String)pMap.get("action"));
            		map.put("count", ((Number)pMap.get("count")).longValue());
            		
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




