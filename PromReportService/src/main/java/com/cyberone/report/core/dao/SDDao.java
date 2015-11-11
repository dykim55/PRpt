package com.cyberone.report.core.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Repository
public class SDDao {

	@Autowired
	private SqlSession sqlSession;

    @Autowired
    MongoTemplate mongoProm;

    @Autowired
    MongoTemplate mongoReport;
    
	public List<DBObject> FW_SessionLogMonth(int assetCode, int nDirection, String sEndDay, int nSearchPort) throws Exception {

		DBCursor dbCursor = null;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		try {
			Date endDate = sdf.parse(sEndDay); // 조회월에 한달을 더하고 1초를 빼 조회월 마지막날 23:59:59초를 구한다.
			Calendar endCal = getCalendar(endDate);
			endCal.add(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			endCal.add(Calendar.SECOND, -1);
			
			Date startDate = sdf.parse(sEndDay); 
			Calendar startCal = getCalendar(startDate);
			startCal.add(Calendar.MONTH, -5);
			
			DBCollection dbCollection = mongoReport.getCollection("FW_" + assetCode + "_MON");

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

			dbCursor = dbCollection.find(condition).sort(sortFields);

		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
		
		return dbCursor != null ? dbCursor.toArray() : new ArrayList<DBObject>();
	}
	
	//관제대상 장비현황
	public List<HashMap<String, Object>> getAssetList(String[] arrAssets) throws Exception {
		
		List<HashMap<String, Object>> assetList = new ArrayList<HashMap<String, Object>>();
		 
		SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		DBCollection dbCollection = mongoProm.getCollection("Product");
		BasicDBObject condition = new BasicDBObject();
		condition.put("deleted", false);
		DBCursor dbCursor = dbCollection.find(condition);
		HashMap<String, DBObject> productMap = new HashMap<String, DBObject>();
		for (DBObject obj : dbCursor) {
			productMap.put(((Integer)obj.get("productCode")).toString(), obj);
		}
		
		dbCollection = mongoProm.getCollection("Asset");
		
		for (int i = 0; i < arrAssets.length; i++) {
			String[] saTemp = arrAssets[i].split("_");
			BasicDBObject searchQuery = new BasicDBObject("assetCode", Integer.parseInt(saTemp[0]));
			DBObject asset = dbCollection.findOne(searchQuery);
			
			DBCollection policyCollection = mongoProm.getCollection("AssetPolicy");
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
	
	public Calendar getCalendar(Date date) {
		Calendar cal = new GregorianCalendar( TimeZone.getTimeZone( "GMT+09:00"), Locale.KOREA );
		cal.setTime(date);
		return cal;
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

	public String lZero(int n) {
		return n < 10 ? "0" + n : String.valueOf(n);
	}
	
}
