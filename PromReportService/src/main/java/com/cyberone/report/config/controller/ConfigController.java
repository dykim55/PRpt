package com.cyberone.report.config.controller;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cyberone.report.config.service.ConfigService;
import com.cyberone.report.model.UserInfo;
import com.cyberone.report.utils.StringUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Controller
@RequestMapping("/config")
public class ConfigController {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ConfigService configService;

    @RequestMapping("/")
    public String config(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        logger.debug("환경설정[config] ");

        UserInfo userInfo = (UserInfo)request.getSession().getAttribute("userInfo");
        model.addAttribute("userInfo", userInfo);
        
        DBObject dbObj = configService.selectAutoReportConfig();
        model.addAttribute("configInfo", dbObj.toMap());
        
        return "config/config";
    }

    @RequestMapping("/serverList")
    public ModelAndView serverList(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        logger.debug("환경설정[serverList] ");
        
    	DBObject sortFields = new BasicDBObject();
    	sortFields.put("serverName", 1);
        
        List<DBObject> dbList = configService.selectAutoReportDb(new BasicDBObject(), sortFields);
        
        ModelAndView modelAndView = new ModelAndView("jsonView");
        
        modelAndView.addObject("rows", dbList);
        
        return modelAndView.addObject("status", "success");
    }
    
    @RequestMapping("/serverRegistry")
    public String serverRegistry(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        logger.debug("환경설정 - 서버등록[serverRegistry] ");
        
        String sIdx = request.getParameter("idx");
        if (!StringUtil.isEmpty(sIdx)) {
        	BasicDBObject condition = new BasicDBObject("idx", Integer.parseInt(sIdx));
        	List<DBObject> dbList = configService.selectAutoReportDb(condition, new BasicDBObject());
        	if (dbList.size() > 0) {
        		model.addAttribute("svrInfo", dbList.get(0).toMap());
        	}
        }
        
        return "config/configDialog";
    }

    @RequestMapping("/saveOpt")
    public ModelAndView saveOpt(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
    	logger.debug("환경설정 - 설정저장[saveOpt] ");
    	
    	ModelAndView modelAndView = new ModelAndView("jsonView");
    	
    	String sSvcUse = request.getParameter("cfgSvcUse");
    	String sSvcIp = request.getParameter("cfgSvcIp");
    	String sSvcPort = request.getParameter("cfgSvcPort");
    	String sSvcName = request.getParameter("cfgSvcName");
    	String sSvcAccount = request.getParameter("cfgSvcAccount");
    	String sSvcPassword = request.getParameter("cfgSvcPassword");
    	
    	HashMap<String, Object> svcMap = new HashMap<String, Object>();
    	svcMap.put("host", sSvcIp);
    	svcMap.put("port", Integer.parseInt(sSvcPort));
    	svcMap.put("dbname", sSvcName);
    	svcMap.put("account", sSvcAccount);
    	svcMap.put("password", sSvcPassword);
    	
    	logger.debug(svcMap.toString());
    	
    	BasicDBObject dbObject = new BasicDBObject();
    	dbObject.put("serviceDeskUse", StringUtil.isEmpty(sSvcUse) ? 0 : 1);
    	dbObject.put("serviceDeskInfo", svcMap);
    	
    	configService.updateAutoReportConfig(dbObject);    	
    	
    	return modelAndView.addObject("status", "success");
    }    

    @RequestMapping("/saveServer")
    public ModelAndView saveServer(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
    	logger.debug("환경설정 - 서버등록[saveServer] ");
    	
    	ModelAndView modelAndView = new ModelAndView("jsonView");
    	
    	logger.debug(request.getParameterMap().toString());
    	
    	String sSvrIdx = request.getParameter("cfgSvrIdx");
    	String sSvrName = request.getParameter("cfgSvrName");
    	String sSvrDomain = request.getParameter("cfgSvrDomain");
    	String sPromIp = request.getParameter("cfgPromIp");
    	String sPromPort = request.getParameter("cfgPromPort");
    	String sPromName = request.getParameter("cfgPromName");
    	String sPromAccount = request.getParameter("cfgPromAccount");
    	String sPromPassword = request.getParameter("cfgPromPassword");
    	String sReportIp = request.getParameter("cfgReportIp");
    	String sReportPort = request.getParameter("cfgReportPort");
    	String sReportName = request.getParameter("cfgReportName");
    	String sReportAccount = request.getParameter("cfgReportAccount");
    	String sReportPassword = request.getParameter("cfgReportPassword");

    	BasicDBObject dbObject = new BasicDBObject();
    	dbObject.put("serverName", sSvrName);
    	dbObject.put("domainCode", Integer.parseInt(sSvrDomain));
    	dbObject.put("promIp", sPromIp);
    	dbObject.put("promPort", Integer.parseInt(sPromPort));
    	dbObject.put("promName", sPromName);
    	dbObject.put("promAccount", sPromAccount);
    	dbObject.put("promPassword", sPromPassword);
    	dbObject.put("reportIp", sReportIp);
    	dbObject.put("reportPort", Integer.parseInt(sReportPort));
    	dbObject.put("reportName", sReportName);
    	dbObject.put("reportAccount", sReportAccount);
    	dbObject.put("reportPassword", sReportPassword);
    	
    	if (StringUtil.isEmpty(sSvrIdx)) {
	    	DBObject sortFields = new BasicDBObject();
	    	sortFields.put("idx", 1);
	    	
	    	List<DBObject> dbList = configService.selectAutoReportDb(new BasicDBObject(), sortFields);
	    	int i = 1;
	    	for (; i <= dbList.size(); i++) {
	    		DBObject o = dbList.get(i-1);
	    		if (i < (Integer)o.get("idx")) {
	    			break;
	    		}
	    	}
	    	dbObject.put("idx", i);
	
	    	configService.insertAutoReportDb(dbObject);
    	} else {
    		BasicDBObject condition = new BasicDBObject("idx", Integer.parseInt(sSvrIdx));
    		configService.updateAutoReportDb(condition, dbObject);
    	}
    	
    	return modelAndView.addObject("status", "success");
    }    

    @RequestMapping("/delServer")
    public ModelAndView delServer(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
    	logger.debug("환경설정 - 서버삭제[delServer] ");

    	System.out.println(request.getParameterMap());
    	
    	String sSvrIdx = request.getParameter("idx");

    	BasicDBObject condition = new BasicDBObject("idx", Integer.parseInt(sSvrIdx));
    	configService.deleteAutoReportDb(condition);
    	
    	ModelAndView modelAndView = new ModelAndView("jsonView");
    	return modelAndView.addObject("status", "success");
    }    
    
}
