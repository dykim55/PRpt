<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
    import = "java.util.List
        , com.mongodb.DBObject
        , com.cyberone.report.utils.StringUtil
        , com.cyberone.report.model.UserInfo"
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

$(document).ready(function(){
	
    $("label").bind("mouseover", function () {
        $(this).addClass("labelHover");
    });
    
    $("label").bind("mouseleave", function () {
        $(this).removeClass("labelHover");
    });
	
    $("#assetSaveBtn1,#assetSaveBtn2,#assetSaveBtn3,#assetSaveBtn4").button({icons: {primary: "ui-icon-document"},}).click(function( event ) {
    	var reportType;
    	if (event.currentTarget.id == "assetSaveBtn1") {
    		reportType = 1;
    	} else if (event.currentTarget.id == "assetSaveBtn2") {
            reportType = 2;    		
    	} else if (event.currentTarget.id == "assetSaveBtn3") {
            reportType = 3;    		
    	} else if (event.currentTarget.id == "assetSaveBtn4") {
            reportType = 4;    		
    	}
    	
        DIALOG.Open().load("/report/reportOption", {
        	"groupCode": _slct_group_code,
        	"ciText": $("#rptopt_ci_text").val(),
        	"ciType": $(".Board_report :input:radio[name=rptopt_ci_radio]:checked").val(),
        	"pGroup": $("#rptopt_group").html(),
        	"cGroup": $("#rptopt_client").html(),
        	"reportType": reportType, 
        	"slctAssets": JSON.stringify($("#selected_asset_grid").jqGrid("getRowData"))
        }, function(res) {
        	try {
        		promAlert(3, JSON.parse(res).message);
        	} catch(e) {
        		REPORT_OPTION.init($(this));
        	}
        });
    });
    
	var _slct_rowid;
	var _slct_group_name;
	var _slct_group_code;

    $("#asset_tabs").tabs({
        activate: function( event, ui ) {
            
            //해당 설정Tab이 활성화되면
            if (ui.newPanel[0].id == 'asset_day') {
                $(ui.oldPanel).children().remove();
                var slctRow = $("#asset_grid").jqGrid("getRowData", $("#asset_grid").getGridParam("selrow"));
                $.ajax({
                    url : "/report/reportConfig",
                    type : 'POST',
                    data : {"prefix":slctRow.prefix},
                    dataType: 'json',
                    success : function(data){
                        if (data.status == 'success') {
                            $("#asset_day").load("/option/d_" + data.prefix.toLowerCase() + ".html" , function() {
                                FORM_OPTION.init(undefined);
                            });
                        } else {
                        }
                    }
                });
            } else if (ui.newPanel[0].id == 'asset_week') {
            	$(ui.oldPanel).children().remove();
                var slctRow = $("#asset_grid").jqGrid("getRowData", $("#asset_grid").getGridParam("selrow"));
                $.ajax({
                    url : "/report/reportConfig",
                    type : 'POST',
                    data : {"prefix":slctRow.prefix},
                    dataType: 'json',
                    success : function(data){
                        if (data.status == 'success') {
                            $("#asset_week").load("/option/w_" + data.prefix.toLowerCase() + ".html" , function() {
                                WEEK_OPTION.init(undefined);
                            });
                        } else {
                        }
                    }
                });
            } else if (ui.newPanel[0].id == 'asset_month') {
            } else if (ui.newPanel[0].id == 'asset_user') {
                $(ui.oldPanel).children().remove();
                var slctRow = $("#asset_grid").jqGrid("getRowData", $("#asset_grid").getGridParam("selrow"));
                $.ajax({
                    url : "/report/reportConfig",
                    type : 'POST',
                    data : {"prefix":slctRow.prefix},
                    dataType: 'json',
                    success : function(data){
                        if (data.status == 'success') {
                            $("#asset_user").load("/option/u_" + data.prefix.toLowerCase() + ".html" , function() {
                                USER_OPTION.init(undefined);
                            });
                        } else {
                        }
                    }
                });
            }
        }
    });

    $("#selected_asset_grid").jqGrid({
        datatype: "jsonstring",
        colNames:["Id", "Level", "formCode", "선택된 자산명", "","통계구분","장비구분"],
        colModel:[
            {name:'id', index:'id', width:0, hidden:true, key:true},
            {name:'level', index:'level', width:0, hidden:true},
            {name:'applyFormCode', index:'applyFormCode', width:0, hidden:true},
            {name:'name', index:'name', width:200, fixed:true, title:true, sortable: false,
                formatter: function (cellvalue, options, rowObject) {
                    return $.jgrid.htmlEncode(cellvalue);
                }
            },
            {name:'parent',        index:'parent',         width:0, hidden:true},
            {name:'prefix',        index:'prefix',        width:80, align:'center', sortable: false,
                formatter: function (cellvalue, options, rowObject) {
                    if (cellvalue) {
                          return cellvalue.toUpperCase();
                    }
                    return "";
                }
            },
            {name:'assetType',     index:'assetType',     width:80, align:'center', sortable: false,
                formatter: function (cellvalue, options, rowObject) {
                    if (cellvalue) {
                          return cellvalue.toUpperCase();
                    }
                    return "";
                }, title:false
            }
        ],
        rowNum: 10000,
        gridview:true, 
        height: 'auto',
        viewrecords: true,
        rownumbers:true,
        rownumWidth: 40,
        sortable: false,
        beforeSelectRow: function (rowid, e) {
        },
        onRightClickRow: function(rowid, iRow, iCol, e) {
        },
        gridComplete: function () {
        }
    });
    
    var _assetsSelectedData = [];
    var gridInitSelectedRowData = function () {
        // selected asset row grid
        var checkedRowId, checkedRow, firstGroupRow, secondGroupRow;
        
        _assetsSelectedData.length = 0;
        $.each($("#asset_grid").find("input:checkbox:checked"), function(idx, cnode) {
            checkedRowId = $(cnode).parent().parent().parent().attr("id");
            checkedRow = $("#asset_grid").jqGrid("getLocalRow", checkedRowId);
            if (checkedRow.isLeaf) {
                secondGroupRow = $("#asset_grid").jqGrid("getNodeParent", checkedRow);
                firstGroupRow = $("#asset_grid").jqGrid("getNodeParent", secondGroupRow);
                checkedRow["groupName"] = firstGroupRow.name + " > " + secondGroupRow.name;
                _assetsSelectedData.push(checkedRow);       
            }
        });
        $("#selected_asset_grid")[0].addJSONData({
            total: 1,
            page: 1,
            records: _assetsSelectedData.length,
            rows: _assetsSelectedData
        });
    }
    
    $("#asset_grid").jqGrid({
    	url:  "/report/assetList",
        datatype: "json",
        colNames:["Id", "Level",'자산그룹/자산명','통계구분','장비구분',''/*,'제품명','상태','등록자','등록일시', '설명'*/],
        colModel:[
            {name:'id',            index:'id',            width:0, hidden:true, key:true},
            {name:'level',         index:'level',         width:0, hidden:true},
            {name:'name',          index:'name',          width:323, align:'left', sortable: false,
                formatter: function (cellvalue, options, rowObject) {
                    if (rowObject.level == 2) {
                        //return "&nbsp;<input type='radio' name='t71_radio' class='itmchk'>&nbsp;" + $.jgrid.htmlEncode(cellvalue);
                        return "&nbsp;<input type='checkbox' class='itmchk' >&nbsp;" + $.jgrid.htmlEncode(cellvalue);
                    } else if (rowObject.level == 3) {
                        return "&nbsp;<input type='checkbox' class='itmchk' >&nbsp;" + $.jgrid.htmlEncode(cellvalue);
                    } else {
                        return cellvalue;
                    }
                }, title:false
            },
            {name:'prefix',        index:'prefix',        width:80, align:'center', sortable: false,
            	formatter: function (cellvalue, options, rowObject) {
            		if (cellvalue) {
            			  return cellvalue.toUpperCase();
            		}
            		return "";
            	}
            },
            {name:'assetType',     index:'assetType',     width:80, align:'center', sortable: false, 
                formatter: function (cellvalue, options, rowObject) {
                	if (cellvalue) {
                		return cellvalue.toUpperCase();
                	}
                	return "";
                }, title:false
            },
            {name:'productCode',   index:'productCode',   width:0, hidden:true}
            /*,
            {name:'assetIp',       index:'assetIp',       width:150, align:'center', sortable: false, title:false},
            {name:'stat',          index:'stat',          width:50,  align:'center', sortable: false, title:false},
            {name:'createId',      index:'createId',      width:100, align:'center', sortable: false, title:false},
            {name:'createTime',    index:'createTime',    width:200, align:'center', sortable: false, title:false},
            {name:'memo',          index:'memo',          width:0, hidden:true}*/
        ],
        rowNum: 1000,
        width: 'auto',
        height: '600',
        sortname: 'id',
        treeGrid: true,
        treeGridModel: 'adjacency',
        ExpandColumn: 'name',
        treeIcons: {
            plus:'folder',
            minus:'folderOpen',
            leaf:'leaf_asset'
        },         
        jsonReader: {
            repeatitems: false,
            root: "rows"
        },
        postData: {},
        gridComplete: function () {
            initContextMenu();
        },
        beforeSelectRow: function (rowid, e) {
        	var setChildrenItems = function (grid, children, state) {
                $.each(children, function () {
                    $("#" + this.id, grid).find('input[type=checkbox]').prop("checked", state)
                    if (state) {
                    	$("#" + this.id, grid).addClass("ui-state-checked");
                    } else {
                    	$("#" + this.id, grid).removeClass("ui-state-checked");
                    }
                });
            };
            var getChildrenCheckedCount = function (grid, children) {
            	var cnt=0;
            	$.each(children, function () {
            		 if ($("#" + this.id, grid).find('input[type=checkbox]').is(':checked')) cnt++;
            	});
            	return cnt;
            };

        	if ($(this).jqGrid('getLocalRow', rowid).level < 2) return;
        	
        	if ($(this).jqGrid('getLocalRow', rowid).level == 2) {
        		if (_slct_rowid == parseInt(rowid)) {
                    if (e.target.type == 'checkbox') {
                    	setChildrenItems($(this), $(this).jqGrid("getNodeChildren", $(this).jqGrid("getLocalRow", rowid)),  $(e.target).is(':checked'));
                    	gridInitSelectedRowData();
                        return; 
                    } else { return; }
        		}
        		
        		setChildrenItems($(this), $(this).jqGrid("getNodeChildren", $(this).jqGrid("getLocalRow", _slct_rowid)), false);
        		$("#"+ _slct_rowid, this).find('input[type=checkbox]').prop("checked", false);
        		
        		if (e.target.type == 'checkbox') {
        			setChildrenItems($(this), $(this).jqGrid("getNodeChildren", $(this).jqGrid("getLocalRow", rowid)),  $(e.target).is(':checked'));
        		}
        		
        		gridInitSelectedRowData();
        		reloadReportOption(rowid);
        		
                _slct_rowid = rowid;
        	} else if ($(this).jqGrid('getLocalRow', rowid).level == 3) {
        		var pNode = $(this).jqGrid("getNodeParent", $(this).jqGrid("getLocalRow", rowid));
        		if (pNode.id != _slct_rowid) {
        			setChildrenItems($(this), $(this).jqGrid("getNodeChildren", $(this).jqGrid("getLocalRow", _slct_rowid)), false);
        			$("#"+ _slct_rowid, this).find('input[type=checkbox]').prop("checked", false);
        			
                    if (e.target.type != 'checkbox') {
                        $("#"+ rowid, this).find('input[type=checkbox]').prop("checked", !$("#"+ rowid, this).find('input[type=checkbox]').prop("checked"));
                    }
                    
                    reloadReportOption(pNode.id);
                    
        			_slct_rowid = pNode.id;
        		} else {
                    if (e.target.type != 'checkbox') {
                        $("#"+ rowid, this).find('input[type=checkbox]').prop("checked", !$("#"+ rowid, this).find('input[type=checkbox]').prop("checked"));
                    } 
        		}
        		if (getChildrenCheckedCount($(this),$(this).jqGrid("getNodeChildren", $(this).jqGrid("getLocalRow", pNode.id))) == $(this).jqGrid("getNodeChildren", $(this).jqGrid("getLocalRow", pNode.id)).length) {
        			$("#"+ _slct_rowid, this).find('input[type=checkbox]').prop("checked", true);
        		} else {
        			$("#"+ _slct_rowid, this).find('input[type=checkbox]').prop("checked", false);
        		}
        		
        		if ($("#"+ rowid, this).find('input[type=checkbox]').is(':checked')) {
        			$("#"+ rowid, this).addClass("ui-state-checked"); 
        		} else {
        			$("#"+ rowid, this).removeClass("ui-state-checked");
        		}
        		
        		gridInitSelectedRowData();
        	}
        	
            $(this).jqGrid('expandNode', $(this).jqGrid("getLocalRow", rowid));
            $(this).jqGrid('expandRow', $(this).jqGrid("getLocalRow", rowid));
        },
        onSelectRow: function(rowid, status, e) {
            var slctRow = $("#asset_grid").jqGrid("getRowData", $("#asset_grid").getGridParam("selrow"));

            $.ajax({
                url : "/report/reportConfig",
                type : 'POST',
                data : {"prefix":slctRow.prefix},
                dataType: 'json',
                success : function(data){
                    if (data.status == 'success') {
                    	$("#asset_tabs").tabs("option", "active", 0);
                    	$("#asset_day").load("/option/d_" + data.prefix.toLowerCase() + ".html" , function() {
                    		FORM_OPTION.init(undefined);
                    	});
                    } else {
                    }
                }
            });
        }
    });

    function clearReportOption() {
        $("#rptopt_ci_text").val("");
        $('.Board_report :input:radio[name=rptopt_ci_radio]:input[value="text"]').attr("checked", true);
        $("#rptopt_ci_img")[0].src = "imageDownload?type=report&code=0&pos=ci";
        _slct_rowid = 0;
        _slct_group_name = "";
    }
    
    function reloadReportOption(rowid) {
        clearReportOption();
        $.ajax({
            url : "/report/assetReportOption",
            type : 'POST',
            data : {"groupCode": parseInt(rowid)},
            dataType: 'json',
            success : function(data){
                if (data.status == 'success') {

                    var crow = $("#asset_grid").jqGrid("getLocalRow", rowid);
                    var prow = $("#asset_grid").jqGrid("getNodeParent", crow)
                    $("#rptopt_group").html(prow.name);
                    $("#rptopt_client").html(crow.name);
                    $("#rptopt_ci_text").val(crow.name);
                    _slct_group_name = crow.name;
                    _slct_group_code = crow.id;
                    console.log(_slct_group_code);

                    if (data.reportOption) {
                        $("#rptopt_ci_text").val(data.reportOption.ciText);
                        $('.Board_report :input:radio[name=rptopt_ci_radio]:input[value="' + data.reportOption.ciType + '"]').attr("checked", true);
                        $("#rptopt_ci_img")[0].src = "imageDownload?type=report&code="+data.reportOption.groupCode+"&pos=ci";
                    }
                } else {
                }
            }
        });
        _slct_rowid = parseInt(rowid);
    }
    
    function initContextMenu() {    
        jQuery(".jqgrow", "#asset_grid").contextMenu('context', {
            bindings: {
                'applyForm': function(t) {
                	applyForm();
                }
            },
            onContextMenu : function(e, menu) {
               var rowId = $(e.target).parents("tr").attr("id")
               var slctRow = $("#asset_grid").jqGrid("getRowData", rowId);
               if (slctRow.level < 2) {
            	   return false;
               }
               
               $("#asset_grid").setSelection(rowId);
               return true;                                    
            },
            onShowMenu: function(e, menu) {
                return menu;
            }        
        });             
    } 
    
    function applyForm() {
    	stop();
    	var slctRow = $("#asset_grid").jqGrid("getRowData", $("#asset_grid").getGridParam("selrow"));
        DIALOG.Open().load("/report/applyForm", function() {
            APPLY_FORM_DLG.init($(this), slctRow);
        }); 
    }
    
    $("#btn_report").click(function () {
        $.ajax({
            url : "/report/printReport",
            type : 'POST',
            data : {},
            dataType: 'json',
            success : function(data){
                if (data.status == 'success') {
                } else {
                }
            }
        });
    	
    });
	
    $("#rptopt_btn_file_ci").click(function(event) {
        DIALOG.Open().load("/report/fileUpload", function() {
            var Dlg = $(this);
            
            var pos = "";
            if (event.currentTarget.id == 'rptopt_btn_file_ci') {
                pos = "ci";
            }
            FILE_UPLOAD.init(_slct_rowid, pos, Dlg);
        }); 
    });

    $("#rptopt_btn_del_ci").click(function(event) {
        $.ajax({
            url : "/report/imageDelete",
            type : 'POST',
            data : {"clientCode": _slct_rowid},
            dataType: 'json',
            success : function(data){
            	$("#rptopt_ci_img")[0].src = $("#rptopt_ci_img")[0].src;
            }
        });
    });
    	
    $("#assetSaveBtn").button({icons: {primary: "ui-icon-refresh"},}).click(function( event ) {
    });
    
    $("#asset_server_type").change(function() {
        $("#asset_grid").jqGrid('setGridParam', {
            url:  "/report/assetList",
            postData:{idx:$("#asset_server_type option:selected").val()}
        }).trigger("reloadGrid");
    });
    
    
});

</script>

<div class="reportWrap" style="background: #676767;padding: 0px;height: 100%;width: 100%;">
    <div class="report_List" style="float: left;width: 500px;border: 1px solid #92a0ac;padding:10px;">
        <div class="reportSearchWrap">
            <strong>검색 : </strong>
            <select id="asset_search_type" style="width:100px;height: 22px;">
                <option value="groupName">자산그룹</option>
                <option value="assetName">자산명</option>
            </select>
            <input id="asset_search_group" type="text" style="width:150px;vertical-align: top;height: 16px;"/>
        </div>
        <div class="report_Listdata" style="padding-left: 0px;width: 500px;">
            <table id="asset_grid"></table>
        </div>
    </div>

    <div class="report_body" style="overflow-y: auto;">
        <div class="reportNaviWrap"><h7 id="rptopt_gubun">통합 분석보고서</h7><h8>ㅣ <span id="rptopt_group"></span>&nbsp;&gt;&nbsp;<span id="rptopt_client"></span></h8></div>
        <div class="Board_report">
            <table width="100%" cellspacing="0" cellpadding="0" summary="보고서리스트">
                <colgroup>
                   <col width="80" />
                   <col width="50" />
                   <col width="70" />
                   <col width="*" />
                </colgroup>
                <tr>
                    <th class="end">레포트 제목</th>
                    <td colspan="2" class="end">
                        <input id="rptopt_lb1" name="rptopt_ci_radio" type="radio" class="chk_rdo_none" value="text" /><label for="rptopt_lb1">타이틀입력</label>
                    </td>
                    <td class="end">
                        <div style="float:left;margin-top: 1px;"><input id="rptopt_ci_text" type="text" class="info_M important" maxbytes="128"/></div>
                        <div class="reportBtn">
                            <img src="/img/btn_pic_add.png" width="53" height="22" id="rptopt_btn_del_ci"/>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td colspan="2">
                        <input id="rptopt_lb2" name="rptopt_ci_radio" type="radio" class="chk_rdo_none" value="image" /><label for="rptopt_lb2">이미지선택</label>
                    </td>
                    <td>
                        <div class="reportLogo"><img src="/img/no_image.png" id="rptopt_ci_img" width="237" height="47" /></div>
                        <div class="reportBtn">
                            <img class="top" src="/img/btn_pic_add.png" width="53" height="22" id="rptopt_btn_file_ci"/>
                            <img src="/img/btn_pic_delete.png" width="53" height="22" id="rptopt_btn_del_ci"/>
                        </div>
                    </td>
                </tr>
                <tr>
                    <th class="end">선택 자산</th>
                    <td colspan="3" class="end">
                        <div class="floatL" style="margin-bottom: 5px;"><table id="selected_asset_grid" style="margin-bottom: 5px;"></table></div>
                    </td>
                </tr>
            </table>
        </div>
        
	    <table class="tbl_button marginT5">
	        <tr>
	            <td class="alignL">
                    <button id="assetSaveBtn1">일일보고서</button>
                    <button id="assetSaveBtn2">주간보고서</button>
                    <button id="assetSaveBtn3">월간보고서</button>
                    <button id="assetSaveBtn4">임의기간보고서</button>
	            </td>
	            <td class="alignR">
	            </td>
	        </tr>
	    </table>
        
    </div>
   
</div>







</br></br>

<input type="button" id="btn_report" value="보고서 작성">

<%@ include file="../include/footer.jsp"%>

<div class="contextMenu" id="context" style="display:none">
    <ul style="width: 110px;">
        <li id="applyForm">
            <span class="ui-icon ui-icon-document" style="float:left"></span>
            <span style="font-size:12px; font-family:맑은 고딕">적용양식지정</span>
        </li>
    </ul>
</div>
