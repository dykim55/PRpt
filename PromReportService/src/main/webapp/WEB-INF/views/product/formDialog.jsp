<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
    import = "java.util.HashMap
            , com.cyberone.report.utils.StringUtil"
%>

<%
String sGubun = request.getParameter("gubun");
%>
<script type="text/javascript">
FORM_DLG = (function() {
	var _Dlg;
    return {
        init : function(Dlg, slctRow) {
        	
        	_Dlg = Dlg;
        	
        	$("#formName").val(slctRow.level==2 ? slctRow.name : "");
        	$("#formDesc").val(slctRow.level==2 ? slctRow.desc : "");
        	
        	$(".wrap_tbl").load("/product/formUI?option=" + slctRow.reportType + "_" + slctRow.prefix.toLowerCase() + (slctRow.prefix.toLowerCase()=="ddos" && slctRow.formType == 2 ? "_dp" : "") , function() {

        		$.ajax({
                    url : "/product/formInfo",
                    type : 'POST',
                    data : {"prefix":slctRow.prefix.toLowerCase(), "reportType":slctRow.reportType, "formCode":slctRow.level==2 ? slctRow.code : 0},
                    dataType: 'json',
                    success : function(data){
                        if (data.status == 'success') {
                        	FORM_OPTION.init({ "data": data.formMap, "etc": undefined });
                        	
                            _Dlg.dialog({
                                autoOpen: false,
                                resizable: false,
                                width: 674,
                                width: 600,
                                modal: true,
                                title: FORM_OPTION.title(),
                                buttons: {
                            <% if (sGubun.equals("add")) { %>
                                    "저장": function() {
                                        var chkData = FORM_OPTION.done();
                                        $.ajax({
                                            url : "/product/formSave",
                                            type : 'POST',
                                            data : {
                                                "formType":slctRow.formType, 
                                                "prefix":slctRow.prefix.toLowerCase(),
                                                "reportType":slctRow.reportType, 
                                                "formCode":slctRow.level==2 ? slctRow.code : 0, 
                                                "data":JSON.stringify(chkData.data),
                                                "name":$("#formName").val(),
                                                "desc":$("#formDesc").val()
                                            },
                                            dataType: 'json',
                                            success : function(data){
                                                if (data.status == 'success') {
                                                    $("#form_grid").jqGrid('clearGridData');
                                                    $("#form_grid")[0].addJSONData({
                                                        total: 1,
                                                        page: 1,
                                                        records: data.rows.length,
                                                        rows: data.rows
                                                    });
                                                    _Dlg.dialog("close");
                                                } else {
                                                    promAlert(3, data.message);
                                                }
                                            }
                                        });
                                    	
                                    },
                            <% } else { %>
                                    "저장": function() {
                                        DIALOG.Open().load("/main/promConfirm", function() {
                                            var Dlg = $(this);
                                            
                                            PROM_CONFIRM.init("양식을 수정하면 해당 양식이 적용된 모든 자산에 적용이 됩니다.</br></br>수정하시겠습니까?");
                                            
                                            Dlg.dialog({
                                                autoOpen: false,
                                                resizable: false,
                                                width: 380,
                                                height: 180,
                                                modal: true,
                                                title: "알림",
                                                buttons: {
                                                    "확인": function() {
                                                        Dlg.dialog( "close" );
                                                        var chkData = FORM_OPTION.done();
                                                        $.ajax({
                                                            url : "/product/formSave",
                                                            type : 'POST',
                                                            data : {
                                                                "formType":slctRow.formType, 
                                                                "prefix":slctRow.prefix.toLowerCase(),
                                                                "reportType":slctRow.reportType, 
                                                                "formCode":slctRow.level==2 ? slctRow.code : 0, 
                                                                "data":JSON.stringify(chkData.data),
                                                                "name":$("#formName").val(),
                                                                "desc":$("#formDesc").val()
                                                            },
                                                            dataType: 'json',
                                                            success : function(data){
                                                                if (data.status == 'success') {
                                                                    $("#form_grid").jqGrid('clearGridData');
                                                                    $("#form_grid")[0].addJSONData({
                                                                        total: 1,
                                                                        page: 1,
                                                                        records: data.rows.length,
                                                                        rows: data.rows
                                                                    });
                                                                    _Dlg.dialog("close");
                                                                } else {
                                                                    alert(data.message);
                                                                }
                                                            }
                                                        });
                                                    },
                                                    "취소": function() {
                                                        Dlg.dialog("close");
                                                    }
                                                },
                                                open: function () {
                                                    $('.ui-dialog-buttonpane').find('button:contains("확인")').button({icons: { primary: 'ui-icon-check' }});
                                                    $('.ui-dialog-buttonpane').find('button:contains("취소")').button({icons: { primary: 'ui-icon-closethick' }});
                                                },
                                                close: function( event, ui ) {
                                                    Dlg.parent().remove();
                                                    Dlg.children().remove();
                                                }
                                            });
                                            Dlg.dialog("open");    
                                            Dlg.dialog('option', 'position', 'center');                
                                        });
                                    },
                            <% } %>
                                    "취소": function() {
                                        _Dlg.dialog("close");
                                    }
                                },
                                close: function( event, ui ) {
                                    _Dlg.parent().remove();
                                    _Dlg.children().remove();
                                },
                                open: function( event, ui ) {
                                    $('.ui-dialog-buttonpane').find('button:contains("저장")').button({icons: { primary: 'ui-icon-check' }});
                                    $('.ui-dialog-buttonpane').find('button:contains("취소")').button({icons: { primary: 'ui-icon-closethick' }});
                                }            
                            });
                            _Dlg.dialog("open");    
                            _Dlg.dialog('option', 'position', 'center');                
                        	
                        } else {
                        }
                    }
                });
        		
        	});
        }
    };
})();

$(document).ready(function () {
}); 

</script>

<form id="frmDlg" style="font-weight: bold;">

<div style="width: 558px;border: 1px solid #92a0ac;padding:10px;background: white;">
    <table class="tbl_dialog">
        <colgroup>
            <col width="120px">
            <col width="*">
        </colgroup>
        <tbody>
        <tr id="control01_chartInfo_searchRange_topN">
            <th>양식명</th>
            <td>
                <input class="important" id="formName" name="fromName" type="text" style="width:405px;vertical-align: top;height: 16px;"/>
            </td>
        </tr>
        <tr id="control01_chartInfo_searchRange_topN">
            <th>양식설명</th>
            <td>
                <textarea class="textbox" id="formDesc" name="formDesc" style="width:405px;height:50px;"></textarea>
            </td>
        </tr>
        </tbody>
    </table>
</div>
<div class="marginT5" style="width:558px;height:600px;border:1px solid #92a0ac;padding:10px;background: white;overflow-y:auto;">

    <div class="wrap_tbl">
    </div>   

</div>
            
</form>
            