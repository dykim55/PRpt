package com.cyberone.report.core.dao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class WAFReportDao extends BaseDao {

	public WAFReportDao(DB promDb, DB rptDb)	{
		super(promDb, rptDb);
	}

	public List<DBObject> WAF_SessionLogMonth(int assetCode, String sEndDay) throws Exception {
		
		StartTimeCheck("WAF_SessionLogMonth");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay); // 조회월에 한달을 더하고 1초를 빼 조회월 마지막날 23:59:59초를 구한다.
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay); 
			Calendar startCal = getCalendar(startDate);
			startCal.add(Calendar.MONTH, -5);
			
			DBCollection dbCollection = rptDb.getCollection("WAF_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 4);
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("action", 1);
			sortFields.put("year", 1);
			sortFields.put("month", 1);
			
			DBCursor dbCursor = dbCollection.find(condition).sort(sortFields);
			
			return dbCursor.toArray();

		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}

	public List<DBObject> WAF_SessionLogDay(int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		StartTimeCheck("WAF_SessionLogDay");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			startCal.add(Calendar.MONTH, -1);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);

			DBCollection dbCollection = rptDb.getCollection("WAF_" + assetCode + "_DY");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 4);
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("action", 1);
			sortFields.put("year", 1);
			sortFields.put("month", 1);
			sortFields.put("day", 1);
			
			DBCursor dbCursor = dbCollection.find(condition).sort(sortFields);
			
			return dbCursor.toArray();

		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}

	public List<DBObject> WAF_TopInfoMonth(int assetCode, String sEndDay, String sAction, int nField, int nLimit) throws Exception {
		
		StartTimeCheck("WAF_TopInfoMonth");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);
			
			DBCollection dbCollection = rptDb.getCollection("WAF_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 5);
			if (!sAction.isEmpty()) {
				condition.put("action", sAction);
			}
			condition.put("host", new BasicDBObject("$ne", "-1"));

			DBCursor dbCursor = dbCollection.find(condition).sort(new BasicDBObject("count", -1)).limit(nLimit);			
			
			return dbCursor.toArray();

		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}
	
	public Iterable<DBObject> WAF_EtcInfoMonth(int assetCode, String sEndDay, String sAction, List<String> hosts) throws Exception {
		
		StartTimeCheck("WAF_EtcInfoMonth");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);
			
			DBCollection dbCollection = rptDb.getCollection("WAF_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 5);
			if (!sAction.isEmpty()) {
				condition.put("action", sAction);
			}
			condition.put("host", new BasicDBObject("$nin", hosts));
			
			DBObject match = new BasicDBObject("$match", condition);
			
			DBObject project = new BasicDBObject();
			DBObject fields = new BasicDBObject();
			fields.put("rptGubun", 1);
			fields.put("count", 1);
			project.put("$project", fields);
			
			BasicDBObject keys = new BasicDBObject();
			keys.put("rptGubun", "$rptGubun");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			DBObject group = new BasicDBObject("$group", groupFields);
			
			DBObject sortFields = new BasicDBObject();
		    sortFields.put("count", -1);
		    DBObject sort = new BasicDBObject("$sort", sortFields );
		    
			// run aggregation
			AggregationOutput output = dbCollection.aggregate(match, project, group, sort);

			return output.results();

		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}

	public List<DBObject> WAF_DomainMonthPieData(int assetCode, String sEndDay) throws Exception {
		
		StartTimeCheck("WAF_DomainMonthPieData");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);
			
			DBCollection dbCollection = rptDb.getCollection("WAF_" + assetCode + "_MON");
	
			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 5);
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("action", -1);
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
	
	public List<DBObject> WAF_Top10DomainChange(int assetCode, String sStartDay, String sEndDay, String sAction, List<String> hosts) throws Exception {
		
		StartTimeCheck("WAF_Top10DomainChange");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = rptDb.getCollection("WAF_" + assetCode + "_DY");
	
			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 5);
			condition.put("action", sAction);
			condition.put("host", new BasicDBObject("$in", hosts));
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("host", 1);
			sortFields.put("year", 1);
			sortFields.put("month", 1);
			sortFields.put("day", 1);
			
			DBCursor dbCursor = dbCollection.find(condition).sort(sortFields);
			
			return dbCursor.toArray();

		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}

	public Iterable<DBObject> WAF_Top5DestIp(int assetCode, String sEndDay, String sAction, String sHost, String sMessage) throws Exception {
		
		StartTimeCheck("WAF_Top5DestIp");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);

			DBCollection dbCollection = rptDb.getCollection("WAF_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			condition.put("action", sAction);
			condition.put("host", sHost);
			condition.put("message", sMessage);
			DBObject match = new BasicDBObject("$match", condition);

			DBObject project = new BasicDBObject();
			DBObject fields = new BasicDBObject();
			fields.put("srcIp", 1);
			fields.put("destIp", 1);
			fields.put("count", 1);
			project.put("$project", fields);

			BasicDBObject keys = new BasicDBObject();
			keys.put("srcIp", "$srcIp");
			keys.put("destIp", "$destIp");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			DBObject group = new BasicDBObject("$group", groupFields);

			DBObject sortFields = new BasicDBObject();
			sortFields.put("_id.srcIp", 1);
			sortFields.put("count", -1);
		    DBObject sort = new BasicDBObject("$sort", sortFields );
			
			// run aggregation
			AggregationOutput output = dbCollection.aggregate(match, project, group, sort);

			return output.results();
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}

	public Iterable<DBObject> WAF_EtcDestIp(int assetCode, String sEndDay, String sAction, List<String> hosts, String sMessage) throws Exception {
		
		StartTimeCheck("WAF_Top5DestIp");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);

			DBCollection dbCollection = rptDb.getCollection("WAF_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			condition.put("action", sAction);
			condition.put("host", new BasicDBObject("$nin", hosts));
			condition.put("message", sMessage);
			DBObject match = new BasicDBObject("$match", condition);

			DBObject project = new BasicDBObject();
			DBObject fields = new BasicDBObject();
			fields.put("srcIp", 1);
			fields.put("destIp", 1);
			fields.put("count", 1);
			project.put("$project", fields);

			BasicDBObject keys = new BasicDBObject();
			keys.put("srcIp", "$srcIp");
			keys.put("destIp", "$destIp");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			DBObject group = new BasicDBObject("$group", groupFields);

			DBObject sortFields = new BasicDBObject();
			sortFields.put("_id.srcIp", 1);
			sortFields.put("count", -1);
		    DBObject sort = new BasicDBObject("$sort", sortFields );
			
			// run aggregation
			AggregationOutput output = dbCollection.aggregate(match, project, group, sort);

			return output.results();
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}
	
	public Iterable<DBObject> WAF_Top5SrcIp(int assetCode, String sEndDay, String sAction, String sHost, String sMessage) throws Exception {
		
		StartTimeCheck("WAF_Top5SrcIp");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);

			DBCollection dbCollection = rptDb.getCollection("WAF_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			condition.put("action", sAction);
			condition.put("host", sHost);
			condition.put("message", sMessage);
			DBObject match = new BasicDBObject("$match", condition);

			DBObject project = new BasicDBObject();
			DBObject fields = new BasicDBObject();
			fields.put("srcIp", 1);
			fields.put("count", 1);
			project.put("$project", fields);

			BasicDBObject keys = new BasicDBObject();
			keys.put("srcIp", "$srcIp");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			DBObject group = new BasicDBObject("$group", groupFields);

			DBObject sortFields = new BasicDBObject();
			sortFields.put("count", -1);
		    DBObject sort = new BasicDBObject("$sort", sortFields );
			
		    DBObject limit = new BasicDBObject("$limit", 5);
		    
			// run aggregation
			AggregationOutput output = dbCollection.aggregate(match, project, group, sort, limit);

			return output.results();
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}

	public Iterable<DBObject> WAF_EtcSrcIp(int assetCode, String sEndDay, String sAction, List<String> hosts, String sMessage) throws Exception {
		
		StartTimeCheck("WAF_Top5SrcIp");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);

			DBCollection dbCollection = rptDb.getCollection("WAF_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			condition.put("action", sAction);
			condition.put("host", new BasicDBObject("$nin", hosts));
			condition.put("message", sMessage);
			DBObject match = new BasicDBObject("$match", condition);

			DBObject project = new BasicDBObject();
			DBObject fields = new BasicDBObject();
			fields.put("srcIp", 1);
			fields.put("count", 1);
			project.put("$project", fields);

			BasicDBObject keys = new BasicDBObject();
			keys.put("srcIp", "$srcIp");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			DBObject group = new BasicDBObject("$group", groupFields);

			DBObject sortFields = new BasicDBObject();
			sortFields.put("count", -1);
		    DBObject sort = new BasicDBObject("$sort", sortFields );
			
		    DBObject limit = new BasicDBObject("$limit", 5);
		    
			// run aggregation
			AggregationOutput output = dbCollection.aggregate(match, project, group, sort, limit);

			return output.results();
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}
	
	public List<DBObject> WAF_Top10Message(int assetCode, String sEndDay, String sAction, String sField) throws Exception {
		
		StartTimeCheck("WAF_Top10Message");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);

			DBCollection dbCollection = rptDb.getCollection("WAF_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 6);
			condition.put("action", sAction);
			condition.put("host", sField);

			DBCursor dbCursor = dbCollection.find(condition).sort(new BasicDBObject("count", -1));
			
			return dbCursor.toArray();
			
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}

	public Iterable<DBObject> WAF_EtcMessage(int assetCode, String sEndDay, String sAction, List<String> hosts) throws Exception {
		
		StartTimeCheck("WAF_EtcMessage");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);

			DBCollection dbCollection = rptDb.getCollection("WAF_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			condition.put("action", sAction);
			condition.put("host", new BasicDBObject("$nin", hosts));
			DBObject match = new BasicDBObject("$match", condition);

			DBObject project = new BasicDBObject();
			DBObject fields = new BasicDBObject();
			fields.put("message", 1);
			fields.put("count", 1);
			project.put("$project", fields);

			BasicDBObject keys = new BasicDBObject();
			keys.put("message", "$message");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			DBObject group = new BasicDBObject("$group", groupFields);

			DBObject sortFields = new BasicDBObject();
			sortFields.put("count", -1);
		    DBObject sort = new BasicDBObject("$sort", sortFields );
			
			// run aggregation
			AggregationOutput output = dbCollection.aggregate(match, project, group, sort);

			return output.results();
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}
	
	public Iterable<DBObject> WAF_Top10EventCondition(int assetCode, String sEndDay, String sAction, int nField, String sField) throws Exception {
		
		StartTimeCheck("WAF_Top10EventCondition");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);

			DBCollection dbCollection = rptDb.getCollection("WAF_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			condition.put("action", sAction);
			if (nField == 1) {
				condition.put("host", sField);
			} else if (nField == 2) {
				condition.put("message", sField);
			} else if (nField == 3) {
				condition.put("srcIp", sField);
			}
			DBObject match = new BasicDBObject("$match", condition);

			DBObject project = new BasicDBObject();
			DBObject fields = new BasicDBObject();
			fields.put("host", 1);
			fields.put("message", 1);
			fields.put("srcIp", 1);
			fields.put("destIp", 1);
			fields.put("count", 1);
			project.put("$project", fields);

			BasicDBObject keys = new BasicDBObject();
			keys.put("host", "$host");
			keys.put("message", "$message");
			keys.put("srcIp", "$srcIp");
			keys.put("destIp", "$destIp");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			DBObject group = new BasicDBObject("$group", groupFields);

			DBObject sortFields = new BasicDBObject();
			sortFields.put("count", -1);
		    DBObject sort = new BasicDBObject("$sort", sortFields );
			
		    DBObject limit = new BasicDBObject("$limit", 5);
		    
			// run aggregation
			AggregationOutput output = dbCollection.aggregate(match, project, group, sort, limit);

			return output.results();
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}

}
