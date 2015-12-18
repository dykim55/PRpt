package com.cyberone.report.report.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cyberone.report.report.dao.ReportDao;
import com.mongodb.BasicDBList;
import com.mongodb.DB;
import com.mongodb.DBObject;

@Service
public class ReportService {
	
	@Autowired
	private ReportDao reportDao;
	
	public List<DBObject> selectAssetGroups(DB db, int domainCode, int parentCode, boolean deleted) throws Exception {
		return reportDao.selectAssetGroups(db, domainCode, parentCode, deleted);
	}
	
	public List<DBObject> selectAssetGroups(DB db, int domainCode, int[] groupCodes)	throws Exception {
		return reportDao.selectAssetGroups(db, domainCode, groupCodes);
	}
	
	public List<DBObject> selectProductGroups(DB db, boolean deleted)	throws Exception {
		return reportDao.selectProductGroups(db, deleted);
	}
	
	public List<DBObject> selectAssetsProduct(DB db, int groupCode, int productCode) throws Exception {
		return reportDao.selectAssetsProduct(db, groupCode, productCode);
	}
	
	public DBObject selectAsset(DB db, int assetCode) throws Exception {
		return reportDao.selectAsset(db, assetCode);
	}
	
	public DBObject selectAssetPolicy(DB db, int assetCode) throws Exception {
		return reportDao.selectAssetPolicy(db, assetCode);
	}
	
	public List<DBObject> selectProductParsers(DB db, int productCode, boolean deleted)	throws Exception {
		return reportDao.selectProductParsers(db, productCode, deleted);
	}
	
	public DBObject selectStatus(DB db, int assetCode) throws Exception {
		return reportDao.selectStatus(db, assetCode);
	}
	
	public int[] getAccessAssetGroups(DB db, String sLoginID) throws Exception {
		BasicDBList basicDbList = reportDao.getAccessAssetGroups(db, sLoginID);
		int[] accessAssetGroups = new int[basicDbList.size()];
		for (int i = 0; i < basicDbList.size(); i++) {
			accessAssetGroups[i] = (Integer)basicDbList.get(i);
		}
		return accessAssetGroups;
	}

	public DBObject selectReportOption(DB db, int groupCode) throws Exception {
		return reportDao.selectReportOption(db, groupCode);
	}
	
	public List<HashMap<String, Object>> getCscoCd(List<String> assetCodes) throws Exception {
		return reportDao.getCscoCd(assetCodes);
	}
}
