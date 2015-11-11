<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
    import = "java.util.List
        , com.mongodb.DBObject
        , com.cyberone.report.utils.StringUtil
        , com.cyberone.report.model.UserInfo"
%>

<%
List<DBObject> dbList = (List<DBObject>)request.getAttribute("dbList");
%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>PROM-통합보안관리시스템</title>

<link rel="shortcut icon" href="/img/icon/icon_prom.ico">

<link rel="stylesheet" type="text/css" href="/css/jquery-ui-1.10.2.custom.css"/>
<link rel="stylesheet" type="text/css" href="/css/login.css">
<link rel="stylesheet" type="text/css" href="/css/main.css">

<script type="text/javascript" src="/js/jquery-1.8.3.js"></script>
<script type="text/javascript" src="/js/jquery-ui-1.10.2.custom.js"></script>

<script type="text/javascript">


jQuery(document).ready(function(){
    
    $("#login_error").hide();
    
    $("#loginPw").keydown(function(e) {
        if (e.keyCode==13) {
            login();
            return false;
        }
    });
    
    $("#btn_login").click(function( event ) {
        login();
    });

    function login() {
        $("#login_error").hide();
        
        if ($("#loginId").val().length == 0) {
            $("#loginId").focus();
            return;
        }
        if ($("#loginPw").val().length == 0) {
            $("#loginPw").focus();
            return;
        }
        
        if(!sendRequest) {
            sendRequest = true;
            document.loginFrm.submit();
            $("#btn_login").attr("disabled", true);
        }
    }
    
    $("#loginId").focus();
});

</script>

<style>
.ui-tabs-active:focus{outline:none;}
.ui-tabs-anchor:focus{outline:none;}
.ui-button-icon-only:focus{outline:none;}
</style>

</head>
<body>
    <div id="loading2" class="login_wrap" style="display:none;">
        <div class="login_inner_wrap">
            <div style="height:260px;overflow:hidden">
                <div class="login">
                    <p class="loading_login">데이터를 불러오고 있습니다. 잠시만 기다려 주십시오.</p>
                </div>
            </div>
            <div id="copyright_loading1" class="footer"></div>
        </div>
    </div>
    
    <div id="loading1" class="login_wrap">
        <div class="login_inner_wrap">
            <div style="height:260px;overflow:hidden">
                <div class="login" id="login">
                    <form action="/login/verifyAccount" name="loginFrm" method="post">
                    <table>
                        <colgroup>
                            <col width="58px">
                            <col>
                            <col width="100px">
                        </colgroup>
                        <tr>
                            <th>접속서버</th>
                            <td colspan="2" style="padding: 4px 0 0 0;">
					            <select id="login_server" name="login_server">
					            <% for (DBObject obj : dbList) { %>
					                <option value="<%=obj.get("idx") %>"><%=StringUtil.convertString(obj.get("serverName")) %> </option>
					            <% } %>
					            </select>
                            </td>
                        </tr>
                        <tr>
                            <th>아이디</th>
                            <td><input type="text" class="login_text" id="loginId" name="loginId" tabindex=1 value="dykim"></td>
                            <td rowspan="2"><button class="login_button" id="btn_login" tabindex=3>로그인</button></td>
                        </tr>
                        <tr>
                            <th>비밀번호</th>
                            <td><input type="password" class="login_text" id="loginPw" name="loginPw" tabindex=2></td>
                        </tr>
                    </table>
                    </form>
                </div>
                <div class="wrap_login_error">
                    <div class="login_error" id="login_error">입력하신 아이디 또는 비밀번호가 일치하지 않습니다.</div>
                </div>
            </div>
            <div id="copyright_loading2" class="footer"></div>
        </div>
    </div>
     
    <script type="text/javascript">
        var currentYear = new Date().getFullYear();
        var copyText = "PROM ⓒ " + currentYear + " CYBERONE.Co.LTD All Right Reserved.";
        document.getElementById("copyright_loading1").innerHTML=copyText;
        document.getElementById("copyright_loading2").innerHTML=copyText;
    </script>
    
</body>

</html>
