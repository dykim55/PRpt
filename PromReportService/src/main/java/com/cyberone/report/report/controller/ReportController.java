package com.cyberone.report.report.controller;

import java.io.File;
import java.io.FileInputStream;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.cyberone.report.Constants;
import com.cyberone.report.core.report.FwDailyReport;
import com.cyberone.report.core.report.FwMonthlyReport;
import com.cyberone.report.core.report.FwPeriodReport;
import com.cyberone.report.core.report.FwWeeklyReport;
import com.cyberone.report.core.service.SDService;
import com.cyberone.report.exception.BizException;
import com.cyberone.report.model.UserInfo;
import com.cyberone.report.product.service.ProductService;
import com.cyberone.report.report.service.ReportService;
import com.cyberone.report.utils.StringUtil;
import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

@Controller
@RequestMapping("/report")
public class ReportController {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ReportService reportService;

    @Autowired
    private ProductService productService;
    
    @Autowired
    private SDService sdService;
    
    @RequestMapping("/")
    public String reportManage(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        logger.debug("자산목록[/reportManage] ");
        
        UserInfo userInfo = (UserInfo)request.getSession().getAttribute("userInfo");
        model.addAttribute("userInfo", userInfo);
        
        return "report/reportManage";
    }

    @RequestMapping("/reportOption")
    public String reportOption(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        logger.debug("자산목록[/reportOption] ");
        
        UserInfo userInfo = (UserInfo)request.getSession().getAttribute("userInfo");
        
        String sAssets = request.getParameter("slctAssets");
        String sReportType = request.getParameter("reportType");
        
		ObjectMapper mapper = new ObjectMapper();
    	List<HashMap<String,Object>> slctAssetList = mapper.readValue(sAssets,
    			TypeFactory.defaultInstance().constructCollectionType(List.class,  
    			   HashMap.class));        	
        
    	StringBuffer sBuf = null;
    	for (HashMap<String,Object> hMap : slctAssetList) {
    		String sAssetCode = StringUtil.convertString(hMap.get("id"));
    		sAssetCode = sAssetCode.substring(0, sAssetCode.indexOf("_"));
    		
    		DBObject dbObj = productService.selectAutoReportForm(userInfo.getPromDb(), Integer.parseInt(sReportType), Integer.parseInt(sAssetCode));
    		if (dbObj == null) {
    			if (sBuf == null) {
    				sBuf = new StringBuffer();
    				sBuf.append("[ ").append((String)hMap.get("name"));
    			} else {
    				sBuf.append(", ").append((String)hMap.get("name"));
    			}
    		}
    	}
    	
    	if (sBuf != null) {
    		sBuf.append(" ]");
    		throw new BizException("적용된 보고서양식이 존재하지 않습니다.</br>" + sBuf.toString());
    	}
    	
        model.addAttribute("slctAssetList", slctAssetList);
        
        return "report/reportOption";
    }
    
    @RequestMapping("/assetList")
    public ModelAndView assetList(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        logger.debug("자산목록[assetList] ");
        
        UserInfo userInfo = (UserInfo)request.getSession().getAttribute("userInfo");

		HashMap<String, Integer> groupMap = new HashMap<String, Integer>();
		HashMap<String, DBObject> productGroupMap = new HashMap<String, DBObject>();
		List<HashMap<String, Object>> treeData = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> rootNode = null;
		HashMap<String, Object> node1 = null;
		HashMap<String, Object> node2 = null;
		HashMap<String, Object> node3 = null;
		DBObject assetStatus = null;
        
		int productGroupCode = 0;
		
		try {
			int nDomainCode = userInfo.getDomainCode();
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			rootNode = new HashMap<String, Object>();
			rootNode.put("id", 0);
			rootNode.put("name", "도메인명");
			rootNode.put("level", 0);
			rootNode.put("parent", "");
			rootNode.put("isLeaf", false);
			rootNode.put("expanded", true);
			rootNode.put("loaded", true);
			rootNode.put("enbl", false);
			rootNode.put("createId", "");
			rootNode.put("createTime", "");
			rootNode.put("assetIp", "");
			rootNode.put("productCode", "");
			rootNode.put("assetType", "");
			rootNode.put("memo", "");
			treeData.add(rootNode);

			List<DBObject> assetGroups = null;
			assetGroups = reportService.selectAssetGroups(userInfo.getPromDb(), nDomainCode, -1, false);
			
			List<DBObject> productGroups = reportService.selectProductGroups(userInfo.getPromDb(), false);
			for (DBObject group : productGroups) {
				productGroupMap.put(StringUtil.convertString(group.get("productGroupCode")), group);
			}
			
			String strSearchPrefix = "";
			if (productGroupCode > 0) {
				DBObject productGroup = productGroupMap.get(StringUtil.convertString(productGroupCode));
				strSearchPrefix = StringUtil.convertString(productGroup.get("prefix"));
			}
			
			for (DBObject group : assetGroups) {
				
				if ((Integer)group.get("parentCode") == 0) {
					node1 = new HashMap<String, Object>();
					node1.put("id", StringUtil.convertString(group.get("groupCode")));
					node1.put("name", StringUtil.convertString(group.get("groupName")));
					node1.put("level", (Integer)rootNode.get("level") + 1);
					node1.put("parent", (Integer)rootNode.get("id"));
					node1.put("isLeaf", false);
					node1.put("expanded", true);
					node1.put("loaded", true);
					node1.put("enbl", false);
					node1.put("createId", "");
					node1.put("createTime", "");
					node1.put("assetIp", "");
					node1.put("productCode", "");
					node1.put("assetType", "");
					node1.put("memo", StringUtil.convertString(group.get("memo")));
					node1.put("stat", "");
					groupMap.put(StringUtil.convertString(group.get("groupCode")), (Integer)group.get("groupCode"));

					List<DBObject> childGroups = reportService.selectAssetGroups(userInfo.getPromDb(), nDomainCode, (Integer)group.get("groupCode"), false);
					if (childGroups.size() > 0) {
						boolean bFlag = true;
						for (DBObject childGroup : childGroups) {
							node2 = new HashMap<String, Object>();
							node2.put("id", (Integer)childGroup.get("groupCode"));
							node2.put("name", StringUtil.convertString(childGroup.get("groupName")));
							node2.put("level", (Integer)node1.get("level") + 1);
							node2.put("parent", StringUtil.convertString(childGroup.get("parentCode")));
							node2.put("isLeaf", false);
							node2.put("expanded", false);
							node2.put("loaded", true);
							node2.put("enbl", false);
							node2.put("createId", "");
							node2.put("createTime", "");
							node2.put("assetIp", "");
							node2.put("productCode", "");
							node2.put("assetType", "");
							node2.put("memo", StringUtil.convertString(childGroup.get("memo")));
							node2.put("stat", "");
							
							/*
							if (strSearchWord.trim().length() > 0) {
								if (strSearchType.equals("groupName")) {
									if (childGroup.getGroupName().toLowerCase().indexOf(strSearchWord.toLowerCase()) < 0) {
										continue;
									}
								}
							}
							*/
							
							boolean bFlag1 = true;
							List<DBObject> assets = reportService.selectAssetsProduct(userInfo.getPromDb(), (Integer)childGroup.get("groupCode"), 0);
							for (DBObject asset : assets) {
								node3 = new HashMap<String, Object>();
								node3.put("name", StringUtil.convertString(asset.get("assetName")));
								node3.put("level", (Integer)node2.get("level") + 1);
								node3.put("parent", (Integer)node2.get("id"));
								node3.put("isLeaf", true);
								node3.put("expanded", true);
								node3.put("loaded", true);
								node3.put("enbl", false);
								node3.put("createId", StringUtil.convertString(asset.get("createId")));
								node3.put("createTime", dateFormat.format((Date) asset.get("createTime")));
								node3.put("assetIp", StringUtil.convertString(asset.get("assetIp")));
								node3.put("productCode", StringUtil.convertString(asset.get("productCode")));
								//node3.put("assetType", StringUtil.convertString(asset.get("assetType")));
								node3.put("memo", StringUtil.convertString(asset.get("memo")));
								//node3.put("stat", asset.getAssetStatus().getStatus());
								assetStatus = reportService.selectStatus(userInfo.getPromDb(), (Integer)asset.get("assetCode"));
								node3.put("stat", StringUtil.convertString(assetStatus.get("status")));
								node3.put("updateTime", assetStatus.get("updateTime"));

								/*
								if (strSearchWord.trim().length() > 0) {
									if (strSearchType.equals("assetName")) {
										if (asset.getAssetName().toLowerCase().indexOf(strSearchWord.toLowerCase()) < 0) {
											continue;
										}
									}
								}								
								*/
								
								HashMap<String, Integer> parserMap = new HashMap<String, Integer>();
								
								if (StringUtil.convertString(asset.get("productCode")).substring(0, 3).equals("504")) { //UTM
									
									DBObject assetPolicy = reportService.selectAssetPolicy(userInfo.getPromDb(), (Integer)asset.get("assetCode"));
									DBObject logPolicy = (DBObject)assetPolicy.get("logPolicy");
									DBObject pdLog = (DBObject)logPolicy.get("pdLog");
								
									List<DBObject> parsers = reportService.selectProductParsers(userInfo.getPromDb(), (Integer)asset.get("productCode"), false);
									for (DBObject parser : parsers) {
										HashMap<String, Object> tmpNode = (HashMap<String, Object>)node3.clone();
										
										if ((Integer)parser.get("logCode") == 2) {
											continue;
										}

										BasicDBList ps = (BasicDBList)pdLog.get("parserCodes");
										
										for(int i = 0; i < ps.size(); i++) {
											int parserCode = (Integer)ps.get(i);
											if (parserCode == (Integer)parser.get("parserCode")) {
												if ((Integer)parser.get("posStat") == 0) {
													logger.info("@" + asset.get("productCode"));
													continue;
												}
												DBObject productGroup = productGroupMap.get(String.valueOf(parser.get("posStat")));
												tmpNode.put("prefix", productGroup.get("prefix"));
												tmpNode.put("id", asset.get("assetCode")+"_"+productGroup.get("prefix"));
												tmpNode.put("assetType", "UTM");
												
												if (productGroupCode > 0) {
													if (!strSearchPrefix.equals((String)tmpNode.get("prefix"))) {
														continue;
													}
												}
												if (parserMap.get(productGroup.get("prefix")) != null) {
													continue;
												}
												parserMap.put(StringUtil.convertString(productGroup.get("prefix")), (Integer)parser.get("parserCode"));
												
												if (bFlag) {
													bFlag = false; //권한이있는 자산그룹(level2)가 있을경우만 level1을 추가한다.
													treeData.add(node1);								
												}
												if (bFlag1) {
													bFlag1 = false;
													treeData.add(node2);								
												}
												treeData.add(tmpNode);
											}
										}
									}
								} else {
									DBObject productGroup = productGroupMap.get(StringUtil.convertString(asset.get("productCode")).substring(0, 3));
									
									String sTempPrefix = StringUtil.convertString(productGroup.get("prefix")).toLowerCase();
									if (sTempPrefix.equals("ddos") && (Integer)asset.get("productCode") == 506126) {
										sTempPrefix = sTempPrefix + "_dp";
									}
									
									node3.put("prefix", sTempPrefix);
									node3.put("id", asset.get("assetCode")+"_"+sTempPrefix);
									node3.put("assetType", sTempPrefix);
									
									if (productGroupCode > 0) {
										if (!strSearchPrefix.equals((String)node3.get("prefix"))) {
											continue;
										}
									}
									
									if (bFlag) {
										bFlag = false; //권한이있는 자산그룹(level2)가 있을경우만 level1을 추가한다.
										treeData.add(node1);								
									}
	
									if (bFlag1) {
										bFlag1 = false;
										treeData.add(node2);								
									}
									
									treeData.add(node3);
								}
							}
						}
					}					
				}				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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

    @RequestMapping("/assetApplyList")
    public ModelAndView assetApplyList(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        logger.debug("자산목록[assetApplyList] ");
        
        UserInfo userInfo = (UserInfo)request.getSession().getAttribute("userInfo");

        String sPrefix = request.getParameter("prefix");
        String sReportType = request.getParameter("reportType");
        
		HashMap<String, Integer> groupMap = new HashMap<String, Integer>();
		HashMap<String, DBObject> productGroupMap = new HashMap<String, DBObject>();
		List<HashMap<String, Object>> treeData = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> rootNode = null;
		HashMap<String, Object> node1 = null;
		HashMap<String, Object> node2 = null;
		HashMap<String, Object> node3 = null;
        
		try {
			int nDomainCode = userInfo.getDomainCode();
			
			rootNode = new HashMap<String, Object>();
			rootNode.put("id", 0);
			rootNode.put("name", "도메인명");
			rootNode.put("level", 0);
			rootNode.put("parent", "");
			rootNode.put("isLeaf", false);
			rootNode.put("expanded", true);
			rootNode.put("loaded", true);
			rootNode.put("enbl", false);
			rootNode.put("prefix", "");
			rootNode.put("assetType", "");
			rootNode.put("applyForm","");
			rootNode.put("applyFormCode",0);
			treeData.add(rootNode);

			List<DBObject> assetGroups = null;
			assetGroups = reportService.selectAssetGroups(userInfo.getPromDb(), nDomainCode, -1, false);
			
			List<DBObject> productGroups = reportService.selectProductGroups(userInfo.getPromDb(), false);
			for (DBObject group : productGroups) {
				productGroupMap.put(StringUtil.convertString(group.get("productGroupCode")), group);
			}
			
			for (DBObject group : assetGroups) {
				
				if ((Integer)group.get("parentCode") == 0) {
					node1 = new HashMap<String, Object>();
					node1.put("id", StringUtil.convertString(group.get("groupCode")));
					node1.put("name", StringUtil.convertString(group.get("groupName")));
					node1.put("level", (Integer)rootNode.get("level") + 1);
					node1.put("parent", (Integer)rootNode.get("id"));
					node1.put("isLeaf", false);
					node1.put("expanded", true);
					node1.put("loaded", true);
					node1.put("enbl", false);
					node1.put("prefix", "");
					node1.put("assetType", "");
					node1.put("applyForm","");
					node1.put("applyFormCode",0);
					groupMap.put(StringUtil.convertString(group.get("groupCode")), (Integer)group.get("groupCode"));
					
					List<DBObject> childGroups = reportService.selectAssetGroups(userInfo.getPromDb(), nDomainCode, (Integer)group.get("groupCode"), false);
					if (childGroups.size() > 0) {
						boolean bFlag = true;
						for (DBObject childGroup : childGroups) {
							node2 = new HashMap<String, Object>();
							node2.put("id", (Integer)childGroup.get("groupCode"));
							node2.put("name", StringUtil.convertString(childGroup.get("groupName")));
							node2.put("level", (Integer)node1.get("level") + 1);
							node2.put("parent", StringUtil.convertString(childGroup.get("parentCode")));
							node2.put("isLeaf", false);
							node2.put("expanded", true);
							node2.put("loaded", true);
							node2.put("enbl", false);
							node2.put("prefix", "");
							node2.put("assetType", "");
							node2.put("applyForm","");
							node2.put("applyFormCode",0);
							
							/*
							if (strSearchWord.trim().length() > 0) {
								if (strSearchType.equals("groupName")) {
									if (childGroup.getGroupName().toLowerCase().indexOf(strSearchWord.toLowerCase()) < 0) {
										continue;
									}
								}
							}
							*/
							
							boolean bFlag1 = true;
							List<DBObject> assets = reportService.selectAssetsProduct(userInfo.getPromDb(), (Integer)childGroup.get("groupCode"), 0);
							for (DBObject asset : assets) {
								node3 = new HashMap<String, Object>();
								node3.put("name", StringUtil.convertString(asset.get("assetName")));
								node3.put("level", (Integer)node2.get("level") + 1);
								node3.put("parent", (Integer)node2.get("id"));
								node3.put("isLeaf", true);
								node3.put("expanded", false);
								node3.put("loaded", true);
								node3.put("enbl", false);

								/*
								if (strSearchWord.trim().length() > 0) {
									if (strSearchType.equals("assetName")) {
										if (asset.getAssetName().toLowerCase().indexOf(strSearchWord.toLowerCase()) < 0) {
											continue;
										}
									}
								}								
								*/

								DBObject dbObj = productService.selectAutoReportForm(userInfo.getPromDb(), Integer.valueOf(sReportType), (Integer)asset.get("assetCode"));
								node3.put("applyForm", dbObj != null ? StringUtil.convertString(dbObj.get("formName")) : "");
								node3.put("applyFormCode", dbObj != null ? (Integer)dbObj.get("formCode") : 0);
								
								if (StringUtil.convertString(asset.get("productCode")).substring(0, 3).equals("504")) { //UTM
									
									DBObject assetPolicy = reportService.selectAssetPolicy(userInfo.getPromDb(), (Integer)asset.get("assetCode"));
									DBObject logPolicy = (DBObject)assetPolicy.get("logPolicy");
									DBObject pdLog = (DBObject)logPolicy.get("pdLog");
								
									List<DBObject> parsers = reportService.selectProductParsers(userInfo.getPromDb(), (Integer)asset.get("productCode"), false);
									for (DBObject parser : parsers) {
										HashMap<String, Object> tmpNode = (HashMap<String, Object>)node3.clone();
										
										if ((Integer)parser.get("logCode") == 2) {
											continue;
										}

										BasicDBList ps = (BasicDBList)pdLog.get("parserCodes");
										
										for(int i = 0; i < ps.size(); i++) {
											int parserCode = (Integer)ps.get(i);
											if (parserCode == (Integer)parser.get("parserCode")) {
												if ((Integer)parser.get("posStat") == 0) {
													logger.info("@" + asset.get("productCode"));
													continue;
												}
												DBObject productGroup = productGroupMap.get(String.valueOf(parser.get("posStat")));
												String sTempPrefix = StringUtil.convertString(productGroup.get("prefix")).toLowerCase();
												
												tmpNode.put("prefix", sTempPrefix);
												tmpNode.put("id", asset.get("assetCode"));
												tmpNode.put("assetType", "utm");
												
												if (!sPrefix.equals(StringUtil.convertString(tmpNode.get("prefix")).toLowerCase())) {
													continue;
												}

												if (bFlag) {
													bFlag = false; //권한이있는 자산그룹(level2)가 있을경우만 level1을 추가한다.
													treeData.add(node1);								
												}
												if (bFlag1) {
													bFlag1 = false;
													treeData.add(node2);								
												}
												treeData.add(tmpNode);
											}
										}
									}
								} else {
									DBObject productGroup = productGroupMap.get(StringUtil.convertString(asset.get("productCode")).substring(0, 3));
									
									String sTempPrefix = StringUtil.convertString(productGroup.get("prefix")).toLowerCase();
									if (sTempPrefix.equals("ddos") && (Integer)asset.get("productCode") == 506126) {
										sTempPrefix = sTempPrefix + "_dp";
									}

									if (!sPrefix.equals(sTempPrefix)) {
										continue;
									}
									
									node3.put("prefix", sTempPrefix);
									node3.put("id", asset.get("assetCode"));
									node3.put("assetType", sTempPrefix);
									
									if (bFlag) {
										bFlag = false; //권한이있는 자산그룹(level2)가 있을경우만 level1을 추가한다.
										treeData.add(node1);								
									}
	
									if (bFlag1) {
										bFlag1 = false;
										treeData.add(node2);								
									}
									
									treeData.add(node3);
								}
							}
						}
					}					
				}				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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

    @RequestMapping("/groupApplyList")
    public ModelAndView groupApplyList(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        logger.debug("자산그룹목록[groupApplyList]");
        
        UserInfo userInfo = (UserInfo)request.getSession().getAttribute("userInfo");

        String sPrefix = request.getParameter("prefix");
        String sReportType = request.getParameter("reportType");
        
		HashMap<String, Integer> groupMap = new HashMap<String, Integer>();
		List<HashMap<String, Object>> treeData = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> rootNode = null;
		HashMap<String, Object> node1 = null;
		HashMap<String, Object> node2 = null;
		HashMap<String, Object> node3 = null;
        
		try {
			int nDomainCode = userInfo.getDomainCode();
			
			rootNode = new HashMap<String, Object>();
			rootNode.put("id", 0);
			rootNode.put("name", "도메인명");
			rootNode.put("level", 0);
			rootNode.put("parent", "");
			rootNode.put("isLeaf", false);
			rootNode.put("expanded", true);
			rootNode.put("loaded", true);
			rootNode.put("enbl", false);
			rootNode.put("prefix", "");
			rootNode.put("assetType", "");
			rootNode.put("applyForm","");
			rootNode.put("applyFormCode",0);
			treeData.add(rootNode);

			List<DBObject> assetGroups = null;
			assetGroups = reportService.selectAssetGroups(userInfo.getPromDb(), nDomainCode, -1, false);
			
			for (DBObject group : assetGroups) {
				
				if ((Integer)group.get("parentCode") == 0) {
					node1 = new HashMap<String, Object>();
					node1.put("id", StringUtil.convertString(group.get("groupCode")));
					node1.put("name", StringUtil.convertString(group.get("groupName")));
					node1.put("level", (Integer)rootNode.get("level") + 1);
					node1.put("parent", (Integer)rootNode.get("id"));
					node1.put("isLeaf", false);
					node1.put("expanded", true);
					node1.put("loaded", true);
					node1.put("enbl", false);
					node1.put("prefix", "");
					node1.put("assetType", "");
					node1.put("applyForm","");
					node1.put("applyFormCode",0);
					groupMap.put(StringUtil.convertString(group.get("groupCode")), (Integer)group.get("groupCode"));
					
					List<DBObject> childGroups = reportService.selectAssetGroups(userInfo.getPromDb(), nDomainCode, (Integer)group.get("groupCode"), false);
					if (childGroups.size() > 0) {
						boolean bFlag = true;
						for (DBObject childGroup : childGroups) {
							node2 = new HashMap<String, Object>();
							node2.put("id", (Integer)childGroup.get("groupCode"));
							node2.put("name", StringUtil.convertString(childGroup.get("groupName")));
							node2.put("level", (Integer)node1.get("level") + 1);
							node2.put("parent", StringUtil.convertString(childGroup.get("parentCode")));
							node2.put("isLeaf", true);
							node2.put("expanded", false);
							node2.put("loaded", true);
							node2.put("enbl", false);
							node2.put("prefix", "");
							node2.put("assetType", "");

							DBObject dbObj = productService.selectAutoReportForm(userInfo.getPromDb(), Integer.valueOf(sReportType), (Integer)childGroup.get("groupCode"));
							node2.put("applyForm", dbObj != null ? StringUtil.convertString(dbObj.get("formName")) : "");
							node2.put("applyFormCode", dbObj != null ? (Integer)dbObj.get("formCode") : 0);
							
							/*
							if (strSearchWord.trim().length() > 0) {
								if (strSearchType.equals("groupName")) {
									if (childGroup.getGroupName().toLowerCase().indexOf(strSearchWord.toLowerCase()) < 0) {
										continue;
									}
								}
							}
							*/
							
							if (bFlag) {
								bFlag = false; //자산그룹(level2)가 있을경우만 level1을 추가한다.
								treeData.add(node1);								
							}
							treeData.add(node2);
						}
					}					
				}				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
    
    @RequestMapping("/reportConfig")
    public ModelAndView reportConfig(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
    	logger.debug("보고서설정 [reportConfig] ");
    	
    	ModelAndView modelAndView = new ModelAndView("jsonView");
    	
    	modelAndView.addObject("prefix", request.getParameter("prefix"));
    	
    	return modelAndView.addObject("status", "success");
    }    
    
    @RequestMapping("/assetReportOption")
    public ModelAndView assetReportOption(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
    	logger.debug("보고서설정 [assetReportOption] ");
    	
    	UserInfo userInfo = (UserInfo)request.getSession().getAttribute("userInfo");
    	
    	String sGroupCode = request.getParameter("groupCode");
    	int groupCode = Integer.parseInt(sGroupCode);
    	
    	DBObject dbObj = reportService.selectReportOption(userInfo.getPromDb(), groupCode);

    	ModelAndView modelAndView = new ModelAndView("jsonView");
    	modelAndView.addObject("reportOption", dbObj);
    	return modelAndView.addObject("status", "success");
    }    
    
    @RequestMapping("/imageDownload")
    public void imageDownload(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
    	logger.debug("보고서설정 [imageDownload] ");
    	
    	UserInfo userInfo = (UserInfo)request.getSession().getAttribute("userInfo");
    	
		String sType = request.getParameter("type");
		
		// 레포트 html에서 호출
		if (sType != null && sType.equals("report")) {
		
			String sClientCode = request.getParameter("code");
			String sPos = request.getParameter("pos");
			String imagePath = "D:/" + "jasper/images/" + sClientCode + "/" + sPos;
			
			File file = new File(imagePath);
			if (!file.exists()) {
				file = new File("D:/" + "jasper/images/no_image.png");
			}
			
			if (file.exists()) {
				int length = (int)file.length();
				byte[] image = new byte[length];
				FileInputStream	fIn	= null;
				try {
					fIn	= new FileInputStream(file);
					for (int readBytes = 0, totalBytes = 0; totalBytes < length && readBytes > -1; totalBytes += readBytes) {
						readBytes	= fIn.read(image, totalBytes, length - totalBytes);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {fIn.close();} catch (Exception e) {}
				}
				
				response.getOutputStream().write(image);
				response.getOutputStream().flush();
			}
			
		}
    	
    }    
    
    @RequestMapping("/fileUpload")
    public String fileUpload(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        logger.debug("파일업로드[/fileUpload] ");
        return "common/fileUpload";
    }

    @RequestMapping("/formSaveSimpleDlg")
    public String formSaveSimpleDlg(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        logger.debug("양식저장[/formSaveSimpleDlg] ");
        return "report/formSaveSimpleDlg";
    }

    @RequestMapping("/applyForm")
    public String applyForm(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        logger.debug("적용양식지정[/applyForm] ");
        return "report/applyFormDlg";
    }

    @RequestMapping("/imageUpload")
    public void imageUpload(MultipartHttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
    	logger.debug("자산목록[/imageUpload] ");
		
        List<MultipartFile> mpfList = request.getFiles("upload_file");
        
		String sAction = request.getParameter("action");
		//String sPositionType = request.getParameter("positionType");
		String sClientCode = request.getParameter("clientCode");

		String sRootPath = (new File("/")).getAbsolutePath();
		
		File toFile = new File(sRootPath + "jasper/images/" + sClientCode + "/");
		if (!toFile.exists()) {
			toFile.mkdirs();
		}

		toFile = new File(sRootPath + "jasper/images/" + sClientCode + "/ci");
		
		if (toFile.exists()) {
			toFile.delete();
		}

		if (sAction != null && sAction.equals("delete")) {
			response.getWriter().write("정상처리 되었습니다.");
			return;
		}
		
		for (MultipartFile mpf : mpfList) {
        	File uploadFile = new File(sRootPath + "jasper/images/" + sClientCode + "/ci");
        	mpf.transferTo(uploadFile);
		}
	}

    @RequestMapping("/imageDelete")
    public void imageDelete(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
    	logger.debug("자산목록[/imageDelete] ");
		
		String sClientCode = request.getParameter("clientCode");
		if (StringUtil.isEmpty(sClientCode)) {
			return;
		}
		
		String sRootPath = (new File("/")).getAbsolutePath();
		
		File toFile = new File(sRootPath + "jasper/images/" + sClientCode + "/");
		if (!toFile.exists()) {
			toFile.mkdirs();
		}
		toFile = new File(sRootPath + "jasper/images/" + sClientCode + "/ci");
		
		if (toFile.exists()) {
			toFile.delete();
		}
	}
    
    
    
    
    
    
    
    
    
    
    
    @SuppressWarnings("unchecked")
	@RequestMapping("/printReport")
    public ModelAndView printReport(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        logger.debug("보고서작성 [printReport] ");
        
        UserInfo userInfo = (UserInfo)request.getSession().getAttribute("userInfo");

        HashMap<String, Object> assetReportMap = new HashMap<String, Object>();
        HashMap<String, Object> assetReportInfo = null;
        HashMap<String, Object> reportData = new HashMap<String, Object>();
        
		String sStartDay = "";
		String sEndDay = "";
        
        String strPrintInfo = request.getParameter("printInfo");
        
		ObjectMapper mapper = new ObjectMapper();
		HashMap<String,Object> printInfoMap = mapper.readValue(StringUtil.convertString(strPrintInfo), HashMap.class);

		String sReportType = StringUtil.convertString(printInfoMap.get("reportType")); 	//1:일일보고서 2:주간보고서 3:월간보고서 4:임의기간보고서
		String sReportSct = StringUtil.convertString(printInfoMap.get("reportSct")); 	//1:관제실적 2:원시로그 통계 3:관제실적+원시로그통계
		String sSearchDate = StringUtil.convertString(printInfoMap.get("searchDate"));
		String sReviewText = StringUtil.convertString(printInfoMap.get("reviewText"));
		String sFileFormat = StringUtil.convertString(printInfoMap.get("format"));

		List<HashMap<String,Object>> formDataList = (ArrayList<HashMap<String, Object>>)printInfoMap.get("formDatas");
		
		if (sReportSct.equals("1") || sReportSct.equals("3")) {
			for (HashMap<String,Object> hMap : formDataList) {
				String sPrefix = StringUtil.convertString(hMap.get("prefix"));
				if (sPrefix.equals("servicedesk")) {
					//sdService.getDataSource(sReportType, sStartDay, sEndDay, reportData, hMap);
					break;
				}
			}
		}
		
		if (sReportSct.equals("2") || sReportSct.equals("3")) {
			for (HashMap<String,Object> hMap : formDataList) {
				String sPrefix = StringUtil.convertString(hMap.get("prefix"));
				if (sPrefix.equals("servicedesk")) {
				} else if (sPrefix.equals("fw")) {
					
					if (sReportType.equals(Constants.REPORT_DAILY)) {
						
						FwDailyReport fwReport = new FwDailyReport(userInfo);
						assetReportInfo = fwReport.getDataSource(sReportType, sSearchDate, hMap);
						
					} else if (sReportType.equals(Constants.REPORT_WEEKLY)) {

						FwWeeklyReport fwReport = new FwWeeklyReport(userInfo.getPromDb(), userInfo.getReportDb());
						assetReportInfo = fwReport.getDataSource(sReportType, sSearchDate, hMap);
						
					} else if (sReportType.equals(Constants.REPORT_MONTHLY)) {

						FwMonthlyReport fwReport = new FwMonthlyReport(userInfo.getPromDb(), userInfo.getReportDb());
						assetReportInfo = fwReport.getDataSource(sReportType, sSearchDate, hMap);
						
					} else if (sReportType.equals(Constants.REPORT_PERIOD)) {

						FwPeriodReport fwReport = new FwPeriodReport(userInfo.getPromDb(), userInfo.getReportDb());
						assetReportInfo = fwReport.getDataSource(sReportType, sSearchDate, hMap);
						
					}
					
				} else if (sPrefix.equals("waf")) {
					
				} else if (sPrefix.equals("ips")) {
					
				} else if (sPrefix.equals("ids")) {
					
				}
				
				assetReportMap.put(StringUtil.convertString(hMap.get("assetCode")), assetReportInfo);
			}
			
		}
		
		/*
		HashMap<String, Object> reportInitInfo = new HashMap<String, Object>();
		HashMap<String, Object> baseReportInfo = new HashMap<String, Object>();
		
		HashMap<String, Object> assetReportInfo = new HashMap<String, Object>();
		
		fwService.FW_SessionLogMonthlyChange("2015-05-01", "2015-05-31", 990001, null, assetReportInfo);
		
		assetReportMap.put("990001_FW", assetReportInfo);
		
		
		String[] saAssets = {"990001_FW"};
		List<HashMap<String, Object>> assetList = fwService.getAssetList(saAssets);
		baseReportInfo.put("AssetList", new SynthesisDataSource(assetList));
		
        
        String sContextPath = request.getSession().getServletContext().getRealPath("/");
        
        SimpleDateFormat dateTime = new SimpleDateFormat("yyyyMMddHHmmss");
        
        String sJaperPath = sContextPath + "WEB-INF/jasper/";
        String sReportPath = sContextPath + "WEB-INF/views/report/";        
        String sExportPath = sJaperPath + "export/";
        String sFileFormat = "pdf";
        
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("baseDir", sJaperPath);
        parameters.put("assetReportMap", assetReportMap);
        parameters.put("baseReportInfo", baseReportInfo);        
        
        File destFile = new File(sExportPath, (String)request.getParameter("ciText") + "_월간종합보고서_" + dateTime.format(new Date()) + "." + sFileFormat);
        
        JasperPrint jasperPrint = null;
        
		try {
			String sourceFileName = sReportPath + sFileFormat + "/MonthlyReport.jasper";
			jasperPrint = JasperFillManager.fillReport(
					sourceFileName, 
					parameters, 
					new JREmptyDataSource());
		} catch (JRException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		//JRDocxExporter exporter = new JRDocxExporter(DefaultJasperReportsContext.getInstance());
		//exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		//exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(destFile));
		//exporter.exportReport();
		
		
		JRPdfExporter exporter = new JRPdfExporter(DefaultJasperReportsContext.getInstance());
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(destFile));
		exporter.exportReport();

        */
		
        ModelAndView modelAndView = new ModelAndView("jsonView");
        return modelAndView.addObject("status", "success");
		
    }    
}
