package com.cyberone.report.core.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cyberone.report.core.dao._FWReportDao;
import com.cyberone.report.core.datasource.SynthesisDataSource;
import com.mongodb.DBObject;

public class _FWService {

	protected final boolean bLocalDebug = true;
	protected final String REPORT_DIR = "D:/Project/prom/infra/prom-webconsole/src/main/resources/WEB-INF/reports/synthesis/";
	protected final String BASE_DIR = "/jasper";
	
	//protected  final boolean bLocalDebug = false;
	//protected  final String REPORT_DIR = "WEB-INF/reports/synthesis/";
	//protected  final String BASE_DIR = "/jasper";

	protected final String promHome = System.getProperty("prom.home");
	
	protected final String ALLOW = "1";
	protected final String CUTOFF = "0";

	private int Depth1 = 1;
	private List<String> contentsList = new ArrayList<String>();
	
	@Autowired
	private _FWReportDao reportDao;
	
	public void FW_PerformanceLog(String sStartDay, String sEndDay, int assetCode, HashMap<String, Object> assetReportInfo) throws Exception {
		
		boolean bPerformanceExist = false;
		
		List<String> dayPeriod = reportDao.getPeriodDay(sStartDay, sEndDay, -1, 1);
		
		List<DBObject> pfDbInfo = reportDao.getPerformanceMonth(assetCode, sEndDay);    	
			
		int nIndex = 1;
    	for (DBObject mVal : pfDbInfo) {
    		
    		if (!bPerformanceExist) {
    		    assetReportInfo.put("SubChapter46", addContents(Depth1 + ".5 월간 성능 추이", "  "));
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
	
	@SuppressWarnings("unchecked")
	public void FW_OutboundSessionLog(String sStartDay, String sEndDay, int assetCode, HashMap<String, Object> assetReportInfo) throws Exception {
		
    	/*
		 * 보고서 6.4.1번 항목, 월간 허용 서비스  TOP10 분포 현황
		 */
    	List<HashMap<String, Object>> FW_6_4_1_G = new ArrayList<HashMap<String, Object>>();
    	
    	assetReportInfo.put("SubChapter28", addContents(Depth1 + ".4 내부에서 외부로의 세션 로그 분석", "  "));
    	
    	List<DBObject> logResult = reportDao.FW_ServiceSessionLog(assetCode, 0, ALLOW, sEndDay, true);   	

    	List<String> monthPeriod = reportDao.getPeriodMonth(sEndDay, -1, 1);
    	
    	int nTop = 1;
    	long lTotalCount = 0;
    	HashMap<String, Object> currMap = new HashMap<String, Object>(); 
    	HashMap<String, Object> prevMap = new HashMap<String, Object>();
    	for (DBObject val : logResult) {
    		String sYear = ((Integer)val.get("year")).toString();
    		String sMonth = ((Integer)val.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("year", sYear);
			map.put("month", sMonth);
			map.put("port", (Integer)val.get("destPort"));
			map.put("count", ((Number)val.get("count")).longValue());
			map.put("trafficSize", ((Number)val.get("trafficSize")).longValue());
    		
    		if (monthPeriod.get(0).equals(sYear + sMonth)) {
    			prevMap.put(String.valueOf((Integer)val.get("destPort")), map);
    		} else if (monthPeriod.get(1).equals(sYear + sMonth)) {
    		
    			if ((Integer)val.get("destPort") != -1 && nTop <= 10) {
    				HashMap<String, Object> map1 = new HashMap<String, Object>();
    				map1.put("no", nTop);		
    				map1.put("port", (Integer)val.get("destPort"));
    				map1.put("svcName", reportDao.getPortServiceName((Integer)val.get("destPort")));

    				HashMap<String, Object> pMap = (HashMap<String, Object>)prevMap.remove(String.valueOf((Integer)val.get("destPort")));
    				
    				long lCurrCount = ((Number)val.get("count")).longValue();
    				long lCurrTraffic = ((Number)val.get("trafficSize")).longValue();

    				long lPrevCount = pMap != null ? ((Number)pMap.get("count")).longValue() : 0; 
    				long lPrevTraffic = pMap != null ? ((Number)pMap.get("trafficSize")).longValue() : 0; 
    				
    				lTotalCount += lCurrCount;
    				
    				map1.put("count", lCurrCount);
    				map1.put("countVariation", lCurrCount - lPrevCount);
    				map1.put("trafficSize", lCurrTraffic);
    				map1.put("trafficVariation", lCurrTraffic - lPrevTraffic);
    				
    				nTop++;
    				
    				FW_6_4_1_G.add(map1);
    			} else {
    				lTotalCount += ((Number)val.get("count")).longValue();
    				currMap.put(String.valueOf((Integer)val.get("destPort")), map);
    			}
    		}
    	}

    	long lPrevEtcCount = 0;
    	long lPrevEtcTraffic = 0;
    	Set<Entry<String, Object>> prevEntry = prevMap.entrySet();
    	for (Entry<String, Object> obj : prevEntry) {
    		HashMap<String,Object> map = (HashMap<String,Object>)obj.getValue();
    		lPrevEtcCount += ((Number)map.get("count")).longValue();
    		lPrevEtcTraffic += ((Number)map.get("trafficSize")).longValue();
    	}
    	
    	long lCurrEtcCount = 0;
    	long lCurrEtcTraffic = 0;
    	Set<Entry<String, Object>> currEntry = currMap.entrySet();
    	for (Entry<String, Object> obj : currEntry) {
    		HashMap<String,Object> map = (HashMap<String,Object>)obj.getValue();
    		lCurrEtcCount += ((Number)map.get("count")).longValue();
    		lCurrEtcTraffic += ((Number)map.get("trafficSize")).longValue();
    	}

    	if (FW_6_4_1_G.size() > 0) {
	    	HashMap<String, Object> map1 = new HashMap<String, Object>();
			map1.put("no", nTop);		
			map1.put("port", -1);
			map1.put("svcName", "기타");
			map1.put("count", lCurrEtcCount);
			map1.put("countVariation", lCurrEtcCount - lPrevEtcCount);
			map1.put("trafficSize", lCurrEtcTraffic);
			map1.put("trafficVariation", lCurrEtcTraffic - lPrevEtcTraffic);
			FW_6_4_1_G.add(map1);
    	}
    	
		for (HashMap<String, Object> map : FW_6_4_1_G) {
			long count = ((Number)map.get("count")).longValue();
			map.put("ratio", (count * 100f) / lTotalCount);
		}
		
    	assetReportInfo.put("FW_6_4_1_G", new SynthesisDataSource(FW_6_4_1_G));
    	assetReportInfo.put("SubChapter29", addContents(Depth1 + ".4.1 월간 허용 서비스 TOP10 분포 현황", "    "));
    	
    	assetReportInfo.put("FW_6_4_2_T", new SynthesisDataSource(FW_6_4_1_G));
    	assetReportInfo.put("SubChapter30", addContents(Depth1 + ".4.2 월간 허용 서비스 TOP10 증감 현황", "    "));
    	
    	/*
		 * 보고서 6.4.3번 항목, 월간 허용 로그 상세 분석
		 */
    	List<HashMap<String, Object>> FW_6_4_3_1_G = new ArrayList<HashMap<String, Object>>();
    	//List<HashMap<String, Object>> FW_6_4_3_2_G = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> FW_6_4_3_3_T = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> FW_6_4_3_4_T = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> FW_6_4_3_5_T = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> FW_6_4_3_6_T = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> FW_6_4_3_7_T = new ArrayList<HashMap<String, Object>>();
    	
    	assetReportInfo.put("SubChapter31", addContents(Depth1 + ".4.3 월간 허용 세션로그 상세", "    "));
    	
    	//출발지IP 건수기준 TOP10 리스트
    	List<DBObject> srcIpTop10Result = reportDao.FW_Top10Ips(assetCode, sEndDay, 0, ALLOW, true, true);
    	List<String> top10SrcIps = new ArrayList<String>();
    	for (DBObject val : srcIpTop10Result) {
    		top10SrcIps.add((String)val.get("srcIp"));
    	}
    	
    	//출발지IP TOP10에 대한 세션로그 일별 발생 추이
    	Iterable<DBObject> ipTop10Result = reportDao.FW_Top10LogDetail(assetCode, sStartDay, sEndDay, 0, ALLOW, top10SrcIps, true);
    	
    	List<String> dayPeriod = reportDao.getPeriodDay(sStartDay, sEndDay, 0, 1);
    	
    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
    	for (DBObject val : ipTop10Result) {
    		HashMap<String,Object> mapId = (HashMap<String,Object>)val.get("_id");
    		String sSrcIp = (String)mapId.get("srcIp");
    		String sYear = ((Integer)mapId.get("year")).toString();
    		String sMonth = ((Integer)mapId.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    		String sDay = ((Integer)mapId.get("day")).toString();
    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
    		
    		mapResult.put(sSrcIp + "-" + sYear + sMonth + sDay, val);
    	}
    	
    	for (String strIp : top10SrcIps) {
	    	for (int i = 0; i < dayPeriod.size(); i++) {
	    		DBObject val = mapResult.get(strIp + "-" + dayPeriod.get(i));

	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		map.put("srcIp", strIp);
	    		map.put("day", Integer.valueOf(dayPeriod.get(i).substring(6,8)));
	    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
	    		map.put("trafficSize", val != null ? ((Number)val.get("trafficSize")).longValue() : 0);
	    		FW_6_4_3_1_G.add(map);
	    	}
    	}
    	assetReportInfo.put("FW_6_4_3_1_G", new SynthesisDataSource(FW_6_4_3_1_G));
    	assetReportInfo.put("SubChapter32", addContents("가) 출발지IP TOP10 에 대한 세션 로그 발생 추이", "      "));
    	
    	assetReportInfo.put("FW_6_4_3_2_G", new SynthesisDataSource(FW_6_4_3_1_G));
    	assetReportInfo.put("SubChapter33", addContents("나) 출발지IP TOP10 에 대한 통신량 추이", "      "));

    	
    	//출발지IP TOP10 현황 (로그건수기준)
    	int nNo = 1;
    	for (DBObject val : srcIpTop10Result) {
    		String strIp = (String)val.get("srcIp");

    		Iterable<DBObject> ipTop10Condition = reportDao.FW_Top10LogCondition(assetCode, sEndDay, 0, ALLOW, strIp, true, true);
    	
    		for (DBObject obj : ipTop10Condition) {
    			HashMap<String,Object> tmpId = (HashMap<String,Object>)obj.get("_id");
    			
    			HashMap<String, Object> map = new HashMap<String, Object>();
    			map.put("no", nNo);
    			map.put("srcIp", strIp);
    			map.put("total", ((Number)val.get("count")).longValue());
    			map.put("destIp", (String)tmpId.get("destIp"));
    			map.put("port", (Integer)tmpId.get("destPort"));
    			map.put("count", ((Number)obj.get("count")).longValue());
    			map.put("trafficSize", ((Number)obj.get("trafficSize")).longValue());
    			map.put("zone", (String)tmpId.get("destZone"));
    			map.put("policy", (String)tmpId.get("policyId"));
    			
    			FW_6_4_3_3_T.add(map);	
    		}
    		nNo++;
    	}	    	
    	assetReportInfo.put("FW_6_4_3_3_T", new SynthesisDataSource(FW_6_4_3_3_T));
    	assetReportInfo.put("SubChapter34", addContents("다) 로그건수 별 출발지IP TOP10 현황", "      "));
    	
    	//출발지IP 통신량기준 TOP10 리스트
    	srcIpTop10Result = reportDao.FW_Top10Ips(assetCode, sEndDay, 0, ALLOW, true, false);
    	top10SrcIps = new ArrayList<String>();
    	for (DBObject val : srcIpTop10Result) {
    		top10SrcIps.add((String)val.get("srcIp"));
    	}

    	//출발지IP TOP10 현황 (통신량기준)
    	nNo = 1;
    	for (DBObject val : srcIpTop10Result) {
    		String strIp = (String)val.get("srcIp");
    		
    		Iterable<DBObject> ipTop10Condition = reportDao.FW_Top10LogCondition(assetCode, sEndDay, 0, ALLOW, strIp, true, false);
    	
    		for (DBObject obj : ipTop10Condition) {
    			HashMap<String,Object> tmpId = (HashMap<String,Object>)obj.get("_id");
    			
    			HashMap<String, Object> map = new HashMap<String, Object>();
    			map.put("no", nNo);
    			map.put("srcIp", strIp);
    			map.put("total", ((Number)val.get("trafficSize")).longValue());
    			map.put("destIp", (String)tmpId.get("destIp"));
    			map.put("port", (Integer)tmpId.get("destPort"));
    			map.put("count", ((Number)obj.get("count")).longValue());
    			map.put("trafficSize", ((Number)obj.get("trafficSize")).longValue());
    			map.put("zone", (String)tmpId.get("destZone"));
    			map.put("policy", (String)tmpId.get("policyId"));
    			
    			FW_6_4_3_4_T.add(map);	
    		}
    		nNo++;
    	}	    	
    	assetReportInfo.put("FW_6_4_3_4_T", new SynthesisDataSource(FW_6_4_3_4_T));
    	assetReportInfo.put("SubChapter35", addContents("라) 통신량 별 출발지IP TOP10 현황", "      "));
    	
    	//목적지IP 건수기준 TOP10 리스트
    	List<DBObject> destIpTop10Result = reportDao.FW_Top10Ips(assetCode, sEndDay, 0, ALLOW, false, true);
    	List<String> top10DestIps = new ArrayList<String>();
    	for (DBObject val : destIpTop10Result) {
    		top10DestIps.add((String)val.get("destIp"));
    	}
    	
    	//목적지IP TOP10 현황 (로그건수기준)
    	nNo = 1;
    	for (DBObject val : destIpTop10Result) {
    		String strIp = (String)val.get("destIp");
    		
    		Iterable<DBObject> ipTop10Condition = reportDao.FW_Top10LogCondition(assetCode, sEndDay, 0, ALLOW, strIp, false, true);
    	
    		for (DBObject obj : ipTop10Condition) {
    			HashMap<String,Object> tmpId = (HashMap<String,Object>)obj.get("_id");
    			
    			HashMap<String, Object> map = new HashMap<String, Object>();
    			map.put("no", nNo);
    			map.put("destIp", strIp);
    			map.put("total", ((Number)val.get("count")).longValue());
    			map.put("srcIp", (String)tmpId.get("srcIp"));
    			map.put("port", (Integer)tmpId.get("destPort"));
    			map.put("count", ((Number)obj.get("count")).longValue());
    			map.put("trafficSize", ((Number)obj.get("trafficSize")).longValue());
    			map.put("zone", (String)tmpId.get("destZone"));
    			map.put("policy", (String)tmpId.get("policyId"));
    			
    			FW_6_4_3_5_T.add(map);	
    		}
    		nNo++;
    	}	    	
    	assetReportInfo.put("FW_6_4_3_5_T", new SynthesisDataSource(FW_6_4_3_5_T));
    	assetReportInfo.put("SubChapter36", addContents("마) 로그건수 별 목적지IP TOP10 현황", "      "));

    	//서비스 TOP10 현황 (로그건수기준)
    	nNo = 1;
    	for (HashMap<String, Object> val : FW_6_4_1_G) {
    		int nPort = (Integer)val.get("port");
    		long totalCount = ((Number)val.get("count")).longValue();
    		
    		Iterable<DBObject> svcTop10Condition = reportDao.FW_Top10ServiceCondition(assetCode, sEndDay, 0, ALLOW, nPort, true);
    		
    		for (DBObject obj : svcTop10Condition) {
    			HashMap<String,Object> tmpId = (HashMap<String,Object>)obj.get("_id");
    			
    			HashMap<String, Object> map = new HashMap<String, Object>();
    			map.put("no", nNo);
    			map.put("port", nPort);
    			map.put("total", totalCount);
    			map.put("srcIp", (String)tmpId.get("srcIp"));
    			map.put("destIp", (String)tmpId.get("destIp"));
    			map.put("count", ((Number)obj.get("count")).longValue());
    			map.put("trafficSize", ((Number)obj.get("trafficSize")).longValue());
    			map.put("zone", (String)tmpId.get("destZone"));
    			map.put("policy", (String)tmpId.get("policyId"));
    			
    			FW_6_4_3_6_T.add(map);	
    		}
    		nNo++;
    	}
    	assetReportInfo.put("FW_6_4_3_6_T", new SynthesisDataSource(FW_6_4_3_6_T));
    	assetReportInfo.put("SubChapter37", addContents("바) 로그건수 별 서비스 TOP10 현황", "      "));
    	
    	
    	//서비스 TOP10 현황 (통신량기준)
    	List<HashMap<String, Object>> tempList = new ArrayList<HashMap<String, Object>>();
    	nTop = 1;
    	logResult = reportDao.FW_ServiceSessionLog(assetCode, 0, ALLOW, sEndDay, false);
    	for (DBObject val : logResult) {
    		String sYear = ((Integer)val.get("year")).toString();
    		String sMonth = ((Integer)val.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    		
    		if (monthPeriod.get(1).equals(sYear + sMonth)) {
    			if ((Integer)val.get("destPort") == -1) {
    				continue;
    			}
    			
    			if (nTop <= 10) {
    				HashMap<String, Object> map = new HashMap<String, Object>();
    				long lCurrTraffic = ((Number)val.get("trafficSize")).longValue();
    				map.put("port", (Integer)val.get("destPort"));
    				map.put("trafficSize", lCurrTraffic);
    				tempList.add(map);
    				nTop++;
    			} else {
    				break;
    			}
    		}
    	}
    	
    	nNo = 1;
    	for (HashMap<String, Object> val : tempList) {
    		int nPort = (Integer)val.get("port");
    		long totalTraffic = ((Number)val.get("trafficSize")).longValue();
    		
    		Iterable<DBObject> svcTop10Condition = reportDao.FW_Top10ServiceCondition(assetCode, sEndDay, 0, ALLOW, nPort, false);
    		
    		for (DBObject obj : svcTop10Condition) {
    			HashMap<String,Object> tmpId = (HashMap<String,Object>)obj.get("_id");
    			
    			HashMap<String, Object> map = new HashMap<String, Object>();
    			map.put("no", nNo);
    			map.put("port", nPort);
    			map.put("total", totalTraffic);
    			map.put("srcIp", (String)tmpId.get("srcIp"));
    			map.put("destIp", (String)tmpId.get("destIp"));
    			map.put("count", ((Number)obj.get("count")).longValue());
    			map.put("trafficSize", ((Number)obj.get("trafficSize")).longValue());
    			map.put("zone", (String)tmpId.get("destZone"));
    			map.put("policy", (String)tmpId.get("policyId"));
    			FW_6_4_3_7_T.add(map);	
    		}
    		nNo++;
    	}
    	assetReportInfo.put("FW_6_4_3_7_T", new SynthesisDataSource(FW_6_4_3_7_T));
    	assetReportInfo.put("SubChapter38", addContents("사) 통신량 별 서비스 TOP10 현황", "      "));

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	    	
    	
    	/*
		 * 보고서 6.4.4번 항목, 월간 차단 서비스  TOP10 분포 현황
		 */
    	List<HashMap<String, Object>> FW_6_4_4_G = new ArrayList<HashMap<String, Object>>();
    	
    	//외부/차단
    	logResult = reportDao.FW_ServiceSessionLog(assetCode, 0, CUTOFF, sEndDay, true);   	

    	monthPeriod = reportDao.getPeriodMonth(sEndDay, -1, 1);
    	
    	nTop = 1;
    	lTotalCount = 0;
    	currMap = new HashMap<String, Object>(); 
    	prevMap = new HashMap<String, Object>();
    	for (DBObject val : logResult) {
    		String sYear = ((Integer)val.get("year")).toString();
    		String sMonth = ((Integer)val.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("year", sYear);
			map.put("month", sMonth);
			map.put("port", (Integer)val.get("destPort"));
			map.put("count", ((Number)val.get("count")).longValue());
			map.put("trafficSize", ((Number)val.get("trafficSize")).longValue());
    		
    		if (monthPeriod.get(0).equals(sYear + sMonth)) {
    			prevMap.put(String.valueOf((Integer)val.get("destPort")), map);
    		} else if (monthPeriod.get(1).equals(sYear + sMonth)) {
    		
    			if ((Integer)val.get("destPort") != -1 && nTop <= 10) {
    				HashMap<String, Object> map1 = new HashMap<String, Object>();
    				map1.put("no", nTop);		
    				map1.put("port", (Integer)val.get("destPort"));
    				map1.put("svcName", reportDao.getPortServiceName((Integer)val.get("destPort")));

    				HashMap<String, Object> pMap = (HashMap<String, Object>)prevMap.remove(String.valueOf((Integer)val.get("destPort")));
    				
    				long lCurrCount = ((Number)val.get("count")).longValue();
    				long lCurrTraffic = ((Number)val.get("trafficSize")).longValue();

    				long lPrevCount = pMap != null ? ((Number)pMap.get("count")).longValue() : 0; 
    				long lPrevTraffic = pMap != null ? ((Number)pMap.get("trafficSize")).longValue() : 0; 
    				
    				lTotalCount += lCurrCount;
    				
    				map1.put("count", lCurrCount);
    				map1.put("countVariation", lCurrCount - lPrevCount);
    				map1.put("trafficSize", lCurrTraffic);
    				map1.put("trafficVariation", lCurrTraffic - lPrevTraffic);
    				
    				nTop++;
    				
    				FW_6_4_4_G.add(map1);
    			} else {
    				lTotalCount += ((Number)val.get("count")).longValue();
    				currMap.put(String.valueOf((Integer)val.get("destPort")), map);
    			}
    		}
    	}

    	lPrevEtcCount = 0;
    	lPrevEtcTraffic = 0;
    	prevEntry = prevMap.entrySet();
    	for (Entry<String, Object> obj : prevEntry) {
    		HashMap<String,Object> map = (HashMap<String,Object>)obj.getValue();
    		lPrevEtcCount += ((Number)map.get("count")).longValue();
    		lPrevEtcTraffic += ((Number)map.get("trafficSize")).longValue();
    	}
    	
    	lCurrEtcCount = 0;
    	lCurrEtcTraffic = 0;
    	currEntry = currMap.entrySet();
    	for (Entry<String, Object> obj : currEntry) {
    		HashMap<String,Object> map = (HashMap<String,Object>)obj.getValue();
    		lCurrEtcCount += ((Number)map.get("count")).longValue();
    		lCurrEtcTraffic += ((Number)map.get("trafficSize")).longValue();
    	}

    	if (FW_6_4_4_G.size() > 0) {
	    	HashMap<String, Object> map1 = new HashMap<String, Object>();
			map1.put("no", nTop);		
			map1.put("port", -1);
			map1.put("svcName", "기타");
			map1.put("count", lCurrEtcCount);
			map1.put("countVariation", lCurrEtcCount - lPrevEtcCount);
			map1.put("trafficSize", lCurrEtcTraffic);
			map1.put("trafficVariation", lCurrEtcTraffic - lPrevEtcTraffic);
			FW_6_4_4_G.add(map1);
    	}
    	
		for (HashMap<String, Object> map : FW_6_4_4_G) {
			long count = ((Number)map.get("count")).longValue();
			map.put("ratio", (count * 100f) / lTotalCount);
		}
		
    	assetReportInfo.put("FW_6_4_4_G", new SynthesisDataSource(FW_6_4_4_G));
    	assetReportInfo.put("SubChapter39", addContents(Depth1 + ".4.4 월간 차단 서비스 TOP10 분포 현황", "    "));
    	
    	assetReportInfo.put("FW_6_4_5_T", new SynthesisDataSource(FW_6_4_4_G));
    	assetReportInfo.put("SubChapter40", addContents(Depth1 + ".4.5 월간 차단 서비스 TOP10 증감 현황", "    "));
    	
    	/*
		 * 보고서 6.3.4번 항목, 월간 차단 로그 상세 분석
		 */
    	List<HashMap<String, Object>> FW_6_4_6_1_G = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> FW_6_4_6_2_T = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> FW_6_4_6_3_T = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> FW_6_4_6_4_T = new ArrayList<HashMap<String, Object>>();
    	
    	assetReportInfo.put("SubChapter41", addContents(Depth1 + ".4.6 월간 차단 세션로그 상세", "    "));
    	
    	//출발지IP 건수기준 TOP10 리스트
    	srcIpTop10Result = reportDao.FW_Top10Ips(assetCode, sEndDay, 0, CUTOFF, true, true);
    	top10SrcIps = new ArrayList<String>();
    	for (DBObject val : srcIpTop10Result) {
    		top10SrcIps.add((String)val.get("srcIp"));
    	}
    	
    	//출발지IP TOP10에 대한 세션로그 일별 발생 추이
    	ipTop10Result = reportDao.FW_Top10LogDetail(assetCode, sStartDay, sEndDay, 0, CUTOFF, top10SrcIps, true);
    	
    	dayPeriod = reportDao.getPeriodDay(sStartDay, sEndDay, 0, 1);
    	
    	mapResult = new HashMap<String, DBObject>(); 
    	for (DBObject val : ipTop10Result) {
    		HashMap<String,Object> mapId = (HashMap<String,Object>)val.get("_id");
    		String sSrcIp = (String)mapId.get("srcIp");
    		String sYear = ((Integer)mapId.get("year")).toString();
    		String sMonth = ((Integer)mapId.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    		String sDay = ((Integer)mapId.get("day")).toString();
    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
    		
    		mapResult.put(sSrcIp + "-" + sYear + sMonth + sDay, val);
    	}
    	
    	for (String strIp : top10SrcIps) {
	    	for (int i = 0; i < dayPeriod.size(); i++) {
	    		DBObject val = mapResult.get(strIp + "-" + dayPeriod.get(i));

	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		map.put("srcIp", strIp);
	    		map.put("day", Integer.valueOf(dayPeriod.get(i).substring(6,8)));
	    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
	    		map.put("trafficSize", val != null ? ((Number)val.get("trafficSize")).longValue() : 0);
	    		FW_6_4_6_1_G.add(map);
	    	}
    	}
    	assetReportInfo.put("FW_6_4_6_1_G", new SynthesisDataSource(FW_6_4_6_1_G));
    	assetReportInfo.put("SubChapter42", addContents("가) 출발지IP TOP10 에 대한 세션 로그 발생 추이", "      "));
    	
    	//출발지IP TOP10 현황 (로그건수기준)
    	nNo = 1;
    	for (DBObject val : srcIpTop10Result) {
    		String strIp = (String)val.get("srcIp");

    		Iterable<DBObject> ipTop10Condition = reportDao.FW_Top10LogCondition(assetCode, sEndDay, 0, CUTOFF, strIp, true, true);
    	
    		for (DBObject obj : ipTop10Condition) {
    			HashMap<String,Object> tmpId = (HashMap<String,Object>)obj.get("_id");
    			
    			HashMap<String, Object> map = new HashMap<String, Object>();
    			map.put("no", nNo);
    			map.put("srcIp", strIp);
    			map.put("total", ((Number)val.get("count")).longValue());
    			map.put("destIp", (String)tmpId.get("destIp"));
    			map.put("port", (Integer)tmpId.get("destPort"));
    			map.put("count", ((Number)obj.get("count")).longValue());
    			map.put("trafficSize", ((Number)obj.get("trafficSize")).longValue());
    			map.put("zone", (String)tmpId.get("destZone"));
    			map.put("policy", (String)tmpId.get("policyId"));
    			
    			FW_6_4_6_2_T.add(map);	
    		}
    		nNo++;
    	}	    	
    	assetReportInfo.put("FW_6_4_6_2_T", new SynthesisDataSource(FW_6_4_6_2_T));
    	assetReportInfo.put("SubChapter43", addContents("나) 로그건수 별 출발지IP TOP10 현황", "      "));
    	
    	//목적지IP 건수기준 TOP10 리스트
    	destIpTop10Result = reportDao.FW_Top10Ips(assetCode, sEndDay, 0, CUTOFF, false, true);
    	top10DestIps = new ArrayList<String>();
    	for (DBObject val : destIpTop10Result) {
    		top10DestIps.add((String)val.get("destIp"));
    	}
    	
    	//목적지IP TOP10 현황 (로그건수기준)
    	nNo = 1;
    	for (DBObject val : destIpTop10Result) {
    		String strIp = (String)val.get("destIp");
    		
    		Iterable<DBObject> ipTop10Condition = reportDao.FW_Top10LogCondition(assetCode, sEndDay, 0, CUTOFF, strIp, false, true);
    	
    		for (DBObject obj : ipTop10Condition) {
    			HashMap<String,Object> tmpId = (HashMap<String,Object>)obj.get("_id");
    			
    			HashMap<String, Object> map = new HashMap<String, Object>();
    			map.put("no", nNo);
    			map.put("destIp", strIp);
    			map.put("total", ((Number)val.get("count")).longValue());
    			map.put("srcIp", (String)tmpId.get("srcIp"));
    			map.put("port", (Integer)tmpId.get("destPort"));
    			map.put("count", ((Number)obj.get("count")).longValue());
    			map.put("trafficSize", ((Number)obj.get("trafficSize")).longValue());
    			map.put("zone", (String)tmpId.get("destZone"));
    			map.put("policy", (String)tmpId.get("policyId"));
    			
    			FW_6_4_6_3_T.add(map);	
    		}
    		nNo++;
    	}	    	
    	assetReportInfo.put("FW_6_4_6_3_T", new SynthesisDataSource(FW_6_4_6_3_T));
    	assetReportInfo.put("SubChapter44", addContents("다) 로그건수 별 목적지IP TOP10 현황", "      "));
    	
    	//서비스 TOP10 현황 (로그건수기준)
    	nNo = 1;
    	for (HashMap<String, Object> val : FW_6_4_4_G) {
    		int nPort = (Integer)val.get("port");
    		long totalCount = ((Number)val.get("count")).longValue();
    		
    		Iterable<DBObject> svcTop10Condition = reportDao.FW_Top10ServiceCondition(assetCode, sEndDay, 0, CUTOFF, nPort, true);
    		
    		for (DBObject obj : svcTop10Condition) {
    			HashMap<String,Object> tmpId = (HashMap<String,Object>)obj.get("_id");
    			
    			HashMap<String, Object> map = new HashMap<String, Object>();
    			map.put("no", nNo);
    			map.put("port", nPort);
    			map.put("total", totalCount);
    			map.put("srcIp", (String)tmpId.get("srcIp"));
    			map.put("destIp", (String)tmpId.get("destIp"));
    			map.put("count", ((Number)obj.get("count")).longValue());
    			map.put("trafficSize", ((Number)obj.get("trafficSize")).longValue());
    			map.put("zone", (String)tmpId.get("destZone"));
    			map.put("policy", (String)tmpId.get("policyId"));
    			
    			FW_6_4_6_4_T.add(map);	
    		}
    		nNo++;
    	}
    	assetReportInfo.put("FW_6_4_6_4_T", new SynthesisDataSource(FW_6_4_6_4_T));
    	assetReportInfo.put("SubChapter45", addContents("라) 로그건수 별 서비스 TOP10 현황", "      "));
	}

	@SuppressWarnings("unchecked")
	public void FW_InboundSessionLog(String sStartDay, String sEndDay, int assetCode, HashMap<String, Object> assetReportInfo) throws Exception {
		
    	/*
		 * 보고서 6.3.1번 항목, 월간 허용 서비스  TOP10 분포 현황
		 */
    	List<HashMap<String, Object>> FW_6_3_1_G = new ArrayList<HashMap<String, Object>>();
    	
    	assetReportInfo.put("SubChapter10", addContents(Depth1 + ".3 외부에서 내부로의 세션 로그 분석", "  "));
    	
    	//내부/허용
    	List<DBObject> logResult = reportDao.FW_ServiceSessionLog(assetCode, 1, ALLOW, sEndDay, true);   	

    	List<String> monthPeriod = reportDao.getPeriodMonth(sEndDay, -1, 1);
    	
    	int nTop = 1;
    	long lTotalCount = 0;
    	HashMap<String, Object> currMap = new HashMap<String, Object>(); 
    	HashMap<String, Object> prevMap = new HashMap<String, Object>();
    	for (DBObject val : logResult) {
    		String sYear = ((Integer)val.get("year")).toString();
    		String sMonth = ((Integer)val.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("year", sYear);
			map.put("month", sMonth);
			map.put("port", (Integer)val.get("destPort"));
			map.put("count", ((Number)val.get("count")).longValue());
			map.put("trafficSize", ((Number)val.get("trafficSize")).longValue());
    		
    		if (monthPeriod.get(0).equals(sYear + sMonth)) {
    			prevMap.put(String.valueOf((Integer)val.get("destPort")), map);
    		} else if (monthPeriod.get(1).equals(sYear + sMonth)) {
    		
    			if ((Integer)val.get("destPort") != -1 && nTop <= 10) {
    				HashMap<String, Object> map1 = new HashMap<String, Object>();
    				map1.put("no", nTop);		
    				map1.put("port", (Integer)val.get("destPort"));
    				map1.put("svcName", reportDao.getPortServiceName((Integer)val.get("destPort")));

    				HashMap<String, Object> pMap = (HashMap<String, Object>)prevMap.remove(String.valueOf((Integer)val.get("destPort")));
    				
    				long lCurrCount = ((Number)val.get("count")).longValue();
    				long lCurrTraffic = ((Number)val.get("trafficSize")).longValue();

    				long lPrevCount = pMap != null ? ((Number)pMap.get("count")).longValue() : 0; 
    				long lPrevTraffic = pMap != null ? ((Number)pMap.get("trafficSize")).longValue() : 0; 
    				
    				lTotalCount += lCurrCount;
    				
    				map1.put("count", lCurrCount);
    				map1.put("countVariation", lCurrCount - lPrevCount);
    				map1.put("trafficSize", lCurrTraffic);
    				map1.put("trafficVariation", lCurrTraffic - lPrevTraffic);
    				
    				nTop++;
    				
    				FW_6_3_1_G.add(map1);
    			} else {
    				lTotalCount += ((Number)val.get("count")).longValue();
    				currMap.put(String.valueOf((Integer)val.get("destPort")), map);
    			}
    		}
    	}
    	
    	long lPrevEtcCount = 0;
    	long lPrevEtcTraffic = 0;
    	Set<Entry<String, Object>> prevEntry = prevMap.entrySet();
    	for (Entry<String, Object> obj : prevEntry) {
    		HashMap<String,Object> map = (HashMap<String,Object>)obj.getValue();
    		lPrevEtcCount += ((Number)map.get("count")).longValue();
    		lPrevEtcTraffic += ((Number)map.get("trafficSize")).longValue();
    	}
    	
    	long lCurrEtcCount = 0;
    	long lCurrEtcTraffic = 0;
    	Set<Entry<String, Object>> currEntry = currMap.entrySet();
    	for (Entry<String, Object> obj : currEntry) {
    		HashMap<String,Object> map = (HashMap<String,Object>)obj.getValue();
    		lCurrEtcCount += ((Number)map.get("count")).longValue();
    		lCurrEtcTraffic += ((Number)map.get("trafficSize")).longValue();
    	}

    	if (FW_6_3_1_G.size() > 0) {
	    	HashMap<String, Object> map1 = new HashMap<String, Object>();
			map1.put("no", nTop);		
			map1.put("port", -1);
			map1.put("svcName", "기타");
			map1.put("count", lCurrEtcCount);
			map1.put("countVariation", lCurrEtcCount - lPrevEtcCount);
			map1.put("trafficSize", lCurrEtcTraffic);
			map1.put("trafficVariation", lCurrEtcTraffic - lPrevEtcTraffic);
			FW_6_3_1_G.add(map1);
    	}
    	
		for (HashMap<String, Object> map : FW_6_3_1_G) {
			long count = ((Number)map.get("count")).longValue();
			map.put("ratio", (count * 100f) / lTotalCount);
		}
		
    	assetReportInfo.put("FW_6_3_1_G", new SynthesisDataSource(FW_6_3_1_G));
    	assetReportInfo.put("SubChapter11", addContents(Depth1 + ".3.1 월간 허용 서비스 TOP10 분포 현황", "    "));
    	
    	assetReportInfo.put("FW_6_3_2_T", new SynthesisDataSource(FW_6_3_1_G));
    	assetReportInfo.put("SubChapter12", addContents(Depth1 + ".3.2 월간 허용 서비스 TOP10 증감 현황", "    "));
    	
    	/*
		 * 보고서 6.3.3번 항목, 월간 허용 로그 상세 분석
		 */
    	List<HashMap<String, Object>> FW_6_3_3_1_G = new ArrayList<HashMap<String, Object>>();
    	//List<HashMap<String, Object>> FW_6_3_3_2_G = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> FW_6_3_3_3_T = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> FW_6_3_3_4_T = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> FW_6_3_3_5_T = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> FW_6_3_3_6_T = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> FW_6_3_3_7_T = new ArrayList<HashMap<String, Object>>();
    	
    	assetReportInfo.put("SubChapter13", addContents(Depth1 + ".3.3 월간 허용 세션로그 상세", "    "));
    	
    	//출발지IP 건수기준 TOP10 리스트
    	List<DBObject> srcIpTop10Result = reportDao.FW_Top10Ips(assetCode, sEndDay, 1, ALLOW, true, true);
    	List<String> top10SrcIps = new ArrayList<String>();
    	for (DBObject val : srcIpTop10Result) {
    		top10SrcIps.add((String)val.get("srcIp"));
    	}
    	
    	//출발지IP TOP10에 대한 세션로그 일별 발생 추이
    	Iterable<DBObject> ipTop10Result = reportDao.FW_Top10LogDetail(assetCode, sStartDay, sEndDay, 1, ALLOW, top10SrcIps, true);
    	
    	List<String> dayPeriod = reportDao.getPeriodDay(sStartDay, sEndDay, 0, 1);
    	
    	HashMap<String, DBObject> mapResult = new HashMap<String, DBObject>(); 
    	for (DBObject val : ipTop10Result) {
    		HashMap<String,Object> mapId = (HashMap<String,Object>)val.get("_id");
    		String sSrcIp = (String)mapId.get("srcIp");
    		String sYear = ((Integer)mapId.get("year")).toString();
    		String sMonth = ((Integer)mapId.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    		String sDay = ((Integer)mapId.get("day")).toString();
    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
    		
    		mapResult.put(sSrcIp + "-" + sYear + sMonth + sDay, val);
    	}
    	
    	for (String strIp : top10SrcIps) {
	    	for (int i = 0; i < dayPeriod.size(); i++) {
	    		DBObject val = mapResult.get(strIp + "-" + dayPeriod.get(i));

	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		map.put("srcIp", strIp);
	    		map.put("day", Integer.valueOf(dayPeriod.get(i).substring(6,8)));
	    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
	    		map.put("trafficSize", val != null ? ((Number)val.get("trafficSize")).longValue() : 0);
	    		FW_6_3_3_1_G.add(map);
	    	}
    	}
    	assetReportInfo.put("FW_6_3_3_1_G", new SynthesisDataSource(FW_6_3_3_1_G));
    	assetReportInfo.put("SubChapter14", addContents("가) 출발지IP TOP10 에 대한 세션 로그 발생 추이", "      "));
    	
    	assetReportInfo.put("FW_6_3_3_2_G", new SynthesisDataSource(FW_6_3_3_1_G));
    	assetReportInfo.put("SubChapter15", addContents("나) 출발지IP TOP10 에 대한 통신량 추이", "      "));
    	
    	//출발지IP TOP10 현황 (로그건수기준)
    	int nNo = 1;
    	for (DBObject val : srcIpTop10Result) {
    		String strIp = (String)val.get("srcIp");

    		Iterable<DBObject> ipTop10Condition = reportDao.FW_Top10LogCondition(assetCode, sEndDay, 1, ALLOW, strIp, true, true);
    	
    		for (DBObject obj : ipTop10Condition) {
    			HashMap<String,Object> tmpId = (HashMap<String,Object>)obj.get("_id");
    			
    			HashMap<String, Object> map = new HashMap<String, Object>();
    			map.put("no", nNo);
    			map.put("srcIp", strIp);
    			map.put("total", ((Number)val.get("count")).longValue());
    			map.put("destIp", (String)tmpId.get("destIp"));
    			map.put("port", (Integer)tmpId.get("destPort"));
    			map.put("count", ((Number)obj.get("count")).longValue());
    			map.put("trafficSize", ((Number)obj.get("trafficSize")).longValue());
    			map.put("zone", (String)tmpId.get("destZone"));
    			map.put("policy", (String)tmpId.get("policyId"));
    			
    			FW_6_3_3_3_T.add(map);	
    		}
    		nNo++;
    	}	    	
    	assetReportInfo.put("FW_6_3_3_3_T", new SynthesisDataSource(FW_6_3_3_3_T));
    	assetReportInfo.put("SubChapter16", addContents("다) 로그건수 별 출발지IP TOP10 현황", "      "));
    	
    	//출발지IP 통신량기준 TOP10 리스트
    	srcIpTop10Result = reportDao.FW_Top10Ips(assetCode, sEndDay, 1, ALLOW, true, false);
    	top10SrcIps = new ArrayList<String>();
    	for (DBObject val : srcIpTop10Result) {
    		top10SrcIps.add((String)val.get("srcIp"));
    	}

    	//출발지IP TOP10 현황 (통신량기준)
    	nNo = 1;
    	for (DBObject val : srcIpTop10Result) {
    		String strIp = (String)val.get("srcIp");
    		
    		Iterable<DBObject> ipTop10Condition = reportDao.FW_Top10LogCondition(assetCode, sEndDay, 1, ALLOW, strIp, true, false);
    	
    		for (DBObject obj : ipTop10Condition) {
    			HashMap<String,Object> tmpId = (HashMap<String,Object>)obj.get("_id");
    			
    			HashMap<String, Object> map = new HashMap<String, Object>();
    			map.put("no", nNo);
    			map.put("srcIp", strIp);
    			map.put("total", ((Number)val.get("trafficSize")).longValue());
    			map.put("destIp", (String)tmpId.get("destIp"));
    			map.put("port", (Integer)tmpId.get("destPort"));
    			map.put("count", ((Number)obj.get("count")).longValue());
    			map.put("trafficSize", ((Number)obj.get("trafficSize")).longValue());
    			map.put("zone", (String)tmpId.get("destZone"));
    			map.put("policy", (String)tmpId.get("policyId"));
    			
    			FW_6_3_3_4_T.add(map);	
    		}
    		nNo++;
    	}	    	
    	assetReportInfo.put("FW_6_3_3_4_T", new SynthesisDataSource(FW_6_3_3_4_T));
    	assetReportInfo.put("SubChapter17", addContents("라) 통신량 별 출발지IP TOP10 현황", "      "));
    	
    	//목적지IP 건수기준 TOP10 리스트
    	List<DBObject> destIpTop10Result = reportDao.FW_Top10Ips(assetCode, sEndDay, 1, ALLOW, false, true);
    	List<String> top10DestIps = new ArrayList<String>();
    	for (DBObject val : destIpTop10Result) {
    		top10DestIps.add((String)val.get("destIp"));
    	}
    	
    	//목적지IP TOP10 현황 (로그건수기준)
    	nNo = 1;
    	for (DBObject val : destIpTop10Result) {
    		String strIp = (String)val.get("destIp");
    		
    		Iterable<DBObject> ipTop10Condition = reportDao.FW_Top10LogCondition(assetCode, sEndDay, 1, ALLOW, strIp, false, true);
    	
    		for (DBObject obj : ipTop10Condition) {
    			HashMap<String,Object> tmpId = (HashMap<String,Object>)obj.get("_id");
    			
    			HashMap<String, Object> map = new HashMap<String, Object>();
    			map.put("no", nNo);
    			map.put("destIp", strIp);
    			map.put("total", ((Number)val.get("count")).longValue());
    			map.put("srcIp", (String)tmpId.get("srcIp"));
    			map.put("port", (Integer)tmpId.get("destPort"));
    			map.put("count", ((Number)obj.get("count")).longValue());
    			map.put("trafficSize", ((Number)obj.get("trafficSize")).longValue());
    			map.put("zone", (String)tmpId.get("destZone"));
    			map.put("policy", (String)tmpId.get("policyId"));
    			
    			FW_6_3_3_5_T.add(map);	
    		}
    		nNo++;
    	}	    	
    	assetReportInfo.put("FW_6_3_3_5_T", new SynthesisDataSource(FW_6_3_3_5_T));
    	assetReportInfo.put("SubChapter18", addContents("마) 로그건수 별 목적지IP TOP10 현황", "      "));

    	//서비스 TOP10 현황 (로그건수기준)
    	nNo = 1;
    	for (HashMap<String, Object> val : FW_6_3_1_G) {
    		int nPort = (Integer)val.get("port");
    		long totalCount = ((Number)val.get("count")).longValue();
    		
    		Iterable<DBObject> svcTop10Condition = reportDao.FW_Top10ServiceCondition(assetCode, sEndDay, 1, ALLOW, nPort, true);
    		
    		for (DBObject obj : svcTop10Condition) {
    			HashMap<String,Object> tmpId = (HashMap<String,Object>)obj.get("_id");
    			
    			HashMap<String, Object> map = new HashMap<String, Object>();
    			map.put("no", nNo);
    			map.put("port", nPort);
    			map.put("total", totalCount);
    			map.put("srcIp", (String)tmpId.get("srcIp"));
    			map.put("destIp", (String)tmpId.get("destIp"));
    			map.put("count", ((Number)obj.get("count")).longValue());
    			map.put("trafficSize", ((Number)obj.get("trafficSize")).longValue());
    			map.put("zone", (String)tmpId.get("destZone"));
    			map.put("policy", (String)tmpId.get("policyId"));
    			
    			FW_6_3_3_6_T.add(map);	
    		}
    		nNo++;
    	}
    	assetReportInfo.put("FW_6_3_3_6_T", new SynthesisDataSource(FW_6_3_3_6_T));
    	assetReportInfo.put("SubChapter19", addContents("바) 로그건수 별 서비스 TOP10 현황", "      "));
    	
    	//서비스 TOP10 현황 (통신량기준)
    	List<HashMap<String, Object>> tempList = new ArrayList<HashMap<String, Object>>();
    	nTop = 1;
    	logResult = reportDao.FW_ServiceSessionLog(assetCode, 1, ALLOW, sEndDay, false);
    	for (DBObject val : logResult) {
    		String sYear = ((Integer)val.get("year")).toString();
    		String sMonth = ((Integer)val.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    		
    		if (monthPeriod.get(1).equals(sYear + sMonth)) {
    			if ((Integer)val.get("destPort") == -1) {
    				continue;
    			}
    			
    			if (nTop <= 10) {
    				HashMap<String, Object> map = new HashMap<String, Object>();
    				long lCurrTraffic = ((Number)val.get("trafficSize")).longValue();
    				map.put("port", (Integer)val.get("destPort"));
    				map.put("trafficSize", lCurrTraffic);
    				tempList.add(map);
    				nTop++;
    			} else {
    				break;
    			}
    		}
    	}
    	
    	nNo = 1;
    	for (HashMap<String, Object> val : tempList) {
    		int nPort = (Integer)val.get("port");
    		long totalTraffic = ((Number)val.get("trafficSize")).longValue();
    		
    		Iterable<DBObject> svcTop10Condition = reportDao.FW_Top10ServiceCondition(assetCode, sEndDay, 1, ALLOW, nPort, false);
    		
    		for (DBObject obj : svcTop10Condition) {
    			HashMap<String,Object> tmpId = (HashMap<String,Object>)obj.get("_id");
    			
    			HashMap<String, Object> map = new HashMap<String, Object>();
    			map.put("no", nNo);
    			map.put("port", nPort);
    			map.put("total", totalTraffic);
    			map.put("srcIp", (String)tmpId.get("srcIp"));
    			map.put("destIp", (String)tmpId.get("destIp"));
    			map.put("count", ((Number)obj.get("count")).longValue());
    			map.put("trafficSize", ((Number)obj.get("trafficSize")).longValue());
    			map.put("zone", (String)tmpId.get("destZone"));
    			map.put("policy", (String)tmpId.get("policyId"));
    			FW_6_3_3_7_T.add(map);	
    		}
    		nNo++;
    	}
    	assetReportInfo.put("FW_6_3_3_7_T", new SynthesisDataSource(FW_6_3_3_7_T));
    	assetReportInfo.put("SubChapter20", addContents("사) 통신량 별 서비스 TOP10 현황", "      "));
    	

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	    	
    	
    	/*
		 * 보고서 6.3.4번 항목, 월간 차단 서비스  TOP10 분포 현황
		 */
    	List<HashMap<String, Object>> FW_6_3_4_G = new ArrayList<HashMap<String, Object>>();
    	
    	//내부/차단
    	logResult = reportDao.FW_ServiceSessionLog(assetCode, 1, CUTOFF, sEndDay, true);   	

    	monthPeriod = reportDao.getPeriodMonth(sEndDay, -1, 1);
    	
    	nTop = 1;
    	lTotalCount = 0;
    	currMap = new HashMap<String, Object>(); 
    	prevMap = new HashMap<String, Object>();
    	for (DBObject val : logResult) {
    		String sYear = ((Integer)val.get("year")).toString();
    		String sMonth = ((Integer)val.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("year", sYear);
			map.put("month", sMonth);
			map.put("port", (Integer)val.get("destPort"));
			map.put("count", ((Number)val.get("count")).longValue());
			map.put("trafficSize", ((Number)val.get("trafficSize")).longValue());
    		
    		if (monthPeriod.get(0).equals(sYear + sMonth)) {
    			prevMap.put(String.valueOf((Integer)val.get("destPort")), map);
    		} else if (monthPeriod.get(1).equals(sYear + sMonth)) {
    		
    			if ((Integer)val.get("destPort") != -1 && nTop <= 10) {
    				HashMap<String, Object> map1 = new HashMap<String, Object>();
    				map1.put("no", nTop);		
    				map1.put("port", (Integer)val.get("destPort"));
    				map1.put("svcName", reportDao.getPortServiceName((Integer)val.get("destPort")));

    				HashMap<String, Object> pMap = (HashMap<String, Object>)prevMap.remove(String.valueOf((Integer)val.get("destPort")));
    				
    				long lCurrCount = ((Number)val.get("count")).longValue();
    				long lCurrTraffic = ((Number)val.get("trafficSize")).longValue();

    				long lPrevCount = pMap != null ? ((Number)pMap.get("count")).longValue() : 0; 
    				long lPrevTraffic = pMap != null ? ((Number)pMap.get("trafficSize")).longValue() : 0; 
    				
    				lTotalCount += lCurrCount;
    				
    				map1.put("count", lCurrCount);
    				map1.put("countVariation", lCurrCount - lPrevCount);
    				map1.put("trafficSize", lCurrTraffic);
    				map1.put("trafficVariation", lCurrTraffic - lPrevTraffic);
    				
    				nTop++;
    				
    				FW_6_3_4_G.add(map1);
    			} else {
    				lTotalCount += ((Number)val.get("count")).longValue();
    				currMap.put(String.valueOf((Integer)val.get("destPort")), map);
    			}
    		}
    	}

    	lPrevEtcCount = 0;
    	lPrevEtcTraffic = 0;
    	prevEntry = prevMap.entrySet();
    	for (Entry<String, Object> obj : prevEntry) {
    		HashMap<String,Object> map = (HashMap<String,Object>)obj.getValue();
    		lPrevEtcCount += ((Number)map.get("count")).longValue();
    		lPrevEtcTraffic += ((Number)map.get("trafficSize")).longValue();
    	}
    	
    	lCurrEtcCount = 0;
    	lCurrEtcTraffic = 0;
    	currEntry = currMap.entrySet();
    	for (Entry<String, Object> obj : currEntry) {
    		HashMap<String,Object> map = (HashMap<String,Object>)obj.getValue();
    		lCurrEtcCount += ((Number)map.get("count")).longValue();
    		lCurrEtcTraffic += ((Number)map.get("trafficSize")).longValue();
    	}

    	if (FW_6_3_4_G.size() > 0) {
	    	HashMap<String, Object> map1 = new HashMap<String, Object>();
			map1.put("no", nTop);		
			map1.put("port", -1);
			map1.put("svcName", "기타");
			map1.put("count", lCurrEtcCount);
			map1.put("countVariation", lCurrEtcCount - lPrevEtcCount);
			map1.put("trafficSize", lCurrEtcTraffic);
			map1.put("trafficVariation", lCurrEtcTraffic - lPrevEtcTraffic);
			FW_6_3_4_G.add(map1);
    	}
    	
		for (HashMap<String, Object> map : FW_6_3_4_G) {
			long count = ((Number)map.get("count")).longValue();
			map.put("ratio", (count * 100f) / lTotalCount);
		}
		
    	assetReportInfo.put("FW_6_3_4_G", new SynthesisDataSource(FW_6_3_4_G));
    	assetReportInfo.put("SubChapter21", addContents(Depth1 + ".3.4 월간 차단 서비스 TOP10 분포 현황", "    "));
    	
    	assetReportInfo.put("FW_6_3_5_T", new SynthesisDataSource(FW_6_3_4_G));
    	assetReportInfo.put("SubChapter22", addContents(Depth1 + ".3.5 월간 차단 서비스 TOP10 증감 현황", "    "));
    	
    	/*
		 * 보고서 6.3.4번 항목, 월간 차단 로그 상세 분석
		 */
    	List<HashMap<String, Object>> FW_6_3_6_1_G = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> FW_6_3_6_2_T = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> FW_6_3_6_3_T = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> FW_6_3_6_4_T = new ArrayList<HashMap<String, Object>>();
    	
    	assetReportInfo.put("SubChapter23", addContents(Depth1 + ".3.6 월간 차단 세션로그 상세", "    "));
    	
    	//출발지IP 건수기준 TOP10 리스트
    	srcIpTop10Result = reportDao.FW_Top10Ips(assetCode, sEndDay, 1, CUTOFF, true, true);
    	top10SrcIps = new ArrayList<String>();
    	for (DBObject val : srcIpTop10Result) {
    		top10SrcIps.add((String)val.get("srcIp"));
    	}
    	
    	//출발지IP TOP10에 대한 세션로그 일별 발생 추이
    	ipTop10Result = reportDao.FW_Top10LogDetail(assetCode, sStartDay, sEndDay, 1, CUTOFF, top10SrcIps, true);
    	
    	dayPeriod = reportDao.getPeriodDay(sStartDay, sEndDay, 0, 1);
    	
    	mapResult = new HashMap<String, DBObject>(); 
    	for (DBObject val : ipTop10Result) {
    		HashMap<String,Object> mapId = (HashMap<String,Object>)val.get("_id");
    		String sSrcIp = (String)mapId.get("srcIp");
    		String sYear = ((Integer)mapId.get("year")).toString();
    		String sMonth = ((Integer)mapId.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    		String sDay = ((Integer)mapId.get("day")).toString();
    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
    		
    		mapResult.put(sSrcIp + "-" + sYear + sMonth + sDay, val);
    	}
    	
    	for (String strIp : top10SrcIps) {
	    	for (int i = 0; i < dayPeriod.size(); i++) {
	    		DBObject val = mapResult.get(strIp + "-" + dayPeriod.get(i));

	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		map.put("srcIp", strIp);
	    		map.put("day", Integer.valueOf(dayPeriod.get(i).substring(6,8)));
	    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
	    		map.put("trafficSize", val != null ? ((Number)val.get("trafficSize")).longValue() : 0);
	    		FW_6_3_6_1_G.add(map);
	    	}
    	}
    	assetReportInfo.put("FW_6_3_6_1_G", new SynthesisDataSource(FW_6_3_6_1_G));
    	assetReportInfo.put("SubChapter24", addContents("가) 출발지IP TOP10 에 대한 세션 로그 발생 추이", "      "));
    	
    	//출발지IP TOP10 현황 (로그건수기준)
    	nNo = 1;
    	for (DBObject val : srcIpTop10Result) {
    		String strIp = (String)val.get("srcIp");

    		Iterable<DBObject> ipTop10Condition = reportDao.FW_Top10LogCondition(assetCode, sEndDay, 1, CUTOFF, strIp, true, true);
    	
    		for (DBObject obj : ipTop10Condition) {
    			HashMap<String,Object> tmpId = (HashMap<String,Object>)obj.get("_id");
    			
    			HashMap<String, Object> map = new HashMap<String, Object>();
    			map.put("no", nNo);
    			map.put("srcIp", strIp);
    			map.put("total", ((Number)val.get("count")).longValue());
    			map.put("destIp", (String)tmpId.get("destIp"));
    			map.put("port", (Integer)tmpId.get("destPort"));
    			map.put("count", ((Number)obj.get("count")).longValue());
    			map.put("trafficSize", ((Number)obj.get("trafficSize")).longValue());
    			map.put("zone", (String)tmpId.get("destZone"));
    			map.put("policy", (String)tmpId.get("policyId"));
    			
    			FW_6_3_6_2_T.add(map);	
    		}
    		nNo++;
    	}	    	
    	assetReportInfo.put("FW_6_3_6_2_T", new SynthesisDataSource(FW_6_3_6_2_T));
    	assetReportInfo.put("SubChapter25", addContents("나) 로그건수 별 출발지IP TOP10 현황", "      "));
    	
    	//목적지IP 건수기준 TOP10 리스트
    	destIpTop10Result = reportDao.FW_Top10Ips(assetCode, sEndDay, 1, CUTOFF, false, true);
    	top10DestIps = new ArrayList<String>();
    	for (DBObject val : destIpTop10Result) {
    		top10DestIps.add((String)val.get("destIp"));
    	}
    	
    	//목적지IP TOP10 현황 (로그건수기준)
    	nNo = 1;
    	for (DBObject val : destIpTop10Result) {
    		String strIp = (String)val.get("destIp");
    		
    		Iterable<DBObject> ipTop10Condition = reportDao.FW_Top10LogCondition(assetCode, sEndDay, 1, CUTOFF, strIp, false, true);
    	
    		for (DBObject obj : ipTop10Condition) {
    			HashMap<String,Object> tmpId = (HashMap<String,Object>)obj.get("_id");
    			
    			HashMap<String, Object> map = new HashMap<String, Object>();
    			map.put("no", nNo);
    			map.put("destIp", strIp);
    			map.put("total", ((Number)val.get("count")).longValue());
    			map.put("srcIp", (String)tmpId.get("srcIp"));
    			map.put("port", (Integer)tmpId.get("destPort"));
    			map.put("count", ((Number)obj.get("count")).longValue());
    			map.put("trafficSize", ((Number)obj.get("trafficSize")).longValue());
    			map.put("zone", (String)tmpId.get("destZone"));
    			map.put("policy", (String)tmpId.get("policyId"));
    			
    			FW_6_3_6_3_T.add(map);	
    		}
    		nNo++;
    	}	    	
    	assetReportInfo.put("FW_6_3_6_3_T", new SynthesisDataSource(FW_6_3_6_3_T));
    	assetReportInfo.put("SubChapter26", addContents("다) 로그건수 별 목적지IP TOP10 현황", "      "));
    	
    	//서비스 TOP10 현황 (로그건수기준)
    	nNo = 1;
    	for (HashMap<String, Object> val : FW_6_3_4_G) {
    		int nPort = (Integer)val.get("port");
    		long totalCount = ((Number)val.get("count")).longValue();
    		
    		Iterable<DBObject> svcTop10Condition = reportDao.FW_Top10ServiceCondition(assetCode, sEndDay, 1, CUTOFF, nPort, true);
    		
    		for (DBObject obj : svcTop10Condition) {
    			HashMap<String,Object> tmpId = (HashMap<String,Object>)obj.get("_id");
    			
    			HashMap<String, Object> map = new HashMap<String, Object>();
    			map.put("no", nNo);
    			map.put("port", nPort);
    			map.put("total", totalCount);
    			map.put("srcIp", (String)tmpId.get("srcIp"));
    			map.put("destIp", (String)tmpId.get("destIp"));
    			map.put("count", ((Number)obj.get("count")).longValue());
    			map.put("trafficSize", ((Number)obj.get("trafficSize")).longValue());
    			map.put("zone", (String)tmpId.get("destZone"));
    			map.put("policy", (String)tmpId.get("policyId"));
    			
    			FW_6_3_6_4_T.add(map);	
    		}
    		nNo++;
    	}
    	assetReportInfo.put("FW_6_3_6_4_T", new SynthesisDataSource(FW_6_3_6_4_T));
    	assetReportInfo.put("SubChapter27", addContents("라) 로그건수 별 서비스 TOP10 현황", "      "));
	}	
	
	public void FW_SessionLogDailyChange(String sStartDay, String sEndDay, int assetCode, HashMap<String, Object> assetReportInfo) throws Exception {

    	List<HashMap<String, Object>> FW_6_2_1_G = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> FW_6_2_2_G = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> FW_6_2_3_G = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> FW_6_2_4_G = new ArrayList<HashMap<String, Object>>();
    	
    	assetReportInfo.put("SubChapter5", addContents(Depth1 + ".2 금월 일별 세션 로그 추이", "  "));
    	
    	List<DBObject> outToIn = reportDao.FW_SessionLogDay(assetCode, sStartDay, sEndDay);   	
    	
    	HashMap<String, DBObject> mapOutToIn = new HashMap<String, DBObject>(); 
    	for (DBObject val : outToIn) {
    		String sDirection = ((Integer)val.get("direction")).toString();
    		String sAction = (String)val.get("action");
    		String sYear = ((Integer)val.get("year")).toString();
    		String sMonth = ((Integer)val.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    		String sDay = ((Integer)val.get("day")).toString();
    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
    		mapOutToIn.put(sDirection + sAction + sYear + sMonth + sDay, val);
    	}
    	
    	List<String> dayPeriod = reportDao.getPeriodDay(sStartDay, sEndDay, -1, 1);
    	
    	for (int i = 0; i < 2; i++) { //외부/내부/
    		for (int j = 0; j < 2; j++) { //허용/차단
    			
    			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
    			Date endDate = sdf.parse(sEndDay);
    			Calendar endCal = reportDao.getCalendar(endDate);
    			endCal.add(Calendar.MONTH, -2);
    			
    			for (int d = 0; d < dayPeriod.size(); d++) {
		    		HashMap<String, Object> map = new HashMap<String, Object>();
		    		DBObject val = mapOutToIn.get(String.valueOf(i) + String.valueOf(j) + dayPeriod.get(d));
		    		
		    		if (Integer.valueOf(dayPeriod.get(d).substring(6,8)) == Integer.valueOf(sStartDay.substring(8, 10))) {
		    			endCal.add(Calendar.MONTH, 1);
		    		}
	    			map.put("gubun", (endCal.get(Calendar.MONTH)+1) + "월");
	    			map.put("day", Integer.valueOf(dayPeriod.get(d).substring(6,8)));
		    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);

		    		if (i == 1 && j ==1) {
			    		FW_6_2_1_G.add(map);
		    		} else if (i == 1 && j ==0) {
			    		FW_6_2_2_G.add(map);
		    		} else if (i == 0 && j ==1) {
			    		FW_6_2_3_G.add(map);
		    		} else if (i == 0 && j ==0) {
			    		FW_6_2_4_G.add(map);
		    		}
    			}
    		}
    	}
    	assetReportInfo.put("FW_6_2_1_G", new SynthesisDataSource(FW_6_2_1_G));
    	assetReportInfo.put("SubChapter6", addContents(Depth1 + ".2.1 외부에서 내부로의 허용 세션 로그 ", "    "));
    	
    	assetReportInfo.put("FW_6_2_2_G", new SynthesisDataSource(FW_6_2_2_G));
    	assetReportInfo.put("SubChapter7", addContents(Depth1 + ".2.2 외부에서 내부로의 차단 세션 로그 ", "    "));
    	
    	assetReportInfo.put("FW_6_2_3_G", new SynthesisDataSource(FW_6_2_3_G));
    	assetReportInfo.put("SubChapter8", addContents(Depth1 + ".2.3 내부에서 외부로의 허용 세션 로그  ", "    "));
    	
    	assetReportInfo.put("FW_6_2_4_G", new SynthesisDataSource(FW_6_2_4_G));
    	assetReportInfo.put("SubChapter9", addContents(Depth1 + ".2.4 내부에서 외부로의 차단 세션 로그  ", "    "));
	}
	
	public void FW_SessionLogMonthlyChange(String sStartDay, String sEndDay, int assetCode, int[] nSearchPorts, HashMap<String, Object> assetReportInfo) throws Exception {

    	List<HashMap<String, Object>> FW_6_1_1_G = new ArrayList<HashMap<String, Object>>();
    	List<DBObject> outToIn = reportDao.FW_SessionLogMonth(assetCode, 1, sEndDay, 0);   	

    	assetReportInfo.put("SubChapter2", addContents(Depth1 + ".1 최근 6개월 세션 로그 추이", "  "));
    	
    	HashMap<String, DBObject> mapOutToIn = new HashMap<String, DBObject>(); 
    	for (DBObject val : outToIn) {
    		String sAction = (String)val.get("action");
    		String sYear = ((Integer)val.get("year")).toString();
    		String sMonth = ((Integer)val.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    		mapOutToIn.put(sAction + sYear + sMonth, val);
    	}

    	List<String> monthPeriod = reportDao.getPeriodMonth(sEndDay, -5, 1);
    	
    	String[] saGubun = {"차단", "허용"};
    	for (int i = 1; i >= 0; i--) {
    		for (int j = 0; j < 6; j++) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		DBObject val = mapOutToIn.get(String.valueOf(i) + monthPeriod.get(j));
	    		map.put("gubun", saGubun[i]);
	    		map.put("month", Integer.valueOf(monthPeriod.get(j)));
	    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
	    		
	    		FW_6_1_1_G.add(map);
    		}	    		
    	}

    	if (nSearchPorts != null) {
    		for (int i = 0; i < nSearchPorts.length; i++) {
	    		String strServiceName = reportDao.getPortServiceName(nSearchPorts[i]);
		    	outToIn = reportDao.FW_SessionLogMonth(assetCode, 1, sEndDay, nSearchPorts[i]);   	
	    	
		    	mapOutToIn = new HashMap<String, DBObject>(); 
		    	for (DBObject val : outToIn) {
		    		String sYear = ((Integer)val.get("year")).toString();
		    		String sMonth = ((Integer)val.get("month")).toString();
		    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
		    		mapOutToIn.put(sYear + sMonth, val);
		    	}
	    	
	    		for (int j = 0; j < 6; j++) {
		    		HashMap<String, Object> map = new HashMap<String, Object>();
		    		DBObject val = mapOutToIn.get(monthPeriod.get(j));
		    		map.put("gubun", strServiceName);
		    		map.put("month", Integer.valueOf(monthPeriod.get(j)));
		    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
		    		
		    		FW_6_1_1_G.add(map);
	    		}
    	    }
    	}
    	assetReportInfo.put("FW_6_1_1_G", new SynthesisDataSource(FW_6_1_1_G));
    	assetReportInfo.put("FW_6_1_1_T", new SynthesisDataSource(FW_6_1_1_G));
    	assetReportInfo.put("SubChapter3", addContents(Depth1 + ".1.1 외부에서 내부로의 세션 로그 발생 추이", "    "));
    	
    	/*
		 * 보고서 6.1.2번 항목, 내부에서 외부로의 세션로그 발생 추이
		 */
    	List<HashMap<String, Object>> FW_6_1_2_G = new ArrayList<HashMap<String, Object>>();
    	List<DBObject> inToOut = reportDao.FW_SessionLogMonth(assetCode, 0, sEndDay, 0);   	
    	
    	HashMap<String, DBObject> mapInToOut = new HashMap<String, DBObject>(); 
    	for (DBObject val : inToOut) {
    		String sAction = (String)val.get("action");
    		String sYear = ((Integer)val.get("year")).toString();
    		String sMonth = ((Integer)val.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    		mapInToOut.put(sAction + sYear + sMonth, val);
    	}

    	for (int i = 1; i >= 0; i--) {
    		for (int j = 0; j < 6; j++) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		DBObject val = mapInToOut.get(String.valueOf(i) + monthPeriod.get(j));
	    		map.put("gubun", saGubun[i]);
	    		map.put("month", Integer.valueOf(monthPeriod.get(j)));
	    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
	    		
	    		FW_6_1_2_G.add(map);
    		}	    		
    	}

    	assetReportInfo.put("FW_6_1_2_G", new SynthesisDataSource(FW_6_1_2_G));
    	assetReportInfo.put("FW_6_1_2_T", new SynthesisDataSource(FW_6_1_2_G));		
    	assetReportInfo.put("SubChapter4", addContents(Depth1 + ".1.2 내부에서 외부로의 세션 로그 발생 추이", "    "));
	}
	
	@SuppressWarnings("unused")
	public List<HashMap<String, Object>> getContents() {
		List<HashMap<String, Object>> contents = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < contentsList.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("content", contentsList.get(i));
			contents.add(map);
		}
		return contents;
	}
	
	public String addContents(String strContents) {
		return addContents(strContents, "");
	}
	public String addContents(String strContents, String strIndent) {
		contentsList.add(strIndent + strContents);
		return strContents;
	}

	public HashMap<String, Object> getReportDataSource(
			int Depth, 
			List<String> contentsList, 
			String sStartDay, 
			String sEndDay,
			HashMap<String, Object> assetMap, 
			int[] searchPort) throws Exception {

		this.Depth1 = Depth;
		this.contentsList = contentsList;
		
		HashMap<String, Object> assetReportInfo = new HashMap<String, Object>();
    	
    	assetReportInfo.put("SubChapter1", addContents(Depth1 + ". " + (String)assetMap.get("assetName") + " 월간 로그 분석"));
    	
    	try {
			//외부에서 내부로의 세션로그 발생 추이
	    	FW_SessionLogMonthlyChange(sStartDay, sEndDay, (Integer)assetMap.get("assetCode"), searchPort, assetReportInfo);
	    	
			//외부에서 내부로의 허용 세션 로그 비교 
	    	FW_SessionLogDailyChange(sStartDay, sEndDay, (Integer)assetMap.get("assetCode"), assetReportInfo);
	    	
			//외부에서 내부로의 세션 로그 분석
	    	FW_InboundSessionLog(sStartDay, sEndDay, (Integer)assetMap.get("assetCode"), assetReportInfo);
	    	
			//내부에서 외부로의 세션 로그 분석
	    	FW_OutboundSessionLog(sStartDay, sEndDay, (Integer)assetMap.get("assetCode"), assetReportInfo);
	    	
			//월간 방화벽 성능 추이
	    	FW_PerformanceLog(sStartDay, sEndDay, (Integer)assetMap.get("assetCode"), assetReportInfo);
	    	
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw e;
    	}
    	return assetReportInfo;
	}
}
