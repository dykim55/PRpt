package com.cyberone.report.product.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cyberone.report.exception.BizException;
import com.cyberone.report.model.UserInfo;
import com.cyberone.report.product.service.ProductService;
import com.cyberone.report.utils.StringUtil;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Controller
@RequestMapping("/product")
public class ProductController {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ProductService productService;

    @RequestMapping("/formManage")
    public String formManage(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        logger.debug("제품별양식관리");
        
        UserInfo userInfo = (UserInfo)request.getSession().getAttribute("userInfo");
        model.addAttribute("userInfo", userInfo);
        
        List<DBObject> productGroups = productService.selectProductGroups(userInfo.getPromDb());
        model.addAttribute("productGroups", productGroups);
        
        return "product/formManage";
    }

    @RequestMapping("/products")
    public ModelAndView products(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
    	logger.debug("제품별양식관리[products]");
    	
    	UserInfo userInfo = (UserInfo)request.getSession().getAttribute("userInfo");
    	
    	String sProductGroupCode = request.getParameter("productGroupCode");
    	
    	List<DBObject> dbList = productService.selectProducts(userInfo.getPromDb(), Integer.parseInt(sProductGroupCode));
    	
    	ModelAndView modelAndView = new ModelAndView("jsonView");
		modelAndView.addObject("products", dbList);
    	return modelAndView.addObject("status", "success");
    }    

    @RequestMapping("/formTypeList")
    public ModelAndView formTypeList(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        logger.debug("제품별양식관리[formTypeList]");

		@SuppressWarnings("rawtypes")
		List<HashMap> treeData = new ArrayList<HashMap>();
		HashMap<String, Object> treeNode = new HashMap<String, Object>();

		try {
			treeNode.put("id", 0);
			treeNode.put("name", "자동화 보고서 제품군");
			treeNode.put("code", "");
			treeNode.put("level", 0);
			treeNode.put("parent", "");
			treeNode.put("isLeaf", false);
			treeNode.put("expanded", true);
			treeNode.put("loaded", true);
			treeNode.put("prefix", "");
			treeData.add(treeNode);

			String[] sFormTypeName = {"", "AntiDDOS", "AntiDDOS-DP", "방화벽(FW)", "웹방화벽(WAF)", "침입방지(IPS)", "침입탐지(IDS)", "", "", "서비스데스크"};
			String[] sPrefix = {"", "ddos", "ddos_dp", "fw", "waf", "ips", "ids", "", "", "servicedesk"};
			
			for (int i = 1; i <= 6; i++) {
				treeNode = new HashMap<String, Object>();
				treeNode.put("id", "TYPE" + i);
				treeNode.put("name", sFormTypeName[i]);
				treeNode.put("code", i);
				treeNode.put("level", 1);
				treeNode.put("parent", 0);
				treeNode.put("isLeaf", true);
				treeNode.put("expanded", false);
				treeNode.put("loaded", true);
				treeNode.put("prefix", sPrefix[i]);
				treeData.add(treeNode);
			}
			
			treeNode = new HashMap<String, Object>();
			treeNode.put("id", 1);
			treeNode.put("name", "자동화 보고서 관제실적");
			treeNode.put("code", "");
			treeNode.put("level", 0);
			treeNode.put("parent", "");
			treeNode.put("isLeaf", false);
			treeNode.put("expanded", true);
			treeNode.put("loaded", true);
			treeNode.put("prefix", "");
			treeData.add(treeNode);

			treeNode = new HashMap<String, Object>();
			treeNode.put("id", "TYPE99");
			treeNode.put("name", sFormTypeName[9]);
			treeNode.put("code", 9);
			treeNode.put("level", 1);
			treeNode.put("parent", 1);
			treeNode.put("isLeaf", true);
			treeNode.put("expanded", false);
			treeNode.put("loaded", true);
			treeNode.put("prefix", sPrefix[9]);
			treeData.add(treeNode);
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
        
		ModelAndView modelAndView = new ModelAndView("jsonView");
		model.addAttribute("rows", treeData);
		model.addAttribute("page", 1);
		model.addAttribute("rowNum", 1);
		model.addAttribute("total", 1);
		model.addAttribute("records", treeData.size());
    	modelAndView.addAllObjects(model.asMap());
        
        return modelAndView.addObject("status", "success");
    }

    @RequestMapping("/formList")
    public ModelAndView formList(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

    	UserInfo userInfo = (UserInfo)request.getSession().getAttribute("userInfo");
    	
		List<HashMap<?, ?>> treeData = new ArrayList<HashMap<?, ?>>();
		HashMap<String, Object> treeNode = new HashMap<String, Object>();
			
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			String sFormType = request.getParameter("formType"); 
			int formType = Integer.parseInt(sFormType);

			String sPrefix = request.getParameter("prefix");
			if (StringUtil.isEmpty(sPrefix)) {
				throw new BizException("제품정보(prefix)가 없습니다.\n관리자에게 문의하세요.");
			}
			
			String[] sFormTypeName = {"", "AntiDDOS", "AntiDDOS-DP", "방화벽(FW)", "웹방화벽(WAF)", "침입방지(IPS)", "침입탐지(IDS)", "", "", "서비스데스크"};
			
			treeNode = new HashMap<String, Object>();
			treeNode.put("id", "FORM");
			treeNode.put("level", 0);
			treeNode.put("parent", "");
			treeNode.put("isLeaf", false);
			treeNode.put("expanded", true);
			treeNode.put("loaded", true);
			treeNode.put("code", formType);
			treeNode.put("name", "자동화 보고서 " + sFormTypeName[formType] + " 양식");
			treeNode.put("desc", "");
			treeNode.put("prefix", sPrefix);
			treeNode.put("reportType", 0);
			treeNode.put("modifyTime", "");
			treeNode.put("modifyId", "");
			treeNode.put("createTime", "");
			treeNode.put("createId", "");
			treeNode.put("applyCnt", "");
			treeNode.put("formType", formType);
			treeData.add(treeNode);
			
			String[] sReportType = {"", "일일보고서", "주간보고서", "월간보고서", "임의기간보고서"};
			
			for (int i = 1; i <= sReportType.length-1; i++) {
				treeNode = new HashMap<String, Object>();
				treeNode.put("id", "TYPE" + i);
				treeNode.put("level", 1);
				treeNode.put("parent", "FORM");
				treeNode.put("isLeaf", false);
				treeNode.put("loaded", true);
				treeNode.put("code", i);
				treeNode.put("name", sReportType[i]);
				treeNode.put("desc", "");
				treeNode.put("prefix", sPrefix);
				treeNode.put("reportType", i);
				treeNode.put("modifyTime", "");
				treeNode.put("modifyId", "");
				treeNode.put("createTime", "");
				treeNode.put("createId", "");
				treeNode.put("applyCnt", "");
				treeNode.put("formType", formType);
				
				List<DBObject> dbForms = productService.selectAutoReportFormList(userInfo.getPromDb(), formType, i);
				
				treeNode.put("expanded", dbForms.size() > 0 ? true : false);
				treeData.add(treeNode);
				
				for (DBObject obj : dbForms) {
					treeNode = new HashMap<String, Object>();
					treeNode.put("id", (Integer)obj.get("formCode"));
					treeNode.put("level", 2);
					treeNode.put("parent", "TYPE" + i);
					treeNode.put("isLeaf", true);
					treeNode.put("expanded", false);
					treeNode.put("loaded", true);
					treeNode.put("code", (Integer)obj.get("formCode"));
					treeNode.put("name", StringUtil.convertString(obj.get("formName")));
					treeNode.put("desc", StringUtil.convertString(obj.get("formDesc")));
					treeNode.put("prefix", sPrefix);
					treeNode.put("reportType", i);
					treeNode.put("modifyTime", dateFormat.format((Date)obj.get("modifyTime")));
					treeNode.put("modifyId", StringUtil.convertString(obj.get("modifyId")));
					treeNode.put("createTime", dateFormat.format((Date)obj.get("createTime")));
					treeNode.put("createId", StringUtil.convertString(obj.get("createId")));
					treeNode.put("applyCnt", ((BasicDBList)obj.get("appliedAssets")).size());
					treeNode.put("formType", formType);
					treeData.add(treeNode);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ModelAndView modelAndView = new ModelAndView("jsonView");
		model.addAttribute("rows", treeData);
		model.addAttribute("page", 1);
		model.addAttribute("rowNum", 1);
		model.addAttribute("total", 1);
		model.addAttribute("records", treeData.size());
    	modelAndView.addAllObjects(model.asMap());
        
        return modelAndView.addObject("status", "success");
	}
    
    @RequestMapping("/formDialog")
    public String formDialog(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
    	logger.debug("제품별양식관리[formDialog]");
    	return "product/formDialog";
    }    

    @RequestMapping("/applyGroupDialog")
    public String applyGroupDialog(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
    	logger.debug("제품별양식관리[applyGroupDialog]");
    	return "product/applyGroupDialog";
    }    

    @RequestMapping("/applyAssetDialog")
    public String applyAssetDialog(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
    	logger.debug("제품별양식관리[applyAssetDialog]");
    	return "product/applyAssetDialog";
    }    

    @RequestMapping("/formInfo")
    public ModelAndView formInfo(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
    	logger.debug("제품별양식관리[formInfo]");
    	
    	UserInfo userInfo = (UserInfo)request.getSession().getAttribute("userInfo");
    	
    	String sFormCode = request.getParameter("formCode");
    	String sReportType = request.getParameter("reportType");
    	String sPrefix = request.getParameter("prefix");
    	
    	ModelAndView modelAndView = new ModelAndView("jsonView");
    	
    	HashMap<String, String> formMap = null;
    	if (Integer.parseInt(sFormCode) == 0) {
    		formMap = getDefaultFormData(sPrefix, sReportType);
    		modelAndView.addObject("formMap", formMap);
    	} else {
    		DBObject dbObj = productService.selectAutoReportForm(userInfo.getPromDb(), Integer.parseInt(sFormCode));
    		modelAndView.addObject("formName", StringUtil.convertString(dbObj.get("formName")));
    		modelAndView.addObject("formMap", new ObjectMapper().readValue(StringUtil.convertString(dbObj.get("formData")), HashMap.class));
    	}
    	
    	return modelAndView.addObject("status", "success");
    }    

    @RequestMapping("/assetFormInfo")
    public ModelAndView assetFormInfo(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
    	logger.debug("제품별양식관리[assetFormInfo]");
    	
    	UserInfo userInfo = (UserInfo)request.getSession().getAttribute("userInfo");
    	
    	String sAssetCode = request.getParameter("assetCode");
    	if (StringUtil.isEmpty(sAssetCode)) {
    		sAssetCode = request.getParameter("groupCode");
    	}
    	
    	if (sAssetCode.indexOf("_") != -1) {
    		sAssetCode = sAssetCode.substring(0, sAssetCode.indexOf("_"));
    	}
    	
    	String sReportType = request.getParameter("reportType");
    	String sPrefix = request.getParameter("prefix");
    	
    	ModelAndView modelAndView = new ModelAndView("jsonView");
    	
		DBObject dbObj = productService.selectAutoReportForm(userInfo.getPromDb(), Integer.parseInt(sReportType), Integer.parseInt(sAssetCode));
		modelAndView.addObject("formName", StringUtil.convertString(dbObj.get("formName")));
		modelAndView.addObject("formMap", new ObjectMapper().readValue(StringUtil.convertString(dbObj.get("formData")), HashMap.class));
		modelAndView.addObject("formInfo", dbObj);
		
    	return modelAndView.addObject("status", "success");
    }    
    
    @RequestMapping("/formUI")
    public String formUI(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
    	logger.debug("제품별양식관리[formUI]");
    	String sUI = request.getParameter("option");
    	return "product/option/" + sUI;
    }    
    
    @RequestMapping("/formSave")
    public String formSave(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
    	logger.debug("제품별양식관리[formSave]");
    	
    	UserInfo userInfo = (UserInfo)request.getSession().getAttribute("userInfo");
    	
    	String sFormCode = request.getParameter("formCode");
    	String sReportType = request.getParameter("reportType");
    	String sData = request.getParameter("data");
    	String sFormType = request.getParameter("formType");
    	String sName = request.getParameter("name");
    	String sDesc = request.getParameter("desc");

    	if (StringUtil.isEmpty(sName)) {
    		throw new BizException("양식명을 입력하세요.");
    	}
    	
    	if ((new ObjectMapper()).readValue(sData, HashMap.class).size() == 0) {
    		throw new BizException("선택된 양식항목이 없습니다.");
    	}

    	String sAssetCode = request.getParameter("assetCode");
    	
    	Date currentDate = new Date();
    	
    	if (Integer.parseInt(sFormCode) > 0) {
    		DBObject dbObject = new BasicDBObject();
    		dbObject.put("formName", sName);
    		dbObject.put("formData", sData);
    		dbObject.put("formDesc", sDesc);
    		dbObject.put("modifyTime", currentDate);
    		dbObject.put("modifyId", userInfo.getAdmin().getLoginID());
    		
    		productService.updateAutoReportForm(userInfo.getPromDb(), new BasicDBObject("formCode",Integer.parseInt(sFormCode)), dbObject);
    	} else {
    		DBObject dbObject = new BasicDBObject();
    		dbObject.put("formName", sName);
    		dbObject.put("formReportType", Integer.parseInt(sReportType));
    		dbObject.put("formData", sData);
    		dbObject.put("formDesc", sDesc);
    		dbObject.put("formType", Integer.parseInt(sFormType));
    		
    		if (!StringUtil.isEmpty(sAssetCode)) {
    			BasicDBList applyList = new BasicDBList();
    			applyList.add(new BasicDBObject("code", Integer.parseInt(sAssetCode)));
    			dbObject.put("appliedAssets", applyList);
    			
    			DBObject condition = new BasicDBObject("formType", Integer.parseInt(sFormType));
    			condition.put("formReportType", Integer.parseInt(sReportType));
    			DBObject query1 = new BasicDBObject("appliedAssets", new BasicDBObject("code", Integer.parseInt(sAssetCode)));
    			DBObject query2 = new BasicDBObject("$pull", query1);
    			productService.pullAutoReportForm(userInfo.getPromDb(), condition, query2);
    		} else {   		
    			dbObject.put("appliedAssets", new BasicDBList());
    		}
    		
    		dbObject.put("createTime", currentDate);
    		dbObject.put("createId", userInfo.getAdmin().getLoginID());
    		dbObject.put("modifyTime", currentDate);
    		dbObject.put("modifyId", userInfo.getAdmin().getLoginID());
    	    
    		productService.insertAutoReportForm(userInfo.getPromDb(), dbObject);
    	}
    	
    	return "forward:/product/formList"; 
    }    

    @RequestMapping("/formDataSave")
    public ModelAndView formDataSave(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
    	logger.debug("제품별양식관리[formDataSave]");
    	
    	UserInfo userInfo = (UserInfo)request.getSession().getAttribute("userInfo");
    	
    	String sFormCode = request.getParameter("formCode");
    	String sData = request.getParameter("data");
    	
    	if ((new ObjectMapper()).readValue(sData, HashMap.class).size() == 0) {
    		throw new BizException("선택된 양식항목이 없습니다.");
    	}

    	Date currentDate = new Date();
    	
    	if (Integer.parseInt(sFormCode) > 0) {
    		DBObject dbObject = new BasicDBObject();
    		dbObject.put("formData", sData);
    		dbObject.put("modifyTime", currentDate);
    		dbObject.put("modifyId", userInfo.getAdmin().getLoginID());
    		productService.updateAutoReportForm(userInfo.getPromDb(), new BasicDBObject("formCode",Integer.parseInt(sFormCode)), dbObject);
    	}	
    	
    	ModelAndView modelAndView = new ModelAndView("jsonView");
    	return modelAndView.addObject("status", "success");
    }    
    
    
    @RequestMapping("/formDelete")
    public String formDelete(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
    	logger.debug("제품별양식관리[formDelete] ");

    	UserInfo userInfo = (UserInfo)request.getSession().getAttribute("userInfo");
    	
    	String sCode = request.getParameter("code");

    	BasicDBObject condition = new BasicDBObject("formCode", Integer.parseInt(sCode));
    	productService.deleteAutoReportForm(userInfo.getPromDb(), condition);
    	
    	return "forward:/product/formList";
    }    
    
    @RequestMapping("/appliedAssets")
    public ModelAndView appliedAssets(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
    	logger.debug("제품별양식관리[appliedAssets]");
    	
    	UserInfo userInfo = (UserInfo)request.getSession().getAttribute("userInfo");
    	
    	String sFormCode = request.getParameter("formCode");
    	
    	ModelAndView modelAndView = new ModelAndView("jsonView");
    	
		DBObject dbObj = productService.selectAutoReportForm(userInfo.getPromDb(), Integer.parseInt(sFormCode));
		modelAndView.addObject("appliedAssets", dbObj.get("appliedAssets"));
    	
    	return modelAndView.addObject("status", "success");
    }    
    
	@RequestMapping("/applyAssetSave")
    public String applyAssetSave(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
    	logger.debug("제품별양식관리[applyAssetSave]");
    	
    	UserInfo userInfo = (UserInfo)request.getSession().getAttribute("userInfo");
    	
    	String sFormCode = request.getParameter("formCode");
    	String sFormType = request.getParameter("formType");
    	String sReportType = request.getParameter("reportType");
    	String sApplyAsset = request.getParameter("applyAsset");
    	String sChangeAsset = request.getParameter("changeAsset");

		ObjectMapper mapper = new ObjectMapper();
    	List<HashMap<String,Object>> applyAssetList = mapper.readValue(sApplyAsset,
    			TypeFactory.defaultInstance().constructCollectionType(List.class,  
    			   HashMap.class));        	
		
    	List<HashMap<String,Object>> changeAssetList = mapper.readValue(sChangeAsset,
    			TypeFactory.defaultInstance().constructCollectionType(List.class,  
    			   HashMap.class));        	
		
		if (changeAssetList.size() > 0) {
			List<Integer> lstCodes = new ArrayList<Integer>();
			for (HashMap<String, Object> hMap : changeAssetList) {
				lstCodes.add((Integer)hMap.get("code"));
			}
			DBObject condition = new BasicDBObject("formType", Integer.parseInt(sFormType));
			condition.put("formReportType", Integer.parseInt(sReportType));
			
			DBObject query1 = new BasicDBObject("code", new BasicDBObject("$in", lstCodes));
			DBObject query2 = new BasicDBObject("appliedAssets", query1);
			DBObject query3 = new BasicDBObject("$pull", query2);
			
			productService.pullAutoReportForm(userInfo.getPromDb(), condition, query3);
		}

		DBObject dbObject = new BasicDBObject("appliedAssets", applyAssetList);
		productService.updateAutoReportForm(userInfo.getPromDb(), new BasicDBObject("formCode",Integer.parseInt(sFormCode)), dbObject);
		
		return "forward:/product/formList";
    }    

	@RequestMapping("/applyFormSave")
    public ModelAndView applyFormSave(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
    	logger.debug("제품별양식관리[applyFormSave]");
    	
    	UserInfo userInfo = (UserInfo)request.getSession().getAttribute("userInfo");
    	
    	String sAssetCode = request.getParameter("assetCode");
    	if (sAssetCode.indexOf("_") != -1) {
    		sAssetCode = sAssetCode.substring(0, sAssetCode.indexOf("_"));
    	}
    	
    	String sFormList = request.getParameter("formList");

    	ObjectMapper mapper = new ObjectMapper();
    	List<HashMap<String,Object>> fromList = mapper.readValue(sFormList,
    			TypeFactory.defaultInstance().constructCollectionType(List.class,  
    			   HashMap.class));        	

    	DBObject condition = new BasicDBObject();
		DBObject query1 = new BasicDBObject("code", Integer.parseInt(sAssetCode));
		DBObject query2 = new BasicDBObject("appliedAssets", query1);
		DBObject query3 = new BasicDBObject("$pull", query2);
    	productService.pullAutoReportForm(userInfo.getPromDb(), condition, query3);

    	for (HashMap<String, Object> hMap : fromList) {
    		condition = new BasicDBObject("formCode", hMap.get("formCode"));
    		query1 = new BasicDBObject("code", Integer.parseInt(sAssetCode));
    		query2 = new BasicDBObject("appliedAssets", query1);
    		query3 = new BasicDBObject("$addToSet", query2);
    		productService.pullAutoReportForm(userInfo.getPromDb(), condition, query3);
    	}
		
    	ModelAndView modelAndView = new ModelAndView("jsonView");
    	return modelAndView.addObject("status", "success");
    }    
	
    @RequestMapping("/formInAsset")
    public ModelAndView formInAsset(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
    	logger.debug("제품별양식관리[formInAsset]");
    	
    	UserInfo userInfo = (UserInfo)request.getSession().getAttribute("userInfo");
    	
    	String sAssetCode = request.getParameter("assetCode");
    	if (sAssetCode.indexOf("_") != -1) {
    		sAssetCode = sAssetCode.substring(0, sAssetCode.indexOf("_"));
    	}
    	
    	List<DBObject> applyList = productService.selectFormInAsset(userInfo.getPromDb(), Integer.parseInt(sAssetCode));

    	ModelAndView modelAndView = new ModelAndView("jsonView");
    	modelAndView.addObject("applyList", applyList);
    	return modelAndView.addObject("status", "success");
    }    

    public HashMap<String, String> getDefaultFormData(String sPrefix, String sType) throws Exception {
    	String sDefaultData = "";
    	switch (Integer.parseInt(sType)) { //1:일일,2:주간,3:월간,4:임의보고서
    	case 1:
    		if (sPrefix.toLowerCase().equals("fw")) {
    			sDefaultData = "{\"opt1\":\"on\",\"rd1\":\"2\",\"opt2\":\"on\",\"opt3\":\"on\",\"opt4\":\"on\",\"rd4\":\"2\",\"opt5\":\"on\",\"opt6\":\"on\",\"opt7\":\"on\",\"rd7\":\"2\",\"opt8\":\"on\",\"opt9\":\"on\",\"opt10\":\"on\",\"rd10\":\"2\",\"opt12\":\"on\",\"rd12\":\"2\",\"opt14\":\"on\",\"rd14\":\"2\",\"opt16\":\"on\",\"rd16\":\"3\",\"opt18\":\"on\",\"rd18\":\"3\",\"opt20\":\"on\",\"rd20\":\"3\",\"opt23\":\"on\",\"ck23\":\"on\",\"opt26\":\"on\",\"ck26\":\"on\",\"opt99\":\"on\"}";
    		} else if (sPrefix.toLowerCase().equals("waf")) {
    			sDefaultData = "{\"opt1\":\"on\"}";
    		} else if (sPrefix.toLowerCase().equals("ips")) {
    			sDefaultData = "{\"opt1\":\"on\"}";
    		} else if (sPrefix.toLowerCase().equals("ids")) {
    			sDefaultData = "{\"opt1\":\"on\"}";
    		} else if (sPrefix.toLowerCase().equals("ddos")) {
    			sDefaultData = "{\"opt1\":\"on\"}";
    		} else if (sPrefix.toLowerCase().equals("ddos_dp")) {
    			sDefaultData = "{\"opt1\":\"on\"}";
    		} else if (sPrefix.toLowerCase().equals("servicedesk")) {
    			sDefaultData = "{\"opt1\":\"on\"}";
    		}
    		break;
    	case 2:
    		if (sPrefix.toLowerCase().equals("fw")) {
    			sDefaultData = "{\"opt1\":\"on\",\"opt4\":\"on\",\"opt7\":\"on\",\"opt10\":\"on\",\"rd10\":\"1\",\"opt13\":\"on\",\"rd13\":\"1\",\"opt16\":\"on\",\"rd16\":\"1\",\"opt19\":\"on\",\"rd19\":\"1\",\"opt22\":\"on\",\"opt23\":\"on\",\"ck23\":\"on\",\"opt24\":\"on\",\"opt25\":\"on\",\"opt26\":\"on\",\"ck26\":\"on\",\"opt27\":\"on\",\"opt99\":\"on\"}";
    		} else if (sPrefix.toLowerCase().equals("waf")) {
    			sDefaultData = "{\"opt1\":\"on\"}";
    		} else if (sPrefix.toLowerCase().equals("ips")) {
    			sDefaultData = "{\"opt1\":\"on\"}";
    		} else if (sPrefix.toLowerCase().equals("ids")) {
    			sDefaultData = "{\"opt1\":\"on\"}";
    		} else if (sPrefix.toLowerCase().equals("ddos")) {
    			sDefaultData = "{\"opt1\":\"on\"}";
    		} else if (sPrefix.toLowerCase().equals("ddos_dp")) {
    			sDefaultData = "{\"opt1\":\"on\"}";
    		} else if (sPrefix.toLowerCase().equals("servicedesk")) {
    			sDefaultData = "{\"opt1\":\"on\"}";
    		}
    		break;
    	case 3:
    		if (sPrefix.toLowerCase().equals("fw")) {
    			sDefaultData = "{\"opt1\":\"on\",\"opt4\":\"on\",\"opt7\":\"on\",\"opt10\":\"on\",\"rd10\":\"1\",\"opt13\":\"on\",\"rd13\":\"1\",\"opt16\":\"on\",\"rd16\":\"1\",\"opt19\":\"on\",\"rd19\":\"1\",\"opt22\":\"on\",\"opt23\":\"on\",\"ck23\":\"on\",\"opt24\":\"on\",\"opt25\":\"on\",\"opt26\":\"on\",\"ck26\":\"on\",\"opt27\":\"on\",\"opt99\":\"on\"}";
    		} else if (sPrefix.toLowerCase().equals("waf")) {
    			sDefaultData = "{\"opt1\":\"on\"}";
    		} else if (sPrefix.toLowerCase().equals("ips")) {
    			sDefaultData = "{\"opt1\":\"on\"}";
    		} else if (sPrefix.toLowerCase().equals("ids")) {
    			sDefaultData = "{\"opt1\":\"on\"}";
    		} else if (sPrefix.toLowerCase().equals("ddos")) {
    			sDefaultData = "{\"opt1\":\"on\"}";
    		} else if (sPrefix.toLowerCase().equals("ddos_dp")) {
    			sDefaultData = "{\"opt1\":\"on\"}";
    		} else if (sPrefix.toLowerCase().equals("servicedesk")) {
    			sDefaultData = "{\"opt1\":\"on\"}";
    		}
    		break;
    	case 4:
    		if (sPrefix.toLowerCase().equals("fw")) {
    			sDefaultData = "{\"opt1\":\"on\",\"opt4\":\"on\",\"opt7\":\"on\",\"opt10\":\"on\",\"rd10\":\"1\",\"opt13\":\"on\",\"rd13\":\"1\",\"opt16\":\"on\",\"rd16\":\"1\",\"opt19\":\"on\",\"rd19\":\"1\",\"opt22\":\"on\",\"opt25\":\"on\",\"opt99\":\"on\"}";
    		} else if (sPrefix.toLowerCase().equals("waf")) {
    			sDefaultData = "{\"opt1\":\"on\"}";
    		} else if (sPrefix.toLowerCase().equals("ips")) {
    			sDefaultData = "{\"opt1\":\"on\"}";
    		} else if (sPrefix.toLowerCase().equals("ids")) {
    			sDefaultData = "{\"opt1\":\"on\"}";
    		} else if (sPrefix.toLowerCase().equals("ddos")) {
    			sDefaultData = "{\"opt1\":\"on\"}";
    		} else if (sPrefix.toLowerCase().equals("ddos_dp")) {
    			sDefaultData = "{\"opt1\":\"on\"}";
    		} else if (sPrefix.toLowerCase().equals("servicedesk")) {
    			sDefaultData = "{\"opt1\":\"on\"}";
    		}
    		break;
    	}
    	return new ObjectMapper().readValue(sDefaultData, HashMap.class);
    }
    
}
