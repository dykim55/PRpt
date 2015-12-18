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

<span id="formTitle" style="display:none;">관제실적 임의기간보고서 설정</span>

<form id="frm" style="font-weight: bold;">

<h2 style="margin-top:0px;">보안관제 업무현황</h2>
<div class="mb10">
  <input type="checkbox" id="opt01" name="opt01"><label for="opt01"> 비정기 보고현황 : 상황/ 장애/ 작업 현황정보 (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="checkbox" id="ck1" name="ck1"><label for="ck1"> 비정기 보고현황(상세) : 상황/ 장애/ 작업 현황정보 (표)</label>
  </div>
</div>

<h2 style="margin-top:0px;">보안장비 운영현황</h2>
<div class="mb10">
  <input type="checkbox" id="opt02" name="opt02"><label for="opt02"> 설정변경/운영 현황 : 방화벽정책/룰설정/패턴업데이트/패치 현황 (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="checkbox" id="ck2" name="ck2"><label for="ck2"> 설정변경/운영 현황(상세) (표)</label>
  </div>
</div>

<h2 style="margin-top:0px;">고객요청 처리현황</h2>
<div class="mb10">
  <input type="checkbox" id="opt03" name="opt03"><label for="opt03"> 요청처리 현황 : 일반요청/ 기술지원/ 침해사고 분석 (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="checkbox" id="ck3" name="ck3"><label for="ck3"> 요청처리 현황(상세) (표)</label>
  </div>
</div>

<h2 style="margin-top:0px;">보안정보 제공현황</h2>
<div class="mb10">
  <input type="checkbox" id="opt04" name="opt04"><label for="opt04"> 보안정보 제공현황 (표)</label>
</div>

<h2 style="margin-top:0px;">침해위협 탐지현황</h2>
<div class="mb10">
  <input type="checkbox" id="opt05" name="opt05"><label for="opt05"> 침해위협탐지 추이 : 공격유형별 침해위협통보 추이 (차트)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt06" name="opt06"><label for="opt06"> 침해위협탐지 분석 : 공격유형별 통계 (차트, 표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="checkbox" id="ck6" name="ck6"><label for="ck6"> 침해탐지현황(상세) (표)</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt07" name="opt07"><label for="opt07"> 공격IP 탐지동향 : 침해위협통보건에 대한 SIP TOP (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd7" id="rd7-1" value="10" checked><label for="rd7-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd7" id="rd7-2" value="20"><label for="rd7-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd7" id="rd7-3" value="30"><label for="rd7-3"> TOP 30</label></br>
    <input type="checkbox" id="ck7" name="ck7"><label for="ck7"> 공격자IP 침해시도 추이 (차트)</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt08" name="opt08"><label for="opt08"> 공격지 탐지동향 : 침해위협통보건에 대한 국가별 통계 (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="checkbox" id="ck8" name="ck8"><label for="ck8"> 공격지별 침해시도 추이 (차트)</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt09" name="opt09"><label for="opt09"> 목적지 탐지동향 : 침해위협통보건에 대한 DIP TOP (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd9" id="rd9-1" value="10" checked><label for="rd9-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd9" id="rd9-2" value="20"><label for="rd9-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd9" id="rd9-3" value="30"><label for="rd9-3"> TOP 30</label>
  </div>
</div>

<h2 style="margin-top:0px;">홈페이지 악성코드 유포탐지 현황</h2>
<div class="mb10">
  <input type="checkbox" id="opt10" name="opt10"><label for="opt10"> 탐지보고 현황 (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="checkbox" id="ck10" name="ck10"><label for="ck10"> 홈페이지 악성코드 유포 탐지현황(상세) (표)</label>
  </div>
</div>

<h2 style="margin-top:0px;">웹쉘 탐지현황</h2>
<div class="mb10">
  <input type="checkbox" id="opt11" name="opt11"><label for="opt11"> 탐지보고 현황 (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="checkbox" id="ck11" name="ck11"><label for="ck11"> 웹쉘 탐지현황(상세) (표)</label>
  </div>
</div>

<h2 style="margin-top:0px;">RAT 의심탐지 현황</h2>
<div class="mb10">
  <input type="checkbox" id="opt12" name="opt12"><label for="opt12"> 탐지보고 현황 (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="checkbox" id="ck12" name="ck12"><label for="ck12"> RAT 의심 탐지현황(상세) (표)</label>
  </div>
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
