package com.cyberone.report.core.datasource;

import java.util.HashMap;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import com.cyberone.report.utils.StringUtil;

public class SynthesisDataSource implements JRDataSource {

	List<HashMap<String, Object>> data;
	
	protected int index = -1;

	public SynthesisDataSource(List<HashMap<String, Object>> data) {
		this.data = data;
	}

	public boolean next() throws JRException {
		index++;
		return (index < data.size());
	}

	public Object getFieldValue(JRField field) throws JRException {
		
		Object value = null;
		
		String fieldName = field.getName();
		if ("_id".equals(fieldName)) {
			value = (String)data.get(index).get("_id");
		}
		else if ("series".equals(fieldName)) {
			value = (String)data.get(index).get("series");
		}
		else if ("category".equals(fieldName)) {
			value = (String)data.get(index).get("category");
		}
		else if ("value".equals(fieldName)) {
			value = (Long)data.get(index).get("value");
		}
		else if ("reviewText".equals(fieldName)) {
			value = (String)data.get(index).get("reviewText");
		}
		else if ("content".equals(fieldName)) {
			value = (String)data.get(index).get("content");
		}
		else if ("no".equals(fieldName)) {
			value = (Integer)data.get(index).get("no");
		}
		else if ("srcIp".equals(fieldName)) {
			value = (String)data.get(index).get("srcIp");
		}
		else if ("destIp".equals(fieldName)) {
			value = (String)data.get(index).get("destIp");
		}
		else if ("total".equals(fieldName)) {
			value = (Long)data.get(index).get("total");
		}
		else if ("count".equals(fieldName)) {
			value = (Long)data.get(index).get("count");
		}
		else if ("inc".equals(fieldName)) {
			value = (Long)data.get(index).get("inc");
		}
		else if ("port".equals(fieldName)) {
			value = (Integer)data.get(index).get("port");
		}
		else if ("destPort".equals(fieldName)) {
			value = (String)data.get(index).get("destPort");
		}
		else if ("svcName".equals(fieldName)) {
			value = (String)data.get(index).get("svcName");
		}
		else if ("ratio".equals(fieldName)) {
			value = (Float)data.get(index).get("ratio");
		}
		else if ("geoCode".equals(fieldName)) {
			value = (String)data.get(index).get("geoCode");
		}
		else if ("action".equals(fieldName)) {
			value = (String)data.get(index).get("action");
		}
		else if ("countVariation".equals(fieldName)) {
			value = (Long)data.get(index).get("countVariation");
		}
		else if ("gubun".equals(fieldName)) {
			value = (String)data.get(index).get("gubun");
		}
		else if ("date".equals(fieldName)) {
			value = (String)data.get(index).get("date");
		}
		else if ("message".equals(fieldName)) {
			value = (String)data.get(index).get("message");
		}
		else if ("group".equals(fieldName)) {
			value = (String)data.get(index).get("group");
		}
		else if ("eqpmName".equals(fieldName)) {
			value = (String)data.get(index).get("eqpmName");
		}
		else if ("esmSvrt".equals(fieldName)) {
			value = (String)data.get(index).get("esmSvrt");
		}
		else if ("protocol".equals(fieldName)) {
			value = (String)data.get(index).get("protocol");
		}
		else if ("etc".equals(fieldName)) {
			value = (String)data.get(index).get("etc");
		}
		else if ("solution".equals(fieldName)) {
			value = StringUtil.convertString(data.get(index).get("solution"));
		}
		else if ("eqpmNm".equals(fieldName)) {
			value = StringUtil.convertString(data.get(index).get("eqpmNm"));
		}
		else if ("rqrNm".equals(fieldName)) {
			value = StringUtil.convertString(data.get(index).get("rqrNm"));
		}
		else if ("eqpmIp".equals(fieldName)) {
			value = StringUtil.convertString(data.get(index).get("eqpmIp"));
		}
		else if ("instPos".equals(fieldName)) {
			value = StringUtil.convertString(data.get(index).get("instPos"));
		}
		else if ("rank".equals(fieldName)) {
			value = StringUtil.convertString(data.get(index).get("rank"));
		}
		else if ("domainUrl".equals(fieldName)) {
			value = StringUtil.convertString(data.get(index).get("domainUrl"));
		}
		
		
		
		
		
		
		
		
		
		
		
		else if ("assetCode".equals(fieldName)) {
			value = (Integer)data.get(index).get("assetCode");
		}
		else if ("assetName".equals(fieldName)) {
			value = (String)data.get(index).get("assetName");
		}
		else if ("month".equals(fieldName)) {
			value = (Integer)data.get(index).get("month");
		}
		else if ("day".equals(fieldName)) {
			value = (Integer)data.get(index).get("day");
		}
		else if ("groupType".equals(fieldName)) {
			value = (String)data.get(index).get("groupType");
		}
		else if ("productName".equals(fieldName)) {
			value = (String)data.get(index).get("productName");
		}
		else if ("assetIp".equals(fieldName)) {
			value = (String)data.get(index).get("assetIp");
		}
		else if ("detect".equals(fieldName)) {
			value = (Integer)data.get(index).get("detect");
		}
		else if ("answer".equals(fieldName)) {
			value = (Integer)data.get(index).get("answer");
		}
		else if ("trafficSize".equals(fieldName)) {
			value = (Long)data.get(index).get("trafficSize");
		}
		else if ("trafficVariation".equals(fieldName)) {
			value = (Long)data.get(index).get("trafficVariation");
		}
		else if ("zone".equals(fieldName)) {
			value = (String)data.get(index).get("zone");
		}
		else if ("policy".equals(fieldName)) {
			value = (String)data.get(index).get("policy");
		}
		else if ("max".equals(fieldName)) {
			value = (Long)data.get(index).get("max");
		}
		else if ("min".equals(fieldName)) {
			value = (Long)data.get(index).get("min");
		}
		else if ("avg".equals(fieldName)) {
			value = (Long)data.get(index).get("avg");
		}
		else if ("host".equals(fieldName)) {
			value = (String)data.get(index).get("host");
		}
		else if ("eventName".equals(fieldName)) {
			value = (String)data.get(index).get("eventName");
		}
		else if ("pktCnt".equals(fieldName)) {
			value = (Long)data.get(index).get("pktCnt");
		}
		else if ("bandWidth".equals(fieldName)) {
			value = (Long)data.get(index).get("bandWidth");
		}
		else if ("productType".equals(fieldName)) {
			value = (String)data.get(index).get("productType");
		}
		else if ("productType".equals(fieldName)) {
			value = (String)data.get(index).get("productType");
		}
		
		return value;
	}

	public boolean isEmpty() {
		return data.size() > 0 ? false : true;
	}
}
