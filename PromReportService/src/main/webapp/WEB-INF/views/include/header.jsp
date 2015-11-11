<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
    import = "com.cyberone.report.model.UserInfo"
%>

<%
UserInfo userInfo = (UserInfo)request.getAttribute("userInfo");
%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>PROM-통합보안관리시스템</title>

 
<link rel="shortcut icon" href="/img/icon/icon_prom.ico">
<link rel="stylesheet" type="text/css" href="/css/main.css">
<link rel="stylesheet" type="text/css" href="/css/jquery-ui-1.10.2.custom.css"/>
<link rel="stylesheet" type="text/css" href="/css/jqGrid-4.4.1/ui.jqgrid.css"/>
<link rel="stylesheet" type="text/css" href="/css/jqGrid-4.4.1/ui.multiselect.css"/>
<link rel="stylesheet" type="text/css" href="/css/jquery.multiselect.css"/>
 
<script type="text/javascript" src="/js/jquery-1.8.3.js"></script>
<script type="text/javascript" src="/js/jquery-ui-1.10.2.custom.js"></script>
<script type="text/javascript" src="/js/jqGrid-4.4.1/jquery.jqGrid.src.js"></script>
<script type="text/javascript" src="/js/jqGrid-4.4.1/i18n/grid.locale-en.js"></script>
<script type="text/javascript" src="/js/jquery.contextmenu-fixed.js"></script>
<script type="text/javascript" src="/js/jquery.multiselect.js"></script>
<script type="text/javascript" src="/js/jquery.timepicker.js"></script>
<script type="text/javascript" src="/js/util.js"></script>
<script type="text/javascript" src="/js/socket.js"></script>
 
<style>
#navigation {
    width: auto;
    height: 50px;
    margin: 0;
    padding: 0;
    background: #436D8B;
} 
#navigation ul {
    list-style: none;
    margin: 0;
    padding: 0;
} 
#navigation ul li {
    display: inline;
    margin: 0px;
} 
#navigation ul li a {
    height:33px;
    display: block;
    float: left;
    padding: 17px 15px 0 15px;
    font: bold 12px Arial;
    color: #FFF;
    text-decoration: none;
    background: #436D8B;
} 

#navigation ul li a:hover {
    color:#134264;
    background: #B8AC87;
}

#navigation ul li#active a {
    color:#134264;
    background: #B8AC87;
}	

#navigation ul li .active {
    color:#134264;
    background: #B8AC87;
}   

</style>

<script>

$(document).ready(function(){
	
    $("#navigation li a").click(function() {
        $("#navigation li a").removeClass("active");
        $(this).addClass("active");
    });
	
});

</script>

</head>

<body>
    <div id="navigation">
        <ul style="float:left;">
            <li><a href="#">스케쥴 관리</a></li>
            <li><a href="/report/">보고서 작성</a></li>
            <li><a href="/product/formManage">양식관리</a></li>
            <li><a href="/config/">환경설정</a></li>
        </ul>
        <ul id="accoutDiv" style="float: right;">
            <li><a href="#"><%=userInfo.getAdmin().getLoginID() %>(<%=userInfo.getAdmin().getAdminName() %>)</a></li>
            <li><a href="/login/logout">로그아웃</a></li>
        </ul>  
    </div>    
