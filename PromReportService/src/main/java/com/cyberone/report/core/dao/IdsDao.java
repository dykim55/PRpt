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
import com.mongodb.DBObject;

public class IdsDao extends BaseDao {
	
	public IdsDao(DB promDB, DB reportDB) {
		super(promDB, reportDB);
	}

    /*
     * 전체탐지로그 해당일-시간대별 발생추이
     */
	public Iterable<DBObject> AllDetectLogTrend(String sCol, int nDirection, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		StartTimeCheck("AllDetectLogTrend");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("N_IDS_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 4);
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			if (nDirection > -1) { fields.put("direction", "$_id.direction"); }
			fields.put("year", "$_id.year");
			switch (sCol) {
				case "HR" : fields.put("hour", "$_id.hour");
				case "DY" : fields.put("day", "$_id.day");
				case "MON" : fields.put("month", "$_id.month");
			}
			fields.put("count", 1);

			BasicDBObject keys = new BasicDBObject();
			if (nDirection > -1) { keys.put("direction", "$direction"); }
			keys.put("year", "$year");
			switch (sCol) {
				case "HR" : keys.put("hour", "$hour");
				case "DY" : keys.put("day", "$day");
				case "MON" : keys.put("month", "$month");
			}
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			
			DBObject sortFields = new BasicDBObject();
			if (nDirection > -1) { sortFields.put("direction", -1); }
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
		} finally {
			EndTimeCheck();
		}
	}
	
    /*
     * 전체/허용/차단 - 시간대별 추이
     */
	public Iterable<DBObject> AllActionTrend(String sCol, int nDirection, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		StartTimeCheck("AllActionTrend");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("N_IDS_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 4);
			if (nDirection > -1) {
				condition.put("direction", nDirection);
			}
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("year", "$_id.year");
			switch (sCol) {
				case "HR" : fields.put("hour", "$_id.hour");
				case "DY" : fields.put("day", "$_id.day");
				case "MON" : fields.put("month", "$_id.month");
			}
			fields.put("count", 1);

			BasicDBObject keys = new BasicDBObject();
			keys.put("year", "$year");
			switch (sCol) {
				case "HR" : keys.put("hour", "$hour");
				case "DY" : keys.put("day", "$day");
				case "MON" : keys.put("month", "$month");
			}
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			
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
		} finally {
			EndTimeCheck();
		}
	}
	
	public Iterable<DBObject> EventTopN(String sCol, int nDirection, int assetCode, String sStartDay, String sEndDay, int nLimit) throws Exception {
		
		StartTimeCheck("EventTopN");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("N_IDS_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 5);
			condition.put("direction", nDirection);
			condition.put("message", new BasicDBObject("$ne", null));
			
			BasicDBObject keys = new BasicDBObject();
			keys.put("message", "$message");
			
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("message", "$_id.message");
			fields.put("count", 1);
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("count", -1);

			List<DBObject> pipeline = new ArrayList<DBObject>();
			pipeline.add(new BasicDBObject("$match", condition));
			pipeline.add(new BasicDBObject("$group", groupFields));
			pipeline.add(new BasicDBObject("$project", fields));
			pipeline.add(new BasicDBObject("$sort", sortFields ));
			if (nLimit > 0) {
				pipeline.add(new BasicDBObject("$limit", nLimit));
			}
			
			AggregationOutput output = dbCollection.aggregate(pipeline);

			return output.results();

		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}
	
	public Iterable<DBObject> EventSrcIpTopN(String sCol, int nDirection, int assetCode, String sStartDay, String sEndDay, String sMessage, int nLimit) throws Exception {
		
		StartTimeCheck("EventSrcIpTopN");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);

			DBCollection dbCollection = reportDB.getCollection("N_IDS_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			condition.put("direction", nDirection);
			if (!StringUtil.isEmpty(sMessage)) {
				condition.put("message", sMessage);
			}
			
			BasicDBObject keys = new BasicDBObject();
			keys.put("srcIp", "$srcIp");
			keys.put("destIp", "$destIp");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("srcIp", "$_id.srcIp");
			fields.put("destIp", "$_id.destIp");
			fields.put("count", 1);
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("count", -1);

			List<DBObject> pipeline = new ArrayList<DBObject>();
			pipeline.add(new BasicDBObject("$match", condition));
			pipeline.add(new BasicDBObject("$group", groupFields));
			pipeline.add(new BasicDBObject("$project", fields));
			pipeline.add(new BasicDBObject("$sort", sortFields ));
			if (nLimit > 0) {
				pipeline.add(new BasicDBObject("$limit", nLimit));
			}
			
			AggregationOutput output = dbCollection.aggregate(pipeline);

			return output.results();
			
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}
	
	public Iterable<DBObject> EventTopNTrend(String sCol, int nDirection, int assetCode, String sStartDay, String sEndDay, String message) throws Exception {
		List<String> messages = new ArrayList<String>();
		messages.add(message);
		return EventTopNTrend(sCol, nDirection, assetCode, sStartDay, sEndDay, messages);
	}
	
	public Iterable<DBObject> EventTopNTrend(String sCol, int nDirection, int assetCode, String sStartDay, String sEndDay, List<String> messages) throws Exception {
		
		StartTimeCheck("EventTopNTrend");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("N_IDS_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 5);
			condition.put("direction", nDirection);
			condition.put("message", new BasicDBObject("$in", messages));
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("message", "$_id.message");
			fields.put("year", "$_id.year");
			switch (sCol) {
				case "HR" : fields.put("hour", "$_id.hour");
				case "DY" : fields.put("day", "$_id.day");
				case "MON" : fields.put("month", "$_id.month");
			}
			fields.put("count", 1);

			BasicDBObject keys = new BasicDBObject();
			keys.put("message", "$message");
			keys.put("year", "$year");
			switch (sCol) {
				case "HR" : keys.put("hour", "$hour");
				case "DY" : keys.put("day", "$day");
				case "MON" : keys.put("month", "$month");
			}
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("message", 1);
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
		} finally {
			EndTimeCheck();
		}
	}
	
    /*
     * 이벤트 TopN
     */
	public Iterable<DBObject> EventTopN(String sCol, int nDirection, int assetCode, String sStartDay, String sEndDay, List<String> messages) throws Exception {
		
		StartTimeCheck("EventTopN");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("N_IDS_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 5);
			condition.put("direction", nDirection);
			
			if (messages != null) {
				ArrayList<BasicDBObject> andList = new ArrayList<BasicDBObject>();
				andList.add(new BasicDBObject("message", new BasicDBObject("$ne", null)));
				andList.add(new BasicDBObject("message", new BasicDBObject("$nin", messages)));
				condition.put("$and", andList);
			} else {
				condition.put("message", new BasicDBObject("$ne", null));
			}
			
			BasicDBObject keys = new BasicDBObject();
			keys.put("message", "$message");
			
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("message", "$_id.message");
			fields.put("count", 1);
			
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
     * 전체 세션로그 TOP N 
     */
	public Iterable<DBObject> IpTopN(String sCol, int nDirection, int assetCode, String sStartDay, String sEndDay, boolean bSrcIp, int nLimit) throws Exception {
		
		StartTimeCheck("IpTopN");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("N_IDS_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
		    condition.put("rptGubun", 5);
		    condition.put("direction", nDirection);
			
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
			} else {
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
			if (nLimit > 0) {
				pipeline.add(new BasicDBObject("$limit", nLimit));
			}
			
			AggregationOutput output = dbCollection.aggregate(pipeline);

			return output.results();
			
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}
	
	public Iterable<DBObject> BeforeIpCondition(String sCol, int nDirection, int assetCode, String sStartDay, String sEndDay, boolean bSrcIp, List<String> saIps) throws Exception {
		
		StartTimeCheck("BeforeIpCondition");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);

			DBCollection dbCollection = reportDB.getCollection("N_IDS_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 5);
			condition.put("direction", nDirection);
			
			if (saIps != null) {
				if (bSrcIp) {
					condition.put("srcIp", new BasicDBObject("$in", saIps));	
				} else {
					condition.put("destIp", new BasicDBObject("$in", saIps));
				}
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
	
	public Iterable<DBObject> IpTopNCondition(String sCol, int nDirection, int assetCode, String sStartDay, String sEndDay, String ip, boolean bSrcIp) throws Exception {
		
		StartTimeCheck("IpTopNCondition");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);

			DBCollection dbCollection = reportDB.getCollection("N_IDS_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			condition.put("direction", nDirection);
			
			if (bSrcIp) { 
				condition.put("srcIp", ip);
			} else {
				condition.put("destIp", ip);
			}

			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("message", "$_id.message");
			fields.put("destIp", "$_id.destIp");
			fields.put("srcIp", "$_id.srcIp");
			fields.put("action", "$_id.action");
			fields.put("count", 1);

			BasicDBObject keys = new BasicDBObject();
			keys.put("message", "$message");
			keys.put("destIp", "$destIp");
			keys.put("srcIp", "$srcIp");
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
	 * SRC IP TOP10 탐지로그 발생추이 - 시간별
	 */
	public Iterable<DBObject> IpTopNTrend(String sCol, int nDirection, int assetCode, String sStartDay, String sEndDay, List<String> ips, boolean bSrcIp) throws Exception {
		
		StartTimeCheck("IpTopNTrend");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);

			DBCollection dbCollection = reportDB.getCollection("N_IDS_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 5);
			condition.put("direction", nDirection);
			
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
			switch (sCol) {
				case "HR" : fields.put("hour", "$_id.hour");
				case "DY" : fields.put("day", "$_id.day");
				case "MON" : fields.put("month", "$_id.month");
			}
			fields.put("count", 1);
			
			BasicDBObject keys = new BasicDBObject();
			if (bSrcIp) {
				keys.put("srcIp", "$srcIp");
			} else {
				keys.put("destIp", "$destIp");
			}
			keys.put("year", "$year");
			switch (sCol) {
				case "HR" : keys.put("hour", "$hour");
				case "DY" : keys.put("day", "$day");
				case "MON" : keys.put("month", "$month");
			}
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			
			DBObject sortFields = new BasicDBObject();
			if (bSrcIp) {
				sortFields.put("srcIp", 1);
			} else {
				sortFields.put("destIp", 1);
			}
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
		} finally {
			EndTimeCheck();
		}
	}
	
	public Iterable<DBObject> ServiceDetectLog(String sCol, int nDirection, int assetCode, String sStartDay, String sEndDay, int nLimit) throws Exception {
		
		StartTimeCheck("ServiceDetectLog");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);

			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("N_IDS_" + assetCode + "_" + sCol);
			
			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
		    condition.put("rptGubun", 5); //카운트 기준
			condition.put("direction", nDirection);
			condition.put("destPort", new BasicDBObject("$ne", null));
			
			BasicDBObject keys = new BasicDBObject();
			keys.put("destPort", "$destPort");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("destPort", "$_id.destPort");
			fields.put("count", 1);
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("count", -1);

			List<DBObject> pipeline = new ArrayList<DBObject>();
			pipeline.add(new BasicDBObject("$match", condition));
			pipeline.add(new BasicDBObject("$group", groupFields));
			pipeline.add(new BasicDBObject("$project", fields));
			pipeline.add(new BasicDBObject("$sort", sortFields ));
			if (nLimit > 0) {
				pipeline.add(new BasicDBObject("$limit", nLimit));
			}
			
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
     * 서비스 TopN 이벤트 유형
     */
	public Iterable<DBObject> ServiceEventTopN(String sCol, int nDirection, int assetCode, String sStartDay, String sEndDay, int nPort) throws Exception {
		
		StartTimeCheck("ServiceEventTopN");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("N_IDS_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 9);
			condition.put("direction", nDirection);
			condition.put("destPort", nPort);
			
			BasicDBObject keys = new BasicDBObject();
			keys.put("message", "$message");
			
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("message", "$_id.message");
			fields.put("count", 1);

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
	
}
