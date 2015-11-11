package com.cyberone.report.interceptor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpSession;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.cyberone.report.login.controller.LoginController;
import com.cyberone.report.model.UserInfo;
import com.cyberone.report.utils.Encryption;
import com.cyberone.report.utils.StringUtil;

public class EchoHandler extends TextWebSocketHandler {
	 
    private Logger logger = LoggerFactory.getLogger(EchoHandler.class);
 
    //세션을 모두 저장한다.
    private Map<String, WebSocketSession> sessions = new HashMap<String, WebSocketSession>();
    
    /**
     * 클라이언트 연결 이후에 실행되는 메소드
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        logger.info("{} 연결됨", session.getId());
        
        Map<String, Object> attrMap = session.getAttributes();
        if (attrMap.get("userInfo") != null) {
			UserInfo userInfo = (UserInfo)attrMap.get("userInfo");
			userInfo.setWsSession(session);
        } else {
        	session.close();
        }
    }
    
    /**
     * 클라이언트가 웹소켓 서버로 메시지를 전송했을 때 실행되는 메소드
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.info("{}로 부터 {} 받음", session.getId(), message.getPayload());

		ObjectMapper mapper = new ObjectMapper();
		@SuppressWarnings("unchecked")
		HashMap<String,Object> hMap = mapper.readValue(message.getPayload(), HashMap.class);

		for (Entry<String, Object> e : hMap.entrySet()) {
		
			switch (e.getKey()) {
				case "open" :
					//String sLoginID = Encryption.AESDecrypt(StringUtil.convertString(e.getValue()));
					String sLoginID = StringUtil.convertString(e.getValue());
					if (null != LoginController.userMap.get(sLoginID)) {
						HttpSession httpSession = LoginController.userMap.get(sLoginID);
						UserInfo userInfo = (UserInfo)httpSession.getAttribute("userInfo");
						userInfo.setWsSession(session);
					}
					break;
			}
		}
    	
    }
    
    /**
     * 클라이언트 연결을 끊었을 때 실행되는 메소드
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        logger.info("{} 연결 끊김.", session.getId());
    }
    
}