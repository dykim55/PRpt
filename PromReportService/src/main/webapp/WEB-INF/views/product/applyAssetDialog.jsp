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

APPLY_DLG = (function() {
	var _Dlg;
	var _formCode;
	var _assetsSelectedData = [];
	
    return {
        apply_asset: function() {
            var applyAssetList = [];
            var changeAssetList = [];
            var rowIDs = jQuery("#apply_selected_asset").jqGrid("getDataIDs");
            $.each(rowIDs, function(idx, rowID) {
                var row =$("#apply_selected_asset").jqGrid("getRowData", rowID);
                var applyAsset = {};
                applyAsset.code = parseInt(row.id);
                applyAssetList.push(applyAsset);
                
                if (parseInt(row.applyFormCode) > 0 && parseInt(row.applyFormCode) != parseInt(_formCode)) {
                	var changeAsset = {};
                	changeAsset.code = parseInt(row.id);
                	changeAssetList.push(applyAsset);
                }
            });

            return { "applyAsset": applyAssetList, "changeAsset": changeAssetList };
        },
        init : function(Dlg, slctRow) {
        	_Dlg = Dlg;
        	_formCode = slctRow.code;
        	
            $("#apply_selected_asset").jqGrid({
                datatype: "jsonstring",
                colNames:["Id", "Level", "formCode", "", "적용된(선택된) 자산명", "그룹명"],
                colModel:[
                    {name:'id', index:'id', width:0, hidden:true, key:true},
                    {name:'level', index:'level', width:0, hidden:true},
                    {name:'applyFormCode', index:'applyFormCode', width:0, hidden:true},
                    {name:'itmchk', index:'itmchk', width:20, align:'center', fixed:true, title:false, sortable: false, resizable:false,
                        formatter: function (cellvalue, options, rowObject) {
                            return '<input type="checkbox" class="itmchk" checked="checked">';
                        }
                    },
                    {name:'name', index:'name', width:200, fixed:true, title:true, sortable: false,
                        formatter: function (cellvalue, options, rowObject) {
                            return $.jgrid.htmlEncode(cellvalue);
                        }

                    },
                    {name:'groupName', index:'groupName', width:140, fixed:true, title:true, sortable: false,
                        formatter: function (cellvalue, options, rowObject) {
                            return rowObject.groupName;
                        }
                    }
                ],
                height: 535,
                rowNum: 10000,
                gridview:true,        
                viewrecords: true,
                sortable: false,
                beforeSelectRow: function (rowid, e) {
                    if ($(e.target).attr("class") == "itmchk") {
                        $("#"+ rowid, $("#apply_asset")).find('input').prop("checked", false);
                        $("#"+ rowid, $("#apply_asset")).removeClass("apply");
                        $("#"+ rowid, $("#apply_selected_asset")).remove();
                        for (var i = 0; i < _assetsSelectedData.length; i++) {
                            if (_assetsSelectedData[i].id == rowid) {
                                _assetsSelectedData.splice(i, 1);
                                break;
                            }
                        }

                        var localData, localState;
                        var setChechedStateOfParentItems = function (rowid, children) {
                            if (children.length == 0) {
                                return $("#" + rowid, $("#apply_asset")).find('input').is(":checked");
                            }       
                            var flag = true;
                            $.each(children, function () {
                                if (!this.isLeaf) {
                                    if(setChechedStateOfParentItems(this.id, $("#apply_asset").jqGrid("getNodeChildren", this))) {
                                        $("#"+ this.id, $("#apply_asset")).find('input').prop("checked", true);
                                    } else {
                                        $("#"+ this.id, $("#apply_asset")).find('input').prop("checked", false);
                                        flag = false;
                                    }
                                } else {
                                    if(!$("#"+ this.id, $("#apply_asset")).find('input').is(":checked")) {
                                        flag = false;
                                    }
                                }
                            });
                            return flag;
                        };

                        localData =  $("#apply_asset").jqGrid("getLocalRow", 0);
                        $("#"+ localData.id, $("#apply_asset")).find('input').prop("checked", setChechedStateOfParentItems(0, $("#apply_asset").jqGrid("getNodeChildren", localData)));

                        $("#apply_asset").jqGrid("setSelection", rowid);
                    }                },
                onRightClickRow: function(rowid, iRow, iCol, e) {
                    $("#apply_selected_asset").jqGrid("resetSelection");
                },
                gridComplete: function () {
                }
            });
        	
            $("#apply_asset").jqGrid({
            	url:  "/report/assetApplyList",
            	datatype: "json",
                colNames:["Id", "Level", "적용가능한 그룹/자산명","통계구분","제품군", "적용양식",""],
                colModel:[
                    {name:'id', index:'id', width:0, hidden:true, key:true},
                    {name:'level', index:'level', width:0, hidden:true},
                    {name:'name', index:'name', width:250, 
                        formatter: function (cellvalue, options, rowObject) {
                            return "&nbsp;<input type='checkbox' class='itmchk' >&nbsp;" + $.jgrid.htmlEncode(cellvalue);
                        }
                    },
                    {name:'prefix',        index:'prefix',        width:50, align:'center', fixed:true, title:false, sortable: false,
                    	formatter: function (cellvalue, options, rowObject) {
                            return cellvalue.toUpperCase();
                        }
                    },
                    {name:'assetType',     index:'assetType',     width:50, align:'center', fixed:true, title:false, sortable: false,
                    	formatter: function (cellvalue, options, rowObject) {
                    		return cellvalue.toUpperCase();
                        }                    	
                    },
                    {name:'applyForm',     index:'applyForm',     width:250, align:'left', fixed:true, title:false, sortable: false},
                    {name:'applyFormCode', index:'applyFormCode', width:0, hidden:true},
                ],
                height: 535,
                gridview: true,
                rowNum: 10000,
                sortname: 'id',
                scrollrows: true,
                treeGrid: true,
                treeGridModel: 'adjacency',
                jsonReader: {
                    repeatitems: false,
                    root: "rows"
                },
                postData: {
                    prefix: slctRow.prefix,
                    reportType: slctRow.reportType
                },
                ExpandColumn: 'name',
                treeIcons: {
                    plus:'folder',
                    minus:'folderOpen',
                    leaf:'leaf_asset'
                },
                gridComplete: function () {
                	
                    $.ajax({
                        url : "/product/appliedAssets",
                        type : 'POST',
                        data : {formCode: _formCode},
                        dataType: 'json',
                        success : function(data){
                            if (data.status == 'success') {
                            	$.each(data.appliedAssets, function(idx, obj) {
                            		$("#"+ obj.code,$("#apply_asset")).find(':input').attr("checked", true);
                            		$("#"+ obj.code,$("#apply_asset")).addClass("apply");
                            	});

                            	var localData = $("#apply_asset").jqGrid("getLocalRow", 0);
                                $("#"+ localData.id, $("#apply_asset")).find(':input').attr("checked", checkParentNodes(0, "apply_asset", $("#apply_asset").jqGrid("getNodeChildren", localData)));
                            	
                                gridInitSelectedRowData();
                            } else {
                            }
                        }
                    });
                	
                },
                beforeSelectRow: function (rowid, e) {
                    var $this = $(this), localData, localState, parentData;

                    var setChechedStateOfParentItems = function (rowid, children) {
                        if (children.length == 0) {
                            return $("#" + rowid, $this).find('input').is(":checked");
                        }       
                        var flag = true;
                        $.each(children, function () {
                            if (!this.isLeaf) {
                                if(setChechedStateOfParentItems(this.id, $this.jqGrid("getNodeChildren", this))) {
                                    $("#"+ this.id, $this).find('input').prop("checked", true);
                                } else {
                                    $("#"+ this.id, $this).find('input').prop("checked", false);
                                    flag = false;
                                }
                            } else {
                                if(!$("#"+ this.id, $this).find('input').is(":checked")) {
                                    flag = false;
                                }
                            }
                        });
                        return flag;
                    };
                    var setChechedStateOfChildrenItems = function (children) {
                        $.each(children, function () {
                            $("#" + this.id, $this).find('input').prop("checked", localState);
                            if (this.isLeaf && localState) {
                            	$("#" + this.id, $this).addClass("apply");
                            } else {
                            	$("#" + this.id, $this).removeClass("apply");
                            }
                            if (!this.isLeaf) {
                                setChechedStateOfChildrenItems($this.jqGrid("getNodeChildren", this));
                            }
                        });
                    };
                    
                    localState = $("#"+ rowid, this).find('input').prop("checked");
                    if (e.target.nodeName != "INPUT") {
                        $("#"+ rowid, this).find('input').prop("checked", !localState);
                    }

                    localState = $("#"+ rowid, this).find('input').prop("checked");
                    localData = $this.jqGrid("getLocalRow", rowid);
                    setChechedStateOfChildrenItems($this.jqGrid("getNodeChildren", localData), localState);
                    
                    if (localData.level==3 && localState) {
                    	$("#"+ rowid, this).addClass("apply");
                    } else {
                    	$("#"+ rowid, this).removeClass("apply");
                    }

                    parentData =  $this.jqGrid("getLocalRow", 0);
                    $("#"+ parentData.id, $this).find('input').prop("checked", setChechedStateOfParentItems(0, $this.jqGrid("getNodeChildren", parentData)));

                    gridInitSelectedRowData();

                    $("#apply_asset").jqGrid("resetSelection");
                },
                onRightClickRow: function(rowid, iRow, iCol, e) {
                    $("#apply_asset").jqGrid("resetSelection");
                }
            });
        	
            var gridInitSelectedRowData = function () {
                // selected asset row grid
                var checkedRowId, checkedRow, firstGroupRow, secondGroupRow;
                
                _assetsSelectedData.length = 0;
                $.each($("#apply_asset").find("input:checkbox:checked"), function(idx, cnode) {
                    checkedRowId = $(cnode).parent().parent().parent().attr("id");
                    checkedRow = $("#apply_asset").jqGrid("getLocalRow", checkedRowId);
                    if (checkedRow.isLeaf) {
                        secondGroupRow = $("#apply_asset").jqGrid("getNodeParent", checkedRow);
                        firstGroupRow = $("#apply_asset").jqGrid("getNodeParent", secondGroupRow);
                        checkedRow["groupName"] = firstGroupRow.name + " > " + secondGroupRow.name;
                        
                        _assetsSelectedData.push(checkedRow);       
                    }
                });

                $("#apply_selected_asset")[0].addJSONData({
                    total: 1,
                    page: 1,
                    records: _assetsSelectedData.length,
                    rows: _assetsSelectedData
                });

                if (_assetsSelectedData.length > 0) {
                    $("#noregist_apply_selected_asset").hide();
                } else {
                    $("#noregist_apply_selected_asset").show();
                }
            }
            
            var leafCount = 0;
            var checkParentNodes = function (rowid, parentid, children) {
                if (children.length == 0) {
                    $("#"+parentid).jqGrid("delRowData", rowid);
                    return true;
                    //return $("#" + rowid, $("#p42_apply_asset")).find('input').is(":checked");
                }       
                var flag = true;
                $.each(children, function () {
                    if (!this.isLeaf) {
                        if(checkParentNodes(this.id, parentid, $("#"+parentid).jqGrid("getNodeChildren", this))) {
                            $("#"+ this.id, $("#"+parentid)).find(':input').attr("checked", true);
                        } else {
                            flag = false;
                        }
                        if (this.level == 1) {
                            leafCount = 0;                      
                        }
                    } else {
                        leafCount++;
                        if(!$("#"+ this.id, $("#"+parentid)).find(':input').is(":checked")) {
                            flag = false;
                        }
                    }
                });

                if(leafCount == 0 && rowid != 0) {
                    $("#"+parentid).jqGrid("delRowData", rowid);
                }
                return flag;
            };
            
            _Dlg.dialog({
                autoOpen: false,
                resizable: false,
                width: 1050, 
                height: 700, 
                modal: true,
                title: "[ " + slctRow.name + " ] 자산지정",
                buttons: {
                    "저장": function() {
                    	var token = APPLY_DLG.apply_asset();

                        $.ajax({
                            url : "/product/applyAssetSave",
                            type : 'POST',
                            data : { 
                                "formType":slctRow.formType, 
                                "reportType":slctRow.reportType,
                                "prefix":slctRow.prefix.toLowerCase(),
                                "formCode":slctRow.code,
                                "applyAsset": JSON.stringify(token.applyAsset),
                                "changeAsset": JSON.stringify(token.changeAsset)
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
    };
})();

$(document).ready(function () {
}); 

</script>

<div class="dialog_Body" style="padding: 3px 3px 3px 5px; margin-bottom: 2px;">
    <strong>검색 : </strong><input type="text" id="p42_search_asset" style="width: 200px;">
</div>


<div class="dialog_Body" style="height: 100%;">
    <div class="floatL"><table id="apply_asset"></table></div>
    <div class="floatL marginL10"><table id="apply_selected_asset"></table></div>
</div>
            
</form>
            