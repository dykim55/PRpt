package com.cyberone.report.main.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cyberone.report.main.service.MainService;
import com.cyberone.report.model.UserInfo;
import com.cyberone.report.utils.Encryption;

@Controller
@RequestMapping("/main")
public class MainController {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private MainService mainService;
    
    @RequestMapping("/")
    public String main(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        logger.debug("main()");
        
        UserInfo userInfo = (UserInfo)request.getSession().getAttribute("userInfo");
        model.addAttribute("userInfo", userInfo);
        model.addAttribute("loginID", Encryption.AESEncrypt(userInfo.getAdmin().getLoginID()));
        return "main/home";
    }

    @RequestMapping("/promAlert")
    public String promAlert(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        logger.debug("promAlert()");
        
        return "common/promAlert";
    }

    @RequestMapping("/promConfirm")
    public String promConfirm(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        logger.debug("promConfirm()");
        
        return "common/promConfirm";
    }

}
