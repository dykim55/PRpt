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

<span id="formTitle" style="display:none;">관제실적 주간보고서 설정</span>

<form id="frm" style="font-weight: bold;">

<h2 style="margin-top:0px;">보안관제 장비현황</h2>
<div class="mb10">
  <input type="checkbox" id="opt1" name="opt1"><label for="opt1"> 관제대상 장비 (표)</label>
</div>

<h2 style="margin-top:0px;">보안관제 업무현황</h2>
<div class="mb10">
  <input type="checkbox" id="opt2" name="opt2"><label for="opt2"> 보고(비정기) 현황 (표)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt3" name="opt3"><label for="opt3"> 보고내역 상세 (표)</label>
</div>

<h2 style="margin-top:0px;">보안장비 운영현황</h2>
<div class="mb10">
  <input type="checkbox" id="opt4" name="opt4"><label for="opt4"> 설정변경/운영 현황 (표)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt5" name="opt5"><label for="opt5"> 운영내역 상세 (표)</label>
</div>

<h2 style="margin-top:0px;">고객요청 처리현황</h2>
<div class="mb10">
  <input type="checkbox" id="opt6" name="opt6"><label for="opt6"> 요청처리 현황 (표)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt7" name="opt7"><label for="opt7"> 요청처리 상세 (표)</label>
</div>

<h2 style="margin-top:0px;">보안정보 제공현황</h2>
<div class="mb10">
  <input type="checkbox" id="opt8" name="opt8"><label for="opt8"> 보안정보 제공현황 (표)</label>
</div>

<h2 style="margin-top:0px;">침해위협 탐지현황</h2>
<div class="mb10">
  <input type="checkbox" id="opt9" name="opt9"><label for="opt9"> 침해위협탐지 추이 (차트)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt10" name="opt10"><label for="opt10"> 침해위협탐지 분석 (차트, 표)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt11" name="opt11"><label for="opt11"> 탐지현황 상세 (표)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt12" name="opt12"><label for="opt12"> 공격IP 탐지동향 (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd12" id="rd12-1" value="10" checked><label for="rd12-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd12" id="rd12-2" value="20"><label for="rd12-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd12" id="rd12-3" value="30"><label for="rd12-3"> TOP 30</label></br>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt13" name="opt13"><label for="opt13"> 공격자IP 침해시도 추이 (차트)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt14" name="opt14"><label for="opt14"> 공격지 탐지동향 (표)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt15" name="opt15"><label for="opt15"> 공격지별 침해시도 추이 (차트)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt16" name="opt16"><label for="opt16"> 목적지 탐지동향 (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd16" id="rd16-1" value="10" checked><label for="rd16-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd16" id="rd16-2" value="20"><label for="rd16-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd16" id="rd16-3" value="30"><label for="rd16-3"> TOP 30</label>
  </div>
</div>

<h2 style="margin-top:0px;">홈페이지 악성코드 유포탐지 현황</h2>
<div class="mb10">
  <input type="checkbox" id="opt17" name="opt17"><label for="opt17"> 탐지보고 현황 (표)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt18" name="opt18"><label for="opt18"> 탐지현황 상세 (표)</label>
</div>

<h2 style="margin-top:0px;">웹쉘 탐지현황</h2>
<div class="mb10">
  <input type="checkbox" id="opt19" name="opt19"><label for="opt19"> 탐지보고 현황 (표)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt20" name="opt20"><label for="opt20"> 탐지현황 상세 (표)</label>
</div>

<h2 style="margin-top:0px;">RAT 의심탐지 현황</h2>
<div class="mb10">
  <input type="checkbox" id="opt21" name="opt21"><label for="opt21"> 탐지보고 현황 (표)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt22" name="opt22"><label for="opt22"> 탐지현황 상세 (표)</label>
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
