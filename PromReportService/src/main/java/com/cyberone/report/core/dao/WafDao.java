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

public class WafDao extends BaseDao {
	
	public WafDao(DB promDB, DB reportDB) {
		super(promDB, reportDB);
	}

    /*
     * SrcIp TOP10 발생추이
     */
	public  Iterable<DBObject> SrcIpTopNTrend(String sCol, int assetCode, String sStartDay, String sEndDay, List<String> srcIps) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("N_WAF_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 5);
			condition.put("srcIp", new BasicDBObject("$in", srcIps));
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("srcIp", "$_id.srcIp");
			fields.put("year", "$_id.year");
			switch (sCol) {
				case "HR" : fields.put("hour", "$_id.hour");
				case "DY" : fields.put("day", "$_id.day");
				case "MON" : fields.put("month", "$_id.month");
			}
			fields.put("count", 1);

			BasicDBObject keys = new BasicDBObject();
			keys.put("srcIp", "$srcIp");
			keys.put("year", "$year");
			switch (sCol) {
				case "HR" : keys.put("hour", "$hour");
				case "DY" : keys.put("day", "$day");
				case "MON" : keys.put("month", "$month");
			}
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("srcIp", 1);
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
		}
	}

    /*
     * 이벤트 TOP10 시간별 발생추이
     */
	public Iterable<DBObject> DomainEventTopNTrend(String sCol, int assetCode, String sStartDay, String sEndDay, String sHost, List<String> messages) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("N_WAF_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 6);
			condition.put("host", sHost);
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
		}
	}
	
    /*
     * 도메인 TOP10 발생추이
     */
	public Iterable<DBObject> DomainTopNTrend(String sCol, String sAction, int assetCode, String sStartDay, String sEndDay, String sHost) throws Exception {
		List<String> hosts = new ArrayList<String>();
		hosts.add(sHost);
		return DomainTopNTrend(sCol, sAction, assetCode, sStartDay, sEndDay, hosts);
	}
	
	public Iterable<DBObject> DomainTopNTrend(String sCol, String sAction, int assetCode, String sStartDay, String sEndDay, List<String> hosts) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("N_WAF_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 5);
			condition.put("host", new BasicDBObject("$in", hosts));
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("host", "$_id.host");
			if (!StringUtil.isEmpty(sAction)) { fields.put("action", "$_id.action"); }
			fields.put("year", "$_id.year");
			switch (sCol) {
				case "HR" : fields.put("hour", "$_id.hour");
				case "DY" : fields.put("day", "$_id.day");
				case "MON" : fields.put("month", "$_id.month");
			}
			fields.put("count", 1);

			BasicDBObject keys = new BasicDBObject();
			keys.put("host", "$host");
			if (!StringUtil.isEmpty(sAction)) { keys.put("action", "$action"); }
			keys.put("year", "$year");
			switch (sCol) {
				case "HR" : keys.put("hour", "$hour");
				case "DY" : keys.put("day", "$day");
				case "MON" : keys.put("month", "$month");
			}
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("host", 1);
			if (!StringUtil.isEmpty(sAction)) { sortFields.put("action", -1); }
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
		}
	}

    /*
     * 전체탐지로그 발생추이
     */
	public  Iterable<DBObject> DetectLogTrend(String sCol, String sAction, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("N_WAF_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 4);
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			if (!StringUtil.isEmpty(sAction)) { fields.put("action", "$_id.action"); }
			fields.put("year", "$_id.year");
			switch (sCol) {
				case "HR" : fields.put("hour", "$_id.hour");
				case "DY" : fields.put("day", "$_id.day");
				case "MON" : fields.put("month", "$_id.month");
			}
			fields.put("count", 1);

			BasicDBObject keys = new BasicDBObject();
			if (!StringUtil.isEmpty(sAction)) { keys.put("action", "$action"); }
			keys.put("year", "$year");
			switch (sCol) {
				case "HR" : keys.put("hour", "$hour");
				case "DY" : keys.put("day", "$day");
				case "MON" : keys.put("month", "$month");
			}
			
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			
			DBObject sortFields = new BasicDBObject();
			if (!StringUtil.isEmpty(sAction)) { sortFields.put("action", -1); }
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
		}
	}
	
    /*
     * 전체탐지로그 도메인 TopN
     */
	public  Iterable<DBObject> DomainTopN(String sCol, int assetCode, String sStartDay, String sEndDay, int nLimit) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("N_WAF_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 5);
			
			ArrayList<BasicDBObject> andList = new ArrayList<BasicDBObject>();
			andList.add(new BasicDBObject("host", new BasicDBObject("$ne", null)));
			andList.add(new BasicDBObject("host", new BasicDBObject("$ne", "-1")));
			condition.put("$and", andList);

			BasicDBObject keys = new BasicDBObject();
			keys.put("host", "$host");
			
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("host", "$_id.host");
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
		}
	}
	
	public Iterable<DBObject> DomainEventSrcIpCondition(String sCol, int assetCode, String sStartDay, String sEndDay, String sHost, String sMessage, int nLimit) throws Exception {
		
		StartTimeCheck("DomainEventSrcIpCondition");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);

			DBCollection dbCollection = reportDB.getCollection("N_WAF_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			condition.put("host", sHost);
			condition.put("message", sMessage);
			
			BasicDBObject keys = new BasicDBObject();
			keys.put("srcIp", "$srcIp");
			keys.put("action", "$action");
			
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("srcIp", "$_id.srcIp");
			fields.put("action", "$_id.action");
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
     * 이벤트 TopN
     */
	public  Iterable<DBObject> EventTopN(String sCol, int assetCode, String sStartDay, String sEndDay, int nLimit) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("N_WAF_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 5);
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
		}
	}

    /*
     * 도메인별 이벤트 TopN
     */
	public  Iterable<DBObject> DomainEventTopN(String sCol, int assetCode, String sStartDay, String sEndDay, String sHost, int nLimit) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("N_WAF_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 6);
			condition.put("host", sHost);
			
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
		}
	}
	
    /*
     * 출발지 SrcIp TopN
     */
	public  Iterable<DBObject> SrcIpTopN(String sCol, int assetCode, String sStartDay, String sEndDay, int nLimit, List<String> srcIps) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("N_WAF_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 5);
			if (srcIps != null) {
				condition.put("srcIp", new BasicDBObject("$in", srcIps));	
			} else {
				ArrayList<BasicDBObject> andList = new ArrayList<BasicDBObject>();
				andList.add(new BasicDBObject("srcIp", new BasicDBObject("$ne", null)));
				andList.add(new BasicDBObject("srcIp", new BasicDBObject("$ne", "-1")));
				condition.put("$and", andList);
			}
			
			BasicDBObject keys = new BasicDBObject();
			keys.put("srcIp", "$srcIp");
			
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("srcIp", "$_id.srcIp");
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
		}
	}
	
    /*
     * 이벤트 탐지/차단 카운트
     */
	public  Iterable<DBObject> EventActionCount(String sCol, int assetCode, String sStartDay, String sEndDay, String sMessage) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("N_WAF_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 5);
			condition.put("message", sMessage);
			
			BasicDBObject keys = new BasicDBObject();
			keys.put("message", "$message");
			keys.put("action", "$action");
			
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("message", "$_id.message");
			fields.put("action", "$_id.action");
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
		}
	}
	
    /*
     * 이벤트 도메인 TopN
     */
	public  Iterable<DBObject> EventDomainTopN(String sCol, int assetCode, String sStartDay, String sEndDay, String sMessage) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("N_WAF_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 7);
			condition.put("message", sMessage);
			
			BasicDBObject keys = new BasicDBObject();
			keys.put("host", "$host");
			keys.put("action", "$action");
			
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("host", "$_id.host");
			fields.put("action", "$_id.action");
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
		}
	}
	
	public Iterable<DBObject> EventHostCondition(String sCol, int assetCode, String sStartDay, String sEndDay, String sSrcIp) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);

			DBCollection dbCollection = reportDB.getCollection("N_WAF_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			condition.put("srcIp", sSrcIp);

			BasicDBObject keys = new BasicDBObject();
			keys.put("message", "$message");
			keys.put("host", "$host");
			keys.put("action", "$action");
			
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("message", "$_id.message");
			fields.put("host", "$_id.host");
			fields.put("action", "$_id.action");
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
