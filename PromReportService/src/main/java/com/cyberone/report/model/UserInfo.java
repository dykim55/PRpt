package com.cyberone.report.model;

import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.mongodb.DB;

/**
 * 계정정보
 *
 */
public class UserInfo {

	private Admin admin;
	private int domainCode;
	private transient DB promDb;
	private transient DB reportDb;
	private transient WebSocketSession wsSession;
	
	public Admin getAdmin() {
		return admin;
	}

	public void setAdmin(Admin admin) {
		this.admin = admin;
	}

	public int getDomainCode() {
		return domainCode;
	}

	public void setDomainCode(int domainCode) {
		this.domainCode = domainCode;
	}

	public DB getPromDb() {
		return promDb;
	}

	public void setPromDb(DB promDb) {
		this.promDb = promDb;
	}

	public DB getReportDb() {
		return reportDb;
	}

	public void setReportDb(DB reportDb) {
		this.reportDb = reportDb;
	}

	public WebSocketSession getWsSession() {
		return wsSession;
	}

	public void setWsSession(WebSocketSession wsSession) {
		this.wsSession = wsSession;
	}
	
	public void push(HashMap<String, Object> map) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			wsSession.sendMessage(new TextMessage(mapper.writeValueAsString(map)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
