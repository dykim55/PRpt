package com.cyberone.report.config.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Repository
public class ConfigDao {

	@Autowired
	private SqlSession sqlSession;

    @Autowired
    MongoTemplate mongoProm;
	
	public void updateAutoReportConfig(DBObject dbObject) throws Exception {

		BasicDBObject updateDocument = new BasicDBObject();

		DBCollection collection =  mongoProm.getCollection("AutoReportConfig");

		try {
			updateDocument.append("$set", dbObject);
			collection.update(new BasicDBObject(), updateDocument);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}

	public void updateAutoReportDb(DBObject condition, DBObject dbObject) throws Exception {

		BasicDBObject updateDocument = new BasicDBObject();

		DBCollection collection =  mongoProm.getCollection("AutoReportDb");

		try {
			updateDocument.append("$set", dbObject);
			collection.update(condition, updateDocument);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}

	public void deleteAutoReportDb(DBObject condition) throws Exception {
		DBCollection collection =  mongoProm.getCollection("AutoReportDb");
		try {
			collection.remove(condition);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}
	
	public void insertAutoReportDb(DBObject dbObject) throws Exception {
		DBCollection collection =  mongoProm.getCollection("AutoReportDb");
		try {
			collection.insert(dbObject);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}

	public DBObject selectAutoReportConfig() throws Exception {
		DBCollection collection =  mongoProm.getCollection("AutoReportConfig");
		try {
			return collection.findOne();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}
	
	public List<DBObject> selectAutoReportDb(DBObject condition, DBObject sortFields)	throws Exception {
		DBCursor dbCursor = null;
		DBCollection collection =  mongoProm.getCollection("AutoReportDb");
		try {
			dbCursor = collection.find(condition).sort(sortFields);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
		return dbCursor != null ? dbCursor.toArray() : new ArrayList<DBObject>();
	}

	public DBObject selectAutoReportDb(DBObject condition) throws Exception {
		DBObject dbObj = null;
		DBCollection collection =  mongoProm.getCollection("AutoReportDb");
		try {
			dbObj = collection.findOne(condition);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
		return dbObj;
	}
    
    
    
}
