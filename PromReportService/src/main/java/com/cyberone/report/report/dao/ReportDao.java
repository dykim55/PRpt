package com.cyberone.report.report.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Repository
public class ReportDao {

	@Autowired
	private SqlSession sqlSession;

	public List<DBObject> selectAssetGroups(DB db, int domainCode, int parentCode, boolean deleted) throws Exception {

		DBObject condition = new BasicDBObject();
		DBObject sortFields = new BasicDBObject();

		condition.put("domainCode", domainCode);
		if (!deleted) { condition.put("deleted", false); }
		if (parentCode >= 0) { 
			condition.put("parentCode", parentCode);
			sortFields.put("groupName", 1);
		} else {
			sortFields.put("parentCode", 1);
			sortFields.put("groupName", 1);
		}
		
		DBCollection collection =  db.getCollection("AssetGroup");
		
		try (DBCursor dbCursor = collection.find(condition).sort(sortFields)) {
			return dbCursor != null ? dbCursor.toArray() : new ArrayList<DBObject>();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}
	
	public List<DBObject> selectAssetGroups(DB db, int domainCode, int[] groupCodes)	throws Exception {

		DBObject condition = new BasicDBObject();
		DBObject sortFields = new BasicDBObject();
		condition.put("domainCode", domainCode);
		condition.put("deleted", false);
		condition.put("groupCode", new BasicDBObject("$in", groupCodes));
		sortFields.put("groupName", 1);
		
		DBCollection collection =  db.getCollection("AssetGroup");

		try (DBCursor dbCursor = collection.find(condition).sort(sortFields)) {
			return dbCursor != null ? dbCursor.toArray() : new ArrayList<DBObject>();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}

	public List<DBObject> selectProductGroups(DB db, boolean deleted)	throws Exception {
		DBObject condition = new BasicDBObject();
		DBObject sortFields = new BasicDBObject();
		condition.put("deleted", deleted);
		sortFields.put("productGroupName", 1);
		
		DBCollection collection =  db.getCollection("ProductGroup");

		try (DBCursor dbCursor = collection.find(condition).sort(sortFields)) {
			return dbCursor != null ? dbCursor.toArray() : new ArrayList<DBObject>();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}

	public List<DBObject> selectAssetsProduct(DB db, int groupCode, int productCode) throws Exception {

		DBObject condition = new BasicDBObject();
		DBObject sortFields = new BasicDBObject();
		condition.put("groupCode", groupCode);
		condition.put("deleted", false);
		if (productCode > 999) {
			condition.put("productCode", productCode);
		} else if (productCode > 0 && productCode <= 999) {
			int maxCode = productCode * 1000 + 999;
			int minCode = productCode * 1000;
			condition.put("productCode", new BasicDBObject("$gte", minCode).append("$lte", maxCode));
		}
		sortFields.put("assetName", 1);
		
		DBCollection collection =  db.getCollection("Asset");

		try (DBCursor dbCursor = collection.find(condition).sort(sortFields)) {
			return dbCursor != null ? dbCursor.toArray() : new ArrayList<DBObject>();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}

	public DBObject selectAsset(DB db, int assetCode) throws Exception {
		DBObject condition = new BasicDBObject();
		DBObject dbObj = null;
		DBCollection collection =  db.getCollection("Asset");
		try {
			condition.put("assetCode", assetCode);
			dbObj = collection.findOne(condition);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
		return dbObj;
	}
	
	public DBObject selectAssetPolicy(DB db, int assetCode) throws Exception {

		DBObject condition = new BasicDBObject();
		DBObject dbObj = null;
		
		DBCollection collection =  db.getCollection("AssetPolicy");

		try {
			condition.put("assetCode", assetCode);
			
			dbObj = collection.findOne(condition);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
			
		return dbObj;
	}

	public List<DBObject> selectProductParsers(DB db, int productCode, boolean deleted)	throws Exception {
		DBObject condition = new BasicDBObject();
		DBObject sortFields = new BasicDBObject();
		condition.put("productCode", productCode);
		condition.put("deleted", deleted);
		sortFields.put("productCode", 1);

		DBCollection collection =  db.getCollection("ProductParser");

		try (DBCursor dbCursor = collection.find(condition).sort(sortFields)) {
			return dbCursor != null ? dbCursor.toArray() : new ArrayList<DBObject>();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}

	public DBObject selectStatus(DB db, int assetCode) throws Exception {

		DBObject condition = new BasicDBObject();
		DBObject dbObj = null;
		
		DBCollection collection =  db.getCollection("AssetStatus");

		try {
			condition.put("assetCode", assetCode);
			
			dbObj = collection.findOne(condition);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
			
		return dbObj;
	}

	public BasicDBList getAccessAssetGroups(DB db, String sLoginID) throws Exception {

		DBCollection colAdmin =  db.getCollection("Admin");
		DBObject dbAdmin = colAdmin.findOne(new BasicDBObject("loginID", sLoginID));

		DBObject condition = new BasicDBObject("adminCode", dbAdmin.get("adminCode"));
		DBObject dbObj = null;
		
		DBCollection collection =  db.getCollection("AdminAccessPower");
		try {
			dbObj = collection.findOne(condition);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
		
		return (BasicDBList)dbObj.get("assetGroupCodes");
	}

	public DBObject selectReportOption(DB db, int groupCode) throws Exception {
		DBObject condition = new BasicDBObject();
		DBObject dbObj = null;
		DBCollection collection =  db.getCollection("AssetReportOption");
		try {
			condition.put("groupCode", groupCode);
			dbObj = collection.findOne(condition);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
		return dbObj;
	}

	public List<HashMap<String, Object>> getCscoCd(List<String> assetCodes) throws Exception {
		return sqlSession.selectList("ServiceDesk.selectCscoEqpm_RefCd", assetCodes);
	}
	
}
