package com.cyberone.report.core.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.cyberone.report.utils.StringUtil;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class BaseDao {

	private static final long h24 = 86400000;
	private static final int timezoneOffset = Calendar.getInstance()
			.getTimeZone().getRawOffset();

	private HashMap<Integer, String> portServiceMap;
	
	protected DB promDB;
	protected DB reportDB;
	
	private String checkMethodName;

	private long lLastTime;
	
	public BaseDao(DB promDB, DB reportDB) {
		this.promDB = promDB;
		this.reportDB = reportDB;
		
		//포트 서비스네임 조회
		portServiceMap = new HashMap<Integer, String>(); 
		DBCollection dbCollection = promDB.getCollection("PortServiceName");
		try (DBCursor dbCursor = dbCollection.find()) {
			for (DBObject obj : dbCursor) {
				portServiceMap.put((Integer)obj.get("port"), (String)obj.get("serviceName"));
			}
		}
	}
	
	public DBObject selectAutoReportForm(int formReportType, int assetCode) throws Exception {
		DBObject condition = new BasicDBObject();
		DBObject dbObj = null;
		DBCollection collection =  promDB.getCollection("AutoReportForm");
		try {
			condition.put("formReportType", formReportType);
			condition.put("appliedAssets.code", assetCode);
			dbObj = collection.findOne(condition);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
		return dbObj;
	}

	public int getStatMonthBaseDay(int assetCode) throws Exception {
		DBCollection policyCollection = promDB.getCollection("AssetPolicy");
		BasicDBObject searchPolicyQuery = new BasicDBObject("assetCode", assetCode);
		DBObject policy = policyCollection.findOne(searchPolicyQuery);
		DBObject basePolicy = (DBObject)policy.get("basePolicy");
	
		return basePolicy.containsField("statMonthBaseDay") ? Integer.valueOf(StringUtil.convertString(basePolicy.get("statMonthBaseDay"))) : 1;
	}

	public List<String> getPeriodMonth(String sEndDay, int nMonth, int sort) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMM");

		Calendar startCal = null; 
		Calendar endCal = null;
		if (nMonth > 0) {
			Date startDate = sdf.parse(sEndDay); 
			startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			endCal = getCalendar(endDate);
			endCal.add(Calendar.MONTH, nMonth + 1);
			endCal.add(Calendar.SECOND, -1);
		} else if (nMonth < 0) {
			Date startDate = sdf.parse(sEndDay);
			startCal = getCalendar(startDate);
			startCal.add(Calendar.MONTH, nMonth);
			
			Date endDate = sdf.parse(sEndDay);
			endCal = getCalendar(endDate);
			endCal.add(Calendar.MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
		}
		
		List<String> period = new ArrayList<String>();
		
		if (sort == -1) {
			while (endCal.after(startCal)) {
				period.add(sdf2.format(endCal.getTime()));
				endCal.add(Calendar.MONTH, -1);
			}
		} else if (sort == 1) {
			while (startCal.before(endCal)) {
				period.add(sdf2.format(startCal.getTime()));
				startCal.add(Calendar.MONTH, 1);
			}
		}
		
		return period;
	}

	public String lZero(int n) {
		return n < 10 ? "0" + n : String.valueOf(n);
	}
	
	public List<String> getPeriodHour(String sDay, int n) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
		
		Date startDate = sdf.parse(sDay);
		Calendar startCal = getCalendar(startDate);
		startCal.add(Calendar.DAY_OF_MONTH, n);
		
		Date endDate = sdf.parse(sDay);
		Calendar endCal = getCalendar(endDate);
		endCal.add(Calendar.DAY_OF_MONTH, 1);
		endCal.add(Calendar.SECOND, -1);
		
		List<String> period = new ArrayList<String>();
	
		while (startCal.before(endCal)) {
			period.add(sdf2.format(startCal.getTime()));
			startCal.add(Calendar.DAY_OF_MONTH, 1);
		}
		return period;
	}
	
	public List<String> getPeriodDay(String sStartDay, String sEndDay, int sort) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
		
		Date startDate = sdf.parse(sStartDay);
		Calendar startCal = getCalendar(startDate);
		
		Date endDate = sdf.parse(sEndDay);
		Calendar endCal = getCalendar(endDate);
		endCal.add(Calendar.DAY_OF_MONTH, 1);
		endCal.add(Calendar.SECOND, -1);
		
		List<String> period = new ArrayList<String>();
		
		if (sort == -1) {
			while (endCal.after(startCal)) {
				period.add(sdf2.format(endCal.getTime()));
				endCal.add(Calendar.DAY_OF_MONTH, -1);
			}
		} else if (sort == 1) {
			while (startCal.before(endCal)) {
				period.add(sdf2.format(startCal.getTime()));
				startCal.add(Calendar.DAY_OF_MONTH, 1);
			}
		}
		return period;
	}
	
	public List<String> getPeriodDay(String sStartDay, String sEndDay, int nMonth, int sort) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
		
		List<String> period = new ArrayList<String>();
		
		Calendar startCal = null; 
		Calendar endCal = null;
		if (nMonth >= 0) {
			Date startDate = sdf.parse(sStartDay); 
			startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			endCal = getCalendar(endDate);
			endCal.add(Calendar.MONTH, nMonth);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			if (sort == -1) {
				while (endCal.after(startCal)) {
					period.add(sdf2.format(endCal.getTime()));
					endCal.add(Calendar.DAY_OF_MONTH, -1);
				}
			} else if (sort == 1) {
				while (startCal.before(endCal)) {
					period.add(sdf2.format(startCal.getTime()));
					startCal.add(Calendar.DAY_OF_MONTH, 1);
				}
			}
		} else if (nMonth < 0) {
			Date startDate = sdf.parse(sStartDay);
			startCal = getCalendar(startDate);
			startCal.add(Calendar.MONTH, -1);
			
			Date endDate = sdf.parse(sEndDay);
			endCal = getCalendar(endDate);
			
			SimpleDateFormat sdfMM = new SimpleDateFormat("yyyyMM");
			
			if (sort == -1) {
			} else if (sort == 1) {
				while (Integer.valueOf(sdfMM.format(startCal.getTime())) < Integer.valueOf(sdfMM.format(endCal.getTime()))) {
					for (int i = startCal.get(Calendar.DAY_OF_MONTH); i <= 31; i++) {
						period.add(startCal.get(Calendar.YEAR) + lZero((startCal.get(Calendar.MONTH) + 1)) + lZero(i));
					}
					startCal.add(Calendar.MONTH, 1);
					for (int i = 1; i <= endCal.get(Calendar.DAY_OF_MONTH); i++) {
						period.add(startCal.get(Calendar.YEAR) + lZero((startCal.get(Calendar.MONTH) + 1)) + lZero(i));
					}
				}
			}
		}
		
		return period;
	}
	
	public String addDate(String sDate, int nDt) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dt = sdf.parse(sDate);
		Calendar cal = getCalendar(dt);
		cal.add(Calendar.DAY_OF_MONTH, nDt);
		return sdf.format(cal.getTime());
	}

	public String addMonth(String sDate, int nDt) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dt = sdf.parse(sDate);
		Calendar cal = getCalendar(dt);
		cal.add(Calendar.MONTH, nDt);
		return sdf.format(cal.getTime());
	}
	
	public void StartTimeCheck(String strMethodName) {
		checkMethodName = strMethodName;
		lLastTime = System.currentTimeMillis();
	}
	
	public void EndTimeCheck() {
		System.out.println("#### " + checkMethodName + " : " + (System.currentTimeMillis() - lLastTime));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//자산그룹의 자산리스트
	public List<DBObject> getGroupAssets(int groupCode) throws Exception {
		DBObject condition = new BasicDBObject();
		condition.put("groupCode", groupCode);
		condition.put("deleted", false);
		DBCollection collection =  promDB.getCollection("Asset");
		try (DBCursor dbCursor = collection.find(condition)) {
			return dbCursor != null ? dbCursor.toArray() : new ArrayList<DBObject>();		
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}
	
	//관제대상 장비현황
	public List<HashMap<String, Object>> getAssetList(String[] arrAssets) throws Exception {
		
		List<HashMap<String, Object>> assetList = new ArrayList<HashMap<String, Object>>();
		 
		SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		DBCollection dbCollection = promDB.getCollection("Product");
		BasicDBObject condition = new BasicDBObject();
		condition.put("deleted", false);
		
		HashMap<String, DBObject> productMap = new HashMap<String, DBObject>();
		try (DBCursor dbCursor = dbCollection.find(condition)) {
			for (DBObject obj : dbCursor) {
				productMap.put(((Integer)obj.get("productCode")).toString(), obj);
			}
		}
		
		dbCollection = promDB.getCollection("Asset");
		
		for (int i = 0; i < arrAssets.length; i++) {
			String[] saTemp = arrAssets[i].split("_");
			BasicDBObject searchQuery = new BasicDBObject("assetCode", Integer.parseInt(saTemp[0]));
			DBObject asset = dbCollection.findOne(searchQuery);
			
			DBCollection policyCollection = promDB.getCollection("AssetPolicy");
			BasicDBObject searchPolicyQuery = new BasicDBObject("assetCode", (Integer)asset.get("assetCode"));
			DBObject policy = policyCollection.findOne(searchPolicyQuery);
			
			DBObject basePolicy = (DBObject)policy.get("basePolicy");
			int baseDay = 1;
			if (basePolicy.containsField("statMonthBaseDay")) {
				baseDay = (Integer)basePolicy.get("statMonthBaseDay");
			}
			
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("_id", arrAssets[i]);
			map.put("assetCode", (Integer)asset.get("assetCode"));
			map.put("assetName", (String)asset.get("assetName"));
			map.put("assetIp", (String)asset.get("assetIp"));
			map.put("baseDay", baseDay);
			
			Date createTime = (Date)asset.get("createTime");
			String strCreateTime = dateTime.format(createTime);
			strCreateTime = strCreateTime.substring(0, 10);
			map.put("createTime", strCreateTime);

			DBObject productObj = productMap.get(((Integer)asset.get("productCode")).toString());
			map.put("productName", (String)productObj.get("productName") );
			map.put("productType", saTemp[1]);
			
			//DDOS Radware_DP_ODS/Sniper_DDX 구분
			if (saTemp[1].equals("DDOS")) {
				if ((Integer)asset.get("productCode") == 506126) {
					map.put("etc", "1");
				} else if ((Integer)asset.get("productCode") == 506119) {
					map.put("etc", "2");
				} else {
					map.put("etc", "2");
				}
			} else {
				map.put("etc", "0");
			}

			assetList.add(map);
		}

		Comparator<HashMap<String, Object>> comparator = new Comparator<HashMap<String, Object>>() {
			@Override
			public int compare(HashMap<String, Object> m1, HashMap<String, Object> m2) {
				String o1 = (String)m1.get("productType");
				String o2 = (String)m2.get("productType");
				if (o1 == null || o2 == null) {
					return 0;
				}
				return o1.compareTo(o2);
			}
		};

		Collections.sort(assetList, comparator);
		
		return assetList;
	}
	
	//보안관제 실적 현황
	public DBObject getDetectResult(String strPrefix, String strDateType, int assetCode, String sEndDay) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

		Date tmpDate = sdf.parse(sEndDay);
		Calendar tmpCal = getCalendar(tmpDate);		
		
		DBCollection dbCollection = reportDB.getCollection(strPrefix + "_" + assetCode + "_" + strDateType);
		BasicDBObject condition = new BasicDBObject();
		condition.put("rptGubun", 1);
		condition.put("year", tmpCal.get(Calendar.YEAR));
		condition.put("month", tmpCal.get(Calendar.MONTH) + 1);
		
		try (DBCursor dbCursor = dbCollection.find(condition)) {
			List<DBObject> tmpList = dbCursor.toArray();
			return tmpList.size() > 0 ? tmpList.get(0) : null;
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	//월간 침해위협 탐지보고 내역
	public List<HashMap<String, Object>> getDetectResultList(List<Integer> assetCodes, String sStartDay, String sEndDay) throws Exception {

		List<HashMap<String, Object>> resultList = new ArrayList<HashMap<String, Object>>();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			for (int i = 0; i < assetCodes.size(); i++) {
				StartTimeCheck("getDetectResultList(" + assetCodes.get(i) + ")");
				
				DBCollection dbCollection = promDB.getCollection("EventAnnotation");

				BasicDBObject condition = new BasicDBObject();
				condition.put("createTime", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
				condition.put("assetCode", assetCodes.get(i));
				condition.put("logCode", 1);
				condition.put("status", 2);
				DBObject match = new BasicDBObject("$match", condition);

				DBObject project = new BasicDBObject();
				DBObject fields = new BasicDBObject();
				fields.put("createTime", 1);
				fields.put("assetName", 1);
				fields.put("ruleName", 1);
				fields.put("srcIp", 1);
				project.put("$project", fields);

				BasicDBObject keys = new BasicDBObject();
				keys.put("createTime", new BasicDBObject("$substr", Arrays.asList("$createTime", 0, 10)));
				keys.put("assetName", "$assetName");
				keys.put("ruleName", "$ruleName");
				keys.put("srcIp", "$srcIp");
				DBObject groupFields = new BasicDBObject( "_id", keys);
				groupFields.put("count", new BasicDBObject( "$sum", 1));
				DBObject group = new BasicDBObject("$group", groupFields);
				
				DBObject sortFields = new BasicDBObject();
				sortFields.put("_id.assetName", 1);
				sortFields.put("_id.createTime", 1);
			    DBObject sort = new BasicDBObject("$sort", sortFields );
				
				AggregationOutput output = dbCollection.aggregate(match, project, group, sort);

				Iterable<DBObject> results = output.results();
				for (DBObject dbObj : results) {
					@SuppressWarnings("unchecked")
					HashMap<String,Object> mapId = (HashMap<String,Object>)dbObj.get("_id");
					HashMap<String, Object> map = new HashMap<String, Object>();
				    map.put("no", resultList.size() + 1);
				    map.put("date", (String)mapId.get("createTime"));
				    map.put("assetName", (String)mapId.get("assetName"));
				    map.put("title", (String)mapId.get("ruleName"));
				    map.put("srcIp", (String)mapId.get("srcIp"));
				    map.put("count", ((Number)dbObj.get("count")).longValue());
				    resultList.add(map);
				}
				EndTimeCheck();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return resultList;
	}

	//월 성능정보
	public Iterable<DBObject> getPerformanceInfo(String sCol, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("N_SV_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 3);

			BasicDBObject keys = new BasicDBObject();
			keys.put("name", "$name");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("avg", new BasicDBObject( "$avg", "$avg"));
			groupFields.put("min", new BasicDBObject( "$avg", "$min"));
			groupFields.put("max", new BasicDBObject( "$avg", "$max"));
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("name", "$_id.name");
			fields.put("avg", 1);
			fields.put("min", 1);
			fields.put("max", 1);

			DBObject sortFields = new BasicDBObject();
			sortFields.put("name", 1);
			
			List<DBObject> pipeline = new ArrayList<DBObject>();
			pipeline.add(new BasicDBObject("$match", condition));
			pipeline.add(new BasicDBObject("$group", groupFields));
			pipeline.add(new BasicDBObject("$project", fields));
			pipeline.add(new BasicDBObject("$sort", sortFields ));
			
			AggregationOutput output = dbCollection.aggregate(pipeline);

			return output.results();
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public Iterable<DBObject> getPerformanceTrend(String sCol, int assetCode, String sStartDay, String sEndDay, String sField) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);

			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);

			DBCollection dbCollection = reportDB.getCollection("N_SV_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 3);
			condition.put("name", sField);
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("year", "$_id.year");
			switch (sCol) {
				case "HR" : fields.put("hour", "$_id.hour");
				case "DY" : fields.put("day", "$_id.day");
				case "MON" : fields.put("month", "$_id.month");
			}
			fields.put("avg", 1);
			fields.put("max", 1);
			
			BasicDBObject keys = new BasicDBObject();
			keys.put("year", "$year");
			switch (sCol) {
				case "HR" : keys.put("hour", "$hour");
				case "DY" : keys.put("day", "$day");
				case "MON" : keys.put("month", "$month");
			}
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("avg", new BasicDBObject( "$sum", "$avg"));
			groupFields.put("max", new BasicDBObject( "$sum", "$max"));
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("year", 1);
			switch (sCol) {
				case "HR" : sortFields.put("hour", 1);
				case "DY" : sortFields.put("day", 1);
				case "MON" : sortFields.put("month", 1);
			}
			
			List<DBObject> pipeline = new ArrayList<DBObject>();
			pipeline.add(new BasicDBObject("$match", condition));
			pipeline.add(new BasicDBObject("$group", groupFields));
			pipeline.add(new BasicDBObject("$project", fields));
			pipeline.add(new BasicDBObject("$sort", sortFields ));
			
			AggregationOutput output = dbCollection.aggregate(pipeline);

			return output.results();
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	
	public String getPortServiceName(int nPort) {
		String strServiceName = portServiceMap.get(nPort);
		return strServiceName != null ? strServiceName : String.valueOf(nPort);
	}	
	
	public Date getDay(Date date) {
		long time = date.getTime();
		time = time + timezoneOffset;
		time = time - (time % h24); // 24H
		time = time - timezoneOffset;
		date.setTime(time);
		return date;
	}

	public Calendar getCalendar(Date date) {
		Calendar cal = new GregorianCalendar( TimeZone.getTimeZone( "GMT+09:00"), Locale.KOREA );
		cal.setTime(date);
		return cal;
	}
	
	public String getLastDayOfMonth(String strMonth) throws Exception {
		String strLastDayOfMonth = "31";
		SimpleDateFormat day = new SimpleDateFormat("yyyy-MM");
		try {
			Date tmpDate = day.parse(strMonth);
			Calendar tmpCal = getCalendar(tmpDate);
			strLastDayOfMonth = String.valueOf(tmpCal.getActualMaximum(Calendar.DAY_OF_MONTH));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return strLastDayOfMonth;
	}
	
	public String getSearchPeriod(String sStartDay, String sEndDay) throws Exception {
		String strStartDate = "";
		String strEndDate = "";

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			
			strStartDate = startCal.get(Calendar.YEAR) + "년 " + lZero((startCal.get(Calendar.MONTH) + 1)) + "월 " + lZero(startCal.get(Calendar.DAY_OF_MONTH)) + "일";
			strEndDate = endCal.get(Calendar.YEAR) + "년 " + lZero((endCal.get(Calendar.MONTH) + 1)) + "월 " + lZero(endCal.get(Calendar.DAY_OF_MONTH)) + "일";
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return strStartDate + " ~ " + strEndDate;
	}

}
