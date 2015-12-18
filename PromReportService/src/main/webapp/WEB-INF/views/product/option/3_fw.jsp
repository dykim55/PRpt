<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script>

FORM_OPTION = (function() {

    var _formCode, _formType, _reportType, _assetCode, _data;
    var _port = [
                 {"code":"80",   "name":"HTTP"},
                 {"code":"443",  "name":"HTTPS"},
                 {"code":"1433", "name":"MS-SQL"},
                 {"code":"1521", "name":"Oracle"},
                 {"code":"3306", "name":"Mysql"},
                 //{"code":"21",   "name":"FTP"},
                 //{"code":"23",   "name":"Telnet"},
                 {"code":"22",   "name":"SSH"},
                 {"code":"3389", "name":"Terminal"},
                 {"code":"8502", "name":"HTS"}
             ];
    
    $("#search_port").loadSelect(_port, "name", "code");
    $("#search_port").multiselect({
        header: false,
        selectedList: 4,
        noneSelectedText: "선택하세요.",
        position: {
            my: 'left bottom',
            at: 'left top'
         }                
    });
    $("#search_port").multiselect("refresh");
	
    return {
        title : function() {
            return $("#formTitle").html();
        },
    	done : function() {
            var data = {};
            $("#frm").serializeArray().map(function(x){data[x.name] = x.value;});
            
            var port = '';
            $.each($("#search_port").multiselect("getChecked").map(function(){ return this.value; }).get(), function(idx, pack) {
                port += pack + ",";
            });
            
            return { "data": data, "formInfo": { formCode:_formCode, formType:_formType, formReportType:_reportType}, "etc": port };
    	},
        formData : function() {
            return { formCode:_formCode, formType:_formType, formReportType:_reportType, assetCode:_assetCode};
        },
        init : function(formData, assetCode) {
        	_data = formData.data;
        	
            if (formData.formInfo) {
                _formCode = formData.formInfo.formCode;
                _formType = formData.formInfo.formType;
                _reportType = formData.formInfo.formReportType;
            }
            _assetCode = assetCode;
            $("#btn_save_div").css('display', _assetCode ? '' : 'none');
            $("#sch_port_div").css('display', _assetCode ? '' : 'none');
        	
            $.each(Object.keys(_data), function() {
                if (this.indexOf("opt") != -1) {
                    $("#"+this).prop("checked", true);
                    $("#"+this).nextAll('div').children().prop("disabled", false);
                } else if (this.indexOf("rd") != -1) {
                    $("input:radio[name="+this+"]:input[value="+_data[this]+"]").prop("checked", true);
                } else if (this.indexOf("ck") != -1) {
                    $("#"+this).prop("checked", true);
                }
            });
            
            if (formData.etc) {
                $.each(formData.etc.split(','), function(idx, pack) {
                    $.each($("#search_port")[0].options, function(idx, opt) {
                        if (pack == $(opt).val()) {
                            $(opt).attr('selected','selected');
                        }
                    });
                });
                $("#search_port").multiselect("refresh");
            }
            
            $("#formSaveAs").button({icons: {primary: "ui-icon-save"},}).click(function( event ) {
                formSaveAs();
            });
        }
    };
    
})();

$(document).ready(function(){

    $(".subpanel").children().prop("disabled", true);
    
    $("label").bind("mouseover", function () {
        $(this).addClass("labelHover");
    });
    
    $("label").bind("mouseleave", function () {
        $(this).removeClass("labelHover");
    });

    $(":input:checkbox").bind("click", function () {
        $(this).nextAll('div').children().prop("disabled", !$(this).is(":checked"));
    });
    
});

</script>

<span id="formTitle" style="display:none;">방화벽(FW) 월간보고서 설정</span>

<form id="frm" style="font-weight: bold;">

<div id="sch_port_div" style="display:none;">
  <h2 style="margin-top:0px;">조회포트</h2>
  <select style="width:278px;margin-bottom:10px;" id="search_port" multiple="multiple"/>
  <div style="height: 10px;"></div>
</div>

<h2 style="margin-top:0px;">세션로그 발생추이</h2>
<div class="mb10">
  <input type="checkbox" id="opt01" name="opt01"><label for="opt01"> 외부에서 내부로의 전체 세션 로그 발생추이 (차트, 표)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt02" name="opt02"><label for="opt02"> 외부에서 내부로의 허용 세션 로그 발생추이 (차트)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt03" name="opt03"><label for="opt03"> 외부에서 내부로의 차단 세션 로그 발생추이 (차트)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt04" name="opt04"><label for="opt04"> 내부에서 외부로의 전체 세션 로그 발생추이 (차트, 표)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt05" name="opt05"><label for="opt05"> 내부에서 외부로의 허용 세션 로그 발생추이 (차트)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt06" name="opt06"><label for="opt06"> 내부에서 외부로의 차단 세션 로그 발생추이 (차트)</label>
</div>


<h2 class="mb10">출발지IP TOP#N</h2>
<div class="mb10">
  <input type="checkbox" id="opt07" name="opt07"><label for="opt07"> 외부에서 내부로의 전체 세션로그 & SIP TOP (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd7" id="rd7-1" value="10" checked><label for="rd7-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd7" id="rd7-2" value="20"><label for="rd7-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd7" id="rd7-3" value="30"><label for="rd7-3"> TOP 30</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt08" name="opt08"><label for="opt08"> 외부에서 내부로의 허용 세션로그 & SIP TOP (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd8" id="rd8-1" value="10" checked><label for="rd8-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd8" id="rd8-2" value="20"><label for="rd8-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd8" id="rd8-3" value="30"><label for="rd8-3"> TOP 30</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt09" name="opt09"><label for="opt09"> 외부에서 내부로의 차단 세션로그 & SIP TOP (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd9" id="rd9-1" value="10" checked><label for="rd9-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd9" id="rd9-2" value="20"><label for="rd9-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd9" id="rd9-3" value="30"><label for="rd9-3"> TOP 30</label></br>
    <input type="checkbox" id="ck9" name="ck9"><label for="ck9"> SRC IP TOP10 세션 로그 발생추이 (차트)</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt10" name="opt10"><label for="opt10"> 내부에서 외부로의 전체 세션로그 & SIP TOP (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd10" id="rd10-1" value="10" checked><label for="rd10-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd10" id="rd10-2" value="20"><label for="rd10-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd10" id="rd10-3" value="30"><label for="rd10-3"> TOP 30</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt11" name="opt11"><label for="opt11"> 내부에서 외부로의 허용 세션로그 & SIP TOP (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd11" id="rd11-1" value="10" checked><label for="rd11-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd11" id="rd11-2" value="20"><label for="rd11-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd11" id="rd11-3" value="30"><label for="rd11-3"> TOP 30</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt12" name="opt12"><label for="opt12"> 내부에서 외부로의 차단 세션로그 & SIP TOP (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd12" id="rd12-1" value="10" checked><label for="rd12-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd12" id="rd12-2" value="20"><label for="rd12-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd12" id="rd12-3" value="30"><label for="rd12-3"> TOP 30</label></br>
    <input type="checkbox" id="ck12" name="ck12"><label for="ck12"> SRC IP TOP10 세션 로그 발생추이 (차트)</label>
  </div>
</div>

<h2 class="mb10">목적지IP TOP#N</h2>
<div class="mb10">
  <input type="checkbox" id="opt13" name="opt13"><label for="opt13"> 외부에서 내부로의 전체 세션로그 & DIP TOP (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd13" id="rd13-1" value="10" checked><label for="rd13-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd13" id="rd13-2" value="20"><label for="rd13-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd13" id="rd13-3" value="30"><label for="rd13-3"> TOP 30</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt14" name="opt14"><label for="opt14"> 외부에서 내부로의 허용 세션로그 & DIP TOP (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd14" id="rd14-1" value="10" checked><label for="rd14-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd14" id="rd14-2" value="20"><label for="rd14-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd14" id="rd14-3" value="30"><label for="rd14-3"> TOP 30</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt15" name="opt15"><label for="opt15"> 외부에서 내부로의 차단 세션로그 & DIP TOP (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd15" id="rd15-1" value="10" checked><label for="rd15-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd15" id="rd15-2" value="20"><label for="rd15-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd15" id="rd15-3" value="30"><label for="rd15-3"> TOP 30</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt16" name="opt16"><label for="opt16"> 내부에서 외부로의 전체 세션로그 & DIP TOP (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd16" id="rd16-1" value="10" checked><label for="rd16-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd16" id="rd16-2" value="20"><label for="rd16-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd16" id="rd16-3" value="30"><label for="rd16-3"> TOP 30</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt17" name="opt17"><label for="opt17"> 내부에서 외부로의 허용 세션로그 & DIP TOP (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd17" id="rd17-1" value="10" checked><label for="rd17-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd17" id="rd17-2" value="20"><label for="rd17-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd17" id="rd17-3" value="30"><label for="rd17-3"> TOP 30</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt18" name="opt18"><label for="opt18"> 내부에서 외부로의 차단 세션로그 & DIP TOP (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd18" id="rd18-1" value="10" checked><label for="rd18-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd18" id="rd18-2" value="20"><label for="rd18-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd18" id="rd18-3" value="30"><label for="rd18-3"> TOP 30</label>
  </div>
</div>

<h2 class="mb10">서비스 TOP#N</h2>
<div class="mb10">
  <input type="checkbox" id="opt19" name="opt19"><label for="opt19"> 외부에서 내부로의 허용 세션로그 & 서비스 TOP10 (차트, 표)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt20" name="opt20"><label for="opt20"> 외부에서 내부로의 차단 세션로그 & 서비스 TOP10 (차트, 표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="checkbox" id="ck20" name="ck20"><label for="ck20"> 서비스 TOP10 세션 로그 발생추이 (차트)</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt21" name="opt21"><label for="opt21"> 외부에서 내부로의 차단 세션로그 & 서비스 TOP10 상세 (차트)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt22" name="opt22"><label for="opt22"> 내부에서 외부로의 허용 세션로그 & 서비스 TOP10 (차트, 표)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt23" name="opt23"><label for="opt23"> 내부에서 외부로의 차단 세션로그 & 서비스 TOP10 (차트, 표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="checkbox" id="ck23" name="ck23"><label for="ck23"> 서비스 TOP10 세션 로그 발생추이 (차트)</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt24" name="opt24"><label for="opt24"> 내부에서 외부로의 차단 세션로그 & 서비스 TOP10 상세 (차트)</label>
</div>

<h2 class="mb10">성능정보</h2>
<div class="mb10">
  <input type="checkbox" id="opt99" name="opt99"><label for="opt99"> 성능정보 (차트, 표)</label>
</div>

</form>

<div id="btn_save_div" style="display:none;"> 
    <table class="tbl_button marginT5">
        <tr>
            <td class="alignL">
            </td>
            <td class="alignR">
                <button id="formSaveAs">양식 저장</button>
            </td>
        </tr>
    </table>
</div>
