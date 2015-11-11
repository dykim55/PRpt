<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
    import = "java.util.HashMap
            , com.cyberone.report.utils.StringUtil"
%>

<%
HashMap<String, Object> svrInfo = (HashMap<String, Object>)request.getAttribute("svrInfo");
if (svrInfo == null) {
	svrInfo = new HashMap<String, Object>();
}

%>
<script type="text/javascript">

    CONFIG_DLG = (function() {
    	var _Dlg;
        return {
            init : function(Dlg) {
                _Dlg = Dlg;
                _Dlg.dialog({
                    autoOpen: false,
                    resizable: false,
                    width: 674,
                    modal: true,
                    title: "서버 등록",
                    buttons: {
                        "저장": function() {

                            var data = {};
                            $("#frmDlg").serializeArray().map(function(x){data[x.name] = x.value;});     
                        	
                            $.ajax({
                                url : "/config/saveServer",
                                type : 'POST',
                                data : data,
                                dataType: 'json',
                                success : function(data){
                                    if (data.status == 'success') {
                                    	$("#cfgServerGrid").jqGrid().trigger("reloadGrid");
                                        _Dlg.dialog("close");
                                    } else {
                                    }
                                }
                            });
                        	
                        },
                        "취소": function() {
                        	_Dlg.dialog("close");
                        }
                    },
                    close: function( event, ui ) {
                        _Dlg.parent().remove();
                    },
                    open: function( event, ui ) {
                        $('.ui-dialog-buttonpane').find('button:contains("저장")').button({icons: { primary: 'ui-icon-check' }});
                        $('.ui-dialog-buttonpane').find('button:contains("취소")').button({icons: { primary: 'ui-icon-closethick' }});
                    }            
                });
                _Dlg.dialog("open");    
                _Dlg.dialog('option', 'position', 'center');            	
            }
        };
    })();

    $(document).ready(function () {
    }); 

</script>

<form id="frmDlg" style="font-weight: bold;">

<div class="report_List" style="float: none;width: 627px;border: 1px solid #92a0ac;padding:10px;">
    <table class="tbl_dialog" style="margin-left:5px;">
        <colgroup>
            <col width="120px">
            <col width="206px">
            <col width="120px">
            <col width="*">
        </colgroup>
        <tbody>
        <tr id="control01_chartInfo_searchRange_topN">
            <th>서버명</th>
            <td>
                <input class="important" id="cfgSvrName" name="cfgSvrName" type="text" value="<%=StringUtil.convertString(svrInfo.get("serverName")) %>" style="width:150px;vertical-align: top;height: 16px;"/>
            <% if (!StringUtil.isEmpty(svrInfo.get("idx"))) { %>
                <input type="hidden" id="cfgSvrIdx" name="cfgSvrIdx" value="<%=svrInfo.get("idx") %>">
            <% } %>
            </td>
            <th>도메인코드</th>
            <td>
                <input class="important" id="cfgSvrDomain" name="cfgSvrDomain" type="text" value="<%=StringUtil.convertString(svrInfo.get("domainCode")) %>" style="width:60px;vertical-align: top;height: 16px;"/>
            </td>
        </tr>
        </tbody>
    </table>
</div>
<div class="report_List marginT5" style="float: left;width: 300px;border: 1px solid #92a0ac;padding:10px;">

    <div class="wrap_tbl">
        <table class="tbl_dialog">
            <colgroup>
                <col width="120px">
                <col width="*">
            </colgroup>
            <tbody>
            <tr id="control01_chartInfo_searchRange_topN">
                <th>PROM DB IP</th>
                <td>
                    <input class="important" id="cfgPromIp" name="cfgPromIp" type="text" value="<%=StringUtil.convertString(svrInfo.get("promIp")) %>" style="width:150px;vertical-align: top;height: 16px;"/>
                </td>
            </tr>
            <tr id="control01_chartInfo_searchRange_progress">
                <th>PROM DB Port</th>
                <td>
                    <input class="important" id="cfgPromPort" name="cfgPromPort" type="text" value="<%=StringUtil.convertString(svrInfo.get("promPort")) %>" style="width:150px;vertical-align: top;height: 16px;"/>
                </td>
            </tr>
            <tr id="control01_chartInfo_markingType_progress">
                <th>PROM DB 명</th>
                <td>
                    <input class="important" id="cfgPromName" name="cfgPromName" type="text" value="<%=StringUtil.convertString(svrInfo.get("promName")) %>" style="width:150px;vertical-align: top;height: 16px;"/>
                </td>
            </tr>
            <tr id="control01_chartInfo_reloadTime_topN">
                <th>PROM DB 계정</th>
                <td>
                    <input class="important" id="cfgPromAccount" name="cfgPromAccount" type="text" value="<%=StringUtil.convertString(svrInfo.get("promAccount")) %>" style="width:150px;vertical-align: top;height: 16px;"/>
                </td>
            </tr>
            <tr id="control01_chartInfo_chartTypeView">
                <th>PROM DB 비밀번호</th>
                <td>
                    <input class="important" id="cfgPromPassword" name="cfgPromPassword" type="password" value="<%=StringUtil.convertString(svrInfo.get("promPassword")) %>" style="width:150px;vertical-align: top;height: 16px;"/>
                </td>
            </tr>
            </tbody>
        </table>
    </div>   
</div>
            
<div class="report_List marginT5" style="float: right;width: 300px;border: 1px solid #92a0ac;padding:10px;">

    <div class="wrap_tbl">
        <table class="tbl_dialog">
            <colgroup>
                <col width="120px">
                <col width="*">
            </colgroup>
            <tbody>
            <tr id="control01_chartInfo_searchRange_topN">
                <th>Report DB IP</th>
                <td>
                    <input class="important" id="cfgReportIp" name="cfgReportIp" type="text" value="<%=StringUtil.convertString(svrInfo.get("reportIp")) %>" style="width:150px;vertical-align: top;height: 16px;"/>
                </td>
            </tr>
            <tr id="control01_chartInfo_searchRange_progress">
                <th>Report DB Port</th>
                <td>
                    <input class="important" id="cfgReportPort" name="cfgReportPort" type="text" value="<%=StringUtil.convertString(svrInfo.get("reportPort")) %>" style="width:150px;vertical-align: top;height: 16px;"/>
                </td>
            </tr>
            <tr id="control01_chartInfo_markingType_progress">
                <th>Report DB 명</th>
                <td>
                    <input class="important" id="cfgReportName" name="cfgReportName" type="text" value="<%=StringUtil.convertString(svrInfo.get("reportName")) %>" style="width:150px;vertical-align: top;height: 16px;"/>
                </td>
            </tr>
            <tr id="control01_chartInfo_reloadTime_topN">
                <th>Report DB 계정</th>
                <td>
                    <input class="important" id="cfgReportAccount" name="cfgReportAccount" type="text" value="<%=StringUtil.convertString(svrInfo.get("reportAccount")) %>" style="width:150px;vertical-align: top;height: 16px;"/>
                </td>
            </tr>
            <tr id="control01_chartInfo_chartTypeView">
                <th>Report DB 비밀번호</th>
                <td>
                    <input class="important" id="cfgReportPassword" name="cfgReportPassword" type="password" value="<%=StringUtil.convertString(svrInfo.get("reportPassword")) %>" style="width:150px;vertical-align: top;height: 16px;"/>
                </td>
            </tr>
            </tbody>
        </table>
    </div>   
</div>
            
</form>
            