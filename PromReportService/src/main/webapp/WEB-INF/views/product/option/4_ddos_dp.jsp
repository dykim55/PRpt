<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script>

FORM_OPTION = (function() {

    var _formCode, _formType, _reportType, _assetCode, _data;
    
    return {
        title : function() {
            return $("#formTitle").html();
        },
        done : function() {
            var data = {};
            $("#frm").serializeArray().map(function(x){data[x.name] = x.value;});
            return { "data": data, "formInfo": { formCode:_formCode, formType:_formType, formReportType:_reportType}, "etc": undefined };
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

<span id="formTitle" style="display:none;">DDoS(DP) 임의기간보고서 설정</span>

<form id="frm" style="font-weight: bold;">

<h2 style="margin-top:0px;">탐지로그 발생추이</h2>
<div class="mb10">
  <input type="checkbox" id="opt01" name="opt01"><label for="opt01"> 전체 탐지로그 발생추이 - 로그건 (차트)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt02" name="opt02"><label for="opt02"> 전체 탐지로그 발생추이 - 유형건 (차트)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt03" name="opt03"><label for="opt03"> 전체 공격규모 추이 - 로그건 (차트)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt04" name="opt04"><label for="opt04"> 전체 공격규모 추이 - 유형 & Drop Packet Cnt (차트)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt05" name="opt05"><label for="opt05"> 전체 공격규모 추이 - 유형 & BandWidth (차트)</label>
</div>

<h2 class="mb10">이벤트 TOP#N</h2>
<div class="mb10">
  <input type="checkbox" id="opt06" name="opt06"><label for="opt06"> 전체 탐지로그 & 공격 유형별 TOP#10 (차트, 표)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt07" name="opt07"><label for="opt07"> 전체 탐지로그 & 이벤트 TOP - 로그건 (차트, 표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd7" id="rd7-1" value="10" checked><label for="rd7-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd7" id="rd7-2" value="20"><label for="rd7-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd7" id="rd7-3" value="30"><label for="rd7-3"> TOP 30</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt08" name="opt08"><label for="opt08"> 전체 탐지로그 & 이벤트 TOP - Drop Packet Cnt (차트, 표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd8" id="rd8-1" value="10" checked><label for="rd8-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd8" id="rd8-2" value="20"><label for="rd8-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd8" id="rd8-3" value="30"><label for="rd8-3"> TOP 30</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt09" name="opt09"><label for="opt09"> 전체 탐지로그 & 이벤트 TOP - Bandwidth (차트, 표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd9" id="rd9-1" value="10" checked><label for="rd9-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd9" id="rd9-2" value="20"><label for="rd9-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd9" id="rd9-3" value="30"><label for="rd9-3"> TOP 30</label>
  </div>
</div>

<h2 class="mb10">출발지IP TOP#N</h2>
<div class="mb10">
  <input type="checkbox" id="opt10" name="opt10"><label for="opt10"> 전체 탐지로그 & SIP TOP - 로그건 (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd10" id="rd10-1" value="10" checked><label for="rd10-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd10" id="rd10-2" value="20"><label for="rd10-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd10" id="rd10-3" value="30"><label for="rd10-3"> TOP 30</label></br>
    <input type="checkbox" id="ck10" name="ck10"><label for="ck10"> 출발지IP TOP10 탐지로그 발생추이 - 로그건 (차트)</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt11" name="opt11"><label for="opt11"> 전체 탐지로그 & SIP TOP - Drop Packet Cnt (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd11" id="rd11-1" value="10" checked><label for="rd11-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd11" id="rd11-2" value="20"><label for="rd11-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd11" id="rd11-3" value="30"><label for="rd11-3"> TOP 30</label></br>
    <input type="checkbox" id="ck11" name="ck11"><label for="ck11"> 출발지IP TOP10 탐지로그 발생추이 - Drop Packet Cnt (차트)</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt12" name="opt12"><label for="opt12"> 전체 탐지로그 & SIP TOP - BandWidth (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd12" id="rd12-1" value="10" checked><label for="rd12-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd12" id="rd12-2" value="20"><label for="rd12-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd12" id="rd12-3" value="30"><label for="rd12-3"> TOP 30</label></br>
    <input type="checkbox" id="ck12" name="ck12"><label for="ck12"> 출발지IP TOP10 탐지로그 발생추이 - BandWidth (차트)</label>
  </div>
</div>

<h2 class="mb10">목적지IP TOP#N</h2>
<div class="mb10">
  <input type="checkbox" id="opt13" name="opt13"><label for="opt13"> 전체 탐지로그 & DIP TOP - 로그건 (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd13" id="rd13-1" value="10" checked><label for="rd13-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd13" id="rd13-2" value="20"><label for="rd13-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd13" id="rd13-3" value="30"><label for="rd13-3"> TOP 30</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt14" name="opt14"><label for="opt14"> 전체 탐지로그 & DIP TOP - Drop Packet Cnt (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd14" id="rd14-1" value="10" checked><label for="rd14-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd14" id="rd14-2" value="20"><label for="rd14-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd14" id="rd14-3" value="30"><label for="rd14-3"> TOP 30</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt15" name="opt15"><label for="opt15"> 전체 탐지로그 & DIP TOP - BandWidth (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd15" id="rd15-1" value="10" checked><label for="rd15-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd15" id="rd15-2" value="20"><label for="rd15-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd15" id="rd15-3" value="30"><label for="rd15-3"> TOP 30</label>
  </div>
</div>

<h2 class="mb10">서비스 TOP#N</h2>
<div class="mb10">
  <input type="checkbox" id="opt16" name="opt16"><label for="opt16"> 전체 탐지로그 & 서비스 TOP10 - 로그건 (차트, 표)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt17" name="opt17"><label for="opt17"> 전체 탐지로그 & 서비스 TOP10 - Drop Packet Cnt (차트, 표)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt18" name="opt18"><label for="opt18"> 전체 탐지로그 & 서비스 TOP10 - BandWidth (차트, 표)</label>
</div>

<h2 class="mb10">성능정보</h2>
<div class="mb10">
  <input type="checkbox" id="opt99" name="opt99"><label for="opt99"> 성능정보 (차트)</label>
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
