<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
    import = "java.util.List
        , com.mongodb.DBObject
        , com.cyberone.report.utils.StringUtil
        , com.cyberone.report.model.UserInfo"
%>

<%

List<DBObject> productGroups = (List<DBObject>)request.getAttribute("productGroups");

%>


<%@ include file="../include/header.jsp"%>

<style>

.panel { margin-bottom:10px; width:90%; }
.mb10 { margin-bottom:10px; }
.labelHover { color: #ff0000; }

.dialog_tabBody h1 {
  float: left;
  width: 100%;
  margin-top: -2px;
  margin-bottom: 5px;
  font-size: 11pt;
  font-weight: large;
}

.dialog_tabBody h2 {
  float: left;
  width: 100%;
  margin-bottom: 3px;
  margin-top: 10px;
  padding-left: 10px;
  background: url(/img/bullets.png) no-repeat 0 -397px;
  font-size: 9pt;
  font-weight: large;
  -webkit-box-sizing: border-box;
  -moz-box-sizing: border-box;
  box-sizing: border-box;
}

</style>

<script>

$(window).bind('resize', function() {
    $("#form_grid").setGridWidth($("#optionDiv").width());
}).trigger('resize');

$(document).ready(function(){

    $("#product_grid").jqGrid({
    	url: "/product/formTypeList",
        datatype: "json",
        colNames:["Id", "Level", "자동화보고서제품군", "제품코드", ""/*, '버전', '연동일자', '제조사', '정책설정', '설명'*/],
        colModel:[
            {name:'id',            index:'id',            width:0, hidden:true, key:true},
            {name:'level',         index:'level',         width:0, hidden:true},
            {name:'name',          index:'name',          width:450, align:'left', sortable: false},
            {name:'code',          index:'code',          width:0, hidden:true},
            {name:'prefix',        index:'prefix',        width:0, hidden:true},
            /*{name:'version',       index:'version',       width:100, align:'left', sortable: false, title:false},
            {name:'createTime',    index:'createTime',    width:150, align:'left', sortable: false, title:false},
            {name:'vendor',        index:'vendor',        width:200, align:'left', sortable: false, title:false},
            {name:'productPolicy', index:'productPolicy', width:100, align:'center', sortable: false, title:false,
                formatter: function (cellval, options, rowObject) {
                    return cellval > 0 ? "연결됨" : "";
                }
            },
            {name:'memo',          index:'memo',          width:0, align:'left', hidden:true}*/
        ],
        rowNum: 1000,
        treeGrid: true,
        treeGridModel: 'adjacency',
        ExpandColumn: 'name',
        width: '500',
        height: '600',
        treeIcons: {
            plus:'folder',
            minus:'folderOpen',
            leaf:'leaf_product'
        },         
        jsonReader: {
            repeatitems: false,
            root: "rows"
        },
        loadComplete: function () {
            console.log("product_grid loadComplete...");
        },
        gridComplete: function () {
        	console.log("product_grid gridComplete...");
        },
        beforeSelectRow: function (rowid, e) {
        	var slctRow = $("#product_grid").jqGrid("getRowData", rowid);
        	if (slctRow.level != 1) return;
        	$("#product_grid").setSelection(rowid);
        },
        onSelectRow: function(id) {
        	var slctRow = $("#product_grid").jqGrid("getRowData", $("#product_grid").getGridParam("selrow"));
        	
            $.ajax({
                url : "/product/formList",
                type : 'POST',
                data : {"formType": slctRow.code, "prefix": slctRow.prefix},
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
                    } else {
                    }
                }
            });
        	
        }
        
    });
    
    // 양식 그리드
    $("#form_grid").jqGrid({
        datatype: "jsonstring",
        colNames:["Id", "Level", "Code", "양식명", "적용자산", "양식설명", "수정자", "수정일", "등록자", "등록일", "통계구분", "보고서타입", "양식타입"],
        colModel:[
            {name:'id',          index:'id',          width:0, hidden:true, key:true, title:false},
            {name:'level',       index:'level',       width:0, hidden:true, title:false},
            {name:'code',        index:'code',        width:0, hidden:true, title:false},
            {name:'name',        index:'name',        width:200,sortable:false, title:false},
            {name:'applyCnt',    index:'applyCnt',    width:50, align:'center', sortable:false, title:false},
            {name:'desc',        index:'desc',        width:200,sortable:false},
            {name:'modifyId',    index:'modifyId',    width:50, align:'center', sortable:false, title:false},
            {name:'modifyTime',  index:'modifyTime',  width:100,align:'center', sortable:false, title:false},
            {name:'createId',    index:'createId',    width:50, align:'center', sortable:false, title:false},
            {name:'createTime',  index:'createTime',  width:100,align:'center', sortable:false, title:false},
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
        height: '600',
        treeIcons: {
            plus:'folder',
            minus:'folderOpen',
            leaf:'leaf_rule'
        },    
        loadComplete: function () {
            console.log("form_grid loadComplete...");
        },
        gridComplete: function () {
        	console.log("form_grid gridComplete...");
            initContextMenu();
            $($("#form_grid")[0].rows[1]).find("div.ui-icon").removeClass("folderOpen").addClass("leaf_product");
        }
    });
    
    function initContextMenu() {    
        jQuery(".jqgrow", "#form_grid").contextMenu('context', {
            bindings: {
                'applyAssetGroup': function(t) {
                    applyAssetGroup();
                },
                'applyAsset': function(t) {
                	applyAsset();
                },
                'formAdd': function(t) {
                	formAdd();
                },
                'formEdit': function(t) {
                	formEdit();
                },
                'formDel': function(t) {
                	formDel();
                }
            },
            onContextMenu : function(e, menu) {
               var rowId = $(e.target).parents("tr").attr("id")
               $("#form_grid").setSelection(rowId);        
               
               var slctRow = $("#form_grid").jqGrid("getRowData", rowId);
               if (slctRow.level == 0) {
            	   return false
               }
               return true;                                    
            },
            onShowMenu: function(e, menu) {
                var rowId = $(e.target).parents("tr").attr("id")
                var slctRow = $("#form_grid").jqGrid("getRowData", rowId);
                console.log(slctRow);
                if (slctRow.level == 1) {
                	$('#applyAssetGroup', menu).remove();
                    $('#applyAsset', menu).remove();
                    $('#formEdit', menu).remove();
                    $('#formDel', menu).remove();
                } else if (slctRow.level == 2) {
                    $('#formAdd', menu).remove();
                    if (9 == parseInt(slctRow.formType)) {
                    	$('#applyAsset', menu).remove();
                    } else {
                    	$('#applyAssetGroup', menu).remove();
                    }
                }
                return menu;
            }        
        });             
    } 

    function applyAssetGroup() {
        var slctRow = $("#form_grid").jqGrid("getRowData", $("#form_grid").getGridParam("selrow"));
        DIALOG.Open().load("/product/applyGroupDialog", function() {
            APPLY_DLG.init($(this), slctRow);
        });
    }
    
    function applyAsset() {
        var slctRow = $("#form_grid").jqGrid("getRowData", $("#form_grid").getGridParam("selrow"));
        DIALOG.Open().load("/product/applyAssetDialog", function() {
        	APPLY_DLG.init($(this), slctRow);
        });
    }
    
    function formAdd() {
        var slctRow = $("#form_grid").jqGrid("getRowData", $("#form_grid").getGridParam("selrow"));
    	DIALOG.Open().load("/product/formDialog", {"gubun": "add"}, function() {
    		FORM_DLG.init($(this), slctRow);
    	});
    }
    
    function formEdit() {
        var slctRow = $("#form_grid").jqGrid("getRowData", $("#form_grid").getGridParam("selrow"));
        DIALOG.Open().load("/product/formDialog", {"gubun": "edit"}, function() {
            FORM_DLG.init($(this), slctRow);
        });
    }

    function formDel() {

        DIALOG.Open().load("/main/promConfirm", function() {
            var Dlg = $(this);
            
            PROM_CONFIRM.init("선택한 양식을 삭제하시겠습니까?");
            
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
                        var slctRow = $("#form_grid").jqGrid("getRowData", $("#form_grid").getGridParam("selrow")); 
                        $.ajax({
                            url : "/product/formDelete",
                            type : 'POST',
                            data : slctRow,
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

    }

    $("#prdt_group").change(function() {
        var productGroupCode = parseInt($("#prdt_group option:selected").val());
        if (isNaN(productGroupCode)) {
            productGroupCode = 0;
        }
        
        $.ajax({
            url : "/product/products",
            type : 'POST',
            data : {"productGroupCode":productGroupCode},
            dataType: 'json',
            success : function(data){
                if (data.status == 'success') {
                    for (i=0;i<data.products.length;i++) {
                    	data.products[i].productName = data.products[i].productName + '_Ver_' + data.products[i].productVersion; 
                    }
                    $("#prdt_name").loadSelect(data.products, "productName", "productCode", "선택하세요");
                }
            }
        });
        
        $("#product_grid").jqGrid('setGridParam', {
            postData:{
                productGroupCode: productGroupCode,
                productCode: 0
            }
        }).trigger("reloadGrid");
    });
   
    $("#prdt_name").change(function() {
        var productCode = parseInt($("#prdt_name option:selected").val());
        if (isNaN(productCode)) {
            productCode = 0;
        }
        
        $("#product_grid").jqGrid('setGridParam', {
            postData:{
            	productGroupCode: parseInt($("#prdt_group option:selected").val()),
            	productCode: productCode
            }
        }).trigger("reloadGrid");
    });
    
});

</script>

<div class="reportWrap" style="background: #676767;padding: 0px;height: 100%;width: 100%;">
    <div class="report_List" style="float: left;width: 500px;border: 1px solid #92a0ac;padding:10px;">
        <div class="report_Listdata" style="padding-left: 0px;width: 500px;">
            <table id="product_grid"></table>
        </div>
    </div>

    <div id="optionDiv" style="overflow:hidden;height: 625px;background-color: #FFF;padding: 10px;border: 1px solid #92a0ac;">
        <table id="form_grid"></table>
    </div>
</div>







</br></br>

<input type="button" id="btn_report" value="보고서 작성">

<%@ include file="../include/footer.jsp"%>

<div class="contextMenu" id="context" style="display:none">
    <ul style="width: 110px;">
        <li id="applyAssetGroup">
            <span class="ui-icon ui-icon-check" style="float:left"></span>
            <span style="font-size:12px; font-family:맑은 고딕">적용그룹지정</span>
        </li>
        <li id="applyAsset">
            <span class="ui-icon ui-icon-check" style="float:left"></span>
            <span style="font-size:12px; font-family:맑은 고딕">적용자산지정</span>
        </li>
        <li id="formAdd">
            <span class="ui-icon ui-icon-plus" style="float:left"></span>
            <span style="font-size:12px; font-family:맑은 고딕">양식추가</span>
        </li>
        <li id="formEdit">
            <span class="ui-icon ui-icon-pencil" style="float:left"></span>
            <span style="font-size:12px; font-family:맑은 고딕">양식수정</span>
        </li>
        <li id="formDel">
            <span class="ui-icon ui-icon-trash" style="float:left"></span>
            <span style="font-size:12px; font-family:맑은 고딕">양식삭제</span>
        </li>
    </ul>
</div>
