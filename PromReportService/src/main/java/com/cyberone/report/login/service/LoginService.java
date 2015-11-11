package com.cyberone.report.login.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cyberone.report.login.dao.LoginDao;
import com.cyberone.report.model.Admin;
import com.mongodb.DB;
import com.mongodb.DBObject;

@Service
public class LoginService {
	
	@Autowired
	private LoginDao loginDao;
	
	public Admin verifyAccount(Map<String, Object> paramMap) throws Exception {
		return loginDao.verifyAccount(paramMap);
	}

	public Admin selectAcct(Map<String, Object> paramMap) throws Exception {
		return loginDao.selectAcct(paramMap);
	}

	public DBObject selectAdminAccessPower(DB db, int adminCode)	throws Exception {
		return loginDao.selectAdminAccessPower(db, adminCode);
	}
}
