package com.cyberone.report.login.dao;

import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cyberone.report.model.Admin;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

@Repository
public class LoginDao {

	@Autowired
	private SqlSession sqlSession;

	public Admin verifyAccount(Map<String, Object> paramMap) throws Exception {
		return sqlSession.selectOne("Login.verifyAccount", paramMap);
	}

	public Admin selectAcct(Map<String, Object> paramMap) throws Exception {
		return sqlSession.selectOne("Login.selectAccount", paramMap);
	}

	public DBObject selectAdminAccessPower(DB db, int adminCode)	throws Exception {
		DBObject condition = new BasicDBObject();
		DBObject dbObj = null;
		DBCollection collection =  db.getCollection("AdminAccessPower");
		try {
			condition.put("adminCode", adminCode);
			dbObj = collection.findOne(condition);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
		return dbObj;
	}
	
}
