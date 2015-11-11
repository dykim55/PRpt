package com.cyberone.report.core.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.cyberone.report.core.dao.IPSReportDao;
import com.cyberone.report.core.datasource.SynthesisDataSource;
import com.mongodb.DBObject;

public class IPSService extends BaseService {

	private int Depth1 = 1;
	private List<String> contentsList = new ArrayList<String>();
	private IPSReportDao reportDao;
	
	public IPSService(IPSReportDao reportDao) {
		this.reportDao = reportDao;
	}
	
	private void IPS_PerformanceLog(String sStartDay, String sEndDay, int assetCode, HashMap<String, Object> assetReportInfo) throws Exception {
		
		boolean bPerformanceExist = false;
		
		List<String> dayPeriod = reportDao.getPeriodDay(sStartDay, sEndDay, -1, 1);
		
		List<DBObject> pfDbInfo = reportDao.getPerformanceMonth(assetCode, sEndDay);    	
			
		int nIndex = 1;
    	for (DBObject mVal : pfDbInfo) {
        	if (!bPerformanceExist) {
        		assetReportInfo.put("SubChapter24", addContents(Depth1 + ".5 월간 성능 추이", "  "));
        	}
    		bPerformanceExist = true;

    		String sName = (String)mVal.get("name");

    		if (sName.equals("cpu")) {
    			assetReportInfo.put("PF_T_" + sName, addContents(Depth1 + ".5." + nIndex++ + " CPU 추이", "    "));
    		} else if (sName.equals("disk")) {
    			assetReportInfo.put("PF_T_" + sName, addContents(Depth1 + ".5." + nIndex++ + " 디스크 추이", "    "));
    		} else if (sName.equals("mem/real")) {
    			assetReportInfo.put("PF_T_" + sName, addContents(Depth1 + ".5." + nIndex++ + " 메모리 추이(Real)", "    "));
    		} else if (sName.equals("mem/swap")) {
    			assetReportInfo.put("PF_T_" + sName, addContents(Depth1 + ".5." + nIndex++ + " 메모리 추이(Swap)", "    "));
    		} else if (sName.equals("net/rx")) {
    			assetReportInfo.put("PF_T_" + sName, addContents(Depth1 + ".5." + nIndex++ + " 트래픽 추이(RX)", "    "));
    		} else if (sName.equals("net/tx")) {
    			assetReportInfo.put("PF_T_" + sName, addContents(Depth1 + ".5." + nIndex++ + " 트래픽 추이(TX)", "    "));
    		} else if (sName.equals("session")) {
    			assetReportInfo.put("PF_T_" + sName, addContents(Depth1 + ".5." + nIndex++ + " 세션 추이", "    "));
    		}
    		
    		List<HashMap<String, Object>> pfMonthInfo = new ArrayList<HashMap<String, Object>>();
    		
    		HashMap<String, Object> map = new HashMap<String, Object>();
    		map.put("gubun", sName);
    		map.put("max", ((Number)mVal.get("max")).longValue());
    		map.put("avg", ((Number)mVal.get("avg")).longValue());
    		map.put("min", ((Number)mVal.get("min")).longValue());
    		pfMonthInfo.add(map);
        	assetReportInfo.put("PF_M_" + sName, new SynthesisDataSource(pfMonthInfo));
    		
        	Iterable<DBObject> pfAvgResult = reportDao.getPerformanceChange(assetCode, sStartDay, sEndDay, sName);
    		
        	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
        	for (DBObject dVal : pfAvgResult) {
        		String sYear = ((Integer)dVal.get("year")).toString();
        		String sMonth = ((Integer)dVal.get("month")).toString();
        		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
        		String sDay = ((Integer)dVal.get("day")).toString();
        		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
        		mapResult.put(sYear + sMonth + sDay, dVal);
        	}
        	
        	List<HashMap<String, Object>> pfDayInfo = new ArrayList<HashMap<String, Object>>();
        	
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = reportDao.getCalendar(endDate);
			endCal.add(Calendar.MONTH, -2);

        	for (int i = 0; i < dayPeriod.size(); i++) {
        		DBObject tmpVal = mapResult.get(dayPeriod.get(i));
        		
        		if (Integer.valueOf(dayPeriod.get(i).substring(6,8)) == Integer.valueOf(sStartDay.substring(8, 10))) {
        			endCal.add(Calendar.MONTH, 1);
        		}
        		
        		HashMap<String, Object> tmpMap = new HashMap<String, Object>();
        		tmpMap.put("gubun", (endCal.get(Calendar.MONTH)+1) + "월 일평균");
        		tmpMap.put("day", Integer.valueOf(dayPeriod.get(i).substring(6,8)));
        		tmpMap.put("avg", tmpVal != null ? ((Number)tmpVal.get("avg")).longValue() : 0);
        		pfDayInfo.add(tmpMap);
        	}
        	
			endDate = sdf.parse(sEndDay);
			endCal = reportDao.getCalendar(endDate);
			endCal.add(Calendar.MONTH, -2);

        	for (int i = 0; i < dayPeriod.size(); i++) {
        		DBObject tmpVal = mapResult.get(dayPeriod.get(i));
        		
        		if (Integer.valueOf(dayPeriod.get(i).substring(6,8)) == Integer.valueOf(sStartDay.substring(8, 10))) {
        			endCal.add(Calendar.MONTH, 1);
        		}
        		
        		HashMap<String, Object> tmpMap = new HashMap<String, Object>();
        		tmpMap.put("gubun", (endCal.get(Calendar.MONTH)+1) + "월 일최고");
        		tmpMap.put("day", Integer.valueOf(dayPeriod.get(i).substring(6,8)));
        		tmpMap.put("avg", tmpVal != null ? ((Number)tmpVal.get("max")).longValue() : 0);
        		pfDayInfo.add(tmpMap);
        	}
        	assetReportInfo.put("PF_D_" + sName, new SynthesisDataSource(pfDayInfo));
    	}
    	
    	assetReportInfo.put("PF_EXIST", bPerformanceExist);
	}
	
	private void IPS_Top10DailyEventChange(String sStartDay, String sEndDay, int assetCode, HashMap<String, Object> assetReportInfo, int nAction) throws Exception {

		if (nAction == 1) {
			//허용 이벤트 TOP10
			assetReportInfo.put("SubChapter6", addContents(Depth1 + ".3 월간 허용 이벤트 상세 분석", "  "));
			
	    	List<HashMap<String, Object>> IPS_6_3_1_G = new ArrayList<HashMap<String, Object>>();
	    	List<DBObject> dbTop10 = reportDao.IPS_TopInfoMonth(assetCode, sEndDay, ALLOW, 1, 10); //message
	    	
	    	List<String> saTop10Event = new ArrayList<String>();
	    	for (DBObject val : dbTop10) {
	    		saTop10Event.add((String)val.get("message"));
	    	}
	    	
	    	List<DBObject> dbResult = reportDao.IPS_Top10EventChange(assetCode, sStartDay, sEndDay, ALLOW, saTop10Event);
	    	
	    	List<String> dayPeriod = reportDao.getPeriodDay(sStartDay, sEndDay, 0, 1);
	
	    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
	    	for (DBObject val : dbResult) {
	    		String sEvent = (String)val.get("message");
	    		String sYear = ((Integer)val.get("year")).toString();
	    		String sMonth = ((Integer)val.get("month")).toString();
	    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
	    		String sDay = ((Integer)val.get("day")).toString();
	    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
	    		mapResult.put(sEvent + "-" + sYear + sMonth + sDay, val);
	    	}
	    	
	    	for (String strEvent : saTop10Event) {
		    	for (int i = 0; i < dayPeriod.size(); i++) {
		    		DBObject val = mapResult.get(strEvent + "-" + dayPeriod.get(i));
	
		    		HashMap<String, Object> map = new HashMap<String, Object>();
		    		map.put("gubun", strEvent);
		    		map.put("day", Integer.valueOf(dayPeriod.get(i).substring(6,8)));
		    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
		    		IPS_6_3_1_G.add(map);
		    	}
	    	}
	    	assetReportInfo.put("IPS_6_3_1_G", new SynthesisDataSource(IPS_6_3_1_G));
	    	assetReportInfo.put("SubChapter7", addContents(Depth1 + ".3.1 월간 허용 이벤트 TOP10 일별 발생 추이", "    "));
		} else {    	
	    	//차단 이벤트 TOP10
	    	assetReportInfo.put("SubChapter15", addContents(Depth1 + ".4 월간 차단 이벤트 상세 분석", "  "));
	    	
	    	List<HashMap<String, Object>> IPS_6_4_1_G = new ArrayList<HashMap<String, Object>>();
	    	List<DBObject> dbTop10 = reportDao.IPS_TopInfoMonth(assetCode, sEndDay, CUTOFF, 1, 10); //message
	    	
	    	List<String> saTop10Event = new ArrayList<String>();
	    	for (DBObject val : dbTop10) {
	    		saTop10Event.add((String)val.get("message"));
	    	}
	    	
	    	List<DBObject> dbResult = reportDao.IPS_Top10EventChange(assetCode, sStartDay, sEndDay, CUTOFF, saTop10Event);
	    	
	    	List<String> dayPeriod = reportDao.getPeriodDay(sStartDay, sEndDay, 0, 1);
	
	    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
	    	for (DBObject val : dbResult) {
	    		String sEvent = (String)val.get("message");
	    		String sYear = ((Integer)val.get("year")).toString();
	    		String sMonth = ((Integer)val.get("month")).toString();
	    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
	    		String sDay = ((Integer)val.get("day")).toString();
	    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
	    		mapResult.put(sEvent + "-" + sYear + sMonth + sDay, val);
	    	}
	    	
	    	for (String strEvent : saTop10Event) {
		    	for (int i = 0; i < dayPeriod.size(); i++) {
		    		DBObject val = mapResult.get(strEvent + "-" + dayPeriod.get(i));
	
		    		HashMap<String, Object> map = new HashMap<String, Object>();
		    		map.put("gubun", strEvent);
		    		map.put("day", Integer.valueOf(dayPeriod.get(i).substring(6,8)));
		    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
		    		IPS_6_4_1_G.add(map);
		    	}
	    	}
	    	assetReportInfo.put("IPS_6_4_1_G", new SynthesisDataSource(IPS_6_4_1_G));
	    	assetReportInfo.put("SubChapter16", addContents(Depth1 + ".4.1 월간 차단 이벤트 TOP10 일별 발생 추이", "    "));
		}
	}
	
	@SuppressWarnings("unchecked")
	private void IPS_Top10MonthlyChart(String sStartDay, String sEndDay, int assetCode, HashMap<String, Object> assetReportInfo, int nAction) throws Exception {

		List<String> monthPeriod = reportDao.getPeriodMonth(sEndDay, -1, 1);
		
		//허용/차단 이벤트 TOP10
    	List<HashMap<String, Object>> IPS_MAP_G = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> IPS_MAP_T = new ArrayList<HashMap<String, Object>>();
    	
    	List<DBObject> dbTop10 = reportDao.IPS_TopInfoMonth(assetCode, sEndDay, String.valueOf(nAction), 1, 10); //message
    	
    	for (DBObject val : dbTop10) {
    		HashMap<String, Object> map = new HashMap<String, Object>();
    		map.put("gubun", (String)val.get("message"));
    		map.put("count", ((Number)val.get("count")).longValue());
   			IPS_MAP_G.add(map);
    	}
    	
    	if (nAction == 0) {
    		assetReportInfo.put("IPS_6_4_2_G", new SynthesisDataSource(IPS_MAP_G));
    		assetReportInfo.put("SubChapter17", addContents(Depth1 + ".4.2 월간 차단 이벤트 TOP10", "    "));
    	} else {
    		assetReportInfo.put("IPS_6_3_2_G", new SynthesisDataSource(IPS_MAP_G));
    		assetReportInfo.put("SubChapter8", addContents(Depth1 + ".3.2 월간 허용 이벤트 TOP10", "    "));
    	}

    	List<DBObject> dbResult = reportDao.IPS_DataVariation(assetCode, sEndDay, String.valueOf(nAction), 1);
    	
    	HashMap<String, Object> currMap = new HashMap<String, Object>(); 
    	HashMap<String, Object> prevMap = new HashMap<String, Object>();
    	
    	int nTop = 1;
    	for (DBObject val : dbResult) {
    		String sYear = ((Integer)val.get("year")).toString();
    		String sMonth = ((Integer)val.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("year", sYear);
			map.put("month", sMonth);
			map.put("message", (String)val.get("message"));
			map.put("count", ((Number)val.get("count")).longValue());
    		
			if (monthPeriod.get(0).equals(sYear + sMonth)) {
				prevMap.put((String)val.get("message"), map);
			} else if (monthPeriod.get(1).equals(sYear + sMonth)) {
			
				if ((String)val.get("message") != "-1" && nTop <= 10) {
    				HashMap<String, Object> map1 = new HashMap<String, Object>();
    				HashMap<String, Object> pMap = (HashMap<String, Object>)prevMap.remove(val.get("message"));
					
    				long lCurrCount = ((Number)val.get("count")).longValue();
    				long lPrevCount = pMap != null ? ((Number)pMap.get("count")).longValue() : 0;

    				map1.put("no", nTop);		
    				map1.put("message", (String)val.get("message"));
    				map1.put("count", lCurrCount);
    				map1.put("prevCount", lPrevCount);
    				map1.put("countVariation", lCurrCount - lPrevCount);
    				map1.put("ratio", lPrevCount > 0 ? ((lCurrCount - lPrevCount) * 100f) / lPrevCount : 0);
    				
    				nTop++;
    				
   					IPS_MAP_T.add(map1);
				} else {
					currMap.put((String)val.get("message"), map);					
				}
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
    	
    	if (lCurrEtcCount > 0 || lPrevEtcCount > 0) {
	    	HashMap<String, Object> map1 = new HashMap<String, Object>();
			map1.put("no", nTop);		
			map1.put("message", "기타");
			map1.put("count", lCurrEtcCount);
			map1.put("prevCount", lPrevEtcCount);
			map1.put("countVariation", lCurrEtcCount - lPrevEtcCount);
			map1.put("ratio", lPrevEtcCount > 0 ? ((lCurrEtcCount - lPrevEtcCount) * 100f) / lPrevEtcCount : 0);
			IPS_MAP_T.add(map1);
    	}
		
    	if (nAction == 0) {
			assetReportInfo.put("IPS_6_4_2_T", new SynthesisDataSource(IPS_MAP_T));
			assetReportInfo.put("SubChapter18", addContents(Depth1 + ".4.3 월간 차단 이벤트 TOP10 증감 현황 (전월비교)", "    "));
    	} else {
			assetReportInfo.put("IPS_6_3_2_T", new SynthesisDataSource(IPS_MAP_T));
			assetReportInfo.put("SubChapter9", addContents(Depth1 + ".3.3 월간 허용 이벤트 TOP10 증감 현황 (전월비교)", "    "));
    	}
	}
	
	@SuppressWarnings("unchecked")
	private void IPS_ServiceMonthPie(String sStartDay, String sEndDay, int assetCode, HashMap<String, Object> assetReportInfo, int nAction) throws Exception {
	
		List<String> monthPeriod = reportDao.getPeriodMonth(sEndDay, -1, 1);
		
    	List<HashMap<String, Object>> IPS_MAP_G = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> IPS_MAP_T = new ArrayList<HashMap<String, Object>>();
		
    	List<DBObject> dbTop10 = reportDao.IPS_TopInfoMonth(assetCode, sEndDay, String.valueOf(nAction), 2, 0); //destPort
		
		HashMap<String, Object> etcMap = new HashMap<String, Object>();

		int nTop = 1;
		long lCount = 0;
		long lTotalCount = 0;
    	for (DBObject val : dbTop10) {
    		lCount = ((Number)val.get("count")).longValue();
    		HashMap<String, Object> map = new HashMap<String, Object>();
    		map.put("port", (Integer)val.get("destPort"));
    		map.put("svcName", reportDao.getPortServiceName((Integer)val.get("destPort")));
    		map.put("count", lCount);

    		lTotalCount += lCount;
    		
    		if ((Integer)val.get("destPort") != -1 && nTop <= 10) {
    			IPS_MAP_G.add(map);
	    		nTop++;
			} else {
				etcMap.put(String.valueOf((Integer)val.get("destPort")), map);
			}
    	}
    	
    	long lEtcCount = 0;
    	Set<Entry<String, Object>> etcEntry = etcMap.entrySet();
    	for (Entry<String, Object> obj : etcEntry) {
    		HashMap<String,Object> map = (HashMap<String,Object>)obj.getValue();
    		lEtcCount += ((Number)map.get("count")).longValue();
    	}

    	if (lEtcCount > 0) {
    		HashMap<String, Object> tmpMap = new HashMap<String, Object>();
    		tmpMap.put("port", -1);
    		tmpMap.put("svcName", "기타");
    		tmpMap.put("count", lEtcCount);
   			IPS_MAP_G.add(tmpMap);
    	}

		for (HashMap<String, Object> map : IPS_MAP_G) {
			long count = ((Number)map.get("count")).longValue();
			map.put("ratio", (count * 100f) / lTotalCount);
		}
    	
    	if (nAction == 0) {
    		assetReportInfo.put("IPS_6_4_3_G", new SynthesisDataSource(IPS_MAP_G));
    		assetReportInfo.put("SubChapter19", addContents(Depth1 + ".4.4 월간 차단 이벤트에 대한 서비스 분포", "    "));
    	} else {
    		assetReportInfo.put("IPS_6_3_3_G", new SynthesisDataSource(IPS_MAP_G));
    		assetReportInfo.put("SubChapter10", addContents(Depth1 + ".3.4 월간 허용 이벤트에 대한 서비스 분포", "    "));
    	}
    	
    	List<DBObject> dbResult = reportDao.IPS_DataVariation(assetCode, sEndDay, String.valueOf(nAction), 2);
    	
    	HashMap<String, Object> currMap = new HashMap<String, Object>(); 
    	HashMap<String, Object> prevMap = new HashMap<String, Object>();
    	
    	nTop = 1;
    	for (DBObject val : dbResult) {
    		String sYear = ((Integer)val.get("year")).toString();
    		String sMonth = ((Integer)val.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("year", sYear);
			map.put("month", sMonth);
			map.put("destPort", (Integer)val.get("destPort"));
			map.put("count", ((Number)val.get("count")).longValue());
    		
			if (monthPeriod.get(0).equals(sYear + sMonth)) {
				prevMap.put(String.valueOf((Integer)val.get("destPort")), map);
			} else if (monthPeriod.get(1).equals(sYear + sMonth)) {
			
				if ((Integer)val.get("destPort") != -1 && nTop <= 10) {
    				HashMap<String, Object> map1 = new HashMap<String, Object>();
    				HashMap<String, Object> pMap = (HashMap<String, Object>)prevMap.remove(String.valueOf((Integer)val.get("destPort")));
					
    				long lCurrCount = ((Number)val.get("count")).longValue();
    				long lPrevCount = pMap != null ? ((Number)pMap.get("count")).longValue() : 0;

    				map1.put("no", nTop);		
    				map1.put("port", (Integer)val.get("destPort"));
    				map1.put("svcName", reportDao.getPortServiceName((Integer)val.get("destPort")));
    				map1.put("count", lCurrCount);
    				map1.put("prevCount", lPrevCount);
    				map1.put("countVariation", lCurrCount - lPrevCount);
    				map1.put("ratio", lPrevCount > 0 ? ((lCurrCount - lPrevCount) * 100f) / lPrevCount : 0);
    				
    				nTop++;
    				
   					IPS_MAP_T.add(map1);
				} else {
					currMap.put(String.valueOf((Integer)val.get("destPort")), map);
			
				}
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
    	
    	if (lCurrEtcCount > 0 || lPrevEtcCount > 0) {
	    	HashMap<String, Object> map1 = new HashMap<String, Object>();
			map1.put("no", nTop);		
			map1.put("port", -1);
			map1.put("svcName", "기타");
			map1.put("count", lCurrEtcCount);
			map1.put("prevCount", lPrevEtcCount);
			map1.put("countVariation", lCurrEtcCount - lPrevEtcCount);
			map1.put("ratio", lPrevEtcCount > 0 ? ((lCurrEtcCount - lPrevEtcCount) * 100f) / lPrevEtcCount : 0);
			IPS_MAP_T.add(map1);
    	}
		
    	if (nAction == 0) {
    		assetReportInfo.put("IPS_6_4_3_T", new SynthesisDataSource(IPS_MAP_T));
    		assetReportInfo.put("SubChapter20", addContents(Depth1 + ".4.5 월간 차단 이벤트 별 서비스 증감 현황 (전월비교)", "    "));
    	} else {
    		assetReportInfo.put("IPS_6_3_3_T", new SynthesisDataSource(IPS_MAP_T));
    		assetReportInfo.put("SubChapter11", addContents(Depth1 + ".3.5 월간 허용 이벤트 별 서비스 증감 현황 (전월비교)", "    "));
    	}
	}
	
	private void IPS_Top10Detail(String sStartDay, String sEndDay, int assetCode, HashMap<String, Object> assetReportInfo, int nAction) throws Exception {
	
		List<HashMap<String, Object>> IPS_MAP_T1 = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> IPS_MAP_T2 = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> IPS_MAP_T3 = new ArrayList<HashMap<String, Object>>();
		
		//이벤트 Top10에 대한 출발지 | 목적지 Top5
		List<DBObject> dbEventTop10 = reportDao.IPS_TopInfoMonth(assetCode, sEndDay, String.valueOf(nAction), 1, 10); //message
		
    	int nNo = 1;
    	List<String> lstMessage = new ArrayList<String>();
    	for (DBObject val : dbEventTop10) {
    		String sMessage = (String)val.get("message");

    		lstMessage.add(sMessage);
    		
    		List<DBObject> ipTop5_1st = reportDao.IPS_Top10Condition_1st(assetCode, sEndDay, String.valueOf(nAction), 1, sMessage);
    		for (DBObject mObj : ipTop5_1st) {
    			List<DBObject> ipTop5_2nd = reportDao.IPS_Top10Condition_2nd(assetCode, sEndDay, String.valueOf(nAction), 1, sMessage, (String)mObj.get("srcIp"));
    			for (DBObject sObj : ipTop5_2nd) {
	    			HashMap<String, Object> map = new HashMap<String, Object>();
	    			map.put("no", nNo);
	    			map.put("message", sMessage);
	    			map.put("total", ((Number)val.get("count")).longValue());
	    			map.put("srcIp", (String)mObj.get("srcIp"));
	    			map.put("destIp", (String)sObj.get("destIp"));
	    			map.put("count", ((Number)sObj.get("count")).longValue());
    				IPS_MAP_T1.add(map);
    			}
    		}
    		nNo++;
    	}
    	
    	HashMap<String, Object> prevMap = new HashMap<String, Object>();
    	Iterable<DBObject> dbPrevMessages = reportDao.IPS_PrevMonthInfo(assetCode, sEndDay, String.valueOf(nAction), 3, lstMessage);
    	for (DBObject mObj : dbPrevMessages) {
    		prevMap.put((String)mObj.get("message"), ((Number)mObj.get("count")).longValue());
    	}

		for (HashMap<String, Object> map : IPS_MAP_T1) {
			String sMessage = (String)map.get("message");
			Object obj = prevMap.get(sMessage);
			long lPrevCount = obj != null ? ((Number)obj).longValue() : 0;
			map.put("countVariation", ((Number)map.get("total")).longValue() - lPrevCount);
		}
    	
    	if (nAction == 0) {
    		assetReportInfo.put("IPS_6_4_4_T", new SynthesisDataSource(IPS_MAP_T1));
    		assetReportInfo.put("SubChapter21", addContents(Depth1 + ".4.6 월간 차단 이벤트 TOP10 현황", "    "));
    	} else {
    		assetReportInfo.put("IPS_6_3_4_T", new SynthesisDataSource(IPS_MAP_T1));
    		assetReportInfo.put("SubChapter12", addContents(Depth1 + ".3.6 월간 허용 이벤트 TOP10 현황", "    "));
    	}
		
		//출발지 Top10에 대한 이벤트 | 목적지 Top5
    	List<DBObject> dbSrcIpTop10 = reportDao.IPS_TopInfoMonth(assetCode, sEndDay, String.valueOf(nAction), 3, 10); //srcIp

    	nNo = 1;
    	List<String> lstSrcIp = new ArrayList<String>();
    	for (DBObject val : dbSrcIpTop10) {
    		String strSrcIp = (String)val.get("srcIp");
    		
    		lstSrcIp.add(strSrcIp);
    		
    		List<DBObject> ipTop5_1st = reportDao.IPS_Top10Condition_1st(assetCode, sEndDay, String.valueOf(nAction), 2, strSrcIp);
    	
    		for (DBObject mObj : ipTop5_1st) {
    			List<DBObject> ipTop5_2nd = reportDao.IPS_Top10Condition_2nd(assetCode, sEndDay, String.valueOf(nAction), 2, strSrcIp, (String)mObj.get("message"));
    			for (DBObject sObj : ipTop5_2nd) {
	    			HashMap<String, Object> map = new HashMap<String, Object>();
	    			map.put("no", nNo);
	    			map.put("srcIp", strSrcIp);
	    			map.put("total", ((Number)val.get("count")).longValue());
	    			map.put("message", (String)mObj.get("message"));
	    			map.put("destIp", (String)sObj.get("destIp"));
	    			map.put("count", ((Number)sObj.get("count")).longValue());

	    			IPS_MAP_T2.add(map);
    			}
    		}
    		nNo++;
    	}
    	
    	prevMap = new HashMap<String, Object>();
    	Iterable<DBObject> dbPrevSrcIps = reportDao.IPS_PrevMonthInfo(assetCode, sEndDay, String.valueOf(nAction), 1, lstSrcIp);
    	for (DBObject mObj : dbPrevSrcIps) {
    		prevMap.put((String)mObj.get("srcIp"), ((Number)mObj.get("count")).longValue());
    	}

		for (HashMap<String, Object> map : IPS_MAP_T2) {
			String sSrcIp = (String)map.get("srcIp");
			Object obj = prevMap.get(sSrcIp);
			long lPrevCount = obj != null ? ((Number)obj).longValue() : 0;
			map.put("countVariation", ((Number)map.get("total")).longValue() - lPrevCount);
		}
    	
    	if (nAction == 0) {
    		assetReportInfo.put("IPS_6_4_5_T", new SynthesisDataSource(IPS_MAP_T2));
    		assetReportInfo.put("SubChapter22", addContents(Depth1 + ".4.7 월간 출발지IP TOP10 현황", "    "));
    	} else {
    		assetReportInfo.put("IPS_6_3_5_T", new SynthesisDataSource(IPS_MAP_T2));
    		assetReportInfo.put("SubChapter13", addContents(Depth1 + ".3.7 월간 출발지IP TOP10 현황", "    "));
    	}
		
		//목적지 Top10에 대한 이벤트 | 출발지목적지 Top5
    	List<DBObject> dbDestIpTop10 = reportDao.IPS_TopInfoMonth(assetCode, sEndDay, String.valueOf(nAction), 4, 10); //destIp
		
    	nNo = 1;
    	List<String> lstDestIp = new ArrayList<String>();
    	for (DBObject val : dbDestIpTop10) {
    		String strDestIp = (String)val.get("destIp");

    		lstDestIp.add(strDestIp);
    		
    		List<DBObject> ipTop5_1st = reportDao.IPS_Top10Condition_1st(assetCode, sEndDay, String.valueOf(nAction), 3, strDestIp);
    	
    		for (DBObject mObj : ipTop5_1st) {
    			List<DBObject> ipTop5_2nd = reportDao.IPS_Top10Condition_2nd(assetCode, sEndDay, String.valueOf(nAction), 3, strDestIp, (String)mObj.get("message"));
    			for (DBObject sObj : ipTop5_2nd) {
	    			HashMap<String, Object> map = new HashMap<String, Object>();
	    			map.put("no", nNo);
	    			map.put("destIp", strDestIp);
	    			map.put("total", ((Number)val.get("count")).longValue());
	    			map.put("message", (String)mObj.get("message"));
	    			map.put("srcIp", (String)sObj.get("srcIp"));
	    			map.put("count", ((Number)sObj.get("count")).longValue());

    				IPS_MAP_T3.add(map);
    			}
    		}
    		nNo++;
    	}
    	
    	prevMap = new HashMap<String, Object>();
    	Iterable<DBObject> dbPrevDestIps = reportDao.IPS_PrevMonthInfo(assetCode, sEndDay, String.valueOf(nAction), 2, lstDestIp);
    	for (DBObject mObj : dbPrevDestIps) {
    		prevMap.put((String)mObj.get("destIp"), ((Number)mObj.get("count")).longValue());
    	}

		for (HashMap<String, Object> map : IPS_MAP_T3) {
			String sDestIp = (String)map.get("destIp");
			Object obj = prevMap.get(sDestIp);
			long lPrevCount = obj != null ? ((Number)obj).longValue() : 0;
			map.put("countVariation", ((Number)map.get("total")).longValue() - lPrevCount);
		}
    	
    	if (nAction == 0) {
    		assetReportInfo.put("IPS_6_4_6_T", new SynthesisDataSource(IPS_MAP_T3));
    		assetReportInfo.put("SubChapter23", addContents(Depth1 + ".4.8 월간 목적지IP TOP10 현황", "    "));
    	} else {
    		assetReportInfo.put("IPS_6_3_6_T", new SynthesisDataSource(IPS_MAP_T3));
    		assetReportInfo.put("SubChapter14", addContents(Depth1 + ".3.8 월간 목적지IP TOP10 현황", "    "));
    	}
	}
	
	private void IPS_DailyChange(String sStartDay, String sEndDay, int assetCode, HashMap<String, Object> assetReportInfo) throws Exception {

    	List<HashMap<String, Object>> IPS_6_2_1_G = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> IPS_6_2_2_G = new ArrayList<HashMap<String, Object>>();
    	
    	assetReportInfo.put("SubChapter3", addContents(Depth1 + ".2 금월 일별 이벤트 발생 추이", "  "));
    	
    	List<DBObject> dbResult = reportDao.IPS_SessionLogDay(assetCode, sStartDay, sEndDay);   	
    	
    	HashMap<String, DBObject> mapOutToIn = new HashMap<String, DBObject>(); 
    	for (DBObject val : dbResult) {
    		String sAction = (String)val.get("action");
    		String sYear = ((Integer)val.get("year")).toString();
    		String sMonth = ((Integer)val.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    		String sDay = ((Integer)val.get("day")).toString();
    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
    		mapOutToIn.put(sAction + sYear + sMonth + sDay, val);
    	}
    	
    	List<String> dayPeriod = reportDao.getPeriodDay(sStartDay, sEndDay, -1, 1);
    	
    	for (int action = 0; action < 2; action++) {
    		
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = reportDao.getCalendar(endDate);
			endCal.add(Calendar.MONTH, -2);
    		
			for (int i = 0; i < dayPeriod.size(); i++) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		DBObject val = mapOutToIn.get(String.valueOf(action) + dayPeriod.get(i));
	    		
	    		if (Integer.valueOf(dayPeriod.get(i).substring(6,8)) == Integer.valueOf(sStartDay.substring(8, 10))) {
	    			endCal.add(Calendar.MONTH, 1);
	    		}
	    		
	    		map.put("gubun", (endCal.get(Calendar.MONTH)+1) + "월");
	    		map.put("day", Integer.valueOf(dayPeriod.get(i).substring(6, 8)));
	    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
        		if (action == 0) {
        			IPS_6_2_2_G.add(map);
        		} else {
        			IPS_6_2_1_G.add(map);
        		}
			}
    	}
    	assetReportInfo.put("IPS_6_2_1_G", new SynthesisDataSource(IPS_6_2_1_G));
    	assetReportInfo.put("SubChapter4", addContents("가) 금월 전체 허용 이벤트 비교", "    "));
    	
    	assetReportInfo.put("IPS_6_2_2_G", new SynthesisDataSource(IPS_6_2_2_G));
    	assetReportInfo.put("SubChapter5", addContents("나) 금월 전체 차단 이벤트 비교", "    "));
	}
	
	private void IPS_MonthlyChange(String sStartDay, String sEndDay, int assetCode, HashMap<String, Object> assetReportInfo) throws Exception {

    	List<HashMap<String, Object>> IPS_6_1_G = new ArrayList<HashMap<String, Object>>();
    	Iterable<DBObject> outToIn = reportDao.IPS_SessionLogMonth(assetCode, sEndDay);   	

    	HashMap<String, DBObject> keyMap = new HashMap<String, DBObject>(); 
    	for (DBObject val : outToIn) {
    		String sAction = (String)val.get("action");
    		String sYear = ((Integer)val.get("year")).toString();
    		String sMonth = ((Integer)val.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    		keyMap.put(sAction + sYear + sMonth, val);
    	}

    	List<String> monthPeriod = reportDao.getPeriodMonth(sEndDay, -5, 1);
    	
    	String[] saGubun = {"차단", "허용"};
    	for (int i = 1; i >= 0; i--) {
    		for (int j = 0; j < 6; j++) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		DBObject val = keyMap.get(String.valueOf(i) + monthPeriod.get(j));
	    		map.put("gubun", saGubun[i]);
	    		map.put("month", Integer.valueOf(monthPeriod.get(j)));
	    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
	    		
	    		IPS_6_1_G.add(map);
    		}	    		
    	}
    	assetReportInfo.put("IPS_6_1_G", new SynthesisDataSource(IPS_6_1_G));
    	assetReportInfo.put("IPS_6_1_T", new SynthesisDataSource(IPS_6_1_G));
    	
    	assetReportInfo.put("SubChapter2", addContents(Depth1 + ".1 최근 6개월 추이", "  "));
	}
	
	private String addContents(String strContents) {
		return addContents(strContents, "");
	}
	private String addContents(String strContents, String strIndent) {
		contentsList.add(strIndent + strContents);
		return strContents;
	}
	
	@SuppressWarnings("unused")
	private List<HashMap<String, Object>> getContents() {
		List<HashMap<String, Object>> contents = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < contentsList.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("content", contentsList.get(i));
			contents.add(map);
		}
		return contents;
	}
	
	public HashMap<String, Object> getReportDataSource(
			int Depth, 
			List<String> contentsList, 
			String sStartDay, 
			String sEndDay,
			HashMap<String, Object> assetMap) throws Exception {

		this.Depth1 = Depth;
		this.contentsList = contentsList;
		
		HashMap<String, Object> assetReportInfo = new HashMap<String, Object>();
    	
		assetReportInfo.put("SubChapter1", addContents(Depth1 + ". " + (String)assetMap.get("assetName") + " 월간 로그 분석"));
    	
    	try {
    		//최근 6개월 추이
    		IPS_MonthlyChange(sStartDay, sEndDay, (Integer)assetMap.get("assetCode"), assetReportInfo);
    		
    		//금월 일별 이벤트 발생 추이
    		IPS_DailyChange(sStartDay, sEndDay, (Integer)assetMap.get("assetCode"), assetReportInfo);
    		
    		for (int action = 1; action >= 0; action--) {
    			//월간 이벤트 TOP10에 대한 일별 발생 추이
    	    	IPS_Top10DailyEventChange(sStartDay, sEndDay, (Integer)assetMap.get("assetCode"), assetReportInfo, action);
    	    	
    	    	//월간 이벤트 TOP10(차트)
    	    	IPS_Top10MonthlyChart(sStartDay, sEndDay, (Integer)assetMap.get("assetCode"), assetReportInfo, action);
    	    	
    	    	//월간 이벤트에 대한 서비스 분포 Pie
    	    	IPS_ServiceMonthPie(sStartDay, sEndDay, (Integer)assetMap.get("assetCode"), assetReportInfo, action);
    	    	
    	    	//월간 이벤트 TOP10
    	    	IPS_Top10Detail(sStartDay, sEndDay, (Integer)assetMap.get("assetCode"), assetReportInfo, action);
    		}
    		
    		//월간 IPS 성능 추이
    		IPS_PerformanceLog(sStartDay, sEndDay, (Integer)assetMap.get("assetCode"), assetReportInfo);
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw e;
    	}
    	return assetReportInfo;
	}
	
}
