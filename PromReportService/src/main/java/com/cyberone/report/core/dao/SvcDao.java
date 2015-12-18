package com.cyberone.report.core.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.ibatis.session.SqlSession;

import com.cyberone.report.utils.StringUtil;

public class SvcDao {
	
	private static SqlSession sqlSession;
	
	public int getCscoCd(List<String> assetCodes) {
		List<HashMap<String, Object>> cscoEqpms = sqlSession.selectList("ServiceDesk.selectCscoEqpm_RefCd", assetCodes);
		for (HashMap<String, Object> map : cscoEqpms) {
			if ((Integer)map.get("cscoCd") > 0) {
				return (Integer)map.get("cscoCd");
			}
		}
		return -1;
	}
	
	public List<HashMap<String, Object>> selectWorkCondition(int cscoCd, String sStartDay, String sEndDay) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date startDate = sdf.parse(sStartDay);
		Calendar startCal = getCalendar(startDate);
		
		Date endDate = sdf.parse(sEndDay);
		Calendar endCal = getCalendar(endDate);
		endCal.add(Calendar.DAY_OF_MONTH, 1);
		endCal.add(Calendar.SECOND, -1);
		
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cscoCd", cscoCd);
		paramMap.put("startDtime", startCal.getTime());
		paramMap.put("endDtime", endCal.getTime());

		return sqlSession.selectList("ServiceDesk.selectWorkCondition", paramMap);
	}

	public List<HashMap<String, Object>> selectWorkDetail(int cscoCd, String sStartDay, String sEndDay) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date startDate = sdf.parse(sStartDay);
		Calendar startCal = getCalendar(startDate);
		
		Date endDate = sdf.parse(sEndDay);
		Calendar endCal = getCalendar(endDate);
		endCal.add(Calendar.DAY_OF_MONTH, 1);
		endCal.add(Calendar.SECOND, -1);
		
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cscoCd", cscoCd);
		paramMap.put("startDtime", startCal.getTime());
		paramMap.put("endDtime", endCal.getTime());
		
		return sqlSession.selectList("ServiceDesk.selectWorkDetail", paramMap);
	}

	public List<HashMap<String, Object>> selectOperCondition(int cscoCd, String sStartDay, String sEndDay) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date startDate = sdf.parse(sStartDay);
		Calendar startCal = getCalendar(startDate);
		
		Date endDate = sdf.parse(sEndDay);
		Calendar endCal = getCalendar(endDate);
		endCal.add(Calendar.DAY_OF_MONTH, 1);
		endCal.add(Calendar.SECOND, -1);
		
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cscoCd", cscoCd);
		paramMap.put("startDtime", startCal.getTime());
		paramMap.put("endDtime", endCal.getTime());
		
		return sqlSession.selectList("ServiceDesk.selectOperCondition", paramMap);
	}
	
	public List<HashMap<String, Object>> selectOperDetail(int cscoCd, String sStartDay, String sEndDay) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date startDate = sdf.parse(sStartDay);
		Calendar startCal = getCalendar(startDate);
		
		Date endDate = sdf.parse(sEndDay);
		Calendar endCal = getCalendar(endDate);
		endCal.add(Calendar.DAY_OF_MONTH, 1);
		endCal.add(Calendar.SECOND, -1);
		
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cscoCd", cscoCd);
		paramMap.put("startDtime", startCal.getTime());
		paramMap.put("endDtime", endCal.getTime());
		
		return sqlSession.selectList("ServiceDesk.selectOperDetail", paramMap);
	}
	
	public List<HashMap<String, Object>> selectRequestCondition(int cscoCd, String sStartDay, String sEndDay) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date startDate = sdf.parse(sStartDay);
		Calendar startCal = getCalendar(startDate);
		
		Date endDate = sdf.parse(sEndDay);
		Calendar endCal = getCalendar(endDate);
		endCal.add(Calendar.DAY_OF_MONTH, 1);
		endCal.add(Calendar.SECOND, -1);
		
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cscoCd", cscoCd);
		paramMap.put("startDtime", startCal.getTime());
		paramMap.put("endDtime", endCal.getTime());

		return sqlSession.selectList("ServiceDesk.selectRequestCondition", paramMap);
	}

	public List<HashMap<String, Object>> selectRequestDetail(int cscoCd, String sStartDay, String sEndDay) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date startDate = sdf.parse(sStartDay);
		Calendar startCal = getCalendar(startDate);
		
		Date endDate = sdf.parse(sEndDay);
		Calendar endCal = getCalendar(endDate);
		endCal.add(Calendar.DAY_OF_MONTH, 1);
		endCal.add(Calendar.SECOND, -1);
		
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cscoCd", cscoCd);
		paramMap.put("startDtime", startCal.getTime());
		paramMap.put("endDtime", endCal.getTime());
		
		return sqlSession.selectList("ServiceDesk.selectRequestDetail", paramMap);
	}
	
	public List<HashMap<String, Object>> selectNewsCondition(String sStartDay, String sEndDay) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date startDate = sdf.parse(sStartDay);
		Calendar startCal = getCalendar(startDate);
		
		Date endDate = sdf.parse(sEndDay);
		Calendar endCal = getCalendar(endDate);
		endCal.add(Calendar.DAY_OF_MONTH, 1);
		endCal.add(Calendar.SECOND, -1);
		
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startDtime", startCal.getTime());
		paramMap.put("endDtime", endCal.getTime());

		return sqlSession.selectList("ServiceDesk.selectNewsCondition", paramMap);
	}
	
	public List<HashMap<String, Object>> selectRuleGroupPrntTrend_D(int cscoCd, String sStartDay, String sEndDay) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date startDate = sdf.parse(sStartDay);
		Calendar startCal = getCalendar(startDate);
		
		Date endDate = sdf.parse(sEndDay);
		Calendar endCal = getCalendar(endDate);
		endCal.add(Calendar.DAY_OF_MONTH, 1);
		endCal.add(Calendar.SECOND, -1);
		
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cscoCd", cscoCd);
		paramMap.put("startDtime", startCal.getTime());
		paramMap.put("endDtime", endCal.getTime());

		return sqlSession.selectList("ServiceDesk.selectRuleGroupPrntTrend_D", paramMap);
	}

	public List<HashMap<String, Object>> selectRuleGroupPrntTrend_M(int cscoCd, String sStartDay, String sEndDay) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date startDate = sdf.parse(sStartDay);
		Calendar startCal = getCalendar(startDate);
		
		Date endDate = sdf.parse(sEndDay);
		Calendar endCal = getCalendar(endDate);
		endCal.add(Calendar.DAY_OF_MONTH, 1);
		endCal.add(Calendar.SECOND, -1);
		
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cscoCd", cscoCd);
		paramMap.put("startDtime", startCal.getTime());
		paramMap.put("endDtime", endCal.getTime());

		return sqlSession.selectList("ServiceDesk.selectRuleGroupPrntTrend_M", paramMap);
	}

	public List<HashMap<String, Object>> selectViolAlrtTopN(int cscoCd, String sStartDay, String sEndDay, int nType, String sQuery) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date startDate = sdf.parse(sStartDay);
		Calendar startCal = getCalendar(startDate);
		
		Date endDate = sdf.parse(sEndDay);
		Calendar endCal = getCalendar(endDate);
		endCal.add(Calendar.DAY_OF_MONTH, 1);
		endCal.add(Calendar.SECOND, -1);
		
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		if (cscoCd > 0) {
			paramMap.put("cscoCd", cscoCd);
		}
		paramMap.put("startDtime", startCal.getTime());
		paramMap.put("endDtime", endCal.getTime());
		if (!StringUtil.isEmpty(sQuery)) {
			if (nType == 1) {
				paramMap.put("srcIp", sQuery);
			} else if (nType == 2) {
				paramMap.put("destIp", sQuery);
			} else if (nType == 3) {
				paramMap.put("srcGeo", sQuery);
			} else if (nType == 4) {
				paramMap.put("destPort", Integer.valueOf(sQuery));
			}
		}
		return sqlSession.selectList("ServiceDesk.selectViolAlrtTopN", paramMap);
	}
	
	public List<HashMap<String, Object>> selectRuleGroupCondition(int cscoCd, String sStartDay, String sEndDay, int nType, String sQuery, String prntCd, int nLimit) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date startDate = sdf.parse(sStartDay);
		Calendar startCal = getCalendar(startDate);
		
		Date endDate = sdf.parse(sEndDay);
		Calendar endCal = getCalendar(endDate);
		endCal.add(Calendar.DAY_OF_MONTH, 1);
		endCal.add(Calendar.SECOND, -1);
		
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		if (cscoCd > 0) {
			paramMap.put("cscoCd", cscoCd);
		}
		paramMap.put("startDtime", startCal.getTime());
		paramMap.put("endDtime", endCal.getTime());
		if (!StringUtil.isEmpty(sQuery) && nType == 1) {
			paramMap.put("srcIp", sQuery);
		} else if (!StringUtil.isEmpty(sQuery) && nType == 2) {
			paramMap.put("destIp", sQuery);
		} else if (!StringUtil.isEmpty(sQuery) && nType == 3) {
			paramMap.put("srcGeo", sQuery);
		} else if (!StringUtil.isEmpty(sQuery) && nType == 4) {
			paramMap.put("destPort", Integer.valueOf(sQuery));
		}
		paramMap.put("prntCd", prntCd);
		if (nLimit > 0) {
			paramMap.put("limit", nLimit);
		}

		return sqlSession.selectList("ServiceDesk.selectRuleGroupCondition", paramMap);
	}
	
	public List<HashMap<String, Object>> selectRuleGroupDetail(int cscoCd, String sStartDay, String sEndDay) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date startDate = sdf.parse(sStartDay);
		Calendar startCal = getCalendar(startDate);
		
		Date endDate = sdf.parse(sEndDay);
		Calendar endCal = getCalendar(endDate);
		endCal.add(Calendar.DAY_OF_MONTH, 1);
		endCal.add(Calendar.SECOND, -1);
		
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cscoCd", cscoCd);
		paramMap.put("startDtime", startCal.getTime());
		paramMap.put("endDtime", endCal.getTime());

		return sqlSession.selectList("ServiceDesk.selectRuleGroupDetail", paramMap);
	}
	
	public List<HashMap<String, Object>> selectAttackIpTopN(int cscoCd, String sStartDay, String sEndDay, boolean bSrcIp, int nLimit, String[] saIps) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date startDate = sdf.parse(sStartDay);
		Calendar startCal = getCalendar(startDate);
		
		Date endDate = sdf.parse(sEndDay);
		Calendar endCal = getCalendar(endDate);
		endCal.add(Calendar.DAY_OF_MONTH, 1);
		endCal.add(Calendar.SECOND, -1);
		
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		if (cscoCd > 0) {
			paramMap.put("cscoCd", cscoCd);
		}
		paramMap.put("startDtime", startCal.getTime());
		paramMap.put("endDtime", endCal.getTime());
		paramMap.put("bSrcIp", bSrcIp);
		if (saIps != null) {
			paramMap.put("ips", saIps);
		}
		paramMap.put("limit", nLimit);

		try {
			return sqlSession.selectList("ServiceDesk.selectAttackIpTopN", paramMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<HashMap<String, Object>>();
	}
	
	public List<HashMap<String, Object>> selectSrcIpTrend_D(int cscoCd, String sStartDay, String sEndDay, String[] saIps) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date startDate = sdf.parse(sStartDay);
		Calendar startCal = getCalendar(startDate);
		
		Date endDate = sdf.parse(sEndDay);
		Calendar endCal = getCalendar(endDate);
		endCal.add(Calendar.DAY_OF_MONTH, 1);
		endCal.add(Calendar.SECOND, -1);
		
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cscoCd", cscoCd);
		paramMap.put("startDtime", startCal.getTime());
		paramMap.put("endDtime", endCal.getTime());
		if (saIps != null) {
			paramMap.put("ips", saIps);
		}

		try {
			return sqlSession.selectList("ServiceDesk.selectSrcIpTrend_D", paramMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<HashMap<String, Object>>(); 
	}

	public List<HashMap<String, Object>> selectSrcIpTrend_M(int cscoCd, String sStartDay, String sEndDay, String[] saIps) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date startDate = sdf.parse(sStartDay);
		Calendar startCal = getCalendar(startDate);
		
		Date endDate = sdf.parse(sEndDay);
		Calendar endCal = getCalendar(endDate);
		endCal.add(Calendar.DAY_OF_MONTH, 1);
		endCal.add(Calendar.SECOND, -1);
		
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cscoCd", cscoCd);
		paramMap.put("startDtime", startCal.getTime());
		paramMap.put("endDtime", endCal.getTime());
		if (saIps != null) {
			paramMap.put("ips", saIps);
		}

		try {
			return sqlSession.selectList("ServiceDesk.selectSrcIpTrend_M", paramMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<HashMap<String, Object>>(); 
	}
	
	public List<HashMap<String, Object>> selectAttackGeoTopN(int cscoCd, String sStartDay, String sEndDay, String[] saGeos, int nLimit) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date startDate = sdf.parse(sStartDay);
		Calendar startCal = getCalendar(startDate);
		
		Date endDate = sdf.parse(sEndDay);
		Calendar endCal = getCalendar(endDate);
		endCal.add(Calendar.DAY_OF_MONTH, 1);
		endCal.add(Calendar.SECOND, -1);
		
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		if (cscoCd > 0) {
			paramMap.put("cscoCd", cscoCd);
		}
		paramMap.put("startDtime", startCal.getTime());
		paramMap.put("endDtime", endCal.getTime());
		if (saGeos != null) {
			paramMap.put("geos", saGeos);
		}
		if (nLimit > 0) {
			paramMap.put("limit", nLimit);
		}
		try {
			return sqlSession.selectList("ServiceDesk.selectAttackGeoTopN", paramMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<HashMap<String, Object>>(); 
	}

	public List<HashMap<String, Object>> selectSrcGeoTrend_D(int cscoCd, String sStartDay, String sEndDay, String[] saGeos) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date startDate = sdf.parse(sStartDay);
		Calendar startCal = getCalendar(startDate);
		
		Date endDate = sdf.parse(sEndDay);
		Calendar endCal = getCalendar(endDate);
		endCal.add(Calendar.DAY_OF_MONTH, 1);
		endCal.add(Calendar.SECOND, -1);
		
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cscoCd", cscoCd);
		paramMap.put("startDtime", startCal.getTime());
		paramMap.put("endDtime", endCal.getTime());
		if (saGeos != null) {
			paramMap.put("geos", saGeos);
		}

		try {
			return sqlSession.selectList("ServiceDesk.selectSrcGeoTrend_D", paramMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<HashMap<String, Object>>(); 
	}

	public List<HashMap<String, Object>> selectSrcGeoTrend_M(int cscoCd, String sStartDay, String sEndDay, String[] saGeos) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date startDate = sdf.parse(sStartDay);
		Calendar startCal = getCalendar(startDate);
		
		Date endDate = sdf.parse(sEndDay);
		Calendar endCal = getCalendar(endDate);
		endCal.add(Calendar.DAY_OF_MONTH, 1);
		endCal.add(Calendar.SECOND, -1);
		
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cscoCd", cscoCd);
		paramMap.put("startDtime", startCal.getTime());
		paramMap.put("endDtime", endCal.getTime());
		if (saGeos != null) {
			paramMap.put("geos", saGeos);
		}

		try {
			return sqlSession.selectList("ServiceDesk.selectSrcGeoTrend_M", paramMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<HashMap<String, Object>>(); 
	}
	
	public List<HashMap<String, Object>> selectSrcIpDetail(int cscoCd, String sStartDay, String sEndDay, String sDestIp, String prntCd) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date startDate = sdf.parse(sStartDay);
		Calendar startCal = getCalendar(startDate);
		
		Date endDate = sdf.parse(sEndDay);
		Calendar endCal = getCalendar(endDate);
		endCal.add(Calendar.DAY_OF_MONTH, 1);
		endCal.add(Calendar.SECOND, -1);
		
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cscoCd", cscoCd);
		paramMap.put("startDtime", startCal.getTime());
		paramMap.put("endDtime", endCal.getTime());
		paramMap.put("destIp", sDestIp);
		paramMap.put("prntCd", prntCd);
		try {
			return sqlSession.selectList("ServiceDesk.selectSrcIpDetail", paramMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<HashMap<String, Object>>(); 
	}
	
	public List<HashMap<String, Object>> selectCodeProDomainList(int cscoCd) throws Exception {
		
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cscoCd", cscoCd);
		try {
			return sqlSession.selectList("ServiceDesk.selectCodeProDomainList", paramMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<HashMap<String, Object>>(); 
	}

	public List<HashMap<String, Object>> selectEsmAssetAggregate(int cscoCd, String sStartDay, String sEndDay, int gearType) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date startDate = sdf.parse(sStartDay);
		Calendar startCal = getCalendar(startDate);
		
		Date endDate = sdf.parse(sEndDay);
		Calendar endCal = getCalendar(endDate);
		endCal.add(Calendar.DAY_OF_MONTH, 1);
		endCal.add(Calendar.SECOND, -1);
		
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cscoCd", cscoCd);
		paramMap.put("startDtime", startCal.getTime());
		paramMap.put("endDtime", endCal.getTime());
		paramMap.put("gearType", gearType);
		try {
			return sqlSession.selectList("ServiceDesk.selectEsmAssetAggregate", paramMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<HashMap<String, Object>>(); 
	}
	
	public List<HashMap<String, Object>> selectGearTypeDetail(int cscoCd, String sStartDay, String sEndDay, int gearType) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date startDate = sdf.parse(sStartDay);
		Calendar startCal = getCalendar(startDate);
		
		Date endDate = sdf.parse(sEndDay);
		Calendar endCal = getCalendar(endDate);
		endCal.add(Calendar.DAY_OF_MONTH, 1);
		endCal.add(Calendar.SECOND, -1);
		
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cscoCd", cscoCd);
		paramMap.put("startDtime", startCal.getTime());
		paramMap.put("endDtime", endCal.getTime());
		paramMap.put("gearType", gearType);
		try {
			return sqlSession.selectList("ServiceDesk.selectGearTypeDetail", paramMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<HashMap<String, Object>>(); 
	}

	public List<HashMap<String, Object>> selectTotalTrendTopN(String sStartDay, String sEndDay, int nSct, int nLimit) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date startDate = sdf.parse(sStartDay);
		Calendar startCal = getCalendar(startDate);
		
		Date endDate = sdf.parse(sEndDay);
		Calendar endCal = getCalendar(endDate);
		endCal.add(Calendar.DAY_OF_MONTH, 1);
		endCal.add(Calendar.SECOND, -1);
		
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startDtime", startCal.getTime());
		paramMap.put("endDtime", endCal.getTime());
		if (nSct == 1) {
			paramMap.put("prntCd", nSct);
		} else if (nSct == 2) {
			paramMap.put("srcIp", nSct);
		} else if (nSct == 3) {
			paramMap.put("destPort", nSct);
		}
		if (nLimit > 0) {
			paramMap.put("limit", nLimit);
		}
		
		try {
			return sqlSession.selectList("ServiceDesk.selectTotalTrendTopN", paramMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<HashMap<String, Object>>(); 
	}
	
	
	
	
	
	
	
	
	
	
	public Calendar getCalendar(Date date) {
		Calendar cal = new GregorianCalendar( TimeZone.getTimeZone( "GMT+09:00"), Locale.KOREA );
		cal.setTime(date);
		return cal;
	}
	
	public SqlSession getSqlSession() {
		return this.sqlSession;
	}

	public void setSqlSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}
	
}
