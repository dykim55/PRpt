package com.cyberone.report.core.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.cyberone.report.utils.StringUtil;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class FwDao extends BaseDao {
	
	public FwDao(DB promDB, DB reportDB) {
		super(promDB, reportDB);
	}

	/*
	 * 차단 세션로그 서비스 TOP10 상세 
	 */
	public Iterable<DBObject> ServiceConditionDay(int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay, int port) throws Exception {
		
		StartTimeCheck("ServiceConditionDay");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);

			DBCollection dbCollection = reportDB.getCollection("FW_" + assetCode + "_DY");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			condition.put("direction", nDirection);
			condition.put("action", sAction);
			condition.put("destPort", port);

			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("destPort", "$_id.destPort");
			fields.put("srcIp", "$_id.srcIp");
			fields.put("destIp", "$_id.destIp");
			fields.put("count", 1);

			BasicDBObject keys = new BasicDBObject();
			keys.put("destPort", "$destPort");
			keys.put("srcIp", "$srcIp");
			keys.put("destIp", "$destIp");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));

			DBObject sortFields = new BasicDBObject();
			sortFields.put("count", -1);
		    
			List<DBObject> pipeline = new ArrayList<DBObject>();
			pipeline.add(new BasicDBObject("$match", condition));
			pipeline.add(new BasicDBObject("$group", groupFields));
			pipeline.add(new BasicDBObject("$project", fields));
			pipeline.add(new BasicDBObject("$sort", sortFields ));
			pipeline.add(new BasicDBObject("$limit", 5));
			
			AggregationOutput output = dbCollection.aggregate(pipeline);

			return output.results();
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}

	public Iterable<DBObject> ServiceConditionMonth(int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay, int port) throws Exception {
		
		StartTimeCheck("ServiceConditionMonth");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);

			DBCollection dbCollection = reportDB.getCollection("FW_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			condition.put("direction", nDirection);
			condition.put("action", sAction);
			condition.put("destPort", port);

			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("destPort", "$_id.destPort");
			fields.put("srcIp", "$_id.srcIp");
			fields.put("destIp", "$_id.destIp");
			fields.put("count", 1);

			BasicDBObject keys = new BasicDBObject();
			keys.put("destPort", "$destPort");
			keys.put("srcIp", "$srcIp");
			keys.put("destIp", "$destIp");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));

			DBObject sortFields = new BasicDBObject();
			sortFields.put("count", -1);
		    
			List<DBObject> pipeline = new ArrayList<DBObject>();
			pipeline.add(new BasicDBObject("$match", condition));
			pipeline.add(new BasicDBObject("$group", groupFields));
			pipeline.add(new BasicDBObject("$project", fields));
			pipeline.add(new BasicDBObject("$sort", sortFields ));
			pipeline.add(new BasicDBObject("$limit", 5));
			
			AggregationOutput output = dbCollection.aggregate(pipeline);

			return output.results();
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}
	
    /*
     * 서비스 TOP10 시간대별 발생추이
     */
	public  Iterable<DBObject> ServiceTopNHourTrend(int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay, List<Integer> ports) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("FW_" + assetCode + "_HR");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 5);
			if (nDirection > -1) condition.put("direction", nDirection);
			condition.put("action", sAction);
			condition.put("destPort", new BasicDBObject("$in", ports));
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("destPort", "$_id.destPort");
			fields.put("year", "$_id.year");
			fields.put("month", "$_id.month");
			fields.put("day", "$_id.day");
			fields.put("hour", "$_id.hour");
			fields.put("count", 1);

			BasicDBObject keys = new BasicDBObject();
			keys.put("destPort", "$destPort");
			keys.put("year", "$year");
			keys.put("month", "$month");
			keys.put("day", "$day");
			keys.put("hour", "$hour");
			
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("destPort", 1);
			sortFields.put("year", 1);
			sortFields.put("month", 1);
			sortFields.put("day", 1);
			sortFields.put("hour", 1);

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
		} finally {
		}
	}

    /*
     * 서비스 TOP10 일자별 발생추이
     */
	public  Iterable<DBObject> ServiceDayTopNTrend(int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay, List<Integer> ports) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("FW_" + assetCode + "_DY");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 5);
			if (nDirection > -1) condition.put("direction", nDirection);
			condition.put("action", sAction);
			condition.put("destPort", new BasicDBObject("$in", ports));
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("destPort", "$_id.destPort");
			fields.put("year", "$_id.year");
			fields.put("month", "$_id.month");
			fields.put("day", "$_id.day");
			fields.put("count", 1);

			BasicDBObject keys = new BasicDBObject();
			keys.put("destPort", "$destPort");
			keys.put("year", "$year");
			keys.put("month", "$month");
			keys.put("day", "$day");
			
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("destPort", 1);
			sortFields.put("year", 1);
			sortFields.put("month", 1);
			sortFields.put("day", 1);

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
		} finally {
		}
	}
	
	public List<DBObject> ServiceSessionLogDay(int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		StartTimeCheck("ServiceSessionLogDay");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);

			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("FW_" + assetCode + "_DY");
			
			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
		    condition.put("rptGubun", 5); //카운트 기준
			condition.put("direction", nDirection);
			condition.put("action", sAction);
			condition.put("destPort", new BasicDBObject("$ne", null));
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("count", -1);

			DBCursor dbCursor = dbCollection.find(condition).sort(sortFields);

			return dbCursor.toArray();
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}
	
	public Iterable<DBObject> ServiceSessionLogPeriod(int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		StartTimeCheck("ServiceSessionLogPeriod");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);

			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("FW_" + assetCode + "_DY");
			
			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
		    condition.put("rptGubun", 5); //카운트 기준
			condition.put("direction", nDirection);
			condition.put("action", sAction);
			condition.put("destPort", new BasicDBObject("$ne", null));
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("destPort", "$_id.destPort");
			fields.put("count", 1);

			BasicDBObject keys = new BasicDBObject();
			keys.put("destPort", "$destPort");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("count", -1);

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
		} finally {
			EndTimeCheck();
		}
	}

	public Iterable<DBObject> ServiceSessionLogMonth(int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		StartTimeCheck("ServiceSessionLogMonth");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);

			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("FW_" + assetCode + "_MON");
			
			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
		    condition.put("rptGubun", 5); //카운트 기준
			condition.put("direction", nDirection);
			condition.put("action", sAction);
			condition.put("destPort", new BasicDBObject("$ne", null));
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("destPort", "$_id.destPort");
			fields.put("count", 1);

			BasicDBObject keys = new BasicDBObject();
			keys.put("destPort", "$destPort");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("count", -1);

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
		} finally {
			EndTimeCheck();
		}
	}
	
	/*
	 * SRC IP TOP10 세션 로그 발생추이 - 시간별
	 */
	public Iterable<DBObject> IpHourTopNTrend(int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay, List<String> ips, boolean bSrcIp) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);

			DBCollection dbCollection = reportDB.getCollection("FW_" + assetCode + "_HR");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			condition.put("direction", nDirection);
			condition.put("action", sAction);
			if (bSrcIp) { 
				condition.put("srcIp", new BasicDBObject("$in", ips));
			} else {
				condition.put("destIp", new BasicDBObject("$in", ips));
			}
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			if (bSrcIp) {
				fields.put("srcIp", "$_id.srcIp");
			} else {
				fields.put("destIp", "$_id.destIp");
			}
			fields.put("year", "$_id.year");
			fields.put("month", "$_id.month");
			fields.put("day", "$_id.day");
			fields.put("hour", "$_id.hour");
			fields.put("count", 1);
			
			BasicDBObject keys = new BasicDBObject();
			if (bSrcIp) {
				keys.put("srcIp", "$srcIp");
			} else {
				keys.put("destIp", "$destIp");
			}
			keys.put("year", "$year");
			keys.put("month", "$month");
			keys.put("day", "$day");
			keys.put("hour", "$hour");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			
			DBObject sortFields = new BasicDBObject();
			if (bSrcIp) {
				sortFields.put("srcIp", 1);
			} else {
				sortFields.put("destIp", 1);
			}
			sortFields.put("year", 1);
			sortFields.put("month", 1);
			sortFields.put("day", 1);
			sortFields.put("hour", 1);
			sortFields.put("count", -1);

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
		} finally {
		}
	}

	/*
	 * SRC IP TOP10 세션 로그 발생추이 - 일자별
	 */
	public Iterable<DBObject> IpDayTopNTrend(int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay, boolean bSrcIp, List<String> ips) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);

			DBCollection dbCollection = reportDB.getCollection("FW_" + assetCode + "_DY");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			condition.put("direction", nDirection);
			condition.put("action", sAction);
			if (bSrcIp) { 
				condition.put("srcIp", new BasicDBObject("$in", ips));
			} else {
				condition.put("destIp", new BasicDBObject("$in", ips));
			}
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			if (bSrcIp) {
				fields.put("srcIp", "$_id.srcIp");
			} else {
				fields.put("destIp", "$_id.destIp");
			}
			fields.put("year", "$_id.year");
			fields.put("month", "$_id.month");
			fields.put("day", "$_id.day");
			fields.put("count", 1);
			
			BasicDBObject keys = new BasicDBObject();
			if (bSrcIp) {
				keys.put("srcIp", "$srcIp");
			} else {
				keys.put("destIp", "$destIp");
			}
			keys.put("year", "$year");
			keys.put("month", "$month");
			keys.put("day", "$day");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			
			DBObject sortFields = new BasicDBObject();
			if (bSrcIp) {
				sortFields.put("srcIp", 1);
			} else {
				sortFields.put("destIp", 1);
			}
			sortFields.put("year", 1);
			sortFields.put("month", 1);
			sortFields.put("day", 1);
			sortFields.put("count", -1);

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
		} finally {
		}
	}
	
    /*
     * 증감현황 조회
     */
	public Iterable<DBObject> IpSessionLogCount(int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay, boolean bSrcIp, String sIp) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("FW_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
		    condition.put("rptGubun", 5);
		    
		    if (nDirection > -1) {
		    	condition.put("direction", nDirection);
		    }
		    
			if (!StringUtil.isEmpty(sAction)) {
				condition.put("action", sAction);;
			}
			
			if (bSrcIp) {
				condition.put("srcIp", sIp);
			} else {
				condition.put("destIp", sIp);
			}

			BasicDBObject keys = new BasicDBObject();
			if (bSrcIp) {
				keys.put("srcIp", "$srcIp");
			} else {
				keys.put("destIp", "$destIp");
			}
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			if (bSrcIp) {
				fields.put("srcIp", "$_id.srcIp");
			}else {
				fields.put("destIp", "$_id.destIp");
			}
			fields.put("count", 1);
			
			List<DBObject> pipeline = new ArrayList<DBObject>();
			pipeline.add(new BasicDBObject("$match", condition));
			pipeline.add(new BasicDBObject("$group", groupFields));
			pipeline.add(new BasicDBObject("$project", fields));
			
			AggregationOutput output = dbCollection.aggregate(pipeline);
			
			return output.results();
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}
	
	public Iterable<DBObject> BeforeIpDayCondition(int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay, boolean bSrcIp, String sIp) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);

			DBCollection dbCollection = reportDB.getCollection("FW_" + assetCode + "_DY");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 5);
			condition.put("direction", nDirection);
			if (!StringUtil.isEmpty(sAction)) condition.put("action", sAction);
			if (bSrcIp) { 
				condition.put("srcIp", sIp);
			} else {
				condition.put("destIp", sIp);
			}

			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			if (bSrcIp) {
				fields.put("srcIp", "$_id.srcIp");
			} else {
				fields.put("destIp", "$_id.destIp");
			}
			fields.put("count", 1);

			BasicDBObject keys = new BasicDBObject();
			if (bSrcIp) {
				keys.put("srcIp", "$srcIp");
			} else {
				keys.put("destIp", "$destIp");				
			}
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));

			DBObject sortFields = new BasicDBObject("count", -1);
		    
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
		} finally {
			EndTimeCheck();
		}
	}
	
    /*
     * 전체 세션로그 TOP N 
     */
	public List<DBObject> IpDayTopN(int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay, boolean bSrcIp, int nLimit) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("FW_" + assetCode + "_DY");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
		    condition.put("rptGubun", 5);
		    if (nDirection > -1) condition.put("direction", nDirection);
			if (!StringUtil.isEmpty(sAction)) condition.put("action", sAction);
			
			ArrayList<BasicDBObject> andList = new ArrayList<BasicDBObject>();
			if (bSrcIp) {
				andList.add(new BasicDBObject("srcIp", new BasicDBObject("$ne", null)));
				andList.add(new BasicDBObject("srcIp", new BasicDBObject("$ne", "-1")));
			} else {
				andList.add(new BasicDBObject("destIp", new BasicDBObject("$ne", null)));
				andList.add(new BasicDBObject("destIp", new BasicDBObject("$ne", "-1")));
			}
			condition.put("$and", andList);
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("count", -1);
			
			DBCursor dbCursor = dbCollection.find(condition).sort(sortFields).limit(nLimit);

			return dbCursor.toArray();
			
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}

	public Iterable<DBObject> IpPeriodTopN(int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay, boolean bSrcIp, int nLimit) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("FW_" + assetCode + "_DY");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
		    condition.put("rptGubun", 5); //카운트 기준
			condition.put("direction", nDirection);
			if (!StringUtil.isEmpty(sAction)) condition.put("action", sAction);;
			
			ArrayList<BasicDBObject> andList = new ArrayList<BasicDBObject>();
			if (bSrcIp) {
				andList.add(new BasicDBObject("srcIp", new BasicDBObject("$ne", null)));
				andList.add(new BasicDBObject("srcIp", new BasicDBObject("$ne", "-1")));
			} else {
				andList.add(new BasicDBObject("destIp", new BasicDBObject("$ne", null)));
				andList.add(new BasicDBObject("destIp", new BasicDBObject("$ne", "-1")));
			}
			condition.put("$and", andList);
						
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("srcIp", "$_id.srcIp");
			fields.put("destIp", "$_id.destIp");
			fields.put("count", 1);

			BasicDBObject keys = new BasicDBObject();
			keys.put("srcIp", "$srcIp");
			keys.put("destIp", "$destIp");

			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("count", -1);
			
			List<DBObject> pipeline = new ArrayList<DBObject>();
			pipeline.add(new BasicDBObject("$match", condition));
			pipeline.add(new BasicDBObject("$group", groupFields));
			pipeline.add(new BasicDBObject("$project", fields));
			pipeline.add(new BasicDBObject("$sort", sortFields ));
			pipeline.add(new BasicDBObject("$limit", nLimit));
			
			AggregationOutput output = dbCollection.aggregate(pipeline);
			
			return output.results();
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}
	
	public Iterable<DBObject> IpMonthTopN(int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay, boolean bSrcIp, int nLimit) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);
			
			DBCollection dbCollection = reportDB.getCollection("FW_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
		    condition.put("rptGubun", 5);
		    
		    if (nDirection > -1) {
		    	condition.put("direction", nDirection);
		    }
		    
			if (!StringUtil.isEmpty(sAction)) {
				condition.put("action", sAction);;
			}
			
			ArrayList<BasicDBObject> andList = new ArrayList<BasicDBObject>();
			if (bSrcIp) {
				andList.add(new BasicDBObject("srcIp", new BasicDBObject("$ne", null)));
				andList.add(new BasicDBObject("srcIp", new BasicDBObject("$ne", "-1")));
			} else {
				andList.add(new BasicDBObject("destIp", new BasicDBObject("$ne", null)));
				andList.add(new BasicDBObject("destIp", new BasicDBObject("$ne", "-1")));
			}
			condition.put("$and", andList);

			BasicDBObject keys = new BasicDBObject();
			if (bSrcIp) {
				keys.put("srcIp", "$srcIp");
			} else {
				keys.put("destIp", "$destIp");
			}
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			if (bSrcIp) {
				fields.put("srcIp", "$_id.srcIp");
			}else {
				fields.put("destIp", "$_id.destIp");
			}
			fields.put("count", 1);
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("count", -1);
			
			List<DBObject> pipeline = new ArrayList<DBObject>();
			pipeline.add(new BasicDBObject("$match", condition));
			pipeline.add(new BasicDBObject("$group", groupFields));
			pipeline.add(new BasicDBObject("$project", fields));
			pipeline.add(new BasicDBObject("$sort", sortFields ));
			pipeline.add(new BasicDBObject("$limit", nLimit));
			
			AggregationOutput output = dbCollection.aggregate(pipeline);
			
			return output.results();
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}
	
	public Iterable<DBObject> IpDayTopNCondition(int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay, String ip, boolean bSrcIp) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);

			DBCollection dbCollection = reportDB.getCollection("FW_" + assetCode + "_DY");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			condition.put("direction", nDirection);
			
			if (!StringUtil.isEmpty(sAction)) {
				condition.put("action", sAction);
			}
			
			if (bSrcIp) { 
				condition.put("srcIp", ip);
			} else {
				condition.put("destIp", ip);
			}

			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("destIp", "$_id.destIp");
			fields.put("srcIp", "$_id.srcIp");
			fields.put("destPort", "$_id.destPort");
			fields.put("action", "$_id.action");
			fields.put("count", 1);

			BasicDBObject keys = new BasicDBObject();
			keys.put("destIp", "$destIp");
			keys.put("srcIp", "$srcIp");
			keys.put("destPort", "$destPort");
			keys.put("action", "$action");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));

			DBObject sortFields = new BasicDBObject("count", -1);
		    
			List<DBObject> pipeline = new ArrayList<DBObject>();
			pipeline.add(new BasicDBObject("$match", condition));
			pipeline.add(new BasicDBObject("$group", groupFields));
			pipeline.add(new BasicDBObject("$project", fields));
			pipeline.add(new BasicDBObject("$sort", sortFields ));
			pipeline.add(new BasicDBObject("$limit", 5));
			
			AggregationOutput output = dbCollection.aggregate(pipeline);
		    
			return output.results();
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}
	
	public Iterable<DBObject> IpMonthTopNCondition(int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay, boolean bSrcIp, String sIp) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);

			DBCollection dbCollection = reportDB.getCollection("FW_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			condition.put("direction", nDirection);
			
			if (!StringUtil.isEmpty(sAction)) {
				condition.put("action", sAction);
			}
			
			if (bSrcIp) { 
				condition.put("srcIp", sIp);
			} else {
				condition.put("destIp", sIp);
			}

			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("destIp", "$_id.destIp");
			fields.put("srcIp", "$_id.srcIp");
			fields.put("destPort", "$_id.destPort");
			fields.put("action", "$_id.action");
			fields.put("count", 1);

			BasicDBObject keys = new BasicDBObject();
			keys.put("destIp", "$destIp");
			keys.put("srcIp", "$srcIp");
			keys.put("destPort", "$destPort");
			keys.put("action", "$action");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));

			DBObject sortFields = new BasicDBObject("count", -1);
		    
			List<DBObject> pipeline = new ArrayList<DBObject>();
			pipeline.add(new BasicDBObject("$match", condition));
			pipeline.add(new BasicDBObject("$group", groupFields));
			pipeline.add(new BasicDBObject("$project", fields));
			pipeline.add(new BasicDBObject("$sort", sortFields ));
			pipeline.add(new BasicDBObject("$limit", 5));
			
			AggregationOutput output = dbCollection.aggregate(pipeline);
		    
			return output.results();
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}
	
    /*
     * 전체 허용/차단로그 발생추이 전일/해당일 시간대별 비교
     */
	public  Iterable<DBObject> CompareSessionLogHourTrend(int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("FW_" + assetCode + "_HR");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 4);
			
			if (nDirection > -1) condition.put("direction", nDirection);
			
			condition.put("action", sAction);
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("year", "$_id.year");
			fields.put("month", "$_id.month");
			fields.put("day", "$_id.day");
			fields.put("hour", "$_id.hour");
			fields.put("count", 1);

			BasicDBObject keys = new BasicDBObject();
			keys.put("year", "$year");
			keys.put("month", "$month");
			keys.put("day", "$day");
			keys.put("hour", "$hour");
			
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("year", 1);
			sortFields.put("month", 1);
			sortFields.put("day", 1);
			sortFields.put("hour", 1);

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
		} finally {
		}
	}

    /*
     * 전체 허용/차단로그 발생추이 일별 비교
     */
	public  Iterable<DBObject> CompareSessionLogDayTrend(int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("FW_" + assetCode + "_DY");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 4);
			
			if (nDirection > -1) {
				condition.put("direction", nDirection);
			}
			
			condition.put("action", sAction);
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("year", "$_id.year");
			fields.put("month", "$_id.month");
			fields.put("day", "$_id.day");
			fields.put("count", 1);

			BasicDBObject keys = new BasicDBObject();
			keys.put("year", "$year");
			keys.put("month", "$month");
			keys.put("day", "$day");
			
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("year", 1);
			sortFields.put("month", 1);
			sortFields.put("day", 1);

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
		} finally {
		}
	}
	
    /*
     * 전체세션로그 해당일-시간대별 발생추이
     */
	public  Iterable<DBObject> SessionLogHourTrend(int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay, int nPort) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("FW_" + assetCode + "_HR");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			
			if (nDirection > -1) {
				condition.put("direction", nDirection);
			}
			
			if (nPort > 0) { 
				condition.put("rptGubun", 5); 
				condition.put("action", "1");
				condition.put("destPort", nPort);
			} else {
				condition.put("rptGubun", 4);
			}
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			if (!StringUtil.isEmpty(sAction)) { fields.put("action", "$_id.action"); }
			fields.put("year", "$_id.year");
			fields.put("month", "$_id.month");
			fields.put("day", "$_id.day");
			fields.put("hour", "$_id.hour");
			fields.put("count", 1);

			BasicDBObject keys = new BasicDBObject();
			if (!StringUtil.isEmpty(sAction)) { keys.put("action", "$action"); }
			keys.put("year", "$year");
			keys.put("month", "$month");
			keys.put("day", "$day");
			keys.put("hour", "$hour");
			
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			
			DBObject sortFields = new BasicDBObject();
			if (!StringUtil.isEmpty(sAction)) { sortFields.put("action", -1); }
			sortFields.put("year", 1);
			sortFields.put("month", 1);
			sortFields.put("day", 1);
			sortFields.put("hour", 1);

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
		} finally {
		}
	}

    /*
     * 전체세션로그 일별 발생추이
     */
	public Iterable<DBObject> SessionLogDayTrend(int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay, int nPort) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("FW_" + assetCode + "_DY");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			
			if (nDirection > -1) condition.put("direction", nDirection);
			
			if (nPort > 0) { 
				condition.put("rptGubun", 5); 
				condition.put("action", "1");
				condition.put("destPort", nPort);
			} else {
				condition.put("rptGubun", 4);
			}
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			if (!StringUtil.isEmpty(sAction)) { fields.put("action", "$_id.action"); }
			fields.put("year", "$_id.year");
			fields.put("month", "$_id.month");
			fields.put("day", "$_id.day");
			fields.put("count", 1);

			BasicDBObject keys = new BasicDBObject();
			if (!StringUtil.isEmpty(sAction)) { keys.put("action", "$action"); }
			keys.put("year", "$year");
			keys.put("month", "$month");
			keys.put("day", "$day");
			
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			
			DBObject sortFields = new BasicDBObject();
			if (!StringUtil.isEmpty(sAction)) { sortFields.put("action", -1); }
			sortFields.put("year", 1);
			sortFields.put("month", 1);
			sortFields.put("day", 1);

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
		} finally {
		}
	}
	
    /*
     * 전체세션로그 월별 발생추이
     */
	public Iterable<DBObject> SessionLogMonthTrend(int nDirection, String sAction, int assetCode, String sStartDay, String sEndDay, int nPort) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay); 
			Calendar startCal = getCalendar(startDate);
			startCal.add(Calendar.MONTH, -5);

			DBCollection dbCollection = reportDB.getCollection("FW_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("direction", nDirection);
			if (nPort > 0) {
				condition.put("rptGubun", 5);
				condition.put("destPort", nPort);
				condition.put("action", "1");
			} else {
				condition.put("rptGubun", 4);
			}
			
			BasicDBObject keys = new BasicDBObject();
			if (!StringUtil.isEmpty(sAction)) { keys.put("action", "$action"); }
			keys.put("year", "$year");
			keys.put("month", "$month");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));

			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			if (!StringUtil.isEmpty(sAction)) { fields.put("action", "$_id.action"); }
			fields.put("year", "$_id.year");
			fields.put("month", "$_id.month");
			fields.put("count", 1);
			
			DBObject sortFields = new BasicDBObject();
			if (!StringUtil.isEmpty(sAction)) { sortFields.put("action", -1); }
			sortFields.put("year", 1);
			sortFields.put("month", 1);

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
		} finally {
		}
	}
	
}
