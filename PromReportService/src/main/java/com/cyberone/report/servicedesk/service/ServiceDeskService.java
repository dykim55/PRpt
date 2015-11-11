package com.cyberone.report.servicedesk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cyberone.report.product.dao.ProductDao;
import com.cyberone.report.utils.StringUtil;
import com.mongodb.DB;
import com.mongodb.DBObject;

@Service
public class ServiceDeskService {
	
	@Autowired
	private ProductDao productDao;
	
	public List<DBObject> selectProductGroups(DB db) throws Exception {
		return productDao.selectProductGroups(db); 
	}
	
	public List<DBObject> selectProductGroups(DB db, boolean deleted) throws Exception {
		return productDao.selectProductGroups(db, deleted);
	}
	
	public List<DBObject> selectProducts(DB db, int productGroupCode) throws Exception {
		return productDao.selectProducts(db, productGroupCode);
	}
	
	public DBObject selectProduct(DB db, int productCode) throws Exception {
		return productDao.selectProduct(db, productCode);
	}
	
	public DBObject selectProductGroup(DB db, int productGroupCode) throws Exception {
		return productDao.selectProductGroup(db, productGroupCode);
	}
	
	public List<DBObject> selectProductGroup(DB db) throws Exception {
		return productDao.selectProductGroup(db);
	}
	
	public List<DBObject> selectAutoReportFormList(DB db, int productCode, int formReportType) throws Exception {
		return productDao.selectAutoReportFormList(db, productCode, formReportType);
	}
	
	public DBObject selectAutoReportForm(DB db, int formCode) throws Exception {
		return productDao.selectAutoReportForm(db, formCode);
	}

	public DBObject selectAutoReportForm(DB db, int formReportType, int assetCode) throws Exception {
		return productDao.selectAutoReportForm(db, formReportType, assetCode);
	}
	
	public void insertAutoReportForm(DB db, DBObject dbObject) throws Exception {
		productDao.insertAutoReportForm(db, dbObject);
	}
	
	public void deleteAutoReportForm(DB db, DBObject condition) throws Exception {
		productDao.deleteAutoReportForm(db, condition);
	}
	
	public void updateAutoReportForm(DB db, DBObject condition, DBObject dbObject) throws Exception {
		productDao.updateAutoReportForm(db, condition, dbObject);
	}
	
	public void pullAutoReportForm(DB db, DBObject condition, DBObject dbObject) throws Exception {
		productDao.pullAutoReportForm(db, condition, dbObject);
	}
	
	public String getFormName(DB db, int formReportType, int assetCode) throws Exception {
		DBObject dbObj = productDao.selectAutoReportForm(db, formReportType, assetCode);
		if (dbObj == null) {
			return "";
		}
		return StringUtil.convertString(dbObj.get("formName"));
	}
		
}
