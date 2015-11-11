package com.cyberone.report.core.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.cyberone.report.core.dao.WAFReportDao;
import com.cyberone.report.core.datasource.SynthesisDataSource;
import com.mongodb.DBObject;

public class WAFService extends BaseService {

	private int Depth1 = 1;
	private List<String> contentsList = new ArrayList<String>();
	private WAFReportDao reportDao;
	
	public WAFService(WAFReportDao reportDao) {
		this.reportDao = reportDao;
	}
	
	private void WAF_PerformanceLog(String sStartDay, String sEndDay, int assetCode, HashMap<String, Object> assetReportInfo) throws Exception {
		
		boolean bPerformanceExist = false;
		
		List<String> dayPeriod = reportDao.getPeriodDay(sStartDay, sEndDay, -1, 1);
		
		List<DBObject> pfDbInfo = reportDao.getPerformanceMonth(assetCode, sEndDay);    	
			
		int nIndex = 1;
    	for (DBObject mVal : pfDbInfo) {
        	if (!bPerformanceExist) {
        		assetReportInfo.put("SubChapter15", addContents(Depth1 + ".6 월간 성능 추이", "  "));
        	}
    		bPerformanceExist = true;

    		String sName = (String)mVal.get("name");

    		if (sName.equals("cpu")) {
    			assetReportInfo.put("PF_T_" + sName, addContents(Depth1 + ".6." + nIndex++ + " CPU 추이", "    "));
    		} else if (sName.equals("disk")) {
    			assetReportInfo.put("PF_T_" + sName, addContents(Depth1 + ".6." + nIndex++ + " 디스크 추이", "    "));
    		} else if (sName.equals("mem/real")) {
    			assetReportInfo.put("PF_T_" + sName, addContents(Depth1 + ".6." + nIndex++ + " 메모리 추이(Real)", "    "));
    		} else if (sName.equals("mem/swap")) {
    			assetReportInfo.put("PF_T_" + sName, addContents(Depth1 + ".6." + nIndex++ + " 메모리 추이(Swap)", "    "));
    		} else if (sName.equals("net/rx")) {
    			assetReportInfo.put("PF_T_" + sName, addContents(Depth1 + ".6." + nIndex++ + " 트래픽 추이(RX)", "    "));
    		} else if (sName.equals("net/tx")) {
    			assetReportInfo.put("PF_T_" + sName, addContents(Depth1 + ".6." + nIndex++ + " 트래픽 추이(TX)", "    "));
    		} else if (sName.equals("session")) {
    			assetReportInfo.put("PF_T_" + sName, addContents(Depth1 + ".6." + nIndex++ + " 세션 추이", "    "));
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
	private void WAF_AllowEventDetail(String sStartDay, String sEndDay, int assetCode, HashMap<String, Object> assetReportInfo) throws Exception {
	
		HashMap<String, Object> allowGridInfo = new HashMap<String, Object>();
		HashMap<String, Object> allowPieInfo = new HashMap<String, Object>();
		
		assetReportInfo.put("SubChapter10", addContents(Depth1 + ".5 이벤트 상세 분석", "  "));
		
		List<HashMap<String, Object>> hostList = new ArrayList<HashMap<String, Object>>();
		
		assetReportInfo.put("SubChapter11", addContents(Depth1 + ".5.1 월간 허용 이벤트 상세 분석", "    "));
		assetReportInfo.put("SubChapter12", addContents("가) 도메인 별 이벤트 탐지 현황", "      "));
		
    	//도메인 TOP10 허용 현황
		List<String> lstHosts = new ArrayList<String>();
		List<DBObject> hostTop10 = reportDao.WAF_TopInfoMonth(assetCode, sEndDay, ALLOW, 1, 10);
    	int nOrder = 1;
    	for (DBObject hVal : hostTop10) {
    		String sHost = (String)hVal.get("host");

    		lstHosts.add(sHost);
    		
    		HashMap<String, Object> h_Map = new HashMap<String, Object>();
    		h_Map.put("no", nOrder++);
    		h_Map.put("host", sHost);
    		h_Map.put("count", ((Number)hVal.get("count")).longValue());
    		hostList.add(h_Map);

    		List<HashMap<String, Object>> pieData = new ArrayList<HashMap<String, Object>>();
    		List<HashMap<String, Object>> gridData = new ArrayList<HashMap<String, Object>>();
    		
    		List<DBObject> messageTop10 = reportDao.WAF_Top10Message(assetCode, sEndDay, ALLOW, sHost);
    		int nNo = 1;
    		long mEtcCount = 0;
    		for (DBObject mVal : messageTop10) {
    			String sMessage = (String)mVal.get("message");
    			
        		HashMap<String, Object> m_Map = new HashMap<String, Object>();
        		if (!sMessage.equals("-1") && nNo <= 10) {
	        		m_Map.put("message", sMessage);
	        		m_Map.put("count", ((Number)mVal.get("count")).longValue());
	        		m_Map.put("ratio", (((Number)mVal.get("count")).longValue() * 100f) / ((Number)hVal.get("count")).longValue());
	        		pieData.add(m_Map);
	        		
	        		mEtcCount += ((Number)mVal.get("count")).longValue();
        		} else {
        			continue;
        		}
    			
    			Iterable<DBObject> srcIpTop5 = reportDao.WAF_Top5SrcIp(assetCode, sEndDay, ALLOW, sHost, sMessage);

    			//srcIp의 주요타킷 destIp
    			HashMap<String, String> destIpMap = new HashMap<String, String>(); 
    			Iterable<DBObject> destIps = reportDao.WAF_Top5DestIp(assetCode, sEndDay, ALLOW, sHost, sMessage);
    			String sTemp1 = null, sTemp2 = null; 
    			for (DBObject dVal : destIps) {
        			HashMap<String,Object> dId = (HashMap<String,Object>)dVal.get("_id");
        			sTemp1 = (String)dId.get("srcIp");
        			if (sTemp1.equals(sTemp2)) {
        				continue;
        			}
        			sTemp2 = sTemp1;
    				destIpMap.put(sTemp2, (String)dId.get("destIp"));
    			}
    			
    			for (DBObject sVal : srcIpTop5) {
        			HashMap<String,Object> sId = (HashMap<String,Object>)sVal.get("_id");
        			String sSrcIp = (String)sId.get("srcIp");
        			
            		HashMap<String, Object> s_Map = new HashMap<String, Object>();
            		s_Map.put("no", nNo);
            		s_Map.put("message", sMessage);
            		s_Map.put("total", ((Number)mVal.get("count")).longValue());
            		s_Map.put("srcIp", sSrcIp);
            		s_Map.put("count", ((Number)sVal.get("count")).longValue());
            		s_Map.put("destIp", (String)destIpMap.get(sSrcIp));
            		gridData.add(s_Map);
    			}
    			nNo++;
    		}
    		
    		mEtcCount = ((Number)hVal.get("count")).longValue() - mEtcCount;
    		
    		if (mEtcCount > 0) {
	    		HashMap<String, Object> m_Map = new HashMap<String, Object>();
	    		m_Map.put("message", "기타");
	    		m_Map.put("count", mEtcCount);
	    		m_Map.put("ratio", (mEtcCount * 100f) / ((Number)hVal.get("count")).longValue());
	    		pieData.add(m_Map);
	    		
	    		HashMap<String, Object> s_Map = new HashMap<String, Object>();
	    		s_Map.put("no", nNo);
	    		s_Map.put("message", "기타");
	    		s_Map.put("total", mEtcCount);
	    		s_Map.put("srcIp", "");
	    		s_Map.put("count", 0L);
	    		s_Map.put("destIp", "");
	    		gridData.add(s_Map);
    		}
    		
    		allowGridInfo.put(sHost, new SynthesisDataSource(gridData));
    		allowPieInfo.put(sHost, new SynthesisDataSource(pieData));
    	}	
    	
    	//Top10 이외의 기타 정보
    	long lEtcTotalCount = 0;
    	Iterable<DBObject> etcInfo = reportDao.WAF_EtcInfoMonth(assetCode, sEndDay, ALLOW, lstHosts);
    	for (DBObject hVal : etcInfo) {
    		lEtcTotalCount = ((Number)hVal.get("count")).longValue();
    		
    		HashMap<String, Object> h_Map = new HashMap<String, Object>();
    		h_Map.put("no", nOrder++);
    		h_Map.put("host", "기타");
    		h_Map.put("count", lEtcTotalCount);
    		hostList.add(h_Map);
    	}
    	
    	List<HashMap<String, Object>> gridData = new ArrayList<HashMap<String, Object>>();
    	Iterable<DBObject> etcMessage = reportDao.WAF_EtcMessage(assetCode, sEndDay, ALLOW, lstHosts);
		int nNo = 1;
		long mEtcCount = 0;
		for (DBObject mVal : etcMessage) {
			HashMap<String,Object> mId = (HashMap<String,Object>)mVal.get("_id");
			String sMessage = (String)mId.get("message");
			
			if (!sMessage.equals("-1") && nNo <= 10) {
    			mEtcCount += ((Number)mVal.get("count")).longValue();
    		} else {
    			continue;
    		}
			
			Iterable<DBObject> etcSrcIpTop5 = reportDao.WAF_EtcSrcIp(assetCode, sEndDay, ALLOW, lstHosts, sMessage);

			//srcIp의 주요타킷 destIp
			HashMap<String, String> destIpMap = new HashMap<String, String>(); 
			Iterable<DBObject> destIps = reportDao.WAF_EtcDestIp(assetCode, sEndDay, ALLOW, lstHosts, sMessage);
			String sTemp1 = null, sTemp2 = null; 
			for (DBObject dVal : destIps) {
    			HashMap<String,Object> dId = (HashMap<String,Object>)dVal.get("_id");
    			sTemp1 = (String)dId.get("srcIp");
    			if (sTemp1.equals(sTemp2)) {
    				continue;
    			}
    			sTemp2 = sTemp1;
				destIpMap.put(sTemp2, (String)dId.get("destIp"));
			}
			
			for (DBObject sVal : etcSrcIpTop5) {
    			HashMap<String,Object> sId = (HashMap<String,Object>)sVal.get("_id");
    			String sSrcIp = (String)sId.get("srcIp");
    			
        		HashMap<String, Object> s_Map = new HashMap<String, Object>();
        		s_Map.put("no", nNo);
        		s_Map.put("message", sMessage);
        		s_Map.put("total", ((Number)mVal.get("count")).longValue());
        		s_Map.put("srcIp", sSrcIp);
        		s_Map.put("count", ((Number)sVal.get("count")).longValue());
        		s_Map.put("destIp", (String)destIpMap.get(sSrcIp));
        		gridData.add(s_Map);
			}
			nNo++;
		}

		mEtcCount = lEtcTotalCount - mEtcCount;
		
		if (mEtcCount > 0) {
			HashMap<String, Object> s_Map = new HashMap<String, Object>();
			s_Map.put("no", nNo);
			s_Map.put("message", "기타");
			s_Map.put("total", mEtcCount);
			s_Map.put("srcIp", "");
			s_Map.put("count", 0L);
			s_Map.put("destIp", "");
			gridData.add(s_Map);
		}
		allowGridInfo.put("기타", new SynthesisDataSource(gridData));
		
    	assetReportInfo.put("allowGridInfo", allowGridInfo);
    	assetReportInfo.put("allowPieInfo", allowPieInfo);
    	assetReportInfo.put("allowHosts", new SynthesisDataSource(hostList));
	}
	
	@SuppressWarnings("unchecked")
	private void WAF_CutoffEventDetail(String sStartDay, String sEndDay, int assetCode, HashMap<String, Object> assetReportInfo) throws Exception {
	
		HashMap<String, Object> cutoffGridInfo = new HashMap<String, Object>();
		HashMap<String, Object> cutoffPieInfo = new HashMap<String, Object>();
		
		List<HashMap<String, Object>> hostList = new ArrayList<HashMap<String, Object>>();
		
		assetReportInfo.put("SubChapter13", addContents(Depth1 + ".5.2 월간 차단 이벤트 상세 분석", "    "));
		assetReportInfo.put("SubChapter14", addContents("가) 도메인 별 이벤트 탐지 현황", "      "));
		
    	//도메인 TOP10 허용 현황
		List<String> lstHosts = new ArrayList<String>();
		List<DBObject> hostTop10 = reportDao.WAF_TopInfoMonth(assetCode, sEndDay, CUTOFF, 1, 10);
    	int nOrder = 1;
    	for (DBObject hVal : hostTop10) {
    		String sHost = (String)hVal.get("host");

    		lstHosts.add(sHost);
    		
    		HashMap<String, Object> h_Map = new HashMap<String, Object>();
    		h_Map.put("no", nOrder++);
    		h_Map.put("host", sHost);
    		h_Map.put("count", ((Number)hVal.get("count")).longValue());
    		hostList.add(h_Map);

    		List<HashMap<String, Object>> pieData = new ArrayList<HashMap<String, Object>>();
    		List<HashMap<String, Object>> gridData = new ArrayList<HashMap<String, Object>>();
    		
    		List<DBObject> messageTop10 = reportDao.WAF_Top10Message(assetCode, sEndDay, CUTOFF, sHost);
    		int nNo = 1;
    		long mEtcCount = 0;
    		for (DBObject mVal : messageTop10) {
    			String sMessage = (String)mVal.get("message");
    			
        		HashMap<String, Object> m_Map = new HashMap<String, Object>();
        		if (!sMessage.equals("-1") && nNo <= 10) {
	        		m_Map.put("message", sMessage);
	        		m_Map.put("count", ((Number)mVal.get("count")).longValue());
	        		m_Map.put("ratio", (((Number)mVal.get("count")).longValue() * 100f) / ((Number)hVal.get("count")).longValue());
	        		pieData.add(m_Map);
	        		
	        		mEtcCount += ((Number)mVal.get("count")).longValue();
        		} else {
        			continue;
        		}
    			
    			Iterable<DBObject> srcIpTop5 = reportDao.WAF_Top5SrcIp(assetCode, sEndDay, CUTOFF, sHost, sMessage);

    			//srcIp의 주요타킷 destIp
    			HashMap<String, String> destIpMap = new HashMap<String, String>(); 
    			Iterable<DBObject> destIps = reportDao.WAF_Top5DestIp(assetCode, sEndDay, CUTOFF, sHost, sMessage);
    			String sTemp1 = null, sTemp2 = null; 
    			for (DBObject dVal : destIps) {
        			HashMap<String,Object> dId = (HashMap<String,Object>)dVal.get("_id");
        			sTemp1 = (String)dId.get("srcIp");
        			if (sTemp1.equals(sTemp2)) {
        				continue;
        			}
        			sTemp2 = sTemp1;
    				destIpMap.put(sTemp2, (String)dId.get("destIp"));
    			}
    			
    			for (DBObject sVal : srcIpTop5) {
        			HashMap<String,Object> sId = (HashMap<String,Object>)sVal.get("_id");
        			String sSrcIp = (String)sId.get("srcIp");
        			
            		HashMap<String, Object> s_Map = new HashMap<String, Object>();
            		s_Map.put("no", nNo);
            		s_Map.put("message", sMessage);
            		s_Map.put("total", ((Number)mVal.get("count")).longValue());
            		s_Map.put("srcIp", sSrcIp);
            		s_Map.put("count", ((Number)sVal.get("count")).longValue());
            		s_Map.put("destIp", (String)destIpMap.get(sSrcIp));
            		gridData.add(s_Map);
    			}
    			nNo++;
    		}
    		
    		mEtcCount = ((Number)hVal.get("count")).longValue() - mEtcCount;
    		
    		if (mEtcCount > 0) {
	    		HashMap<String, Object> m_Map = new HashMap<String, Object>();
	    		m_Map.put("message", "기타");
	    		m_Map.put("count", mEtcCount);
	    		m_Map.put("ratio", (mEtcCount * 100f) / ((Number)hVal.get("count")).longValue());
	    		pieData.add(m_Map);
	    		
	    		HashMap<String, Object> s_Map = new HashMap<String, Object>();
	    		s_Map.put("no", nNo);
	    		s_Map.put("message", "기타");
	    		s_Map.put("total", mEtcCount);
	    		s_Map.put("srcIp", "");
	    		s_Map.put("count", 0L);
	    		s_Map.put("destIp", "");
	    		gridData.add(s_Map);
    		}
    		
    		cutoffGridInfo.put(sHost, new SynthesisDataSource(gridData));
    		cutoffPieInfo.put(sHost, new SynthesisDataSource(pieData));
    	}	
    	
    	//Top10 이외의 기타 정보
    	long lEtcTotalCount = 0;
    	Iterable<DBObject> etcInfo = reportDao.WAF_EtcInfoMonth(assetCode, sEndDay, CUTOFF, lstHosts);
    	for (DBObject hVal : etcInfo) {
    		lEtcTotalCount = ((Number)hVal.get("count")).longValue();
    		
    		HashMap<String, Object> h_Map = new HashMap<String, Object>();
    		h_Map.put("no", nOrder++);
    		h_Map.put("host", "기타");
    		h_Map.put("count", lEtcTotalCount);
    		hostList.add(h_Map);
    	}
    	
    	List<HashMap<String, Object>> gridData = new ArrayList<HashMap<String, Object>>();
    	Iterable<DBObject> etcMessage = reportDao.WAF_EtcMessage(assetCode, sEndDay, CUTOFF, lstHosts);
		int nNo = 1;
		long mEtcCount = 0;
		for (DBObject mVal : etcMessage) {
			HashMap<String,Object> mId = (HashMap<String,Object>)mVal.get("_id");
			String sMessage = (String)mId.get("message");
			
			if (!sMessage.equals("-1") && nNo <= 10) {
    			mEtcCount += ((Number)mVal.get("count")).longValue();
    		} else {
    			continue;
    		}
			
			Iterable<DBObject> etcSrcIpTop5 = reportDao.WAF_EtcSrcIp(assetCode, sEndDay, CUTOFF, lstHosts, sMessage);

			//srcIp의 주요타킷 destIp
			HashMap<String, String> destIpMap = new HashMap<String, String>(); 
			Iterable<DBObject> destIps = reportDao.WAF_EtcDestIp(assetCode, sEndDay, CUTOFF, lstHosts, sMessage);
			String sTemp1 = null, sTemp2 = null; 
			for (DBObject dVal : destIps) {
    			HashMap<String,Object> dId = (HashMap<String,Object>)dVal.get("_id");
    			sTemp1 = (String)dId.get("srcIp");
    			if (sTemp1.equals(sTemp2)) {
    				continue;
    			}
    			sTemp2 = sTemp1;
				destIpMap.put(sTemp2, (String)dId.get("destIp"));
			}
			
			for (DBObject sVal : etcSrcIpTop5) {
    			HashMap<String,Object> sId = (HashMap<String,Object>)sVal.get("_id");
    			String sSrcIp = (String)sId.get("srcIp");
    			
        		HashMap<String, Object> s_Map = new HashMap<String, Object>();
        		s_Map.put("no", nNo);
        		s_Map.put("message", sMessage);
        		s_Map.put("total", ((Number)mVal.get("count")).longValue());
        		s_Map.put("srcIp", sSrcIp);
        		s_Map.put("count", ((Number)sVal.get("count")).longValue());
        		s_Map.put("destIp", (String)destIpMap.get(sSrcIp));
        		gridData.add(s_Map);
			}
			nNo++;
		}

		mEtcCount = lEtcTotalCount - mEtcCount;
		
		if (mEtcCount > 0) {
			HashMap<String, Object> s_Map = new HashMap<String, Object>();
			s_Map.put("no", nNo);
			s_Map.put("message", "기타");
			s_Map.put("total", mEtcCount);
			s_Map.put("srcIp", "");
			s_Map.put("count", 0L);
			s_Map.put("destIp", "");
			gridData.add(s_Map);
		}
		cutoffGridInfo.put("기타", new SynthesisDataSource(gridData));
		
    	assetReportInfo.put("cutoffGridInfo", cutoffGridInfo);
    	assetReportInfo.put("cutoffPieInfo", cutoffPieInfo);
    	assetReportInfo.put("cutoffHosts", new SynthesisDataSource(hostList));
	}
	
	
	private void WAF_DomainMonthPie(String sStartDay, String sEndDay, int assetCode, HashMap<String, Object> assetReportInfo) throws Exception {
	
		List<HashMap<String, Object>> WAF_6_2_G = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> WAF_6_3_G = new ArrayList<HashMap<String, Object>>();
		
		List<DBObject> dbResult = reportDao.WAF_DomainMonthPieData(assetCode, sEndDay);
		
		int nTop1 = 1, nTop0 = 1;
		long lEtcCount1 = 0, lEtcCount0 = 0;
		long lTotalCount1 = 0, lTotalCount0 = 0;
		for (DBObject val : dbResult) {
			
			if (((String)val.get("action")).equals("1")) { // 허용
				if (!((String)val.get("host")).equals("-1") && nTop1 <= 10) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("gubun", (String)val.get("host"));
					map.put("count", ((Number)val.get("count")).longValue());
					WAF_6_2_G.add(map);
					lTotalCount1 += ((Number)val.get("count")).longValue();
					nTop1++;
    			} else {
    				lEtcCount1 += ((Number)val.get("count")).longValue();
    			}
			} else if (((String)val.get("action")).equals("0")) { // 차단
				if (!((String)val.get("host")).equals("-1") && nTop0 <= 10) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("gubun", (String)val.get("host"));
					map.put("count", ((Number)val.get("count")).longValue());
					WAF_6_3_G.add(map);
					lTotalCount0 += ((Number)val.get("count")).longValue();
					nTop0++;
    			} else {
    				lEtcCount0 += ((Number)val.get("count")).longValue();
    			}
			}
		}
		
		if (lEtcCount1 > 0) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("gubun", "기타");
			map.put("count", lEtcCount1);
			WAF_6_2_G.add(map);
			lTotalCount1 += lEtcCount1;
		}

		if (lEtcCount0 > 0) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("gubun", "기타");
			map.put("count", lEtcCount0);
			WAF_6_3_G.add(map);
			lTotalCount0 += lEtcCount0;
		}
		
		for (HashMap<String, Object> map : WAF_6_2_G) {
			long count = ((Number)map.get("count")).longValue();
			map.put("ratio", (count * 100f) / lTotalCount1);
		}

		for (HashMap<String, Object> map : WAF_6_3_G) {
			long count = ((Number)map.get("count")).longValue();
			map.put("ratio", (count * 100f) / lTotalCount0);
		}
		assetReportInfo.put("WAF_6_2_G", new SynthesisDataSource(WAF_6_2_G));
		assetReportInfo.put("SubChapter8", addContents(Depth1 + ".3 월간 허용 이벤트에 대한 도메인 분포", "  "));
		
		assetReportInfo.put("WAF_6_3_G", new SynthesisDataSource(WAF_6_3_G));
		assetReportInfo.put("SubChapter9", addContents(Depth1 + ".4 월간 차단 이벤트에 대한 도메인 분포", "  "));
	}
	
	private void WAF_Top10EventChange(String sStartDay, String sEndDay, int assetCode, HashMap<String, Object> assetReportInfo) throws Exception {

    	List<HashMap<String, Object>> WAF_6_1_2_3_G = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> WAF_6_1_2_4_G = new ArrayList<HashMap<String, Object>>();

    	List<DBObject> dbTop10 = reportDao.WAF_TopInfoMonth(assetCode, sEndDay, ALLOW, 1, 10);
    	
    	List<String> saTop10Host = new ArrayList<String>();
    	for (DBObject val : dbTop10) {
    		saTop10Host.add((String)val.get("host"));
    	}
    	
    	List<DBObject> dbResult = reportDao.WAF_Top10DomainChange(assetCode, sStartDay, sEndDay, ALLOW, saTop10Host);
    	
    	List<String> dayPeriod = reportDao.getPeriodDay(sStartDay, sEndDay, 0, 1);

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
    	
    	for (String sHost : saTop10Host) {
	    	for (int i = 0; i < dayPeriod.size(); i++) {
	    		DBObject val = mapResult.get(sHost + "-" + dayPeriod.get(i));

	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		map.put("gubun", sHost);
	    		map.put("day", Integer.valueOf(dayPeriod.get(i).substring(6,8)));
	    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
	    		WAF_6_1_2_3_G.add(map);
	    	}
    	}
    	assetReportInfo.put("WAF_6_1_2_3_G", new SynthesisDataSource(WAF_6_1_2_3_G));
    	assetReportInfo.put("SubChapter6", addContents("다) 금월 상위 TOP10 도메인 별 일일 허용 이벤트 발생 추이", "    "));
    	
    	dbTop10 = reportDao.WAF_TopInfoMonth(assetCode, sEndDay, CUTOFF, 1, 10);
    	
    	saTop10Host = new ArrayList<String>();
    	for (DBObject val : dbTop10) {
    		saTop10Host.add((String)val.get("host"));
    	}
    	
    	dbResult = reportDao.WAF_Top10DomainChange(assetCode, sStartDay, sEndDay, CUTOFF, saTop10Host);
    	
    	mapResult = new HashMap<String, DBObject>(); 
    	for (DBObject val : dbResult) {
    		String sHost = (String)val.get("host");
    		String sYear = ((Integer)val.get("year")).toString();
    		String sMonth = ((Integer)val.get("month")).toString();
    		sMonth = sMonth.length() == 1 ? "0" + sMonth : sMonth;
    		String sDay = ((Integer)val.get("day")).toString();
    		sDay = sDay.length() == 1 ? "0" + sDay : sDay;
    		mapResult.put(sHost + "-" + sYear + sMonth + sDay, val);
    	}
    	
    	for (String sHost : saTop10Host) {
	    	for (int i = 0; i < dayPeriod.size(); i++) {
	    		DBObject val = mapResult.get(sHost + "-" + dayPeriod.get(i));

	    		HashMap<String, Object> map = new HashMap<String, Object>();
	    		map.put("gubun", sHost);
	    		map.put("day", Integer.valueOf(dayPeriod.get(i).substring(6,8)));
	    		map.put("count", val != null ? ((Number)val.get("count")).longValue() : 0);
	    		WAF_6_1_2_4_G.add(map);
	    	}
    	}
    	assetReportInfo.put("WAF_6_1_2_4_G", new SynthesisDataSource(WAF_6_1_2_4_G));
    	assetReportInfo.put("SubChapter7", addContents("라) 금월 상위 TOP10 도메인 별 일일 차단 이벤트 발생 추이", "    "));
	}
	
	private void WAF_DailyChange(String sStartDay, String sEndDay, int assetCode, HashMap<String, Object> assetReportInfo) throws Exception {

    	List<HashMap<String, Object>> WAF_6_1_2_1_G = new ArrayList<HashMap<String, Object>>();
    	List<HashMap<String, Object>> WAF_6_1_2_2_G = new ArrayList<HashMap<String, Object>>();
    	
    	assetReportInfo.put("SubChapter3", addContents(Depth1 + ".2 금월 일별 이벤트 발생 추이", "  "));
    	
    	Iterable<DBObject>  dbResult = reportDao.WAF_SessionLogDay(assetCode, sStartDay, sEndDay);   	
    	
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
        			WAF_6_1_2_2_G.add(map);
        		} else {
        			WAF_6_1_2_1_G.add(map);
        		}
    		}
    	}
    	assetReportInfo.put("WAF_6_1_2_1_G", new SynthesisDataSource(WAF_6_1_2_1_G));
    	assetReportInfo.put("SubChapter4", addContents("가) 금월 전체 허용 이벤트 비교", "    "));
    	
    	assetReportInfo.put("WAF_6_1_2_2_G", new SynthesisDataSource(WAF_6_1_2_2_G));
    	assetReportInfo.put("SubChapter5", addContents("나) 금월 전체 차단 이벤트 비교", "    "));
	}
	
	private void WAF_MonthlyChange(String sStartDay, String sEndDay, int assetCode, HashMap<String, Object> assetReportInfo) throws Exception {

    	List<HashMap<String, Object>> WAF_6_1_1_G = new ArrayList<HashMap<String, Object>>();
    	Iterable<DBObject> outToIn = reportDao.WAF_SessionLogMonth(assetCode, sEndDay);   	

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
	    		
	    		WAF_6_1_1_G.add(map);
    		}	    		
    	}
    	assetReportInfo.put("WAF_6_1_1_G", new SynthesisDataSource(WAF_6_1_1_G));
    	assetReportInfo.put("WAF_6_1_1_T", new SynthesisDataSource(WAF_6_1_1_G));
    	
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
    		WAF_MonthlyChange(sStartDay, sEndDay, (Integer)assetMap.get("assetCode"), assetReportInfo);
    		
    		//금월 일별 허용/차단 이벤트 발생 추이
    		WAF_DailyChange(sStartDay, sEndDay, (Integer)assetMap.get("assetCode"), assetReportInfo);
    		
    		//금월 상위 TOP10 도메인별  허용/차단 이벤트 발생 추이
    		WAF_Top10EventChange(sStartDay, sEndDay, (Integer)assetMap.get("assetCode"), assetReportInfo);
    		
    		//월간 허용/차단 이벤트에 대한 도메인 분포
    		WAF_DomainMonthPie(sStartDay, sEndDay, (Integer)assetMap.get("assetCode"), assetReportInfo);
    		
    		//월간 허용 이벤트 상세 분석
    		WAF_AllowEventDetail(sStartDay, sEndDay, (Integer)assetMap.get("assetCode"), assetReportInfo);
    		
    		//월간 차단 이벤트 상세 분석
    		WAF_CutoffEventDetail(sStartDay, sEndDay, (Integer)assetMap.get("assetCode"), assetReportInfo);
    		
    		//월간 웹방화벽 성능 추이
    		WAF_PerformanceLog(sStartDay, sEndDay, (Integer)assetMap.get("assetCode"), assetReportInfo);
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw e;
    	}
    	return assetReportInfo;
	}
	
}
