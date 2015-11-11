package com.cyberone.report.core.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class _FWReportDao extends BaseDao {

	public _FWReportDao(DB promDb, DB rptDb)	{
		super(promDb, rptDb);
	}
	
	public List<DBObject> FW_SessionLogMonth(int assetCode, int nDirection, String sEndDay, int nSearchPort) throws Exception {
		
		StartTimeCheck("FW_SessionLogMonth");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay); // 조회월에 한달을 더하고 1초를 빼 조회월 마지막날 23:59:59초를 구한다.
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay); 
			Calendar startCal = getCalendar(startDate);
			startCal.add(Calendar.MONTH, -5);
			
			DBCollection dbCollection = rptDb.getCollection("FW_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("direction", nDirection);
			if (nSearchPort > 0) {
				condition.put("rptGubun", 5);
				condition.put("destPort", nSearchPort);
				condition.put("action", "1");
			} else {
				condition.put("rptGubun", 4);
			}
			
			DBObject sortFields = new BasicDBObject();
		    sortFields.put("action", -1);
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

	public List<DBObject> FW_SessionLogDay(int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		StartTimeCheck("FW_SessionLogDay");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			startCal.add(Calendar.MONTH, -1);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = rptDb.getCollection("FW_" + assetCode + "_DY");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 4);
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("direction", 1);
		    sortFields.put("action", -1);
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
	
	public List<DBObject> FW_ServiceSessionLog(int assetCode, int nDirection, String sAction, String sEndDay, boolean bCountSort) throws Exception {
		
		StartTimeCheck("FW_ServiceSessionLog");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);
			startCal.add(Calendar.MONTH, -1);
			
			DBCollection dbCollection = rptDb.getCollection("FW_" + assetCode + "_MON");
			
			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			if (bCountSort) {
			    condition.put("rptGubun", 5); //카운트 기준
			} else {
				condition.put("rptGubun", 6); //통신량 기준
			}
			condition.put("direction", nDirection);
			condition.put("action", sAction);
			condition.put("destPort", new BasicDBObject("$ne", null));
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("year", 1);
			sortFields.put("month", 1);
			if (bCountSort) {
				sortFields.put("count", -1);
			} else {
				sortFields.put("trafficSize", -1);
			}
			
			DBCursor dbCursor = dbCollection.find(condition).sort(sortFields);
			
			return dbCursor.toArray();
			
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}
	
	public List<DBObject> FW_Top10Ips(int assetCode, String sEndDay, int nDirection, String sAction, boolean bSrcIp, boolean bCountSort) throws Exception {
		
		StartTimeCheck("FW_Top10Ips");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);

			DBCollection dbCollection = rptDb.getCollection("FW_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			if (bCountSort) {
			    condition.put("rptGubun", 5); //카운트 기준
			} else {
				condition.put("rptGubun", 6); //통신량 기준
			}
			condition.put("direction", nDirection);
			condition.put("action", sAction);
			
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
			if (bCountSort) {
				sortFields.put("count", -1);
			} else {
				sortFields.put("trafficSize", -1);
			}
			
			DBCursor dbCursor = dbCollection.find(condition).sort(sortFields).limit(10);

			return dbCursor.toArray();
			
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}
	
	public Iterable<DBObject> FW_Top10LogDetail(int assetCode, String sStartDay, String sEndDay, int nDirection, String sAction, List<String> ips, boolean bSrcIp) throws Exception {
		
		StartTimeCheck("FW_Top10LogDetail");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);

			DBCollection dbCollection = rptDb.getCollection("FW_" + assetCode + "_DY");

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
			DBObject match = new BasicDBObject("$match", condition);
			
			DBObject project = new BasicDBObject();
			DBObject fields = new BasicDBObject();
			if (bSrcIp) {
				fields.put("srcIp", 1);
			} else {
				fields.put("destIp", 1);
			}
			fields.put("year", 1);
			fields.put("month", 1);
			fields.put("day", 1);
			fields.put("count", 1);
			fields.put("trafficSize", 1);
			project.put("$project", fields);
			
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
			groupFields.put("trafficSize", new BasicDBObject( "$sum", "$trafficSize"));
			DBObject group = new BasicDBObject("$group", groupFields);
			
			DBObject sortFields = new BasicDBObject();
			if (bSrcIp) {
				sortFields.put("_id.srcIp", 1);
			} else {
				sortFields.put("_id.destIp", 1);
			}
			sortFields.put("_id.year", 1);
			sortFields.put("_id.month", 1);
			sortFields.put("_id.day", 1);
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
	
	public Iterable<DBObject> FW_Top10LogCondition(int assetCode, String sEndDay, int nDirection, String sAction, String ip, boolean bSrcIp, boolean bCountSort) throws Exception {
		
		StartTimeCheck("FW_Top10LogCondition");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);

			DBCollection dbCollection = rptDb.getCollection("FW_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			condition.put("direction", nDirection);
			condition.put("action", sAction);
			if (bSrcIp) { 
				condition.put("srcIp", ip);
			} else {
				condition.put("destIp", ip);
			}
			DBObject match = new BasicDBObject("$match", condition);

			DBObject project = new BasicDBObject();
			DBObject fields = new BasicDBObject();
			if (bSrcIp) {
				fields.put("destIp", 1);
			} else {
				fields.put("srcIp", 1);
			}
			fields.put("destPort", 1);
			fields.put("destZone", 1);
			fields.put("policyId", 1);
			fields.put("count", 1);
			fields.put("trafficSize", 1);
			project.put("$project", fields);

			BasicDBObject keys = new BasicDBObject();
			if (bSrcIp) {
				keys.put("destIp", "$destIp");
			} else {
				keys.put("srcIp", "$srcIp");
			}
			keys.put("destPort", "$destPort");
			keys.put("destZone", "$destZone");
			keys.put("policyId", "$policyId");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			groupFields.put("trafficSize", new BasicDBObject( "$sum", "$trafficSize"));
			DBObject group = new BasicDBObject("$group", groupFields);

			DBObject sortFields = new BasicDBObject();
			
			if (bCountSort) {
				sortFields.put("count", -1);
			} else {
				sortFields.put("trafficSize", -1);
			}
			
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
	
	public Iterable<DBObject> FW_Top10ServiceCondition(int assetCode, String sEndDay, int nDirection, String sAction, int port, boolean bCountSort) throws Exception {
		
		StartTimeCheck("FW_Top10ServiceCondition");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);

			DBCollection dbCollection = rptDb.getCollection("FW_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			condition.put("direction", nDirection);
			condition.put("action", sAction);
			condition.put("destPort", port);
			DBObject match = new BasicDBObject("$match", condition);

			DBObject project = new BasicDBObject();
			DBObject fields = new BasicDBObject();
			fields.put("destPort", 1);
			fields.put("srcIp", 1);
			fields.put("destIp", 1);
			fields.put("destZone", 1);
			fields.put("policyId", 1);
			fields.put("count", 1);
			fields.put("trafficSize", 1);
			project.put("$project", fields);

			BasicDBObject keys = new BasicDBObject();
			keys.put("destPort", "$destPort");
			keys.put("srcIp", "$srcIp");
			keys.put("destIp", "$destIp");
			keys.put("destZone", "$destZone");
			keys.put("policyId", "$policyId");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			groupFields.put("trafficSize", new BasicDBObject( "$sum", "$trafficSize"));
			DBObject group = new BasicDBObject("$group", groupFields);

			DBObject sortFields = new BasicDBObject();
			if (bCountSort) {
				sortFields.put("count", -1);
			} else {
				sortFields.put("trafficSize", -1);
			}
			
		    DBObject sort = new BasicDBObject("$sort", sortFields );
			
		    DBObject limit = new BasicDBObject("$limit", 5);
		    
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
