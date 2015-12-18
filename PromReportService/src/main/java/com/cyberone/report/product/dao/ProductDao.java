package com.cyberone.report.product.dao;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Repository
public class ProductDao {

	@Autowired
	private SqlSession sqlSession;

	public List<DBObject> selectProductGroups(DB db) throws Exception {
		DBObject condition = new BasicDBObject("deleted", false);
		DBObject sortFields = new BasicDBObject("productGroupName", 1);
		DBCollection collection =  db.getCollection("ProductGroup");
		try (DBCursor dbCursor = collection.find(condition).sort(sortFields)) {
			return dbCursor != null ? dbCursor.toArray() : new ArrayList<DBObject>();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}
	
	public List<DBObject> selectProductGroups(DB db, boolean deleted) throws Exception {
		DBObject condition = new BasicDBObject();
		DBObject sortFields = new BasicDBObject();
		DBCollection collection =  db.getCollection("ProductGroup");
		condition.put("deleted", deleted);
		sortFields.put("productGroupName", 1);
		try (DBCursor dbCursor = collection.find(condition).sort(sortFields)) {
			return dbCursor != null ? dbCursor.toArray() : new ArrayList<DBObject>();			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}
	
	public List<DBObject> selectProducts(DB db, int productGroupCode) throws Exception {
		DBObject condition = new BasicDBObject();
		DBObject sortFields = new BasicDBObject();
		if (productGroupCode > 0) {
			condition.put("productGroupCode", productGroupCode);
		}
		condition.put("deleted", false);
		sortFields.put("productName", 1);
		
		DBCollection collection =  db.getCollection("Product");
		try (DBCursor dbCursor = collection.find(condition).sort(sortFields)) {
			return dbCursor != null ? dbCursor.toArray() : new ArrayList<DBObject>();		
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}

	public DBObject selectProduct(DB db, int productCode) throws Exception {
		DBObject condition = new BasicDBObject();
		DBObject dbObj = null;
		DBCollection collection =  db.getCollection("Product");
		try {
			condition.put("productCode", productCode);
			dbObj = collection.findOne(condition);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
		return dbObj;
	}

	public DBObject selectProductGroup(DB db, int productGroupCode) throws Exception {
		DBObject condition = new BasicDBObject();
		DBObject dbObj = null;
		DBCollection collection =  db.getCollection("ProductGroup");
		try {
			condition.put("productGroupCode", productGroupCode);
			dbObj = collection.findOne(condition);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
		return dbObj;
	}

	public List<DBObject> selectProductGroup(DB db) throws Exception {
		DBObject condition = new BasicDBObject("deleted", false);
		DBCollection collection =  db.getCollection("ProductGroup");
		try (DBCursor dbCursor = collection.find(condition).sort(new BasicDBObject("productGroupName",1))) {
			return dbCursor != null ? dbCursor.toArray() : new ArrayList<DBObject>();	
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}
	
	public List<DBObject> selectAutoReportFormList(DB db, int formType, int formReportType) throws Exception {
		DBObject condition = new BasicDBObject();
		condition.put("formType", formType);
		condition.put("formReportType", formReportType);
		
		DBCollection collection =  db.getCollection("AutoReportForm");
		try (DBCursor dbCursor = collection.find(condition).sort(new BasicDBObject("formName",1))) {
			return dbCursor != null ? dbCursor.toArray() : new ArrayList<DBObject>();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}

	public DBObject selectAutoReportForm(DB db, int formCode) throws Exception {
		DBObject condition = new BasicDBObject();
		DBObject dbObj = null;
		DBCollection collection =  db.getCollection("AutoReportForm");
		try {
			condition.put("formCode", formCode);
			dbObj = collection.findOne(condition);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
		return dbObj;
	}

	public DBObject selectAutoReportForm(DB db, int formReportType, int assetCode) throws Exception {
		DBObject condition = new BasicDBObject();
		DBObject dbObj = null;
		DBCollection collection =  db.getCollection("AutoReportForm");
		try {
			condition.put("formReportType", formReportType);
			condition.put("appliedAssets.code", assetCode);
			dbObj = collection.findOne(condition);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
		return dbObj;
	}
	
	public List<DBObject> selectFormInAsset(DB db, int assetCode) throws Exception {
		DBObject condition = new BasicDBObject();
		condition.put("appliedAssets.code", assetCode);
		
		DBCollection collection =  db.getCollection("AutoReportForm");
		try (DBCursor dbCursor = collection.find(condition).sort(new BasicDBObject("formReportType",1))) {
			return dbCursor != null ? dbCursor.toArray() : new ArrayList<DBObject>();	
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}
	
	public void insertAutoReportForm(DB db, DBObject dbObject) throws Exception {
		dbObject.put("formCode", getCodeTableSeq(db, "AutoReportForm"));

		DBCollection collection =  db.getCollection("AutoReportForm");
		try {
			collection.insert(dbObject);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}

	public void updateAutoReportForm(DB db, DBObject condition, DBObject dbObject) throws Exception {
		BasicDBObject updateDocument = new BasicDBObject();
		DBCollection collection =  db.getCollection("AutoReportForm");
		try {
			updateDocument.append("$set", dbObject);
			collection.update(condition, updateDocument);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}

	public void pullAutoReportForm(DB db, DBObject condition, DBObject dbObject) throws Exception {
		DBCollection collection =  db.getCollection("AutoReportForm");
		try {
			collection.update(condition, dbObject, false, true);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}
	
	public void deleteAutoReportForm(DB db, DBObject condition) throws Exception {
		DBCollection collection =  db.getCollection("AutoReportForm");
		try {
			collection.remove(condition);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}
	
	public int getCodeTableSeq(DB db, String sTable) throws Exception {
		BasicDBObject updateDocument = new BasicDBObject();
		DBCollection collection =  db.getCollection("CodeTable");
		try {
			updateDocument.append("$inc", new BasicDBObject("currentValue",1));
			DBObject result = collection.findAndModify(new BasicDBObject("tableName",sTable), null, null, false, updateDocument, true, false, 0L, MILLISECONDS);
			return (Integer)result.get("currentValue");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}
	

	
}
