<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
    import = "java.util.HashMap
            , com.cyberone.report.utils.StringUtil"
%>

<%


%>
<script type="text/javascript">

APPLY_FORM_DLG = (function() {
	var _Dlg, _slctRow;
	
    return {
    	done : function() {
    		stop();
    		var formList = [];
            $.each($("#apply_form_grid").find('input:checked'), function(idx, pack) {
                var tRow = $("#apply_form_grid").jqGrid("getLocalRow", parseInt($(pack).closest('tr')[0].id));
                formList.push({"formCode": tRow.id});
            });
            return {"assetCode": _slctRow.id, "formList": JSON.stringify(formList)};    		
    	},
        init : function(Dlg, slctRow) {
        	_Dlg = Dlg;
        	_slctRow = slctRow;
        	
            $("#apply_form_grid").jqGrid({
                datatype: "jsonstring",
                colNames:["Id", "Level", "Code", "양식명", "양식설명", "통계구분", "보고서타입", "양식타입"],
                colModel:[
                    {name:'id',          index:'id',          width:0, hidden:true, key:true, title:false},
                    {name:'level',       index:'level',       width:0, hidden:true, title:false},
                    {name:'code',        index:'code',        width:0, hidden:true, title:false},
                    {name:'name',        index:'name',        width:350,sortable:false, title:false,
                    	formatter: function (cellvalue, options, rowObject) {
                    		if (rowObject.level == 2) {
                    		    return "&nbsp;<input type='radio' name='"+rowObject.reportType+"' class='itmchk'>&nbsp;" + $.jgrid.htmlEncode(cellvalue);
                    		} else {
                    			return cellvalue;
                    		}
                        }
                    },
                    {name:'desc',        index:'desc',        width:350,sortable:false},
                    {name:'prefix',      index:'prefix',      width:0, hidden:true, title:false},
                    {name:'reportType',  index:'reportType',  width:0, hidden:true, title:false},
                    {name:'formType',    index:'formType',    width:0, hidden:true, title:false}
                ],
                rowNum: 1000,
                scrollrows: true,
                treeGrid: true,
                treeGridModel: 'adjacency',
                treedatatype: "local",
                ExpandColumn: 'name',
                autowidth: true,
                height: '468',
                treeIcons: {
                    plus:'folder',
                    minus:'folderOpen',
                    leaf:'leaf_rule'
                },    
                loadComplete: function () {
                    console.log("apply_form_grid loadComplete...");
                },
                gridComplete: function () {
                    console.log("apply_form_grid gridComplete...");
                    $($("#apply_form_grid")[0].rows[1]).find("div.ui-icon").removeClass("folderOpen").addClass("leaf_product");
                },
                beforeSelectRow: function (rowid, e) {
                	var $this = $(this),
                    clearChecked = function (children) {
                        $.each(children, function () {
                            $("#" + this.id, $this).removeClass("apply");
                        });
                    };
                	if ($("#"+ rowid, this).find('input')[0].type == 'radio') {
                        clearChecked($this.jqGrid("getNodeChildren", $this.jqGrid("getNodeParent", $this.jqGrid("getLocalRow", rowid))));
                        $("#"+ rowid, this).find('input[type=radio]').prop("checked", true);
                        $("#"+ rowid, this).addClass("apply");
                	}
                }
            });
        	
            $.ajax({
                url : "/product/formList",
                type : 'POST',
                data : {"formType": getFomrType(_slctRow.prefix.toLowerCase()), "prefix": _slctRow.prefix},
                dataType: 'json',
                success : function(data){
                    if (data.status == 'success') {
                        $("#apply_form_grid").jqGrid('clearGridData');
                        $("#apply_form_grid")[0].addJSONData({
                            total: 1,
                            page: 1,
                            records: data.rows.length,
                            rows: data.rows
                        });
                        
                        $.ajax({
                            url : "/product/formInAsset",
                            type : 'POST',
                            data : {"assetCode": _slctRow.id},
                            dataType: 'json',
                            success : function(data){
                                if (data.status == 'success') {
                                    $.each(data.applyList, function(idx, pack) {
                                        $("#"+ pack.formCode, $("#apply_form_grid")).find('input[type=radio]').prop("checked", true);
                                        $("#"+ pack.formCode, $("#apply_form_grid")).addClass("apply");
                                    });
                                    
                                    _Dlg.dialog({
                                        autoOpen: false,
                                        resizable: false,
                                        width: 750, 
                                        height: 600, 
                                        modal: true,
                                        title: "양식지정",
                                        buttons: {
                                            "저장": function() {
                                                $.ajax({
                                                    url : "/product/applyFormSave",
                                                    type : 'POST',
                                                    data : APPLY_FORM_DLG.done(),
                                                    dataType: 'json',
                                                    success : function(data){
                                                    	console.log(data);
                                                    }
                                                });
                                            	
                                            	
                                            	
                                            	
                                            },
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
                                    
                                }
                            }
                        });
                        
                    } else {
                    	promAlert(3, data.message);
                    	return;
                    }
                    
                }
            });
                           
        }
    };
})();

$(document).ready(function () {
}); 

</script>

<div class="dialog_Body" style="height: 97%;">
    <div class="floatL"><table id="apply_form_grid"></table></div>
</div>
            
</form>
            