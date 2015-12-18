package com.cyberone.report.core.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.socket.TextMessage;

import com.cyberone.report.Constants;
import com.cyberone.report.core.dao.BaseDao;
import com.cyberone.report.core.dao.SvcDao;
import com.cyberone.report.core.datasource.SynthesisDataSource;
import com.cyberone.report.exception.BizException;
import com.cyberone.report.model.UserInfo;
import com.cyberone.report.utils.StringUtil;
import com.mongodb.DBObject;

public class SvcMonthlyReport extends BaseReport {
	
	private SvcDao svcDao;
	private BaseDao baseDao;
	
	private UserInfo userInfo;
	
	public SvcMonthlyReport(UserInfo userInfo) {
		this.userInfo = userInfo;
		this.svcDao = new SvcDao();
		this.baseDao = new BaseDao(userInfo.getPromDb(), userInfo.getReportDb());
	}
	
	/* 
	 * 서비스데스크 일일보고서 통계데이타 생성
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getDataSource(String sReportType, String sSearchDate, HashMap<String, Object> hMap, int ItemNo, List<String> contentsList) throws Exception {
		logger.debug("서비스데스크 일일보고서 통계데이타 생성");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		
		HashMap<String, Object> reportData = new HashMap<String, Object>();
		
		contentsList.add(ItemNo + ". 관제실적");
		reportData.put("RT", ItemNo + ". 관제실적"); //Report Title
		
		String sStartDay = "";
		String sEndDay = "";

		//서비스데스크 고객사 검색
		String sGroupCode = StringUtil.convertString(hMap.get("assetCode"));
		List<String> saAssetCodes = new ArrayList<String>();
		List<DBObject> dbResult = baseDao.getGroupAssets(Integer.valueOf(sGroupCode));
		for (DBObject asset : dbResult) {
			saAssetCodes.add(StringUtil.convertString(asset.get("assetCode")));
		}
		int cscoCd = svcDao.getCscoCd(saAssetCodes);
		cscoCd = 1;
		
		if (cscoCd <= 0) {
			throw new BizException("서비스데스크의 고객사를 찾을 수 없습니다.");
		}

		//조회 시작일/종료일
		int statBaseDay = baseDao.getStatMonthBaseDay(Integer.valueOf(saAssetCodes.get(0)));
		if (statBaseDay > 1) {
			Date tmpSDate = sdf.parse(sSearchDate);
			Calendar startCal = baseDao.getCalendar(tmpSDate);
			startCal.add(Calendar.DAY_OF_MONTH, statBaseDay);
			startCal.add(Calendar.DAY_OF_MONTH, -1);
			
			Date tmpEDate = sdf.parse(sSearchDate);
			Calendar endCal = baseDao.getCalendar(tmpEDate);
			endCal.add(Calendar.MONTH, 1);
			endCal.add(Calendar.DAY_OF_MONTH, statBaseDay);
			endCal.add(Calendar.DAY_OF_MONTH, -1);
			endCal.add(Calendar.SECOND, -1);
			
			sStartDay = sdf2.format(startCal.getTime());
			sEndDay = sdf2.format(endCal.getTime());
		} else {
			Date tmpSDate = sdf.parse(sSearchDate);
			Calendar startCal = baseDao.getCalendar(tmpSDate);
		
			Date tmpEDate = sdf.parse(sSearchDate);
			Calendar endCal = baseDao.getCalendar(tmpEDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);

			sStartDay = sdf2.format(startCal.getTime());
			sEndDay = sdf2.format(endCal.getTime());
		}
		
		HashMap<String,Object> hFormMap = (HashMap<String,Object>)hMap.get("formData");
		
		String sEtc = "";
		HashMap<String,Object> hData = null;
		if (hFormMap == null) {
			DBObject dbObj = baseDao.selectAutoReportForm(Integer.parseInt(sReportType), Integer.valueOf(sGroupCode));
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
		
		int nC1 = 0, nC2 = 0, nC3 = 0, nC4 = 0, nC5 = 0, nC6 = 0, nC7 = 0, nC8 = 0, nC9 = 0;
		int nS1 = 1, nS2 = 1, nS3 = 1, nS4 = 1, nS5 = 1, nS6 = 1, nS7 = 1, nS8 = 1, nS9 = 1;
		
		int nChoice = 0;
		for (Entry<String, Object> e : hData.entrySet()) {

			switch (e.getKey()) {
				//보안관제 업무현황
				case "opt01" : 
					if (nC1 == 0) { 
						contentsList.add("  " + ItemNo + "." + ++nC1 + " 보안관제 업무현황");
						reportData.put("C1", "  " + ItemNo + "." + nC1 + " 보안관제 업무현황");
					}
					break;
				//보안장비 운영현황
				case "opt02" : 
					if (nC2 == 0) { 
						contentsList.add("  " + ItemNo + "." + (++nC2 + nC1) + " 보안장비 운영현황");
						reportData.put("C2", "  " + ItemNo + "." + (nC2 + nC1) + " 보안장비 운영현황");
					}
					break;
				//고객요청 처리현황
				case "opt03" : 
					if (nC3 == 0) { 
						contentsList.add("  " + ItemNo + "." + (++nC3 + nC2 + nC1) + " 고객요청 처리현황");
						reportData.put("C3", "  " + ItemNo + "." + (nC3 + nC2 + nC1) + " 고객요청 처리현황");
					}
					break;
				//보안정보 제공현황
				case "opt04" : 
					if (nC4 == 0) { 
						contentsList.add("  " + ItemNo + "." + (++nC4 + nC3 + nC2 + nC1) + " 보안정보 제공현황");
						reportData.put("C4", "  " + ItemNo + "." + (nC4 + nC3 + nC2 + nC1) + " 보안정보 제공현황");
					}
					break;
				case "opt05" : case "opt06" : case "opt07" :
					if (nC5 == 0) { 
						contentsList.add("  " + ItemNo + "." + (++nC5 + nC4 + nC3 + nC2 + nC1) + " 싸이버원 침해위협탐지 동향 요약");
						reportData.put("C5", "  " + ItemNo + "." + (nC5 + nC4 + nC3 + nC2 + nC1) + " 싸이버원 침해위협탐지 동향 요약");
					}
					break;
				//침해위협 탐지현황
				case "opt08" : case "opt09" : case "opt10" : case "opt11" : case "opt12" : 
					if (nC6 == 0) { 
						contentsList.add("  " + ItemNo + "." + (++nC6 + nC5 + nC4 + nC3 + nC2 + nC1) + " 침해위협 탐지현황");
						reportData.put("C6", "  " + ItemNo + "." + (nC6 + nC5 + nC4 + nC3 + nC2 + nC1) + " 침해위협 탐지현황");
					}
					break;
				//홈페이지 악성코드 유포탐지 현황
				case "opt13" :  
					if (nC7 == 0) { 
						contentsList.add("  " + ItemNo + "." + (++nC7 + nC6 + nC5 + nC4 + nC3 + nC2 + nC1) + " 홈페이지 악성코드 유포탐지 현황");
						reportData.put("C7", "  " + ItemNo + "." + (nC7 + nC6 + nC5 + nC4 + nC3 + nC2 + nC1) + " 홈페이지 악성코드 유포탐지 현황");
					}
					break;
				//웹쉘 탐지현황
				case "opt14" :  
					if (nC8 == 0) { 
						contentsList.add("  " + ItemNo + "." + (++nC8 + nC7 + nC6 + nC5 + nC4 + nC3 + nC2 + nC1) + " 웹쉘 탐지현황");
						reportData.put("C8", "  " + ItemNo + "." + (nC8 + nC7 + nC6 + nC5 + nC4 + nC3 + nC2 + nC1) + " 웹쉘 탐지현황");
					}
					break;
				//RAT 의심탐지 현황
				case "opt15" :  
					if (nC9 == 0) { 
						contentsList.add("  " + ItemNo + "." + (++nC9 + nC8 + nC7 + nC6 + nC5 + nC4 + nC3 + nC2 + nC1) + " RAT 의심탐지 현황");
						reportData.put("C9", "  " + ItemNo + "." + (nC9 + nC8 + nC7 + nC6 + nC5 + nC4 + nC3 + nC2 + nC1) + " RAT 의심탐지 현황");
					}
					break;
			}
			
			switch (e.getKey()) {
				case "opt01" : 
					reportData.put("opt01", push(contentsList, "    ", ItemNo, nC1, nS1++, " 비정기 보고현황 : 상황/ 장애/ 작업 현황정보"));
					ServiceDeskStatItem01(reportData, cscoCd, sStartDay, sEndDay, !StringUtil.isEmpty(hData.get("ck1")));
					break;
				case "opt02" : 
					reportData.put("opt02", push(contentsList, "    ", ItemNo, nC2 + nC1, nS2++, " 설정변경/운영 현황 : 방화벽 정책 처리/ 룰 설정변경/ 패턴 업데이트/ 패치 현황"));
					ServiceDeskStatItem02(reportData, cscoCd, sStartDay, sEndDay, !StringUtil.isEmpty(hData.get("ck2")));
					break;
				case "opt03" : 
					reportData.put("opt03", push(contentsList, "    ", ItemNo, nC3 + nC2 + nC1, nS3++, " 요청처리 현황 : 일반요청/ 기술지원/ 침해사고 분석"));
					ServiceDeskStatItem03(reportData, cscoCd, sStartDay, sEndDay, !StringUtil.isEmpty(hData.get("ck3")));
					break;
				case "opt04" : 
					reportData.put("opt04", push(contentsList, "    ", ItemNo, nC4 + nC3 + nC2 + nC1, nS4++, " 보안정보 제공현황"));
					ServiceDeskStatItem04(reportData, sStartDay, sEndDay);
					break;
					
				case "opt05" :
					reportData.put("opt05", push(contentsList, "    ", ItemNo, nC5 + nC4 + nC3 + nC2 + nC1, nS5++, " 침해위협 유형별 탐지동향"));
					ServiceDeskStatItem05(reportData, sStartDay, sEndDay);
					break;
				case "opt06" :
					reportData.put("opt06", push(contentsList, "    ", ItemNo, nC5 + nC4 + nC3 + nC2 + nC1, nS5++, " 공격지 별 탐지동향"));
					ServiceDeskStatItem06(reportData, sStartDay, sEndDay);
					break;
				case "opt07" :
					reportData.put("opt07", push(contentsList, "    ", ItemNo, nC5 + nC4 + nC3 + nC2 + nC1, nS5++, " 목적지 별 탐지동향"));
					ServiceDeskStatItem07(reportData, sStartDay, sEndDay);
					break;
					
				case "opt08" : 
					reportData.put("opt08", push(contentsList, "    ", ItemNo, nC6 + nC5 + nC4 + nC3 + nC2 + nC1, nS6++, " 침해위협탐지 추이 : 공격유형별 침해위협통보 추이"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd8")));
					ServiceDeskStatItem08(reportData, cscoCd, sStartDay, sEndDay, nChoice);
					break;
				case "opt09" : 
					reportData.put("opt09", push(contentsList, "    ", ItemNo, nC6 + nC5 + nC4 + nC3 + nC2 + nC1, nS6++, " 침해위협탐지 분석 : 공격유형별 통계"));
					ServiceDeskStatItem09(reportData, cscoCd, sStartDay, sEndDay, !StringUtil.isEmpty(hData.get("ck9")));
					break;
				case "opt10" : 
					reportData.put("opt10", push(contentsList, "    ", ItemNo, nC6 + nC5 + nC4 + nC3 + nC2 + nC1, nS6++, " 공격IP 탐지동향 : 침해위협통보건에 대한 출발지 TOP"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd10")));
					ServiceDeskStatItem10(reportData, cscoCd, sStartDay, sEndDay, nChoice, !StringUtil.isEmpty(hData.get("ck10")), StringUtil.convertString(hData.get("sd10")));
					break;
				case "opt11" : 
					reportData.put("opt11", push(contentsList, "    ", ItemNo, nC6 + nC5 + nC4 + nC3 + nC2 + nC1, nS6++, " 공격지 탐지동향 : 침해위협통보건에 대한 국가별 통계"));
					ServiceDeskStatItem11(reportData, cscoCd, sStartDay, sEndDay, !StringUtil.isEmpty(hData.get("ck11")), StringUtil.convertString(hData.get("sd11")));
					break;
				case "opt12" : 
					reportData.put("opt12", push(contentsList, "    ", ItemNo, nC6 + nC5 + nC4 + nC3 + nC2 + nC1, nS6++, " 목적지 탐지동향 : 침해위협통보건에 대한 목적지 TOP"));
					nChoice = Integer.valueOf(StringUtil.convertString(hData.get("rd12")));
					ServiceDeskStatItem12(reportData, cscoCd, sStartDay, sEndDay, nChoice);
					break;
				case "opt13" : 
					//reportData.put("opt13", push(contentsList, "    ", ItemNo, nC7 + nC6 + nC5 + nC4 + nC3 + nC2 + nC1, nS7++, " 탐지보고 현황"));
					ServiceDeskStatItem13(reportData, cscoCd, sStartDay, sEndDay, !StringUtil.isEmpty(hData.get("ck13")));
					break;
				case "opt14" : 
					//reportData.put("opt14", push(contentsList, "    ", ItemNo, nC8 + nC7 + nC6 + nC5 + nC4 + nC3 + nC2 + nC1, nS8++, " 탐지보고 현황"));
					ServiceDeskStatItem14(reportData, cscoCd, sStartDay, sEndDay, !StringUtil.isEmpty(hData.get("ck14")));
					break;
				case "opt15" : 
					//reportData.put("opt15", push(contentsList, "    ", ItemNo, nC9 + nC8 + nC7 + nC6 + nC5 + nC4 + nC3 + nC2 + nC1, nS9++, " 탐지보고 현황"));
					ServiceDeskStatItem15(reportData, cscoCd, sStartDay, sEndDay, !StringUtil.isEmpty(hData.get("ck15")));
					break;
			}
		}
		
		return reportData;
	}

	private void ServiceDeskStatItem01(HashMap<String, Object> reportData, int cscoCd, String sStartDay, String sEndDay, boolean bChk) throws Exception {
		
		cscoCd = 93;
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();
		
		List<HashMap<String, Object>> resultList = svcDao.selectWorkCondition(cscoCd, sStartDay, sEndDay);
		
		HashMap<String, HashMap<String, Object>> resultMap = new HashMap<String, HashMap<String, Object>>();
		for (HashMap<String, Object> hMap : resultList) {
			resultMap.put(StringUtil.convertString(hMap.get("rptTp")), hMap);
		}
		
		for (int rptTp = 5; rptTp <= 8; rptTp++) {
			HashMap<String, Object> hMap = resultMap.get(String.valueOf(rptTp));	
			
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("series", "보고건수");
			map.put("category", getRptTypeName(String.valueOf(rptTp)));
			map.put("value", hMap != null ? ((Long)hMap.get("CNT")).longValue() : 0);
			dataSource.add(map);
			logger.debug(map.toString());
		}
		reportData.put("OPT1_1", new SynthesisDataSource(dataSource));
		
		if (bChk) {
			dataSource = new ArrayList<HashMap<String, Object>>();
			resultList = svcDao.selectWorkDetail(cscoCd, sStartDay, sEndDay);
			int nNo = 1;
			for (HashMap<String, Object> hMap : resultList) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("no", nNo);
				map.put("gubun", getRptTypeName(StringUtil.convertString(hMap.get("rptTp"))));
				map.put("date", StringUtil.convertString(hMap.get("dateValue")));
				map.put("content", StringUtil.convertString(hMap.get("contValue")));

				dataSource.add(map);
				logger.debug(map.toString());
				nNo++;
			}
			reportData.put("OPT1_2", new SynthesisDataSource(dataSource));
		}
	}

	private void ServiceDeskStatItem02(HashMap<String, Object> reportData, int cscoCd, String sStartDay, String sEndDay, boolean bChk) throws Exception {
		
		cscoCd = 93;
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();
		
		List<HashMap<String, Object>> resultList = svcDao.selectOperCondition(cscoCd, sStartDay, sEndDay);
		
		HashMap<String, HashMap<String, Object>> resultMap = new HashMap<String, HashMap<String, Object>>();
		for (HashMap<String, Object> hMap : resultList) {
			resultMap.put(StringUtil.convertString(hMap.get("SCT")), hMap);
		}
		
		String[] saSct = {"1", "6", "7", "8"};
		for (int i = 0; i < 4; i++) {
			HashMap<String, Object> hMap = resultMap.get(saSct[i]);	
			
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("series", "보고건수");
			map.put("category", getDryTypeName(saSct[i]));
			map.put("value", hMap != null ? ((Long)hMap.get("CNT")).longValue() : 0);
			dataSource.add(map);
			logger.debug(map.toString());
		}
		reportData.put("OPT2_1", new SynthesisDataSource(dataSource));
		
		if (bChk) {
			resultList = svcDao.selectOperDetail(cscoCd, sStartDay, sEndDay);

			int nNo = 1;
			dataSource = new ArrayList<HashMap<String, Object>>();
			for (HashMap<String, Object> hMap : resultList) { //방화벽
				if (!StringUtil.convertString(hMap.get("SCT")).equals("1")) continue;
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("no", nNo);
				map.put("date", StringUtil.convertString(hMap.get("dateValue")));
				map.put("eqpmNm", StringUtil.convertString(hMap.get("eqpmNm")));
				map.put("srcIp", StringUtil.convertString(hMap.get("srcIp")).replace("{@}", "\n"));
				map.put("destIp", StringUtil.convertString(hMap.get("destIp")).replace("{@}", "\n"));
				map.put("svcName", StringUtil.convertString(hMap.get("destPort")).replace("{@}", "\n"));
				map.put("protocol", StringUtil.convertString(hMap.get("prtcl")));
				map.put("action", StringUtil.convertString(hMap.get("execSct")));
				
				dataSource.add(map);
				logger.debug(map.toString());
				nNo++;
			}
			reportData.put("OPT2_2", new SynthesisDataSource(dataSource));
			
			nNo = 1;
			dataSource = new ArrayList<HashMap<String, Object>>();
			for (HashMap<String, Object> hMap : resultList) { //룰변경/패턴/패치
				if (StringUtil.convertString(hMap.get("SCT")).equals("1")) continue;
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("no", nNo);
				map.put("gubun", getDryTypeName(StringUtil.convertString(hMap.get("SCT"))));
				map.put("date", StringUtil.convertString(hMap.get("dateValue")));
				map.put("eqpmNm", StringUtil.convertString(hMap.get("eqpmNm")));
				map.put("content", StringUtil.convertString(hMap.get("contValue")));

				dataSource.add(map);
				logger.debug(map.toString());
				nNo++;
			}
			reportData.put("OPT2_3", new SynthesisDataSource(dataSource));
			
		}
	}

	private void ServiceDeskStatItem03(HashMap<String, Object> reportData, int cscoCd, String sStartDay, String sEndDay, boolean bChk) throws Exception {
		
		cscoCd = 189;
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();
		
		List<HashMap<String, Object>> resultList = svcDao.selectRequestCondition(cscoCd, sStartDay, sEndDay);
		
		HashMap<String, HashMap<String, Object>> resultMap = new HashMap<String, HashMap<String, Object>>();
		for (HashMap<String, Object> hMap : resultList) {
			resultMap.put(StringUtil.convertString(hMap.get("reqSct")), hMap);
		}
		
		for (int reqSct = 2; reqSct <= 4; reqSct++) {
			HashMap<String, Object> hMap = resultMap.get(String.valueOf(reqSct));	
			
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("series", "보고건수");
			map.put("category", getReqTypeName(String.valueOf(reqSct)));
			map.put("value", hMap != null ? ((Long)hMap.get("CNT")).longValue() : 0);
			dataSource.add(map);
			logger.debug(map.toString());
		}
		reportData.put("OPT3_1", new SynthesisDataSource(dataSource));
		
		if (bChk) {
			dataSource = new ArrayList<HashMap<String, Object>>();
			resultList = svcDao.selectRequestDetail(cscoCd, sStartDay, sEndDay);
			int nNo = 1;
			for (HashMap<String, Object> hMap : resultList) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("no", nNo);
				map.put("gubun", getReqTypeName(StringUtil.convertString(hMap.get("reqSct"))));
				map.put("date", StringUtil.convertString(hMap.get("dateValue")));
				map.put("content", StringUtil.convertString(hMap.get("contValue")));

				dataSource.add(map);
				logger.debug(map.toString());
				nNo++;
			}
			reportData.put("OPT3_2", new SynthesisDataSource(dataSource));
		}
	}
	
	private void ServiceDeskStatItem04(HashMap<String, Object> reportData, String sStartDay, String sEndDay) throws Exception {
		
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();
		
		List<HashMap<String, Object>> resultList = svcDao.selectNewsCondition(sStartDay, sEndDay);
		
		HashMap<String, HashMap<String, Object>> resultMap = new HashMap<String, HashMap<String, Object>>();
		for (HashMap<String, Object> hMap : resultList) {
			resultMap.put(StringUtil.convertString(hMap.get("bbsSct")), hMap);
		}
		
		String[] saSct = {"R", "T", "P", "B"};
		for (int i = 0; i < 4; i++) {
			HashMap<String, Object> hMap = resultMap.get(saSct[i]);	
			
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("series", "제공건수");
			map.put("category", getNewsTypeName(saSct[i]));
			map.put("value", hMap != null ? ((Long)hMap.get("CNT")).longValue() : 0);
			dataSource.add(map);
			logger.debug(map.toString());
		}
		reportData.put("OPT4_1", new SynthesisDataSource(dataSource));
	}	
	
	private void ServiceDeskStatItem05(HashMap<String, Object> reportData, String sStartDay, String sEndDay) throws Exception {
		
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();
		
		List<HashMap<String, Object>> resultList = svcDao.selectTotalTrendTopN(sStartDay, sEndDay, 1, 0);

		long lTotalCount = 0;
		for (HashMap<String, Object> hMap : resultList) {
        	HashMap<String, Object> map = new HashMap<String, Object>();
        	map.put("category", (String)hMap.get("esmRuleGroupPrntNm"));
        	map.put("count", ((Long)hMap.get("CNT")).longValue());
    		lTotalCount += ((Long)hMap.get("CNT")).longValue();
    		dataSource1.add(map);
		}
		for (HashMap<String, Object> map : dataSource1) {
			long count = ((Number)map.get("count")).longValue();
			map.put("ratio", (count * 100f) / lTotalCount);
			logger.debug(map.toString());
		}
		reportData.put("OPT5_1", new SynthesisDataSource(dataSource1));
		
		int nNo = 1;
		for (HashMap<String, Object> hMap : resultList) {
        	List<HashMap<String, Object>> dbRuleGroup = svcDao.selectRuleGroupCondition(0, sStartDay, sEndDay, 0, "", StringUtil.convertString(hMap.get("esmRuleGroupPrntCd")), 5);
        	for (HashMap<String, Object> pMap : dbRuleGroup) {
            	HashMap<String, Object> map = new HashMap<String, Object>();
            	map.put("no", nNo);
            	map.put("category", (String)hMap.get("esmRuleGroupPrntNm"));
            	map.put("total", ((Long)hMap.get("CNT")).longValue());
        		map.put("message", (String)pMap.get("esmRuleGroupNm"));
        		map.put("count", ((Long)pMap.get("CNT")).longValue());
        		map.put("ratio", (((Long)pMap.get("CNT")).longValue() * 100f) / ((Long)hMap.get("CNT")).longValue());
        		
        		dataSource2.add(map);
        		logger.debug(map.toString());
        	}
        	nNo++;
		}
		reportData.put("OPT5_2", new SynthesisDataSource(dataSource1));
	}

	private void ServiceDeskStatItem06(HashMap<String, Object> reportData, String sStartDay, String sEndDay) throws Exception {
		
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();
		
		List<HashMap<String, Object>> resultList = svcDao.selectAttackGeoTopN(0, sStartDay, sEndDay, null, 10);
		long lTotalCount = 0;
		for (HashMap<String, Object> hMap : resultList) {
        	HashMap<String, Object> map = new HashMap<String, Object>();
        	map.put("geoCode", (String)hMap.get("srcGeo"));
        	map.put("count", ((Long)hMap.get("CNT")).longValue());
    		lTotalCount += ((Long)hMap.get("CNT")).longValue();
    		dataSource1.add(map);
		}
		for (HashMap<String, Object> map : dataSource1) {
			long count = ((Number)map.get("count")).longValue();
			map.put("ratio", (count * 100f) / lTotalCount);
			logger.debug(map.toString());
		}
		reportData.put("OPT6_1", new SynthesisDataSource(dataSource1));
		
		int nNo = 1;
		for (HashMap<String, Object> hMap : resultList) {
			String srcGeo = (String)hMap.get("srcGeo");
			List<HashMap<String, Object>> prntList = svcDao.selectViolAlrtTopN(0, sStartDay, sEndDay, 3, srcGeo);
    		for (HashMap<String, Object> mMap : prntList) {
            	List<HashMap<String, Object>> dbRuleGroup = svcDao.selectRuleGroupCondition(0, sStartDay, sEndDay, 3, srcGeo, StringUtil.convertString(mMap.get("esmRuleGroupPrntCd")), 5);
            	for (HashMap<String, Object> pMap : dbRuleGroup) {
                	HashMap<String, Object> map = new HashMap<String, Object>();
                	map.put("no", nNo);
                	map.put("geoCode", srcGeo);
                	map.put("category", (String)mMap.get("esmRuleGroupPrntNm"));
                	map.put("total", ((Long)mMap.get("CNT")).longValue());
            		map.put("message", (String)pMap.get("esmRuleGroupNm"));
            		map.put("count", ((Long)pMap.get("CNT")).longValue());
            		map.put("ratio", (((Long)pMap.get("CNT")).longValue() * 100f) / ((Long)mMap.get("CNT")).longValue());
            		
            		dataSource2.add(map);
            		logger.debug(map.toString());
            	}
    			break;
    		}
        	nNo++;
		}
		reportData.put("OPT6_2", new SynthesisDataSource(dataSource2));
	}

	private void ServiceDeskStatItem07(HashMap<String, Object> reportData, String sStartDay, String sEndDay) throws Exception {
		
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();
		
		List<HashMap<String, Object>> resultList = svcDao.selectTotalTrendTopN(sStartDay, sEndDay, 3, 10);
		long lTotalCount = 0;
		for (HashMap<String, Object> hMap : resultList) {
        	HashMap<String, Object> map = new HashMap<String, Object>();
        	map.put("svcName", baseDao.getPortServiceName((Integer)hMap.get("destPort")));
        	map.put("count", ((Long)hMap.get("CNT")).longValue());
    		lTotalCount += ((Long)hMap.get("CNT")).longValue();
    		dataSource1.add(map);
		}
		for (HashMap<String, Object> map : dataSource1) {
			long count = ((Number)map.get("count")).longValue();
			map.put("ratio", (count * 100f) / lTotalCount);
			logger.debug(map.toString());
		}
		reportData.put("OPT7_1", new SynthesisDataSource(dataSource1));

		int nNo = 1;
		for (HashMap<String, Object> hMap : resultList) {
			int nPort = (Integer)hMap.get("destPort");
			List<HashMap<String, Object>> prntList = svcDao.selectViolAlrtTopN(0, sStartDay, sEndDay, 4, String.valueOf(nPort));
    		for (HashMap<String, Object> mMap : prntList) {
            	List<HashMap<String, Object>> dbRuleGroup = svcDao.selectRuleGroupCondition(0, sStartDay, sEndDay, 4, String.valueOf(nPort), StringUtil.convertString(mMap.get("esmRuleGroupPrntCd")), 5);
            	for (HashMap<String, Object> pMap : dbRuleGroup) {
                	HashMap<String, Object> map = new HashMap<String, Object>();
                	map.put("no", nNo);
                	map.put("svcName", baseDao.getPortServiceName(nPort));
                	map.put("category", (String)mMap.get("esmRuleGroupPrntNm"));
                	map.put("total", ((Long)mMap.get("CNT")).longValue());
            		map.put("message", (String)pMap.get("esmRuleGroupNm"));
            		map.put("count", ((Long)pMap.get("CNT")).longValue());
            		map.put("ratio", (((Long)pMap.get("CNT")).longValue() * 100f) / ((Long)mMap.get("CNT")).longValue());
            		
            		dataSource2.add(map);
            		logger.debug(map.toString());
            	}
    			break;
    		}
        	nNo++;
		}
		reportData.put("OPT7_2", new SynthesisDataSource(dataSource2));
	}
	
	private void ServiceDeskStatItem08(HashMap<String, Object> reportData, int cscoCd, String sStartDay, String sEndDay, int nChoice) throws Exception {
		
		cscoCd = 237;
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();
		
		if (nChoice == 1) {
			List<HashMap<String, Object>> resultList = svcDao.selectRuleGroupPrntTrend_D(cscoCd, sStartDay, sEndDay);
	
			List<String> prntList = new ArrayList<String>();
			HashMap<String, String> prntMap = new HashMap<String, String>();
			HashMap<String, HashMap<String, Object>> resultMap = new HashMap<String, HashMap<String, Object>>();
			for (HashMap<String, Object> hMap : resultList) {
				String sPrntCd = (String)hMap.get("esmRuleGroupPrntCd");
				if (!prntList.contains(sPrntCd)) {
					prntList.add(sPrntCd);
					prntMap.put(sPrntCd, (String)hMap.get("esmRuleGroupPrntNm"));
				}
				resultMap.put(sPrntCd + "-" + StringUtil.convertString(hMap.get("dateValue")), hMap);
			}
			
			List<String> dayPeriod = baseDao.getPeriodDay(sStartDay, sEndDay, 1);
	
			for (String sPrntCd : prntList) {
				for (String tDay : dayPeriod) {
					HashMap<String, Object> mVal = resultMap.get(sPrntCd + "-" + tDay);
		    		HashMap<String, Object> map = new HashMap<String, Object>();
		    		map.put("series", prntMap.get(sPrntCd));
		    		map.put("category", tDay);
		    		map.put("value", mVal != null ? ((Long)mVal.get("CNT")).longValue() : 0);
		    		
		    		dataSource.add(map);
		    		logger.debug(map.toString());
				}
			}
		} else {
			List<HashMap<String, Object>> resultList = svcDao.selectRuleGroupPrntTrend_M(cscoCd, baseDao.addMonth(sStartDay, -5), sEndDay);
			
			List<String> prntList = new ArrayList<String>();
			HashMap<String, String> prntMap = new HashMap<String, String>();
			HashMap<String, HashMap<String, Object>> resultMap = new HashMap<String, HashMap<String, Object>>();
			for (HashMap<String, Object> hMap : resultList) {
				String sPrntCd = (String)hMap.get("esmRuleGroupPrntCd");
				if (!prntList.contains(sPrntCd)) {
					prntList.add(sPrntCd);
					prntMap.put(sPrntCd, (String)hMap.get("esmRuleGroupPrntNm"));
				}
				resultMap.put(sPrntCd + "-" + StringUtil.convertString(hMap.get("dateValue")), hMap);
			}
			
			List<String> monthPeriod = baseDao.getPeriodMonth(sEndDay, -5, 1);
	
			for (String sPrntCd : prntList) {
				for (String tMonth : monthPeriod) {
					HashMap<String, Object> mVal = resultMap.get(sPrntCd + "-" + tMonth);
		    		HashMap<String, Object> map = new HashMap<String, Object>();
		    		map.put("series", prntMap.get(sPrntCd));
		    		map.put("category", tMonth);
		    		map.put("value", mVal != null ? ((Long)mVal.get("CNT")).longValue() : 0);
		    		
		    		dataSource.add(map);
		    		logger.debug(map.toString());
				}
			}
		}
		reportData.put("OPT8_1", new SynthesisDataSource(dataSource));
	}
	
	private void ServiceDeskStatItem09(HashMap<String, Object> reportData, int cscoCd, String sStartDay, String sEndDay, boolean bChk) throws Exception {
		
		cscoCd = 237;
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();
		
		List<HashMap<String, Object>> resultList = svcDao.selectViolAlrtTopN(cscoCd, sStartDay, sEndDay, 0, "");

		int nNo = 1;
		long lTotalCount = 0;
		for (HashMap<String, Object> hMap : resultList) {
			String sGrpPrnt = StringUtil.convertString(hMap.get("esmRuleGroupPrntCd"));
			
        	HashMap<String, Object> map1 = new HashMap<String, Object>();
        	map1.put("category", (String)hMap.get("esmRuleGroupPrntNm"));
        	map1.put("count", ((Long)hMap.get("CNT")).longValue());
        	dataSource1.add(map1);
    		
        	lTotalCount += ((Long)hMap.get("CNT")).longValue();
        	
    		List<HashMap<String, Object>> dbBefore = svcDao.selectViolAlrtTopN(cscoCd, baseDao.addDate(sStartDay, -1), baseDao.addDate(sEndDay, -1), 0, "");
			
    		HashMap<String, HashMap<String, Object>> mapBefore = new HashMap<String, HashMap<String, Object>>();
        	for (HashMap<String, Object> val : dbBefore) {
        		mapBefore.put(StringUtil.convertString(hMap.get("esmRuleGroupPrntCd")), val);
        	}
    		
        	HashMap<String, Object> prevMap = mapBefore.get(sGrpPrnt);
        	
        	List<HashMap<String, Object>> dbRuleGroup = svcDao.selectRuleGroupCondition(cscoCd, sStartDay, sEndDay, 0, "", sGrpPrnt, 5);
        	for (HashMap<String, Object> pMap : dbRuleGroup) {
            	HashMap<String, Object> map2 = new HashMap<String, Object>();
            	map2.put("no", nNo);
            	map2.put("category", (String)hMap.get("esmRuleGroupPrntNm"));
            	map2.put("count", ((Long)hMap.get("CNT")).longValue());
            	map2.put("countVariation", ((Long)hMap.get("CNT")).longValue() - (prevMap != null ? ((Long)prevMap.get("CNT")).longValue() : 0));
        		map2.put("message", (String)pMap.get("esmRuleGroupNm"));
        		map2.put("ratio", (((Long)pMap.get("CNT")).longValue() * 100f) / ((Long)hMap.get("CNT")).longValue());
        		
        		dataSource2.add(map2);
        		logger.debug(map2.toString());
        	}
        	nNo++;
		}
		
		for (HashMap<String, Object> map : dataSource1) {
			long count = ((Number)map.get("count")).longValue();
			map.put("ratio", (count * 100f) / lTotalCount);
			logger.debug(map.toString());
		}
		reportData.put("OPT9_1", new SynthesisDataSource(dataSource1));
		reportData.put("OPT9_2", new SynthesisDataSource(dataSource2));
		
		if (bChk) {
			dataSource1 = new ArrayList<HashMap<String, Object>>();
			resultList = svcDao.selectRuleGroupDetail(cscoCd, sStartDay, sEndDay);
			nNo = 1;
			for (HashMap<String, Object> hMap : resultList) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("no", nNo);
				map.put("eqpmName", StringUtil.convertString(hMap.get("esmAssetNm")));
				map.put("date", StringUtil.convertString(hMap.get("dateValue")));
				map.put("category", StringUtil.convertString(hMap.get("esmRuleGroupPrntNm")));
				map.put("group", StringUtil.convertString(hMap.get("esmRuleGroupNm")));
				map.put("srcIp", StringUtil.convertString(hMap.get("srcIp")));
				map.put("geoCode", StringUtil.convertString(hMap.get("srcGeo")));
				map.put("destIp", StringUtil.convertString(hMap.get("destIp")));
				map.put("action", StringUtil.convertString(hMap.get("attackIpBlock")).equals("y") ? "IP차단,메일통보" : "메일통보");
				map.put("count", (Integer)hMap.get("occurCnt"));
				
				dataSource1.add(map);
				logger.debug(map.toString());
				nNo++;
			}
			reportData.put("OPT9_3", new SynthesisDataSource(dataSource1));
		}
		
	}

	private void ServiceDeskStatItem10(HashMap<String, Object> reportData, int cscoCd, String sStartDay, String sEndDay, int nChoice, boolean bChk, String sOpt) throws Exception {
		
		cscoCd = 237;
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();
		
		List<HashMap<String, Object>> resultList = svcDao.selectAttackIpTopN(cscoCd, sStartDay, sEndDay, true, nChoice, null);

		List<String> topNIps = new ArrayList<String>();
		HashMap<String, HashMap<String, Object>> resultMap = new HashMap<String, HashMap<String, Object>>();
		for (HashMap<String, Object> hMap : resultList) {
			topNIps.add((String)hMap.get("srcIp"));
			resultMap.put((String)hMap.get("srcIp"), hMap);
		}
		
		List<HashMap<String, Object>> dbBefore = svcDao.selectAttackIpTopN(cscoCd, baseDao.addDate(sStartDay, -1), baseDao.addDate(sEndDay, -1), true, nChoice, topNIps.toArray(new String[topNIps.size()]));
		HashMap<String, HashMap<String, Object>> mapBefore = new HashMap<String, HashMap<String, Object>>();
    	for (HashMap<String, Object> mVal : dbBefore) {
    		mapBefore.put(StringUtil.convertString(mVal.get("srcIp")), mVal);
    	}
		
    	int nNo = 1;
    	for (String sIp : topNIps) {
    		HashMap<String, Object> hMap = resultMap.get(sIp);
    		HashMap<String, Object> prevMap = mapBefore.get(sIp);
    		
    		List<HashMap<String, Object>> prntList = svcDao.selectViolAlrtTopN(cscoCd, sStartDay, sEndDay, 1, sIp);
    		
    		for (HashMap<String, Object> pMap : prntList) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("no", nNo);
				map.put("srcIp", sIp);
				map.put("geoCode", (String)hMap.get("srcGeo"));
				map.put("count", ((Long)hMap.get("CNT")).longValue());
				map.put("countVariation", ((Long)hMap.get("CNT")).longValue() - (prevMap != null ? ((Long)prevMap.get("CNT")).longValue() : 0));
				map.put("category", StringUtil.convertString(pMap.get("esmRuleGroupPrntNm")));
				map.put("ratio", (((Long)pMap.get("CNT")).longValue() * 100f) / ((Long)hMap.get("CNT")).longValue());
				
				dataSource1.add(map);
				logger.debug(map.toString());
    		}
			nNo++;
    	}
    	reportData.put("OPT10_1", new SynthesisDataSource(dataSource1));
    	
    	if (bChk) {
	    	if (topNIps.size() > 10) {
	    		topNIps = topNIps.subList(0, 10);
	    	}
    		
	    	if (Integer.valueOf(sOpt) == 1) { //최근 1개월
	    		resultList = svcDao.selectSrcIpTrend_D(cscoCd, sStartDay, sEndDay, topNIps.toArray(new String[topNIps.size()]));
	
	    		resultMap = new HashMap<String, HashMap<String, Object>>();
	    		for (HashMap<String, Object> hMap : resultList) {
	    			String sIp = (String)hMap.get("srcIp");
	    			resultMap.put(sIp + "-" + StringUtil.convertString(hMap.get("dateValue")), hMap);
	    		}
	    		
	    		List<String> dayPeriod = baseDao.getPeriodDay(sStartDay, sEndDay, 1);
	
	        	for (String strIp : topNIps) {
	        		for (String tDay : dayPeriod) {
	        			HashMap<String, Object> mVal = resultMap.get(strIp + "-" + tDay);
			    		HashMap<String, Object> map = new HashMap<String, Object>();
			    		map.put("series", strIp);
			    		map.put("category", tDay);
			    		map.put("value", mVal != null ? ((Long)mVal.get("CNT")).longValue() : 0);
			    		
			    		dataSource2.add(map);
						logger.debug(map.toString());
	        		}
	        	}
	    	} else {
	    		resultList = svcDao.selectSrcIpTrend_M(cscoCd, baseDao.addMonth(sStartDay, -5), sEndDay, topNIps.toArray(new String[topNIps.size()]));
	    		
	    		resultMap = new HashMap<String, HashMap<String, Object>>();
	    		for (HashMap<String, Object> hMap : resultList) {
	    			String sIp = (String)hMap.get("srcIp");
	    			resultMap.put(sIp + "-" + StringUtil.convertString(hMap.get("dateValue")), hMap);
	    		}
	    		
	    		List<String> monthPeriod = baseDao.getPeriodMonth(sEndDay, -5, 1);
	
	        	for (String strIp : topNIps) {
	        		for (String tMonth : monthPeriod) {
	        			HashMap<String, Object> mVal = resultMap.get(strIp + "-" + tMonth);
			    		HashMap<String, Object> map = new HashMap<String, Object>();
			    		map.put("series", strIp);
			    		map.put("category", tMonth);
			    		map.put("value", mVal != null ? ((Long)mVal.get("CNT")).longValue() : 0);
			    		
			    		dataSource2.add(map);
						logger.debug(map.toString());
	        		}
	        	}
	    	}
	    	reportData.put("OPT10_2", new SynthesisDataSource(dataSource2));
    	}
	}

	private void ServiceDeskStatItem11(HashMap<String, Object> reportData, int cscoCd, String sStartDay, String sEndDay, boolean bChk, String sOpt) throws Exception {
		
		cscoCd = 237;
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();

		List<HashMap<String, Object>> resultList = svcDao.selectAttackGeoTopN(cscoCd, sStartDay, sEndDay, null, 10);

		List<String> topNGeos = new ArrayList<String>();
		HashMap<String, HashMap<String, Object>> resultMap = new HashMap<String, HashMap<String, Object>>();
		for (HashMap<String, Object> hMap : resultList) {
			topNGeos.add((String)hMap.get("srcGeo"));
			resultMap.put((String)hMap.get("srcGeo"), hMap);
		}
		
		List<HashMap<String, Object>> dbBefore = svcDao.selectAttackGeoTopN(cscoCd, baseDao.addDate(sStartDay, -1), baseDao.addDate(sEndDay, -1), topNGeos.toArray(new String[topNGeos.size()]), 0);
		HashMap<String, HashMap<String, Object>> mapBefore = new HashMap<String, HashMap<String, Object>>();
    	for (HashMap<String, Object> mVal : dbBefore) {
    		mapBefore.put((String)mVal.get("srcGeo"), mVal);
    	}
		
    	int nNo = 1;
    	for (String srcGeo : topNGeos) {
    		HashMap<String, Object> hMap = resultMap.get(srcGeo);
    		HashMap<String, Object> prevMap = mapBefore.get(srcGeo);
    		
    		List<HashMap<String, Object>> prntList = svcDao.selectViolAlrtTopN(cscoCd, sStartDay, sEndDay, 3, srcGeo);
    		
    		for (HashMap<String, Object> pMap : prntList) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("no", nNo);
				map.put("geoCode", (String)hMap.get("srcGeo"));
				map.put("count", ((Long)hMap.get("CNT")).longValue());
				map.put("countVariation", ((Long)hMap.get("CNT")).longValue() - (prevMap != null ? ((Long)prevMap.get("CNT")).longValue() : 0));
				map.put("category", StringUtil.convertString(pMap.get("esmRuleGroupPrntNm")));
				map.put("ratio", (((Long)pMap.get("CNT")).longValue() * 100f) / ((Long)hMap.get("CNT")).longValue());
				
				dataSource1.add(map);
				logger.debug(map.toString());
    		}
			nNo++;
    	}
    	reportData.put("OPT11_1", new SynthesisDataSource(dataSource1));
	
    	if (bChk) {
	    	if (topNGeos.size() > 10) {
	    		topNGeos = topNGeos.subList(0, 10);
	    	}
    		
	    	if (Integer.valueOf(sOpt) == 1) { //최근 1개월
	    		resultList = svcDao.selectSrcGeoTrend_D(cscoCd, sStartDay, sEndDay, topNGeos.toArray(new String[topNGeos.size()]));
	
	    		resultMap = new HashMap<String, HashMap<String, Object>>();
	    		for (HashMap<String, Object> hMap : resultList) {
	    			String sGeo = (String)hMap.get("srcGeo");
	    			resultMap.put(sGeo + "-" + StringUtil.convertString(hMap.get("dateValue")), hMap);
	    		}
	    		
	    		List<String> dayPeriod = baseDao.getPeriodDay(sStartDay, sEndDay, 1);
	
	        	for (String strGeo : topNGeos) {
	        		for (String tDay : dayPeriod) {
	        			HashMap<String, Object> mVal = resultMap.get(strGeo + "-" + tDay);
			    		HashMap<String, Object> map = new HashMap<String, Object>();
			    		map.put("series", strGeo);
			    		map.put("category", tDay);
			    		map.put("value", mVal != null ? ((Long)mVal.get("CNT")).longValue() : 0);
			    		
			    		dataSource2.add(map);
						logger.debug(map.toString());
	        		}
	        	}
	    	} else {
	    		resultList = svcDao.selectSrcGeoTrend_M(cscoCd, baseDao.addMonth(sStartDay, -5), sEndDay, topNGeos.toArray(new String[topNGeos.size()]));
	    		
	    		resultMap = new HashMap<String, HashMap<String, Object>>();
	    		for (HashMap<String, Object> hMap : resultList) {
	    			String sGeo = (String)hMap.get("srcGeo");
	    			resultMap.put(sGeo + "-" + StringUtil.convertString(hMap.get("dateValue")), hMap);
	    		}
	    		
	    		List<String> monthPeriod = baseDao.getPeriodMonth(sEndDay, -5, 1);
	
	        	for (String strGeo : topNGeos) {
	        		for (String tMonth : monthPeriod) {
	        			HashMap<String, Object> mVal = resultMap.get(strGeo + "-" + tMonth);
			    		HashMap<String, Object> map = new HashMap<String, Object>();
			    		map.put("series", strGeo);
			    		map.put("category", tMonth);
			    		map.put("value", mVal != null ? ((Long)mVal.get("CNT")).longValue() : 0);
			    		
			    		dataSource2.add(map);
						logger.debug(map.toString());
	        		}
	        	}
	    	}
	    	reportData.put("OPT11_2", new SynthesisDataSource(dataSource2));
    	}
    	
	}

	private void ServiceDeskStatItem12(HashMap<String, Object> reportData, int cscoCd, String sStartDay, String sEndDay, int nChoice) throws Exception {
		
		cscoCd = 237;
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();
		
		List<HashMap<String, Object>> resultList = svcDao.selectAttackIpTopN(cscoCd, sStartDay, sEndDay, false, nChoice, null);

		List<String> topNIps = new ArrayList<String>();
		HashMap<String, HashMap<String, Object>> resultMap = new HashMap<String, HashMap<String, Object>>();
		for (HashMap<String, Object> hMap : resultList) {
			topNIps.add((String)hMap.get("destIp"));
			resultMap.put((String)hMap.get("destIp"), hMap);
		}
		
		List<HashMap<String, Object>> dbBefore = svcDao.selectAttackIpTopN(cscoCd, baseDao.addDate(sStartDay, -1), baseDao.addDate(sEndDay, -1), false, nChoice, topNIps.toArray(new String[topNIps.size()]));
		HashMap<String, HashMap<String, Object>> mapBefore = new HashMap<String, HashMap<String, Object>>();
    	for (HashMap<String, Object> mVal : dbBefore) {
    		mapBefore.put(StringUtil.convertString(mVal.get("destIp")), mVal);
    	}
		
    	int nNo = 1;
    	for (String sIp : topNIps) {
    		HashMap<String, Object> hMap = resultMap.get(sIp);
    		HashMap<String, Object> prevMap = mapBefore.get(sIp);
    		List<HashMap<String, Object>> prntList = svcDao.selectViolAlrtTopN(cscoCd, sStartDay, sEndDay, 2, sIp);
    		for (HashMap<String, Object> pMap : prntList) {
    			List<HashMap<String, Object>> srcIpList = svcDao.selectSrcIpDetail(cscoCd, sStartDay, sEndDay, sIp, StringUtil.convertString(pMap.get("esmRuleGroupPrntCd")));
    			for (HashMap<String, Object> sMap : srcIpList) {
    				HashMap<String, Object> map = new HashMap<String, Object>();
    				map.put("no", nNo);
    				map.put("destIp", sIp);
    				map.put("count", ((Long)hMap.get("CNT")).longValue());
    				map.put("countVariation", ((Long)hMap.get("CNT")).longValue() - (prevMap != null ? ((Long)prevMap.get("CNT")).longValue() : 0));
    				map.put("category", StringUtil.convertString(pMap.get("esmRuleGroupPrntNm")));
    				map.put("ratio", (((Long)pMap.get("CNT")).longValue() * 100f) / ((Long)hMap.get("CNT")).longValue());
    				map.put("srcIp", StringUtil.convertString(sMap.get("srcIp")));
    				map.put("svcName", baseDao.getPortServiceName((Integer)sMap.get("destPort")));
    				map.put("value", ((Long)sMap.get("CNT")).longValue());
    				
    				dataSource.add(map);
    				logger.debug(map.toString());
    			}
    		}
			nNo++;
    	}
    	reportData.put("OPT12_1", new SynthesisDataSource(dataSource));		

	}

	private void ServiceDeskStatItem13(HashMap<String, Object> reportData, int cscoCd, String sStartDay, String sEndDay, boolean bChk) throws Exception {
		
		cscoCd = 189;
		List<HashMap<String, Object>> dataSource = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> resultList = svcDao.selectCodeProDomainList(cscoCd);
		
		int nNo = 1;
		HashMap<String, HashMap<String, Object>> resultMap = new HashMap<String, HashMap<String, Object>>();
		for (HashMap<String, Object> hMap : resultList) {
			hMap.put("no", nNo++);
			hMap.put("count", 0);
			resultMap.put((String)hMap.get("domainUrl"), hMap);
		}
		reportData.put("OPT13_1", new SynthesisDataSource(resultList));
		
		nNo = 1;
		resultList = svcDao.selectGearTypeDetail(cscoCd, sStartDay, sEndDay, Constants.DTCTGEAR_CODEPRO);
		for (HashMap<String, Object> hMap : resultList) {
	    	String sEtcFields = StringUtil.convertString(hMap.get("etcFields"));
	    	ObjectMapper mapper = new ObjectMapper();
	    	HashMap<String, Object> etcFieldsMap = null;
	    	if (StringUtil.isEmpty(sEtcFields)) {
	    		etcFieldsMap = new HashMap<String, Object>();
	    	} else {
	    		etcFieldsMap = mapper.readValue(sEtcFields, HashMap.class);	
	    	}
			
	    	String sDomainUrl = StringUtil.convertString(etcFieldsMap.get("checkURL"));
	    	
	    	HashMap<String, Object> pMap = resultMap.get(sDomainUrl);
	    	if (pMap != null) {
	    		pMap.put("count", (Integer)pMap.get("count") + 1);
	    	}
	    	
	    	if (bChk) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("no", nNo);
				map.put("date", StringUtil.convertString(hMap.get("dateValue")));
				map.put("message", sDomainUrl);
				map.put("etc", "");
				
				dataSource.add(map);
				logger.debug(map.toString());
				nNo++;
	    	}
		}
		if (bChk) {
			reportData.put("OPT13_2", new SynthesisDataSource(dataSource));
		}
	}

	private void ServiceDeskStatItem14(HashMap<String, Object> reportData, int cscoCd, String sStartDay, String sEndDay, boolean bChk) throws Exception {
		
		cscoCd = 219;
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> resultList = svcDao.selectEsmAssetAggregate(cscoCd, sStartDay, sEndDay, Constants.DTCTGEAR_WSD);
		
		int nNo = 1;
		for (HashMap<String, Object> hMap : resultList) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("no", nNo++);
			map.put("eqpmName", StringUtil.convertString(hMap.get("esmAssetNm")));
			map.put("count", ((Long)hMap.get("CNT")).longValue());
			dataSource1.add(map);
			logger.debug(map.toString());
		}
		reportData.put("OPT14_1", new SynthesisDataSource(dataSource1));
		
		if (bChk) {
			nNo = 1;
			resultList = svcDao.selectGearTypeDetail(cscoCd, sStartDay, sEndDay, Constants.DTCTGEAR_WSD);
			for (HashMap<String, Object> hMap : resultList) {
		    	String sEtcFields = StringUtil.convertString(hMap.get("etcFields"));
		    	ObjectMapper mapper = new ObjectMapper();
		    	HashMap<String, Object> etcFieldsMap = null;
		    	if (StringUtil.isEmpty(sEtcFields)) {
		    		etcFieldsMap = new HashMap<String, Object>();
		    	} else {
		    		etcFieldsMap = mapper.readValue(sEtcFields, HashMap.class);	
		    	}
				
		    	String sFileName = StringUtil.convertString(etcFieldsMap.get("fName"));
			
		    	HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("no", nNo);
				map.put("date", StringUtil.convertString(hMap.get("dateValue")));
				map.put("eqpmName", StringUtil.convertString(hMap.get("esmAssetNm")));
				map.put("message", sFileName);
				map.put("etc", "");
				
				dataSource2.add(map);
				logger.debug(map.toString());
				nNo++;
			}			
			reportData.put("OPT14_2", new SynthesisDataSource(dataSource1));
		}
	}
	
	private void ServiceDeskStatItem15(HashMap<String, Object> reportData, int cscoCd, String sStartDay, String sEndDay, boolean bChk) throws Exception {
		
		cscoCd = 59;
		List<HashMap<String, Object>> dataSource1 = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> dataSource2 = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> resultList = svcDao.selectEsmAssetAggregate(cscoCd, sStartDay, sEndDay, Constants.DTCTGEAR_SNORT);
		
		int nNo = 1;
		for (HashMap<String, Object> hMap : resultList) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("no", nNo++);
			map.put("eqpmName", StringUtil.convertString(hMap.get("esmAssetNm")));
			map.put("count", ((Long)hMap.get("CNT")).longValue());
			dataSource1.add(map);
			logger.debug(map.toString());
		}
		reportData.put("OPT15_1", new SynthesisDataSource(dataSource1));
		
		if (bChk) {
			nNo = 1;
			resultList = svcDao.selectGearTypeDetail(cscoCd, sStartDay, sEndDay, Constants.DTCTGEAR_SNORT);
			for (HashMap<String, Object> hMap : resultList) {
		    	HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("no", nNo);
				map.put("date", StringUtil.convertString(hMap.get("dateValue")));
				map.put("eqpmName", StringUtil.convertString(hMap.get("esmAssetNm")));
				map.put("message", StringUtil.convertString(hMap.get("esmMessage")));
				map.put("srcIp", StringUtil.convertString(hMap.get("srcIp")));
				map.put("destIp", StringUtil.convertString(hMap.get("destIp")));
				map.put("etc", "");
				
				dataSource2.add(map);
				logger.debug(map.toString());
				nNo++;
			}			
			reportData.put("OPT15_2", new SynthesisDataSource(dataSource1));
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

	public String getRptTypeName(String rpt) {
		if (rpt.equals("5")) {
			return "상황보고서";
		} else if (rpt.equals("6")) {
			return "장애보고서";
		} else if (rpt.equals("7")) {
			return "작업수행계획서";
		} else if (rpt.equals("8")) {
			return "작업완료보고서";
		}
		return "-";
	}

	public String getDryTypeName(String sct) {
		if (sct.equals("1")) {
			return "방화벽정책처리";
		} else if (sct.equals("6")) {
			return "룰변경";
		} else if (sct.equals("7")) {
			return "패턴업데이트";
		} else if (sct.equals("8")) {
			return "패치";
		}
		return "-";
	}

	public String getReqTypeName(String rpt) {
		if (rpt.equals("2")) {
			return "일반요청";
		} else if (rpt.equals("3")) {
			return "침해사고분석";
		} else if (rpt.equals("4")) {
			return "기술지원";
		}
		return "-";
	}

	public String getNewsTypeName(String sct) {
		if (sct.equals("R")) {
			return "일일뉴스클리핑";
		} else if (sct.equals("T")) {
			return "주간보안동향";
		} else if (sct.equals("P")) {
			return "상황전파'";
		} else if (sct.equals("B")) {
			return "'악성코드 유포&경유지";
		}
		return "-";
	}
	
}
