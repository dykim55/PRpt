package com.cyberone.report.core.datasource;

import java.util.HashMap;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class EventDataSource implements JRDataSource {

	List<HashMap<String, Object>> data;
	
	protected int index = -1;

	public EventDataSource(List<HashMap<String, Object>> data) {
		this.data = data;
	}

	public boolean next() throws JRException {
		index++;
		return (index < data.size());
	}

	public Object getFieldValue(JRField field) throws JRException {
		
		Object value = null;
		
		String fieldName = field.getName();
		
		if ("assetCodeName".equals(fieldName)) {
			value = (String)data.get(index).get("assetCode") + "=" + (String)data.get(index).get("assetName");
		}
		else if ("assetCode".equals(fieldName)) {
			value = (String)data.get(index).get("assetCode");
		}
		else if ("assetName".equals(fieldName)) {
			value = (String)data.get(index).get("assetName");
		}
		else if ("groupName".equals(fieldName)) {
			value = (String)data.get(index).get("groupName");
		}
		else if ("count".equals(fieldName)) {
			value = (Integer)data.get(index).get("count");
		}
		else if ("hour".equals(fieldName)) {
			value = (Integer)data.get(index).get("hour");
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
		else if ("ruleCodeName".equals(fieldName)) {
			value = (String)data.get(index).get("ruleCode") + "=" + (String)data.get(index).get("ruleName");
		}
		else if ("ruleCode".equals(fieldName)) {
			value = (String)data.get(index).get("ruleCode");
		}
		else if ("ruleName".equals(fieldName)) {
			value = (String)data.get(index).get("ruleName");
		}
		else if ("productName".equals(fieldName)) {
			value = (String)data.get(index).get("productName");
		}
		else if ("productGroupCode".equals(fieldName)) {
			value = (String)data.get(index).get("productGroupCode");
		}
		else if ("productGroupName".equals(fieldName)) {
			value = (String)data.get(index).get("productGroupName");
		}
		else if ("productGroupCodeName".equals(fieldName)) {
			value = (String)data.get(index).get("productGroupCode") + "=" + (String)data.get(index).get("productGroupName");
		}
		else if ("createTime".equals(fieldName)) {
			value = (String)data.get(index).get("createTime");
		}
		else if ("loginID".equals(fieldName)) {
			value = (String)data.get(index).get("loginID");
		}
		else if ("workResult".equals(fieldName)) {
			value = (String)data.get(index).get("workResult");
		}
		else if ("workMsg".equals(fieldName)) {
			value = (String)data.get(index).get("workMsg");
		}
		
		return value;
	}
	
}
