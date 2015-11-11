package com.cyberone.report.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.cyberone.report.exception.BizException;
import com.cyberone.report.model.UserInfo;

public class SessionCheckInterceptor extends HandlerInterceptorAdapter {

	private static int contextNameLength	= -1;
	
	@Override
	public boolean preHandle(HttpServletRequest request,
    		HttpServletResponse response, Object handler) throws Exception {

		if (contextNameLength == -1) {
			contextNameLength = request.getContextPath().length();
		}

		System.out.println("["+request.getRemoteHost()+"] " + request.getRequestURI());
		
		String requestedUrl = request.getRequestURI().substring(contextNameLength);

    	if (request.getRequestURL().indexOf("loginForm") > -1 ||
    		requestedUrl.indexOf("verifyAccount") > -1) {
    		return true;
    	}

    	if (requestedUrl.indexOf("websocket") > -1) {
        	return true;
        }
    	
    	UserInfo userInfo = (UserInfo)request.getSession().getAttribute("userInfo");

		if (userInfo == null) {
			throw new BizException("0000", "");
		}
		
        return true;
    }

}
