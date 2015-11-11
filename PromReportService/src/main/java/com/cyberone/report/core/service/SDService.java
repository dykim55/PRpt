package com.cyberone.report.core.service;

import java.util.HashMap;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cyberone.report.core.dao.SDDao;
import com.cyberone.report.utils.StringUtil;

@Service
public class SDService {
	
	@Autowired
	private SDDao sdDao;

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	/* 
	 * 서비스데스크 보고서 통계데이타 생성
	 */
	@SuppressWarnings("unchecked")
	public void getDataSource(String sReportType, String sStartDay, String sEndDay, HashMap<String, Object> reportData, HashMap<String, Object> hMap) throws Exception {
		logger.debug("서비스데스크 보고서 통계데이타 생성");
		
		String sPrefix = StringUtil.convertString(hMap.get("prefix"));
		String sAssetCode = StringUtil.convertString(hMap.get("assetCode"));
		
		HashMap<String,Object> hFormMap = (HashMap<String,Object>)hMap.get("formData");
		if (hFormMap == null) return;
		
		HashMap<String,Object> hData = (HashMap<String,Object>)hFormMap.get("data");
		String sEtc = StringUtil.convertString(hFormMap.get("etc"));

		for (Entry<String, Object> e : hData.entrySet()) {
			logger.debug("항목:" + e.getKey());

			switch (e.getKey()) {
				case "opt1" : break;
				case "opt2" : break;
				case "opt3" : break;
				case "opt4" : break;
			}
			
			
			
		}
		
		
	}

}
