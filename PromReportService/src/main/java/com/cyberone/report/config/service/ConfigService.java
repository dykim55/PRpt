package com.cyberone.report.config.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cyberone.report.config.dao.ConfigDao;
import com.mongodb.DBObject;

@Service
public class ConfigService {
	
	@Autowired
	private ConfigDao configDao;
	
	public void updateAutoReportConfig(DBObject dbObject) throws Exception {
		configDao.updateAutoReportConfig(dbObject);
	}
	
	public void updateAutoReportDb(DBObject condition, DBObject dbObject) throws Exception {
		configDao.updateAutoReportDb(condition, dbObject);
	}
	
	public void insertAutoReportDb(DBObject dbObject) throws Exception {
		configDao.insertAutoReportDb(dbObject);
	}
	
	public void deleteAutoReportDb(DBObject condition) throws Exception {
		configDao.deleteAutoReportDb(condition);
	}
	
	public DBObject selectAutoReportConfig() throws Exception {
		return configDao.selectAutoReportConfig();
	}
	
	public List<DBObject> selectAutoReportDb(DBObject condition, DBObject sortFields)	throws Exception {
		return configDao.selectAutoReportDb(condition, sortFields);
	}
	
	public DBObject selectAutoReportDb(DBObject condition) throws Exception {
		return configDao.selectAutoReportDb(condition);
	}
}

