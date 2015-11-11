<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
    import = "java.util.HashMap
            , com.cyberone.report.utils.StringUtil"
%>

<%@ include file="../include/header.jsp"%>

<%
HashMap<String, Object> configInfo = (HashMap<String, Object>)request.getAttribute("configInfo");
HashMap<String, Object> serviceDeskInfo = (HashMap<String, Object>)configInfo.get("serviceDeskInfo");
if (StringUtil.isEmpty(serviceDeskInfo)) {
	serviceDeskInfo = new HashMap<String, Object>();
}

%>

<script>

$(window).bind('resize', function() {
    $("#cfgServerGrid").setGridWidth($(".report_body").width());
}).trigger('resize');

$(document).ready(function(){
	
    $("#cfgServerGrid").jqGrid({
        url:  "/config/serverList",
        datatype: "json",
        colNames: ['ID', '서버명', 'PROM DB IP', 'PROM DB Port', 'PROM DB 명', 'PROM DB 계정', 'Report DB IP', 'Report DB Port', 'Report DB 명', 'Report DB 계정'],
        colModel: [
            {name: 'idx',        index: 'idx',         width:0,   hidden:true, title:false},
            {name: 'serverName', index: 'serverName',  width: 100, sortable:false, align: 'center', title:false},
            {name: 'promIp',     index: 'promIp',      width: 100, sortable:false, align: 'center', title:false},
            {name: 'promPort',   index: 'promPort',    width: 100, sortable:false, align: 'center', title:false},
            {name: 'promName',   index: 'promName',    width: 100, sortable:false, align: 'center', title:false},
            {name: 'promAccount',index: 'promAccount', width: 100, sortable:false, align: 'center', title:false},
            {name: 'reportIp',     index: 'reportIp',      width: 100, sortable:false, align: 'center', title:false},
            {name: 'reportPort',   index: 'reportPort',    width: 100, sortable:false, align: 'center', title:false},
            {name: 'reportName',   index: 'reportName',    width: 100, sortable:false, align: 'center', title:false},
            {name: 'reportAccount',index: 'reportAccount', width: 100, sortable:false, align: 'center', title:false}
        ],
        autowidth: true,
        jsonReader : {
            repeatitems:false
        },
        gridComplete: function(){
        	initContextMenu();
        },
        loadComplete: function() {
        },
        ondblClickRow: function(rowid, iRow, iCol, e) {
        	var slctRow = $("#cfgServerGrid").jqGrid("getRowData", rowid);
            DIALOG.Open().load("/config/serverRegistry", {idx: slctRow.idx}, function() {
                CONFIG_DLG.init($(this));
            });
        }
    });

    function initContextMenu() {    
        jQuery(".jqgrow", "#cfgServerGrid").contextMenu('context', {
            bindings: {
                'serverDel': function(t) {
                	serverDel();
                }
            },
            onContextMenu : function(e, menu) {
               var rowId = $(e.target).parents("tr").attr("id")
               $("#cfgServerGrid").setSelection(rowId);        
               return true;                                    
            },
            onShowMenu: function(e, menu) {
                return menu;
            }        
        });    
    } 

    function serverDel() {
        
        DIALOG.Open().load("/main/promConfirm", function() {
            var Dlg = $(this);
            
            PROM_CONFIRM.init("선택한 서버를 삭제하시겠습니까?");
            
            Dlg.dialog({
                autoOpen: false,
                resizable: false,
                width: 380,
                height: 140,
                modal: true,
                title: "알림",
                buttons: {
                    "확인": function() {
                        Dlg.dialog( "close" );
                        var slctRow = $("#cfgServerGrid").jqGrid("getRowData", $("#cfgServerGrid").getGridParam("selrow")); 
                        $.ajax({
                            url : "/config/delServer",
                            type : 'POST',
                            data : slctRow,
                            dataType: 'json',
                            success : function(data){
                                if (data.status == 'success') {
                                	$("#cfgServerGrid").jqGrid().trigger("reloadGrid");
                                } else {
                                }
                            }
                        });
                    },
                    "취소": function() {
                        Dlg.dialog("close");
                    }
                },
                open: function () {
                    $('.ui-dialog-buttonpane').
                    find('button:contains("확인")').button({
                        icons: { primary: 'ui-icon-check' }
                    });
                    $('.ui-dialog-buttonpane').
                    find('button:contains("취소")').button({
                        icons: { primary: 'ui-icon-closethick' }
                    });
                },
                close: function( event, ui ) {
                    Dlg.children().remove();
                }
            });
            Dlg.dialog("open");    
            Dlg.dialog('option', 'position', 'center');                
        });
    }
    
    $("#cfgServiceBtn").button({icons: {primary: "ui-icon-refresh"}}).click(function( event ) {
    	
        var data = {};
        $("#frm").serializeArray().map(function(x){data[x.name] = x.value;});     

        $.ajax({
            url : "/config/saveOpt",
            type : 'POST',
            data : data,
            dataType: 'json',
            success : function(data){
                if (data.status == 'success') {
                } else {
                }
            }
        });
    	
    	
    });
    
    $("#cfgServerBtn").button({icons: {primary: "ui-icon-refresh"},}).click(function( event ) {
        DIALOG.Open().load("/config/serverRegistry", {}, function() {
            CONFIG_DLG.init($(this));
        });
    });
    
});

</script>

<div class="reportWrap" style="background: #676767;padding: 0px;height: 100%;width: 100%;">
    <div class="report_List" style="float: left;width: 500px;height:662px;border: 1px solid #92a0ac;padding:10px;">

        <form id="frm" style="font-weight: bold;">
        <div class="marginT10">
            <h2><input type="checkbox" id="cfgSvcUse" name="cfgSvcUse" <%if ((Integer)configInfo.get("serviceDeskUse") > 0) { %>checked<% } %>>&nbsp;<lable for="cfgSvcUse">서비스데스크 보고서 출력</lable></h2>
            <div class="wrap_tbl">
                <table class="tbl_dialog">
                    <colgroup>
                        <col width="160px">
                        <col width="*">
                    </colgroup>
                    <tbody>
                    <tr id="control01_chartInfo_searchRange_topN">
                        <th>서비스데스크 DB IP</th>
                        <td>
                            <input class="important" id="cfgSvcIp" name="cfgSvcIp" type="text" value="<%=StringUtil.convertString(serviceDeskInfo.get("host")) %>" style="width:150px;vertical-align: top;height: 16px;"/>
                        </td>
                    </tr>
                    <tr id="control01_chartInfo_searchRange_progress">
                        <th>서비스데스크 DB Port</th>
                        <td>
                            <input class="important" id="cfgSvcPort" name="cfgSvcPort" type="text" value="<%=StringUtil.convertString(serviceDeskInfo.get("port")) %>" style="width:150px;vertical-align: top;height: 16px;"/>
                        </td>
                    </tr>
                    <tr id="control01_chartInfo_markingType_progress">
                        <th>서비스데스크 DB 명</th>
                        <td>
                            <input class="important" id="cfgSvcName" name="cfgSvcName" type="text" value="<%=StringUtil.convertString(serviceDeskInfo.get("dbname")) %>" style="width:150px;vertical-align: top;height: 16px;"/>
                        </td>
                    </tr>
                    <tr id="control01_chartInfo_reloadTime_topN">
                        <th>서비스데스크 DB 계정</th>
                        <td>
                            <input class="important" id="cfgSvcAccount" name="cfgSvcAccount" type="text" value="<%=StringUtil.convertString(serviceDeskInfo.get("account")) %>" style="width:150px;vertical-align: top;height: 16px;"/>
                        </td>
                    </tr>
                    <tr id="control01_chartInfo_chartTypeView">
                        <th>서비스데스크 DB 비밀번호</th>
                        <td>
                            <input class="important" id="cfgSvcPassword" name="cfgSvcPassword" type="password" value="<%=StringUtil.convertString(serviceDeskInfo.get("password")) %>" style="width:150px;vertical-align: top;height: 16px;"/>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>   
            </br>
            <h2 class="marginT10">기타 보고서 설정</h2>
            <div class="wrap_tbl" style="height:100px;"></div>
        </div>
        </form>        

        <table class="tbl_button marginT5">
            <tr>
                <td class="alignL">
                </td>
                <td class="alignR">
                    <button id="cfgServiceBtn">설정 저장</button>
                </td>
            </tr>
        </table>

    </div>

    <div class="report_body" style="overflow-y: auto;overflow-y: auto;height: 662px;background-color: #FFF;padding: 10px;border: 1px solid #92a0ac;">
        <table id="cfgServerGrid"></table>
	    <table class="tbl_button marginT5">
	        <tr>
	            <td class="alignL">
	            </td>
	            <td class="alignR">
	                <button id="cfgServerBtn">서버 등록</button>
	            </td>
	        </tr>
	    </table>
        
    </div>
</div>







</br></br>

<input type="button" id="btn_report" value="보고서 작성">

<%@ include file="../include/footer.jsp"%>

<div class="contextMenu" id="context" style="display:none">
    <ul style="width: 100px;">
        <li id="serverDel">
            <span class="ui-icon ui-icon-trash" style="float:left"></span>
            <span style="font-size:12px; font-family:맑은 고딕">서버삭제</span>
        </li>
    </ul>
</div>
