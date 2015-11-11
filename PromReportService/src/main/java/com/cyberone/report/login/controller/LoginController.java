package com.cyberone.report.login.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cyberone.report.config.service.ConfigService;
import com.cyberone.report.exception.BizException;
import com.cyberone.report.login.service.LoginService;
import com.cyberone.report.model.Admin;
import com.cyberone.report.model.UserInfo;
import com.cyberone.report.utils.StringUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

@Controller
@RequestMapping("/login")
public class LoginController {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private LoginService loginService;
    
    @Autowired
    private ConfigService configService;
    
	public static HashMap<String, HttpSession> userMap = new HashMap<String, HttpSession>();
	
    @RequestMapping("loginForm")
    public String loginForm(Model model) throws Exception {
        logger.info("- 로그인 화면");
        
    	DBObject sortFields = new BasicDBObject();
    	sortFields.put("serverName", 1);
        List<DBObject> dbList = configService.selectAutoReportDb(new BasicDBObject(), sortFields);
        model.addAttribute("dbList", dbList);

        return "/login/loginForm";
    }
	
    @RequestMapping(value="verifyAccount", method=RequestMethod.POST)
    public String verifyAccount(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
    	logger.debug("[로그인] verifyAccount");

    	DB promDb = null;
    	DB reportDb = null;
    	MongoClient mongoClient = null;
    	boolean auth = false;
    	
    	String sLoginID = request.getParameter("loginId");
    	//String sAcctPw = Encryption.encrypt(request.getParameter("loginPw"));
        String sIdx = request.getParameter("login_server");
        
    	BasicDBObject condition = new BasicDBObject("idx", Integer.parseInt(sIdx));
    	DBObject dbObj = configService.selectAutoReportDb(condition);
    	
    	//관리계정 접속

    	Admin admin = new Admin();
    	
    	try {
	    	if (dbObj !=  null) {
	    		try {
		    		mongoClient = new MongoClient((String)dbObj.get("reportIp"), (Integer)dbObj.get("reportPort"));
		    		reportDb = mongoClient.getDB((String)dbObj.get("reportName"));
		    		auth = reportDb.authenticate((String)dbObj.get("reportAccount"), ((String)dbObj.get("reportPassword")).toCharArray());
	    		} catch (Exception e) {
	    			if (mongoClient != null) mongoClient.close();
	    			throw new BizException("자동화보고서 서버설정이 잘못되었습니다.\n관리자에게 문의하세요.");
	    		}
	    		try {		    		
		    		mongoClient = new MongoClient((String)dbObj.get("promIp"), (Integer)dbObj.get("promPort"));
		    		promDb = mongoClient.getDB((String)dbObj.get("promName"));
		    		auth = promDb.authenticate((String)dbObj.get("promAccount"), ((String)dbObj.get("promPassword")).toCharArray());
	    		} catch (Exception e) {
	    			if (mongoClient != null) mongoClient.close();
	    			throw new BizException("자동화보고서 서버설정이 잘못되었습니다.\n관리자에게 문의하세요.");
	    		}
	    	}
	
	    	DBCollection colAdmin =  promDb.getCollection("Admin");
	    	DBObject dbAdmin = colAdmin.findOne(new BasicDBObject("loginID", sLoginID));
	    	if (dbAdmin == null) {
	    		promDb.getMongo().close();
	    		reportDb.getMongo().close();
	    		throw new BizException("아이디 또는 비밀번호가 잘못되었습니다.");
	    	}

	    	admin.setLoginID(StringUtil.convertString(dbAdmin.get("loginID")));
	    	admin.setAdminName(StringUtil.convertString(dbAdmin.get("adminName")));
	    	
    	} catch (Exception e) {
    		promDb.getMongo().close();
    		reportDb.getMongo().close();
    		throw new BizException("요청 처리중 오류가 발생했습니다.");
    	}
    	
    	HttpSession oldSession = userMap.remove(admin.getLoginID());
    	if (oldSession != null) {
    		try { oldSession.invalidate(); } catch(Exception e) {}
    	}
    	
    	UserInfo userInfo  = new UserInfo();
    	userInfo.setAdmin(admin);
    	userInfo.setPromDb(promDb);
    	userInfo.setReportDb(reportDb);
    	userInfo.setDomainCode((Integer)dbObj.get("domainCode"));
    	
    	HttpSession session = request.getSession();
    	session.setAttribute("userInfo", userInfo);

    	System.out.printf("Login ID %s Session ID %s ( timeout: %s ) created at %s%n", admin.getLoginID(), session.getId(), session.getMaxInactiveInterval(), new Date());
    	
    	userMap.put(admin.getLoginID(), session);
    	
		return "redirect:/main/";
    }

    @RequestMapping("logout")
    public String logout(UserInfo userInfo, BindingResult bindingResult, 
    		Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("로그아웃");
        
        userInfo = (UserInfo)request.getSession().getAttribute("userInfo");

        userMap.remove(userInfo.getAdmin().getLoginID());
        try { request.getSession(true).invalidate(); } catch(Exception e) {}

        return "redirect:/";
    }
    
}
