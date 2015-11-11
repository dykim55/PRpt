package com.cyberone.report.core.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.cyberone.report.core.dao.DDOSReportDao;
import com.cyberone.report.core.datasource.SynthesisDataSource;
import com.mongodb.DBObject;

public class DDOSService extends BaseService {

	private int Depth1 = 1;
	private List<String> contentsList = new ArrayList<String>();
	private DDOSReportDao reportDao;
	
	public DDOSService(DDOSReportDao reportDao) {
		this.reportDao = reportDao;
	}

	@SuppressWarnings("unchecked")
	private void DDOS_EventMonthlyChange(String sStartDay, String sEndDay, int assetCode, HashMap<String, Object> assetReportInfo) throws Exception {

    	List<HashMap<String, Object>> DDOS_X_1_1 = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> DDOS_X_1_2_1 = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> DDOS_X_1_2_2 = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> DDOS_X_1_3 = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> DDOS_X_1_4_1 = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> DDOS_X_1_4_2 = new ArrayList<HashMap<String, Object>>();
    	
    	List<String> monthPeriod = reportDao.getPeriodMonth(sEndDay, -5, 1);
    	
    	assetReportInfo.put("SubChapter2", addContents(Depth1 + ".1 최근 6개월 추이", "  "));
    	
    	Iterable<DBObject> dbResult = reportDao.DDOS_HalfYearChange(assetCode, sEndDay);    	
    	HashMap<String, DBObject> dbMap = new HashMap<String, DBObject>(); 
    	for (DBObject val : dbResult) {
    		HashMap<String,Object> mapId = (HashMap<String,Object>)val.get("_id");
    		String sYear = ((Integer)mapId.get("year")).toString();
    		String sMonth = ((Integer)mapId.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    		dbMap.put(sYear + sMonth, val);
    	}
    	
		for (int i = 0; i < 6; i++) {
    		HashMap<String, Object> map = new HashMap<String, Object>();
    		DBObject val = dbMap.get(monthPeriod.get(i));
    		map.put("gubun", "탐지건수");
    		map.put("month", Integer.valueOf(monthPeriod.get(i)));
    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
    		DDOS_X_1_1.add(map);
		}	    		

    	assetReportInfo.put("DDOS_X_1_1", new SynthesisDataSource(DDOS_X_1_1));
    	assetReportInfo.put("DDOS_X_1_1_T", new SynthesisDataSource(DDOS_X_1_1));
    	assetReportInfo.put("SubChapter3", addContents(Depth1 + ".1.1 최근 6개월 전체 이벤트 발생 추이", "    "));

    	assetReportInfo.put("SubChapter4", addContents(Depth1 + ".1.2 최근 6개월 전체 공격 규모 추이", "    "));
		for (int i = 0; i < 6; i++) {
    		HashMap<String, Object> map = new HashMap<String, Object>();
    		DBObject val = dbMap.get(monthPeriod.get(i));
    		map.put("gubun", "패킷수");
    		map.put("month", Integer.valueOf(monthPeriod.get(i)));
    		map.put("count", val != null ? ((Number)val.get("pktCnt")).longValue() : 0);
    		DDOS_X_1_2_1.add(map);
		}
		assetReportInfo.put("DDOS_X_1_2_1", new SynthesisDataSource(DDOS_X_1_2_1));
    	assetReportInfo.put("SubChapter5", addContents("가) 패킷기준", "      "));
    	
		for (int i = 0; i < 6; i++) {
    		HashMap<String, Object> map = new HashMap<String, Object>();
    		DBObject val = dbMap.get(monthPeriod.get(i));
    		map.put("gubun", "BandWidth");
    		map.put("month", Integer.valueOf(monthPeriod.get(i)));
    		map.put("count", val != null ? ((Number)val.get("bandwidth")).longValue() : 0);
    		DDOS_X_1_2_2.add(map);
		}	    		
		assetReportInfo.put("DDOS_X_1_2_2", new SynthesisDataSource(DDOS_X_1_2_2));
		assetReportInfo.put("SubChapter6", addContents("나) BandWidth기준", "      "));

		//6개월 공격유형별 추이
		dbResult = reportDao.DDOS_HalfYearChangeOfGroup(assetCode, sEndDay);
		dbMap = new HashMap<String, DBObject>();
		String sTempGroup = "";
		List<String> lstGroup = new ArrayList<String>();
    	for (DBObject val : dbResult) {
    		HashMap<String,Object> mapId = (HashMap<String,Object>)val.get("_id");
    		String sGroup = (String)mapId.get("group");
    		String sYear = ((Integer)mapId.get("year")).toString();
    		String sMonth = ((Integer)mapId.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    		dbMap.put(sGroup + "-" + sYear + sMonth, val);
    		
    		if (!sTempGroup.equals(sGroup)) {
    			lstGroup.add(sGroup);
    			sTempGroup = sGroup;
    		}
    	}

    	for (String sGroup : lstGroup) {
			for (int i = 0; i < 6; i++) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		DBObject val = dbMap.get(sGroup + "-" + monthPeriod.get(i));
	    		map.put("gubun", sGroup);
	    		map.put("month", Integer.valueOf(monthPeriod.get(i)));
	    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
	    		DDOS_X_1_3.add(map);
			}
    	}
    	assetReportInfo.put("DDOS_X_1_3", new SynthesisDataSource(DDOS_X_1_3));
		assetReportInfo.put("SubChapter7", addContents(Depth1 + ".1.3 최근 6개월 유형별 이벤트 발생 추이", "    "));
		
		assetReportInfo.put("SubChapter8", addContents(Depth1 + ".1.4 최근 6개월 유형별 공격 규모 변화", "    "));

		for (String sGroup : lstGroup) {
			for (int i = 0; i < 6; i++) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		DBObject val = dbMap.get(sGroup + "-" + monthPeriod.get(i));
	    		map.put("gubun", sGroup);
	    		map.put("month", Integer.valueOf(monthPeriod.get(i)));
	    		map.put("count", val != null ? ((Number)val.get("pktCnt")).longValue() : 0);
	    		DDOS_X_1_4_1.add(map);
			}
		}
    	assetReportInfo.put("DDOS_X_1_4_1", new SynthesisDataSource(DDOS_X_1_4_1));
		assetReportInfo.put("SubChapter9", addContents("가) 패킷기준", "      "));

		for (String sGroup : lstGroup) {
			for (int i = 0; i < 6; i++) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		DBObject val = dbMap.get(sGroup + "-" + monthPeriod.get(i));
	    		map.put("gubun", sGroup);
	    		map.put("month", Integer.valueOf(monthPeriod.get(i)));
	    		map.put("count", val != null ? ((Number)val.get("bandwidth")).longValue() : 0);
	    		DDOS_X_1_4_2.add(map);
			}
		}
    	assetReportInfo.put("DDOS_X_1_4_2", new SynthesisDataSource(DDOS_X_1_4_2));
		assetReportInfo.put("SubChapter10", addContents("나) BandWidth기준", "      "));
	}
	
	@SuppressWarnings("unchecked")
	private void DDOS_EventDailyChange(String sStartDay, String sEndDay, int assetCode, HashMap<String, Object> assetReportInfo) throws Exception {

    	List<HashMap<String, Object>> DDOS_X_2_1 = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> DDOS_X_2_2_1 = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> DDOS_X_2_2_2 = new ArrayList<HashMap<String, Object>>();
    	
    	List<HashMap<String, Object>> DDOS_X_2_3 = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> DDOS_X_2_4_1 = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> DDOS_X_2_4_2 = new ArrayList<HashMap<String, Object>>();
    	
    	List<String> dayPeriod = reportDao.getPeriodDay(sStartDay, sEndDay, 0, 1);
    	
    	assetReportInfo.put("SubChapter11", addContents(Depth1 + ".2 월간 일자별 공격 추이", "  "));
    	
    	Iterable<DBObject> dbResult = reportDao.DDOS_DailyChange(assetCode, sStartDay, sEndDay);
    	
    	HashMap<String, DBObject> dbMap = new HashMap<String, DBObject>(); 
    	for (DBObject val : dbResult) {
    		HashMap<String,Object> mapId = (HashMap<String,Object>)val.get("_id");
    		String sYear = ((Integer)mapId.get("year")).toString();
    		String sMonth = ((Integer)mapId.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    		String sDay = ((Integer)mapId.get("day")).toString();
    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
    		dbMap.put(sYear + sMonth + sDay, val);
    	}
    	
		for (int i = 0; i < dayPeriod.size(); i++) {
    		HashMap<String, Object> map = new HashMap<String, Object>();
    		DBObject val = dbMap.get(dayPeriod.get(i));
    		map.put("gubun", Integer.valueOf(dayPeriod.get(i).substring(4,6))+"월");
    		map.put("day", Integer.valueOf(dayPeriod.get(i).substring(6,8)));
    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
    		DDOS_X_2_1.add(map);
		}
    	assetReportInfo.put("DDOS_X_2_1", new SynthesisDataSource(DDOS_X_2_1));
    	assetReportInfo.put("SubChapter12", addContents(Depth1 + ".2.1 월간 일자 별 이벤트 발생 추이", "    "));
    	
    	assetReportInfo.put("SubChapter13", addContents(Depth1 + ".2.2 월간 일자 별 공격 규모 추이", "    "));

		for (int i = 0; i < dayPeriod.size(); i++) {
    		HashMap<String, Object> map = new HashMap<String, Object>();
    		DBObject val = dbMap.get(dayPeriod.get(i));
    		map.put("gubun", Integer.valueOf(dayPeriod.get(i).substring(4,6))+"월");
    		map.put("day", Integer.valueOf(dayPeriod.get(i).substring(6,8)));
    		map.put("count", val != null ? ((Number)val.get("pktCnt")).longValue() : 0);
    		DDOS_X_2_2_1.add(map);
		}
    	assetReportInfo.put("DDOS_X_2_2_1", new SynthesisDataSource(DDOS_X_2_2_1));
    	assetReportInfo.put("SubChapter14", addContents("가) 패킷기준", "      "));
    	
		for (int i = 0; i < dayPeriod.size(); i++) {
    		HashMap<String, Object> map = new HashMap<String, Object>();
    		DBObject val = dbMap.get(dayPeriod.get(i));
    		map.put("gubun", Integer.valueOf(dayPeriod.get(i).substring(4,6))+"월");
    		map.put("day", Integer.valueOf(dayPeriod.get(i).substring(6,8)));
    		map.put("count", val != null ? ((Number)val.get("bandwidth")).longValue() : 0);
    		DDOS_X_2_2_2.add(map);
		}
    	assetReportInfo.put("DDOS_X_2_2_2", new SynthesisDataSource(DDOS_X_2_2_2));
    	assetReportInfo.put("SubChapter15", addContents("나) BandWidth기준", "      "));
	
    	//공격 유형별 발생 추이
    	dbResult = reportDao.DDOS_DailyChangeOfGroup(assetCode, sStartDay, sEndDay);
		dbMap = new HashMap<String, DBObject>();
		String sTempGroup = "";
		List<String> lstGroup = new ArrayList<String>();
    	for (DBObject val : dbResult) {
    		HashMap<String,Object> mapId = (HashMap<String,Object>)val.get("_id");
    		String sGroup = (String)mapId.get("group");
    		String sYear = ((Integer)mapId.get("year")).toString();
    		String sMonth = ((Integer)mapId.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    		String sDay = ((Integer)mapId.get("day")).toString();
    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
    		dbMap.put(sGroup + "-" + sYear + sMonth + sDay, val);

    		if (!sTempGroup.equals(sGroup)) {
    			lstGroup.add(sGroup);
    			sTempGroup = sGroup;
    		}
    	}
	
    	for (String sGroup : lstGroup) {
    		for (int i = 0; i < dayPeriod.size(); i++) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		DBObject val = dbMap.get(sGroup + "-" + dayPeriod.get(i));
	    		map.put("gubun", sGroup);
	    		map.put("day", Integer.valueOf(dayPeriod.get(i).substring(6,8)));
	    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
	    		DDOS_X_2_3.add(map);
			}
    	}
    	assetReportInfo.put("DDOS_X_2_3", new SynthesisDataSource(DDOS_X_2_3));
		assetReportInfo.put("SubChapter16", addContents(Depth1 + ".2.3 월간 일자 별 유형별 발생 추이", "    "));
    	
		assetReportInfo.put("SubChapter17", addContents(Depth1 + ".2.4 월간 일자 별 유형별 공격 규모 추이", "    "));
    	
		for (String sGroup : lstGroup) {
			for (int i = 0; i < dayPeriod.size(); i++) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		DBObject val = dbMap.get(sGroup + "-" + dayPeriod.get(i));
	    		map.put("gubun", sGroup);
	    		map.put("day", Integer.valueOf(dayPeriod.get(i).substring(6,8)));
	    		map.put("count", val != null ? ((Number)val.get("pktCnt")).longValue() : 0);
	    		DDOS_X_2_4_1.add(map);
			}
		}
    	assetReportInfo.put("DDOS_X_2_4_1", new SynthesisDataSource(DDOS_X_2_4_1));
    	assetReportInfo.put("SubChapter18", addContents("가) 패킷기준", "      "));
    	
    	for (String sGroup : lstGroup) {
			for (int i = 0; i < dayPeriod.size(); i++) {
	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		DBObject val = dbMap.get(sGroup + "-" + dayPeriod.get(i));
	    		map.put("gubun", sGroup);
	    		map.put("day", Integer.valueOf(dayPeriod.get(i).substring(6,8)));
	    		map.put("count", val != null ? ((Number)val.get("bandwidth")).longValue() : 0);
	    		DDOS_X_2_4_2.add(map);
			}
    	}
    	assetReportInfo.put("DDOS_X_2_4_2", new SynthesisDataSource(DDOS_X_2_4_2));
    	assetReportInfo.put("SubChapter19", addContents("나) BandWidth기준", "      "));
	}
	
	@SuppressWarnings("unchecked")
	private void DDOS_EventMonthlyDetail(String sStartDay, String sEndDay, int assetCode, HashMap<String, Object> assetReportInfo) throws Exception {
		
    	List<HashMap<String, Object>> DDOS_X_3_1 = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> DDOS_X_3_2 = new ArrayList<HashMap<String, Object>>();
    	
    	List<HashMap<String, Object>> DDOS_X_3_4_1 = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> DDOS_X_3_4_2_G = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> DDOS_X_3_4_2_T = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> DDOS_X_3_4_3_G = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> DDOS_X_3_4_3_T = new ArrayList<HashMap<String, Object>>();
		
    	assetReportInfo.put("SubChapter20", addContents(Depth1 + ".3 월간 이벤트 분석", "  "));
    	
    	Iterable<DBObject> dbTop10 = reportDao.DDOS_Top10ByGroup(assetCode, sEndDay);
    	
    	int nNo = 1;
    	for (DBObject gVal : dbTop10) {
    		HashMap<String,Object> gId = (HashMap<String,Object>)gVal.get("_id");
    		String sGroup = (String)gId.get("group");
    		
    		Iterable<DBObject> dbResult = reportDao.DDOS_EventByGroup(assetCode, sEndDay, sGroup);
    		
			for (DBObject mVal : dbResult) {
    			HashMap<String,Object> mId = (HashMap<String,Object>)mVal.get("_id");
    			String sMessage = (String)mId.get("message");
    			
        		HashMap<String, Object> map = new HashMap<String, Object>();
        		map.put("no", nNo);
        		map.put("group", sGroup);
        		map.put("total", ((Number)gVal.get("count")).longValue());
        		map.put("message", sMessage);
        		map.put("count", ((Number)mVal.get("count")).longValue());
        		map.put("pktCnt", ((Number)mVal.get("pktCnt")).longValue());
        		map.put("bandWidth", ((Number)mVal.get("bandwidth")).longValue());
        		DDOS_X_3_1.add(map);
			}
			nNo++;
    	}
    	assetReportInfo.put("DDOS_X_3_1", new SynthesisDataSource(DDOS_X_3_1));
    	assetReportInfo.put("SubChapter21", addContents(Depth1 + ".3.1 월간 공격 유형별 이벤트 TOP10 현황", "    "));
    	
    	List<String> monthPeriod = reportDao.getPeriodMonth(sEndDay, -1, 1);
    	
    	HashMap<String, Object> prevMap = new HashMap<String, Object>();
    	HashMap<String, Object> currMap = new HashMap<String, Object>();
    	
    	dbTop10 = reportDao.DDOS_Top10ByEvent(assetCode, sEndDay, -1, 1, 0);
    	
    	int nTop = 1;
    	for (DBObject mVal : dbTop10) {
    		HashMap<String,Object> mapId = (HashMap<String,Object>)mVal.get("_id");
    		String sYear = ((Integer)mapId.get("year")).toString();
    		String sMonth = ((Integer)mapId.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    		
			HashMap<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("year", sYear);
			tempMap.put("month", sMonth);
			tempMap.put("message", (String)mapId.get("message"));
			tempMap.put("action", (String)mapId.get("action"));
			tempMap.put("count", ((Number)mVal.get("count")).longValue());
    		
			if (monthPeriod.get(0).equals(sYear + sMonth)) {
				prevMap.put((String)mapId.get("message"), tempMap);
			} else if (monthPeriod.get(1).equals(sYear + sMonth)) {
				if (nTop <= 10) {
    				HashMap<String, Object> map = new HashMap<String, Object>();
    				HashMap<String, Object> pMap = (HashMap<String, Object>)prevMap.remove(mapId.get("message"));
					
    				long lCurrCount = ((Number)mVal.get("count")).longValue();
    				long lPrevCount = pMap != null ? ((Number)pMap.get("count")).longValue() : 0;

    				map.put("no", nTop);		
    				map.put("message", (String)mapId.get("message"));
    				map.put("gubun", (String)mapId.get("action"));
    				map.put("count", lCurrCount);
    				map.put("prevCount", lPrevCount);
    				map.put("countVariation", lCurrCount - lPrevCount);
    				map.put("ratio", ((lCurrCount - lPrevCount) * 100f) / lPrevCount);
    				
    				DDOS_X_3_2.add(map);
    				
    				nTop++;
				} else {
					currMap.put((String)mapId.get("message"), tempMap);					
				}
			}			
    	}
    	
    	/*
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
    	
    	HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("no", nTop);		
		map.put("message", "기타");
		map.put("gubun", "기타");
		map.put("count", lCurrEtcCount);
		map.put("prevCount", lPrevEtcCount);
		map.put("countVariation", lCurrEtcCount - lPrevEtcCount);
		map.put("ratio", ((lCurrEtcCount - lPrevEtcCount) * 100f) / lPrevEtcCount);
		DDOS_X_3_2.add(map);
    	*/
    	
		assetReportInfo.put("DDOS_X_3_2", new SynthesisDataSource(DDOS_X_3_2));
		assetReportInfo.put("SubChapter22", addContents(Depth1 + ".3.2 월간 이벤트 TOP10 현황", "    "));

		assetReportInfo.put("DDOS_X_3_3", new SynthesisDataSource(DDOS_X_3_2));
		assetReportInfo.put("SubChapter23", addContents(Depth1 + ".3.3 월간 이벤트 TOP10 증감 현황 (전월비교)", "    "));
		
    	
		
		
		assetReportInfo.put("SubChapter24", addContents(Depth1 + ".3.4 월간 이벤트 TOP10 상세 내역", "    "));
		
		dbTop10 = reportDao.DDOS_Top10ByEventDetail(assetCode, sEndDay, 1);
		
		nTop = 1;
		for (DBObject mVal : dbTop10) {
    		HashMap<String,Object> mId = (HashMap<String,Object>)mVal.get("_id");
    		String sMessage = (String)mId.get("message");
    		
    		Iterable<DBObject> dbTop5 = reportDao.DDOS_EventDetail_1st(assetCode, sEndDay, sMessage, 1);
		
    		for (DBObject sVal : dbTop5) {
        		HashMap<String,Object> sId = (HashMap<String,Object>)sVal.get("_id");
        		String sSrcIp = (String)sId.get("srcIp");
    			
        		Iterable<DBObject> dbResult = reportDao.DDOS_EventDetail_2nd(assetCode, sEndDay, sMessage, sSrcIp, 1);
        		
        		for (DBObject dVal : dbResult) {
            		HashMap<String,Object> dId = (HashMap<String,Object>)dVal.get("_id");

                	HashMap<String, Object> dMap = new HashMap<String, Object>();
                	dMap.put("no", nTop);		
                	dMap.put("message", sMessage);
                	dMap.put("group", (String)mId.get("group"));
                	dMap.put("srcIp", sSrcIp);
                	dMap.put("destIp", (String)dId.get("destIp"));
                	dMap.put("count", ((Number)dVal.get("count")).longValue());
                	dMap.put("pktCnt", ((Number)dVal.get("pktCnt")).longValue());
                	dMap.put("bandWidth", ((Number)dVal.get("bandwidth")).longValue());
                	DDOS_X_3_4_1.add(dMap);
        		}
    		}
    		nTop++;
		}
		assetReportInfo.put("DDOS_X_3_4_1", new SynthesisDataSource(DDOS_X_3_4_1));
		assetReportInfo.put("SubChapter25", addContents("가) 이벤트 발생 건수 별 통계 현황", "      "));
		
		//PktCount별 통계 현황
		dbTop10 = reportDao.DDOS_Top10ByEvent(assetCode, sEndDay, 0, 2, 10);
		
    	for (DBObject mVal : dbTop10) {
    		HashMap<String,Object> mapId = (HashMap<String,Object>)mVal.get("_id");
    		
    		HashMap<String, Object> mMap = new HashMap<String, Object>();
    		mMap.put("message", (String)mapId.get("message"));
    		mMap.put("gubun", (String)mapId.get("action"));
    		mMap.put("count", ((Number)mVal.get("count")).longValue());
			DDOS_X_3_4_2_G.add(mMap);
    	}
    	
		dbTop10 = reportDao.DDOS_Top10ByEventDetail(assetCode, sEndDay, 2);
		
		nTop = 1;
		for (DBObject mVal : dbTop10) {
    		HashMap<String,Object> mId = (HashMap<String,Object>)mVal.get("_id");
    		String sMessage = (String)mId.get("message");
    		
    		Iterable<DBObject> dbTop5 = reportDao.DDOS_EventDetail_1st(assetCode, sEndDay, sMessage, 2);
		
    		for (DBObject sVal : dbTop5) {
        		HashMap<String,Object> sId = (HashMap<String,Object>)sVal.get("_id");
        		String sSrcIp = (String)sId.get("srcIp");
    			
        		Iterable<DBObject> dbResult = reportDao.DDOS_EventDetail_2nd(assetCode, sEndDay, sMessage, sSrcIp, 2);
        		
        		for (DBObject dVal : dbResult) {
            		HashMap<String,Object> dId = (HashMap<String,Object>)dVal.get("_id");

                	HashMap<String, Object> dMap = new HashMap<String, Object>();
                	dMap.put("no", nTop);		
                	dMap.put("message", sMessage);
                	dMap.put("group", (String)mId.get("group"));
                	dMap.put("srcIp", sSrcIp);
                	dMap.put("destIp", (String)dId.get("destIp"));
                	dMap.put("count", ((Number)dVal.get("count")).longValue());
                	dMap.put("pktCnt", ((Number)dVal.get("pktCnt")).longValue());
                	dMap.put("bandWidth", ((Number)dVal.get("bandwidth")).longValue());
                	DDOS_X_3_4_2_T.add(dMap);
        		}
    		}
    		nTop++;
		}
		assetReportInfo.put("DDOS_X_3_4_2_T", new SynthesisDataSource(DDOS_X_3_4_2_T));
		assetReportInfo.put("DDOS_X_3_4_2_G", new SynthesisDataSource(DDOS_X_3_4_2_G));
		assetReportInfo.put("SubChapter26", addContents("나) Drop Packet Count 별 통계 현황", "      "));
			
		//BandWidth별 통계 현황
		dbTop10 = reportDao.DDOS_Top10ByEvent(assetCode, sEndDay, 0, 3, 10);
		
    	for (DBObject mVal : dbTop10) {
    		HashMap<String,Object> mapId = (HashMap<String,Object>)mVal.get("_id");
    		
    		HashMap<String, Object> mMap = new HashMap<String, Object>();
    		mMap.put("message", (String)mapId.get("message"));
    		mMap.put("gubun", (String)mapId.get("action"));
    		mMap.put("count", ((Number)mVal.get("count")).longValue());
			DDOS_X_3_4_3_G.add(mMap);
    	}
    	
		dbTop10 = reportDao.DDOS_Top10ByEventDetail(assetCode, sEndDay, 3);
		
		nTop = 1;
		for (DBObject mVal : dbTop10) {
    		HashMap<String,Object> mId = (HashMap<String,Object>)mVal.get("_id");
    		String sMessage = (String)mId.get("message");
    		
    		Iterable<DBObject> dbTop5 = reportDao.DDOS_EventDetail_1st(assetCode, sEndDay, sMessage, 3);
		
    		for (DBObject sVal : dbTop5) {
        		HashMap<String,Object> sId = (HashMap<String,Object>)sVal.get("_id");
        		String sSrcIp = (String)sId.get("srcIp");
    			
        		Iterable<DBObject> dbResult = reportDao.DDOS_EventDetail_2nd(assetCode, sEndDay, sMessage, sSrcIp, 3);
        		
        		for (DBObject dVal : dbResult) {
            		HashMap<String,Object> dId = (HashMap<String,Object>)dVal.get("_id");

                	HashMap<String, Object> dMap = new HashMap<String, Object>();
                	dMap.put("no", nTop);		
                	dMap.put("message", sMessage);
                	dMap.put("group", (String)mId.get("group"));
                	dMap.put("srcIp", sSrcIp);
                	dMap.put("destIp", (String)dId.get("destIp"));
                	dMap.put("count", ((Number)dVal.get("count")).longValue());
                	dMap.put("pktCnt", ((Number)dVal.get("pktCnt")).longValue());
                	dMap.put("bandWidth", ((Number)dVal.get("bandwidth")).longValue());
                	DDOS_X_3_4_3_T.add(dMap);
        		}
    		}
    		nTop++;
		}
		assetReportInfo.put("DDOS_X_3_4_3_T", new SynthesisDataSource(DDOS_X_3_4_3_T));
		assetReportInfo.put("DDOS_X_3_4_3_G", new SynthesisDataSource(DDOS_X_3_4_3_G));
		assetReportInfo.put("SubChapter27", addContents("다) Band Width 별 통계 현황", "      "));
		
		
    	List<HashMap<String, Object>> DDOS_X_3_5_G = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> DDOS_X_3_5_T = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> DDOS_X_3_6_G = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> DDOS_X_3_6_T = new ArrayList<HashMap<String, Object>>();
		
		dbTop10 = reportDao.DDOS_Top10ByIp(assetCode, sEndDay, 1);
    	List<String> saTop10Ips = new ArrayList<String>();
    	for (DBObject val : dbTop10) {
    		HashMap<String,Object> mapId = (HashMap<String,Object>)val.get("_id");
    		saTop10Ips.add((String)mapId.get("srcIp"));
    	}
    	
    	prevMap = new HashMap<String, Object>();
    	Iterable<DBObject> dbPrevInfo = reportDao.DDOS_PrevInfo(assetCode, sEndDay, 1, saTop10Ips);
    	for (DBObject mVal : dbPrevInfo) {
    		HashMap<String,Object> mId = (HashMap<String,Object>)mVal.get("_id");
    		HashMap<String, Object> tempMap = new HashMap<String, Object>();
    		tempMap.put("srcIp", (String)mId.get("srcIp"));
    		tempMap.put("count", ((Number)mVal.get("count")).longValue());
    		prevMap.put((String)mId.get("srcIp"), tempMap);
    	}
    	
    	for (DBObject mVal : dbTop10) {
    		HashMap<String,Object> mId = (HashMap<String,Object>)mVal.get("_id");
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> pMap = (HashMap<String, Object>)prevMap.remove(mId.get("srcIp"));
			
			long lCurrCount = ((Number)mVal.get("count")).longValue();
			long lPrevCount = pMap != null ? ((Number)pMap.get("count")).longValue() : 0;

			map.put("gubun", (String)mId.get("srcIp"));
			map.put("count", lCurrCount);
			map.put("prevCount", lPrevCount);
			map.put("countVariation", lCurrCount - lPrevCount);
			map.put("ratio", ((lCurrCount - lPrevCount) * 100f) / lPrevCount);
			DDOS_X_3_5_G.add(map);
    	}
		
		nTop = 1;
		for (DBObject mVal : dbTop10) {
    		HashMap<String,Object> mId = (HashMap<String,Object>)mVal.get("_id");
    		String sSrcIp = (String)mId.get("srcIp");
    		
    		Iterable<DBObject> dbTop5 = reportDao.DDOS_IpDetail_1st(assetCode, sEndDay, sSrcIp, 1);
		
    		for (DBObject sVal : dbTop5) {
        		HashMap<String,Object> sId = (HashMap<String,Object>)sVal.get("_id");
        		String sMessage = (String)sId.get("message");
    			
        		Iterable<DBObject> dbResult = reportDao.DDOS_IpDetail_2nd(assetCode, sEndDay, sSrcIp, sMessage, 1);
        		
        		for (DBObject dVal : dbResult) {
            		HashMap<String,Object> dId = (HashMap<String,Object>)dVal.get("_id");

                	HashMap<String, Object> dMap = new HashMap<String, Object>();
                	dMap.put("no", nTop);
                	dMap.put("srcIp", sSrcIp);
                	dMap.put("message", sMessage);
                	dMap.put("total", ((Number)sVal.get("count")).longValue());
                	dMap.put("destIp", (String)dId.get("destIp"));
                	dMap.put("count", ((Number)dVal.get("count")).longValue());
                	dMap.put("pktCnt", ((Number)dVal.get("pktCnt")).longValue());
                	dMap.put("bandWidth", ((Number)dVal.get("bandwidth")).longValue());
                	DDOS_X_3_5_T.add(dMap);
        		}
    		}
    		nTop++;
		}
    	assetReportInfo.put("DDOS_X_3_5_G", new SynthesisDataSource(DDOS_X_3_5_G));
    	assetReportInfo.put("DDOS_X_3_5_T", new SynthesisDataSource(DDOS_X_3_5_T));
		assetReportInfo.put("SubChapter28", addContents(Depth1 + ".3.5 월간 SRC IP TOP10 현황", "    "));
		
		dbTop10 = reportDao.DDOS_Top10ByIp(assetCode, sEndDay, 2);
    	saTop10Ips = new ArrayList<String>();
    	for (DBObject val : dbTop10) {
    		HashMap<String,Object> mapId = (HashMap<String,Object>)val.get("_id");
    		saTop10Ips.add((String)mapId.get("destIp"));
    	}
    	
    	prevMap = new HashMap<String, Object>();
    	dbPrevInfo = reportDao.DDOS_PrevInfo(assetCode, sEndDay, 2, saTop10Ips);
    	for (DBObject mVal : dbPrevInfo) {
    		HashMap<String,Object> mId = (HashMap<String,Object>)mVal.get("_id");
    		HashMap<String, Object> tempMap = new HashMap<String, Object>();
    		tempMap.put("destIp", (String)mId.get("destIp"));
    		tempMap.put("count", ((Number)mVal.get("count")).longValue());
    		prevMap.put((String)mId.get("destIp"), tempMap);
    	}
    	
    	for (DBObject mVal : dbTop10) {
    		HashMap<String,Object> mId = (HashMap<String,Object>)mVal.get("_id");
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> pMap = (HashMap<String, Object>)prevMap.remove(mId.get("destIp"));
			
			long lCurrCount = ((Number)mVal.get("count")).longValue();
			long lPrevCount = pMap != null ? ((Number)pMap.get("count")).longValue() : 0;

			map.put("gubun", (String)mId.get("destIp"));
			map.put("count", lCurrCount);
			map.put("prevCount", lPrevCount);
			map.put("countVariation", lCurrCount - lPrevCount);
			map.put("ratio", ((lCurrCount - lPrevCount) * 100f) / lPrevCount);
			DDOS_X_3_6_G.add(map);
    	}
		
		nTop = 1;
		for (DBObject mVal : dbTop10) {
    		HashMap<String,Object> mId = (HashMap<String,Object>)mVal.get("_id");
    		String sDestIp = (String)mId.get("destIp");
    		
    		Iterable<DBObject> dbTop5 = reportDao.DDOS_IpDetail_1st(assetCode, sEndDay, sDestIp, 2);
		
    		for (DBObject sVal : dbTop5) {
        		HashMap<String,Object> sId = (HashMap<String,Object>)sVal.get("_id");
        		String sMessage = (String)sId.get("message");
    			
        		Iterable<DBObject> dbResult = reportDao.DDOS_IpDetail_2nd(assetCode, sEndDay, sDestIp, sMessage, 2);
        		
        		for (DBObject dVal : dbResult) {
            		HashMap<String,Object> dId = (HashMap<String,Object>)dVal.get("_id");

                	HashMap<String, Object> dMap = new HashMap<String, Object>();
                	dMap.put("no", nTop);
                	dMap.put("destIp", sDestIp);
                	dMap.put("message", sMessage);
                	dMap.put("total", ((Number)sVal.get("count")).longValue());
                	dMap.put("srcIp", (String)dId.get("srcIp"));
                	dMap.put("count", ((Number)dVal.get("count")).longValue());
                	dMap.put("pktCnt", ((Number)dVal.get("pktCnt")).longValue());
                	dMap.put("bandWidth", ((Number)dVal.get("bandwidth")).longValue());
                	DDOS_X_3_6_T.add(dMap);
        		}
    		}
    		nTop++;
		}
    	assetReportInfo.put("DDOS_X_3_6_G", new SynthesisDataSource(DDOS_X_3_6_G));
    	assetReportInfo.put("DDOS_X_3_6_T", new SynthesisDataSource(DDOS_X_3_6_T));
		assetReportInfo.put("SubChapter29", addContents(Depth1 + ".3.6 월간 DST IP TOP10 현황", "    "));
	}	
	
	private void DDOS_MonthlyChange(String sStartDay, String sEndDay, int assetCode, HashMap<String, Object> assetReportInfo) throws Exception {

    	List<HashMap<String, Object>> DDOS_X_1_G = new ArrayList<HashMap<String, Object>>();
    	List<DBObject> outToIn = reportDao.DDOS_SessionLogMonth(assetCode, sEndDay);   	

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
	    		
	    		DDOS_X_1_G.add(map);
    		}	    		
    	}
    	assetReportInfo.put("DDOS_X_1_G", new SynthesisDataSource(DDOS_X_1_G));
    	assetReportInfo.put("DDOS_X_1_T", new SynthesisDataSource(DDOS_X_1_G));
    	
    	assetReportInfo.put("SubChapter2", addContents(Depth1 + ".1 최근 6개월 추이", "  "));
	}
	
	private void DDOS_DailyChange(String sStartDay, String sEndDay, int assetCode, HashMap<String, Object> assetReportInfo) throws Exception {

    	List<HashMap<String, Object>> DDOS_X_2_1_G = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> DDOS_X_2_2_G = new ArrayList<HashMap<String, Object>>();
    	
    	assetReportInfo.put("SubChapter3", addContents(Depth1 + ".2 금월 일별 이벤트 발생 추이", "  "));
    	
    	List<DBObject>  dbResult = reportDao.DDOS_SessionLogDay(assetCode, sStartDay, sEndDay);   	
    	
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
        			DDOS_X_2_2_G.add(map);
        		} else {
        			DDOS_X_2_1_G.add(map);
        		}
			}
    	}
    	assetReportInfo.put("DDOS_X_2_1_G", new SynthesisDataSource(DDOS_X_2_1_G));
    	assetReportInfo.put("SubChapter4", addContents("가) 금월 전체 허용 이벤트 비교", "    "));
    	
    	assetReportInfo.put("DDOS_X_2_2_G", new SynthesisDataSource(DDOS_X_2_2_G));
    	assetReportInfo.put("SubChapter5", addContents("나) 금월 전체 차단 이벤트 비교", "    "));
	}
	
	private void DDOS_Top10DailyEventChange(String sStartDay, String sEndDay, int assetCode, HashMap<String, Object> assetReportInfo, int nAction) throws Exception {

		if (nAction == 1) {
			//허용 이벤트 TOP10
			assetReportInfo.put("SubChapter6", addContents(Depth1 + ".3 월간 허용 이벤트 상세 분석", "  "));
			
	    	List<HashMap<String, Object>> DDOS_X_3_1_G = new ArrayList<HashMap<String, Object>>();
	    	List<DBObject> dbTop10 = reportDao.DDOS_TopInfoMonth(assetCode, sEndDay, ALLOW, 1, 10);
	    	
	    	List<String> saTop10Event = new ArrayList<String>();
	    	for (DBObject val : dbTop10) {
	    		saTop10Event.add((String)val.get("message"));
	    	}
	    	
	    	List<DBObject> dbResult = reportDao.DDOS_Top10EventChange(assetCode, sStartDay, sEndDay, ALLOW, saTop10Event);
	    	
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
		    		DDOS_X_3_1_G.add(map);
		    	}
	    	}
	    	assetReportInfo.put("DDOS_X_3_1_G", new SynthesisDataSource(DDOS_X_3_1_G));
	    	assetReportInfo.put("SubChapter7", addContents(Depth1 + ".3.1 월간 허용 이벤트 TOP10 일별 발생 추이", "    "));
		} else {    	
	    	//차단 이벤트 TOP10
	    	assetReportInfo.put("SubChapter15", addContents(Depth1 + ".4 월간 차단 이벤트 상세 분석", "  "));
	    	
	    	List<HashMap<String, Object>> DDOS_X_4_1_G = new ArrayList<HashMap<String, Object>>();
	    	List<DBObject> dbTop10 = reportDao.DDOS_TopInfoMonth(assetCode, sEndDay, CUTOFF, 1, 10);
	    	
	    	List<String> saTop10Event = new ArrayList<String>();
	    	for (DBObject val : dbTop10) {
	    		saTop10Event.add((String)val.get("message"));
	    	}
	    	
	    	List<DBObject> dbResult = reportDao.DDOS_Top10EventChange(assetCode, sStartDay, sEndDay, CUTOFF, saTop10Event);
	    	
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
		    		DDOS_X_4_1_G.add(map);
		    	}
	    	}
	    	assetReportInfo.put("DDOS_X_4_1_G", new SynthesisDataSource(DDOS_X_4_1_G));
	    	assetReportInfo.put("SubChapter16", addContents(Depth1 + ".4.1 월간 차단 이벤트 TOP10 일별 발생 추이", "    "));
		}
	}
	
	@SuppressWarnings("unchecked")
	private void DDOS_Top10MonthlyChart(String sStartDay, String sEndDay, int assetCode, HashMap<String, Object> assetReportInfo, int nAction) throws Exception {

		List<String> monthPeriod = reportDao.getPeriodMonth(sEndDay, -1, 1);
		
		//허용/차단 이벤트 TOP10
    	List<HashMap<String, Object>> DDOS_MAP_G = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> DDOS_MAP_T = new ArrayList<HashMap<String, Object>>();
    	
    	List<DBObject> dbTop10 = reportDao.DDOS_TopInfoMonth(assetCode, sEndDay, String.valueOf(nAction), 1, 10);
    	
    	for (DBObject val : dbTop10) {
    		HashMap<String, Object> map = new HashMap<String, Object>();
    		map.put("gubun", (String)val.get("message"));
    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
   			DDOS_MAP_G.add(map);
    	}
    	
    	if (nAction == 0) {
    		assetReportInfo.put("DDOS_X_4_2_G", new SynthesisDataSource(DDOS_MAP_G));
    		assetReportInfo.put("SubChapter17", addContents(Depth1 + ".4.2 월간 차단 이벤트 TOP10", "    "));
    	} else {
    		assetReportInfo.put("DDOS_X_3_2_G", new SynthesisDataSource(DDOS_MAP_G));
    		assetReportInfo.put("SubChapter8", addContents(Depth1 + ".3.2 월간 허용 이벤트 TOP10", "    "));
    	}

    	List<DBObject> dbResult = reportDao.DDOS_DataVariation(assetCode, sEndDay, String.valueOf(nAction), 1);
    	
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
			
				if (!((String)val.get("message")).equals("-1") && nTop <= 10) {
    				HashMap<String, Object> map1 = new HashMap<String, Object>();
    				HashMap<String, Object> pMap = (HashMap<String, Object>)prevMap.remove(val.get("message"));
					
    				long lCurrCount = ((Number)val.get("count")).longValue();
    				long lPrevCount = pMap != null ? ((Number)pMap.get("count")).longValue() : 0;

    				map1.put("no", nTop);		
    				map1.put("message", (String)val.get("message"));
    				map1.put("count", lCurrCount);
    				map1.put("prevCount", lPrevCount);
    				map1.put("countVariation", lCurrCount - lPrevCount);
    				map1.put("ratio", ((lCurrCount - lPrevCount) * 100f) / lPrevCount);
    				
    				nTop++;
    				
   					DDOS_MAP_T.add(map1);
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
			map1.put("ratio", ((lCurrEtcCount - lPrevEtcCount) * 100f) / lPrevEtcCount);
			DDOS_MAP_T.add(map1);
    	}
    	
    	if (nAction == 0) {
			assetReportInfo.put("DDOS_X_4_2_T", new SynthesisDataSource(DDOS_MAP_T));
			assetReportInfo.put("SubChapter18", addContents(Depth1 + ".4.3 월간 허용 이벤트 TOP10 증감 현황 (전월비교)", "    "));
    	} else {
			assetReportInfo.put("DDOS_X_3_2_T", new SynthesisDataSource(DDOS_MAP_T));
			assetReportInfo.put("SubChapter9", addContents(Depth1 + ".3.3 월간 허용 이벤트 TOP10 증감 현황 (전월비교)", "    "));
    	}
	}
	
	@SuppressWarnings("unchecked")
	private void DDOS_ServiceMonthPie(String sStartDay, String sEndDay, int assetCode, HashMap<String, Object> assetReportInfo, int nAction) throws Exception {
	
		List<String> monthPeriod = reportDao.getPeriodMonth(sEndDay, -1, 1);
		
    	List<HashMap<String, Object>> DDOS_MAP_G = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> DDOS_MAP_T = new ArrayList<HashMap<String, Object>>();
		
    	List<DBObject> dbTop10 = reportDao.DDOS_TopInfoMonth(assetCode, sEndDay, String.valueOf(nAction), 2, 0);
		
		HashMap<String, Object> etcMap = new HashMap<String, Object>();

		int nTop = 1;
		long lTotalCount = 0;
    	for (DBObject val : dbTop10) {
    		HashMap<String, Object> map = new HashMap<String, Object>();
    		map.put("port", (Integer)val.get("destPort"));
    		map.put("svcName", reportDao.getPortServiceName((Integer)val.get("destPort")));
    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);

    		lTotalCount += ((Number)val.get("count")).longValue();
    		
    		if ((Integer)val.get("destPort") != -1 && nTop <= 10) {
    			DDOS_MAP_G.add(map);
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
    		DDOS_MAP_G.add(tmpMap);
    	}

		for (HashMap<String, Object> map : DDOS_MAP_G) {
			long count = ((Number)map.get("count")).longValue();
			map.put("ratio", (count * 100f) / lTotalCount);
		}
    	
    	if (nAction == 0) {
    		assetReportInfo.put("DDOS_X_4_3_G", new SynthesisDataSource(DDOS_MAP_G));
    		assetReportInfo.put("SubChapter19", addContents(Depth1 + ".4.4 월간 차단 이벤트에 대한 서비스 분포", "    "));
    	} else {
    		assetReportInfo.put("DDOS_X_3_3_G", new SynthesisDataSource(DDOS_MAP_G));
    		assetReportInfo.put("SubChapter10", addContents(Depth1 + ".3.4 월간 허용 이벤트에 대한 서비스 분포", "    "));
    	}
    	
    	List<DBObject> dbResult = reportDao.DDOS_DataVariation(assetCode, sEndDay, String.valueOf(nAction), 2);
    	
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
    				map1.put("ratio", ((lCurrCount - lPrevCount) * 100f) / lPrevCount);
    				
    				nTop++;
    				
    				DDOS_MAP_T.add(map1);
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
			map1.put("ratio", ((lCurrEtcCount - lPrevEtcCount) * 100f) / lPrevEtcCount);
			DDOS_MAP_T.add(map1);
    	}
    	
    	if (nAction == 0) {
    		assetReportInfo.put("DDOS_X_4_3_T", new SynthesisDataSource(DDOS_MAP_T));
    		assetReportInfo.put("SubChapter20", addContents(Depth1 + ".4.5 월간 차단 이벤트 별 서비스 증감 현황 (전월비교)", "    "));
    	} else {
    		assetReportInfo.put("DDOS_X_3_3_T", new SynthesisDataSource(DDOS_MAP_T));
    		assetReportInfo.put("SubChapter11", addContents(Depth1 + ".3.5 월간 허용 이벤트 별 서비스 증감 현황 (전월비교)", "    "));
    	}
	}
	
	private void DDOS_Top10Detail(String sStartDay, String sEndDay, int assetCode, HashMap<String, Object> assetReportInfo, int nAction) throws Exception {
	
		List<HashMap<String, Object>> DDOS_MAP_T1 = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> DDOS_MAP_T2 = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> DDOS_MAP_T3 = new ArrayList<HashMap<String, Object>>();
		
		//이벤트 Top10에 대한 출발지 | 목적지 Top5
		List<DBObject> dbEventTop10 = reportDao.DDOS_TopInfoMonth(assetCode, sEndDay, String.valueOf(nAction), 1, 10);
		
    	int nNo = 1;
    	List<String> lstMessage = new ArrayList<String>();
    	for (DBObject val : dbEventTop10) {
    		String sMessage = (String)val.get("message");

    		lstMessage.add(sMessage);
    		
    		List<DBObject> ipTop5_1st = reportDao.DDOS_Top10Condition_1st(assetCode, sEndDay, String.valueOf(nAction), 1, sMessage);
    		
    		for (DBObject mObj : ipTop5_1st) {
    			List<DBObject> ipTop5_2nd = reportDao.DDOS_Top10Condition_2nd(assetCode, sEndDay, String.valueOf(nAction), 1, sMessage, (String)mObj.get("srcIp"));
    			for (DBObject sObj : ipTop5_2nd) {
	    			HashMap<String, Object> map = new HashMap<String, Object>();
	    			map.put("no", nNo);
	    			map.put("message", sMessage);
	    			map.put("total", ((Number)val.get("count")).longValue());
	    			map.put("srcIp", (String)sObj.get("srcIp"));
	    			map.put("destIp", (String)sObj.get("destIp"));
	    			map.put("count", ((Number)sObj.get("count")).longValue());

	    			DDOS_MAP_T1.add(map);
    			}
    		}
    		nNo++;
    	}
    	
    	HashMap<String, Object> prevMap = new HashMap<String, Object>();
    	List<DBObject> dbPrevMessages = reportDao.DDOS_PrevMonthInfo(assetCode, sEndDay, String.valueOf(nAction), 3, lstMessage);
    	for (DBObject mObj : dbPrevMessages) {
    		prevMap.put((String)mObj.get("message"), ((Number)mObj.get("count")).longValue());
    	}

		for (HashMap<String, Object> map : DDOS_MAP_T1) {
			String sMessage = (String)map.get("message");
			Object obj = prevMap.get(sMessage);
			long lPrevCount = obj != null ? ((Number)obj).longValue() : 0;
			map.put("countVariation", ((Number)map.get("total")).longValue() - lPrevCount);
		}
    	
    	if (nAction == 0) {
    		assetReportInfo.put("DDOS_X_4_4_T", new SynthesisDataSource(DDOS_MAP_T1));
    		assetReportInfo.put("SubChapter21", addContents(Depth1 + ".4.6 월간 차단 이벤트 TOP10 현황", "    "));
    	} else {
    		assetReportInfo.put("DDOS_X_3_4_T", new SynthesisDataSource(DDOS_MAP_T1));
    		assetReportInfo.put("SubChapter12", addContents(Depth1 + ".3.6 월간 허용 이벤트 TOP10 현황", "    "));
    	}
		
		//출발지 Top10에 대한 이벤트 | 목적지 Top5
    	List<DBObject> dbSrcIpTop10 = reportDao.DDOS_TopInfoMonth(assetCode, sEndDay, String.valueOf(nAction), 3, 10);

    	nNo = 1;
    	List<String> lstSrcIp = new ArrayList<String>();
    	for (DBObject val : dbSrcIpTop10) {
    		String strSrcIp = (String)val.get("srcIp");
    		
    		lstSrcIp.add(strSrcIp);
    		
    		List<DBObject> ipTop5_1st = reportDao.DDOS_Top10Condition_1st(assetCode, sEndDay, String.valueOf(nAction), 2, strSrcIp);
    	
    		for (DBObject mObj : ipTop5_1st) {
    			List<DBObject> ipTop5_2nd = reportDao.DDOS_Top10Condition_2nd(assetCode, sEndDay, String.valueOf(nAction), 2, strSrcIp, (String)mObj.get("message"));
    			for (DBObject sObj : ipTop5_2nd) {
	    			HashMap<String, Object> map = new HashMap<String, Object>();
	    			map.put("no", nNo);
	    			map.put("srcIp", strSrcIp);
	    			map.put("total", ((Number)val.get("count")).longValue());
	    			map.put("message", (String)mObj.get("message"));
	    			map.put("destIp", (String)sObj.get("destIp"));
	    			map.put("count", ((Number)sObj.get("count")).longValue());

	    			DDOS_MAP_T2.add(map);
    			}
    		}
    		nNo++;
    	}
    	
    	prevMap = new HashMap<String, Object>();
    	List<DBObject> dbPrevSrcIps = reportDao.DDOS_PrevMonthInfo(assetCode, sEndDay, String.valueOf(nAction), 1, lstSrcIp);
    	for (DBObject mObj : dbPrevSrcIps) {
    		prevMap.put((String)mObj.get("srcIp"), ((Number)mObj.get("count")).longValue());
    	}

		for (HashMap<String, Object> map : DDOS_MAP_T2) {
			String sSrcIp = (String)map.get("srcIp");
			Object obj = prevMap.get(sSrcIp);
			long lPrevCount = obj != null ? ((Number)obj).longValue() : 0;
			map.put("countVariation", ((Number)map.get("total")).longValue() - lPrevCount);
		}
    	
    	if (nAction == 0) {
    		assetReportInfo.put("DDOS_X_4_5_T", new SynthesisDataSource(DDOS_MAP_T2));
    		assetReportInfo.put("SubChapter22", addContents(Depth1 + ".4.7 월간 출발지IP TOP10 현황", "    "));
    	} else {
    		assetReportInfo.put("DDOS_X_3_5_T", new SynthesisDataSource(DDOS_MAP_T2));
    		assetReportInfo.put("SubChapter13", addContents(Depth1 + ".3.7 월간 출발지IP TOP10 현황", "    "));
    	}
		
		//목적지 Top10에 대한 이벤트 | 출발지목적지 Top5
    	List<DBObject> dbDestIpTop10 = reportDao.DDOS_TopInfoMonth(assetCode, sEndDay, String.valueOf(nAction), 4, 10);
		
    	nNo = 1;
    	List<String> lstDestIp = new ArrayList<String>();
    	for (DBObject val : dbDestIpTop10) {
    		String strDestIp = (String)val.get("destIp");

    		lstDestIp.add(strDestIp);
    		
    		List<DBObject> ipTop5_1st = reportDao.DDOS_Top10Condition_1st(assetCode, sEndDay, String.valueOf(nAction), 3, strDestIp);
    	
    		for (DBObject mObj : ipTop5_1st) {
    			List<DBObject> ipTop5_2nd = reportDao.DDOS_Top10Condition_2nd(assetCode, sEndDay, String.valueOf(nAction), 3, strDestIp, (String)mObj.get("message"));
    			for (DBObject sObj : ipTop5_2nd) {
	    			HashMap<String, Object> map = new HashMap<String, Object>();
	    			map.put("no", nNo);
	    			map.put("destIp", strDestIp);
	    			map.put("total", ((Number)val.get("count")).longValue());
	    			map.put("message", (String)mObj.get("message"));
	    			map.put("srcIp", (String)sObj.get("srcIp"));
	    			map.put("count", ((Number)sObj.get("count")).longValue());

    				DDOS_MAP_T3.add(map);
    			}
    		}
    		nNo++;
    	}
    	
    	prevMap = new HashMap<String, Object>();
    	List<DBObject> dbPrevDestIps = reportDao.DDOS_PrevMonthInfo(assetCode, sEndDay, String.valueOf(nAction), 2, lstDestIp);
    	for (DBObject mObj : dbPrevDestIps) {
    		prevMap.put((String)mObj.get("destIp"), ((Number)mObj.get("count")).longValue());
    	}

		for (HashMap<String, Object> map : DDOS_MAP_T3) {
			String sDestIp = (String)map.get("destIp");
			Object obj = prevMap.get(sDestIp);
			long lPrevCount = obj != null ? ((Number)obj).longValue() : 0;
			map.put("countVariation", ((Number)map.get("total")).longValue() - lPrevCount);
		}
    	
    	if (nAction == 0) {
    		assetReportInfo.put("DDOS_X_4_6_T", new SynthesisDataSource(DDOS_MAP_T3));
    		assetReportInfo.put("SubChapter23", addContents(Depth1 + ".4.8 월간 목적지IP TOP10 현황", "    "));
    	} else {
    		assetReportInfo.put("DDOS_X_3_6_T", new SynthesisDataSource(DDOS_MAP_T3));
    		assetReportInfo.put("SubChapter14", addContents(Depth1 + ".3.8 월간 목적지IP TOP10 현황", "    "));
    	}
	}
	
	private void DDOS_PerformanceLog(String sStartDay, String sEndDay, int assetCode, HashMap<String, Object> assetReportInfo, int nSubNo) throws Exception {
		
		boolean bPerformanceExist = false;
		
		List<String> dayPeriod = reportDao.getPeriodDay(sStartDay, sEndDay, -1, 1);
		
		List<DBObject> pfDbInfo = reportDao.getPerformanceMonth(assetCode, sEndDay);    	
			
		int nIndex = 1;
    	for (DBObject mVal : pfDbInfo) {
    		
    		if (!bPerformanceExist) {
    		    assetReportInfo.put("SubChapter30", addContents(Depth1 + "." + nSubNo + " 월간 성능 추이", "  "));
    		}
    		bPerformanceExist = true;

    		String sName = (String)mVal.get("name");

    		if (sName.equals("cpu")) {
    			assetReportInfo.put("PF_T_" + sName, addContents(Depth1 + "." + nSubNo + "." + nIndex++ + " CPU 추이", "    "));
    		} else if (sName.equals("disk")) {
    			assetReportInfo.put("PF_T_" + sName, addContents(Depth1 + "." + nSubNo + "." + nIndex++ + " 디스크 추이", "    "));
    		} else if (sName.equals("mem/real")) {
    			assetReportInfo.put("PF_T_" + sName, addContents(Depth1 + "." + nSubNo + "." + nIndex++ + " 메모리 추이(Real)", "    "));
    		} else if (sName.equals("mem/swap")) {
    			assetReportInfo.put("PF_T_" + sName, addContents(Depth1 + "." + nSubNo + "." + nIndex++ + " 메모리 추이(Swap)", "    "));
    		} else if (sName.equals("net/rx")) {
    			assetReportInfo.put("PF_T_" + sName, addContents(Depth1 + "." + nSubNo + "." + nIndex++ + " 트래픽 추이(RX)", "    "));
    		} else if (sName.equals("net/tx")) {
    			assetReportInfo.put("PF_T_" + sName, addContents(Depth1 + "." + nSubNo + "." + nIndex++ + " 트래픽 추이(TX)", "    "));
    		} else if (sName.equals("session")) {
    			assetReportInfo.put("PF_T_" + sName, addContents(Depth1 + "." + nSubNo + "." + nIndex++ + " 세션 추이", "    "));
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
	
	private String addContents(String strContents) {
		return addContents(strContents, "");
	}
	
	private String addContents(String strContents, String strIndent) {
		contentsList.add(strIndent + strContents);
		return strContents;
	}

	public HashMap<String, Object> getReportDataSource1(
			int Depth, 
			List<String> contentsList, 
			String sStartDay, 
			String sEndDay,
			HashMap<String, Object> assetMap) throws Exception {

		this.Depth1 = Depth;
		this.contentsList = contentsList;
		
		HashMap<String, Object> assetReportInfo = new HashMap<String, Object>();
    	
		assetReportInfo.put("SubChapter1", addContents(Depth1 + ". " + (String)assetMap.get("assetName") + " 월간 이벤트 분석"));
    	
    	try {
    		//최근 6개월 추이
    		DDOS_EventMonthlyChange(sStartDay, sEndDay, (Integer)assetMap.get("assetCode"), assetReportInfo);
    		
    		//월간 일자별 공격 추이 
    		DDOS_EventDailyChange(sStartDay, sEndDay, (Integer)assetMap.get("assetCode"), assetReportInfo);
    		
    		//월간 이벤트 분석
    		DDOS_EventMonthlyDetail(sStartDay, sEndDay, (Integer)assetMap.get("assetCode"), assetReportInfo);
    		
    		//월간 성능 추이
    		DDOS_PerformanceLog(sStartDay, sEndDay, (Integer)assetMap.get("assetCode"), assetReportInfo, 4);
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw e;
    	}
    	return assetReportInfo;
	}
	
	public HashMap<String, Object> getReportDataSource2(
			int Depth, 
			List<String> contentsList, 
			String sStartDay, 
			String sEndDay,
			HashMap<String, Object> assetMap) throws Exception {

		this.Depth1 = Depth;
		this.contentsList = contentsList;
		
		HashMap<String, Object> assetReportInfo = new HashMap<String, Object>();
    	
		assetReportInfo.put("SubChapter1", addContents(Depth1 + ". " + (String)assetMap.get("assetName") + " 월간 이벤트 분석"));
    	
    	try {
    		//최근 6개월 추이
    		DDOS_MonthlyChange(sStartDay, sEndDay, (Integer)assetMap.get("assetCode"), assetReportInfo);
    		
    		//금월 일별 이벤트 발생 추이
    		DDOS_DailyChange(sStartDay, sEndDay, (Integer)assetMap.get("assetCode"), assetReportInfo);
    		
    		for (int action = 1; action >= 0; action--) {
    			//월간 이벤트 TOP10에 대한 일별 발생 추이
    	    	DDOS_Top10DailyEventChange(sStartDay, sEndDay, (Integer)assetMap.get("assetCode"), assetReportInfo, action);
    	    	
    	    	//월간 이벤트 TOP10(차트)
    	    	DDOS_Top10MonthlyChart(sStartDay, sEndDay, (Integer)assetMap.get("assetCode"), assetReportInfo, action);
    	    	
    	    	//월간 이벤트에 대한 서비스 분포 Pie
    	    	DDOS_ServiceMonthPie(sStartDay, sEndDay, (Integer)assetMap.get("assetCode"), assetReportInfo, action);
    	    	
    	    	//월간 이벤트 TOP10
    	    	DDOS_Top10Detail(sStartDay, sEndDay, (Integer)assetMap.get("assetCode"), assetReportInfo, action);
    		}
    		
    		//월간 성능 추이
    		DDOS_PerformanceLog(sStartDay, sEndDay, (Integer)assetMap.get("assetCode"), assetReportInfo, 5);
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw e;
    	}
    	return assetReportInfo;
	}
	
}
