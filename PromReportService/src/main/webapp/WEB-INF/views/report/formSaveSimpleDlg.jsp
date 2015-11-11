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

FORM_DLG = (function() {
	var _Dlg;
    return {
        init : function(Dlg, token) {
        	_Dlg = Dlg;

        	_Dlg.dialog({
                autoOpen: false,
                resizable: false,
                width: 600,
                height: 213,
                modal: true,
                title: "새 양식저장",
                buttons: {
                    "저장": function() {
                        $.ajax({
                            url : "/product/formSave",
                            type : 'POST',
                            data : {
                                "formType": token.formType, 
                                "reportType": token.reportType,
                                "assetCode": token.assetCode,
                                "formCode": 0, 
                                "data": token.data,
                                "name": $("#formName").val(),
                                "desc": $("#formDesc").val()
                            },
                            dataType: 'json',
                            success : function(data){
                                if (data.status == 'success') {
                                    _Dlg.dialog("close");
                                } else {
                                    promAlert(1, data.message);
                                }
                            }
                        });
                    },
                    "닫기": function() {
                    	_Dlg.dialog("close");
                    }
                },
                open: function () {
                    $('.ui-dialog-buttonpane').find('button:contains("저장")').button({icons: { primary: 'ui-icon-check' }});
                    $('.ui-dialog-buttonpane').find('button:contains("닫기")').button({icons: { primary: 'ui-icon-closethick' }});
                },
                close: function( event, ui ) {
                	_Dlg.parent().remove();
                	_Dlg.children().remove();
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
            