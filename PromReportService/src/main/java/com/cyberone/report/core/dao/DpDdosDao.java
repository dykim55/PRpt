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

public class DpDdosDao extends BaseDao {
	
	public DpDdosDao(DB promDB, DB reportDB) {
		super(promDB, reportDB);
	}

    /*
     * 전체탐지로그 해당일-시간대별 발생추이
     */
	public Iterable<DBObject> AllDetectLogTrend(String sCol, String sAction, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("N_DDOS_" + assetCode + "_" + sCol);

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
			DBObject groupFields = new BasicDBObject("_id", keys);
			groupFields.put("count", new BasicDBObject("$sum", "$count"));
			
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
     * 유형별 탐지로그 -시간대별 발생추이
     */
	public Iterable<DBObject> AllDetectGroupTrend2(String sCol, int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("N_DDOS_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 5);
			condition.put("group", new BasicDBObject("$ne", null));
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("group", "$_id.group");
			fields.put("year", "$_id.year");
			switch (sCol) {
				case "HR" : fields.put("hour", "$_id.hour");
				case "DY" : fields.put("day", "$_id.day");
				case "MON" : fields.put("month", "$_id.month");
			}
			fields.put("count", 1);

			BasicDBObject keys = new BasicDBObject();
			keys.put("group", "$group");
			keys.put("year", "$year");
			switch (sCol) {
				case "HR" : keys.put("hour", "$hour");
				case "DY" : keys.put("day", "$day");
				case "MON" : keys.put("month", "$month");
			}
			DBObject groupFields = new BasicDBObject("_id", keys);
			groupFields.put("count", new BasicDBObject("$sum", "$count"));
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("group", 1);
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
     * 전체 공격규모 -시간대별 발생추이
     */
	public Iterable<DBObject> AllDetectAttackTrend(String sCol, int assetCode, String sStartDay, String sEndDay, boolean bGroup, String sType) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("N_DDOS_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			if (bGroup) {
				if (sType.equals("pktCnt")) {
					condition.put("rptGubun", 15);
				} else if (sType.equals("bandWidth")) {
					condition.put("rptGubun", 25);
				} else {
					condition.put("rptGubun", 5);
				}
				condition.put("group", new BasicDBObject("$ne", null));
			} else {
				condition.put("rptGubun", 4);
			}
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			if (bGroup) {
				fields.put("group", "$_id.group");
			}
			fields.put("year", "$_id.year");
			switch (sCol) {
				case "HR" : fields.put("hour", "$_id.hour");
				case "DY" : fields.put("day", "$_id.day");
				case "MON" : fields.put("month", "$_id.month");
			}
			fields.put(sType, 1);

			BasicDBObject keys = new BasicDBObject();
			if (bGroup) {
				keys.put("group", "$group");
			}
			keys.put("year", "$year");
			switch (sCol) {
				case "HR" : keys.put("hour", "$hour");
				case "DY" : keys.put("day", "$day");
				case "MON" : keys.put("month", "$month");
			}
			DBObject groupFields = new BasicDBObject("_id", keys);
			groupFields.put(sType, new BasicDBObject("$sum", "$" + sType));
			
			DBObject sortFields = new BasicDBObject();
			if (bGroup) {
				sortFields.put("group", 1);
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
		}
	}

	public Iterable<DBObject> GroupTopN(String sCol, int assetCode, String sStartDay, String sEndDay, boolean bAction, String sGroup) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("N_DDOS_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 5);
			
			if (!StringUtil.isEmpty(sGroup)) {
				condition.put("group", sGroup);
			} else {
				condition.put("group", new BasicDBObject("$ne", null));
			}
			
			BasicDBObject keys = new BasicDBObject();
			keys.put("group", "$group");
			if (bAction) {
				keys.put("action", "$action");
			}
			DBObject groupFields = new BasicDBObject("_id", keys);
			groupFields.put("count", new BasicDBObject("$sum", "$count"));
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("group", "$_id.group");
			if (bAction) {
				fields.put("action", "$_id.action");
			}
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
		}
	}

	public Iterable<DBObject> EventTopN(String sCol, int assetCode, String sStartDay, String sEndDay, boolean bAction, String sMessage, String sSortField) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("N_DDOS_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			if (sSortField.equals("pktCnt")) {
				condition.put("rptGubun", 15);
			} else if (sSortField.equals("bandWidth")) {
				condition.put("rptGubun", 25);
			} else {
				condition.put("rptGubun", 5);
			}
			
			if (!StringUtil.isEmpty(sMessage)) {
				condition.put("message", sMessage);
			} else {
				condition.put("message", new BasicDBObject("$ne", null));
			}
			
			BasicDBObject keys = new BasicDBObject();
			keys.put("message", "$message");
			if (bAction) {
				keys.put("action", "$action");
			}
			DBObject groupFields = new BasicDBObject("_id", keys);
			groupFields.put("count", new BasicDBObject("$sum", "$count"));
			groupFields.put("pktCnt", new BasicDBObject("$sum", "$pktCnt"));
			groupFields.put("bandWidth", new BasicDBObject("$sum", "$bandWidth"));
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("message", "$_id.message");
			if (bAction) {
				fields.put("action", "$_id.action");
			}
			fields.put("count", 1);
			fields.put("pktCnt", 1);
			fields.put("bandWidth", 1);
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put(sSortField, -1);

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
	
	public Iterable<DBObject> GroupMessageCondition(String sCol, int assetCode, String sStartDay, String sEndDay, String sGroup, int nLimit) throws Exception {
		
		StartTimeCheck("GroupMessageCondition");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);

			DBCollection dbCollection = reportDB.getCollection("N_IPS_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			if (!StringUtil.isEmpty(sGroup)) {
				condition.put("group", sGroup);
			}
			
			BasicDBObject keys = new BasicDBObject();
			keys.put("message", "message");
			keys.put("action", "$action");
			DBObject groupFields = new BasicDBObject("_id", keys);
			groupFields.put("count", new BasicDBObject("$sum", "$count"));
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("message", "$_id.message");
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

	public Iterable<DBObject> EventSrcIpTopN(String sCol, int assetCode, String sStartDay, String sEndDay, String sAction, String sMessage, String sSortField, int nLimit) throws Exception {
		
		StartTimeCheck("EventSrcIpTopN");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);

			DBCollection dbCollection = reportDB.getCollection("N_DDOS_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			condition.put("message", sMessage);
			condition.put("action", sAction);
			
			BasicDBObject keys = new BasicDBObject();
			keys.put("srcIp", "$srcIp");
			keys.put("destIp", "$destIp");
			DBObject groupFields = new BasicDBObject("_id", keys);
			groupFields.put("count", new BasicDBObject("$sum", "$count"));
			groupFields.put("pktCnt", new BasicDBObject("$sum", "$pktCnt"));
			groupFields.put("bandWidth", new BasicDBObject("$sum", "$bandWidth"));
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("srcIp", "$_id.srcIp");
			fields.put("destIp", "$_id.destIp");
			fields.put("count", 1);
			fields.put("pktCnt", 1);
			fields.put("bandWidth", 1);

			DBObject sortFields = new BasicDBObject();
			sortFields.put(sSortField, -1);

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

	public Iterable<DBObject> EventTopNTrend(String sCol, String sAction, int assetCode, String sStartDay, String sEndDay, String message) throws Exception {
		List<String> messages = new ArrayList<String>();
		messages.add(message);
		return EventTopNTrend(sCol, sAction, assetCode, sStartDay, sEndDay, messages);
	}
	
	public Iterable<DBObject> EventTopNTrend(String sCol, String sAction, int assetCode, String sStartDay, String sEndDay, List<String> messages) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("N_DDOS_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 5);
			if (!StringUtil.isEmpty(sAction)) {
				condition.put("action", sAction);
			}
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
			DBObject groupFields = new BasicDBObject("_id", keys);
			groupFields.put("count", new BasicDBObject("$sum", "$count"));
			
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

	public Iterable<DBObject> IpTopN(String sCol, int assetCode, String sStartDay, String sEndDay, boolean bSrcIp, String sSortField, int nLimit) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("N_DDOS_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			if (sSortField.equals("pktCnt")) {
				condition.put("rptGubun", 15);
			} else if (sSortField.equals("bandWidth")) {
				condition.put("rptGubun", 25);
			} else {
				condition.put("rptGubun", 5);
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
			DBObject groupFields = new BasicDBObject("_id", keys);
			groupFields.put("count", new BasicDBObject("$sum", "$count"));
			groupFields.put("pktCnt", new BasicDBObject("$sum", "$pktCnt"));
			groupFields.put("bandWidth", new BasicDBObject("$sum", "$bandWidth"));
			
			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			if (bSrcIp) {
				fields.put("srcIp", "$_id.srcIp");
			} else {
				fields.put("destIp", "$_id.destIp");
			}
			fields.put("count", 1);
			fields.put("pktCnt", 1);
			fields.put("bandWidth", 1);
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put(sSortField, -1);

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

	public Iterable<DBObject> ServiceTopN(String sCol, int assetCode, String sStartDay, String sEndDay, String sSortField, int nLimit) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = reportDB.getCollection("N_DDOS_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			if (sSortField.equals("pktCnt")) {
				condition.put("rptGubun", 15);
			} else if (sSortField.equals("bandWidth")) {
				condition.put("rptGubun", 25);
			} else {
				condition.put("rptGubun", 5);
			}
			ArrayList<BasicDBObject> andList = new ArrayList<BasicDBObject>();
			andList.add(new BasicDBObject("destPort", new BasicDBObject("$ne", null)));
			andList.add(new BasicDBObject("destPort", new BasicDBObject("$ne", -1)));
			condition.put("$and", andList);
			
			BasicDBObject keys = new BasicDBObject();
			keys.put("destPort", "$destPort");
			DBObject groupFields = new BasicDBObject("_id", keys);
			groupFields.put("count", new BasicDBObject("$sum", "$count"));
			groupFields.put("pktCnt", new BasicDBObject("$sum", "$pktCnt"));
			groupFields.put("bandWidth", new BasicDBObject("$sum", "$bandWidth"));
			
			DBObject fields = new BasicDBObject();
			fields.put("destPort", "$_id.destPort");
			fields.put("count", 1);
			fields.put("pktCnt", 1);
			fields.put("bandWidth", 1);
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put(sSortField, -1);

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
	
	public Iterable<DBObject> BeforeIpCondition(String sCol, int assetCode, String sStartDay, String sEndDay, boolean bSrcIp, String sSortField, List<String> saIps) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);

			DBCollection dbCollection = reportDB.getCollection("N_DDOS_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			if (sSortField.equals("pktCnt")) {
				condition.put("rptGubun", 15);
			} else if (sSortField.equals("bandWidth")) {
				condition.put("rptGubun", 25);
			} else {
				condition.put("rptGubun", 5);
			}
			
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
			fields.put("pktCnt", 1);
			fields.put("bandWidth", 1);

			BasicDBObject keys = new BasicDBObject();
			if (bSrcIp) {
				keys.put("srcIp", "$srcIp");
			} else {
				keys.put("destIp", "$destIp");				
			}
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject("$sum", "$count"));
			groupFields.put("pktCnt", new BasicDBObject("$sum", "$pktCnt"));
			groupFields.put("bandWidth", new BasicDBObject("$sum", "$bandWidth"));

			DBObject sortFields = new BasicDBObject(sSortField, -1);
		    
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

	public Iterable<DBObject> BeforeServiceCondition(String sCol, int assetCode, String sStartDay, String sEndDay, String sSortField, List<Integer> saPorts) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);

			DBCollection dbCollection = reportDB.getCollection("N_DDOS_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			if (sSortField.equals("pktCnt")) {
				condition.put("rptGubun", 15);
			} else if (sSortField.equals("bandWidth")) {
				condition.put("rptGubun", 25);
			} else {
				condition.put("rptGubun", 5);
			}
			condition.put("destPort", new BasicDBObject("$in", saPorts));	

			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("destPort", "$_id.destPort");
			fields.put("count", 1);
			fields.put("pktCnt", 1);
			fields.put("bandWidth", 1);

			BasicDBObject keys = new BasicDBObject();
			keys.put("destPort", "$destPort");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject("$sum", "$count"));
			groupFields.put("pktCnt", new BasicDBObject("$sum", "$pktCnt"));
			groupFields.put("bandWidth", new BasicDBObject("$sum", "$bandWidth"));

			DBObject sortFields = new BasicDBObject(sSortField, -1);
		    
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
	
	public Iterable<DBObject> IpTopNCondition(String sCol, int assetCode, String sStartDay, String sEndDay, String ip, boolean bSrcIp, String sSortField) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);

			DBCollection dbCollection = reportDB.getCollection("N_DDOS_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
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
			fields.put("pktCnt", 1);
			fields.put("bandWidth", 1);


			BasicDBObject keys = new BasicDBObject();
			keys.put("message", "$message");
			keys.put("destIp", "$destIp");
			keys.put("srcIp", "$srcIp");
			keys.put("action", "$action");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject("$sum", "$count"));
			groupFields.put("pktCnt", new BasicDBObject("$sum", "$pktCnt"));
			groupFields.put("bandWidth", new BasicDBObject("$sum", "$bandWidth"));

			DBObject sortFields = new BasicDBObject(sSortField, -1);
		    
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

	public Iterable<DBObject> ServiceTopNCondition(String sCol, int assetCode, String sStartDay, String sEndDay, int nPort, String sSortField) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);

			DBCollection dbCollection = reportDB.getCollection("N_DDOS_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			if (sSortField.equals("pktCnt")) {
				condition.put("rptGubun", 19);
			} else if (sSortField.equals("bandWidth")) {
				condition.put("rptGubun", 29);
			} else {
				condition.put("rptGubun", 9);
			}
			condition.put("destPort", nPort);

			DBObject fields = new BasicDBObject();
			fields.put("_id", 0);
			fields.put("message", "$_id.message");
			fields.put("destIp", "$_id.destIp");
			fields.put("action", "$_id.action");
			fields.put("count", 1);
			fields.put("pktCnt", 1);
			fields.put("bandWidth", 1);

			BasicDBObject keys = new BasicDBObject();
			keys.put("message", "$message");
			keys.put("destIp", "$destIp");
			keys.put("action", "$action");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject("$sum", "$count"));
			groupFields.put("pktCnt", new BasicDBObject("$sum", "$pktCnt"));
			groupFields.put("bandWidth", new BasicDBObject("$sum", "$bandWidth"));

			DBObject sortFields = new BasicDBObject(sSortField, -1);
		    
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
	
	public Iterable<DBObject> IpTopNTrend(String sCol, int assetCode, String sStartDay, String sEndDay, List<String> ips, boolean bSrcIp, String sSortField) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);

			DBCollection dbCollection = reportDB.getCollection("N_DDOS_" + assetCode + "_" + sCol);

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			if (sSortField.equals("pktCnt")) {
				condition.put("rptGubun", 15);
			} else if (sSortField.equals("bandWidth")) {
				condition.put("rptGubun", 25);
			} else {
				condition.put("rptGubun", 5);
			}
			
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
			fields.put(sSortField, 1);
			
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
			groupFields.put(sSortField, new BasicDBObject( "$sum", "$" + sSortField));
			
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
		}
	}
	
}
