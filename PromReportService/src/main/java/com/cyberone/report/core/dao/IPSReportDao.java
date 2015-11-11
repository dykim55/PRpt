package com.cyberone.report.core.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class IPSReportDao extends BaseDao {

	public IPSReportDao(DB promDb, DB rptDb)	{
		super(promDb, rptDb);
	}

	public List<DBObject> IPS_SessionLogMonth(int assetCode, String sEndDay) throws Exception {
		
		StartTimeCheck("IPS_SessionLogMonth");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay); // 조회월에 한달을 더하고 1초를 빼 조회월 마지막날 23:59:59초를 구한다.
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay); 
			Calendar startCal = getCalendar(startDate);
			startCal.add(Calendar.MONTH, -5);
			
			DBCollection dbCollection = rptDb.getCollection("IPS_" + assetCode + "_MON");

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

	public List<DBObject> IPS_SessionLogDay(int assetCode, String sStartDay, String sEndDay) throws Exception {
		
		StartTimeCheck("IPS_SessionLogDay");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			startCal.add(Calendar.MONTH, -1);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);

			DBCollection dbCollection = rptDb.getCollection("IPS_" + assetCode + "_DY");

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

	public List<DBObject> IPS_TopInfoMonth(int assetCode, String sEndDay, String sAction, int nField, int nLimit) throws Exception {
		
		StartTimeCheck("IPS_TopInfoMonth");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);
			
			DBCollection dbCollection = rptDb.getCollection("IPS_" + assetCode + "_MON");

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

	public List<DBObject> IPS_PrevMonthInfo(int assetCode, String sEndDay, String sAction, int nField, List<String> lstFields) throws Exception {
		
		StartTimeCheck("IPS_PrevMonthInfo");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);
			startCal.add(Calendar.MONTH, -1);
			
			DBCollection dbCollection = rptDb.getCollection("IPS_" + assetCode + "_MON");

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
	
	public List<DBObject> IPS_Top10EventChange(int assetCode, String sStartDay, String sEndDay, String sAction, List<String> events) throws Exception {
		
		StartTimeCheck("IPS_Top10EventChange");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = sdf.parse(sStartDay);
			Calendar startCal = getCalendar(startDate);
			
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, 1);
			endCal.add(Calendar.SECOND, -1);
			
			DBCollection dbCollection = rptDb.getCollection("IPS_" + assetCode + "_DY");
	
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

	public List<DBObject> IPS_DataVariation(int assetCode, String sEndDay, String sAction, int nField) throws Exception {
		
		StartTimeCheck("IPS_DataVariation");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);
			startCal.add(Calendar.MONTH, -1);
			
			DBCollection dbCollection = rptDb.getCollection("IPS_" + assetCode + "_MON");
	
			BasicDBObject condition = new BasicDBObject();
			condition.put("rptDate", new BasicDBObject("$gte", startCal.getTime()).append("$lte", endCal.getTime()));
			condition.put("rptGubun", 5);
			condition.put("action", sAction);
			
			DBCursor dbCursor = null;
			
			if (nField == 1) {
				condition.put("message", new BasicDBObject("$ne", null));
			} else if (nField == 2) {
				condition.put("destPort", new BasicDBObject("$ne", null));
			}
			
			DBObject sortFields = new BasicDBObject();
			sortFields.put("year", 1);
			sortFields.put("month", 1);
			sortFields.put("count", -1);
			
			dbCursor = dbCollection.find(condition).sort(sortFields);
	
			return dbCursor.toArray();
			
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			EndTimeCheck();
		}
	}

	public List<DBObject> IPS_Top10Condition_1st(int assetCode, String sEndDay, String sAction, int nField, String sField) throws Exception {
		
		StartTimeCheck("IPS_Top10Condition_1st");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);

			DBCollection dbCollection = rptDb.getCollection("IPS_" + assetCode + "_MON");

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
	
	public List<DBObject> IPS_Top10Condition_2nd(int assetCode, String sEndDay, String sAction, int nField, String sField1, String sField2) throws Exception {
		
		StartTimeCheck("IPS_Top10Condition_2nd");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay);
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay);
			Calendar startCal = getCalendar(startDate);

			DBCollection dbCollection = rptDb.getCollection("IPS_" + assetCode + "_MON");

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
}
