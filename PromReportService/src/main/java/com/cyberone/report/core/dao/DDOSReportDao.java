package com.cyberone.report.core.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class DDOSReportDao extends BaseDao {

	public DDOSReportDao(DB promDb, DB rptDb)	{
		super(promDb, rptDb);
	}
	
	public Iterable<DBObject> DDOS_HalfYearChange(int assetCode, String sEndDay) throws Exception {
		
		StartTimeCheck("DDOS_HalfYearChange");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay); // 조회월에 한달을 더하고 1초를 빼 조회월 마지막날 23:59:59초를 구한다.
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay); 
			Calendar startCal = getCalendar(startDate);
			startCal.add(Calendar.MONTH, -5);
			
			DBCollection dbCollection = rptDb.getCollection("DDOS_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			DBObject match = new BasicDBObject("$match", condition);
			
			DBObject project = new BasicDBObject();
			DBObject fields = new BasicDBObject();
			fields.put("year", 1);
			fields.put("month", 1);
			fields.put("count", 1);
			fields.put("pktCnt", 1);
			fields.put("bandwidth", 1);
			project.put("$project", fields);
			
			BasicDBObject keys = new BasicDBObject();
			keys.put("year", "$year");
			keys.put("month", "$month");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			groupFields.put("pktCnt", new BasicDBObject( "$sum", "$pktCnt"));
			groupFields.put("bandwidth", new BasicDBObject( "$sum", "$bandwidth"));
			
			DBObject group = new BasicDBObject("$group", groupFields);
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("_id.year", 1);
			sortFields.put("_id.month", 1);
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

	public Iterable<DBObject> DDOS_HalfYearChangeOfGroup(int assetCode, String sEndDay) throws Exception {
		
		StartTimeCheck("DDOS_HalfYearChangeOfGroup");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay); // 조회월에 한달을 더하고 1초를 빼 조회월 마지막날 23:59:59초를 구한다.
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay); 
			Calendar startCal = getCalendar(startDate);
			startCal.add(Calendar.MONTH, -5);
			
			DBCollection dbCollection = rptDb.getCollection("DDOS_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			DBObject match = new BasicDBObject("$match", condition);
			
			DBObject project = new BasicDBObject();
			DBObject fields = new BasicDBObject();
			fields.put("group", 1);
			fields.put("year", 1);
			fields.put("month", 1);
			fields.put("count", 1);
			fields.put("pktCnt", 1);
			fields.put("bandwidth", 1);
			project.put("$project", fields);
			
			BasicDBObject keys = new BasicDBObject();
			keys.put("group", "$group");
			keys.put("year", "$year");
			keys.put("month", "$month");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			groupFields.put("pktCnt", new BasicDBObject( "$sum", "$pktCnt"));
			groupFields.put("bandwidth", new BasicDBObject( "$sum", "$bandwidth"));
			DBObject group = new BasicDBObject("$group", groupFields);
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("_id.group", 1);
			sortFields.put("_id.year", 1);
			sortFields.put("_id.month", 1);
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
	
	public Iterable<DBObject> DDOS_DailyChange(int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		StartTimeCheck("DDOS_DailyChange");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);

			DBCollection dbCollection = rptDb.getCollection("DDOS_" + assetCode + "_DY");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			DBObject match = new BasicDBObject("$match", condition);
			
			DBObject project = new BasicDBObject();
			DBObject fields = new BasicDBObject();
			fields.put("year", 1);
			fields.put("month", 1);
			fields.put("day", 1);
			fields.put("count", 1);
			fields.put("pktCnt", 1);
			fields.put("bandwidth", 1);
			project.put("$project", fields);
			
			BasicDBObject keys = new BasicDBObject();
			keys.put("year", "$year");
			keys.put("month", "$month");
			keys.put("day", "$day");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			groupFields.put("pktCnt", new BasicDBObject( "$sum", "$pktCnt"));
			groupFields.put("bandwidth", new BasicDBObject( "$sum", "$bandwidth"));
			DBObject group = new BasicDBObject("$group", groupFields);
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("_id.year", 1);
			sortFields.put("_id.month", 1);
			sortFields.put("_id.day", 1);
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

	public Iterable<DBObject> DDOS_DailyChangeOfGroup(int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		StartTimeCheck("DDOS_DailyChangeOfGroup");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);

			DBCollection dbCollection = rptDb.getCollection("DDOS_" + assetCode + "_DY");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			DBObject match = new BasicDBObject("$match", condition);
			
			DBObject project = new BasicDBObject();
			DBObject fields = new BasicDBObject();
			fields.put("group", 1);
			fields.put("year", 1);
			fields.put("month", 1);
			fields.put("day", 1);
			fields.put("count", 1);
			fields.put("pktCnt", 1);
			fields.put("bandwidth", 1);
			project.put("$project", fields);
			
			BasicDBObject keys = new BasicDBObject();
			keys.put("group", "$group");
			keys.put("year", "$year");
			keys.put("month", "$month");
			keys.put("day", "$day");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			groupFields.put("pktCnt", new BasicDBObject( "$sum", "$pktCnt"));
			groupFields.put("bandwidth", new BasicDBObject( "$sum", "$bandwidth"));
			DBObject group = new BasicDBObject("$group", groupFields);
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("_id.group", 1);
			sortFields.put("_id.year", 1);
			sortFields.put("_id.month", 1);
			sortFields.put("_id.day", 1);
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

	public Iterable<DBObject> DDOS_Top10ByGroup(int assetCode, String sEndDay) throws Exception {
		
		StartTimeCheck("DDOS_Top10ByGroup");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);

			DBCollection dbCollection = rptDb.getCollection("DDOS_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			DBObject match = new BasicDBObject("$match", condition);
			
			DBObject project = new BasicDBObject();
			DBObject fields = new BasicDBObject();
			fields.put("group", 1);
			fields.put("count", 1);
			project.put("$project", fields);
			
			BasicDBObject keys = new BasicDBObject();
			keys.put("group", "$group");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			DBObject group = new BasicDBObject("$group", groupFields);
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("count", -1);
			
		    DBObject sort = new BasicDBObject("$sort", sortFields );

		    DBObject limit = new BasicDBObject("$limit", 10 );
		    
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

	public Iterable<DBObject> DDOS_Top10ByEvent(int assetCode, String sEndDay, int nPeriod, int nSort, int nLimit) throws Exception {
		
		StartTimeCheck("DDOS_Top10ByEvent");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);
			if (nPeriod != 0)  {
				startCal.add(Calendar.MONTH, nPeriod);
			}

			DBCollection dbCollection = rptDb.getCollection("DDOS_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			DBObject match = new BasicDBObject("$match", condition);
			
			DBObject project = new BasicDBObject();
			DBObject fields = new BasicDBObject();
			fields.put("year", 1);
			fields.put("month", 1);
			fields.put("message", 1);
			fields.put("action", 1);
			fields.put("count", 1);
			fields.put("pktCnt", 1);
			fields.put("bandwidth", 1);
			project.put("$project", fields);
			
			BasicDBObject keys = new BasicDBObject();
			keys.put("year", "$year");
			keys.put("month", "$month");
			keys.put("message", "$message");
			keys.put("action", "$action");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			groupFields.put("pktCnt", new BasicDBObject( "$sum", "$pktCnt"));
			groupFields.put("bandwidth", new BasicDBObject( "$sum", "$bandwidth"));
			DBObject group = new BasicDBObject("$group", groupFields);
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("_id.year", 1);
			sortFields.put("_id.month", 1);
			if (nSort == 1) {
				sortFields.put("count", -1);
			} else if (nSort == 2) {
				sortFields.put("pktCnt", -1);
			} else if (nSort == 3) {
				sortFields.put("bandwidth", -1);
			}
		    DBObject sort = new BasicDBObject("$sort", sortFields );

		    AggregationOutput output = null;
		    if (nLimit > 0) {
		    	DBObject limit = new BasicDBObject("$limit", nLimit );
		    	output = dbCollection.aggregate(match, project, group, sort, limit);
		    } else {
		    	output = dbCollection.aggregate(match, project, group, sort);
		    }

			return output.results();
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}

	public Iterable<DBObject> DDOS_Top10ByIp(int assetCode, String sEndDay, int nField) throws Exception {
		
		StartTimeCheck("DDOS_Top10ByIp");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);

			DBCollection dbCollection = rptDb.getCollection("DDOS_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			DBObject match = new BasicDBObject("$match", condition);
			
			DBObject project = new BasicDBObject();
			DBObject fields = new BasicDBObject();
			fields.put("year", 1);
			fields.put("month", 1);
			if (nField == 1) {
				fields.put("srcIp", 1);
			} else if (nField == 2) {
				fields.put("destIp", 1);
			}
			fields.put("count", 1);
			project.put("$project", fields);
			
			BasicDBObject keys = new BasicDBObject();
			keys.put("year", "$year");
			keys.put("month", "$month");
			if (nField == 1) {
				keys.put("srcIp", "$srcIp");
			} else if (nField == 2) {
				keys.put("destIp", "$destIp");
			}
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			DBObject group = new BasicDBObject("$group", groupFields);
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("_id.year", 1);
			sortFields.put("_id.month", 1);
			sortFields.put("count", -1);
		    DBObject sort = new BasicDBObject("$sort", sortFields );

	    	DBObject limit = new BasicDBObject("$limit", 10);
	    	AggregationOutput output = dbCollection.aggregate(match, project, group, sort, limit);

			return output.results();
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}

	public Iterable<DBObject> DDOS_PrevInfo(int assetCode, String sEndDay, int nField, List<String> lstFields) throws Exception {
		
		StartTimeCheck("DDOS_Top10ByIp");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);
			startCal.add(Calendar.MONTH, -1);
			
			DBCollection dbCollection = rptDb.getCollection("DDOS_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			if (nField == 1) {
				condition.put("srcIp", new BasicDBObject("$in", lstFields));
			} else if (nField == 2) {
				condition.put("destIp", new BasicDBObject("$in", lstFields));
			}
			DBObject match = new BasicDBObject("$match", condition);
			
			DBObject project = new BasicDBObject();
			DBObject fields = new BasicDBObject();
			if (nField == 1) {
				fields.put("srcIp", 1);
			} else if (nField == 2) {
				fields.put("destIp", 1);
			}
			fields.put("count", 1);
			project.put("$project", fields);
			
			BasicDBObject keys = new BasicDBObject();
			if (nField == 1) {
				keys.put("srcIp", "$srcIp");
			} else if (nField == 2) {
				keys.put("destIp", "$destIp");
			}
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			DBObject group = new BasicDBObject("$group", groupFields);
			
	    	AggregationOutput output = dbCollection.aggregate(match, project, group);

			return output.results();
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}
	
	public Iterable<DBObject> DDOS_Top10ByEventDetail(int assetCode, String sEndDay, int nField) throws Exception {
		
		StartTimeCheck("DDOS_Top10ByEventDetail");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);

			DBCollection dbCollection = rptDb.getCollection("DDOS_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			DBObject match = new BasicDBObject("$match", condition);
			
			DBObject project = new BasicDBObject();
			DBObject fields = new BasicDBObject();
			fields.put("message", 1);
			fields.put("group", 1);
			fields.put("count", 1);
			fields.put("pktCnt", 1);
			fields.put("bandwidth", 1);
			project.put("$project", fields);
			
			BasicDBObject keys = new BasicDBObject();
			keys.put("message", "$message");
			keys.put("group", "$group");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			groupFields.put("pktCnt", new BasicDBObject( "$sum", "$pktCnt"));
			groupFields.put("bandwidth", new BasicDBObject( "$sum", "$bandwidth"));
			DBObject group = new BasicDBObject("$group", groupFields);
			
			DBObject sortFields = new BasicDBObject();
			if (nField == 1) {
				sortFields.put("count", -1);
			} else if (nField == 2) {
				sortFields.put("pktCnt", -1);
			} else if (nField == 3) {
				sortFields.put("bandwidth", -1);
			}
		    DBObject sort = new BasicDBObject("$sort", sortFields );

		    DBObject limit = new BasicDBObject("$limit", 10);
		    
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

	public Iterable<DBObject> DDOS_EventByGroup(int assetCode, String sEndDay, String sGroup) throws Exception {
		
		StartTimeCheck("DDOS_EventByGroup");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);

			DBCollection dbCollection = rptDb.getCollection("DDOS_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			condition.put("group", sGroup);
			DBObject match = new BasicDBObject("$match", condition);

			DBObject project = new BasicDBObject();
			DBObject fields = new BasicDBObject();
			fields.put("message", 1);
			fields.put("count", 1);
			fields.put("pktCnt", 1);
			fields.put("bandwidth", 1);
			project.put("$project", fields);

			BasicDBObject keys = new BasicDBObject();
			keys.put("message", "$message");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			groupFields.put("pktCnt", new BasicDBObject( "$sum", "$pktCnt"));
			groupFields.put("bandwidth", new BasicDBObject( "$sum", "$bandwidth"));
			DBObject group = new BasicDBObject("$group", groupFields);

			DBObject sortFields = new BasicDBObject();
			sortFields.put("count", -1);
		    DBObject sort = new BasicDBObject("$sort", sortFields);
			
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
	
	public Iterable<DBObject> DDOS_EventDetail_1st(int assetCode, String sEndDay, String sMessage, int nField) throws Exception {
		
		StartTimeCheck("DDOS_EventDetail_1st");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);

			DBCollection dbCollection = rptDb.getCollection("DDOS_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			condition.put("message", sMessage);
			DBObject match = new BasicDBObject("$match", condition);
			
			DBObject project = new BasicDBObject();
			DBObject fields = new BasicDBObject();
			fields.put("srcIp", 1);
			fields.put("count", 1);
			fields.put("pktCnt", 1);
			fields.put("bandwidth", 1);
			project.put("$project", fields);
			
			BasicDBObject keys = new BasicDBObject();
			keys.put("srcIp", "$srcIp");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			groupFields.put("pktCnt", new BasicDBObject( "$sum", "$pktCnt"));
			groupFields.put("bandwidth", new BasicDBObject( "$sum", "$bandwidth"));
			DBObject group = new BasicDBObject("$group", groupFields);
			
			DBObject sortFields = new BasicDBObject();
			if (nField == 1) {
				sortFields.put("count", -1);
			} else if (nField == 2) {
				sortFields.put("pktCnt", -1);
			} else if (nField == 3) {
				sortFields.put("bandwidth", -1);
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
	
	public Iterable<DBObject> DDOS_EventDetail_2nd(int assetCode, String sEndDay, String sMessage, String sSrcIp, int nField) throws Exception {
		
		StartTimeCheck("DDOS_EventDetail_2nd");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);

			DBCollection dbCollection = rptDb.getCollection("DDOS_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			condition.put("message", sMessage);
			condition.put("srcIp", sSrcIp);
			DBObject match = new BasicDBObject("$match", condition);
			
			DBObject project = new BasicDBObject();
			DBObject fields = new BasicDBObject();
			fields.put("destIp", 1);
			fields.put("count", 1);
			fields.put("pktCnt", 1);
			fields.put("bandwidth", 1);
			project.put("$project", fields);
			
			BasicDBObject keys = new BasicDBObject();
			keys.put("destIp", "$destIp");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			groupFields.put("pktCnt", new BasicDBObject( "$sum", "$pktCnt"));
			groupFields.put("bandwidth", new BasicDBObject( "$sum", "$bandwidth"));
			DBObject group = new BasicDBObject("$group", groupFields);
			
			DBObject sortFields = new BasicDBObject();
			if (nField == 1) {
				sortFields.put("count", -1);
			} else if (nField == 2) {
				sortFields.put("pktCnt", -1);
			} else if (nField == 3) {
				sortFields.put("bandwidth", -1);
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
	
	public Iterable<DBObject> DDOS_IpDetail_1st(int assetCode, String sEndDay, String sIp, int nField) throws Exception {
		
		StartTimeCheck("DDOS_IpDetail_1st");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);

			DBCollection dbCollection = rptDb.getCollection("DDOS_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			if (nField == 1) {
				condition.put("srcIp", sIp);
			} else if (nField == 2) {
				condition.put("destIp", sIp);
			}
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

	public Iterable<DBObject> DDOS_IpDetail_2nd(int assetCode, String sEndDay, String sIp, String sMessage, int nField) throws Exception {
		
		StartTimeCheck("DDOS_IpDetail_2nd");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);

			DBCollection dbCollection = rptDb.getCollection("DDOS_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			if (nField == 1) {
				condition.put("srcIp", sIp);
			} else if (nField == 2) {
				condition.put("destIp", sIp);
			}
			condition.put("message", sMessage);
			DBObject match = new BasicDBObject("$match", condition);
			
			DBObject project = new BasicDBObject();
			DBObject fields = new BasicDBObject();
			if (nField == 1) {
				fields.put("destIp", 1);	
			} else if (nField == 2) {
				fields.put("srcIp", 1);
			}
			fields.put("count", 1);
			fields.put("pktCnt", 1);
			fields.put("bandwidth", 1);
			project.put("$project", fields);
			
			BasicDBObject keys = new BasicDBObject();
			if (nField == 1) {
				keys.put("destIp", "$destIp");	
			} else if (nField == 2) {
				keys.put("srcIp", "$srcIp");
			}
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			groupFields.put("pktCnt", new BasicDBObject( "$sum", "$pktCnt"));
			groupFields.put("bandwidth", new BasicDBObject( "$sum", "$bandwidth"));
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
	
	public Iterable<DBObject> DDOS_ServiceSessionLog(int assetCode, int nDirection, String sAction, String sEndDay, boolean bCountSort) throws Exception {
		
		StartTimeCheck("DDOS_ServiceSessionLog");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);

			DBCollection dbCollection = rptDb.getCollection("DDOS_" + assetCode + "_MON");
			
			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			condition.put("direction", nDirection);
			condition.put("action", sAction);
			DBObject match = new BasicDBObject("$match", condition);
			
			DBObject project = new BasicDBObject();
			DBObject fields = new BasicDBObject();
			fields.put("destPort", 1);
			fields.put("year", 1);
			fields.put("month", 1);
			fields.put("count", 1);
			fields.put("trafficSize", 1);
			project.put("$project", fields);
			
			BasicDBObject keys = new BasicDBObject();
			keys.put("year", "$year");
			keys.put("month", "$month");
			keys.put("destPort", "$destPort");
			DBObject groupFields = new BasicDBObject( "_id", keys);
			groupFields.put("count", new BasicDBObject( "$sum", "$count"));
			groupFields.put("trafficSize", new BasicDBObject( "$sum", "$trafficSize"));
			DBObject group = new BasicDBObject("$group", groupFields);
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("_id.year", 1);
			sortFields.put("_id.month", 1);
			if (bCountSort) {
				sortFields.put("count", -1);
			} else {
				sortFields.put("trafficSize", -1);
			}
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
	
	
	public Iterable<DBObject> DDOS_Top10LogDetail(int assetCode, String sStartDay, String sEndDay, int nDirection, String sAction, List<String> ips, boolean bSrcIp) throws Exception {
		
		StartTimeCheck("DDOS_Top10LogDetail");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);

			DBCollection dbCollection = rptDb.getCollection("DDOS_" + assetCode + "_DY");

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
	
	
	public Iterable<DBObject> DDOS_Top10ServiceCondition(int assetCode, String sEndDay, int nDirection, String sAction, int port, boolean bCountSort) throws Exception {
		
		StartTimeCheck("DDOS_Top10ServiceCondition");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);

			DBCollection dbCollection = rptDb.getCollection("DDOS_" + assetCode + "_MON");

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

	public List<DBObject> DDOS_SessionLogMonth(int assetCode, String sEndDay) throws Exception {
		
		StartTimeCheck("DDOS_SessionLogMonth");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay); // 조회월에 한달을 더하고 1초를 빼 조회월 마지막날 23:59:59초를 구한다.
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay); 
			Calendar startCal = getCalendar(startDate);
			startCal.add(Calendar.MONTH, -5);
			
			DBCollection dbCollection = rptDb.getCollection("DDOS_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 4);
			
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
	
	public List<DBObject> DDOS_SessionLogDay(int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		StartTimeCheck("DDOS_SessionLogDay");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			startCal.add(Calendar.MONTH, -1);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = rptDb.getCollection("DDOS_" + assetCode + "_DY");

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
	
	public List<DBObject> DDOS_TopInfoMonth(int assetCode, String sEndDay, String sAction, int nField, int nLimit) throws Exception {
		
		StartTimeCheck("DDOS_TopInfoMonth");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);
			
			DBCollection dbCollection = rptDb.getCollection("DDOS_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 5);
			if (!sAction.isEmpty()) {
				condition.put("action", sAction);
			}
			
			DBCursor dbCursor = null;
			if (nLimit > 0) {
				ArrayList<BasicDBObject> andList = new ArrayList<BasicDBObject>();
				if (nField == 1) {
					andList.add(new BasicDBObject("message", new BasicDBObject("$ne", null)));
					andList.add(new BasicDBObject("message", new BasicDBObject("$ne", "-1")));
				} else if (nField == 2) {
					andList.add(new BasicDBObject("destPort", new BasicDBObject("$ne", null)));
					andList.add(new BasicDBObject("destPort", new BasicDBObject("$ne", -1)));
				} else if (nField == 3) {
					andList.add(new BasicDBObject("srcIp", new BasicDBObject("$ne", null)));
					andList.add(new BasicDBObject("srcIp", new BasicDBObject("$ne", "-1")));
				} else if (nField == 4) {
					andList.add(new BasicDBObject("destIp", new BasicDBObject("$ne", null)));
					andList.add(new BasicDBObject("destIp", new BasicDBObject("$ne", "-1")));
				}
				condition.put("$and", andList);				
				
				dbCursor = dbCollection.find(condition).sort(new BasicDBObject("count", -1)).limit(nLimit);
			} else {
				if (nField == 1) {
					condition.put("message", new BasicDBObject("$ne", null));
				} else if (nField == 2) {
					condition.put("destPort", new BasicDBObject("$ne", null));
				} else if (nField == 3) {
					condition.put("srcIp", new BasicDBObject("$ne", null));
				} else if (nField == 4) {
					condition.put("destIp", new BasicDBObject("$ne", null));
				}
				
				dbCursor = dbCollection.find(condition).sort(new BasicDBObject("count", -1));
			}
			
			return dbCursor.toArray();

		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}

	public List<DBObject> DDOS_Top10EventChange(int assetCode, String sStartDay, String sEndDay, String sAction, List<String> events) throws Exception {
		
		StartTimeCheck("DDOS_Top10EventChange");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
	
			DBCollection dbCollection = rptDb.getCollection("DDOS_" + assetCode + "_DY");
	
			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 5);
			condition.put("action", sAction);
			condition.put("message", new BasicDBObject("$in", events));
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("message", 1);
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
	
	public List<DBObject> DDOS_DataVariation(int assetCode, String sEndDay, String sAction, int nField) throws Exception {
		
		StartTimeCheck("DDOS_DataVariation");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);
			startCal.add(Calendar.MONTH, -1);
			
			DBCollection dbCollection = rptDb.getCollection("DDOS_" + assetCode + "_MON");
	
			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 5);
			condition.put("action", sAction);
	
			if (nField == 1) {
				condition.put("message", new BasicDBObject("$ne", null));
			} else if (nField == 2) {
				condition.put("destPort", new BasicDBObject("$ne", null));
			}
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("year", 1);
			sortFields.put("month", 1);
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
	
	public List<DBObject> DDOS_Top10Condition_1st(int assetCode, String sEndDay, String sAction, int nField, String sField) throws Exception {
		
		StartTimeCheck("DDOS_Top10Condition_1st");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);

			DBCollection dbCollection = rptDb.getCollection("DDOS_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("action", sAction);

			if (nField == 1) {
				condition.put("rptGubun", 6);
				condition.put("message", sField);
				condition.put("srcIp", new BasicDBObject("$ne", "-1"));
			} else if (nField == 2) {
				condition.put("rptGubun", 7);
				condition.put("srcIp", sField);
				condition.put("message", new BasicDBObject("$ne", "-1"));
			} else if (nField == 3) {
				condition.put("rptGubun", 8);
				condition.put("destIp", sField);
				condition.put("message", new BasicDBObject("$ne", "-1"));
			}
			
			DBCursor dbCursor = dbCollection.find(condition).sort(new BasicDBObject("count", -1)).limit(5);
			
			return dbCursor.toArray();
			
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}
	
	public List<DBObject> DDOS_Top10Condition_2nd(int assetCode, String sEndDay, String sAction, int nField, String sField1, String sField2) throws Exception {
		
		StartTimeCheck("DDOS_Top10Condition_2nd");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);

			DBCollection dbCollection = rptDb.getCollection("DDOS_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 2);
			condition.put("action", sAction);
			
			if (nField == 1) {
				condition.put("message", sField1);
				condition.put("srcIp", sField2);
			} else if (nField == 2) {
				condition.put("srcIp", sField1);
				condition.put("message", sField2);
			} else if (nField == 3) {
				condition.put("destIp", sField1);
				condition.put("message", sField2);
			}
			
			DBCursor dbCursor = dbCollection.find(condition).sort(new BasicDBObject("count", -1)).limit(5);
			
			return dbCursor.toArray();
			
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}
	
	public List<DBObject> DDOS_PrevMonthInfo(int assetCode, String sEndDay, String sAction, int nField, List<String> lstFields) throws Exception {
		
		StartTimeCheck("DDOS_PrevMonthInfo");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);
			startCal.add(Calendar.MONTH, -1);
			
			DBCollection dbCollection = rptDb.getCollection("DDOS_" + assetCode + "_MON");

			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 5);
			if (!sAction.isEmpty()) {
				condition.put("action", sAction);
			}
			if (nField == 1) {
				condition.put("srcIp", new BasicDBObject("$in", lstFields));
			} else if (nField == 2) {
				condition.put("destIp", new BasicDBObject("$in", lstFields));
			} else if (nField == 3) {
				condition.put("message", new BasicDBObject("$in", lstFields));
			}
			
			DBCursor dbCursor = dbCollection.find(condition).sort(new BasicDBObject("count", -1));
			
			return dbCursor.toArray();

		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}
}
